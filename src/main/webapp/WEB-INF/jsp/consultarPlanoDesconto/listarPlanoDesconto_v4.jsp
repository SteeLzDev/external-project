<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditarPla = (boolean) request.getAttribute("podeEditarPla");
boolean podeExcluirPla = (boolean) request.getAttribute("podeExcluirPla");
boolean podeConsultarPla = (boolean) request.getAttribute("podeConsultarPla");
String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (Integer) request.getAttribute("filtro_tipo");
List<?> planos = (List<?>) request.getAttribute("planos");
%>

<c:set var="title">
  <hl:message key="<%=TextHelper.forHtml("rotulo.listar.plano.titulo")%>"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
  <%if (podeEditarPla) {%>
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/consultarPlanoDesconto?acao=editar&plaCodigo=&tipo=consultar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.novo.plano.desconto"/></a>
        </div>
      </div>
    </div>
  </div>
  <%}%>
  
  <div class="row">
    <div class="col-sm-5 col-md-4">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
          </div>
          <div class="card-body">
            <form NAME="form1" METHOD="post" ACTION="../v3/consultarPlanoDesconto?acao=iniciar&tipo=consultar&<%=SynchronizerToken.generateToken4URL(request)%>">
              <input type="hidden" name="tipo" value="consultar">
              <div class="row">
                <div class="form-group col-sm">
                  <label for="iFiltro"><hl:message key="rotulo.plano.filtro"/></label>
                  <input type="text" class="form-control" id="iFiltro" name="FILTRO" value="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>">
                </div>
              </div>
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                      <select name="FILTRO_TIPO" class="form-control form-select select" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);" nf="Filtrar">
                        <optgroup>
                          <option value=""   <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></option>
                          <option value="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.plano.codigo"/></option>
                          <option value="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.plano.nome"/></option>
                          <option value="00" <%=(String)((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.plano.bloqueado"/></option>
                          <option value="01" <%=(String)((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.plano.desbloqueado"/></option>
                        </optgroup>
                      </select>
                  </div>
                </div>
                <div class="btn-action">
                  <a class="btn btn-primary" href="#no-back" name="Filtrar" id="Filtrar" onClick="form1.submit(); return false;">
                    <svg width="20">
                      <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use>
                    </svg> <hl:message key="rotulo.botao.pesquisar"/>
                  </a>
                </div>
            </form>
          </div>
        </div>
    </div>
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.listar.plano.subtitulo"/></h2>
        </div>
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <th scope="col"><hl:message key="rotulo.plano.codigo"/></th>
              <th scope="col"><hl:message key="rotulo.plano.nome"/></th>
              <th scope="col"><hl:message key="rotulo.servico.singular"/></th>
              <th scope="col"><hl:message key="rotulo.plano.natureza"/></th>
              <th scope="col"><hl:message key="rotulo.situacao"/></th>
              <th scope="col"><hl:message key="rotulo.acoes"/></th>
            </thead>
            <tbody>
              <%=JspHelper.msgRstVazio(planos.size()==0, 13, responsavel)%>
              <%
              Iterator<?> it = planos.iterator();              
              while (it.hasNext()) {
                CustomTransferObject plano = (CustomTransferObject)it.next();
                String plaCodigo = (String)plano.getAttribute(Columns.PLA_CODIGO);
                String plaDescricao = (String)plano.getAttribute(Columns.PLA_DESCRICAO);
                String plaIdentificador = (String)plano.getAttribute(Columns.PLA_IDENTIFICADOR);
                String svcDescricao = (String)plano.getAttribute(Columns.SVC_DESCRICAO);
                String svcCodigo = (String)plano.getAttribute(Columns.SVC_CODIGO);
                String csaCodigo = (String)plano.getAttribute(Columns.CSA_CODIGO);
                String nplCodigo = (String)plano.getAttribute(Columns.NPL_CODIGO);
                String nplDescricao = (String)plano.getAttribute(Columns.NPL_DESCRICAO);
                String plaAtivo = plano.getAttribute(Columns.PLA_ATIVO) != null ? plano.getAttribute(Columns.PLA_ATIVO).toString() : "1";                        
              %>
              <tr>
                <td><%=TextHelper.forHtmlContent(plaIdentificador)%></td>
                <td><%=TextHelper.forHtmlContent(plaDescricao.toUpperCase())%></td>
                <td><%=TextHelper.forHtmlContent(svcDescricao.toUpperCase())%></td>
                <td><%=TextHelper.forHtmlContent(nplDescricao.toUpperCase())%></td>
                <td><%= plaAtivo.equals("1") ? ApplicationResourcesHelper.getMessage("rotulo.plano.desbloqueado", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.plano.bloqueado", responsavel) %></td>                
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="Mais ações" aria-label="Mais ações"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <% if (podeEditarPla) { %>                  
                          <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(plaAtivo)%>, '<%=TextHelper.forJavaScript(plaCodigo)%>', 'PLA', '../v3/consultarPlanoDesconto?acao=bloquear&PLA_DESCRICAO=<%=TextHelper.forJavaScript(plaDescricao)%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(plaDescricao)%>', '')"><hl:message key="rotulo.acao.bloquear.desbloquear"/></a>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/consultarPlanoDesconto?acao=editar&plaCodigo=<%=TextHelper.forJavaScriptAttribute(plaCodigo)%>&svcCodigo=<%=TextHelper.forJavaScript(svcCodigo )%>&nplCodigo=<%=TextHelper.forJavaScript(nplCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                        <% if (podeExcluirPla) { %>
                          <a class="dropdown-item" href="#no-back" onClick="ExcluirEntidade('<%=TextHelper.forJavaScript(plaCodigo)%>', 'PLA', '../v3/consultarPlanoDesconto?acao=excluir&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(plaDescricao)%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                        <% } %>
                        <% } else if (podeConsultarPla) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/consultarPlanoDesconto?acao=consultar&plaCodigo=<%=TextHelper.forJavaScriptAttribute(plaCodigo)%>&svcCodigo=<%=TextHelper.forJavaScript(svcCodigo )%>&csaCodigo=<%=TextHelper.forJavaScript(csaCodigo )%>&nplCodigo=<%=TextHelper.forJavaScript(nplCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.consultar"/></a>
                        <% } %>                         
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
                <td colspan="12">
                  <%=ApplicationResourcesHelper.getMessage("mensagem.listagem.plano.desconto", responsavel) + " - "%>
                  <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
                </td>                
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
      <div class="card-footer">
        <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>   
  </div>
  <div class="btn-action mt-3">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" ><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>