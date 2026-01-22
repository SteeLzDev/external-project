package com.zetra.econsig.web.tag.v4;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: ListaSolicitacaoAutorizacaoTag</p>
 * <p>Description: Tag para listagem das solicitacao de autorização para leiaute v4.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSolicitacaoAutorizacaoTag extends ZetraTagSupport  {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaSolicitacaoAutorizacaoTag.class);

    private boolean table;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setTable(boolean table) {
        this.table = table;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int doEndTag() throws JspException {
        try {
            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            // Obtém a lista com os anexos da consignação
            List<TransferObject> historicos = (List<TransferObject>) pageContext.getAttribute(name, getScopeAsInt("request"));

            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if (historicos != null && historicos.size() > 0) {
                if (table) {
                    code.append(abrirTabela(responsavel));
                }

                code.append(montarCabecalho(responsavel));
                code.append("<tbody>");

                Iterator<TransferObject> it = historicos.iterator();
                TransferObject historico = null;
                String soaData, soaResponsavel, ssoDescricao, osoDescricao, soaResposta, dataResposta;
                String cssLinha = "Li";
                while (it.hasNext()) {
                    historico = it.next();

                    historico = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) historico, null, responsavel);

                    soaData = DateHelper.toDateTimeString((Date) historico.getAttribute(Columns.SOA_DATA));
                    dataResposta = historico.getAttribute(Columns.SOA_DATA_RESPOSTA) != null ? DateHelper.toDateTimeString((Date) historico.getAttribute(Columns.SOA_DATA_RESPOSTA)) : "";
                    soaResponsavel = historico.getAttribute(Columns.USU_LOGIN) != null ? historico.getAttribute(Columns.USU_LOGIN).toString() : "";
                    ssoDescricao = historico.getAttribute(Columns.SSO_DESCRICAO) != null ? historico.getAttribute(Columns.SSO_DESCRICAO).toString() : "";
                    osoDescricao = historico.getAttribute(Columns.OSO_DESCRICAO) != null ? historico.getAttribute(Columns.OSO_DESCRICAO).toString() : "";
                    soaResposta = historico.getAttribute(Columns.SOA_OBS) != null ? historico.getAttribute(Columns.SOA_OBS).toString() : "";
                    code.append(montarLinhaLista(soaData, soaResponsavel, ssoDescricao, osoDescricao, soaResposta, dataResposta, request, responsavel));

                    cssLinha = (cssLinha.equalsIgnoreCase("Li") ? "Lp" : "Li");
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

    protected String abrirTabela(AcessoSistema responsavel) {
        return "<table class=\"table table-striped table-hover table-responsive\">\n";
    }

    protected String fecharTabela(AcessoSistema responsavel) {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        String subTitulo = (String) request.getAttribute("_paginacaoSubTitulo");

        String textoRodape = ApplicationResourcesHelper.getMessage("mensagem.detalhe.historico.solicitacao.autorizacao.validar.documentos", responsavel);
        String tfoot = "";

        if(subTitulo != null) {
            tfoot = "<tfoot>\n" +
                    "  <tr><td colspan=\"5\">"+textoRodape+" - <span class=\"font-italic\">"+subTitulo+"</span></td></tr>"+
                    "</tfoot>\n" +
                    "</table>\n";
        }else {
            tfoot = "<tfoot>\n" +
                    "  <tr><td colspan=\"5\">"+textoRodape+"</td></tr>"+
                    "</tfoot>\n" +
                    "</table>\n";
        }
        return tfoot;

    }

    protected String montarCabecalho(AcessoSistema responsavel) {
        StringBuilder cabecalhoPadrao = new StringBuilder();

        cabecalhoPadrao.append("<thead>");
        cabecalhoPadrao.append("<tr>");
        cabecalhoPadrao.append("<th id=\"soaData\">").append(ApplicationResourcesHelper.getMessage("rotulo.historico.solicitacao.autorizacao.data", responsavel)).append("</th>");
        cabecalhoPadrao.append("<th id=\"responsavelSoa\">").append(ApplicationResourcesHelper.getMessage("rotulo.historico.solicitacao.autorizacao.responsavel", responsavel)).append("</th>");
        cabecalhoPadrao.append("<th id=\"descricaoStatus\">").append(ApplicationResourcesHelper.getMessage("rotulo.historico.solicitacao.autorizacao.status.solicitacao", responsavel)).append("</th>");
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_ASSINATURA_DIGITAL_CONSIGNACAO_SOMENTE_CERT_DIGITAL, responsavel)) {
            cabecalhoPadrao.append("<th id=\"descricaoOrigem\">").append(ApplicationResourcesHelper.getMessage("rotulo.historico.solicitacao.autorizacao.status.origem", responsavel)).append("</th>");
            cabecalhoPadrao.append("<th id=\"soaObservacao\">").append(ApplicationResourcesHelper.getMessage("rotulo.historico.solicitacao.autorizacao.status.resposta", responsavel)).append("</th>");
            cabecalhoPadrao.append("<th id=\"soaDataResposta\">").append(ApplicationResourcesHelper.getMessage("rotulo.historico.solicitacao.autorizacao.status.data.resposta", responsavel)).append("</th>");
        }
        cabecalhoPadrao.append("</tr>");
        cabecalhoPadrao.append("</thead>");
        ;
        return cabecalhoPadrao.toString();
    }

    protected String montarLinhaLista(String soaData, String responsavelSoa, String descricaoStatus, String descricaoOrigem, String soaObservacao, String soaDataResposta, HttpServletRequest request, AcessoSistema responsavel) {
        StringBuilder linhaLista = new StringBuilder();

        linhaLista.append("<tr>");
        linhaLista.append("<td header=\"soaData\">").append(TextHelper.forHtmlContent(soaData)).append("</td>");
        linhaLista.append("<td header=\"responsavelSoa\">").append(TextHelper.forHtmlContent(responsavelSoa)).append("</td>");
        linhaLista.append("<td header=\"descricaoStatus\">").append(TextHelper.forHtmlContent(descricaoStatus)).append("</td>");
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_ASSINATURA_DIGITAL_CONSIGNACAO_SOMENTE_CERT_DIGITAL, responsavel)) {
            linhaLista.append("<td header=\"descricaoOrigem\">").append(TextHelper.forHtmlContent(descricaoOrigem)).append("</td>");
            linhaLista.append("<td header=\"soaObservacao\">").append(TextHelper.forHtmlContent(soaObservacao)).append("</td>");
            linhaLista.append("<td header=\"soaDataResposta\">").append(TextHelper.forHtmlContent(soaDataResposta)).append("</td>");
        }

        linhaLista.append("</tr>");
        return linhaLista.toString();
    }

    protected String montarLinhaResultadoVazio(AcessoSistema responsavel) {
        return "  <tr>\n" +
               "    <td class=\"sem-registro\" colspan=\"100%\">&nbsp;" + ApplicationResourcesHelper.getMessage("mensagem.anexo.erro.nenhum.registro", responsavel) + "</td>\n" +
               "  </tr>\n"
               ;
    }
}
