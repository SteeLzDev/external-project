<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
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
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  boolean podeEditarCsa = (Boolean) request.getAttribute("podeEditarCsa");
  boolean podeEditarSvc = (Boolean) request.getAttribute("podeEditarSvc");
  boolean podeConsultarSvc = (Boolean) request.getAttribute("podeConsultarSvc");
  boolean podeEditarPrazo = (Boolean) request.getAttribute("podeEditarPrazo");
  boolean podeConsultarPrazo = (Boolean) request.getAttribute("podeConsultarPrazo");
  boolean podeEditarCnv = (Boolean) request.getAttribute("podeEditarCnv");
  boolean podeConsultarCnv = (Boolean) request.getAttribute("podeConsultarCnv");
  boolean podeEditarIndices = (Boolean) request.getAttribute("podeEditarIndices");
  boolean podeBloquearPostoCsaSvc = (Boolean) request.getAttribute("podeBloquearPostoCsaSvc");
  boolean permiteCadIndice = (Boolean) request.getAttribute("permiteCadIndice");
  boolean csePodeEditarParamCnv = (Boolean) request.getAttribute("csePodeEditarParamCnv");
  boolean utiBloqVincSer = (Boolean) request.getAttribute("utiBloqVincSer");
  
  int filtro_tipo = (int) request.getAttribute("filtro_tipo");
  
  List<?> servicos = (List<?>) request.getAttribute("servicos");
  
  String csa_codigo = (String) request.getAttribute("csa_codigo");
  String csa_nome_link = (String) request.getAttribute("csa_nome_link");
  String filtro = (String) request.getAttribute("filtro");
  String org_codigo = (String) request.getAttribute("org_codigo");
  String titulo = (String) request.getAttribute("titulo");
  
%>
<c:set var="title">
<hl:message key="rotulo.listar.servicos.consignataria.titulo"/><%=TextHelper.forHtmlContent(!titulo.equals("") ? " - " + titulo.toUpperCase() : "")%>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row firefox-print-fix">
<!-- INICIO FILTRO -->    
    <div class="col-sm-5 col-md-4 d-print-none">
    <form NAME="form1" METHOD="post" ACTION="../v3/manterServico?acao=listarServicosCsa&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&tipo=consultar&csa=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(csa_nome_link)%>">
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
                    <OPTION VALUE="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.convenio.codigo"/></OPTION>
                    <OPTION VALUE="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.convenio.descricao"/></OPTION>
                    <OPTION VALUE="00" <%=(String)((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.convenio.bloqueado"/></OPTION>
                    <OPTION VALUE="01" <%=(String)((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.convenio.desbloqueado"/></OPTION>
                    <OPTION VALUE="04" <%=(String)((filtro_tipo ==  4) ? "SELECTED" : "")%>><hl:message key="rotulo.consignacao.existe"/></OPTION>
                    <OPTION VALUE="05" <%=(String)((filtro_tipo ==  5) ? "SELECTED" : "")%>><hl:message key="rotulo.consignacao.nao.existe"/></OPTION>
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
          <h2 class="card-header-title"><hl:message key="rotulo.servico.plural"/></h2>
        </div>
          <div class="card-body table-responsive p-0">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.convenio.codigo"/></th>
                  <th scope="col"><hl:message key="rotulo.convenio.descricao"/></th>
                  <th scope="col"><hl:message key="rotulo.situacao"/></th>
                  <th scope="col"><hl:message key="rotulo.consignataria.servico.consignacao"/></th>
                  <% if ((podeEditarSvc || podeConsultarSvc) && (podeEditarCnv || podeConsultarCnv || podeEditarPrazo || podeConsultarPrazo)) { %>
                  <th scope="col"><hl:message key="rotulo.acoes"/></th>
                  <% } %>
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
                      String descStatus = ((status == 0) ? 
                              ApplicationResourcesHelper.getMessage("rotulo.lista.servico.desbloqueado", responsavel) : 
                                  ((status == 1) ? 
                                          ApplicationResourcesHelper.getMessage("rotulo.lista.servico.bloqueado", responsavel) : 
                                          ApplicationResourcesHelper.getMessage("rotulo.lista.servico.desbloqueado", responsavel)));
                      String existeAde = (String) servico.getAttribute("TEMADE");
                  %>
                  <tr>
                    <td><%=TextHelper.forHtmlContent(svc_identificador.toUpperCase())%></td>
                    <td><%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%></td>
                    <%if (responsavel.isOrg()) { %>
                        <td <%=scv_codigo.equals("1") ? "" : "class=\"block\" "%>><%=descStatus%></td>
                    <% } else { %>
                        <td <%=scv_codigo.equals("0") ? "" : "class=\"block\" "%>><%=descStatus%></td>                    
                    <% } %>
                    <td><%=existeAde.equals(CodedValues.TPC_SIM) ? TextHelper.forHtmlContent(ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel)) : TextHelper.forHtmlContent(ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel))%></td>
                    <% if (podeEditarSvc || podeConsultarSvc) { %>
                          <td>
                            <div class="actions">
                               <div class="dropdown">
                                 <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                  <div class="form-inline">
                                    <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>">
                                     <svg>
                                       <use xlink:href="#i-engrenagem"></use>
                                     </svg>
                                   </span> <hl:message key="rotulo.botao.opcoes"/>
                                  </div>
                                  </a>
                                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                      <% if (!responsavel.isOrg()) { %>
                        <% if (podeEditarCnv || podeConsultarCnv) { %>
                                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/mantemConvenio?acao=iniciar&svc_codigo=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&svc_descricao=<%=TextHelper.forJavaScript(svc_descricao)%>&svc_identificador=<%=TextHelper.forJavaScript(svc_identificador)%>&csa_codigo=<%=TextHelper.forJavaScript(csa_codigo)%>&csa_nome=<%=TextHelper.forJavaScript(csa_nome_link)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                                    <% if (podeEditarCnv) { %>
                                        <hl:message key="rotulo.acao.bloquear.desbloquear"/>                                      
                                        <% } else { %>
                                        <hl:message key="rotulo.acoes.consultar.convenio"/>
                                    <% } %>
                                  </a>
                          <% } %>
                          <% if (status == 1) { %>
                            <% if (podeEditarPrazo || podeConsultarPrazo) { %>
                              <a class="dropdown-item" onclick="alerta_prazo()">
                                <hl:message key="rotulo.coeficiente.prazos"/>
                              </a>
                            <% } %>
                            <% if (responsavel.isCseSup() && csePodeEditarParamCnv && podeEditarCsa) { %>
                              <a class="dropdown-item" onclick="alerta_param()">
                                <hl:message key="rotulo.convenio.parametros"/>
                              </a>
                            <% } %>
                            <% if (permiteCadIndice && podeEditarCsa && podeEditarIndices) { %>
                              <a class="dropdown-item" onclick="alerta_indice()">
                                <hl:message key="rotulo.convenio.indices"/>
                              </a>
                              <% } %>
                          <% } else { %>
                            <% if (podeEditarPrazo || podeConsultarPrazo) { %>
                              <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarPrazoConsignataria?acao=iniciar&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(svc_descricao)%>&CSA_CODIGO=<%=TextHelper.forJavaScript(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(csa_nome_link)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                                <hl:message key="rotulo.coeficiente.prazos"/>
                              </a>
                            <% } %>
                           <% if (responsavel.isCseSup() && csePodeEditarParamCnv && podeEditarCsa) { %>
                              <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConsignataria?acao=editarServico&svc=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&SVC_IDENTIFICADOR=<%=TextHelper.forJavaScript(svc_identificador)%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(svc_descricao)%>&csa_codigo=<%=TextHelper.forJavaScript(csa_codigo)%>&csa_nome=<%=TextHelper.forJavaScript(csa_nome_link)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                                 <hl:message key="rotulo.convenio.parametros"/>
                               </a>
                            <% } %>                                          
                            <% if (permiteCadIndice && podeEditarCsa && podeEditarIndices) { %>
                               <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterIndice?acao=iniciar&svcCodigo=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&csaCodigo=<%=TextHelper.forJavaScript(csa_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                                 <hl:message key="rotulo.convenio.indices"/>
                              </a >
                            <% } %>
                            <% if (podeBloquearPostoCsaSvc) { %>
                              <a class="dropdown-item" href="#no-back" onClick="postData('../v3/bloquearPostoCsaSvc?acao=iniciar&svc=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&csa=<%=TextHelper.forJavaScript(csa_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                                <hl:message key="rotulo.acoes.bloquear.posto"/>
                              </a>
                            <% } %>
                          <% } %>
                        <% } else { %>
                          <% if (podeEditarCnv || podeConsultarCnv) { %>
                              <a class="dropdown-item" href="#no-back" onClick="postData('../v3/mantemConvenio?acao=iniciar&svc_codigo=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&svc_descricao=<%=TextHelper.forJavaScript(svc_descricao)%>&svc_identificador=<%=TextHelper.forJavaScript(svc_identificador)%>&csa_codigo=<%=TextHelper.forJavaScript(csa_codigo)%>&csa_nome=<%=TextHelper.forJavaScript(csa_nome_link)%><%=TextHelper.forJavaScript((!TextHelper.isNull(org_codigo)) ? "&org_codigo=" + org_codigo : "")%>&<%=SynchronizerToken.generateToken4URL(request)%>')">
                            <% if (podeEditarCnv) { %>
                                <hl:message key="rotulo.acao.bloquear.desbloquear"/>
                            <% } else { %>
                                  <hl:message key="rotulo.acoes.consultar.convenio"/>
                            <% } %>
                                </a>
                          <% } %>
                        <% } %>
                        <% if (utiBloqVincSer) { %>
                            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConsignataria?acao=editarCnvVincServidor&svc=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&SVC_IDENTIFICADOR=<%=TextHelper.forJavaScript(svc_identificador)%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(svc_descricao)%>&csa=<%=TextHelper.forJavaScript(csa_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                              <hl:message key="rotulo.acoes.editar.vinculo"/>
                            </a>                        
                        <% } %>
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
        <tfoot>
           <tr>
            <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.listar.servicos.consignataria.titulo", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
          </tr>
         </tfoot>        
      </table>
      <div class="card-footer">
        <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>               
    </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    </div> 
  </div>
 </div>   
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript">
    f0 = document.forms[0];
    function alerta_prazo() {
      alert('<hl:message key="mensagem.convenio.desbloquear.para.cadastrar.prazos"/>');
    }
    function alerta_param() {
      alert('<hl:message key="mensagem.convenio.desbloquear.para.editar.parametros"/>');
    }
    function alerta_indice() {
      alert('<hl:message key="mensagem.convenio.desbloquear.para.editar.indices"/>');
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
