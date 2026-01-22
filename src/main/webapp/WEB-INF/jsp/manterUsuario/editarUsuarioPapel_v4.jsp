<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.sql.Date"%>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.CertificadoDigital"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditarUsu = (boolean) request.getAttribute("podeEditarUsu");
boolean podeConsultarUsu = (boolean) request.getAttribute("podeConsultarUsu");
boolean podeEditarBloqFunUsu = (boolean) request.getAttribute("podeEditarBloqFunUsu");
boolean podeCriarUsu = (boolean) request.getAttribute("podeCriarUsu");
boolean cpfObrigatorio = (boolean) request.getAttribute("cpfObrigatorio");
boolean telObrigatorio = (boolean) request.getAttribute("telObrigatorio");
boolean emailObrigatorio = (boolean) request.getAttribute("emailObrigatorio");

// Exige tipo de motivo da operacao
boolean exigeMotivoOperacaoUsu = (boolean) request.getAttribute("exigeMotivoOperacaoUsu");
boolean exibeMotivoOperacao = (boolean) request.getAttribute("exibeMotivoOperacao");

boolean podeEditarUsuDeficienteVisual = (boolean) request.getAttribute("podeEditarUsuDeficienteVisual");

boolean bloqueiaEdicaoEmail = (boolean) request.getAttribute("bloqueiaEdicaoEmail");

String codigo = (String) request.getAttribute("codigo");
String titulo = (String) request.getAttribute("titulo");
String usu_codigo = (String) request.getAttribute("usu_codigo");

boolean podeEdtRestAcessoFun = (boolean) request.getAttribute("podeEdtRestAcessoFun");  

// Não valida CPF de usuário de suporte em sistema que não seja no Brasil
boolean validarCpfSuporte = (boolean) request.getAttribute("validarCpfSuporte");

String arr_p = (String) request.getAttribute("arr_p");

List usuFunCodigos = (List) request.getAttribute("usuFunCodigos");
List funcoes = (List) request.getAttribute("funcoes");
String arrPerfis = (String) request.getAttribute("arrPerfis");
String arrFuncoes = (String) request.getAttribute("arrFuncoes");
UsuarioTransferObject usuario = null;
List perfis = (List) request.getAttribute("perfis");

String stu_codigo = (String) request.getAttribute("stu_codigo");
String usu_dica_senha = (String) request.getAttribute("usu_dica_senha");
usu_dica_senha.replaceAll("\"", "&quot;");
String usu_email = (String) request.getAttribute("usu_email");
usu_email.replaceAll("\"", "&quot;");
String usu_tel = (String) request.getAttribute("usu_tel");
usu_tel.replaceAll("\"", "&quot;");
usu_tel.replaceAll("\"", "&quot;");
String usu_login = (String) request.getAttribute("usu_login");
usu_login.replaceAll("\"", "&quot;");
String usu_nome = (String) request.getAttribute("usu_nome");
usu_nome.replaceAll("\"", "&quot;");
String usu_ip_acesso = (String) request.getAttribute("usu_ip_acesso");
String usu_ddns_acesso = (String) request.getAttribute("usu_ddns_acesso");
String usu_cpf = (String) request.getAttribute("usu_cpf");
String usu_centralizador = (String) request.getAttribute("usu_centralizador");
String usu_autentica_sso = (String) request.getAttribute("usu_autentica_sso");
String usu_visivel = (String) request.getAttribute("usu_visivel");
String usu_exige_certificado = (String) request.getAttribute("usu_exige_certificado");
String usu_matricula_inst = (String) request.getAttribute("usu_matricula_inst");
String usu_deficiente_visual = (String) request.getAttribute("usu_deficiente_visual");
String perfil = (String) request.getAttribute("perfil");
String usu_permite_validacao_totp = (String) request.getAttribute("usu_permite_validacao_totp");
Date usu_data_fim_vig = (Date) request.getAttribute("usu_data_fim_vig");
String usuQtdConsultasMargem = request.getAttribute("usuQtdConsultasMargem") != null ? ((Integer) request.getAttribute("usuQtdConsultasMargem")).toString() : null;
Boolean exibeQtdConsultasMargem = request.getAttribute("exibeQtdConsultasMargem") != null ? (Boolean) request.getAttribute("exibeQtdConsultasMargem") : false;

String icnBloquearFuncoes = "desbloqueado.gif";

boolean inserirUsuario = (boolean) request.getAttribute("inserirUsuario");
boolean readOnly = (boolean) request.getAttribute("readOnly");
boolean podeEditarUsuExigeCertificado = (boolean) request.getAttribute("podeEditarUsuExigeCertificado");

//Exibe Botao Rodapé
boolean exibeBotaoRodape = (boolean) (request.getAttribute("exibeBotaoRodape"));

// Se for edição de usuário csa/cor, verifica se parâmetro de sistema permite cadastro de ip interno
boolean permiteIpInterno = (boolean) request.getAttribute("permiteIpInterno");
boolean podeEditarValidacaoTotp = (boolean) request.getAttribute("podeEditarValidacaoTotp");

String msgAlertaCriacaoUsuarioGestor = (String) request.getAttribute("msgAlertaCriacaoUsuarioGestor");
String tituloPagina = (String) request.getAttribute("tituloPagina");

List inicio_grupo = new ArrayList();

boolean exibeCampoUsuAutenticaSso = (boolean) request.getAttribute("exibeCampoUsuAutenticaSso");
%>
<c:set var="title">
  ${tituloPagina}
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<% if (!inserirUsuario && (podeConsultarUsu || podeEditarBloqFunUsu || podeEdtRestAcessoFun) && !stu_codigo.equals(CodedValues.STU_EXCLUIDO)) { %>
<div class="row">
  <div class="col-sm mb-2">
    <div class="float-end">
      <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.mais.acoes"/></button>
      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
      <% if (!inserirUsuario && (podeConsultarUsu || podeEditarBloqFunUsu) && !stu_codigo.equals(CodedValues.STU_EXCLUIDO)) { %>
        <a class="dropdown-item" href="#no-back" onclick="EditarBloqueioUsuarioFunSvc('<%=TextHelper.forJavaScript(usu_codigo)%>', '<%=TextHelper.forJavaScript(codigo)%>');"><hl:message key="rotulo.botao.editar.funcoes"/></a>
      <% } 
      if (!inserirUsuario && podeEdtRestAcessoFun && !stu_codigo.equals(CodedValues.STU_EXCLUIDO)) {%>
        <a class="dropdown-item" href="#no-back" onclick="postData('../v3/manterRestricaoAcessoUsuario?acao=listar&usucodigo=<%=TextHelper.forUriComponent(usu_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>');"><hl:message key="rotulo.botao.editar.restricao.acesso"/></a>
      <% } %>        
      </div>
    </div>
  </div>
</div>
<% } %> 
<form action="${linkAction}&<%=SynchronizerToken.generateToken4URL(request)%>"  method="POST" name="form1">
  <div class="row">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.usuario.titulo.dados.usuario"/></h2>
      </div>
      <div class="card-body">
          <input type="hidden" id="ip_list" name="ip_list" value="<%=TextHelper.forHtmlAttribute(usu_ip_acesso )%>">
          <input type="hidden" id="ddns_list" name="ddns_list" value="<%=TextHelper.forHtmlAttribute(usu_ddns_acesso )%>">
          <input type="HIDDEN" name="codigo" value="<%=TextHelper.forHtmlAttribute(codigo)%>">
          <INPUT TYPE="HIDDEN" NAME="titulo" VALUE="<%=TextHelper.encode64(titulo)%>">
          <INPUT TYPE="HIDDEN" NAME="USU_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(usu_codigo)%>">
          <INPUT TYPE="HIDDEN" NAME="USU_EXIGE_CERTIFICADO_OLD" VALUE="<%=TextHelper.forHtmlAttribute(usu_exige_certificado)%>">
          <input type="hidden" name="stu_codigo" value="<%=TextHelper.forHtmlAttribute((stu_codigo.equals("")?"1":stu_codigo))%>">
          <% if (!TextHelper.isNull(perfil)) {%>
            <%-- necessário quando usuário está sendo tirado de um perfil para personalizado --%>
            <INPUT TYPE="HIDDEN" NAME="perCodigoOld" VALUE="<%=TextHelper.forHtmlAttribute(perfil)%>">
          <% } %>
          <fieldset>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="iNome"><hl:message key="rotulo.usuario.nome"/></label>
                <input type="text" class="form-control" id="usuNome" name="USU_NOME" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nome", responsavel)%>" onFocus="SetarEventoMascaraV4(this,'#*50',true);" onBlur="fout(this);ValidaMascaraV4(this);" value="<%=TextHelper.forHtmlAttribute(usu_nome)%>" size="32" <%if(readOnly){%> disabled <%}%> ><%=JspHelper.verificaCampoNulo(request, "USU_NOME")%>
              </div>
              <div class="form-group col-sm-6">
                <label for="iUsuario"><hl:message key="rotulo.usuario.singular"/></label>
                <input type="text" class="form-control" id="usuLogin" name="USU_LOGIN" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.usuario", responsavel)%>" onFocus="SetarEventoMascaraV4(this,'#L32',true);" onBlur="fout(this);ValidaMascaraV4(this);" value="<%=TextHelper.forHtmlAttribute(usu_login)%>" size="32" <%if(readOnly){%> disabled <%}%> ><%=JspHelper.verificaCampoNulo(request, "USU_LOGIN")%>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="iEmail"><hl:message key="rotulo.usuario.email"/></label>
                <input type="text" class="form-control" id="usuEmail" name="USU_EMAIL" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email", responsavel)%>" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" value="<%=TextHelper.forHtmlAttribute(usu_email)%>" size="32" <%if(readOnly || (bloqueiaEdicaoEmail && !TextHelper.isNull(usu_email) && !inserirUsuario)){%> disabled <%}%> >
              </div>
              <% if (!inserirUsuario) { %>
              <div class="form-group col-sm-6">
                <label for="iDicaSenha"><hl:message key="rotulo.usuario.dica.senha"/></label>
                <input type="text" class="form-control" id="iDicaSenha" name="USU_DICA_SENHA" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.dica.senha", responsavel)%>" onFocus="SetarEventoMascaraV4(this,'#*120',true);" onBlur="fout(this);ValidaMascaraV4(this);" value="<%=TextHelper.forHtmlAttribute(usu_dica_senha)%>" size="32" <%if(readOnly){%> disabled <%}%> >
              </div>
              <% } %>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="iCPF"><hl:message key="rotulo.usuario.cpf"/></label>
                <hl:htmlinput type="text"
                              mask="<%=validarCpfSuporte ? TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask()) : "#*19"%>"
                              name="USU_CPF"
                              di="usuCpf"
                              value="<%=TextHelper.forHtmlAttribute(usu_cpf)%>"
                              size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfSize())%>"
                              classe="form-control"
                              placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.cpf", responsavel)%>"
                              others="<%=TextHelper.forHtmlAttribute((readOnly) ? "disabled" : "")%>"
                />
              </div>
              <div class="form-group col-sm-6">
                <label for="iTelefone"><hl:message key="rotulo.usuario.telefone"/></label>
                <input type="text" class="form-control" id="usuTel" name="USU_TEL" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.telefone", responsavel)%>" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" value="<%=TextHelper.forHtmlAttribute(usu_tel)%>" size="32" <%if(readOnly){%> disabled <%}%> >
              </div>
            </div>
            <div class="legend"></div>
            <% if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_IP, CodedValues.TPC_NAO, responsavel)) { %>
              <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
                <jsp:param name="tipo_endereco" value="numero_ip"/>
                <jsp:param name="nome_campo" value="novoIp"/>
                <jsp:param name="nome_lista" value="listaIps"/> 
                <jsp:param name="lista_resultado" value="usu_ip_acesso"/>
                <jsp:param name="label" value="rotulo.usuario.ips.acesso"/>
                <jsp:param name="mascara" value="#I30"/>
                <jsp:param name="placeHolder" value="mensagem.placeholder.digite.ip.acesso"/>
                <jsp:param name="pode_editar" value="<%=(boolean) !readOnly %>"/>      
                <jsp:param name="bloquear_ip_interno" value="<%=(boolean)!permiteIpInterno%>"/>
              </jsp:include>
            <% } %>
            <% if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_DNS, CodedValues.TPC_NAO, responsavel)) { %>
              <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
                <jsp:param name="tipo_endereco" value="url"/>
                <jsp:param name="nome_campo" value="novoDDNS"/>
                <jsp:param name="nome_lista" value="listaDDNSs"/>
                <jsp:param name="lista_resultado" value="usu_ddns_acesso"/>
                <jsp:param name="label" value="rotulo.usuario.enderecos.acesso"/>
                <jsp:param name="mascara" value="#*100"/>
                <jsp:param name="placeHolder" value="mensagem.placeholder.digite.endereco.acesso"/>
                <jsp:param name="pode_editar" value="<%=(boolean) !readOnly %>"/>              
              </jsp:include>
            <% } %>
			<% if (exibeCampoUsuAutenticaSso) { %> 
			<div class="row">
              <div class="col-sm-6 col-md-6 mb-2">
                <div class="form-group mb-0">
                  <span id="iUsuarioSso"><hl:message key="rotulo.usuario.autentica.sso"/></span>
                </div>
                <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="iUsuarioSso">
                  <input class="form-check-input ml-1" type="radio" name="USU_AUTENTICA_SSO" id="USU_AUTENTICA_SSO_SIM" value="S" <%=(String)( (usu_autentica_sso != null && usu_autentica_sso.equals(CodedValues.TPC_SIM) ? "checked" : "") )%>  <%if(readOnly){%> disabled <%}%>>
                  <label class="form-check-label pr-3 labelSemNegrito" for="USU_AUTENTICA_SSO_SIM">
                    <hl:message key="rotulo.sim"/>
                  </label>
                </div>
                <div class="form-check-inline form-check">
                  <input class="form-check-input ml-1" type="radio" name="USU_AUTENTICA_SSO" id="USU_AUTENTICA_SSO_NAO" value="N" <%=(String)( (usu_autentica_sso != null && usu_autentica_sso.equals(CodedValues.TPC_NAO) ? "checked" : "") )%> <%if(readOnly){%> disabled <%}%>>
                  <label class="form-check-label labelSemNegrito" for="USU_AUTENTICA_SSO_NAO">
                    <hl:message key="rotulo.nao"/>
                  </label>
                </div>
              </div>
            </div>
			<% } %> 
            <% if (responsavel.isSup()) { %> 
            <div class="row">
              <div class="col-sm-6 col-md-6 mb-2">
                <div class="form-group mb-0">
                  <span id="iUsuarioCentralizador"><hl:message key="rotulo.usuario.centralizador"/></span>
                </div>
                <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="iUsuarioCentralizador">
                  <input class="form-check-input ml-1" type="radio" name="USU_CENTRALIZADOR" id="USU_CENTRALIZADOR_SIM" value="S" <%=(String)( (usu_centralizador != null && usu_centralizador.equals(CodedValues.TPC_SIM) ? "checked" : "") )%>  <%if(readOnly){%> disabled <%}%>>
                  <label class="form-check-label pr-3 labelSemNegrito" for="USU_CENTRALIZADOR_SIM">
                    <hl:message key="rotulo.sim"/>
                  </label>
                </div>
                <div class="form-check-inline form-check">
                  <input class="form-check-input ml-1" type="radio" name="USU_CENTRALIZADOR" id="USU_CENTRALIZADOR_NAO" value="N" <%=(String)( (usu_centralizador != null && usu_centralizador.equals(CodedValues.TPC_NAO) ? "checked" : "") )%> <%if(readOnly){%> disabled <%}%>>
                  <label class="form-check-label labelSemNegrito" for="USU_CENTRALIZADOR_NAO">
                    <hl:message key="rotulo.nao"/>
                  </label>
                </div>
              </div>
            </div>			
            <div class="row">
              <div class="col-sm-6 col-md-6 mb-2">
                <div class="form-group mb-0">
                  <span id="iUsuarioVisivelOutrosPapeis"><hl:message key="rotulo.usuario.visivel.outros.papeis"/></span>
                </div>
                <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="iUsuarioVisivelOutrosPapeis">
                  <input class="form-check-input ml-1" type="radio" name="USU_VISIVEL" id="USU_VISIVEL_SIM" value="S" <%=(String)( (usu_visivel != null && usu_visivel.equals(CodedValues.TPC_SIM) ? " checked " : "") )%> <%=(String)(!podeEditarUsu ? " disabled " : "")%> >
                  <label class="form-check-label pr-3 labelSemNegrito" for="USU_VISIVEL_SIM">
                    <hl:message key="rotulo.sim"/>
                  </label>
                </div>
                <div class="form-check-inline form-check">
                  <input class="form-check-input ml-1" type="radio" name="USU_VISIVEL" id="USU_VISIVEL_NAO" value="N" <%=(String)( (usu_visivel != null && usu_visivel.equals(CodedValues.TPC_NAO) ? " checked " : "") )%> <%=(String)(!podeEditarUsu ? " disabled " : "")%> >
                  <label class="form-check-label labelSemNegrito" for="USU_VISIVEL_NAO">
                    <hl:message key="rotulo.nao"/>
                  </label>
                </div>
              </div>
            </div>
            <% } %>
            <div class="row">
              <div class="col-sm-6 col-md-6 mb-2">
                <div class="form-group mb-0">
                  <span id="iExigeCertificado"><hl:message key="rotulo.usuario.exige.certificado.digital"/></span>
                </div>
                <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="iExigeCertificado">
                  <input class="form-check-input ml-1" type="radio" name="USU_EXIGE_CERTIFICADO" id="USU_EXIGE_CERTIFICADO_SIM" value="S" <%=(String)( (usu_exige_certificado != null && usu_exige_certificado.equals(CodedValues.TPC_SIM) ? "checked" : "") )%>  <%if(!podeEditarUsuExigeCertificado || readOnly){%> disabled <%}%>>
                  <label class="form-check-label pr-3 labelSemNegrito" for="USU_EXIGE_CERTIFICADO_SIM">
                    <hl:message key="rotulo.sim"/>
                  </label>
                </div>
                <div class="form-check form-check-inline">
                  <input class="form-check-input ml-1" type="radio" name="USU_EXIGE_CERTIFICADO" id="USU_EXIGE_CERTIFICADO_NAO" value="N" <%=(String)( (usu_exige_certificado != null && usu_exige_certificado.equals(CodedValues.TPC_NAO) ? "checked" : "") )%> <%if(!podeEditarUsuExigeCertificado || readOnly){%> disabled <%}%>>
                  <label class="form-check-label labelSemNegrito" for="USU_EXIGE_CERTIFICADO_NAO">
                    <hl:message key="rotulo.nao"/>
                  </label>
                </div>
              </div>
            </div>
            <% if (CertificadoDigital.getInstance().aceitaCertificadoPelaMatriculaInst()) { %>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="iMatriculaInstitucional"><hl:message key="rotulo.usuario.matricula.institucional"/></label>
                <input type="text" class="form-control" id="usuMatriculaInst" name="USU_MATRICULA_INST" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula.institucional", responsavel)%>" size="20" maxlength="20" value="<%=TextHelper.forHtmlAttribute(usu_matricula_inst)%>" <%if(readOnly){%> disabled <%}%>  onFocus="SetarEventoMascaraV4(this,'#L20',true);" onBlur="fout(this);ValidaMascaraV4(this);">
              </div>
            </div>
            <% } %>
            <% if (responsavel.isSup() && exibeQtdConsultasMargem) { %>
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="usuQtdConsultasMargem"><hl:message key="rotulo.usuario.qtd.max.consultas.margem"/></label>
                  <input type="text" class="form-control" id="usuQtdConsultasMargem" name="usuQtdConsultasMargem" value="<%=(!TextHelper.isNull(usuQtdConsultasMargem)) ? TextHelper.forHtmlAttribute(usuQtdConsultasMargem) : ""%>" <%if(readOnly){%> disabled <%}%> placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.informacao.usuario.quantidade.max.consulta.margem", responsavel)%>" size="20" maxlength="20" value="<%=TextHelper.forHtmlAttribute(usu_matricula_inst)%>" <%if(readOnly){%> disabled <%}%>  onFocus="SetarEventoMascaraV4(this,'#D10',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                </div>
              </div>
            <% }%>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="iValidoAte"><hl:message key="rotulo.usuario.valido.ate"/></label>
                <input type="text" class="form-control" id="usuDataFimVig" name="USU_DATA_FIM_VIG" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.data.validade", responsavel)%>" size="20" maxlength="20" value="<%=TextHelper.forHtmlAttribute(!TextHelper.isNull(usu_data_fim_vig) ? DateHelper.toDateString(usu_data_fim_vig) : "")%>" <%if(readOnly){%> disabled <%}%>  onFocus="SetarEventoMascaraV4(this,'<%=LocaleHelper.getDateJavascriptPattern()%>',true);" onBlur="fout(this);ValidaMascaraV4(this);"><%=JspHelper.verificaCampoNulo(request, "USU_DATA_FIM_VIG")%>
              </div>
            </div>
            <% if (podeEditarUsuDeficienteVisual) { %>
            <div class="row">
              <div class="col-sm-6 col-md-6 mb-2">
                <div class="form-group mb-0">
                  <span id="iDeficienteVisual"><hl:message key="rotulo.usuario.deficiente.visual"/></span>
                </div>
                <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="iDeficienteVisual">
                  <input class="form-check-input ml-1" type="radio" name="USU_DEFICIENTE_VISUAL" id="USU_DEFICIENTE_VISUAL_SIM" value="S" <%=(String)( (usu_deficiente_visual != null && usu_deficiente_visual.equals(CodedValues.TPC_SIM) ? " checked " : "") )%> <%=(String)(readOnly ? " disabled " : "")%> >
                  <label class="form-check-label pr-3 labelSemNegrito" for="USU_DEFICIENTE_VISUAL_SIM">
                   <hl:message key="rotulo.sim"/>
                  </label>
                </div>
                <div class="form-check-inline form-check">
                  <input class="form-check-input ml-1" type="radio" name="USU_DEFICIENTE_VISUAL" id="USU_DEFICIENTE_VISUAL_NAO" value="N" <%=(String)( (usu_deficiente_visual != null && usu_deficiente_visual.equals(CodedValues.TPC_NAO) ? " checked " : "") )%> <%=(String)(readOnly ? " disabled " : "")%> >
                  <label class="form-check-label labelSemNegrito" for="USU_DEFICIENTE_VISUAL_NAO">
                    <hl:message key="rotulo.nao"/>
                  </label>
                </div>
              </div>
            </div>
            <% } %>
            <% if (podeEditarValidacaoTotp) { %>
            <div class="row">
              <div class="col-sm-6 col-md-6 mb-2">
                <div class="form-group mb-0" role="radiogroup" aria-labelledby="iValidacaoTotp">
                  <span id="iValidacaoTotp"><hl:message key="rotulo.usuario.permite.validacao.totp"/></span>
                </div>
                <div class="form-check form-check-inline">
                  <input class="form-check-input ml-1" type="radio" name="USU_PERMITE_VALIDACAO_TOTP" value="S" id="USU_PERMITE_VALIDACAO_TOTP_SIM" <%=(String)( (usu_permite_validacao_totp != null && usu_permite_validacao_totp.equals(CodedValues.TPC_SIM) ? " checked " : "") )%> <%=(String)(readOnly ? " disabled " : "")%> >
                  <label class="form-check-label pr-3" for="USU_PERMITE_VALIDACAO_TOTP_SIM">
                    <span class="text-nowrap align-text-top"><hl:message key="rotulo.sim"/></span>
                  </label>
              </div>
              <div class="form-check form-check-inline">
                  <input class="form-check-input ml-1" type="radio" name="USU_PERMITE_VALIDACAO_TOTP" value="N" id="USU_PERMITE_VALIDACAO_TOTP_NAO" <%=(String)( (usu_permite_validacao_totp != null && usu_permite_validacao_totp.equals(CodedValues.TPC_NAO) ? " checked " : "") )%> <%=(String)(readOnly ? " disabled " : "")%> >
                  <label class="form-check-label" for="USU_PERMITE_VALIDACAO_TOTP_NAO">
                    <span class="text-nowrap align-text-top"><hl:message key="rotulo.nao"/></span>
                  </label>
                </div>
              </div>
            </div>
            <% } %>
           
                        
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="iPerfil"><hl:message key="rotulo.perfil.singular"/></label>
                <select class="form-control form-select" id="montaFuncoesPerfil" name="perfil" onChange="MontaFuncoesPerfil()" <%if(readOnly){%> disabled <%}%>>
                <% if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERFIL_PERSONALIZADO, CodedValues.TPC_NAO, responsavel) || 
                        (perfil == null && !inserirUsuario)) { %>
                  <option value="0"><hl:message key="rotulo.usuario.perfil.personalizado"/></option>
                <% }
                Iterator it = perfis.iterator();
                
                while (it.hasNext()) {
                  CustomTransferObject next = (CustomTransferObject)it.next();
                  String per_codigo = next.getAttribute(Columns.PER_CODIGO).toString();
                  String per_descricao = next.getAttribute(Columns.PER_DESCRICAO).toString();
                %>
                  <option value="<%=TextHelper.forHtmlAttribute(per_codigo)%>" <%=(perfil != null && perfil.equals(per_codigo) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(per_descricao)%></option>
                <% } %>
                </select>
              </div>
            </div>
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
              if (!grf_codigo.equals("")) {
                %>
                <div class="row">
                  <div class="col-sm-12 col-md-12">
                    <h3 class="legend">
                      <span id="<%=TextHelper.forHtmlAttribute(grf_codigo)%>"><%=TextHelper.forHtmlContent(grf_descricao)%></span>
                    </h3>
                    <div class="form-check">
                      <div class="row" role="group" aria-labelledby="<%=TextHelper.forHtmlAttribute(grf_descricao)%>">
                   <%
                   for (int i=0; i<funcoes_grf.size(); i++) {
                     CustomTransferObject custom = (CustomTransferObject)funcoes_grf.get(i);
                     funcao = custom.getAttribute(Columns.FUN_DESCRICAO).toString();
                     fun_codigo = custom.getAttribute(Columns.FUN_CODIGO).toString();
                   %>
                        <div class="col-sm-12 col-md-6">
                          <input class="form-check-input ml-1" type="checkbox" name="funcao" id="<%=TextHelper.forHtmlAttribute(fun_codigo)%>" value="<%=TextHelper.forHtmlAttribute(fun_codigo)%>" <%=usuFunCodigos == null || usuFunCodigos.contains(fun_codigo) ? "CHECKED" : ""%> <%if(readOnly) {%> "disabled" <% }%>>
                          <label class="form-check-label" for="<%=TextHelper.forHtmlAttribute(fun_codigo)%>">
                            <span class="text-nowrap align-text-top"><%=TextHelper.forHtmlContent(funcao)%></span>
                          </label>
                        </div>
                   <% } %>
                        <div class="col-sm-12 col-md-6">
                          <input class="form-check-input ml-1" name="checkGrupo" type="checkbox" id="<%=TextHelper.forHtmlAttribute(grf_codigo) + "Todos"%>" onClick="check_uncheck_grupo(<%=TextHelper.forJavaScript((inicio_grupo.get(num_grupo)))%>,<%=(int)fim_grupo%>,<%=(int)num_grupo%>);" <%if(readOnly) {%> "disabled" <% }%> >
                          <label class="form-check-label" for="<%=TextHelper.forHtmlAttribute(grf_codigo) + "Todos"%>">
                            <span class="text-nowrap align-text-top font-weight-bold"><hl:message key="rotulo.campo.todos.simples"/></span>
                          </label>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>  
                <%
              }
              funcoes_grf.clear();  
          
              grf_codigo = (String) customs.getAttribute(Columns.FUN_GRF_CODIGO);
              int j = 0;
              if (grf_codigo.equals(CodedValues.GRUPO_FUNCAO_ADMINISTRADOR)) {
                  j++;
              }
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
          inicio_grupo.add(new Integer(fim_grupo + 1));
          %>   
          </fieldset>
      </div>
    </div>
  </div>

<% if (exibeMotivoOperacao && !readOnly) { %>
     <div class="col-sm-12">
       <div class="card">
         <div class="card-header">
           <h2 class="card-header-title"><hl:message key="rotulo.motivo.operacao.singular" /></h2>
         </div>
         <div class="card-body">
           <div class="row">
             <div class="form-group col-sm-12">
               <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.alteracao.usuario.perfil", responsavel)%>" operacaoUsuario="true" inputSizeCSS="col-sm-12"/>
             </div>                
           </div>
         </div>
       </div>
     </div>
<% } %>
  <div id="actions" class="btn-action">
    <% if (readOnly) { %>
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    <% } else { %>
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" onClick="montaListaIps('usu_ip_acesso','listaIps'); montaListaIps('usu_ddns_acesso','listaDDNSs'); vf_cadastro_usu(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    <% } %>  
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
	  <a id="page-actions" onclick="toActionBtns()">
		<svg xmlns="http://www.w3.org/2000/svg" width="145.344" height="145.344" viewBox="0 0 145.344 145.344">
		  <path id="União_1" data-name="União 1" d="M-20,59.672a72.672,72.672,0,1,1,72.671,72.672A72.671,72.671,0,0,1-20,59.672Zm10.164,0A62.508,62.508,0,1,0,52.672-2.836,62.579,62.579,0,0,0-9.836,59.672Zm82.6,40.182H24.545A12.069,12.069,0,0,1,12.49,87.8V31.544A12.069,12.069,0,0,1,24.545,19.49h44.2a4.014,4.014,0,0,1,2.841,1.177L91.678,40.757A4.019,4.019,0,0,1,92.855,43.6V87.8A12.069,12.069,0,0,1,80.8,99.854Zm0-40.182a4.018,4.018,0,0,1,4.019,4.018V91.817H80.8A4.023,4.023,0,0,0,84.818,87.8V45.263L67.081,27.526H36.6V39.58H64.727a4.019,4.019,0,0,1,0,8.037H32.581A4.018,4.018,0,0,1,28.563,43.6V27.526H24.545a4.023,4.023,0,0,0-4.018,4.019V87.8a4.023,4.023,0,0,0,4.018,4.018h4.019V63.689a4.018,4.018,0,0,1,4.018-4.018ZM36.6,91.817H68.745V67.708H36.6Z" transform="translate(20 13)"/>
		</svg>
	  </a>
	</div>
<% }%>
</c:set>
<c:set var="javascript">
<% if (exibeMotivoOperacao && !readOnly) { %>
  <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" scriptOnly="true" />
<% } %>
<script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/validaform.js"></script>
<script type="text/JavaScript" src="../js/validacoes.js"></script>
<script type="text/JavaScript" src="../js/listagem.js"></script>
<script type="text/JavaScript">
f0 = document.forms[0];
var arrPerfis = new Array(<%=(String)arrPerfis%>);
var arrFuncoes = new Array(<%=(String)arrFuncoes%>);

function imprime() {
    window.print();
}

function filtrar() {
   f0.submit();
}

function MontaFuncoes() {
  var arr_p = new Array(<%=(String)arr_p%>);
  var p = 0;
  if ((f0.perfil.options[f0.perfil.selectedIndex].value == null) ||
      (f0.perfil.options[f0.perfil.selectedIndex].value == 0)) {
    if (arr_p != null && arr_p != '') {
      uncheckAll(f0);
      for (i=0; i < f0.elements.length; i++) {
        var e = f0.elements[i];
        if ((e.type == 'check') || (e.type == 'checkbox')) {
          p = 0;
          while (p < arr_p.length) {
            if (e.value == arr_p[p]) {
              e.checked = true;
            }
            p++;
          }
        }
      }
    }
  }
}

function MontaFuncoesPerfil() {
  var f,p, ini, fim;
  f = 0;
  p = 0;
  ini = null;
  fim = 0;
  if (f0.perfil.selectedIndex >= 0) {
    if ((f0.perfil.options[f0.perfil.selectedIndex].value == null) ||
        (f0.perfil.options[f0.perfil.selectedIndex].value == 0)) {
       MontaFuncoes();
       // Habilita checks
       for (i=0; i < f0.elements.length; i++) {
          var e = f0.elements[i];
          if ((e.type == 'check') || (e.type == 'checkbox')) {
           <% if (readOnly || (((perfil == null && ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERFIL_PERSONALIZADO, CodedValues.TPC_NAO, responsavel))))){ %>
             e.disabled = true;
           <% } else { %>  
             e.disabled = false;
           <% } %>  
          }
       }
    } else {
       uncheckAll(f0);
       while (f < arrPerfis.length) {
          if (arrPerfis[f] == f0.perfil.options[f0.perfil.selectedIndex].value) {
             if (ini == null) {
                ini = f;
                fim += ini;
             }
             fim++;
          }
          f++;
       }
       for (i=0; i < f0.elements.length; i++) {
          var e = f0.elements[i];
          if ((e.type == 'check') || (e.type == 'checkbox')) {
             e.checked = false;
             p = ini;
             while ((p < fim) && !(e.checked)) {
                if (e.value == arrFuncoes[p]) {
                   e.checked = true;
                }
                p++;
             }
             e.disabled = true;
         }
       }
    }
  }
}

function checkAll(form) {
  for (i=0; i < form.elements.length; i++) {
    var e = form.elements[i];
    if ((e.type == 'check') || (e.type == 'checkbox')) {
      e.checked = true;
    }
  }
}

function uncheckAll(form) {
  for (i=0; i < form.elements.length; i++) {
    var e = form.elements[i];
    if ((e.type == 'check') || (e.type == 'checkbox')) {
      e.checked = false;
    }
  }
}

function EditarBloqueioUsuarioFunSvc(usuario, codigoEntidade) {
  f0.action = "${linkEditarUsuario}?acao=iniciarBloqueioUsuarioFuncaoServico&<%=SynchronizerToken.generateToken4URL(request)%>";
  f0.submit();
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

function formLoad() {
 <% if (!readOnly){ %>
    if (f0.USU_NOME != null){
      f0.USU_NOME.focus();
    }
 <% } %>  
  MontaFuncoesPerfil();
  inicializa_grupos();
}

// Verifica formulários de inserção e edição de usuarios
// Obrigatoriedade de campo IP (ou DDNS) verificada fora do javascript
function vf_cadastro_usu()
{
  var Controles = new Array("USU_NOME", "USU_LOGIN");
  var Msgs = new Array('<hl:message key="mensagem.informe.usu.nome"/>',
                       '<hl:message key="mensagem.informe.usu.login"/>');
  var count = 1;                     
                       
  <% if (cpfObrigatorio) { %>
     count++;
     Controles[count] = "USU_CPF";
     Msgs[count] = '<hl:message key="mensagem.informe.usu.cpf"/>';
  <% } %>
  
  <% if (telObrigatorio) { %>
  	 count++;
     Controles[count] = "USU_TEL";
     Msgs[count] = '<hl:message key="mensagem.informe.usu.telefone"/>';
  <% } %>
  
  <% if (emailObrigatorio) { %>
	 count++;
     Controles[count] = "USU_EMAIL";
     Msgs[count] = '<hl:message key="mensagem.informe.usu.email"/>';
  <% } %>
   
  <% if (exigeMotivoOperacaoUsu && exibeMotivoOperacao){ %>
      count++;
      Controles[count] = "TMO_CODIGO";
      Msgs[count] = '<hl:message key="mensagem.motivo.operacao.obrigatorio"/>';
  <% } %>
  
  if (!ValidaCamposV4(Controles, Msgs)) {
      return false;
  }
  if (<%=validarCpfSuporte%> && f0.USU_CPF.value != '' && !CPF_OK(extraiNumCNPJCPF(f0.USU_CPF.value))) {
      f0.USU_CPF.focus();
      return false;
  }

  <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERFIL_PERSONALIZADO, CodedValues.TPC_NAO, responsavel)) {%>
     if((f0.perfil.selectedIndex >= 0) && (f0.perfil.options[f0.perfil.selectedIndex].value == 0)) {
        alert('<hl:message key="mensagem.erro.usuario.perfil.personalizado.nao.permitido"/>');
        return false; 
     }
  <% } %> 

  for (i=0; i < f0.funcao.length; i++) {
    if (f0.funcao[i].value == '<%=CodedValues.FUN_USUARIO_AUDITOR%>' && f0.funcao[i].checked) {
       if (f0.USU_EMAIL.value == '') {
          alert('<hl:message key="mensagem.informe.usu.auditor.email"/>');
          f0.USU_EMAIL.focus();
          return false;
       } else {
          break;
       }
    }
  }

  if (existeFuncaoAdmSelecionada() && !(confirm('<hl:message key="mensagem.confirmacao.usuario.perfil.auditor"/>'))) {
    return false;
  }
  
  <% //mensagem de alerta para criação de usuários gestores e de órgão
     if (!TextHelper.isNull(msgAlertaCriacaoUsuarioGestor)) { %>
         var msg = '<%=msgAlertaCriacaoUsuarioGestor%>';
         msg = msg.replace('<br/>','');
         if (!(confirm(msg))) {
        	return false; 
         }
  <% } %>

  f0.submit();
}

function inicializa_grupos() {
  var check;
  <% for (int i = 0; i < inicio_grupo.size() - 1; i++) { %>
    check = true;
    for (j = <%=TextHelper.forJavaScriptBlock(inicio_grupo.get(i))%>; j <= <%=TextHelper.forJavaScriptBlock((((Integer)inicio_grupo.get(i+1)).intValue() - 1))%>; j++){
      if (f0.funcao[j] != undefined && f0.funcao[j].checked == false) {
        check = false;
      }
    }
    if (f0.checkGrupo[<%=(int)(i)%>] != undefined) {
      f0.checkGrupo[<%=(int)(i)%>].checked = check;
    }
  <% } %>
}
window.onload = formLoad;
</script>
<script>
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
	    let scrollSize = document.documentElement.scrollTop;
	    
	    if(scrollSize >= 300){
		    btnDown.classList.add('btns-active');    
	    } else {
		    btnDown.classList.remove('btns-active');
	    }
	}
	

	window.addEventListener('scroll', btnTab);
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>