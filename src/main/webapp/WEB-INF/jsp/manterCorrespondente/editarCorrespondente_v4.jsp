<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%@ page import="com.zetra.econsig.dto.entidade.*" %>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

/* usuario */
boolean podeCriarUsu = (Boolean) request.getAttribute("podeCriarUsu");
boolean podeConsultarUsu = (Boolean) request.getAttribute("podeConsultarUsu");
/* correspondente */
boolean podeConsultarCor = (Boolean) request.getAttribute("podeConsultarCor");
boolean podeEditarCor = (Boolean) request.getAttribute("podeEditarCor");
boolean podeEditarCorBackup = (Boolean) request.getAttribute("podeEditarCorBackup");
boolean podeExcluirCor = (Boolean) request.getAttribute("podeExcluirCor");
boolean podeEditarCnpj = (Boolean) request.getAttribute("podeEditarCnpj");
boolean podeEditarEnderecosCor = (Boolean) request.getAttribute("podeEditarEnderecosCor");

/* perfil usuario */
boolean podeConsultarPerfilUsu = (Boolean) request.getAttribute("podeConsultarPerfilUsu");

// Verifica parâmetro de sistema se permite cadastro de ip interno
boolean permiteCadIpInternoCsaCor = (Boolean) request.getAttribute("permiteCadIpInternoCsaCor");
boolean podeEditarEnderecoAcesso = (Boolean) request.getAttribute("podeEditarEnderecoAcesso");

String cor_codigo = (String) request.getAttribute("cor_codigo");
String titulo = (String) request.getAttribute("titulo");
String parametros = (String) request.getAttribute("parametros");
String csa_codigo = (String) request.getAttribute("csa_codigo");
Object paramVrfIpAcesso = (Object) request.getAttribute("paramVrfIpAcesso");
boolean cadastraEmpCorrespondente = (Boolean) request.getAttribute("cadastraEmpCorrespondente");
String msgErro = (String) request.getAttribute("msgErro");
String ecoCodigo = (String) request.getAttribute("ecoCodigo");
String cor_ip_acesso = (String) request.getAttribute("cor_ip_acesso");
String cor_ddns_acesso = (String) request.getAttribute("cor_ddns_acesso");
String ecoCnpj = (String) request.getAttribute("ecoCnpj");
CorrespondenteTransferObject correspondente = (CorrespondenteTransferObject) request.getAttribute("correspondente");
String periodoEnvioEmailAudit = (String) request.getAttribute("periodoEnvioEmailAudit");
String cor_ativo = (String) request.getAttribute("cor_ativo");
String cor_nome = (String) request.getAttribute("cor_nome");

%>
<c:set var="title">
  <hl:message key="rotulo.correspondente.singular"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <%= JspHelper.geraCamposHidden(parametros) %>
    <%if (cor_codigo != null) {%>
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
            <% if ((responsavel.isCsa() || responsavel.isCseSup() || responsavel.isOrg()) && podeEditarCorBackup) { %>
              <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(cor_ativo)%>, '<%=TextHelper.forJavaScript(cor_codigo)%>', 'COR', '../v3/manterCorrespondente?acao=bloquear&csa=<%=csa_codigo%>&<%=TextHelper.forJavaScript(cor_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>', '<%=TextHelper.forJavaScript(cor_nome)%>')"><hl:message key="rotulo.acao.bloquear.desbloquear"/></a>
            <% } %> 
            <% if (podeEditarCorBackup) { %>
              <% if (podeConsultarPerfilUsu) {%>
                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarPerfilCor?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&titulo=<%=TextHelper.forJavaScript(cor_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.listar.perfil.usuario"/></a>
              <%} if (podeConsultarUsu) {%>
                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarUsuarioCor?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&titulo=<%=TextHelper.encode64(cor_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.usuario.singular"/></a>
              <% } %>
            <% } else if (podeConsultarCor) {%>
              <%//Icone ativado apenas se nao puder editarPerfil
              if (podeConsultarPerfilUsu) {%>  
                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarPerfilCor?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&titulo=<%=TextHelper.forJavaScript(cor_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.listar.perfil.usuario"/></a>
              <% }// Icone ativado apenas se nao puder editarUsu
              if (podeConsultarUsu) {%>
                <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarUsuarioCor?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&titulo=<%=TextHelper.encode64(cor_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.usuario.singular"/></a>
              <% } %>
            <% } %>
            <% if (podeCriarUsu) { %>
              <a class="dropdown-item" href="#no-back" onClick="postData('../v3/inserirUsuarioCor?acao=iniciar&operacao=inserir_cor&tipo=COR&codigo=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&titulo=<%=TextHelper.encode64(cor_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.novo.usuario"/></a>
            <% } %>
            <% if (responsavel.isCsaCor() && responsavel.temPermissao(CodedValues.FUN_USUARIO_AUDITOR) && (periodoEnvioEmailAudit != null && !periodoEnvioEmailAudit.equals(CodedValues.PER_ENV_EMAIL_AUDIT_DESABILITADO))) {%>
              <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterFuncoesAuditaveis?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&descricao=<%=TextHelper.forJavaScript(cor_nome)%>&tipo=<%=TextHelper.forJavaScript(AcessoSistema.ENTIDADE_COR)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.configurar.auditoria"/></a>
            <% } %>
            <% if (podeEditarEnderecosCor) {%>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterEnderecosCorrespondente?acao=iniciar&COR_CODIGO=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.correspondente.editar.enderecos"/></a>
            <% } %>
          </div>
        </div> 
      </div>  
    <%}%>
    <div class="col-sm-12">  
      <form method="post" action="../v3/manterCorrespondente" name="form1">
        <%= JspHelper.geraCamposHidden(parametros) %>
        <input type="hidden" id="csa" name="csa" value="<%=TextHelper.forHtmlAttribute(csa_codigo )%>">
        <div class="card">
          <div class="card-header">
            <%if ((correspondente != null && TextHelper.isNull(JspHelper.verificaVarQryStr (request, "ECO_CNPJ"))) && !TextHelper.isNull(correspondente.getCorIdentificador())) {%>
              <h2 class="card-header-title"><%=TextHelper.forHtmlContent(correspondente.getCorIdentificador() + " - " + correspondente.getCorNome())%></h2>
              <% if (cor_ativo.equals(CodedValues.STS_ATIVO.toString())) { %>
              <span class="ultima-edicao"><hl:message key="rotulo.situacao.correspondente.desbloqueado"/></span>
              <% } else { %>
              <span class="ultima-edicao"><hl:message key="rotulo.situacao.correspondente.bloqueado"/></span>
              <% } %>
            <% } else {%>
              <h2 class="card-header-title"><hl:message key="rotulo.titulo.novo.correspondente"/></h2>
            <% }%>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-4">
                <label for="codigo"><hl:message key="rotulo.codigo.correspondente"/></label>
                <%String onFocusCodCor = "SetarEventoMascara(this,'#A40',true);";%>
                <hl:htmlinput name="COR_IDENTIFICADOR"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorIdentificador(): JspHelper.verificaVarQryStr(request, \"COR_IDENTIFICADOR\"))%>"
               	  size="32"
                  onFocus="<%= onFocusCodCor %>"
                  others="<%=TextHelper.forHtmlAttribute( responsavel.isCor() ? "disabled" : (!podeEditarCor && TextHelper.isNull(JspHelper.verificaVarQryStr (request, "ECO_CNPJ"))) ? "disabled" : "")%>"
                  di="codigo"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.codigo", responsavel)%>"
                />
                <%=JspHelper.verificaCampoNulo(request, "COR_IDENTIFICADOR")%>
              </div>
              <div class="form-group col-sm-4">
                <label for="nome"><hl:message key="rotulo.nome.correspondente"/></label>
                <%String onFocusNomeCor = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_NOME"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorNome():JspHelper.verificaVarQryStr(request, \"COR_NOME\"))%>"
                  size="32"
                  onFocus="<%= onFocusNomeCor %>"
                  others="<%=TextHelper.forHtmlAttribute( responsavel.isCor() ? "disabled" : !podeEditarCor ? "disabled" : "")%>"
                  di="nome"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nome", responsavel)%>"
                />
                <%=JspHelper.verificaCampoNulo(request, "COR_NOME")%>
              </div>
              <div class="form-group col-sm-4">
                <label for=cnpj><hl:message key="rotulo.cnpj.correspondente"/></label>
                <%String onFocusCnpj = "SetarEventoMascara(this,'" + LocaleHelper.getCnpjMask() + "',true);"; %>
                <hl:htmlinput name="COR_CNPJ"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente !=null ? correspondente.getCorCnpj():JspHelper.verificaVarQryStr(request, \"COR_CNPJ\"))%>"
                  onFocus="<%= onFocusCnpj %>"
                  size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjSize())%>"
                  maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMaxLenght())%>"
                  others="<%=TextHelper.forHtmlAttribute( responsavel.isCor() ? "disabled" : (!podeEditarCor || !podeEditarCnpj) ? "disabled" : "")%>"
                  di="cnpj"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cnpj", responsavel)%>"
                />
              </div>
            </div>
            <div class="legend">
              <span><hl:message key="rotulo.responsavel.plural"/></span>
            </div>
            <div class="row">
              <div class="form-group col-sm-4">
                <label for="responsavel1"><hl:message key="rotulo.correspondente.responsavel.um"/></label>
                <%String onFocusResUm = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_RESPONSAVEL"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorResponsavel():JspHelper.verificaVarQryStr(request, \"COR_RESPONSAVEL\"))%>"
                  size="32"
                  onFocus="<%=onFocusResUm%>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="responsavel1"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.responsavel", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="cargo1"><hl:message key="rotulo.correspondente.cargo.um"/></label>
                <%String onFocusCarDois = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_RESP_CARGO"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorRespCargo():JspHelper.verificaVarQryStr(request, \"COR_RESP_CARGO\"))%>"
                  size="32"
                  onFocus="<%=onFocusCarDois %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="cargo1"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.cargo", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="telefone1"><hl:message key="rotulo.correspondente.telefone.um"/></label>
                <%String onFocusTelUm = "etarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_RESP_TELEFONE"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorRespTelefone():JspHelper.verificaVarQryStr(request, \"COR_RESP_TELEFONE\"))%>"
                  size="32"
                  onFocus="<%=onFocusTelUm %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="telefone1"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.telefone", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="responsavel2"><hl:message key="rotulo.correspondente.responsavel.dois"/></label>
                <%String onFocusResDois = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_RESPONSAVEL_2"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorResponsavel2():JspHelper.verificaVarQryStr(request, \"COR_RESPONSAVEL_2\"))%>"
                  size="32"
                  onFocus="<%= onFocusResDois %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="responsavel2"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.responsavel", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="cargo2"><hl:message key="rotulo.correspondente.cargo.dois"/></label>
                <%String onFocusCorCarDois = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_RESP_CARGO_2"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorRespCargo2():JspHelper.verificaVarQryStr(request, \"COR_RESP_CARGO_2\"))%>"
                  size="32"
                  onFocus="onFocusCorCarDois"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="cargo2"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.cargo", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="telefone2"><hl:message key="rotulo.correspondente.telefone.dois"/></label>
                <%String onFocusTelDois = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_RESP_TELEFONE_2"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorRespTelefone2():JspHelper.verificaVarQryStr(request, \"COR_RESP_TELEFONE_2\"))%>"
                  size="32"
                  onFocus="<%= onFocusTelDois  %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="telefone2"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.telefone", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="responsavel3"><hl:message key="rotulo.correspondente.responsavel.tres"/></label>
                <%String onFocusCoreResTres = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_RESPONSAVEL_3"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorResponsavel3():JspHelper.verificaVarQryStr(request, \"COR_RESPONSAVEL_3\"))%>"
                  size="32"
                  onFocus="<%= onFocusCoreResTres %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="responsavel3"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.responsavel", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="cargo3"><hl:message key="rotulo.correspondente.cargo.tres"/></label>
                <%String onFocusCorCarTres = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_RESP_CARGO_3"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorRespCargo3():JspHelper.verificaVarQryStr(request, \"COR_RESP_CARGO_3\"))%>"
                  size="32"
                  onFocus="<%= onFocusCorCarTres %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="cargo3"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.cargo", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="telefone3"><hl:message key="rotulo.correspondente.telefone.tres"/></label>
                <%String onFocusCorTelTres = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_RESP_TELEFONE_3"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorRespTelefone3():JspHelper.verificaVarQryStr(request, \"COR_RESP_TELEFONE_3\"))%>"
                  size="32"
                  onFocus="<%= onFocusCorTelTres %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="telefone3"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.telefone", responsavel)%>"
                />
              </div>
            </div>
            <div class="legend">
              <span><hl:message key="rotulo.endereco.singular"/></span>
            </div>
            <div class="row">
              <div class="form-group col-sm-4">
                <label for="logradouro"><hl:message key="rotulo.logradouro.correspondente"/></label>
                <%String onFocusLogCor = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_LOGRADOURO"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorLogradouro():JspHelper.verificaVarQryStr(request, \"COR_LOGRADOURO\"))%>"
                  size="32"
                  onFocus="<%= onFocusLogCor %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="logradouro"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.logradouro", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-2">
                <label for="numero"><show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDT_CORRESPONDENTE_NRO)%>" ><hl:message key="rotulo.numero.correspondente"/> - </show:showfield><hl:message key="rotulo.complemento.correspondente"/></label>
                <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDT_CORRESPONDENTE_NRO)%>">
                  <%
                      String onFocusNumCor = "SetarEventoMascara(this,'#D11',true);";
                  %>
                  <hl:htmlinput name="COR_NRO"
                    type="text"
                    classe="form-control"
                    value="<%=TextHelper.forHtmlAttribute(correspondente!=null ? (java.util.Objects.toString(correspondente.getCorNro(), JspHelper.verificaVarQryStr(request, "COR_NRO"))):"")%>"
                    size="32"
                    onFocus="<%=onFocusNumCor%>"
                    others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                    di="numero"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.numero", responsavel)%>"
                  />
                </show:showfield>
              </div>
              <div class="form-group col-sm-3">
                <label for="complemento"><hl:message key="rotulo.endereco.complemento"/></label>
                <%String onFocusEndComp = "SetarEventoMascara(this,'#*40',true);"; %>
                <hl:htmlinput name="COR_COMPL"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorCompl():JspHelper.verificaVarQryStr(request, \"COR_COMPL\"))%>"
                  size="32"
                  onFocus="<%= onFocusEndComp %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="complemento"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.mensagem.endereco.complemento", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-3">
                <label for="bairro"><hl:message key="rotulo.bairro.correspondente"/></label>
                <%String onFocusBairroCor = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_BAIRRO"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorBairro():JspHelper.verificaVarQryStr(request, \"COR_BAIRRO\"))%>"
                  size="32"
                  onFocus="<%= onFocusBairroCor %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="bairro"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.bairro", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="cidade"><hl:message key="rotulo.cidade.correspondente"/></label>
                <%String onFocusCidCor = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_CIDADE"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorCidade():JspHelper.verificaVarQryStr(request, \"COR_CIDADE\"))%>"
                  size="32"
                  onFocus="<%= onFocusCidCor %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="cidade"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cidade", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="COR_UF">
                  <hl:message key="rotulo.endereco.estado" />
                </label>
                <hl:campoUFv4 name="COR_UF"
                  rotuloUf="rotulo.uf.correspondente"
                  valorCampo="<%=TextHelper.forHtmlAttribute(correspondente != null && correspondente.getCorUf() != null ? correspondente.getCorUf() : JspHelper.verificaVarQryStr(request, "COR_UF"))%>"
                  desabilitado="<%=!podeEditarCor %>"
                  classe="form-control"
                  di="COR_UF"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.uf", responsavel)%>"
                  />
              </div>
              <div class="form-group col-sm-4">
                <label for="cep"><hl:message key="rotulo.cep.correspondente"/></label>
                <%String onFocusCepCor = "SetarEventoMascara(this,'" + LocaleHelper.getCepMask() + "',true);"; %>
                <hl:htmlinput name="COR_CEP"
                  type="text"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorCep():JspHelper.verificaVarQryStr(request, \"COR_CEP\"))%>"
                  size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepSize())%>"
                  classe="form-control"
                  onFocus="<%= onFocusCepCor %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="cep"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cep", responsavel)%>"
                />
              </div>
            </div>
            <div class="legend">
              <span><hl:message key="rotulo.correspondente.contato"/></span>
            </div>
            <div class="row">
              <div class="form-group col-sm-4">
                <label for="fone"><hl:message key="rotulo.telefone.correspondente"/></label>
                <%String onFocusTelCor = "SetarEventoMascara(this,'#*30',true);"; %>
                <hl:htmlinput name="COR_TEL"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorTel():JspHelper.verificaVarQryStr(request, \"COR_TEL\"))%>"
                  size="32"
                  onFocus="<%= onFocusTelCor %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="fone"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.telefone", responsavel)%>"
                />
              </div>
              <div class="form-group col-sm-4">
                <label for="fax"><hl:message key="rotulo.fax.correspondente"/></label>
                <%String onFocusFaxCor = "SetarEventoMascara(this,'" + LocaleHelper.getMultiplosTelefonesMask() + "',true);"; %>
                <hl:htmlinput name="COR_FAX"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorFax():JspHelper.verificaVarQryStr(request, \"COR_FAX\"))%>"
                  size="32"
                  onFocus="<%= onFocusFaxCor %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  di="fax"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.fax", responsavel)%>"
                />
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="COR_EMAIL"><hl:message key="rotulo.email.correspondente"/></label>
                <%String onFocusEmailCor = "SetarEventoMascara(this,'#*100',true);"; %>
                <hl:htmlinput name="COR_EMAIL"
                  type="textarea"
                  rows="3" 
                  cols="67"
                  di="COR_EMAIL"
                  onBlur="formataEmails('COR_EMAIL');"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(correspondente != null ? correspondente.getCorEmail():JspHelper.verificaVarQryStr(request, \"COR_EMAIL\"))%>"
                  size="69"
                  onFocus="<%= onFocusEmailCor %>"
                  others="<%=TextHelper.forHtmlAttribute(!podeEditarCor ? "disabled" : "")%>"
                  nf="submit"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email", responsavel)%>"
                />
              </div>
            </div>
            <% if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_IP, CodedValues.TPC_NAO, responsavel)) { %>
              <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
                <jsp:param name="tipo_endereco" value="numero_ip"/>
                <jsp:param name="nome_campo" value="novoIp"/>
                <jsp:param name="nome_lista" value="listaIps"/>
                <jsp:param name="lista_resultado" value="cor_ip_acesso"/>
                <jsp:param name="label" value="rotulo.usuario.ips.acesso"/>
                <jsp:param name="mascara" value="#I30"/>
                <jsp:param name="pode_editar" value="<%=(boolean)podeEditarEnderecoAcesso%>"/>              
                <jsp:param name="bloquear_ip_interno" value="<%=(boolean)!permiteCadIpInternoCsaCor%>"/>
                <jsp:param name="placeHolder" value="mensagem.placeholder.digite.ip.acesso" />              
              </jsp:include>
            <% } %>
            <% if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_DNS, CodedValues.TPC_NAO, responsavel)) { %>
              <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
                <jsp:param name="tipo_endereco" value="url"/>
                <jsp:param name="nome_campo" value="novoDDNS"/>
                <jsp:param name="nome_lista" value="listaDDNSs"/>
                <jsp:param name="lista_resultado" value="cor_ddns_acesso"/>
                <jsp:param name="label" value="rotulo.usuario.enderecos.acesso"/>
                <jsp:param name="mascara" value="#*100"/>
                <jsp:param name="pode_editar" value="<%=(boolean)podeEditarEnderecoAcesso%>"/>      
                <jsp:param name="placeHolder" value="mensagem.placeholder.digite.endereco.acesso" />
              </jsp:include>
            <% } %>  
            <% if (true) { 
              
              if (true) {
                  String exigeEnderecoOld = "";
                  boolean exigeEndereco = false;
                  if (correspondente != null && correspondente.getCorExigeEnderecoAcesso() != null && !correspondente.getCorExigeEnderecoAcesso().equals("")) {
                      exigeEndereco = correspondente.getCorExigeEnderecoAcesso().equals(CodedValues.TPC_SIM);
                      exigeEnderecoOld = correspondente.getCorExigeEnderecoAcesso().toString();
                  } else {
                      exigeEndereco = (paramVrfIpAcesso != null && paramVrfIpAcesso.equals("S")) ? true : false;
                  } 
            %>
                <div class="row">
                  <div class="col-sm-6 col-md-12">
                    <span id="descricao"><hl:message key="rotulo.verifica.cadastro.ip.correspondente"/></span>
                    <div class="form-group mb-1" role="radiogroup" aria-labelledby="VerificaCadastroEnderecoAcessoLogin">
                      <div class="form-check form-check-inline pt-3">
                        <input id="IANFSim" class="form-check-input ml-1" name="cor_exige_endereco_acesso" type="radio" value="N" <%=(String)(!exigeEndereco ? "checked" : "")%> <%=(String)(!podeEditarCorBackup ? "disabled" : "")%> >
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="IANFSim"><hl:message key="rotulo.sim"/></label>
                      </div>
                      <div class="form-check form-check-inline">
                        <input id="IANFNão" class="form-check-input ml-1" name="cor_exige_endereco_acesso" type="radio" value="N" <%=(String)(!exigeEndereco ? "checked" : "")%> <%=(String)(!podeEditarCorBackup ? "disabled" : "")%> >
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="IANFNão"><hl:message key="rotulo.nao"/></label>
                        <input type="hidden" name="cor_exige_endereco_acesso_old" value="<%=TextHelper.forHtmlAttribute(exigeEnderecoOld )%>">
                      </div>
                    </div>
                  </div>
                </div>
            <% } %>      
          <% } %>    
          </div>
        </div>
        <%if (cor_codigo != null) {%>
          <hl:htmlinput name="COR_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(cor_codigo)%>"/>
          <hl:htmlinput name="cor" type="hidden" value="<%=TextHelper.forHtmlAttribute(cor_codigo)%>"/>
          <input type="hidden" id="ip_list" name="ip_list" value="<%=TextHelper.forHtmlAttribute(cor_ip_acesso )%>">
          <input type="hidden" id="ddns_list" name="ddns_list" value="<%=TextHelper.forHtmlAttribute(cor_ddns_acesso )%>">    
        <%}if (!TextHelper.isNull(JspHelper.verificaVarQryStr (request, "ECO_CNPJ")) && !TextHelper.isNull(ecoCodigo)) {%>
            <hl:htmlinput name="ECO_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(ecoCodigo)%>"/>
            <hl:htmlinput name="ECO_CNPJ" type="hidden" value="<%=TextHelper.forHtmlAttribute(ecoCnpj)%>"/>
        <% } %>
        <hl:htmlinput name="MM_update" type="hidden"  value="form1" />
        <%String btnCancelar = "";
        if (!responsavel.isCor()) {        
            btnCancelar = "postData('" + TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)) + "'); return false;";
        } else {
            btnCancelar = "postData('../v3/carregarPrincipal'); return false;";
        } %>
        <div class="btn-action">
          <% if (podeEditarCorBackup || podeEditarEnderecoAcesso) {%>
            <a class="btn btn-outline-danger" href="#no-back" onClick="<%=(String)btnCancelar%>"><hl:message key="rotulo.botao.cancelar" /></a>
            <a class="btn btn-primary" href="#no-back" onClick="montaListaIps('cor_ip_acesso','listaIps'); montaListaIps('cor_ddns_acesso','listaDDNSs'); habilitaCampos(); return false;"><hl:message key="rotulo.botao.salvar" /></a>
          <%} else {%>
            <a class="btn btn-outline-danger" href="#no-back" onClick="<%=(String)btnCancelar%>"><hl:message key="rotulo.botao.voltar" /></a>  
          <%}%>
        </div>
      </form>
    </div>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/listagem.js"></script>
<script type="text/JavaScript">
 var f0 = document.forms[0];
</script>
<script type="text/JavaScript">
function formLoad() {
  focusFirstField();
}
function habilitaCampos() {
  if (vf_cadastro_cor()) {
    enableAll();
    f0.submit();
  }
}
//field pode ser ID ou NAME
function formataEmails(field) {
    
    var emailsTag = document.getElementById(field);        
    
    if(!!emailsTag)
    {                      
        console.log(emailsTag);
        document.getElementById(field).value = replaceCaracteresInvalidos(emailsTag.value.trim());        
    }
    else
    {
        var emails = document.getElementsByName(field)[0].value;    
        if(!!emails)
        {                   
            emails = replaceCaracteresInvalidos(emails.trim());
            document.getElementsByName(field)[0].value = emails;            
        }               
    }
}

function replaceCaracteresInvalidos(emailString){
    var emails = emailString;   
    var separador = ',';
    
    emails = replaceAll(emails,' ', separador);
    emails = replaceAll(emails,';', separador);
    emails = replaceAll(emails,'\n',separador); 
    emails = replaceAll(emails,'\t',separador);
    
    while(emails.indexOf(",,") !== -1){
        emails = replaceAll(emails,",,",separador);
    }
    
    if(emails[0] === separador){    // Se o primeiro caracter é vírgula ',' então
        emails = emails.slice(1);   // remove o primeiro caracter.
    }
    
    if(emails.slice(-1) === separador){ // Se o último caracter é vírgula ',' então
        emails = emails.slice(0,-1);    // remove o último caracter.
    }
    
    return emails;
}

function replaceAll(str, find, replace) {       
    return str.replace(new RegExp(find, 'g'), replace);
}

window.onload = formLoad;
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>