<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.helper.margem.MargemHelper"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String readOnly = (String) request.getAttribute("readOnly");

String rseCodigo = (String) request.getAttribute("rseCodigo");
String rseMatricula = (String) request.getAttribute("rseMatricula");
String serNomeCodificado = (String) request.getAttribute("serNomeCodificado");
String serNome = (String) request.getAttribute("serNome");
String paginaAnterior = (String) request.getAttribute("paginaAnterior");

String transfTotal = (String) request.getAttribute("transfTotal");

List<String> margensOri = (List<String>) request.getAttribute("margensOri"); // Lista de margens que podem ser origem de transferencia
List<String> margensDes = (List<String>) request.getAttribute("margensDes"); // Lista de margens que podem ser destino de transferencia

RegistroServidorTO registroServidor = (RegistroServidorTO) request.getAttribute("registroServidor");

if (transfTotal == null || transfTotal.equals("")) {
	transfTotal = CodedValues.TPC_SIM;
} else if(transfTotal.equals("N")){
    transfTotal = CodedValues.TPC_NAO;
}

%>

<c:set var="title">
   <hl:message key="mensagem.servidor.transferencia.margem.titulo"/>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
	<div class="card">
		<div class="card-header hasIcon">
        	<span class="card-header-icon">
          		<svg width="24"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-servidor"></use></svg>
          	</span>
          	<h2 class="card-header-title"><%=TextHelper.forHtmlContent(rseMatricula)%> - <%=TextHelper.forHtmlContent(serNome)%></h2>
        </div>
        <div class="card-body">
			<form method="post" action="../v3/transferirMargemServidor?acao=editar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
				<% if (!readOnly.equals("true")) { %>
				<input name="flow"          type="hidden" value="endpoint">
	    		<input name="RSE_CODIGO"    type="hidden" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>">
	    		<input name="RSE_MATRICULA" type="hidden" value="<%=TextHelper.forHtmlAttribute(rseMatricula)%>">    
	    		<input name="SER_NOME"      type="hidden" value="<%=TextHelper.forHtmlAttribute(serNomeCodificado)%>">
	    		<% } 
				
				if (responsavel.isSer()) { %>
				<div class="row">
					<div class="form-group col-sm">
						<input class="form-check-input ml-1" type="checkbox" name="TERMO_ACEITE" id="TERMO_ACEITE" value="SIM"/><label class="form-check-label font-weight-bold"><hl:message key="mensagem.transferencia.margem.termoaceite"/></label>
					</div>
				</div>
	  			<% } %>
    			<div class="row">
              		<div class="form-group col-sm-6">
                		<label for="MAR_CODIGO_ORIGEM"><hl:message key="mensagem.servidor.transferencia.margem.origem"/></label>
						<select class="form-control" name="MAR_CODIGO_ORIGEM" id="MAR_CODIGO_ORIGEM" onChange="mudaMarRestOrigem();validarTransferencia();" <%=(String)( readOnly.equals("true")? "disabled" : "")%> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
						<%
	                	String selectedOrigem = JspHelper.verificaVarQryStr(request, "MAR_CODIGO_ORIGEM");    
	                    Iterator<String> it = margensOri.iterator();
                      	while (it.hasNext()) {
                        	Short marCodigo = Short.valueOf((String) it.next());
                        	String marDescricao = MargemHelper.getInstance().getMarDescricao(marCodigo, responsavel);
                        	if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO)) { %>
                            	<option value="<%=(Short)marCodigo%>" <%= (selectedOrigem.equals(marCodigo.toString())) ? "SELECTED" : "" %>><%=TextHelper.forHtmlContent(marDescricao)%></option>
                     	<% }
                      } %>
                    	</select>
            		</div>
	            	<div class="form-group col-sm-6">
	            		<label for="MAR_REST_ORIGEM"><hl:message key="mensagem.servidor.transferencia.margem.origem.saldo"/></label>
	            		<div class="input-group">
	                  		<div class="input-group-addon"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(CodedValues.TIPO_VLR_FIXO))%></div>
	            			<input type="text" class="form-control" id="MAR_REST_ORIGEM" name="MAR_REST_ORIGEM" disabled >
	            		</div>
	            	</div>
            	</div>
            
	            <div class="row">
	            	<div class="form-group col-sm-6 col-md-3">
	            		<span id="iTipoTransferencia"><hl:message key="mensagem.servidor.transferencia.margem.tipo"/></span>
	                  	<div class="form-check mt-2" role="radio-group" aria-labelledby="iTipoTransferencia">
	                  		<div class="row">
	                    		<div class="col-sm-4 text-nowrap">
			                  		<input name="TRANSF_TOTAL" id="TRANSF_TOTAL" class="form-check-input mt-1 ml-1" type="radio" value="S" <%=(String)( (transfTotal.equals(CodedValues.TPC_SIM)) ? "CHECKED" : "" )%> onClick="habilitarCampoValor('false');" <%=(String)( readOnly.equals("true")? "disabled" : "")%>/><label class="form-check-label labelSemNegrito ml-1 pr-4" for="TRANSF_TOTAL"><hl:message key="mensagem.servidor.transferencia.margem.tipo.total"/></label>
			                  	</div>
			                  	<div class="col-sm text-nowrap">
			                  		<input name="TRANSF_TOTAL" id="TRANSF_PARCIAL" class="form-check-input mt-1 ml-1" type="radio" value="N" <%=(String)( (transfTotal.equals(CodedValues.TPC_NAO)) ? "CHECKED" : "" )%> onClick="habilitarCampoValor('true');"  <%=(String)( readOnly.equals("true")? "disabled" : "")%>/><label class="form-check-label labelSemNegrito ml-1 pr-4" for="TRANSF_PARCIAL"><hl:message key="mensagem.servidor.transferencia.margem.tipo.parcial"/></label>
	                  			</div>
	                  		</div>
	                  	</div>
	            	</div>
	            	<div class="form-group col-sm-3">
	                	<label for="VALOR_TRANSF"><hl:message key="rotulo.valor.ser.transferido" /></label>
	            		<div class="input-group">
	                  		<div class="input-group-addon"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(CodedValues.TIPO_VLR_FIXO))%></div>
			            	<hl:htmlinput name="VALOR_TRANSF"
			                              type="text"
			                              classe="form-control"
			                              di="VALOR_TRANSF"
			                              size="15"
			                              mask="#F30"
			                              onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
			                              others="<%=TextHelper.forHtmlAttribute( (readOnly.equals("true")) ? "disabled" : "" )%>" />
	           			</div>
	           		</div>
	           	</div>
	           	<div class="row">
	            	<div class="form-group col-sm-6">
	            		<label for="MAR_CODIGO_DESTINO"><hl:message key="mensagem.servidor.transferencia.margem.destino"/></label>
	            		<select class="form-control" name="MAR_CODIGO_DESTINO" id="MAR_CODIGO_DESTINO" onChange="mudaMarRestDestino()" <%=(String)( readOnly.equals("true")? "disabled" : "")%> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
	                      <%
	                      String selectedDestino = JspHelper.verificaVarQryStr(request, "MAR_CODIGO_DESTINO");                      
	                      it = margensDes.iterator();
	                      while (it.hasNext()) {
	                        Short marCodigo = Short.valueOf((String) it.next());
	                        String marDescricao = MargemHelper.getInstance().getMarDescricao(marCodigo, responsavel);
	                        if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO)) { %>
	                            <option value="<%=(Short)marCodigo%>" <%= (selectedDestino.equals(marCodigo.toString())) ? "SELECTED" : "" %>><%=TextHelper.forHtmlContent(marDescricao)%></option>
	                     <% }
	                      } %>
	                    </select>
	               	</div>
	         		<div class="form-group col-sm-6">
	            		<label for="MAR_REST_DESTINO"><hl:message key="mensagem.servidor.transferencia.margem.destino.saldo"/></label>
	            		<div class="input-group">
	                  		<div class="input-group-addon"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(CodedValues.TIPO_VLR_FIXO))%></div>
	            			<input type="text" class="form-control" id="MAR_REST_DESTINO" name="MAR_REST_DESTINO" disabled>
	            		</div>
	            	</div>
	            </div>
			</form>
		</div>
	</div>
	<div class="btn-action">
		<% if (readOnly.equals("true")) { %>
		<a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(paginaAnterior)%>')"><hl:message key="rotulo.botao.voltar"/></a>
	  	<% } else { %>
	    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(paginaAnterior)%>')"><hl:message key="rotulo.botao.voltar"/></a>
	    <a class="btn btn-primary" href="#no-back" onClick="if(confirm('<%=(String)(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.servidor.transferencia.margem", responsavel))%>')){validaForm();} return false;"><hl:message key="rotulo.botao.concluir"/></a>
	  	<% } %>
	</div>
</c:set>

<c:set var="javascript">
	<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
	<script type="text/JavaScript" src="../js/xbdhtml.js"></script>
	<script type="text/JavaScript">
	
		var f0 = document.forms[0];
		
		function formLoad() {
		<% if (!readOnly.equals("true")) { %>
			f0.elements[0].focus();
		  	<% if (transfTotal == null || transfTotal.equals("") || transfTotal.equals(CodedValues.TPC_SIM)) {%>
		  		habilitarCampoValor('false');  
		  	<% } else if (transfTotal.equals(CodedValues.TPC_NAO)) { %>
		  		habilitarCampoValor('true');
		  	<% } %>
		<% } %>
			mudaMarRestOrigem();
		  	mudaMarRestDestino();
		}
		
		function habilitarCampoValor(exibir) {
		  var e = document.getElementById("VALOR_TRANSF");
		  var e1 = document.getElementById("MAR_REST_ORIGEM");
		  if (exibir == 'true') {
		    e.disabled = false;    
		    e.focus();
		  } else {
		    e.disabled = true;
		  } 
		  e.value = e1.value;
		}
		
		function mudaMarRestOrigem() {
		  	var codOrig = document.getElementById("MAR_CODIGO_ORIGEM").value;
		  	var e1 = document.getElementById("MAR_REST_ORIGEM");
		  	var e2 = document.getElementById("VALOR_TRANSF");
		    
		  	if (codOrig == <%=TextHelper.forJavaScriptBlock(CodedValues.INCIDE_MARGEM_SIM)%>) {
		    	e1.value = '<%=TextHelper.forHtmlContent( (registroServidor.getRseMargemRest() != null) ? NumberHelper.format(registroServidor.getRseMargemRest().doubleValue(), NumberHelper.getLang()) : "" )%>';
		    	
		  	} else if (codOrig == <%=TextHelper.forJavaScriptBlock(CodedValues.INCIDE_MARGEM_SIM_2)%>) {
				e1.value = '<%=TextHelper.forHtmlContent( (registroServidor.getRseMargemRest2() != null) ? NumberHelper.format(registroServidor.getRseMargemRest2().doubleValue(), NumberHelper.getLang()) : "" )%>';
		    
			} else if (codOrig == <%=TextHelper.forJavaScriptBlock(CodedValues.INCIDE_MARGEM_SIM_3)%>) {
			    e1.value = '<%=TextHelper.forHtmlContent( (registroServidor.getRseMargemRest3() != null) ? NumberHelper.format(registroServidor.getRseMargemRest3().doubleValue(), NumberHelper.getLang()) : "" )%>';
		  	}    
		  	e2.value = e1.value;

		  	if(document.getElementById('TRANSF_TOTAL').checked) {
		  		habilitarCampoValor('false');
		  	} else {
			  habilitarCampoValor('true');
			}
		}
		
		function validarTransferencia() {
		  f0.flow.value = 'start';
		  f0.submit();  
		}
		
		function mudaMarRestDestino() {
		  var codDest = document.getElementById("MAR_CODIGO_DESTINO").value;
		  var e1 = document.getElementById("MAR_REST_DESTINO");
		
		  if (codDest == <%=TextHelper.forJavaScriptBlock(CodedValues.INCIDE_MARGEM_SIM)%>) {
		    e1.value = '<%=TextHelper.forHtmlContent( (registroServidor.getRseMargemRest() != null) ? NumberHelper.format(registroServidor.getRseMargemRest().doubleValue(), NumberHelper.getLang()) : "" )%>';
		    
		  } else if (codDest == <%=TextHelper.forJavaScriptBlock(CodedValues.INCIDE_MARGEM_SIM_2)%>) {
		    e1.value = '<%=TextHelper.forHtmlContent( (registroServidor.getRseMargemRest2() != null) ? NumberHelper.format(registroServidor.getRseMargemRest2().doubleValue(), NumberHelper.getLang()) : "" )%>';
		    
		  } else if (codDest == <%=TextHelper.forJavaScriptBlock(CodedValues.INCIDE_MARGEM_SIM_3)%>) {
		    e1.value = '<%=TextHelper.forHtmlContent( (registroServidor.getRseMargemRest3() != null) ? NumberHelper.format(registroServidor.getRseMargemRest3().doubleValue(), NumberHelper.getLang()) : "" )%>';
		  }
		}
		
		function validaForm() {
		  var codOrig = document.getElementById("MAR_CODIGO_ORIGEM").value;
		  var codDest = document.getElementById("MAR_CODIGO_DESTINO").value;
		  var concorda = document.getElementById("TERMO_ACEITE");
		  var servidor = '<%=(boolean)(responsavel.isSer())%>';
		  
		  var restOrig = parse_num(document.getElementById("MAR_REST_ORIGEM").value.replace(",","."));
		  var restDest = parse_num(document.getElementById("MAR_REST_DESTINO").value.replace(",","."));
		  var valorTransf = parse_num(document.getElementById("VALOR_TRANSF").value.replace(",","."));
		
		  if (codOrig == codDest) {
		    alert('<hl:message key="mensagem.erro.servidor.transferencia.margem.iguais"/>');
		    return false;
		    
		  } else if (restOrig == '' || restDest == '') {  
		    alert('<hl:message key="mensagem.erro.servidor.transferencia.margem.saldo.indisponivel"/>');
		    return false;   
		    
		  } else if (restOrig < 0) {
		    alert('<hl:message key="mensagem.erro.servidor.transferencia.margem.negativa"/>');
		    return false;   
		     
		  } else if (valorTransf <= 0) {
		    alert('<hl:message key="mensagem.erro.servidor.transferencia.margem.valor.zero"/>');
		    return false;   
		
		  } else if (restOrig - valorTransf < 0) {
		    alert('<hl:message key="mensagem.erro.servidor.transferencia.margem.valor.maior.que.saldo"/>');
		    return false;   
		  
		  } else if (servidor == 'true' && (concorda == null || !concorda.checked)) {
		    alert('<hl:message key="mensagem.informe.servidor.transferencia.margem.termo"/>');
		    return false;
		
		  } else {
		    f0.submit();
		  }
		}
		
		window.onload = formLoad;

	</script>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>