<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<?> relatorios = (List<?>) request.getAttribute("relatorios");
String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
int filtro_tipo = (Integer)request.getAttribute("filtro_tipo");
int offset = (int) request.getAttribute("offset");
%>
<c:set var="title">
  <hl:message key="rotulo.listar.relatorio.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/editarRelatorio?acao=iniciarEdicao&tipo=inserir&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.criar.novo.arg" arg0="<%=ApplicationResourcesHelper.getMessage("rotulo.menu.relatorio", responsavel).toLowerCase()%>"/></a>
        </div>
      </div>
    </div>
  </div>
  <div class="row firefox-print-fix">
    <!-- INICIO FILTRO -->    
    <div class="col-sm-5 col-md-4 d-print-none">
      <FORM NAME="form1" METHOD="post" ACTION="../v3/editarRelatorio?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
          </div>
          <div class="card-body">
              <div class="row">
                <div class="form-group col-sm">
                  <label for="FILTRO"><hl:message key="rotulo.servico.filtro"/></label>
                  <input type="text" class="form-control" id="FILTRO" name="FILTRO" value="<%=TextHelper.forHtmlAttribute(filtro)%>" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>">
                </div>
              </div>
              <div class="row">
                <div class="form-group col-sm">
                  <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                  <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO" onFocus="SetarEventoMascaraV4(this,'#*200',true);"  onBlur="fout(this);ValidaMascaraV4(this);">
                    <optgroup label="<hl:message key="rotulo.filtro.plural"/>:">
                      <OPTION VALUE=""   <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                      <OPTION VALUE="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.relatorio.codigo"/></OPTION>
                      <OPTION VALUE="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.relatorio.titulo"/></OPTION>
                      <OPTION VALUE="00" <%=(String)((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.relatorio.bloqueado"/></OPTION>
                      <OPTION VALUE="01" <%=(String)((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.relatorio.desbloqueado"/></OPTION>
                    </optgroup>
                  </select>
                </div>
              </div>
          </div>
        </div>
        <div class="btn-action">
          <button class="btn btn-primary">
            <svg width="20">
              <use xlink:href="#i-consultar"></use>
            </svg>
            <hl:message key="rotulo.acao.pesquisar"/>
          </button>
        </div>
      </form>
    </div>
    <!-- FIM FILTRO -->       
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.relatorio.titulo"/></th>
                <th scope="col"><hl:message key="rotulo.situacao"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
              <%=JspHelper.msgRstVazio(relatorios.size()==0, 13, responsavel)%>
              <%
              Iterator<?> it = relatorios.iterator();
              while (it.hasNext()) {
                CustomTransferObject relatorio = (CustomTransferObject)it.next();
                String rel_codigo = (String)relatorio.getAttribute(Columns.REL_CODIGO);
                String rel_nome = (String)relatorio.getAttribute(Columns.REL_TITULO);
                String rel_ativo = relatorio.getAttribute(Columns.REL_ATIVO) != null ? relatorio.getAttribute(Columns.REL_ATIVO).toString() : "1";
              %>
              <tr>
                <td><%=TextHelper.forHtmlContent(rel_nome.toUpperCase())%></td>
                <% String msgServicoBloqueadoDesbloqueado = rel_ativo.equals("1") ? ApplicationResourcesHelper.getMessage("rotulo.relatorio.desbloqueado", responsavel): ApplicationResourcesHelper.getMessage("rotulo.relatorio.bloqueado", responsavel); %>
                <td <%=rel_ativo.equals("0") ? "class=\"block\"" : ""%>><%=TextHelper.forHtmlAttribute(msgServicoBloqueadoDesbloqueado)%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="MASTER" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes" />" aria-label="<hl:message key="rotulo.botao.opcoes" />"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(rel_ativo)%>, '<%=TextHelper.forJavaScript(rel_codigo)%>', 'EST', '../v3/editarRelatorio?acao=bloquear&tipo=bloquear&offset=<%=offset%>&FILTRO=<%=filtro%>&FILTRO_TIPO=<%=filtro_tipo%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(rel_nome)%>')"><hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarRelatorio?acao=iniciarEdicao&tipo=editar&REL_CODIGO=<%=TextHelper.forJavaScriptAttribute(rel_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                        <a class="dropdown-item" href="#no-back" onClick="ExcluirEntidade('<%=TextHelper.forJavaScript(rel_codigo)%>', 'EST', '../v3/editarRelatorio?acao=excluir&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(rel_nome)%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarSubrelatorio?acao=iniciar&relCodigo=<%=TextHelper.forJavaScriptAttribute(rel_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.subrelatorio.editavel"/></a>
                      </div>
                    </div>
                  </div>
                </td>               
              </tr>
              <%} %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="5">
                  <hl:message key="rotulo.relatorio.listagem"/>
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
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>