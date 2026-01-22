<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> autdesList = (List<TransferObject>) request.getAttribute("lstConsignacao");

Boolean exigeMotivoOperacao = FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_CONF_RESERVA, responsavel);

// Busca atributos quanto a exigencia de Tipo de motivo da operacao
if (!ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, CodedValues.TPC_SIM, responsavel) || !exigeMotivoOperacao) {
    exigeMotivoOperacao = false;
}

Boolean exibirAnexo = (Boolean) request.getAttribute("exibirAnexo");
Boolean anexoObrigatorio = (Boolean) request.getAttribute("anexoObrigatorio");

%>
<c:set var="javascript">
  <%if (exibirAnexo) { %>
    <hl:fileUploadV4 obrigatorio="<%=anexoObrigatorio ? true : false %>" multiplo="true" tipoArquivo="anexo_consignacao" scriptOnly="true"/>
  <%} %>
  <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.reserva", responsavel)%>" scriptOnly="true"/>
  <script type="text/JavaScript">
    var f0 = document.forms[0];

    function formLoad(){
        focusFirstField();
    }

    function vf_confirmar_reserva() {
        var exigeMotivo = <%=exigeMotivoOperacao%>;
        var exibirAnexo = <%=exibirAnexo%>;
        var anexoObrigatorio = <%=anexoObrigatorio%>;
        
        var motivo = f0.TMO_CODIGO;
        if (motivo != null && motivo.disabled == false && (exigeMotivo)) {
            if (motivo.value == '') {
                alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
                motivo.focus();
                return false;
            }
        }

        if (!confirmaAcaoConsignacao()) {
            return false;
        }

        if (exibirAnexo && anexoObrigatorio) {
        	var arquivos = f0.FILE1.value;
        	if (arquivos == null || arquivos == undefined || arquivos == '') {
                alert('<hl:message key="mensagem.erro.confirmar.anexo.obrigatorio"/>');
        	    return false;
        	}
        }
        
        return ('<hl:message key="mensagem.confirmacao.reserva"/>');
    }   
  </script> 
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
  <%=TextHelper.forHtml(request.getAttribute("tituloPagina"))%>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="<%=TextHelper.forHtmlAttribute(SynchronizerToken.updateTokenInURL((String) request.getAttribute("urlDestino"), request))%>" name="formTmo" ENCTYPE='multipart/form-data'>
    <hl:htmlinput name="anexoObrigatorio"              type="hidden" value="<%=TextHelper.forHtmlAttribute(anexoObrigatorio.toString())%>" />
    <div class="row">
      <!--Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE -->
      <%pageContext.setAttribute("autdes", autdesList);%>
      <hl:detalharADEv4 name="autdes" table="false" type="alterar" />
      <!--Fim dos dados da ADE -->
    </div>
    <div class="col-sm p-0">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <hl:message key="rotulo.efetiva.acao.consignacao.dados.operacao" />
          </h2>
        </div>
        <div class="card-body">              
          <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
          <% if (exigeMotivoOperacao) { %>
            <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.reserva", responsavel)%>"/>
          <%} %>
             
          <%-- Fim dos dados do Motivo da Operação --%>
          <% if (exibirAnexo) { %>
            <div class="row">
                  <div class="col-sm-12 col-md-6">
            <hl:fileUploadV4 obrigatorio="<%=anexoObrigatorio ? true : false %>" multiplo="true" tipoArquivo="anexo_consignacao"/>  
            </div>
                </div>       
                       
          <% } %>

        </div>
      </div>
      

    </div>

    <% for (int i = 0; i < autdesList.size(); i++) { %>
      <hl:htmlinput name="chkConfirmar" type="hidden" value="<%=TextHelper.forHtmlAttribute(autdesList.get(i).getAttribute(Columns.ADE_CODIGO))%>" />
    <% } %>

    <div class="btn-action"> 
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" id="btnEnvia" href="#no-back" onClick="if(vf_confirmar_reserva()){f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a> 
    </div>
  </form>
</c:set>
<t:page_v4>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>