<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
OrgaoTransferObject orgao = (OrgaoTransferObject) request.getAttribute("orgao");
List<TransferObject> parametrosOrgao = (List<TransferObject>) request.getAttribute("parametrosOrgao");
boolean podeEditarParamOrgao = (Boolean) request.getAttribute("podeEditarParamOrgao");
%>
<c:set var="title">
  <hl:message key="rotulo.consultar.parametro.orgao.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/manterParamOrgao?<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <input type="hidden" name="acao" value="salvar">
    <input type="hidden" name="codigo" value="<%=TextHelper.forHtmlContent(orgao.getOrgCodigo())%>">
    <%=SynchronizerToken.generateHtmlToken(request)%>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title">
          <hl:message key="rotulo.consultar.parametro.orgao.titulo.arg0" arg0="<%=TextHelper.forHtmlContent(orgao.getOrgNome())%>"/>
        </h2>
      </div>
      <div class="card-body">
        <%
          for (TransferObject param : parametrosOrgao) {
            String codigo = param.getAttribute(Columns.TAO_CODIGO).toString();
            String descricao = param.getAttribute(Columns.TAO_DESCRICAO).toString();
            String campo = param.getAttribute("campo_parametro").toString();
        %>
        <div class="row">
          <div class="col-sm-6 col-md-12 mb-2">
            <div class="form-group mb-0">
              <label for="<%=TextHelper.forHtmlContent(codigo)%>"><%=TextHelper.forHtmlContent(descricao)%></label>
            </div>
            <div class="form-check mt-2"><%=campo%></div>
            </div>
        </div>
        <% } %>
      </div>
    </div>
  </form>
  <div class="row">
    <div class="col-sm-12">
      <div class="btn-action">
        <% if (podeEditarParamOrgao) { %>
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.acoes.cancelar"/></a>
          <a class="btn btn-primary" href="#no-back" onClick="f0.submit(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
        <% } else { %>
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
        <% } %>
      </div>
    </div>
  </div>
  <div class="row">
  </div>  
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
    f0 = document.forms[0];
    function formLoad() {
        focusFirstField();
    }
    window.onload = formLoad;
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>