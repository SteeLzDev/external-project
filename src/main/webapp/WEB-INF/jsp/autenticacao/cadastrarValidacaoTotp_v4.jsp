<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.GoogleAuthenticatorHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.values.OperacaoValidacaoTotpEnum" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  UsuarioTransferObject usuario = (UsuarioTransferObject) request.getAttribute("usuario");
  
  String linkRet = JspHelper.verificaVarQryStr(request, "linkRet");
  String mensagemCadastroTotp = (String) request.getAttribute("mensagemCadastroTotp");
  String mensagemCadastroTotpPopUp = (String) request.getAttribute("mensagemCadastroTotpPopUp");
  String mensagemTotpCadastrado = (String) request.getAttribute("mensagemTotpCadastrado");
  String mensagemTotpRemoverCliqueAqui = (String) request.getAttribute("mensagemTotpRemoverCliqueAqui");
  String mensagemTotpCadastrarCliqueAqui = (String) request.getAttribute("mensagemTotpCadastrarCliqueAqui");
  String usuChaveValidacaoTotp = (String) request.getAttribute("usuChaveValidacaoTotp");
  Boolean possuiChaveCadastrada = (Boolean) request.getAttribute("possuiChaveCadastrada");
  OperacaoValidacaoTotpEnum operacoesValidacaoTotp = (OperacaoValidacaoTotpEnum) request.getAttribute("operacoesValidacaoTotp");
%>

<c:set var="javascript">
    <script type="text/JavaScript">
  function formLoad() {
      if(f0.senha2aAutorizacao != null) {
        f0.senha2aAutorizacao.focus();
      }
    }
    
    function gerar() {
      if (confirm('<%=TextHelper.forJavaScriptBlock(mensagemCadastroTotpPopUp)%>')) {
        postData('<%=TextHelper.forJavaScriptBlock("../v3/cadastrarValidacaoTotp?acao=gerar")%>');
      }
      return false;
    }
    
    function cadastrar() {
      if (confirm('<%=TextHelper.forJavaScriptBlock(mensagemCadastroTotpPopUp)%>')) {
        if (validaSenha()) {
          f0.action="../v3/cadastrarValidacaoTotp?acao=cadastrar";
            f0.submit();
        }
      }
      return false;
    }
    
    function remover() {
      if (confirm('<%=TextHelper.forJavaScriptBlock(mensagemCadastroTotpPopUp)%>')) {
          if (validaSenha()) {
          f0.action="../v3/cadastrarValidacaoTotp?acao=remover";
          f0.submit();
          }
        }
        return false;
      }
    
    function validaSenha() {
      if (f0.senha2aAutorizacao != null && trim(f0.senha2aAutorizacao.value) == '') {
        alert('<hl:message key="mensagem.totp.informe.codigo"/>');
        f0.senha2aAutorizacao.focus();
        return false;
      }
      if (f0.senha2aAutorizacao != null && trim(f0.senha2aAutorizacao.value) != '') {
        if(!CriptografaSenha(f0.senha2aAutorizacao, f0.senhaRSA, false)) {
          return false;
        }
      }
      f0.timeInMilliseconds.value = (new Date()).getTime();
      return true;
    }
  
    f0 = document.forms[0];
    window.onload = formLoad;
  </script>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.totp.titulo.pagina"/>
</c:set>
<head>
  <link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
</head>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.codigo.validacao.totp.singular"/></h2>
    </div>
    <div class="card-body">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><%=TextHelper.forHtmlContent(!possuiChaveCadastrada ? mensagemCadastroTotp : mensagemTotpCadastrado)%></p>
      </div>
      <form method="post" action="../v3/cadastrarValidacaoTotp" onsubmit="return <%if (!TextHelper.isNull(session.getAttribute(GoogleAuthenticatorHelper.CHAVE_VALIDACAO))){%>cadastrar();<%} else if (!possuiChaveCadastrada) {%>gerar();<%} else {%>remover();<%}%>">
        <input type="hidden" id="timeInMilliseconds" name="timeInMilliseconds" value="">
        <dl class="row data-list firefox-print-fix">
          <dt class="col-6"><hl:message key="rotulo.totp.nome"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(usuario.getUsuNome())%></dd>
          <dt class="col-6"><hl:message key="rotulo.totp.usuario"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(usuario.getUsuLogin())%></dd>
          <dt class="col-6"><hl:message key="rotulo.totp.cpf"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(usuario.getUsuCPF()) ? usuario.getUsuCPF() : ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel))%></dd>
          <dt class="col-6"><hl:message key="rotulo.totp.email"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(usuario.getUsuEmail()) ? usuario.getUsuEmail() : ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel))%></dd>
          <dt class="col-6"><hl:message key="rotulo.totp.telefone"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(usuario.getUsuTel()) ? usuario.getUsuTel() : ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel))%></dd>

          <% if (possuiChaveCadastrada || !TextHelper.isNull(session.getAttribute(GoogleAuthenticatorHelper.CHAVE_VALIDACAO))) { %>
          <dt class="col-6 mt-3"><label id="operacoesValidacaoTotpLabel"><hl:message key="rotulo.totp.operacoes"/></label></dt>
          <dd class="col-6">
            <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="operacoesValidacaoTotpLabel">
              <input class="form-check-input ml-1" type="radio" name="operacoesValidacaoTotp" id="operacoesValidacaoTotp_1" value="<%= OperacaoValidacaoTotpEnum.AUTORIZACAO_OPERACAO_SENSIVEL.getCodigo() %>" <%=(String) (OperacaoValidacaoTotpEnum.AUTORIZACAO_OPERACAO_SENSIVEL.equals(operacoesValidacaoTotp) ? " checked " : "") %><%=(String) (possuiChaveCadastrada ? " disabled " : "") %>>
              <label class="form-check-label pr-3 labelSemNegrito" for="operacoesValidacaoTotp_1">
               <hl:message key="rotulo.totp.operacoes.autorizacao"/>
              </label>
            </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input ml-1" type="radio" name="operacoesValidacaoTotp" id="operacoesValidacaoTotp_2" value="<%= OperacaoValidacaoTotpEnum.AUTENTICACAO_SISTEMA.getCodigo() %>" <%=(String) (OperacaoValidacaoTotpEnum.AUTENTICACAO_SISTEMA.equals(operacoesValidacaoTotp) ? " checked " : "") %><%=(String) (possuiChaveCadastrada ? " disabled " : "") %>>
              <label class="form-check-label labelSemNegrito" for="operacoesValidacaoTotp_2">
                <hl:message key="rotulo.totp.operacoes.autenticacao"/>
              </label>
            </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input ml-1" type="radio" name="operacoesValidacaoTotp" id="operacoesValidacaoTotp_3" value="<%= OperacaoValidacaoTotpEnum.AMBOS.getCodigo() %>" <%=(String) (OperacaoValidacaoTotpEnum.AMBOS.equals(operacoesValidacaoTotp) ? " checked " : "") %><%=(String) (possuiChaveCadastrada ? " disabled " : "") %>>
              <label class="form-check-label labelSemNegrito" for="operacoesValidacaoTotp_3">
                <hl:message key="rotulo.totp.operacoes.ambos"/>
              </label>
            </div>
          </dd>
            <dt class="col-6 mt-3"><label for="senha2aAutorizacao"><hl:message key="rotulo.totp.codigo.seguranca"/></label></dt>
            <dd class="col-6">
                <hl:htmlpassword classe="form-control col-md-3" size="15" name="senha2aAutorizacao" 
                     cryptedfield="senhaRSA" cryptedPasswordFieldName="segundaSenha" 
                     nf="btnEnvia"/>
            </dd>
          <% } %>
          
          <% if (!TextHelper.isNull(session.getAttribute(GoogleAuthenticatorHelper.CHAVE_VALIDACAO))) { %>
            <dt class="col-6 mt-3"><label for="qrCode"><hl:message key="rotulo.totp.codigo.qr"/></label></dt>
            <dd class="col-6"><img class="w-50" id="qrCode" src="../img/qrcode.jsp" alt="<hl:message key="rotulo.totp.codigo.qr"/>" /></dd>
          </dl>
        </form>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger text-danger" onClick="postData('../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary collapsed text-white" id="btnEnvia" onClick="cadastrar();" data-bs-toggle="collapse" href="#faq1" aria-expanded="false" aria-controls="faq1" title="<%=TextHelper.forHtmlAttribute(mensagemTotpCadastrarCliqueAqui)%>">
      <svg width="17"><use xlink:href="#i-confirmar"></use></svg>
      <hl:message key="rotulo.botao.confirmar"/>
    </a>
  </div>

          <% } else if (!possuiChaveCadastrada) { %>
          </dl>
        </form>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger text-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary collapsed text-white" id="btnEnvia" onClick="gerar();" title="<%=TextHelper.forHtmlAttribute(mensagemTotpCadastrarCliqueAqui)%>">
      <svg width="17"><use xlink:href="#i-confirmar"></use></svg>
      <hl:message key="rotulo.botao.gerar"/>
    </a>
  </div>

          <% } else if (!TextHelper.isNull(usuChaveValidacaoTotp)) { %>
          </dl>
        </form>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger text-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary collapsed text-white" id="btnEnvia" onClick="remover();" title="<%=TextHelper.forHtmlAttribute(mensagemTotpRemoverCliqueAqui)%>">
      <hl:message key="rotulo.botao.remover"/>
    </a>
  </div>
          <% } %>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
