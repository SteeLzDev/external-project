<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean bloqueado = (Boolean) request.getAttribute("bloqueado");
String linkVoltar = (String) request.getAttribute("linkVoltar");
String corCodigo = (String) request.getAttribute("corCodigo");
List<TransferObject> tiposMotivoOperacao = (List<TransferObject>) request.getAttribute("tiposMotivoOperacao");
%>
<c:set var="title">
   <%=(!bloqueado) ? ApplicationResourcesHelper.getMessage("rotulo.bloquear.correspondente.titulo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.desbloquear.correspondente.titulo", responsavel)%>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
     <div class="col-sm">
        <div class="card">
           <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.motivo.operacao.singular"/></h2>
           </div>
           <div class="card-body">
              <form method="post" action="../v3/manterCorrespondente?acao=salvarMotivoBloqueioDesbloqueio&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
              
            	 <% if (tiposMotivoOperacao != null && !tiposMotivoOperacao.isEmpty()) { %>	
                   <div class="row">                    
                      <div class="form-group col-sm">
                        <label for="tmoCodigo"><hl:message key="rotulo.motivo.singular"/></label>
                        <select class="form-control" id="tmoCodigo" name="tmoCodigo">
                          <option value=""><hl:message key="rotulo.campo.selecione"/>	</option>
                          <%for (TransferObject tipoMotivoTO: tiposMotivoOperacao) { %>
                              <option value="<%=(String) tipoMotivoTO.getAttribute(Columns.TMO_CODIGO)+';'+(String) tipoMotivoTO.getAttribute(Columns.TMO_EXIGE_OBS)%>"><%=(String) tipoMotivoTO.getAttribute(Columns.TMO_DESCRICAO)%></option>                      
                          <%} %>
                        </select>
                      </div>
                   </div>
                 <% } %>

                 <div class="row">
                  <div class="form-group col-sm">
                    <label for="OCR_OBS"><hl:message key="rotulo.efetiva.acao.consignacao.dados.observacao"/></label>
                    <textarea class="form-control" 
                              placeholder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs", responsavel)%>'
                              id="OCR_OBS" 
                              name="OCR_OBS" 
                              rows="6"></textarea>
                  </div>
                </div>

                <input type="hidden" name="cor_codigo" value="<%=corCodigo%>">
                <input type="hidden" name="link_voltar" value="<%=TextHelper.forHtmlAttribute(linkVoltar)%>">
                <input type="hidden" name="status" value="<%=TextHelper.forHtmlAttribute(request.getAttribute("status"))%>">
              </form>
           </div>
        </div>
     </div>
  </div>
  <div class="btn-action">
     <a class="btn btn-outline-danger" HREF="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkVoltar)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a> 
     <a class="btn btn-primary" HREF="#" onClick="if(confirmar()){f0.submit();} return false;" ><hl:message key="rotulo.botao.salvar"/></a>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
  var f0 = document.forms[0];

  function formLoad() {
  }

  function confirmar() {
      var textObs = document.getElementById('OCR_OBS');
      var tipoMotivo = document.getElementById('tmoCodigo');
      
      if(tipoMotivo.value == null || tipoMotivo.value == 'undefined' || tipoMotivo.value == ''){
          alert('<hl:message key="rotulo.consignante.informe.motivo.operacao"/>');
          return false;
      }

      var msgs;
      <%if(!bloqueado) {%> 
          msgs = '<hl:message key="mensagem.confirmacao.bloqueio.cor"/>';
      <%} else{%>
          msgs = '<hl:message key="mensagem.confirmacao.desbloqueio.cor"/>';
      <%}%>
      
	  var tipoMotivoSplit = tipoMotivo.value.split(";");
      var exigeObs = tipoMotivoSplit[1];

      if(exigeObs == "S" && (textObs.value == null || textObs.value == 'undefined' || textObs.value.trim() == '')){
    	  alert('<hl:message key="mensagem.informe.observacao"/>');
          return false;
      }

      if (confirm(msgs)) {
        return true;
      } else {
        return false;
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