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
    
List<?> servidores = (List<?>) session.getAttribute("servidores");
%>
<c:set var="title">
   <%=TextHelper.forHtml(JspHelper.getNomeSistema(responsavel))%>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="26">
          <use xlink:href="../img/sprite.svg#i-servidor"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.alterar.registro.funcional.titulo"/></h2>
        </div>
        <div class="card-body table-responsive ">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.servidor.nome"/></th>
                <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
                <th scope="col"><hl:message key="rotulo.servidor.status"/></th>
                <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
                <th scope="col"><hl:message key="rotulo.orgao.singular"/></th>
                <th scope="col"><hl:message key="rotulo.estabelecimento.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
              <%=JspHelper.msgRstVazio(servidores == null || servidores.isEmpty(), 7, responsavel)%>
              <%
                if (servidores != null && !servidores.isEmpty()) {
                  TransferObject servidor = null;
                  String serNome, serCpf, serCodigo, rseMatricula, orgNome, rseCodigo, orgIdentificador, orgCodigo, estIdentificador, estCodigo, codigoOrgao, orgao, serStatus, usuCodigo;
                  Iterator<?> it = servidores.iterator();
                  while (it.hasNext()) {
                    servidor = (CustomTransferObject) it.next();
                    usuCodigo = (String) servidor.getAttribute(Columns.USU_CODIGO);
                    serNome = (String) servidor.getAttribute(Columns.SER_NOME);
                    serCpf = (String) servidor.getAttribute(Columns.SER_CPF);
                    serCodigo = (String) servidor.getAttribute(Columns.SER_CODIGO);
                    rseMatricula = (String) servidor.getAttribute(Columns.RSE_MATRICULA);
                    orgNome = (String) servidor.getAttribute(Columns.ORG_NOME);
                    rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
                    serStatus = (String) servidor.getAttribute(Columns.SRS_DESCRICAO);
                    orgIdentificador = (String) servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
                    orgCodigo = (String) servidor.getAttribute(Columns.ORG_CODIGO);
                    estIdentificador = (String) servidor.getAttribute(Columns.EST_IDENTIFICADOR);
                    estCodigo = (String) servidor.getAttribute(Columns.EST_CODIGO);
                  %>
                  <tr>
                    <td ><%=TextHelper.forHtmlContent(serNome)%></td>
                    <td><%=TextHelper.forHtmlContent(serCpf)%></td>
                    <td><%=TextHelper.forHtmlContent(serStatus)%></td>
                    <td><%=TextHelper.forHtmlContent(rseMatricula)%></td>
                    <td><%=TextHelper.forHtmlContent(orgNome + " - " + orgIdentificador)%></td>
                    <td><%=TextHelper.forHtmlContent(estIdentificador)%></td>
                    <%
                        if (!rseCodigo.equals(responsavel.getRseCodigo())) {
                    %>
                      <td><a href="#no-back" onClick="doIt('s','ser_codigo_sel=<%=TextHelper.forJavaScript(serCodigo)%>&username_sel=<%=TextHelper.forJavaScript(rseMatricula)%>&org_codigo_sel=<%=TextHelper.forJavaScript(orgCodigo)%>&org_id_sel=<%=TextHelper.forJavaScript(orgIdentificador)%>&est_codigo_sel=<%=TextHelper.forJavaScript(estCodigo)%>&est_id_sel=<%=TextHelper.forJavaScript(estIdentificador)%>&ser_cpf_sel=<%=TextHelper.forJavaScript(serCpf)%>&rse_matricula_sel=<%=TextHelper.forJavaScript(rseMatricula)%>');" aria-label="<hl:message key="mensagem.selecionar.servidor.clique.aqui"/>"><hl:message key="rotulo.botao.selecionar"/></a></td>
                    <%
                        } else {
                    %>
                      <td><hl:message key="rotulo.alterar.login.servidor.selecionado"/></td>
                    <%       
                        }
                    %>
                  </tr>
                    <%
                    }
                }
              %>
          </tbody>
        </table>
      </div>
    </div>
    <div class="btn-action">
       <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  function doIt(opt, compl) {
	  var msg = '', j;
	  if (opt == 's') {
	    j = '../v3/autenticar?acao=alterarLoginSer&' + compl + '&' + <%="'" + SynchronizerToken.generateToken4URL(request) + "'"%>;
	    } else {
	      return false;
	  }
	 
	  if (msg != '') {
	      ConfirmaUrl(msg, j);
	  } else {   
	      postData(j,"_parent");
	  }
	}
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>