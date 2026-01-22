<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
%>
<c:set var="title">
   <hl:message key="rotulo.grupo.servico.inclusao"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form method="post" action="../v3/manterGrupoServico?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" onSubmit="return vf_cadastro_grupo_svc()">
  <div class="card">
    <div class="card-header hasIcon pl-3">
      <h2 class="card-header-title"><hl:message key="rotulo.grupo.servico.novo"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="form-group col-sm-6 ">
          <label for="tgsIdentificador"><hl:message key="rotulo.grupo.servico.codigo"/> </label>
          <hl:htmlinput type="text" classe="form-control" 
                 di="tgsIdentificador" 
                 value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"tgsIdentificador\"))%>"
                 name="tgsIdentificador"
                 size="10"
                 mask="#A40" 
                 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.codigo", responsavel) %>"/>
          <%=JspHelper.verificaCampoNulo(request, "tgsIdentificador")%>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-6 ">
          <label for="tgsGrupo"><hl:message key="rotulo.grupo.servico.descricao"/></label>
          <hl:htmlinput type="text" classe="form-control" 
                 di="tgsGrupo" 
                 value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"tgsGrupo\"))%>"
                 name="tgsGrupo"
                 size="32"
                 mask="#*100" 
                 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.descricao", responsavel) %>"/>
          <%=JspHelper.verificaCampoNulo(request, "tgsGrupo")%>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-6 ">
          <label for="tgsQuantidade"><hl:message key="rotulo.grupo.servico.quantidade.geral"/></label>
          <hl:htmlinput type="text" classe="form-control" 
                 di="tgsQuantidade" 
                 value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"tgsQuantidade\"))%>"
                 name="tgsQuantidade"
                 size="4"
                 mask="#D2" 
                 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.quantidade.geral", responsavel) %>"/>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-6 ">
          <label for="tgsQuantidadePorCsa"><hl:message key="rotulo.grupo.servico.quantidade.consignataria"/></label>
          <hl:htmlinput type="text" classe="form-control" 
                 di="tgsQuantidadePorCsa" 
                 value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"tgsQuantidadePorCsa\"))%>"
                 name="tgsQuantidadePorCsa"
                 size="4"
                 mask="#D2" 
                 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.quantidade.consignataria", responsavel) %>"
          />
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/manterGrupoServico?acao=iniciar'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" id="btnEnvia" href="#no-back" onClick="salvar()"><hl:message key="rotulo.botao.salvar"/></a>
  </div>     
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
var f0 = document.forms[0];
</script>
<script type="text/JavaScript">
function salvar() {
   f0.submit(); 
}
  
function formLoad() {
  f0.tgsIdentificador.focus();
}

function vf_cadastro_grupo_svc() {
  var Controles = new Array("tgsIdentificador", "tgsGrupo");
  var Msgs = new Array('<hl:message key="mensagem.informe.grupo.servico.codigo"/>',
      '<hl:message key="mensagem.informe.grupo.servico.descricao"/>');
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