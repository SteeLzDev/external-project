<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="com.zetra.econsig.web.controller.coeficiente.ListarServicosCoeficienteDTO"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List servicos = (List)request.getAttribute("servicos");
String csa_codigo = (String)request.getAttribute("csa_codigo");
String titulo = (String) request.getAttribute("titulo");
String acao = (String) request.getParameter("acao");

%>
<c:set var="title">
<hl:message key="rotulo.coeficiente.lst.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.coeficiente.lst.titulo"/><%=TextHelper.forHtmlContent(!titulo.equals("") ? " - " + titulo.toUpperCase() : "")%></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col" width="80%"><hl:message key="rotulo.coeficiente.codigo"/></th>
            <th scope="col" width="20%"><hl:message key="rotulo.coeficiente.descricao"/></th>
            <th scope="col" width="20%"><hl:message key="rotulo.coeficiente.ativo"/></th>
            <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
        <%=JspHelper.msgRstVazio(servicos.size()==0, "13", "lp")%>
        <%
    		for (int i = 0; i < servicos.size(); i++) { 
  			ListarServicosCoeficienteDTO dto = (ListarServicosCoeficienteDTO) servicos.get(i);
		%>
          <tr>
            <td><%=TextHelper.forHtmlContent(dto.getCodVerba())%></td>
            <td><%=TextHelper.forHtmlContent(dto.getSvcDescricao().toUpperCase())%></td>
            <td>
              <a href="#no-back" onClick="postData('../v3/verificarCoeficiente?acao=<%=TextHelper.forJavaScriptAttribute(acao)%>&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(dto.getSvcCodigo())%>&SVC_DESCRICAO=<%=TextHelper.forJavaScriptAttribute(dto.getSvcDescricao())%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.forJavaScriptAttribute(titulo)%>&tipo=<%=TextHelper.forJavaScriptAttribute(dto.getCoeficienteAtivo())%>&ativo=<%=TextHelper.forJavaScriptAttribute(dto.getCoeficienteAtivo())%>&OPERACAO=ATIVO&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" CLASS="TitTab">
                <span class="font-weight-bold"><%=(!dto.getCoeficienteAtivo().equals("")) ? (dto.getCoeficienteAtivo().equals("M") ? ApplicationResourcesHelper.getMessage("rotulo.coeficientes.mensal", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.coeficientes.diario", responsavel)) : ""%></span>
              </a>
            </td>
            <td>
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>">
                        <svg>
                          <use xlink:href="#i-engrenagem"></use>
                        </svg>
                      </span> <hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/visualizarRankingServico?acao=<%=TextHelper.forJavaScriptAttribute(acao)%>&SVC_CODIGO=<%=TextHelper.forJavaScript(dto.getSvcCodigo())%>&titulo=<%=TextHelper.forJavaScript(dto.getSvcDescricao())%>&CSA_CODIGO=<%=TextHelper.forJavaScript(csa_codigo)%>&CSA_NOME=<%=TextHelper.forJavaScript(titulo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                      <hl:message key="rotulo.coeficiente.acao.reservar.margem"/>                      
                    </a>
                    <% if (responsavel.isCsa()) { %>
                    <a class="dropdown-item" href="#no-back" onclick="postData('../v3/editarCoeficiente?SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(dto.getSvcCodigo())%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(dto.getSvcDescricao())%>&CSA_CODIGO=<%=TextHelper.forJavaScript(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&tipo=M&ativo=<%=TextHelper.forJavaScript(dto.getCoeficienteAtivo())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                      <hl:message key="rotulo.acoes.editar.mensal"/>
                    </a>
                    <% } %>
                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/verificarCoeficiente?acao=<%=TextHelper.forJavaScriptAttribute(acao)%>&SVC_CODIGO=<%=TextHelper.forJavaScript(dto.getSvcCodigo())%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(dto.getSvcDescricao())%>&CSA_CODIGO=<%=TextHelper.forJavaScript(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&tipo=M&ativo=<%=TextHelper.forJavaScript(dto.getCoeficienteAtivo())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                      <hl:message key="rotulo.acoes.verificar.mensal"/>
                    </a>
                    <% if (responsavel.isCsa()) { %>
                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarCoeficiente?SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(dto.getSvcCodigo())%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(dto.getSvcDescricao())%>&CSA_CODIGO=<%=TextHelper.forJavaScript(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&tipo=D&ativo=<%=TextHelper.forJavaScript(dto.getCoeficienteAtivo())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                      <hl:message key="rotulo.acoes.editar.diario"/>
                    </a>
                    <% } %>
                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/verificarCoeficiente?acao=<%=TextHelper.forJavaScriptAttribute(acao)%>&SVC_CODIGO=<%=TextHelper.forJavaScript(dto.getSvcCodigo())%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(dto.getSvcDescricao())%>&CSA_CODIGO=<%=TextHelper.forJavaScript(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&tipo=D&ativo=<%=TextHelper.forJavaScript(dto.getCoeficienteAtivo())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                      <hl:message key="rotulo.acoes.verificar.diario"/>
                    </a>
                  </div>
                </div>
              </div>
            </td>
          </tr>
        <% }%>
        </tbody> 
        <tfoot>
           <tr>
            <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.listage.coeficientes", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
          </tr>
        </tfoot>   
      </table>
    </div>
    <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div> 
  </div>
  <div class="btn-action">
    <A href="#no-back" class="btn btn-outline-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></A>
  </div> 
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>