<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

int qtdColunas = (int) request.getAttribute("qtdColunas");
String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
List<?> lstTextos = (List<?>) request.getAttribute("lstTextos");
%>

<c:set var="title">
   <hl:message key="mensagem.listagem.texto.sistema.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
    <div class="row">
		<div class="col-sm-5 col-md-4">
      		<div class="card">
            	<div class="card-header hasIcon pl-3">
              		<h2 class="card-header-title"><hl:message key="rotulo.listagem.mensagem.pesquisar"/></h2>
            	</div>
            	<div class="card-body">
      				<form name="form1" method="post" action="../v3/manterTextoSistema?acao=listar&<%=SynchronizerToken.generateToken4URL(request)%>">
      					<div class="row">
                  			<div class="form-group col-sm">
                  				<label for="FILTRO"><hl:message key="rotulo.texto.filtrar.texto"/></label>
                  				<input type="text" id="FILTRO" name="FILTRO" class="EditMinusculo form-control" SIZE="10" VALUE="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);"  onBlur="fout(this);ValidaMascaraV4(this);">
                  			</div>
                  		</div>
                  		<div class="row">
                  			<div class="form-group col-sm">
                  				<label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
		                  		<select id="FILTRO_TIPO" name="FILTRO_TIPO" class="Select form-select form-control" onBlur="fout(this);ValidaMascaraV4(this);" nf="Filtrar" onChange="verificaCombo()">
		                    		<option value="1" <%=(String)((filtro_tipo == 1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
		                    		<option value="2" <%=(String)((filtro_tipo == 2) ? "SELECTED" : "")%>><hl:message key="rotulo.texto.sistema.chave"/></OPTION>
		                    		<option value="3" <%=(String)((filtro_tipo == 3) ? "SELECTED" : "")%>><hl:message key="rotulo.texto.sistema.texto"/></OPTION>
		                    		<option value="4" <%=(String)((filtro_tipo == 4) ? "SELECTED" : "")%>><hl:message key="rotulo.texto.sistema.data.alteracao.nao.nula"/></OPTION>
		                    		<option value="5" <%=(String)((filtro_tipo == 5) ? "SELECTED" : "")%>><hl:message key="rotulo.texto.sistema.data.alteracao.nula"/></OPTION>
		                  		</select>
		                  	</div>
                  		</div>
          			</form>
          		</div>
          	</div>
          	<div class="btn-action">
            	<a class="btn btn-primary" href="#no-back" onClick="filtrar();">
              		<svg width="20"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg> <hl:message key="rotulo.botao.pesquisar"/>
            	</a>
       		</div>
       		</div>
       		<div class="col-sm-7 col-md-8">
          		<div class="card">
            		<div class="card-header hasIcon pl-3">
              			<h2 class="card-header-title"><hl:message key="rotulo.texto.sistema.texto.plural"/></h2>
            		</div>
            		<div class="card-body table-responsive p-0">
              			<table class="table table-striped table-hover">
                			<thead>
                  				<tr>
                    				<th scope="col" width="30%"><hl:message key="rotulo.texto.sistema.chave"/></th>
            						<th scope="col" width="30%"><hl:message key="rotulo.texto.sistema.texto"/></th>
						            <th scope="col" width="10%"><hl:message key="rotulo.texto.sistema.data.alteracao"/></th>
						            <th scope="col" width="30%"><hl:message key="rotulo.acoes"/></th>
						        </tr>
						    </thead>
                			<tbody>
   							<%
       							CustomTransferObject textoSistema = null;
      							String texTexto = null;
      							String texDataAlteracao = null;
						      	String texChave = null;
						      	if (!lstTextos.isEmpty()) {
						          	Iterator<?> it = lstTextos.iterator();
						          	while (it.hasNext()) {
						              	textoSistema = (CustomTransferObject)it.next(); 
						              	texChave = (String) textoSistema.getAttribute(Columns.TEX_CHAVE);
						              	texTexto = (String) textoSistema.getAttribute(Columns.TEX_TEXTO);
						              	texDataAlteracao = DateHelper.toDateTimeString((Date) textoSistema.getAttribute(Columns.TEX_DATA_ALTERACAO));
						   	%>
           						<tr>
            						<td><%=TextHelper.forHtmlContent(texChave)%></td>
						            <td><%=TextHelper.forHtmlContent(texTexto)%></td>
						            <td><%=TextHelper.forHtmlContent(texDataAlteracao)%></td>
						            <td>
				                      	<div class="actions">
				                        	<div class="dropdown">
                          						<a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            						<div class="form-inline">
                              							<span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                                							<use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                              							</span> <hl:message key="rotulo.botao.opcoes"/>
                            						</div>
                          						</a>
                          						<div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                              						<a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterTextoSistema?acao=visualizar&texChave=<%=TextHelper.forJavaScript(texChave)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.visualizar"/></a>
                            						<a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterTextoSistema?acao=editar&texChave=<%=TextHelper.forJavaScript(texChave)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                          						</div>
				                        	</div>
				                      	</div>
				                    </td>
           						</tr>
       						<% 
       								}
   								} else {%>
						       	<tr>
						        	<td colspan="4"><hl:message key="mensagem.erro.nenhum.texto.sistema.encontrado"/></td>
						       	</tr>
   							<% } %>
   							</tbody>
   							<tfoot>
			                	<tr>
			                    	<td colspan="<%=qtdColunas%>"><%=ApplicationResourcesHelper.getMessage("rotulo.paginacao.listagem.textos.sistema", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
			                  	</tr>
			                </tfoot>
          				</table>
          			</div>
          			<div class="card-footer">
	              		<%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
	            	</div> 
          		</div>
          	</div>
	    </div>
	    <div class="btn-action">
	      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
	    </div>
</c:set>

<c:set var="javascript">
    <script type="text/JavaScript">
	    f0 = document.forms[0];
	
	    function filtrar() {
	       f0.submit();
	    }
	    
    	function verificaCombo() {
        	var selecao = document.form1.FILTRO_TIPO[document.form1.FILTRO_TIPO.selectedIndex].value;    
        	// Desabilita campo de filtro se a selecao for data ultima alteracao
        	if (selecao != 2 && selecao != 3) {
          		document.form1.FILTRO.disabled = true;
          		// Limpa campo de filtro
          		document.form1.FILTRO.value = "";
        	} else {
          		document.form1.FILTRO.disabled = false;
        	}
      	}

      	window.onload = verificaCombo;
    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>