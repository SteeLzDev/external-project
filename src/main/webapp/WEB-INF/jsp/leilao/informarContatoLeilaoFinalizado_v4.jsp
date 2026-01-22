<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@page import="com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<TransferObject> leiloes = (List<TransferObject>) request.getAttribute("leiloes");

String dias = request.getAttribute("dias").toString();
%>

<c:set var="imageHeader">
    <use xlink:href="#i-mensagem"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.titulo.informar.contato.leilao.finalizado"/>
</c:set>
<c:set var="bodyContent">
	<form method="post" action="../v3/informarContatoLeilaoFinalizado?acao=salvar">
        <%=SynchronizerToken.generateHtmlToken(request)%>
        <div class="row firefox-print-fix">
            <div class="col-sm">
            <%
               for (TransferObject ade : leiloes) {
                   String adeNumero = TextHelper.forHtmlContent(ade.getAttribute(Columns.ADE_NUMERO).toString());
                   String soaData = TextHelper.forHtmlContent(DateHelper.toDateString((Date)ade.getAttribute(Columns.SOA_DATA)));
                   String soaDataValidade = TextHelper.forHtmlContent(DateHelper.toDateString((Date)ade.getAttribute(Columns.SOA_DATA_VALIDADE)));
                   String adeVlrLiquido = TextHelper.forHtmlContent(NumberHelper.format(((java.math.BigDecimal)ade.getAttribute(Columns.ADE_VLR_LIQUIDO)).doubleValue(), NumberHelper.getLang()));
                   String adeVlr = TextHelper.forHtmlContent(NumberHelper.format(((java.math.BigDecimal)ade.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang()));
                   String adePrazo = TextHelper.forHtmlContent(ade.getAttribute(Columns.ADE_PRAZO).toString());
                   String csaNome = TextHelper.forHtmlContent(ade.getAttribute(Columns.CSA_NOME).toString());
                   String csaIdentificador = TextHelper.forHtmlContent(ade.getAttribute(Columns.CSA_IDENTIFICADOR).toString());
            %>
                <div class="card">
                    <div class="card-header"><h2 class="card-header-title"><hl:message key="rotulo.dados.leilao.singular"/>:&nbsp;<%=adeNumero%></h2></div>
                    <div class="card-body">
                        <dl class="row data-list firefox-print-fix">
                            <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/></dt><dd class="col-6"><%=csaIdentificador%> - <%=csaNome%></dd>
                            <dt class="col-6"><hl:message key="rotulo.proposta.leilao.solicitacao.data.cadastro"/></dt><dd class="col-6"><%=soaData%></dd>
                            <dt class="col-6"><hl:message key="rotulo.proposta.leilao.solicitacao.data.validade"/></dt><dd class="col-6"><%=soaDataValidade%></dd>
                            <dt class="col-6"><hl:message key="rotulo.proposta.leilao.solicitacao.valor.liberado"/></dt><dd class="col-6"><%=adeVlrLiquido%></dd>
                            <dt class="col-6"><hl:message key="rotulo.proposta.leilao.solicitacao.valor.prestacao"/></dt><dd class="col-6"><%=adeVlr%></dd>
                            <dt class="col-6"><hl:message key="rotulo.proposta.leilao.solicitacao.prazo"/></dt><dd class="col-6"><%=adePrazo%></dd>
                        </dl>
                    </div>
                </div>
            <% } %>
            </div>
            <div class="col-sm">
                <div class="card">
                    <div class="card-header hasIcon">
                        <span class="card-header-icon"><svg width="24"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-mensagem"></use></svg></span>
                        <h2 class="card-header-title"><hl:message key="mensagem.atualizacao.email.telefone.informacao"/></h2>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="form-group col-sm-12 col-md-6">
                                <input type="radio" onclick="toggleResposta(!this.checked)" name="resposta" value="aceite"> <hl:message key="rotulo.opcao.informar.contato.leilao.finalizado.aceite"/>
                            </div>
                            <div class="form-group col-sm-12 col-md-6">
                                <input type="radio" onclick="toggleResposta(this.checked)" name="resposta" value="recusa"> <hl:message key="rotulo.opcao.informar.contato.leilao.finalizado.recusa"/>
                            </div>
                        </div>
                            <div class="row" id="divMsgRecusa" style="display:none">
                                <div class="form-group col-sm-24 col-md-12">
                                    <font class="aviso"><input type="checkbox" name="recusa" value="sim"> <hl:message key="mensagem.informar.contato.leilao.finalizado.recusa" arg0="<%=dias%>"/></font>
                                </div>
                            </div>
                            <div class="row" id="divMsgAceite" style="display:none">
                                    <div class="form-group col-sm-24 col-md-12">
                                        <label for="email"><hl:message key="rotulo.servidor.email"/></label>
                                        <input type="text" 
                                               name="email"
                                                 id="email"
                                        placeholder="<hl:message key="mensagem.informacao.alterar.senha.digite.email"/>"
                                              value=""
                                            onFocus="SetarEventoMascara(this,'#*100',true);"
                                             onBlur="fout(this);ValidaMascara(this);"
                                              class="form-control"/>
                                    </div>
                                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE)%>">
                                    <div class="form-group col-sm-2">
                                        <label for="ddd"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
                                        <hl:htmlinput name="ddd"
                                                        di="ddd"
                                                      type="text"
                                                    classe="form-control"
                                                     value=""
                                                      mask="<%=LocaleHelper.getDDDMask()%>"
                                               placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel) %>" />
                                    </div>
                                    </show:showfield>
                                    <div class="form-group col-sm-6">
                                        <label for="telefone"><hl:message key="rotulo.servidor.telefone"/></label>
                                        <hl:htmlinput name="telefone"
                                                        di="telefone"
                                                      type="text"
                                                    classe="form-control"
                                                     value=""
                                                      mask="<%=LocaleHelper.getCelularMask()%>"
                                              placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.servidor.telefone", responsavel) %>" />
                                    </div>
                            </div>
                    </div>
                </div>
            </div>
        </div>
	</form>
	<div class="btn-action">
	  <a class="btn btn-primary" href="#no-back" onClick="validaForm(); return false;"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/></a>
	</div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
 f0 = document.forms[0];
 
 function validaForm () {
   var resposta = jQuery("input[name=resposta]:checked").val();
   if (typeof(resposta) === 'undefined') {
       alert('<hl:message key="mensagem.erro.selecione.opcao.informar.contato.leilao.finalizado"/>');
       return false;
   } else if (resposta === 'recusa') {
       if (!f0.recusa.checked) {
           alert('<hl:message key="mensagem.erro.selecione.compromisso.recusa.informar.contato.leilao.finalizado"/>');
           return false;
       }
   } else {
       if (f0.email != null && (f0.email.value == null || f0.email.value.trim() == '')) {
           alert('<hl:message key="mensagem.informe.ser.email"/>');
           f0.email.focus();
           return false;
       }
       if (f0.email != null && f0.email.value != null && f0.email.value != '' && !isEmailValid(f0.email.value)) {
           alert('<hl:message key="mensagem.erro.email.invalido"/>');
           f0.email.focus();
           return false;
       }
       if (f0.ddd != null && (f0.ddd.value == null || f0.ddd.value.trim() == '')) {
           alert('<hl:message key="mensagem.informe.ser.telefone"/>');
           f0.ddd.focus(); 
           return false;
       }
       
       if (f0.telefone != null && (f0.telefone.value == null || f0.telefone.value.trim() == '')) {
           alert('<hl:message key="mensagem.informe.ser.telefone"/>');
           f0.telefone.focus();
           return false;
       }
   }
	f0.submit();
}

function toggleResposta(recusa) {
   if (recusa) {
       jQuery("#divMsgRecusa").show();
       jQuery("#divMsgAceite").hide();
   } else {
     jQuery("#divMsgRecusa").hide();
     jQuery("#divMsgAceite").show();
   }
}</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>