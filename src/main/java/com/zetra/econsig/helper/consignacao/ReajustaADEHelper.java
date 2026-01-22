package com.zetra.econsig.helper.consignacao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ServicoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ReajustaADEHelper</p>
 * <p>Description: Helper Class para Operação de Reajuste de ADE</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReajustaADEHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReajustaADEHelper.class);

    /**
     * Reajusta as ADE de uma consignatária
     * @param csaCodigo     : código da consignatária
     * @param cseCodigo     : código do consignante
     * @param vlrReajuste   : valor do reajuste a ser aplicado nas ADE´s
     * @param tipoReajuste  : se percentual ou valor em moeda corrente
     * @param regras        : grupo regras para definir para definir o conjunto de
     *                        ADE´s a serem reajustadas
     * @param validar       : define se faz somente a validação do reajuste
     * @param responsavel   : usuário responsavel
     * @param ipUsuario     : IP do usuário logado no sistema
     * @throws ViewHelperException
     */
    public static void reajustaAdes(String csaCodigo, String cseCodigo,
            BigDecimal vlrReajuste, String tipoReajuste,
            CustomTransferObject regras, boolean validar,
            AcessoSistema responsavel) throws ViewHelperException {
        AutorizacaoDelegate adeDelegate;
        ConsignacaoDelegate consigDelegate;

        BigDecimal vlrAde;
        BigDecimal vlrDepois;
        BigDecimal reajuste;
        BigDecimal vlrTAC;
        BigDecimal vlrIOF;
        BigDecimal vlrLiquido;
        BigDecimal vlrMesVinc;
        String limitacaoTipo = "";
        BigDecimal limitacaoVlr = null;
        String msgCritica;
        String msgOk = validar ? ApplicationResourcesHelper.getMessage("mensagem.informacao.reajuste.validado", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informacao.reajuste.sucesso", responsavel);
        List<String> critica = new ArrayList<String>();
        List<String> criticaRegras = new ArrayList<String>();
        List<TransferObject> lstReajusta = null;

        //Monta demonstrativo de regras usadas para compor o arquivo de critica
        try {
            if (regras != null && regras.getAtributos().size() > 0) {
                if (regras.getAttribute("vlr_igual") != null && !regras.getAttribute("vlr_igual").equals("")) {
                    criticaRegras.add("#" + TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.reajuste.valor.desconto.folha.igual", responsavel), " ", 50, true) +
                            " : " + TextHelper.formataMensagem(NumberHelper.reformat(regras.getAttribute("vlr_igual").toString(), "en", NumberHelper.getLang()), " ", 12, false));
                }
                if (regras.getAttribute("vlr_maior_igual") != null && !regras.getAttribute("vlr_maior_igual").equals("")) {
                    criticaRegras.add("#" + TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.reajuste.valor.desconto.folha.maior", responsavel), " ", 50, true) +
                            " : " + TextHelper.formataMensagem(NumberHelper.reformat(regras.getAttribute("vlr_maior_igual").toString(), "en", NumberHelper.getLang()), " ", 12, false));
                }
                if (regras.getAttribute("vlr_menor_igual") != null && !regras.getAttribute("vlr_menor_igual").equals("")) {
                    criticaRegras.add("#" + TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.reajuste.valor.desconto.folha.menor", responsavel) , " ", 50, true) +
                            " : " + TextHelper.formataMensagem(NumberHelper.reformat(regras.getAttribute("vlr_menor_igual").toString(), "en", NumberHelper.getLang()), " ", 12, false));
                }
                if (regras.getAttribute("verba") != null && !regras.getAttribute("verba").equals("")) {
                    criticaRegras.add("#" + TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.reajuste.verba.igual", responsavel), " ", 50, true) +
                            " : " + TextHelper.formataMensagem(regras.getAttribute("verba").toString(), " ", 12, false));
                }
                if (regras.getAttribute("servico") != null && !regras.getAttribute("servico").equals("")) {
                    String servico = "";
                    try {
                        ServicoDelegate svcDelegate = new ServicoDelegate();
                        TransferObject svcTO = svcDelegate.findServico((String) regras.getAttribute("servico"));
                        servico = (String) svcTO.getAttribute(Columns.SVC_DESCRICAO);
                    } catch (ServicoControllerException e) {
                        throw new ZetraException(e);
                    }
                    criticaRegras.add("#" + TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.reajuste.servico.igual", responsavel), " ", 50, true) +
                            " : " + servico.toString());
                }
                if (regras.getAttribute("padrao_verba") != null && !regras.getAttribute("padrao_verba").equals("")) {
                    String nomeCampo = ApplicationResourcesHelper.getMessage("rotulo.verba.singular", responsavel);
                    if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel)) {
                        nomeCampo += " " + ApplicationResourcesHelper.getMessage("rotulo.e", responsavel) + " " + ApplicationResourcesHelper.getMessage("rotulo.indice.singular", responsavel).toLowerCase();
                    }

                    criticaRegras.add("#" + TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.reajuste.campo.seguinte.padrao", responsavel, nomeCampo), " ", 50, true) +
                            " : " + TextHelper.formataMensagem(regras.getAttribute("padrao_verba").toString() + (regras.getAttribute("padrao_indice") != null ? regras.getAttribute("padrao_indice").toString() : ""), " ", 12, false));
                }
                if (regras.getAttribute("limitado_vlr") != null && !regras.getAttribute("limitado_vlr").equals("")) {
                    criticaRegras.add("#" + TextHelper.formataMensagem(regras.getAttribute("limitado_tipo").toString().equals("reajuste") ? ApplicationResourcesHelper.getMessage("rotulo.reajuste.limitando.valor.reajuste", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.reajuste.limitando.valor.novo.desconto.reajustado", responsavel), " ", 50, true) +
                            " : " + TextHelper.formataMensagem(NumberHelper.reformat(regras.getAttribute("limitado_vlr").toString(), "en", NumberHelper.getLang()), " ", 12, false));
                }
            }
        } catch (Exception ex) {
            throw new ViewHelperException(ex);
        }
        try {
            if (!validar) {
                LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConsignante(cseCodigo);
                log.setConsignataria(csaCodigo);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.log.reajuste.automatico.consignacoes", responsavel));
                log.write();
            }
            adeDelegate = new AutorizacaoDelegate();
            consigDelegate = new ConsignacaoDelegate();
            lstReajusta = adeDelegate.lstReajustaAde(csaCodigo, regras, responsavel);
            if ((lstReajusta.size() > 5000) &&
                    !(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                    Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 17 ||
                    (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE) <= 7 * 60 + 30))) {
                throw new AutorizacaoControllerException("mensagem.erro.reajuste.maior.limite.horario", responsavel);
            }
        } catch (Exception ex) {
            throw new ViewHelperException(ex);
        }

        if (regras != null && regras.getAtributos().size() > 0) {
            if (regras.getAttribute("limitado_vlr") != null &&
                    !regras.getAttribute("limitado_vlr").equals("")) {
                limitacaoTipo = regras.getAttribute("limitado_tipo").toString();
                limitacaoVlr = new BigDecimal(regras.getAttribute("limitado_vlr").toString());
            }
        }

        int totalInvalidos = 0;
        int totalAde = lstReajusta.size();
        Iterator<TransferObject> it = lstReajusta.iterator();
        TransferObject cto = null;
        while (it.hasNext()) {
            msgCritica = "";
            cto = it.next();
            // Dados da ADE
            vlrAde = new BigDecimal(cto.getAttribute(Columns.ADE_VLR).toString());
            reajuste = new BigDecimal("0.00");

            if (tipoReajuste.equals("percentual")) {
                reajuste = vlrAde.multiply(vlrReajuste.setScale(10, java.math.RoundingMode.HALF_UP).divide(new BigDecimal(100), java.math.RoundingMode.HALF_UP)).setScale(2, java.math.RoundingMode.HALF_UP);
            } else {
                reajuste = vlrReajuste;
            }

            vlrDepois = vlrAde.add(reajuste);

            vlrTAC = new BigDecimal(cto.getAttribute(Columns.ADE_VLR_TAC) == null ? "0.0" : cto.getAttribute(Columns.ADE_VLR_TAC).toString());
            vlrIOF = new BigDecimal(cto.getAttribute(Columns.ADE_VLR_IOF) == null ? "0.0" : cto.getAttribute(Columns.ADE_VLR_IOF).toString());
            vlrLiquido = new BigDecimal(cto.getAttribute(Columns.ADE_VLR_LIQUIDO) == null ? "0.0" : cto.getAttribute(Columns.ADE_VLR_LIQUIDO).toString());
            vlrMesVinc = new BigDecimal(cto.getAttribute(Columns.ADE_VLR_MENS_VINC) == null ? "0.0" : cto.getAttribute(Columns.ADE_VLR_MENS_VINC).toString());

            if (limitacaoTipo.equals("reajuste") && limitacaoVlr != null && reajuste.abs().compareTo(limitacaoVlr) > 0) {
                vlrDepois = vlrAde.add(limitacaoVlr.multiply(new BigDecimal(reajuste.signum())));
            } else if (limitacaoTipo.equals("desconto") && limitacaoVlr != null) {
                if (reajuste.signum() == -1 && vlrDepois.compareTo(limitacaoVlr) < 0) {
                    vlrDepois = limitacaoVlr.compareTo(vlrAde) < 0 ? limitacaoVlr : vlrAde;
                } else if (reajuste.signum() == 1 && vlrDepois.compareTo(limitacaoVlr) > 0) {
                    vlrDepois = limitacaoVlr.compareTo(vlrAde) > 0 ? limitacaoVlr : vlrAde;
                }
            }
            try {
                int qtdeParcelasPagas = cto.getAttribute(Columns.ADE_PRD_PAGAS) != null ? Integer.valueOf(cto.getAttribute(Columns.ADE_PRD_PAGAS).toString()) : 0;

                AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(cto.getAttribute(Columns.ADE_CODIGO).toString(), vlrDepois,
                        cto.getAttribute(Columns.ADE_PRAZO) == null ? null : Integer.valueOf(cto.getAttribute(Columns.ADE_PRAZO).toString()) - qtdeParcelasPagas,
                        cto.getAttribute(Columns.ADE_IDENTIFICADOR).toString(), validar, (String) cto.getAttribute(Columns.ADE_INDICE), vlrTAC, vlrIOF,
                        vlrLiquido, vlrMesVinc, null, null, null);
                alterarParam.setAdePeriodicidade((String) cto.getAttribute(Columns.ADE_PERIODICIDADE));
                alterarParam.setAtualizacaoReajuste(true);
                consigDelegate.alterarConsignacao(alterarParam, responsavel);
            } catch (NumberFormatException ex) {
                msgCritica = ex.getMessage();
            } catch (AutorizacaoControllerException ex) {
                msgCritica = ex.getMessage();
            }
            if (!msgCritica.equals("")) {
                totalInvalidos++;
            }
            critica.add(TextHelper.formataMensagem(cto.getAttribute(Columns.ADE_NUMERO).toString(), " ", 11, false) + " " +
                    TextHelper.formataMensagem(cto.getAttribute(Columns.RSE_MATRICULA).toString(), " ", 12, false) + " " +
                    TextHelper.formataMensagem(cto.getAttribute(Columns.SER_CPF).toString(), " ", 16, true) +
                    TextHelper.formataMensagem(cto.getAttribute(Columns.SER_NOME).toString(), " ", 30, true, true) +
                    TextHelper.formataMensagem(cto.getAttribute(Columns.ADE_PRAZO) == null ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : cto.getAttribute(Columns.ADE_PRAZO).toString(), " ", 14, false) +
                    TextHelper.formataMensagem(vlrAde.toString(), " ", 15, false) +
                    TextHelper.formataMensagem(msgCritica.equals("") ? vlrDepois.toString() : vlrAde.toString(), " ", 17, false) + " " +
                    TextHelper.formataMensagem(msgCritica.equals("") ? msgOk : msgCritica, " ", 50, true));
        }

        try {
            // Grava arquivo contendo as parcelas não encontradas no sistema
            String pathSaida = ParamSist.getDiretorioRaizArquivos()
                    + File.separatorChar + "reajuste"
                    + File.separatorChar + "csa"
                    + File.separatorChar + csaCodigo
                    + File.separatorChar
                    ;

            File dir = new File(pathSaida);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new ViewHelperException("mensagem.erro.reajuste.diretorio.nao.existe.arquivo.critica", responsavel);
            }

            String tipo = validar ? "validacao" : "critica";
            String nomeArqSaida = pathSaida + tipo + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".txt";

            PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaida)));
            arqSaida.println(TextHelper.formataMensagem("", "#", CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(validar ? ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.validacao.reajuste.contratos", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.critica.reajuste.contratos", responsavel), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.data", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "dd/MM/yyyy-HHmmss")), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem("", "#", CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));

            // Imprime as linhas de critica no arquivo
            if (criticaRegras.size() > 0) {
                arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.regras.utilizadas", responsavel), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
                arqSaida.println(TextHelper.join(criticaRegras, System.getProperty("line.separator")));
                arqSaida.println(TextHelper.formataMensagem("", "#", CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            }

            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.total.registro.satisfazem.regras", responsavel, String.valueOf(totalAde)), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.total.registro.validos", responsavel, String.valueOf(totalAde - totalInvalidos)), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.total.registro.invalidos", responsavel, String.valueOf(totalInvalidos)), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem("", "#", CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println("#" + TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.ade", responsavel), " ", 10, false) + " " +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.matricula", responsavel), " ", 12, false) + " " +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.cpf", responsavel), " ", 16, true) +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.nome", responsavel), " ", 30, true) +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.prazo", responsavel), " ", 14, false) +
                    TextHelper.formataMensagem(" " + ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.valor", responsavel), " ", 15, false) +
                    " " + ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.valor.reajustado", responsavel) + " " +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.reajuste.mensagem", responsavel), " ", 50, true));
            arqSaida.println(TextHelper.formataMensagem("", CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            // Imprime as linhas de critica no arquivo
            if (critica.size() > 0) {
                arqSaida.println(TextHelper.join(critica, System.getProperty("line.separator")));
            }
            arqSaida.close();
            LOG.debug("FIM ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());

            // Compacta os arquvivos gerados em apenas um
            LOG.debug("compacta os arquivos: " + DateHelper.getSystemDatetime());
            String nomeArqZip = nomeArqSaida.replaceAll(".txt", ".zip");
            FileHelper.zip(nomeArqSaida, nomeArqZip);
            LOG.debug("fim - compacta os arquivos: " + DateHelper.getSystemDatetime());
            FileHelper.delete(nomeArqSaida);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
