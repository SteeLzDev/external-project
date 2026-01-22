<%--
* <p>Title: simulacao</p>
* <p>Description: Página de resultado da simulação de empréstimos</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> svcRenegociacao = (List <TransferObject>) request.getAttribute("svcRenegociacao");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.simular.renegociacao.titulo"/>
</c:set>
<c:set var="bodyContent">
  <div class="card">
  <div class="card-header">
    <h2 class="card-header-title"><hl:message key="rotulo.simulacao.lista.servico.disponiveis.titulo"/></h2>
  </div>
  <div class="card-body table-responsive">
    <table class="table table-striped table-hover">
      <thead>
        <tr>
          <th> <hl:message key="rotulo.descricao.servico"/></th>
          <th> <hl:message key="rotulo.acoes"/></th>
        </tr>
      </thead>
      <tbody>
        <% if (svcRenegociacao != null && !svcRenegociacao.isEmpty()) { %>                                
          <%                          
            Iterator it = svcRenegociacao.iterator();
            CustomTransferObject next = null;
            String link = null;
            String label = null;
            while (it.hasNext()) {
              next = (CustomTransferObject) it.next();
              link = SynchronizerToken.updateTokenInURL(next.getAttribute("link").toString(), request);;
              label = next.getAttribute("label").toString().toUpperCase();
          %>
             <tr>
               <td>
                 <%=TextHelper.forHtmlContent(label)%>
               </td>
               <td>
                  <a href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(link)%>'); return false;"><hl:message key="rotulo.acoes.selecionar"/></a>
               </td>
             </tr>
          <%
            }
          %>
        <%} else { %>
          <tr class="li">
             <td colspan="2"><hl:message key="mensagem.erro.simulacao.servico.nao.encontrado"/></td>
          </tr>
        <%} %>
      </tbody>
      <tfoot>
        <tr>
          <td colspan="3"><hl:message key="rotulo.simulacao.lista.servico.disponiveis.rodape"/>
          </td>
        </tr>
      </tfoot>
    </table>
  </div>
</div>
<div class="btn-action">
  <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>"><hl:message key="rotulo.botao.cancelar"/></a>
</div>
</c:set>

<c:set var="javascript">
<script type="text/JavaScript">
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>