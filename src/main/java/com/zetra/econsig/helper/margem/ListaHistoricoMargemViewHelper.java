package com.zetra.econsig.helper.margem;

import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.web.VisualizarHistoricoDTO;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;

/**
 * <p>Title: ListaHistoricoMargemViewHelper</p>
 * <p>Description: ViewHelper para exibir uma tabela com uma listagem de tamanho fixo de histórico de margens.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author:
 * (Seg, 11 jun 2018) $
 */
public class ListaHistoricoMargemViewHelper {
    public static final int MAXIMO_LINHAS_NA_VIEW = 50;
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaHistoricoMargemViewHelper.class);

    protected static String abrirTabela(AcessoSistema responsavel) {
        return "<div class=\"card-body table-responsive p-0\">\n" +
                "<table class=\"table table-striped table-hover\">";
    }

    protected static String montarCabecalho(AcessoSistema responsavel) {
        return "<thead>\n" +
                "  <tr>\n" +
                "    <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.historico.margem.data.evento", responsavel) + "</th>\n" +
                "    <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.historico.margem.tipo.margem", responsavel) + "</th>\n" +
                "    <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.historico.margem.operacao", responsavel) + "</th>\n" +
                "    <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel) + "</th>\n" +
                "    <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel) + "</th>\n" +
                "    <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.historico.margem.valor.margem.antes", responsavel) + "</th>\n" +
                "    <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.historico.margem.valor.margem.depois", responsavel) + "</th>\n" +
                "  </tr>\n" +
                "</thead>\n" +
                "<tbody>\n";
    }

    protected static String montarLinhaLista(VisualizarHistoricoDTO dto, AcessoSistema responsavel) {
        return "  <tr>\n" +
                "    <td valign=\"top\">" + TextHelper.forHtmlContent(dto.getHmrData()) + "</td>\n" +
                "    <td valign=\"top\">" + TextHelper.forHtmlContent(dto.getMarDescricao()) + "</td>\n" +
                "    <td valign=\"top\">" + TextHelper.forHtmlContentComTags(dto.getDescricao()) + "</td>\n" +
                "    <td valign=\"top\">" + TextHelper.forHtmlContent(dto.getAdeNumero()) + "</td>\n" +
                "    <td valign=\"top\" align=\"right\">" + TextHelper.forHtmlContent(dto.getLabelTipoVlr()) + "&nbsp;" + TextHelper.forHtmlContent(dto.getAdeVlr()) + "&nbsp;</td>\n" +
                "    <td valign=\"top\" align=\"right\">" + TextHelper.forHtmlContent(dto.getHmrMargemAntes()) + "&nbsp;</td>\n" +
                "    <td valign=\"top\" align=\"right\">" + TextHelper.forHtmlContent(dto.getHmrMargemDepois()) + "&nbsp;</td>\n" +
                " </tr>\n"
                ;
    }

    protected static String fecharTabela(String rseCodigo, int listSize, HttpServletRequest request, AcessoSistema responsavel) {
        String linkToHistMargemPage = "<a href=\"#no-back\" onClick=\"postData('../v3/visualizarHistorico?acao=iniciar&RSE_CODIGO=" +
                                      TextHelper.forJavaScriptAttribute(rseCodigo) + "&" + TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request)) +
                                      "')\">" + ApplicationResourcesHelper.getMessage("rotulo.consultar.historico.margem.completo", responsavel) + "</a>";

        return  "</tbody>\n" +
        "<tfoot>\n" +
        "<tr>\n" +
        "<td colspan=\"8\">" + "<span class=\"font-italic\">" + (listSize > MAXIMO_LINHAS_NA_VIEW ? linkToHistMargemPage : "") +
        "</span></td>\n" +
        "</tr>\n" +
        "</tfoot>\n" +
        "</table>\n" +
        "</div>\n";
    }

    public static String constroiView(String rseCodigo, List<VisualizarHistoricoDTO> hist, int totalDeHistorico, HttpServletRequest request, AcessoSistema responsavel) throws JspException {
        try {
            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();


            code.append(abrirTabela(responsavel));
            code.append(montarCabecalho(responsavel));

            if (hist != null && !hist.isEmpty()) {
                Iterator<VisualizarHistoricoDTO> it = hist.iterator();
                VisualizarHistoricoDTO cto = null;

                while (it.hasNext()) {
                    cto = it.next();

                    code.append(montarLinhaLista(cto, responsavel));
                }
            } else {
                code.append(JspHelper.msgRstVazio(true, 7, responsavel));
            }

            code.append(fecharTabela(rseCodigo, totalDeHistorico, request, responsavel));

            return code.toString();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }
}
