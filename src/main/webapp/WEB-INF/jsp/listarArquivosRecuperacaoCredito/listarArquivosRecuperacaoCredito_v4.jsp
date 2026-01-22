<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<% 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

final String tipo      = "recuperacao_credito";
String csaCodigo = (String) request.getAttribute("csaCodigo");
String pathCsa         = (String) request.getAttribute("pathCsa");
List<TransferObject>  lstConsignatarias = (List<TransferObject> ) request.getAttribute("lstConsignatarias");
ArrayList<?>  arquivos = (ArrayList<?> ) request.getAttribute("arquivos");
int offset             = (int) request.getAttribute("offset");
int size               = (int) request.getAttribute("size");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.titulo.lista.arquivo.recuperacao.credito"/>
</c:set>
<head>
  <link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
</head>
<c:set var="bodyContent">
  <% if (responsavel.isSup()) { %>
    <form name="form1" method="post" ACTION="../v3/listarArquivosRecuperacaoCredito">
      <input type="hidden" name="acao" value="iniciar"/>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="consignataria"><hl:message key="rotulo.consignataria.singular"/></label>
          <select name="CSA_CODIGO" id="consignataria" class="form-control" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);" onChange="document.form1.submit();"  >
            <option value="" ><hl:message key="rotulo.campo.selecione"/></option>
            <%
            Iterator<TransferObject> itFiltro = lstConsignatarias.iterator();
            while (itFiltro.hasNext()) {
                TransferObject csaTO = itFiltro.next();
                String csa_codigo = (String) csaTO.getAttribute(Columns.CSA_CODIGO);
                String csa_nome = (String) csaTO.getAttribute(Columns.CSA_NOME);
            %>
              <option value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>" <%=(String)((!TextHelper.isNull(csaCodigo) && csa_codigo.equals(csaCodigo)) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(csa_nome)%></option>
            <%
            }
            %>
          </select>
        </div>
      </div>
      <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
    </form>  
  <% } %>  
<%--  INICIO DA LISTA DE ARQUIVOS --%>
  <% if (!TextHelper.isNull(csaCodigo) && arquivos != null) {   %>
    <div class="card">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title"><hl:message key="rotulo.lista.arquivo.recuperacao.credito"/></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.upload.arquivo.nome"/></th>
              <th scope="col"><hl:message key="rotulo.upload.arquivo.tamanho"/></th>
              <th scope="col"><hl:message key="rotulo.upload.arquivo.data"/></th>
              <th><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
        <tbody>
          <%
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
      
            j++;
  
            if ((arquivo != null) || (responsavel.isSup() || (responsavel.isCsa()))) {
              nome = java.net.URLEncoder.encode("" + nome, "UTF-8");
            %>
            <tr>
                      <td><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
                      <td align="right"><%=TextHelper.forHtmlContent(tam)%></td>
                      <td align="center"><%=TextHelper.forHtmlContent(data)%></td>
                      <%if (responsavel.isSup()) {%>
                        <td align="center">
                          <div class="actions">
                            <div class="dropdown">
                              <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <div class="form-inline">
                                  <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key='rotulo.botao.opcoes'/>" title="<hl:message key='rotulo.botao.opcoes'/>">
                                    <svg><use xlink:href="#i-engrenagem"></use></svg>
                                  </span>
                                  <hl:message key='rotulo.botao.opcoes'/>
                                </div>
                              </a>
                              <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                                <a class="dropdown-item" href="#" onClick="javascript:doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>', '<%=TextHelper.forJavaScript(nome)%>','<%=TextHelper.forJavaScript(csaCodigo)%>'); return false;" aria-label="<hl:message key="mensagem.excluir.lst.arq.recuperacao.credito"/> <%=TextHelper.forHtmlContent(arquivo.getName())%>"><hl:message key="rotulo.acoes.excluir"/></a> 
                                <a class="dropdown-item" href="#confirmarMensagem" onClick="postData('../v3/downloadArquivo?arquivo_nome='+encodeURIComponent('<%=TextHelper.forJavaScriptAttribute(nome)%>') + '&tipo=<%=TextHelper.forJavaScript(tipo)%>&csaCodigo=<%=TextHelper.forJavaScript(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;" data-bs-toggle="modal" aria-label="<hl:message key='rotulo.botao.aria.download.arquivo' arg0='<%=TextHelper.forHtmlContent(arquivo.getName())%>'/>"><hl:message key='rotulo.acoes.download'/></a>
                              </div>
                            </div>
                          </div>
                        </td>                
                      <%} else {%>
                        <td align="center"><a class="dropdown-item" href="#confirmarMensagem" onClick="postData('../v3/downloadArquivo?arquivo_nome='+encodeURIComponent('<%=TextHelper.forJavaScriptAttribute(nome)%>') + '&tipo=<%=TextHelper.forJavaScript(tipo)%>&csaCodigo=<%=TextHelper.forJavaScript(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;" data-bs-toggle="modal" aria-label="<hl:message key='rotulo.botao.aria.download.arquivo' arg0='<%=TextHelper.forHtmlContent(arquivo.getName())%>'/>"><hl:message key="rotulo.acoes.download"/></a></td>
                      <%}%>
                    </tr>
                 <%
                      }
                    } %>
        </tbody>
          <tfoot>
          <tr>
            <td colspan="5"><hl:message key="rotulo.lista.arquivo.recuperacao.credito"/>
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
   <% }%> 
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#" onClick="postData('../v3/carregarPrincipal')">
      <hl:message key="rotulo.botao.cancelar"/>
    </a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript">
    function doIt(opt, arq, path, csaCodigo) {
      var msg = '', j;
      if (opt == 'e') {
        msg = '<hl:message key="mensagem.confirmacao.lst.arq.recuperacao.credito"/>'.replace('{0}', arq);
        j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>&csaCodigo=<%=TextHelper.forJavaScriptBlock(csaCodigo)%>';
       
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
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>