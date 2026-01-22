<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
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

int tamMaxMsg = (int) request.getAttribute("tamMaxMsg");

boolean rrsApenasPraCsaComAde = (boolean) request.getAttribute("rrsApenasPraCsaComAde");
boolean mensagemLida = (boolean) request.getAttribute("mensagemLida");

String serCodigo = (String) request.getAttribute("serCodigo");
String rrsMensagem = (String) request.getAttribute("rrsMensagem");
String csaCodigo = (String) request.getAttribute("rrsMensagem");

List<TransferObject> tiposReclamacao = (List<TransferObject>) request.getAttribute("tiposReclamacao");
List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
%>

<c:set var="title">
  <hl:message key="rotulo.reclamacao.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <form action="../v3/editarReclamacao?acao=editarReclamacao&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" method="post">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.reclamacao.subtitulo"/></h2>
      </div>
      <div class="card-body">
          <% if (!mensagemLida) { %>
            <p><%=ApplicationResourcesHelper.getMessage("mensagem.reclamacao.leitura.termo", responsavel)%></p>
            <div class="row form-check" role="group" aria-labelledby="geral">
              <div class="col-sm-12 col-md-6">
                <input class="form-check-input ml-1" type="checkbox" name="MSG_CHECK" id="confirmarLeitura" value="true">
                <label class="form-check-label" for="confirmarLeitura">
                  <span class="text-nowrap align-text-top font-weight-bold"><hl:message key="rotulo.reclamacao.confirmar.leitura"/></span>
                </label>
              </div>
            </div>
          <% } else { %>
            <div class="row">
              <div class="form-group col-sm-12  col-md-6">
                <label for="consignataria"><hl:message key="rotulo.consignataria.singular"/>:</label>
                <select class="form-control form-select" name="CSA_CODIGO_DESTINO" id="CSA_CODIGO_DESTINO" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);" <%=(String)(mensagemLida ? "" : "disabled")%>>
                  <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
                  <%
                    Iterator it = consignatarias.iterator();
                    CustomTransferObject csa = null;
                    String csaCodigo2, csaNome, csaId;
                    String selected = JspHelper.verificaVarQryStr(request, "CSA_CODIGO_DESTINO");
                    while (it.hasNext()) {
                       csa = (CustomTransferObject)it.next();
                       csaCodigo2 = (String)csa.getAttribute(Columns.CSA_CODIGO);
                       csaId = csa.getAttribute(Columns.CSA_IDENTIFICADOR).toString();
                       csaNome = csa.getAttribute(Columns.CSA_NOME).toString();
                       if (csaNome.length() > 50){
                           csaNome = csaNome.substring(0, 47) + "...";
                       }
                  %>
                  <option value="<%=TextHelper.forHtmlAttribute(csaCodigo2)%>" <%=(String)(selected.equals(csaCodigo2) ? "selected" : "")%>>
                    <%=TextHelper.forHtmlContent(csaNome)%> - <%=TextHelper.forHtmlContent(csaId)%>
                  </option>
                  <% } %>
                </SELECT>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6" role="checkBox">
                <span id="tipoMotivoReclamacao" class="mb-2"><hl:message key="rotulo.tipo.motivo.reclamacao.singular"/>:</span>
                <div class="row" role="group" aria-labelledby="tipoMotivoReclamacao">
                  <div class="form-check" aria-labelledby="tipoMotivoReclamacao">
                    <%
                      CustomTransferObject tipo = null;
                      String nome = "", codigo = "", scv_codigo = "";
                      
                      Iterator ittr = tiposReclamacao.iterator();
                      while (ittr.hasNext()) {
                         tipo = (CustomTransferObject)ittr.next();
                         nome = tipo.getAttribute(Columns.TMR_DESCRICAO).toString();
                         codigo = tipo.getAttribute(Columns.TMR_CODIGO).toString();
                    %>
                    <div class="col-sm-12 col-md-6">
                      <input class="form-check-input ml-1" name="TMR_CODIGO" id="<%=TextHelper.forHtmlAttribute(nome)%>-<%=TextHelper.forHtmlAttribute(codigo)%>" type="checkbox" value="<%=TextHelper.forHtmlAttribute(codigo)%>">
                      <label class="form-check-label ml-1 ml-1 ml-1 ml-1" for="<%=TextHelper.forHtmlAttribute(nome)%>-<%=TextHelper.forHtmlAttribute(codigo)%>">
                        <span class="text-nowrap align-text-top labelSemNegrito"><%=TextHelper.forHtmlContent(nome)%></span>
                      </label>
                    </div>
                    <% } %>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="descricaoOcorrido"><hl:message key="rotulo.reclamacao.texto"/></label>
                <textarea class="form-control" name="MENSAGEM" id="descricaoOcorrido" rows="6" placeholder="Digite a descrição do ocorrido" <%= (mensagemLida ? "" : "disabled" ) %> onFocus="SetarEventoMascara(this,'#*<%=TextHelper.forJavaScript((tamMaxMsg))%>',true);" onBlur="fout(this);ValidaMascara(this);"><%=TextHelper.forHtmlContent((rrsMensagem != null) ? rrsMensagem : "")%></textarea>
              </div>
            </div>
           <% } %>
      </div>
    </div>
    <div class="btn-action">
      <% if (mensagemLida) { %>
        <hl:htmlinput name="MSG_CHECK" type="hidden" value="true"/>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;">Cancelar</a>
        <a class="btn btn-primary" href="#no-back" onClick="javascript: enviar(); return false;">Salvar</a>
      <% } else { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;">Cancelar</a>
        <a class="btn btn-primary" href="#no-back" onClick="javascript: validaForm(); return false;">Continuar</a>
      <% } %>
    </div>
    <input name="FORM" type="hidden" value="form1">
  </form>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
  	  function enviar() {
      	var msg = trim(f0.MENSAGEM.value);

      	// Verifica quantidade de caracteres informados na mensagem
      	if (msg.length < 10) {
      		alert('<hl:message key="mensagem.erro.reclamacao.texto.minimo"/>');
      		f0.MENSAGEM.focus();
      		return;
      	}

      	// Verifica se existe pelo menos uma letra na mensagem
      	var regex = /([a-zA-Z]+)/g;
      	if (!msg.match(regex)) {
      		alert('<hl:message key="mensagem.erro.reclamacao.texto.invalido"/>');
      		f0.MENSAGEM.focus();
      		return;
      	}

      	var checked = false;
      	for (i = 0; i < f0.TMR_CODIGO.length; i++) {
      		var e = f0.TMR_CODIGO[i];
      		if (((e.type == 'check') || (e.type == 'checkbox'))
      				&& (e.checked == true)) {
      			checked = true;
      			break;
      		}
      	}

      	if (!checked) {
      		alert('<hl:message key="mensagem.informe.reclamacao.tipo.motivo.reclamacao"/>');
      		return;
      	}

      	Controles = new Array("CSA_CODIGO_DESTINO", "MENSAGEM");
      	Msgs = new Array(
      			'<hl:message key="mensagem.informe.reclamacao.consignataria"/>',
      			'<hl:message key="mensagem.informe.reclamacao.texto"/>');

      	// Valida vampos do formulário
      	if (ValidaCampos(Controles, Msgs)) {
      		f0.submit();
      	}
      }

      function validaForm() {
      	if (!f0.MSG_CHECK.checked) {
      		alert('<hl:message key="mensagem.informe.reclamacao.leitura.mensagem"/>');
      		f0.MSG_CHECK.focus();
      		return false;
      	}
      	f0.submit();
      }
  </script>
  <script type="text/JavaScript">
  	f0 = document.forms[0];
  </script>
</c:set>

<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>