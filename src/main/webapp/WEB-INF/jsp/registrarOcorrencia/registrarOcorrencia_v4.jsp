<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<CustomTransferObject> autdesList = (List<CustomTransferObject>) request.getAttribute("autdesList");
boolean isMotivoLeilao = (Boolean) request.getAttribute("isMotivoLeilao");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>  
</c:set>
<c:set var="title">
    <hl:message key="rotulo.registrar.ocorrencia.consignacao.titulo"/>
</c:set>
<c:set var="bodyContent">
    <form action="../v3/registrarOcorrenciaConsignacao" method="post" name="form1">
      <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
      <hl:htmlinput name="acao" type="hidden" value="<%= isMotivoLeilao ? "finalizarRegistroLeilao" : "finalizarRegistro" %>" />
      <% if (autdesList != null && autdesList.size() > 0) { 
             for (CustomTransferObject ade : autdesList) { %>
                 <input type="hidden" name="chkOcorrencia" value="<%=TextHelper.forHtmlAttribute(ade.getAttribute(Columns.ADE_CODIGO))%>">
      <%     }
         } 
      %>
      <div class="row">
          <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
          <hl:detalharADEv4 name="autdesList" table="true" type="alterar" scope="request"/>
          <%-- Fim dos dados da ADE --%>
          
      </div>
      <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.efetiva.acao.consignacao.dados.operacao"/></h2>
          </div>
          <div class="card-body">
            <div class="row">  
              <div class="form-group col-md-12 col-sm-6">
                <label for="tocCodigo"><hl:message key="rotulo.registrar.ocorrencia.consignacao.tipo"/></label>
                <hl:htmlcombo
                       listName="tipoOcorrencia" 
                       name="tocCodigo" 
                       fieldValue="<%=Columns.TOC_CODIGO%>" 
                       fieldLabel="<%=Columns.TOC_DESCRICAO%>" 
                       notSelectedLabel="<%= ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) %>"
                       selectedValue="<%=isMotivoLeilao ? CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO : CodedValues.TOC_INFORMACAO%>"
                       classe="form-control"
                       />
               </div>
             </div>
             <div class="row">  
              <div class="form-group col-md-12 col-sm-6">
                <label for="ocaObs"><hl:message key="rotulo.registrar.ocorrencia.consignacao.observacao"/></label>
                <textarea id="ocaObs" name="ocaObs" rows="6" wrap="VIRTUAL" class="form-control" placeHolder="<hl:message key='mensagem.placeholder.digite.obs'/>" onFocus="SetarEventoMascara(this,'#*65000',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>
              </div>
          </div>
        </div>
     </div>
  </form>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" data-bs-dismiss="modal" href="#" id="btnEnvia" onClick="if(vf_insere_ocorrencia()){f0.submit();} return false;">
          <svg width="17">
              <use xlink:href="#i-confirmar"></use>
          </svg>
          <hl:message key="rotulo.botao.confirmar"/>
        </a>
      </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
window.onload = formLoad;

f0 = document.forms[0];

function formLoad() {
  focusFirstField();
}

function vf_insere_ocorrencia() {
  if (f0.tocCodigo != null && f0.tocCodigo.options[f0.tocCodigo.selectedIndex].value == '') {  
    alert('<hl:message key="mensagem.informe.oca.tipo.ocorrencia"/>');
    f0.tocCodigo.focus();
    return false;
  }
  if (f0.ocaObs != null && trim(f0.ocaObs.value).replace(/[\r\n\s]/g, '') == '') {
    alert('<hl:message key="mensagem.informe.oca.observacao"/>');
    f0.ocaObs.focus();
    return false;
  }

  return confirm('<hl:message key="mensagem.confirmacao.registrar.ocorrencia"/>');
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
