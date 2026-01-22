<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
String csaCodigo = (String) request.getAttribute("csaCodigo");
String svcCodigo = (String) request.getAttribute("svcCodigo");  
String indCodigoOrig = (String) request.getAttribute("indCodigoOrig");
String indDescricaoOrig = (String) request.getAttribute("indDescricaoOrig");
String indCodigoNovo = (String) request.getAttribute("indCodigoNovo"); 
String indDescricaoNovo = (String) request.getAttribute("indDescricaoNovo"); 
String reqColumnsStr = (String) request.getAttribute("reqColumnsStr");
String msgErro = (String) request.getAttribute("msgErro");
String maskNum = (String) request.getAttribute("maskNum");
String maskNaoNum = (String) request.getAttribute("maskNaoNum");
Boolean indiceNumerico = (Boolean) request.getAttribute("indiceNumerico");
%>

<c:set var="title">
<% if ( indCodigoOrig == null || indCodigoOrig.equals("null") ) {%>
  <hl:message key="rotulo.criar.indice.titulo"/>
<%} else { %>
  <hl:message key="rotulo.editar.indice.titulo"/>
<% } %>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/manterIndice?acao=modificar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <input type="hidden" name="operacao" value="<%=TextHelper.forHtmlAttribute((!TextHelper.isNull(indCodigoOrig)) ? "modificar" : "inserir" )%>">
    <div class="card">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title">    
    <% if ( indCodigoOrig == null || indCodigoOrig.equals("null") ) {%>
          <hl:message key="rotulo.criar.indice.subtitulo"/>
    <%} else { %>
          <%=TextHelper.forHtmlContent( indCodigoNovo != null? indCodigoNovo : indCodigoOrig )%>  - 
          <%=TextHelper.forHtmlContent( indDescricaoNovo != null? indDescricaoNovo : indDescricaoOrig )%>
    <% } %>    
        </h2>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="form-group col-sm-3">
            <label for="indCodigo"><hl:message key="rotulo.indice.codigo"/></label>
            <hl:htmlinput name="indCodigo"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(indCodigoNovo != null ? indCodigoNovo : JspHelper.verificaVarQryStr(request, "indCodigoOrig") )%>"
                          size="32"
                          mask="<%=TextHelper.forHtmlAttribute(indiceNumerico ? maskNum  : maskNaoNum )%>"
            />
          </div>
          
        </div>
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="indDescricao"><hl:message key="rotulo.indice.descricao"/></label>
            <hl:htmlinput name="indDescricao"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(indDescricaoNovo != null ? indDescricaoNovo : JspHelper.verificaVarQryStr(request, "indDescricaoOrig") )%>"
                          size="32"
                          mask="#*40"
            />
          </div>
        </div>  
      </div>
    </div>  
    <div class="btn-action mt-2 mb-0">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" onClick="if (vf_cadastro_ind()) { f0.submit(); } return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>
  <input name="csaCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
  <input name="svcCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>">
  <input name="indCodigoOrig" type="hidden" value="<%=TextHelper.forHtmlAttribute(indCodigoOrig)%>">
  <input name="indDescricaoOrig" type="hidden" value="<%=TextHelper.forHtmlAttribute(indDescricaoOrig)%>">
  </form>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
    var f0 = document.forms[0];
  </script>
  <script type="text/JavaScript">
    function formLoad(){   		
      focusFirstField();
    }
    
    // Verifica formulários de inserção e edição de indices
    function vf_cadastro_ind()
    {
      var Controles = new Array("indCodigo", "indDescricao");
      var Msgs = new Array('<hl:message key="mensagem.informe.indice.codigo"/>',
                   '<hl:message key="mensagem.informe.indice.descricao"/>');
      if (!ValidaCampos(Controles, Msgs)) {
        return false;
      } else {
        return true;
      }
    }    
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
