<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = (AcessoSistema)request.getAttribute("responsavel"); 
CustomTransferObject autdes = (CustomTransferObject)request.getAttribute("autdes");
Boolean exigeSenhaSerCancel = (Boolean)request.getAttribute("exigeSenhaSerCancel");
Boolean exigeMotivo = (Boolean)request.getAttribute("exigeMotivo");
String adeCodigo = (String)request.getAttribute("adeCodigo");;
%>
<c:set var="title">
<hl:message key="rotulo.cancelar.renegociacao.titulo"/>
</c:set>
<c:set var="imageHeader">
<use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <form action="../v3/cancelarRenegociacao" method="post" name="form1">
     <div class="row">
       <div class="col-sm-7">
          <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
          <% pageContext.setAttribute("autdes", autdes); %>
          <hl:detalharADEv4 name="autdes" table="false" type="alterar"/>
          <%-- Fim dos dados da ADE --%>
        </div>
         <% if (autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_DEFERIDA) || autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_EMANDAMENTO) || autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_LIQUIDADA)) { %>
          <div class="col-sm-5">
           <div class="card">
              <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.senha.servidor.consulta.singular"/></h2>
              </div>
              <div class="card-body">
                   <%
                    String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
                    if (!TextHelper.isNull(mascaraLogin)) {
                    %>
                      <dl>
                        <dt class="col-sm-12 px-1">
                          <label><hl:message key="rotulo.usuario.autorizacao.servidor.singular"/></label>
                         </dt>
                          <dd class="col-sm-12 px-1">
                            <hl:htmlinput name="serLogin" type="text" classe="form-control" di="serLogin" size="15" mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>" />
                          </dd>
                      </dl>
                    <% } %>
                  <hl:senhaServidorv4 senhaObrigatoria="<%=(String)(exigeSenhaSerCancel? "true": "false")%>"               
                  senhaParaAutorizacaoReserva="true"
                  nomeCampoSenhaCriptografada="serAutorizacao"
                  rseCodigo="<%=(String) autdes.getAttribute(Columns.RSE_CODIGO)%>"
                  nf="btnEnvia"
                  classe="form-control" 
                  inputSizeCSS="col-sm-12"
                  separador2pontos="false"
                  />
                  <%   out.print(SynchronizerToken.generateHtmlToken(request)); %>
                  <% } %>
             </div>
          </div>
          <% if (exigeMotivo) { %>
          <div class="card">
              <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.efetiva.acao.consignacao.dados.tipo.mtv.cancelamento"/></h2>
              </div>
              <div class="card-body">
              <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
              <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancReneg", responsavel)%>" inputSizeCSS="col-sm-12"/>
              <%-- Fim dos dados do Motivo da Operação --%>
              </div>
           </div>
          <% } %>
           <div class="btn-action">
            <% if (autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_DEFERIDA) || autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_EMANDAMENTO) || autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_LIQUIDADA)) { %>
              <a class="btn btn-outline-danger" name="btnCancelar" id="btnCancelar" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
              <a class="btn btn-primary" href="#confirmarSenha" data-bs-toggle="modal" name="btnEnvia" id="btnEnvia" href="#" onClick="if(vf_cancelar_renegociacao()){f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
            <% } %>
            <hl:htmlinput name="MM_update" type="hidden" value="form1" />
            <hl:htmlinput name="ADE_CODIGO" type="hidden" di="ADE_CODIGO" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" />
            <hl:htmlinput name="rseCodigo" type="hidden" di="rseCodigo" value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.RSE_CODIGO)))%>" />
            <hl:htmlinput name="acao"      type="hidden" di="acao"      value="cancelarRenegociacao" />
          </div>
        </div>
        </div>
      </form>
</c:set>
<c:set var="javascript">
  <% if (exigeMotivo) { %>
    <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancReneg", responsavel)%>" scriptOnly="true" />
   <% } %>
  <hl:senhaServidorv4 senhaObrigatoria="<%=(String)(exigeSenhaSerCancel? "true": "false")%>"               
    senhaParaAutorizacaoReserva="true"
    nomeCampoSenhaCriptografada="serAutorizacao"
    rseCodigo="<%=(String) autdes.getAttribute(Columns.RSE_CODIGO)%>"
    nf="btnEnvia"
    classe="form-control" 
    inputSizeCSS="col-sm-12"
    separador2pontos="false"
    scriptOnly="true"
    />

  <script type="text/JavaScript">
  f0 = document.forms[0];
  function formLoad() {
    focusFirstField();
  }
  
  function vf_cancelar_renegociacao() {
    if (f0.serLogin != null && f0.serLogin.value == '') {
      alert('<hl:message key="mensagem.informe.ser.usuario"/>');
      f0.serLogin.focus();
      return false;
    }
    <% if (exigeSenhaSerCancel) { %>
    if (f0.senha != null && f0.senha.value == '') {
      alert('<hl:message key="mensagem.informe.ser.senha"/>');
      f0.senha.focus();
      return false;
    }
    <% } %>
    if (f0.senha != null) {
      CriptografaSenha(f0.senha, f0.serAutorizacao, false);
    }
    <% if (exigeMotivo) { %>
     if(!confirmaAcaoConsignacao())  {
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

