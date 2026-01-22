package com.zetra.econsig.web.tag.v4;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.consignacao.StatusAutorizacaoDescontoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: ListaHistoricoContratoTag</p>
 * <p>Description: Tag para listagem dos históricos de ocorrência de uma consignação no leiaute v4.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaHistoricoContratoTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaHistoricoContratoTag.class);

    // Indica se <table></table> deve ser impresso
    private boolean table;
    // Nome do atributo que contém os dados dao consignação
    private String name;
    // Escopo do atributo que contém os dados da consignação
    private String scope;

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

            // Obtém a lista com os anexos da consignação
            List<TransferObject> hist = (List<TransferObject>) pageContext.getAttribute(name, getScopeAsInt(scope));

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if (hist != null && (hist.size() > 0 || responsavel.isSup())) {

                if (table) {
                    if (responsavel.isSup()) {
                        code.append(mostrarOculto(request, responsavel));
                    }
                    code.append(abrirTabela(responsavel));
                }

                code.append(montarCabecalho(responsavel));
                code.append("<tbody>");

                Iterator<TransferObject> it = hist.iterator();
                TransferObject cto = null;

                String ocaData, ocaObs, ocaIpAcesso, tocCodigo, tocDescricao, tmoDescricao;
                String ocaResponsavel, loginOcaResponsavel;
                String tjuDescricao, ufCod, cidNome, djuNumProcesso, djuData, djuTexto;

                boolean habilitaCadDecisaoJud = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel);

                while (it.hasNext()) {
                    cto = it.next();

                    cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, responsavel);

                    ocaData = DateHelper.toDateTimeString((Date)cto.getAttribute(Columns.OCA_DATA));
                    ocaObs = cto.getAttribute(Columns.OCA_OBS) != null ? cto.getAttribute(Columns.OCA_OBS).toString() : "";

                    ocaIpAcesso = cto.getAttribute(Columns.OCA_IP_ACESSO) != null ?  cto.getAttribute(Columns.OCA_IP_ACESSO).toString() : "";

                    loginOcaResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                    ocaResponsavel = (loginOcaResponsavel.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO)) &&
                            cto.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : loginOcaResponsavel;

                    tocCodigo = cto.getAttribute(Columns.TOC_CODIGO).toString();
                    tocDescricao = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
                    tmoDescricao = cto.getAttribute(Columns.TMO_DESCRICAO) != null ?  cto.getAttribute(Columns.TMO_DESCRICAO).toString() : "";
                    Date periodo = (Date) cto.getAttribute(Columns.OCA_PERIODO);
                    ocaObs = StatusAutorizacaoDescontoHelper.formataOcaObsHtml(ocaObs, periodo, tmoDescricao, responsavel);

                    if (habilitaCadDecisaoJud) {
                        tjuDescricao = cto.getAttribute(Columns.TJU_DESCRICAO) != null ? cto.getAttribute(Columns.TJU_DESCRICAO).toString() : "";
                        ufCod = cto.getAttribute(Columns.CID_UF_CODIGO) != null ? cto.getAttribute(Columns.CID_UF_CODIGO).toString() : "";
                        cidNome = cto.getAttribute(Columns.CID_NOME) != null ? cto.getAttribute(Columns.CID_NOME).toString() : "";
                        djuNumProcesso = cto.getAttribute(Columns.DJU_NUM_PROCESSO) != null ? cto.getAttribute(Columns.DJU_NUM_PROCESSO).toString() : "";
                        djuData = cto.getAttribute(Columns.DJU_DATA) != null ? DateHelper.toDateString((Date) cto.getAttribute(Columns.DJU_DATA)) : "";
                        djuTexto = cto.getAttribute(Columns.DJU_TEXTO) != null ? cto.getAttribute(Columns.DJU_TEXTO).toString() : "";

                        // Concatena ao OCA_OBS o texto da decisão judicial
                        ocaObs += montarTextoDecisaoJudicial(tjuDescricao, ufCod, cidNome, djuNumProcesso, djuData, djuTexto, responsavel);
                    }

                    if (tocCodigo.equals(CodedValues.TOC_RELACIONAMENTO_ADE)) {
                        // Monta javascript de link para acesso à ADE relacionada
                        String acaoJavascript = cto.getAttribute("JAVASCRIPT").toString();
                        ocaObs = montarJavascriptRelacionamento(acaoJavascript, ocaObs, responsavel);
                    }

                    boolean existeHistorico = cto.getAttribute("EXISTE_HISTORICO") != null && cto.getAttribute("EXISTE_HISTORICO").equals("S");
                    boolean podeEditar = cto.getAttribute("PODE_EDITAR") != null && cto.getAttribute("PODE_EDITAR").equals("SIM") && responsavel.temPermissao(CodedValues.FUN_REGISTRAR_OCO_CONSIGNACAO);
                    String ocaCodigo = null;
                    if (existeHistorico || podeEditar) {
                       ocaCodigo = cto.getAttribute(Columns.OCA_CODIGO) != null ? (String) cto.getAttribute(Columns.OCA_CODIGO) : "";
                    }

                    if (!responsavel.isSup() && podeEditar) {
                        podeEditar = cto.getAttribute(Columns.USU_CODIGO) != null && cto.getAttribute(Columns.USU_CODIGO).equals(responsavel.getUsuCodigo());
                    }

                    code.append(montarLinhaLista(ocaCodigo, ocaData, ocaResponsavel, tocDescricao, ocaObs, ocaIpAcesso, existeHistorico, podeEditar, responsavel));
                }

                code.append("</tbody>");
                if (table) {
                    code.append(fecharTabela(responsavel));
                }
            }

            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    private String mostrarOculto(HttpServletRequest request, AcessoSistema responsavel) {
        String oculto = request.getParameter("oculto");
        String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");
        String check = "";
        if (request.getParameter("oculto") != null && oculto.equalsIgnoreCase("true") ) {
            check = "checked";
        }
        return "<br>" +
               "<label class=\"exibeHistOculto\">" +
               "<input  name=\"EXIBIR_OCULTOS\" " + check + " type=\"checkbox\" onClick=\"if (this.checked) {doIt('eho','" + TextHelper.forJavaScriptAttribute(adeCodigo) + "')} else {doIt('oho','" + TextHelper.forJavaScriptAttribute(adeCodigo) + "')};\" style=\"vertical-align: middle; margin: 1px;\" />" +
               ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.exibir.oculto", responsavel) +
               "</label>"
               ;
    }

    private String abrirTabela(AcessoSistema responsavel) {
        return "<div class=\"card-body table-responsive p-0\">" +
                "<table class=\"table table-striped table-hover table-responsive\">\n";
    }

    private String fecharTabela(AcessoSistema responsavel) {
        String dataAtual = DateHelper.format(DateHelper.getSystemDatetime(), LocaleHelper.getDateTimePattern());
        String textoRodape = ApplicationResourcesHelper.getMessage("mensagem.detalhe.consignacao.historico.ade", responsavel, dataAtual);
        return  "<tfoot>\n" +
                "  <tr><td colspan=\"6\">" + textoRodape + "</td></tr>\n" +
                "</tfoot>\n" +
                "</table>" +
                "</div>";
    }

    private String montarCabecalho(AcessoSistema responsavel) {
        return "<thead>\n" +
               "  <tr>\n" +
               "    <th class=\"oculta-ocorrencia\" scope=\"col\">" +
               "      <div class=\"form-check\">" +
               "        <input type=\"checkbox\" class=\"form-check-input ml-0\" id=\"checkAll\" name=\"checkAll\" aria-label='" + ApplicationResourcesHelper.getMessage("mensagem.ocultar.ocorrencia.selecione.todas", responsavel) + "' data-bs-toggle=\"tooltip\" data-original-title='" + ApplicationResourcesHelper.getMessage("mensagem.ocultar.ocorrencia.selecione.todas", responsavel) + "'>" +
               "      </div>" +
               "    </th>" +
               "    <th id=\"data\" scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.data", responsavel) + "</th>\n" +
               "    <th id=\"responsavel\" scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.responsavel", responsavel) + "</th>\n" +
               "    <th id=\"tipo\" scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.tipo", responsavel) + "</th>\n" +
               "    <th id=\"descricaoHistorico\" scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.descricao", responsavel) + "</th>\n" +
               "    <th id=\"ipAcesso\" class=\"text-nowrap\" scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.ip.acesso", responsavel) + "</th>\n" +
               "    <th>" + ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel) +"</th>" +
               "  </tr>\n" +
               "</thead>\n"
               ;
    }

    private String montarLinhaLista(String ocaCodigo, String ocaData, String ocaResponsavel, String ocaTipo, String ocaObs, String ocaIpAcesso, boolean existeHistorico, boolean podeEditar, AcessoSistema responsavel) {
        String html = "  <tr class=\"selecionarLinha\">\n" +
                "    <td class=\"ocultarColuna oculta-ocorrencia\" aria-label='" + ApplicationResourcesHelper.getMessage("mensagem.ocultar.ocorrencia.selecione", responsavel) + "' title=\"\" data-bs-toggle=\"tooltip\" data-original-title='" + ApplicationResourcesHelper.getMessage("mensagem.ocultar.ocorrencia.selecione", responsavel) + "'>" +
                "      <div class=\"form-check\">" +
                "       <input type=\"checkbox\" name=\"selecionarCheckBox\" class=\"form-check-input ml-0\" value=\"<%=TextHelper.forHtmlAttribute(rseCodigo)%>\">" +
                "      </div>" +
                "    </td>" +
                "    <td class=\"selecionarColuna\" header=\"data\">" + TextHelper.forHtmlContent(ocaData) + "</td>\n" +
                "    <td class=\"selecionarColuna\" header=\"responsavel\">" + TextHelper.forHtmlContent(ocaResponsavel) + "</td>\n" +
                "    <td class=\"selecionarColuna\" header=\"tipo\">" + TextHelper.forHtmlContent(ocaTipo) + "</td>\n" +
                "    <td class=\"selecionarColuna\" header=\"descricaoHistorico\">" + JspHelper.formataMsgOca(ocaObs) + "</td>\n" +
                "    <td class=\"selecionarColuna\" header=\"ipAcesso\">" + (!TextHelper.isNull(ocaIpAcesso) ? TextHelper.forHtmlContent(ocaIpAcesso) : "-") + "</td>\n" +
                "    <td> "+
                "   <div class=\"actions\">" +
                "   <div class=\"dropdown\">" +
                "   <a class=\"dropdown-toggle ico-action\" href=\"#\" role=\"button\" data-original-title='" + ApplicationResourcesHelper.getMessage("rotulo.botao.opcoes", responsavel) + "' aria-label='" + ApplicationResourcesHelper.getMessage("rotulo.botao.opcoes", responsavel) + "' id=\"userMenu\" data-bs-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">" +
                "    <div class=\"form-inline\">" +
                "      <span class=\"mr-1\" data-bs-toggle=\"tooltip\" data-original-title='" + ApplicationResourcesHelper.getMessage("rotulo.botao.opcoes", responsavel) + "'/><svg>" +
                "      <use xlink:href=\"#i-engrenagem\"></use></svg>" +
                "     </span>" + ApplicationResourcesHelper.getMessage("rotulo.botao.opcoes", responsavel) +
                "    </div>" +
                "   </a>" +
                "   <div class=\"dropdown-menu dropdown-menu-right\" aria-labelledby=\"userMenu\">\n " +
                "    <a class=\"dropdown-item selecionarColuna\" onclick=return false; href=\"#no-back\" aria-label=' " + ApplicationResourcesHelper.getMessage("mensagem.ocultar.ocorrencia.selecione", responsavel) + "' data-bs-toggle=\"tooltip\" data-original-title='" + ApplicationResourcesHelper.getMessage("mensagem.ocultar.ocorrencia.selecione", responsavel) + "'>" + ApplicationResourcesHelper.getMessage("rotulo.botao.ocultar", responsavel) + "</a>";
        if (podeEditar) {
            html += "    <a class=\"dropdown-item\" onclick=\"editarHoa('" + ocaCodigo + "', '" + ocaTipo + "' , '" + ocaObs + "');\" return false;\" href=\"#\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel) + "</a>";
        }
        if (existeHistorico) {
            html += "    <a class=\"dropdown-item\" onclick=\"historicoHoa('" + ocaCodigo + "');\" return false;\" href=\"#\">" + ApplicationResourcesHelper.getMessage("rotulo.historico", responsavel) + "</a>";
        }
        html += "</div></div></div></tr></td>\n";
        return html;
    }

    private String montarJavascriptRelacionamento(String acaoJavascript, String ocaObs, AcessoSistema responsavel) {
        return "<a href=\"#no-back\" onclick=\"" + acaoJavascript + "\">" + ocaObs + "</a>";
    }

    protected String montarTextoDecisaoJudicial(String tjuDescricao, String ufCod, String cidNome, String djuNumProcesso, String djuData, String djuTexto, AcessoSistema responsavel) {
        StringBuilder txtDecisaoJudicial = new StringBuilder();

        if (!TextHelper.isNull(tjuDescricao) && !TextHelper.isNull(djuTexto) && !TextHelper.isNull(djuData)) {
            txtDecisaoJudicial.append("<br><br><b>").append(ApplicationResourcesHelper.getMessage("rotulo.historico.ade.decisao.judicial.titulo", responsavel)).append("</b>");
            txtDecisaoJudicial.append("<br><b>").append(ApplicationResourcesHelper.getMessage("rotulo.historico.ade.decisao.judicial.tipo.justica", responsavel)).append(":</b> ").append(tjuDescricao);
            if (!TextHelper.isNull(cidNome) && !TextHelper.isNull(ufCod)) {
                txtDecisaoJudicial.append("<br><b>").append(ApplicationResourcesHelper.getMessage("rotulo.historico.ade.decisao.judicial.comarca", responsavel)).append(":</b> ").append(cidNome).append("/").append(ufCod);
            }
            if (!TextHelper.isNull(djuNumProcesso)) {
                txtDecisaoJudicial.append("<br><b>").append(ApplicationResourcesHelper.getMessage("rotulo.historico.ade.decisao.judicial.numero.processo", responsavel)).append(":</b> ").append(djuNumProcesso);
            }
            txtDecisaoJudicial.append("<br><b>").append(ApplicationResourcesHelper.getMessage("rotulo.historico.ade.decisao.judicial.data", responsavel)).append(":</b> ").append(djuData);
            txtDecisaoJudicial.append("<br><b>").append(ApplicationResourcesHelper.getMessage("rotulo.historico.ade.decisao.judicial.texto", responsavel)).append(":</b> ").append(djuTexto);
        }

        return txtDecisaoJudicial.toString();
    }
}
