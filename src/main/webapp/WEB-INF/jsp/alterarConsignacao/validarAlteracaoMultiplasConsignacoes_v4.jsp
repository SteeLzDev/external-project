<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> autdesList = (List<TransferObject>) request.getAttribute("autdesList");
BigDecimal totalAtual = (BigDecimal) request.getAttribute("totalAtual");
BigDecimal totalNovo = (BigDecimal) request.getAttribute("totalNovo");
List<TransferObject> lstMtvOperacao = (List<TransferObject>) request.getAttribute("lstMtvOperacao");
List<TransferObject> lstTipoJustica = (List<TransferObject>) request.getAttribute("lstTipoJustica");

// Exibição e obrigatoriedade campos decisão judicial
Boolean exibirTipoJustica = (Boolean) request.getAttribute("exibirTipoJustica");
Boolean tipoJusticaObrigatorio = (Boolean) request.getAttribute("tipoJusticaObrigatorio");

Boolean exibirComarcaJustica = (Boolean) request.getAttribute("exibirComarcaJustica");
Boolean comarcaJusticaObrigatorio = (Boolean) request.getAttribute("comarcaJusticaObrigatorio");

Boolean exibirNumeroProcesso = (Boolean) request.getAttribute("exibirNumeroProcesso");
Boolean numeroProcessoObrigatorio = (Boolean) request.getAttribute("numeroProcessoObrigatorio");

Boolean exibirDataDecisao = (Boolean) request.getAttribute("exibirDataDecisao");
Boolean dataDecisaoObrigatorio = (Boolean) request.getAttribute("dataDecisaoObrigatorio");

Boolean exibirTextoDecisao = (Boolean) request.getAttribute("exibirTextoDecisao");
Boolean textoDecisaoObrigatorio = (Boolean) request.getAttribute("textoDecisaoObrigatorio");

Boolean exibirAnexo = (Boolean) request.getAttribute("exibirAnexo");
Boolean anexoObrigatorio = (Boolean) request.getAttribute("anexoObrigatorio");

String autdesListRaw = (String) request.getAttribute("autdesListRaw");
String parametros = (String) request.getAttribute("parametros");
List<String> adeListNaoSelecionadas = (List<String>) request.getAttribute("adeListNaoSelecionadas");

Boolean exibeAdequarMargem = (Boolean) request.getAttribute("exibeAdequarMargem");

String tmoCod = "";
%>
<c:set var="title">
<hl:message key="rotulo.alterar.multiplo.consignacao.titulo"/>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form action="../v3/alterarMultiplasConsignacoes" method="post" name="form1">
    <input type="hidden" name="autdesListRaw" value="<%=TextHelper.forHtmlAttribute(autdesListRaw)%>" />
    <input type="hidden" name="parametros" value="<%=parametros%>" />
  
    <div class="card">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title"><hl:message key="mensagem.alterar.multiplo.consignacao.resumo.operacao"/></h2>
      </div>
      <div class="card-body">
        <dl class="row data-list">
          <hl:detalharServidorv4 name="servidor" scope="request"/>
        </dl>   
      </div>
    </div>

    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon">
          <svg width="24">
              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consignacao"></use>
          </svg>
        </span>
        <h2 class="card-header-title">
           <hl:message key="mensagem.alterar.multiplo.consignacao.lista.consignacao.titulo"/>
        </h2>
      </div>
      <div class="card-body table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
              <th scope="col"><hl:message key="rotulo.servico.singular"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.atual"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.novo"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.prazo.restante.atual"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.prazo.restante.novo"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.valor.ultima.parcela"/></th>
            </tr>
          </thead>
          <tbody>
            <%=JspHelper.msgRstVazio(autdesList.size() == 0, 13, responsavel)%>
            
            <% for (TransferObject ade : autdesList) { 
            	String adeCodigo = (String) ade.getAttribute(Columns.ADE_CODIGO);
            	if (adeListNaoSelecionadas != null && !adeListNaoSelecionadas.isEmpty() && adeListNaoSelecionadas.contains(adeCodigo)) {
                	continue;
                } 
            %>
             <tr class="selecionarLinha">
               <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA))%></td>
               <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO))%></td>
               <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO))%></td>
               <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA))%></td>
               <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA_NOVO))%></td>
               <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO))%></td>
               <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO_NOVO))%></td>
               <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_ULTIMA_PARCELA))%></td>
             </tr>
            <% } %>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="13">
                <hl:message key="mensagem.info.alterar.multiplo.consignacao.lista.consignacao" /> 
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
      <div class="card-footer">
      </div>
    </div>

    <div class="card d-print-none">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="mensagem.alterar.multiplo.consignacao.informe.valores"/></h2>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="form-group col-md-6">
            <label for="totalAtual"><hl:message key="rotulo.alterar.multiplo.consignacao.valor.total.atual"/> (<hl:message key="rotulo.moeda"/>)</label>
            <input type="text" class="form-control" id="totalAtual" name="totalAtual" value="<%=NumberHelper.format(totalAtual.doubleValue(), LocaleHelper.getLanguage())%>" disabled/>
          </div>

          <div class="form-group col-md-6">
            <label for="totalNovo"><hl:message key="rotulo.alterar.multiplo.consignacao.valor.total.novo.curto"/> (<hl:message key="rotulo.moeda"/>)</label>
            <input type="text" class="form-control" id="totalNovo" name="totalNovo" value="<%=NumberHelper.format(totalNovo.doubleValue(), LocaleHelper.getLanguage())%>" disabled/>
          </div>

          <div class="form-group col-md-12 mt-2">
            <label for="tmoCodigo"><hl:message key="rotulo.avancada.tmoCodigo"/></label>
            <%=JspHelper.geraCombo(lstMtvOperacao, "tmoCodigo", Columns.TMO_CODIGO, Columns.TMO_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\" onChange=\"mostrarCheckbox()\"", false, 1)%>
          </div>
          
          <% if (exibeAdequarMargem) { %>
          <div class="form-group col-md-12 mt-2" id="divAdequarMargem">
	          <input class="form-check-input ml-4" id="adequarMargem" name="adequarMargem" type="checkbox" value="S" checked>
	          <label class="form-check-label ml-4" for="adequarMargem">
	            <span class="text-nowrap align-text-top font-weight-bold"><hl:message key="rotulo.alterar.multiplo.consignacao.adequar.margem.servidor"/></span>
	          </label>
          </div>
          <% } %>

          <div class="form-group col-md-12 mt-2">
            <label for="adeObs"><hl:message key="rotulo.avancada.adeObs"/></label>
            <textarea class="form-control" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs",responsavel)%>' id="adeObs" name="adeObs" rows="6" onFocus="SetarEventoMascara(this,'#*10000',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>
          </div>

          <% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) { %>
              <div class="card-header col-md-12 mt-2">
                <h2 class="card-header-title"><hl:message key="rotulo.avancada.decisao.judicial.titulo"/></h2>
              </div>

              <% if (exibirTipoJustica) { %>
              <div class="form-check form-group col-md-12 mt-2">
                <label for="tjuCodigo"><hl:message key="rotulo.avancada.decisao.judicial.tipo.justica"/></label>
                <%=JspHelper.geraCombo(lstTipoJustica, "tjuCodigo", Columns.TJU_CODIGO, Columns.TJU_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\"")%>
              </div>
              <% } %>
    
              <% if (exibirComarcaJustica) { %>
              <div class="form-check form-group col-md-12 mt-2">
                <label for="djuEstado"><hl:message key="rotulo.avancada.decisao.judicial.estado"/></label>
                <%= JspHelper.geraComboUF("djuEstado", "djuEstado", "", false, "form-control", responsavel) %>
              </div>
    
              <div class="form-check form-group col-md-12 mt-2">
                <label for="djuComarca"><hl:message key="rotulo.avancada.decisao.judicial.comarca"/></label>
                <select name="djuComarca" id="djuComarca" class="form-control"></select>
                <hl:htmlinput name="cidCodigo" di="cidCodigo" type="hidden" />
              </div>
              <% } %>
    
              <% if (exibirNumeroProcesso) { %>
              <div class="form-check form-group col-md-12 mt-2">
                <label for="djuNumProcesso"><hl:message key="rotulo.avancada.decisao.judicial.numero.processo"/></label>
                <hl:htmlinput name="djuNumProcesso" di="djuNumProcesso" type="text" classe="form-control" size="40"/>
              </div>
              <% } %>
    
              <% if (exibirDataDecisao) { %>
              <div class="form-check form-group col-md-12 mt-2">
                <label for="djuData"><hl:message key="rotulo.avancada.decisao.judicial.data"/></label>
                <hl:htmlinput name="djuData" di="djuData" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>"/>
              </div>
              <% } %>
    
              <% if (exibirTextoDecisao) { %>
              <div class="form-check form-group col-md-12 mt-2">
                <label for="djuTexto"><hl:message key="rotulo.avancada.decisao.judicial.texto"/></label>
                <textarea name="djuTexto" id="djuTexto" class="form-control" cols="32" rows="5" onFocus="SetarEventoMascara(this,'#*10000',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>
              </div>
              <% } %>
          <% } %>

        </div>
      </div>
    </div>
    <%out.print(SynchronizerToken.generateHtmlToken(request));%>     
    <hl:htmlinput name="acao"  type="hidden" di="acao" value="salvar" /> 
    <div class="btn-action d-print-none">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      <a class="btn btn-primary" href="#no-back" id="aplicar" onClick="alterarContratos(); return false;"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg><hl:message key="rotulo.botao.aplicar"/></a>
    </div>
  </form>
</body>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
f0 = document.forms[0];
<% if (exibeAdequarMargem) { %>
$("#divAdequarMargem").hide();
<% } %>
</script>
<script type="text/JavaScript">
function alterarContratos() {
	var ControleMotivoOperacao = new Array("tmoCodigo", "adeObs");
    var Msgs = new Array('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>',
            			 '<hl:message key="mensagem.informe.observacao"/>');

    if (!ValidaCampos(ControleMotivoOperacao, Msgs)) {
      return false;
    }

    f0.submit();
}
</script>
<script>
function mostrarCheckbox() {
	<% if (exibeAdequarMargem) { %>
		var tmoCodigo = document.getElementById("tmoCodigo").value;
		var adequarMargem = document.getElementById("adequarMargem");
		<%Iterator<TransferObject> it = lstMtvOperacao.iterator();
	  	while (it.hasNext()) {
	      TransferObject tmo = it.next();%>
	      if(tmoCodigo == '<%=tmo.getAttribute(Columns.TMO_CODIGO)%>'){
	      	if('<%=tmo.getAttribute(Columns.TMO_DECISAO_JUDICIAL)%>' == "S"){
	      		$("#divAdequarMargem").show();
	      		adequarMargem.value = "S";
	      		adequarMargem.checked = true;
	      	} else {
	      		$("#divAdequarMargem").hide();
	      		adequarMargem.value = "N";
	      		adequarMargem.checked = false;
	      	}
	      }
	  <%}%>
  <%}%>
}
</script>
<% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) { %>
<script type="text/JavaScript">
$(document).ready(function() {
    document.getElementById('djuEstado').setAttribute("onchange", "listarCidades(this.value)");
    document.getElementById('djuComarca').setAttribute("onchange", "setCidCodigo(this.value)");
});

function listarCidades(codEstado) {
      if (!codEstado) {
        document.getElementById('djuComarca').innerText = "";
        $("[name='cidCodigo']").val("");        
          return;
      } else {  
        $.ajax({  
          type : 'post',
          url : "../v3/listarCidades?acao=alteracaoMultiplosAdes&codEstado=" + codEstado + "&_skip_history_=true",
          async : true,
          contentType : 'application/json',         
          success : function(data) {
              var options = "<option value>" + "<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>" + "</option> ";
              var cidades = null;
              var nomeCidade = null;
              var codigoCidade = null;               

              data.forEach(function(objeto) {
                codigoCidade = objeto.atributos['<%=Columns.CID_CODIGO_IBGE%>'];
                nomeCidade = objeto.atributos['<%=Columns.CID_NOME%>'];
                options = options.concat('<option value="').concat(objeto.atributos['<%=Columns.CID_CODIGO%>']).concat('">').concat(nomeCidade).concat('</option>');                  
              });
              
              document.getElementById('djuComarca').innerHTML = options;              
          },
          error: function (response) {
                console.log(response.statusText);
          }
        });
      }
  }

  function setCidCodigo(cidCodigo) {
    $("[name='cidCodigo']").val(cidCodigo);
  }
</script>
<% } %>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>