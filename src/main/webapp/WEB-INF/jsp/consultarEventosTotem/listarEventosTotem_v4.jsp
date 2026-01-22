<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String tituloPagina = (String) request.getAttribute("tituloPagina");
String linkRet = (String) request.getAttribute("linkRet");
List<TransferObject> eventos = (List<TransferObject>) request.getAttribute("eventos");
String matriculaPesquisa = request.getAttribute("matricula") != null ? (String) request.getAttribute("matricula") : "";
String cpfPesquisa = request.getAttribute("cpf") != null ? (String) request.getAttribute("cpf") : "";
String periodoIni = request.getAttribute("periodoIni") != null ? (String) request.getAttribute("periodoIni") : "";
String periodoFim = request.getAttribute("periodoFim") != null ? (String) request.getAttribute("periodoFim") : "";
String vlrPossuiFotoPesquisa = request.getAttribute("vlrPossuiFotoPesquisa") != null ? (String) request.getAttribute("vlrPossuiFotoPesquisa") : "";
%>

<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   ${tituloPagina}
</c:set>
<c:set var="bodyContent">
   <div class="row">
     <div class="col-sm mb-2">
      <div class="float-end">
       <button data-bs-toggle="dropdown" aria-haspopup="true" id="exportarResultado" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.evento.totem.relatorio.exportar"/></button>
       <div class="dropdown-menu dropdown-menu-right" aria-labelledby="exportarResultado">
          <a class="dropdown-item" href="#no-back" onClick="exportarResultado('CSV');"><hl:message key="rotulo.evento.totem.relatorio.csv"/></a>
          <a class="dropdown-item" href="#no-back" onClick="exportarResultado('PDF');"><hl:message key="rotulo.evento.totem.relatorio.pdf"/></a>
          <a class="dropdown-item" href="#no-back" onClick="exportarResultado('TEXT');"><hl:message key="rotulo.evento.totem.relatorio.txt"/></a>
          <a class="dropdown-item" href="#no-back" onClick="exportarResultado('XLS')"><hl:message key="rotulo.evento.totem.relatorio.xls"/></a>
          <a class="dropdown-item" href="#no-back" onClick="exportarResultado('XLSX')"><hl:message key="rotulo.evento.totem.relatorio.xlsx"/></a>
       </div>
      </div>
     </div>
    </div>
    
 <div class="opcoes-avancadas">
      <form action="<%=linkRet%>" method="post" name="formPesqAvancada">
  <a class="opcoes-avancadas-head" href="#faq1" data-bs-toggle="collapse" aria-expanded="false" aria-controls="faq1" aria-label='<hl:message key="mensagem.inclusao.avancada.clique.aqui"/>'><hl:message key="rotulo.avancada.opcoes"/></a>
  <div class="collapse" id="faq1">
    <div class="opcoes-avancadas-body pl-4 mb-2">
        <%= SynchronizerToken.generateHtmlToken(request) %>
        <fieldset>
          <h3 class="legend">
            <span><hl:message key="rotulo.acao.filtrar.por"/></span>
          </h3>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="vlrPossuiFotoPesquisa"><hl:message key="rotulo.evento.totem.foto"/></label>
                 <select name="vlrPossuiFotoPesquisa" id="vlrPossuiFotoPesquisa" class="form-control" onfocus="SetarEventoMascara(this,'#*200',true);" onblur="fout(this);ValidaMascara(this);" style="background-color: white; color: black;"> 
                 <option value="" <%=vlrPossuiFotoPesquisa.isEmpty() ? "selected" : "" %>><hl:message key="rotulo.campo.todos"/></option> 
                 <option value="1" <%=vlrPossuiFotoPesquisa.equals("1") ? "selected" : "" %>><hl:message key="rotulo.evento.totem.possui.foto"/></option>
                 <option value="0" <%=vlrPossuiFotoPesquisa.equals("0") ? "selected" : "" %>><hl:message key="mensagem.evento.totem.nao.possui.foto"/></option>
                 </select>
              </div>
          </div>
        </fieldset>
            </div>
      <div class="btn-action">
        <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="pesquisarComFiltro()"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
      </div>
    </div>
         </form>
</div>
    
    
    
    
<div class="card">
  <div class="card-header hasIcon pl-3">
    <h2 class="card-header-title"><hl:message key="rotulo.resultado.pesquisa"/></h2>
  </div>
  <div class="card-body table-responsive ">
    <table class="table table-striped table-hover">
      <thead>
        <tr>
          <th scope="col"><hl:message key="rotulo.matricula.singular"/></th>
          <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
          <th scope="col"><hl:message key="rotulo.evento.totem.data"/></th>
          <th scope="col"><hl:message key="rotulo.evento.totem.ip.acesso"/></th>
          <th scope="col"><hl:message key="rotulo.evento.totem.descricao"/></th>
          <th scope="col"><hl:message key="rotulo.evento.totem.possui.foto"/></th>
          <th scope="col"><hl:message key="rotulo.acoes"/></th>  
        </tr>
      </thead>
      <tbody>
      <%=JspHelper.msgRstVazio(eventos == null || eventos.size() == 0, 12, responsavel)%>
      <%
        for (TransferObject eventoTO : eventos) {
            String matricula = (String)eventoTO.getAttribute("MATRICULA");
            String cpf = (String)eventoTO.getAttribute("CPF");
            String data = (String)eventoTO.getAttribute("DATA");
            String ip = (String)eventoTO.getAttribute("IP");
            String descricao = (String)eventoTO.getAttribute("DESCRICAO");
            String possuiFoto = (String)eventoTO.getAttribute("FOTO");
           %>
        <tr>
          <td><%=TextHelper.forHtmlContent(matricula)%></td>
          <td><%=TextHelper.forHtmlContent(cpf)%></td>
          <td><%=TextHelper.forHtmlContent(data)%></td>
          <td><%=TextHelper.forHtmlContent(ip)%></td>
          <td><%=TextHelper.forHtmlContent(descricao)%></td>
          <td><%=possuiFoto != null ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel)%></td>
          <td>
            <div class="actions">
              <div class="dropdown">
                <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  <div class="form-inline">
                    <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"><svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg></span>
                    <hl:message key="rotulo.botao.opcoes"/>
                  </div>
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                  <% if (responsavel.temPermissao(CodedValues.FUN_CONSULTA_EVENTOS_TOTEM)) { %>
                    <a href="javascript:void(0);" class="dropdown-item" onclick="doIt('detalharEvento', '<%=TextHelper.forHtmlAttribute(eventoTO.getAttribute("evn_codigo"))%>', '<%=TextHelper.forHtmlAttribute(eventoTO.getAttribute("EVN_CODIGO_BIOMETRIA"))%>')"><hl:message key="mensagem.eventos.totem.detalhar"/></a>
                  <% } %>
                </div>
              </div>
            </div>
          </td>
        </tr>
     <% } %>
       </tbody>
       <tfoot>
          <tr>
            <td colspan="10">
              <hl:message key="mensagem.evento.totem.data" arg0="<%=DateHelper.toDateTimeString(DateHelper.getSystemDatetime())%>" />
              <span class="font-italic"> - <%=request.getAttribute("_paginacaoSubTitulo")%></span>
            </td>
          </tr>
       </tfoot>
    </table>
  </div>      
  <div class="card-footer">
    <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
  </div>
</div>
<div class="btn-action">
  <a class="btn btn-outline-danger" href="javascript:void(0);" onClick="postData('../v3/listarEventosTotem?acao=iniciar'); return false;"><hl:message key="rotulo.botao.voltar"/></a> 
</div>

<div class="modal fade" id="modalExportarResultado" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
  <div class="modal-dialog" role="document" style="max-width:301px">
    <div class="modal-content p-3">
      <div class="modal-header">
        <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.evento.totem.relatorio.exportar"/></h5>
      </div>
      <div class="form-group modal-body m-0">
        <span class="modal-title mb-0" id="subTitulo"><hl:message key="mensagem.processando.arquivo"/></span>
      </div>
    </div>
  </div>
</div>


</c:set>
<c:set var="javascript">

<script type="text/JavaScript">
  function doIt(op, cod, cod2, ser, svc) {
    var link = '';
    if (op == 'detalharEvento') {
      link = '../v3/listarEventosTotem?acao=detalhar&EVN_CODIGO='+cod+'&EVN_CODIGO_BIOMETRIA='+cod2+'&linkRet=<%=linkRet%>';
    } else {
      return;
    }

    link += '&<%=SynchronizerToken.generateToken4URL(request)%>';
    
    postData(link);
  }
  
  function pesquisarComFiltro() {
	  f0 = document.forms[0];
      f0.submit();
  }
  
function exportarResultado(formato) {
  $.post("../v3/listarEventosTotem?acao=exportarResultado&tipoRelatorio=eventos_totem&matricula=<%=matriculaPesquisa%>&cpf=<%=cpfPesquisa%>&periodoIni=<%=periodoIni%>&periodoFim=<%=periodoFim%>&vlrPossuiFotoPesquisa=<%=vlrPossuiFotoPesquisa%>&formato=" + formato + "&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>", function(data) {
	  $('#modalExportarResultado').modal('show');
	  setTimeout(verificarRelatorio, 5000); // a cada 5 segundos
  }, "json");

}
  
function verificarRelatorio() {
  var nomeArquivo;
  
  $.post("../v3/listarEventosTotem?acao=verificarRelatorio&_skip_history_=true", function(data) {
       try {
    	   var jsonResult = $.trim(JSON.stringify(data));
           var obj = JSON.parse(jsonResult);
           nomeArquivo = obj.nomeArquivo;

           if (nomeArquivo == "erro"){
        	   // Se não tem o arquivo setTimeout neste processo por 5 segundos e depois executa de novo 
        	   setTimeout(verificarRelatorio, 5000); // a cada 5 segundos 
        	   return;
           } else {
	        	 // Se tem o arquivo faz o download do mesmo.
         	 	 $('#modalExportarResultado').modal('hide');
	      	     postData("../v3/downloadArquivo?arquivo_nome=" + nomeArquivo + "&subtipo=eventos_totem&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>");
	        }
        } catch(err) {
     	   console.log(err);
        } 
  	}, "json");
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>