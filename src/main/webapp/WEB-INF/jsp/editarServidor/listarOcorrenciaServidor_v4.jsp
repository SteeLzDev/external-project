<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema ocs_responsavel = JspHelper.getAcessoSistema(request);
String serCodigo = JspHelper.verificaVarQryStr(request, "SER_CODIGO");
String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
String serNome = (String) request.getAttribute("serNome");
String rseMatricula = (String) request.getAttribute("rseMatricula");

List<?> lstOcorrencias = (List<?>) request.getAttribute("lstOcorrencias");
%>
<c:set var="title">
  <hl:message key="rotulo.servidor.lista.ocorrencias.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-7 col-md">
      <div class="card">
        <div class="card-header">
        <%if (!TextHelper.isNull(serNome) && !TextHelper.isNull(rseMatricula)) {%>
        <h2 class="card-header-title"><%=TextHelper.forHtmlContent(rseMatricula)%> - <%=TextHelper.forHtmlContent(serNome)%></h2>
        <% } else { %>
          <h2 class="card-header-title"><hl:message key="rotulo.servidor.historico.titulo.lower"/></h2>
        <% } %>
        </div>
        <div class="card-body table-responsive p-0">
            <table class="table table-striped table-hover">
              <thead>
                 <tr>
                   <th scope="col" width="10%"><hl:message key="rotulo.servidor.historico.data.lower"/></th>
                   <th scope="col" width="10%"><hl:message key="rotulo.servidor.historico.responsavel.lower"/></th>
                   <th scope="col" width="20%"><hl:message key="rotulo.servidor.historico.tipo.lower"/></th>
                   <th scope="col" width="50%"><hl:message key="rotulo.servidor.historico.descricao.lower"/></th>
                   <th scope="col" width="10%"><hl:message key="rotulo.servidor.historico.ip.acesso.lower"/></th>
                 </tr>
               </thead>
               <tbody>
               <%=JspHelper.msgRstVazio((lstOcorrencias == null || lstOcorrencias.isEmpty()), 5, ocs_responsavel)%>
              <%
              if (lstOcorrencias != null && !lstOcorrencias.isEmpty()) {
                  Iterator<?> itHistorico = lstOcorrencias.iterator();
                  if (itHistorico.hasNext()) {            
                    int i = 0;
                    while (itHistorico.hasNext()) { 
                       CustomTransferObject cto = (CustomTransferObject) itHistorico.next();
                    
                       cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, ocs_responsavel);
                    
                       Date dataAux = (cto.getAttribute(Columns.OCS_DATA) != null) ? (Date) cto.getAttribute(Columns.OCS_DATA) : (Date) cto.getAttribute(Columns.ORS_DATA);
                       String ocsData = DateHelper.toDateTimeString(dataAux);
      
                       String loginOcsResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                       String ocsResponsavel = (loginOcsResponsavel.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO)) && cto.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? 
                                    cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : loginOcsResponsavel;
                       String ocsTipo = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
                       String ocsObs = (cto.getAttribute(Columns.OCS_OBS) != null) ? cto.getAttribute(Columns.OCS_OBS).toString() : (String) cto.getAttribute(Columns.ORS_OBS);
                       String ocsIpAcesso = cto.getAttribute(Columns.OCS_IP_ACESSO) != null ?  cto.getAttribute(Columns.OCS_IP_ACESSO).toString() : (cto.getAttribute(Columns.ORS_IP_ACESSO) != null ? (String) cto.getAttribute(Columns.ORS_IP_ACESSO) : "" );
              %>
                   <tr class="<%=TextHelper.forHtmlAttribute(i++%2==0?"Li":"Lp")%>">
                     <td><%=TextHelper.forHtmlContent(ocsData)%></td>
                     <td><%=TextHelper.forHtmlContent(ocsResponsavel)%></td>
                     <td><%=TextHelper.forHtmlContent(ocsTipo)%></td>
                     <td><%=JspHelper.formataMsgOca(ocsObs)%></td>
                     <td><%=TextHelper.forHtmlContent(ocsIpAcesso)%></td>
                   </tr>
              <%  } %>
              <%  
                 }
               }
              %>   
               </tbody>   
               <tfoot>
                   <tr>
                      <td colspan="5"><hl:message key="rotulo.servidor.historico.listagem"/>
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
    <div class="btn-action" aria-label='<hl:message key="rotulo.botoes.acao.pagina"/>'>  
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
