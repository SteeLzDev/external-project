<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  List<?> menus = (List<?>) request.getAttribute("menus");
%>
<c:set var="title">
  <hl:message key="rotulo.lst.menu.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.lst.menu.titulo"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
        <tr>
          <th scope="col"><hl:message key="rotulo.lst.menu.descricao"/></th>
          <th scope="col"><hl:message key="rotulo.lst.menu.sequencia"/></th>
          <th scope="col"><hl:message key="rotulo.lst.menu.situacao"/></th>
          <th scope="col"><hl:message key="rotulo.acoes"/></th>
        </tr>               
      </thead>
      <tbody>        
  <%=JspHelper.msgRstVazio(menus.size()==0, 4, responsavel)%>
  <%
    Iterator<?> it = menus.iterator();
    while (it.hasNext()) {
        String classStatusMenu ="";
        TransferObject to = (TransferObject) it.next();
        String mnuCodigo = to.getAttribute(Columns.MNU_CODIGO).toString();
        String mnuDescricao = to.getAttribute(Columns.MNU_DESCRICAO).toString();
        String mnuSequencia = to.getAttribute(Columns.MNU_SEQUENCIA).toString();
        String mnuAtivo = to.getAttribute(Columns.MNU_ATIVO).toString();
        String msgBloquearDesbloquear = "";
        String msgStatusMenu = "";
        if (mnuAtivo.equals("1")) {
            msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.bloquear.clique.aqui", responsavel);
            msgStatusMenu = ApplicationResourcesHelper.getMessage("rotulo.desbloqueado.empresa.correspondente", responsavel);
        } else {
            classStatusMenu ="block";
            msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.desbloquear.clique.aqui", responsavel);
            msgStatusMenu = ApplicationResourcesHelper.getMessage("rotulo.bloqueado.empresa.correspondente", responsavel);
        }
  %>
        <tr>
          <td><%=TextHelper.forHtmlContent(mnuDescricao)%></td>
          <td><%=TextHelper.forHtmlContent(mnuSequencia)%></td>
          <td class="<%=TextHelper.forHtmlAttribute(classStatusMenu)%>"><%=TextHelper.forHtmlContent(msgStatusMenu)%></td>                                
          <td><a href="#no-back" onClick="postData('../v3/editarMenu?acao=listarItemMenu&MNU_CODIGO=<%=TextHelper.forJavaScriptAttribute(mnuCodigo)%>&MNU_DESCRICAO=<%=TextHelper.forJavaScript(mnuDescricao)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a></td>
        </tr>
  <%} %>             
      </tbody>
      </table>      
    </div>
  </div>
  
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" ><hl:message key="rotulo.botao.cancelar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>  
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>