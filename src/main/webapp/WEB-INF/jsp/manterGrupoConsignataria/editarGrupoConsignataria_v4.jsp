<%--
* <p>Title: edt_grupo_consignataria</p>
* <p>Description: Página de edição de grupo de consignataria</p>
* <p>Copyright: Copyright (c) 2006</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: anderson.assis $
* $Revision: 31174 $
* $Date: 2021-01-21 15:49:30 -0300 (qui., 21 jan. 2021) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeCriarGrpCsa = (Boolean) request.getAttribute("podeCriarGrpCsa");
String tgcCodigo  = (String) request.getAttribute("tgcCodigo");
String tgcIdentificador = (String) request.getAttribute("tgcIdentificador");
String tgcDescricao = (String) request.getAttribute("tgcDescricao");
%>
<c:set var="title">
  <hl:message key="rotulo.editar.grupo.consignataria"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form action="../v3/manterGrupoConsignataria?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post">
    <input type="hidden" name="tgcCodigo" value="<%=TextHelper.forHtmlAttribute(tgcCodigo)%>">
    <input type="hidden" name="operacao" value="<%=TextHelper.forHtmlAttribute( (!TextHelper.isNull(tgcCodigo)) ? "modificar" : "inserir" )%>">
    
    <div class=card>
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title"><hl:message key="rotulo.grupo.consignataria.codigo"/></h2>
      </div>
      <div class="card-body">
        <fieldset>
          <div class="row">
            <div class="form-group col-sm-12 col-md-4">
              <label for="tgcIdentificador"><hl:message key="rotulo.grupo.consignataria.codigo"/></label>
              <input class="form-control" id="tgcIdentificador" name="tgcIdentificador" value="<%=TextHelper.forHtmlAttribute( tgcIdentificador )%>"/>
              <%=JspHelper.verificaCampoNulo(request, "tgcIdentificador")%>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-12 col-md-4">
              <label for="tgcDescricao"><hl:message key="rotulo.grupo.consignataria.descricao"/></label>
              <input class="form-control" id="tgcDescricao" name="tgcDescricao" value="<%=TextHelper.forHtmlAttribute( tgcDescricao )%>"/>
              <%=JspHelper.verificaCampoNulo(request, "tgcDescricao")%>
            </div>
          </div>
        </fieldset>
      </div>
    </div>
    
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="javascript:postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" ><hl:message key="rotulo.botao.cancelar"/></a>
    <% if (podeCriarGrpCsa) { %>
      <a class="btn btn-primary" name="submit2" href="#no-back" onClick="javascript: return vf_cadastro_grupo_csa();"><hl:message key="rotulo.botao.salvar"/></a>
    <% } %>
      
    </div>
      
  </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript">
    function formLoad() {
      f0.tgcIdentificador.focus();
    }
       
    function vf_cadastro_grupo_csa() {
  	  var Controles = new Array("tgcIdentificador", "tgcDescricao");
  	  var Msgs = new Array("<hl:message key="mensagem.informe.grupo.servico.codigo"/>",
  			  "<hl:message key="mensagem.informe.grupo.servico.descricao"/>");
  	  if(ValidaCamposV4(Controles, Msgs)){
  		  f0.submit();
  	  }
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