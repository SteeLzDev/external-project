<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String tipo = (String) request.getAttribute("tipo");
String csa = (String) request.getAttribute("csa");
String titulo = (String) request.getAttribute("titulo");
String novo = (String) request.getAttribute("novo");

%>
<c:set var="title">
  <hl:message key="rotulo.pesquisa.cnpj.empresa.correspondente.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form action="../v3/manterCorrespondente?acao=consultar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
    <div id="main">
      <div class="row">
        <div class="col-sm">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.pesquisar.cnpj.empresa.correspondente"/></h2>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="iCNPJ"><hl:message key="rotulo.cnpj.empresa.correspondente"/></label>
                  <%String onFocusCnpj = "SetarEventoMascara(this,'"+ LocaleHelper.getCnpjMask() + "',true);"; %>
                  <hl:htmlinput name="ECO_CNPJ"
                    di="iCNPJ"
                    type="text"
                    classe="form-control"
                    value=""
                    onFocus="<%=onFocusCnpj%>"
                    size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjSize())%>"
                    maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMaxLenght())%>"    
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cnpj", responsavel)%>"                 
                    />
                </div>
              </div>
            </div>
          </div>
          <hl:htmlinput name="novo" type="hidden" di="novo" value="<%=TextHelper.forHtmlAttribute(novo )%>" />
          <hl:htmlinput name="titulo" type="hidden" di="titulo" value="<%=TextHelper.forHtmlAttribute(titulo )%>" /> 
          <hl:htmlinput name="csa" type="hidden" di="csa" value="<%=TextHelper.forHtmlAttribute(csa)%>" />
          <hl:htmlinput name="tipo" type="hidden" di="tipo" value="<%=TextHelper.forHtmlAttribute(tipo)%>" />
          <div class="btn-action">
            <a class="btn btn-outline-danger" HREF="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.acoes.cancelar"/></a> 
            <a class="btn btn-primary" name="btnEnvia" id="btnEnvia" href="#no-back" onClick="vf_cadastro_cnpj();"><hl:message key="rotulo.acao.pesquisar"/></a>
          </div>
        </div>
      </div>
    </div>
  </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  	var f0 = document.forms[0];
  	window.onload = formLoad;
  </script>
  <script type="text/JavaScript">
    function vf_cadastro_cnpj()
    {
      var Controles = new Array("ECO_CNPJ");
      var Msgs = new Array('<hl:message key="mensagem.informe.empresa.correspondente.cnpj2"/>');
      
      if (!CGC_OK(extraiNumCNPJCPF(f0.ECO_CNPJ.value))) {     
         f0.ECO_CNPJ.focus();
         return ;
      }
      
      if (ValidaCampos(Controles, Msgs)) {    
          f0.submit();    
      }                     
    }
    
    function formLoad() {
       f0.ECO_CNPJ.focus();
    }                       
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>