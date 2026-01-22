<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
 AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

 String tipo = (String) request.getAttribute("tipo");
 String codigo = (String) request.getAttribute("codigo");
 String titulo = (String) request.getAttribute("titulo"); 
 String operacao = (String) request.getAttribute("operacao"); 
 String perCodigo = (String) request.getAttribute("perCodigo");
 String entAltera = request.getAttribute("entAltera") != null ? (String) request.getAttribute("entAltera") : "";
 String exibeAutoDesbloqueio = request.getAttribute("exibeAutoDesbloqueio") != null ? (String) request.getAttribute("exibeAutoDesbloqueio") : "";
 boolean entPodeAltPer = (boolean) request.getAttribute("entPodeAltPer");
 boolean podeEditarPerfil = entAltera.equals("N") ? (entPodeAltPer ? (boolean) request.getAttribute("podeEditarPerfil") : false) : (boolean) request.getAttribute("podeEditarPerfil");
 List perFunCodigos = (List) request.getAttribute("perFunCodigos");
 String perDescricao = (String) (request.getAttribute("perDescricao") != null ? request.getAttribute("perDescricao") : "") ;
 String perVisivel = (String) (request.getAttribute("perVisivel") != null ? request.getAttribute("perVisivel") : "");
 Date perDataExpiracao = (Date) request.getAttribute("perDataExpiracao");
 String perAutoDesbloqueio = request.getAttribute("perAutoDesbloqueio") != null ? (String) request.getAttribute("perAutoDesbloqueio") : "";
 String linkAction = (String) request.getAttribute("linkAction");
 String perIpAcesso = (String) request.getAttribute("perIpAcesso");
 String perDdnsAcesso = (String) request.getAttribute("perDdnsAcesso");
 
 String linkRetorno = paramSession.getLastHistory();
 List funcoes = (List) request.getAttribute("funcoes");
 List perfil = (List) request.getAttribute("perfil");
 List paramSist = (List) request.getAttribute("paramSist");
 List inicio_grupo = new ArrayList();

//Exibe Botao Rodapé
boolean exibeBotaoRodape = request.getAttribute("exibeBotaoRodape") != null && (boolean) request.getAttribute("exibeBotaoRodape");


    if (AcessoSistema.ENTIDADE_SER.equals(tipo)) {
     linkRetorno = "../v3/carregarPrincipal";
 }
%>
<script language="JavaScript" type="text/JavaScript" src="../js/listagem.js"></script>
<c:set var="title">
<hl:message key="rotulo.usuario.perfil.editar" />
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form action="${linkAction}&<%=SynchronizerToken.generateToken4URL(request)%>"  method="POST" name="form1">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.editar.grid" /></h2>
    </div>
    <div class="card-body">
      
      <input type="HIDDEN" name="codigo" value="<%=TextHelper.forHtmlAttribute(codigo)%>">
      <input TYPE="HIDDEN" NAME="titulo" VALUE="<%=TextHelper.forHtmlAttribute(titulo)%>">
      <input TYPE="HIDDEN" NAME="PER_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(perCodigo)%>">
      <input type="HIDDEN" id="ip_list" name="ip_list" value="<%=!TextHelper.isNull(TextHelper.forHtmlAttribute(perIpAcesso)) ? TextHelper.forHtmlAttribute(perIpAcesso) : ""%>">
      <input type="HIDDEN" id="ddns_list" name="ddns_list" value="<%=!TextHelper.isNull(TextHelper.forHtmlAttribute(perDdnsAcesso)) ? TextHelper.forHtmlAttribute(perDdnsAcesso) : ""%>">
        
      <fieldset>
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="EDdescricao"><hl:message key="rotulo.usuario.perfil.descricao"/></label>
            <input type="text" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.descricao", responsavel)%>" class="form-control" onFocus="SetarEventoMascara(this,'#*50',true);" onBlur="fout(this);ValidaMascaraV4(this);" id="EDdescricao" name="PER_DESCRICAO" value="<%=TextHelper.forHtmlAttribute(perDescricao.replaceAll("\"", "&quot;"))%>"  <%=(String)((!podeEditarPerfil) ? "disabled" : "")%>>
          </div>
        </div>
        <% if (responsavel.isSup() && !AcessoSistema.ENTIDADE_SER.equals(tipo)) { %>
            <div class="form-group" role="radiogroup" aria-labelledby="PerfilVisivelParaOutrosPapeis">
                <div class="form-group mb-0">
                <span id="PerfilVisivelParaOutrosPapeis"><hl:message key="rotulo.perfil.visivel.outros.papeis"/></span>
                </div>
                <div class="form-check form-check-inline pt-2">
                  <input class="form-check-input ml-1" type="radio" name="PER_VISIVEL" id="Rsim" value="S" <%=(String)((perVisivel != null && perVisivel.equals("S") ? " checked " : "") )%> <%=(String)(!podeEditarPerfil ? " disabled " : "")%>>
                  <label class="form-check-label labelSemNegrito ml-1 pr-4" for="Rsim" ><hl:message key="rotulo.sim"/></label>
                </div>
                <div class="form-check form-check-inline pt-2">
                  <input class="form-check-input ml-1" type="radio" name="PER_VISIVEL" id="Rnao" value="N" <%=(String)((perVisivel != null && perVisivel.equals("N") ? " checked " : "") )%> <%=(String)(!podeEditarPerfil ? " disabled " : "")%> >
                  <label class="form-check-label labelSemNegrito ml-1 pr-4" for="Rnao"><hl:message key="rotulo.nao"/></label>
                </div>
            </div>
         <%} %>
        <div class="row">
          <div class="form-group col-sm-4">
            <label for=""><hl:message key="rotulo.usuario.perfil.data.expiracao"/></label>
            <hl:htmlinput name="PER_DATA_EXPIRACAO" di="PER_DATA_EXPIRACAO" type="text" 
            classe="Edit form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
            value="<%= !TextHelper.isNull(perDataExpiracao) ? DateHelper.format(perDataExpiracao, LocaleHelper.getDatePattern()) : "" %>"
            placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.data.expiracao.place.holder", responsavel)%>"
            others = "<%=(String)((!podeEditarPerfil) ? "disabled" : "")%>"
            /> 
          </div>
        </div> 
        <% if (entPodeAltPer) { %> 
          <div class="row">
            <div class="col-sm-6 col-md-6 mb-2">
              <div class="form-group mb-0">
                <span id="perEntAltera"><hl:message key="rotulo.usuario.entidade.altera"/></span>
              </div>
              <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="perEntAltera">
                <input class="form-check-input ml-1" type="radio" name="PER_ENT_ALTERA" id="PER_ENT_ALTERA_SIM" value="S" <%= entAltera.equals(CodedValues.TPA_SIM) ? "checked" : "" %> <%=(String)(!podeEditarPerfil ? " disabled " : "")%>>
                <label class="form-check-label pr-3" for="PER_ENT_ALTERA_SIM">
                  <span class="text-nowrap align-text-top"><hl:message key="rotulo.sim"/></span>
                </label>
                </div>
                <div class="form-check form-check-inline pt-2">
                 <input class="form-check-input ml-1" type="radio" name="PER_ENT_ALTERA" id="PER_ENT_ALTERA_NAO" value="N" <%= entAltera.equals(CodedValues.TPA_NAO) ? "checked" : "" %> <%=(String)(!podeEditarPerfil ? " disabled " : "")%>>
                 <label class="form-check-label" for="PER_ENT_ALTERA_NAO">
                  <span class="text-nowrap align-text-top"><hl:message key="rotulo.nao"/></span>
                </label>
              </div>
            </div>
          </div>    
        <% } %>
          <% if (!TextHelper.isNull(exibeAutoDesbloqueio) && exibeAutoDesbloqueio.equals(CodedValues.TPA_SIM)) { %>
          <div class="row">
              <div class="col-sm-6 col-md-6 mb-2">
                  <div class="form-group mb-0">
                      <span id="PER_AUTO_DESBLOQUEIO"><hl:message key="rotulo.usuario.auto.desbloqueio"/></span>
                  </div>
                  <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="perEntAltera">
                      <input class="form-check-input ml-1" type="radio" name="PER_AUTO_DESBLOQUEIO" id="PER_AUTO_DESBLOQUEIO_SIM" value="S" <%= perAutoDesbloqueio.equals(CodedValues.TPA_SIM) ? "checked" : "" %> <%=(String)(!podeEditarPerfil ? " disabled " : "")%>>
                      <label class="form-check-label pr-3" for="PER_AUTO_DESBLOQUEIO_SIM">
                          <span class="text-nowrap align-text-top"><hl:message key="rotulo.sim"/></span>
                      </label>
                  </div>
                  <div class="form-check form-check-inline pt-2">
                      <input class="form-check-input ml-1" type="radio" name="PER_AUTO_DESBLOQUEIO" id="PER_AUTO_DESBLOQUEIO_NAO" value="N" <%= perAutoDesbloqueio.equals(CodedValues.TPA_NAO) ? "checked" : "" %> <%=(String)(!podeEditarPerfil ? " disabled " : "")%>>
                      <label class="form-check-label" for="PER_AUTO_DESBLOQUEIO_NAO">
                          <span class="text-nowrap align-text-top"><hl:message key="rotulo.nao"/></span>
                      </label>
                  </div>
              </div>
          </div>
          <% } 
	          	if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_PERFIL_IP_ACESSO, responsavel)) {
	          %>
	          <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
	            <jsp:param name="tipo_endereco" value="numero_ip" />
	            <jsp:param name="nome_campo" value="PER_IP_ACESSO" />
	            <jsp:param name="nome_lista" value="listaIps" />
	            <jsp:param name="lista_resultado" value="perfil_ip_acesso" />
	            <jsp:param name="label" value="rotulo.usuario.ips.acesso" />
	            <jsp:param name="mascara" value="#I30" />
	            <jsp:param name="pode_editar" value="<%=(boolean) true%>" />
	            <jsp:param name="bloquear_ip_interno" value="false" />
	            <jsp:param name="placeHolder" value="mensagem.placeholder.digite.ip.acesso" />
	          </jsp:include>
	          <%
	          	}
	          %>
	          
	          <%
	          	if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_PERFIL_ENDERECO_ACESSO, responsavel)) {
	          %>
	          <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
	            <jsp:param name="tipo_endereco" value="url" />
	            <jsp:param name="nome_campo" value="PER_DDNS_ACESSO" />
	            <jsp:param name="nome_lista" value="listaDDNSs" />
	            <jsp:param name="lista_resultado" value="perfil_ddns_acesso" />
	            <jsp:param name="label" value="rotulo.usuario.enderecos.acesso" />
	            <jsp:param name="mascara" value="#*100" />
	            <jsp:param name="pode_editar" value="<%=(boolean) true%>" />
	            <jsp:param name="placeHolder" value="mensagem.placeholder.digite.endereco.acesso" />
	          </jsp:include>
	          <%
	          	}
          %>
          
          <% if (!TextHelper.isNull(perCodigo) && (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_PERFIL_IP_ACESSO, responsavel) || ShowFieldHelper.showField(FieldKeysConstants.EDITAR_PERFIL_ENDERECO_ACESSO, responsavel))) { %>
                 <div class="row">
                   <div class="form-group col-sm-4 col-md-6">
                     <span class="text-nowrap align-text-top">
                       <input class="form-check-input ml-1" type="checkbox" name="PER_REMOVE_RES_USU" id="PER_REMOVE_RES_USU" value="S">
                       <label class="form-check-label labelSemNegirto ml-1" aria-label='<hl:message key="rotulo.remover.restricao.ip.dns.usuario.perfil"/>' for="PER_REMOVE_RES_USU"><hl:message key="rotulo.remover.restricao.ip.dns.usuario.perfil"/></label>
                     </span>
                   </div>
                 </div>
         <%} %>
          
      </fieldset>
      <fieldset>
    <%
      String funcao = "", fun_codigo = "";
      String grf_codigo = "";
      String grf_descricao = "";
      
      List funcoes_grf = new ArrayList();
      List fun_codigos = new ArrayList();
      
      int fim_grupo = -1;
      int num_grupo = -1;
      
      CustomTransferObject cto = new CustomTransferObject();
      cto.setAttribute(Columns.FUN_GRF_CODIGO, "");
      funcoes.add(cto);
      
      Iterator it2 = funcoes.iterator();
      CustomTransferObject customs;
      
      while (it2.hasNext()) {
      
        customs = (CustomTransferObject) it2.next();
      
        if (!customs.getAttribute(Columns.FUN_GRF_CODIGO).toString().equals(grf_codigo)) {
          if (!grf_codigo.equals("")) { %>
          
            <div class="row">
              <div class="col-sm-12 col-md-12">
                <h3 class="legend">
                  <span id="geral"><%=TextHelper.forHtmlContent(grf_descricao)%></span>
                </h3>
                <div class="form-check">
                  <div class="row" role="group" aria-labelledby="geral">
                  <%for (int i=0; i<funcoes_grf.size(); i++) {
                      CustomTransferObject custom = (CustomTransferObject)funcoes_grf.get(i);
                      funcao = custom.getAttribute(Columns.FUN_DESCRICAO).toString();
                      fun_codigo = custom.getAttribute(Columns.FUN_CODIGO).toString();
                  %>
                    <div class="col-sm-12 col-md-6">
                      <input class="form-check-input ml-1" type="checkbox" name="funcao" id="fun_<%=TextHelper.forHtmlAttribute(fun_codigo)%>" value="<%=TextHelper.forHtmlAttribute(fun_codigo)%>" <%=(String)(perFunCodigos == null || perFunCodigos.contains(fun_codigo) ? " checked " : "")%> <%=(String)((!podeEditarPerfil) ? " disabled " : "")%>>
                      <label class="form-check-label ml-1 ml-1 ml-1 ml-1" for="fun_<%=TextHelper.forHtmlAttribute(fun_codigo)%>">
                        <span class="text-nowrap align-text-top"><%=TextHelper.forHtmlContent(funcao)%></span>
                      </label>
                    </div> 
                  <%}%>
                    <div class="col-sm-12 col-md-6">
                      <input class="form-check-input ml-1" type="checkbox" name="checkGrupo" id="geralTodos_<%=grf_codigo%>" onClick="check_uncheck_grupo(<%=TextHelper.forJavaScript((inicio_grupo.get(num_grupo)))%>,<%=(int)fim_grupo%>,<%=(int)num_grupo%>);"
                        <%=(String)((!podeEditarPerfil) ? "disabled" : "")%>>
                      <label class="form-check-label ml-1 ml-1 ml-1 ml-1" for="geralTodos_<%=grf_codigo%>">
                        <span class="text-nowrap align-text-top font-weight-bold"><hl:message key="rotulo.campo.todos.simples"/></span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            </div>
        <%}
      
          funcoes_grf.clear();  

          grf_codigo = (String) customs.getAttribute(Columns.FUN_GRF_CODIGO);
          grf_descricao = (String) customs.getAttribute(Columns.GRF_DESCRICAO);
      
          if (!grf_codigo.equals("")) {
            num_grupo++;
            inicio_grupo.add(new Integer(fim_grupo + 1));
          } else {
            break;
          }
        }
    
        fun_codigos.add(customs.getAttribute(Columns.FUN_CODIGO));
        fim_grupo ++;  
        funcoes_grf.add(customs);
      }
      inicio_grupo.add(new Integer(fim_grupo + 1));%>
      </fieldset>

    </div>
  </div>
<%if (podeEditarPerfil) {%>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="checkAll(f0, 'funcao');checkAll(f0, 'checkGrupo'); return false;"><hl:message key="rotulo.usuario.perfil.funcao.marcar.tudo"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="uncheckAll(f0, 'funcao');uncheckAll(f0, 'checkGrupo'); return false;"><hl:message key="rotulo.usuario.perfil.funcao.desmarcar.tudo"/></a>
  </div>

  <% if (perfil != null && perfil.size() > 0) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.perfil.copiar.funcoes"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="form-group col-sm-6">
          <label for="copia_perfil"><hl:message key="rotulo.perfil.copiar.funcoes.do.perfil"/></label>
          <select class="form-control form-select select" id="copia_perfil" name="copia_perfil" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
            <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
            <%
              for (int x = 0; x < perfil.size(); x++) {
                TransferObject to = (TransferObject)perfil.get(x);
                Short status = (Short)to.getAttribute("STATUS");
                if (!status.equals(CodedValues.STS_INDISP)) {
            %>
              <option value="<%=TextHelper.forHtmlAttribute((to.getAttribute(Columns.PER_CODIGO)))%>"><%=TextHelper.forHtml((to.getAttribute(Columns.PER_DESCRICAO)))%></option>
            <%
                }
              }
            %>
          </select>
        </div>
        <%if (!operacao.equals("inserir")) {%>
          <div class="form-group col-sm-6 mb-1" style="page-break-after: avoid;">
            <label for="aplica_perfil"><hl:message key="rotulo.perfil.aplicar.funcoes.no.perfil"/></label>
            <select class="form-control form-select" name="aplica_perfil" class="Select" id="aplica_perfil" size="4" multiple="multiple" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
             <%
                for (int x = 0; x < perfil.size(); x++) {
                  TransferObject to = (TransferObject)perfil.get(x);
                  Short status = (Short)to.getAttribute("STATUS");
                  if (!status.equals(CodedValues.STS_INDISP)) {
              %>
              <option value="<%=TextHelper.forHtmlAttribute((to.getAttribute(Columns.PER_CODIGO)))%>"><%=TextHelper.forHtml((to.getAttribute(Columns.PER_DESCRICAO)))%></option>
              <%
                  }
                }
             %>
            </select>
            <div class="slider col-sm-12 mt-2 ">
              <div class="tooltip-inner"><hl:message key="mensagem.perfil.tecla.ctrl.selecionar.multiplas"/></div>
            </div>
          </div>
        <%}%>
      </div>
    </div>
  </div>
  <%}%>
<%}%>

</form>

<div id="actions" class="btn-action mt-3">
      <% if (podeEditarPerfil) { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkRetorno, request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="vf_cadastro_perfil(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
      <% } else { %>
        <a class="btn btn-primary" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkRetorno, request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      <% } %>
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
        <a id="page-actions" onclick="toActionBtns()">
            <svg xmlns="http://www.w3.org/2000/svg" width="145.344" height="145.344" viewBox="0 0 145.344 145.344">
                <path id="União_1" data-name="União 1" d="M-20,59.672a72.672,72.672,0,1,1,72.671,72.672A72.671,72.671,0,0,1-20,59.672Zm10.164,0A62.508,62.508,0,1,0,52.672-2.836,62.579,62.579,0,0,0-9.836,59.672Zm82.6,40.182H24.545A12.069,12.069,0,0,1,12.49,87.8V31.544A12.069,12.069,0,0,1,24.545,19.49h44.2a4.014,4.014,0,0,1,2.841,1.177L91.678,40.757A4.019,4.019,0,0,1,92.855,43.6V87.8A12.069,12.069,0,0,1,80.8,99.854Zm0-40.182a4.018,4.018,0,0,1,4.019,4.018V91.817H80.8A4.023,4.023,0,0,0,84.818,87.8V45.263L67.081,27.526H36.6V39.58H64.727a4.019,4.019,0,0,1,0,8.037H32.581A4.018,4.018,0,0,1,28.563,43.6V27.526H24.545a4.023,4.023,0,0,0-4.018,4.019V87.8a4.023,4.023,0,0,0,4.018,4.018h4.019V63.689a4.018,4.018,0,0,1,4.018-4.018ZM36.6,91.817H68.745V67.708H36.6Z" transform="translate(20 13)"/>
            </svg>
        </a>
    </div>
    <% }%>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  window.onload = formLoad;
  f0 = document.forms[0];

  function formLoad() {
	  if (document.forms[0].listaIps != undefined) {
	    preencheLista('ip_list','listaIps');
	  }
	  if (document.forms[0].listaDDNSs != undefined) {
	    preencheLista('ddns_list','listaDDNSs');
	  }
  }
  
  function vf_cadastro_perfil() {
	  var Controles = new Array("PER_DESCRICAO");
	    var Msgs = new Array('<hl:message key="mensagem.perfil.informar.descricao"/>');
	    if (ValidaCamposV4(Controles, Msgs)) {
		    	montaListaIps('perfil_ip_acesso','listaIps'); 
		    	montaListaIps('perfil_ddns_acesso','listaDDNSs');
		    if (existeFuncaoAdmSelecionada() && !(confirm('<hl:message key="mensagem.perfil.confirma.atribuicao.funcoes.administrador"/>'))) {
		         return false;
		    } else {
		         f0.submit();
		    }
	    }

  }

  function existeFuncaoAdmSelecionada() {
    var arrFunAdm = new Array(<%=(String)("'" + TextHelper.join(CodedValues.FUNCOES_ADMINISTRADOR.toArray(), "','") + "'")%>);
    for (i=0; i < f0.funcao.length; i++) {
      if (f0.funcao[i].checked) {
        var x = arrFunAdm.length;
        while (x--) {
          if (arrFunAdm[x] === f0.funcao[i].value) {
            return true;
          }
        }
      }
    }
  }

  function check_uncheck_grupo(inicio, fim, grupo) {
    if (f0.checkGrupo[grupo].checked == true){
      for (i=inicio; i <= fim; i++) {
        f0.funcao[i].checked = true;
      }
    } else {
      for (i=inicio; i <= fim; i++) {
        f0.funcao[i].checked = false;
      }
    }
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

        function toActionBtns(){
            let save = document.querySelector('#actions').getBoundingClientRect().top;
            window.scrollBy({
                top: save,
                behavior: "smooth",
            });
        }

        function btnTab(){
            let scrollSize = document.body.scrollTop;
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
