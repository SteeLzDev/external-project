<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, java.io.*, java.math.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String vrrCodigo = (String) request.getAttribute("VRR_CODIGO");
List<TransferObject> listaContratosRetencao = (List<TransferObject>) request.getAttribute("listaContratosRetencao");
BigDecimal vrrValor = (BigDecimal) request.getAttribute("vrrValor");
%>

<c:set var="title">
   <hl:message key="rotulo.editar.verba.rescisoria.titulo"/>
</c:set>

<c:set var="imageHeader">
   <use xlink:href="#i-rescisao"></use>
</c:set>

<c:set var="bodyContent">
<div class="col-sm">
<form method="post" action="../v3/editarVerbaRescisoria?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.editar.verba.rescisoria.subtitulo"/></h2>
    </div> 
    <div class="card-body">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><hl:message key="mensagem.informacao.editar.verba.rescisoria.valor"/></p>
      </div>
      <div class="row">
        <div class="form-group col-sm-12 col-md-4">
          <label for="vrrValor"><hl:message key="rotulo.editar.verba.rescisoria.valor"/></label>
          <hl:htmlinput 
            name="VRR_VALOR"
            di="VRR_VALOR" 
            type="text" 
            classe="form-control"
            value="<%=TextHelper.forHtmlAttribute((vrrValor != null) ? NumberHelper.format(vrrValor.doubleValue(), NumberHelper.getLang(), 2, 8): "")%>"
            size="8"
            maxlength="10"
            onChange='calcularPrevia();'
            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.editar.verba.rescisoria.valor", responsavel)%>"
            onFocus="SetarEventoMascaraV4(this,'#F10',true);" 
            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" 
          />
        </div>
      </div> 
    </div>
   </div>
   <hl:htmlinput name="VRR_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(vrrCodigo)%>"/>
   <hl:htmlinput name="_skip_history_" type="hidden" value="true"/>
   <div class="btn-action">
     <a class="btn btn-outline-danger" href="#no-back" onClick="<%="postData('" + TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)) + "'); return false;"%>"><hl:message key="rotulo.botao.cancelar"/></a>
     <a class="btn btn-primary" href="#no-back" onClick="javascript:confirmar();"><hl:message key="rotulo.botao.salvar"/></a>
   </div>
   
   <% if (listaContratosRetencao != null) { %>
   <div class="card">
     <div class="card-header">
       <h2 class="card-header-title"><hl:message key="rotulo.editar.verba.rescisoria.resultado.pesquisa"/></h2>
     </div>
     <div class="card-body table-responsive p-0">
       <table class="table table-striped table-hover">
         <thead>
           <tr>
             <th scope="col"><hl:message key="rotulo.matricula.singular"/></th>
             <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
             <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
             <th scope="col"><hl:message key="rotulo.editar.verba.rescisoria.saldo.devedor"/></th>
             <th scope="col"><hl:message key="rotulo.editar.verba.rescisoria.valor.retencao"/></th>
           </tr>
         </thead>
         <tbody>
            <%=JspHelper.msgRstVazio(listaContratosRetencao.size() == 0, 7, responsavel)%>
            <%
            Iterator<TransferObject> it = listaContratosRetencao.iterator();
            while (it.hasNext()) {
              CustomTransferObject contrato = (CustomTransferObject) it.next();
              String rseMatricula = contrato.getAttribute(Columns.RSE_MATRICULA).toString();
              String adeNumero = contrato.getAttribute(Columns.ADE_NUMERO).toString();
              String csaNome = (String) contrato.getAttribute(Columns.CSA_NOME);
              // Valor do saldo devedor
              BigDecimal sdvValor = new BigDecimal(0.00);
              String strSdvValor = ApplicationResourcesHelper.getMessage("rotulo.editar.verba.rescisoria.nao.informado", responsavel);
              if (!TextHelper.isNull(contrato.getAttribute(Columns.SDV_VALOR))) {
                  sdvValor = ((BigDecimal) contrato.getAttribute(Columns.SDV_VALOR));
		              strSdvValor = NumberHelper.format(sdvValor.doubleValue(), NumberHelper.getLang());
              }
              // Valor a ser pago no contrato
              BigDecimal retencaoValor = new BigDecimal(0.00);
              String strRetencaoValor = NumberHelper.format(retencaoValor.doubleValue(), NumberHelper.getLang());
            %>
            <tr>
              <td><%=TextHelper.forHtmlContent(rseMatricula)%></td>
              <td><%=TextHelper.forHtmlContent(adeNumero)%></td>
              <td><%=TextHelper.forHtmlContent(csaNome)%></td>
              <td><%=TextHelper.forHtmlContent(strSdvValor)%></td>
              <td>
                <span id='VALOR_<%=adeNumero%>'><%=TextHelper.forHtmlContent(strRetencaoValor)%></span>
              </td>
            </tr>
            <% } %>
         </tbody>
       </table>
     </div>
   </div>
   <% } %>       
</form>
</div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    f0 = document.forms[0];
  </script>
  <script type="text/JavaScript">
  function formLoad() {
    focusFirstField();
  }

  function calcularPrevia() {
	var vrrValor = document.getElementById('VRR_VALOR').value;
    $.ajax({
  	  type: 'post',
	    url: '../v3/calcularPreviaRescisao?vrrCodigo=<%=vrrCodigo%>&vrrValor='+vrrValor,
        async : true,
        contentType : 'application/json',
        success : function(data) {
  	      var valor = null;
  	      var dados = JSON.parse(JSON.stringify(data));
          dados.forEach(function(objeto) {
   	        numero = objeto.atributos['<%=Columns.ADE_NUMERO%>'];
   	      	valor = objeto.atributos['VALOR_PREVIA_PAGTO'];
          	document.getElementById('VALOR_' + numero).innerText = FormataContabil(parse_num(valor),2);
   	      })
        }
    });
  }
  
  function confirmar() {
	var vrrValor = document.getElementById('VRR_VALOR').value;
	var link = '../v3/editarVerbaRescisoria?acao=confirmar&vrrCodigo=<%=vrrCodigo%>&vrrValor=' + vrrValor + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>';
	if (confirm('<hl:message key="mensagem.confirmacao.editar.verba.rescisoria"/>')) {
	  postData(link);
	}
	return false;
  }
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>