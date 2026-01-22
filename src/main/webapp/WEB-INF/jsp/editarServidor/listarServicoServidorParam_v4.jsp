<%--
* <p>Title: ListarServicos</p>
* <p>Description: Contem a lista de servicos</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: andrea.giorgini $
* $Revision: 26797 $
* $Date: 2019-05-23 11:59:25 -0300 (qui, 23 mai 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  String rseCodigo = (String) request.getAttribute("rseCodigo");
  String rseMatricula = (String) request.getAttribute("rseMatricula");
  String serNomeCodificado = (String) request.getAttribute("serNomeCodificado");
  String serNome = (String) request.getAttribute("serNome");
  
  // Obtem os valores dos bloqueios por serviços
  List servicosServidor = (List) request.getAttribute("servicosServidor");

%>
<c:set var="title">
  <hl:message key="rotulo.servidor.listar.servicos.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row firefox-print-fix">
    <div class="col-sm-11 col-md-12">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><%=TextHelper.forHtmlContent(rseMatricula)%> - <%=TextHelper.forHtmlContent(serNome)%></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.servico.identificador"/></th>
                <th scope="col"><hl:message key="rotulo.servico.descricao"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
            <%
            Iterator<?> it = servicosServidor.iterator();
            while (it.hasNext()) {
              CustomTransferObject servico = (CustomTransferObject)it.next();
              String svc_codigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
              String svc_descricao = servico.getAttribute(Columns.SVC_DESCRICAO).toString();
              String svc_identificador = servico.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
            %>
              <tr>
                <td><%=TextHelper.forHtmlContent(svc_identificador.toUpperCase())%></td>
                <td><%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%></td>
                <td><a href="#no-back" onClick="postData('../v3/manterServico?acao=consultarServicoParamSobrepoe&svc=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&SVC_IDENTIFICADOR=<%=TextHelper.forJavaScriptAttribute(svc_identificador)%>&SVC_DESCRICAO=<%=TextHelper.forJavaScriptAttribute(svc_descricao)%>&rseCodigo=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a></td>
              </tr>
            <%} %>
            
            </tbody>
            <tfoot>
              <tr>
                <td colspan="5">
                  <hl:message key="rotulo.taxa.juros.listagem.servico"/>
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
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');return false;" ><hl:message key="rotulo.botao.cancelar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script language="JavaScript" type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script language="JavaScript" type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script language="JavaScript">
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>