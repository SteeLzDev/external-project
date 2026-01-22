<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  String titulo = (String) request.getAttribute("titulo");
  String codigo = (String) request.getAttribute("codigo");
  String usu_codigo = (String) request.getAttribute("usu_codigo");
  String usu_nome = (String) request.getAttribute("usu_nome");

  List<TransferObject> funcoes = (List<TransferObject>) request.getAttribute("funcoes");
  List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("servicos");
  HashMap<String, List<String>> bloqueios = (HashMap<String, List<String>>) request.getAttribute("bloqueios");

  boolean readOnly = (boolean) request.getAttribute("readOnly");
%>

<c:set var="title">
	<hl:message key="rotulo.bloquear.funcao.usuario.titulo" arg0="<%=TextHelper.forHtmlAttribute(titulo)%>"/>
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
	<div class="row">
		<div class="col-sm mb-2">
	    	<div class="float-end">
	      		<button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
	      		<div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
	      		<%
				if (funcoes.size() > 0 && servicos.size() > 0) {
	      			if (!readOnly) { %>
                	<a class="dropdown-item" href="#no-back" id="reativar" onClick="checkAll(f0, 'funcao_servico')"><hl:message key="rotulo.acoes.selecionar.todos"/></a>
	        		<a class="dropdown-item" href="#no-back" id="reativar" onClick="uncheckAll(f0, 'funcao_servico')"><hl:message key="rotulo.acoes.deselecionar.todos"/></a>
           		<%	}
	      		}	%>
	      		</div>
	    	</div>
		</div>
	</div>
	<div class="row justify-content-md-center">
		<div class="col-sm-12 form-check mt-2 form-group">
			<div class="card">
				<div class="card-header">
					<h2 class="card-header-title">
						<hl:message key="rotulo.edicao.bloqueio.funcao"/>
					</h2>
				</div>
				<div class="card-body">
					<div class="alert alert-warning" role="alert">
          				<hl:message key="mensagem.deselecionar.linha" />
       				</div>
					<form action="${linkEditarUsuario}?acao=editarBloqueioUsuarioFuncaoServico&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>"  method="POST" name="form1">
						<input type="HIDDEN" name="titulo" value="<%=TextHelper.encode64(titulo)%>">
						<input type="HIDDEN" name="USU_CODIGO" value="<%=TextHelper.forHtmlAttribute(usu_codigo)%>">
						<input type="HIDDEN" name="codigo" value="<%=TextHelper.forHtmlAttribute(codigo)%>">
					
						<%
						if (funcoes.size() > 0 && servicos.size() > 0) {
							boolean novaLinha = false;
							Iterator<TransferObject> itFuncao = funcoes.iterator();
						  	while (itFuncao.hasNext()) {
						  	  	novaLinha = !novaLinha;
						    	CustomTransferObject funcao = (CustomTransferObject) itFuncao.next();
    							String fun_descricao = funcao.getAttribute(Columns.FUN_DESCRICAO).toString();
    							String fun_codigo = funcao.getAttribute(Columns.FUN_CODIGO).toString();
								if(novaLinha) { 
						%>
						<div class="row">
							<%	}	%>
							
							<div class="form-group col-sm-6 mb-3">
								<label for="<%=TextHelper.forHtmlContent(fun_codigo)%>"><%=TextHelper.forHtmlContent(fun_descricao)%></label>
								<select class="form-control" id="<%=TextHelper.forHtmlContent(fun_codigo)%>" name="funcao_servico" multiple="multiple">
                    			
                    			<%
  								Iterator<TransferObject> itServico = servicos.iterator();
  								boolean linhaPar = true;
  								while (itServico.hasNext()) {
	    							CustomTransferObject servico = (CustomTransferObject) itServico.next();
	    							String svc_codigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
	    							String svc_descricao = servico.getAttribute(Columns.SVC_DESCRICAO).toString();
	    							boolean marcado = (bloqueios.get(fun_codigo) != null && ((List<String>) bloqueios.get(fun_codigo)).contains(svc_codigo));
								%>
                  					<option value="<%=TextHelper.forHtmlAttribute(fun_codigo + "_" + svc_codigo)%>" <%=TextHelper.forHtmlContent((marcado ? "SELECTED" : ""))%>><%=TextHelper.forHtmlContent(svc_descricao)%></option>
							<%	}	%>
							
  								</select>
  							</div>
  							<%	if(!novaLinha) {	%>
						</div>
								<% 	
								} 
  							} if(novaLinha) {	%>
  						</div>
						<%	}
  						}	%>
					</form>
				</div>
			</div>
		</div>
	</div>

	<div class="btn-action">
	<% if (!readOnly) { %>
		<a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="SalvarBloqueios(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    <% } else { %>
    	<a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;">><hl:message key="rotulo.botao.voltar"/></a>
    <% } %>
    </div>
</c:set>

<c:set var="javascript">
	<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
	<script type="text/JavaScript" src="../js/validaform.js"></script>
	<script type="text/JavaScript" src="../js/validacoes.js"></script>

	<script type="text/JavaScript">
		var f0 = document.forms[0];

		function checkAll(form) {
	  		for (i=0; i < form.elements.length; i++) {
	    		var e = form.elements[i];
	    		if ((e.type == 'select-multiple')) {
	    			for (j=0; ele = e.options[j]; j++)
	    				ele.selected = true;
	    		}
	  		}
		}
	
		function uncheckAll(form) {
			for (i=0; i < form.elements.length; i++) {
	    		var e = form.elements[i];
	    		if ((e.type == 'select-multiple')) {
	    			for (j=0; ele = e.options[j]; j++)
	    				ele.selected = false;
	    		}
	  		}
		}
	
		function SalvarBloqueios() {
	  		f0.submit();
		}
	
		function DisabledAll() {
	  		for (i=0; i < f0.elements.length; i++) {
	    		var e = f0.elements[i];
	    		if ((e.type == 'select-multiple')) {
	        		e.disabled = true;
	    		}
	  		}
		}
	
		<% if(readOnly){%>
		DisabledAll();
		<% } %>
	</script>
</c:set>

<t:page_v4>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>