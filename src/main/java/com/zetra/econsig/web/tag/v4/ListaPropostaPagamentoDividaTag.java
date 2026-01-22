package com.zetra.econsig.web.tag.v4;

import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
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
 * <p>Title: ListaPropostaPagamentoDividaTag</p>
 * <p>Description: Tag para listagem das propostas de pagamento de dívida</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author: alexandre $
 * $Revision: 31521 $
 * $Date: 2021-03-24 19:29:19 -0300 (qua, 24 mar 2021) $
 */
public class ListaPropostaPagamentoDividaTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaPropostaPagamentoDividaTag.class);

    // Indica se <table></table> deve ser impresso
    private boolean table;
    // Tipo de exibição
    private String type;
    // Lista de propostas a serem exibidas
    private List<TransferObject> lstPropostas;

    private boolean card;

    public void setCard(boolean card) {
        this.card = card;
    }

    public void setTable(boolean table) {
        this.table = table;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLstPropostas(List<TransferObject> lstPropostas) {
        this.lstPropostas = lstPropostas;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                boolean podeAprovarProposta = (type.equals("consultar") && responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_APROVAR_PROPOSTA_PGT_DIVIDA));
                boolean podeRenegociarProposta = (type.equals("renegociar") && responsavel.isCsaCor() && responsavel.temPermissao(new String[]{CodedValues.FUN_RENE_CONTRATO, CodedValues.FUN_COMP_CONTRATO}));

                // Inicia geração do código HTML
                StringBuilder html = new StringBuilder();

                if (lstPropostas != null && lstPropostas.size() > 0) {

                    if (podeAprovarProposta) {
                        // Se é servidor e pode aprovar proposta, constrói javascript para aprovação
                        // da proposta passada por parâmetro
                        html.append("\n<script language=\"JavaScript\" type=\"text/JavaScript\">");
                        html.append("\n  function aprovarProposta(ade, ppd) {");
                        html.append("\n    var link = '../v3/acompanharFinanciamentoDivida?acao=aprovar';");
                        html.append("\n    link += '&ade=' + ade;");
                        html.append("\n    link += '&ppd=' + ppd;");
                        html.append("\n    link += '&").append(SynchronizerToken.generateToken4URL((HttpServletRequest) pageContext.getRequest())).append("';");
                        html.append("\n    if (confirm('" + ApplicationResourcesHelper.getMessage("mensagem.confirmacao.aprovar.proposta.pagamento", responsavel) + "')) {");
                        html.append("\n      postData(link);");
                        html.append("\n    }");
                        html.append("\n  }");
                        html.append("\n</script>");

                    } else if (podeRenegociarProposta) {
                        // Se é operação de renegociação/compra, verifica se alguma proposta
                        // está aprovada. Caso esteja, os campos de renegociação serão preenchidos
                        // com o valor da proposta. Se não tiver, constrói javascript para escolha
                        // das propostas, que irão definir os valores dos campos.
                        html.append("\n<script language=\"JavaScript\" type=\"text/JavaScript\">");
                        html.append("\n  function selecionarProposta(codigo, divida, parcela, prazo) {");
                        html.append("\n    with (document.forms[0]) {");
                        html.append("\n      ppd.value = codigo;");
                        html.append("\n      adeVlrLiquido.value = divida;");
                        html.append("\n      adeVlr.value = parcela;");
                        html.append("\n      if (adePrazo.type == 'text') {");
                        html.append("\n        adePrazo.value = prazo;");
                        html.append("\n      } else {");
                        html.append("\n        SelecionaComboMsg(adePrazo, prazo);");
                        html.append("\n      }");
                        html.append("\n    }");
                        html.append("\n  }");
                        html.append("\n");
                        html.append("\n  function carregaPropostas() {");
                        html.append("\n    document.forms[0].adeVlrLiquido.disabled = true;");

                        for (TransferObject cto : lstPropostas) {
                            StatusPropostaEnum status = StatusPropostaEnum.recuperaStatusProposta(cto.getAttribute(Columns.STP_CODIGO).toString());
                            if (status.equals(StatusPropostaEnum.APROVADA)) {
                                // Obtém os valores da proposta
                                String codigo = cto.getAttribute(Columns.PPD_CODIGO).toString();
                                String valorDivida = NumberHelper.reformat(cto.getAttribute(Columns.PPD_VALOR_DIVIDA).toString(), "en", NumberHelper.getLang());
                                String valorParcela = NumberHelper.reformat(cto.getAttribute(Columns.PPD_VALOR_PARCELA).toString(), "en", NumberHelper.getLang());
                                String prazo = cto.getAttribute(Columns.PPD_PRAZO).toString();

                                // Gera o javascript necessário para adição de novas linhas na tabela de propostas
                                html.append("\n    selecionarProposta('").append(TextHelper.forJavaScriptBlock(codigo));
                                html.append("', '").append(TextHelper.forJavaScriptBlock(valorDivida));
                                html.append("', '").append(TextHelper.forJavaScriptBlock(valorParcela));
                                html.append("', '").append(TextHelper.forJavaScriptBlock(prazo)).append("');");

                                break;
                            }
                        }

                        html.append("\n  }");
                        html.append("\n</script>");
                        html.append("\n");
                        html.append("\n<input type=\"hidden\" name=\"ppd\" id=\"ppd\" value=\"\"/>");
                    }

                    if (table) {
                        html.append("<br>");
                        html.append(abrirTabela(responsavel));
                    }

                    html.append(montarCabecalho(podeAprovarProposta, podeRenegociarProposta, responsavel));

                    String adeCodigo, ppdCodigo, dataCadastro, dataValidade, consignataria, valorDivida, valorParcela, prazo, taxa, statusDesc;
                    StatusPropostaEnum status;

                    String cssLinha = "Li";
                    for (TransferObject cto : lstPropostas) {

                        adeCodigo = cto.getAttribute(Columns.ADE_CODIGO).toString();
                        ppdCodigo = cto.getAttribute(Columns.PPD_CODIGO).toString();
                        dataCadastro = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.PPD_DATA_CADASTRO));
                        dataValidade = DateHelper.toDateString((Date) cto.getAttribute(Columns.PPD_DATA_VALIDADE));

                        consignataria = (String) cto.getAttribute(Columns.CSA_NOME_ABREV);
                        if (TextHelper.isNull(consignataria)) {
                            consignataria = cto.getAttribute(Columns.CSA_NOME).toString();
                        }
                        consignataria = cto.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + consignataria;
                        if (consignataria.length() > 50) {
                            consignataria = consignataria.substring(0, 47) + "...";
                        }

                        valorDivida = NumberHelper.reformat(cto.getAttribute(Columns.PPD_VALOR_DIVIDA).toString(), "en", NumberHelper.getLang());
                        valorParcela = NumberHelper.reformat(cto.getAttribute(Columns.PPD_VALOR_PARCELA).toString(), "en", NumberHelper.getLang());
                        prazo = cto.getAttribute(Columns.PPD_PRAZO).toString();
                        taxa = NumberHelper.reformat(cto.getAttribute(Columns.PPD_TAXA_JUROS).toString(), "en", NumberHelper.getLang());

                        status = StatusPropostaEnum.recuperaStatusProposta(cto.getAttribute(Columns.STP_CODIGO).toString());
                        statusDesc = cto.getAttribute(Columns.STP_DESCRICAO).toString();

                        html.append(montarLinhaLista(adeCodigo, ppdCodigo, dataCadastro, consignataria, valorDivida, valorParcela, prazo, taxa, status, statusDesc, dataValidade, cssLinha, podeAprovarProposta, podeRenegociarProposta, responsavel));
                        cssLinha = (cssLinha.equalsIgnoreCase("Li") ? "Lp" : "Li");
                    }

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

    private String montarCabecalho(boolean podeAprovarProposta, boolean podeRenegociarProposta, AcessoSistema responsavel) {
        StringBuilder html = new StringBuilder();
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.pagamento.data.proposta", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.pagamento.valor", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.pagamento.prazo", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.pagamento.status", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.pagamento.validade", responsavel) + "</th>");
        html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.proposta.pagamento.situacao", responsavel)  + "</th>");
        if (podeAprovarProposta) {
            html.append("<th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel) + "</th>");
        }
        html.append("</tr>");
        html.append("</thead>");
        return html.toString();
    }

    private String montarLinhaLista(String adeCodigo, String ppdCodigo, String dataCadastro, String consignataria,
            String valorDivida, String valorParcela, String prazo, String taxa, StatusPropostaEnum status, String statusDesc,
            String dataValidade, String cssLinha, boolean podeAprovarProposta, boolean podeRenegociarProposta, AcessoSistema responsavel) {
        StringBuilder html = new StringBuilder();
        html.append("<tr>");
        html.append("    <td>" + TextHelper.forHtmlContent(dataCadastro) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(consignataria) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(valorParcela) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(prazo) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(taxa) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(statusDesc) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlContent(dataValidade) + "</td>");
        html.append("    <td>" + TextHelper.forHtmlAttribute(statusDesc) + "</td>");
        if (podeAprovarProposta) {
            if (status.equals(StatusPropostaEnum.AGUARDANDO_APROVACAO)) {
                html.append("<td><a href=\"#no-back\" onClick=\"aprovarProposta('").append(TextHelper.forJavaScriptAttribute(adeCodigo)).append("','").append(TextHelper.forJavaScriptAttribute(ppdCodigo)).append("')\"> " + ApplicationResourcesHelper.getMessage("rotulo.acoes.aprovar", responsavel) + "</a></td>\n");
            } else {
                html.append("<td></td>");
            }
        } else if (podeRenegociarProposta) {
            if (status.equals(StatusPropostaEnum.AGUARDANDO_APROVACAO) || status.equals(StatusPropostaEnum.APROVADA)) {
                html.append("<td><a href=\"#no-back\" onclick=\"selecionarProposta('\").append(TextHelper.forJavaScriptAttribute(ppdCodigo)).append(\"','\").append(TextHelper.forJavaScriptAttribute(valorDivida)).append(\"','\").append(TextHelper.forJavaScriptAttribute(valorParcela)).append(\"','\").append(TextHelper.forJavaScriptAttribute(prazo)).append(\"')\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.aprovar", responsavel) + "</a></td>");
            } else {
                html.append("<td></td>");
            }
        }

        return html.toString();
    }

    private String fecharTabela(AcessoSistema responsavel) {
        StringBuilder html = new StringBuilder();
        html.append("<tfoot>");
        html.append("<tr>");
        html.append("<td colspan=\"8\">");
        html.append(ApplicationResourcesHelper.getMessage("rotulo.proposta.listagem", responsavel));
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
