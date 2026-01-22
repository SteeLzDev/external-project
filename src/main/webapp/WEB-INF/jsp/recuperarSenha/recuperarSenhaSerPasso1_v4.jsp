<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
// Responsável é o usuário do sistema 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

// Verifica parâmetro que indica a forma do login de usuário servidor
boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);

boolean autoDesbloqueio = (boolean) request.getAttribute("autodesbloqueio");
boolean recuperacaoSenhaServidorComCpf = (boolean) request.getAttribute("recuperacaoSenhaServidorComCpf");
boolean omiteCpf = ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) && !recuperacaoSenhaServidorComCpf;
boolean recuperaSenhaEmail = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_EMAIL, responsavel);
boolean recuperaSenhaSMS = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_SMS, responsavel);
boolean recuperaSenhaSMSEmail = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_EMAIL_SMS, responsavel);
boolean loginComCfp = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_CPF, CodedValues.TPC_SIM, responsavel);

String action = (autoDesbloqueio ? "../v3/autoDesbloquearServidor" : "../v3/recuperarSenhaServidor")
              + "?acao=concluirServidor"
              ;

// Define se o campo username é CPF ou matrícula de acordo com o parâmetro de sistema TPC_LOGIN_USU_SERVIDOR_COM_CPF
String serCpf = loginComCfp ? JspHelper.verificaVarQryStr(request, "username") : "";
String rseMatricula = loginComCfp ? "" : JspHelper.verificaVarQryStr(request, "username");

String ajudaCampoCaptcha = ApplicationResourcesHelper.getMessage("ajuda.campo.captcha", responsavel);

// Seleciona a entidade Órgão ou Estabelecimento de acordo com o login do servidor
String campoLabel = null;
String campoValor = null;

List<TransferObject> entidades = null;
if(!recuperacaoSenhaServidorComCpf) {
  if (loginComEstOrg) {
      entidades = (List<TransferObject>) request.getAttribute("lstOrgao");;
      campoLabel = Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR;
      campoValor = Columns.ORG_CODIGO;
  } else {
      entidades = (List<TransferObject>) request.getAttribute("lstEstabelecimento");;
      campoLabel = Columns.EST_NOME;
      campoValor = Columns.EST_CODIGO;
  }
}
%>
<c:set var="bodyContent">
<% if(autoDesbloqueio){%>
	<div class="modal fade" id="staticBackdrop" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="staticBackdropLabel"><hl:message key="rotulo.auto.desbloqueio.servidor.modal.dados.titulo"/></h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      </div>
	      <div class="modal-body" id="modalConfirmacao"></div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" onClick="solicitarAtualizacaoDados()"><hl:message key="rotulo.auto.desbloqueio.servidor.modal.dados.incorretos"/></button>
	        <button type="button" class="btn btn-primary" onClick="if(verificaForm()){cleanFields();} return false;"><hl:message key="rotulo.auto.desbloqueio.servidor.modal.dados.corretos"/></button>
	      </div>
	    </div>
	  </div>
	</div>
<%}%>
  <form name="form1" method="post" action="<%=action%>" autocomplete="off">
  <% if (autoDesbloqueio) {%>
  	<input id="dadosIncorretos" name="dadosIncorretos" type="hidden">    
  <% } %>
    <div class="alert alert-warning" role="alert">
      <p><% if (autoDesbloqueio) {%>
      <hl:message key="rotulo.auto.desbloqueio.servidor.titulo"/>
      <% } else {%>
      <hl:message key="rotulo.recuperar.senha.servidor.titulo"/>
      <% } %></p>
      <hl:message key="rotulo.recuperar.senha.servidor.subtitulo"/>
      <a href="#no-back" id="linkCaptchaNormal" onclick="alteraCaptcha()">
          <hl:message key="rotulo.mensagem.altera.captcha.audio"/>
      </a>
      <a href="#no-back" id="linkCaptchaAuditivo" class='d-none' onclick="alteraCaptcha()">
          <hl:message key="rotulo.mensagem.altera.captcha.simples"/>
      </a>
    </div>
    <%if (!recuperacaoSenhaServidorComCpf) {%>
      <div class="form-group">
        <label for="codigoOrgao"><hl:message key="rotulo.orgao.singular"/></label> 
        <%=JspHelper.geraCombo(entidades, "codigoOrgao", campoValor, campoLabel, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\"", true)%>
      </div>
      <div class="form-group">
        <label for="matricula"><hl:message key="rotulo.servidor.matricula"/></label> 
        <input class="form-control" id="matricula" name="matricula" type="text" onFocus="SetarEventoMascara(this,'<%=TextHelper.forJavaScript(LoginHelper.getMascaraMatriculaServidor())%>',true);" onBlur="fout(this);ValidaMascara(this);" value="<%=TextHelper.forHtmlAttribute(rseMatricula)%>">
        <span><hl:message key="mensagem.pagina.login.ser.matricula"/></span>
      </div>
    <% } %>
    <% if (omiteCpf) { %>
      <% if (recuperaSenhaEmail || recuperaSenhaSMSEmail) { %>
        <div class="form-group">
          <label for="usuEmail"><hl:message key="rotulo.usuario.recuperar.senha.email"/></label> 
          <hl:htmlinput type="text" classe="form-control" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" di="usuEmail" name="usuEmail" value="" size="32"/>
        </div>
      <% } if(autoDesbloqueio && (recuperaSenhaSMS || recuperaSenhaSMSEmail)) { %>
        <div class="row">
          <div class="form-group col-sm-3">
            <label for="serDddCelular"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
            <hl:htmlinput name="serDddCelular"
                          di="serDddCelular"
                          type="text"
                          classe="form-control"                    
                          value=""
                          size="2"
                          placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel) %>"
                          mask="<%=LocaleHelper.getDDDCelularMask()%>"
            />
          </div>
          <div class="form-group col-sm-9">
            <label for="serCelular"><hl:message key="rotulo.servidor.celular"/></label>
            <hl:htmlinput name="serCelular"
                          di="serCelular"
                          type="text"
                          classe="form-control"                    
                          value=""
                          size="9"
                          placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.celular", responsavel) %>"
                          mask="<%=LocaleHelper.getCelularMask()%>"
            />
          </div>
        </div>
      <% } %>
    <% } else { %>
    <div class="form-group">
      <label for="usuCpf"><hl:message key="rotulo.servidor.cpf"/></label> 
      <hl:htmlinput type="text" classe="form-control" di="usuCpf" name="usuCpf" value="<%=TextHelper.forHtmlAttribute(serCpf)%>" mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask())%>"  />
    </div>
    <% } %>
    <div class="row justify-content-between" id="captchaNormal">
      <div class="form-group col-sm-5">
        <label for="captcha"><hl:message key="rotulo.captcha.codigo"/></label> 
        <input class="form-control" name="captcha" id="captcha" type="text" placeholder="<%=TextHelper.forHtmlAttribute(ajudaCampoCaptcha)%>" type="text">
      </div>
      <div class="form-group col-sm-6 mr-3">
        <div class="captcha">
          <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt="<hl:message key="rotulo.captcha.codigo"/>" title="<hl:message key="rotulo.captcha.codigo"/>" height="50" width="200"/>
          <div class="float-end">
            <a href="javascript:void(0);" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt="<hl:message key="mensagem.gerar.novo.captcha.clique.aqui"/>" title="<hl:message key="mensagem.gerar.novo.captcha.clique.aqui"/>" border="0"/></a>
            <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
              data-bs-content='<hl:message key="mensagem.ajuda.captcha.usuario.v3"/>'
              data-original-title=<hl:message key="rotulo.ajuda" />> 
              <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
            </a>
          </div>
        </div>
      </div>
    </div>
    <div class="row d-none" id="captchaAuditivo">
	    <div class="form-group col-sm-6">
	      <label for="captchaAudio"><hl:message key="rotulo.captcha.codigo"/></label>
	      <input type="text" class="form-control" id="captchaAudio" name="captchaAudio" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
	      <div class="mt-3" id="divCaptchaSound"></div>
	      <a class="ml-2" href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
	      <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
	    </div>
    </div>
    
  </form>
  <div class="row justify-content-end">
    <div class="col-12 col-sm-8">
      <div class="clearfix text-right">
        <button class="btn btn-outline-danger mr-2" aria-label="<hl:message key="rotulo.botao.voltar"/>" onClick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>'); return false;">
          <hl:message key="rotulo.botao.voltar"/>
        </button>
        <% if(autoDesbloqueio){%>
        	<button class="btn btn-primary" type="submit" onClick="if(verificaForm()){confirmaDados();} return false;">
        <% } else {%>
        	<button class="btn btn-primary" type="submit" onClick="if(verificaForm()){cleanFields();} return false;">
        <% }%>
          <svg width="17"> 
            <use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
          <hl:message key="rotulo.botao.concluir"/>
        </button>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/passwordmeter_3010.js?<hl:message key="release.tag"/>"></script>
  <script src="<c:url value='/js/validalogin.js'/>?<hl:message key='release.tag'/>"></script>
  <script type="text/JavaScript">
    f0 = document.forms[0];
    
    function formLoad() {
        focusFirstField();
        montaCaptchaSom();
    }
    
<% if(autoDesbloqueio){%>
    function confirmaDados(){
    	var usuCpf;
    	var usuEmail;
    	var codigoOrgao;
    	var captcha;
    	var matricula;
    	var serDddCelular;
    	var serCelular;
    	
    	if (f0.usuCpf != undefined && f0.usuCpf.value != "") {
    		usuCpf = document.getElementById("usuCpf").value;
    	}
    	if (f0.usuEmail != undefined && f0.usuEmail.value != "") {
    		usuEmail = document.getElementById("usuEmail").value;
    	}
    	if (f0.codigoOrgao != undefined && f0.codigoOrgao.value != "") {
    		codigoOrgao = document.getElementById("codigoOrgao").value;
    	}
    	if (f0.serDddCelular != undefined && f0.serDddCelular.value != "") {
    		serDddCelular = document.getElementById("serDddCelular").value;
    	}
    	if (f0.serCelular != undefined && f0.serCelular.value != "") {
    		serCelular = document.getElementById("serCelular").value;
    	}
    	if (f0.captcha != undefined && f0.captcha.value != "") {
    		captcha = document.getElementById("captcha").value;
    	}
    	if (f0.matricula != undefined && f0.matricula.value != "") {
    		matricula = document.getElementById("matricula").value;
    	}
    	
  		$.ajax({
              type: 'post',
              url: '../v3/autoDesbloquearServidor?acao=concluirServidorAjax&confirmaDados=S&_skip_history_=true',
              data: {
	              'codigoOrgao': codigoOrgao,
	              'usuEmail': usuEmail,
	              'usuCpf': usuCpf,
	              'captcha': captcha,
	              'matricula': matricula,
	              'serCelular': serCelular,
	              'serDddCelular': serDddCelular,
	          	},
              async: true,
              success: function (data) {
	           	  if(data.includes("_v4")){
	           		  f0.submit();
	           		  return;
	           	  }
              	$("#modalConfirmacao").html(data);
              	$("#staticBackdrop").modal('show')
              },
              error: function (response) {
                  console.log(response.statusText);
              }
          });
    }
    
    function solicitarAtualizacaoDados(){
    	$("#dadosIncorretos").val(true);
    	f0.submit();
    }
<% }%>

		var captchaAuditivo = false;

    function verificaForm() {
        <% if(!recuperacaoSenhaServidorComCpf) { %>
          if (f0.matricula.value == "") {
              alert('<hl:message key="mensagem.informe.servidor.matricula"/>');
              f0.matricula.focus();
              return false;
          } 
        <% } %>
        if (!captchaAuditivo && (f0.captcha != undefined) && (f0.captcha.value == "")) {
            alert('<hl:message key="mensagem.informe.captcha.codigo"/>');
            f0.captcha.focus();
            return false;
        }
        if (captchaAuditivo && (f0.captchaAudio != undefined) && (f0.captchaAudio.value == "")) {
            alert('<hl:message key="mensagem.informe.captcha.codigo"/>');
            f0.captchaAudio.focus();
            return false;
        }
        if (f0.usuCpf != undefined && f0.usuCpf.value == "") {
            alert('<hl:message key="mensagem.informe.servidor.cpf"/>');
            f0.usuCpf.focus();
            return false;
        } else if (f0.usuCpf != undefined && !CPF_OK(extraiNumCNPJCPF(f0.usuCpf.value))) {
            f0.usuCpf.focus();
            return false;
        }
        
        <% if(!recuperaSenhaSMSEmail) { %>
        if (f0.usuEmail != undefined && f0.usuEmail.value == "") {
            alert('<hl:message key="mensagem.informe.servidor.email"/>');
            f0.usuEmail.focus();
            return false;
        }  
        <% } %>
        return true;
    }

    function cleanFields() {
        if (f0.matricula && f0.matricula.type == "text") {
            f0.matricula.type = "hidden";
        }
        if (f0.usuCpf && f0.usuCpf.type == "text") {
            f0.usuCpf.type = "hidden";
        }
        if (f0.captcha && f0.captcha.type == "text") {
            f0.captcha.type = "hidden";
        }
        if (f0.captchaAudio && f0.captchaAudio.type == "text") {
            f0.captchaAudio.type = "hidden";
        }
        if (f0.usuEmail && f0.usuEmail.type == "text") {
            f0.usuEmail.type = "hidden";
        }
        f0.submit();
    }
    
    function alteraCaptcha() {
    	if (!captchaAuditivo) {
    		document.getElementById('linkCaptchaNormal').classList.add('d-none');
    		document.getElementById('captchaNormal').classList.add('d-none');
    		document.getElementById('linkCaptchaAuditivo').classList.remove('d-none');
    		document.getElementById('captchaAuditivo').classList.remove('d-none');
    		captchaAuditivo = true
    	} else {
    		document.getElementById('linkCaptchaAuditivo').classList.add('d-none');
    		document.getElementById('captchaAuditivo').classList.add('d-none');
    		document.getElementById('linkCaptchaNormal').classList.remove('d-none');
    		document.getElementById('captchaNormal').classList.remove('d-none');
    		captchaAuditivo = false
    	}
    }
    
    window.onload = formLoad;
  </script>
</c:set>
<t:empty_v4>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>