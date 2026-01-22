<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.dto.entidade.ParamSvcTO" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.math.BigDecimal"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean exigeMotivo =(boolean)(request.getAttribute("exigeMotivo")!=null ? request.getAttribute("exigeMotivo") : false);
CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");
String linkAcao = (String) request.getAttribute("linkAcao");
String adeCodigo = (String) request.getAttribute("adeCodigo");
boolean exigeSenhaServidor = request.getAttribute("exigeSenhaServidor") !=null ? (boolean) request.getAttribute("exigeSenhaServidor") : true;
%>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>  
</c:set>
<c:set var="title">
    <hl:message key="rotulo.autorizar.reserva.titulo"/>
</c:set>
<c:set var="bodyContent">
<form action="<%=TextHelper.forHtmlAttribute(linkAcao)%>&_skip_history_=true" method="post" name="form1">
      <div class="row">
       <div class="col-sm-6">
          <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
          <% pageContext.setAttribute("autdes", autdes); %>
          <hl:detalharADEv4 name="autdes" table="true" type="consultar"/>
          <%-- Fim dos dados da ADE --%>
       </div>
       <div class="col-sm-6">
          <% if (exigeMotivo) { %>
              <div class="card">
                  <div class="card-header">
                    <h2 class="card-header-title"><hl:message key="rotulo.motivo.operacao.singular"/></h2>
                  </div>
                  <div class="card-body">
                  <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
                  <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.autorizacao", responsavel)%>" inputSizeCSS="col-sm-12"/>
                  <%-- Fim dos dados do Motivo da Operação --%>
                  </div>
              </div>
          <% } %>      

           <div class="card">
              <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.senha.servidor.consulta.singular"/></h2>
              </div>
              <div class="card-body">
                <div class="form-group">
                   <% if (autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_AGUARD_CONF) ||
                          autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_AGUARD_DEFER)) { %>
                 <%
                 String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
                 if (!TextHelper.isNull(mascaraLogin)) {
                 %>
                      <div class="row">
                        <div class="col-sm-12">
                        <label for="serLogin"><hl:message key="rotulo.usuario.autorizacao.servidor.singular"/></label>
                        <hl:htmlinput name="serLogin" type="text" classe="form-control" di="serLogin" size="15" mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>" />
                       </div>
                     </div>
                 <% } %>
                     <div class="row">
                       <hl:senhaServidorv4 senhaObrigatoria="<%=String.valueOf(exigeSenhaServidor)%>"                          
                          senhaParaAutorizacaoReserva="<%=String.valueOf(exigeSenhaServidor)%>"
                          nomeCampoSenhaCriptografada="serAutorizacao"
                          rseCodigo="<%=(String) autdes.getAttribute(Columns.RSE_CODIGO)%>"
                          classe="form-control"
                          inputSizeCSS="col-sm-12"
                          nf="btnEnvia" />
                   <% } %>    
                   </div>
                 </div>
              </div>
           </div>
           <div class="btn-action">
               <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
               
                <% if (autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_AGUARD_CONF) ||
                   autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_AGUARD_DEFER)) { %>
                     <a class="btn btn-primary" data-bs-dismiss="modal" id="btnEnvia" href="#" onClick="if(vf_autoriza_reserva()){f0.submit();} return false;"><hl:message key="rotulo.botao.confirmar"/></a>
               <% } %>
          </div> 
        </div>
      </div>
      <hl:htmlinput name="ADE_CODIGO" type="hidden" di="ADE_CODIGO" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" />
      <hl:htmlinput name="rseCodigo" type="hidden" di="rseCodigo" value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.RSE_CODIGO)))%>" />
    </form>
</c:set>
<c:set var="javascript">
<% if (exigeMotivo) { %>
  <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.autorizacao", responsavel)%>" scriptOnly="true" />
 <% } %>
<hl:senhaServidorv4 senhaObrigatoria="<%=String.valueOf(exigeSenhaServidor)%>"                          
  senhaParaAutorizacaoReserva="<%=String.valueOf(exigeSenhaServidor)%>"
  nomeCampoSenhaCriptografada="serAutorizacao"
  rseCodigo="<%=(String) autdes.getAttribute(Columns.RSE_CODIGO)%>"
  classe="form-control"
  inputSizeCSS="col-sm-12"
  nf="btnEnvia"
  scriptOnly="true" />

<script type="text/JavaScript">
f0 = document.forms[0];

window.onload = formLoad;

function formLoad() {
  focusFirstField();
}

function vf_autoriza_reserva() {
<% if (exigeSenhaServidor) { %>
	  if (f0.serLogin != null && f0.serLogin.value == '') {
	    alert('<hl:message key="mensagem.informe.ser.usuario"/>');
	    f0.serLogin.focus();
	    return false;
	  }
	  if (f0.senha != null && f0.senha.value == '') {
	    alert('<hl:message key="mensagem.informe.ser.senha"/>');
	    f0.senha.focus();
	    return false;
	  }
	  if (f0.senha != null) {
	    CriptografaSenha(f0.senha, f0.serAutorizacao, false);
	  }
<% } %>

  <% if (exigeMotivo) { %>
  if (!confirmaAcaoConsignacao())  {
       return false;
  }
  <% } %>

  return true;
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
