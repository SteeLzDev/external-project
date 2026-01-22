<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.entidade.RegistroServidorTO"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String titulo = (String) request.getAttribute("titulo");
String acao = (String) request.getAttribute("acao");
int tamMaxSenhaServidor = request.getAttribute("tamMaxSenhaServidor") != null ? (Integer) request.getAttribute("tamMaxSenhaServidor") : 0;
int tamMinSenhaServidor = request.getAttribute("tamMinSenhaServidor") != null ? (Integer) request.getAttribute("tamMinSenhaServidor") : 0;
String rseMatricula = (String) request.getAttribute("rseMatricula");
String orgIdentificador = (String) request.getAttribute("orgIdentificador");
String estIdentificador = (String) request.getAttribute("estIdentificador");
String orgCodigo = (String) request.getAttribute("orgCodigo");
String serCpf = (String) request.getAttribute("serCpf");
RegistroServidorTO rse = (RegistroServidorTO) request.getAttribute("rse");
String msg = (String) request.getAttribute("msg");
String actionForm = (String) request.getAttribute("actionForm");

CustomTransferObject servInfo = (CustomTransferObject) request.getAttribute("servidor");

boolean senhaServidorNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);
boolean novoUsuarioSer = request.getAttribute("novoUsuarioSer") != null;
boolean emailObrigatorio = request.getAttribute("emailObrigatorio") != null ? (boolean) request.getAttribute("emailObrigatorio") : false;
boolean telefoneSerEditavel = request.getAttribute("telefoneSerEditavel") != null ? (boolean) request.getAttribute("telefoneSerEditavel") : false;
boolean emailSerEditavel = request.getAttribute("emailSerEditavel") != null ? (boolean) request.getAttribute("emailSerEditavel") : false;
boolean celularSerEditavel = request.getAttribute("celularSerEditavel") != null ? (boolean) request.getAttribute("celularSerEditavel") : false;
String serTel =  request.getAttribute("serTel") != null ? (String) request.getAttribute("serTel") : "";
String serEmail = request.getAttribute("serEmail") != null ? (String) request.getAttribute("serEmail") : "";
String serCel = request.getAttribute("serCel") != null ? (String) request.getAttribute("serCel") : "";

%>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="title">
   <%=TextHelper.forHtmlContent(titulo)%>
</c:set>
<c:set var="bodyContent">
    <form name="form1" id="form1" method="post" action="<%=actionForm + "&" + SynchronizerToken.generateToken4URL(request)%>">
      <div class="row">
        <div class="col-sm-7">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.dados.gerais"/></h2>
            </div>
            <div class="card-body">
              <dl class="row data-list">
                <dt class="col-6"><hl:message key="rotulo.consignante.singular"/>:</dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel))%></dd>
                <% pageContext.setAttribute("servidor", servInfo); %>
                <hl:detalharServidorv4 name="servidor"/>
              </dl>
            </div>
          </div>
        </div>
        <div class="col-sm-5">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.cadastrar.usuario.servidor.login"/></h2>
            </div>
            <div class="card-body">
                <div class="row justify-content-md-center">
                  <div class="form-group col-sm">
                    <label for="login"><hl:message key="rotulo.cadastrar.usuario.servidor.login"/></label>
                    <input class="form-control" onFocus="SetarEventoMascaraV4(this,'#*30',true);" onBlur="fout(this);ValidaMascaraV4(this);" name="login" value="<%=TextHelper.forHtmlAttribute(rseMatricula)%>" readonly type="text">
                  </div>
                </div>
                <div class="row justify-content-md-center">
                  <div class="form-group col-sm">
                    <label for="senhaNova"><%= acao.equals("ALTERAR_SENHA_AUTORIZACAO") ? ApplicationResourcesHelper.getMessage("rotulo.alterar.senha.aut.usuario.nova", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.alterar.senha.usuario.nova", responsavel) %></label>
                    <hl:htmlpassword name="senhaNova"
                                     di="senhaNova"
                                     cryptedfield="senhaNovaCriptografada"
                                     onFocus="setMascaraSenha(this,true);"
                                     onBlur="fout(this);ValidaMascaraV4(this);"
                                     classe="form-control"
                                     isSenhaServidor="true"
                                     maxlength="<%=String.valueOf(tamMaxSenhaServidor)%>"
                                     placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.senha", responsavel)%>"
                                     size="32" />
                  </div>
                </div>
                <div class="row justify-content-md-center">
                  <div class="form-group col-sm">
                    <label for="senhaNovaConfirmacao"><hl:message key="rotulo.servidor.confirma.nova.senha"/></label>
                    <input type="password" onFocus="setMascaraSenha(this,true);" onBlur="fout(this);ValidaMascaraV4(this);" id="senhaNovaConfirmacao" name="senhaNovaConfirmacao" class="form-control" size="32" placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.confirme.nova.senha", responsavel)%>" autocomplete="off">
                  </div>
                </div>
                <!-- EMAIL -->
                <%if (emailSerEditavel) { %>
                <div class="row justify-content-md-center">
                  <div class="form-group col-sm">
                <label for="email"><hl:message key="rotulo.servidor.email"/><% if (!emailObrigatorio) { %> <hl:message key="rotulo.campo.opcional"/><% } %></label>
                <input type="text" name="email" id="email"  placeholder="<hl:message key="mensagem.informacao.alterar.senha.digite.email"/>" value="<%=TextHelper.forHtmlAttribute(serEmail)%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" class="form-control"/>
                  </div>
                </div>
                <% } %>
                <!-- TELEFONE -->
                <% if (telefoneSerEditavel) { 
                    // Quebra o telefone em DDD + número.
                	  String serTelDdd = "";
                    if (!TextHelper.isNull(serTel)) {
                  	  serTel = TextHelper.dropSeparator(serTel);
                    	  serTelDdd = serTel.substring(0, 2);
                    	  serTel = serTel.substring(2, serTel.length());
                    }
                %> 
                <div class="row">
      			  <div class="form-group col-sm-2">
      				<label for="ddd"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
      				<hl:htmlinput name="ddd"
                              di="ddd"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(serTelDdd)%>"
                              mask="<%=LocaleHelper.getDDDMask()%>"
                              placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel) %>"  />
      				
      			  </div>
      			<div class="form-group col-sm-6 col-md-6">
      				<label for="telefone"><hl:message key="rotulo.servidor.telefone"/></label>
      				<hl:htmlinput name="telefone"
                             di="telefone"
                             type="text"
                             classe="form-control"
                             value="<%=TextHelper.forHtmlAttribute(serTel)%>"
                             mask="<%=LocaleHelper.getTelefoneMask()%>"
                             placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.telefone", responsavel) %>" />
      				
      			</div>
                </div> 
                <% } %>
                <!-- CELULAR -->
                <% if (celularSerEditavel) { 
                
                // Quebra o celular em DDD + número.
            	  String serCelDdd = "";
                if (!TextHelper.isNull(serCel)) {
              	  serCel = TextHelper.dropSeparator(serCel);
                	  serCelDdd = serCel.substring(0, 2);
                	  serCel = serCel.substring(2, serCel.length());
                }
                %>
                <div class="row">
      			  <div class="form-group col-sm-2">
      				<label for="dddcel"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
      				<hl:htmlinput name="dddcel"
                              di="dddcel"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(serCelDdd)%>"
                              mask="<%=LocaleHelper.getDDDCelularMask()%>"
                              placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel) %>"  />
      				
      			  </div>
      			<div class="form-group col-sm-6 col-md-6">
      				<label for="celular"><hl:message key="rotulo.servidor.celular"/></label>
      				<hl:htmlinput name="celular"
                             di="celular"
                             type="text"
                             classe="form-control"
                             value="<%=TextHelper.forHtmlAttribute(serCel)%>"
                             mask="<%=LocaleHelper.getCelularMask()%>"
                             placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.celular", responsavel) %>" />
      				
      			</div>
                </div> 
                <% } %>
                <input type="hidden" name="RSE_MATRICULA" value="<%=TextHelper.forHtmlAttribute(rseMatricula)%>">
                <input type="hidden" name="ORG_IDENTIFICADOR" value="<%=TextHelper.forHtmlAttribute(orgIdentificador)%>">
                <input type="hidden" name="EST_IDENTIFICADOR" value="<%=TextHelper.forHtmlAttribute(estIdentificador)%>">
                <input type="hidden" name="ORG_CODIGO" value="<%=TextHelper.forHtmlAttribute(orgCodigo)%>">
                <input type="hidden" name="SER_CPF" value="<%=TextHelper.forHtmlAttribute(serCpf)%>">
                <input type="hidden" name="ACAO" value="<%=TextHelper.forHtmlAttribute(acao)%>">
                <input type="hidden" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(rse.getRseCodigo())%>">
                <input type="hidden" name="MM_update" value="true"/>
            </div>
          </div>
          <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
            <a class="btn btn-primary" href="#no-back" onClick="if(verificaForm()){f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
          </div>
          <% if (!TextHelper.isNull(msg)) { %>
          <div class="alert alert-info" role="alert">
            <p class="mb-0">
              <% out.write(msg); %>
            </p>
          </div>
          <% } %>
        </div>
      </div>
    </form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
f0 = document.forms[0];
function formLoad() {
  if( f0.senhaNova != null) {
    f0.senhaNova.focus();
  }
}

function setMascaraSenha(ctrl, AutoSkip) {
  SetarEventoMascaraV4(ctrl,'<%=(String)("#" + ((acao.equals("ALTERAR_SENHA") && senhaServidorNumerica) ? "D" : "*") + tamMaxSenhaServidor)%>', AutoSkip);
}

function verificaForm () {
  //Se for pro caso de uso criação de novo usuário servidor valida campos
  <% if (novoUsuarioSer) { %>
    // Validação se os campos email, telefone e celular são obrigatórios no front
    <% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel)) { %>
    var serEmailField = f0.email;
    if (serEmailField.value == null || serEmailField.value == '') {
        alert('<hl:message key="mensagem.informe.servidor.email"/>');
        serEmailField.focus();
        return false;
    }
  <% } %>
  <% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel)) { %>
  var serTelField = f0.telefone;
  var dddTelefoneField = f0.ddd;
    if (serTelField.value == null || serTelField.value == '') {
        alert('<hl:message key="mensagem.informe.servidor.telefone"/>');
        serTelField.focus();
        return false;
    } else if (dddTelefoneField.value == null || dddTelefoneField.value.trim() == '') {
        alert('<hl:message key="mensagem.informe.servidor.ddd.telefone"/>');
        dddTelefoneField.focus();
        return false;
        }
<% } %>
  
  <% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel)) { %>
      var serCelField = f0.celular;
      var dddCelularField = f0.dddcel;
      if (serCelField.value == null || serCelField.value == '') {
          alert('<hl:message key="mensagem.informe.servidor.celular"/>');
          serCelField.focus();
          return false;
       } else if (dddCelularField.value == null || dddCelularField.value.trim() == '') {
             alert('<hl:message key="mensagem.informe.servidor.ddd.celular"/>');
             dddCelularField.focus();
             return false;
           }
  <% } %>
  
  // Validação se os campos podem ser editados e se estão válidos
  <% if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel)) { %>
      var telefoneField = f0.telefone;
      var dddTelefoneField = f0.ddd;
      if (telefoneField.value != null && telefoneField.value != '' && telefoneField.value.length < '<%=LocaleHelper.getTelefoneSize()%>') {
      	alert('<hl:message key="mensagem.erro.servidor.telefone.invalido"/>');
      	telefoneField.focus();
      	return false;
      } else if (dddTelefoneField.value != null && dddTelefoneField.value != '' && (telefoneField.value == null || telefoneField.value == '')){
        	alert('<hl:message key="mensagem.erro.servidor.telefone.invalido"/>');
          	telefoneField.focus();
          	return false;
          }
  
      if ((telefoneField.value != null && telefoneField.value != '') && (dddTelefoneField.value != null && dddTelefoneField.value.trim().length < <%=LocaleHelper.getDDDMask().length()%>)) {
      	alert('<hl:message key="mensagem.erro.servidor.ddd.telefone.invalido"/>');
      	dddTelefoneField.focus();
      	return false;
      }
  <% } %>
  
  <% if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel)) { %>
      var celularField = f0.celular;
      var dddCelularField = f0.dddcel;
      if (celularField.value != null && celularField.value != '' && celularField.value.length < '<%=LocaleHelper.getCelularSize()%>') {
      	alert('<hl:message key="mensagem.erro.servidor.celular.invalido"/>');
      	celularField.focus();
      	return false;
      } else if (dddCelularField.value != null && dddCelularField.value != '' && (celularField.value == null || celularField.value == '')){
        alert('<hl:message key="mensagem.erro.servidor.celular.invalido"/>');
        dddCelularField.focus();
        return false;
      }
       if (celularField.value != null && celularField.value != '' && (dddCelularField.value != null && dddCelularField.value.trim().length < <%=LocaleHelper.getDDDCelularMask().length()%>)) {
      	alert('<hl:message key="mensagem.erro.servidor.ddd.celular.invalido"/>');
      	dddCelularField.focus();
      	return false;
        }
   <% } %>
     
<% } %>

if ((f0.senhaNova.value!=null) && (f0.senhaNovaConfirmacao.value!=null) &&
        (f0.senhaNova.value != "") && (f0.senhaNovaConfirmacao.value != "")) {
      if (f0.senhaNova.value.length < <%=(int)(tamMinSenhaServidor)%>) {
        alert('<hl:message key="mensagem.erro.cadastrar.senha.servidor.minimo"/>'.replace('{0}', <%=(int)(tamMinSenhaServidor)%>));
        return false;
      } else if (f0.senhaNova.value.length > <%=(int)(tamMaxSenhaServidor)%>) {
        alert('<hl:message key="mensagem.erro.cadastrar.senha.servidor.maximo"/>'.replace('{0}', <%=(int)(tamMaxSenhaServidor)%>));
        return false;
      } else if (f0.senhaNova.value == f0.senhaNovaConfirmacao.value) {
        CriptografaSenha(f0.senhaNova, f0.senhaNovaCriptografada, false);
        //f0.submit();
      } else {
        alert('<hl:message key="mensagem.erro.servidor.recuperar.senha.diferente"/>');
        f0.senhaNova.focus();
        return false;
      }
    } else {
      alert ('<hl:message key="mensagem.informe.servidor.alterar.senha.aut"/>');
      f0.senhaNova.focus();
      return false;
    }
  
  return true;
}

window.onload = formLoad;
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
