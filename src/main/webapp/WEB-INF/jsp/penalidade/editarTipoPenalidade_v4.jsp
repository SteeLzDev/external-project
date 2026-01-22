<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean possuiDesblAutCsaPrazoPenalidade = (Boolean) request.getAttribute("possuiDesblAutCsaPrazoPenalidade");
String link = (String) request.getAttribute("link");
String tpeCodigo  = (String) request.getAttribute("tpeCodigo") != null ? (String) request.getAttribute("tpeCodigo") : ""  ;
String msgErro = (String) request.getAttribute("msgErro");
String descricao = (String) request.getAttribute("descricao");
String prazo = (String) request.getAttribute("prazo");
%>
<c:set var="title">
  <hl:message  key="rotulo.tipo.penalidade.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/editarTipoPenalidade?acao=salvar&tpeCodigo=<%=TextHelper.forHtmlAttribute(tpeCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" onSubmit="return vf_cadastro_grupo_svc()">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.penalidade.manutencao.tipo.penalidade.titulo"/></h2>
      </div>
    <div class="card-body">
      <div class="row">
          <div class="form-group col-sm-6">
            <label for="tpeDescricao"><hl:message key="rotulo.penalidade.descricao"/></label>
            <input id="tpeDescricao" type="text" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.descricao", responsavel)%>" class="form-control" NAME="tpeDescricao" VALUE="<%=TextHelper.forHtmlAttribute(descricao)%>" SIZE="20" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"<%=JspHelper.verificaCampoNulo(request, "tpeDescricao")%>/>
          </div>
          <% if (possuiDesblAutCsaPrazoPenalidade) { %>
            <div class="form-group col-sm-6">
              <label for="tpePrazoPenalidade" ><hl:message key="rotulo.penalidade.prazo"/></label>
              <input id="tpePrazoPenalidade" type="text" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.prazo", responsavel)%>" class="form-control" NAME="tpePrazoPenalidade" VALUE="<%=TextHelper.forHtmlAttribute(prazo)%>" SIZE="20" onFocus="SetarEventoMascara(this,'#D2',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          <% } %>
        </div>
      </div>   
     </div> 
      <div class="btn-action mt-3"> 
          <a class="btn btn-outline-danger" href="#no-back" id="btnVoltar" onClick="postData('<%=TextHelper.forJavaScriptAttribute(link)%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
          <a class="btn btn-primary" id="btnSalvar" href="#no-back" onClick="f0.submit(); return false;"><hl:message key="rotulo.botao.confirmar"/></a>      
      </div>
 </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
	  var f0 = document.forms[0];
  </script>
  <script type="text/JavaScript">
    function formLoad() {
      f0.tpeDescricao.focus();
    }
    
    function () {
      var Controles = new Array("tpeDescricao");
      var Msgs = new Array('<hl:message key="mensagem.penalidade.informar.descricao.tipo.penalidade"/>');
      return ValidaCampos(Controles, Msgs);
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>