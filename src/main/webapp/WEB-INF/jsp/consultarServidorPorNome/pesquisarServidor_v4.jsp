<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
%>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   <%= request.getAttribute("tituloPagina") %>
</c:set>
<c:set var="bodyContent">

<%
String tituloPagina = (String) request.getAttribute("tituloPagina");
String estIdentificador = (String) request.getAttribute("estIdentificador");
String orgIdentificador = (String) request.getAttribute("orgIdentificador");
%>
  
<div class="card">
  <div class="card-header hasIcon">
      <span class="card-header-icon">
        <svg width="26">
                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use>
              </svg>
            </span>
      <h2 class="card-header-title"><hl:message key="mensagem.pesquisa.servidor.titulo"/></h2>
  </div>
  <div class="card-body">
    <form action="../v3/pesquisarServidor" method="post" name="form1">
       <hl:htmlinput type="hidden" name="acao" di="acao" value="pesquisar" />      
       <%=SynchronizerToken.generateHtmlToken(request) %>    
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.PESQUISA_SERVIDOR_NOME)%>" >
         <div class="row">
            <div class="form-group col-sm-6">
              <label for="serNome"><%=ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_NOME, responsavel) ? "*" : "" %><hl:message key="rotulo.servidor.nome"/></label>
              <hl:htmlinput name="serNome"
                            di="serNome"
                            type="text"
                            classe="form-control"
                            size="20"                                
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome", responsavel)%>"
              />
            </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.PESQUISA_SERVIDOR_SOBRENOME)%>" >  
         <div class="row"> 
            <div class="form-group col-sm-6">
              <label for="serSobreNome"><%=ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_SOBRENOME, responsavel) ? "*" : ""%><hl:message key="rotulo.servidor.sobrenome"/></label>
              <hl:htmlinput name="serSobreNome"
                            di="serSobreNome"
                            type="text"
                            classe="form-control"
                            size="20"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.sobrenome", responsavel)%>"
              />
            </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.PESQUISA_SERVIDOR_CPF)%>" >
         <div class="row"> 
            <div class="form-group col-sm-6">
              <hl:campoCPFv4 configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.PESQUISA_SERVIDOR_CPF)%>"
                       description="<%=ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel) + (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_CPF, responsavel) ? "*" : "")%>" 
                       placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>"/>
            </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.PESQUISA_SERVIDOR_DATA_NASC)%>" >    
         <div class="row"> 
            <div class="form-group col-sm-6">
              <label for="serDataNasc"><%=ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_DATA_NASC, responsavel) ? "*" : ""%><hl:message key="rotulo.servidor.dataNasc"/></label>
              <hl:htmlinput name="serDataNasc" di="serDataNasc"
                type="text" classe="form-control" size="10"
                mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                value="" 
                placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"/>
            </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.PESQUISA_SERVIDOR_ESTABELECIMENTO)%>" >
         <%
             List lstEstabelecimentos = (List) request.getAttribute("lstEstabelecimento");
         %>
         <%
             if (lstEstabelecimentos != null && !lstEstabelecimentos.isEmpty()) {
         %>
         <div class="row">
           <div class="form-group col-sm-6">
              <label for="EST_CODIGO"><%=ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ESTABELECIMENTO, responsavel) ? "*" : ""%><hl:message key="rotulo.estabelecimento.singular"/></label>
              <%=JspHelper.geraCombo(lstEstabelecimentos, "EST_CODIGO", Columns.EST_CODIGO, Columns.EST_NOME + ";" + Columns.EST_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, estIdentificador, null, false, "form-control")%>
           </div>
         </div>
         <%
             }
         %>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.PESQUISA_SERVIDOR_ORGAO)%>" >
         <%
             List lstOrgaos = (List) request.getAttribute("lstOrgao");
         %>
         <%
             if (lstOrgaos != null && !lstOrgaos.isEmpty()) {
         %>             
         <div class="row"> 
            <div class="form-group col-sm-6">
              <label for="ORG_CODIGO"><%=ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ORGAO, responsavel) ? "*" : ""%><hl:message key="rotulo.orgao.singular"/></label>
              <%=JspHelper.geraCombo(lstOrgaos, "ORG_CODIGO", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, orgIdentificador, null, false, "form-control")%>
            </div>
         </div>
         <%
             }
         %>
       </show:showfield>  
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.PESQUISA_SERVIDOR_MATRICULA)%>" >
            <%
                request.setAttribute("configKey", FieldKeysConstants.PESQUISA_SERVIDOR_MATRICULA);
            %>      
       <div class="row"> 
          <div class="form-group col-sm-6">
            <hl:campoMatriculav4 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>" />
          </div>
       </div>
       </show:showfield>
    </form>
    <% if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_NOME, responsavel) || 
           ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_SOBRENOME, responsavel) || 
           ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_CPF, responsavel) || 
           ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_DATA_NASC, responsavel) || 
           ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ESTABELECIMENTO, responsavel) || 
           ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ORGAO, responsavel)) { %>
    <div class="col-sm-8 col-md-8 mb-2">
      <div class="alert alert-info">
        * <hl:message key="rotulo.informacao.parametros.obrigatorios.exceto.matricula"/>
      </div>
    </div>
    <% } %>
  </div>
</div>
<div class="btn-action">
  <a class="btn btn-outline-danger" href="javascript:void(0);" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
  <a class="btn btn-primary" href="javascript:void(0);"  onClick="validaSubmit()"><svg width="20"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
</div>
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript">
f0 = document.forms[0];  

function formLoad() {
  focusFirstField();
}

function checkObrigatorio() {
	
  if(f0.RSE_MATRICULA.value == null || f0.RSE_MATRICULA.value == '') {
 
   <%if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_NOME, responsavel)) { %>
       if (f0.serNome.value == null || f0.serNome.value == '') {        
        f0.serNome.focus();
           alert('<hl:message key="mensagem.informe.suporte.online.campos.obrigatorios"/>');
           return false;
       }
   <%} %>

   <%if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_SOBRENOME, responsavel)) { %>
      if (f0.serSobreNome.value == null || f0.serSobreNome.value == '') {
       f0.serSobreNome.focus();
       alert('<hl:message key="mensagem.informe.suporte.online.campos.obrigatorios"/>');
        return false;
      }
   <%} %>

   <%if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_CPF, responsavel)) { %>
      if (f0.SER_CPF.value == null || f0.SER_CPF.value == '') {
        f0.SER_CPF.focus();
        alert('<hl:message key="mensagem.informe.suporte.online.campos.obrigatorios"/>');
          return false;
      }
   <%} %>

   <%if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_DATA_NASC, responsavel)) { %>
      if (f0.serDataNasc.value == null || f0.serDataNasc.value == '') {
        f0.serDataNasc.focus();
        alert('<hl:message key="mensagem.informe.suporte.online.campos.obrigatorios"/>');
          return false;
      }
   <%} %>

   <%if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ESTABELECIMENTO, responsavel)) { %>
      if (f0.EST_CODIGO.value == null || f0.EST_CODIGO.value == '') {
        f0.EST_CODIGO.focus();
        alert('<hl:message key="mensagem.informe.suporte.online.campos.obrigatorios"/>');
          return false;
      }
   <%} %>

   <%if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_ORGAO, responsavel)) { %>
      if (f0.ORG_CODIGO.value == null || f0.ORG_CODIGO.value == '') {
        f0.ORG_CODIGO.focus();
        alert('<hl:message key="mensagem.informe.suporte.online.campos.obrigatorios"/>');
      }
   <%} %>

   <%if (ShowFieldHelper.isRequired(FieldKeysConstants.PESQUISA_SERVIDOR_MATRICULA, responsavel)) { %>
      if (f0.RSE_MATRICULA.value == null || f0.RSE_MATRICULA.value == '') {
        f0.RSE_MATRICULA.focus();
        alert('<hl:message key="mensagem.informe.suporte.online.campos.obrigatorios"/>');
          return false;
      }
   <%} %>
     return true;
   } 
   return true;
}

function validaSubmit() {
    if (checkObrigatorio()) { 
      if (typeof vfRseMatricula === 'function') {
        if(vfRseMatricula(true)) {
          f0.submit();
        }
      } else {
        f0.submit();
      }
    }
}
</script>
<hl:campoMatriculav4 scriptOnly="true"/>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
