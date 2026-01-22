<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
String arrRisco = (String) request.getAttribute("arrRisco");
String arrData = (String) request.getAttribute("arrData");
String riscoTexto = (String) request.getAttribute("riscoTexto");
String rseCodigo = (String) request.getAttribute("rseCodigo");
%>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>  
</c:set>
<c:set var="title">
   <hl:message key="rotulo.editar.servidor.risco.csa.titulo"/>
</c:set>
<c:set var="bodyContent">
    <form action="../v3/cadastrarAnaliseRiscoServidor" method="post" name="form1">
      <input type="hidden" name="acao" value="salvar" />
      <input type="hidden" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" />
      <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.validar.servidor.dados.servidor"/></h2>
        </div>
        <div class="card-body">
          <dl class="row data-list firefox-print-fix">
            <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
            <hl:detalharServidorv4 name="servidor" scope="request"/>
            <%-- Fim dos dados do servidor --%> 
              <dt class="col-6"><hl:message key="rotulo.servidor.risco.csa"/></dt>
              <dd class="col-6"><%=TextHelper.forHtmlContent(riscoTexto)%></dd>
              <% if (!TextHelper.isNull(arrRisco)) { %>
                <dt class="col-6"><hl:message key="rotulo.servidor.risco.csa.data.cadastro"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(arrData)%></dd>
              <% } %>
          </dl>
        </div>
      </div>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.editar.grid"/></h2>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="form-group col-sm">
              <label for="riscoConsignataria"><hl:message key="rotulo.servidor.risco.csa"/></label>
              <select class="form-control" id="ARR_RISCO" name="ARR_RISCO">
                <option <%=TextHelper.isNull(arrRisco) ? "SELECTED" : ""%> value=""><hl:message key="rotulo.campo.selecione"/></option>
                <option <%=arrRisco.equals("0") ? "SELECTED" : ""%> value="0"><hl:message key="rotulo.servidor.risco.csa.baixissimo"/></option>
                <option <%=arrRisco.equals("1") ? "SELECTED" : ""%> value="1"><hl:message key="rotulo.servidor.risco.csa.baixo"/></option>
                <option <%=arrRisco.equals("2") ? "SELECTED" : ""%> value="2"><hl:message key="rotulo.servidor.risco.csa.medio"/></option>
                <option <%=arrRisco.equals("3") ? "SELECTED" : ""%> value="3"><hl:message key="rotulo.servidor.risco.csa.alto"/></option>
                <option <%=arrRisco.equals("4") ? "SELECTED" : ""%> value="4"><hl:message key="rotulo.servidor.risco.csa.altissimo"/></option>
              </select>
            </div>
          </div>
        </div>
      </div>
       <div class="btn-action">
         <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
         <a class="btn btn-primary" data-bs-dismiss="modal" href="#no-back" onClick="f0.submit(); return false;" id="btnEnviar"><hl:message key="rotulo.botao.confirmar"/></a>
      </div> 
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
function formLoad() {
  focusFirstField();
 }
</script>
<script type="text/JavaScript">
    f0 = document.forms[0];
   </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>