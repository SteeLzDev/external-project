<%--
* <p>Title: confirmarReservaGap.jsp</p>
* <p>Description: Página que confirma a reserva do acordo GAP</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: alexandre $
* $Revision: 31212 $
* $Date: 2021-01-28 15:37:52 -0300 (qui, 28 jan 2021) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %><%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.*, java.io.*, java.math.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String usuLoginResponsavel = responsavel.getUsuLogin();

String rseCodigo = (String) request.getAttribute("rseCodigo");
String cnvCodigo = (String) request.getAttribute("cnvCodigo");
String adeIdentificador = (String) request.getAttribute("adeIdentificador");
String numBanco = (String) request.getAttribute("numBanco");
String numAgencia = (String) request.getAttribute("numAgencia");
String numConta = (String) request.getAttribute("numConta");

String estIdentificador = (String) request.getAttribute("estIdentificador");
String estNome = (String) request.getAttribute("estNome");
String orgIdentificador = (String) request.getAttribute("orgIdentificador");
String orgNome = (String) request.getAttribute("orgNome");
String rseMatricula = (String) request.getAttribute("rseMatricula");
String serNome = (String) request.getAttribute("serNome");

Map<String, Integer> mapMargemGap = (Map<String, Integer>) request.getAttribute("mapMargemGap");
String csaNome = (String) request.getAttribute("csaNome");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String corNome = (String) request.getAttribute("corNome");
String corCodigo = (String) request.getAttribute("corCodigo");

String svcCodigo = (String) request.getAttribute("svcCodigo");
String orgCodigo = (String) request.getAttribute("orgCodigo");
String svcDescricao = (String) request.getAttribute("svcDescricao");
String descricao = (String) request.getAttribute("descricao");

String ser_cpf = (String) request.getAttribute("ser_cpf");
String categoria = (String) request.getAttribute("categoria");
String cod_cargo = (String) request.getAttribute("cod_cargo");
String cargo = (String) request.getAttribute("cargo");
String codPadrao = (String) request.getAttribute("codPadrao");
String padrao = (String) request.getAttribute("padrao");
String codSubOrgao = (String) request.getAttribute("codSubOrgao");
String subOrgao = (String) request.getAttribute("subOrgao");
String codUnidade = (String) request.getAttribute("codUnidade");
String unidade = (String) request.getAttribute("unidade");
String dataAdmissao = (String) request.getAttribute("dataAdmissao");
String serDataNasc = (String) request.getAttribute("serDataNasc");

boolean serSenhaObrigatoria = (boolean) request.getAttribute("serSenhaObrigatoria");

// Valida informações bancárias
String msgInfBancarias = (String) request.getAttribute("msgInfBancarias");

// Busca as margens para o registro servidor associadas ao serviço
List<TransferObject> lstMargem = (List<TransferObject>) request.getAttribute("lstMargem");

String fileName = (String) request.getAttribute("fileName");

%>
<c:set var="title">
<hl:message key="rotulo.reservar.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
<form action="../v3/reservarMargemGap?acao=incluirReserva&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
   <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.editar.servidor.grid"/></h2>
      </div>
      <div class="card-body">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><hl:message key="mensagem.gap.verifique.informacoes.reserva"/></p>
        <p class="mb-0"><%=serSenhaObrigatoria ? ApplicationResourcesHelper.getMessage("mensagem.gap.informe.senha.servidor", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.gap.informe.senha.servidor.opcional", responsavel)%></p>
        <p class="mb-0"><hl:message key="mensagem.gap.clique.concluir.v4"/></p>
      </div>       
        <dl class="row data-list">
          <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(csaNome)%></dd>
          <%if (!corNome.equals("")) {%>
          <dt class="col-6"><hl:message key="rotulo.correspondente.singular"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(corNome)%></dd>
          <%}%>
          <dt class="col-6"><hl:message key="rotulo.estabelecimento.singular"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(estIdentificador)%> - <%=TextHelper.forHtmlContent(estNome)%></dd>
          <dt class="col-6"><hl:message key="rotulo.orgao.singular"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(orgIdentificador)%> - <%=TextHelper.forHtmlContent(orgNome)%></dd>
          <% if (!codSubOrgao.equals("") || !subOrgao.equals("")) { %>
          <dt class="col-6"><hl:message key="rotulo.servidor.sub.orgao"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(codSubOrgao)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(subOrgao)%></dd>
          <% } %>
          <% if (!codUnidade.equals("") || !unidade.equals("")) { %>
          <dt class="col-6"><hl:message key="rotulo.servidor.unidade"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(codUnidade)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(unidade)%></dd>
          <% } %>
          <dt class="col-6"><hl:message key="rotulo.servidor.singular"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(rseMatricula)%> - <%=TextHelper.forHtmlContent(serNome)%></dd>
          <% if (!serDataNasc.equals("") || !ser_cpf.equals("")) { %>
          <dt class="col-6"><hl:message key="rotulo.gap.data.nascimento.cpf"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(serDataNasc)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(ser_cpf)%></dd>
          <%}%>
          <% if (!dataAdmissao.equals("") || !categoria.equals("")) { %>
          <dt class="col-6"><hl:message key="rotulo.gap.data.admissao.categoria"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(dataAdmissao)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(categoria)%></dd>
          <% } %>
          <% if (!cod_cargo.equals("") || !cargo.equals("")) { %>
          <dt class="col-6"><hl:message key="rotulo.servidor.cargo"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(cod_cargo)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(cargo)%></dd>
          <% } %>
          <% if (!codPadrao.equals("") || !padrao.equals("")) { %>
          <dt class="col-6"><hl:message key="rotulo.servidor.padrao"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(codPadrao)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(padrao)%></dd>
          <% } %>
          <dt class="col-6"><hl:message key="rotulo.servico.singular"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(descricao)%></dd>
          <dt class="col-6"><hl:message key="rotulo.consignacao.identificador"/></dt>
          <dd class="col-6"><hl:htmlinput name="adeIdentificador" type="text" classe="Edit" di="adeIdentificador" size="15" mask="#*40" readonly="true" value="<%=TextHelper.forHtmlAttribute(adeIdentificador)%>" /></dd>
          <dt class="col-6"><hl:message key="rotulo.servico.singular"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(usuLoginResponsavel)%></dd>
          <% if (lstMargem != null && lstMargem.size() > 0) { %>
          <dt class="col-6"><label for="numConta"><hl:message key="mensagem.gap.prestacoes.selecionadas.confirmar" /></label></dt>
          <dd class="col-6">
              <%
                String marCodigo = null;
                Iterator itMargem = lstMargem.iterator();
                TransferObject margem = null;
                while (itMargem.hasNext()) {
                  margem = (TransferObject) itMargem.next();
                  marCodigo = margem.getAttribute(Columns.MAR_CODIGO).toString();
                  if (mapMargemGap.get(marCodigo) != null) {
                  %>
                    <div class="row">
                      <input type="checkbox" class="form-check-input ml-1" name="incMargem" id="incMargem" value="<%=TextHelper.forHtmlAttribute((margem.getAttribute(Columns.MAR_CODIGO)))%>" checked disabled />
                      <label for="incMargem" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.moeda"/> <%=TextHelper.forHtmlContent( margem.getAttribute(Columns.ADE_VLR) )%> : <%=TextHelper.forHtmlContent( DateHelper.toPeriodString((java.util.Date) margem.getAttribute(Columns.ADE_ANO_MES_INI)) )%></label>
                    </div>
                  <%}%>
                <%}%>
          </dd>
          <%}%>
          <hl:senhaServidorv4 senhaObrigatoria="<%=(String)(serSenhaObrigatoria ? "true" : "false")%>"
                              senhaParaAutorizacaoReserva="true"
                              nomeCampoSenhaCriptografada="senhaRSA"
                              rseCodigo="<%=rseCodigo%>"
                              nf="btnEnvia"
                              classe="form-control" />
        </dl>
        <% if (!responsavel.isSer()) { %>
          <%
             String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
             if (!TextHelper.isNull(mascaraLogin)) {
          %>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="serLogin"><hl:message key="rotulo.usuario.autorizacao.servidor.singular"/><%=serSenhaObrigatoria ? "" : " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel)%></label>
              <hl:htmlinput name="serLogin" type="text" classe="form-control" di="serLogin" size="15" mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>" />    
            </div>
          </div>
          <% } %>
        <% } %>
      </div>
    </div>
            
    <hl:htmlinput type="hidden" name="CNV_CODIGO"      value="<%=TextHelper.forHtmlAttribute(cnvCodigo)%>" />
    <hl:htmlinput type="hidden" name="SVC_CODIGO"      value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>" />
    <hl:htmlinput type="hidden" name="RSE_CODIGO"      value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" />
    <hl:htmlinput type="hidden" name="ORG_CODIGO"      value="<%=TextHelper.forHtmlAttribute(orgCodigo)%>" />
    <hl:htmlinput type="hidden" name="CSA_CODIGO"      value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>" />
    <% if (!corCodigo.equals("")) {%>
    <hl:htmlinput type="hidden" name="COR_CODIGO"      value="<%=TextHelper.forHtmlAttribute(corCodigo)%>" />
    <% } %>
    <hl:htmlinput type="hidden" name="dataNasc"       value="<%=TextHelper.forHtmlAttribute(serDataNasc)%>" />
    <hl:htmlinput type="hidden" name="numBanco"       value="<%=TextHelper.forHtmlAttribute(numBanco)%>" />
    <hl:htmlinput type="hidden" name="numAgencia"     value="<%=TextHelper.forHtmlAttribute(numAgencia)%>" />
    <hl:htmlinput type="hidden" name="numConta"       value="<%=TextHelper.forHtmlAttribute(numConta)%>" />
    <div class="btn-action">
      <a class="btn btn-outline-danger" HREF="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
      <% if (SynchronizerToken.isTokenValid(request)) { %>
      <a class="btn btn-primary" ID="btnEnvia" HREF="#no-back" onClick="reservarGap(); return false;"><hl:message key="rotulo.botao.concluir"/></a>
      <%  } else { %>
      <a class="btn btn-outline-danger" HREF="#no-back" onClick="insereNovo();"><hl:message key="rotulo.botao.confirmar"/></a>
      <% } %>
    </div>
</form>
</c:set>
<c:set var="javascript">
<script src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
<script src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
<script src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
  var f0;
  
  function formLoad() {
    if (f0.serLogin != null) {
      f0.serLogin.focus();
    } else if (f0.senha != null) {
      f0.senha.focus();
    }
  }

  function insereNovo() {
    if (confirm('<hl:message key="mensagem.gap.confirmacao.reserva.ja.inserida"/>')) {
      postData('../v3/reservarMargem?acao=iniciar');
    }
  }
  
  function campos() {
    if (<%=(boolean)serSenhaObrigatoria%> && f0.serLogin != null && f0.serLogin.value == '') {
      alert('<hl:message key="mensagem.informe.ser.usuario"/>');
      f0.serLogin.focus();
      return false;
    }
    if (<%=(boolean)serSenhaObrigatoria%> && f0.senha.value == '') {
      alert('<hl:message key="mensagem.informe.ser.senha"/>');
      f0.senha.focus();
      return false;
    }
  
    if (f0.senha != null && trim(f0.senha.value) != '') {
      f0.senhaRSA.value = criptografaRSA(f0.senha.value);
      f0.senha.value = '';
    }
    return true;
  }
  
  function enableAll() {
    if (f0.incMargem.length == undefined) {
      f0.incMargem.disabled = false;
    } else {
      for (i = 0; i < f0.incMargem.length; i++) {
        f0.incMargem[i].disabled = false;
      }
    }
  }
  
  function reservarGap() {
    if (campos()) { 
      enableAll();
      f0.submit();
    }
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
