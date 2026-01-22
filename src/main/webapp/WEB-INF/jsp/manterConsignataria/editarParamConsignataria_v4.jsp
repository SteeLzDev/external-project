<%@page import="com.zetra.econsig.helper.periodo.PeriodoHelper"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String parametros = (String) request.getAttribute("parametros");
String titulo = (String) request.getAttribute("titulo");
List param = (List) request.getAttribute("param");
String csaCodigo = (String) request.getAttribute("csaCodigo");
int qtdParamColuna = (param != null && !param.isEmpty()) ? new Double(Math.ceil((param.size() ))).intValue() : 0;
String mensagemDiaCorteCsa = ApplicationResourcesHelper.getMessage("mensagem.erro.dia.corte.csa.maior.corte.sistema", responsavel, String.valueOf(PeriodoHelper.getInstance().getProximoDiaCorte(null, responsavel)));
%>
<c:set var="title">
   <hl:message key="rotulo.parametros.consignataria"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
     <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.parametro.plural"/></h2>
     </div>
     <div class="card-body">
        <form method="post" action="../v3/editarParamConsignataria" name="form1">
          <%= JspHelper.geraCamposHidden(parametros) %>
          <%for (int i=0; i < qtdParamColuna; i+=2) { %>
              <div class="row ">
          <%    
              CustomTransferObject next = (CustomTransferObject)param.get(i);
              String tpa_codigo = next.getAttribute(Columns.TPA_CODIGO).toString();
              String tpa_descricao = next.getAttribute(Columns.TPA_DESCRICAO).toString();
              String tpa_dominio = next.getAttribute(Columns.TPA_DOMINIO).toString();
              String pcs_vlr = next.getAttribute(Columns.PCS_VLR) != null ? next.getAttribute(Columns.PCS_VLR).toString() : "";

              // Valor default para parametro verificar_validacoes_limites = sim
              if (tpa_codigo.equals(CodedValues.TPA_VERIFICAR_VALIDACOES_LIMITES) && pcs_vlr.equals("")) {
                  pcs_vlr = "S";
              }
              
              // Valor default para parametro desbloqueia_csa_aprovacao_por_sup = sim
              if (tpa_codigo.equals(CodedValues.TPA_DESBLOQUEIA_CSA_APROVACAO_POR_SUP) && pcs_vlr.equals("")) {
                  pcs_vlr = "S";
              }
              
              String placeHolder = ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, tpa_descricao);
           %>  
              <%=JspHelper.montaValorParamCsaV4("TPA_" + tpa_codigo, tpa_dominio, TextHelper.forHtmlContent(pcs_vlr), true, null, placeHolder, tpa_descricao, responsavel)%>
           
           <%   
              if ((i + 1) < param.size()) {
                 next = (CustomTransferObject)param.get(i + 1);
                 if (next != null) {
                   tpa_codigo = next.getAttribute(Columns.TPA_CODIGO).toString();
                   tpa_descricao = next.getAttribute(Columns.TPA_DESCRICAO).toString();
                   tpa_dominio = next.getAttribute(Columns.TPA_DOMINIO).toString();
                   pcs_vlr = next.getAttribute(Columns.PCS_VLR) != null ? next.getAttribute(Columns.PCS_VLR).toString() : "";

                   // Valor default para parametro verificar_validacoes_limites = sim
                   if (tpa_codigo.equals(CodedValues.TPA_VERIFICAR_VALIDACOES_LIMITES) && pcs_vlr.equals("")) {
                      pcs_vlr = "S";
                   }
                   
                   // Valor default para parametro desbloqueia_csa_aprovacao_por_sup = sim
                   if (tpa_codigo.equals(CodedValues.TPA_DESBLOQUEIA_CSA_APROVACAO_POR_SUP) && pcs_vlr.equals("")) {
                       pcs_vlr = "S";
                   }
                   
                   placeHolder = ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, tpa_descricao);
           %>       
                 <%=JspHelper.montaValorParamCsaV4("TPA_" + tpa_codigo, tpa_dominio, TextHelper.forHtmlContent(pcs_vlr), true, null, placeHolder, tpa_descricao, responsavel)%>
            <%   }
              }%>
              </div>
          <%} %>
          <hl:htmlinput name="MM_update" type="hidden" value="form1" />
          <hl:htmlinput name="flow" type="hidden" value="endpoint" />
          <INPUT TYPE="hidden" NAME="CSA_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(csaCodigo)%>" SIZE="32">
        </form>
     </div>
  </div>
  <div class="btn-action">
     <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
     <a class="btn btn-primary" id="btnEnvia" HREF="#no-back" onClick="if(verificaParamCsa()) { f0.submit(); } return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div> 
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript">
f0 = document.forms[0];

function formLoad() {
  try {
    f0.elements[0].focus();
  } catch(ex) {
  }
}

function verificaParamCsa() {
  $('#btnEnvia').on('click', function (e) {
      e.preventDefault();
  });

  var diaCorteCsa = null;
  if (f0.TPA_<%=(String)CodedValues.TPA_DIA_CORTE%> != null){
      diaCorteCsa = document.getElementById('TPA_<%=(String)CodedValues.TPA_DIA_CORTE%>').value;
  }
  var diaCorteSistema = <%=PeriodoHelper.getInstance().getProximoDiaCorte(null, responsavel)%>;
  var diaCorteCsaInt = parseInt(diaCorteCsa);
  
  if (((f0.TPA_<%=(String)CodedValues.TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE%> != null) && (getCheckedRadio("form1", "TPA_<%=(String)CodedValues.TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE%>") == '<%=(String)CodedValues.TPA_SIM%>')) &&
	  ((f0.TPA_<%=(String)CodedValues.TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE%> != null) && (getCheckedRadio("form1", "TPA_<%=(String)CodedValues.TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE%>") == '<%=(String)CodedValues.TPA_SIM%>'))) {
	 alert('<hl:message key="mensagem.erro.habilitar.ordenacao.margem.servidor.lote"/>');
     return false; 
  }
  if ((f0.TPA_<%=(String)CodedValues.TPA_SEPARAR_RELATORIO_INTEGRACAO%> != null) && (getCheckedRadio("form1", "TPA_<%=(String)CodedValues.TPA_SEPARAR_RELATORIO_INTEGRACAO%>") != '<%=(String)CodedValues.SEPARA_REL_INTEGRACAO_NAO%>')) {
     return confirm('<hl:message key="mensagem.confirme.separar.relatorio.integracao"/>');
  }

  if ((f0.TPA_<%=(String)CodedValues.TPA_DIA_CORTE%> != null) && diaCorteCsa != "" && (!Number.isInteger(diaCorteCsaInt) || diaCorteCsaInt == 0)){
	 alert('<hl:message key="mensagem.erro.dia.corte.csa.nao.numerico"/>');
     return false;  
  } else if ((f0.TPA_<%=(String)CodedValues.TPA_DIA_CORTE%> != null) && diaCorteCsa != "" && Number.isInteger(diaCorteCsaInt) && diaCorteCsaInt > diaCorteSistema) {
     return confirm('<%=mensagemDiaCorteCsa%>');
  }
  return true;
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>