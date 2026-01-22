<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.parser.config.DocumentoTipo" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.xml.XmlHelper"%>
<%@ page import="com.zetra.econsig.parser.*" %>
<%
String csaCodigo = (String) request.getAttribute("csaCodigo");
List<?> nomesTabelas = (List<?>) request.getAttribute("nomesTabelas");
boolean xmlDefault = (boolean) request.getAttribute("xmlDefault");
String pathXmlDefault = (String) request.getAttribute("pathXmlDefault");
String pathXml = (String) request.getAttribute("pathXml");
String linkRet = (String) request.getAttribute("linkRet");
boolean conciliacaoMultipla = (boolean) request.getAttribute("conciliacaoMultipla");
%>
<c:set var="title">
   <hl:message key="rotulo.processar.conciliacao.arquivo.multiplo.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>  
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="<%= conciliacaoMultipla ? "rotulo.processar.arquivo.conciliacao.arquivo.multiplo.titulo" : "rotulo.processar.arquivo.conciliacao.titulo" %>"/></h2>
    </div> 
    <div class="card-body table-responsive ">
      <table class="table table-striped table-hover"> 
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.opcoes.leiaute.arquivos.conciliacao"/></th>
            <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <%
        if (nomesTabelas.size() == 0){
        %>
          <tr>
            <td colspan="2"><hl:message key="rotulo.lst.arq.generico.encontrado"/></td>
          </tr>
        <%
        } else {
          int i = 0;
          Iterator <?> it = nomesTabelas.iterator();
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
              <td><a href="#no-back" onClick="postData('../v3/<%= conciliacaoMultipla ? "conciliarArquivoMultiplo" : "conciliarArquivo" %>?acao=selecionar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&XML=<%=TextHelper.forJavaScript(nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.selecionar"/></a></td>
            </tr>
          <%
              }
            }
          %>   
        <tfoot>
          <tr>
            <td colspan="50">
              <hl:message key="rotulo.arquivos.conciliacao.leiaute"/>
            </td>
          </tr>
        </tfoot>  
      </table>
    </div> 
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkRet)%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a> 
  </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>