<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
TransferObject funcao = (TransferObject) request.getAttribute("funcao");
%>
<c:set var="title">
  ${tituloPagina}
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
<div class="card">
  <div class="card-header">
    <h2 class="card-header-title">
      <%=TextHelper.forHtmlContent(funcao.getAttribute(Columns.FUN_DESCRICAO).toString())%>
      - 
      <%=TextHelper.forHtmlContent(funcao.getAttribute(Columns.GRF_DESCRICAO).toString())%>
    </h2>
  </div>
  <div class="card-body">
    <form method="post" action="../v3/manterFuncao?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">     
        <div class="row">
          <div class="form-group col-sm-4">
            <label for="funDescricao"><hl:message key="rotulo.funcao.singular"/></label>
            <input type="text" class="form-control" id="funDescricao" name="funDescricao" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.usuario", responsavel)%>" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" value="<%=TextHelper.forHtmlAttribute(funcao.getAttribute(Columns.FUN_DESCRICAO).toString())%>" size="32" >
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-4 mb-1">
            <label for="funExigeTmo"><span><hl:message key="rotulo.funcao.exige.motivo.operacao"/></span></label>
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input mt-2 ml-1" type="radio" name="funExigeTmo" id="lblFunExigeTmoSim" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent(!TextHelper.isNull(funcao.getAttribute(Columns.FUN_EXIGE_TMO)) && funcao.getAttribute(Columns.FUN_EXIGE_TMO).equals(CodedValues.TPC_SIM) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="lblFunExigeTmoSim"><hl:message key="rotulo.sim"/></label>
            </div>
            <div class="form-check form-check-inline pt-2">
              <input class="form-check-input mt-2 ml-1" type="radio" id="lblFunExigeTmoNao" name="funExigeTmo" value="<%=(String)CodedValues.TPC_NAO%>" <%=TextHelper.forHtmlContent(TextHelper.isNull(funcao.getAttribute(Columns.FUN_EXIGE_TMO)) || funcao.getAttribute(Columns.FUN_EXIGE_TMO).equals(CodedValues.TPC_NAO) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="lblFunExigeTmoNao"><hl:message key="rotulo.nao"/></label>
            </div>
        </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-4">  
            <label for="funExigeSegundaSenhaCse"><hl:message key="rotulo.funcao.exige.segunda.senha.cse"/></label>
              <select id="funExigeSegundaSenhaCse" name="funExigeSegundaSenhaCse" class="form-control form-select Select" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO)?"SELECTED":"")%>><hl:message key="rotulo.funcao.nao.exige.segunda.senha"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.outro.usuario"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.propria"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE).equals((String)CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.fila"/></option>
              </select>
          </div>
          <div class="form-group col-sm-4">  
            <label for="funExigeSegundaSenhaOrg"><hl:message key="rotulo.funcao.exige.segunda.senha.org"/></label>
              <select id="funExigeSegundaSenhaOrg" name="funExigeSegundaSenhaOrg" class="form-control form-select Select" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO)?"SELECTED":"")%>><hl:message key="rotulo.funcao.nao.exige.segunda.senha"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.outro.usuario"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.propria"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG).equals((String)CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.fila"/></option>
              </select>
          </div>
          <div class="form-group col-sm-4">  
            <label for="funExigeSegundaSenhaSup"><hl:message key="rotulo.funcao.exige.segunda.senha.sup"/></label>
              <select id="funExigeSegundaSenhaSup" name="funExigeSegundaSenhaSup" class="form-control form-select Select" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO)?"SELECTED":"")%>><hl:message key="rotulo.funcao.nao.exige.segunda.senha"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.outro.usuario"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.propria"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP).equals((String)CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.fila"/></option>
              </select>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-4">  
            <label for="funExigeSegundaSenhaCsa"><hl:message key="rotulo.funcao.exige.segunda.senha.csa"/></label>
              <select id="funExigeSegundaSenhaCsa" name="funExigeSegundaSenhaCsa" class="form-control form-select Select" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO)?"SELECTED":"")%>><hl:message key="rotulo.funcao.nao.exige.segunda.senha"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.outro.usuario"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.propria"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA).equals((String)CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.fila"/></option>
              </select>
          </div>
          <div class="form-group col-sm-4">  
            <label for="funExigeSegundaSenhaCor"><hl:message key="rotulo.funcao.exige.segunda.senha.cor"/></label>
              <select id="funExigeSegundaSenhaCor" name="funExigeSegundaSenhaCor" class="form-control form-select Select" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_COR).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO)?"SELECTED":"")%>><hl:message key="rotulo.funcao.nao.exige.segunda.senha"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_COR).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.outro.usuario"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_COR).equals((String)CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.propria"/></option>
                  <option value="<%=(String)CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA%>" <%=(String)(funcao.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_COR).equals((String)CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA)?"SELECTED":"")%>><hl:message key="rotulo.funcao.exige.segunda.senha.fila"/></option>
              </select>
          </div>
        </div>
        <hl:htmlinput name="funCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(funcao.getAttribute(Columns.FUN_CODIGO).toString())%>"/>
    </form>
  </div>
</div>
  
  <div class="btn-action d-print-none">
    <a class="btn btn-outline-danger" onClick="<%="postData('" + TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)) + "'); return false;"%>" href="#no-back"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" href="#no-back" name="btnSalvar" id="btnSalvar" onClick="if (validaCampos()) { f0.submit(); } return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>
</c:set>
<c:set var="javascript">
<link type="text/css" rel="stylesheet" href="../css/uedit.ui.css" />
<link type="text/css" rel="stylesheet" href="../css/uedit.ui.complete.css" />
<script type="text/javascript" src="../js/uedit.js"></script>
<script type="text/javascript" src="../js/uedit.ui.complete.js"></script>
<script type="text/JavaScript" src="../js/editorMsgs.js"></script>
<script type="text/JavaScript">
  var f0 = document.forms[0];
  
  function formLoad() {
    focusFirstField();
  }

  function validaCampos() {
    var Controles = new Array("funDescricao");
    var Msgs = new Array('<hl:message key="mensagem.informe.funcao.descricao"/>');
    
    if (ValidaCamposV4(Controles, Msgs)) {
  	  return true;
    } else {
  	  return false;
    }
  }
 
  window.onload = formLoad();
  
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>