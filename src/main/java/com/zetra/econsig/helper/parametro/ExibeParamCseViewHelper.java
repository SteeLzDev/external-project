package com.zetra.econsig.helper.parametro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ExibeParamCseViewHelper</p>
 * <p>Description: Monta para visualização os parâmetros de consignante editados anteriormente.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExibeParamCseViewHelper {

    public static String exibeParamCse(HttpServletRequest request, AcessoSistema responsavel) {
        StringBuilder corpo = new StringBuilder();
        List<TransferObject> paramSist = null;

        try {
            ParametroDelegate parDelegate = new ParametroDelegate();
            if (responsavel.isCse()) {
                paramSist = parDelegate.selectParamSistCse(CodedValues.TPC_SIM, null, null, null, responsavel);
            } else if (responsavel.isSup()) {
                paramSist = parDelegate.selectParamSistCse(null, null, CodedValues.TPC_SIM, null, responsavel);
            }
        } catch (ParametroControllerException ex) {
            paramSist = new ArrayList<>();
        }

        corpo.append("<table width=\"60%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"1\" class=\"TabelaEntradaDeDados\">");

        String tpcCodigo, tpcDescricao, tpcDominio, psiVlr, controle;
        String gpsDescricao, gpsDescricaoAnterior = null;
        TransferObject next = null;

        Iterator<TransferObject> it = paramSist.iterator();
        while (it.hasNext()) {
            next = it.next();

            tpcCodigo = next.getAttribute(Columns.TPC_CODIGO).toString();
            tpcDescricao = next.getAttribute(Columns.TPC_DESCRICAO).toString();
            tpcDominio = next.getAttribute(Columns.TPC_DOMINIO).toString();

            gpsDescricao = (next.getAttribute(Columns.GPS_DESCRICAO) != null ? next.getAttribute(Columns.GPS_DESCRICAO).toString() : ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel));

            psiVlr = JspHelper.verificaVarQryStr (request, tpcCodigo);
            controle = JspHelper.montaValor(tpcCodigo, tpcDominio, psiVlr, false);

            if (gpsDescricaoAnterior == null || !gpsDescricaoAnterior.equals(gpsDescricao)) {
                corpo.append("<tr><td colspan=\"2\" valign=\"baseline\" nowrap class=\"tabelatopo\">&nbsp;").append(TextHelper.forHtmlContent(gpsDescricao)).append("</td></tr>");
            }

            corpo.append("<tr>");
            corpo.append("<td class=\"TLEDmeio\" align=\"right\" width=\"50%\">").append(TextHelper.forHtmlContent(tpcDescricao)).append(":&nbsp;</td>");
            corpo.append("<td class=\"CEDmeio\">&nbsp;").append(controle).append("</td>");
            corpo.append("</tr>");

            gpsDescricaoAnterior = gpsDescricao;
        }

        corpo.append("</table>");
        return corpo.toString();
    }
}
