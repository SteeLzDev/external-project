<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
// Responsável é o usuário do sistema, já que a operação não é realizada dentro da sessão do usuário
AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

String cpf = request.getAttribute("cpf") != null ? (String) request.getAttribute("cpf") : (String) JspHelper.getFieldValue(request, FieldKeysConstants.CAD_SERVIDOR_CPF, "", true, responsavel);
String matricula = request.getAttribute("matricula") != null ? (String) request.getAttribute("matricula") : (String) JspHelper.getFieldValue(request, FieldKeysConstants.CAD_SERVIDOR_MATRICULA, "", true, responsavel); 
String orgCod = (String) JspHelper.getFieldValue(request, FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO, "", true, responsavel);
List listaOrgaos =  request.getAttribute("listaOrgaos") != null ? (List) request.getAttribute("listaOrgaos") : null;

%>
<c:set var="bodyContent">
<form name="form1" method="post" action="../v3/cadastrarSenhaServidor?acao=selecionarServidor" autocomplete="off">
  <input name="score" type="hidden" id="score">
  <input name="matchlog" type="hidden" id="matchlog">
  <div class="alert alert-warning" role="alert">
    <hl:message key="mensagem.auto.cadastro.senha.ser.ajuda.selecao"/>
  </div>
  <div class="row">
    <div class="form-group col-sm-12">  
      <hl:campoCPFv4 name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_CPF)%>"
                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_CPF)%>"
                   value="<%=TextHelper.forHtmlAttribute(cpf)%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel) %>"
                   textHelpKey="mensagem.auto.cadastro.senha.ser.ajuda.cpf"
                   classe="form-control"
      />
    </div>
  </div>
  <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>" >
    <div class="form-group">
      <label for="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>"><hl:message key="rotulo.servidor.matricula"/></label>
      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>"
                    di="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>"
                    type="text"
                    classe="form-control"
                    onFocus="SetarEventoMascara(this,'#*30',true);"
                    onBlur="fout(this);ValidaMascara(this);"
                    size="20"
                    configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>"
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel) %>"
                    value="<%=TextHelper.forHtmlAttribute(matricula)%>"
      />
      <hl:message key="mensagem.auto.cadastro.senha.ser.ajuda.matricula"/>
    </div>
  </show:showfield>
  <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>" >
  <%
      // Busca lista de órgãos 
      if (listaOrgaos != null) {
        request.setAttribute("listaOrgaos",listaOrgaos);  
        if(listaOrgaos.size() > 1){
  %>
    <div class="form-group ">
      <label for="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>"><hl:message key="rotulo.orgao.singular"/></label>
      <hl:htmlcombo
           listName="listaOrgaos" 
           name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>"
           di="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>" 
           fieldValue="<%=TextHelper.forHtmlAttribute( Columns.ORG_CODIGO)%>" 
           fieldLabel="<%=(String)(Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR)%>" 
           notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>" 
           configKey="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>"
           selectedValue="<%=TextHelper.forHtmlAttribute(orgCod)%>"
           classe="form-control"
         />
    </div>
    <%
        } else {
           Iterator itOrgReg = listaOrgaos.iterator();
           CustomTransferObject orgRegTO = (CustomTransferObject) itOrgReg.next();
           String orgCodigo = (String) orgRegTO.getAttribute(Columns.ORG_CODIGO);
    %>
    <input type="HIDDEN" name="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO)%>" value="<%=TextHelper.forHtmlAttribute(orgCodigo)%>">
    <%
        }
       }
    %>
</show:showfield>                   
</form>  
<div class="clearfix text-right">
  <button class="btn btn-outline-danger mr-2" aria-label="Voltar" onClick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>')">
    <hl:message key="rotulo.botao.voltar"/>
  </button>
  <button id="btnOK" name="btnOK" class="btn btn-primary" type="submit" onClick="if(verificaForm()){cleanFields();} return false;">         
          <svg width="17"><use xlink:href="#i-avancar"></use></svg>
          <hl:message key="rotulo.botao.proximo"/>                
  </button>
</div>
</c:set>

<c:set var="javascript">
<script type="text/JavaScript">
  var primOnFocus = true;
  
  function formLoad() {
    focusFirstField();
  }
  
  function newOnKeyUp(Controle) {
    testPassword(Controle.value);
  }
  
  function setanewOnKeyUp(Controle) {
    if (!primOnFocus) {
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
  }
  
  function verificaForm() {
      var Controles = new Array("<%=(String)( FieldKeysConstants.CAD_SERVIDOR_CPF )%>",
                "<%=(String)( FieldKeysConstants.CAD_SERVIDOR_MATRICULA )%>",
                "<%=(String)( FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO )%>");
        var Msgs = new Array('<hl:message key="mensagem.informe.servidor.cpf"/>',
                           '<hl:message key="mensagem.informe.servidor.matricula"/>',
                         '<hl:message key="mensagem.informe.servidor.orgao"/>');
        
        if (!ValidaCampos(Controles, Msgs)) {
          return false;
        }
        
        if (f0.<%=(String)( FieldKeysConstants.CAD_SERVIDOR_CPF )%> != null && 
            !CPF_OK(extraiNumCNPJCPF(f0.<%=(String)( FieldKeysConstants.CAD_SERVIDOR_CPF )%>.value))) {
          return false;
        }
        
        return true;
  }
  
  function cleanFields() {
     if (f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_CPF)%> && f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_CPF)%>.type == "text") {
       f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_CPF)%>.type = "hidden";
     }
     if (f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%> && f0.<%=TextHelper.forHtmlAttribute( FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>.type == "text") {
       f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CAD_SERVIDOR_MATRICULA)%>.type = "hidden";
     }
     f0.submit();
        
  }

  window.onload = formLoad;
  
</script>
<script>
  f0 = document.forms[0];
</script>
</c:set>
<t:empty_v4>    
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>