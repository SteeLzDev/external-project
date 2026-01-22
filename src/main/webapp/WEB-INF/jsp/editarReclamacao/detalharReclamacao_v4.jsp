<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

CustomTransferObject rrs = (CustomTransferObject) request.getAttribute("rrs");
List<TransferObject> rrsMotivos = (List<TransferObject>) request.getAttribute("rrsMotivos");
%>

<c:set var="title">
  <hl:message key="rotulo.reclamacao.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <div class="page-title">
    <div class="row d-print-none">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <a class="btn btn-primary" href="#no-back" onClick="imprime();">Imprimir</a>
        </div>
      </div>
    </div>
  </div>
  <div class="row firefox-print-fix">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">Dados gerais</h2>
        </div>
        <div class="card-body">
          <dl class="row data-list firefox-print-fix">
            <dt class="col-6"><hl:message key="rotulo.servidor.singular"/>:</dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(rrs.getAttribute(Columns.SER_NOME))%></dd>
            <dt class="col-6"><hl:message key="rotulo.servidor.matricula"/>:</dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(rrs.getAttribute(Columns.RSE_MATRICULA))%></dd>
            <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/>:</dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(rrs.getAttribute(Columns.CSA_NOME))%></dd>
            <dt class="col-6"><hl:message key="rotulo.reclamacao.data.abreviado"/>:</dt>
            <dd class="col-6"><%=DateHelper.toDateTimeString((Date) rrs.getAttribute(Columns.RRS_DATA))%></dd>
            <dt class="col-6"><hl:message key="rotulo.tipo.motivo.reclamacao.singular"/>:</dt>
            <dd class="col-6">
              <%
              if (rrsMotivos != null && rrsMotivos.size() > 0) {
                  Iterator<TransferObject> it = rrsMotivos.iterator();
                  while (it.hasNext()) {
                      TransferObject tipoMotivoReclamacaoCTO = it.next();
                      String tmrDescricao = (String) tipoMotivoReclamacaoCTO.getAttribute(Columns.TMR_DESCRICAO);
              %>
              <span><%=TextHelper.forHtmlContent(tmrDescricao)%> </span>
              <%
                  }
              } 
              %>
            </dd>
            <dt class="col-6"><hl:message key="rotulo.reclamacao.texto"/>:</dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(rrs.getAttribute(Columns.RRS_TEXTO)).replace("\n","<br>&nbsp;")%></dd>
          </dl>
        </div>
      </div>
    </div>
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.reclamacao.titulo"/></h2>
        </div>
        <div class="card-body">
          <p><%=ApplicationResourcesHelper.getMessage("mensagem.reclamacao.leitura.termo", responsavel)%></p>
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="<%="postData('" + TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)) + "'); return false;"%>">Voltar</a>
  </div>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
    function imprime() {
        window.print();
    }
  </script>
</c:set>

<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>