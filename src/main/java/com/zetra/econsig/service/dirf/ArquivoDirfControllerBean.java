package com.zetra.econsig.service.dirf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.exception.ArquivoDirfControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.folha.dirf.ArquivoComplementoDirfParser;
import com.zetra.econsig.folha.dirf.ArquivoDirf;
import com.zetra.econsig.folha.dirf.ArquivoDirfParser;
import com.zetra.econsig.folha.dirf.CalculoDirf;
import com.zetra.econsig.folha.dirf.ConteudoDirf;
import com.zetra.econsig.folha.dirf.ImportaArquivoDirfDTO;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Arquivo;
import com.zetra.econsig.persistence.entity.ArquivoHome;
import com.zetra.econsig.persistence.entity.DirfServidor;
import com.zetra.econsig.persistence.entity.DirfServidorHome;
import com.zetra.econsig.persistence.entity.DirfServidorId;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: ArquivoDirfControllerBean</p>
 * <p>Description: Session Bean para operações sobre arquivo de DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ArquivoDirfControllerBean implements ArquivoDirfController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ArquivoDirfControllerBean.class);

    @Override
    public void importarArquivoDirf(ImportaArquivoDirfDTO dto, AcessoSistema responsavel) throws ArquivoDirfControllerException {
        try {

        	String nomeArquivo = dto.getNomeArquivo();
        	String nomeArquivoComplemento = dto.getNomeArquivoComplemento();

        	File f = new File(nomeArquivo);
        	if (!f.exists()) {
        		LOG.error("Arquivo '" + f.getAbsolutePath() + "' não existe.");
                return;
        	}

        	if (!TextHelper.isNull(nomeArquivoComplemento)) {
        		f = new File(nomeArquivoComplemento);
        		if (!f.exists()) {
        			LOG.error("Arquivo '" + f.getAbsolutePath() + "' não existe.");
        			return;
        		}
        	}

            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoBrasaoDirf(responsavel));
            String template = "/com/zetra/econsig/folha/dirf/ModeloDirf.jrxml";

            // Diretório temporário usado para geração do PDF da DIRF
            String diretorioRaizSistema = ParamSist.getDiretorioRaizArquivos();
            File dir = new File(diretorioRaizSistema + "/temp");
            if (!dir.exists() && !dir.mkdirs()) {
                LOG.error("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
                return;
            }

            // Faz o parse do arquivo informado
            ArquivoDirf arquivoDirf = new ArquivoDirfParser().parse(nomeArquivo);

            // Efetua os cálculos dos valores da DIRF pelo conteúdo do arquivo
            List<ConteudoDirf> conteudoDirfs = new CalculoDirf().calcular(arquivoDirf);

            Map<String, String> conteudoComplemento = new ArquivoComplementoDirfParser().parse(nomeArquivoComplemento);

            // Para cada registro gerado, verifica se o servidor existe, exporta o PDF, e grava informação no banco de dados
            for (ConteudoDirf conteudoDirf : conteudoDirfs) {
                // Verifica se existe servidor com o CPF informdo
                List<Servidor> servidores = ServidorHome.findByCPF(conteudoDirf.getBeneficiarioCpf());
                if (servidores == null || servidores.isEmpty()) {
                    LOG.warn("Nenhum servidor encontrado para o CPF " + conteudoDirf.getBeneficiarioCpf());
                    continue;
                }

                // Nome do arquivo a ser gerado
                String reportName = dir.getAbsolutePath() + "/DIRF_" + conteudoDirf.getDeclaracaoAnoCalendario() + "_" + TextHelper.dropSeparator(conteudoDirf.getBeneficiarioCpf()) + ".pdf";

                realizarAlteracaoComplemento(conteudoDirf, conteudoComplemento, responsavel);

                // Gera o PDF com o conteúdo da DIRF
                parameters.put("declaracao", conteudoDirf);
                ReportManager.getInstance().exportSimplePDF(template, reportName, parameters, AcessoSistema.getAcessoUsuarioSistema());

                // Lê o conteúdo do arquivo
                byte[] conteudoArquivoPdf = Files.readAllBytes(Paths.get(reportName));

                // Transforma o conteúdo em Base64 para gravação no banco de dados
                byte[] conteudoArquivoBase64 = Base64.getEncoder().encode(conteudoArquivoPdf);

                // Grava o conteúdo no banco
                String arqConteudo = new String(conteudoArquivoBase64);
                Arquivo arquivoBean = ArquivoHome.create(arqConteudo, TipoArquivoEnum.ARQUIVO_DIRF_SERVIDOR.getCodigo());

                // Associa o conteúdo a todos os servidores, retornados para o CPF
                for (Servidor servidor : servidores) {
                    try {
                        // Caso já exista, procede a atualização do registro
                        DirfServidor dirf = DirfServidorHome.findByPrimaryKey(new DirfServidorId(servidor.getSerCodigo(), conteudoDirf.getDeclaracaoAnoCalendario()));

                        // Pega a referência ao arquivo antigo, a ser removido
                        Arquivo arquivoAntigo = dirf.getArquivo();

                        // Atualiza o registro para o novo arquivo criado
                        dirf.setArquivo(arquivoBean);
                        dirf.setDisDataCarga(DateHelper.getSystemDatetime());
                        DirfServidorHome.update(dirf);

                        // Apaga o arquivo antigo
                        ArquivoHome.remove(arquivoAntigo);

                    } catch (FindException ex) {
                        // Caso não exista, procede a criação do registro
                        DirfServidorHome.create(conteudoDirf.getDeclaracaoAnoCalendario(), servidor.getSerCodigo(), arquivoBean.getArqCodigo());
                    }
                }

                // Apaga o arquivo criado
                File arq = new File(reportName);
                arq.delete();
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (CreateException | UpdateException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Irá realizar o ajuste no conteudo de complemento, segundo o arquivo informado de complemento
     * @param dto
     */
    private void realizarAlteracaoComplemento(ConteudoDirf conteudoDirf, Map<String, String> conteudoComplemento, AcessoSistema responsavel) {

    	String cpf = conteudoDirf.getBeneficiarioCpf();

    	// remove qualquer mascara do "CPF"
        cpf = cpf.replaceAll("[^a-zA-Z0-9]", "");

    	if (conteudoComplemento.containsKey(cpf)) {

    		String valor = conteudoComplemento.get(cpf);
    		try {
    			// tenta formatar o valor, senão grava da forma como chegou.
    			valor = NumberHelper.format(NumberHelper.objectToBigDecimal(valor).doubleValue(), NumberHelper.getLang(), true);
    		} catch (Exception ex){
    		    LOG.error(ex.getMessage(), ex);
    		}
    		StringBuilder builder = new StringBuilder();
    		builder.append(conteudoDirf.getComplemento()).append("\n\n");
    		String message = ApplicationResourcesHelper.getMessage("rotulo.operadora.beneficio.dirf", responsavel, valor);
    		builder.append(message);
    		conteudoDirf.setComplemento(builder.toString());

    	}

	}

	private String getCaminhoBrasaoDirf(AcessoSistema responsavel) {
        String diretorioRaizSistema = ParamSist.getDiretorioRaizArquivos();
        File dir = new File(diretorioRaizSistema + "/conf/relatorio/imagem/");
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.error("Diretório '" + dir.getAbsolutePath() + "' não existe e não é possível criá-lo.");
            return null;
        }
        String path = dir.getAbsolutePath() + "/brasao_armas_rfb.jpg";
        if (!(new File(path).exists())) {
            InputStream input = getClass().getResourceAsStream("/com/zetra/econsig/folha/dirf/brasao_armas_rfb.jpg");
            try {
                FileHelper.saveStreamToFile(input, path);
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        }
        return path;
    }

    @Override
    public List<Short> listarAnoCalendarioDirf(String serCodigo, AcessoSistema responsavel) throws ArquivoDirfControllerException {
        try {
            List<DirfServidor> dirfs = DirfServidorHome.listAnoCalendarioBySerCodigo(serCodigo);
            if (dirfs != null && !dirfs.isEmpty()) {
                List<Short> anoCalendarioDirf = new ArrayList<>();
                for (DirfServidor dirf : dirfs) {
                    anoCalendarioDirf.add(dirf.getDisAnoCalendario());
                }
                return anoCalendarioDirf;
            }
            return null;
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoDirfControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String obterConteudoArquivoDirf(String serCodigo, Short disAnoCalendario, AcessoSistema responsavel) throws ArquivoDirfControllerException {
        try {
            DirfServidor dirf = DirfServidorHome.findByPrimaryKey(new DirfServidorId(serCodigo, disAnoCalendario));
            return dirf.getArquivo().getArqConteudo();
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoDirfControllerException("mensagem.erro.servidor.consultar.dirf.nao.encontrada", responsavel, ex);
        }
    }
}
