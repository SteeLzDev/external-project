<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
String tipo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "tipo")) ? JspHelper.verificaVarQryStr(request, "tipo") : (request.getAttribute("tipo") != null ? request.getAttribute("tipo").toString() : null);
String link = null;
String linkRet = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "linkRet")) ? JspHelper.verificaVarQryStr(request, "linkRet") : (String) request.getAttribute("linkRet");
String linkRet64 = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "linkRet64")) ? JspHelper.verificaVarQryStr(request, "linkRet64") : (String) request.getAttribute("linkRet64");
if (!TextHelper.isNull(tipo) && tipo.equals("index")) {
  link = "javascript:top.location.href='../index.jsp';";
} else if (!TextHelper.isNull(tipo) && tipo.equals("principal")) {
  link = "../v3/carregarPrincipal";
} else if (!TextHelper.isNull(tipo) && tipo.equals("indisponivel")) {
  link = "";
} else if (!TextHelper.isNull(tipo) && tipo.equals("popup")) {
  link = "javascript:window.close();";
} else if (!TextHelper.isNull(linkRet) && !linkRet.equals("")) {
  String cache = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "cache")) ? JspHelper.verificaVarQryStr(request, "cache") : (String) request.getAttribute("cache");
  link = SynchronizerToken.updateTokenInURL(linkRet.replace('$','?').replace('|','&').replace('(','=') + ((!TextHelper.isNull(cache)) ? "&cache=" + cache : ""), request) + "&linkRet=" + linkRet;
} else if (!TextHelper.isNull(linkRet64) && !linkRet64.equals("")) {
  linkRet = TextHelper.decode64(linkRet64);
  link = SynchronizerToken.updateTokenInURL(linkRet, request); 
} else if (!TextHelper.isNull(tipo) && tipo.equals("msgErro")){
    String chaveMsg = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "chaveMsg")) ? JspHelper.verificaVarQryStr(request, "chaveMsg") : (String) request.getAttribute("chaveMsg");
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(chaveMsg, responsavel));
  link = "";
} else{
  link = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
}

boolean reconhecimentoFacialServidorSimulacao = request.getAttribute("exigeReconhecimentoFacil") != null && request.getAttribute("exigeReconhecimentoFacil").equals("true");
if(reconhecimentoFacialServidorSimulacao){
    link += "&exigeReconhecimentoFacil=false";
}
%>
<c:set var="bodyContent">
  <% if(!TextHelper.isNull(link)) { %> 
    <div class="clearfix">
      <button class="btn btn-primary" type="submit" onClick="postData('<%=TextHelper.forJavaScriptAttribute(link)%>')">
        <svg width="17"><use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
        <% if (!TextHelper.isNull(tipo) && tipo.equals("popup")) {%>
          <hl:message key="rotulo.botao.fechar"/>
        <%} else {%>
          <hl:message key="rotulo.botao.voltar"/>
        <%} %>
      </button>
    </div>
  <% } %> 
</c:set>
<t:empty_v4>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>