<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);    
    
  boolean podeEditarCnv = (Boolean) request.getAttribute("podeEditarCnv");
  boolean podeConsultarCnv = (Boolean) request.getAttribute("podeEditarCnv");    
  String svcCodigo = (String) request.getAttribute("SVC_CODIGO");
  ServicoTransferObject servico = (ServicoTransferObject) request.getAttribute("servico");
  List<TransferObject> lstConvenio = (List<TransferObject>) request.getAttribute("lstConvenio");
  String linkRetorno = (String) request.getAttribute("linkRetorno");
  String voltar = "../v3/manterServico?acao=iniciar";
%>
<c:set var="title">
   <hl:message key="mensagem.convenio.edt.prioridade.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><%=TextHelper.forHtmlContent(servico.getSvcIdentificador())%> - <%=TextHelper.forHtmlContent(servico.getSvcDescricao())%></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col" width="40%"><hl:message key="rotulo.consignataria.singular"/></th>
              <th scope="col" width="25%"><hl:message key="rotulo.convenio.nome.abrev"/></th>
              <th scope="col" width="10%"><hl:message key="rotulo.verba.singular"/></th>
              <th scope="col" width="10%"><hl:message key="rotulo.convenio.prioridade"/></th>
              <th scope="col" width="15%"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
          <%=JspHelper.msgRstVazio(lstConvenio.size()==0, "5", "lp")%>
          <%
            Iterator<TransferObject> it = lstConvenio.iterator();
            int i = 0;
            while (it.hasNext()) {
              TransferObject convenio = (TransferObject) it.next();
              String cnv_codigo = (String) convenio.getAttribute(Columns.CNV_CODIGO);
              String csa_identificador = convenio.getAttribute(Columns.CSA_IDENTIFICADOR)!= null? (String) convenio.getAttribute(Columns.CSA_IDENTIFICADOR): "";
              String csa_nome = convenio.getAttribute(Columns.CSA_NOME)!= null? (String) convenio.getAttribute(Columns.CSA_NOME): "";
              String csa_nome_abrev = convenio.getAttribute(Columns.CSA_NOME_ABREV)!= null? (String) convenio.getAttribute(Columns.CSA_NOME_ABREV): "";
              String cnv_cod_verba = convenio.getAttribute(Columns.CNV_COD_VERBA)!= null? (String) convenio.getAttribute(Columns.CNV_COD_VERBA): "";
              String cnv_prioridade = convenio.getAttribute(Columns.CNV_PRIORIDADE)!= null? convenio.getAttribute(Columns.CNV_PRIORIDADE).toString(): "";
              String podeEditar = "../v3/manterServico?acao=consultarCampo&SVC_CODIGO="+svcCodigo+"&CNV_CODIGO="+cnv_cod_verba+"&"+SynchronizerToken.generateToken4URL(request)+"&_skip_history_=true&linkRetorno="+linkRetorno;  
          %>
            <tr>
              <td><%=TextHelper.forHtmlContent(csa_identificador)%> - <%=TextHelper.forHtmlContent(csa_nome)%></td>
              <td><%=TextHelper.forHtmlContent(csa_nome_abrev.toUpperCase())%></td>
              <td><%=TextHelper.forHtmlContent(cnv_cod_verba)%></td>
              <td><%=TextHelper.forHtmlContent(cnv_prioridade)%></td>
              <c:choose>             
                <c:when test="${podeEditarCnv}">                  
                  <td><a href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(podeEditar)%>')"><hl:message  key="rotulo.acoes.editar"/></a></td>
                </c:when>
                <c:when test="${podeConsultarCnv}">
                  <td><a href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(podeEditar)%>')"><hl:message  key="rotulo.acoes.consultar"/></a></td>
                </c:when>
              </c:choose>
            </tr>
            <%                        
            }  
            %> 
          <tfoot>
            <tr>
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.folha.lista.convenios.titulo", responsavel ) + " - " %>
                <span class="font-italic">
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
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(voltar)%>')"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>