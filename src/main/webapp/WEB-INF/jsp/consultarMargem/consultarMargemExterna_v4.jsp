<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="title">
  <hl:message key="rotulo.consultar.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
<form action="../v3/consultarMargem?<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
  <div class="row">
    <div class="col-sm">
      <div class="card d-print-none" id="consultaMargem">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.consultar.margem.resultado"/></h2>
        </div>
        <div class="card-body">
          <dl class="row data-list">
          <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
            <hl:detalharServidorv4 name="servidor" margem="lstMargens" scope="request" exibeIconConSer="true"/>
          <%-- Fim dos dados da ADE --%>
          </dl>
        </div>
      </div>
      <div class="btn-action">        
        <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      </div>
    </div>
  </div>
</form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>