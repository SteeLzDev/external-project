<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
ServidorTransferObject servidor = (ServidorTransferObject) request.getAttribute("servidor");
RegistroServidorTO registroServidor = (RegistroServidorTO) request.getAttribute("registroServidor");
String cmaCodigo = (String) request.getAttribute("CMA_CODIGO");
String rseCodigo = (String) request.getAttribute("RSE_CODIGO");
String vctCodigo = (String) request.getAttribute("vctCodigo");
String cmaVlr = (String) request.getAttribute("cmaVlr");
String cmaVinculo = (String) request.getAttribute("cmaVinculo");
String cmaQuantidade = (String) request.getAttribute("cmaQuantidade");
String vrsCodigo = (String) request.getAttribute("vrsCodigo");
String crsCodigo = (String) request.getAttribute("crsCodigo");
String desconto = (String) request.getAttribute("desconto");
List<TransferObject> vencimentos = (List<TransferObject>) request.getAttribute("vencimentos");
List<TransferObject> vinculos = (List<TransferObject>) request.getAttribute("vinculos");
List<TransferObject> cargos = (List<TransferObject>) request.getAttribute("cargos");

String linkRet = (String) request.getAttribute("linkRet");
String msgErro = (String) request.getAttribute("msgErro");
%>
<c:set var="title">
  <hl:message key="rotulo.editar.composicao.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/manterComposicaoMargemServidor?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><%=TextHelper.forHtmlContent(registroServidor.getRseMatricula())%> - <%=TextHelper.forHtmlContent(servidor.getSerNome())%></h2>
      </div>
      <div class="card-body">
          <fieldset>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="vctCodigo"><hl:message key="rotulo.editar.composicao.margem.vencimento"/></label>
                <%=JspHelper.geraCombo(vencimentos, "vctCodigo", Columns.VCT_CODIGO, Columns.VCT_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.arg", responsavel, ""), null, false, 1, vctCodigo, null, false, "form-control")%>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="cmaVlr"><hl:message key="rotulo.editar.composicao.margem.valor"/></label>
                <hl:htmlinput name="cmaVlr" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute(cmaVlr)%>" mask="#F11" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="cmaVinculo"><hl:message key="rotulo.editar.composicao.margem.vinculo"/></label>
                <hl:htmlinput name="cmaVinculo" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute(cmaVinculo)%>" mask="#*40"/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="cmaQuantidade"><hl:message key="rotulo.editar.composicao.margem.quantidade"/></label>
                <hl:htmlinput name="cmaQuantidade" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute(cmaQuantidade)%>" mask="#F5"/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="vrsCodigo"><hl:message key="rotulo.servidor.vinculo"/></label>
                <%=JspHelper.geraCombo(vinculos, "vrsCodigo", Columns.VRS_CODIGO, Columns.VRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.nenhum.simples", responsavel), null, false, 1, vrsCodigo, null, false, "form-control")%>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="crsCodigo"><hl:message key="rotulo.servidor.cargo"/></label>
                <%=JspHelper.geraCombo(cargos, "crsCodigo", Columns.CRS_CODIGO, Columns.CRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.nenhum.simples", responsavel), null, false, 1, crsCodigo, null, false, "form-control")%>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="desconto"><hl:message key="rotulo.editar.composicao.margem.informacoes"/></label>
                <div class="form-check pt-2">
                  <input type="checkbox" class="form-check-input ml-1" name="desconto" id="desconto" value="1" <%=desconto.equals("1") ? "checked" : ""%> />
                  <label for="desconto" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.editar.composicao.margem.cadastrar.desconto"/></label>
                </div>
              </div>
            </div>
          </fieldset>
      </div>
    </div>
    <hl:htmlinput name="CMA_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(cmaCodigo)%>"/>  
    <hl:htmlinput name="RSE_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>"/>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');" id="btnVoltar"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" id="Salvar" onClick="f0.submit(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>
  </form>   
        
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript">
  	var f0 = document.forms[0];
  	
  	window.onload = formLoad; 

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