<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String usuCodigo = (String) request.getAttribute("usuCodigo");
String usuNome = (String) request.getAttribute("usuNome");
String usuLogin = (String) request.getAttribute("usuLogin");
String tipo = (String) request.getAttribute("tipo");
String codEntidade = (String) request.getAttribute("codEntidade");

boolean podeEdtRestAcessoFun = (boolean) request.getAttribute("podeEdtRestAcessoFun");

String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");

Map<String, EnderecoFuncaoTransferObject> mapFuncoesTo = (Map<String, EnderecoFuncaoTransferObject>) request.getAttribute("mapFuncoesTo");

%>
<c:set var="title">
  <hl:message key="rotulo.usuario.restricao.acesso.funcao.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
  <FORM NAME="form1" METHOD="post" ACTION="manterRestricaoAcessoUsuario?acao=listar&usucodigo=<%=TextHelper.forUriComponent(usuCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.usuario.titulo.dados.usuario"/></h2>
      </div>
      <div class="card-body">
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="campoFiltro"><hl:message key="rotulo.usuario.papel.filtro"/></label>
              <INPUT TYPE="text" NAME="FILTRO" placeholder='<hl:message key="mensagem.informe.filtro"/>' id="campoFiltro" CLASS="form-control" SIZE="10" VALUE="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);">
            </div>
            <div class="form-group col-sm-6">
              <label for="filtroTipo"><hl:message key="rotulo.usuario.tipo"/></label>
              <SELECT id="filtroTipo" NAME="FILTRO_TIPO" CLASS="form-control form-select" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);" nf="FILTRAR">
                <OPTION VALUE=""   <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                <OPTION VALUE="00" <%=(String)((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.usuario.funcao.codigo"/></OPTION>
                <OPTION VALUE="01" <%=(String)((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.usuario.funcao.descricao"/></OPTION>
              </SELECT>
            </div>
          </div>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-primary" onclick="filtrar();" href="#no-back" NAME="FILTRAR" ID="FILTRAR"><hl:message key="rotulo.usuario.filtrar"/></a>
    </div>
  </FORM>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.lista.regra.restricoes.acesso.titulo.tabela"/> - <%=TextHelper.forHtmlContent(usuNome)%></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.funcao.singular"/></th>
            <th scope="col"><hl:message key="rotulo.funcao.descricao"/></th>
            <th scope="col"><hl:message key="rotulo.funcao.restricao.acesso.ip"/></th>
            <th scope="col"><hl:message key="rotulo.funcao.restricao.acesso.ddns"/></th>
            <% if (podeEdtRestAcessoFun) { %> <!-- Se permitir visualizar o campo de edição não será exibido  -->
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
            <% } %>
          </tr>
        </thead>
        <tbody>
          <%=JspHelper.msgRstVazio(mapFuncoesTo.size()==0, (podeEdtRestAcessoFun ? "5" : "4"), "lp")%>
          <%
          String funCodigo, funDescricao, eafIpAcesso, eafDdnsAcesso;
          
          for (Map.Entry<String, EnderecoFuncaoTransferObject> enderecoFuncaoEntry : mapFuncoesTo.entrySet()) {
              EnderecoFuncaoTransferObject enderecoFuncaoTo = enderecoFuncaoEntry.getValue();
              funCodigo = enderecoFuncaoTo.getFunCodigo();
              funDescricao = enderecoFuncaoTo.getFunDescricao();
              eafIpAcesso = TextHelper.isNull(enderecoFuncaoTo.getEafIpAcesso()) ? "" : enderecoFuncaoTo.getEafIpAcesso();
              eafDdnsAcesso = TextHelper.isNull(enderecoFuncaoTo.getEafDdnsAcesso()) ? "" : enderecoFuncaoTo.getEafDdnsAcesso();
              %>
                        <tr>
                          <td><%=TextHelper.forHtmlContent(funCodigo)%></td>
                          <td><%=TextHelper.forHtmlContent(funDescricao)%></td>
                          <td><%=TextHelper.forHtmlContent(eafIpAcesso)%></td>
                          <td><%=TextHelper.forHtmlContent(eafDdnsAcesso)%></td>
                          <% if (podeEdtRestAcessoFun) { %>
                            <td align="center"><a href="#no-back" onClick="postData('../v3/manterRestricaoAcessoUsuario?acao=iniciar&tipo=&<%=TextHelper.forJavaScript(tipo)%>&funcodigo=<%=TextHelper.forJavaScript(funCodigo)%>&usucodigo=<%=TextHelper.forJavaScript(usuCodigo)%>&codentidade=<%=TextHelper.forJavaScript(codEntidade)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                          <% } %>
                        </tr>
          <% }; %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="7"><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
          </tr>
        </tfoot>
      </table>
    </div>
    <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div> 
  </div>
   <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></A>
  </div>          
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/usuario.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
  var f0 = document.forms[0];
  
  function filtrar(){
	  f0.submit();
  }
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>