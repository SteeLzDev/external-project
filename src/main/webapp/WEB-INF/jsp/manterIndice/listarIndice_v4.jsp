<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
String svcCodigo = (String) request.getAttribute("svcCodigo");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String parametros = (String) request.getAttribute("parametros");
String filtro = (String) request.getAttribute("filtro");
String linkRet = (String) request.getAttribute("linkRet");
String linkBtnNovo = (String) request.getAttribute("linkBtnNovo");
String linkAction = (String) request.getAttribute("linkAction");
List<?> indices = (List<?>) request.getAttribute("indices");
String indCodigoOrig = (String) request.getAttribute("indCodigoOrig");
String indDescricaoOrig = (String) request.getAttribute("indDescricaoOrig");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
%>

<c:set var="title">
  <hl:message key="rotulo.consultar.indice.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="btn-action mt-2 mb-4">
    <a class="btn btn-primary" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkBtnNovo)%>')"><hl:message key="rotulo.consignacao.indice.novo"/></a>
  </div> 
  <div class="row">
    <div class="col-sm-5 col-md-4">
      <form NAME="form1" METHOD="post" ACTION="<%=TextHelper.forHtmlAttribute(linkAction)%>">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.indice.plural"/></h2>
          </div>
          <div class="card-body">
            <input type="hidden" name="acao" value="iniciar">
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO"><hl:message key="rotulo.acoes.filtrar"/> <hl:message key="rotulo.usuario.singular"/></label>
                <input type="text" class="form-control" id="FILTRO" name="FILTRO" value="<%=TextHelper.forHtmlAttribute(filtro)%>" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>">
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO">
                  <optgroup>
                    <option value="" <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></option>
                    <option value="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.indice.codigo"/></option>
                    <option value="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.indice.descricao"/></option>
                  </optgroup>
                </select>
              </div>
            </div>
          </div>
        </div>        
        <div class="btn-action">
          <a name="Filtrar" id="Filtrar" class="btn btn-primary" href="#no-back" onClick="form1.submit(); return false;">
            <svg width="20">
            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg> <hl:message key="rotulo.botao.pesquisar"/>
          </a>
        </div>
      </form>
    </div>
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.usuario.plural"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th><hl:message key="rotulo.indice.codigo"/></th>
                <th><hl:message key="rotulo.indice.descricao"/></th>
                <th><hl:message key="rotulo.acoes"/></th>
              </tr>              
            </thead>
            <tbody>
<%=JspHelper.msgRstVazio(indices==null || indices.size() <= 0, "13", "lp")%>
<%
if(indices != null){
  Iterator it = indices.iterator();
  while (it.hasNext()) {
    CustomTransferObject indice = (CustomTransferObject)it.next();
    String indCodigo = (String)indice.getAttribute(Columns.IND_CODIGO);
    String indDescricao = (String)indice.getAttribute(Columns.IND_DESCRICAO);
      
%>
              <tr>
                <td><%=TextHelper.forHtmlContent(indCodigo)%></td>
                <td><%=TextHelper.forHtmlContent(indDescricao)%></td>
                <td>
                   <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span> <hl:message key="rotulo.botao.opcoes"/>
                      </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back" onClick="editarIndice('<%=TextHelper.forJavaScript(indCodigo)%>', '<%=TextHelper.forJavaScript(indDescricao)%>', '<%=TextHelper.forJavaScript(csaCodigo)%>', '<%=TextHelper.forJavaScript(svcCodigo)%>')"><hl:message key="rotulo.indice.editar"/></a>                 
                        <a class="dropdown-item" href="#no-back" onClick="excluirIndice('<%=TextHelper.forJavaScript(indCodigo)%>', '<%=TextHelper.forJavaScript(indDescricao)%>', '<%=TextHelper.forJavaScript(csaCodigo)%>', '<%=TextHelper.forJavaScript(svcCodigo)%>')"><hl:message key="rotulo.indice.excluir"/></a>
                      </div>
                    </div>
                </div>
                </td>              
              </tr>
<% 
  }
}

%>            
              
            </tbody>
            <tfoot>
              <tr>
                <td>
                  <hl:message key="rotulo.listagem.operacoes.fila.autorizacao"/> - 
                  <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
                </td>
              </tr>
           </tfoot>
          </table>
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>
      </div>
    </div>
  </div>
  
  
  
  <div class="btn-action mt-2 mb-0">
    <a class="btn btn-outline-danger" href="#no-back"  onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
	function filtrar() {
		f0.submit();
	}
  	
    <!--
    
    function editarIndice (indCodigo, indDescricao, csaCodigo, svcCodigo) {
      var url = "../v3/manterIndice?acao=editar&indCodigoOrig=" + indCodigo + "&indDescricaoOrig=" + indDescricao + "&csaCodigo=" + csaCodigo + "&svcCodigo=" + svcCodigo + "&" + "<%=SynchronizerToken.generateToken4URL(request)%>";
      postData(url);
    }
    
    function excluirIndice(indCodigo, indDescricao, csaCodigo, svcCodigo) {
      
      var url = "../v3/manterIndice?acao=excluir&indCodigo=" + indCodigo + "&indDescricao=" + indDescricao + "&csaCodigo=" + csaCodigo + "&svcCodigo=" + svcCodigo + "&excluir=sim" + "&" + "<%=SynchronizerToken.generateToken4URL(request)%>";
      var msg = '<hl:message key="mensagem.confirmacao.exclusao.indice"/>'.replace('{0}', indDescricao);
    
      return ConfirmaUrl(msg,url); 
    
    }  
    
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>