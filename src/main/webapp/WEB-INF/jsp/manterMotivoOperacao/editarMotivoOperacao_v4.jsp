<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

TipoMotivoOperacaoTransferObject motivoOperacao = (TipoMotivoOperacaoTransferObject) request.getAttribute("motivoOperacao");
String tmoCodigo  = motivoOperacao != null ? motivoOperacao.getTmoCodigo() : null;
List<TransferObject> tiposEntidade = (List<TransferObject>) request.getAttribute("tiposEntidade");

%>
<c:set var="title">
  <hl:message key="rotulo.tipo.motivo.manutencao"/>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
  <form method="post" action="../v3/motivoOperacao?<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.editar.grid" /></h2>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="tmoIdentificador"><hl:message key="rotulo.tipo.motivo.codigo"/></label>
            <input id="tmoIdentificador" type="text" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.codigo", responsavel)%>" class="form-control" type="text" name="tmoIdentificador" value="<%=TextHelper.forHtmlAttribute(motivoOperacao != null? motivoOperacao.getTmoIdentificador(): "")%>" size="10" onFocus="SetarEventoMascara(this,'#A40',true);" onBlur="fout(this);ValidaMascara(this);">
            <%=JspHelper.verificaCampoNulo(request, "tmoIdentificador")%>
          </div>
          <div class="form-group col-sm-6">
            <label for="tmoDescricao"><hl:message key="rotulo.tipo.motivo.descricao"/></label>
            <input id="tmoDescricao" type="text" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.descricao", responsavel)%>" class="form-control" NAME="tmoDescricao" VALUE="<%=TextHelper.forHtmlAttribute(motivoOperacao != null? motivoOperacao.getTmoDescricao(): "")%>" SIZE="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
            <%=JspHelper.verificaCampoNulo(request, "tmoDescricao")%>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-4">
            <label for="tenCodigo"><hl:message key="rotulo.tipo.motivo.tipo.entidade"/></label>
            <%=JspHelper.geraCombo(tiposEntidade, "tenCodigo", Columns.TEN_CODIGO, Columns.TEN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, motivoOperacao != null? motivoOperacao.getTenCodigo(): "", null, false, "form-control")%>
          </div>
          <div class="form-group col-sm-4">
            <label for="tmoExigeObs"><hl:message key="rotulo.tipo.motivo.exige.observacao"/></label>
            <select class="form-control form-select" name="tmoExigeObs" id="tmoExigeObs">
              <option value="N" <%=motivoOperacao != null && motivoOperacao.getTmoExigeObs().equals("N") ? "selected" : "" %>><hl:message key="rotulo.tipo.motivo.exige.observacao.opcional"/></option>
              <option value="S" <%=motivoOperacao != null && motivoOperacao.getTmoExigeObs().equals("S") ? "selected" : "" %>><hl:message key="rotulo.tipo.motivo.exige.observacao.obrigatorio"/></option>
              <option value="D" <%=motivoOperacao != null && motivoOperacao.getTmoExigeObs().equals("D") ? "selected" : "" %>><hl:message key="rotulo.tipo.motivo.exige.observacao.nao.editavel"/></option>
            </select>
          </div>
          <div class="form-group col-sm-4">
            <label for="tmoDecisaoJudicial"><hl:message key="rotulo.tipo.motivo.decisao.judicial"/></label>
            <select class="form-control form-select" name="tmoDecisaoJudicial" id="tmoDecisaoJudicial">
              <option value="N" <%=motivoOperacao != null && motivoOperacao.getTmoDecisaoJudicial().equals("N") ? "selected" : "" %>><hl:message key="rotulo.nao"/></option>
              <option value="S" <%=motivoOperacao != null && motivoOperacao.getTmoDecisaoJudicial().equals("S") ? "selected" : "" %>><hl:message key="rotulo.sim"/></option>
            </select>
          </div>
        </div>
      </div>
    </div>
    <div class="btn-action mt-3">
      <a class="btn btn-outline-danger" href="#no-back" onClick="javascript:postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/motivoOperacao?acao=iniciar", request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" onClick="verificaCampos(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
      <input type="hidden" name="acao" value="salvar">
      <input type="hidden" name="tmoCodigo" value="<%=TextHelper.forHtmlAttribute(tmoCodigo != null ? tmoCodigo: "")%>">
    </div>
 </form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
function formLoad() {
  f0.tmoIdentificador.focus();
}

function verificaCampos() {
    var controles = new Array("tmoIdentificador", "tmoDescricao", "tenCodigo");
    var msgs = new Array ("<hl:message key='mensagem.informe.codigo.tipo.motivo'/>",
                          "<hl:message key='mensagem.informe.descricao.tipo.motivo'/>",
                          "<hl:message key='mensagem.informe.codigo.tipo.entidade.tipo.motivo'/>");

    if (!ValidaCampos(controles, msgs)) {
      return false;
    }

    return salvar();
}

function salvar() {
	f0.submit();
}

window.onload = formLoad;

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