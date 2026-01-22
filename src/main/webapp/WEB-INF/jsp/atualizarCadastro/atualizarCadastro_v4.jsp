<%@page import="jakarta.json.JsonObjectBuilder"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.persistence.entity.Banco"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
//Responsável é o usuário do sistema 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
%>
<c:set var="bodyContent">
<%
Map<?, ?> hshDadosAtualizacao = (Map<?, ?>) request.getAttribute("dadosAtualizacao");

String nomeResponsavel = !TextHelper.isNull(hshDadosAtualizacao) ? (String) hshDadosAtualizacao.get("nomeResponsavel") : "";
String telResponsavel = !TextHelper.isNull(hshDadosAtualizacao) ? (String) hshDadosAtualizacao.get("telResponsavel") : "";
String email = !TextHelper.isNull(hshDadosAtualizacao) ? (String) hshDadosAtualizacao.get("email") : "";
String codigoEntidade = !TextHelper.isNull(hshDadosAtualizacao) ? (String) hshDadosAtualizacao.get("codigoEntidade") : "";
String nomeEntidade = !TextHelper.isNull(hshDadosAtualizacao) ? (String) hshDadosAtualizacao.get("nomeEntidade") : "";
String cseNomeFolha = !TextHelper.isNull(hshDadosAtualizacao) ? (String) hshDadosAtualizacao.get("cseNomeFolha") : "";
List<Banco> listBanco = (List<Banco>) request.getAttribute("listBanco");
Boolean listBancoVazia = (Boolean) request.getAttribute("listBancoVazia");
Boolean podeEditarConsignante = (Boolean) request.getAttribute("podeEditarConsignante");


String msgAtualizacao = (String) request.getAttribute("msgAtualizacao");
boolean exigeAtualizacaoCadastralCsaCnpj = (boolean) request.getAttribute("exigeAtualizacaoCadastralCsaCnpj");
boolean retornoErro = (boolean) request.getAttribute("retornoErro");
%>
<%if (!retornoErro) { %>
    <form name="form1" method="post" action="../v3/atualizarCadastro?acao=editar" autocomplete="off" onload="formLoad();">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><%=TextHelper.forHtmlContent(msgAtualizacao)%></p>    
      </div>

    <%
    if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_RESPONSAVEL, responsavel)) {
    %>
    <div class="form-group">
      <label for="NOME_RESPONSAVEL"><hl:message key="rotulo.atualizar.cadastro.responsavel" /><% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_RESPONSAVEL, responsavel)) { %>*<% } %></label>
      <hl:htmlinput classe="form-control" di="NOME_RESPONSAVEL"
        name="NOME_RESPONSAVEL" type="text"
        value="<%=TextHelper.forHtmlAttribute(nomeResponsavel)%>"
        onFocus="SetarEventoMascara(this,'#*100',true);"
        onBlur="fout(this);ValidaMascara(this);" size="100"
        placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.atualizar.cadastro.responsavel.placeholder", responsavel)%>" />
    </div>
    <% } %>
    <%
    if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_RESP_TELEFONE, responsavel)) {
    %>
    <div class="form-group">
        <label for="TEL_RESP"><hl:message key="rotulo.atualizar.cadastro.telefone"/><% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_RESP_TELEFONE, responsavel)) { %>*<% } %></label>
        <hl:htmlinput 
            classe="form-control" 
            di="TEL_RESP" 
            name="TEL_RESP" 
            type="text"
            value="<%=TextHelper.forHtmlAttribute(telResponsavel)%>"
            size="32" 
            onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
            placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.atualizar.cadastro.telefone.placeholder", responsavel)%>"
            /> 
      </div>
    <% } %>
    <%
    if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_EMAIL, responsavel)) {
    %>
      <div class="form-group"> 
        <label for="RESP_EMAIL"><hl:message key="rotulo.atualizar.cadastro.email"/><% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_EMAIL, responsavel)) { %>*<% } %></label>
          <hl:htmlinput 
              classe="form-control" 
              di="RESP_EMAIL" 
              name="RESP_EMAIL" 
              type="text" 
              value="<%=TextHelper.forHtmlAttribute(email)%>"
              onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
              size="32" 
              placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.atualizar.cadastro.email.placeholder", responsavel)%>"
              /> 
      </div>
       <% } %>
      <%
      if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_SISTEMA_FOLHA, responsavel) && responsavel.isCse() && responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE)) {
      %>
      <div class="form-group">
              <label for="CSE_SISTEMA_FOLHA">
                <hl:message key="rotulo.consignante.sistema.folha" /><% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_SISTEMA_FOLHA, responsavel)) { %>*<% } %>
              </label>
              <hl:htmlinput name="CSE_SISTEMA_FOLHA" type="text" classe="form-control"
                placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.cse.sistema.folha",
                responsavel)%>" di="CSE_SISTEMA_FOLHA"
                value="<%=TextHelper.forHtmlAttribute(cseNomeFolha)%>" size="32" mask="#*100" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
      <% } %>
      <%
      if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.ATUALIZA_CADASTRO_BCO_FOLHA, responsavel) && responsavel.isCse() && responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE)) {
      %>
      <div class="form-group">
              <label for="BCO_CODIGO">
                <hl:message key="rotulo.consignante.banco.folha" /><% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_BCO_FOLHA, responsavel)) { %>*<% } %>
              </label>
              <%
                    StringBuffer comboBancoFolha = new StringBuffer();
                    comboBancoFolha.append("<select name=\"BCO_CODIGO\" class=\"form-control\"");
                    comboBancoFolha.append(" multiple size=\"4\" onFocus=\"SetarEventoMascara(this,'#*200',true);\"");
                    comboBancoFolha.append(" onBlur=\"fout(this);ValidaMascara(this);\"");
                    comboBancoFolha.append(" >");

                    if (listBancoVazia) {
                      comboBancoFolha.append("<option value=\"\" selected>");
                    } else {
                      comboBancoFolha.append("<option value=\"\">");
                    }

                    comboBancoFolha.append(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel))
                        .append("</option>");

                    for (Banco banco : listBanco) {
                      comboBancoFolha.append("<option value=\"");
                      comboBancoFolha.append(TextHelper.forHtmlAttribute(banco.getBcoCodigo()));
                      if(banco.getBcoFolha().equals("S")){
                        comboBancoFolha.append("\" selected>");
                      } else {
                        comboBancoFolha.append("\">");
                      }
                      comboBancoFolha.append(TextHelper.forHtmlContent(banco.getBcoDescricao()));
                      comboBancoFolha.append("</option>");
                    }
                    comboBancoFolha.append("</select>");
              %>
              <%=comboBancoFolha.toString()%>
            </div>
      <% } %>
      <%if(exigeAtualizacaoCadastralCsaCnpj && responsavel.isCsa()) {%>
          <% String cnpjEntidade = !TextHelper.isNull(hshDadosAtualizacao) ? (String) hshDadosAtualizacao.get("cnpjEntidade") : ""; %>
          <div class="form-group">
            <label for="CNPJ_ENTIDADE"><hl:message key="rotulo.atualizar.cadastro.cnpj"/></label>
            <hl:htmlinput 
                classe="form-control" 
                di="CNPJ_ENTIDADE" 
                name="CNPJ_ENTIDADE" 
                type="text"
                value="<%=TextHelper.forHtmlAttribute(cnpjEntidade)%>"
                mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMask())%>"
                size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjSize())%>"
                maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMaxLenght())%>" 
                onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.atualizar.cadastro.cnpj.placeholder", responsavel)%>"
                /> 
          </div>
      <%} %>
      <hl:htmlinput name="codigoEntidade" type="hidden" di="codigoResponsavel" value="<%=TextHelper.forHtmlAttribute(codigoEntidade)%>"/>
      <hl:htmlinput name="nomeEntidade" type="hidden" di="nomeEntidade" value="<%=TextHelper.forHtmlAttribute(nomeEntidade)%>"/>
    </form>
<%} %>
    <div class="mr-3 float-end">
    <div class="clearfix flex-end">
	  <button class="btn btn-outline-danger mr-2" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#no-back" onclick="postData('../v3/autenticarUsuario')">
	    <hl:message key="rotulo.botao.voltar"/>
      </button>
    <%if (!retornoErro){ %>
        <button class="btn btn-primary" type="submit" onClick="if(verificaForm()){cleanFields();} return false;">
          <svg width="17"> 
          <use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
          <hl:message key="rotulo.atualizar.cadastro.botao"/>
        </button>
      <%} %>
    </div>
  </div>
</div>
</c:set>
<c:set var="javascript">
  <script type="text/javascript">
  var f0 = document.forms[0];
  
  function formLoad() {
      focusFirstField();
  }
  
  function verificaForm() {
	  <% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_RESPONSAVEL, responsavel)) { %>
      if (f0.NOME_RESPONSAVEL.value == "") {
          alert('<hl:message key="mensagem.atualizar.cadastro.informe.responsavel"/>');
          f0.NOME_RESPONSAVEL.focus();
          return false;
      } 
      <% } %>
      
      <% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_EMAIL, responsavel)) { %>
      if (f0.RESP_EMAIL != undefined && f0.RESP_EMAIL.value == ""){
      	alert('<hl:message key="mensagem.atualizar.cadastro.informe.email"/>');
          f0.RESP_EMAIL.focus();
          return false;
      } 
      <% } %>
      
      <% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_RESP_TELEFONE, responsavel)) { %>
      if (f0.TEL_RESP.value == ""){
      	alert('<hl:message key="mensagem.atualizar.cadastro.informe.telefone"/>');
          f0.TEL_RESP.focus();
          return false;
      }
      <% } %>
      
      <% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_SISTEMA_FOLHA, responsavel) && responsavel.isCse() && responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE)) { %>
      if (f0.CSE_SISTEMA_FOLHA.value == ""){
      	alert('<hl:message key="mensagem.atualizar.cadastro.informe.folha"/>');
          f0.CSE_SISTEMA_FOLHA.focus();
          return false;
      }
      <% } %>

      <% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_BCO_FOLHA, responsavel) && responsavel.isCse() && responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE)) { %>
      if (f0.BCO_CODIGO.value == ""){
      	alert('<hl:message key="mensagem.atualizar.cadastro.informe.banco"/>');
          f0.BCO_CODIGO.focus();
          return false;
      }
      <% } %>
      
      if (f0.CNPJ_ENTIDADE != undefined && f0.CNPJ_ENTIDADE.value == ""){
    	  alert('<hl:message key="mensagem.atualizar.cadastro.informe.cnpj"/>');
          f0.CNPJ_ENTIDADE.focus();
          return false;
      }
      return true;
  }
  
  function cleanFields() {
      if (f0.NOME_RESPONSAVEL && f0.NOME_RESPONSAVEL.type == "text") {
          f0.NOME_RESPONSAVEL.type = "hidden";
      }
      if (f0.RESP_EMAIL && f0.RESP_EMAIL.type == "text") {
          f0.RESP_EMAIL.type = "hidden";
      }
      if (f0.TEL_RESP && f0.TEL_RESP.type == "text") {
          f0.TEL_RESP.type = "hidden";
      }
      if (f0.CNPJ_ENTIDADE && f0.CNPJ_ENTIDADE.type == "text"){
    	  f0.CNPJ_ENTIDADE.type = "hidden";
      }
      f0.submit();
  }
  
  window.onload = formLoad;
  </script>
</c:set>
<t:empty_v4>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>