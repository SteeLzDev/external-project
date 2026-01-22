<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<CalendarioTO> calendario = (List<CalendarioTO>) request.getAttribute("calendario");
String nomeMes = (String) request.getAttribute("nomeMes");
String anterior = (String) request.getAttribute("anterior");
String proximo = (String) request.getAttribute("proximo");
String rotuloCalendarioEditarCliqueAqui = (String) request.getAttribute("rotuloCalendarioEditarCliqueAqui");

%>
<c:set var="title">
  <hl:message key="rotulo.calendario.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(nomeMes)%></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <% if (responsavel.temPermissao(CodedValues.FUN_EDT_CALENDARIO)) {%>
              <th scope="col"><hl:message key="rotulo.calendario.data"/></th>
              <th scope="col"><hl:message key="rotulo.calendario.descricao"/></th>
              <th scope="col"><hl:message key="rotulo.calendario.dia.util"/></th>
              <th scope="col"><hl:message key="rotulo.prazo.acoes"/></th>
            <% } else { %>
              <th scope="col"><hl:message key="rotulo.calendario.data"/></th>
              <th scope="col"><hl:message key="rotulo.calendario.descricao"/></th>
              <th scope="col"><hl:message key="rotulo.calendario.dia.util"/></th>
            <% } %>
          </tr>
        </thead>
        <tbody>
          <%=(String)JspHelper.msgRstVazio(calendario.size()==0, "13", "lp") %>
          <%
            int rowCount = 0;
            Iterator<CalendarioTO> it = calendario.iterator();
            while (it.hasNext()) {
                TransferObject calendarioTO = (TransferObject) it.next();
              java.util.Date cabData = (java.util.Date) calendarioTO.getAttribute(Columns.CAB_DATA);
              String cabDescricao    = (String) calendarioTO.getAttribute(Columns.CAB_DESCRICAO);
              String cabDiaUtil      = (String) calendarioTO.getAttribute(Columns.CAB_DIA_UTIL);
              cabDescricao = DateHelper.getWeekDayName(cabData) + (cabDescricao.length() > 0 ? " / <b>" + TextHelper.forHtmlContent(cabDescricao) + "</b>" : "");
          %>
             <tr>
              <td><%=DateHelper.toDateString(cabData)%></td>
              <td><%=(String)cabDescricao.toUpperCase()%></td>
              <td><%=cabDiaUtil.equals("S") ? "<b>"+ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel)+"</b>" : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel) %></td>
              <% if (responsavel.temPermissao(CodedValues.FUN_EDT_CALENDARIO)) { %>
              <td>
               <a href="#no-back" onClick="postData('../v3/editarCalendario?acao=editar&CAB_DATA=<%=TextHelper.forJavaScriptAttribute(DateHelper.format(cabData, "yyyy-MM-dd"))%>&<%=SynchronizerToken.generateToken4URL(request)%>')">
                <hl:message key="rotulo.acoes.editar"/>
               </a>
              </td>
             <%}%>
             </tr>
            <%                    
              }
            %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="3"><hl:message key="rotulo.prazo.listage.prazo.disponivel"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
    <div class="card-footer">
      <ul class="pagination justify-content-end">
        <li class="page-item justify-content-end"><a class="page-link" href="#no-back" onClick="postData('../v3/editarCalendario?acao=iniciar&ANO-MES=<%=TextHelper.forJavaScriptAttribute(anterior)%>&<%=(String)SynchronizerToken.generateToken4URL(request)%>')">«</a></li>
        <li class="page-item justify-content-end disabled"><a class="page-link" href="#"><%=TextHelper.forHtmlContent(nomeMes)%></a></li>
        <li class="page-item justify-content-end"><a class="page-link" href="#no-back" onClick="postData('../v3/editarCalendario?acao=iniciar&ANO-MES=<%=TextHelper.forJavaScriptAttribute(proximo)%>&<%=(String)SynchronizerToken.generateToken4URL(request)%>')">»</a></li>
      </ul>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>