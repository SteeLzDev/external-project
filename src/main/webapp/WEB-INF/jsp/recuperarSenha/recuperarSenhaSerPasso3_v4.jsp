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

boolean autoDesbloqueio = (boolean) request.getAttribute("autodesbloqueio");

String codRecuperar = JspHelper.verificaVarQryStr(request, "cod_recuperar");
if (TextHelper.isNull(codRecuperar)) {
    codRecuperar = (String) request.getAttribute("codRecuperar");    
}
if (TextHelper.isNull(codRecuperar)) {
    codRecuperar = (String) request.getAttribute("codRecuperaOtp");    
}

// Verifica parâmetro que indica a forma do login de usuário servidor
boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
boolean senhaServidorNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);
boolean recuperacaoSenhaServidorComCpf = (boolean) request.getAttribute("recuperacaoSenhaServidorComCpf");

String ajudaCampoCaptcha = ApplicationResourcesHelper.getMessage("ajuda.campo.captcha", responsavel);

Integer tamMinSenhaServidor = (Integer) request.getAttribute("tamMinSenhaServidor");
Integer tamMaxSenhaServidor = (Integer) request.getAttribute("tamMaxSenhaServidor");
Integer pwdStrengthLevel = (Integer) request.getAttribute("pwdStrengthLevel");
Integer intpwdStrength = (Integer) request.getAttribute("intpwdStrength");
String strpwdStrengthLevel = (String) request.getAttribute("strpwdStrengthLevel");
boolean ignoraSeveridade = (Boolean) request.getAttribute("ignoraSeveridade");
boolean geraOtp = (request.getAttribute("geraOtp") != null && (Boolean) request.getAttribute("geraOtp"));

String action = (autoDesbloqueio ? "../v3/autoDesbloquearServidor" : "../v3/recuperarSenhaServidor")
              + "?acao=recuperarServidor"
              + (geraOtp ? "" : "&cod_recuperar=" + TextHelper.forHtmlAttribute(codRecuperar)) 
              ;

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
  <form name="form1" method="post" action="<%=action%>" autocomplete="off">
    <div class="alert alert-warning" role="alert">
      <% if (autoDesbloqueio) { %>
        <p><hl:message key="rotulo.auto.desbloqueio.servidor.titulo"/></p>
      <% } else { %>
        <p><hl:message key="rotulo.recuperar.senha.servidor.titulo"/></p>
      <% } %>
      <p><hl:message key="rotulo.ajuda.alteracaoSenha.servidor" arg0="<%=TextHelper.forHtmlAttribute(strpwdStrengthLevel)%>" /></p>
      <p><hl:message key="rotulo.recuperar.senha.servidor.subtitulo"/></p>
    </div>
    
   <% if(geraOtp) { %>
      <div class="form-group">
        <label for="otp"><hl:message key="rotulo.otp"/></label> 
        <input class="form-control" id="otp" name="otp" type="text" onFocus="SetarEventoMascara(this,'<%=TextHelper.forJavaScript(LoginHelper.getMascaraMatriculaServidor())%>',true);" onBlur="fout(this);ValidaMascara(this);" placeholder="<hl:message key="rotulo.recuperar.senha.usuario.otp"/>" maxlength="6">
      </div>
    <%}%>
    
    <%if (!recuperacaoSenhaServidorComCpf) {%>
      <div class="form-group">
        <label for="codigoOrgao"><hl:message key="rotulo.orgao.singular"/></label> 
        <%=JspHelper.geraCombo(entidades, "codigoOrgao", campoValor, campoLabel, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\"", true)%>
      </div>
      <div class="form-group">
        <label for="matricula"><hl:message key="rotulo.servidor.matricula"/></label> 
        <input class="form-control" id="matricula" name="matricula" type="text" onFocus="SetarEventoMascara(this,'<%=TextHelper.forJavaScript(LoginHelper.getMascaraMatriculaServidor())%>',true);" onBlur="fout(this);ValidaMascara(this);">
        <span><hl:message key="mensagem.pagina.login.ser.matricula"/></span>
        <input name="score" type="hidden" id="score">
        <input name="matchlog" type="hidden" id="matchlog">
      </div>  
    <% } else {%>
    <div class="form-group">
      <label for="USU_CPF"><hl:message key="rotulo.servidor.cpf"/></label> 
      <hl:htmlinput type="text" classe="form-control" di="USU_CPF" name="USU_CPF" value="" mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask())%>"/>
      <input name="score" type="hidden" id="score">
      <input name="matchlog" type="hidden" id="matchlog">
    </div>
    <% } %>
    <div class="form-group">
      <label for="senhaNova"><hl:message key="rotulo.servidor.nova.senha"/></label> 
      <hl:htmlpassword name="senhaNova" di="senhaNova" cryptedfield="senhaNovaRSA" onFocus="setMascaraSenha(this,true); setanewOnKeyUp(this);" onBlur="fout(this);ValidaMascara(this);newOnKeyUp(this);" classe="form-control" size="20" isSenhaServidor="true"/>
    </div>
    <% if (!ignoraSeveridade) { %>
    <div class="form-group">
      <div id="divSeveridade" class="alert alert-danger divSeveridade" role="alert">
        <p class="mb-0"><hl:message key="rotulo.usuario.nivel.seguranca"/>: <span id="verdict"><hl:message key="rotulo.nivel.senha.muito.baixo"/></span></p>
      </div>
    </div>
    <% } %>
    <div class="form-group">
      <label for="senhaNovaConfirmacao"><hl:message key="rotulo.servidor.confirma.nova.senha"/></label>
      <hl:htmlpassword name="senhaNovaConfirmacao" di="senhaNovaConfirmacao" cryptedfield="senhaNovaConfirmacaoRSA" onFocus="setMascaraSenha(this,true);" onBlur="fout(this);ValidaMascara(this);" classe="form-control" size="20" isSenhaServidor="true"/>    
    </div>

    <div class="row">
      <div class="form-group col-sm-5">
        <label for="captcha"><hl:message key="rotulo.captcha.codigo"/></label> 
        <input class="form-control" name="captcha" id="captcha" type="text" placeholder="<%=TextHelper.forHtmlAttribute(ajudaCampoCaptcha)%>" type="text">
      </div>
      <div class="form-group col-sm-6">
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
  </form>
  <div class="row justify-content-end">
    <div class="btn-action mt-2 mb-0">
	  <button class="btn btn-outline-danger" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#no-back" onclick="postData('../v3/autenticarUsuario')">
	    <hl:message key="rotulo.botao.voltar"/>
	  </button>
	  <button class="btn btn-primary ml-2" type="submit" onClick="if(verificaForm()){cleanFields();} return false;">
	    <svg width="17"> 
	      <use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
	    <hl:message key="rotulo.botao.confirmar"/>
	  </button>
    </div>
  </div>
  <!--   Modal tamanha maximo da senha -->
  <div class="modal fade" id="limiteSenhaModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header pb-0">
          <span class="modal-title about-title mb-0" id="exampleModalLabel"> <hl:message key="mensagem.aviso" />
          </span>
          <button type="button" class="logout mr-1 d-print-none" onclick="limpaCampoSenha()" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <hl:message key="rotulo.mensagem.senha.limite.caracter.arg0" arg0="<%= (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel) %>" />
        </div>
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a class="btn btn-outline-danger" onclick="limpaCampoSenha()" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>"
              title="<hl:message key="rotulo.botao.cancelar"/>">
              <hl:message key="rotulo.botao.cancelar" />
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <script language="JavaScript" type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
  <script language="JavaScript" type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
  <script language="JavaScript" type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
  <script language="JavaScript" type="text/JavaScript" src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
  <script language="Javascript" type="text/JavaScript" src="../js/passwordmeter_3010.js?<hl:message key="release.tag"/>"></script>
  <script language="JavaScript" type="text/JavaScript">
    f0 = document.forms[0];
    function formLoad() {
      focusFirstField();
    }

    var primOnFocus = true;

    function newOnKeyUp(Controle) {
      <% if (!ignoraSeveridade) { %>
       testPassword(Controle.value, 'divSeveridade', <%=intpwdStrength%>);
      <% } %>
    }

    function setanewOnKeyUp(Controle) {
      <% if (!ignoraSeveridade) { %>
      if(!primOnFocus) {
        return false;
      }
      primOnFocus = false;
      var oldonkeyup = Controle.onkeyup;
      if (typeof Controle.onkeyup != 'function') {
        Controle.onkeyup = newOnKeyUp(Controle);
      } else {
        Controle.onkeyup = function() {
          if (oldonkeyup) {
            oldonkeyup(Controle);
          }
          testPassword(Controle.value, 'divSeveridade', <%=intpwdStrength%>);
        }
      } 
      <% } %>
    }    
    
    function setMascaraSenha(ctrl, AutoSkip) {
    	  SetarEventoMascara(ctrl,'<%=(String)("#" + ("*")  + (tamMaxSenhaServidor))%>', AutoSkip);
  	}

  	function limpaSenhas() {
    	  f0.senhaNova.value = '';
    	  f0.senhaNovaConfirmacao.value = '';
    	  f0.senhaNova.focus();
    	  <% if (!ignoraSeveridade) { %>
    	  document.getElementById('verdict').innerText = '<hl:message key="rotulo.nivel.senha.muito.baixo"/>';
    	  try {
    	      $('#divSeveridade').removeClass('alert-success alert-danger').addClass('alert-danger');  
    	  } catch(e) {}
    	  <% } %>
    	}    

    function verificaForm() {
      <% if(!recuperacaoSenhaServidorComCpf) { %>
        if (f0.matricula.value == "" || f0.matricula.value == null) {
          alert('<hl:message key="mensagem.informe.servidor.matricula"/>');
          f0.matricula.focus();
          return false;
        } 
      <% } %>
      if ((f0.USU_CPF != undefined) && f0.USU_CPF.value == "") {
          alert('<hl:message key="mensagem.informe.servidor.cpf"/>');
          f0.USU_CPF.focus();
          return false;
      } else if (f0.USU_CPF != undefined) {
        if (!CPF_OK(extraiNumCNPJCPF(f0.USU_CPF.value))) {
            f0.USU_CPF.focus();
            return false;
        }
      }
      if ((f0.captcha != undefined) && (f0.captcha.value == "")) {
        alert('<hl:message key="mensagem.informe.captcha.codigo"/>');
        f0.captcha.focus();
        return false;
      }
      if ((f0.senhaNova != undefined) && (f0.senhaNova != null)) {
        if ((f0.senhaNova.value != null) && (f0.senhaNovaConfirmacao.value != null) &&
          (f0.senhaNova.value != "") && (f0.senhaNovaConfirmacao.value != "")) {
          newOnKeyUp(f0.senhaNova);
          if (f0.senhaNova.value.length < <%=(int)(tamMinSenhaServidor)%>) {
            alert('<hl:message key="mensagem.erro.cadastrar.senha.servidor.minimo"/>'.replace('{0}', <%=(int)(tamMinSenhaServidor)%>));
            limpaSenhas();
            return false;
          } else if (f0.senhaNova.value.length > <%=(int)(tamMaxSenhaServidor)%>) {
            alert('<hl:message key="mensagem.erro.cadastrar.senha.servidor.maximo"/>'.replace('{0}', <%=(int)(tamMaxSenhaServidor)%>));
            limpaSenhas();
            return false;
          } else if (f0.senhaNova.value == f0.senhaNovaConfirmacao.value) {
          <% if (!ignoraSeveridade) { %>    
             if (f0.score.value < <%=(int)(pwdStrengthLevel)%>) {
               alert('<hl:message key="mensagem.erro.servidor.recuperar.senha.invalida"/>');
               limpaSenhas();
               return false;
             }
          <% } %>
          } else {
              alert('<hl:message key="mensagem.erro.servidor.recuperar.senha.diferente"/>');
              limpaSenhas();
              return false;
          }
        } else {
          alert('<hl:message key="mensagem.informe.servidor.recuperar.senha.matricula"/>');
          limpaSenhas();
          return false;
        }
        // A validação passou.
        f0.senhaNovaRSA.value = criptografaRSA(f0.senhaNova.value);
        f0.senhaNova.value = '';
        f0.senhaNovaConfirmacao.value = '';
      }
    return true;
    }

    function cleanFields() {
        if (f0.matricula && f0.matricula.type == "text") {
          f0.matricula.type = "hidden";
        }
        if (f0.captcha && f0.captcha.type == "text") {
            f0.captcha.type = "hidden";
        }
        f0.submit();
    }
   
    window.onload = formLoad;

    $("#senhaNova").keypress(function( event ) {
    	if (event.target.value.length >= <%= TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ? "8" : ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel) %>){
          $('#limiteSenhaModal').modal('show');
    	} 
    });

    $("#senhaNovaConfirmacao").keypress(function( event ) {
      if (event.target.value.length >= <%= TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ? "8" : ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel) %>){
          $('#limiteSenhaModal').modal('show');
      } 
    });
    function limpaCampoSenha(){
  	  document.getElementById("senhaNova").value = ""; 
  	  document.getElementById("senhaNovaConfirmacao").value = "";  
  	}
  </script>
</c:set>
<t:empty_v4>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>