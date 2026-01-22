<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
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
  
  String svcCodigo = (String) request.getAttribute("svc_codigo");
  String svcIdentificador = (String) request.getAttribute("svc_identificador");
  String svcDescricao = (String) request.getAttribute("svc_descricao");
  String rseCodigo = (String) request.getAttribute("rseCodigo");
  String bloqIncAdeMesma = (String) request.getAttribute("bloqIncAdeMesma");
  boolean isDisabled = (boolean) request.getAttribute("isDisabled");
  boolean bloqIncAdeMesmaCse = (boolean) request.getAttribute("bloqIncAdeMesmaCse");

  Map<String, Boolean> parametrosSvcSobrepoe = (Map<String, Boolean>) request.getAttribute("parametrosSvcSobrepoe");
  
  String voltar = (String) request.getAttribute("voltar");
%>

<c:set var="javascript">
<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
<script type="text/JavaScript" src="../js/validaform.js"></script>
<script type="text/JavaScript" src="../js/validacoes.js"></script>
<script type="text/JavaScript" src="../js/xbdhtml.js"></script>
<script type="text/JavaScript" src="../js/validaemail.js"></script>
<script type="text/JavaScript">
var f0 = document.forms['form1'];

function validaRadiosPadrao(checkbox, cse, nome) {
    var f0 = document.forms[0];
    for (i=0; i < f0.elements.length; i++) {
      var e = f0.elements[i];
      if (e.name == nome){
          if ((!checkbox.checked) && (e.type == 'radio')) {
           e.disabled = false;
           } else {
             e.disabled = true;
             if(((cse == "S") || (cse == 'true')) && (e.value == "1")){
               e.checked = true;
             }
             if(((cse == "N") || (cse == 'false')) && (e.value == "0")){
               e.checked = true;
             }
            }
      }
    }   
   }

window.onload = formLoad;
</script>
</c:set>
<c:set var="title">
<hl:message key="rotulo.manutencao.servicos.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title"><%=TextHelper.forHtmlContent(svcIdentificador)%> - <%=TextHelper.forHtmlContent(svcDescricao)%></h2>
      </div>
    </div>
    <div class="card-body">
      <form method="post" action="../v3/manterServico?acao=salvarServicoSobrepoe&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">           
      <% if (parametrosSvcSobrepoe.containsKey(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)) { %>
        <div class="row">  
          <div class="form-group col-sm-12 col-md-12" aria-labelledby="bloqueiacontratoprdrejeitada">     
             <span id="bloqueiacontratoprdrejeitada"><hl:message key="rotulo.param.svc.bloqueia.novo.contrato.prd.rejeitada.mesma.natureza"/></span>
             <div class="form-check">
              <% if (bloqIncAdeMesma.isEmpty()) { %>
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(bloqIncAdeMesmaCse)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>');" checked> 
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label> 
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>_SIM" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="1" <%=(String)(bloqIncAdeMesmaCse ? "checked" : "")%> (check_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>.checked) ? DISABLED : ""> 
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>_SIM"><hl:message key="rotulo.sim"/></label>  
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>_NAO" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="0" <%=(String)(!bloqIncAdeMesmaCse ? "checked" : "")%> (check_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>.checked) ? DISABLED : ""> 
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>_NAO"><hl:message key="rotulo.nao"/></label>                   
              <% } else { %>
                    <INPUT NAME="check_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(bloqIncAdeMesmaCse)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>');"> 
                    <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label> 
                    <INPUT NAME="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>_SIM" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="1" <%=(String)(bloqIncAdeMesma.equals("1") ? "checked" : "")%> > 
                    <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>_SIM"><hl:message key="rotulo.sim"/></label>  
                    <INPUT NAME="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>_NAO" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="0" <%=(String)(bloqIncAdeMesma.equals("0") ? "checked" : "")%> > 
                    <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)%>_NAO"><hl:message key="rotulo.nao"/></label>                   
            <% } %>
            </div>
         </div>                  
        </div>
      <% } %>

      <input name="svc" type="hidden" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>">
      <input type="hidden" name="svc" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>">
      <input type="hidden" name="svcIdentificador" value="<%=TextHelper.forHtmlAttribute(svcIdentificador)%>">
      <input type="hidden" name="svcDescricao" value="<%=TextHelper.forHtmlAttribute(svcDescricao)%>">
      <input type="hidden" name="rseCodigo" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>">
      </form>
    </div>
  </div>
    <div class="btn-action">
     <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=voltar%>'); return false;">
      <hl:message key="rotulo.botao.cancelar"/>
     </a> 
     <a class="btn btn-primary" HREF="#no-back" onClick="f0.submit(); return false;">
      <hl:message key="rotulo.botao.salvar"/>
     </a>
    </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
  <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>