<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.values.StatusConsignatariaEnum"%>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t"    tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl"   uri="/html-lib" %>
<%@ taglib prefix="show" uri="/showfield-lib" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
ConsignatariaTransferObject consignataria = (ConsignatariaTransferObject) request.getAttribute("consignataria");

String csaNome = (String) request.getAttribute("csaNome");
String btnVoltar = (String) request.getAttribute("btnVoltar");
%>
<c:set var="title">
  <hl:message key="rotulo.consignataria.singular"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <div class="row firefox-print-fix">
      <div class="col-sm">
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><%=TextHelper.forHtmlContent(consignataria.getCsaIdentificador())%> - <%=TextHelper.forHtmlContent(csaNome)%></h2>
          </div>
          <div class="card-body">
            <dl class="row data-list firefox-print-fix">

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_SITUACAO)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.status"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(ApplicationResourcesHelper.getMessage(StatusConsignatariaEnum.ATIVO.getCodigo().equals(consignataria.getCsaAtivo().toString()) ? "rotulo.consignataria.filtro.desbloqueado" : "rotulo.consignataria.filtro.bloqueado", responsavel))%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_CNPJ)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.cnpj"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaCnpj()) ? consignataria.getCsaCnpj() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_CONTATO)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.contato"/>&nbsp;/&nbsp;<hl:message key="rotulo.consignataria.telefone"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaContato()) ?  consignataria.getCsaContato() : "-" )%> <%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaContatoTel()) ? " / "+ consignataria.getCsaContatoTel() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_LOGRADOURO)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.logradouro"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaLogradouro()) ? consignataria.getCsaLogradouro() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_NUM_LOGRADOURO)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.logradouro.numero"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaNro()) ?  consignataria.getCsaNro() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_COMPLEMENTO)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.logradouro.complemento"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaCompl()) ? consignataria.getCsaCompl() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_BAIRRO)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.bairro"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaBairro()) ? consignataria.getCsaBairro() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_CIDADE)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.cidade"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaCidade()) ? consignataria.getCsaCidade() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_UF)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.uf"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaUf()) ? consignataria.getCsaUf() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_CEP)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.cep"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaCep()) ? consignataria.getCsaCep() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_TELEFONE)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.telefone"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaTel()) ? consignataria.getCsaTel() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_FAX)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.fax"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaFax()) ? consignataria.getCsaFax() : "-" )%></dd>
              </show:showfield>

              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_EMAIL)%>">
                <dt class="col-6"><hl:message key="rotulo.consignataria.email"/></dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaEmail()) ? consignataria.getCsaEmail() : "-" )%></dd>
              </show:showfield>

            </dl>
          </div>
        </div>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(btnVoltar)%>'); return false;"><hl:message key="rotulo.botao.voltar" /></a>
    </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>