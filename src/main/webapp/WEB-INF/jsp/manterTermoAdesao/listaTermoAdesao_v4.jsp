<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="title">
    <%=request.getAttribute("tituloPagina")%>
</c:set>
<c:set var="bodyContent">

    <%
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String msg = "";
        List<?> termosAdesao = (List<?>) request.getAttribute("termosAdesao");
		String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
    %>

    <FORM NAME="form1" METHOD="post" ACTION="../v3/informarTermoAdesao?acao=visualizar&tadCodigo=" class="was-validated">
        <%
            Integer i = 0;
            Iterator<?> it = termosAdesao.iterator();
            while (it.hasNext()) {
                TransferObject termoAdesao = (TransferObject) it.next();

                String tadCodigo = (String) termoAdesao.getAttribute(Columns.TAD_CODIGO);
                String tadTitulo = (String) termoAdesao.getAttribute(Columns.TAD_TITULO);
                msg = DateHelper.format((Date) termoAdesao.getAttribute(Columns.LTU_DATA), "dd-MM-yyyy");
        %>
        <div class="card min-h-100 w-75">
            <div class="card-body  d-flex justify-content-between align-items-center">
                <div class="title">
                    <h6 class="mx-4"><strong><%=TextHelper.forHtmlContent(tadTitulo)%></strong></h6>
                </div>
                <div class="card-text">
                    <% if (msg != null && !msg.equals("")) {%>
                    <hl:message key="rotulo.ultima.atualizacao" />
                    <%=msg%>
                    <%}%>
                    <a class="btn btn-outline-primary py-1 p-3 m-2" href="#no-back" onClick="validaForm(<%=TextHelper.forHtmlAttribute(tadCodigo)%>); return false;"> <hl:message key="rotulo.ler.termo" /></a>
                </div>
            </div>
        </div>
        <%i++;%>
        <%}%>
        <div class="btn-action">
            <a id="btnVoltar" class="btn btn-outline-danger" href="#no-back"><hl:message key="rotulo.botao.voltar"/></a>
        </div>
    </FORM>
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
    <script type="text/JavaScript">
        f0 = document.forms[0];
        $(function () {
            $('#btnVoltar').bind('click', function (){
                postData('../v3/carregarPrincipal?listarmostraMensagem=true&limitaMsg=true');
            });
        });

        function validaForm(tadCodigo) {
            document.form1.action += tadCodigo;
            f0.submit();
        }
    </script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>