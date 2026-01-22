<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t"      tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"     uri="/html-lib" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String rseCodigo = (String) request.getAttribute("rseCodigo");
String rseMatricula = (String) request.getAttribute("rseMatricula");
String serNomeCodificado = (String) request.getAttribute("serNomeCodificado");
String serNome = (String) request.getAttribute("serNome");

String link = (String) request.getAttribute("link");

List contracheques = (List) request.getAttribute("contracheques");
String periodo = (String) request.getAttribute("periodo");

String texto = (String) request.getAttribute("texto");
String nomeMes = (String) request.getAttribute("nomeMes");
String anterior = (String) request.getAttribute("anterior");
String proximo = (String) request.getAttribute("proximo");

List periodosDisponiveis = (List) request.getAttribute("periodosDisponiveis");
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="title">
  <hl:message key="rotulo.servidor.listar.contracheque.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
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
  <div class="row">
    <div class="col-sm-12 col-md-12">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#" onClick="imprimir();"><hl:message key="rotulo.botao.imprimir"/></a>
        </div>
      </div>
    </div>
  </div>
  <div class="card">
    <div class="card-header">
      <div class="row">
        <div class="col-sm-7 float-left pt-3">
          <h2 class="card-header-title"><%=TextHelper.forHtmlContent(rseMatricula)%> - <%=TextHelper.forHtmlContent(serNome)%></h2>
        </div>
        <div class="col-sm-5 float-end">
          <div id="periodo-contracheque" class="row">
           <div class="form-group text-right col-md-4 mb-0 pt-3">
             <label class="label-for-white" for="periodoCcq"><hl:message key="rotulo.servidor.contracheque.periodo"/></label>
           </div>
           <div class="form-group col-md-8 mb-0 pl-0">
             <select class="form-control form-select" id="periodoCcq" name="periodoCcq" onChange="postData('<%=TextHelper.forJavaScriptAttribute( link )%>&PERIODO='+this.value);">
               <option value="" selected><hl:message key="rotulo.servidor.contracheque.selecione.periodo"/></option>
               <%
                 if (periodosDisponiveis != null && periodosDisponiveis.size() > 0) {
                  Iterator it = periodosDisponiveis.iterator();
                  CustomTransferObject periodoTO = null;
                  String periodoId, periodoNome;
                  String selected = (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "PERIODO")) ? JspHelper.verificaVarQryStr(request, "PERIODO") : periodo.toString());
                  
                  while (it.hasNext()) {
                    periodoTO = (CustomTransferObject)it.next();
                    periodoId = DateHelper.format((Date) periodoTO.getAttribute(Columns.CCQ_PERIODO), "yyyy-MM");
                    periodoNome = DateHelper.getMonthName((Date) periodoTO.getAttribute(Columns.CCQ_PERIODO)) + "/" + DateHelper.getYear((Date) periodoTO.getAttribute(Columns.CCQ_PERIODO));
                    
                    if (DateHelper.getMonth((Date) periodoTO.getAttribute(Columns.CCQ_PERIODO)) == 12) {
                    	periodoNome+= " - " + ApplicationResourcesHelper.getMessage("rotulo.servidor.contracheque.periodo.decimo.terceiro", responsavel);
                    }
                %>
                <option value="<%=TextHelper.forHtmlAttribute(periodoId)%>" <%= (selected.equals(periodoId)) ? "SELECTED" : "" %>><%=TextHelper.forHtmlContent(periodoNome)%></option>
               <% }
                }  
               %>
             </select>
           </div>
         </div>
       </div>
     </div>
     <div id="periodo-contracheque-print" class="row" style="display: none">
       <p><b><hl:message key="rotulo.servidor.contracheque.periodo"/>:</b> <%=TextHelper.forHtmlContent(nomeMes)%></p>
     </div>
   </div>
   <div class="card-body text-center">
     <pre><%=TextHelper.forHtmlContent( texto )%></pre>
   </div>
 </div>
 <div class="btn-action">
   <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute( responsavel.isSer() ? "../v3/carregarPrincipal" : SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request) )%>')">
     <hl:message key="rotulo.botao.voltar"/>
   </a>
 </div>
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
		  }
		  #periodo-contracheque{display: none;}
          #periodo-contracheque-print{display: inline!important;}
		  #menuAcessibilidade {display: none;}
		  #header-print img{width: 10%;}
		  #footer-print {position: absolute; bottom: 0;}
		}
		@page{margin: 0.3cm;}
	</style>
	<script>
		function injectDate(){
			const dateTimePrint = document.querySelector('#date-time-print');
			const printDate = new Date();
			printDate.toLocaleString("pt-br");
			dateTimePrint.innerHTML = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);	
		}
	</script>
  <script type="text/JavaScript">
    function imprimir() {
      $("#CABECA").css({"visibility":"hidden"});
      $("#BOTOES").css({"visibility":"hidden"});
      $("#container").css({"visibility":"hidden"});
      document.body.className = "PRINT";
      injectDate();
      window.print();
      document.body.className = "fundo";
      $("#CABECA").css({"visibility":"visible"});
      $("#BOTOES").css({"visibility":"visible"});
      $("#container").css({"visibility":"visible"});
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>