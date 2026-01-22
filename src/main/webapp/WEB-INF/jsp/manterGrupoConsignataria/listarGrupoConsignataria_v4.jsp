<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
//Página inicial
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<?> grupo = (List<?>) request.getAttribute("grupo");
boolean podeCriarGrpCsa = (Boolean) request.getAttribute("podeCriarGrpCsa");
%>
<c:set var="title">
  <hl:message key="rotulo.grupo.consignataria.singular"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
  <% if (podeCriarGrpCsa) { %>
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/manterGrupoConsignataria?acao=editar&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.novo.grupo.consignataria"/></a>
  <% } %>
        </div>
      </div>
    </div>
  </div>
<div class="row">
  <div class="col-12">
    <div class="card">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title"><hl:message key="rotulo.grupo.consignataria.singular"/></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.grupo.consignataria.codigo"/></th>
                <th scope="col"><hl:message key="rotulo.grupo.consignataria.descricao"/></th>
  <% if (podeCriarGrpCsa) { %>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
  <% } %>                
              </tr>
            </thead>
            <tbody>
<%=JspHelper.msgRstVazio(grupo.size()==0, "13", "lp")%>
  <%
    Iterator<?> it = grupo.iterator();
    while (it.hasNext()) {
      CustomTransferObject ctoGrupoConsignataria = (CustomTransferObject)it.next();
      String tgcCodigo        = ctoGrupoConsignataria.getAttribute(Columns.TGC_CODIGO).toString();
      String tgcDescricao     = (String)ctoGrupoConsignataria.getAttribute(Columns.TGC_DESCRICAO);
      String tgcIdentificador = (String)ctoGrupoConsignataria.getAttribute(Columns.TGC_IDENTIFICADOR);
  %>
              <tr>
                <td><%=TextHelper.forHtmlContent(tgcIdentificador)%></td>
                <td><%=TextHelper.forHtmlContent(tgcDescricao.toUpperCase())%></td>
    <% if (podeCriarGrpCsa) { %>
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
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterGrupoConsignataria?acao=editar&tipo=consultar&tgcCodigo=<%=TextHelper.forJavaScriptAttribute(tgcCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.acoes.editar"/></a>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterGrupoConsignataria?acao=excluir&tgcCodigo=<%=TextHelper.forJavaScriptAttribute(tgcCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')" onclick="return confirmaExclusao();"><hl:message key="rotulo.acoes.excluir"/></a>
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
  </div>
</div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" ><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
    function confirmaExclusao(){
        if (!confirm('<hl:message key="mensagem.confirmacao.exclusao.grupo.consignataria"/>')){
            return false;
        }
        return true;
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>