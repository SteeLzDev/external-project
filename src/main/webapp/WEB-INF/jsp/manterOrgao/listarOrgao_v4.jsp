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

boolean podeEditarOrgaos = (boolean) request.getAttribute("podeEditarOrgaos");
boolean podeExcluirOrgao = (boolean) request.getAttribute("podeExcluirOrgao");
boolean podeEditarEnderecoAcesso = (boolean) request.getAttribute("podeEditarEnderecoAcesso");
boolean podeConsultarOrgaos = (boolean) request.getAttribute("podeConsultarOrgaos");
boolean podeCriarUsu = (boolean) request.getAttribute("podeCriarUsu");
boolean podeConsultarSvc = (boolean) request.getAttribute("podeConsultarSvc");
boolean podeEditarSvc = (boolean) request.getAttribute("podeEditarSvc");
boolean podeConsultarCnvCor = (boolean) request.getAttribute("podeConsultarCnvCor");
boolean podeConsultarPerfilUsu = (boolean) request.getAttribute("podeConsultarPerfilUsu");
boolean podeConsultarUsu = (boolean) request.getAttribute("podeConsultarUsu");
boolean podeConsultarParamOrgao = (boolean) request.getAttribute("podeConsultarParamOrgao");

//Exibe Botao Rodapé
boolean exibeBotaoRodape = request.getAttribute("exibeBotaoRodape") != null && (boolean) request.getAttribute("exibeBotaoRodape");

int filtro_tipo = (int) request.getAttribute("filtro_tipo");
String filtro = (String) request.getAttribute("filtro");
List orgaos = (List) request.getAttribute("orgaos");

%>

<c:set var="title">
<hl:message key="rotulo.consultar.orgao.titulo"/>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
      <div class="row">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <div class="btn-action">
            <%if (podeEditarOrgaos) {%>
              <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/editarOrgao?acao=consultarOrgao&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.criar.novo.arg" arg0="<%=ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel).toLowerCase()%>"/></a>
            <%}%>
            </div>
          </div>
        </div>
      </div>
      <form name="form1" method="post" action="../v3/listarOrgao?acao=iniciar">
        <div class="row">
          <div class="col-sm-5 col-md-4">
            <div class="card">
              <div class="card-header hasIcon pl-3">
                <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
              </div>
              <div class="card-body">
                  <div class="row">
                    <div class="form-group col-sm">
                      <label for="FILTRO"><hl:message key="rotulo.orgao.filtrar"/></label>
                      <input type="text" class="form-control" id="FILTRO" name="FILTRO" VALUE="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.acao.digite.filtro", responsavel)%>">
                    </div>
                  </div>
                  <div class="row">
                    <div class="form-group col-sm">
                      <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                      <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO" onFocus="SetarEventoMascaraV4(this,'#*200',true);"  onBlur="fout(this);ValidaMascaraV4(this);" nf="Filtrar">
                        <optgroup label="<hl:message key="rotulo.filtro.plural"/>:">
                          <option value=""   <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                          <option value="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.orgao.codigo"/></OPTION>
                          <option value="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.orgao.nome"/></OPTION>
                          <option value="04" <%=(String)((filtro_tipo ==  4) ? "SELECTED" : "")%>><hl:message key="rotulo.orgao.codigo.est.extenso"/></OPTION>
                          <option value="05" <%=(String)((filtro_tipo ==  5) ? "SELECTED" : "")%>><hl:message key="rotulo.orgao.nome.est.extenso"/></OPTION>
                          <option value="00" <%=(String)((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.orgao.filtro.bloqueado"/></OPTION>
                          <option value="01" <%=(String)((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.orgao.filtro.desbloqueado"/></OPTION>
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
                <h2 class="card-header-title"><hl:message key="rotulo.orgao.plural"/></h2>
              </div>
              <div class="card-body table-responsive p-0">
                <table class="table table-striped table-hover">
                  <thead>
                    <tr>
                      <th scope="col"><hl:message key="rotulo.orgao.codigo"/></th>
                      <th scope="col"><hl:message key="rotulo.orgao.nome"/></th>
                      <th scope="col"><hl:message key="rotulo.estabelecimento.singular"/></th>
                      <th scope="col"><hl:message key="rotulo.orgao.status"/></th>
                      <th scope="col"><hl:message key="rotulo.acoes"/></th>
                    </tr>
                  </thead>
                  <tbody>
                  <%=JspHelper.msgRstVazio(orgaos.size()==0, "13", "lp")%>
                  <%
                  Iterator it = orgaos.iterator();
                  String org_codigo, org_nome, org_identificador, org_ativo, est_identificador;
                  while (it.hasNext()) {
                    String classStatusOrg = "";
                    CustomTransferObject orgao = (CustomTransferObject)it.next();
                    org_codigo = (String)orgao.getAttribute(Columns.ORG_CODIGO);
                    org_nome = (String)orgao.getAttribute(Columns.ORG_NOME);
                    org_identificador = (String)orgao.getAttribute(Columns.ORG_IDENTIFICADOR);
                    org_ativo = orgao.getAttribute(Columns.ORG_ATIVO) != null ? orgao.getAttribute(Columns.ORG_ATIVO).toString() : "1";
                    est_identificador = orgao.getAttribute(Columns.EST_IDENTIFICADOR).toString();
                    String msgBloquearDesbloquear = "";
                    String msgStatusOrg = "";
                    if (org_ativo.equals("1")) {
                        msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.bloquear.orgao.clique.aqui", responsavel);
                        msgStatusOrg = ApplicationResourcesHelper.getMessage("rotulo.orgao.filtro.desbloqueado", responsavel);
                    } else {
                  	    classStatusOrg = "block";
                        msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.desbloquear.orgao.clique.aqui", responsavel);
                        msgStatusOrg = ApplicationResourcesHelper.getMessage("rotulo.orgao.filtro.bloqueado", responsavel);                    
                    }
                  %>
                    <tr>
                      <td><%=TextHelper.forHtmlContent(org_identificador)%></td>
                      <td><%=TextHelper.forHtmlContent(org_nome.toUpperCase())%></td>
                      <td><%=TextHelper.forHtmlContent(est_identificador)%></td>
                      <td class="<%=TextHelper.forHtmlAttribute(classStatusOrg)%>"><%=TextHelper.forHtmlContent(msgStatusOrg)%></td>
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
                              <% if (podeEditarOrgaos) { %>
                                <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(org_ativo)%>, '<%=TextHelper.forJavaScript(org_codigo)%>', 'ORG', '../v3/editarOrgao?acao=bloquearOrgao&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(org_nome)%>')" alt="<%=TextHelper.forHtmlAttribute(msgBloquearDesbloquear)%>" title="<%=TextHelper.forHtmlAttribute(msgBloquearDesbloquear)%>"><hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarOrgao?acao=consultarOrgao&org=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.editar.orgao.clique.aqui"/>" title="<hl:message key= "mensagem.editar.orgao.clique.aqui"/>"><hl:message key="rotulo.acoes.editar"/></a>
                                <% if (podeExcluirOrgao) { %>
                                  <a class="dropdown-item" href="#no-back" onClick="ExcluirEntidade('<%=TextHelper.forJavaScript(org_codigo)%>', 'ORG', '../v3/editarOrgao?acao=excluirOrgao&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(org_nome)%>')" alt="<hl:message key="mensagem.excluir.orgao.clique.aqui"/>" title="<hl:message key= "mensagem.excluir.orgao.clique.aqui"/>"><hl:message key="rotulo.acoes.excluir"/></a>
                                <% } %>
                              <% } else if (podeConsultarOrgaos && !responsavel.isCsa()) { %>
                              <%     if (podeEditarEnderecoAcesso) { %>
                                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarOrgao?acao=consultarOrgao&org=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.editar.orgao.clique.aqui"/>" title="<hl:message key= "mensagem.editar.orgao.clique.aqui"/>"><hl:message key= "rotulo.acoes.editar.endereco.acesso"/></a>
                              <%     } else { %>
                                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarOrgao?acao=consultarOrgao&org=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.consultar.orgao.clique.aqui"/>" title="<hl:message key="mensagem.consultar.orgao.clique.aqui"/>"><hl:message key="rotulo.acoes.consultar"/></a>
                              <%     } %> 
                              <% } %>           
                              <% if (podeConsultarPerfilUsu) { %>
                                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarPerfilOrg?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&titulo=<%=TextHelper.forJavaScript(org_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.listar.perfil.orgao.clique.aqui"/>" title="<hl:message key="mensagem.listar.perfil.orgao.clique.aqui"/>"><hl:message key="rotulo.acao.listar.perfil.usuario"/></a>
                              <% } %>
                              <% if (podeConsultarUsu) { %>
                                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarUsuarioOrg?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&titulo=<%=TextHelper.encode64(org_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.listar.usuario.orgao.clique.aqui"/>" title="<hl:message key= "mensagem.listar.usuario.orgao.clique.aqui"/>"><hl:message key= "rotulo.acoes.listar.usuario"/></a>
                              <% } %>   
                              <% if (podeCriarUsu) { %>
                                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/inserirUsuarioOrg?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&titulo=<%=TextHelper.encode64(org_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.criar.usuario.orgao.clique.aqui"/>" title="<hl:message key="mensagem.criar.usuario.orgao.clique.aqui"/>"><hl:message key= "rotulo.acoes.criar.usuario"/></a>
                              <% } %>
                              <% if (podeEditarSvc || podeConsultarSvc) { %>
                                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarServicoOrgao?acao=iniciar&org=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&titulo=<%=TextHelper.forJavaScript(org_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.listar.servico.orgao.clique.aqui"/>" title="<hl:message key= "mensagem.listar.servico.orgao.clique.aqui"/>"><hl:message key= "rotulo.acoes.listar.servico"/></a>
                              <% } %>
                              <% if (podeConsultarCnvCor) { %>
                                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarServicoCorrespondente?acao=iniciar&org=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&titulo=<%=TextHelper.forJavaScript(org_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.listar.servico.orgao.clique.aqui"/>" title="<hl:message key= "mensagem.listar.servico.orgao.clique.aqui"/>"><hl:message key= "rotulo.acoes.listar.convenio.cor"/></a>
                              <% } %>
                              <% if (podeConsultarParamOrgao) { %>
                                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterParamOrgao?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.consultar.parametro.orgao.clique.aqui"/>" title="<hl:message key="mensagem.consultar.parametro.orgao.clique.aqui"/>"><hl:message key="rotulo.consultar.parametro.orgao.opcao"/></a>
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
                        <hl:message key="rotulo.lote.listagem.consignataria"/>
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
            <div class="btn-action">
              <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
            </div>
          </div>
        </div>
      </form>
  <% if (exibeBotaoRodape) { %>
  <div id="btns">
    <a id="page-up" onclick="up()">
      <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
        <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
      </svg>
    </a>
    <a id="page-down" onclick="down()">
      <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
        <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
      </svg>
    </a>
  </div>
  <% }%>
</c:set>

<c:set var="javascript">
  <script>
    <% if (exibeBotaoRodape) { %>
    let btnDown = document.querySelector('#btns');
    const pageActions = document.querySelector('#page-actions');
    const pageSize = document.body.scrollHeight;

    function up(){
      window.scrollTo({
        top: 0,
        behavior: "smooth",
      });
    }

    function down(){
      let toDown = document.body.scrollHeight;
      window.scrollBy({
        top: toDown,
        behavior: "smooth",
      });
    }

    function btnTab(){
      let scrollSize = document.documentElement.scrollTop;

      if(scrollSize >= 100){
        btnDown.classList.add('btns-active');
      } else {
        btnDown.classList.remove('btns-active');
      }
    }

    window.addEventListener('scroll', btnTab);
    <% } %>
  </script>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
