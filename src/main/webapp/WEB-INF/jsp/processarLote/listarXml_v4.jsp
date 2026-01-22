<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"    tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"   uri="/html-lib" %>
<%@ taglib prefix="fl"   uri="/function-lib" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.xml.XmlHelper" %>
<%@ page import="com.zetra.econsig.parser.*" %>
<%@ page import="com.zetra.econsig.parser.config.DocumentoTipo" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
String pathXmlDefault = (String) request.getAttribute("pathXmlDefault");
String pathXml        = (String) request.getAttribute("pathXml");
String csa_codigo     = (String) request.getAttribute("csa_codigo");
String cor_codigo     = (String) request.getAttribute("cor_codigo");
List nomesTabelas     = (List) request.getAttribute("nomesTabelas");
boolean xmlDefault    = (boolean) request.getAttribute("xmlDefault");
%>
<c:set var="title">
  <hl:message key="rotulo.processar.lote.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
      <div class="card">
        <div class="card-header pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.xml.lote.disponiveis"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.xml.lote.opcoes.layout"/></th>
                <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
              <%
                if (nomesTabelas.size() == 0){
              %>
              <tr><td colspan='2'><hl:message key="mensagem.erro.nenhum.arquivo.encontrado"/></td></tr>
              <%
                } else {
                  int i = 0;
                  int inicial = 0;
                  Iterator it = nomesTabelas.iterator();
                  String nome = null;
                  while (it.hasNext()) {
                    nome = it.next().toString();
                    //Lê XML e busca ID
                    FileInputStream entrada = null;
                    if (xmlDefault) {
                      entrada = new FileInputStream(pathXmlDefault + File.separatorChar + nome + "_entrada.xml");
                    } else {
                      entrada = new FileInputStream(pathXml + File.separatorChar + nome + "_entrada.xml");
                    }
                    DocumentoTipo doc = XmlHelper.unmarshal( entrada );
              %>
              <tr>
                <td><%=TextHelper.forHtmlContent((doc.getID() != null) ? doc.getID() : nome)%></td>
                <td>
                  <div class="actions"> 
                    <a class="ico-action" href="#" onClick="postData('../v3/processarLote?acao=listarArquivosImportacao&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&COR_CODIGO=<%=TextHelper.forJavaScript(cor_codigo)%>&XML=<%=TextHelper.forJavaScript(nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" role="button">
                      <div class="form-inline">
                        <span class="mr-1" title="" aria-label="<hl:message key='rotulo.acoes.selecionar'/>">
                          <svg><use xlink:href="#i-confirmar"></use></svg>
                        </span> 
                        <hl:message key="rotulo.acoes.selecionar"/> 
                      </div>
                    </a>
                  </div>
                </td>
              </tr>
              <%  
                  }
              %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="4">
                  <hl:message key="rotulo.lote.listagem.leiaute"/>

                </td>
              </tr>
            </tfoot>
              <% 
                }
              %>
          </table>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" HREF="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.teclado.virtual.cancelar"/></a>
      </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
	f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>