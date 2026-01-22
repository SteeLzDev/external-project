<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String usuCodigo = (String) request.getAttribute("usuCodigo");
String funCodigo = (String) request.getAttribute("funCodigo");
String usuNome = (String) request.getAttribute("usuNome");
String usuLogin = (String) request.getAttribute("usuLogin");
String tipo = (String) request.getAttribute("tipo");
String codEntidade = (String) request.getAttribute("codEntidade");

boolean podeEdtRestAcessoFun = (boolean) request.getAttribute("podeEdtRestAcessoFun");

Map<String, EnderecoFuncaoTransferObject> mapFuncoesTo = (Map<String, EnderecoFuncaoTransferObject>) request.getAttribute("mapFuncoesTo");

// Se for edição de usuário csa/cor, verifica se parâmetro de sistema permite cadastro de ip interno
boolean permiteIpInterno = (boolean) request.getAttribute("permiteIpInterno");

String funDescricao = (String) request.getAttribute("funDescricao");
String eafIpAcesso = (String) request.getAttribute("eafIpAcesso");
String eafDdnsAcesso = (String) request.getAttribute("eafDdnsAcesso");

%>
<c:set var="title">
  <hl:message key="rotulo.usuario.manutencao.restricao.acesso.funcao.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form action="../v3/manterRestricaoAcessoUsuario?acao=editar"  method="POST" name="form1">
  <input type="HIDDEN" name="usucodigo" value="<%=TextHelper.forHtmlAttribute(usuCodigo)%>">
  <input type="HIDDEN" name="funcodigo" value="<%=TextHelper.forHtmlAttribute(funCodigo)%>">
  <% out.println(SynchronizerToken.generateHtmlToken(request)); %>
  <input type="hidden" id="ip_list" name="ip_list" value="<%=TextHelper.forHtmlAttribute(eafIpAcesso)%>">
  <input type="hidden" id="ddns_list" name="ddns_list" value="<%=TextHelper.forHtmlAttribute(eafDdnsAcesso)%>">
  <input type="hidden" id="atualizar" name="atualizar" value="true">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(" " + funCodigo + " - " + funDescricao)%></h2>
    </div>
    <div class="card-body">
      <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
        <jsp:param name="tipo_endereco" value="numero_ip"/>
        <jsp:param name="nome_campo" value="novoIp"/>
        <jsp:param name="nome_lista" value="listaIps"/> 
        <jsp:param name="lista_resultado" value="eafIpAcesso"/>
        <jsp:param name="label" value="rotulo.usuario.ips.acesso"/>
        <jsp:param name="mascara" value="#I30"/>
        <jsp:param name="placeHolder" value="mensagem.placeholder.digite.ip.acesso"/>
        <jsp:param name="pode_editar" value="<%=(boolean) podeEdtRestAcessoFun %>"/>      
        <jsp:param name="bloquear_ip_interno" value="<%=(boolean)!permiteIpInterno%>"/>
      </jsp:include>
      <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
        <jsp:param name="tipo_endereco" value="url"/>
        <jsp:param name="nome_campo" value="novoDDNS"/>
        <jsp:param name="nome_lista" value="listaDDNSs"/>
        <jsp:param name="lista_resultado" value="eafDdnsAcesso"/>
        <jsp:param name="label" value="rotulo.usuario.enderecos.acesso"/>
        <jsp:param name="mascara" value="#*100"/>
        <jsp:param name="placeHolder" value="mensagem.placeholder.digite.endereco.acesso"/>
        <jsp:param name="pode_editar" value="<%=(boolean) podeEdtRestAcessoFun %>"/> 
      </jsp:include>
    </div>
  </div>
  <div class="btn-action">
    <% if (!podeEdtRestAcessoFun) { %> 
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></A>
    <% } else { %>
     <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
     <A class="btn btn-primary"  href="#no-back"  onClick="montaListaIps('eafIpAcesso','listaIps'); montaListaIps('eafDdnsAcesso','listaDDNSs'); f0.submit(); return false;"><hl:message key="rotulo.botao.salvar"/></A>
    <% } %>
  </div>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/usuario.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/listagem.js"></script>
<script type="text/JavaScript">
function formLoad() {
    preencheLista('ip_list','listaIps');
    preencheLista('ddns_list','listaDDNSs');
}
</script>
<script type="text/JavaScript">
  var f0 = document.forms[0];
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>