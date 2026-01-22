package com.zetra.econsig.web.tag.v4;

import java.math.BigDecimal;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

public class ListarHistoricoLancamentosTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarHistoricoLancamentosTag.class);

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

    @Override
    public int doEndTag() throws JspException {
        try {
            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            // Obtém a lista com os anexos da consignação
            List<TransferObject> parcelas = (List<TransferObject>) pageContext.getAttribute(name, getScopeAsInt(scope));

            if (table) {
                code.append(abrirTabela(responsavel));
            }

            code.append(montarCabecalho(responsavel));
            code.append("<tbody>");

            parcelas.remove(0);

            String prdNumero;
            String prdDataDesconto;
            String prdVlrRealizado;
            String spdDescricao;
            String ocpData;
            String usuLogin;
            String ocpObs;
            BigDecimal vlrRealizado;

            for (TransferObject parcela : parcelas) {
                parcela = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) parcela, null, responsavel);

                vlrRealizado = (BigDecimal) parcela.getAttribute(Columns.PRD_VLR_REALIZADO);

                prdNumero = parcela.getAttribute(Columns.ADE_NUMERO) != null ? parcela.getAttribute(Columns.ADE_NUMERO).toString() : null;
                prdDataDesconto = parcela.getAttribute(Columns.PRD_DATA_DESCONTO) != null ? DateHelper.toPeriodString((java.util.Date) parcela.getAttribute(Columns.PRD_DATA_DESCONTO)) : null;
                prdVlrRealizado = vlrRealizado != null ? NumberHelper.reformat(vlrRealizado.toString(), "en", NumberHelper.getLang()) : "";
                spdDescricao = parcela.getAttribute(Columns.SPD_DESCRICAO) != null ?  parcela.getAttribute(Columns.SPD_DESCRICAO).toString() : null;
                usuLogin = parcela.getAttribute(Columns.USU_LOGIN) != null ? parcela.getAttribute(Columns.USU_LOGIN).toString() : null;
                ocpData = parcela.getAttribute(Columns.OCP_DATA) != null ? DateHelper.toDateString((java.util.Date) parcela.getAttribute(Columns.OCP_DATA)) : null;
                ocpObs = parcela.getAttribute(Columns.OCP_OBS) != null ? parcela.getAttribute(Columns.OCP_OBS).toString() : null;

                code.append(montarLinhaLista(prdNumero, prdDataDesconto, prdVlrRealizado, spdDescricao, ocpData, usuLogin, ocpObs, responsavel));
            }

            code.append("</tbody>");
            if (table) {
                code.append(fecharTabela(responsavel));
            }

            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    private String abrirTabela(AcessoSistema responsavel) {
        return "<table class=\"table table-striped table-hover table-responsive\">\n";
    }

    private String fecharTabela(AcessoSistema responsavel) {
        String dataAtual = DateHelper.format(DateHelper.getSystemDatetime(), LocaleHelper.getDateTimePattern());
        String textoRodape = ApplicationResourcesHelper.getMessage("mensagem.detalhe.consignacao.historico.lanc", responsavel, dataAtual);
        return  "<tfoot>\n" +
                "  <tr><td colspan=\"7\">" + textoRodape + "</td></tr>" +
                "</tfoot>\n" +
                "</table>";
    }

    private String montarCabecalho(AcessoSistema responsavel) {
        return "<thead>\n" +
                "  <tr>\n" +
                "    <th id=\"numeroParcela\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.lancamento.numero", responsavel) + "</th>\n" +
                "    <th id=\"dataDesconto\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.data.desconto", responsavel) + "</th>\n" +
                "    <th id=\"valor\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.valor", responsavel) + "</th>\n" +
                "    <th id=\"situacao\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.situacao", responsavel) + "</th>\n" +
                "    <th id=\"dataOcorrencia\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.data.ocorrencia", responsavel) + "</th>\n" +
                "    <th id=\"responsavelOcorrencia\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.responsavel", responsavel) + "</th>\n" +
                "    <th id=\"ocorrencias\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.subtitulo", responsavel) + "</th>\n" +
                "  </tr>\n" +
                "</thead>\n"
                ;
    }

    private String montarLinhaLista(String prdNumero, String prdDataDesconto, String prdVlrRealizado, String spdDescricao, String ocpData, String usuLogin, String ocpObs, AcessoSistema responsavel) {
        return "  <tr>\n" +
                "    <td header=\"numeroParcela\">" + TextHelper.forHtmlContent(prdNumero) + "</td>\n" +
                "    <td header=\"dataDesconto\">" + TextHelper.forHtmlContent(prdDataDesconto) + "</td>\n" +
                "    <td header=\"valor\" class=\"text-right\">" + TextHelper.forHtmlContent(prdVlrRealizado) + "</td>\n" +
                "    <td header=\"situacao\">" + TextHelper.forHtmlContent(spdDescricao) + "</td>\n" +
                "    <td header=\"dataOcorrencia\">" + TextHelper.forHtmlContent(ocpData) + "</td>\n" +
                "    <td header=\"responsavelOcorrencia\">" + TextHelper.forHtmlContent(usuLogin) + "</td>\n" +
                "    <td header=\"observacaoOcorrencia\">" + TextHelper.forHtmlContentComTags(ocpObs) + "</td>\n" +
                "  </tr>\n"
                ;
    }
}
