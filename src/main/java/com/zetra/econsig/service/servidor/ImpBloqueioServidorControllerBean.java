package com.zetra.econsig.service.servidor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.ParamCnvRseTO;
import com.zetra.econsig.dto.entidade.ParamNseRseTO;
import com.zetra.econsig.dto.entidade.ParamSvcRseTO;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.persistence.entity.NaturezaServicoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaRegistroServidorHome;
import com.zetra.econsig.persistence.entity.ParamConvenioRegistroSer;
import com.zetra.econsig.persistence.entity.ParamConvenioRegistroServidorHome;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import jakarta.transaction.Transactional;

/**
 * <p>Title: ImpBloqueioServidorControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ImpBloqueioServidorControllerBean implements ImpBloqueioServidorController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImpBloqueioServidorControllerBean.class);

    public static final int TAMANHO_MSG_ERRO_DEFAULT = 100;
    public static final String COMPLEMENTO_DEFAULT = " ";

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Override
    public void importarBloqueioServidor(String nomeArquivo, AcessoSistema responsavel) throws ServidorControllerException {

        ParamSist ps = ParamSist.getInstance();
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        // Arquivos de configuração para importar retorno
        String entradaImpRetorno = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_BLOQ_SERVIDOR, responsavel);
        String tradutorImpRetorno = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_BLOQ_SERVIDOR, responsavel);

        // Path padão dos arquivos de importação de margem
        String entradaImpRetornoPadrao = absolutePath + File.separatorChar + "conf" + File.separatorChar + entradaImpRetorno;
        String tradutorImpRetornoPadrao = absolutePath + File.separatorChar + "conf" + File.separatorChar + tradutorImpRetorno;
        // Carrega arquivos.
        File arqConfEntrada  = new File(entradaImpRetornoPadrao);
        File arqConfTradutor = new File(tradutorImpRetornoPadrao);

        // Gera exceção.
        if (!arqConfEntrada.exists() || !arqConfTradutor.exists()) {
            throw new ServidorControllerException("mensagem.erro.configuracao.banco.de.dados.incorreta", responsavel);
        }

        String pathFile;
        String fileName;
        String entidade;

        if (responsavel.isCsa()) {
            entidade = AcessoSistema.ENTIDADE_CSA;
            pathFile = absolutePath + File.separatorChar + "bloqueio_ser" + File.separatorChar + "csa" + File.separatorChar + responsavel.getCsaCodigo();
            fileName = pathFile + File.separatorChar + nomeArquivo;
        } else {
            entidade = AcessoSistema.ENTIDADE_CSE;
            pathFile = absolutePath + File.separatorChar + "bloqueio_ser" + File.separatorChar + "cse";
            fileName = pathFile + File.separatorChar + nomeArquivo;
        }

        Map<String, Object> entrada = new HashMap<>();
        // Configura o leitor de acordo com o arquivo de entrada
        LeitorArquivoTexto leitor = null;
        if (fileName.toLowerCase().endsWith(".zip")) {
            leitor = new LeitorArquivoTextoZip(arqConfEntrada.getAbsolutePath(), fileName);
        } else {
            leitor = new LeitorArquivoTexto(arqConfEntrada.getAbsolutePath(), fileName);
        }
        List<String> critica = new ArrayList<>();
        try {
            // Prepara tradução do arquivo de retorno.
            LOG.debug("ARQUIVO: " + nomeArquivo);
            LOG.debug("TRADUCAO ARQUIVO: " + DateHelper.getSystemDatetime());
            Escritor escritor = new EscritorMemoria(entrada);
            Tradutor tradutor = new Tradutor(arqConfTradutor.getAbsolutePath(), leitor, escritor);
            tradutor.iniciaTraducao();
            String delimitador = leitor.getDelimitador() == null ? "" : leitor.getDelimitador();
            while (tradutor.traduzProximo()) {
                // Lê campos do arquivo de retorno
                String rseMatricula = (String) entrada.get("RSE_MATRICULA");
                String serCpf = (String) entrada.get("SER_CPF");
                String orgIdentificador = (String) entrada.get("ORG_IDENTIFICADOR");
                String estIdentificador = (String) entrada.get("EST_IDENTIFICADOR");
                String svcIdentificador = (String) entrada.get("SVC_IDENTIFICADOR");
                String cnvCodVerba = (String) entrada.get("CNV_COD_VERBA");
                String nseCodigo = (String) entrada.get("NSE_CODIGO");

                try {
                    // Se o valor for nulo ou vazio, caso exista um bloqueio o mesmo será removido
                    String valor = (String) entrada.get("VALOR");
                    String observacao = (String) entrada.get("OBSERVACAO");

                    List<ParamConvenioRegistroSer> servidores = new ArrayList<>();
                    TransferObject servidor;
                    if (entidade.equals(AcessoSistema.ENTIDADE_CSA) && (TextHelper.isNull(valor))) {
                        servidores = buscaServidoresCsaDesbloqueio(serCpf, responsavel);
                        for (ParamConvenioRegistroSer paramConvenioRg : servidores) {
                            ConvenioTransferObject convenio = convenioController.findByPrimaryKey(paramConvenioRg.getCnvCodigo(), responsavel);
                            servidor = pesquisarServidorController.buscaServidor(paramConvenioRg.getRseCodigo(), responsavel);
                            String ocsObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.desbloqueio.bloqueio.convenio.unificado", responsavel, convenio.getCnvDescricao(), (String) servidor.getAttribute(Columns.SER_NOME));
                            ParamConvenioRegistroServidorHome.remove(paramConvenioRg);
                            OcorrenciaRegistroServidorHome.create(paramConvenioRg.getRseCodigo(), CodedValues.TOC_RSE_DESBLOQUEIO_VERBAS, responsavel.getUsuCodigo(), ocsObs, null, null);
                        }
                    } else {
                        if (TextHelper.isNull(rseMatricula)) {
                            throw new ServidorControllerException("mensagem.erro.informe.matricula", responsavel);
                        }
                        if (TextHelper.isNull(svcIdentificador) && TextHelper.isNull(cnvCodVerba) &&  TextHelper.isNull(nseCodigo)) {
                            throw new ServidorControllerException("mensagem.erro.informe.servico.cod.verba", responsavel);
                        }

                        servidor = buscaServidor(entidade, rseMatricula, serCpf, orgIdentificador, estIdentificador, responsavel);
                        String orgCodigo = (String) servidor.getAttribute(Columns.ORG_CODIGO);
                        String rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);

                        //Bloqueia convênio/serviço/natureza de serviços por ervidor
                        if (!TextHelper.isNull(cnvCodVerba)){
                            TransferObject convenio = buscaConvenio(cnvCodVerba, orgCodigo, null, svcIdentificador, false, responsavel);
                            String cnvCodigo = (String) convenio.getAttribute(Columns.CNV_CODIGO);

                            ParamCnvRseTO parametro = new ParamCnvRseTO();
                            parametro.setRseCodigo(rseCodigo);
                            parametro.setCnvCodigo(cnvCodigo);
                            parametro.setPcrVlr(valor);
                            if (entidade.equals(AcessoSistema.ENTIDADE_CSA)) {
                                parametro.setPcrVlrCsa(valor);
                            } else if(entidade.equals(AcessoSistema.ENTIDADE_CSE) || entidade.equals(AcessoSistema.ENTIDADE_SUP) || entidade.equals(AcessoSistema.ENTIDADE_ORG)) {
                                parametro.setPcrVlrCse(valor);
                            } else if (entidade.equals(AcessoSistema.ENTIDADE_SER)) {
                                parametro.setPcrVlrSer(valor);
                            }
                            parametro.setPcrObs(observacao);
                            parametro.setTpsCodigo(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO);

                            parametroController.setBloqueioCnvRegistroServidor(parametro, responsavel);
                        } else if (!TextHelper.isNull(svcIdentificador)) {
                            Servico servico = buscaServico(svcIdentificador);

                            ParamSvcRseTO parametro = new ParamSvcRseTO();
                            parametro.setRseCodigo(rseCodigo);
                            parametro.setSvcCodigo(servico.getSvcCodigo());
                            parametro.setPsrVlr(valor);
                            parametro.setPsrObs(observacao);
                            parametro.setTpsCodigo(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO);

                            parametroController.setBloqueioSvcRegistroServidor(parametro, null, responsavel);
                        } else if (!TextHelper.isNull(nseCodigo)) {
                            NaturezaServico naturezaSvc = buscaNaturezaServico(nseCodigo);

                            ParamNseRseTO parametro = new ParamNseRseTO();
                            parametro.setRseCodigo(rseCodigo);
                            parametro.setNseCodigo(naturezaSvc.getNseCodigo());
                            parametro.setPnrVlr(valor);
                            parametro.setPnrObs(observacao);
                            parametro.setTpsCodigo(CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO);

                            parametroController.setBloqueioNseRegistroServidor(parametro, responsavel);
                        }
                    }


                } catch (ParametroControllerException e) {
                    String msgErro = ApplicationResourcesHelper.getMessage("mensagem.erro.nao.possivel.bloquear.registro", responsavel);
                    LOG.error(msgErro, e);
                    critica.add(leitor.getLinha() + delimitador + formataMsgErro(msgErro, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                } catch (ServidorControllerException | FindException e) {
                    critica.add(leitor.getLinha() + delimitador + formataMsgErro(e.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                } catch (CreateException | RemoveException | ConvenioControllerException e) {
                    throw new RuntimeException(e);
                }

            }
            tradutor.encerraTraducao();
            LOG.debug("FIM TRADUCAO ARQUIVO: " + DateHelper.getSystemDatetime());
        } catch (ParserException e) {
            throw new ServidorControllerException("mensagem.erro.impossivel.importar.bloqueio.servidor", responsavel, e);
        }

        geraCritica(nomeArquivo, pathFile, critica, responsavel);

    }

    private void geraCritica(String nomeArquivo, String pathFile, List<String> critica, AcessoSistema responsavel) {
        if (!critica.isEmpty()) {
            try {
                LOG.debug("ARQUIVO DE CRITICA: " + DateHelper.getSystemDatetime());
                String nomeArqSaida = pathFile + File.separatorChar + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
                nomeArqSaida += nomeArquivo + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
                String nomeArqSaidaTxt = nomeArqSaida + ".txt";
                PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)));
                LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);
                arqSaida.println(TextHelper.join(critica, System.lineSeparator()));

                arqSaida.close();

                String nomeArqSaidaZip = nomeArqSaida + ".zip";
                FileHelper.zip(nomeArqSaidaTxt, nomeArqSaidaZip);
                FileHelper.delete(nomeArqSaidaTxt);
                LOG.debug("FIM ARQUIVO DE CRITICA: " + DateHelper.getSystemDatetime());
            } catch (IOException e) {
                LOG.error("Não foi possível gerar arquivo de crítica.", e);
            }
        }
    }

    private List<ParamConvenioRegistroSer> buscaServidoresCsaDesbloqueio(String cpf, AcessoSistema responsavel) throws FindException {
        List<ParamConvenioRegistroSer> paramConvenioRg;
        try {
            paramConvenioRg = ParamConvenioRegistroServidorHome.findByCpfCsa(cpf, responsavel);
        } catch (FindException ex ) {
            LOG.error(ex.getMessage(), ex);
            throw new FindException("mensagem.erro.servidor.nao.encontrado", responsavel);
        }

        return paramConvenioRg;
    }

    private TransferObject buscaServidor(String entidade, String rseMatricula, String serCpf, String orgIdentificador,
                                              String estIdentificador, AcessoSistema responsavel) throws ServidorControllerException {
        // Faz a pesquisa de servidor
        TransferObject servidor = null;
        List<TransferObject> servidores = new ArrayList<>();
        int excluidos = 0;

        try {
            List<TransferObject> candidatos = pesquisarServidorController.pesquisaServidorExato(entidade, entidade.equals(AcessoSistema.ENTIDADE_CSA) ? responsavel.getCsaCodigo() : CodedValues.CSE_CODIGO_SISTEMA, estIdentificador, orgIdentificador, rseMatricula, serCpf, responsavel);

            // Verifica se os servidores candidatos não estão excluidos
            if (!candidatos.isEmpty()) {
                Iterator<TransferObject> it = candidatos.iterator();
                while (it.hasNext()) {
                    servidor = it.next();
                    if (!CodedValues.SRS_INATIVOS.contains(servidor.getAttribute(Columns.SRS_CODIGO))) {
                        servidores.add(servidor);
                    } else {
                        excluidos++;
                    }
                }
            }
        } catch (ServidorControllerException e) {
            throw new ServidorControllerException("mensagem.erro.servidor.nao.encontrado", responsavel);
        }

        if (servidores.size() == 0) {
            if (excluidos > 0) {
                throw new ServidorControllerException("mensagem.servidorExcluido", responsavel);
            } else {
                throw new ServidorControllerException("mensagem.erro.servidor.nao.encontrado", responsavel);
            }
        } else if (servidores.size() > 1) {
            throw new ServidorControllerException("mensagem.erro.servidor.multiplo", responsavel);
        }

        servidor = servidores.get(0);
        return servidor;
    }

    private TransferObject buscaConvenio(String cnvCodVerba, String orgCodigo, String csaIdentificador,
                                                      String svcIdentificador, boolean cnvAtivo, AcessoSistema responsavel) throws ServidorControllerException {

        // Busca o convênio
        List<TransferObject> conveniosCandidatos = null;
        try {
            conveniosCandidatos = convenioController.lstConvenios(cnvCodVerba, null, null, orgCodigo, cnvAtivo, responsavel);
        } catch (ConvenioControllerException e) {
            throw new ServidorControllerException("mensagem.erro.nenhum.convenio.encontrado", responsavel);
        }

        if (conveniosCandidatos == null || conveniosCandidatos.size() == 0) {
            throw new ServidorControllerException("mensagem.erro.nenhum.convenio.encontrado", responsavel);
        }

        List<TransferObject> convenios = new ArrayList<>();
        if (conveniosCandidatos.size() > 1 && (csaIdentificador != null || svcIdentificador != null)) {
            // Se o id da CSA ou do SVC foi passado, navega nos convênios até encontrar o desejado
            Iterator<TransferObject> itCnv = conveniosCandidatos.iterator();
            TransferObject cnv = null;
            String csa = null, svc = null;
            while (itCnv.hasNext()) {
                cnv = itCnv.next();
                csa = (String) cnv.getAttribute(Columns.CSA_IDENTIFICADOR);
                svc = (String) cnv.getAttribute(Columns.SVC_IDENTIFICADOR);
                if (csa != null && csaIdentificador != null && csa.equals(csaIdentificador) &&
                    svc != null && svcIdentificador != null && svc.equals(svcIdentificador)) {
                    convenios.add(cnv);
                } else if (csa != null && csaIdentificador != null && csa.equals(csaIdentificador) && svcIdentificador == null) {
                    convenios.add(cnv);
                } else if (svc != null && svcIdentificador != null && svc.equals(svcIdentificador) && csaIdentificador == null) {
                    convenios.add(cnv);
                }
            }

            if (convenios == null || convenios.size() == 0) {
                throw new ServidorControllerException("mensagem.erro.nenhum.convenio.encontrado", responsavel);
            } else if (convenios.size() > 1) {
                throw new ServidorControllerException("mensagem.erro.convenio.multiplo", responsavel);
            }

        } else if (conveniosCandidatos.size() > 1) {
            throw new ServidorControllerException("mensagem.erro.convenio.multiplo", responsavel);

        } else if (conveniosCandidatos.size() == 1) {
            convenios.addAll(conveniosCandidatos);
        }

        return convenios.get(0);
    }

    private static Servico buscaServico(String svcIdentificador) throws ServidorControllerException {
        try {
            return ServicoHome.findByIdn(svcIdentificador);
        } catch (FindException e) {
            throw new ServidorControllerException("mensagem.erro.servico.nao.encontrado", (AcessoSistema) null);
        }
    }

    private static NaturezaServico buscaNaturezaServico(String nseCodigo) throws ServidorControllerException {
        try {
            return NaturezaServicoHome.findByPrimaryKey(nseCodigo);
        } catch (FindException e) {
            throw new ServidorControllerException("mensagem.erro.natureza.servico.nao.encontrada", (AcessoSistema) null);
        }
    }

    private static String formataMsgErro (String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        mensagem = (mensagem == null ? "" : mensagem);
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }
}
