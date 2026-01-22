<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/function-lib" prefix="fl"%>
<c:set var="title">
  ${tituloPagina}
</c:set>
<c:set var="bodyContent">
    <div class="alert alert-warning" role="alert">
      <p class="mb-0 font-weight-bold" id="msgSessaoExpirada">${fl:forHtmlContent(mensagemSessaoExpirada)}</p>
      <p class="mb-0 font-weight-bold"><hl:message key="mensagem.informacao.autentique.novamente"/></p>
    </div>
    <div class="clearfix">
      <button class="btn btn-primary" type="submit" onClick="postData('../v3/expirarSistema?acao=expirar','_top')">
        <svg width="17"><use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
        <hl:message key="rotulo.usuario.login"/>
      </button>
    </div>
</c:set>
<t:empty_v4>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>