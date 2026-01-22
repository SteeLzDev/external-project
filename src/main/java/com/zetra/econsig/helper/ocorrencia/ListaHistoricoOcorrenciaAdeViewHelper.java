package com.zetra.econsig.helper.ocorrencia;

import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.HistoricoOcorrenciaAde;

import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: ListaHistoricoOcorrenciaAdeViewHelper</p>
 * <p>Description: ViewHelper para exibir uma tabela com uma listagem de tamanho fixo de histórico de margens.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author:
 * (Seg, 07 jun 2024) $
 */

public class ListaHistoricoOcorrenciaAdeViewHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaHistoricoOcorrenciaAdeViewHelper.class);

    protected static String abrirTabela() {
        return "<div class=\"card-body table-responsive p-0\">\n" +
                "<table class=\"table table-striped table-hover\">";
    }

    protected static String montarCabecalho(AcessoSistema responsavel) {
        return "<thead>\n" +
                "  <tr>\n" +
                "    <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.historico.ocorrencia.data", responsavel) + "</th>\n" +
                "    <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.historico.ocorrencia.descricao", responsavel) + "</th>\n" +
                "    <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.historico.ocorrencia.ip", responsavel) + "</th>\n" +
                "  </tr>\n" +
                "</thead>\n" +
                "<tbody>\n";
    }

    protected static String montarLinhaLista(HistoricoOcorrenciaAde dto) {
        return "  <tr>\n" +
                "    <td valign=\"top\">" + TextHelper.forHtmlContent(dto.getHoaData()) + "</td>\n" +
                "    <td valign=\"top\">" + JspHelper.formataMsgOca(TextHelper.forHtmlContent(dto.getHoaObs())) + "</td>\n" +
                "    <td valign=\"top\">" + TextHelper.forHtmlContentComTags(dto.getHoaIpAcesso()) + "</td>\n" +
                " </tr>\n"
                ;
    }

    protected static String fecharTabela() {
        return "</tbody>\n" +
                "<tfoot>\n" +
                "<tr>\n" +
                "<td colspan=\"8\">" +
                "<span class=\"font-italic\">" +
                "</span></td>\n" +
                "</tr>\n" +
                "</tfoot>\n" +
                "</table>\n" +
                "</div>\n";
    }

    public static String constroiView(List<HistoricoOcorrenciaAde> historico, AcessoSistema responsavel) throws JspException {
        try {
            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();


            code.append(abrirTabela());
            code.append(montarCabecalho(responsavel));

            if (historico != null && !historico.isEmpty()) {
                Iterator<HistoricoOcorrenciaAde> it = historico.iterator();
                HistoricoOcorrenciaAde cto = null;

                while (it.hasNext()) {
                    cto = it.next();

                    code.append(montarLinhaLista(cto));
                }
            } else {
                code.append(JspHelper.msgRstVazio(true, 7, responsavel));
            }

            code.append(fecharTabela());

            return code.toString();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }
}

