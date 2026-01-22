<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> grupo = (List<TransferObject>) request.getAttribute("grupo");

%>
<c:set var="title">
   <hl:message key="rotulo.grupo.servico.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <% if (responsavel.temPermissao(CodedValues.FUN_CRIAR_GRUPO_SERVICO)) { %>
  <div class="row">
    <div class="col-sm-12">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/manterGrupoServico?acao=inserir&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.grupo.servico.novo"/></a>
        </div>
      </div>
    </div>
  </div>
  <% } %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.grupo.servico.titulo"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.grupo.servico.codigo"/></th>
            <th scope="col"><hl:message key="rotulo.grupo.servico.quantidade.geral.grid"/>.</th>
            <th scope="col"><hl:message key="rotulo.grupo.servico.quantidade.consignataria.grid"/></th>
            <th scope="col"><hl:message key="rotulo.grupo.servico.descricao"/></th>
          <% if (responsavel.temPermissao(CodedValues.FUN_CRIAR_GRUPO_SERVICO)) { %>
            <th scope="col" width="15%"><hl:message key="rotulo.acoes"/></th>
          <% } %>
          </tr>
        </thead>
        <tbody>
         <%=JspHelper.msgRstVazio(grupo.size()==0, "13", "lp")%>
         <%
           Iterator it = grupo.iterator();
           String tgsCodigo = null;
           String tgsGrupo = null;
           String tgsIdentificador = null;
           Integer tgsQuantidade = null;
           Integer tgsQuantidadePorCsa = null;
           
           while (it.hasNext()) {
              CustomTransferObject grupoServicoCTO = (CustomTransferObject)it.next();
              tgsCodigo           = grupoServicoCTO.getAttribute(Columns.TGS_CODIGO).toString();
              tgsGrupo            = (String) grupoServicoCTO.getAttribute(Columns.TGS_GRUPO);
              tgsIdentificador    = (String) grupoServicoCTO.getAttribute(Columns.TGS_IDENTIFICADOR);
              tgsQuantidade       = (Integer) grupoServicoCTO.getAttribute(Columns.TGS_QUANTIDADE);
              tgsQuantidadePorCsa = (Integer) grupoServicoCTO.getAttribute(Columns.TGS_QUANTIDADE_POR_CSA);
         %>
         <tr >
          <td><%=TextHelper.forHtmlContent(tgsIdentificador)%></td>
          <td><%=TextHelper.forHtmlContent(tgsQuantidade != null ? tgsQuantidade.toString() : "")%></td>
          <td><%=TextHelper.forHtmlContent(tgsQuantidadePorCsa != null ? tgsQuantidadePorCsa.toString() : "")%></td>
          <td><%=TextHelper.forHtmlContent(tgsGrupo.toUpperCase())%></td>
          <% if (responsavel.temPermissao(CodedValues.FUN_CRIAR_GRUPO_SERVICO)) { %>
            <td>          
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenuTeste" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                      </span> <hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenuTeste">
                    <% if (responsavel.temPermissao(CodedValues.FUN_CONS_SERVICOS)) { %>
                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterServico?acao=iniciar&grupo=<%=TextHelper.forJavaScriptAttribute(tgsCodigo)%>&titulo=<%=TextHelper.forJavaScript(tgsGrupo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.servico.singular"/></a>
                    <% } %>
                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterGrupoServico?acao=editar&tgsCodigo=<%=TextHelper.forJavaScriptAttribute(tgsCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                    <a class="dropdown-item" href="#no-back" onClick="ExcluirEntidade('<%=TextHelper.forJavaScript(tgsCodigo)%>', 'grupoSvc', '../v3/manterGrupoServico?acao=excluir&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(tgsGrupo)%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                  </div>
                </div>
              </div>
            </td>
          <% } %>
         </tr>
         <%
          }
         %>
       </tbody>
     </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/> </a>
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