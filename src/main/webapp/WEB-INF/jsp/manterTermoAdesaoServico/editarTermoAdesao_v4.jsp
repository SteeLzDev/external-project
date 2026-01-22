<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.TermoAdesaoServicoTO"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  String acao = (String) request.getAttribute("acao");
  String csaCodigo = (String) request.getAttribute("csaCodigo");
  String csaNome = (String) request.getAttribute("csaNome");
  String operacao = (String) request.getAttribute("operacao");
  String svcCodigo = (String) request.getAttribute("svcCodigo");
  String svcDescricao = (String) request.getAttribute("svcDescricao");
  String terAdsTexto = (String) request.getAttribute("terAdsTexto");
  String voltar = (String) request.getAttribute("voltar");
%>
<link type="text/css" rel="stylesheet" href="../css/uedit.ui.css" />
<link type="text/css" rel="stylesheet" href="../css/uedit.ui.complete.css" />
<script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
<script type="text/javascript" src="../js/uedit.js"></script>
<script type="text/javascript" src="../js/uedit.ui.complete.js"></script>
<c:set var="title">
<hl:message key="rotulo.termo.adesao.singular"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header hasIcon pl-3">
      <h2 class="card-header-title"><hl:message key="rotulo.termo.adesao.singular"/> - <%=TextHelper.forHtmlContent(svcDescricao.toUpperCase())%></h2>
    </div>
      <form method="post" action="../v3/manterTermoAdesaoServico?acao=salvarTermoAdesao&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
        <div class="row p-4">
          <div class="form-group col-sm-12 col-md-12">
            <ul style="margin-top: 5px" id="uedit_button_strip"></ul>
            <textarea name="innerTemp" cols="120" rows="30" class="form-control" id="uedit_textarea" onFocus="SetarEventoMascara(this,'#*65000',true);" onBlur="fout(this);ValidaMascara(this);"><%=TextHelper.forHtmlContent(terAdsTexto)%></textarea>
          </div>
        </div>
          <hl:htmlinput name="CSA_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CSA_CODIGO"))%>"/>
          <hl:htmlinput name="SVC_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>"/>
          <hl:htmlinput name="SVC_DESCRICAO" type="hidden" value="<%=TextHelper.forHtmlAttribute(svcDescricao)%>"/>
          <INPUT TYPE="hidden" NAME="innerTemp" VALUE="">
          <INPUT TYPE="hidden" NAME="operacao" VALUE="<%=TextHelper.forHtmlAttribute(operacao)%>">
    </form>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=voltar%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
      <a class="btn btn-primary" href="#no-back" onClick="f0.submit(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>  
</c:set>
<c:set var="javascript">    
<SCRIPT id="MainScript">
  var f0 = document.forms[0];
  function formLoad() {}
</SCRIPT>
<script type="text/javascript">
  var uedit_textarea = document.getElementById("uedit_textarea");
  var uedit_button_strip = document.getElementById("uedit_button_strip");
  var ueditorInterface = ueditInterface(uedit_textarea, uedit_button_strip);
</script>    
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>