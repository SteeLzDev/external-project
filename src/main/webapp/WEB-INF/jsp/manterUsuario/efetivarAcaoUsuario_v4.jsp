<%--
* <p>Title: efetivarAcaoUsuario</p>
* <p>Description: Permite colocar informações adicionais nas ações realizadas no usuário</p>
* <p>Copyright: Copyright (c) 2010</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

UsuarioTransferObject usuario = (UsuarioTransferObject) request.getAttribute("usuario");
String usuCodigo = (String) request.getAttribute("usuCodigo");
String codigoEntidade = (String) request.getAttribute("codigoEntidade");
String msgErro = (String) request.getAttribute("msgErro");
String status = (String) request.getAttribute("STATUS");
String _skip_history_ = (String) request.getAttribute("_skip_history_");
String urlDestino = (String) request.getAttribute("urlDestino");

String tituloPagina = (String) request.getAttribute("tituloPagina");
String msgConfirmacao = (String) request.getAttribute("msgConfirmacao");

String usuCpf = usuario.getUsuCPF();
String usuLogin = usuario.getStuCodigo().equals(CodedValues.STU_EXCLUIDO) ? usuario.getUsuTipoBloq() + "(*)" : usuario.getUsuLogin();
String ipAcesso = usuario.getUsuIpAcesso();
if (!TextHelper.isNull(ipAcesso)) {
    ipAcesso = ipAcesso.replaceAll(";", "<br>&nbsp;");    
}
String ddnsAcesso =  usuario.getUsuDDNSAcesso();
if (!TextHelper.isNull(ddnsAcesso)) {
    ddnsAcesso = ddnsAcesso.replaceAll(";", "<br>&nbsp;");    
}

%>
<c:set var="title">
  <%=TextHelper.forHtml(tituloPagina)%>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-7">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.efetiva.acao.usuario.dados.gerais"/></h2>
        </div>
        <div class="card-body">
          <dl class="row data-list">
            <dt class="col-5"><hl:message key="rotulo.consignante.singular"/>:</dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel))%></dd>
            <dt class="col-5"><hl:message key="rotulo.usuario.nome"/>:</dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(usuario.getUsuNome())%></dd>
            <dt class="col-5"><hl:message key="rotulo.usuario.login"/>:</dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(usuLogin)%></dd>
            <% if (!TextHelper.isNull(usuCpf)) { %>
              <dt class="col-5"><hl:message key="rotulo.usuario.cpf"/>:</dt>
              <dd class="col-7"><%=usuCpf%></dd>
            <% } %>
            <dt class="col-5"><hl:message key="rotulo.usuario.email"/>:</dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(usuario.getUsuEmail())%></dd>
            <% if (!TextHelper.isNull(ipAcesso)) { %>
              <dt class="col-5"><hl:message key="rotulo.usuario.ips.acesso"/>:</dt>
              <dd class="col-7"><%=ipAcesso%></dd>
            <% } %>
            <% if (!TextHelper.isNull(ddnsAcesso)) { %>
              <dt class="col-5"><hl:message key="rotulo.usuario.enderecos.acesso"/>:</dt>
              <dd class="col-7"><%=ddnsAcesso%></dd>
            <% } %>
          </dl>
        </div>
      </div>
    </div>
    <div class="col-sm-5">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.motivo.operacao.singular"/></h2>
        </div>
        <div class="card-body">
          <form method="post" action="<%=TextHelper.forHtmlAttribute(urlDestino)%>" name="formTmo">
            <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
            <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=(String)msgConfirmacao%>" operacaoUsuario="true" inputSizeCSS="col-sm-12"/>
            <%-- Fim dos dados do Motivo da Operação --%>
            <input type="hidden" name="MM_update" value="formTmo">
            <%
            out.print(SynchronizerToken.generateHtmlToken(request));
            %>
            <input type="hidden" name="USU_CODIGO" value="<%=usuCodigo%>">
            <input type="hidden" name="codigo" value="<%=codigoEntidade%>">
            <input type="hidden" name="STATUS" value="<%=status%>">
            <input type="hidden" name="_skip_history_" value="<%=_skip_history_%>">
          </form>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="#no-back" name="btnEnvia" id="btnEnvia" onClick="if(confirmaAcaoConsignacao()){f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=(String)msgConfirmacao%>" operacaoUsuario="true" scriptOnly="true"/>
<script type="text/JavaScript">
function formLoad(){
  focusFirstField();
}
var f0 = document.forms[0];
window.onload = formLoad;
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
