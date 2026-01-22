<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> lstSubOrgao = (List<TransferObject>) request.getAttribute("lstSubOrgao");
List<String> usuUnidades = (List<String>) request.getAttribute("usuUnidades");
HashMap<String,List<TransferObject>> hashOrgUnidades = (HashMap<String,List<TransferObject>>) request.getAttribute("hashOrgUnidades");
String usuCodigo = (String) request.getAttribute("usuCodigo");
String linkVoltar = (String) request.getAttribute("linkVoltar");
String orgCodigo = request.getAttribute("orgCodigo") != null ? (String) request.getAttribute("orgCodigo") : "";

boolean usuarioOrgao = request.getAttribute("usuarioOrgao") != null;
%>
<c:set var="title">
   <hl:message key="rotulo.usuario.unidades.permissao.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title">
         <hl:message key="rotulo.usuario.unidades.permissao.lista.unidades"/>
      </h2>
    </div>
    <form action="../v3/<%= !usuarioOrgao ? "listarUsuarioCse" : "listarUsuarioOrg"%>?acao=salvar&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>" method="post" name="form">
      <div class="row">
        <div class=" col-md form-check mt-4">
          <fieldset class="col-sm-12 col-md-12">
            <hl:htmlinput classe="form-check-input ml-1" type="checkbox" name="todasUnidades" di="todasUnidades" onClick="checkTodasUnidades(this);"/>
            <h3 class="legend"><label class="custom-control custom-ckeckbox pb-3" for="todasUnidades"><hl:message key="rotulo.usuario.unidades.permissao.permitir.todas"/></label></h3>
          </fieldset>
        </div>
      </div>
      <%for (TransferObject subOrgao : lstSubOrgao){ 
          String subCodigo = (String) subOrgao.getAttribute(Columns.SBO_CODIGO);
          String subDescricao = (String) subOrgao.getAttribute(Columns.SBO_DESCRICAO);
          if(hashOrgUnidades.get(subCodigo) == null){
              continue;
          }
          List<TransferObject> lstUnidades = hashOrgUnidades.get(subCodigo);
      %>
        <fieldset class="col-sm-12 col-md-12">
          <div class="row">
            <div class="form-group col-sm-12 col-md-12">
              <span id="<%=subCodigo%>"><%=TextHelper.forHtmlContent(subDescricao)%></span>
                <div class="form-check">
                  <div class="row">
                    <%for(TransferObject unidade : lstUnidades){ 
                      String uniCodigo = (String) unidade.getAttribute(Columns.UNI_CODIGO);
                      String uniDescricao = (String) unidade.getAttribute(Columns.UNI_DESCRICAO);
                      String uniIdentificador = (String) unidade.getAttribute(Columns.UNI_IDENTIFICADOR);
                  %>
                      <div class="col-sm-12 col-md-4">
                        <span class="align-text-top">
                          <input type="checkbox" class="form-check-input ml-1" name="uniCodigos" id="<%=uniCodigo%>" value="<%=uniCodigo%>" title="<%=TextHelper.forHtmlContent(uniIdentificador+"-"+uniDescricao)%>" onBlur="fout(this);ValidaMascara(this);" <%=usuUnidades !=null && !usuUnidades.isEmpty() && usuUnidades.contains(uniCodigo) ? "checked" : ""%>>
                          <label class="form-check-label labelSemNegrito ml-1" id="<%=uniCodigo%>" for="<%=uniCodigo%>"><%=TextHelper.forHtmlContent(uniIdentificador+"-"+uniDescricao)%></label>
                        </span>
                      </div>                      
                  <%} %>
                </div>
              </div>
            </div>
          </div>
        </fieldset>
      <%} %>
      <hl:htmlinput type="hidden" name="usuCodigo" di="usuCodigo" value="<%=usuCodigo%>" />
      <hl:htmlinput type="hidden" name="<%=Columns.getColumnName(Columns.ORG_CODIGO)%>" di="usuCodigo" value="<%=orgCodigo%>" />
    </form>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkVoltar, request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="f0.submit(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/javascript">
  f0 = document.forms[0];
  
  function checkTodasUnidades(data) {
	  checkboxes = document.getElementsByName('uniCodigos');
	  for(var i=0, n=checkboxes.length;i<n;i++) {
	    checkboxes[i].checked = data.checked;
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
