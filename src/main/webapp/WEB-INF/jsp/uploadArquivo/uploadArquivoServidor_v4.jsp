<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %><%--
  Created by IntelliJ IDEA.
  User: douglas.neves
  Date: 25/11/2022
  Time: 15:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String linkRet = (String) request.getAttribute("linkVoltar");
    String action = (String) request.getAttribute("action");
%>
<c:set var="title">
    <hl:message key="rotulo.acao.anexar.foto"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-upload"></use>
</c:set>
<c:set var="bodyContent">
    <form method="post" action="${action}" enctype="multipart/form-data">
        <div class="card">
            <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.anexar.foto.servidor"/></h2>
            </div>
            <div class="card-body">
                <div class="row">
                    <input type="file" id="arquivo" class="form-control" accept="image/*" name="arquivo"
                           onChange="validation()">
                </div>
            </div>
        </div>

        <div id="actions" class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back"
               onClick="<%=(String)"postData('" + linkRet + "'); return false;"%>"><hl:message
                    key="rotulo.botao.cancelar"/></a>
            <a id="salvarBtn" class="btn btn-primary" href="#no-back" onClick="salvar(); return false;"><hl:message
                    key="rotulo.botao.salvar"/></a>
        </div>
        <div id="logger">
        </div>
    </form>
</c:set>

<c:set var="javascript">
    <script type="text/JavaScript">
        let file = document.getElementById("arquivo");
        var f0 = document.forms[0];

        function validation() {
            if (!file.files[0].type.includes('image')) {
                postData("../v3/uploadArquivoServidor?acao=salvar&TIPO=N" + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
                file.value = '';
            }
        }

        function salvar() {
            if (file.value === '') {
                alert('Nenhum arquivo inserido');
            } else {
               f0.submit();
            }
        }
    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>