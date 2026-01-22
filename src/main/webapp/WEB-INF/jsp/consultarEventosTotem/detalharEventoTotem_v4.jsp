<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%> 
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<% 
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel"); 
CustomTransferObject evento = (CustomTransferObject) request.getAttribute("evento");
String image = TextHelper.encode64Binary((byte[]) evento.getAttribute("FOTO"));
String imageBiometria = TextHelper.encode64Binary((byte[]) evento.getAttribute("FOTOBIOMETRIA"));

%>
<c:set var="title">
<%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
<use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-md-12">
      <div class="card">
        <div class="card-header">
        
          <h2 class="card-header-title"><hl:message key="rotulo.anexo.arquivo.titulo"/></h2>
        </div>
        <div class="card-body">
        <dl class="row data-list firefox-print-fix">
          <dt class="col-6"><hl:message key="rotulo.matricula.singular"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(evento.getAttribute("MATRICULA"))%></dd>
          
          <dt class="col-6"><hl:message key="rotulo.servidor.cpf"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(evento.getAttribute("CPF"))%></dd>
          
          <dt class="col-6"><hl:message key="rotulo.evento.totem.data"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(evento.getAttribute("DATA"))%></dd>
          
          <dt class="col-6"><hl:message key="rotulo.evento.totem.ip.acesso"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(evento.getAttribute("IP"))%></dd>
          
          <dt class="col-6"><hl:message key="rotulo.evento.totem.descricao"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(evento.getAttribute("DESCRICAO"))%></dd>
        </dl>
        
        
        <div class="row">
        <%if(evento.getAttribute("FOTOBIOMETRIA") == null) {%>
        <div class="col-12 text-center">
              <img width="260" height="300" src="data:image/jpeg;base64,<%=TextHelper.forHtmlAttribute(image)%>" alt="<%=ApplicationResourcesHelper.getMessage("rotulo.evento.totem.foto", responsavel)%>">
        </div>
          
           <%} else {%>
           <div class="col-12 col-sm-6 img-eventos-totem-foto-face img-eventos-totem">
              <img width="260" height="300" src="data:image/jpeg;base64,<%=TextHelper.forHtmlAttribute(image)%>" alt="<%=ApplicationResourcesHelper.getMessage("rotulo.evento.totem.foto", responsavel)%>">
        </div>
        
          <div class="col-12 col-sm-6 img-eventos-totem">
              <img width="260" height="300" src="data:image/jpeg;base64,<%=TextHelper.forHtmlAttribute(imageBiometria)%>" alt="<%=ApplicationResourcesHelper.getMessage("rotulo.evento.totem.foto", responsavel)%>">
        </div>
          <%} %>
        
        </div>
        
      </div>
    </div>
    <div class="btn-action">
        <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
      </div>
  </div> 

<c:set var="javascript">
 <link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
 <script type="text/JavaScript">
  
 </script>
</c:set>
</c:set>
<t:page_v4>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
