package com.zetra.econsig.helper.folha;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;

import com.zetra.econsig.delegate.ImpRetornoDelegate;
import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.folha.ProcessarFolhaController;
import com.zetra.econsig.service.pontuacao.PontuacaoServidorController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRetorno</p>
 * <p>Description: Classe para execução de processos via Script</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRetorno implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRetorno.class);
    private static final String NOME_CLASSE = ProcessaRetorno.class.getName();

    public static final String MARGEM = "margem";
    public static final String MARGEM_COMPLEMENTAR = "margemcomplementar";
    public static final String TRANSFERIDOS = "transferidos";
    public static final String RETORNO  = "retorno";
    public static final String CRITICA  = "critica";
    public static final String ATRASADO = "atrasado";
    public static final String ATRASADO_SOMA_PARCELA = "atrasado_soma_parcela";
    public static final String CRITICA_ATRASADO = "critica_atrasado";

    private String nomeArqTransferidosAcimaPermitido = null;
    private boolean qtdLinhasArqTransferidosAcimaPermitido = false;

    private void importaMargens(String nomeArquivo, boolean margemTotal,
            boolean gerarTransferidos, boolean processarTransferidos, String orgCodigo, String estCodigo,
            AcessoSistema responsavel, boolean margemComplementar) throws ViewHelperException {

        String nomeArqTransferidos = null;

        String tipoEntidade = !TextHelper.isNull(estCodigo) ? "EST" : !TextHelper.isNull(orgCodigo) ? "ORG" : "CSE";
        String codigoEntidade = !TextHelper.isNull(estCodigo) ? estCodigo : !TextHelper.isNull(orgCodigo) ? orgCodigo : CodedValues.CSE_CODIGO_SISTEMA;

        String tipo = margemComplementar ? MARGEM_COMPLEMENTAR : MARGEM;
        String fileName = obtemArquivoProcessamento(tipo, nomeArquivo, tipoEntidade, codigoEntidade, responsavel);

        try {
            LOG.debug("INÍCIO - IMPORTAÇÃO DE MARGENS (" + nomeArquivo + "): " + DateHelper.getSystemDatetime());

            ServidorDelegate serDelegate = new ServidorDelegate();
            nomeArqTransferidos = serDelegate.importaCadastroMargens(fileName, tipoEntidade, codigoEntidade, margemTotal, gerarTransferidos, responsavel);

            LOG.debug("FIM - IMPORTAÇÃO DE MARGENS (" + nomeArquivo + "): " + DateHelper.getSystemDatetime());

        } catch (ServidorControllerException ex) {
            LOG.error("ERRO - IMPORTAÇÃO DE MARGENS (" + nomeArquivo + "): " + DateHelper.getSystemDatetime());
            throw new ViewHelperException(ex);
        }

        if (gerarTransferidos && processarTransferidos) {
            if (nomeArqTransferidos == null || nomeArqTransferidos.equals("")) {
                LOG.info("Nenhum arquivo de transferidos gerado");
            } else {
                importaTransferidos(nomeArqTransferidos, orgCodigo, estCodigo, responsavel);
            }
        }
    }

    private void importaTransferidos(String nomeArquivo, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ViewHelperException {
        String tipoEntidade = !TextHelper.isNull(estCodigo) ? "EST" : !TextHelper.isNull(orgCodigo) ? "ORG" : "CSE";
        String codigoEntidade = !TextHelper.isNull(estCodigo) ? estCodigo : !TextHelper.isNull(orgCodigo) ? orgCodigo : CodedValues.CSE_CODIGO_SISTEMA;

        String fileName = obtemArquivoProcessamento(TRANSFERIDOS, nomeArquivo, tipoEntidade, codigoEntidade, responsavel);

        try {
            LOG.debug("INÍCIO - IMPORTAÇÃO DE TRANSFERIDOS (" + nomeArquivo + "): " + DateHelper.getSystemDatetime());
            ServidorDelegate serDelegate = new ServidorDelegate();
            if (!qtdLinhasArqTransferidosAcimaPermitido) {
                qtdLinhasArqTransferidosAcimaPermitido = serDelegate.qtdLinhasArqTransferidosAcimaPermitido(fileName, tipoEntidade, codigoEntidade, responsavel);
                if (qtdLinhasArqTransferidosAcimaPermitido) {
                    nomeArqTransferidosAcimaPermitido = fileName;
                }
            }
            serDelegate.importaServidoresTransferidos(fileName, tipoEntidade, codigoEntidade, responsavel);
            LOG.debug("FIM - IMPORTAÇÃO DE TRANSFERIDOS (" + nomeArquivo + "): " + DateHelper.getSystemDatetime());
        } catch (ServidorControllerException ex) {
            LOG.error("ERRO - IMPORTAÇÃO DE TRANSFERIDOS (" + nomeArquivo + "): " + DateHelper.getSystemDatetime());
            throw new ViewHelperException(ex);
        }
    }

    private void importaRetorno(String nomeArquivo, String tipo, String orgCodigo, String estCodigo, java.sql.Date periodoRetAtrasado, AcessoSistema responsavel) throws ViewHelperException {
        try {
            LOG.debug("INÍCIO - IMPORTAÇÃO DE " + tipo.toUpperCase() + " (" + nomeArquivo + "): " + DateHelper.getSystemDatetime());
            ImpRetornoDelegate retDelegate = new ImpRetornoDelegate();
            retDelegate.importarRetornoIntegracao(nomeArquivo, orgCodigo, estCodigo, tipo, periodoRetAtrasado, responsavel);
            LOG.debug("FIM - IMPORTAÇÃO DE " + tipo.toUpperCase() + " (" + nomeArquivo + "): " + DateHelper.getSystemDatetime());
        } catch (ImpRetornoControllerException ex) {
            LOG.error("ERRO - IMPORTAÇÃO DE " + tipo.toUpperCase() + " (" + nomeArquivo + "): " + DateHelper.getSystemDatetime());
            throw new ViewHelperException(ex);
        }
    }

    private void concluirRetorno(String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ViewHelperException {
        String tipoEntidade = !TextHelper.isNull(estCodigo) ? "EST" : !TextHelper.isNull(orgCodigo) ? "ORG" : "CSE";
        String codigoEntidade = !TextHelper.isNull(estCodigo) ? estCodigo : !TextHelper.isNull(orgCodigo) ? orgCodigo : CodedValues.CSE_CODIGO_SISTEMA;

        try {
            LOG.debug("INÍCIO - CONCLUSÃO DE RETORNO: " + DateHelper.getSystemDatetime());
            ImpRetornoDelegate retDelegate = new ImpRetornoDelegate();
            retDelegate.finalizarIntegracaoFolha(tipoEntidade, codigoEntidade, responsavel);
            LOG.debug("FIM - CONCLUSÃO DE RETORNO: " + DateHelper.getSystemDatetime());
        } catch (ImpRetornoControllerException ex) {
            LOG.error("ERRO - CONCLUSÃO DE RETORNO: " + DateHelper.getSystemDatetime());
            throw new ViewHelperException(ex);
        }

        geraRelatorioIntegracao(orgCodigo, estCodigo, responsavel);
    }

    private void geraRelatorioIntegracao(String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            LOG.debug("INÍCIO - RELATÓRIO DE INTEGRAÇÃO: " + DateHelper.getSystemDatetime());
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            relatorioController.geraRelatorioIntegracao(estCodigo, orgCodigo, responsavel);
            LOG.debug("FIM - RELATÓRIO DE INTEGRAÇÃO: " + DateHelper.getSystemDatetime());
        } catch (RelatorioControllerException ex) {
            LOG.error("ERRO - RELATÓRIO DE INTEGRAÇÃO: " + DateHelper.getSystemDatetime());
            throw new ViewHelperException(ex);
        }
    }

    private void recalculaMargem(boolean historicoMargem, String tipoEntidade, String codEntidade, AcessoSistema responsavel) throws ViewHelperException {
        try {
            LOG.debug("INÍCIO - RECÁLCULO DE MARGEM: " + DateHelper.getSystemDatetime());

            String tipoEntidadeRecalculo = null;
            List<String> codigosEntidades = null;
            if (!TextHelper.isNull(tipoEntidade)) {
                tipoEntidadeRecalculo = tipoEntidade;
                codigosEntidades = new ArrayList<>();
                codigosEntidades.add(codEntidade);
            } else {
                tipoEntidadeRecalculo = "CSE";
            }

            ServidorDelegate serDelegate = new ServidorDelegate();
            if (historicoMargem) {
                serDelegate.recalculaMargemComHistorico(tipoEntidadeRecalculo, codigosEntidades, responsavel);
            } else {
                serDelegate.recalculaMargem(tipoEntidadeRecalculo, codigosEntidades, responsavel);
            }

            LOG.debug("FIM - RECÁLCULO DE MARGEM: " + DateHelper.getSystemDatetime());
        } catch (MargemControllerException ex) {
            LOG.error("ERRO - RECÁLCULO DE MARGEM: " + DateHelper.getSystemDatetime());
            throw new ViewHelperException(ex);
        }
    }

    private void importarMargemRetorno(String nomeArquivoMargem, String nomeArquivoRetorno, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            LOG.debug("INÍCIO - IMPORTAÇÃO/CONCLUSÃO DE MARGEM/RETORNO (" + nomeArquivoMargem + "," + nomeArquivoRetorno + "): " + DateHelper.getSystemDatetime());
            ImpRetornoDelegate retDelegate = new ImpRetornoDelegate();
            retDelegate.importarMargemRetorno(nomeArquivoMargem, nomeArquivoRetorno, orgCodigo, estCodigo, responsavel);
            LOG.debug("FIM - IMPORTAÇÃO/CONCLUSÃO DE MARGEM/RETORNO (" + nomeArquivoMargem + "," + nomeArquivoRetorno + "): " + DateHelper.getSystemDatetime());
        } catch (ImpRetornoControllerException ex) {
            LOG.error("ERRO - IMPORTAÇÃO/CONCLUSÃO DE MARGEM/RETORNO (" + nomeArquivoMargem + "," + nomeArquivoRetorno + "): " + DateHelper.getSystemDatetime());
            throw new ViewHelperException(ex);
        }
    }

    private void prepararProcessamentoFolha(String nomeArquivoMargem, String nomeArquivoRetorno, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ViewHelperException {
        String tipoEntidade = "CSE";
        String codigoEntidade = CodedValues.CSE_CODIGO_SISTEMA;
        if (!TextHelper.isNull(orgCodigo) && TextHelper.isNull(estCodigo)) {
            tipoEntidade = "ORG";
            codigoEntidade = orgCodigo;
        } else if (TextHelper.isNull(orgCodigo) && !TextHelper.isNull(estCodigo)) {
            tipoEntidade = "EST";
            codigoEntidade = estCodigo;
        }

        try {
            LOG.debug("INÍCIO - PREPARAÇÃO NOVO PROCESSO DE MARGEM/RETORNO (" + nomeArquivoMargem + "," + nomeArquivoRetorno + "): " + DateHelper.getSystemDatetime());
            ProcessarFolhaController controller = ApplicationContextProvider.getApplicationContext().getBean(ProcessarFolhaController.class);
            controller.prepararProcessamento(nomeArquivoMargem, nomeArquivoRetorno, tipoEntidade, codigoEntidade, responsavel);
            LOG.debug("FIM - PREPARAÇÃO NOVO PROCESSO DE MARGEM/RETORNO (" + nomeArquivoMargem + "," + nomeArquivoRetorno + "): " + DateHelper.getSystemDatetime());
        } catch (BeansException ex) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (ZetraException ex) {
            LOG.error("ERRO - PREPARAÇÃO NOVO PROCESSO DE MARGEM/RETORNO (" + nomeArquivoMargem + "," + nomeArquivoRetorno + "): " + DateHelper.getSystemDatetime());
            throw new ViewHelperException(ex);
        }
    }

    public static String obtemArquivoProcessamento(String tipo, String nomeArquivo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) {
        String nomeArquivoComCaminho = ParamSist.getDiretorioRaizArquivos()
                                     + File.separatorChar + tipo;

        if (tipoEntidade.equalsIgnoreCase("CSE")) {
            nomeArquivoComCaminho += File.separatorChar + "cse"
                                   + File.separatorChar + nomeArquivo;

        } else if (tipoEntidade.equalsIgnoreCase("EST")) {
            nomeArquivoComCaminho += File.separatorChar + "est"
                                   + File.separatorChar + codigoEntidade
                                   + File.separatorChar + nomeArquivo;

        } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
            nomeArquivoComCaminho += File.separatorChar + "cse"
                                   + File.separatorChar + codigoEntidade
                                   + File.separatorChar + nomeArquivo;
        }

        return nomeArquivoComCaminho;
    }

    private void imprimePeriodoRetorno(String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            List<String> orgCodigos = null;
            List<String> estCodigos = null;
            if (!TextHelper.isNull(orgCodigo)) {
                orgCodigos = new ArrayList<>();
                orgCodigos.add(orgCodigo);
            }
            if (!TextHelper.isNull(estCodigo)) {
                estCodigos = new ArrayList<>();
                estCodigos.add(estCodigo);
            }

            // Obtém o periodo de exportação
            List<TransferObject> periodoExportacao = new PeriodoDelegate().obtemPeriodoImpRetorno(orgCodigos, estCodigos, false, responsavel);
            // Imprime o periodo de exportação
            LOG.debug("Período de retorno: ");
            ExportaMovimentoHelper.imprimePeriodoExportacao(periodoExportacao);
        } catch (PeriodoException ex) {
            throw new ViewHelperException(ex);
        }
    }

    private void calcularPontuacaoServidores(String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            String tipoEntidade = "CSE";
            List<String> entCodigos = null;
            if (!TextHelper.isNull(orgCodigo) && TextHelper.isNull(estCodigo)) {
                tipoEntidade = "ORG";
                entCodigos = List.of(orgCodigo);
            } else if (TextHelper.isNull(orgCodigo) && !TextHelper.isNull(estCodigo)) {
                tipoEntidade = "EST";
                entCodigos = List.of(estCodigo);
            }

            // Calcula a pontuação dos servidores
            PontuacaoServidorController pontuacaoServidorController = ApplicationContextProvider.getApplicationContext().getBean(PontuacaoServidorController.class);
            pontuacaoServidorController.calcularPontuacao(tipoEntidade, entCodigos, responsavel);
        } catch (Exception ex) {
            throw new ViewHelperException(ex);
        }
    }

    private String ajustaTipoRetornoPeloPeriodo(String nomeArquivo, String orgCodigo, String estCodigo, String tipo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            ImpRetornoDelegate retDelegate = new ImpRetornoDelegate();
            return retDelegate.ajustaTipoRetornoPeloPeriodo(nomeArquivo, orgCodigo, estCodigo, tipo, responsavel);
        } catch (ImpRetornoControllerException ex) {
            throw new ViewHelperException(ex);
        }
    }

    @Override
    public int executar(String args[]) {
        try {
            final String msgErroProcessamentoCompleto = "USE para processamento completo:                 java " + NOME_CLASSE + " P [ARQUIVO_MARGEM] [ARQUIVO_RETORNO] [ORGAO](Opcional) [ESTABELECIMENTO](Opcional)";
            final String msgErroMargem =                "USE para importação de margem:                   java " + NOME_CLASSE + " M [NOME_ARQUIVO] [MARGEM_TOTAL](0=Não; 1=Sim) [GERAR_TRANSFERIDOS](0=Não; 1=Sim) [IMPORTAR_TRANSFERIDOS_GERADOS](0=Não; 1=Sim) [ORGAO](Opcional) [ESTABELECIMENTO](Opcional)";
            final String msgErroMargemComplementar =    "USE para importação de margem complementar:      java " + NOME_CLASSE + " MC [NOME_ARQUIVO] [ORGAO](Opcional) [ESTABELECIMENTO](Opcional)";
            final String msgErroTransferidos =          "USE para importação de transferidos:             java " + NOME_CLASSE + " T [NOME_ARQUIVO] [ORGAO](Opcional) [ESTABELECIMENTO](Opcional)";
            final String msgErroRetorno =               "USE para importação de retorno:                  java " + NOME_CLASSE + " R [NOME_ARQUIVO] [ORGAO](Opcional) [ESTABELECIMENTO](Opcional) [VALIDA_PERIODOS_LINHAS](0=Não; 1=Sim. Opcional. Se sim, não processa se houver combinação de parcelas atrasadas e atuais/férias)";
            final String msgErroCritica =               "USE para importação de critica:                  java " + NOME_CLASSE + " X [NOME_ARQUIVO] [ORGAO](Opcional) [ESTABELECIMENTO](Opcional) [VALIDA_PERIODOS_LINHAS](0=Não; 1=Sim. Opcional. Se sim, não processa se houver combinação de parcelas atrasadas e atuais/férias)";
            final String msgErroAtrasado =              "USE para importação de retorno atrasado:         java " + NOME_CLASSE + " A [NOME_ARQUIVO] [ORGAO](\"todos\", para executar para todos os órgãos) [ESTABELECIMENTO](\"todos\", para executar para todos os estabelecimentos) [PERIODO](MM/AAAA ou DD/MM/AAAA Opcional) [SOMA_PARCELA](0=Não; 1=Sim. Soma valor do arquivo de atrasado ao valor já realizado no histórico de parcelas do período em questão. Opcional)";
            final String msgErroConclusao =             "USE para conclusão de retorno:                   java " + NOME_CLASSE + " C [ORGAO](Opcional) [ESTABELECIMENTO](Opcional)";
            final String msgErroRelatorioIntegracao =   "USE para gerar relatório de integração:          java " + NOME_CLASSE + " I [ORGAO](Opcional) [ESTABELECIMENTO](Opcional)";
            final String msgErroRecalculoMargem =       "USE para recálculo de margem:                    java " + NOME_CLASSE + " RM [HISTORICO_MARGEM](0=Não; 1=Sim) [ORGAO](Opcional) [ESTABELECIMENTO](Opcional)";
            final String msgErroListarPeriodoRetorno =  "USE para listar o período de retorno:            java " + NOME_CLASSE + " LP [ORGAO](Opcional) [ESTABELECIMENTO](Opcional)";
            final String msgErroCalcularPontuacao    =  "USE para calcular a pontuação dos servidores:    java " + NOME_CLASSE + " SC [ORGAO](Opcional) [ESTABELECIMENTO](Opcional)";
            final String msgErroPreNovoProcessoGeral =  "USE para preparação do novo processo:            java " + NOME_CLASSE + " PRE [ARQUIVO_MARGEM] [ARQUIVO_RETORNO] [ORGAO](Opcional) [ESTABELECIMENTO](Opcional)";

            final String msgErroPrincipal = "\n"
            	    + msgErroProcessamentoCompleto+ "\n"
                    + msgErroMargem + "\n"
                    + msgErroMargemComplementar + "\n"
                    + msgErroTransferidos + "\n"
                    + msgErroRetorno + "\n"
                    + msgErroCritica + "\n"
                    + msgErroAtrasado + "\n"
                    + msgErroConclusao + "\n"
                    + msgErroRelatorioIntegracao + "\n"
                    + msgErroRecalculoMargem + "\n"
                    + msgErroListarPeriodoRetorno + "\n"
                    + msgErroCalcularPontuacao + "\n"
                    + msgErroPreNovoProcessoGeral
                    ;

            if (args.length < 1) {
                LOG.error(msgErroPrincipal);
                return -1;
            } else {
                AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                LOG.debug(ParamSist.getDiretorioRaizArquivos());

                ProcessaRetorno p = new ProcessaRetorno();
                String tipoOperacao = args[0];

                if (tipoOperacao.equals("P")) {
                    if (args.length < 3) {
                        LOG.error(msgErroProcessamentoCompleto);
                    } else {
                        String orgCodigo = args.length == 4 ? args[3] : null;
                        String estCodigo = args.length == 5 ? args[4] : null;
                        p.importarMargemRetorno(args[1], args[2], orgCodigo, estCodigo, responsavel);
                    }
                } else if (tipoOperacao.equals("PRE")) {
                    if (args.length < 3) {
                        LOG.error(msgErroPreNovoProcessoGeral);
                    } else {
                        String orgCodigo = args.length == 4 ? args[3] : null;
                        String estCodigo = args.length == 5 ? args[4] : null;
                        p.prepararProcessamentoFolha(args[1], args[2], orgCodigo, estCodigo, responsavel);
                    }
                } else if (tipoOperacao.equals("M")) {
                    if (args.length < 5) {
                        LOG.error(msgErroMargem);
                    } else {
                        boolean margemTotal = args[2].equals("1");
                        boolean gerarTransferidos = args[3].equals("1");
                        boolean processarTransferidos = args[4].equals("1");
                        String orgCodigo = args.length == 6 ? args[5] : null;
                        String estCodigo = args.length == 7 ? args[6] : null;

                        if (!margemTotal && gerarTransferidos) {
                            LOG.info("O arquivo de transferidos só pode ser gerado quando for margem total.");
                        } else if (!gerarTransferidos && processarTransferidos) {
                            LOG.info("Impossível processar transferidos quando não são gerados.");
                        } else {
                            p.importaMargens(args[1], margemTotal, gerarTransferidos, processarTransferidos, orgCodigo, estCodigo, responsavel, false);
                        }
                    }
                } else if (tipoOperacao.equals("MC")) {
                    if (args.length < 2) {
                        LOG.error(msgErroMargemComplementar);
                    } else {
                        String orgCodigo = args.length == 3 ? args[2] : null;
                        String estCodigo = args.length == 4 ? args[3] : null;

                        p.importaMargens(args[1], false, false, false, orgCodigo, estCodigo, responsavel, true);
                    }
                } else if (tipoOperacao.equals("T")) {
                    if (args.length < 2) {
                        LOG.error(msgErroTransferidos);
                    } else {
                        String orgCodigo = args.length == 3 ? args[2] : null;
                        String estCodigo = args.length == 4 ? args[3] : null;
                        p.importaTransferidos(args[1], orgCodigo, estCodigo, responsavel);
                    }
                } else if (tipoOperacao.equals("R")) {
                    if (args.length < 2) {
                        LOG.error(msgErroRetorno);
                    } else {
                        String orgCodigo = args.length >= 3 ? args[2] : null;
                        String estCodigo = args.length >= 4 ? args[3] : null;
                        String validaPeridoArq = args.length >= 5 ? args[4] : null;

                        if (!TextHelper.isNull(validaPeridoArq) && validaPeridoArq.equals("1")) {
                            String tipoRetornoAjustado = p.ajustaTipoRetornoPeloPeriodo(args[1], orgCodigo, estCodigo, RETORNO , responsavel);

                            p.importaRetorno(args[1], tipoRetornoAjustado, orgCodigo, estCodigo, null, responsavel);
                        } else {
                            p.importaRetorno(args[1], RETORNO, orgCodigo, estCodigo, null, responsavel);
                        }
                    }
                } else if (tipoOperacao.equals("X")) {
                    if (args.length < 2) {
                        LOG.error(msgErroCritica);
                    } else {
                        String orgCodigo = args.length >= 3 ? args[2] : null;
                        String estCodigo = args.length >= 4 ? args[3] : null;
                        String validaPeridoArq = args.length >= 5 ? args[4] : null;

                        if (!TextHelper.isNull(validaPeridoArq) && validaPeridoArq.equals("1")) {
                            String tipoRetornoAjustado = p.ajustaTipoRetornoPeloPeriodo(args[1], orgCodigo, estCodigo, CRITICA , responsavel);

                            if (tipoRetornoAjustado.equals(CRITICA_ATRASADO)) {
                                int indicePonto = args[1].lastIndexOf(".");
                                args[1] = args[1].substring(0, indicePonto) + "_critica_atrasa" + args[1].substring(indicePonto, args[1].length());
                            }

                            p.importaRetorno(args[1], tipoRetornoAjustado, orgCodigo, estCodigo, null, responsavel);
                        } else {
                            p.importaRetorno(args[1], CRITICA, orgCodigo, estCodigo, null, responsavel);
                        }
                    }
                } else if (tipoOperacao.equals("A")) {
                    if (args.length < 4) {
                        LOG.error(msgErroAtrasado);
                    } else {
                        boolean somaParcela = false;
                        java.sql.Date periodoRetAtrasado = null;

                        String orgCodigo = !args[2].toLowerCase().equals("todos") ? args[2] : null;
                        String estCodigo = !args[3].toLowerCase().equals("todos") ? args[3] : null;
                        String arg5 = args.length >= 5 ? args[4] : null;

                        if (!TextHelper.isNull(arg5) && arg5.length() > 1) {
                            if (!TextHelper.isNull(arg5)) {
                                try {
                                    if (arg5.length() == 7) {
                                        arg5 = DateHelper.reformat(arg5, "MM/yyyy", "01/MM/yyyy");
                                    } else if (arg5.length() != 10) {
                                        LOG.error("Período deve ser informado no formato MM/AAAA ou DD/MM/AAAA.");
                                        return -1;
                                    }
                                    periodoRetAtrasado = DateHelper.toSQLDate(DateHelper.parse(arg5, LocaleHelper.getDatePattern()));
                                } catch (ParseException ex) {
                                    LOG.error("Período deve ser informado no formato MM/AAAA ou DD/MM/AAAA.");
                                    return -1;
                                }
                            }
                            somaParcela = (args.length == 6 && args[5].equals("1"));
                        } else if (!TextHelper.isNull(arg5) && arg5.length() == 1) {
                            somaParcela = arg5.equals("1");
                        }

                        p.importaRetorno(args[1], !somaParcela ? ATRASADO : ATRASADO_SOMA_PARCELA, orgCodigo, estCodigo, periodoRetAtrasado, responsavel);
                    }
                } else if (tipoOperacao.equals("C")) {
                    if (args.length < 1) {
                        LOG.error(msgErroConclusao);
                    } else {
                        String orgCodigo = args.length == 2 ? args[1] : null;
                        String estCodigo = args.length == 3 ? args[2] : null;
                        p.concluirRetorno(orgCodigo, estCodigo, responsavel);
                    }
                } else if (tipoOperacao.equals("I")) {
                    if (args.length < 1) {
                        LOG.error(msgErroRelatorioIntegracao);
                    } else {
                        String orgCodigo = args.length == 2 ? args[1] : null;
                        String estCodigo = args.length == 3 ? args[2] : null;
                        p.geraRelatorioIntegracao(orgCodigo, estCodigo, responsavel);
                    }
                } else if (tipoOperacao.equals("RM")) {
                    if (args.length < 2) {
                        LOG.error(msgErroRecalculoMargem);
                    } else {
                        boolean historicoMargem = args[1].equals("1");

                        String orgCodigo = args.length >= 3 ? args[2] : null;
                        String estCodigo = args.length == 4 ? args[3] : null;

                        String tipoEntidade = null;
                        String codigoEntidade = null;
                        if (!TextHelper.isNull(orgCodigo) && TextHelper.isNull(estCodigo)) {
                            tipoEntidade = "ORG";
                            codigoEntidade = orgCodigo;
                        } else if (TextHelper.isNull(orgCodigo) && !TextHelper.isNull(estCodigo)) {
                            tipoEntidade = "EST";
                            codigoEntidade = estCodigo;
                        } else if (!TextHelper.isNull(orgCodigo) && !TextHelper.isNull(estCodigo)) {
                            LOG.error(msgErroRecalculoMargem + "\n" +
                                    "Somente um entre órgão e estabelecimento pode ser informado.");
                            return -1;
                        }

                        p.recalculaMargem(historicoMargem, tipoEntidade, codigoEntidade, responsavel);
                    }
                } else if (tipoOperacao.equals("LP")) {
                    String orgCodigo = args.length >= 2 ? args[1] : null;
                    String estCodigo = args.length >= 3 ? args[2] : null;
                    p.imprimePeriodoRetorno(orgCodigo, estCodigo, responsavel);

                } else if (tipoOperacao.equals("SC")) {
                    String orgCodigo = args.length >= 2 ? args[1] : null;
                    String estCodigo = args.length >= 3 ? args[2] : null;
                    p.calcularPontuacaoServidores(orgCodigo, estCodigo, responsavel);

                } else {
                    LOG.error("Opção inválida.");
                    LOG.error(msgErroPrincipal);
                    return -1;
                }

                if (p.qtdLinhasArqTransferidosAcimaPermitido) {
                    LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.warn.cadMargem.qtd.linhas.arq.transferidos.acima.permitido.arg0", responsavel, p.nomeArqTransferidosAcimaPermitido));
                }
                return 0;
            }
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }
}
