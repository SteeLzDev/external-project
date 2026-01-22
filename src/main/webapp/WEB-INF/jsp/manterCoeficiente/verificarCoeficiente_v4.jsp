<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

Boolean mostraAtivo = (Boolean) request.getAttribute("mostraAtivo");
Boolean temCET = (Boolean) request.getAttribute("temCET");
Boolean ocultarCamposTac = (Boolean) request.getAttribute("ocultarCamposTac");
String acao = (String) request.getAttribute("acao");
String svc_descricao = (String) request.getAttribute("svc_descricao");
String titulo = (String) request.getAttribute("titulo");
String tac = (String) request.getAttribute("tac");
String op = (String) request.getAttribute("op");
Boolean tipoMensal = (Boolean) request.getAttribute("tipoMensal");
List prazos = (List) request.getAttribute("prazos");
int minDia = (int) request.getAttribute("minDia");
int maxDia = (int) request.getAttribute("maxDia");
List coeficientes = (List) request.getAttribute("coeficientes");
BigDecimal tacBd = (BigDecimal) request.getAttribute("tacBd");
BigDecimal opBd = (BigDecimal) request.getAttribute("opBd");
String svc_codigo = (String) request.getAttribute("svc_codigo");
String csa_codigo = (String) request.getAttribute("csa_codigo");
String tipo = (String) request.getAttribute("tipo");
Boolean podeAtivar = (Boolean) request.getAttribute("podeAtivar");

Boolean podeAtivarPsc = (Boolean) request.getAttribute("podeAtivarPsc");
Boolean podeAtivarCft = (Boolean) request.getAttribute("podeAtivarCft");
Boolean pscAtivo = (Boolean) request.getAttribute("pscAtivo");
Boolean cftAtivo = (Boolean) request.getAttribute("cftAtivo");

%>
<c:set var="title">
  <hl:message key="rotulo.coeficiente.vrf.titulo.semarg"/> <%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%> - <%=TextHelper.forHtmlContent(titulo.toUpperCase())%>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<FORM NAME="form1" METHOD="post" ACTION="../v3/verificarCoeficiente?<%=SynchronizerToken.generateToken4URL(request)%>">
  <div class="alert alert-success mb-1" role="alert">
    <p class="mb-0"><hl:message key="mensagem.coeficiente.simulacao.valor.liberado"/></p>    
  </div>
  <% if (mostraAtivo) { %>
  <div class="alert alert-success" role="alert">
    <p class="mb-0"><hl:message key="mensagem.coeficiente.simulacao.ativos"/></p>    
  </div>
  <% } else { %>
  <div class="alert alert-success" role="alert">
    <p class="mb-0"><hl:message key="mensagem.coeficiente.simulacao.uso.novos.coeficientes"/></p>    
  </div>
  <% } %>
  <% if (!temCET && !ocultarCamposTac) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.coeficiente.taxas.fixas"/></h2>
    </div>
    <div class="card-body">
      <div class="form-group mt-3">
        <div class="row">
          <div class="col-sm-6">
            <label for="tps_<%=(String)CodedValues.TPS_TAC_FINANCIADA%>"><hl:message key="rotulo.coeficiente.tac"/></label>
            <input id="tps_<%=(String)CodedValues.TPS_TAC_FINANCIADA%>" name="tps_<%=(String)CodedValues.TPS_TAC_FINANCIADA%>" value="<%=TextHelper.forHtmlContent(tac)%>" type="text" class="form-control" disabled>
          </div>
          <div class="col-sm-6">
            <label for="tps_<%=(String)CodedValues.TPS_OP_FINANCIADA%>"><hl:message key="rotulo.coeficiente.op"/></label>
            <INPUT id="tps_<%=(String)CodedValues.TPS_OP_FINANCIADA%>" name="tps_<%=(String)CodedValues.TPS_OP_FINANCIADA%>" value="<%=TextHelper.forHtmlContent(op)%>" type="text" class="form-control" disabled>
          </div>
        </div>
      </div>
    </div>
  </div> 
  <% } %>
  
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.coeficiente.plural"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <% if (!tipoMensal) { %>
            <th scope="col" class="pt-0"><hl:message key="rotulo.coeficiente.dia"/></th>
            <%}
              PrazoTransferObject pto;
              StringBuffer legenda = new StringBuffer();
              Iterator it = prazos.iterator();
              while (it.hasNext()) {
            	pto = (PrazoTransferObject)it.next();
            %>
              <th scope="col" class="text-nowrap  text-center"><hl:message key="rotulo.coeficiente"/> <br> <span class="font-weight-bold"><hl:message key="rotulo.coeficiente.prazo"/>  <%=TextHelper.forHtmlContent(pto.getPrzVlr())%></span></th>
              <th scope="col" class="text-nowrap  text-center"><hl:message key="rotulo.coeficiente.valor.parcela.abreviado"/> <br> <span class="font-weight-bold"><hl:message key="rotulo.coeficiente.prazo"/>  <%=TextHelper.forHtmlContent(pto.getPrzVlr())%></th>
            <%}%>
          </tr>
        </thead>
        <tbody>
          <%
            int j = 0;
            for (int i=minDia; i<=maxDia; i++) {
          %>
          <tr>
            <% if (!tipoMensal) { %>
            <td class="font-weight-bold"><%=(int)i%></td>
            <%}
            CustomTransferObject cto = null;
            BigDecimal ade_vlr = null;
            BigDecimal cft_vlr = null;
            BigDecimal vlr_liberado = new BigDecimal("1000.00");
            Short prz_vlr = null;
            Object cft_data_ini_vig = null;
            
            it = prazos.iterator();
            while (it.hasNext()) {
              pto = (PrazoTransferObject)it.next();
              prz_vlr = pto.getPrzVlr();
          
              cto = (coeficientes.size() > j) ? (CustomTransferObject)coeficientes.get(j) : null;

              if (cto != null &&
                  cto.getAttribute(Columns.CFT_DIA).toString().equals(String.valueOf(i)) &&
                  cto.getAttribute(Columns.PRZ_VLR).toString().equals(prz_vlr.toString())) {

                cft_vlr = new BigDecimal(cto.getAttribute(Columns.CFT_VLR).toString());
                ade_vlr = vlr_liberado.add(tacBd).add(opBd).multiply(cft_vlr);
                cft_data_ini_vig = cto.getAttribute(Columns.CFT_DATA_INI_VIG);
                podeAtivarCft = podeAtivarCft && (cft_data_ini_vig == null);
                cftAtivo = cftAtivo && (cft_data_ini_vig != null);

                j++;
              } else {
                cft_vlr = null;
                ade_vlr = null;
                podeAtivarCft = false;
                cftAtivo = false;
              } 
            %>
            <td align="center"><%=(String)(cft_vlr != null ? NumberHelper.format(cft_vlr.doubleValue(), NumberHelper.getLang(), 2, 8) : "&nbsp;")%></td>
            <td align="center"><%=(String)(ade_vlr != null ? NumberHelper.format(ade_vlr.doubleValue(), NumberHelper.getLang(), true) : "&nbsp;")%></td>
            <% } %>
          </tr>
          <% } %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="60"><hl:message key="rotulo.listagem.coeficientes"/></td>
          </tr>
        </tfoot>      
      </table>
    </div>
  </div>
  <%
  if (responsavel.isCsa()) {
  if (!mostraAtivo) {
  %>
  <INPUT TYPE="hidden" NAME="ATIVA_CFT" VALUE="<%=(String)(cftAtivo ? "0" : "1")%>">
  <INPUT TYPE="hidden" NAME="ATIVA_PSC" VALUE="<%=(String)(pscAtivo ? "0" : "1")%>">

  <INPUT TYPE="hidden" NAME="acao" VALUE="ativar">
  <INPUT TYPE="hidden" NAME="SVC_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
  <INPUT TYPE="hidden" NAME="SVC_DESCRICAO" VALUE="<%=TextHelper.forHtmlAttribute(svc_descricao)%>">
  <INPUT TYPE="hidden" NAME="CSA_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
  <INPUT TYPE="hidden" NAME="titulo" VALUE="<%=TextHelper.forHtmlAttribute(titulo)%>">
  <INPUT TYPE="hidden" NAME="tipo" VALUE="<%=TextHelper.forHtmlAttribute(tipo)%>">
  <INPUT TYPE="hidden" NAME="MM_update" VALUE="form1">
  
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="return vf_ativa_coeficiente();"><hl:message key="rotulo.botao.salvar"/></a>
  </div>
  <% } else { %>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
  </div>
  <% } %>
<% } %>
  
<% if (responsavel.isCseSup()) { %>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
<% } %>

</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
function formLoad() {
  if (QualNavegador() !='NE'){
    if (getElt('btnAtivar') != null)
      getElt('btnAtivar').focus();
    else if (getElt('btnVoltar') != null)
      getElt('btnVoltar').focus()
  }
}
</script>
<script type="text/JavaScript">
function vf_ativa_coeficiente() {
<% if (podeAtivar) { %>
  var ok = confirm('<hl:message key="mensagem.coeficientes.confirmacao.ativacao"/>');
  if (ok) {
    f0.submit();
//    return true;
  }
  return false;
<% } else if (pscAtivo && cftAtivo) { %>
  alert('<hl:message key="mensagem.coeficiente.ativados"/>');
  return false;
<% } else { %>
  alert('<hl:message key="mensagem.coeficientes.preenchimento"/>');
  return false;
<% } %>
}

f0 = document.forms[0];
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>