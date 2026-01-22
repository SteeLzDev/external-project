<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

TransferObject solicitacao = (TransferObject) request.getAttribute("solicitacao");

%>
<c:set var="title">
<hl:message key="rotulo.solicitacao.suporte.titulo" />
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.solicitacao.suporte.dados.solicitacao"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <dl class="row data-list">
          <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.chave"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_CHAVE))%></dd>
          <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.cliente"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_CLIENTE_TRANSIENTE) != null ? solicitacao.getAttribute(Columns.SOS_CLIENTE_TRANSIENTE) : "")%></dd>
          <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.status"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_STATUS_TRANSIENTE))%></dd>
          <dt class="col-5"><hl:message key="rotulo.nova.solicitacao.grupo"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_PAPEL_TRANSIENTE) != null ? solicitacao.getAttribute(Columns.SOS_PAPEL_TRANSIENTE) : "")%></dd>
          <dt class="col-5"><hl:message key="rotulo.nova.solicitacao.identificacao"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(responsavel.getNomeEntidade())%></dd>
          <%if (responsavel.isCsaCor()) {%> 
            <dt class="col-5"><hl:message key="rotulo.consignataria.singular"/></dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_DESCSA_TRANSIENTE))%></dd>
          <%} %>
          <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.tipo.servico"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_SERVICO_TRANSIENTE) != null ? solicitacao.getAttribute(Columns.SOS_SERVICO_TRANSIENTE) : "")%></dd>
          <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.prioridade"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_PRIORIDADE_ID_TRANSIENTE) != null ? solicitacao.getAttribute(Columns.SOS_PRIORIDADE_ID_TRANSIENTE) : "")%></dd>
          <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.data.criacao"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_DATA_CADASTRO))%></dd>
          <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.ultima.atualizacao"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_DATA_ATUALIZACAO_TRANSIENTE))%></dd>
          <%if (solicitacao.getAttribute(Columns.SOS_DATA_RESOLUCAO_TRANSIENTE) != null) {%>
            <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.data.resolucao"/></dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_DATA_RESOLUCAO_TRANSIENTE))%></dd>
          <%} %>
          <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.sla.indicator"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_SLA_INDICATOR) != null ? solicitacao.getAttribute(Columns.SOS_SLA_INDICATOR) : "")%></dd>
          <%String responsavelSos = (String) solicitacao.getAttribute(Columns.SOS_RESPONSAVEL_TRANSIENTE);%>
            <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.responsavel"/></dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(!TextHelper.isNull(responsavelSos) ? responsavelSos:"")%></dd>
          <%String email = (String) solicitacao.getAttribute(Columns.SOS_EMAIL_TRANSIENTE);%>
          <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.email"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(!TextHelper.isNull(email) ? email:"")%></dd>
          <dt class="col-5"><hl:message key="rotulo.solicitacao.suporte.assunto"/></dt>
          <dd class="col-7"><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_SUMARIO))%></dd>
        </dl>
        <div class="form-check form-group col-md-12 mt-2">
          <label for="adeObs"><hl:message key="rotulo.solicitacao.suporte.descricao"/></label>
          <textarea class="form-control" name="sosDescricao" cols="50" rows="15" disabled><%=TextHelper.forHtmlContent(((String) solicitacao.getAttribute(Columns.SOS_DESCRICAO_TRANSIENTE)).trim())%></textarea>
        </div> 
        <div class="form-check form-group col-md-12 mt-2">
          <label for="solucao"><hl:message key="rotulo.solicitacao.suporte.solucao"/></label>
          <textarea class="form-control" name="solucao" cols="50" rows="10" disabled><%=TextHelper.forHtmlContent(solicitacao.getAttribute(Columns.SOS_SOLUCAO_TRANSIENTE) != null ? solicitacao.getAttribute(Columns.SOS_SOLUCAO_TRANSIENTE) : "")%></textarea>
        </div> 
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">   
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>