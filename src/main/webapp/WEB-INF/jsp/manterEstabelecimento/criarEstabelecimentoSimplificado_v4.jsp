<%@page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String est_codigo = (String) request.getAttribute("est_codigo");
String usu_cpf = (String) request.getAttribute("usu_cpf");
String msgErro = (String) request.getAttribute("msgErro");
EstabelecimentoTransferObject estabelecimento = (EstabelecimentoTransferObject) request.getAttribute("estabelecimento");
boolean podeEditar = (boolean) request.getAttribute("podeEditar");
boolean habilitarCodigoFolha = (boolean) request.getAttribute("habilitarCodigoFolha");
%>

<c:set var="title">
<%if (est_codigo != null && !est_codigo.equals("")) {%>
  <hl:message key="rotulo.editar.estabelecimento.titulo"/>
<%} else {%>
  <hl:message key="rotulo.criar.estabelecimento.titulo"/>
<%}%>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/manterEstabelecimento?acao=salvar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" onSubmit="return vf_cadastro_est()">     
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.estabelecimento.dados"/></h2>
      </div>
      <div class="card-body">
        <fieldset>
          <div class="row">
            <div class="form-group col-sm-12 col-md-4">
              <label for="EST_NOME"><hl:message key="rotulo.estabelecimento.nome"/></label>
              <hl:htmlinput name="EST_NOME"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(estabelecimento !=null ? estabelecimento.getEstNome() :JspHelper.verificaVarQryStr(request, \"EST_NOME\"))%>"
                            size="32"
                            others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
                            mask="#*100"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.nome", responsavel).toLowerCase()) %>"
              />
              <%=JspHelper.verificaCampoNulo(request, "EST_NOME")%>
            </div>
            <div class="form-group col-sm-12 col-md-4">
              <label for="EST_CNPJ"><hl:message key="rotulo.estabelecimento.cnpj"/></label>
              <hl:htmlinput name="EST_CNPJ"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(estabelecimento !=null ?(estabelecimento.getEstCnpj()!=null?(String)estabelecimento.getEstCnpj():\"\") : JspHelper.verificaVarQryStr(request, \"EST_CNPJ\"))%>"
                            others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
                            mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMask())%>"
                            size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjSize())%>"
                            maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMaxLenght())%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.cnpj", responsavel).toLowerCase()) %>"
              />
              <%=JspHelper.verificaCampoNulo(request, "EST_CNPJ")%>
            </div>
            <c:if test="${habilitarCodigoFolha}">
              <div class="form-group col-sm-12 col-md-4">
                <label for="EST_FOLHA"><hl:message key="rotulo.estabelecimento.folha"/></label>
                <hl:htmlinput name="EST_FOLHA"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(estabelecimento !=null ? estabelecimento.getEstFolha():JspHelper.verificaVarQryStr(request, \"EST_FOLHA\"))%>"
                              size="40"
                              others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
                              nf="submit"
                              placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.folha", responsavel).toLowerCase()) %>"
                />
              </div>
            </c:if>
          </div>
          <div class="legend"></div>
          <div class="row">
            <div class="form-group col-sm-4">
              <label for="EST_RESPONSAVEL"><hl:message key="rotulo.estabelecimento.responsavel.1"/></label>
              <hl:htmlinput name="EST_RESPONSAVEL"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(estabelecimento !=null ? estabelecimento.getEstResponsavel(): JspHelper.verificaVarQryStr(request, \"EST_RESPONSAVEL\"))%>"
                            size="32"
                            others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
                            mask="#*100"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.responsavel.1", responsavel).toLowerCase()) %>"
              />
            </div>
            <div class="form-group col-sm-4">
              <label for="EST_RESP_CARGO"><hl:message key="rotulo.estabelecimento.cargo"/></label>
              <hl:htmlinput name="EST_RESP_CARGO"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(estabelecimento !=null ? estabelecimento.getEstRespCargo(): JspHelper.verificaVarQryStr(request, \"EST_RESP_CARGO\"))%>"
                            size="32"
                            others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
                            mask="#*100"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.cargo", responsavel).toLowerCase()) %>"
              />
            </div>
            <div class="form-group col-sm-4">
              <label for="EST_RESP_TELEFONE"><hl:message key="rotulo.estabelecimento.telefone"/></label>
              <hl:htmlinput name="EST_RESP_TELEFONE"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(estabelecimento !=null ? estabelecimento.getEstRespTelefone(): JspHelper.verificaVarQryStr(request, \"EST_RESP_TELEFONE\"))%>"
                            size="32"
                            others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
                            mask="#*100"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.telefone", responsavel).toLowerCase()) %>"
              />
            </div>
          </div>
              <div class="row">
                <div class="form-group col-sm-4">
                  <label for="EST_EMAIL"><hl:message key="rotulo.estabelecimento.email"/></label>
                  <hl:htmlinput name="EST_EMAIL"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(estabelecimento !=null ? estabelecimento.getEstEmail():JspHelper.verificaVarQryStr(request, \"EST_EMAIL\"))%>"
                                size="69"
                                onBlur="formataEmails('EST_EMAIL');"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"
                                nf="submit"
                                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.email", responsavel).toLowerCase()) %>"
                  />
                </div>
	                <div class="form-group col-sm-4">
				      <label for="USU_CPF"><hl:message key="rotulo.usuario.cpf"/></label> 
				      <hl:htmlinput type="text" classe="form-control" di="USU_CPF" name="USU_CPF" value="<%=TextHelper.forHtmlAttribute(usu_cpf)%>" 
				      				placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.usuario.cpf", responsavel).toLowerCase()) %>" 
				      				mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask())%>"  
				      />
				    </div>
              </div>
            </fieldset>                 
        </fieldset>
      </div>
    </div>
    <div class="btn-action">
      <% if(podeEditar){%>
        <a class="btn btn-outline-danger" href="#no-back" onClick="<%="postData('" + SynchronizerToken.updateTokenInURL("../v3/manterEstabelecimento?acao=iniciar", request) + "'); return false;"%>"><hl:message key="rotulo.botao.voltar"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="enviar(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
      <% } else { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="<%="postData('" + SynchronizerToken.updateTokenInURL("../v3/manterEstabelecimento?acao=iniciar", request) + "'); return false;"%>"><hl:message key="rotulo.botao.voltar"/></a>
      <% } %>
    </div>

    <%if (est_codigo !=null) {%>
      <hl:htmlinput name="EST_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(est_codigo)%>"/>
      <hl:htmlinput name="est" type="hidden" value="<%=TextHelper.forHtmlAttribute(est_codigo)%>"/>      
    <%}%>
  </form>    
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript">
  var f0 = document.forms[0];
  function formLoad()
  {
    focusFirstField();
  }

  function enviar() {
	  if (vf_cadastro_est()) {
		  f0.submit();	  
	  }	  
  }
  
  //field pode ser ID ou NAME
  function formataEmails(field) {
  	
      var emailsTag = document.getElementById(field);        
      
      if(!!emailsTag)
      {    	    	       
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

  function replaceCaracteresInvalidos(emailString)
  {
  	var emails = emailString;	
  	var separador = ',';
  	
      emails = replaceAll(emails,' ', separador);
      emails = replaceAll(emails,';', separador);
      emails = replaceAll(emails,'\n',separador); 
      emails = replaceAll(emails,'\t',separador);
      
      while(emails.indexOf(",,") !== -1){
      	emails = replaceAll(emails,",,",separador);
      }
      
      if(emails[0] === separador){	// Se o primeiro caracter é vírgula ',' então
      	emails = emails.slice(1);	// remove o primeiro caracter.
      }
      
      if(emails.slice(-1) === separador){ // Se o último caracter é vírgula ',' então
      	emails = emails.slice(0,-1); 	// remove o último caracter.
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