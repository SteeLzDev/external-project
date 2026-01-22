<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.periodo.PeriodoHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.margem.MargemDisponivel" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
boolean isIe = (request.getHeader("user-agent").indexOf("MSIE") > 0);

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

//Verifica os contratos selecionados para renegociação
String[] chkAde = (String[]) request.getAttribute("chkAde");
String svcCodigo = (String) request.getAttribute("svcCodigo");
String svcDescricao = (String) request.getAttribute("svcDescricao");
String svcIdentificador = (String) request.getAttribute("svcIdentificador");
String csaIdentificador = (String) request.getAttribute("csaIdentificador");
String csaNome = (String) request.getAttribute("csaNome");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String rseCodigo = (String) request.getAttribute("rseCodigo");
CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");
// Parâmetros de sistema
boolean permiteSimularSemMargem = (Boolean) request.getAttribute("permiteSimularSemMargem");
// Parâmetros de serviço
boolean alteraAdeVlr = (Boolean) request.getAttribute("alteraAdeVlr"); // Habilita ou nao campo de valor da reserva, campo ja vem preenchido
String adeVlrPadrao = (String) request.getAttribute("adeVlrPadrao"); // Valor da prestação fixo para o serviço
// Margem
MargemDisponivel margemDisponivel = (MargemDisponivel) request.getAttribute("margemDisponivel");
ExibeMargem exibeMargem = (ExibeMargem) request.getAttribute("exibeMargem");
boolean podeMostrarMargem = (Boolean) request.getAttribute("podeMostrarMargem");
BigDecimal rseMargemRest = (BigDecimal) request.getAttribute("rseMargemRest");
List<CustomTransferObject> autdesList = (List<CustomTransferObject>) request.getAttribute("autdesList");
String margemConsignavel = (String) request.getAttribute("margemConsignavel");
String adePeriodicidade = (String) request.getAttribute("adePeriodicidade");
boolean permiteEscolherPeriodicidade = (Boolean) request.getAttribute("permiteEscolherPeriodicidade");
boolean exigeCaptcha = false;
boolean exibeCaptchaAvancado = false;
boolean exibeCaptchaDeficiente = false;
if(responsavel.isSer()) {
        exigeCaptcha = request.getAttribute("exigeCaptcha") != null && (boolean) request.getAttribute("exigeCaptcha");
        exibeCaptchaAvancado = request.getAttribute("exibeCaptchaAvancado") != null && (boolean) request.getAttribute("exibeCaptchaAvancado");
        exibeCaptchaDeficiente = request.getAttribute("exibeCaptchaDeficiente") != null && (boolean) request.getAttribute("exibeCaptchaDeficiente");
}

// Dados para simulação
String ade_vlr = (String) request.getAttribute("adeVlr");
String vlr_liberado = (String) request.getAttribute("vlrLiberado");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.simulacao.renegociacao.para.titulo" arg0="<%=TextHelper.forHtmlAttribute(csaNome.toUpperCase())%>"/>
</c:set>
<c:set var="bodyContent">
<form NAME="form1" METHOD="post" ACTION="../v3/simularRenegociacao?acao=visualizarRanking&<%=SynchronizerToken.generateToken4URL(request)%>">
  <input type="hidden" name="flow" value="endpoint">
        <% pageContext.setAttribute("autdes", autdesList); %>
        <hl:detalharADEv4 name="autdes" table="false" type="simular_renegociacao" />
          
          <% if (!responsavel.isSer()) { %>
          <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
            <% pageContext.setAttribute("servidor", servidor); %>
            <hl:detalharServidorv4 name="servidor"/>
          <%-- Fim dos dados da ADE --%>
          <% } %>
          <div class="col-sm">
            <div class ="row">
            <% if (podeMostrarMargem) { %>
              <div class="col-sm-4">
                <div class="card">
                  <div class="card-header hasIcon">
                    <span class="card-header-icon"><svg width="30"><use xlink:href="#i-menu-margem"></use></svg></span>
                    <h2 class="card-header-title"><hl:message key="rotulo.simulacao.margem.consignavel"/></h2>
                      <% if(exigeCaptcha){ %>
                      <span class="card-header-icon-ocultar-margem-ser">
                    <a href="#" onclick="exibirmargem()" id="olhoMargemOculto">
                        <svg  width="30" height="30" class="icon-oculta-margem-simu">
                            <use xlink:href="#i-eye-slash-regular"></use>
                        </svg>
                    </a>
                    </span>
                      <% } %>
                  </div>
                  <div class="card-body">
                      <% if (!exigeCaptcha){ %>
                      <span class="margem-disponivel"><%=TextHelper.forHtmlAttribute(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()))%> <strong><%=(String)(exibeMargem.isSemRestricao() || rseMargemRest.doubleValue() > 0 ? NumberHelper.reformat(margemConsignavel, "en", NumberHelper.getLang()) : "0,00")%></strong></span>
                    <hl:htmlinput name="rseMargemRest" di="rseMargemRest" value="<%=TextHelper.forHtmlAttribute(exibeMargem.isSemRestricao() || rseMargemRest.doubleValue() > 0  ? margemConsignavel : "0.00")%>" type="hidden" />
                      <% } else { %>
                      <div class="card-body">
                          <dl class="row data-list firefox-print-fix">
                              <dt class="col-6"><hl:message key="rotulo.reservar.margem.disponivel"/>:</dt>
                              <dd class="col-6"><hl:message key="rotulo.margem.moeda"/><hl:message key="rotulo.margem.disponivel.codigo"/></dd>
                          </dl>
                      </div>
                      <hl:modalCaptchaSer type="<%=TextHelper.forHtmlAttribute("renegociacao")%>"/>
                      <% } %>
                  </div>
                </div>
              </div>
            <% } %>  
              
              
              <div class="col-sm">
                <div class="card">
                  <div class="card-header hasIcon">
                    <span class="card-header-icon"><svg width="25"><use xlink:href="#i-simular"></use></svg></span>
                    <h2 class="card-header-title"><hl:message key="rotulo.simulacao.simule.sua.consignacao"/></h2>
                  </div>
                  <div class="card-body">
                    <div class="alert alert-warning" role="alert">
                      <p class="mb-0"><hl:message key="rotulo.simulacao.defina.tipo.simulacao"/></p>
                    </div>
                    <div class="row">
                      <div class="form-group col-sm-6">
                        <label for="VLR_LIBERADO"><hl:message key="rotulo.consignacao.valor.parcela.moeda"/></label>
                        <div class="input-group">
                          <div class="input-group-addon"><%=TextHelper.forHtmlAttribute(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()))%></div>
                          <hl:htmlinput name="ADE_VLR"
                                      type="text"
                                      classe="form-control"
                                      di="ADE_VLR"
                                      size="15"
                                      value="<%=TextHelper.forHtmlAttribute( !ade_vlr.equals("") ? NumberHelper.reformat(ade_vlr, "en", NumberHelper.getLang()) : (!adeVlrPadrao.equals("") ? adeVlrPadrao : "") )%>"
                                      mask="#F30"
                                      onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                      others="<%=TextHelper.forHtmlAttribute( alteraAdeVlr ? "" : "disabled" )%>"
                          />
                        </div>
                      </div>
                      <div class="form-group col-sm-6">
                        <label for="VLR_LIBERADO"><hl:message key="rotulo.consignacao.valor.solicitado.moeda"/></label>
                        <div class="input-group">
                          <div class="input-group-addon"><%=TextHelper.forHtmlAttribute(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()))%></div>
                          <hl:htmlinput name="VLR_LIBERADO"
                                      type="text"
                                      classe="form-control"
                                      di="VLR_LIBERADO"
                                      size="15"
                                      value="<%=TextHelper.forHtmlAttribute(vlr_liberado.equals("") ? "" : NumberHelper.reformat(vlr_liberado, "en", NumberHelper.getLang()))%>"
                                      mask="#F30"
                                      onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                      others="<%=TextHelper.forHtmlAttribute( alteraAdeVlr ? "" : "disabled" )%>"
                           />
                        </div>
                    </div>
                 </div>
                 <% if (permiteEscolherPeriodicidade && !adePeriodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_MENSAL)) { %>
                 <div class="row">
                    <div class="form-group col-sm-6">
                      <label for="periodicidade"><hl:message key="rotulo.consignacao.periodicidade"/></label>
                        <select class="form-control select" 
                          onFocus="SetarEventoMascara(this,'#*200',true);"
                          onBlur="fout(this);ValidaMascara(this);"
                          name="adePeriodicidade" id="adePeriodicidade">
                          
                          <option value="<%=(String)CodedValues.PERIODICIDADE_FOLHA_QUINZENAL%>" selected><hl:message key="rotulo.consignacao.periodicidade.quinzenal"/></option>
                          <option value="<%=(String)CodedValues.PERIODICIDADE_FOLHA_MENSAL%>"><hl:message key="rotulo.consignacao.periodicidade.mensal"/></option>
                        </select>
                    </div>
                  </div>
                  <% } else { %>
                    <hl:htmlinput type="hidden" name="adePeriodicidade" di="adePeriodicidade" value="<%=TextHelper.forHtmlAttribute(PeriodoHelper.getPeriodicidadeFolha(responsavel))%>" />
                  <% } %>
               </div>
             </div>
           </div>
         </div>      
        <input type="hidden" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>">
        <input type="hidden" name="SVC_CODIGO" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>">
        <input type="hidden" name="CSA_CODIGO" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
        <input type="hidden" name="CSA_NOME" value="<%=TextHelper.forHtmlAttribute(csaNome)%>">
        <input type="hidden" name="SVC_DESCRICAO" value="<%=TextHelper.forHtmlAttribute(svcDescricao)%>">
        <input type="hidden" name="SVC_IDENTIFICADOR" value="<%=TextHelper.forHtmlAttribute(svcIdentificador)%>">
        <input type="hidden" name="CSA_IDENTIFICADOR" value="<%=TextHelper.forHtmlAttribute(csaIdentificador)%>">
        <%for (int i = 0; i < chkAde.length; i++) {  %>
           <input type="hidden" name="chkADE" value="<%=TextHelper.forHtmlAttribute(chkAde[i])%>">
        <%}  %>          
        
        <div class="btn-action">
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(paramSession.getLastHistory())%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
          <a class="btn btn-primary" id="btnConfirmar" href="#" onClick="if(vf_simulacao()){f0.submit();} return false;"><svg width="17"><use xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.simulacao.acao.simular"/></a>
        </div>
</form>
</c:set>
<c:set var="javascript">
<% if (exibeCaptchaAvancado) { %>
<script src='https://www.google.com/recaptcha/api.js'></script>
<% } %>
<script language="JavaScript" type="text/JavaScript">
  f0 = document.forms[0];
  function vf_simulacao() {
  
    if ((f0.ADE_VLR.value == '') && (f0.VLR_LIBERADO.value == '')) {
      alert ('<hl:message key="mensagem.informe.simulacao.valor.prestacao.valor.solicitado"/>');
      f0.ADE_VLR.focus();
      return false;
    } else if ((f0.ADE_VLR.value != '') && (f0.VLR_LIBERADO.value != '')) {
      alert ('<hl:message key="mensagem.erro.simulacao.somente.um.valor.prestacao.valor.solicitado"/>');
      f0.ADE_VLR.focus();
      return false;
    } else {
      if (f0.ADE_VLR.value != '') {
        var adeVlr = parseFloat(parse_num(f0.ADE_VLR.value));
        if (adeVlr <= 0) {
          alert('<hl:message key="mensagem.erro.simulacao.valor.prestacao"/>');
          if (f0.ADE_VLR != null && !f0.ADE_VLR.disabled) {
            f0.ADE_VLR.focus();
          }
          return false;
        }
        <% if (!permiteSimularSemMargem) { %>
        if (f0.rseMargemRest != null) {
          if(f0.rseMargemRest.value - adeVlr < 0) {
            alert('<hl:message key="mensagem.erro.simulacao.valor.prestacao.maior.margem"/>');
            if (f0.ADE_VLR != null && !f0.ADE_VLR.disabled) {
              f0.ADE_VLR.focus();
            }
            return false;
          }
        }
        <% } %>
      } else if (f0.VLR_LIBERADO.value != '') {
        var vlrLiberado = parseFloat(parse_num(f0.VLR_LIBERADO.value));
        if (vlrLiberado <= 0) {
          alert('<hl:message key="mensagem.erro.simulacao.valor.liberado"/>');
          if (f0.VLR_LIBERADO != null)
            f0.VLR_LIBERADO.focus();
          return false;
        }
      }
      
      enableAll();
      return true;
    }
  }

  function exibirmargem(){
      <% if (exibeCaptchaDeficiente) { %>
      montaCaptchaSomSer('renegociacao');
      <% } %>
      $('#modalCaptcha_renegociacao').modal('show');
  }

  function formLoad() {
    focusFirstField();
  }
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
