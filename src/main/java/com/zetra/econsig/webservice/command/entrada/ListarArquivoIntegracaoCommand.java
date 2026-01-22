package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ARQUIVO_INTEGRACAO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ListarArquivoIntegracaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de listar arquivo de integração.</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarArquivoIntegracaoCommand extends RequisicaoExternaFolhaCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarArquivoIntegracaoCommand.class);

    public ListarArquivoIntegracaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        boolean temPermissaoEst = false;

        if (!responsavel.isCseSupOrg()) {
        	throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        if (responsavel.isOrg()) {
            temPermissaoEst = responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO);
        }

        ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);

        String tipo = (String) parametros.get(TIPO_ARQUIVO);
        String papCodigo = responsavel.isSup() ? AcessoSistema.ENTIDADE_CSE : responsavel.getTipoEntidade();
        String orgIdentificador = (String) parametros.get(CODIGO_ORGAO);
        String estIdentificador = (String) parametros.get(CODIGO_ESTABELECIMENTO);
        String csaCodigo = responsavel.getCsaCodigo();

        String estCodigo = "";
        if (!TextHelper.isNull(estIdentificador)) {
        	EstabelecimentoTransferObject est = consignanteController.findEstabelecimentoByIdn(estIdentificador, responsavel);
        	estCodigo = est.getEstCodigo();
        }

        String orgCodigo = "";
        if (!TextHelper.isNull(orgIdentificador)) {
        	OrgaoTransferObject org = consignanteController.findOrgaoByIdn(orgIdentificador, estCodigo, responsavel);
        	orgCodigo = org.getOrgCodigo();
        }

        if (responsavel.isOrg() && temPermissaoEst && !responsavel.getEstCodigo().equals(estCodigo)) {
        	throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        boolean selecionaEstOrg = !TextHelper.isNull(orgCodigo) || !TextHelper.isNull(estCodigo);

        //Path dos arquivos de integração
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        List<String> codigosOrgao = new ArrayList<>();
        Map<String, TransferObject> orgaos = new HashMap<>();

        String diretorioArquivos = null;
        List<ArquivoDownload> arquivosPaginaAtual = null;

        String pathDiretorioArquivos = buscarPathDiretorioArquivos(tipo, csaCodigo, orgCodigo, temPermissaoEst);

        String pathCombo = null;
        String pathDownload = null;

        if (pathDiretorioArquivos == null) {
            pathCombo = absolutePath + File.separatorChar + tipo + File.separatorChar;
            pathDownload = pathCombo;

			if (!selecionaEstOrg) {
                // Se é usuário de órgão, concatena o código do órgão nos paths
                if (responsavel.isOrg() && temPermissaoEst) {
                    pathCombo += "est" + File.separatorChar + responsavel.getCodigoEntidadePai();
                } else if (responsavel.isOrg()) {
                    pathCombo += "cse" + File.separatorChar + responsavel.getCodigoEntidade();
                } else if (responsavel.isCseSup()) {
                    pathCombo += "cse";
                } else {
                    throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
                }
            } else if ((!TextHelper.isNull(estCodigo) && TextHelper.isNull(orgCodigo)) && (responsavel.isCseSup() || (responsavel.isOrg() && temPermissaoEst))) {
                if (responsavel.isOrg()) {
                    estCodigo = responsavel.getEstCodigo();
                }

                pathCombo += "est" + File.separatorChar + (TextHelper.isNull(estCodigo) ? "" : estCodigo);

            } else if (!TextHelper.isNull(orgCodigo)) {
                if (responsavel.isOrg() && !temPermissaoEst) {
                    orgCodigo = responsavel.getCodigoEntidade();
                }
                pathCombo += "cse" + File.separatorChar + (TextHelper.isNull(orgCodigo) ? "" : orgCodigo);
            } else if (responsavel.isCseSup()) {
                pathCombo += "cse";
            } else {
                throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
            }

            pathCombo += File.separatorChar;
        } else {
            pathCombo = absolutePath + File.separatorChar + pathDiretorioArquivos;
            pathDownload = pathCombo;
        }

        // Cria filtro para seleção de arquivos .txt, .zip, .xls, .xlsx e .csv
        FileFilter filtro = arq -> {
            String arqNome = arq.getName().toLowerCase();
            return (arqNome.endsWith(".txt") ||
                    arqNome.endsWith(".zip") ||
                    arqNome.endsWith(".xls") ||
                    arqNome.endsWith(".xlsx") ||
                    arqNome.endsWith(".csv") ||
                    arqNome.endsWith(".txt.crypt") ||
                    arqNome.endsWith(".zip.crypt"));
        };

        // Faz as checagens de diretório
        File diretorioRetorno = new File(pathCombo);
        if ((!diretorioRetorno.exists() && !diretorioRetorno.mkdirs())) {
            throw new ZetraException("mensagem.erro.upload.criacao.diretorio", responsavel);
        }

        // Alteração para ficar compatível com o FileAbstractWebController, onde os arquivos de integração com a folha
        // para os papéis CSE e SUP devem conter a pasta no nome do arquivo
        if (responsavel.isCseSup() && (
                tipo.equals("margem") ||
                        tipo.equals("margemcomplementar") ||
                        tipo.equals("transferidos") ||
                        tipo.equals("retorno") ||
                        tipo.equals("retornoatrasado") ||
                        tipo.equals("critica") ||
                        tipo.equals("contracheque") ||
                        tipo.equals("historico"))) {
            diretorioArquivos = pathDownload;
        } else {
            diretorioArquivos = pathCombo;
        }

        // Lista os arquivos
        List<File> arquivosCombo = new ArrayList<>();
        File[] temp = null;
        if (!TextHelper.isNull(papCodigo) && !(papCodigo.equals(AcessoSistema.ENTIDADE_ORG) && TextHelper.isNull(orgCodigo)) && !(papCodigo.equals(AcessoSistema.ENTIDADE_EST) && TextHelper.isNull(estCodigo)) || (TextHelper.isNull(papCodigo) && (responsavel.isCseSupOrg()))) {
            temp = diretorioRetorno.listFiles(filtro);
        }

        if (temp != null) {
            arquivosCombo.addAll(Arrays.asList(temp));

            // Pega o identificador dos órgão, e os arquivos dos subdiretórios
            if (responsavel.isCseSup()) {
                String[] nome_subdir = diretorioRetorno.list();
                if (nome_subdir != null) {
                    for (String element : nome_subdir) {
                        File arq = new File(pathCombo + element);
                        if (arq.isDirectory()) {
                            arquivosCombo.addAll(Arrays.asList(arq.listFiles(filtro)));
                            codigosOrgao.add(element);
                        }
                    }
                }

                if (!codigosOrgao.isEmpty()) {
                    List<TransferObject> orgaosTO = null;
                    try {
                        TransferObject criterio = new CustomTransferObject();
                        criterio.setAttribute(Columns.ORG_CODIGO, codigosOrgao);

                        orgaosTO = consignanteController.lstOrgaos(criterio, responsavel);
                        Iterator<TransferObject> it1 = orgaosTO.iterator();
                        while (it1.hasNext()) {
                            criterio = it1.next();
                            orgaos.put((String) criterio.getAttribute(Columns.ORG_CODIGO), criterio);
                        }
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
                    }
                }
            }
        }

        // Ordena os arquivos baseado na data de modificação
        Collections.sort(arquivosCombo, (f1, f2) -> {
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        });

        arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivosCombo, diretorioArquivos, orgaos, responsavel);

        if (arquivosPaginaAtual == null || arquivosPaginaAtual.isEmpty()) {
            throw new ZetraException("rotulo.nenhum.arquivo.encontrado", responsavel);
        }

        parametros.put(ARQUIVO_INTEGRACAO, arquivosPaginaAtual);
    }

	private String buscarPathDiretorioArquivos(String tipo, String csaCodigo, String orgCodigo, boolean temPermissaoEst) throws ZetraException {
		String path = null;

		if (tipo.equalsIgnoreCase("cadastroDependentes")) {
	        path = "beneficiarios" + File.separatorChar;
		} else if (tipo.equalsIgnoreCase("carteirinhasTombadas")) {
			path = "integracaobeneficio" + File.separatorChar + "tombamento" + File.separatorChar;
		} else if (tipo.equalsIgnoreCase("previafaturamentobeneficios")) {
	    	path = "fatura" + File.separatorChar + "previa" + File.separatorChar + "csa" + File.separatorChar;
	    	if (!TextHelper.isNull(csaCodigo)) {
	    		path +=  csaCodigo + File.separatorChar;
	    	}
		} else if (tipo.equalsIgnoreCase("relatorioCustomizado")) {
			path = "relatorio" + File.separatorChar + "csa" + File.separatorChar + "customizacoes" + File.separatorChar;
		} else if (tipo.equalsIgnoreCase("integracao")) {
	        path = "relatorio" + File.separatorChar;

	        // Se é usuário de órgão, concatena o código do órgão nos paths
            if (responsavel.isOrg()) {
                if (responsavel.isOrg() && (TextHelper.isNull(orgCodigo) || !temPermissaoEst)) {
                    orgCodigo = responsavel.getCodigoEntidade();
                }

            	path += "cse" + File.separatorChar + "integracao" + File.separatorChar + orgCodigo + File.separatorChar;
            } else if (responsavel.isCseSup()) {
            	path += "cse" + File.separatorChar + "integracao" + File.separatorChar;

            	if (!TextHelper.isNull(orgCodigo)) {
                	path += orgCodigo + File.separatorChar;
            	}
            } else {
                throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
            }
		}

		return path;
	}
}
