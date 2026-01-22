<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.rejeicao.servidor", responsavel);
ArrayList<TransferObject> rseList = (ArrayList<TransferObject>) request.getAttribute("servidores");
String rseCodigoList = "";
%>
<c:set var="title">
  <hl:message key="rotulo.validar.servidor.rejeitar.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-5">
      <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="26">
              <use xlink:href="../img/sprite.svg#i-servidor"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.validar.servidor.dados.servidor"/></h2>
        </div>
        <%
        for(TransferObject rseTO : rseList) {
        	if (!TextHelper.isNull(rseCodigoList)) {
        		rseCodigoList = rseCodigoList.concat(";");
        	}
        	rseCodigoList = rseCodigoList.concat((String)rseTO.getAttribute(Columns.RSE_CODIGO));
        %>
        <div class="card-body">
          <dl class="row data-list">
          <% request.setAttribute("servidor", rseTO); %>
            <hl:detalharServidorv4 name="servidor" scope="request"/>
          </dl>
        </div>
        <%} %>
      </div>
    </div>
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.validar.servidor.dados.operacao"/></h2>
        </div>
        <div class="card-body">
          <form method="post" action="../v3/validarServidor" name="form">
            <%= SynchronizerToken.generateHtmlToken(request) %>
            <input type="hidden" name="MM_update" value="true">
            <input type="hidden" name="acao" value="rejeitarMultiplos">
            <input type="hidden" name="offset" value="<%= TextHelper.forHtmlAttribute(request.getParameter("offset")) %>">
            <input type="hidden" name="rseCodigos" value="<%= rseCodigoList != null ? rseCodigoList : "" %>">
            <hl:efetivaAcaoMotivoOperacaov4 inputSizeCSS="col-sm-12" msgConfirmacao="<%=msgConfirmacao%>" operacaoRegistroServidor="true" tmoSempreObrigatorio="true"/>
          </form>
        </div>
      </div>
    </div> 
  </div>
  <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="if(confirmaAcaoConsignacao()){document.forms[0].submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
      </div> 
</c:set>
<c:set var="javascript">
<hl:efetivaAcaoMotivoOperacaov4 inputSizeCSS="col-sm-12" msgConfirmacao="<%=msgConfirmacao%>" operacaoRegistroServidor="true" tmoSempreObrigatorio="false" scriptOnly="true"/>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
