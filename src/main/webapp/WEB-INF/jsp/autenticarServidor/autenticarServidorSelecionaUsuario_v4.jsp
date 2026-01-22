<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);

String serCpf = (String) request.getAttribute("serCpf");

List<TransferObject> usuarios = (List) request.getAttribute("usuarios");
%>
<c:set var="bodyContent">
    <form id="enviar"
          name="enviar" 
          method="post" 
          action="../v3/autenticar?acao=autenticar"  
          autocomplete="off">
          
      <input name="username" id="username" type="hidden">
      <input name="codigo_orgao" id="codigo_orgao" type="hidden">
      <input name="orgao" id="orgao" type="hidden">
      <input name="usuLogin" id="usuLogin" type="hidden">
      <input name="serCpf" id="serCpf" type="hidden" value="<%=TextHelper.forHtmlAttribute(serCpf)%>">

      <div class="alert alert-warning" role="alert">
        <hl:message key="mensagem.selecione.usuario.fluxo.cpf"/>
      </div>      
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.matricula.singular"/></th>
            <th scope="col"><hl:message key="rotulo.orgao.singular"/></th>
          </tr>
        </thead>
        <tbody>
          <%
            int row = 0;
            for (TransferObject usuario : usuarios) {
	           String matricula = (String) usuario.getAttribute(Columns.RSE_MATRICULA);
               String usuLogin = (String) usuario.getAttribute(Columns.USU_LOGIN);
               String orgao = (String) usuario.getAttribute(Columns.ORG_NOME);

               String codigo = "";
               String identificador = "";

               if (loginComEstOrg) {
                  codigo = (String) usuario.getAttribute(Columns.ORG_CODIGO);
                  identificador = (String) usuario.getAttribute(Columns.ORG_IDENTIFICADOR);
               } else {
                  codigo = (String) usuario.getAttribute(Columns.EST_CODIGO);
                  identificador = (String) usuario.getAttribute(Columns.EST_IDENTIFICADOR);
                }
          %> 
          <tr onClick="preSubmmit(<%= row %>)">
            <td style="display:none;">
              <input id="<%=TextHelper.forHtmlAttribute("username" + row)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(matricula)%>">
              <input id="<%=TextHelper.forHtmlAttribute("codigo_orgao_row" + row)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(codigo)%>">
              <input id="<%=TextHelper.forHtmlAttribute("orgao_row" + row)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(identificador)%>">
              <input id="<%=TextHelper.forHtmlAttribute("usuLogin_row" + row)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(usuLogin)%>">
            </td>
            <td><%=TextHelper.forHtmlContent(matricula)%></td>
            <td><%=TextHelper.forHtmlContent(orgao)%></td>
          </tr>
          <%
            row++;
            } 
          %>
        </tbody>
      </table>
      
      <div class="row">
        <div class="col-12">
          <span class="float-md-right">
            <button class="btn btn-outline-danger" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#no-back" onclick="postData('../v3/autenticar'); return false;">
              <hl:message key="rotulo.botao.voltar"/>
            </button>
          </span>
        </div>
      </div>
 
     <%out.print(SynchronizerToken.generateHtmlToken(request));%>      
  </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  f0 = document.forms[0];  

  function preSubmmit(selected_value) {	  
	  const username = $('#username'+selected_value).val();
	  const codigo_orgao = $('#codigo_orgao_row'+selected_value).val();
	  const orgao = $('#orgao_row'+selected_value).val();
	  const usuLogin = $('#usuLogin_row'+selected_value).val();
	  
	  $('#username').val(username);
	  $('#codigo_orgao').val(codigo_orgao);
	  $('#orgao').val(orgao);
	  $('#usuLogin').val(usuLogin);
	  
      f0.submit();
  }
  </script>
</c:set>
<t:empty_v4>    
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>
