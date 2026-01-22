<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
GrupoServicoTransferObject grupoServico = (GrupoServicoTransferObject) request.getAttribute("grupoServico");
%>
<c:set var="title">
   <hl:message key="rotulo.grupo.servico.manutencao.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form method="post" action="../v3/manterGrupoServico?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" onSubmit="return vf_cadastro_grupo_svc()">
  <div class="card">
    <div class="card-header hasIcon pl-3">
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(grupoServico.getGrupoSvcIdentificador())%> - <%=TextHelper.forHtmlContent(grupoServico.getGrupoSvcGrupo())%></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="form-group col-sm-6 ">
          <label for="tgsIdentificador"><hl:message key="rotulo.grupo.servico.codigo"/></label>
          <input type="text" 
                 class="form-control"
                 name="tgsIdentificador" 
                 value="<%=TextHelper.forHtmlAttribute(grupoServico.getGrupoSvcIdentificador())%>" 
                 size="10" 
                 onFocus="SetarEventoMascara(this,'#A40',true);" onBlur="fout(this);ValidaMascara(this);" 
                 id="tgsIdentificador" 
                 placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.codigo", responsavel) %>">
        </div>
        <%=JspHelper.verificaCampoNulo(request, "tgsIdentificador")%>
      </div>
      <div class="row">
        <div class="form-group col-sm-6 ">
          <label for="tgsGrupo"><hl:message key="rotulo.grupo.servico.descricao"/></label>
          <input type="text" 
                 class="form-control"
                 name="tgsGrupo" 
                 value="<%=TextHelper.forHtmlAttribute(grupoServico.getGrupoSvcGrupo())%>" 
                 size="32" 
                 onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" 
                 id="tgsGrupo" 
                 placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.descricao", responsavel) %>">
        </div>
        <%=JspHelper.verificaCampoNulo(request, "tgsGrupo")%>
      </div>
      <div class="row">
        <div class="form-group col-sm-6 ">
          <label for="tgsQuantidade"><hl:message key="rotulo.grupo.servico.quantidade.geral"/></label>
          <input type="text"
                 class="form-control" 
                 name="tgsQuantidade" 
                 value="<%=TextHelper.forHtmlAttribute(grupoServico.getGrupoSvcQuantidade() != null ? grupoServico.getGrupoSvcQuantidade().toString() : "")%>" 
                 size="4" 
                 onFocus="SetarEventoMascara(this,'#D2',true);" onBlur="fout(this);ValidaMascara(this);" 
                 id="tgsQuantidade" 
                 placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.quantidade.geral", responsavel) %>">
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-6 ">
          <label for="tgsQuantidadePorCsa"><hl:message key="rotulo.grupo.servico.quantidade.consignataria"/></label>
          <input type="text"
                 class="form-control" 
                 name="tgsQuantidadePorCsa" 
                 value="<%=TextHelper.forHtmlAttribute(grupoServico.getGrupoSvcQuantidadePorCsa() != null ? grupoServico.getGrupoSvcQuantidadePorCsa().toString() : "")%>" 
                 size="4" 
                 onFocus="SetarEventoMascara(this,'#D2',true);" onBlur="fout(this);ValidaMascara(this);" 
                 id="tgsQuantidadePorCsa" 
                 placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.quantidade.consignataria", responsavel) %>">
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <input name="tgsCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(grupoServico.getGrupoSvcCodigo().toString())%>">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/manterGrupoServico?acao=iniciar'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="salvar()"><hl:message key="rotulo.botao.salvar"/></a>
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
  var Msgs = new Array("<hl:message key="mensagem.informe.grupo.servico.codigo"/>",
      "<hl:message key="mensagem.informe.grupo.servico.descricao"/>");
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