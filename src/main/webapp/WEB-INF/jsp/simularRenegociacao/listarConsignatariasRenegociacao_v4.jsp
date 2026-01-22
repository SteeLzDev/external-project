<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String rseCodigo = (String) request.getAttribute("rseCodigo");
String svcCodigo = (String) request.getAttribute("svcCodigo");
String svcDescricao = (String) request.getAttribute("svcDescricao");
String svcIdentificador = (String) request.getAttribute("svcIdentificador");
String orgCodigo = (String) request.getAttribute("orgCodigo");

List<TransferObject> csaList = (List <TransferObject>) request.getAttribute("csaList");
%>
<c:set var="title">
  <hl:message key="rotulo.simulacao.consignataria.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form NAME="form1" METHOD="post" ACTION="../v3/simularRenegociacao">
    <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.consignataria.plural"/></h2>
      </div>
      <div class="card-body table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th> <hl:message key="rotulo.funcao.codigo"/></th>
              <th> <hl:message key="rotulo.consignataria.singular"/></th>
              <th> <hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
          <% if (csaList == null || csaList.size() == 0) { %>
             <tr>
               <td colspan="3"><hl:message key="mensagem.erro.simulacao.nenhuma.consignataria.encontrada"/></td>
             </tr>
          <% } else { %>
              <%
                 CustomTransferObject next = null;
                 String csaIdentificador, csaNome, csaCodigo;
                 Iterator it = csaList.iterator();
                 while (it.hasNext()) {
                    next = (CustomTransferObject) it.next();
                    csaCodigo = next.getAttribute(Columns.CSA_CODIGO).toString();
                    csaIdentificador = next.getAttribute(Columns.CSA_IDENTIFICADOR).toString();
                    csaNome = next.getAttribute(Columns.CSA_NOME).toString();
              %>
                   <tr>
                    <td>
                        <%=TextHelper.forHtmlContent(csaIdentificador)%>
                    </td>
                    <td>
                        <%=TextHelper.forHtmlContent(csaNome.toUpperCase())%>
                    </td>
                    <td>
                      <a href="#no-back" onClick="javascript:enviar('<%=TextHelper.forJavaScript(csaCodigo)%>', '<%=TextHelper.forJavaScript(svcCodigo)%>', '<%=TextHelper.forJavaScript(csaNome )%>', '<%=TextHelper.forJavaScript(svcDescricao )%>', '<%=TextHelper.forJavaScript(csaIdentificador )%>'); return false;"><hl:message key="rotulo.acoes.selecionar"/></a>
                    </td>
                  </tr>
            <% } %>
          <% } %>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="3"><hl:message key="rotulo.lote.listagem.consignatarias"/>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(paramSession.getLastHistory())%>'); return false;" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
  
  <hl:htmlinput name="CSA_CODIGO"         type="hidden" di="CSA_CODIGO"         value="" />
  <hl:htmlinput name="CSA_NOME"           type="hidden" di="CSA_NOME"           value="" />
  <hl:htmlinput name="CSA_IDENTIFICADOR"  type="hidden" di="CSA_IDENTIFICADOR"  value="" />
  <hl:htmlinput name="SVC_CODIGO"         type="hidden" di="SVC_CODIGO"         value="" />
  <hl:htmlinput name="SVC_DESCRICAO"      type="hidden" di="SVC_DESCRICAO"      value="" />
  <hl:htmlinput name="SVC_IDENTIFICADOR"  type="hidden" di="SVC_IDENTIFICADOR"  value="<%=TextHelper.forHtmlAttribute(svcIdentificador )%>" />
  <hl:htmlinput name="acao"               type="hidden" di="acao"               value="pesquisarConsignacao" />
  <hl:htmlinput name="funcao"             type="hidden" di="funcao"             value="<%=(String)CodedValues.FUN_SIMULAR_RENEGOCIACAO%>" />
  <hl:htmlinput name="RSE_MATRICULA"      type="hidden" di="RSE_MATRICULA"      value="<%=TextHelper.forHtmlAttribute(responsavel.getRseMatricula() )%>" />
  <% if (responsavel.isSer()) { %>
    <hl:htmlinput name="RSE_CODIGO"       type="hidden" di="RSE_CODIGO"         value="<%=TextHelper.forHtmlAttribute(responsavel.getRseCodigo() )%>" />
  <% } %>
  <hl:htmlinput name="TIPO_LISTA"         type="hidden" di="TIPO_LISTA"         value="pesquisa" />
 </form>
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript">
f0 = document.forms[0];

function enviar(csaCodigo, svcCodigo, csaNome, svcDescricao, csaIdentificador) {
	f0.CSA_CODIGO.value = csaCodigo;
	f0.CSA_NOME.value = csaNome;
	f0.SVC_CODIGO.value = svcCodigo;
	f0.SVC_DESCRICAO.value = svcDescricao;
	f0.CSA_IDENTIFICADOR.value = csaIdentificador;
	
	f0.submit(); 
}

</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>