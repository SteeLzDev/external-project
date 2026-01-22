<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.List"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page import="com.zetra.econsig.helper.web.v3.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String usuCodigo = (String) request.getAttribute("usuCodigo");;
String serCodigo = (String) request.getAttribute("serCodigo");;
List<?> senhasAutorizacao = (List<?>) request.getAttribute("senhasAutorizacao"); 

boolean podeEditarAutorizacao = responsavel.temPermissao(CodedValues.FUN_GESTOR_EDITA_SENHA_MULT_AUT_SER);
%>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.listar.senha.autorizacao.servidor.titulo"/>
</c:set>
<c:set var="bodyContent">
 <%if (podeEditarAutorizacao) {%>
  <div class="row"> 
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end show">
        <%String acaoGerarSenhaAut = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.acao.gerar", responsavel); %>
        <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/modificarSenhaAutorizacao?acao=alterar&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(serCodigo)%>&USU_CODIGO=<%=TextHelper.forJavaScript(usuCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.alterar.senha.usuario.nova"/></a>
      </div>
    </div>
  </div>
  <%}%>
    
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.senha.servidor.autorizacao.plural"/></h2>
    </div>
    <div class="card-body table-responsive ">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col" width="20%"><hl:message key="rotulo.listar.senha.autorizacao.servidor.numero.sem.abrevia"/></th>
            <th scope="col" width="20%"><hl:message key="rotulo.listar.senha.autorizacao.servidor.validade"/></th>
            <th scope="col" width="20%"><hl:message key="rotulo.listar.senha.autorizacao.servidor.operacoes"/></th>
            <%if (podeEditarAutorizacao) {%>
              <th scope="col" width="5%"><hl:message key="rotulo.listar.senha.autorizacao.servidor.acoes.cancelar"/></th>              
            <%} %>
          </tr>
        </thead>             
        <tbody>
        <%if (podeEditarAutorizacao) {%>
          <%=JspHelper.msgRstVazio(senhasAutorizacao.size()==0, "4", "lp")%>
        <%} else { %>
          <%=JspHelper.msgRstVazio(senhasAutorizacao.size()==0, "3", "lp")%>
        <%} %>
        <%
          int count = 0;
          Iterator<?> itSenhasAut = senhasAutorizacao.iterator();
          String acaoCancelarSenhaAut = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.acao.cancelar", responsavel);
          while (itSenhasAut.hasNext()) {
              TransferObject senhaAutorizacao = (TransferObject) itSenhasAut.next();
              Short qtdOperacoesSenhaAutorizacao = (Short) senhaAutorizacao.getAttribute(Columns.SAS_QTD_OPERACOES);
              Date dataExpSenhaAutorizacao = (Date) senhaAutorizacao.getAttribute(Columns.SAS_DATA_EXPIRACAO);
              String senhaCrypt = (String) senhaAutorizacao.getAttribute(Columns.SAS_SENHA);
          
        %>
          <tr>
            <td><%=TextHelper.forHtmlContent(++count)%></td>
            <td><%=DateHelper.toDateString(dataExpSenhaAutorizacao)%></td>
            <td><%=TextHelper.forHtmlContent(qtdOperacoesSenhaAutorizacao)%></td>
            <%if (podeEditarAutorizacao) {%>
              <td>
                <a href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute("../v3/modificarSenhaAutorizacao?acao=cancelar&SENHA=" + senhaCrypt + "&SER_CODIGO=" + serCodigo + "&USU_CODIGO=" + usuCodigo + "&" + SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.cancelar"/></a>
              </td>
            <%} %>
          </tr>
        <% } %>
        </tbody>
  </table>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    f0 = document.forms[0];
  </script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4> 
      
                     