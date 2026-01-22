<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
  AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
  String csaCodigo = (String) request.getAttribute("csaCodigo");
  String tipo = (String) request.getAttribute("tipo");
  boolean exibeCaptcha = (boolean) request.getAttribute("exibeCaptcha");
  boolean exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
  boolean exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
  String btnVoltar = (String) request.getAttribute("btnVoltar");
  String entidade = "csa";
%>
<c:set var="title">
  <hl:message key="rotulo.manutencao.consignataria.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <% if(responsavel.temPermissao(CodedValues.FUN_UPLOAD_ANEXO_CSA)){ %>
  <form name="form1" method="POST" action="../v3/uploadAnexosConsignataria?acao=upload&csaCodigo=<%=csaCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>" enctype="multipart/form-data">
    <div class="row">
      <div class="form-group col-sm-12">
        <label for="arquivo"><hl:message key="rotulo.upload.arquivo.arquivo"/></label>
        <input type="file" class="form-control" id="arquivo" name="FILE1" onChange="vf_nome_arquivo();">
      </div>
    </div>
    <div class="row">
      <div class="form-group col-sm-5">
        <label for="captcha"><hl:message key="rotulo.captcha.codigo"/>:</label>
        <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
      </div>
      <div class="form-group col-sm-6">
        <div class="captcha">
          <% if (exibeCaptcha) { %>
          <img src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
          <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>'/></a>
          <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
             data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
             data-original-title=<hl:message key="rotulo.ajuda" />>
            <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />'>
          </a>
          <% } else if (exibeCaptchaAvancado) { %>
          <hl:recaptcha />
          <% } else if (exibeCaptchaDeficiente) {%>
          <div id="divCaptchaSound"></div>
          <a href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
          <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
          <% } %>
        </div>
      </div>
    </div>
    <input name="csaCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
  </form>
  <div class="btn-action">
    <button class="btn btn-primary" type="submit" onClick="if(vf_upload_arquivos()){ f0.submit();} return false"><svg width="17"><use xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/></button>
  </div>
  <% } %>
  <% if(responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIAS) || responsavel.temPermissao(CodedValues.FUN_UPLOAD_ANEXO_CSA) || responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIA)) { %>
  <div class="card">
    <div class="card-header hasIcon">
      <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
      <h2 class="card-header-title"><hl:message key="rotulo.anexo.csa.disponiveis"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
        <tr>
          <th scope="col"><hl:message key="rotulo.lote.nome"/></th>
          <th scope="col"><hl:message key="rotulo.lote.tamanho.abreviado"/></th>
          <th scope="col"><hl:message key="rotulo.lote.data"/></th>
          <th scope="col"><hl:message key="rotulo.acoes"/></th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
          <c:when test="${empty arquivos}">
            <tr>
              <td colspan='7'><hl:message key="mensagem.erro.nenhum.arquivo.encontrado"/></td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach items="${arquivos}" var="arquivo">
              <tr>
                <td>${fl:forHtmlContent(arquivo.nomeOriginal)}</td>
                <td>${fl:forHtmlContent(arquivo.tamanho)}</td>
                <td>${fl:forHtmlContent(arquivo.data)}</td>
                <td>
                  <div class="actions">
                    <div class="actions">
                      <div class="dropdown">
                        <a class="dropdown-toggle ico-action" href="#" role="button"
                           id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true"
                           aria-expanded="false">
                          <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title=""
                              data-original-title="<hl:message key='rotulo.mais.acoes'/>"
                              aria-label="<hl:message key='rotulo.mais.acoes'/>">
                          <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span><hl:message key="rotulo.botao.opcoes"/>
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right"
                             aria-labelledby="userMenu">
                          <a class="dropdown-item" href="#"
                             onClick="fazDownload('${fl:forJavaScriptAttribute(arquivo.nomeOriginal)}', '<%=TextHelper.forJavaScript(tipo)%>', '<%=TextHelper.forJavaScript(entidade)%>'); return false;"
                             ><hl:message
                                  key="rotulo.acoes.download"/></a>
                          <%if(responsavel.temPermissao(CodedValues.FUN_UPLOAD_ANEXO_CSA)){ %>
                          <a class="dropdown-item" href="#"
                             onClick="excluirArquivo('${fl:forJavaScriptAttribute(arquivo.nomeOriginal)}', '${fl:forJavaScriptAttribute(arquivo.nomeOriginal)}'); return false;"
                             ><hl:message
                                  key="rotulo.acoes.excluir"/></a>
                          <% } %>
                        </div>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
        </tbody>
        <tfoot>
        <tr>
          <td colspan="4">
            <hl:message key="rotulo.listar.arquivos.download.anexo.consignataria.titulo.paginacao"/>
            <span class="font-italic">
                    <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}"
                                arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
                  </span>
          </td>
        </tr>
        </tfoot>
      </table>
    </div>
    <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>
  </div>
  <% } %>
  <div class="btn-action">
    <a class="btn btn-outline-danger" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(btnVoltar)%>');"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
  <div class="modal fade" id="modalAguarde" data-focus="false" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog-upload modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-body">
          <div class="row">
            <div class="col-md-12 d-flex justify-content-center">
              <img src="../img/loading.gif" class="loading">
            </div>
            <div class="col-md-12">
              <div class="modal-body"><span><hl:message key="mensagem.upload.generico.aguarde"/></span></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <% if (exibeCaptchaAvancado) { %>
  <script src='https://www.google.com/recaptcha/api.js'></script>
  <% } %>
  <script type="text/JavaScript">
    var f0 = document.forms[0];

    if (document.getElementById('captcha') != 'null' && document.getElementById('captcha') != null && document.getElementById('captcha') != 'undefined') {
      document.getElementById('captcha').blur();
    }

    function fazDownload(nome, tipo, entidade) {
      postData('../v3/downloadArquivo?arquivo_nome=' + nome + '&tipo=' + tipo + '&entidade=' + entidade + '&csaCodigo=<%=csaCodigo%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
    }
    function vf_upload_arquivos() {

      var controles = '';
      var msgs = '';
      var ok = ValidaCampos(controles, msgs);
      if (ok) {
        $('#modalAguarde').modal({
          backdrop: 'static',
          keyboard: false
        });
      }
      return ok;

    }
    function excluirArquivo(arq, path){
      var msg = '<hl:message key="mensagem.confirmacao.exclusao.lote"/>'.replace("{0}", arq);
      var j = '../v3/uploadAnexosConsignataria?acao=excluirArquivo&arquivo_nome=' + encodeURIComponent(path) + '&ext=exc' + '&csaCodigo=<%=csaCodigo%>' + '&tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>&entidade=<%=TextHelper.forJavaScriptBlock(entidade)%>&<%=SynchronizerToken.generateToken4URL(request)%>';

      if (msg != '') {
        if (confirm(msg)) {
          postData(j);
        } else {
          return false;
        }
      } else {
        postData(j);
      }
      return true;
    }

    function formFullLoad(){
      <% if (exibeCaptchaDeficiente) {%>
      montaCaptchaSom();
      <% } %>
    }

    window.onload = formFullLoad;
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
