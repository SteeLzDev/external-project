<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
java.util.Date calData = (java.util.Date) request.getAttribute("calData");
TransferObject calendario = (TransferObject) request.getAttribute("calendario");
%>
<c:set var="title">
  <hl:message key="rotulo.calendario.manutencao.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
    <form method="post" action="../v3/editarCalendario?<%=SynchronizerToken.generateToken4URL(request)%>&_skip_history_=true" name="form1">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><%=DateHelper.toDateString(calData) + " - " + DateHelper.getWeekDayName(calData)%></h2>
        </div>
        <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="CAB_DESCRICAO"><hl:message key="rotulo.calendario.descricao"/></label>
                <input type="text" class="form-control" id="CAB_DESCRICAO" name="CAB_DESCRICAO" value="<%=TextHelper.forHtmlAttribute((calendario.getAttribute(Columns.CAB_DESCRICAO)))%>" size="30" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/> 
              </div>
              <div class="form-group col-sm-12 col-md-6">
                 <div>
                  <span id="descricao"><hl:message key="rotulo.calendario.dia.util"/></span>
                </div>
                <div class="form-check form-check-inline mt-0" >
                  <input class="form-check-input" type="radio" id="vIPSim" name="CAB_DIA_UTIL" value="S" <%=(String)(calendario.getAttribute(Columns.CAB_DIA_UTIL).equals("S") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito pr-3" for="vIPSim"><hl:message key="rotulo.sim"/></label>
                </div>
                <div class="form-check form-check-inline mt-0">
                  <input class="form-check-input" type="radio" id="vIPNao" name="CAB_DIA_UTIL" value="N" <%=(String)(!calendario.getAttribute(Columns.CAB_DIA_UTIL).equals("S") ? "checked" : "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito" for="vIPNao"><hl:message key="rotulo.nao"/></label>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="btn-action">
          <a href="#no-back" class="btn btn-outline-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
          <a href="#no-back" class="btn btn-primary" onClick="salvar(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
        </div>
        <input type="hidden" name="acao" value="salvar">
        <input type="hidden" name="CAB_DATA" value="<%=(String)(DateHelper.format(calData, "yyyy-MM-dd"))%>">
    </form>
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript">
var f0 = document.forms[0];
function salvar() {
	f0.submit();
}
</script>
<script language="JavaScript" type="text/JavaScript">
  function formLoad() {
    focusFirstField();
  }
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>