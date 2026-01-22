<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@page import="com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<c:set var="imageHeader">
  <use xlink:href="#i-mensagem"></use>
</c:set>
<c:set var="title">
  <%=request.getAttribute("tituloPagina")%>
</c:set>
<c:set var="bodyContent">

  <%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  String msg = "";
  String funCodigo = (String) request.getAttribute("funCodigo");
  List<?> termosAdesao = (List<?>) request.getAttribute("termosAdesao");
  boolean verTermoAdesao = request.getAttribute("verTermoAdesao") != null;
  Boolean termoAceito = null;
  String tadCodigoAdesao = (String) request.getAttribute("tadCodigo");
  String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
  %>

  <div id="header-print">
    <img src="../img/econsig-logo-v5.png" alt="econsig">
    <p id="date-time-print"></p>
  </div>

  <div class="row d-print-none">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <button id="btnImprime" type="button" class="float-end btn btn-primary mt-0" onClick="imprimir()">
          <hl:message key="rotulo.botao.imprimir" />
        </button>
      </div>
    </div>
  </div>

  <div class="d-none d-print-block">
    <h3><hl:message key="rotulo.termo.adesao" /></h3>
  </div>

  <%if (verTermoAdesao) {%>
  <FORM NAME="form1" METHOD="post" ACTION="../v3/informarTermoAdesao?acao=editar" class="was-validated">
  <%} else {%>
  <FORM NAME="form1" METHOD="post" ACTION="../v3/informarTermoAdesao?acao=salvar" class="was-validated">
  <%}%>
    <div class="card">
      <div class="card-header hasIcon d-print-none">
        <span class="card-header-icon"><svg width="24"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-mensagem"></use></svg></span>
        <h2 class="card-header-title">
          <hl:message key="mensagem.confirmar.leitura.termo.adesao.titulo" />
        </h2>
      </div>
      <div class="card-body">
        <div class="alert alert-warning m-0 d-print-none" role="alert">
          <p class="mb-0">
            <hl:message key="mensagem.informacao.fazer.leitura.termo.adesao.para.continuar" />
          </p>
        </div>
        <div class="messages">
          <%
          Integer i = 0;
          Iterator<?> it = termosAdesao.iterator();
          while (it.hasNext()) {
            TransferObject termoAdesao = (TransferObject) it.next();

          	String tadCodigo = termoAdesao.getAttribute(Columns.TAD_CODIGO).toString();
          	msg = termoAdesao.getAttribute(Columns.TAD_TEXTO).toString();
          	if (termoAdesao.getAttribute(Columns.TAD_HTML).toString().equals("N")) {
               msg = new Markdown4jProcessorExtended().process(TextHelper.forHtmlContent(msg));
          	}

            if (!TextHelper.isNull(termoAdesao.getAttribute(Columns.LTU_TERMO_ACEITO))) {
               termoAceito = termoAdesao.getAttribute(Columns.LTU_TERMO_ACEITO).toString().equals(CodedValues.TPC_SIM);
            }

          	if (msg != null && !msg.equals("")) {%>
              <div class="message">
                <h3 class="message-title"><%=TextHelper.forHtmlContent(termoAdesao.getAttribute(Columns.TAD_TITULO).toString())%></h3>
                <%=msg%>
                <div class="custom-controls-stacked d-block d-print-none">
                  <fieldset>
                    <legend class="sr-only sr-only-focusable">
                      <hl:message key="rotulo.confirmacao.leitura.termo.adesao.confirmar.leitura" />
                    </legend>
                    <div class="custom-controls-stacked mt-2 d-block">
                      <label class="custom-control custom-radio" for="confirmar-leitura<%=tadCodigo%>">
                        <input id="confirmar-leitura<%=tadCodigo%>" name="confirmar-leitura<%=tadCodigo%>" class="custom-control-input" value="<%=CodedValues.TERMO_ADESAO_CONFIRMADO%>" required="" type="radio"
                               <%if(Boolean.TRUE.equals(termoAceito)) {%>checked="checked"<%}%>>
                        <span class="custom-control-indicator"></span>
                        <span class="custom-control-description"><hl:message key="rotulo.confirmacao.leitura.termo.adesao.aceitar" /></span>
                      </label>
                      <%if (termoAdesao.getAttribute(Columns.TAD_PERMITE_RECUSAR).toString().equals(CodedValues.TPC_SIM)) {%>
                      <label class="custom-control custom-radio" for="recusar-leitura<%=tadCodigo%>">
                        <input id="recusar-leitura<%=tadCodigo%>" name="confirmar-leitura<%=tadCodigo%>" class="custom-control-input" value="<%=CodedValues.TERMO_ADESAO_RECUSADO%>" required="" type="radio"
                               <%if(Boolean.FALSE.equals(termoAceito)) {%>checked="checked"<%}%>>
                        <span class="custom-control-indicator"></span>
                        <span class="custom-control-description"><hl:message key="rotulo.confirmacao.leitura.termo.adesao.recusar" /></span>
                      </label>
                      <%}%>
                      <%if (termoAdesao.getAttribute(Columns.TAD_PERMITE_LER_DEPOIS).toString().equals(CodedValues.TPC_SIM)) {%>
                      <label class="custom-control custom-radio" for="ler-depois<%=tadCodigo%>">
                        <input id="ler-depois<%=tadCodigo%>" name="confirmar-leitura<%=tadCodigo%>" class="custom-control-input" value="<%=CodedValues.TERMO_ADESAO_LER_DEPOIS%>" required="" type="radio"
                               <%if(TextHelper.isNull(termoAceito) && termoAdesao.getAttribute(Columns.TAD_PERMITE_LER_DEPOIS).toString().equals(CodedValues.TPC_SIM) && verTermoAdesao) {%>checked="checked"<%}%>>
                        <span class="custom-control-indicator"></span>
                        <span class="custom-control-description"><hl:message key="rotulo.confirmacao.leitura.termo.adesao.ler.depois" /></span>
                      </label>
                      <%}%>
                    </div>
                  </fieldset>
                </div>
              </div>
              <%i++;%>
            <%}%>
          <%}%>
        </div>
      </div>
    </div>
    <div class="btn-action">
      <%if (verTermoAdesao) {%>
      <a id="btnVoltar" class="btn btn-outline-danger" href="#no-back"><hl:message key="rotulo.botao.voltar"/></a>
      <%}%>
      <a class="btn btn-primary" href="#no-back" id="botaoConfirmar" onClick="validaForm(); return false;"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar" /></a>
    </div>
    <%if (verTermoAdesao) {%>
    <hl:htmlinput name="tadCodigo" di="tadCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(tadCodigoAdesao)%>"/>
    <%} else {%>
    <hl:htmlinput name="funCodigo" di="funCodigo" type="hidden" value="<%=!TextHelper.isNull(funCodigo) ? TextHelper.forHtmlAttribute(funCodigo) : ""%>"/>
    <%}%>
  </FORM>
</c:set>
<style>
  @media print {
    *{
      margin: 0;
      padding: 0;
      color: #000 !important;
      font-size: 11px;
    }
    #header-print img {max-width: 15%;}
  }
  @page{margin: 1cm;}
</style>
<c:set var="javascript">
<script type="text/JavaScript">
 f0 = document.forms[0];

 $(function () {
   $('#btnVoltar').bind('click', function (){
     postData('../v3/informarTermoAdesao?acao=listar&usuCodigo=' + <%TextHelper.forJavaScript(JspHelper.getAcessoSistema(session).getUsuCodigo());%> + '&listarmostraMensagem=true&limitaMsg=true');
   });
 });

 async function imprimir() {
   injectDate();
   window.print();
 }

 function injectDate() {
   const dateTimePrint = document.querySelector('#date-time-print');
   const printDate = new Date();
   printDate.toLocaleString("pt-br");
   dateTimePrint.innerHTML = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);
 }
 
 function validaForm() {
	 var i = 0;
     const botaoConfirmar = document.getElementById("botaoConfirmar");

     if (botaoConfirmar.disabled){
    	 return false;
     }
	 
     while (i < f0.elements.length) {
       if (i + 1 < f0.elements.length) {
         var e = f0.elements[i]; 
         var e2 = f0.elements[i + 1];
         var e3 = f0.elements[i + 2];
         if (e.name == e2.name && e.name == e3.name && e.type == 'radio' && e2.type == 'radio' && e3.type == 'radio') { 
           // Um dos 2 checkbox de mesmo nome tem que estar marcado
           if (e.checked || e2.checked || e3.checked) {
             i++; // Junto com o incremento la debaixo, vai pular o e2
           } else {
             alert('<hl:message key="mensagem.erro.existe.termo.adesao.sem.confirmacao"/>');
             e.focus();
             return false;
           }
         }
       }
       i++;
     }
     
     botaoConfirmar.disabled = true;
  	 f0.submit();
 }
 
 function confirmarTodasMensagens() {
     var i = 0;
     while (i < f0.elements.length) {
       if (i + 1 < f0.elements.length) {
         var e = f0.elements[i]; 
         var e2 = f0.elements[i + 1];
         if (e.id != null && e.id.includes("confirmar-leitura")) { 
           if (!e.checked) {
             e.checked = true;  
             i++;
           } else {
             e.checked = false;
             i++;
           }
         } else if(e2.id != null && e2.id.includes("confirmar-leitura")){
        	 if (!e2.checked) {
                 e2.checked = true;  
                 i++;
               } else {
                 e2.checked = false;
                 i++;
               }
         }
       }
       i++;
     }
}  

</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>