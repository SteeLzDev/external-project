<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="title">
   <hl:message key="rotulo.folha.download.arquivos.integracao"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="page-title">
    <div class="row">
      <div class="col-sm mb-2">
        <div class="float-end">
            <a class="btn btn-primary" aria-label="<hl:message key="rotulo.download.arquivos.integracao.tooltip.manual.ajuda"/>" href="#" onClick="downloadManual(); return false;">
                <svg width="17"><use xlink:href="#i-download"></use></svg>
                <span data-bs-toggle="tooltip" title="<hl:message key="rotulo.download.arquivos.integracao.tooltip.manual.ajuda"/>">
                    <hl:message key="rotulo.download.arquivos.dashboard.download.manual"/>
                </span>
            </a>
        </div>
      </div>
    </div>
  </div>
  <div class="card">
    <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
        <h2 class="card-header-title"><hl:message key="mensagem.folha.arquivos.integracao.disponiveis.download"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.folha.nome"/></th>
            <th scope="col"><hl:message key="rotulo.folha.tamanho.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.folha.data"/></th>
            <c:if test="${responsavel.cseSup}">
              <th scope="col"><hl:message key="rotulo.orgao.singular"/>-<hl:message key="rotulo.estabelecimento.abreviado"/></th>
            </c:if>
            <th scope="col" width="10%"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty arquivos}">
              <tr>
                <td colspan="7"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach items="${arquivos}" var="arquivo">   
                <tr>
                  <td>${fl:forHtmlContent(arquivo.nomeOriginal)}</td>
                  <td>${fl:forHtmlContent(arquivo.tamanho)}</td>
                  <td>${fl:forHtmlContent(arquivo.data)}</td>
                  <c:if test="${responsavel.cseSup}">
                    <td>${fl:forHtmlContent(arquivo.entidade)}</td>
                  </c:if>
                  <td>
                    <div class="actions">
                      <div class="dropdown">
                        <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" aria-label='<hl:message key="rotulo.botao.opcoes"/>' title='<hl:message key="rotulo.botao.opcoes"/>'>
                              <svg><use xlink:href="../img/sprite.svg#i-engrenagem"></use></svg>
                            </span>
                            <hl:message key="rotulo.botao.opcoes"/>
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                          <c:if test="${not empty arquivo.conversorTexto && usuarioPodeConverterArqInt}">
                            <a class="dropdown-item" href="#no-back" onClick="doIt('c', '${fl:forJavaScript(arquivo.nomeOriginal)}', '${fl:forJavaScript(arquivo.nome)}'); return false;" aria-label='<hl:message key="mensagem.folha.converter.arquivo.nome.clique.aqui" arg0="${arquivo.nome}"/>'>${fl:forHtmlContent(arquivo.conversorTexto)}</a>
                          </c:if>
                          <c:if test="${usuarioPodeRemoverArqInt}">
                          <a class="dropdown-item" href="#no-back" onClick="doIt('e', '${fl:forJavaScript(arquivo.nomeOriginal)}', '${fl:forJavaScript(arquivo.nome)}'); return false;" aria-label='<hl:message key="mensagem.folha.excluir.arquivo.nome.clique.aqui" arg0="${arquivo.nome}"/>'><hl:message key="rotulo.acoes.excluir"/></a>
                          </c:if>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/liberarMovimento?acao=liberarMovimento&arquivo_nome='+encodeURIComponent('${fl:forJavaScriptAttribute(arquivo.nome)}') + '&tipo=${fl:forJavaScriptAttribute(tipo)}&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;" aria-label='<hl:message key="mensagem.folha.download.arquivo.nome.clique.aqui" arg0="${arquivo.nome}"/>'><hl:message key="rotulo.acoes.download"/></a>
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
                <td colspan="5"><hl:message key="rotulo.paginacao.titulo.download.arq.integracao"/>
                  <span class="font-italic"> - 
                    <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
                  </span>
              </td>
            </tr>
          </tfoot>
      </table>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
function doIt(opt, arq, path) {
  var msg = '', j;
  if (opt == 'e') {
    msg = '<hl:message key="mensagem.folha.confirmacao.remover.arquivo.nome"/>'.replace('{0}',arq);
    j = '../v3/excluirArquivo?acao=iniciar&ext=exc&arquivo_nome=' + encodeURIComponent(path) + '&tipo=${fl:forJavaScriptBlock(tipo)}';
  } else if (opt == 'c') {
    msg = '<hl:message key="mensagem.folha.confirmacao.conversao.arquivo.nome"/>'.replace('{0}',arq);
	j = '../v3/converterArquivo?acao=iniciar&arquivo_nome=' + encodeURIComponent(path) + '&tipo=${fl:forJavaScriptBlock(tipo)}';
  } else {
    return false;
  }
  j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>';
  if (msg != '') {
    ConfirmaUrl(msg, j);
  } else {
    postData(j);
  }
  return true;
}
function downloadManual() {
	var nomeArquivo = "<hl:message key='rotulo.download.arquivos.integracao.nome.manual'/>";
    postData('../v3/downloadArquivo?tipo=manualFolha&arquivo_nome='+encodeURIComponent(nomeArquivo) + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
