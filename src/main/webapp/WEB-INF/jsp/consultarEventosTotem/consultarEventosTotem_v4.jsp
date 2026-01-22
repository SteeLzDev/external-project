<%--
* <p>Title: pesquisarServidor_v4.jsp</p>
* <p>Description: pesquisa servidor/consignação</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

%>
<c:set var="title">
   <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>" method="post" name="form1">
    <%= SynchronizerToken.generateHtmlToken(request) %>
    <hl:htmlinput type="hidden" name="acao" value="pesquisar" />
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="26">
            <use xlink:href="../img/sprite.svg#i-consultar"></use></svg>
        </span>
        <h2 class="card-header-title"><hl:message key="mensagem.pesquisa.titulo"/></h2>
      </div>
      <div class="card-body">
        <fieldset>
          <div class="row">
            <div class="form-group col-sm-6">
              <hl:campoMatriculav4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>' />
            </div>
          </div>
          
          <div class="row">
            <div class="form-group col-sm-6">
              <hl:campoCPFv4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>' classe="form-control"/>
            </div>
          </div>
          
          <div class="row form-group col-sm-12 col-md-6 mt-4">
            <span id="periodo"><hl:message key="rotulo.pesquisa.data.periodo"/></span>
            <div class="row" role="group" aria-labelledby="periodo">
              <div class="form-check pt-2 col-sm-12 col-md-1">
                <div class="float-left align-middle mt-4 form-control-label">
                  <label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.pesquisa.data.prefixo.inicio"/></label>
                </div>
              </div>
              <div class="form-check pt-2 col-sm-12 col-md-5">
                 <hl:htmlinput name="periodoIni" di="periodoIni" type="text" classe="form-control w-100" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoIni"))%>"/>
              </div>
              <div class="form-check pt-2 col-sm-12 col-md-1">
                <div class="float-left align-middle mt-4 form-control-label">
                  <label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.pesquisa.data.prefixo.fim"/></label>
                </div>
              </div>
              <div class="form-check pt-2 col-sm-12 col-md-5">
                 <hl:htmlinput name="periodoFim" di="periodoFim" type="text" classe="form-control w-100" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoFim"))%>"/>
              </div>
            </div>
          </div>     
        </fieldset>
      </div>
    </div>
  </form>
 
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar" /></a>
    <a class="btn btn-primary" href="javascript:void(0);"  onClick="if(validaSubmit()){document.forms[0].submit();} return false;"> <svg width="20"> <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/> </a>
  </div>         

</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/listutils.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
f0 = document.forms["form1"];

function validaSubmit() {  
	if ((f0.RSE_MATRICULA == null || f0.RSE_MATRICULA.value == '') && 
		(f0.SER_CPF == null || f0.SER_CPF.value == '') && 
		(f0.periodoFim == null || f0.periodoFim.value == '') && (f0.periodoIni == null || f0.periodoIni.value == '')) {
        alert(mensagem('mensagem.informe.campo'));
        return false;
	} else {
		if (((f0.periodoIni != null && f0.periodoIni.value != '') && (f0.periodoFim == null || f0.periodoFim.value == '')) || 
			((f0.periodoFim != null && f0.periodoFim.value != '') && (f0.periodoIni == null || f0.periodoIni.value == ''))) {
	        alert(mensagem('mensagem.informe.ambos.campos.data'));
	        return false;
		}
	}
	
	return true;
}

</script>
<% if (!responsavel.isSer()) { %>
  <hl:campoMatriculav4 scriptOnly="true"/>
<% } %> 
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>


