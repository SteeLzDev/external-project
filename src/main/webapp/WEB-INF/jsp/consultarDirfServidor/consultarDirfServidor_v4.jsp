<%--
* <p>Title: consultarDirfServidor.jsp</p>
* <p>Description: Interface para a consulta de DIRF</p>
* <p>Copyright: Copyright (c) 2002-2019</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: igor.lucas $
* $Revision: 26179 $
* $Date: 2019-02-07 14:41:47 -0200 (qui, 07 fev 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String rseMatricula = (String) request.getAttribute("rseMatricula");
String serNome = (String) request.getAttribute("serNome");

String link = (String) request.getAttribute("link");

List<Short> anosDisponiveis = (List<Short>) request.getAttribute("anosDisponiveis");

Short anoAtual = (Short) request.getAttribute("anoAtual");
Short anoAnterior = (Short) request.getAttribute("anoAnterior");
Short anoProximo = (Short) request.getAttribute("anoProximo");

boolean exibirPdf = (Boolean) request.getAttribute("exibirPdf");
%>
<c:set var="title">
  <%=ApplicationResourcesHelper.getMessage("rotulo.servidor.consultar.dirf.ano.calendario.titulo", responsavel, String.valueOf(anoAtual)) %>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <div class="row">
        <div class="col-sm-7 float-left pt-3">
          <h2 class="card-header-title"><%=TextHelper.forHtmlContent(rseMatricula)%>  <%=TextHelper.forHtmlContent(serNome)%></h2>
        </div>
        <div class="col-sm-5 float-end">
          <div class="row">
           <div class="form-group text-right col-md-4 mb-0 pt-3">
             <label class="label-for-white" for="periodoCcq"><hl:message key="rotulo.servidor.contracheque.periodo"/></label>
           </div>
           <div class="form-group col-md-8 mb-0 pl-0">
            <select class="form-control form-select" id="periodoCcq" name="periodoCcq" onChange="postData('<%=TextHelper.forJavaScriptAttribute(link)%>&ANO='+this.value);">
              <option value="" selected><hl:message key="rotulo.servidor.consultar.dirf.selecione.ano.calendario"/></option>
              <%
              if (anosDisponiveis != null && anosDisponiveis.size() > 0) {
                for (Short ano : anosDisponiveis) {
                %>
                  <option value="<%=(Short) ano%>" <%= (anoAtual != null && anoAtual.equals(ano)) ? "SELECTED" : "" %>><%=(Short) ano%></option>
                <% 
                }
              }  
              %>
            </select>
          </div>
        </div>
      </div>
    </div>
    </div>
    <div class="card-body">
      <div class="row justify-content-center">
        <% if (exibirPdf) { %>
        
          <object data="../v3/consultarDirfServidor?acao=exibir&ANO=<%=anoAtual%>" type="application/pdf" width="100%" height="600"></object>
          
        <% } else { %>
          <h6><hl:message key="mensagem.erro.servidor.consultar.dirf.nao.encontrada"/></h6>
        <% } %>
      </div>  
    </div>
  </div>
  <div class="btn-action">
    <a href="#no-back" class="btn btn-outline-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute( responsavel.isSer() ? "../v3/carregarPrincipal" : SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request) )%>')"><hl:message key="rotulo.botao.voltar"/> </a>
  </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>