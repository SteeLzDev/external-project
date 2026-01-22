<%--
* <p>Title: visualizarPoliticaPrivacidade_v4</p>
* <p>Description: Página de visualização da política de privacidade.</p>
* <p>Copyright: Copyright (c) 2002-2019</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: ricardo.magno $
* $Revision:  $
* $Date:  $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="java.util.Date"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="title">
   <%= request.getAttribute("tituloPagina") %>
</c:set>
<c:set var="bodyContent">
	<div id="header-print">
		<% if ("v4".equals(versaoLeiaute)) { %>
			<img src="../img/econsig-logo.svg">
		<% } else { %>
			<img src="../img/logo_sistema_v5.png">
		<%} %>
		<p id="date-time-print"></p>
	</div>
   <%
      String msg = (String) request.getAttribute("msg");
      Date dataPoliticaPrivacidade = (Date) request.getAttribute("dataPoliticaPrivacidade");
      Date dataUltimaAceitacao = (Date) request.getAttribute("dataUltimaAceitacao");
      String dataUltimaAtualizacaoSistema = (String) request.getAttribute("dataUltimaAtualizacaoSistema");
      boolean aceiteValido = (boolean) request.getAttribute("aceiteValido");
   %>
   <div class="row d-print-none">
		<div class="col-sm-12 col-md-12 mb-2">
			<div class="float-end">
              <div class="btn-action">
				<button id="btnImprime" type="button" class="float-end d-print-none btn btn-primary mt-0 mb-2" onClick="imprimir()">
				  <hl:message key="rotulo.botao.imprimir" />
				</button>             
              </div>
			</div>
		</div>
   </div>
   <form method="post" action="../v3/aceitarPoliticaPrivacidade" name="form1">
        <input type="hidden" name="acao" value="aceitar" />
        <%=SynchronizerToken.generateHtmlToken(request)%>
		<div class="card firefox-print-fix">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.informacoes.legais.sobre.sistema"/></h2>           
          </div>
          <div class="card-body">
            <div class="about p-0">
              <%if (dataPoliticaPrivacidade != null) {%>
              	  <p class="mb-5 mt-3">
                  	<hl:message key="rotulo.politica.privacidade.data"/>:<span class="font-weight-bold"> <%=DateHelper.toDateString(dataPoliticaPrivacidade)%></span>
                  </p>
                  <p class="mb-5 mt-3"></p>
              <%}%>
              <p><%out.print(msg);%></p>
              <%if (dataUltimaAceitacao != null) {%>
                  <p class="mt-3">
                    <hl:message key="rotulo.politica.privacidade.data.aceitacao" arg0="<%=responsavel.getUsuLogin()%>"/>: <span class="font-weight-bold"><%=DateHelper.toDateTimeString(dataUltimaAceitacao)%></span></p>
              <%} %>
              <p class="version">
                <hl:message key="mensagem.sobre.versao.sistema.argdata" arg0="<%=dataUltimaAtualizacaoSistema%>"/>
              </p>
            </div>
          </div>
        </div>
        <div class="btn-action mt-3">
          <%if (aceiteValido || dataPoliticaPrivacidade == null) {%> 
            <%if (!JspHelper.verificaVarQryStr(request,"BTN_voltar").equals("false")){%>
              <a id="btnVoltar" class="btn btn-outline-danger" href="#no-back"><hl:message key="rotulo.botao.voltar"/></a>
            <%}%>
          <%} else {%>
              <a class="btn btn-outline-danger" href="#sairModal" data-bs-toggle="modal"><hl:message key="rotulo.botao.cancelar"/></a>
              <a id="btnConfirmar" class="btn btn-primary" href="#no-back"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/></a>
          <%} %>
        </div>
   </form>
   <% if ("v4".equals(versaoLeiaute)) { %>
     <div id="footer-print">
   	<img src="../img/footer-logo.png">
     </div>
   <% } else { %>
   	<div id="footer-print">
   		<img src="../img/footer-logo-v5.png">
   	</div>
   <%} %>
</c:set>
<c:set var="javascript">
	<style>
		@media print {
		  *{
		  	margin: 0;
		  	padding: 0;
		  	color: #000 !important;
		  	font-size: 11px;
		  }
		  #menuAcessibilidade {display: none;}
		  #header-print img{width: 10%;}
		  #footer-print {position: absolute; bottom: 0;}
		}
		@page{margin: 0.5cm;}
	</style>
<SCRIPT language="javascript" id="MainScript">
  function imprimir() {
    $("#BOTOES").css({"visibility":"hidden"});
    $("#container").css({"visibility":"hidden"});
    document.body.className = "PRINT";
    injectDate();
    window.print();
    document.body.className = "";
    $("#BOTOES").css({"visibility":"visible"});
    $("#container").css({"visibility":"visible"});
  }
</SCRIPT>
<script>
	function injectDate(){
		const dateTimePrint = document.querySelector('#date-time-print');
		const printDate = new Date();
		printDate.toLocaleString("pt-br");
		dateTimePrint.innerHTML = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);	
	}
</script>
<script type="text/JavaScript">
	f0 = document.forms[0];
	
	$(function () {
		$('#btnConfirmar').bind('click', function (){
			if(confirm('<hl:message key="mensagem.confirma.aceitacao.politica.privacidade"/>')){f0.submit();}
		});
		$('#btnVoltar').bind('click', function (){
			postData('../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true');
		});
	});
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>