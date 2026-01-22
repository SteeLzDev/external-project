package com.zetra.econsig.web.tag.v4;

import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusPropostaEnum;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: ListaPropostaLeilaoSolicitacaoTag</p>
 * <p>Description: Tag para listagem das propostas de leilão de solicitação</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 22442 $
 * $Date: 2017-09-08 10:03:45 -0300 (sex, 08 set 2017) $
 */
public class ListaPropostaLeilaoSolicitacaoTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaPropostaLeilaoSolicitacaoTag.class);

    // Indica se <table></table> deve ser impresso
    private boolean table;
    // Lista de propostas a serem exibidas
    private List<TransferObject> lstPropostas;

    private boolean card;

    public void setTable(boolean table) {
        this.table = table;
    }

    public void setLstPropostas(List<TransferObject> lstPropostas) {
        this.lstPropostas = lstPropostas;
    }

    public void setCard(boolean card) {
        this.card = card;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                // Inicia geração do código HTML
                StringBuilder html = new StringBuilder();

                if (lstPropostas != null && lstPropostas.size() > 0) {

                    if (table) {
                        html.append("<br>");
                        html.append(abrirTabela(responsavel));
                    }

                    html.append(montarCabecalho(responsavel));
                    html.append("<tbody>");

                    String adeCodigo, plsCodigo, dataCadastro, dataValidade, consignataria, valorLiberado, valorParcela, prazo, taxa, statusDesc;
                    StatusPropostaEnum status;

                    String cssLinha = "Li";
                    for (TransferObject cto : lstPropostas) {

                        adeCodigo = cto.getAttribute(Columns.ADE_CODIGO).toString();
                        plsCodigo = cto.getAttribute(Columns.PLS_CODIGO).toString();

                        dataCadastro = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.PLS_DATA_CADASTRO));
                        if (cto.getAttribute(Columns.PLS_DATA_VALIDADE) != null) {
                            dataValidade = DateHelper.toDateString((Date) cto.getAttribute(Columns.PLS_DATA_VALIDADE));
                        } else {
                            dataValidade = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
                        }

                        consignataria = (String) cto.getAttribute(Columns.CSA_NOME_ABREV);
                        if (TextHelper.isNull(consignataria)) {
                            consignataria = cto.getAttribute(Columns.CSA_NOME).toString();
                        }
                        consignataria = cto.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + consignataria;
                        if (consignataria.length() > 50) {
                            consignataria = consignataria.substring(0, 47) + "...";
                        }

                        prazo = cto.getAttribute(Columns.PLS_PRAZO).toString();
                        valorLiberado = NumberHelper.reformat(cto.getAttribute(Columns.PLS_VALOR_LIBERADO).toString(), "en", NumberHelper.getLang());
                        valorParcela = NumberHelper.reformat(cto.getAttribute(Columns.PLS_VALOR_PARCELA).toString(), "en", NumberHelper.getLang());
                        taxa = NumberHelper.reformat(cto.getAttribute(Columns.PLS_TAXA_JUROS).toString(), "en", NumberHelper.getLang());

                        status = StatusPropostaEnum.recuperaStatusProposta(cto.getAttribute(Columns.STP_CODIGO).toString());
                        statusDesc = cto.getAttribute(Columns.STP_DESCRICAO).toString();

                        html.append(montarLinhaLista(adeCodigo, plsCodigo, dataCadastro, consignataria, valorLiberado, valorParcela, prazo, taxa, status, statusDesc, dataValidade, cssLinha, responsavel));
                        cssLinha = (cssLinha.equalsIgnoreCase("Li") ? "Lp" : "Li");
                    }

                    html.append("</tbody>");

                    if (table) {
                        html.append(fecharTabela(responsavel));
                    }
                }

                pageContext.getOut().print(html.toString());
            }

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    private String abrirTabela(AcessoSistema responsavel) {
        StringBuilder html = new StringBuilder();

        if (card) {
            html.append("<div class=\"card\">");
            html.append("<div class=\"card-header\">");
            html.append("<h2 class=\"card-header-title\">");
            html.append(ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.titulo", responsavel));
            html.append("</div>");
            html.append("<div class=\"card-body table-responsive p-0\">");
        }

        html.append("<table class=\"table table-striped table-hover\">");
        return html.toString();
    }

    private String montarCabecalho(AcessoSistema responsavel) {
        StringBuilder html = new StringBuilder();
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.data.cadastro", responsavel) + "</th>");
        if (!responsavel.isCsaCor() && !responsavel.isSer()) {
            html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel) + "</th>");
        }
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.valor.liberado", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.valor.prestacao", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.prazo", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.taxa.juros", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.proposta.status", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.data.validade", responsavel) + "</th>");
        html.append("</tr>");
        html.append("</thead>");
        return html.toString();
    }

    private String montarLinhaLista(String adeCodigo, String plsCodigo, String dataCadastro, String consignataria,
            String valorLiberado, String valorParcela, String prazo, String taxa, StatusPropostaEnum status, String statusDesc,
            String dataValidade, String cssLinha, AcessoSistema responsavel) {
        StringBuilder html = new StringBuilder();
        html.append("<tr>");
        html.append("    <td>" + TextHelper.forHtmlContent(dataCadastro) + "</td>");
        if (!responsavel.isCsaCor() && !responsavel.isSer()) {
            html.append("    <td>" + TextHelper.forHtmlContent(consignataria) + "</td>");
        }
        html.append("    <td>" + TextHelper.forHtmlContent(valorLiberado) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(valorParcela) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(prazo) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(taxa) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(statusDesc) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(dataValidade) + "</td>");
        html.append("</tr>");

        return html.toString();
    }

    private String fecharTabela(AcessoSistema responsavel) {
        StringBuilder html = new StringBuilder();
        html.append("<tfoot>");
        html.append("<tr>");
        html.append("<td colspan=\"8\">");
        html.append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.listagem.proposta", responsavel));
        html.append("</td>");
        html.append("</tr>");
        html.append("</tfoot>");
        html.append("</table>");

        if (card) {
            html.append("</div>");
            html.append("</div>");
        }

        return html.toString();
    }
}
