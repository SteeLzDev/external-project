package com.zetra.econsig.dto.web;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AcaoConsignacao</p>
 * <p>Description: POJO contendo configuração sobre as ações realizadas sobre consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AcaoConsignacao {

    protected String acao;
    protected String funcao;
    protected String descricao;
    protected final String descricaoCompleta;
    protected String icone;
    protected String idIcone;
    protected String textoAlternativo;
    protected String mensagemConfirmacao;
    protected String mensagemAdicionalConfirmacao;
    protected String link;
    protected String linkAdicional;
    protected String idCheckbox;

    public AcaoConsignacao(String acao, String funcao, String descricao, String icone, String idIcone, String textoAlternativo, String mensagemConfirmacao, String mensagemAdicionalConfirmacao, String link, String idCheckbox) {
        this(acao, funcao, descricao, descricao, icone, idIcone, textoAlternativo, mensagemConfirmacao, mensagemAdicionalConfirmacao, link, null, idCheckbox);
    }

    public AcaoConsignacao(String acao, String funcao, String descricao, String icone, String idIcone, String textoAlternativo, String mensagemConfirmacao, String mensagemAdicionalConfirmacao, String link, String linkAdicional, String idCheckbox) {
        this(acao, funcao, descricao, descricao, icone, idIcone, textoAlternativo, mensagemConfirmacao, mensagemAdicionalConfirmacao, link, linkAdicional, idCheckbox);
    }

    public AcaoConsignacao(String acao, String funcao, String descricao, String descricaoCompleta, String icone, String idIcone, String textoAlternativo, String mensagemConfirmacao, String mensagemAdicionalConfirmacao, String link, String linkAdicional, String idCheckbox) {
        super();
        this.acao = acao;
        this.funcao = funcao;
        this.descricao = descricao;
        this.descricaoCompleta = descricaoCompleta;
        this.icone = icone;
        this.idIcone = idIcone;
        this.textoAlternativo = textoAlternativo;
        this.mensagemConfirmacao = mensagemConfirmacao;
        this.mensagemAdicionalConfirmacao = mensagemAdicionalConfirmacao;
        this.link = link;
        this.linkAdicional = linkAdicional;
        this.idCheckbox = idCheckbox;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getTextoAlternativo() {
        return textoAlternativo;
    }

    public void setTextoAlternativo(String textoAlternativo) {
        this.textoAlternativo = textoAlternativo;
    }

    public String getMensagemConfirmacao() {
        return mensagemConfirmacao;
    }

    public void setMensagemConfirmacao(String mensagemConfirmacao) {
        this.mensagemConfirmacao = mensagemConfirmacao;
    }

    public String getMensagemAdicionalConfirmacao() {
        return mensagemAdicionalConfirmacao;
    }

    public void setMensagemAdicionalConfirmacao(String mensagemAdicionalConfirmacao) {
        this.mensagemAdicionalConfirmacao = mensagemAdicionalConfirmacao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkAdicional() {
        return linkAdicional;
    }

    public void setLinkAdicional(String linkAdicional) {
        this.linkAdicional = linkAdicional;
    }

    public String getIdIcone() {
        return idIcone;
    }

    public void setIdIcone(String idIcone) {
        this.idIcone = idIcone;
    }

    public String getIdCheckbox() {
        return idCheckbox;
    }

    public void setIdCheckbox(String idCheckbox) {
        this.idCheckbox = idCheckbox;
    }

    public String addAcaoCheckBox(boolean ocultarColunaCheckBox,AcessoSistema responsavel) {
        StringBuilder chkAcao = new StringBuilder("\n");

        if (!TextHelper.isNull(idCheckbox)) {
            String[] idCheckboxs = {idCheckbox};
            if ((acao.equals("DEF_CONSIGNACAO") || acao.equals("INDF_CONSIGNACAO")) &&
                    responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO) &&
                    responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO)) {
                idCheckboxs = new String[]{"chkDeferir", "chkIndeferir"};
            }

            for (String id : idCheckboxs) {
                if(ocultarColunaCheckBox) {
                    chkAcao.append("ocultarColuna();");
                }
                chkAcao.append("\n");
                // Click no ações, selecionar
                chkAcao.append("$(\".selecionaAcaoSelecionar_").append(id).append("\")").append(".click(function() {");
                if(ocultarColunaCheckBox) {
                    chkAcao.append("$(\"table th:first\").show();");
                    chkAcao.append("$(\".ocultarColuna\").show();");
                }
                chkAcao.append("$(this).parents(\"tr\").toggleClass(\"table-checked\");");
                chkAcao.append("$(this).parents(\"tr\").find('input[name=\"").append(id).append("\"]').prop(\"checked\", function(i, val) {");
                chkAcao.append("return !val;");
                chkAcao.append("});");
                chkAcao.append("var qtdCheckboxCheked = $('input[name=\"").append(id).append("\"]')").append(".filter(':checked').length;");
                chkAcao.append("var qtdCheckbox = $('input[name=\"").append(id).append("\"]')").append(".length;");
                chkAcao.append("if (qtdCheckboxCheked == 0) {");
                if(ocultarColunaCheckBox) {
                    chkAcao.append("$(\"table th:first\").hide();");
                    chkAcao.append("$(\".ocultarColuna\").hide();");
                }
                chkAcao.append("} else if (qtdCheckbox == qtdCheckboxCheked) {");
                chkAcao.append("$(\"#checkAll_").append(id).append("\").prop('checked', true);");
                chkAcao.append("} else if (qtdCheckbox != qtdCheckboxCheked) {");
                chkAcao.append("$(\"#checkAll_").append(id).append("\").prop('checked', false);");
                chkAcao.append("}");
                chkAcao.append("return true;");
                chkAcao.append("});");
                //Click da linha
                chkAcao.append("$(\".selecionarColuna").append("\").click(function() {");

                chkAcao.append("$(this).parents(\"tbody\").toggleClass(\"table-checked\");");
                chkAcao.append("$(this).parents(\"tr\").find('input[name=\"").append(id).append("\"]').prop(\"checked\", function(i, val) {");
                if(ocultarColunaCheckBox) {
                    chkAcao.append("$(\"table th:first\").show();");
                    chkAcao.append("$(\".ocultarColuna\").show();");
                }
                chkAcao.append("return !val;");
                chkAcao.append("});");
                chkAcao.append("var qtdCheckboxCheked = $('input[name=\"").append(id).append("\"]')").append(".filter(':checked').length;");
                chkAcao.append("var qtdCheckbox = $('input[name=\"").append(id).append("\"]')").append(".length;");
                chkAcao.append("if (qtdCheckboxCheked == 0) {");
                if(ocultarColunaCheckBox) {
                    chkAcao.append("$(\"table th:first\").hide();");
                    chkAcao.append("$(\".ocultarColuna\").hide();");
                    chkAcao.append("$(this).parents(\"tr\").toggleClass(\"table-checked\");");
                }
                chkAcao.append("} else if (qtdCheckbox == qtdCheckboxCheked) {");
                chkAcao.append("$(\"#checkAll_").append(id).append("\").prop('checked', true);");
                chkAcao.append("$(this).parents(\"tr\").toggleClass(\"table-checked\");");
                chkAcao.append("} else if (qtdCheckbox != qtdCheckboxCheked) {");
                chkAcao.append("$(\"#checkAll_").append(id).append("\").prop('checked', false);");
                chkAcao.append("$(this).parents(\"tr\").toggleClass(\"table-checked\");");
                chkAcao.append("}");
                chkAcao.append("});");

                //CHECKALL
                chkAcao.append("$(\"#checkAll_").append(id).append("\").click(function() {$('input[name=\"").append(id).append("\"]').prop(\"checked\",function(i, val) {");
                chkAcao.append("if ($(\"#checkAll_").append(id).append("\").is(\":checked\")) {");
                chkAcao.append("$(this).parentsUntil(\"tbody\",\".selecionarLinha").append("\").addClass(\"table-checked\");");
                chkAcao.append("} else {");
                chkAcao.append("$(this).parentsUntil(\"tbody\",\".selecionarLinha\").removeClass(\"table-checked\");");
                chkAcao.append("$(\".selecionaAcaoSelecionar_").append(id).append("\").parentsUntil(\"tr\").removeClass(\"table-checked\");");
                chkAcao.append("}");
                chkAcao.append("return $(\"#checkAll_").append(id).append("\").is(\":checked\");");
                chkAcao.append("});");

                chkAcao.append("if (!$(\"#checkAll_").append(id).append("\").is(\":checked\")) {");
                if(ocultarColunaCheckBox) {
                    chkAcao.append("$(\"table th:first\").hide();");
                    chkAcao.append("$(\".ocultarColuna\").hide();");
                }
                chkAcao.append("}");
                chkAcao.append("});");

                //Click do check
                chkAcao.append("$(\"[name='").append(id).append("']\").click(function() {");
                chkAcao.append("if ($(this).is(\":checked\")) {");
                chkAcao.append("$(this).parentsUntil(\"tbody\", \".selecionarLinha").append("\").addClass(\"table-checked\");");
                chkAcao.append("} else {");
                chkAcao.append("$(this).parentsUntil(\"tbody\", \".selecionarLinha").append("\").removeClass(\"table-checked\");");
                chkAcao.append("}");
                chkAcao.append("var qtdCheckboxCheked = $('input[name=\"").append(id).append("\"]')").append(".filter(':checked').length;");
                chkAcao.append("var qtdCheckbox = $('input[name=\"").append(id).append("\"]')").append(".length;");
                chkAcao.append("if (qtdCheckbox == qtdCheckboxCheked) {");
                chkAcao.append("$(\"#checkAll_").append(id).append("\").prop('checked', true);");
                chkAcao.append("} else if (qtdCheckbox != qtdCheckboxCheked) {");
                chkAcao.append("$(\"#checkAll_").append(id).append("\").prop('checked', false);");
                chkAcao.append("}");
                chkAcao.append("if ($('input[name=\"").append(id).append("\"]').filter(':checked').length == 0) {");
                if(ocultarColunaCheckBox) {
                    chkAcao.append("$(\"table th:first\").hide();");
                    chkAcao.append("$(\".ocultarColuna\").hide();");
                }
                chkAcao.append("}");
                chkAcao.append("});");
                if(ocultarColunaCheckBox) {
                    chkAcao.append("function ocultarColuna() {");
                    chkAcao.append("$(\"table th:first\").hide();");
                    chkAcao.append("$(\".ocultarColuna\").hide();");
                    chkAcao.append("}");
                    chkAcao.append("\n");
              }
            }
        }

        return chkAcao.toString();
    }

    public String getAcaoJavascript(String queryString, String tipoOperacao, int qtdMaxSelecaoMultipla, String msgErroQtdMaxSelecaoMultiplaSuperada, AcessoSistema responsavel) {
        StringBuilder js = new StringBuilder();

        if (!TextHelper.isNull(link)) {
            String msgSelecione = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.selecione.info", responsavel);
            msgErroQtdMaxSelecaoMultiplaSuperada = TextHelper.forJavaScriptBlock(msgErroQtdMaxSelecaoMultiplaSuperada);
            tipoOperacao = TextHelper.forJavaScriptBlock(tipoOperacao);
            queryString = TextHelper.forJavaScriptBlock(queryString);

            link = TextHelper.forJavaScriptBlock(link);
            linkAdicional = (linkAdicional != null ? TextHelper.forJavaScriptBlock(linkAdicional) : "");
            idCheckbox = TextHelper.forJavaScriptBlock(idCheckbox);
            mensagemConfirmacao = TextHelper.forJavaScriptBlock(mensagemConfirmacao);
            mensagemAdicionalConfirmacao = (mensagemAdicionalConfirmacao != null ? TextHelper.forJavaScriptBlock(mensagemAdicionalConfirmacao) : "");


            js.append("\n").append("function ").append(acao).append("(ade, exibeMsgAdicional, usarLinkAdicional) {");
            js.append("\n").append("  var j    = '").append(link).append("';");
            js.append("\n").append("  var j2   = '").append(linkAdicional).append("';");
            js.append("\n").append("  var qs   = '").append(queryString).append("&tipo=").append(tipoOperacao).append("&ADE_CODIGO=' + (ade != undefined ? ade : '');");
            js.append("\n").append("  var msg  = '").append(mensagemConfirmacao).append("';");
            js.append("\n").append("  var msg2 = '").append(mensagemAdicionalConfirmacao).append("';");
            js.append("\n").append("  var qtd  = 0;");
            js.append("\n").append("  usarLinkAdicional = (usarLinkAdicional == undefined ? false : (usarLinkAdicional == '1'));");
            js.append("\n").append("  exibeMsgAdicional = (exibeMsgAdicional == undefined ? false : (exibeMsgAdicional == '1'));");

            if (!TextHelper.isNull(idCheckbox)) {
                String[] idCheckboxs = {idCheckbox};
                if ((acao.equals("DEF_CONSIGNACAO") || acao.equals("INDF_CONSIGNACAO")) &&
                        responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO) &&
                        responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO)) {
                    idCheckboxs = new String[]{"chkDeferir", "chkIndeferir"};
                }

                for (String id : idCheckboxs) {
                    js.append("\n").append("  var chkFields = document.getElementsByName(\"").append(id).append("\");");
                    js.append("\n").append("  if (chkFields) {");
                    js.append("\n").append("    if (chkFields.length) {");
                    js.append("\n").append("      for (i = 0; i < chkFields.length; i++) {");
                    js.append("\n").append("        if (chkFields[i].checked) {");
                    js.append("\n").append("          qtd++;");
                    js.append("\n").append("          qs += '&").append(id).append("=' + chkFields[i].value;");
                    js.append("\n").append("          exibeMsgAdicional = (exibeMsgAdicional || chkFields[i].getAttribute('data-exibe-msg2') == '1');");
                    js.append("\n").append("          usarLinkAdicional = (usarLinkAdicional || chkFields[i].getAttribute('data-usa-link2') == '1');");
                    js.append("\n").append("        }");
                    js.append("\n").append("      }");
                    js.append("\n").append("    } else if (chkFields.value) {");
                    js.append("\n").append("      if (chkFields.checked) {");
                    js.append("\n").append("        qtd++;");
                    js.append("\n").append("        qs += '&").append(id).append("=' + chkFields.value;");
                    js.append("\n").append("        exibeMsgAdicional = (exibeMsgAdicional || chkFields.getAttribute('data-exibe-msg2') == '1');");
                    js.append("\n").append("        usarLinkAdicional = (usarLinkAdicional || chkFields.getAttribute('data-usa-link2') == '1');");
                    js.append("\n").append("      }");
                    js.append("\n").append("    }");
                    js.append("\n").append("  }");
                }

                if (!acao.equals("NOTIFICA_CSA") && !acao.equals("REGISTRAR_VALOR_CONSIGANCAO")) {
                    js.append("\n").append("  if (qtd == 0) {");
                    js.append("\n").append("    alert('").append(msgSelecione).append("');");
                    js.append("\n").append("    return false;");
                    js.append("\n").append("  }");
                }

                if (qtdMaxSelecaoMultipla != Integer.MAX_VALUE) {
                    js.append("\n").append("    if (qtd > ").append(qtdMaxSelecaoMultipla).append(") {");
                    js.append("\n").append("      alert('").append(msgErroQtdMaxSelecaoMultiplaSuperada).append("');");
                    js.append("\n").append("      return false;");
                    js.append("\n").append("    }");
                }
            }

            js.append("\n").append("  if (exibeMsgAdicional && msg != '') {");
            js.append("\n").append("    msg = msg + ' ' + msg2;");
            js.append("\n").append("  }");
            js.append("\n").append("  if (usarLinkAdicional && j2 != '') {");
            js.append("\n").append("    j = j2;");
            js.append("\n").append("  }");
            js.append("\n").append("  if (j.indexOf('?') >= 0) {");
            js.append("\n").append("      qs = qs.replace('?', '&');");
            js.append("\n").append("  }");

            if (acao.equals("RETIRAR_CONTRATO_COMPRA")) {
                js.append("\n").append("  $(\"#adeCodigo\").val(ade);");
                js.append("\n").append("  $(\"#confirmarRetirarPortabilidade\").modal('show');");
                js.append("\n").append("}");

            } else if (acao.equals("REPROVAR_ANEXO_CONSIGNACAO")) {
                js.append("\n").append("  $(\"#adeCodigo\").val(ade);");
                js.append("\n").append("  $(\"#modalReprovarAnexoConsignacao\").modal('show');");
                js.append("\n").append("}");

            } else if (acao.equals("REAT_CONSIGNACAO")) {

                js.append("\n").append("  if (typeof verificarAdesDataFim !== 'undefined') {");
                js.append("\n").append("    var resultadoVerificar = verificarAdesDataFim(); ");
                js.append("\n").append("    if (!resultadoVerificar) {");
                js.append("\n").append("        return false;");
                js.append("\n").append("    }");
                js.append("\n").append("  }");

                js.append("\n").append("  if (msg == '' || confirm(msg)) {");
                js.append("\n").append("    postData(j + qs);");
                js.append("\n").append("  }");
                js.append("\n").append("}");

            } else if (acao.equals("NOTIFICA_CSA") || acao.equals("REGISTRAR_VALOR_CONSIGANCAO")) {
                js.append("\n").append("var adesCodigosIncluir = $('[name=chkNotificar]').filter(':checked').map(function () {return this.value; }).get();");
                js.append("\n").append("var adesCodigosRemover = $('[name=chkNotificar]').filter(':not(:checked)').map(function () {return this.value; }).get();");
                js.append("\n").append(" qs += '&").append("adesCodigosIncluir").append("=' + adesCodigosIncluir;");
                js.append("\n").append(" qs += '&").append("adesCodigosRemover").append("=' + adesCodigosRemover;");
                js.append("\n").append("  if (qtd == 0) {");
                js.append("\n").append(" if (msg2 == '' || confirm(msg2)) {");
                js.append("\n").append("   postData(j + qs);");
                js.append("\n").append(" }");
                js.append("\n").append(" } else {");
                js.append("\n").append(" if (msg == '' || confirm(msg)) {");
                js.append("\n").append("   postData(j + qs);");
                js.append("\n").append(" }");
                js.append("\n").append(" }");
                js.append("\n").append("}");
            } else {
                js.append("\n").append("  if (msg == '' || confirm(msg)) {");
                js.append("\n").append("    postData(j + qs);");
                js.append("\n").append("  }");
                js.append("\n").append("}");
            }
        }

        return js.toString();
    }
}
