package com.zetra.econsig.helper.consignacao;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.ColunaListaConsignacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import jakarta.servlet.jsp.JspException;

public class ListaConsignacaoSerViewHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaConsignacaoSerViewHelper.class);

    protected static String abrirCard(String nomeServidor, AcessoSistema responsavel) {
        return  "<div class=\"card\">\n" +
                " <div class=\"card-header\">\n" +
                "  <h2 class=\"card-header-title\">\n" +
                     ApplicationResourcesHelper.getMessage("rotulo.consignacao.plural", responsavel) + " - " + nomeServidor +
                "  </h2>\n" +
                " </div>\n" +
                "<div class=\"card-body\"> \n" +
                "<ul class='nav nav-tabs' role='tablist'> \n" +
                "    <li class='nav-item'> \n" +
                "        <a class='nav-link active'  href='#table-consig-ser-ativo' data-bs-toggle=\"tab\" aria-controls=\"profile\" aria-selected=\"true\" role='tab'>" + ApplicationResourcesHelper.getMessage("rotulo.status.ativo", responsavel) + "</a> \n" +
                "   </li> \n" +
                "   <li class='nav-item'> \n" +
                "        <a class='nav-link' href='#table-consig-ser-inativo' data-bs-toggle=\"tab\" aria-controls=\"profile\" aria-selected=\"false\" role='tab'>" + ApplicationResourcesHelper.getMessage("rotulo.status.inativo.consignacao", responsavel) + "</a> \n" +
                "   </li> \n" +
                "</ul> \n" +
                "<div class='tab-content' id=\"consignacaoSerInfo\"> \n";
    }

    protected static String montarCabecalho(List<ColunaListaConsignacao> lstColunas, AcessoSistema responsavel) {
        final StringBuilder cabecalho = new StringBuilder();
        cabecalho.append("<thead>\n");
        cabecalho.append("  <tr>\n");
        for(final ColunaListaConsignacao coluna : lstColunas) {
            cabecalho.append("    <th scope=\"col\">" + TextHelper.forHtmlContentComTags(coluna.getTitulo()) + "</th>\n");
        }
        cabecalho.append("  </tr>\n");
        cabecalho.append("</thead>\n");
        cabecalho.append("<tbody>\n");
        return cabecalho.toString();
    }

    protected static String montarLinhaLista(Object dto) {
        return "    <td>" + TextHelper.forHtmlContent(dto) + "</td>\n";
    }

    protected static String constroiTabela(String id, boolean exibeAtivo, List<TransferObject> lstConsignacaoSer, List<ColunaListaConsignacao> lstColunas, AcessoSistema responsavel) {
        final StringBuilder code = new StringBuilder();
        final String exibicao = exibeAtivo ? "show active" : "";
        code.append("<div class='tab-pane fade " + exibicao + "' id=\"table-consig-ser-" + id + "\" role='tabpanel' aria-labelledby='table-consig-ser-" + id + "'> \n");
        code.append(" <table class='dataTableConsignacaoSer table table-striped table-hover w-100'> \n");
        code.append(montarCabecalho(lstColunas, responsavel));
        if ((lstConsignacaoSer != null) && !lstConsignacaoSer.isEmpty()) {
            for(final TransferObject consignacao : lstConsignacaoSer) {
                code.append("  <tr>\n");
                for(final ColunaListaConsignacao coluna : lstColunas) {
                   if(consignacao.getAttribute(coluna.getChaveCampo()) != null) {
                       code.append(montarLinhaLista(consignacao.getAttribute(coluna.getChaveCampo())));
                   }
                }
               code.append("</tr> \n");
            }
        }
        code.append("</tbody> \n");
        code.append("</table> \n");
        code.append("</div> \n");
        return code.toString();
    }

    protected static String fecharCard() {
        return "</div> \n" +
                "</div> \n" +
               "</div>\n";
    }

    public static String constroiView(List<TransferObject> lstConsignacaoSer, List<ColunaListaConsignacao> lstColunas, AcessoSistema responsavel) throws JspException {
        try {
            // Inicia geração do código HTML
            final StringBuilder code = new StringBuilder();
            final List<TransferObject> lstConsignacaoAtivo = new ArrayList<>();
            final List<TransferObject> lstConsignacaoInativo = new ArrayList<>();

            final String nomeServidor = lstConsignacaoSer.get(0).getAttribute(Columns.RSE_MATRICULA) + " - " + lstConsignacaoSer.get(0).getAttribute(Columns.SER_CPF) + " - " + lstConsignacaoSer.get(0).getAttribute(Columns.SER_NOME);

            code.append(abrirCard(nomeServidor, responsavel));

            for(final TransferObject consignacao : lstConsignacaoSer) {
                final String sadCodigo = (String) consignacao.getAttribute(Columns.SAD_CODIGO);
                if(CodedValues.SAD_CODIGOS_ATIVOS.contains(sadCodigo)) {
                    lstConsignacaoAtivo.add(consignacao);
                } else {
                    lstConsignacaoInativo.add(consignacao);
                }
            }

            code.append(constroiTabela("ativo", true, lstConsignacaoAtivo, lstColunas, responsavel));
            code.append(constroiTabela("inativo", false, lstConsignacaoInativo, lstColunas, responsavel));

            code.append(fecharCard());

            return code.toString();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

}
