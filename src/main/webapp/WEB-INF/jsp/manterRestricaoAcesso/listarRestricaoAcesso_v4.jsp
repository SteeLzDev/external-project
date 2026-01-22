<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Time" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>

<%@ taglib uri="/html-lib" prefix="hl" %>
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditarRra = (boolean) request.getAttribute("podeEditarRra");
List<TransferObject> restricoes = (List<TransferObject>) request.getAttribute("restricoes");

%>
<c:set var="title">
   <hl:message key="rotulo.lista.regra.restricao.acesso.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <%if (podeEditarRra) {%>
  <div class="btn-action">
    <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/restricaoAcesso?acao=editar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.lista.regra.restricao.acesso.novo"/></a>
  </div>
  <%}%>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.lista.regra.restricao.acesso.titulo.tabela"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.descricao.restricao.acesso"/></th>
            <th scope="col"><hl:message key="rotulo.permissao.restricao.acesso"/></th>
            <th scope="col"><hl:message key="rotulo.permissao.restricao.inicio"/></th>
            <th scope="col"><hl:message key="rotulo.permissao.restricao.fim"/></th>
            <th scope="col"><hl:message key="rotulo.permissao.restricao.papel"/></th>
            <th scope="col"><hl:message key="rotulo.permissao.restricao.data"/></th>
            <th scope="col"><hl:message key="rotulo.permissao.restricao.dia.semana"/></th>
            <th scope="col"><hl:message key="rotulo.permissao.restricao.dias.uteis"/></th>
            <%if (podeEditarRra) {%>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
            <%} %>
          </tr>
        </thead>
        <tbody>
        <%=JspHelper.msgRstVazio(restricoes.size()==0, (podeEditarRra) ? "9" : "8", "lp")%>
        <%
           Iterator it = restricoes.iterator();
           while (it.hasNext()) {
          	CustomTransferObject restricao = (CustomTransferObject)it.next();  
          	String rraCodigo = (String) restricao.getAttribute(Columns.RRA_CODIGO);
          	String papCodigo = (String) restricao.getAttribute(Columns.RRA_PAP_CODIGO);
          	
          	if (!TextHelper.isNull(papCodigo)) {
          		if(papCodigo.equals(CodedValues.PAP_CONSIGNANTE)) {
          			papCodigo = AcessoSistema.ENTIDADE_CSE;
          		} else if(papCodigo.equals(CodedValues.PAP_CONSIGNATARIA)) {
          			papCodigo = AcessoSistema.ENTIDADE_CSA;
          		} else if(papCodigo.equals(CodedValues.PAP_CORRESPONDENTE)) {
          			papCodigo = AcessoSistema.ENTIDADE_COR;
          		} else if(papCodigo.equals(CodedValues.PAP_ORGAO)) {
          			papCodigo = AcessoSistema.ENTIDADE_ORG;
          		} else if(papCodigo.equals(CodedValues.PAP_SERVIDOR)) {
          			papCodigo = AcessoSistema.ENTIDADE_SER;
          		} else if(papCodigo.equals(CodedValues.PAP_SUPORTE)) {
          			papCodigo = AcessoSistema.ENTIDADE_CSE;
          		}
          	} else {
          		papCodigo = ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel);
          	}
          	
          	String funDescricao = (String) restricao.getAttribute(Columns.FUN_DESCRICAO);
          	funDescricao = (TextHelper.isNull(funDescricao)) ? ApplicationResourcesHelper.getMessage("rotulo.geral.singular", responsavel) : funDescricao;
              String rraDescricao = (String) restricao.getAttribute(Columns.RRA_DESCRICAO);
              Time rraHoraIni = (Time) restricao.getAttribute(Columns.RRA_HORA_INICIO);
              Time rraHoraFim = (Time) restricao.getAttribute(Columns.RRA_HORA_FIM);
              Date data = (Date) restricao.getAttribute(Columns.RRA_DATA);
              Short diaSemana = (Short) restricao.getAttribute(Columns.RRA_DIA_SEMANA);
              String semana = null;
              if (diaSemana != null) {
              	semana = DateHelper.getWeekDayName(diaSemana.intValue());
              }
              String diaUtil = (String) restricao.getAttribute(Columns.RRA_DIAS_UTEIS);
        %>
       <tr>   
          <td ><%=TextHelper.forHtmlContent(rraDescricao)%></td>
          <td ><%=TextHelper.forHtmlContent(funDescricao)%></td>
          <td><%=TextHelper.forHtmlContent(rraHoraIni)%></td>
          <td><%=TextHelper.forHtmlContent(rraHoraFim)%></td>
          <td><%=TextHelper.forHtmlContent(papCodigo)%></td>
          <td><%=TextHelper.forHtmlContent((data != null) ? DateHelper.toDateString(data) : "")%></td>
          <td><%=TextHelper.forHtmlContent((!TextHelper.isNull(semana)) ? semana : "")%></td>
          <td><%=TextHelper.forHtmlContent((!TextHelper.isNull(diaUtil)) ? diaUtil : "")%></td>
          <% if (podeEditarRra) { %>
              <td><a href="#no-back" onClick="ExcluirEntidade('<%=TextHelper.forJavaScript(rraCodigo)%>', '', '../v3/restricaoAcesso?acao=excluir&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(rraDescricao)%>')"><hl:message key="rotulo.acoes.excluir"/></a></td>
          <%} %>
        </tr>
        <%
           }
        %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="9"><hl:message key="rotulo.lista.regra.restricao.acesso.listagem"/> <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
    <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>  
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>  