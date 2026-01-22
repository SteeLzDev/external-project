<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
String filtroCsa = (String) request.getAttribute("filtro_csa");
boolean podeEnviarEmail = (boolean) request.getAttribute("podeEnviarEmail");
List lstMensagens = (List) request.getAttribute("lstMensagens");
List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
boolean exibeColunaCsa = (boolean) request.getAttribute("exibeColunaCsa");
%>
<c:set var="title">
<hl:message key="rotulo.mensagem.plural"/>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
<div class="row">
  <div class="col-sm-12 col-md-12 mb-2">
    <div class="float-end">
      <div class="btn-action">
        <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/manterMensagem?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')">
          <svg width="20">
                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-mensagem"></use></svg>
              <hl:message key="rotulo.listagem.mensagem.criar.nova"/>
        </a>
      </div>
    </div>
  </div>
</div>
<form name="form1" method="post" action="../v3/manterMensagem?acao=listar&<%=SynchronizerToken.generateToken4URL(request)%>">
<div class="row">
  <div class="col-sm-5 col-md-4">
    <div class="card">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title"><hl:message key="rotulo.listagem.mensagem.pesquisar"/></h2>
      </div>
      <div class="card-body">
          <div class="row">
            <div class="form-group col-sm">
              <label for="filtro"><hl:message key="rotulo.listagem.mensagem.filtrar.mensagem"/></label>
              <input id="filtro" type="text" name="FILTRO" class="form-control" size="10" value="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);" placeholder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.filtro.mensagem", responsavel)%>'>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm">
              <label for="uf"><hl:message key="rotulo.acao.filtrar.por"/></label>
              <select class="form-control form-select select" id="uf" name="FILTRO_TIPO" class="Select"  onBlur="fout(this);ValidaMascara(this);" nf="Filtrar" onChange="verificaCombo()">
                <optgroup label="<hl:message key="rotulo.listagem.mensagem.filtros"/>">
                  <option value="1" <%=(String)((filtro_tipo == 1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                  <option value="2" <%=(String)((filtro_tipo == 2) ? "SELECTED" : "")%>><hl:message key="rotulo.consignante.singular"/></OPTION>
                  <option value="3" <%=(String)((filtro_tipo == 3) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.singular"/></OPTION>
                  <option value="4" <%=(String)((filtro_tipo == 4) ? "SELECTED" : "")%>><hl:message key="rotulo.correspondente.singular"/></OPTION>
                  <option value="5" <%=(String)((filtro_tipo == 5) ? "SELECTED" : "")%>><hl:message key="rotulo.orgao.singular"/></OPTION>    
                  <option value="6" <%=(String)((filtro_tipo == 6) ? "SELECTED" : "")%>><hl:message key="rotulo.servidor.singular"/></OPTION>
                  <option value="7" <%=(String)((filtro_tipo == 7) ? "SELECTED" : "")%>><hl:message key="rotulo.suporte.singular"/></OPTION>
                  <option value="8" <%=(String)((filtro_tipo == 8) ? "SELECTED" : "")%>><hl:message key="rotulo.mensagem.titulo"/></OPTION>                
                </optgroup>
              </select>
            </div>
          </div>
          <div class="row" id="campoConsignatarias">
            <div class="form-group">
              <label for="CSA_CODIGO"><hl:message key="rotulo.consignataria.plural"/></label>
                <hl:htmlcombo listName="consignatarias"
                              name="CSA_CODIGO"
                              classe="form-control" 
                              fieldValue="<%=TextHelper.forHtmlAttribute( Columns.CSA_CODIGO )%>" 
                              fieldLabel="<%=(String)(Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME_ABREV)%>" 
                              notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel)%>'
                              selectedValue="<%=filtroCsa%>"
                              autoSelect="true"
                              size="5"
                />
              <div class='slider mt-2 col-sm-12 col-md-12 pl-0 pr-0'>
                <div class='tooltip-inner'><hl:message key="mensagem.utilize.crtl"/></div>
                <div class='btn-action float-end mt-3'>
                <a class='btn btn-outline-danger' href='#' onclick="reiniciarSelectConsignatarias()"><hl:message key="mensagem.limpar.selecao"/></a>
                </div>
              </div>
                
            </div>
           </div>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-primary" href="#" onClick = "document.forms['form1'].submit(); return false;">
        <svg width="20">
                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg> 
        <hl:message key="rotulo.listagem.mensagem.pesquisar"/>
      </a>
    </div>
  </div>
  <div class="col-sm-7 col-md-8">
    <div class="card">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title"><hl:message key="rotulo.mensagem.plural"/></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.mensagem.singular"/></th>
              <th scope="col"><hl:message key="rotulo.mensagem.sequencia"/></th>
              <th scope="col"><hl:message key="rotulo.mensagem.data.criacao"/></th>
              <%if(exibeColunaCsa){%>
              <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
              <% } %>
              <th scope="col"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
            <%
              CustomTransferObject mensagem = null;
              String menTitulo = null;
              String menSequencia = null;
              String menData = null;
              String menCodigo = null;
              String menExibeCsa = null;
              String csaNome = null;
              if (!lstMensagens.isEmpty()) { 
                  Iterator it = lstMensagens.iterator();       
                  while (it.hasNext()) {
                      mensagem = (CustomTransferObject)it.next(); 
                      menTitulo = (String) mensagem.getAttribute(Columns.MEN_TITULO);
                      menCodigo = (String) mensagem.getAttribute(Columns.MEN_CODIGO);
                      menData = DateHelper.toDateTimeString((Date) mensagem.getAttribute(Columns.MEN_DATA));
                      menSequencia = (mensagem.getAttribute(Columns.MEN_SEQUENCIA) != null ? mensagem.getAttribute(Columns.MEN_SEQUENCIA).toString() : "");
                      menExibeCsa = (String) mensagem.getAttribute(Columns.MEN_EXIBE_CSA);
                      if(exibeColunaCsa){
                      	csaNome = (String) mensagem.getAttribute("CSA_NOME");
                      }
           %>
            <tr>
              <td><%=TextHelper.forHtmlContent(menTitulo)%></td>
              <td><%=TextHelper.forHtmlContent(menSequencia)%></td>
              <td ><%=TextHelper.forHtmlContent(menData)%></td>
              <% if(exibeColunaCsa) {%>
              	<td ><%=TextHelper.forHtmlContent(csaNome)%></td>
              <% } %>
              <td>
                <div class="actions">
                  <div class="dropdown">
                    <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title='<hl:message key="rotulo.botao.mais.opcoes"/>' aria-label='<hl:message key="rotulo.botao.mais.opcoes"/>'> <svg>
                            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span><hl:message key="rotulo.botao.opcoes"/>
                      </div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterMensagem?acao=iniciar&menTitulo=<%=TextHelper.forJavaScriptAttribute(menTitulo)%>&menCodigo=<%=TextHelper.forJavaScript(menCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.listagem.mensagem.editar"/></a>
                      <% if (podeEnviarEmail) { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterMensagem?acao=enviar&menCodigo=<%=TextHelper.forJavaScriptAttribute(menCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.listagem.mensagem.enviar"/></a>
                      <% } %>
                      <a class="dropdown-item" href="#no-back" onClick="ExcluirMensagem('<%=TextHelper.forJavaScript(menCodigo)%>', '../v3/manterMensagem?acao=remover&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(menTitulo)%>')"><hl:message key="rotulo.listagem.mensagem.excluir"/></a>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
            <%  }       
             } else {%>
             <tr>
               <td colspan="5">&nbsp;<hl:message key="mensagem.erro.nenhuma.mensagem.encontrada"/></td>
             </tr>
             <% } %>
          </tbody>
          <tfoot>
            <tr>
            <%if(exibeColunaCsa){ %>
              <td colspan="4"><%=ApplicationResourcesHelper.getMessage("rotulo.listagem.de.mensagem", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
            <% } else {%>
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.listagem.de.mensagem", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
            <% } %>
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
    <div class="btn-action">
      <a class="btn btn-outline-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" href="#no-back"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
$(document).ready(function() {
	var selecao = document.form1.FILTRO_TIPO[document.form1.FILTRO_TIPO.selectedIndex].value;
	
	if(selecao==03){
		$( "#campoConsignatarias" ).show();
		filtro.disabled = true;
	  }else{
		$( "#campoConsignatarias" ).hide();
	  }
});

function ExcluirMensagem(menCodigo, alink, menTitulo, menExibeCsa) {    
    var url = alink + (alink.indexOf('?') == -1 ? "?" : "&")  + "menCodigo=" + menCodigo + "&menTitulo=" + menTitulo + "&excluir=sim";
    return ConfirmaUrl('<hl:message key="mensagem.confirmacao.confirma.exclusao.mensagem" arg0="'+menTitulo+'"/>', url);
   }

function verificaCombo() {
  var selecao = document.form1.FILTRO_TIPO[document.form1.FILTRO_TIPO.selectedIndex].value;    
  // Desabilita campo de filtro se a selecao for cse, servidor ou orgao
  if (selecao !=04 && selecao != 08 && selecao !=03) {
    // Limpa campo de filtro
    document.form1.FILTRO.value="";
    filtro.removeAttribute("style"); 
    document.form1.FILTRO.disabled=true;
    $( "#campoConsignatarias" ).hide();
  } else if(selecao==03){
	  $( "#campoConsignatarias" ).show();
	  filtro.removeAttribute("style");
	  document.form1.FILTRO.value="";
	  filtro.disabled=true;
  } else {
    document.form1.FILTRO.disabled=false;
    $( "#campoConsignatarias" ).hide();
  }
}

function reiniciarSelectConsignatarias(){
	$("#CSA_CODIGO option:selected").each(function () {
		$(this).removeAttr('selected');
	});
	$("#CSA_CODIGO")[0].selectedIndex = 0;
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>