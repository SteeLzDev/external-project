<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>

<c:set var="bodyContent">

  <div class="clearfix row justify-content-end">
    <div class="col-12 col-sm-8">
      <button class="btn btn-primary" onClick="postData('<%=TextHelper.forHtmlAttribute(LoginHelper.getPaginaLogin())%>'); return false;">
        <svg width="17"> 
            <use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
        <hl:message key="rotulo.botao.voltar"/>
      </button>
    </div>
  </div>

</c:set>
<t:empty_v4>
  <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>