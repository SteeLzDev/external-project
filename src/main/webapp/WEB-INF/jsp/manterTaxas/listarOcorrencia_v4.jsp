<%--
* <p>Title: listarOcorrencia_v4.jsp</p>
* <p>Description: Página de listagem de ocorrência de coeficiente</p>
* <p>Copyright: Copyright (c) 2002-2014</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> coeficientes = (List) request.getAttribute("coeficientes");
String linkRetorno = (String) request.getAttribute("linkRetorno");
String svcCodigo = (String) request.getAttribute("svcCodigo");
String csaCodigo = (String) request.getAttribute("csaCodigo");
ConsignatariaTransferObject consignataria = (ConsignatariaTransferObject) request.getAttribute("consignataria");
TransferObject servico = (TransferObject) request.getAttribute("servico");
%>
<c:set var="title">
  <hl:message key="rotulo.ocorrencia.coeficiente.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-ms-12">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.ocorrencia.coeficiente.resultado.consulta"/></h2>
        </div>
        <dl class= "row data-list firefox-print-fix pt-2">
          <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/>:</dt><dd class="col-6"><%=TextHelper.forHtmlContent(consignataria.getCsaIdentificador())%> - <%=TextHelper.forHtmlContent(consignataria.getCsaNome())%></dd>
          <dt class="col-6"><hl:message key="rotulo.servico.singular"/>:</dt><dd class="col-6"><%=TextHelper.forHtmlContent(servico.getAttribute(Columns.SVC_IDENTIFICADOR).toString())%> - <%=TextHelper.forHtmlContent(servico.getAttribute(Columns.SVC_DESCRICAO).toString())%></dd>
        </dl>
      </div>
    </div>
    <div class="col-sm-12 col-ms-12">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.ocorrencia.coeficiente.historico.titulo"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
            <table id="dataTables" class="table table-striped table-hover">
              <thead>
                 <tr>
                   <th scope="col"><hl:message key="rotulo.ocorrencia.coeficiente.tipo"/></th>
                   <th scope="col"><hl:message key="rotulo.ocorrencia.coeficiente.data"/></th>
                   <th scope="col"><hl:message key="rotulo.ocorrencia.coeficiente.responsavel"/></th>
                   <th scope="col"><hl:message key="rotulo.ocorrencia.coeficiente.observacao"/></th>
                   <th scope="col"><hl:message key="rotulo.ocorrencia.coeficiente.ip.acesso"/></th>
                 </tr>
               </thead>
               <tbody>
               <%=JspHelper.msgRstVazio((coeficientes == null || coeficientes.isEmpty()), 5, responsavel)%>
<%
             if (!coeficientes.isEmpty()) {
                Iterator<TransferObject> it = coeficientes.iterator();
                while (it.hasNext()) {
                    TransferObject cto = it.next();

                    cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, responsavel);

                    String tocDescricao = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
                    String ocfData = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.OCF_DATA));
                    String loginResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                    String ocfResponsavel = (loginResponsavel.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO)) && cto.getAttribute(Columns.USU_TIPO_BLOQ) != null) ?
                                    cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : loginResponsavel;
                    String ocfObs = (String) cto.getAttribute(Columns.OCF_OBS);
                    String ocfIpAcesso = cto.getAttribute(Columns.OCF_IP_ACESSO) != null ?  cto.getAttribute(Columns.OCF_IP_ACESSO).toString() : "";
%>
                   <tr>
                     <td><%=TextHelper.forHtmlContent(tocDescricao)%></td>
                     <td><%=TextHelper.forHtmlContent(ocfData)%></td>
                     <td><%=TextHelper.forHtmlContent(ocfResponsavel)%></td>
                     <td><%=JspHelper.formataMsgOca(ocfObs)%></td>
                     <td><%=TextHelper.forHtmlContent(ocfIpAcesso)%></td>
                   </tr>
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
      <hl:htmlinput name="SVC_CODIGO"  type="hidden" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>" />
      <hl:htmlinput name="CSA_CODIGO"  type="hidden" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>" />
   </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>   
