package com.zetra.econsig.web.tag.v4;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.HTMLInputTag;
import com.zetra.econsig.web.tag.ZetraTagSupport;

public class EditaPropostaPagamentoDividaTag extends ZetraTagSupport {

    protected boolean disabled;
    protected int qtdMinPropostas;
    protected int qtdMaxPropostas;
    protected Map<Integer, TransferObject> lstPropostas;
    protected List<Integer> prazosObrigatorios;

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setQtdMinPropostas(int qtdMinPropostas) {
        this.qtdMinPropostas = qtdMinPropostas;
    }

    public void setQtdMaxPropostas(int qtdMaxPropostas) {
        this.qtdMaxPropostas = qtdMaxPropostas;
    }

    public void setLstPropostas(Map<Integer, TransferObject> lstPropostas) {
        this.lstPropostas = lstPropostas;
    }

    public void setPrazosObrigatorios(List<Integer> prazosObrigatorios) {
        this.prazosObrigatorios = prazosObrigatorios;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            imprimeHTMLTag();
        } catch (IOException | ServletException ex) {
            throw new JspException(ex);
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

    private void imprimeHTMLTag() throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        //if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {

        // Faz a pesquisa dos contratos em situação de compra
        if (qtdMinPropostas > 0) {
            // Início da geração do HTML de detalhe de compra
            StringBuilder html = new StringBuilder();

            // Gera o javascript necessário para adição de novas linhas na tabela de propostas
            html.append("\n<script language=\"JavaScript\" type=\"text/JavaScript\">");
            html.append("\n  var next = ").append(lstPropostas != null ? lstPropostas.size() + 1 : qtdMinPropostas + 1).append(";");
            html.append("\n  var max  = ").append(qtdMaxPropostas).append(";");
            html.append("\n  function adicionarProposta() {");
            html.append("\n    if (next <= max) {");
            html.append("\n      var nextRow = document.getElementById('proposta' + next);");
            html.append("\n      nextRow.classList.remove(\"d-none\");");
            html.append("\n      next++;");
            html.append("\n    }");
            html.append("\n    if (next > max) {");
            html.append("\n      var image = document.getElementById('showMoreImage');");
            html.append("\n      image.style.display = 'none';");
            html.append("\n    }");
            html.append("\n  }");
            html.append("\n</script>");

            // Imprime o cabeçalho da tabela
            html.append("\n<div class='card'>");
            html.append("\n  <div class='card-header hasIcon pl-3'>");
            html.append("\n    <h2 class=\"card-header-title\">" + ApplicationResourcesHelper.getMessage("rotulo.pagamento.parcelado.saldo", responsavel) + "</h2>");
            html.append("\n  </div>");
            html.append("\n  <div class=\"card-body table-responsive p-0\">");
            html.append("\n    <table class=\"table table-striped table-hover\">");
            html.append("\n      <thead>");
            html.append("\n        <tr>");
            html.append("\n          <th scope=\"col-10\">" + ApplicationResourcesHelper.getMessage("rotulo.campo.numero.abreviado", responsavel) + "</th>");
            html.append("\n          <th scope=\"col-45\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo", responsavel) + "</th>");
            html.append("\n          <th scope=\"col-45\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.moeda", responsavel) + "</th>");
            html.append("\n        </tr>");
            html.append("\n      </thead>");
            html.append("\n    <tbody>");

            Iterator<Integer> itPrzObrigatorio = (prazosObrigatorios != null ? prazosObrigatorios.iterator() : null);

            // Imprime as linhas com os campos de edição das propostas
            for (int i = 1; i <= qtdMaxPropostas; i++) {
                TransferObject proposta = (lstPropostas != null ? lstPropostas.get(i) : null);
                String cdgProposta = "";
                String vlrProposta = "";
                String przProposta = "";
                if (proposta != null) {
                    cdgProposta = proposta.getAttribute(Columns.PPD_CODIGO).toString();
                    vlrProposta = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PPD_VALOR_PARCELA)).doubleValue(), NumberHelper.getLang());
                    przProposta = proposta.getAttribute(Columns.PPD_PRAZO).toString();
                } else if (itPrzObrigatorio != null && itPrzObrigatorio.hasNext()) {
                    vlrProposta = JspHelper.verificaVarQryStr(request, "vlrProposta" + i);
                    przProposta = itPrzObrigatorio.next().toString();
                } else {
                    vlrProposta = JspHelper.verificaVarQryStr(request, "vlrProposta" + i);
                    przProposta = JspHelper.verificaVarQryStr(request, "przProposta" + i);
                }

                html.append("\n    <tr id=\"proposta").append(i).append("\"");
                html.append((proposta == null && i > qtdMinPropostas) ? " class=\"d-none\"" : "").append(">");
                // Campo código proposta
                html.append("\n      <td>").append(i);
                HTMLInputTag tagCodigo = new HTMLInputTag();
                tagCodigo.setName("cdgProposta" + i);
                tagCodigo.setDi("cdgProposta" + i);
                tagCodigo.setType("hidden");
                tagCodigo.setValue(TextHelper.forHtmlAttribute(cdgProposta));
                html.append(tagCodigo.generateHtml(responsavel));
                html.append("\n      </td>");

                // Campo prazo proposta
                html.append("\n      <td>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-sm-1 pt-3\">");
                html.append(i);
                html.append("</div>");
                html.append("<div class=\"col-sm-3 \">");
                HTMLInputTag tagPrazo = new HTMLInputTag();
                tagPrazo.setName("przProposta" + i);
                tagPrazo.setDi("przProposta" + i);
                tagPrazo.setType("text");
                tagPrazo.setClasse("form-control");
                tagPrazo.setSize("4");
                tagPrazo.setMask("#D4");
                tagPrazo.setOnFocus("SetarEventoMascaraV4(this,'#D4',true);");
                tagPrazo.setValue(TextHelper.forHtmlAttribute(przProposta));
                tagPrazo.setOthers(disabled ? "disabled" : "");
                html.append(tagPrazo.generateHtml(responsavel));
                html.append("</div>");
                html.append("\n      </td>");

                // Campo valor proposta
                html.append("\n      <td>");
                html.append("<div class=\"row\">");
                html.append("<div class=\"col-sm-1 pt-3\">");
                html.append(i);
                html.append("</div>");
                html.append("<div class=\"col-sm-3 \">");
                HTMLInputTag tagValor = new HTMLInputTag();
                tagValor.setName("vlrProposta" + i);
                tagValor.setDi("vlrProposta" + i);
                tagValor.setType("text");
                tagValor.setClasse("form-control");
                tagValor.setSize("8");
                tagValor.setMask("#F11");
                tagPrazo.setOnFocus("SetarEventoMascaraV4(this,'#F11',true);");
                tagValor.setValue(TextHelper.forHtmlAttribute(vlrProposta));
                tagValor.setOnBlur("if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }");
                tagPrazo.setOthers(disabled ? "disabled" : "");
                html.append(tagValor.generateHtml(responsavel));
                html.append("</div>");
                html.append("\n      </td>");

                html.append("\n    </tr>");
            }
            // Fecha a tabela de edição
            if (lstPropostas == null || lstPropostas.size() < qtdMaxPropostas) {
                html.append("\n              <tr id=\"showMoreImage\">");
                html.append("\n                <td align=\"center\"><img src=\"../img/icones/plus.gif\" onclick=\"adicionarProposta(); return false;\"/></td>");
                html.append("\n                <td colspan=\"2\">&nbsp;</td>");
                html.append("\n              </tr>");
            }
            html.append("\n    </tbody>");
            html.append("\n  </table>");
            html.append("\n  </div>");
            html.append("\n</div>");

            // Gera o resultado
            pageContext.getOut().print(html.toString());
            // }
        }
    }
}
