<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.web.ArquivoDownload"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  boolean usuExportaXML = (Boolean) request.getAttribute("usuExportaXML");
  
  List<?> listFilesOffset = (List<?>) request.getAttribute("listFilesOffset");
%>
<c:set var="title">
   <hl:message key="rotulo.download.arq.xml.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>  
</c:set>

<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.lst.arq.disponiveis"/></h2>
    </div>  
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.download.arq.arquivo.configuracao"/></th>
            <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
            <th scope="col"><hl:message key="rotulo.download.arq.tamanho.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.download.arq.data"/></th>
            <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody> 
          <%
            if (!listFilesOffset.isEmpty()) {
              Iterator<?> it = listFilesOffset.iterator();
                while (it.hasNext()) {
                  ArquivoDownload confFile = (ArquivoDownload) it.next();
                  String fileNameAbrev = confFile.getDescricao();
                  
                  // link para download do arquivo
                  String linkFileName = confFile.getNome();
                  String tam = confFile.getTamanho();
                  String data = confFile.getData();
        
                  // recupera dados da consignatária
                  String csaNome = confFile.getEntidade();
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent(fileNameAbrev)%></td>
            <td><%=TextHelper.forHtmlContent(csaNome)%></td>
            <td><%=TextHelper.forHtmlContent(tam)%></td>
            <td><%=TextHelper.forHtmlContent(data)%></td>
            <td> 
              <%if(usuExportaXML){ %>    
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
                        <a class="dropdown-item" href="#" onClick="javascript:fazDownload('<%=TextHelper.forHtmlContent(linkFileName)%>');"><hl:message key="rotulo.acoes.download.arquivo"/></a>
                        <a class="dropdown-item" href="#" onClick="javascript:downLayout('<%=TextHelper.forHtmlContent(linkFileName)%>');"><hl:message key="rotulo.acoes.download.leiaute"/></a>
                      </div> 
                  </div>
                </div>       
              <%}else{%>
                <a href="#no-back" onClick="javascript:fazDownload('<%=TextHelper.forHtmlContent(linkFileName)%>');" aria-label='<hl:message key="mensagem.download.arquivo.configuracao.clique.aqui"/>'><hl:message key="rotulo.acoes.download"/></a>
              <% } %>
            </td>
          </tr>
                   
               <%}
              } else {
              %>
                <tr>
                  <td colspan="5">&nbsp;<hl:message key="mensagem.informe.arquivo.conf.nao.encontrado"/></td>
                </tr>
              <% } %>
        </tbody>
      </table>
    </div> 
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script> 
  	function fazDownload(linkFileName) {
            postData('../v3/consultarArquivoXml?acao=downloadArquivo&arquivo_nome=' + encodeURIComponent(linkFileName) + '&tipo=xml&entidade=sup&skip_history=true&<%=SynchronizerToken.generateToken4URL(request)%>','download');
        }
      
        function downLayout(linkFileName) {
            postData('../v3/downloadLayoutXml?acao=downloadLayoutXml&arquivo_nome=' + encodeURIComponent(linkFileName) + '&tipo=xml&entidade=sup&skip_history=true&<%=SynchronizerToken.generateToken4URL(request)%>','download');
 	 }
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
