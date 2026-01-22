<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String csaCodigo = (String) request.getAttribute("csaCodigo");
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
boolean podeProcessarArquivo = (boolean) request.getAttribute("podeProcessarArquivo");
boolean podeExcluirArquivo = (boolean) request.getAttribute("podeExcluirArquivo");
List<?> arquivos = (List<?>) request.getAttribute("arquivos");
int offset = (int) request.getAttribute("offset");
String linkRet = (String) request.getAttribute("linkRet");
String xml = (String) request.getAttribute("xml");
int size = (int) request.getAttribute("size");
final String tipo = (String) request.getAttribute("tipo");
String tipoCodigo = (String) request.getAttribute("tipoCodigo");
String entidade = (String) request.getAttribute("entidade");
String absolutePath = (String) request.getAttribute("absolutePath");
String parametros = (String) request.getAttribute("parametros");
boolean conciliacaoMultipla = (boolean) request.getAttribute("conciliacaoMultipla");
%>
<c:set var="title">
     <hl:message key="<%= conciliacaoMultipla ? "rotulo.processar.conciliacao.arquivo.multiplo.titulo" : "rotulo.processar.arquivo.conciliacao.titulo" %>"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>  
</c:set>
<c:set var="bodyContent">
  <form name="form1" action="../v3/<%=conciliacaoMultipla ? "conciliarArquivoMultiplo" : "conciliarArquivo"%>?acao=processa&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" method="post">
    <% if (!temProcessoRodando) { %>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="<%= conciliacaoMultipla ? "rotulo.processar.conciliacao.arquivo.multiplo.titulo"  : "rotulo.processar.conciliacao.titulo"%>"/></h2>
      </div> 
        <div class="card-body table-responsive ">
          <table class="table table-striped table-hover">  
               <thead>
                  <tr>
                    <th scope="col"><hl:message key="rotulo.conciliacao.nome"/></th>
                    <th scope="col"><hl:message key="rotulo.conciliacao.tamanho.abreviado"/></th>
                    <th scope="col"><hl:message key="rotulo.conciliacao.data"/></th>
                    <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
                  </tr>
                </thead>
              <%
                if (arquivos == null || arquivos.size() == 0){
              %>
                <tr>
                   <td colspan="4"><hl:message key="rotulo.lst.arq.generico.encontrado"/></td>
                </tr>
              <%
                } else {
                int i = 0;
                int j = offset == -1 ? ((arquivos.size() % size) == 0 ? (arquivos.size() - size) : arquivos.size() - arquivos.size() % size) : offset;
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
                  String nome = arquivo.getPath().substring(absolutePath.length());
                  String formato = (nome.toLowerCase().endsWith(".zip") ? "zip.gif" : "text.gif");
            
                  j++;
                  nome = java.net.URLEncoder.encode(nome, "UTF-8");
                %>
                <tr>
                  <td><%=TextHelper.forHtmlAttribute(formato)%><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
                  <td><%=TextHelper.forHtmlContent(tam)%></td>
                  <td><%=TextHelper.forHtmlContent(data)%></td>
                  <td>
                  <% if ((podeProcessarArquivo && !arquivo.getName().startsWith("lote_sincronia")) || podeExcluirArquivo) { %>
                    <div class="actions">
                      <div class="dropdown">
                        <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" aria-label='<hl:message key="rotulo.acoes"/>' title='<hl:message key="rotulo.acoes"/>'> 
                              <svg><use xlink:href="#i-engrenagem"></use></svg>
                            </span><hl:message key="rotulo.botao.opcoes"/>
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                          <% if (podeProcessarArquivo && !arquivo.getName().startsWith("lote_sincronia")) { %>
                            <a class="dropdown-item" href="#no-back" onClick="doIt('i', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;"><hl:message key="rotulo.acoes.processar"/></a>
                          <% }%>
                            <a class="dropdown-item" href="#no-back" onClick="fazDownload('<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(tipo)%>', '<%=TextHelper.forJavaScript(entidade)%>', '<%=TextHelper.forJavaScript(tipoCodigo)%>');"><hl:message key="rotulo.acoes.download"/></a>
                          <% if (podeExcluirArquivo) { %>
                            <a class="dropdown-item" href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;"><hl:message key="rotulo.acoes.excluir"/></a>
                          <% } %>
                        </div>
                      </div>
                    </div>
                  <% } else {  %>
                    <a href="#no-back" onClick="fazDownload('<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(tipo)%>', '<%=TextHelper.forJavaScript(entidade)%>', '<%=TextHelper.forJavaScript(tipoCodigo)%>');"><hl:message key="rotulo.acoes.download"/></a>
                  <% } %>
                  </td>
                </tr> 
              <%
                  }
                }
              %>
            <tfoot>
              <tr>
                <td colspan="5"><hl:message key="rotulo.arquivos.conciliacao"/>
                  <span class="font-italic"> - 
                    <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
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
          <% if (!temProcessoRodando) { %>
            <div class="btn-action">
              <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkRet.replace('$', '?').replace('|', '&').replace('(', '='), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
            </div>
            <input name="VALIDAR" type="hidden" value="">
            <input name="operacao" type="hidden" value="listar">
            <input name="arquivo_nome" type="hidden" value="">
            <input name="CSA_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
            <input name="XML" type="hidden" value="<%=TextHelper.forHtmlAttribute(xml)%>">
          <% } else { %>
            <div class="btn-action">
              <a class="btn btn-outline-danger" id="btnVoltar" href="#" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
            </div>
          <% } %>
  </form>  
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  	var f0 = document.forms[0];
  </script>
  <script type="text/JavaScript">
    function doIt(opt, arq, path) {
      var msg = '', j;
      if (opt == 'e') {
        msg = '<hl:message key="mensagem.confirmacao.lst.arq.generico.exclusao"/>'.replace('{0}', arq);
        j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&ext=exc' + '&tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>&entidade=<%=TextHelper.forJavaScriptBlock(entidade)%>&subtipo=<%=TextHelper.forJavaScriptBlock(tipoCodigo)%>&link=../v3/<%=conciliacaoMultipla ? "conciliarArquivoMultiplo" : "conciliarArquivo"%><%=TextHelper.forJavaScriptBlock(parametros)%>&acao=selecionar|offset(<%=TextHelper.forJavaScriptBlock(offset)%>|linkRet(<%=TextHelper.forJavaScriptBlock(linkRet.replace('?', '$').replace('&', '|').replace('=', '('))%>&MM_update=true';
      } else if (opt == 'i') {
      msg = '<hl:message key="mensagem.conciliacao.confirma.importacao.arquivo"/>'.replace('{0}', arq);
        f0.VALIDAR.value = 'I';
        f0.arquivo_nome.value = encodeURIComponent(path);
      } else {
        return false;
      }
      j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>';
      if (msg != '') {
        if (confirm(msg)) {
          if (opt == 'i') {
            f0.submit();
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
      postData("../v3/<%=conciliacaoMultipla ? "conciliarArquivoMultiplo" : "conciliarArquivo"%>?acao=selecionar&CSA_CODIGO=<%=TextHelper.forJavaScriptBlock(csaCodigo)%>&XML=<%=TextHelper.forJavaScriptBlock(xml)%>&<%=SynchronizerToken.generateToken4URL(request)%>");
    }
    
    function fazDownload(nome, tipo, entidade, tipoCodigo){
      postData('../v3/downloadArquivo?arquivo_nome=' + encodeURIComponent(nome) + '&tipo='+ tipo + '&entidade='+ entidade + '&subtipo=' + tipoCodigo + '&MM_update=true&skip_history=true&<%=SynchronizerToken.generateToken4URL(request)%>','download');
    }
    window.onload = doLoad(<%=(boolean)temProcessoRodando%>);
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>