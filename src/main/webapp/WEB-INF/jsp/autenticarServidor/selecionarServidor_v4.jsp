<%--
* <p>Title: selecionarServidor_v4.jsp</p>
* <p>Description: seleção de servidor</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String loginExterno = (String) session.getAttribute("serLogin");
List<TransferObject> servidores = (List) session.getAttribute("servidores");
%>
<c:set var="bodyContent">
        <%
        TransferObject servidor = null;
        String serNome, serCpf, rseMatricula, orgNome, rseCodigo, orgIdentificador, estIdentificador, serStatus;
        Iterator<TransferObject> it = servidores.iterator();
        int i = 0;
        int rowCount = 0;
        while (it.hasNext()) {
          servidor = it.next();
          serNome  = (String)servidor.getAttribute(Columns.SER_NOME);
          
          serCpf   = (String)servidor.getAttribute(Columns.SER_CPF);
          rseMatricula = (String)servidor.getAttribute(Columns.RSE_MATRICULA);
          orgNome   = (String)servidor.getAttribute(Columns.ORG_NOME);
          rseCodigo = (String)servidor.getAttribute(Columns.RSE_CODIGO);
          orgIdentificador = (String)servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
          estIdentificador = (String)servidor.getAttribute(Columns.EST_IDENTIFICADOR);
          serStatus = (String)servidor.getAttribute(Columns.SRS_DESCRICAO);
        %>
		<div class="row">
			<div class="card">
				<div class="card-header">
					<h2 class="card-header-title"><hl:message key="rotulo.servidor.matricula"/>&nbsp;<%=TextHelper.forHtmlContent(rseMatricula)%></h2>
				</div>
				<div class="card-body">
					<dl class="row data-list">
						<dt class="col-4"><hl:message key="rotulo.servidor.nome"/></dt>
						<dd class="col-8"><%=TextHelper.forHtmlContent(serNome)%></dd>
						<dt class="col-4"><hl:message key="rotulo.servidor.cpf"/></dt>
						<dd class="col-8"><%=TextHelper.forHtmlContent(serCpf)%></dd>
						<dt class="col-4"><hl:message key="rotulo.servidor.status"/></dt>
						<dd class="col-8"><%=TextHelper.forHtmlContent(serStatus)%></dd>
						<dt class="col-4"><hl:message key="rotulo.orgao.singular"/></dt>
						<dd class="col-8"><%=TextHelper.forHtmlContent(orgNome + " - " + orgIdentificador)%></dd>
						<dt class="col-4"><hl:message key="rotulo.estabelecimento.abreviado"/></dt>
						<dd class="col-8"><%=TextHelper.forHtmlContent(estIdentificador)%></dd>
					</dl>								
				    <button class="btn float-end btn-primary" type="button" onClick="doIt('s', '<%=(int)i++%>'); return false;" aria-label="<hl:message key="mensagem.selecionar.servidor.clique.aqui"/>"><svg width="17"><use xlink:href="#i-avancar"></use></svg><hl:message key="rotulo.botao.selecionar"/></button>
				</div>
			</div>
		</div>
      <%
      }
      %>     
      <div class="btn-action">
         <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" id="btnVoltar"><hl:message key="rotulo.botao.cancelar"/></a>
      </div>
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript">
function doIt(opt, rse)
{
 var msg = '', j;
 if (opt == 's') {
    j = '../v3/autenticar?acao=autenticar&username=<%=TextHelper.forJavaScriptBlock(loginExterno)%>&rseOpt=' + rse;
  } else {
    return false;
 }

 if (msg != '') {
   ConfirmaUrl(msg, j);
 } else {
   postData(j);
 }
}
</script>
</c:set>
<t:empty_v4>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>
