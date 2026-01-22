<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="title">
    <%= request.getAttribute("tituloPagina") %>
</c:set>
<c:set var="bodyContent">
    <c:if test="${requestScope['versaoLeiaute'] == 'v4'}">
        <c:if test="${!requestScope['omitirSobreZetra']}">
            <div class="about">
                <div class="about-logo">
                    <img src="../img/sobre_empresa_v4.png" alt="<hl:message key="rotulo.alt.sobre.a.zetrasoft"/>"
                         width="163" height="51">
                </div>
                <h2 class="about-title"><hl:message key="rotulo.zetrasoft"/></h2>
                <p><hl:message key="mensagem.sobre.a.zetrasoft"/></p>
            </div>
        </c:if>
        <c:if test="${!requestScope['omitirSobreSistema']}">
            <div class="about">
                <div class="about-logo">
                    <img src="../img/sobre_sistema_v4.png" alt="<hl:message key="rotulo.alt.sobre.versao.sistema"/>"
                         width="191" height="83">
                </div>
                <h2 class="about-title"><hl:message key="rotulo.nome.sistema"/></h2>
                <p><hl:message key="mensagem.sobre.o.sistema"/></p>
                <p class="version"><hl:message key="mensagem.sobre.versao.sistema.argdata"
                                               arg0="<%= (String) request.getAttribute("dataUltimaAtualizacaoSistema") %>"/></p>
            </div>
        </c:if>
    </c:if>
    <c:if test="${requestScope['versaoLeiaute'] != 'v4'}">
        <c:if test="${!requestScope['omitirSobreZetra']}">
            <div class="about">
                <div class="about-logo">
                    <img src="../img/sobre_empresa_v5.png" alt="<hl:message key="mensagem.sobre.a.serasa"/>"
                         width="163" height="51">
                </div>
                <h2 class="about-title"><hl:message key="rotulo.serasa.experian"/></h2>
                <p><hl:message key="mensagem.sobre.a.serasa"/></p>
            </div>
        </c:if>
        <c:if test="${!requestScope['omitirSobreSistema']}">
            <div class="about">
                <div class="about-logo">
                    <img src="../img/sobre_sistema_v5.png" alt="<hl:message key="rotulo.alt.sobre.versao.sistema"/>"
                         width="191" height="83">
                </div>
                <h2 class="about-title"><hl:message key="rotulo.nome.sistema"/></h2>
                <p><hl:message key="mensagem.sobre.o.sistema"/></p>
                <p class="version"><hl:message key="mensagem.sobre.versao.sistema.argdata"
                                               arg0="<%= (String) request.getAttribute("dataUltimaAtualizacaoSistema") %>"/></p>
            </div>
        </c:if>
            </c:if>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>