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
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditarEst = (boolean) request.getAttribute("podeEditarEst");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
String filtro = (String) request.getAttribute("filtro");
boolean podeExcluirEst = (boolean) request.getAttribute("podeExcluirEst");
boolean podeConsultarEst = (boolean) request.getAttribute("podeConsultarEst");
List estabelecimentos = (List) request.getAttribute("estabelecimentos");

%>

<c:set var="title">
  <hl:message key="rotulo.listar.estabelecimento.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
        <%if (podeEditarEst) {%>
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/manterEstabelecimento?acao=consultar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.criar.novo.arg" arg0="<%=ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular", responsavel).toLowerCase()%>"/></a>
        <%}%>
        </div>
      </div>
    </div>
  </div>
  
  <form name="form1" method="post" action="../v3/manterEstabelecimento?acao=iniciar">
    <div class="row">
      <div class="col-sm-5 col-md-4">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO"><hl:message key="rotulo.estabelecimento.filtrar"/></label>
                <input type="text" class="form-control" id="FILTRO" name="FILTRO" VALUE="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.acao.digite.filtro", responsavel)%>">
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO" onFocus="SetarEventoMascaraV4(this,'#*200',true);"  onBlur="fout(this);ValidaMascaraV4(this);" nf="Filtrar">
                  <optgroup label="<hl:message key="rotulo.filtro.plural"/>:">
                    <option value=""   <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                    <option value="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.estabelecimento.codigo"/></OPTION>
                    <option value="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.estabelecimento.nome"/></OPTION>
                    <option value="00" <%=(String)((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.estabelecimento.filtro.bloqueado"/></OPTION>
                    <option value="01" <%=(String)((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.estabelecimento.filtro.desbloqueado"/></OPTION>
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
      </div>
      <div class="col-sm-7 col-md-8">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.estabelecimento.plural"/></h2>
          </div>
          <div class="card-body table-responsive p-0">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.estabelecimento.codigo"/></th>
                  <th scope="col"><hl:message key="rotulo.estabelecimento.nome"/></th>
                  <th scope="col"><hl:message key="rotulo.estabelecimento.situacao"/></th>
                  <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
              </thead>
              <tbody>
              <%
              Iterator it = estabelecimentos.iterator();
              while (it.hasNext()) {
                  String classStatusEst = "";
                  CustomTransferObject estabelecimento = (CustomTransferObject)it.next();
                  String est_codigo = (String)estabelecimento.getAttribute(Columns.EST_CODIGO);
                  String est_nome = (String)estabelecimento.getAttribute(Columns.EST_NOME);
                  String est_identificador = (String)estabelecimento.getAttribute(Columns.EST_IDENTIFICADOR);
                  String est_ativo = estabelecimento.getAttribute(Columns.EST_ATIVO) != null ? estabelecimento.getAttribute(Columns.EST_ATIVO).toString() : "1";
                  String msgBloquearDesbloquear = "";
                  String msgStatusEst = "";
                  if (est_ativo.equals("1")) {
                      msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.bloquear.estabelecimento.clique.aqui", responsavel);
                      msgStatusEst = ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.filtro.desbloqueado", responsavel);
                  } else {
                      classStatusEst = "block";
                      msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.desbloquear.estabelecimento.clique.aqui", responsavel);
                      msgStatusEst = ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.filtro.bloqueado", responsavel);                    
                  }
              %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(est_identificador)%></td>
                  <td><%=TextHelper.forHtmlContent(est_nome.toUpperCase())%></td>
                  <td class="<%=TextHelper.forHtmlAttribute(classStatusEst)%>"><%=TextHelper.forHtmlContent(msgStatusEst)%></td>
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
                        <% if (podeEditarEst) { %>
                          <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(est_ativo)%>, '<%=TextHelper.forJavaScript(est_codigo)%>', 'EST', '../v3/manterEstabelecimento?acao=ativarDesativar&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(est_nome)%>')" alt="<%=TextHelper.forHtmlAttribute(msgBloquearDesbloquear)%>" title="<%=TextHelper.forHtmlAttribute(msgBloquearDesbloquear)%>"><hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterEstabelecimento?acao=consultar&est=<%=TextHelper.forJavaScriptAttribute(est_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.editar.estabelecimento.clique.aqui"/>" title="<hl:message key= "mensagem.editar.estabelecimento.clique.aqui"/>"><hl:message key="rotulo.acoes.editar"/></a>
                          <% if (podeExcluirEst) { %>
                            <a class="dropdown-item" href="#no-back" onClick="ExcluirEntidade('<%=TextHelper.forJavaScript(est_codigo)%>', 'EST', '../v3/manterEstabelecimento?acao=deletar&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(est_nome)%>')" alt="<hl:message key="mensagem.excluir.estabelecimento.clique.aqui"/>" title="<hl:message key= "mensagem.excluir.estabelecimento.clique.aqui"/>"><hl:message key="rotulo.acoes.excluir"/></a>
                          <% } %>
                        <% } else if (podeConsultarEst){ %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterEstabelecimento?acao=consultar&est=<%=TextHelper.forJavaScriptAttribute(est_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.consultar.estabelecimento.clique.aqui"/>" title="<hl:message key= "mensagem.consultar.estabelecimento.clique.aqui"/>"><hl:message key="rotulo.acoes.consultar"/></a> 
                        <% } %>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              <%} %>
              </tbody>
            </table>
          </div>
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
        </div>
      </div>
    </div>
  </form>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>