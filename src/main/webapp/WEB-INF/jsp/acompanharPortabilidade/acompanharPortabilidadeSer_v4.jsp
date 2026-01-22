<%--
* <p>Title: acompanharPortabilidadeSer</p>
* <p>Description: Página de acompanhamento de contratos comprados no leiaute v4</p>
* <p>Copyright: Copyright (c) 2006</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: igor.lucas $
* $Revision: 27066 $
* $Date: 2019-06-28 10:40:51 -0300 (sex, 28 jun 2019) $
--%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<% AcessoSistema responsavel = JspHelper.getAcessoSistema(request); %>
<c:set var="title">
  <hl:message key="rotulo.acompanhar.compra.contrato.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-operacional"><hl:message key="rotulo.aprovacao.saldo.devedor.pendente.titulo"/></use>
</c:set>

<c:set var="bodyContent">
  <hl:listaAcompanhamentoComprav4
    pesquisar="true"
    filtroConfiguravel="3"
    link = "../v3/acompanharPortabilidade?acao=acompanhar"
    csaCodigo="<%=null%>"
    orgCodigo="<%=null%>"
    corCodigo="<%=null%>"
    criteriosPesquisa="<%=(CustomTransferObject)(new CustomTransferObject())%>"
  />
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
  </div>
  <% response.flushBuffer(); %>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
</c:set>


<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
