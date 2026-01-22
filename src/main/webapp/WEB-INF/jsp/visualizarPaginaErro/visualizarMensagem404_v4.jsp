<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="bodyContent">   
  <div class="alert alert-danger role="alert"><hl:message key="mensagem.erro.pagina.nao.encontrada.v3"/></div>
  <div class="clearfix">
    <button class="btn btn-primary" type="submit" onClick="postData('../v3/carregarPrincipal')">
      <svg width="17"><use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
        <hl:message key="rotulo.botao.voltar"/>
    </button>
  </div>
</c:set>
<t:empty_v4>    
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>
