<%--
* <p>Title: ListarServicos</p>
* <p>Description: Contem a lista de servicos</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: andrea.giorgini $
* $Revision: 26797 $
* $Date: 2019-05-23 11:59:25 -0300 (qui, 23 mai 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  boolean podeConsultarSvc = (Boolean) request.getAttribute("podeConsultarSvc");
  boolean podeEditarSvc = (Boolean) request.getAttribute("podeEditarSvc");
  boolean podeConsultarPrazo = (Boolean) request.getAttribute("podeConsultarPrazo");
  boolean podeEditarPrazo = (Boolean) request.getAttribute("podeEditarPrazo");
  boolean podeExcluirSvc = (Boolean) request.getAttribute("podeExcluirSvc");
  boolean podeConsCoef = (Boolean) request.getAttribute("podeConsCoef");
  boolean podeConsTaxaJuros = (Boolean) request.getAttribute("podeConsTaxaJuros");
  boolean podeConsLimiteTaxaJuros = (Boolean) request.getAttribute("podeConsLimiteTaxaJuros");
  boolean exigeMotivoOperacao = (Boolean) request.getAttribute("exigeMotivoOperacao");
  boolean temSimulacaoConsignacao = (Boolean) request.getAttribute("temSimulacaoConsignacao");
  boolean permitePriorizarServico = (Boolean) request.getAttribute("permitePriorizarServico"); 
  boolean temCET = (Boolean) request.getAttribute("temCET");
  
  boolean podeEditarCnv = (Boolean) request.getAttribute("podeEditarCnv");
  boolean podeConsultarCnv = (Boolean) request.getAttribute("podeConsultarCnv");
  
  int filtro_tipo = (int) request.getAttribute("filtro_tipo");
  
  List<?> servicos = (List<?>) request.getAttribute("servicos");
  
  String codGrupo = (String) request.getAttribute("codGrupo");
  String filtro = (String) request.getAttribute("filtro");
  String parametros = (String) request.getAttribute("parametros");
  String titulo = (String) request.getAttribute("titulo");
  //Exibe Botao Rodapé
  boolean exibeBotaoRodape = request.getAttribute("exibeBotaoRodape") != null && (boolean) request.getAttribute("exibeBotaoRodape");
  String digiteOfiltro = "<h1:message key='rotulo.acao.digite.filtro'/>";
%>
<c:set var="title">
  <%=TextHelper.forHtml(titulo)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<% if (codGrupo.equals("")) { %>
  <div class="row d-print-none">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
      <% if(podeEditarSvc) { %>
        <div class="btn-action">
            <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit" onClick="postData('../v3/manterServico?acao=inserirServico&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.inserir.servico"/></button>
        </div>
        <% } %>
      </div>
    </div>
  </div>
<%}%>
  <div class="row firefox-print-fix">
<!-- INICIO FILTRO -->    
    <div class="col-sm-5 col-md-4 d-print-none">
    <FORM NAME="form1" METHOD="post" ACTION="../v3/manterServico?acao=iniciar&<%=TextHelper.forHtmlAttribute(parametros)%>&<%=SynchronizerToken.generateToken4URL(request)%>">
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
                <% if (permitePriorizarServico) { %>
                <th scope="col"><hl:message key="rotulo.servico.prioridade"/></th>
                <% } %>
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
              String svc_codigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
              String svc_descricao = servico.getAttribute(Columns.SVC_DESCRICAO).toString();
              String svc_identificador = servico.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
              String svc_prioridade = servico.getAttribute(Columns.SVC_PRIORIDADE) != null ? servico.getAttribute(Columns.SVC_PRIORIDADE).toString() : "";
              String svc_ativo = servico.getAttribute(Columns.SVC_ATIVO) != null ? servico.getAttribute(Columns.SVC_ATIVO).toString() : "1";
            %>
              <tr>
                <td><%=TextHelper.forHtmlContent(svc_identificador.toUpperCase())%></td>
                <td><%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%></td>
                <% if (permitePriorizarServico) { %>
                <td><%=TextHelper.forHtmlContent(svc_prioridade)%></td>
                <% } %>
                <% String msgServicoBloqueadoDesbloqueado = svc_ativo.equals("1") ? ApplicationResourcesHelper.getMessage("rotulo.servico.status.desbloqueado", responsavel): ApplicationResourcesHelper.getMessage("rotulo.servico.status.bloqueado", responsavel); %>
                <td <%=svc_ativo.equals("0") ? "class=\"block\"" : ""%>><%=TextHelper.forHtmlAttribute(msgServicoBloqueadoDesbloqueado)%></td>
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
                      <% if(podeEditarSvc) { %>
                        <a class="dropdown-item" href="#no-back" onClick="bloquearServico(<%=TextHelper.forJavaScript(svc_ativo)%>, '<%=TextHelper.forJavaScript(svc_codigo)%>', 'SVC', '../v3/manterServico?acao=efetivarAcaoServico&operacao=bloquear&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(svc_descricao)%>')"><hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterServico?acao=consultarServico&svc=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                      <% } else if(podeConsultarSvc) { %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterServico?acao=consultarServico&svc=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.consultar"/></a>           
                      <% } %>
                      <% if (podeEditarPrazo || podeConsultarPrazo) { %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarPrazo?acao=iniciar&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&titulo=<%=TextHelper.forJavaScript(svc_descricao)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.prazos"/></a>
                      <% } %>
                      <% if (podeConsCoef && temSimulacaoConsignacao) { %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/visualizarRankingServico?acao=consultarCoeficiente&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&titulo=<%=TextHelper.forJavaScript(svc_descricao)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.ranking"/></a>
                      <% } else if (podeConsTaxaJuros) { %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarTaxaJuros?acao=iniciar&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&titulo=<%=TextHelper.forJavaScript(svc_descricao)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.ranking"/></a>
                      <% } %>
                      <% if (podeConsLimiteTaxaJuros) { 
                          String msgLimiteCetTaxa = temCET ? ApplicationResourcesHelper.getMessage("rotulo.servico.limite.cet", responsavel): ApplicationResourcesHelper.getMessage("rotulo.servico.limite.taxa", responsavel);
                      %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarLimiteTaxas?acao=iniciar&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&titulo=<%=TextHelper.forJavaScript(svc_descricao)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><%=TextHelper.forHtmlAttribute(msgLimiteCetTaxa)%></a>
                      <% } %>
                      <% if (podeExcluirSvc) { %> 
                        <a class="dropdown-item" href="#no-back" onClick="ExcluirEntidade('<%=TextHelper.forJavaScript(svc_codigo)%>', 'SVC', '../v3/manterServico?<%=SynchronizerToken.generateToken4URL(request)%>&acao=excluirServico', '<%=TextHelper.forJavaScript(svc_descricao)%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                      <% } %>
                      <% if(podeEditarSvc) { %>                  
                         <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterServico?acao=consultarCampo&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                         <hl:message key="rotulo.servico.editar"/></a>
                       <% } 
                         if(podeEditarCnv || podeConsultarCnv) { 
                            String linkRetorno = "../v3/mantemConvenio?acao=edtPrioridadeCnv&SVC_CODIGO=" + svc_codigo + "&" + SynchronizerToken.generateToken4URL(request) + "&_skip_history_=true";
                            String consultarEditar;
                            if(podeEditarCnv) {
                              consultarEditar=ApplicationResourcesHelper.getMessage("rotulo.convenio.editar", responsavel);
                            } else {
                              consultarEditar=ApplicationResourcesHelper.getMessage("rotulo.convenio.consultar", responsavel);
                            } %>               
                         <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkRetorno)%>&linkRetorno=<%=TextHelper.forJavaScript(linkRetorno.replace('?', '$').replace('=', '(').replace('&', '|'))%>')">
                         <%=TextHelper.forHtmlAttribute(consultarEditar)%></a>
                       <% }%>
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
      <div class="btn-action">
        <%if (!TextHelper.isNull(codGrupo)) { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');return false;" ><hl:message key="rotulo.botao.cancelar"/></a>
        <% } else { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
        <% } %>
      </div>
    </div>
  </div>
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
    <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
    <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
    <script type="text/JavaScript">
      function bloquearServico(status, codigo, tipo, alink, desc, msg) {
      <% if (exigeMotivoOperacao) { %>
        var url = alink + (alink.indexOf('?') == -1 ? "?" : "&") + "status=" + status + "&codigo=" + codigo;
        postData(url);
        return true;
      <% } else { %>
        return BloquearEntidade(status, codigo, tipo, alink, desc, msg);
      <% } %>
      }
    </script>
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