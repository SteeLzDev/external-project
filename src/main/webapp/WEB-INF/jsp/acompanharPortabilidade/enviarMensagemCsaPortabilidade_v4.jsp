<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.web.ParamSession"%>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    ParamSession paramSession = (ParamSession) request.getAttribute("paramSession");
	boolean podeIncluirAnexo = (boolean) request.getAttribute("podeIncluirAnexo");
	int tamMaxMsg = (int) request.getAttribute("tamMaxMsg");
	String tituloPagina = (String) request.getAttribute("tituloPagina");
	String adeCodigo = (String) request.getAttribute("adeCodigo");
%>
<c:set var="title">
  <%=TextHelper.forHtml(tituloPagina)%>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set> 
<c:set var="bodyContent">  
<div id="main">
   <div class="card">
    <div class="card-header"> 
      <h2 class="card-header-title"> <hl:message key="mensagem.acao.enviar.mensagem.csa.destino.clique.aqui"/></h2>
    </div>
      <div class="card-body">
       <form method="post" action="../v3/acompanharPortabilidade?acao=enviarMsgCsaPortabilidade&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" ENCTYPE="multipart/form-data">
         <div class="row">
          <div class="form-group col-sm-12">
          </div>
        </div>
            
      <div class="row">
        <div class="form-group col-sm-6">
          <label for="texto"><hl:message key="rotulo.mensagem.csa.portabilidade.texto"/></label>
          <textarea class="form-control" id="texto" rows="6" placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.texto", responsavel)%>" name="mensagem" onFocus="SetarEventoMascara(this,'#*<%=TextHelper.forJavaScript((tamMaxMsg))%>',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>
        </div>
      </div>  
      
        <%if(podeIncluirAnexo){ %>
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="iArquivo"><hl:message key="rotulo.mensagem.csa.portabilidade.arquivo"/></label>
            <input type="file" class="form-control" name="FILE1" id="FILE1" size="56">
          </div>
        </div>
        <%} %>
        <input type="hidden" name="adeCodigo" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>">
     </form>
   </div>
  </div>
  <div class="btn-action">
       <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" ><hl:message key="rotulo.botao.cancelar"/></a>
       <a class="btn btn-primary" href="#" onClick="javascript: enviar(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>
  </div>
  <!-- Modal aguarde -->
  <div class="modal fade" id="modalAguarde" data-focus="false" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
   <div class="modal-dialog-upload modal-dialog" role="document">
	 <div class="modal-content">
	   <div class="modal-body">
		 <div class="row">
		   <div class="col-md-12 d-flex justify-content-center">
			 <img src="../img/loading.gif" class="loading">
		   </div>
		   <div class="col-md-12">
			 <div class="modal-body"><span><hl:message key="mensagem.upload.generico.aguarde"/></span></div>            
		   </div>
		 </div>
	   </div>
	 </div>
   </div>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
f0 = document.forms[0];

function enviar() {
	var msg = trim(f0.mensagem.value);
	   
	   // Verifica quantidade de caracteres informados na mensagem
	   if (msg.length < 10) {
	       alert('<hl:message key="mensagem.erro.email.csa.portabilidade.texto.minimo"/>');
	       f0.mensagem.focus();
	       return;
	   }
	   
	   // Verifica se existe pelo menos uma letra na mensagem
	   var regex = /([a-zA-Z]+)/g;
	   if (!msg.match(regex)) {
	       alert('<hl:message key="mensagem.erro.email.csa.portabilidade.texto.invalido"/>');
	       f0.mensagem.focus();
	       return;
	   }
	   $('#modalAguarde').modal({
			backdrop: 'static',
			keyboard: false
		});
	   f0.submit();
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
