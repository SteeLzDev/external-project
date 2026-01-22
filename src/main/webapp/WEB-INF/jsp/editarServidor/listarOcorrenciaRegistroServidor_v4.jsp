<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.web.ParamSession"%>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
ParamSession paramSession = (ParamSession) request.getAttribute("paramSession");
CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");
List lstOcorrencias = (List) request.getAttribute("lstOcorrencias");
String linkRetorno = (String) request.getAttribute("linkRetorno");
String rse_codigo = (String) request.getAttribute("rse_codigo");
%>
<c:set var="title">
  <hl:message key="rotulo.servidor.lista.ocorrencias.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-ms-12">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.consultar.margem.resultado"/></h2>
        </div>
        <dl class= "row data-list firefox-print-fix pt-2">
            <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
            <% pageContext.setAttribute("servidor", servidor); %>
            <hl:detalharServidorv4 name="servidor"/>
            <%-- Fim dos dados da ADE --%>
        </dl>
      </div>
    </div>
    <div class="col-sm-12 col-ms-12">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.servidor.historico.titulo.lower"/></h2>
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
               <%=JspHelper.msgRstVazio((lstOcorrencias == null || lstOcorrencias.isEmpty()), 5, responsavel)%>
<%
             if (!lstOcorrencias.isEmpty()) {
                Iterator<TransferObject> it = lstOcorrencias.iterator();
                while (it.hasNext()) {
                    TransferObject cto = it.next();

                    cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, responsavel);

                    String orsData = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.ORS_DATA));

                    String loginOrsResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                    String orsResponsavel = (loginOrsResponsavel.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO)) && cto.getAttribute(Columns.USU_TIPO_BLOQ) != null) ?
                                    cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : loginOrsResponsavel;
                    String orsTipo = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
                    String orsObs = (String) cto.getAttribute(Columns.ORS_OBS);

                    String orsIpAcesso = cto.getAttribute(Columns.ORS_IP_ACESSO) != null ?  cto.getAttribute(Columns.ORS_IP_ACESSO).toString() : "";
%>
                   <tr>
                     <td><%=TextHelper.forHtmlContent(orsData)%></td>
                     <td><%=TextHelper.forHtmlContent(orsResponsavel)%></td>
                     <td><%=TextHelper.forHtmlContent(orsTipo)%></td>
                     <td><%=JspHelper.formataMsgOca(orsObs)%></td>
                     <td><%=TextHelper.forHtmlContent(orsIpAcesso)%></td>
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
      <hl:htmlinput name="RSE_CODIGO"  type="hidden" value="<%=TextHelper.forHtmlAttribute(rse_codigo)%>" />
   </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>   
