<%--
* <p>Title: validarDigitalServidor_v4.jsp</p>
* <p>Description: Tela para validação de digital de servidor</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String rotuloBotaoCancelar = ApplicationResourcesHelper.getMessage("rotulo.botao.cancelar", responsavel);
String rotuloBotaoPesquisar = ApplicationResourcesHelper.getMessage("rotulo.botao.pesquisar", responsavel);
String rseCodigo = !TextHelper.isNull(request.getAttribute("rseCodigo")) ? (String) request.getAttribute("rseCodigo") : (String) request.getAttribute("RSE_CODIGO");

// se adeNumero não é relevante para a pesquisa, enviar esse parâmetro como true para esta página 
boolean omitirAdeNumero = TextHelper.isNull(request.getAttribute("omitirAdeNumero")) ? false : (Boolean) request.getAttribute("omitirAdeNumero");

// Parâmetro de obrigatoriedade de CPF e Matrícula
boolean requerMatriculaCpf = (request.getAttribute("requerMatriculaCpf") != null);
//Configura exibição do campo de senha
boolean exibirCampoSenha = request.getAttribute("exibirCampoSenha") != null || request.getAttribute("exibirCampoSenhaAutorizacao") != null;
boolean senhaParaAutorizacaoReserva = request.getAttribute("exibirCampoSenhaAutorizacao") != null;
boolean senhaObrigatoria = senhaParaAutorizacaoReserva || request.getAttribute("senhaObrigatoriaConsulta") != null;

boolean skipHistory = TextHelper.isNull(request.getAttribute("_skip_history_")) ? false : (Boolean) request.getAttribute("_skip_history_");

%>
<c:set var="title">
   <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>" method="post" name="form1">
    <%= SynchronizerToken.generateHtmlToken(request) %>
    <hl:htmlinput type="hidden" name="acao" value="<%= TextHelper.forHtmlAttribute(request.getAttribute("proximaOperacao") != null ? request.getAttribute("proximaOperacao").toString() : "pesquisarServidor") %>" />
    <hl:htmlinput type="hidden" name="TIPO_LISTA" value="pesquisa"/>
    <hl:htmlinput type="hidden" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>"/>
    <% if (skipHistory) { %>
    <hl:htmlinput type="hidden" name="_skip_history_" value="true"/>
    <% } %>
    <c:forEach items="${requestParams}" var="paramName">   
       <input type="hidden" name="${fl:forHtmlAttribute(paramName)}" value="${fl:forHtmlAttribute(param[paramName])}"/>
    </c:forEach>
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="26">
            <use xlink:href="../img/sprite.svg#i-consultar"></use></svg>
        </span>
        <h2 class="card-header-title"><hl:message key="mensagem.pesquisa.titulo"/></h2>
      </div>
      <div class="card-body">
        <% if (!responsavel.isSer()) { %>
          <fieldset>
            <h3 class="legend">
              <span><hl:message key="rotulo.consultar.consignacao.dados.servidor"/></span>
            </h3>

            <% if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ADE_VLR"))) { %>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="ADE_VLR"><hl:message key="rotulo.consultar.margem.valor.parcela"/></label>
                <hl:htmlinput name="ADE_VLR" 
                              di="ADE_VLR" 
                              placeHolder="<%= ApplicationResourcesHelper.getMessage("rotulo.consultar.margem.valor.parcela.placeholder", responsavel) %>"
                              type="text" 
                              classe="form-control"
                              mask="#F11" 
                              size="8"
                              value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_VLR"))%>"
                              disabled="true" 
                 />            
              </div>  
            </div>
            <% } %>

            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <hl:campoMatriculav4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>' disabled="true" />
              </div>
            </div>

            <% if (request.getAttribute("exibirCampoSenha") != null || request.getAttribute("exibirCampoSenhaAutorizacao") != null) { %>
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <hl:campoCPFv4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>' classe="form-control" disabled="true" />
                </div>
              </div>
              <%
                 String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
                 if (!TextHelper.isNull(mascaraLogin)) {
              %>
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <label for="serLogin"><hl:message key="rotulo.usuario.servidor.singular"/>&nbsp;<hl:message key="rotulo.campo.opcional"/></label>
                  <hl:htmlinput name="serLogin"
                                di="serLogin"
                                type="text"
                                classe="Edit"
                                size="8"
                                mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>"
                  />
                </div>
              </div>
              <% 
                 } 
              %>
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <hl:senhaServidorv4 senhaObrigatoria="<%=String.valueOf(senhaObrigatoria)%>" 
                                    senhaParaAutorizacaoReserva="<%=(String)((request.getAttribute("exibirCampoSenhaAutorizacao") != null) ? "true" : "false")%>"
                                    nomeCampoSenhaCriptografada="serAutorizacao"
                                    rseCodigo="<%=rseCodigo%>"
                                    nf="btnPesquisar"
                                    classe="form-control"
                                    separador2pontos="false" 
                                    comTagDD="false"/>
                </div>
              </div>
            <% } else { %>
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <hl:campoCPFv4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>' classe="form-control" nf="btnPesquisar"/>
                </div>
              </div>
            <% } %>
            
          </fieldset>
        <% } %>

      </div>
    </div>
    <%-- Inclusão Avançada --%>
    <% if (responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO) && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) { %>
        <%@ include file="../reservarMargem/incluirCamposInclusaoAvancada_v4.jsp" %>
    <% } %>
  </form>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="if(validaSubmit()){pesquisar(); return false;} else { return false;}"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><%=TextHelper.forHtmlAttribute(rotuloBotaoPesquisar)%></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/listutils.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>
  
  <hl:senhaServidorv4 senhaObrigatoria="<%=String.valueOf(senhaObrigatoria)%>" 
                  senhaParaAutorizacaoReserva="<%=(String)((request.getAttribute("exibirCampoSenhaAutorizacao") != null) ? "true" : "false")%>"
                  nomeCampoSenhaCriptografada="serAutorizacao"
                  rseCodigo="<%=rseCodigo%>"
                  nf="btnPesquisar"
                  classe="form-control"
                  separador2pontos="false" 
                  comTagDD="false"
                  scriptOnly="true"/>
<script type="text/JavaScript">
function formLoad() {
  focusFirstField();
  hideEmptyFieldSet();  
}

function validarCamposOpcionais() {
  var senhaObrigatoria = <%=TextHelper.forJavaScriptBlock(senhaObrigatoria)%>;

  if (f0.senha != null && f0.senha.value == '' && senhaObrigatoria) {
    alert("<hl:message key="mensagem.informe.ser.senha"/>");
    f0.senha.focus();
    return false;
  }
  return true;
}

function validarCamposObrigatorios() {
  var requerAmbos = <%=TextHelper.forJavaScriptBlock(requerMatriculaCpf)%>;

  if (requerAmbos) {
    if (f0.RSE_MATRICULA != null && f0.RSE_MATRICULA.value == '') {
      f0.RSE_MATRICULA.focus();
      alert(mensagem('mensagem.informe.matricula'));
      return false;
    } else if (f0.SER_CPF != null && f0.SER_CPF.value == '') {
      f0.SER_CPF.focus();
      alert(mensagem('mensagem.informe.cpf'));
      return false;
    }
  } else {
    if ((f0.RSE_MATRICULA == null || f0.RSE_MATRICULA.value == '') &&
        (f0.SER_CPF == null || f0.SER_CPF.value == '')) {
      if (f0.RSE_MATRICULA != null) {
        f0.RSE_MATRICULA.focus();
        alert(mensagem('mensagem.informe.matricula'));
        return false;
      } else if (f0.SER_CPF != null) {
        f0.SER_CPF.focus();
        alert(mensagem('mensagem.informe.cpf'));
        return false;
      }
    }
  }
  
  if (f0.SER_CPF != null && f0.SER_CPF.value != '' && !CPF_OK(extraiNumCNPJCPF(f0.SER_CPF.value))) {
    f0.SER_CPF.focus();
    return false;
  }
  
  return true;
}

function pesquisar() {
  if (validarCamposObrigatorios() && validarCamposOpcionais()) {
    if (f0.senha != null && f0.senha.value != '') {
      CriptografaSenha(f0.senha, f0.serAutorizacao, false);
    }
    f0.submit();
  }
}

function validaSubmit()
{  
  if(typeof vfRseMatricula === 'function')
    return vfRseMatricula(true);
    
  else
    return true; 
}

f0 = document.forms["form1"];

window.onload = formLoad;
</script>
<% if (!responsavel.isSer()) { %>
  <hl:campoMatriculav4 scriptOnly="true"/>
<% } %> 
</c:set>
  <t:page_v4>
      <jsp:attribute name="header">${title}</jsp:attribute>
      <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
      <jsp:attribute name="javascript">${javascript}</jsp:attribute>
      <jsp:body>${bodyContent}</jsp:body>
  </t:page_v4>
