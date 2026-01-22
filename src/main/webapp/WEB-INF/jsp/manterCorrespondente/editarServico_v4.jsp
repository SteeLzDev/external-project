<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean TEM_VLR_INTERVENIENCIA_COR = (Boolean) request.getAttribute("TEM_VLR_INTERVENIENCIA_COR");
String valorInterveniencia = (String) request.getAttribute("valorInterveniencia");
String valorIntervenienciaPadrao = (String) request.getAttribute("valorIntervenienciaPadrao");
String valorIntervenienciaRef = (String) request.getAttribute("valorIntervenienciaRef");
String valorIntervenienciaRefPadrao = (String) request.getAttribute("valorIntervenienciaRefPadrao");
  
Map<String, Boolean> parametrosSvc = (Map<String, Boolean>) request.getAttribute("parametrosSvc");
String csa_codigo = (String) request.getAttribute("csa_codigo");
String cor_codigo = (String) request.getAttribute("cor_codigo");
String svc_codigo = (String) request.getAttribute("svc_codigo");
String svc_identificador = (String) request.getAttribute("svc_identificador");
String svc_descricao = (String) request.getAttribute("svc_descricao");
List<String> tps_codigos = (ArrayList<String>) request.getAttribute("tpsCodigos");
%>

<c:set var="javascript">
<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
<script type="text/JavaScript" src="../js/validaform.js"></script>
<script type="text/JavaScript" src="../js/validacoes.js"></script>
<script type="text/JavaScript" src="../js/xbdhtml.js"></script>
<script type="text/JavaScript" src="../js/validaemail.js"></script>
<script type="text/JavaScript">
  var f0 = document.forms['form1'];
  function formLoad() {
    // Focaliza o primeiro campo de edição
    focusFirstField();
  }
  
  function validaTextPadrao(checkbox, cse, nome, nome_ref) {
    var f0 = document.forms[0];
    for (i=0; i < f0.elements.length; i++) {
      var e = f0.elements[i];
      if (e.name == nome) {
        if (!checkbox.checked) {
          e.disabled = false;
        } else {
          e.style = null;
          e.disabled = true;
          if (e.type == 'text') {
            e.value = cse;
          } 
        }
      }
      if (e.name == nome_ref) {
        if ((checkbox.checked) && (<%=(boolean)valorIntervenienciaRefPadrao.equals("2")%>)) {
          e.options[1].selected = true;
          e.style = null;
          e.disabled = true;   
        } else if ((checkbox.checked) && (<%=(boolean)valorIntervenienciaRefPadrao.equals("1")%>)) {
          e.options[0].selected = true;
          e.style = null;
          e.disabled = true;
        } else if ((checkbox.checked) &&(<%=(boolean)valorIntervenienciaRefPadrao.equals("")%>)) {
          e.style = null;	
          e.disabled = true;
        } else {
          e.disabled = false;
        } 
      }
    }        
  } 
  
  function setHidden(campo, valor) {
    campo.value = valor;
  }
  
  function habilitaRadios() {
    var f0 = document.forms[0];
    for (i=0; i < f0.elements.length; i++) {
      var e = f0.elements[i];
      if ((e.type == 'radio') && (e.type == 'text')) {
        e.disabled = false;
      }
    }
  }
  window.onload = formLoad;
</script>
</c:set>
<c:set var="title">
<hl:message key="rotulo.manutencao.servico.cor"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(svc_identificador)%> - <%=TextHelper.forHtmlContent(svc_descricao)%></h2>
    </div>
    <% if (tps_codigos != null && !tps_codigos.isEmpty()) { %>
      <div class="card-body">
        <form method="post" action="../v3/manterCorrespondente?acao=salvarServico&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">           
          <% if (parametrosSvc.containsKey(CodedValues.TPS_VLR_INTERVENIENCIA) && ((Boolean) parametrosSvc.get(CodedValues.TPS_VLR_INTERVENIENCIA)).booleanValue()) {
                 valorInterveniencia = (!valorInterveniencia.equals("") ? NumberHelper.reformat(valorInterveniencia, "en", NumberHelper.getLang()) : "");
                 valorIntervenienciaPadrao = (!valorIntervenienciaPadrao.equals("") ? NumberHelper.reformat(valorIntervenienciaPadrao, "en", NumberHelper.getLang()) : "");
          %>
        <div class="row">
          <% if (!TEM_VLR_INTERVENIENCIA_COR) { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">    
              <label for="valorInterveniencia"><hl:message key="rotulo.servico.cor.valor.interveniencia"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" onFocus="SetarEventoMascaraV4(this,'#F11',true);" onBlur="fout(this);ValidaMascaraV4(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" VALUE="<%=TextHelper.forHtmlAttribute(valorIntervenienciaPadrao)%>" SIZE="10" disabled >
            </div>
            <div class="form-group col-sm-2 col-md-3">
              <label for="tipoDeValor"><hl:message key="rotulo.servico.cor.tipo.valor"/></label>              
                <SELECT CLASS="form-control col-sm-12 m-1" NAME="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF" id="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" DISABLED>
                  <OPTION VALUE="1" <%=(String)(valorIntervenienciaRefPadrao.equals("1")?"SELECTED":"")%>><hl:message key="rotulo.servico.valor"/></OPTION>
                  <OPTION VALUE="2" <%=(String)(valorIntervenienciaRefPadrao.equals("2")?"SELECTED":"")%>><hl:message key="rotulo.servico.valor.percentual"/></OPTION>
                </SELECT>
            </div>
            <div class="float-left align-middle mt-5 form-control-label">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" value="1" onChange="validaTextPadrao(this, '<%=TextHelper.forJavaScript(valorIntervenienciaPadrao)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_INTERVENIENCIA)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF');" checked>
              <label for="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
            </div>                        
          <% } else { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="valorInterveniencia"><hl:message key="rotulo.servico.cor.valor.interveniencia"/></label> 
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" onFocus="SetarEventoMascaraV4(this,'#F11',true);" onBlur="fout(this);ValidaMascaraV4(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" VALUE="<%=TextHelper.forHtmlAttribute(valorInterveniencia)%>" SIZE="10" >
            </div>
            <div class="form-group col-sm-2 col-md-3"> 
              <label for="tipoDeValor"><hl:message key="rotulo.servico.cor.tipo.valor"/></label>                                        
              <SELECT CLASS="form-control col-sm-12 m-1" NAME="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF" id="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                <OPTION VALUE="1" <%=(String)(valorIntervenienciaRef.equals("1")?"SELECTED":"")%>><hl:message key="rotulo.servico.valor"/></OPTION>
                <OPTION VALUE="2" <%=(String)(valorIntervenienciaRef.equals("2")?"SELECTED":"")%>><hl:message key="rotulo.servico.valor.percentual"/></OPTION>
              </SELECT>
            </div>
            <div class="float-left align-middle mt-5 form-control-label">              
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" value="1" onChange="validaTextPadrao(this, '<%=TextHelper.forJavaScript(valorIntervenienciaPadrao)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_INTERVENIENCIA)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF');">
              <label for="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>         
            </div>
          <% } %>
          </div>
        <% } %>
        <input name="svc" type="hidden" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
        <input type="hidden" name="MM_update" value="form1">
        <input type="hidden" name="svc" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
        <input type="hidden" name="SVC_IDENTIFICADOR" value="<%=TextHelper.forHtmlAttribute(svc_identificador)%>">
        <input type="hidden" name="SVC_DESCRICAO" value="<%=TextHelper.forHtmlAttribute(svc_descricao)%>">
        <input type="hidden" name="csa_codigo" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
        <input type="hidden" name="cor_codigo" value="<%=TextHelper.forHtmlAttribute(cor_codigo)%>">
        </form>
      </div>
    <% } %>
  </div>
    <div class="btn-action">
     <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;">
       <hl:message key="rotulo.botao.cancelar"/>
     </a>
     <% if (tps_codigos != null && !tps_codigos.isEmpty()) { %>
       <a class="btn btn-primary" HREF="#no-back" onClick="habilitaRadios();f0.submit();return false;">
         <hl:message key="rotulo.botao.salvar"/>
       </a>
     <% } %>
    </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
  <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>