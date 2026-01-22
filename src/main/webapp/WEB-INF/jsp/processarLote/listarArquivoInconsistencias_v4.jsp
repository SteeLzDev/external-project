<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="fl"    uri="/function-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
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
String absolutePath = (String) request.getAttribute("absolutePath");
boolean podeProcessarArquivo = (boolean) request.getAttribute("podeProcessarArquivo");
boolean podeExcluirArquivo = (boolean) request.getAttribute("podeExcluirArquivo");
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
String tipo = (String) request.getAttribute("tipo");

List<?> arquivos = (List<?>) request.getAttribute("arquivos");
int size = (int) request.getAttribute("size");
int offset = (int) request.getAttribute("offset");
String linkRet = (String) request.getAttribute("linkRet");
%>
<c:set var="title">
  <hl:message key="rotulo.processar.arquivo.inconsistencia.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>  
</c:set>

<c:set var="bodyContent">
  <% if (!temProcessoRodando) { %>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.processar.arquivo.inconsistencia.titulo"/></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">          
            <thead>
            
              <tr>
                <th scope="col"><hl:message key="rotulo.inconsistencia.nome"/></th>
                <th scope="col"><hl:message key="rotulo.inconsistencia.tamanho.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.inconsistencia.data"/></th>
                <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>

    <%
      if (arquivos == null || arquivos.size() == 0){
    %>
    <tr>
      <td colspan="7"><hl:message key="rotulo.lst.arq.generico.encontrado"/></td>
    </tr>
    <%
    } else {
    int i = 0;
    int j = offset == -1 ? ((arquivos.size() % size) == 0 ? (arquivos.size() - size) : arquivos.size() - (arquivos.size() % size)) : offset;
    Iterator<?> it = arquivos.iterator();
    while (arquivos.size() > j && i < size) {
      File arquivo = (File)arquivos.get(j);
      String tam = "";
      if (arquivo.length() > 1024.00) {
        tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
      } else {
        tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
      }
      String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
      String nome = arquivo.getPath().substring(absolutePath.length()).replaceAll("/", "");
      String formato = (nome.toLowerCase().endsWith(".zip") ? "zip.gif" : "text.gif");

      j++;
      nome = java.net.URLEncoder.encode(nome, "UTF-8");
      %>
              <tr>
                <td><%=TextHelper.forHtmlAttribute(formato)%><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
                <td><%=TextHelper.forHtmlContent(tam)%>&nbsp;</td>
                <td><%=TextHelper.forHtmlContent(data)%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key='rotulo.mais.acoes'/>" aria-label="<hl:message key='rotulo.mais.acoes'/>"> <svg>
                            <use xlink:href="#i-engrenagem"></use></svg>
                          </span><hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                      <% if (podeProcessarArquivo && !arquivo.getName().toLowerCase().startsWith(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel).toLowerCase())) { %>
                        <a class="dropdown-item" href="#" onClick="doIt('i', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;"><hl:message key="rotulo.acoes.processar"/></a>
                      <% } %>
                        <a class="dropdown-item" href="#" onClick="fazDownload('<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(tipo)%>', 'cse', '');"><hl:message key="rotulo.acoes.download"/></a>
                      <% if (podeExcluirArquivo) { %>
                        <a class="dropdown-item" href="#" onClick="doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;"><hl:message key="rotulo.acoes.excluir"/></a>
                      <% } %>
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
                    <td colspan="4">
                      <hl:message key="rotulo.lote.listagem.arquivo.inconsistencia"/>
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
              <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/downloadArquivo?arquivo_nome=' + encodeURIComponent(nome)=<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
            </div>
           <% } else { %>
           <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
           </div>
           <% } %>
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
        j = '../v3/processarLoteInconsistencia?acao=excluir&arquivo_nome=' + encodeURIComponent(path) + '&ext=exc' + '&tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>&link=../v3/processarLoteInconsistencia?acao=iniciar$offset(<%=TextHelper.forJavaScriptBlock(offset)%>|linkRet(<%=TextHelper.forJavaScriptBlock(linkRet.replace('?', '$').replace('&', '|').replace('=', '('))%>&MM_update=true';
      } else if (opt == 'i') {
      	msg = '<hl:message key="mensagem.inconsistencia.confirma.processamento.arquivo"/>'.replace('{0}', arq);
        f0.VALIDAR.value = 'I';
        f0.arquivo_nome.value = encodeURIComponent(path);
      } else {
        return false;
      }
      j = j + '&tipo=processar&<%=SynchronizerToken.generateToken4URL(request)%>';
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
      postData("../v3/processarLoteInconsistencia?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>");
    }
    
    function fazDownload(nome, tipo, entidade, tipoCodigo){
      postData('../v3/downloadArquivo?arquivo_nome=' + encodeURIComponent(nome) + '&tipo='+ tipo + '&MM_update=true&skip_history=true&<%=SynchronizerToken.generateToken4URL(request)%>','download');
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>