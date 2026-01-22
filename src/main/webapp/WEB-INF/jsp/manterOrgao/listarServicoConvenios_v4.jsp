<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String org_nome = (String) request.getAttribute("org_nome");
String subTitulo = (String) request.getAttribute("subTitulo");
String csa_codigo = (String) request.getAttribute("csa_codigo");
String svc_codigo = (String) request.getAttribute("svc_codigo");
String org_codigo = (String) request.getAttribute("org_codigo");
List<?> convenios = (List<?>) request.getAttribute("convenios");

boolean podeEditarCnvCor = (boolean) request.getAttribute("podeEditarCnvCor");
boolean primeiro = true;
%>
<c:set var="title">
  <%=org_nome.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.manutencao.servicos.titulo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.manutencao.servicos.existente.titulo", responsavel, org_nome)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
    <form action="../v3/listarServicoConvenios?<%=SynchronizerToken.generateToken4URL(request)%>"  method="POST" name="form1">
    <input type="HIDDEN" name="acao" value="editarConvenio">
    <input type="HIDDEN" name="svc_codigo" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
    <input type="HIDDEN" name="csa_codigo" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
    <input type="HIDDEN" name="org_codigo" value="<%=TextHelper.forHtmlAttribute(org_codigo)%>">
    <input type="HIDDEN" name="MM_update" value="1">
    <div class="row">
      <div class="col-sm-11 col-md-12">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><%=TextHelper.forHtml(subTitulo)%></h2>
          </div>
        <div class="card-body pb-0 mb-0">
           <div class="alert alert-warning mb-0" role="alert">
           <% if (convenios.size() > 0) { %>
             <hl:message key="mensagem.selecione.convenio.habilitar"/>
           <% } else { %>
             <hl:message key="mensagem.erro.nenhum.convenio.encontrado"/>
           <% } %>
           </div>
        </div>
        <% if (convenios.size() > 0) { %>
          <div class="card-body table-responsive p-0">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col" width="10%" class="ocultarColuna">
                    <div class="form-check">                  
                     <input type="checkbox" class="form-check-input ml-0" onClick="checkUnCheckAll();" id="checkAll" name="checkAll" data-bs-toggle="tooltip" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar.todos", responsavel) %>" title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar.todos", responsavel) %>" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar.todos", responsavel) %>">
                    </div>                  
                  </th>
                  <th scope="col"><hl:message key="rotulo.correspondente.singular"/></th>
                  <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
              </thead>
              <tbody>
                <%
                  CustomTransferObject convenio = null;
                  String nome = "", codigo = "", scv_codigo = "";
                
                  Iterator<?> it = convenios.iterator();
                  while (it.hasNext()) {
                    convenio   = (CustomTransferObject)it.next();
                
                    nome = convenio.getAttribute(Columns.COR_NOME).toString() + " - "
                         + convenio.getAttribute(Columns.COR_IDENTIFICADOR).toString();
                
                    codigo = convenio.getAttribute(Columns.COR_CODIGO).toString();
                    scv_codigo = convenio.getAttribute("STATUS").toString();
                    if (primeiro){
                        if (scv_codigo.equals(CodedValues.SCV_ATIVO)){
                           primeiro = false;
                        }
                    }
                %>
                <tr class='refLinha'>
                  <td class="ocultarColuna" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel) %>" data-bs-toggle="tooltip" title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel) %>" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel) %>">
                     <div class="form-check">
                     <input type="checkbox" class="form-check-input ml-0" name="COR_CODIGO" id="chk<%=TextHelper.forHtmlAttribute(codigo)%>" value="<%=TextHelper.forHtmlAttribute(codigo)%>" onChange="enableFields(<%=TextHelper.forHtmlAttribute(codigo)%>);" <%=(String)(scv_codigo.equals(CodedValues.SCV_ATIVO) ? " checked " : "")%> <%=(String)(podeEditarCnvCor ? "" : " disabled " )%>>
                     </div>
                  </td>
                  <td class="selecionarColuna selecionarLinha"><%=TextHelper.forHtmlContent(nome)%>
                    <!-- campo hidden necessário para find do jquery setar valor corretamente para a linha -->
                    <hl:htmlinput name="<%="colPrdNumero" + codigo%>"   type="hidden" di="<%="colPrdNumero" + codigo%>"   value="<%=TextHelper.forHtmlAttribute(codigo)%>" />
                  </td>                     
                  <td class="selecionarColuna selecionarLinha"><a href="#" onClick="enableFields(<%=TextHelper.forHtmlAttribute(codigo)%>);"><hl:message key="rotulo.acoes.selecionar"/></a></td>
                </tr>
             <% }%>
              </tbody>
            </table>
          </div>
          <% } %>
        </div>
      </div>
    </div>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute( SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request) )%>')"><hl:message key="rotulo.botao.voltar"/></a>
      <% if (convenios.size() > 0 && podeEditarCnvCor) { %>
        <a class="btn btn-primary" href="#no-back" onClick="checkAll(f0, 'COR_CODIGO'); $('.ocultarColuna').show(); f0.checkAll.checked = true; return false;"><hl:message key="rotulo.lista.servico.habilitar.tudo"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="uncheckAll(f0, 'COR_CODIGO'); ocultarColuna(); return false;"><hl:message key="rotulo.lista.servico.desabilitar.tudo"/></a>
        <a href="#" onClick="f0.submit(); return false;" class="btn btn-primary"><hl:message key="rotulo.botao.salvar"/></a>
      <% } %>
    </div> 
  </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/colunaCheckboxInput.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript">
    var f0 = document.forms[0];
    window.onload = formLoad;
    
    function formLoad() {
  	  <%if (primeiro == true){%>
  	      ocultarColuna();
      <%}%>
    }

    function enableFields(numero){
  	    var codigo = document.getElementById('chk'+numero);
  		  if (codigo.checked){
  			  codigo.checked = false;
  		  }else{
  			  codigo.checked = true;
  		  }
  	}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
