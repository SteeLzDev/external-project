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

boolean penalidade = (Boolean) request.getAttribute("penalidade");
boolean bloqueado = (Boolean) request.getAttribute("bloqueado");
boolean exigeMotivoOperacaoBloquearDesbloquear = (Boolean) request.getAttribute("exigeMotivoOperacaoBloquearDesbloquear");
boolean exigeMotivoOperacaoPenalizar = (Boolean) request.getAttribute("exigeMotivoOperacaoPenalizar");

String param = (String) request.getAttribute("param");
String csaNome = (String) request.getAttribute("csaNome");
String linkVoltar = (String) request.getAttribute("linkVoltar");
List<TransferObject> tiposPenalidade = (List<TransferObject>) request.getAttribute("tiposPenalidade");
List<TransferObject> tiposMotivoOperacao = (List<TransferObject>) request.getAttribute("tiposMotivoOperacao");

%>
<c:set var="title">
   <%=(penalidade) ? ApplicationResourcesHelper.getMessage("rotulo.penalidade.consignataria.titulo", responsavel) : (!bloqueado) ? ApplicationResourcesHelper.getMessage("rotulo.bloquear.consignataria.titulo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.desbloquear.consignataria.titulo", responsavel)%>
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
              <form method="post" action="../v3/manterConsignataria?<%=TextHelper.forHtmlAttribute(param)%>&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
              
                <% if (!penalidade && !bloqueado) { %>
                  <div class="row">
                    <div class="form-group col-sm">
                      <label for="dataDesbloqueioAutomatica">
                        <hl:message key="rotulo.consignataria.data.desbloqueio.automatico" />
                      </label>
                      <hl:htmlinput name="dataDesbloqueioAutomatica" di="dataDesbloqueioAutomatica" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"/>              
                    </div>
                  </div>
                <% } %>

                <% if ((exigeMotivoOperacaoBloquearDesbloquear && !penalidade) || (exigeMotivoOperacaoPenalizar && penalidade)) { %>
              	 <% if (tiposMotivoOperacao != null && !tiposMotivoOperacao.isEmpty()) { %>	
                     <div class="row">                    
                        <div class="form-group col-sm">
                          <label for="tmoCodigo"><hl:message key="rotulo.motivo.singular"/></label>
                          <select class="form-control" id="tmoCodigo" name="tmoCodigo">
                            <option value=""><hl:message key="rotulo.campo.selecione"/>	</option>
                            <%for (TransferObject tipoMotivoTO: tiposMotivoOperacao) { %>
                                <option value="<%=(String) tipoMotivoTO.getAttribute(Columns.TMO_CODIGO)%>"><%=(String) tipoMotivoTO.getAttribute(Columns.TMO_DESCRICAO)%></option>                      
                            <%} %>
                          </select>
                        </div>
                     </div>
                   <% } %>
                 <% } %>

                 <div class="row">
                  <div class="form-group col-sm">
                    <label for="OCC_OBS"><hl:message key="rotulo.efetiva.acao.consignacao.dados.observacao"/></label>
                    <textarea class="form-control" 
                              placeholder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs", responsavel)%>'
                              id="OCC_OBS" 
                              name="OCC_OBS" 
                              rows="6"></textarea>
                  </div>
                </div>

                <% if (tiposPenalidade != null && !tiposPenalidade.isEmpty() && (!bloqueado || penalidade)) { %>
                     <div class="row">                    
                        <div class="form-group col-sm">
                          <label for="tpeCodigo"><hl:message key="rotulo.penalidade.consignataria.titulo"/></label>
                          <select class="form-control form-select" id="tpeCodigo" name="tpeCodigo">
                            <option value=""><hl:message key="rotulo.campo.selecione"/></option>
                            <%for (TransferObject tipoPenalidadeTO: tiposPenalidade) { %>
                                <option value="<%=(String) tipoPenalidadeTO.getAttribute(Columns.TPE_CODIGO)%>"><%=(String) tipoPenalidadeTO.getAttribute(Columns.TPE_DESCRICAO)%></option>                      
                            <%} %>
                          </select>
                        </div>
                     </div>
                <% } %>
                <input type="hidden" name="codigo" value="<%=TextHelper.forHtmlAttribute(request.getParameter("codigo"))%>">
                <input type="hidden" name="link_voltar" value="<%=TextHelper.forHtmlAttribute(linkVoltar)%>">
                <%if (penalidade) {%>
                   <input type="hidden" name="penalidade" value="true">
                <%} else {%>
                   <input type="hidden" name="status" value="<%=TextHelper.forHtmlAttribute(request.getParameter("status"))%>">
                <%}%>                 
              </form>
           </div>
        </div>
     </div>
  </div>
  <div class="btn-action">
     <a class="btn btn-outline-danger" HREF="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkVoltar)%>')"><hl:message key="rotulo.botao.voltar"/></a> <a class="btn btn-primary" HREF="#" onClick="if(confirmar()){f0.submit();} return false;" ><hl:message key="rotulo.botao.salvar"/></a>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
  var f0 = document.forms[0];

  function formLoad() {
  }

  function confirmar() {
  <%if (penalidade) {%>
    var controles = new Array("tmoCodigo", "OCC_OBS", "tpeCodigo");
    var msgs = new Array ('<hl:message key="rotulo.consignante.informe.motivo.operacao"/>', '<hl:message key="mensagem.informe.observacao"/>', '<hl:message key="mensagem.informe.uma.penalidade.para.consignataria"/>');
  <%} else {%>
    var controles = new Array("tmoCodigo", "OCC_OBS");
    var msgs = new Array ('<hl:message key="rotulo.consignante.informe.motivo.operacao"/>', '<hl:message key="mensagem.informe.observacao"/>');
  <%}%>
    if (ValidaCampos(controles, msgs)) {
  <%if (!bloqueado) {%>
          <%if (penalidade) {%>
              return true;
          <%} else {%>
              var msg = '<hl:message key="mensagem.confirmacao.bloqueio.csa"/>';
              if (validaDataDesblqAut() && confirm(msg)) {
                return true;
              } else {
                return false;
              }
          <%}%>
  <%} else {%>
      <%if (penalidade) {%>
          return true;
      <%} else {%>
          var msg = '<hl:message key="mensagem.confirmacao.desbloqueio.csa"/>';
          if (validaDataDesblqAut() && confirm(msg)) {
            return true;
          } else {
            return false;
         }
    <%}%>
  <%}%>
   }

    return false;
  }
  
  function validaDataDesblqAut() {
      if (f0.dataDesbloqueioAutomatica != null && f0.dataDesbloqueioAutomatica.value != '') {
          if (!verificaData(f0.dataDesbloqueioAutomatica.value)) {
              f0.dataDesbloqueioAutomatica.focus();
              return false;
          }
          var partesData = obtemPartesData(f0.dataDesbloqueioAutomatica.value);
          var dia = partesData[0];
          var mes = partesData[1];
          var ano = partesData[2];
          var dataReativacaoAut = new Date(ano, mes - 1, dia);
          dataReativacaoAut.setHours(0,0,0,0);
          var dataCorrente = new Date();
          dataCorrente.setHours(0,0,0,0);
          if (dataReativacaoAut.getTime() <= dataCorrente.getTime()) {
              f0.dataDesbloqueioAutomatica.focus();
              alert('<hl:message key="mensagem.erro.bloqueio.csa.data.desbloq.aut.maior.hoje"/>');
              return false;
          }
          return confirm('<hl:message key="mensagem.aviso.bloqueio.csa.data.desbloq.aut.previsao"/>');
      }
      return true;
  }
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>