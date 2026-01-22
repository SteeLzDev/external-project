<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.Pair"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="t"  tagdir="/WEB-INF/tags" %>
<%
  AcessoSistema responsavel  = JspHelper.getAcessoSistema(request);
  boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
  boolean atalhoUpload = request.getAttribute("atalhoUpload") != null ? (boolean) request.getAttribute("atalhoUpload") : false;
  String estCodigo    = (String) request.getAttribute("estCodigo");
  String orgCodigo    = (String) request.getAttribute("orgCodigo");
  String absolutePath = (String) request.getAttribute("absolutePath");
  String pathRetorno  = (String) request.getAttribute("pathRetorno");
  String pathCritica  = (String) request.getAttribute("pathCritica");
  List<Pair<File, String>> arquivosRetorno = (List<Pair<File, String>>) request.getAttribute("arquivosRetorno");
  List<Pair<File, String>> arquivosCritica = (List<Pair<File, String>>) request.getAttribute("arquivosCritica");
%>
<c:set var="title">
  <hl:message key="rotulo.folha.importacao.retorno.integracao.folha.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <% if (!temProcessoRodando) { %>
  <% if (responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_GERAL) || responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_PARCIAL) || atalhoUpload) { %>
  <div class="page-title">
    <div class="row d-print-none">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false"
                  class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
            <%if (atalhoUpload) { %>
            <a class="dropdown-item" href="no-back"
               onclick="postData('../v3/listarRetornoIntegracao?acao=atalhoUpload&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message
                    key="rotulo.atalho.upload"/></a>
            <% } %>
            <% if (responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_GERAL)) { %>
            <a class="dropdown-item" href="#no-back"
               onClick="postData('../v3/recalcularMargemGeral?acao=iniciar&direction=2&<%=SynchronizerToken.generateToken4URL(request)%>')">
              <hl:message key="rotulo.recalcular.margem.geral"/> </a>
            <% } %>
            <% if (responsavel.temPermissao(CodedValues.FUN_RECALCULAR_MARGEM_PARCIAL)) {%>
            <a class="dropdown-item" href="#no-back"
               onClick="postData('../v3/recalcularMargemParcial?acao=iniciar&direction=2&<%=SynchronizerToken.generateToken4URL(request)%>')">
              <hl:message key="rotulo.recalcular.margem.parcial"/> </a>
            <% } %>
          </div>
        </div>
      </div>
    </div>
  </div>
  <% } %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="mensagem.folha.disponibilidade.arquivos.retorno"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.folha.nome"/></th>
            <th scope="col"><hl:message key="rotulo.folha.tamanho.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.folha.data"/></th>
            <% if(responsavel.isCseSup()) { %>
            <th scope="col"><hl:message key="rotulo.estabelecimento.abreviado"/> - <hl:message key="rotulo.orgao.abreviado"/></th>
            <% } %>
            <th scope="col" width="15%"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
<%  if (arquivosRetorno == null || arquivosRetorno.size() == 0) {%>
          <tr>
            <td colspan="5"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
          </tr>
<%  } else {
      for (Pair<File, String> arquivoRetorno : arquivosRetorno) {
        File arquivo    = arquivoRetorno.first;
        String entidade = arquivoRetorno.second;
        String tam = "";
        if (arquivo.length() > 1024.00) {
          tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
        } else {
          tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
        }
        String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
        String nome = arquivo.getPath().substring(pathRetorno.length());
        String nomeCodificado = java.net.URLEncoder.encode(nome, "UTF-8");
        String nomeOriginal = arquivo.getName().replaceAll("\\.crypt", "");
        String formato = (nome.toLowerCase().endsWith(".zip") ? "zip.gif" : "text.gif");
%>
          <tr>
            <td><%=TextHelper.forHtmlContent(nomeOriginal)%></td>
            <td><%=TextHelper.forHtmlContent(tam)%></td>
            <td><%=TextHelper.forHtmlContent(data)%></td>
            <% if(responsavel.isCseSup()) { %>
            <td><%=TextHelper.forHtmlContent(entidade)%></td>
            <% } %>
            <td>
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" aria-label='<hl:message key="rotulo.acoes"/>' title='<hl:message key="rotulo.acoes"/>'> 
                        <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                      </span><hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/downloadArquivo?arquivo_nome='+encodeURIComponent('<%=TextHelper.forJavaScript(nomeCodificado)%>') + '&tipo=retorno&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.acoes.download"/></a>
                    <a class="dropdown-item" href="#no-back" onClick="importaRetorno(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>'), 'retorno')"><hl:message key="rotulo.acoes.importar"/></a>
                    <a class="dropdown-item" href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(nomeOriginal)%>','<%=TextHelper.forJavaScript(nomeCodificado)%>', 'retorno'); return false;"><hl:message key="rotulo.acoes.excluir"/></a>
                  </div>
                </div>
              </div>
            </td>
          </tr>
<%    }
    } %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="5"><hl:message key="rotulo.folha.importacao.retorno.integracao.listagem"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="mensagem.folha.disponibilidade.arquivos.critica"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.folha.nome"/></th>
            <th scope="col"><hl:message key="rotulo.folha.tamanho.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.folha.data"/></th>
            <% if(responsavel.isCseSup()) { %>
            <th scope="col"><hl:message key="rotulo.estabelecimento.abreviado"/> - <hl:message key="rotulo.orgao.abreviado"/></th>
            <% } %>
            <th scope="col" width="15%"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
<%  if (arquivosCritica == null || arquivosCritica.size() == 0) {%>
        <tr>
          <td colspan="5"><hl:message key="mensagem.folha.nenhum.arquivo.encontrado"/></td>
        </tr>
<%  } else {
    for (Pair<File, String> arquivoCritica : arquivosCritica) {
      File arquivo = arquivoCritica.first;
      String entidade = arquivoCritica.second;
      String tam = "";
      if (arquivo.length() > 1024.00) {
        tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
      } else {
        tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
      }
      String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
      String nome = arquivo.getPath().substring(pathCritica.length());
      String nomeCodificado = java.net.URLEncoder.encode(nome, "UTF-8");
      String nomeOriginal = arquivo.getName().replaceAll("\\.crypt", "");
      String formato = (nome.toLowerCase().endsWith(".zip") ? "zip.gif" : "text.gif");
%>
          <tr>
            <td><%=TextHelper.forHtmlContent(nomeOriginal)%></td>
            <td><%=TextHelper.forHtmlContent(tam)%></td>
            <td><%=TextHelper.forHtmlContent(data)%></td>
            <% if(responsavel.isCseSup()) { %>
            <td><%=TextHelper.forHtmlContent(entidade)%></td>
            <% } %>
            <td>
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" aria-label='<hl:message key="rotulo.acoes"/>' title='<hl:message key="rotulo.acoes"/>'> 
                        <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                      </span><hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/downloadArquivo?arquivo_nome='+encodeURIComponent('<%=TextHelper.forJavaScript(nomeCodificado)%>') + '&tipo=critica&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.acoes.download"/></a>
                    <a class="dropdown-item" href="#no-back" onClick="importaRetorno(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>'), 'critica')"><hl:message key="rotulo.acoes.importar"/></a>
                    <a class="dropdown-item" href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(nomeOriginal)%>','<%=TextHelper.forJavaScript(nomeCodificado)%>', 'critica'); return false;"><hl:message key="rotulo.acoes.excluir"/></a>
                  </div>
                </div>
              </div>
            </td>
          </tr>
<%    }
    } %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="5"><hl:message key="rotulo.folha.importacao.critica.integracao.listagem"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="finalizaImportacao()"><hl:message key="rotulo.botao.concluir"/></a>
  </div>
  <div class="card">
    <hl:infoPeriodoV4 tipo="retorno"/>
  </div>
  <%}else{ %>
  <div class="card">
    <div class="btn-action">
      <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
  </div>
  <%} %>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
    doLoad(<%=temProcessoRodando%>);

    function importaRetorno(arquivo, tipo) {
      if (confirm('<hl:message key="mensagem.folha.confirmacao.importacao.integracao"/>'.replace('{0}',tipo))) {
        postData('../v3/listarRetornoIntegracao?acao=processar&arquivo=' + arquivo + '&tipo=' + tipo + '&<%=SynchronizerToken.generateToken4URL(request)%>');
      }
    }
    
    function finalizaImportacao() {
      if (confirm('<hl:message key="mensagem.folha.confirmacao.finalizar.importacao"/>')) {
        postData('../v3/listarRetornoIntegracao?acao=concluir&tipo=CONCLUIR&<%=SynchronizerToken.generateToken4URL(request)%>');
      }
    }
    function doIt(opt, arq, path, tipo) {
    	  var msg = '', j;
    	  if (opt == 'e') {
    	    msg = '<hl:message key="mensagem.folha.confirmacao.remover.arquivo.nome"/>'.replace('{0}',arq);
    	    j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(arq) + '&tipo='+tipo;
    	    j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>'
    	  } else {
    	    return false;
    	  }
    	  if (msg != '') {
    	    ConfirmaUrl(msg, j);
    	  } else {
    	    postData(j);
    	  }
    	  return true;
    	}
    function doLoad(reload) {
      if (reload) {
        setTimeout("refresh()", 15*1000);
      }
    }
    function refresh() {
      postData("../v3/listarRetornoIntegracao?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>");
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>