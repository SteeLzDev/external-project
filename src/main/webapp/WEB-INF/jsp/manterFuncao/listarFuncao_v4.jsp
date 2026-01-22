<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
List<?> funcoes = (List<?>) request.getAttribute("funcoes");

%>
<c:set var="title">
  ${tituloPagina}
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
   <div class="row">
      <div class="col-sm-5 col-md-4">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
            </div>
            <div class="card-body">
              <form NAME="form1" METHOD="post" ACTION="../v3/manterFuncao">
                <input type="hidden" name="acao" value="listar">
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="FILTRO"><hl:message key="rotulo.acoes.filtrar"/> <hl:message key="rotulo.usuario.singular"/></label>
                    <input type="text" class="form-control" id="FILTRO" name="FILTRO" value="<%=TextHelper.forHtmlAttribute(filtro)%>" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>">
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                    <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO">
                      <optgroup label="<%=ApplicationResourcesHelper.getMessage("rotulo.usuario.lista.filtros", responsavel)%>:">
                        <option value="" <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></option>
                        <option value="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.funcao.singular"/></option>
                        <option value="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.grupo.funcao.singular"/></option>
                      </optgroup>
                    </select>
                  </div>
                </div>
              </form>
            </div>
          </div>
          <div class="btn-action">
            <a class="btn btn-primary" href="#no-back" onClick="filtrar();">
              <svg width="20">
                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg> <hl:message key="rotulo.botao.pesquisar"/>
            </a>
          </div>
      </div>
      <div class="col-sm-7 col-md-8">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.usuario.plural"/></h2>
            </div>
            <div class="card-body table-responsive p-0">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col"><hl:message key="rotulo.funcao.singular"/></th>
                    <th scope="col"><hl:message key="rotulo.grupo.funcao.singular"/></th>
                    <th scope="col"><hl:message key="rotulo.acoes"/></th>
                  </tr>
                </thead>
                <tbody>
                  <%=JspHelper.msgRstVazio(funcoes.size()==0, 3, responsavel)%>
                  <%
                      String funCodigo, funDescricao, grfDescricao;
                      String linkConsultarUsuario, linkEditarUsuario, linkBloquearUsuario, linkReinicializarSenhaUsuario, linkExcluirUsuario, linkDetalharUsuario;
                      String stu_codigo_class = "";
                      String stu_codigo_descricao = "";
                      
                      Iterator<?> it = funcoes.iterator();
                      while (it.hasNext()) {
                        CustomTransferObject next = (CustomTransferObject)it.next();
                        funCodigo = next.getAttribute(Columns.FUN_CODIGO).toString();
                        funDescricao = next.getAttribute(Columns.FUN_DESCRICAO).toString();
                        grfDescricao = next.getAttribute(Columns.GRF_DESCRICAO).toString();

                  %>
                  <tr>
                    <td><%=TextHelper.forHtmlContent(funDescricao.toUpperCase())%></td>
                    <td><%=TextHelper.forHtmlContent(grfDescricao.toUpperCase())%></td>
                    <td>
                     <a href="#no-back" onClick="postData('../v3/manterFuncao?acao=editar&funCodigo=<%=TextHelper.forJavaScriptAttribute(funCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')">
                      <hl:message key="rotulo.acoes.editar"/>
                     </a>
                    </td>
                  </tr>
                  <% } %>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.funcoes", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
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
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    </div>   
</c:set>
<c:set var="javascript">
<script src="../js/usuario.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
f0 = document.forms[0];
function imprime() {
    window.print();
}

function filtrar() {
   f0.submit();
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>