<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  final String tipo      = "generico";
  ArrayList arquivos     = (ArrayList) request.getAttribute("arquivos");
  HashMap consignatarias = (HashMap) request.getAttribute("consignatarias");
  
  int size               = (int) request.getAttribute("size");
  int offset             = (int) request.getAttribute("offset");
  int campoSelect        = (Integer) request.getAttribute("campoSelect");
  
  String pathCsa         = (String) request.getAttribute("pathCsa");
  String campoTexto      = (String) request.getAttribute("campoTexto");
%>
<c:set var="title">
  <hl:message key="rotulo.lst.arq.generico.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-download"></use>
</c:set>
<c:set var="bodyContent">
    <div class="row firefox-print-fix">
      <div class="col-sm-5 col-md-4 d-print-none">
      <div class="card">
        <FORM NAME="f0" METHOD="post" ACTION="../v3/listarArquivosDownloadGenerico?acao=iniciar">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
            </div>
            <div class="card-body">
        
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="campoTexto"><hl:message key="rotulo.acoes.filtrar"/> <hl:message key="rotulo.listar.consignataria.filtro"/></label>
                    <input type="text" class="form-control" id="campoTexto" name="campoTexto" value="<%=TextHelper.forHtmlAttribute(campoTexto)%>" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>">
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="campoSelect"><hl:message key="rotulo.acao.filtrar.por"/></label>
                    <select class="form-control form-select select" id="campoSelect" name="campoSelect">
                        <optgroup label="<%=ApplicationResourcesHelper.getMessage("rotulo.filtro.plural", responsavel)%>:">
                          <OPTION VALUE="0" <%=(String)((campoSelect == 0) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                        <%if (responsavel.isCseSupOrg()) {%>
                          <OPTION VALUE="1" <%=(String)((campoSelect == 1) ? "SELECTED" : "")%>><hl:message key="rotulo.lst.arq.generico.nome.consignataria"/></OPTION>
                        <%} %>
                          <OPTION VALUE="2" <%=(String)((campoSelect == 2) ? "SELECTED" : "")%>><hl:message key="rotulo.lst.arq.generico.nome.arquivo"/></OPTION>
                      </optgroup>
                    </select>
                  </div>
                </div>
            </div>
          </div>
          <div class="btn-action d-print-none">
            <a class="btn btn-primary mr-4" href="#no-back" onClick="filtrar();">
              <svg width="20">
                <use xlink:href="#i-consultar"></use></svg>
              <hl:message key="rotulo.botao.pesquisar"/>
            </a>
          </div>
        </form>
      </div>
      </div>
        <div class="col-sm-7 col-md-8">             
     <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.lst.arq.disponiveis"/></h2>
            </div>
            <div class="card-body table-responsive">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col"><hl:message key="rotulo.lst.arq.generico.nome"/></th>
                    <th scope="col"><hl:message key="rotulo.lst.arq.generico.tamanho.abreviado"/></th>
                    <th scope="col"><hl:message key="rotulo.lst.arq.generico.data"/></th>
                    <%if (responsavel.isCseSupOrg()) {%>
                      <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
                    <%}%>
                    <th scope="col" width="10%"><hl:message key="rotulo.acoes.lst.arq.generico.acoes"/></th>
                  </tr>
                </thead>
                <tbody>
                 <%
                  if (arquivos == null || arquivos.size() == 0){
                 %>
                  <tr>
                    <td colspan="5" ><hl:message key="rotulo.lst.arq.generico.encontrado"/></td>
                  </tr>
                 <%
                  } else {
                    int i = 0;
                    int j = offset == -1 ? ((arquivos.size() % size) == 0 ? (arquivos.size() - size) : arquivos.size() - (arquivos.size() % size)) : offset;
                    while (arquivos.size() > j && i < size) {
                      File arquivo = (File)arquivos.get(j);
               
                      String tam = "";
                      if (arquivo.length() > 1024.00) {
                        tam = Math.round(arquivo.length() / 1024.00) + " KB";
                      } else {
                        tam = arquivo.length() + " B";
                      }
                      String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                      String nome = arquivo.getName() ;
                      String path = arquivo.getPath();
                
                      int indice = path.indexOf(File.separatorChar + "cse" + File.separatorChar);
                      String entidade = (indice != -1) ? "cse" : "csa";
                      String csa_codigo = (responsavel.isCseSupOrg() && entidade.equals("csa")) ? path.substring(pathCsa.length() + 1, path.indexOf(File.separatorChar, pathCsa.length() + 1)) : null;
      
                      String formato = "";
                      if (nome.toLowerCase().endsWith(".txt")) {
                        formato = "text.gif";
                      } else if (nome.toLowerCase().endsWith(".zip")) {
                        formato = "zip.gif";
                      } else if (nome.toLowerCase().endsWith(".pdf")) {
                        formato = "pdf.gif";
                      } else {
                        formato = "help.gif";
                      }
      
                      j++;
                      i++;
            
                      CustomTransferObject consignataria = (CustomTransferObject)consignatarias.get(csa_codigo);
                      String csa_identificador = null;
                      String csa_nome = null;
      
                      if ((consignataria != null) || (responsavel.isCseSupOrg() && entidade.equals("cse")) || (responsavel.isCsaCor())) {
                        nome = java.net.URLEncoder.encode(((csa_codigo != null) ? csa_codigo + File.separatorChar : "") + nome, "UTF-8");
                        csa_identificador = (consignataria != null) ? consignataria.getAttribute(Columns.CSA_IDENTIFICADOR).toString() : "";
                        csa_nome = consignataria != null ? consignataria.getAttribute(Columns.CSA_NOME).toString() : "";
                      %>
                    <tr>
                      <td><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
                      <td align="right"><%=TextHelper.forHtmlContent(tam)%>&nbsp;</td>
                      <td align="center"><%=TextHelper.forHtmlContent(data)%></td>
                      <%if (responsavel.isCseSupOrg()) {%>
                        <td align="center"><%=consignataria != null ? (TextHelper.forHtmlContent(csa_identificador.toUpperCase()) + " - " + TextHelper.forHtmlContent(csa_nome.toUpperCase())) : ""%></td> 
                        <td align="center">
                          <div class="actions">
                            <div class="dropdown">
                              <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <div class="form-inline">
                                  <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key='rotulo.acoes.lst.arq.generico.opcoes'/>" title="<hl:message key='rotulo.acoes.lst.arq.generico.opcoes'/>">
                                    <svg><use xlink:href="#i-engrenagem"></use></svg>
                                  </span>
                                  <hl:message key='rotulo.acoes.lst.arq.generico.opcoes'/>
                                </div>
                              </a>
                              <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                                <a class="dropdown-item" href="#" onClick="javascript:doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(entidade)%>'); return false;" aria-label="<hl:message key="mensagem.excluir.lst.arq.generico.clique.aqui"/> <%=TextHelper.forHtmlContent(arquivo.getName())%>"><hl:message key="rotulo.acoes.lst.arq.generico.excluir"/></a> 
                                <a class="dropdown-item" href="#confirmarMensagem" onClick="postData('../v3/downloadArquivo?arquivo_nome='+encodeURIComponent('<%=TextHelper.forJavaScriptAttribute(nome)%>') + '&tipo=<%=TextHelper.forJavaScript(tipo)%>&entidade=<%=TextHelper.forJavaScript(entidade)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;" data-bs-toggle="modal" aria-label="<hl:message key='rotulo.botao.aria.download.arquivo' arg0='<%=TextHelper.forHtmlContent(arquivo.getName())%>'/>"><hl:message key='rotulo.acoes.lst.arq.generico.download'/></a>
                              </div>
                            </div>
                          </div>
                        </td>                
                      <%} else {%>
                        <td align="center"><a class="dropdown-item" href="#confirmarMensagem" onClick="postData('../v3/downloadArquivo?arquivo_nome='+encodeURIComponent('<%=TextHelper.forJavaScriptAttribute(nome)%>') + '&tipo=<%=TextHelper.forJavaScript(tipo)%>&entidade=<%=TextHelper.forJavaScript(entidade)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;" data-bs-toggle="modal" aria-label="<hl:message key='rotulo.botao.aria.download.arquivo' arg0='<%=TextHelper.forHtmlContent(arquivo.getName())%>'/>"><hl:message key="rotulo.acoes.lst.arq.generico.download"/></a></td>
                      <%}%>
                    </tr>
                 <%
                      }
                    }
                  }
                 %> 
                  </tbody>
      
                  <tfoot>
                    <tr>
                      <td colspan="5">
                        <hl:message key="rotulo.lst.navegar.arq.generico"/>
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
      </div>
    </div>
    <div class="btn-action mr-1">
      <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
  
</c:set>  
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript">
  <!--
    function doIt(opt, arq, path, entidade) {
      var msg = '', j;
      if (opt == 'e') {
        msg = '<hl:message key="mensagem.confirmacao.lst.arq.generico.exclusao"/>'.replace('{0}', arq);
        j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>&entidade=' + entidade;
       
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
    function filtrar() {
  	   f0.submit();
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