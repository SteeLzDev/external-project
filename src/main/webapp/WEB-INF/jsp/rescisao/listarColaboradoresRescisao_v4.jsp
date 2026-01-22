<%--
* <p>Title: listarColaboradoresRescisao_v4</p>
* <p>Description: Listar colaboradores incluídos para rescisão contratual v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

// Pega dados vindo do webController
String tituloPagina = (String) request.getAttribute("tituloPagina");
List<TransferObject> listaRseCandidatoRescisao = (List<TransferObject>) request.getAttribute("listaRseCandidatoRescisao");
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
%>
<c:set var="title">
   ${tituloPagina}
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-rescisao"></use>
</c:set>

<c:set var="bodyContent">
  <% if (!temProcessoRodando) { %>
  <div class="row  d-flex flex-row-reverse mr-0">
    <div class="pull-right">
      <div class="btn-action ">
        <a class="btn btn-primary" id="btnAdicionarRescisao" href="#no-back" onClick="postData('../v3/incluirColaboradorRescisao?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>')">
          <hl:message key="rotulo.botao.adicionar.colaborador.rescisao" /></a>
      </div>
    </div>
  </div>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title">
            ${tituloPagina}
        </h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.matricula.singular"/></th>
              <th scope="col"><hl:message key="rotulo.servidor.nome"/></th>
              <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
              <th scope="col"><hl:message key="rotulo.orgao.singular"/></th>
              <th scope="col"><hl:message key="rotulo.servidor.dataAdmissao"/></th>
              <th scope="col"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
            <%=JspHelper.msgRstVazio(listaRseCandidatoRescisao.size() == 0, 6, responsavel)%>
            <%
            Iterator it = listaRseCandidatoRescisao.iterator();
            while (it.hasNext()) {
              CustomTransferObject registroServidor = (CustomTransferObject) it.next();
              String vrrCodigo = (String) registroServidor.getAttribute(Columns.VRR_CODIGO);
              String rseMatricula = (String) registroServidor.getAttribute(Columns.RSE_MATRICULA);
              String serNome = (String) registroServidor.getAttribute(Columns.SER_NOME);
              String serCpf = (String) registroServidor.getAttribute(Columns.SER_CPF);
              String orgNome = (String) registroServidor.getAttribute(Columns.ORG_NOME);
              String orgIdentificador = (String) registroServidor.getAttribute(Columns.ORG_IDENTIFICADOR);
              String rseDataAdmissao = "";
              if (registroServidor.getAttribute(Columns.RSE_DATA_ADMISSAO) != null) {
                  rseDataAdmissao = DateHelper.toDateString((Date) registroServidor.getAttribute(Columns.RSE_DATA_ADMISSAO));
              }
            %>
            <tr>
              <td><%=TextHelper.forHtmlContent(rseMatricula)%></td>
              <td><%=TextHelper.forHtmlContent(serNome)%></td>
              <td><%=TextHelper.forHtmlContent(serCpf)%></td>
              <td><%=TextHelper.forHtmlContent(orgNome + " - " + orgIdentificador)%></td>
              <td><%=TextHelper.forHtmlContent(rseDataAdmissao)%></td>
              <td>    
                <a href="#no-back" id="btnExcluirRescisao" onClick="confirmarExclusao('../v3/listarColaboradoresRescisao?acao=excluir&<%=Columns.VRR_CODIGO%>=<%=TextHelper.forJavaScriptAttribute(vrrCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                  <hl:message key="rotulo.acoes.excluir" />
                </a>
              </td>
            </tr>
            <% } %>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.listagem.colaborador.rescisao", responsavel) + " - "%>
                <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
      <div class="card-footer">
        <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>
  <% } %>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    <% if (listaRseCandidatoRescisao != null && listaRseCandidatoRescisao.size() > 0) { %>
      <a class="btn btn-primary" href="#no-back" id="btnConfirmarRescisao" onClick="confirmarListaRescisao();">
         <svg width="17">
            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use>
         </svg>
         <hl:message key="rotulo.botao.confirmar"/>
      </a>
    <% } %>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  window.onload = doLoad(<%=(boolean)temProcessoRodando%>);
  
  function doLoad(reload) {
	if (reload) {
	  setTimeout("refresh()", 15*1000);
    }
  }

  function refresh() {
    postData('../v3/listarColaboradoresRescisao?acao=iniciar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
  }

  function confirmarExclusao(link) {
    if (confirm('<hl:message key="mensagem.confirmacao.exclusao.colaborador.rescisao"/>')) {
      postData(link);
    }
    return false;
  }
  
  function confirmarListaRescisao() {
	var link = '../v3/listarColaboradoresRescisao?acao=confirmar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>';
    if (confirm('<hl:message key="mensagem.confirmacao.lista.de.colaboradores.para.rescisao"/>')) {
      postData(link);
    }
    return false;
  }
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>