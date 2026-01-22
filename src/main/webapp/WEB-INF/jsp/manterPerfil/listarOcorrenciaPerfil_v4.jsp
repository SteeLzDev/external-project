<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String perCodigo = JspHelper.verificaVarQryStr(request, "PER_CODIGO");

List<?> lstOcorrencias = (List<?>) request.getAttribute("lstOcorrencias");
%>
<c:set var="title">
  <hl:message key="rotulo.perfil.lista.ocorrencias.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-7 col-md">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.perfil.historico.titulo.lower"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
            <table class="table table-striped table-hover">
              <thead>
                 <tr>
                   <th scope="col" width="10%"><hl:message key="rotulo.perfil.historico.data.lower"/></th>
                   <th scope="col" width="10%"><hl:message key="rotulo.perfil.historico.responsavel.lower"/></th>
                   <th scope="col" width="20%"><hl:message key="rotulo.perfil.historico.tipo.lower"/></th>
                   <th scope="col" width="50%"><hl:message key="rotulo.perfil.historico.descricao.lower"/></th>
                   <th scope="col" width="10%"><hl:message key="rotulo.perfil.historico.ip.acesso.lower"/></th>
                 </tr>
               </thead>
               <tbody>
               <%=JspHelper.msgRstVazio((lstOcorrencias == null || lstOcorrencias.isEmpty()), 5, responsavel)%>
              <%
              if (lstOcorrencias != null && !lstOcorrencias.isEmpty()) {
                  Iterator<?> itHistorico = lstOcorrencias.iterator();
                  if (itHistorico.hasNext()) {            
                    int i = 0;
                    while (itHistorico.hasNext()) { 
                       CustomTransferObject cto = (CustomTransferObject) itHistorico.next();
                    
                       cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, responsavel);
                    
                       Date dataAux = (Date) cto.getAttribute(Columns.OPR_DATA);
                       String oprData = DateHelper.toDateTimeString(dataAux);
      
                       String loginOprResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                       String oprTipo = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
                       String oprObs = (String) cto.getAttribute(Columns.OPR_OBS);
                       String oprIpAcesso = (cto.getAttribute(Columns.OPR_IP_ACESSO) != null ? (String) cto.getAttribute(Columns.OPR_IP_ACESSO) : "" );
              %>
                   <tr class="<%=TextHelper.forHtmlAttribute(i++%2==0?"Li":"Lp")%>">
                     <td><%=TextHelper.forHtmlContent(oprData)%></td>
                     <td><%=TextHelper.forHtmlContent(loginOprResponsavel)%></td>
                     <td><%=TextHelper.forHtmlContent(oprTipo)%></td>
                     <td><%=JspHelper.formataMsgOca(oprObs)%></td>
                     <td><%=TextHelper.forHtmlContent(oprIpAcesso)%></td>
                   </tr>
              <%  } %>
              <%  
                 }
               }
              %>   
               </tbody>   
               <tfoot>
                   <tr>
                      <td colspan="5"><hl:message key="rotulo.perfil.historico.listagem"/>
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
