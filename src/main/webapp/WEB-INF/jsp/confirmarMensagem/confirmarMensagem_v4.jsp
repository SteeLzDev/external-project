<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@page
  import="com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@page import="com.zetra.econsig.dto.entidade.MensagemTO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
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
  List<?> mensagens = (List<?>) request.getAttribute("mensagens");
  String usuDataCad = (String) request.getAttribute("usu_data_cad");
  int paramMenLidaIndividualmenteInt = (int) request.getAttribute("paramMenLidaIndividualmenteInt");
  %>

  <FORM NAME="form1" METHOD="post" ACTION="../v3/confirmarMensagem?acao=salvar&usu_data_cad=<%=TextHelper.forHtmlAttribute(usuDataCad)%>" class="was-validated">
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="24"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-mensagem"></use></svg></span>
        <h2 class="card-header-title">
          <hl:message key="mensagem.confirmar.leitura.mensagem.titulo" />
        </h2>
      </div>
      <div class="card-body ">
        <div class="alert alert-warning m-0" role="alert">
          <p class="mb-0">
            <hl:message key="mensagem.informacao.fazer.leitura.para.continuar" />
          </p>
        </div>
        <%if (responsavel.isSup()) {%>
        <fieldset>
          <div  class="check-message form-check mt-4">
            <hl:htmlinput classe="form-check-input" type="checkbox" name="confirmarLeitura" di="confirmarLeitura" onClick="confirmarTodasMensagens();" />
            <h3 class="legend"><label class="form-check-label pb-3 custom-control custom-ckeckbox" for="confirmarLeitura"><hl:message key="rotulo.botao.confirmar.leitura"/> </label></h3>
          </div>
        </fieldset>
        <%}if (responsavel.isSer() || responsavel.isCseOrg()) {
          if (paramMenLidaIndividualmenteInt > 0) {%>
        <fieldset class="col-sm-12 col-md-12">
          <div class="check-message form-check mt-4">
            <hl:htmlinput classe="form-check-input" type="checkbox" name="confirmarLeituraMensagensNaoLidasIndividualmente" di="confirmarLeituraMensagensNaoLidasIndividualmente" onClick="confirmarMensagensCseSer();" />
            <h3 class="legend">
              <label class="custom-control custom-ckeckbox pb-3" for="confirmarLeituraMensagensNaoLidasIndividualmente"><hl:message key="rotulo.botao.confirmar.leitura.mensagens.nao.lidas.individualmente" arg0="${paramMenLidaIndividualmenteInt}" /></label>
            </h3>
          </div>
        </fieldset>
        <%}%>
        <%}%>
        <div class="messages">
          <%
          Integer i = 0;
          Iterator<?> it = mensagens.iterator();
          while (it.hasNext()) {
          	MensagemTO menTO = (MensagemTO) it.next();

          	String menCodigo = menTO.getMenCodigo();
          	String menLidaIndividualmente = menTO.getMenLidaIndividualmente();
          	String funCodigo = menTO.getFunCodigo();

          	if (!TextHelper.isNull(funCodigo) && !responsavel.temPermissao(funCodigo)) {
          		continue;
          	}

          	msg = menTO.getMenTexto();
          	if (menTO.getMenHtml().equals("N")) {
               msg = new Markdown4jProcessorExtended().process(TextHelper.forHtmlContent(msg)).toString();
          	}

          	if (msg != null && !msg.equals("")) {
          	  	msg = msg.replaceAll("increment_video\\[", "<iframe src=\"https://player.vimeo.com/video/");
              	msg = msg.replaceAll("\\]final", "\" style=\"width: 100%; height: 40em; border:0;\" title=\"vimeo video\"></iframe>");
				msg = msg.replaceAll("increment_url_", " https://vimeo.com/");
          	%>
              <div class="message">
                <h3 class="message-title"><%=TextHelper.forHtmlContent(menTO.getMenTitulo())%></h3>
                <%=msg%>
                <div class="custom-controls-stacked d-block">
                  <fieldset>
                    <legend class="sr-only sr-only-focusable">
                      <hl:message key="rotulo.confirmacao.leitura.mensagem.confirmar.leitura" />
                    </legend>
                    <div class="custom-controls-stacked mt-2 d-block">
                      <%if (responsavel.isCseOrg() || responsavel.isSer()) {%>
                        <label class="custom-control custom-radio" for="confirmar-leitura<%=menCodigo%>">
                          <input id="confirmar-leitura<%=menCodigo%>" name="<%=menTO.getMenLidaIndividualmente()%><%=i.toString()%>" class="custom-control-input" value="<%=CodedValues.TPC_SIM%>" required="" type="radio">
                          <span class="custom-control-indicator"></span>
                          <span class="custom-control-description"><hl:message key="rotulo.confirmacao.leitura.mensagem.confirmar.leitura" /></span>
                        </label>
                        <%if (menTO.getMenPermiteLerDepois().equals("S")) {%>
                        <label class="custom-control custom-radio" for="ler-depois<%=menCodigo%>">
                          <input id="ler-depois<%=menCodigo%>" name="<%=menTO.getMenLidaIndividualmente()%><%=i.toString()%>" class="custom-control-input" value="<%=CodedValues.TPC_NAO%>" required="" type="radio">
                          <span class="custom-control-indicator"></span>
                          <span class="custom-control-description"><hl:message key="rotulo.confirmacao.leitura.mensagem.ler.depois" /></span>
                        </label>
                        <%}%>
                      <%} else {%>
                        <label class="custom-control custom-radio" for="confirmar-leitura<%=menCodigo%>">
                          <input id="confirmar-leitura<%=menCodigo%>" name="confirma<%=menCodigo%>" class="custom-control-input" value="<%=CodedValues.TPC_SIM%>" required="" type="radio">
                          <span class="custom-control-indicator"></span>
                          <span class="custom-control-description"><hl:message key="rotulo.confirmacao.leitura.mensagem.confirmar.leitura" /></span>
                        </label>
                        <%if (menTO.getMenPermiteLerDepois().equals("S")) {%>
                        <label class="custom-control custom-radio" for="ler-depois<%=menCodigo%>">
                          <input id="ler-depois<%=menCodigo%>" name="confirma<%=menCodigo%>" class="custom-control-input" value="<%=CodedValues.TPC_NAO%>" required="" type="radio">
                          <span class="custom-control-indicator"></span>
                          <span class="custom-control-description"><hl:message key="rotulo.confirmacao.leitura.mensagem.ler.depois" /></span>
                        </label>
                        <%}%>
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
      <a class="btn btn-primary" href="#no-back" onClick="validaForm(); return false;"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar" /></a>
    </div>
  </FORM>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
 f0 = document.forms[0];
 
 function validaForm() {
	 var i = 0;
     while (i < f0.elements.length) {
       if (i + 1 < f0.elements.length) {
         var e = f0.elements[i]; 
         var e2 = f0.elements[i + 1];
         if (e.name == e2.name && e.type == 'radio' && e2.type == 'radio') { 
           // Um dos 2 checkbox de mesmo nome tem que estar marcado
           if (e.checked || e2.checked) {
             i++; // Junto com o incremento la debaixo, vai pular o e2
           } else {
             alert('<hl:message key="mensagem.erro.existem.mensagens.sem.confirmacao"/>');
             e.focus();
             return false;
           }
         }
       }
       i++;
     }
  f0.submit();
}
 
 
 function confirmarMensagensCseSer() { // caso o responsável seja cse ou ser
	  var i = 1;
	  var j = 0;  
	  while (i < f0.elements.length) {
	     var e = f0.elements[i]; 
	     var e2 = f0.elements[i + 1];
	     if (e.name == e2.name && e.type == 'radio' && e2.type == 'radio') {
			 if (e.name == "N" + j && e.checked == false){
			   e.checked = true;
	      	 } else if (e.checked == true) {
	      	   e2.checked = true;
	      	 }
			 j++;
	      	 i++;
	     }
	     i++;
	   }  
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