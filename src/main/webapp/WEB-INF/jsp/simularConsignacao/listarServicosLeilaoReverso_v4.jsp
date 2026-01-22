<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List servicosReserva = (List) request.getAttribute("servicosReserva");
boolean origem = (Boolean) request.getAttribute("origem");
%>

<c:set var="imageHeader">
   <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   <%= request.getAttribute("tituloPagina") %>
</c:set>
<c:set var="bodyContent">
	<div class="card">
		<div class="card-header hasIcon pl-3">
			<h2 class="card-header-title"><hl:message key="rotulo.solicitar.servico"/></h2>
		</div>
		<div class="card-body table-responsive">
			<table class="table table-striped table-hover">
				<thead>
					<tr>
						<th scope="col"><hl:message key="rotulo.servico.descricao"/></th>
						<th scope="col"><hl:message key="rotulo.acoes"/></th>
					</tr>
				</thead>
				<tbody>
					<%=JspHelper.msgRstVazio(servicosReserva.size()==0, "13", "lp")%>
                    <%                          
                    Iterator it = servicosReserva.iterator();
                    CustomTransferObject next = null;
                    String link = null;
                    String label = null;
                    while (it.hasNext()) {
                      next = (CustomTransferObject) it.next();
                      link = SynchronizerToken.updateTokenInURL(next.getAttribute("link").toString(), request) + "&origem="+origem;
                      label = next.getAttribute("label").toString().toUpperCase();
                      %>
					<tr>
						<td><%=TextHelper.forHtmlContent(label)%></td>
						<td><a href="javascript:void(0);" onClick="postData('<%=TextHelper.forJavaScriptAttribute(link)%>'); return false;"><hl:message key="rotulo.acoes.selecionar"/></a></td>
					</tr>
              		<% } %>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="3"><hl:message key="rotulo.simulacao.lista.servico.disponiveis.rodape"/></td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
	<div class="btn-action">
		<a class="btn btn-outline-danger" href="javascript:void(0);" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
	</div>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>