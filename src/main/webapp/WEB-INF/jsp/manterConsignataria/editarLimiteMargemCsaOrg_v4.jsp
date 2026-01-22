<%@page import="java.math.BigDecimal"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<MargemTO> margens = (List<MargemTO>) request.getAttribute("margens");
List<TransferObject> lstLimiteMargemCsaOrg = (List<TransferObject>) request.getAttribute("lstLimiteMargemCsaOrg");
List<TransferObject> orgaos = (List<TransferObject>) request.getAttribute("orgaos");
String csaCodigo = (String) request.getAttribute("csaCodigo"); 
String csaIdentificador = (String) request.getAttribute("csaIdentificador"); 
String csaNome = (String) request.getAttribute("csaNome"); 
String linkVoltar = (String) request.getAttribute("linkVoltar");
%>
<c:set var="title">
  <hl:message key="rotulo.limite.margem.csa.org.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form method="post" action="../v3/manterConsignataria?acao=salvarLimiteMargemCsaOrg&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(csaIdentificador)%> - <%=TextHelper.forHtmlContent(csaNome)%></h2>
    </div>
    <div class="card-body table-responsive">
      <table id="dataTables" class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col" width="40%"><hl:message key="rotulo.orgao.singular"/></th>
            <%for(MargemTO margem: margens){ 
                String marDescricao = margem.getMarDescricao();
            %>
                <th scope="col"><%=TextHelper.forHtmlContent(marDescricao)%></th>
            <%} %>
          </tr>
        </thead>
        <tbody>
        <% for(TransferObject orgao : orgaos){
            String orgCodigo = (String) orgao.getAttribute(Columns.ORG_CODIGO);
            String orgNome = (String) orgao.getAttribute(Columns.ORG_NOME);
            String orgIdentificador = (String) orgao.getAttribute(Columns.ORG_IDENTIFICADOR);
        %>
          <tr>
            <td><%=TextHelper.forHtmlContent(orgIdentificador+" - "+orgNome.toUpperCase())%></td>
            <%for(MargemTO margem : margens){
                Short marCodigoTd = margem.getMarCodigo();
                Optional<TransferObject> limite = lstLimiteMargemCsaOrg.stream().filter((f) -> 
                    f.getAttribute(Columns.LMC_ORG_CODIGO).toString().equals(orgCodigo) && f.getAttribute(Columns.LMC_MAR_CODIGO).toString().equals(marCodigoTd.toString())
                    ).findFirst(); 
            %>
                <%if(limite.isPresent()){ 
                    BigDecimal lmcValorBig = (java.math.BigDecimal) limite.get().getAttribute(Columns.LMC_VALOR);
                    lmcValorBig = lmcValorBig.multiply(new java.math.BigDecimal(100.00));
                    String lmcValor = NumberHelper.format(lmcValorBig.doubleValue(), NumberHelper.getLang()); 
                %>
                    <td>
                      <input class="form-control w-100"
                          id="org_<%=TextHelper.forHtmlAttribute(orgCodigo)%>_mar_<%=TextHelper.forHtmlAttribute(marCodigoTd)%>"
                          name="org_<%=TextHelper.forHtmlAttribute(orgCodigo)%>_mar_<%=TextHelper.forHtmlAttribute(marCodigoTd)%>"
                          type="text"
                          value="<%=TextHelper.forHtmlAttribute(lmcValor)%>" 
                          size="8"
                          onFocus="SetarEventoMascara(this,'#F11',true);" 
                          onChange="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2);tamanhoCampo('org_<%=TextHelper.forHtmlAttribute(orgCodigo)%>_mar_<%=TextHelper.forHtmlAttribute(marCodigoTd)%>')}"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.placeholder.limite.margem.csa.org.porcentagem", responsavel)%>"/>
                    </td>
                <%}else { %>
                    <td>
                      <input class="form-control w-100"
                          id="org_<%=TextHelper.forHtmlAttribute(orgCodigo)%>_mar_<%=TextHelper.forHtmlAttribute(marCodigoTd)%>"
                          name="org_<%=TextHelper.forHtmlAttribute(orgCodigo)%>_mar_<%=TextHelper.forHtmlAttribute(marCodigoTd)%>"
                          type="text"
                          value="" 
                          size="8"
                          onFocus="SetarEventoMascara(this,'#F11',true);" 
                          onChange="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2);tamanhoCampo('org_<%=TextHelper.forHtmlAttribute(orgCodigo)%>_mar_<%=TextHelper.forHtmlAttribute(marCodigoTd)%>')}"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.placeholder.limite.margem.csa.org.porcentagem", responsavel)%>"/>
                    </td>                
                <%} %>
            <%} %>
          </tr>
          <%
            }
          %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="4"><hl:message key="rotulo.limite.margem.csa.org.listagem"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="btn-action">
      <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkVoltar, request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      <a class="btn btn-primary" id="btnEnvia" href="#" onClick="validaForm(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
      <INPUT TYPE="hidden" NAME="csaCodigo" VALUE="<%=TextHelper.forHtmlAttribute((csaCodigo))%>">
      <INPUT TYPE="hidden" NAME="linkVoltar" VALUE="<%=TextHelper.forHtmlAttribute((linkVoltar))%>">
  </div>
</form>  
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript">
  var f0 = document.forms[0];
  function validaForm(){
      f0.submit();
  }
  
  function tamanhoCampo(dados){
  	var percentual = document.getElementById(dados).value;
  	percentual = parseFloat(percentual);
    if (percentual > 100){
    	document.getElementById(dados).value = "100"
    }
  }
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>