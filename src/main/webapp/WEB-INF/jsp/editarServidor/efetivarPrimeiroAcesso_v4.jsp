<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String ajudaCampoCaptcha = ApplicationResourcesHelper.getMessage("ajuda.campo.captcha", responsavel);

// Verifica parâmetro que indica a forma do login de usuário servidor
boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
boolean senhaServidorNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);

int intpwdStrength = (int) request.getAttribute("intpwdStrength");
int pwdStrengthLevel = (int) request.getAttribute("pwdStrengthLevel");
String strpwdStrengthLevel = (String) request.getAttribute("strpwdStrengthLevel");
String pwdStrength = (String) request.getAttribute("pwdStrength");
int tamMinSenhaServidor = (int) request.getAttribute("tamMinSenhaServidor");
int tamMaxSenhaServidor = (int) request.getAttribute("tamMaxSenhaServidor");
boolean ignoraSeveridade = (boolean) request.getAttribute("ignoraSeveridade");
String passo = (String) request.getAttribute("passo");
String usuCodigo = (String) request.getAttribute("usuCodigo");
String token = (String) request.getAttribute("token");
String matricula = (String) request.getAttribute("matricula");
String orgCodigo = (String) request.getAttribute("orgCodigo");
String orgIdentificador = (String) request.getAttribute("orgIdentificador");
String estCodigo = (String) request.getAttribute("estCodigo");
String estIdentificador = (String) request.getAttribute("estIdentificador");

%>
<c:set var="bodyContent">
<% if (passo.equals("passo1")) { %>
    <form name="form1" method="post" action="../v3/efetivarPrimeiroAcesso?acao=iniciar&usu=servidor&<%=SynchronizerToken.generateToken4URL(request)%>" autocomplete="off">
      <input type="hidden" name="passo" value="passo2" />
      <div class="form-group">
        <label for="CPF"><hl:message key="rotulo.servidor.cpf"/></label>
        <hl:htmlinput name="CPF"
                      di="CPF"
                      type="text"
                      mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask())%>" 
                      value=""
                      placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel) %>"
                      classe="form-control"                    
        />
      </div>
        <div id="descricao"><span id="rotuloOtp"><hl:message key="rotulo.otp.possuo"/></span></div>
      <div class="form-group mb-1" role="radiogroup" aria-labelledby="rotuloOtp">
        <div class="form-check form-check-inline pt-3">
          <input class="form-check-input ml-1" type="radio" id="SEM_OTPS" name="SEM_OTP" onchange="enableDisableField('OTP')" checked/>
          <label class="form-check-label labelSemNegrito ml-1 pr-4" for="SEM_OTPS"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check-inline form-check">
          <input class="form-check-input ml-1" type="radio" name="SEM_OTP" value="S" id="SEM_OTPN" onclick="enableDisableField('OTP')">
          <label class="form-check-label labelSemNegrito ml-1 pr-4" for="SEM_OTPN"><hl:message key="rotulo.nao"/></label>
        </div>
      </div>
      <div class="form-group">
        <label for="OTP"><hl:message key="rotulo.otp.codigo"/></label>
        <hl:htmlinput name="OTP"
                      di="OTP"
                      type="text"
                      mask="#D6" 
                      size="7"
                      value=""
                      placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.opt", responsavel) %>"
                      classe="form-control"                    
        />
      </div>
      <div class="row">
        <div class="form-group col-sm-5">
          <label for="loginCodigo"><hl:message key="rotulo.captcha.codigo"/></label>
          <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
        </div>
        <div class="form-group col-sm-6">
          <div class="captcha">
            <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
            <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
            <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
              data-bs-content='<hl:message key="mensagem.ajuda.captcha.usuario.v3"/>'
              data-original-title=<hl:message key="rotulo.ajuda" />>
              <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
            </a>
          </div>
        </div>
      </div>
      <div class="clearfix text-right">
        <a class="btn btn-outline-danger mr-2" id="btnCancelar" aria-label="Voltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>'); return false;">
          <hl:message key="rotulo.botao.voltar"/>
        </a>
        <button id="btnOK" name="btnOK" class=" btn btn-primary" type="submit" onClick="if(verificaForm()){cleanFields();} return false;">
                <svg width="17"><use xlink:href="#i-avancar"></use></svg>
                <hl:message key="rotulo.botao.entrar"/>                
        </button>
      </div>
    </form>
<% } else if (passo.equals("passo2")) { %>
    <form name="form1" method="post" action="../v3/efetivarPrimeiroAcesso?acao=iniciar&usu=servidor&<%=SynchronizerToken.generateToken4URL(request)%>" autocomplete="off">
      <input type="hidden" name="passo" value="passo3" />
      <input type="hidden" name="usuCodigo" value="<%=usuCodigo%>" />
      <input type="hidden" name="token" value="<%=token%>" />
      <input type="hidden" name="CPF" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CPF"))%>" />
      <div class="form-group">
        <label for="CPF2"><hl:message key="rotulo.servidor.cpf"/></label>
        <hl:htmlinput name="CPF2"
                      di="CPF2"
                      type="text"
                      mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask())%>" 
                      value="<%=TextHelper.forHtmlContent(JspHelper.verificaVarQryStr(request, "CPF"))%>"
                      placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel) %>"
                      classe="form-control"
                      others="readonly"                    
        />
      </div>
      <div class="form-group">
        <label for="OTP"><hl:message key="rotulo.otp.codigo"/></label>
        <hl:htmlinput name="OTP"
                      di="OTP"
                      type="text"
                      mask="#D6" 
                      size="7"
                      value=""
                      placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.opt", responsavel) %>"
                      classe="form-control"                    
        />
      </div>
      <div class="row">
        <div class="form-group col-sm-3">
          <label for="SER_DDD_CELULAR"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
          <hl:htmlinput name="SER_DDD_CELULAR"
                        di="SER_DDD_CELULAR"
                        type="text"
                        classe="form-control"                    
                        value=""
                        size="2"
                        placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel) %>"
                        mask="<%=LocaleHelper.getDDDCelularMask()%>"
          />
        </div>
        <div class="form-group col-sm-9">
          <label for="SER_CELULAR"><hl:message key="rotulo.servidor.celular"/></label>
          <hl:htmlinput name="SER_CELULAR"
                        di="SER_CELULAR"
                        type="text"
                        classe="form-control"                    
                        value=""
                        size="9"
                        placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.celular", responsavel) %>"
                        mask="<%=LocaleHelper.getCelularMask()%>"
          />
        </div>
      </div>
      <div class="form-group">
        <label for="SER_EMAIL"><hl:message key="rotulo.servidor.email"/></label>
        <hl:htmlinput name="SER_EMAIL"
                      di="SER_EMAIL"
                      type="text"
                      classe="form-control"                    
                      value=""
                      size="32"
                      placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.email", responsavel) %>"                                
                      mask="#*100" 
        />
      </div>
      <div class="row">
        <div class="form-group col-sm-5">
          <label for="loginCodigo"><hl:message key="rotulo.captcha.codigo"/></label>
          <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
        </div>
        <div class="form-group col-sm-6">
          <div class="captcha">
            <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
            <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
            <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
              data-bs-content='<hl:message key="mensagem.ajuda.captcha.usuario.v3"/>'
              data-original-title=<hl:message key="rotulo.ajuda" />>
              <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
            </a>
          </div>
        </div>
      </div>
      <div class="clearfix text-right">
        <a class="btn btn-outline-danger mr-2" id="btnCancelar" aria-label="Voltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>'); return false;">
          <hl:message key="rotulo.botao.voltar"/>
        </a>
        <button id="btnOK" name="btnOK" class=" btn btn-primary" type="submit" onClick="if(verificaForm()){cleanFields();} return false;">
                <svg width="17"><use xlink:href="#i-avancar"></use></svg>
                <hl:message key="rotulo.botao.entrar"/>                
        </button>
      </div>
    </form>

<% } else if (passo.equals("passo3")) { %>
    <form name="form1" method="post" action="../v3/efetivarPrimeiroAcesso?acao=iniciar&usu=servidor&<%=SynchronizerToken.generateToken4URL(request)%>" autocomplete="off">
      <input type="hidden" name="passo" value="passo4" />
      <%out.print(SynchronizerToken.generateHtmlToken(request));%>
      <input type="hidden" name="usuCodigo" value="<%=usuCodigo%>" />
      <input type="hidden" name="username" value="<%=TextHelper.forHtmlAttribute(matricula)%>" />
<%
    if (loginComEstOrg) {
%>
      <input type="hidden" name="codigo_orgao" value="<%=orgCodigo%>" />
      <input type="hidden" name="orgao" value="<%=orgIdentificador%>" />
<%
    } else {
%>
      <input type="hidden" name="codigo_orgao" value="<%=estCodigo%>" />
      <input type="hidden" name="orgao" value="<%=estIdentificador%>" />
<%
    }
%>
      
      <input type="hidden" name="token" value="<%=token%>" />
      <input type="hidden" name="CPF" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CPF"))%>" />
      <input type="hidden" name="score" id="score">
      <input type="hidden" name="matchlog" id="matchlog">
      <div class="form-group">
        <label for="CPF2"><hl:message key="rotulo.servidor.cpf"/></label>
        <hl:htmlinput name="CPF2"
                      di="CPF2"
                      type="text"
                      mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask())%>" 
                      value="<%=TextHelper.forHtmlContent(JspHelper.verificaVarQryStr(request, "CPF"))%>"
                      placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel) %>"
                      classe="form-control"
                      others="readonly"                    
        />
      </div>
      <div class="form-group">
        <label for="OTP"><hl:message key="rotulo.otp.codigo"/></label>
        <hl:htmlinput name="OTP"
                      di="OTP"
                      type="text"
                      mask="#D6" 
                      size="7"
                      value=""
                      placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.opt", responsavel) %>"
                      classe="form-control"                    
        />
      </div>
      <div class="form-group">
        <label for="senhaNova"><hl:message key="rotulo.servidor.nova.senha"/></label>
        <hl:htmlpassword name="senhaNova"
                         di="senhaNova" 
                         cryptedfield="senhaNovaRSA" 
                         onFocus="setMascaraSenha(this,true); setanewOnKeyUp(this);" 
                         onBlur="fout(this);ValidaMascara(this);newOnKeyUp(this);" 
                         classe="form-control" 
                         size="20" 
                         isSenhaServidor="true"
                         placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.senha", responsavel) %>"
        />
      </div>
      <% if (!ignoraSeveridade) { %> 
      <div class="row mt-2">
        <div class="form-group col-sm-12">
            <div id="divSeveridade" class="alert alert-danger divSeveridade" role="alert">
              <p class="mb-0"><hl:message key="rotulo.usuario.nivel.seguranca"/>: <span id="verdict"><hl:message key="rotulo.nivel.senha.muito.baixo"/></span></p>
            </div>
        </div>
      </div>
      <% } %>
      <div class="form-group">
        <label for="senhaNovaConfirmacao"><hl:message key="rotulo.servidor.confirma.nova.senha"/></label>
        <hl:htmlpassword name="senhaNovaConfirmacao"
                         di="senhaNovaConfirmacao" 
                         cryptedfield="senhaNovaConfirmacaoRSA" 
                         onFocus="setMascaraSenha(this,true);" 
                         onBlur="fout(this);ValidaMascara(this);" 
                         classe="form-control" 
                         size="20" 
                         isSenhaServidor="true"
                         placeHolder="<%= ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.senha", responsavel) %>"
        />
      </div>           
      <div class="row">
        <div class="form-group col-sm-5">
          <label for="loginCodigo"><hl:message key="rotulo.captcha.codigo"/></label>
          <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
        </div>
        <div class="form-group col-sm-6">
          <div class="captcha">
            <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
            <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
            <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
              data-bs-content='<hl:message key="mensagem.ajuda.captcha.usuario.v3"/>'
              data-original-title=<hl:message key="rotulo.ajuda" />>
              <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
            </a>
          </div>
        </div>
      </div>
      <div class="clearfix text-right">
        <a class="btn btn-outline-danger mr-2" id="btnCancelar" aria-label="Voltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>'); return false;">
          <hl:message key="rotulo.botao.voltar"/>
        </a>
        <button id="btnOK" name="btnOK" class=" btn btn-primary" type="submit" onClick="if(verificaForm()){cleanFields();} return false;">
                <svg width="17"><use xlink:href="#i-avancar"></use></svg>
                <hl:message key="rotulo.botao.entrar"/>                
        </button>
      </div>
    </form>
<% } else if (passo.equals("passo4")) { %>
    <div class="row mr-1 justify-content-end"> 
      <a class="btn btn-outline-danger mr-2" id="btnCancelar" aria-label="Voltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>'); return false;">
        <hl:message key="rotulo.botao.voltar"/>
      </a>
    </div>
<% } %>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/passwordmeter_3010.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
  window.onload= formLoad;

  function formLoad() {
    focusFirstField();
  }

  function enableDisableField(fieldName) {
    var field = getElt(fieldName);
    if (field.disabled) {
      field.disabled = false;
    } else {
      field.value = "";
      field.disabled = true;
    }
  }

  function verificaForm() {
    if (f0.CPF != undefined && f0.CPF.value == "") {
      alert('<hl:message key="mensagem.informe.servidor.cpf"/>');
      f0.CPF.focus();
      return false;
    } else if (f0.CPF != undefined) {
      if (!CPF_OK(extraiNumCNPJCPF(f0.CPF.value))) {
        f0.CPF.focus();
        return false;
      }
    }
    if (f0.OTP != undefined && !f0.OTP.disabled && f0.OTP.value == "") {
      alert('<hl:message key="mensagem.informe.otp.codigo"/>');
      f0.OTP.focus();
      return false;
    }
    if (f0.SER_EMAIL != undefined && f0.SER_EMAIL.value == "" && (f0.SER_CELULAR.value == "" || f0.SER_DDD_CELULAR.value == "")) {
      alert('<hl:message key="mensagem.informe.celular.ou.email.primeiro.acesso"/>');
      f0.SER_EMAIL.focus();
      return false;
    }
    if (f0.captcha != undefined && (f0.captcha.value == "" || f0.captcha.value == "<%=TextHelper.forJavaScript(ajudaCampoCaptcha)%>")) {
      alert('<hl:message key="mensagem.informe.captcha.codigo"/>');
      f0.captcha.focus();
      return false;
    }
    if (f0.senhaNova != undefined && f0.senhaNova != null) {
      if (f0.senhaNova.value != null && f0.senhaNovaConfirmacao.value != null &&
          f0.senhaNova.value != "" && f0.senhaNovaConfirmacao.value != "") {
        newOnKeyUp(f0.senhaNova);
        if (f0.senhaNova.value.length < <%=tamMinSenhaServidor%>) {
          alert('<hl:message key="mensagem.erro.cadastrar.senha.servidor.minimo"/>'.replace('{0}', <%=tamMinSenhaServidor%>));
          limpaSenhas();
          return false;
        } else if (f0.senhaNova.value.length > <%=tamMaxSenhaServidor%>) {
          alert('<hl:message key="mensagem.erro.cadastrar.senha.servidor.maximo"/>'.replace('{0}', <%=tamMaxSenhaServidor%>));
          limpaSenhas();
          return false;
        } else if (f0.senhaNova.value == f0.senhaNovaConfirmacao.value) {
        <% if (!ignoraSeveridade) { %>    
           if (f0.score.value < <%=(int)(pwdStrengthLevel)%>) {
             alert('<hl:message key="mensagem.erro.servidor.primeiro.acesso.invalida"/>');
             limpaSenhas();
             return false;
           }
        <% } %>
        } else {
            alert('<hl:message key="mensagem.erro.servidor.primeiro.acesso.diferente"/>');
            limpaSenhas();
            return false;
        }
      } else {
        alert('<hl:message key="mensagem.informe.servidor.primeiro.acesso.matricula"/>');
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
        testPassword(Controle.value);
      }
    } 
    <% } %>
  }

  function setMascaraSenha(ctrl, AutoSkip) {
    SetarEventoMascara(ctrl,'<%=(String)("#" + ((senhaServidorNumerica) ? "D" : "*")  + tamMaxSenhaServidor)%>', AutoSkip);
  }
  
  function limpaSenhas() {
    f0.senhaNova.value = '';
    f0.senhaNovaConfirmacao.value = '';
    f0.senhaNova.focus();
    <% if (!ignoraSeveridade) { %>
    f0.verdict.value = '';
    <% } %>  
  }

  function cleanFields() {
    if (f0.CPF && f0.CPF.type == "text") {
       f0.CPF.type = "hidden";
    }
    if (f0.OTP && f0.OTP.type == "text") {
     f0.OTP.type = "hidden";
    }
    if (f0.captcha && f0.captcha.type == "text") {
       f0.captcha.type = "hidden";
      }
    f0.submit();
        
  }   
</script>
<script>
  f0 = document.forms[0];
</script>
</c:set>
<t:empty_v4>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>