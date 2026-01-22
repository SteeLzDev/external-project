<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/function-lib" prefix="fl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
List arquivos = (List) request.getAttribute("arquivos");
String absolutePath = (String) request.getAttribute("absolutePath");
int size = (int) request.getAttribute("size");
int offset = (int) request.getAttribute("offset");
%>
<c:set var="title">
<hl:message key="rotulo.arquivo.desligado.bloqueado.titulo"/>
</c:set>
<c:set var="imageHeader">
<use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form name="form1" method="post">
     <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.arquivos.disponiveis"/></h2>
        </div>
     <div class="card-body p-0">
         <div class="alert alert-warning m-0" role="alert">
            <p class="mb-0"><hl:message key="mensagem.informacao.processamento.arquivo.nao.pode.ser.desfeito"/></p>
            <p class="mb-0"><hl:message key="mensagem.informacao.recomendacao.validacao.arquivo"/></p>
        </div>
        <div class="table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col" width="70%"><hl:message key="rotulo.lote.nome"/></th>
                <th scope="col" width="10%"><hl:message key="rotulo.lote.tamanho"/></th>
                <th scope="col" width="10%"><hl:message key="rotulo.lote.data"/></th>
                <th scope="col" width="10%"><hl:message key="rotulo.acoes"/></th>
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
            String nome = arquivo.getPath().substring(absolutePath.length());
            String formato = (nome.toLowerCase().endsWith(".zip") ? "zip.gif" : "text.gif");
      
            j++;
            nome = java.net.URLEncoder.encode(nome, "UTF-8");
      %>

            <tr>
              <td><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
              <td><%=TextHelper.forHtmlContent(tam)%></td>
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
                    <% if (!(arquivo.getName().toLowerCase().indexOf(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", responsavel)) > -1) ) { %>
                        <a href="#" class="dropdown-item" title='<hl:message key="mensagem.validar.arquivo.clique.aqui"/>' onClick="doIt('v', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>');"><hl:message key="rotulo.botao.validar.arquivo"/></a>
                        <a href="#" class="dropdown-item" title='<hl:message key="mensagem.importar.arquivo.clique.aqui"/>' onClick="doIt('i', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>');"><hl:message key="rotulo.botao.processar.arquivo"/></a>
                    <% } else { %>
                        <a href="#" class="dropdown-item disabled" title='<hl:message key="mensagem.erro.arquivo.nao.pode.validar"/>'><hl:message key="rotulo.botao.validar.arquivo"/></a>
                        <a href="#" class="dropdown-item disabled" title='<hl:message key="mensagem.erro.arquivo.nao.pode.processar"/>' ><hl:message key="rotulo.botao.processar.arquivo"/></a>
                    <% } %>
                      <a href="#" class="dropdown-item" title='<hl:message key="mensagem.download.arquivo.clique.aqui"/>' onClick="fazDownload('<%=TextHelper.forJavaScript(nome)%>', 'desligado', 'cse')"><hl:message key="rotulo.botao.download.arquivo"/></a>
                      <a href="#" class="dropdown-item" title='<hl:message key="mensagem.excluir.arquivo.clique.aqui"/>' onClick="doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>'); return false;"><hl:message key="rotulo.botao.excluir.arquivo"/></a>
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
          <tr><td colspan="4"><hl:message key="rotulo.lote.listagem.lote"/> - 
            <span class="font-italic"> 
              <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
            </span>
          </td></tr>
        </tfoot>
      </table>
     </div>
   </div> 
    <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
   </div>
  </form>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
        <input name="MM_update" type="hidden" value="form1">
        <input name="arquivo_nome" type="hidden" value="form1">
      </div>
</c:set>

<c:set var="javascript">
<script type="text/JavaScript">
    var f0 = document.forms[0];		
    
    doLoad(<%=temProcessoRodando%>);
    
    function doIt(opt, arq, path) {
      var msg = '', j;
      if (opt == 'e') {
        msg = '<hl:message key="mensagem.confirmacao.exclusao.arquivo"/>'.replace("{0}", arq);
        j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&ext=exc' + '&tipo=desligado&entidade=cse&link=../v3/importarServidoresBloqueadosDesligados';
      } else if (opt == 'i') {
        msg =  '<hl:message key="mensagem.confirmacao.processamento.arquivo"/>'.replace("{0}", arq);
        j = '../v3/importarServidoresBloqueadosDesligados?acao=processar&_skip_history_=true&arquivo_nome=' + encodeURIComponent(path);
        f0.arquivo_nome = encodeURIComponent(path);
      } else if (opt == 'v') {
        msg = '<hl:message key="mensagem.confirmacao.validacao.arquivo"/>'.replace("{0}", arq);
        j = '../v3/importarServidoresBloqueadosDesligados?acao=validar&_skip_history_=true&arquivo_nome=' + encodeURIComponent(path);
        f0.arquivo_nome = encodeURIComponent(path);
      } else {
        return false;
      }
      j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>'
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
    
    function doLoad(reload) {
      if (reload) {
        setTimeout("refresh()", 15*1000);
      }
    }
    
    function refresh() {
      postData("../v3/importarServidoresBloqueadosDesligados?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>");
    }
    
    function fazDownload(nome, tipo, entidade, tipoCodigo){
      postData('../v3/downloadArquivo?arquivo_nome='+ nome + '&tipo='+ tipo+ '&entidade=' + entidade +'&subtipo='+ tipoCodigo + '&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>','download');
      
    }
    
    function habilitaPeriodoManual() {
      var checkLote = f0.permiteLoteAtrasado;
      
      if (checkLote.checked) {
        f0.periodo.disabled = false;
      } else {
        f0.periodo.value = '';
        f0.periodo.disabled = true;
      }
      
      return true;
    }
  </script>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
