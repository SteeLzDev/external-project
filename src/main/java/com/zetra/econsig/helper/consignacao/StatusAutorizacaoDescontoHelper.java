package com.zetra.econsig.helper.consignacao;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: StatusAutorizacaoDescontoHelper</p>
 * <p>Description: Helper para manipulação de status da autorização desconto.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusAutorizacaoDescontoHelper {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(StatusAutorizacaoDescontoHelper.class);

    public static String formataOcaObs(String ocaObs, String tmoDescricao, AcessoSistema responsavel) {
        return formataOcaObs(ocaObs, null, tmoDescricao, false, responsavel);
    }

    public static String formataOcaObsHtml(String ocaObs, String tmoDescricao, AcessoSistema responsavel) {
        return formataOcaObs(ocaObs, null, tmoDescricao, true, responsavel);
    }

    public static String formataOcaObsHtml(String ocaObs, java.util.Date ocaPeriodo, String tmoDescricao, AcessoSistema responsavel) {
        return formataOcaObs(ocaObs, ocaPeriodo, tmoDescricao, true, responsavel);
    }

    private static String formataOcaObs(String ocaObs, java.util.Date ocaPeriodo, String tmoDescricao, boolean html, AcessoSistema responsavel) {
        if (ocaObs != null) {
            try {
                if (ocaObs.startsWith(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.prefixo", responsavel))) {
                    StatusAutorizacaoDesconto sad = StatusAutorizacaoDesconto.getInstance();

                    String strComplemento = "";
                    String para = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.para", responsavel) + " ";

                    if (ocaObs.indexOf(".") > 0) {
                        strComplemento = ocaObs.substring(ocaObs.indexOf(".") + 1, ocaObs.length());
                        ocaObs = ApplicationResourcesHelper.getMessage("rotulo.status.autorizacao.helper.nova.situacao", responsavel) + " " + sad.getDescricao(ocaObs.substring(ocaObs.indexOf(para) + para.length(), ocaObs.indexOf(".") + 0));
                        ocaObs += !TextHelper.isNull(tmoDescricao) ? ((html ? "<br>" : "") + " " + ApplicationResourcesHelper.getMessage("rotulo.motivo.arg0", responsavel, tmoDescricao)) : "";
                        ocaObs += !TextHelper.isNull(strComplemento) ? ((html ? "<br>" : "") + " " + ApplicationResourcesHelper.getMessage("rotulo.observacao.arg0", responsavel, strComplemento)) : "";
                    } else {
                        ocaObs = ApplicationResourcesHelper.getMessage("rotulo.status.autorizacao.helper.nova.situacao", responsavel) + " " + sad.getDescricao(ocaObs.substring(ocaObs.indexOf(para) + para.length(), ocaObs.length()));
                    }
                } else if (!TextHelper.isNull(tmoDescricao)) {
                    String strComplemento = ocaObs.substring(ocaObs.lastIndexOf(".") + 1, ocaObs.length());
                    ocaObs = ocaObs.substring(0, ocaObs.lastIndexOf(".") >= 0 ? ocaObs.lastIndexOf(".") : 0);
                    ocaObs += !TextHelper.isNull(tmoDescricao) ? ((html ? "<br>" : "") + " " + ApplicationResourcesHelper.getMessage("rotulo.motivo.arg0", responsavel, tmoDescricao)) : "";
                    ocaObs += !TextHelper.isNull(strComplemento) ? ((html ? "<br>" : "") + " " + ApplicationResourcesHelper.getMessage("rotulo.observacao.arg0", responsavel, strComplemento)) : "";
                }

                if (ocaPeriodo != null) {
                    String ocaPeriodoRef = DateHelper.toPeriodString(ocaPeriodo);
                    ocaObs = new StringBuilder(ocaObs).append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.oca.info.referencia", responsavel, ocaPeriodoRef)).toString();
                }
            } catch (IndexOutOfBoundsException e) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.formatacao.ocorrencia", responsavel));
            } catch (NullPointerException e) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.formatacao.ocorrencia", responsavel));
            }
        }
        return ocaObs;
    }
}
