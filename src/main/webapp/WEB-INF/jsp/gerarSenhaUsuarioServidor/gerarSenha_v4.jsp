<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.job.process.ControladorProcessos" %>
<%@ page import="com.zetra.econsig.job.process.ProcessaManutencaoSenhaServidor" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean temProcessoRodando = (Boolean) request.getAttribute("temProcessoRodando");
boolean podeGerarSenhas = (Boolean) request.getAttribute("podeGerarSenhas");
String absolutePath = (String) request.getAttribute("absolutePath");
List<?>  arquivosRetorno = (List<?> ) request.getAttribute("arquivosRetorno");
%>
<c:set var="title">
  <%=ApplicationResourcesHelper.getMessage("rotulo.usuario.gerar.senha.titulo", responsavel)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <% if (!temProcessoRodando) { %>
  <div class="card">
    <div class="card-header hasIcon">
      <span class="card-header-icon">
        <svg width="26">
          <use xlink:href="img/sprite.svg#i-servidor"></use></svg>
      </span>
      <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("rotulo.usuario.gerar.senha.subtitulo", responsavel) %></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr class="tabelatopo">
            <th scope="col"><hl:message key="rotulo.usuario.nome"/></th>
            <th scope="col"><hl:message key="rotulo.usuario.gerar.senha.tamanho.abreviado"/> </th>
            <th scope="col"><hl:message key="rotulo.usuario.data"/></th>                
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
  <% if (arquivosRetorno == null || arquivosRetorno.size() == 0){%>
          <tr class="Lp">
            <td colspan="5"><hl:message key="mensagem.erro.nenhum.arquivo.encontrado"/></td>
          </tr>
  <% 
    } else { 
      Iterator<?> it = arquivosRetorno.iterator();
      while (it.hasNext()) {
        File arquivo = (File)it.next();
        String tam = "";
        if (arquivo.length() > 1024.00) {
          tam = Math.round(arquivo.length() / 1024.00) + " KB";
        } else {
          tam = arquivo.length() + " B";
        }
        String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
        String nome = arquivo.getPath().substring(absolutePath.length() + 1);
        String formato = "";
        if (nome.toLowerCase().endsWith(".zip")) {
          formato = "zip.gif";
        } else if (nome.toLowerCase().endsWith(".txt")) {
          formato = "text.gif";
        }
        nome = java.net.URLEncoder.encode(nome, "UTF-8");
  %>
              <tr>
                <td><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
                <td><%=TextHelper.forHtmlContent(tam)%>&nbsp;</td>
                <td><%=TextHelper.forHtmlContent(data)%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="Mais ações" aria-label="Mais ações">
                              <svg>
                                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use>
                              </svg>
                          </span>
                          <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <a name="submit1" value="download" class="dropdown-item" href="#no-back" onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>'),'arquivo_senhas'); return false;"><hl:message key="rotulo.acoes.download"/></a>
                        <a name="submit3" value="excluir" class="dropdown-item" href="#no-back" onClick="javascript:doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>','<%=TextHelper.forJavaScript(nome)%>', 'arquivo_senhas'); return false;"><hl:message key="rotulo.acoes.excluir"/></a>
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
              <hl:message key="rotulo.listagem.senha.servidor"/>
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
  <%  if (podeGerarSenhas) { %>    
    <a class="btn btn-primary" href="#no-back" onClick="gerarSenhas()"><hl:message key="rotulo.botao.gerar"/></a>
  <% } %>
  <%  if (arquivosRetorno != null && arquivosRetorno.size() > 0) { %>
    <a class="btn btn-primary" href="#no-back" onClick="ativarSenhas()"><hl:message key="rotulo.botao.ativar"/></a>
  <% } %>  
  </div>
<% } else { %>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    </div>
<% } %>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript">
    
  </script>
  <script type="text/JavaScript">
  	doLoad(<%=(boolean)temProcessoRodando%>);
  
    function downloadArquivo(arquivo,tipo) {
      postData('../v3/downloadArquivo?arquivo_nome=' + arquivo + '&tipo=' + encodeURIComponent(tipo) + '&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
    }
    
    function doIt(opt, arq, path, tipo) {
      var msg = '', j;
      if (opt == 'e') {
        msg = '<hl:message key="mensagem.confirmacao.exclusao.arquivo"/>'.replace("{0}", arq);
        j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&tipo=' + tipo;
        j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>';
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
    
    function gerarSenhas() {
      if (confirm('<hl:message key="mensagem.confirmacao.usu.ser.gerar.novas.senhas"/>')) {
        postData('../v3/gerarSenhaUsuarioServidor?acao=gerarSenha&<%=SynchronizerToken.generateToken4URL(request)%>');
      }
    }
    
    function ativarSenhas() {
      if (confirm('<hl:message key="mensagem.confirmacao.usu.ser.ativar.novas.senhas"/>')) {
        postData('../v3/gerarSenhaUsuarioServidor?acao=ativarSenha&<%=SynchronizerToken.generateToken4URL(request)%>');
      }
    }
    
    function doLoad(reload) {
      if (reload) {
        setTimeout("refresh()", 15*1000);
      }
    }
    
    function refresh() {
  	  postData("../v3/gerarSenhaUsuarioServidor?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>");
  	}
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>