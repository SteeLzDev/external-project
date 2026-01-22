<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
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
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String titulo = (String) request.getAttribute("titulo");
String parametros = (String) request.getAttribute("parametros");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
String org_codigo = (String) request.getAttribute("org_codigo");
String subTitulo = (String) request.getAttribute("subTitulo");
String cancel = (String) request.getAttribute("cancel");
String filtro = (String) request.getAttribute("filtro");

boolean podeEditarSvc = (boolean) request.getAttribute("podeEditarSvc");
boolean podeConsultarSvc = (boolean) request.getAttribute("podeConsultarSvc");
boolean podeEditarCnv = (boolean) request.getAttribute("podeEditarCnv");
boolean podeConsultarCnv = (boolean) request.getAttribute("podeConsultarCnv");

List<?> servicos = (List<?>) request.getAttribute("servicos");
%>
<c:set var="title">
  <%=TextHelper.forHtml(titulo)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row firefox-print-fix">
<!-- INICIO FILTRO -->    
    <div class="col-sm-5 col-md-4 d-print-none">
    <FORM NAME="form1" METHOD="post" ACTION="../v3/listarServicoOrgao?acao=iniciar">
    <%=JspHelper.geraCamposHidden(parametros)%>
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
        </div>
        <div class="card-body">
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO"><hl:message key="rotulo.orgao.lst.servico.filtro"/></label>
                <input type="text" class="form-control" id="FILTRO" name="FILTRO" value="<%=TextHelper.forHtmlAttribute(filtro)%>" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>">
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO" onFocus="SetarEventoMascaraV4(this,'#*200',true);"  onBlur="fout(this);ValidaMascaraV4(this);">
                  <optgroup label="<hl:message key="rotulo.filtro.plural"/>:">
                    <OPTION VALUE=""   <%=((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                    <OPTION VALUE="02" <%=((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.servico.identificador"/></OPTION>
                    <OPTION VALUE="03" <%=((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.servico.descricao"/></OPTION>
                    <OPTION VALUE="00" <%=((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.servico.filtro.bloqueado"/></OPTION>
                    <OPTION VALUE="01" <%=((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.servico.filtro.desbloqueado"/></OPTION>
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
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><%=TextHelper.forHtmlContent(titulo)%></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.servico.identificador"/></th>
                <th scope="col"><hl:message key="rotulo.servico.descricao"/></th>
                <th scope="col"><hl:message key="rotulo.situacao"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
            <%=JspHelper.msgRstVazio(servicos.size()==0, 13, responsavel)%>
            <%
              Iterator<?> it = servicos.iterator();
              while (it.hasNext()) {
                CustomTransferObject servico = (CustomTransferObject)it.next();
                String svc_codigo = (String)servico.getAttribute(Columns.SVC_CODIGO);
                String svc_descricao = (String)servico.getAttribute(Columns.SVC_DESCRICAO);
                String svc_identificador = (String)servico.getAttribute(Columns.SVC_IDENTIFICADOR);
                String scv_codigo = servico.getAttribute("STATUS").toString();
                int status = scv_codigo.equals("0") ? 0 : scv_codigo.equals("1") ? 2 : 1;
              %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(svc_identificador.toUpperCase())%></td>
                  <td><%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%></td>
                <% String msgServicoBloqueadoDesbloqueado = status != 1 ? ApplicationResourcesHelper.getMessage("rotulo.servico.status.desbloqueado", responsavel): ApplicationResourcesHelper.getMessage("rotulo.servico.status.bloqueado", responsavel); %>
                <td <%=status == 1 ? "class=\"block\"" : ""%>><%=TextHelper.forHtmlAttribute(msgServicoBloqueadoDesbloqueado)%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="MASTER" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes" />" aria-label="<hl:message key="rotulo.botao.opcoes" />"> <svg>
                              <use xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                  <% if (podeEditarSvc || podeConsultarSvc) { %>
                    <% if (podeEditarCnv) { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/mantemConvenio?acao=iniciar&svc_codigo=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&svc_descricao=<%=TextHelper.forJavaScript(svc_descricao)%>&svc_identificador=<%=TextHelper.forJavaScript(svc_identificador)%>&org_codigo=<%=TextHelper.forJavaScript(org_codigo)%>&org_nome=<%=TextHelper.forJavaScript(subTitulo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                      <hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                    <% } else if (podeConsultarCnv) { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/mantemConvenio?acao=iniciar&svc_codigo=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&svc_descricao=<%=TextHelper.forJavaScript(svc_descricao)%>&svc_identificador=<%=TextHelper.forJavaScript(svc_identificador)%>&org_codigo=<%=TextHelper.forJavaScript(org_codigo)%>&org_nome=<%=TextHelper.forJavaScript(subTitulo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                      <hl:message key="rotulo.consultar.convenio"/></a>
                    <% } %>
                    <% if (podeEditarSvc) { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterServico?acao=consultarServicoOrg&svc=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&org=<%=TextHelper.forJavaScript(org_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                      <hl:message key="rotulo.acoes.editar.servico"/></a>
                    <% } else { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterServico?acao=consultarServicoOrg&svc=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&org=<%=TextHelper.forJavaScript(org_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                      <hl:message key="rotulo.acoes.consultar.servico"/></a>
                    <% } %>
                  <% } %>                      
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
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(cancel)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>    
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>