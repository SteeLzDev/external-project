<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
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
CustomTransferObject motivoReclamacao = (CustomTransferObject) request.getAttribute("motivoReclamacao");
String tmr_codigo = (String)request.getAttribute("tmr_codigo");
%>

<c:set var="title">
  <hl:message key="<%=TextHelper.forHtml("rotulo.criar.tipo.motivo.reclamacao.titulo")%>"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
  <form method="post" action="../v3/motivoReclamacao?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <div class="row" onLoad="formLoad();">
      <div class="col-sm">
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.criar.tipo.motivo.reclamacao.titulo"/></h2>
          </div>
          <div class="card-body">
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="descricao"><hl:message key="rotulo.tipo.motivo.reclamacao.descricao"/>:</label>
                  <input type="text" class="form-control" id="descricao" name="tmrDescricao" placeholder="Digite a descrição da reclamação" onFocus="SetarEventoMascara(this, '#*100', true);" onBlur="fout(this); ValidaMascara(this);" value="<%=TextHelper.forHtmlAttribute(motivoReclamacao != null ? (String)motivoReclamacao.getAttribute(Columns.TMR_DESCRICAO) : "")%>">
                  <%= JspHelper.verificaCampoNulo(request, "tmrDescricao") %>
                </div>
              </div>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger" aria-label="Voltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;">Cancelar</a>
          <a class="btn btn-primary" href="#no-back" onClick="javascript: verificaCampos(); return false;">Salvar</a>
          <input type="hidden" name="MM_update" value="form1">
          <input type="hidden" name="tipo" value="editar">
          <input type="hidden" name="tmrCodigo" value="<%=TextHelper.forHtmlAttribute(tmr_codigo != null ? tmr_codigo : "")%>">
        </div>
      </div>
    </div>
  </form>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript">
    function formLoad() {
  	  f0.tmrDescricao.focus();
    }
  
    function verificaCampos() {
      var controles = new Array("tmrDescricao");
      var msgs = new Array('<hl:message key="mensagem.informe.tipo.motivo.reclamacao.descricao"/>');

      if (!ValidaCampos(controles, msgs)) {
        return false;
      }

      f0.submit();
	}
  </script>
  <script type="text/JavaScript">
  	var f0 = document.forms[0];
  </script>
</c:set>

<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>