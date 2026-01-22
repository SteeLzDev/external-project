<%--
* <p>Title: listarEnderecoServidor_v4</p>
* <p>Description: Listar Endereço Servidor v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.persistence.entity.EnderecoServidor"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<script language="JavaScript" type="text/JavaScript">
function confirmarExclusao(link){
  if (confirm('<hl:message key="mensagem.confirmacao.exclusao.endereco.servidor"/>')){
        postData(link);
  }
  return false;
}

</script>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List enderecoServidor = (List) request.getAttribute("enderecoServidor");
ServidorTransferObject servidor = (ServidorTransferObject) request.getAttribute("servidor");
Boolean novo = (Boolean) request.getAttribute("novo");
Boolean podeEditar = (Boolean) request.getAttribute("podeEditar");
%>
<c:set var="title">
  <hl:message key="rotulo.servidor.manutencao.endereco.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <% if(novo && podeEditar == true){%> 
  <div class="pull-right">
    <div class="btn-action ">
      <a class="btn btn-primary"
         href="#no-back" onClick="postData('../v3/editarServidor?acao=novoEndereco&SER_CODIGO=<%=servidor.getSerCodigo()%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>');"><hl:message
          key="rotulo.endereco.novo" /></a>
    </div>
  </div>
  <%}%>
  <div class="row">
  
  <%
if(!TextHelper.isNull(enderecoServidor)){
  Iterator it = enderecoServidor.iterator();
  while (it.hasNext()) {
      EnderecoServidor end = (EnderecoServidor)it.next();
    
    %>
  
    <div class="col-sm-6">
      <form name="form1" method="POST" action="">
        <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.endereco.singular"/>&nbsp;<%=TextHelper.forHtmlContent(end.getTipoEndereco().getTieDescricao())%></h2>
            </div>
            <div class="card-body">
              <div class="row">
                <dl class="row data-list">
                  <dt class="col-5"><hl:message key="rotulo.endereco.logradouro"/>: </dt>
                  <dd class="col-7"><%=end.getEnsLogradouro() != null ? TextHelper.forHtmlContent(end.getEnsLogradouro()) : ""%></dd>
                  
                  <dt class="col-5"><hl:message key="rotulo.endereco.numero.extenso"/>: </dt>
                  <dd class="col-7"><%=end.getEnsNumero() != null ? end.getEnsNumero() : ""%></dd>
                  
                  <dt class="col-5"><hl:message key="rotulo.endereco.complemento"/>: </dt>
                  <dd class="col-7"><%=end.getEnsComplemento() != null ? TextHelper.forHtmlContent(end.getEnsComplemento()) : ""%></dd>
                  
                  <dt class="col-5"><hl:message key="rotulo.endereco.bairro"/>: </dt>
                  <dd class="col-7"><%=end.getEnsBairro() != null ? TextHelper.forHtmlContent(end.getEnsBairro()) : ""%></dd>
                  
                  <dt class="col-5"><hl:message key="rotulo.endereco.cidade"/>: </dt>
                  <dd class="col-7"><%=end.getEnsMunicipio() != null ? TextHelper.forHtmlContent(end.getEnsMunicipio()) : ""%></dd>
                  
                  <dt class="col-5"><hl:message key="rotulo.endereco.estado"/>: </dt>
                  <dd class="col-7"><%=end.getEnsUf() != null ? end.getEnsUf() : ""%></dd>
                  
                  <dt class="col-5"><hl:message key="rotulo.endereco.cep"/>: </dt>
                  <dd class="col-7"><%=end.getEnsCep() != null ? end.getEnsCep() : ""%></dd>
                  
                  <dt class="col-5"><hl:message key="rotulo.tipo.endereco"/>: </dt>
                  <dd class="col-7"><%=end.getTipoEndereco().getTieDescricao() != null ? TextHelper.forHtmlContent(end.getTipoEndereco().getTieDescricao()) : ""%></dd>
                </dl>
              </div>
            </div>
          </div>
          <%if(podeEditar) {%>
            <div class="btn-action">
              <a class="btn btn-outline-danger" href="#no-back" onClick="confirmarExclusao('../v3/editarServidor?acao=excluirEndereco&ENS_CODIGO=<%=end.getEnsCodigo()%>&SER_CODIGO=<%=servidor.getSerCodigo()%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');"><hl:message key="rotulo.acoes.excluir"/></a>
              <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/editarServidor?acao=editarEndereco&ENS_CODIGO=<%=end.getEnsCodigo()%>&SER_CODIGO=<%=servidor.getSerCodigo()%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
            </div>
          <%} %>
      </form>
    </div>
    
  <%}
}%>
    
    
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>