package com.zetra.econsig.web.tag.v4;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.consignacao.StatusAutorizacaoDescontoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

public class ListaHistoricoDespesaComumTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaHistoricoDespesaComumTag.class);

    // Indica se <table></table> deve ser impresso
    private boolean table;
    // Nome do atributo que contém os dados dao consignação
    private String name;
    // Escopo do atributo que contém os dados da consignação
    private String scope;

    protected AcessoSistema responsavel;

    public void setTable(boolean table) {
        this.table = table;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int doEndTag() throws JspException {
        try {
            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            // Obtém a lista com os anexos da despesa comum
            List<TransferObject> hist = (List<TransferObject>) pageContext.getAttribute(name, getScopeAsInt(scope));

            if (hist != null && hist.size() > 0) {
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                responsavel = JspHelper.getAcessoSistema(request);

                if (table) {
                    code.append(abrirTabela());
                }

                code.append(montarCabecalho());

                Iterator<TransferObject> it = hist.iterator();
                TransferObject cto = null;

                String odcData, odcObs, odcIpAcesso, tocDescricao;
                String odcResponsavel, loginOdcResponsavel;

                String cssLinha = "Li";
                while (it.hasNext()) {
                    cto = it.next();

                    cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, responsavel);

                    odcData = DateHelper.toDateTimeString((Date)cto.getAttribute(Columns.ODC_DATA));
                    odcObs = cto.getAttribute(Columns.ODC_OBS).toString();
                    odcIpAcesso = cto.getAttribute(Columns.ODC_IP_ACESSO) != null ?  cto.getAttribute(Columns.ODC_IP_ACESSO).toString() : "";

                    loginOdcResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                    odcResponsavel = (loginOdcResponsavel.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO)) &&
                            cto.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : loginOdcResponsavel;

                    tocDescricao = cto.getAttribute(Columns.TOC_DESCRICAO).toString();

                    odcObs = StatusAutorizacaoDescontoHelper.formataOcaObsHtml(odcObs, null, responsavel);

                    code.append(montarLinhaLista(odcData, odcResponsavel, tocDescricao, odcObs, odcIpAcesso, cssLinha));
                    cssLinha = (cssLinha.equalsIgnoreCase("Li") ? "Lp" : "Li");
                }

                if (table) {
                    code.append(fecharTabela());
                }
            }

            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    @Override
    protected String abrirTabela() {
        return "<table class=\"table table-striped table-hover table-responsive\">\n";
    }

    @Override
    protected String fecharTabela() {
        return "</table>";
    }

    private String montarCabecalho() {
        return "<thead>\n" +
               "  <tr>\n" +
               "    <th id=\"data\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.data", responsavel) + "</th>\n" +
               "    <th id=\"responsavel\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.responsavel", responsavel) + "</th>\n" +
               "    <th id=\"tipo\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.tipo", responsavel) + "</th>\n" +
               "    <th id=\"descricao\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.descricao", responsavel) + "</th>\n" +
               "    <th id=\"ipAcesso\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.ip.acesso", responsavel) + "</th>\n" +
               "  </tr>\n" +
               "</thead>\n"
               ;
    }

    private String montarLinhaLista(String odcData, String odcResponsavel, String odcTipo, String odcObs, String odcIpAcesso, String cssLinha) {
        return "  <tr>\n" +
               "    <td header=\"data\">" + TextHelper.forHtmlContent(odcData) + "</td>\n" +
               "    <td header=\"responsavel\">" + TextHelper.forHtmlContent(odcResponsavel) + "</td>\n" +
               "    <td header=\"tipo\">" + TextHelper.forHtmlContent(odcTipo) + "</td>\n" +
               "    <td header=\"descricao\">" + TextHelper.forHtmlContent(odcObs) + "</td>\n" +
               "    <td header=\"odcIpAcesso\">" + TextHelper.forHtmlContent(odcIpAcesso) + "</td>\n" +
               "  </tr>\n"
               ;
    }
}