<%@page import="com.zetra.econsig.dto.web.ListarServicosDTO"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

Boolean editar = (Boolean) request.getAttribute("editar");
int total = (int) request.getAttribute("total");

List<ListarServicosDTO> listarServicosDTOLst = (List<ListarServicosDTO>) request.getAttribute("listarServicosDTOLst");
String csaCodigo = (responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));
String titulo = JspHelper.verificaVarQryStr(request, "titulo");
String acao = JspHelper.verificaVarQryStr(request, "acao");
%>
<c:set var="title">
  <hl:message key="rotulo.listar.servico.taxa.juros.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<div class="card">
  <div class="card-header hasIcon">
    <span class="card-header-icon"> <svg width="25">
        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-relatorio""></use>
      </svg>
    </span>
    <h2 class="card-header-title"><hl:message key="rotulo.listar.servico.taxa.juros.titulo"/></h2>
  </div>
  <div class="card-body table-responsive p-0">
    <table class="table table-striped table-hover">
      <thead>
        <tr>
          <th scope="col"><hl:message key="rotulo.taxa.juros.servico.codigo"/></th>
          <th scope="col"><hl:message key="rotulo.acoes.editar"/></th>
          <th scope="col" width="10%"><hl:message key="rotulo.acoes.consultar"/></th>
        </tr>
      </thead>
      <tbody>
        <%=JspHelper.msgRstVazio(listarServicosDTOLst.size()==0, "13", "lp")%>
                
                <%
                for (ListarServicosDTO listarServicosDTO : listarServicosDTOLst) {
                    boolean permissaoEditarConsultar = !listarServicosDTO.getIsSvcSemPrazoConvenioCsa();
                %>
        <tr>
          <td><%=TextHelper.forHtmlContent(listarServicosDTO.getCodVerba())%>
          <%
          	if(!permissaoEditarConsultar) {
          %>
          		<span class="block"> <hl:message key="rotulo.servico.situacao.bloqueado"/></span>
          <%
          	}
          %>
          </td>
          <td><%=TextHelper.forHtmlContent(listarServicosDTO.getSvcDescricao().toUpperCase())%></td>
          <td>
            <div class="actions">
              <div class="dropdown">
                <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  <div class="form-inline">
                    <span class="mr-1" data-bs-toggle="tooltip" aria-label="Opções" title="Opções"> <svg>
                      <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use>
                    </svg>
                    </span><hl:message key="rotulo.botao.opcoes"/>   
                  </div>
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/visualizarRankingServico?acao=<%=TextHelper.forJavaScriptAttribute(acao)%>&SVC_CODIGO=<%=TextHelper.forJavaScript(listarServicosDTO.getSvcCodigo())%>&titulo=<%=TextHelper.forJavaScript(listarServicosDTO.getSvcDescricao())%>&CSA_CODIGO=<%=TextHelper.forJavaScript(csaCodigo)%>&CSA_NOME=<%=TextHelper.forJavaScript(titulo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" aria-label="Ranking"><hl:message key="rotulo.acoes.ranking"/></a>
                  <%                  	
					if(permissaoEditarConsultar) {
						if (editar) { %>
							<a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterTaxas?acao=<%=TextHelper.forJavaScriptAttribute(acao)%>&SVC_CODIGO=<%=TextHelper.forJavaScript(listarServicosDTO.getSvcCodigo())%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(listarServicosDTO.getSvcDescricao())%>&CSA_CODIGO=<%=TextHelper.forJavaScript(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&tipo=M&ativo=<%=TextHelper.forJavaScript(listarServicosDTO.getCoeficienteAtivo())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" aria-label="<hl:message key="rotulo.acoes.editar"/>"><hl:message key="rotulo.acoes.editar"/></a>
							<a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarServicos?acao=listarOcorrencia&SVC_CODIGO=<%=TextHelper.forJavaScript(listarServicosDTO.getSvcCodigo())%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(listarServicosDTO.getSvcDescricao())%>&CSA_CODIGO=<%=TextHelper.forJavaScript(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&tipo=M&ativo=<%=TextHelper.forJavaScript(listarServicosDTO.getCoeficienteAtivo())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" aria-label="<hl:message key="rotulo.ocorrencia.coeficiente.titulo"/>"><hl:message key="rotulo.ocorrencia.coeficiente.titulo"/></a>
						<% } else { %>
							<a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterTaxas?acao=<%=TextHelper.forJavaScriptAttribute(acao)%>&SVC_CODIGO=<%=TextHelper.forJavaScript(listarServicosDTO.getSvcCodigo())%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(listarServicosDTO.getSvcDescricao())%>&CSA_CODIGO=<%=TextHelper.forJavaScript(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&tipo=M&ativo=<%=TextHelper.forJavaScript(listarServicosDTO.getCoeficienteAtivo())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" aria-label="<hl:message key="rotulo.acoes.consultar"/>"><hl:message key="rotulo.acoes.consultar"/></a>
						<% } 
					}
                  %>
                </div>
              </div>
            </div>
          </td>
        </tr>
        <%    
          }              
        %>
      </tbody>
      <tfoot>
        <tr>
          <td colspan="3"><%=ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.listagem.servico", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
        </tr>
      </tfoot>
    </table>
    <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
  </div>
</div>
<div class="btn-action">
  <a class="btn btn-outline-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" aria-label="Voltar" href="#"><hl:message key="rotulo.botao.voltar"/></a>
</div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>