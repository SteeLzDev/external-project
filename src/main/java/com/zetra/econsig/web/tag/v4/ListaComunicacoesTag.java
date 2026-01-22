package com.zetra.econsig.web.tag.v4;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: ListaComunicacaoTag</p>
 * <p>Description: Tag para listagem das comunicações leiaute v4.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaComunicacoesTag extends ZetraTagSupport  {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaComunicacoesTag.class);

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
            final StringBuilder code = new StringBuilder();
            final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            // Obtém a lista com os anexos da consignação
            final List<TransferObject> historicos = (List<TransferObject>) pageContext.getAttribute(name, getScopeAsInt("request"));

            final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if ((historicos != null) && (!historicos.isEmpty())) {
                if (table) {
                    code.append(abrirTabela(responsavel));
                }

                code.append(montarCabecalho(responsavel));
                code.append("<tbody>");

                final StringBuilder destinatario = new StringBuilder();
                String data = "";
                String cmnCodigo = "";
                String msg = "";
                String tipoDestinatario = "";
                String cssLinha = "Li";
                for (TransferObject next : historicos) {
                   destinatario.setLength(0);
                   next = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject)next, null, responsavel);
                   cmnCodigo = (String) next.getAttribute(Columns.CMN_CODIGO);
                   final long cmnNumero = ((Long) next.getAttribute(Columns.CMN_NUMERO));
                   tipoDestinatario = (String) next.getAttribute("TIPO_ENTIDADE_DESTINATARIO");

                   // Concatena o tipo do destinatário ao destinatário
                   if(!TextHelper.isNull(tipoDestinatario)){
                     if (AcessoSistema.ENTIDADE_CSE.equals(tipoDestinatario)) {
                         destinatario.append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel));
                     } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoDestinatario)) {
                         destinatario.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel));
                     } else if (AcessoSistema.ENTIDADE_ORG.equals(tipoDestinatario)) {
                         destinatario.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel));
                     } else if (AcessoSistema.ENTIDADE_SER.equals(tipoDestinatario)) {
                         destinatario.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel));
                     }
                   }

                   destinatario.append(next.getAttribute("NOME_ENTIDADE_DESTINATARIO") != null ? ": " + TextHelper.forHtmlContent(next.getAttribute("NOME_ENTIDADE_DESTINATARIO")) : "");
                   data = DateHelper.toDateTimeString((Date) next.getAttribute(Columns.CMN_DATA));
                   msg = (String) next.getAttribute(Columns.CMN_TEXTO);
                   final boolean cmnPendente = ((Boolean) next.getAttribute(Columns.CMN_PENDENCIA));

                   final String displayMsg = (msg.length() > 100) ? msg.substring(0, 100) + "..." :msg;

                   code.append(montarLinhaLista(String.valueOf(cmnNumero), destinatario.toString(), data, cmnPendente ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel).toUpperCase(), displayMsg, cmnCodigo, request, responsavel));
                   cssLinha = ("Li".equalsIgnoreCase(cssLinha) ? "Lp" : "Li");
                }

                code.append("</tbody>");
                if (table) {
                    code.append(fecharTabela(responsavel));
                }
            }

            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    protected String abrirTabela(AcessoSistema responsavel) {
        return "<table class=\"table table-striped table-hover table-responsive\">\n";
    }

    protected String fecharTabela(AcessoSistema responsavel) {

        final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        final String subTitulo = (String) request.getAttribute("_paginacaoSubTitulo");

        final String textoRodape = ApplicationResourcesHelper.getMessage("mensagem.detalhe.historico.comunicacao", responsavel);
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
        final StringBuilder cabecalhoPadrao = new StringBuilder();

        cabecalhoPadrao.append("<thead>");
        cabecalhoPadrao.append("<tr>");
        cabecalhoPadrao.append("<th id=\"identificador\">").append(ApplicationResourcesHelper.getMessage("rotulo.comunicacao.identificador", responsavel)).append("</th>");
        cabecalhoPadrao.append("<th id=\"destinatario\">").append(ApplicationResourcesHelper.getMessage("rotulo.comunicacao.destinatario", responsavel)).append("</th>");
        cabecalhoPadrao.append("<th id=\"data\">").append(ApplicationResourcesHelper.getMessage("rotulo.comunicacao.data", responsavel)).append("</th>");
        cabecalhoPadrao.append("<th id=\"pendente\">").append(ApplicationResourcesHelper.getMessage("rotulo.comunicacao.pendente", responsavel)).append("</th>");
        cabecalhoPadrao.append("<th id=\"mensagem\">").append(ApplicationResourcesHelper.getMessage("rotulo.comunicacao.mensagem", responsavel)).append("</th>");
        cabecalhoPadrao.append("<th id=\"acoes\">").append(ApplicationResourcesHelper.getMessage("rotulo.comunicacao.acoes", responsavel)).append("</th>");
        cabecalhoPadrao.append("</tr>");
        cabecalhoPadrao.append("</thead>");

        return cabecalhoPadrao.toString();
    }

    protected String montarLinhaLista(String cmnNumero, String destinatario, String data, String cmnPendente, String mensagem, String cmnCodigo, HttpServletRequest request, AcessoSistema responsavel) {
        final StringBuilder linhaLista = new StringBuilder();

        linhaLista.append("<tr>");
        linhaLista.append("<td header=\"identificador\">").append(TextHelper.forHtmlContent(cmnNumero)).append("</td>");
        linhaLista.append("<td header=\"destinatario\">").append(TextHelper.forHtmlAttribute(destinatario)).append("</td>");
        linhaLista.append("<td header=\"data\">").append(TextHelper.forHtmlContent(data)).append("</td>");
        linhaLista.append("<td header=\"pendente\">").append(TextHelper.forHtmlContent(cmnPendente)).append("</td>");
        linhaLista.append("<td header=\"mensagem\">").append(TextHelper.forHtmlContent(mensagem)).append("</td>");
        linhaLista.append("<td header=\"acoes\"><a  href=\"#no-back\" onClick='postData(\"../v3/enviarComunicacao?acao=editar&cmn_codigo=").append(cmnCodigo + "&").append(SynchronizerToken.generateToken4URL(request) + "\")'>").append(ApplicationResourcesHelper.getMessage("rotulo.comunicacao.ler", responsavel)).append("</a>").append("</td>");

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
