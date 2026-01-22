package com.zetra.econsig.delegate;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.helper.folha.HistoricoHelper;
import com.zetra.econsig.helper.folha.ProcessaRetorno;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.folha.ImpRetornoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ImpRetornoDelegate</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImpRetornoDelegate extends AbstractDelegate {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImpRetornoDelegate.class);

    private ImpRetornoController impRetornoController = null;

    public ImpRetornoDelegate() throws ImpRetornoControllerException {
        try {
            impRetornoController = ApplicationContextProvider.getApplicationContext().getBean(ImpRetornoController.class);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public void importarMargemRetorno(String nomeArquivoMargem, String nomeArquivoRetorno, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        Long harCodigo = null;
        boolean gerouException = false;
        try {
            if (TextHelper.isNull(nomeArquivoRetorno)) {
                throw new ImpRetornoControllerException("mensagem.folha.erro.importacao.arquivo.retorno", responsavel);
            }

            // Insere histórico do arquivo
            harCodigo = criarHistoricoArquivo(nomeArquivoRetorno, orgCodigo, estCodigo, ProcessaRetorno.RETORNO, responsavel);

            // Executa fora da transação principal a criação das tabelas necessárias para a exportação do movimento financeiro
            impRetornoController.criarTabelasImportacaoRetorno(responsavel);

            // Importa o retorno
            impRetornoController.importarMargemRetorno(nomeArquivoMargem, nomeArquivoRetorno, orgCodigo, estCodigo, responsavel);

        } catch (ImpRetornoControllerException ex) {
            gerouException = true;
            throw ex;
        } finally {
            atualizarHistoricoArquivo(harCodigo, gerouException, nomeArquivoRetorno, orgCodigo, estCodigo, responsavel);
        }
    }

    public void importarRetornoIntegracao(String nomeArquivo, String orgCodigo, String estCodigo, String tipo, Date periodoRetAtrasado, AcessoSistema responsavel) throws ImpRetornoControllerException {
        Long harCodigo = null;
        boolean gerouException = false;
        try {
            if (TextHelper.isNull(nomeArquivo)) {
                throw new ImpRetornoControllerException("mensagem.folha.erro.importacao.arquivo.retorno", responsavel);
            }

            nomeArquivo = java.net.URLDecoder.decode(nomeArquivo, "UTF-8");
            if (nomeArquivo.indexOf("..") != -1) {
                throw new ImpRetornoControllerException("mensagem.folha.erro.importacao.arquivo.retorno", responsavel);
            }

            // Insere histórico do arquivo
            harCodigo = criarHistoricoArquivo(nomeArquivo, orgCodigo, estCodigo, tipo, responsavel);

            // Executa fora da transação principal a criação das tabelas necessárias para a exportação do movimento financeiro
            impRetornoController.criarTabelasImportacaoRetorno(responsavel);

            // Importa o retorno
            impRetornoController.importarRetornoIntegracao(nomeArquivo, orgCodigo, estCodigo, tipo, periodoRetAtrasado, responsavel);

            // Diretório Raiz eConsig
            String absolutePath = ParamSist.getDiretorioRaizArquivos();

            String pathCritica = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "integracao";

            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_IMPORTACAO_AUTOM_SEM_PROC, CodedValues.TPC_SIM, responsavel)) {
                List<TransferObject> linhasSemProcessamento = impRetornoController.getLinhasSemProcessamento(responsavel);
                HistoricoHelper historicoHelper = new HistoricoHelper();
                historicoHelper.importaSemProcessamento(linhasSemProcessamento, pathCritica, responsavel);
            } else {
                LOG.debug("Sistema não importa automaticamente contratos sem processamento.");
            }
        } catch (ImpRetornoControllerException ex) {
            gerouException = true;
            throw ex;
        } catch (ConsignanteControllerException ex) {
            gerouException = true;
            throw new ImpRetornoControllerException(ex);
        } catch (UnsupportedEncodingException ex) {
            throw new ImpRetornoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            atualizarHistoricoArquivo(harCodigo, gerouException, nomeArquivo, orgCodigo, estCodigo, responsavel);
        }
    }

    private Long criarHistoricoArquivo(String nomeArquivo, String orgCodigo, String estCodigo, String tipo, AcessoSistema responsavel) {
        try {
            String harObs = "";
            Map<String, String> arquivosConfiguracao = impRetornoController.buscaArquivosConfiguracao(nomeArquivo, tipo, estCodigo, orgCodigo, responsavel);
            String fileName = arquivosConfiguracao.get("fileName");
            File arquivo = new File(fileName);
            if (arquivo.exists()) {
                nomeArquivo = arquivo.getName();
            }

            String harResultado = CodedValues.STS_INATIVO.toString();

            String funCodigo = null;
            TipoArquivoEnum tipoArquivo = null;
            if (tipo.equalsIgnoreCase(ProcessaRetorno.CRITICA)) {
                tipoArquivo = TipoArquivoEnum.ARQUIVO_CRITICA;
                funCodigo = CodedValues.FUN_IMP_RET_INTEGRACAO;
            } else if (tipo.equalsIgnoreCase(ProcessaRetorno.RETORNO)) {
                tipoArquivo = TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO;
                funCodigo = CodedValues.FUN_IMP_RET_INTEGRACAO;
            } else if (tipo.equalsIgnoreCase(ProcessaRetorno.ATRASADO) || tipo.equalsIgnoreCase(ProcessaRetorno.ATRASADO_SOMA_PARCELA)) {
                tipoArquivo = TipoArquivoEnum.ARQUIVO_RETORNO_ATRASADO;
                funCodigo = CodedValues.FUN_IMP_RET_ATRASADO;
            } else if (tipo.equalsIgnoreCase(ProcessaRetorno.CRITICA_ATRASADO)) {
                tipoArquivo = TipoArquivoEnum.ARQUIVO_CRITICA;
                funCodigo = CodedValues.FUN_IMP_RET_ATRASADO;
            }

            HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
            return hisArqDelegate.createHistoricoArquivo(null, null, tipoArquivo, fileName, harObs, null, null, harResultado, funCodigo, responsavel);
        } catch (HistoricoArquivoControllerException e) {
            LOG.error("Não foi possível inserir o histórico do arquivo de retorno '" + nomeArquivo + "'.", e);
        } catch (ImpRetornoControllerException e) {
            LOG.error("Não foi possível inserir o histórico do arquivo de retorno '" + nomeArquivo + "'.", e);
        }
        return null;
    }

    private void atualizarHistoricoArquivo(Long harCodigo, boolean resultadoComErro, String nomeArquivo, String orgCodigo, String estCodigo, AcessoSistema responsavel) {
        if (harCodigo != null) {
            try {
                java.util.Date pexPeriodo = null;
                try {
                    List<String> orgCodigos = null;
                    if (!TextHelper.isNull(orgCodigo)) {
                        orgCodigos = new ArrayList<>();
                        orgCodigos.add(orgCodigo);
                    }
                    List<String> estCodigos = null;
                    if (!TextHelper.isNull(estCodigo)) {
                        estCodigos = new ArrayList<>();
                        estCodigos.add(estCodigo);
                    }
                    PeriodoDelegate perDelegate = new PeriodoDelegate();
                    TransferObject to = perDelegate.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
                    pexPeriodo = DateHelper.parse(to.getAttribute(Columns.PEX_PERIODO).toString(), "yyyy-MM-dd");
                } catch (Exception e) {
                    LOG.error("Não foi possível localizar o período atual de exportação.", e);
                }

                String harResultado = (resultadoComErro ? CodedValues.STS_INATIVO.toString() : CodedValues.STS_ATIVO.toString());
                HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                hisArqDelegate.updateHistoricoArquivo(harCodigo, null, null, pexPeriodo, harResultado, responsavel);
            } catch (HistoricoArquivoControllerException e) {
                LOG.error("Não foi possível inserir o histórico do arquivo de retorno '" + nomeArquivo + "'.", e);
            }
        }
    }

    public void finalizarIntegracaoFolha(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImpRetornoControllerException {
        // Finaliza a importação do retorno
        impRetornoController.finalizarIntegracaoFolha(tipoEntidade, codigoEntidade, responsavel);
    }

    public void desfazerUltimoRetorno(String orgCodigo, String estCodigo, boolean recalcularMargem, boolean desfazerMovimento, String[] parcelas, AcessoSistema responsavel) throws ImpRetornoControllerException {
        // Finaliza a importação do retorno
        impRetornoController.desfazerUltimoRetorno(orgCodigo, estCodigo, recalcularMargem, desfazerMovimento, parcelas, responsavel);
    }

    public java.util.Date getUltimoPeriodoRetorno(String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        return impRetornoController.getUltimoPeriodoRetorno(orgCodigo, estCodigo, responsavel);
    }

    public List<TransferObject> lstHistoricoConclusaoRetorno(String orgCodigo, int qtdeMesesPesquisa, String periodo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        return impRetornoController.lstHistoricoConclusaoRetorno(orgCodigo, qtdeMesesPesquisa, periodo, responsavel);
    }

    public String recuperaPeriodoRetorno(int tipoImportacaoRetorno, java.sql.Date periodoRetAtrasado, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ImpRetornoControllerException {
        return impRetornoController.recuperaPeriodoRetorno(tipoImportacaoRetorno, periodoRetAtrasado, orgCodigos, estCodigos, responsavel);
    }

    public String ajustaTipoRetornoPeloPeriodo(String nomeArquivo, String orgCodigo, String estCodigo, String tipo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        return impRetornoController.ajustaTipoRetornoPeloPeriodo(nomeArquivo, orgCodigo, estCodigo, tipo, responsavel);
    }
}
