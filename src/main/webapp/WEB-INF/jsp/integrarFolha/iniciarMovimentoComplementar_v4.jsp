<%--
* <p>Title: iniciarMovimentoComplementar_v4.jsp</p>
* <p>Description: pesquisa de consignação para exportação complementar</p>
* <p>Copyright: Copyright (c) 2002-2020</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
%>
<c:set var="title">
   <hl:message key="rotulo.folha.exportar.movimento.financeiro.complementar"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form action="<%=SynchronizerToken.updateTokenInURL("../v3/exportarMovimentoComplementar?acao=pesquisar", request) %>" method="post" name="form1" enctype="multipart/form-data">
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="26">
            <use xlink:href="../img/sprite.svg#i-consultar"></use></svg>
        </span>
        <h2 class="card-header-title"><hl:message key="mensagem.pesquisa.titulo"/></h2>
      </div>
      <div class="card-body">
        <fieldset>
          <h3 class="legend">
            <span><hl:message key="rotulo.exportar.movimento.financeiro.complementar.dados.consignacoes"/></span>
          </h3>

          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="arquivo"><hl:message key="rotulo.upload.arquivo.arquivo"/></label>
              <input type="file" class="form-control" id="arquivo" name="FILE1">
            </div>
          </div>

          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="ADE_NUMERO"><hl:message key="rotulo.consignacao.numero"/></label>
              <hl:htmlinput name="ADE_NUMERO"
                            di="ADE_NUMERO"
                            type="text"
                            classe="form-control w-100"
                            mask="#D20"
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_NUMERO"))%>"
                            placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero", responsavel)%>'
              />
            </div>
            <div class="form-group col-sm-12 col-md-1 mt-4">
              <a id="adicionaAdeLista" class="btn btn-primary w-50" href="javascript:void(0);" onClick="adicionaNumero()" aria-label='<hl:message key="mensagem.inserir.ade.numero.clique.aqui"/>'>
                <svg width="15"><use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
              </a>
              <a id="removeAdeLista" class="btn btn-primary w-50 mt-1" href="javascript:void(0);" onClick="removeNumero()" aria-label='<hl:message key="mensagem.remover.ade.numero.clique.aqui"/>' style="display: none">
                <svg width="15"><use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
              </a>
            </div>
            <div id="adeLista" class="form-group col-sm-12 col-md-5 mt-4" style="display: none">
              <select class="form-control form-select w-100" id="ADE_NUMERO_LIST" name="ADE_NUMERO_LIST" multiple="multiple" size="6"></select>
            </div>
          </div>
        </fieldset>

        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <div class="alert alert-info">
              <p class="mb-0"><hl:message key="mensagem.ajuda.exportar.movimento.financeiro.complementar"/></p>
            </div>
          </div>
        </div>
      </div>
    </div>

  </form>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=SynchronizerToken.updateTokenInURL("../v3/carregarPrincipal", request)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="pesquisar(); return false;"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/listutils.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
f0 = document.forms["form1"];

function formLoad() {
  focusFirstField();
}

function validarCamposObrigatorios() {
  if (f0.ADE_NUMERO != null) {
    if (f0.ADE_NUMERO.value != '') {
      return true;
    } else if (f0.ADE_NUMERO_LIST != null && f0.ADE_NUMERO_LIST.length > 0) {
      return true;
    }
  }
  if (f0.FILE1 != null && f0.FILE1.value) {
    return true;
  }
  
  return false;
}

function pesquisar() {
  if (validarCamposObrigatorios()) {
    selecionarTodosItens('ADE_NUMERO_LIST');
    f0.submit();
  } else {
    alert('<hl:message key="mensagem.ajuda.exportar.movimento.financeiro.complementar"/>');
  }
}

function adicionaNumero() {
    var ade = document.getElementById('ADE_NUMERO').value;

    if (ade != '' && (/\D/.test(ade) || ade.length > 20)) {
        alert('<hl:message key="mensagem.erro.ade.numero.invalido"/>');
        return;
    }
    
    if (document.getElementById('ADE_NUMERO').value != '') {
      document.getElementById('adeLista').style.display = '';
      document.getElementById('removeAdeLista').style.display = '';
      insereItem('ADE_NUMERO', 'ADE_NUMERO_LIST');
    }
}

function removeNumero() {
    removeDaLista('ADE_NUMERO_LIST');
    if (document.getElementById('ADE_NUMERO_LIST').length == 0) {
        document.getElementById('adeLista').style.display = 'none';
        document.getElementById('removeAdeLista').style.display = 'none';
    }
}

window.onload = formLoad;
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
