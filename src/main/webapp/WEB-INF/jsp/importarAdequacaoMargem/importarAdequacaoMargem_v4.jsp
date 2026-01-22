<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.Pair"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
List<Object> arquivos = (List<Object>) request.getAttribute("arquivos");
int offset = (int) request.getAttribute("offset");
int size = (int) request.getAttribute("size");
String diretorioArquivos = (String) request.getAttribute("diretorioArquivos");
%>
<c:set var="title">
  <hl:message key="rotulo.adequacao.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
<form name="form1" action="../v3/importarAdequacaoMargem?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post">
<% if (!temProcessoRodando) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.adequacao.margem.arquivos.disponiveis"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.adequacao.margem.nome"/></th>          
            <th scope="col"><hl:message key="rotulo.adequacao.margem.tamanho.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.adequacao.margem.data"/></th>
            <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
          </tr>            
        </thead>
        <tbody>
          <%
            if (arquivos == null || arquivos.size() == 0){
          %>
              <tr>
                <td colspan="4"><hl:message key="mensagem.erro.nenhum.arquivo.encontrado"/></td>
              </tr>
          <%
              } else {
              int i = 0;
              int j = offset == -1 ? ((arquivos.size() % size) == 0 ? (arquivos.size() - size) : arquivos.size() - (arquivos.size() % size)) : offset;
              Iterator it = arquivos.iterator();
              while (arquivos.size() > j && i < size) {
                File arquivo = (File)arquivos.get(j);
                String tam = "";
                if (arquivo.length() > 1024.00) {
                  tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                } else {
                  tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                }
                String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                String nome = arquivo.getPath().substring(diretorioArquivos.length());
                String formato = (nome.toLowerCase().endsWith(".zip") ? "zip.gif" : "text.gif");
          
                j++;
                nome = java.net.URLEncoder.encode(nome, "UTF-8");
          %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
                  <td><%=TextHelper.forHtmlContent(tam)%></td>
                  <td><%=TextHelper.forHtmlContent(data)%></td>
                  <td class="acoes">
                     <div class="actions">
                      <div class="dropdown">
                        <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.fila.op.sensiveis.ver.detalhes", responsavel)%>"> <svg>
                                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                            </span> <hl:message key="rotulo.botao.opcoes"/>
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                          <% if (!(arquivo.getName().toLowerCase().indexOf(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", responsavel)) > -1) ) { %>
                            <a class="dropdown-item" href="#no-back" onClick="doIt('v', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;"><hl:message key="rotulo.acoes.validar"/></a>
                            <a class="dropdown-item" href="#no-back" onClick="doIt('i', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;"><hl:message key="rotulo.acoes.processar"/></a>
                          <% } %>
                          <a class="dropdown-item" href="#no-back" onClick="fazDownload('<%=TextHelper.forJavaScript(nome)%>')"><hl:message key="rotulo.acoes.download"/></a>
                          <a class="dropdown-item" href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;"><hl:message key="rotulo.acoes.excluir"/></a>                             
                        </div>
                      </div>
                     </div>   
                  </td>
                </tr>
          <%
              }
            }
          %>
           </tbody>
           <tfoot>
          <tr>
            <td colspan="8">
              <hl:message key="rotulo.paginacao.adequacao.margem"/> - 
              <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
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
  <% if (!temProcessoRodando) { %>
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=SynchronizerToken.updateTokenInURL("../v3/carregarPrincipal", request)%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <input name="acao" type="hidden" value="iniciar">
    <input name="arquivo_nome" type="hidden" value="">
  <% } else { %>
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
  <% } %>
</div>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
var f0 = document.forms[0];

window.onload = doLoad(<%=(boolean)temProcessoRodando%>);

function doIt(opt, arq, path) {
  var msg = '', j;
  if (opt == 'e') {
    msg = '<hl:message key="mensagem.confirmacao.exclusao.adequacao.margem"/>'.replace("{0}", arq);
    j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&ext=exc' + '&tipo=adequacao&link=../v3/importarAdequacaoMargem';
  } else if (opt == 'i') {
    msg =  '<hl:message key="mensagem.confirmacao.processamento.adequacao.margem"/>'.replace("{0}", arq);
    j = '../v3/importarAdequacaoMargem?acao=processar&_skip_history_=true&arquivo_nome=' + encodeURIComponent(path);
    f0.arquivo_nome.value = encodeURIComponent(path);
  } else if (opt == 'v') {
    msg = '<hl:message key="mensagem.confirmacao.validacao.adequacao.margem"/>'.replace("{0}", arq);
    j = '../v3/importarAdequacaoMargem?acao=validar&_skip_history_=true&arquivo_nome=' + encodeURIComponent(path);
      f0.arquivo_nome.value = encodeURIComponent(path);
  } else {
    return false;
  }
  j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>'
  if (msg != '') {
    if (confirm(msg)) {
      if (opt == 'i' || opt == 'v') {
        postData(j);
      } else {
        postData(j);
      }
    } else {
      return false;
    }
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
    postData('../v3/importarAdequacaoMargem?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>');
}

function fazDownload(nome) {
  postData('../v3/downloadArquivo?arquivo_nome=' + nome + '&tipo=adequacao&skip_history=true' + '&<%=SynchronizerToken.generateToken4URL(request)%>','download');
}
//-->
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>