<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*"%> 
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
MensagemTO menTO = (MensagemTO) request.getAttribute("menTO");
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<String> destinatarioPermitido = (List<String>) request.getAttribute("destinatarioPermitido");
String menCodigo = (String) request.getAttribute("menCodigo");
String titulo = ApplicationResourcesHelper.getMessage("rotulo.enviar.mensagem", responsavel);
String menTitulo = menTO.getMenTitulo();
%>
<c:set var="title">
<%=TextHelper.forHtmlContent(titulo)%>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <form method="post" action="../v3/manterMensagem?acao=enviar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">      
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><%=TextHelper.forHtmlContent(menTitulo)%></h2>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="form-group col-sm-4">
            <label for="iTitulo"><hl:message key="rotulo.mensagem.titulo"/></label>
            <input type="text" name="matricula" id="iTitulo" value='<%=TextHelper.forHtmlContent(menTO != null && !TextHelper.isNull(menTO.getMenTitulo()) ? menTO.getMenTitulo().toString() : "")%>' class="form-control" disabled>
          </div>
          <div class="form-group col-sm-4">
            <label for="iDataCriacao"><hl:message key="rotulo.mensagem.data.criacao"/></label>
            <input type="text" name="nDataCriacao" id="iDataCriacao" value='<%=TextHelper.forHtmlContent(menTO != null && !TextHelper.isNull(menTO.getMenData()) ? menTO.getMenData().toString() : "")%>' class="form-control" disabled>
          </div>
          <div class="form-group col-sm-4">
            <label for="iSequencia"><hl:message key="rotulo.mensagem.sequencia"/></label>
            <input type="text" name="nSequencia" id="iSequencia" value='<%=TextHelper.forHtmlContent(menTO != null && !TextHelper.isNull(menTO.getMenSequencia()) ? menTO.getMenSequencia().toString() : "")%>' class="form-control" disabled>
          </div>
        </div>
        <div class="row">
          <div class="col-sm-12 col-md-12">
            <h3 class="legend">
              <span id="iExibirPara"><hl:message key="rotulo.mensagem.exibir.para"/></span>
            </h3>
            <div class="form-check">
              <div class="row" role="group" aria-labelledby="iExibirPara">
                <% if (menTO.getMenExibeCse().equals("S") && destinatarioPermitido.contains(CodedValues.PAP_CONSIGNANTE)) { %>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" name="menExibeCse" id="iConsignante">
                    <label class="form-check-label" for="iConsignante">
                      <span class="text-nowrap align-text-top"><hl:message key="rotulo.consignante.singular"/>  </span>
                    </label>
                  </div>
                <% } %>
                <% if (menTO.getMenExibeSer().equals("S") && destinatarioPermitido.contains(CodedValues.PAP_SERVIDOR)) { %>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" name="menExibeSer" id="iServidor">
                    <label class="form-check-label" for="iServidor">
                      <span class="text-nowrap align-text-top"><hl:message key="rotulo.servidor.singular"/></span>
                    </label>
                  </div>
                <% } %>
                <% if (menTO.getMenExibeOrg().equals("S") && destinatarioPermitido.contains(CodedValues.PAP_ORGAO)) { %>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" name="menExibeOrg" id="iOrgao">
                    <label class="form-check-label" for="iOrgao">
                      <span class="text-nowrap align-text-top"><hl:message key="rotulo.orgao.singular"/></span>
                    </label>
                  </div>
                <% } %>
                <% if (menTO.getMenExibeCor().equals("S") && destinatarioPermitido.contains(CodedValues.PAP_CORRESPONDENTE)) { %>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" name="menExibeCor" id="iCorrespondente">
                    <label class="form-check-label" for="iCorrespondente">
                      <span class="text-nowrap align-text-top"><hl:message key="rotulo.correspondente.singular"/></span>
                    </label>
                  </div>
                <% } %>
                <% if (menTO.getMenExibeCsa().equals("S") && destinatarioPermitido.contains(CodedValues.PAP_CONSIGNATARIA)) { %>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="checkbox" onclick="setEnabledTipoEnvioCSA(this.checked)" value="<%=(String)CodedValues.TPC_SIM%>" name="menExibeCsa" id="iConsignataria">
                    <label class="form-check-label" for="iConsignataria">
                      <span class="text-nowrap align-text-top"><hl:message key="rotulo.consignataria.singular"/></span>
                    </label>
                  </div>
                <% } %>
                <% if (menTO.getMenExibeSup().equals("S") && destinatarioPermitido.contains(CodedValues.PAP_SUPORTE)) { %>
                  <div class="col-sm-12 col-md-3">
                    <input class="form-check-input ml-1" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" name="menExibeSup" id="iSuporte">
                    <label class="form-check-label" for="iSuporte">
                      <span class="text-nowrap align-text-top"><hl:message key="rotulo.suporte.singular"/></span>
                    </label>
                  </div>
                <% } %>
                <div class="col-sm-12 col-md-3">
                  <input class="form-check-input ml-1" type="checkbox" onclick="checkTodos(); if (f0.menExibeCsa != null) { setEnabledTipoEnvioCSA(f0.menExibeCsa.checked); }" value="S" name="menCheckTodos" id="iTodos">
                  <label class="form-check-label" for="iTodos">
                    <span class="text-nowrap align-text-top font-weight-bold"><hl:message key="rotulo.campo.todos.simples"/></span>
                  </label>
                </div>
              </div>
            </div>
          </div>
        </div>
        <% if (menTO.getMenExibeCsa().equals("S") && destinatarioPermitido.contains(CodedValues.PAP_CONSIGNATARIA)) { %> 
        <div class="row">
          <div class="col-sm-6 col-md-12">
            <div class="form-group mb-1" role="radiogroup" aria-labelledby="integraAutorizacoesNaFolha">
              <span id="integraAutorizacoesNaFolha"><hl:message key="rotulo.mensagem.opcoes.consignataria"/></span>
              <div class="form-check pt-1">
                <div class="form-check">
                  <input class="form-check-input ml-1" type="radio" name="inclurCsaBloqueda" value="<%=(String)CodedValues.TPC_NAO%>" id="iSomenteDesbolqueadas" disabled checked >
                  <label class="form-check-label labelSemNegrito ml-1 pr-4" for="iSomenteDesbolqueadas"><hl:message key="rotulo.campo.somente.desbloqueadas"/></label>
                </div>
                <div class="form-check">
                  <input class="form-check-input ml-1" type="radio" name="inclurCsaBloqueda" value="<%=(String)CodedValues.TPC_SIM%>" id="iConsignatariaTodos" disabled >
                  <label class="form-check-label labelSemNegrito ml-1 pr-4" for="iConsignatariaTodos"><hl:message key="rotulo.campo.todas.simples"/></label>
                </div>
              </div>
            </div>
          </div>
        </div>
        <% } %>
        <div class="row">
          <div class="col-sm">
            <h3 class="legend">
              <span id="iExibirPara"><hl:message key="rotulo.mensagem.texto"/></span>
            </h3>
            <%=(menTO != null && !TextHelper.isNull(menTO.getMenTexto()) ? (menTO.getMenHtml().equals("N") ? new Markdown4jProcessorExtended().process(TextHelper.forHtmlContent(menTO.getMenTexto())).toString() : menTO.getMenTexto()) : "")%>
          </div>
        </div>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" onClick="<%="postData('" + TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)) + "'); return false;"%>" href="mensagens.php"><hl:message key="rotulo.botao.voltar"/></a>
      <a class="btn btn-primary" name="btnEnviar" id="btnEnviar" onClick="if (vf_escolha_entidade()) {f0.submit();} return false;" href="#no-back">
        <svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg>
        <hl:message key="rotulo.botao.confirmar"/>
      </a>
    </div>
    <hl:htmlinput name="menCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(menCodigo)%>"/>
    <input type="hidden" name="MM_update" value="form1">
    <input type="hidden" name="acao" value="salvar">
  </form>
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript" src="../js/editorMsgs.js"></script>
<script type="text/JavaScript">
function vf_escolha_entidade() {
  if ((f0.menExibeCse != null && f0.menExibeCse.checked) || 
    (f0.menExibeCor != null && f0.menExibeCor.checked) || 
    (f0.menExibeCsa != null && f0.menExibeCsa.checked) || 
    (f0.menExibeSer != null && f0.menExibeSer.checked) || 
    (f0.menExibeOrg != null && f0.menExibeOrg.checked) || 
    (f0.menExibeSup != null && f0.menExibeSup.checked)) {

    return confirm('<hl:message key="mensagem.confirmacao.confirma.envio.email.mensagem"/>');
  }
  
  alert('<hl:message key="mensagem.erro.selecione.entidade"/>');
  return false;
}
var f0 = document.forms[0];

function formLoad() {
  //focusFirstField();
}

function setEnabledTipoEnvioCSA(valor) {
  jQuery('input[name=inclurCsaBloqueda]').attr("disabled", !valor);
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>