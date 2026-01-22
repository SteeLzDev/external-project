<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession"   scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> autdesList = (List<TransferObject>) request.getAttribute("lstConsignacao");
boolean exigeSenhaServidor = TextHelper.isNull(request.getAttribute("exigeSenhaServidor")) ? false : (boolean) request.getAttribute("exigeSenhaServidor");
Boolean temPermissaoAnexarReativar = (Boolean) request.getAttribute("temPermissaoAnexarReativar");
Boolean margemFicaraNegativa = (Boolean) request.getAttribute("margemFicaraNegativa");
Boolean corrigeIncidenciaMargem = (Boolean) request.getAttribute("corrigeIncidenciaMargem");
Set<Date> periodos = (Set<Date>) request.getAttribute("periodos");
String termoAceite = (String) request.getAttribute("termoAceite");
List<TransferObject> lstTipoJustica = (List<TransferObject>) request.getAttribute("lstTipoJustica");
Boolean exibeChkIncidirNaMargem = (Boolean) request.getAttribute("exibeChkIncidirNaMargem");
String descricaoMargemDestino = (String) request.getAttribute("descricaoMargemDestino");

//Exibição e obrigatoriedade campos decisão judicial
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

String adeCodigosSuspensoRejeitadaFolha = !TextHelper.isNull(request.getAttribute("adesParcelaRejeitada")) ? (String) request.getAttribute("adesParcelaRejeitada") : "";
%>
<c:set var="title">
  <hl:message key="rotulo.efetiva.acao.consignacao.confReativacao"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
<form action="<%=TextHelper.forHtmlAttribute(SynchronizerToken.updateTokenInURL(request.getAttribute("acaoFormulario") + "?acao=reativarConsignacao&_skip_history_=true", request))%>" method="post" name="form1" <%= temPermissaoAnexarReativar ? "ENCTYPE='multipart/form-data'" :"" %>>
    <% if (corrigeIncidenciaMargem) { %>
      <div class="alert alerta-checkbox" role="alert">
        <input class="form-check-input " type="checkbox" name="TERMO_ACEITE" id="TERMO_ACEITE" value="SIM">
        <label for="alerta" class="form-check-label font-weight-bold"><%=TextHelper.forHtmlContentComTags(termoAceite)%></label>
      </div>
    <% } %>
    <% if (margemFicaraNegativa) { %>
    <div class="alert alerta-checkbox" role="alert">
      <input class="form-check-input " type="checkbox" name="CHECK_MAR_NEGATIVA" id="CHECK_MAR_NEGATIVA" value="SIM">
      <label for="alerta" class="form-check-label font-weight-bold"><hl:message key="mensagem.margem.negativa.reativacao"/></label>
    </div>
    <% } %>
    <div class="row">
      <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
      <% pageContext.setAttribute("autdes", autdesList); %>
      <hl:detalharADEv4 name="autdes" table="false" type="alterar" />
      <%-- Fim dos dados da ADE --%>
    </div>
    <div class="col-sm p-0">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.efetiva.acao.consignacao.dados.operacao"/></h2>
        </div>
        <div class="card-body">
        <% if (exibeChkIncidirNaMargem) { %>
          <div class="form-group col-md-12 mt-2" id="divIncidirNaMargem">
	          <input class="form-check-input ml-4" id="incidirNaMargem" name="incidirNaMargem" type="checkbox" value="true" checked>
	          <label class="form-check-label ml-4" for="incidirNaMargem">
	            <span class="text-nowrap align-text-top font-weight-bold"><hl:message key="rotulo.suspensao.altera.contrato.para.incidir.na.margem" arg0="<%=descricaoMargemDestino%>"/></span>
	          </label>
          </div>
          <% } %>
        <% if (periodos != null && !periodos.isEmpty()) { %>
          <div class="row">
            <div class="form-group col-sm-12">
              <label for="TMO_CODIGO"><hl:message key="rotulo.folha.periodo"/></label>
              <select class="form-control form-select" name="OCA_PERIODO" id="OCA_PERIODO" onfocus="SetarEventoMascara(this,'#*100',true);" onblur="fout(this);ValidaMascara(this);" style="background-color: white; color: black;">
              <% for (Date periodo : periodos) { %>
                <option value="<%=TextHelper.forHtmlAttribute(periodo)%>"><%=TextHelper.forHtmlContent(DateHelper.toPeriodString(periodo))%></option>
              <% } %>
              </select>
            </div>
          </div>
        <% } %>
          <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
          <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.reativacao", responsavel)%>"/>
          <%-- Fim dos dados do Motivo da Operação --%>

          <% if (exigeSenhaServidor) { %>
          	<%if (!TextHelper.isNull(adeCodigosSuspensoRejeitadaFolha)) { %>
          	 	<hl:htmlinput name="adeCodigosSuspensoRejeitadaFolha" type="hidden" di="adeCodigosSuspensoRejeitadaFolha"  value="<%=TextHelper.forHtmlAttribute(adeCodigosSuspensoRejeitadaFolha)%>" />
	          	<div class="row">
	     			<div class="form-group col-md-12 mt-2" id="divConfRetornoDesconto">
			           <input class="form-check-input ml-4" id="confirmRetornoDesconto" name="confirmRetornoDesconto" type="checkbox" value="true">
			           <label class="form-check-label ml-4" for="confirmRetornoDesconto">
			           <span class="text-nowrap align-text-top font-weight-bold"><hl:message key="rotulo.reativar.consignacao.confirmacao.retorno.desconto.servidor"/></span>
			           </label>
	                </div>
	          	</div>
          	<%} %>
            <div class="row">
              <div class="form-group col-sm-6">
                <hl:senhaServidorv4 senhaObrigatoria="true"                                                 
                                    senhaParaAutorizacaoReserva="true"
                                    nomeCampoSenhaCriptografada="serAutorizacao"
                                    rseCodigo="<%=request.getAttribute("rseCodigo") != null ? request.getAttribute("rseCodigo").toString() : ""%>"
                                    svcCodigo="<%=request.getAttribute("svcCodigo") != null ? request.getAttribute("svcCodigo").toString() : ""%>"
                                    nf="btnEnvia"
                                    classe="form-control"
                                    inputSizeCSS="col-sm-12 px-1"
                                    separador2pontos="false"
                    />
              </div>
            </div>
          <% } %>

        <% if (temPermissaoAnexarReativar && exibirAnexo) {%>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="arquivo"><hl:message key="rotulo.efetiva.acao.consignacao.dados.arquivo"/><% if (!anexoObrigatorio) { %><hl:message key="rotulo.campo.opcional"/><% } %></label>
              <input type="file" class="form-control" id="FILE1" name="FILE1">
            </div>
          </div>
          <% if (responsavel.isCseSupOrg()) { %>
          <div class="row">
            <div class="form-group ml-3" aria-labelledby="visibilidade">
              <div class="form-check pt-2">
                <span id="visibilidade">
                  <hl:message key="rotulo.avancada.anexos.visibilidade"/>
                </span>
                <div class="col-sm-12 col-md-12">
                  <div class="text-nowrap align-text-top">
                    <input class="form-check-input ml-1" id="aadExibeSup" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_SUPORTE%>" checked>
                    <label class="form-check-label labelSemNegrito ml-1" for="aadExibeSup">
                      <hl:message key="rotulo.suporte.singular"/>
                    </label>
                  </div>
                  <div class="text-nowrap align-text-top">
                    <input class="form-check-input ml-1" id="aadExibeCse" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_CONSIGNANTE%>" checked>
                    <label class="form-check-label labelSemNegrito ml-1" for="aadExibeCse">
                      <hl:message key="rotulo.consignante.singular"/>
                    </label>
                  </div>
                  <div class="text-nowrap align-text-top">
                    <input class="form-check-input ml-1" id="aadExibeOrg" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_ORGAO%>" checked>                               
                    <label class="form-check-label labelSemNegrito ml-1" for="aadExibeOrg">
                      <hl:message key="rotulo.orgao.singular"/>
                    </label>
                  </div>
                  <div class="text-nowrap align-text-top">
                   <input class="form-check-input ml-1" id="aadExibeCsa" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_CONSIGNATARIA%>" checked>
                    <label class="form-check-label labelSemNegrito ml-1" for="aadExibeCsa">
                      <hl:message key="rotulo.consignataria.singular"/>
                    </label>
                  </div>
                  <div class="text-nowrap align-text-top">
                   <input class="form-check-input ml-1" id="aadExibeCor" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_CORRESPONDENTE%>" checked>
                    <label class="form-check-label labelSemNegrito ml-1" for="aadExibeCor">
                      <hl:message key="rotulo.correspondente.singular"/>
                    </label>
                  </div>
                  <div class="text-nowrap align-text-top">
                   <input class="form-check-input ml-1" id="aadExibeSer" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_SERVIDOR%>" checked>
                    <label class="form-check-label labelSemNegrito ml-1" for="aadExibeSer">
                      <hl:message key="rotulo.servidor.singular"/>
                    </label>
                  </div>
                  <div class="text-nowrap align-text-top">
                   <input class="form-check-input ml-1" id="checkTodos"  type="checkbox" onclick="!this.checked ? uncheckAll(f0, 'aadExibe') : checkAll(f0, 'aadExibe')" value="S" checked>
                    <label class="form-check-label labelSemNegrito ml-1" for="checkTodos">
                      <hl:message key="rotulo.campo.todos.simples"/>
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <% } %>
        <% } %>
        </div>
      </div>
      
      <% if ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) { %>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <hl:message key="rotulo.avancada.decisao.judicial.titulo"/>
          </h2>
        </div>
        <div class="card-body">
          <div class="row">
            <% if (exibirTipoJustica) { %>
            <div class="form-check form-group col-md-4 mt-2">
              <label for="tjuCodigo"><hl:message key="rotulo.avancada.decisao.judicial.tipo.justica"/></label>
              <%=JspHelper.geraCombo(lstTipoJustica, "tjuCodigo", Columns.TJU_CODIGO, Columns.TJU_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control form-select\"")%>
            </div>
            <% } %>
            <% if (exibirComarcaJustica) { %>
            <div class="form-check form-group col-md-2 mt-2">
              <label for="djuEstado"><hl:message key="rotulo.avancada.decisao.judicial.estado"/></label>
              <%= JspHelper.geraComboUF("djuEstado", "djuEstado", "", false, "form-control", responsavel) %>
            </div>
            <div class="form-check form-group col-md-6 mt-2">
              <label for="djuComarca"><hl:message key="rotulo.avancada.decisao.judicial.comarca"/></label>
              <select name="djuComarca" id="djuComarca" class="form-control form-select"></select>
              <hl:htmlinput name="cidCodigo" di="cidCodigo" type="hidden" />
            </div>
            <% } %>
          </div>
          <div class="row">
            <% if (exibirNumeroProcesso) { %>
            <div class="form-check form-group col-md-8 mt-2">
              <label for="djuNumProcesso"><hl:message key="rotulo.avancada.decisao.judicial.numero.processo"/></label>
              <hl:htmlinput name="djuNumProcesso" di="djuNumProcesso" type="text" classe="form-control" size="40"/>
            </div>
            <% } %>
            <% if (exibirDataDecisao) { %>
            <div class="form-check form-group col-md-4 mt-2">
              <label for="djuData"><hl:message key="rotulo.avancada.decisao.judicial.data"/></label>
              <hl:htmlinput name="djuData" di="djuData" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>"/>
            </div>
            <% } %>
          </div>
          <% if (exibirTextoDecisao) { %>
          <div class="row">
            <div class="form-check form-group col-md-12 mt-2">
              <label for="djuTexto"><hl:message key="rotulo.avancada.decisao.judicial.texto"/></label>
              <textarea name="djuTexto" id="djuTexto" class="form-control" cols="32" rows="5" onFocus="SetarEventoMascara(this,'#*10000',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>
            </div>
          </div>
          <% } %>
        </div>
      </div>
      <% } %>
      
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" data-bs-dismiss="modal" data-bs-toggle="modal" href="#confirmarSenha" onclick="if (vfChecks() <% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) { %> && verificaCamposTju() <% } %>) { enviarRequisicao();} return false;">
          <svg width="17">
              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use>
          </svg>
          <hl:message key="rotulo.botao.confirmar"/>
        </a>
      </div>
      <% for (int i = 0; i < autdesList.size(); i++) { %>
        <hl:htmlinput name="chkReativar" type="hidden" value="<%=TextHelper.forHtmlAttribute(autdesList.get(i).getAttribute(Columns.ADE_CODIGO))%>"/>
      <% } %>
    </div>
</form>
</c:set>
<c:set var="javascript">
<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" scriptOnly="true" />
<% if (exigeSenhaServidor) { %>
<hl:senhaServidorv4 senhaObrigatoria="true"                                                 
                    senhaParaAutorizacaoReserva="true"
                    nomeCampoSenhaCriptografada="serAutorizacao"
                    rseCodigo="<%=request.getAttribute("rseCodigo") != null ? request.getAttribute("rseCodigo").toString() : ""%>"
                    svcCodigo="<%=request.getAttribute("svcCodigo") != null ? request.getAttribute("svcCodigo").toString() : ""%>"
                    scriptOnly="true"
    />
<% } %>
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
        url : "../v3/listarCidades?acao=<%=request.getAttribute("acaoListarCidades")%>&codEstado=" + codEstado + "&_skip_history_=true",
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
 
f0 = document.forms[0];

function enviarRequisicao() {
    if (f0.senha != null && trim(f0.senha.value) != '') {
        CriptografaSenha(f0.senha, f0.serAutorizacao, false);
    }
    f0.submit();
}

function vfChecks() {
	var chkTermoAceite = f0.TERMO_ACEITE;
	
	if (chkTermoAceite != undefined && !chkTermoAceite.checked) {
		alert('<hl:message key="mensagem.reativar.consignacao.confirmar.condicoes"/>');
		return false;
	}
	
	<% if (margemFicaraNegativa) { %>
	    var chkMarNegativa = f0.CHECK_MAR_NEGATIVA;
	    
	    if (!chkMarNegativa.checked) {
	    	alert('<hl:message key="mensagem.reativar.consignacao.confirmar.condicoes"/>');
			return false;
		}
	<% } %>
		
	var comboTMO = f0.TMO_CODIGO.value
	if (comboTMO == '' || comboTMO == null) {
	  alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
	  f0.TMO_CODIGO.focus();
	  return false;
	}

	if (!confirmaAcaoConsignacao()) {
        return false;
    }

	<% if (exigeSenhaServidor && !TextHelper.isNull(adeCodigosSuspensoRejeitadaFolha)) { %>
		const chkConfirmaRetornoDesconto = f0.confirmRetornoDesconto;
	    
	    if (!chkConfirmaRetornoDesconto.checked) {
	    	alert('<hl:message key="mensagem.reativar.consignacao.confirmacao.retorno.desconto.servidor.obrigatorio"/>');
	    	chkConfirmaRetornoDesconto.focus();
			return false;
		}
	<%} %>

	<% if (exigeSenhaServidor) { %>
		if (f0.senha != null && f0.senha.value == '') {
		    alert("<hl:message key="mensagem.informe.ser.senha"/>");
		    f0.senha.focus();
		    return false;
		}
	<%} %>
	

    return true;
}

function verificaCamposTju() {
    var validaCamposTju = false;
    var Controles = new Array();
    var Msgs = new Array();
    
    // Verifica se algum campo foi informado, sendo assim, tipo de justiça é obrigatório
  	Controles = new Array("tjuCodigo", "djuEstado", "djuComarca", "djuNumProcesso", "djuData", "djuTexto");
    for (var j=0;j<Controles.length;j++) {
        var elementTju =  document.getElementById(Controles[j]);

        if (elementTju != null && (elementTju.value != null && elementTju.value != '')) {
            validaCamposTju = true;
            break;
        }
    }

    // Verifica campos obrigatórios na campo sistema
    Controles = new Array();
    if (<%=tipoJusticaObrigatorio%> || validaCamposTju) {
    	Controles.push("tjuCodigo");
    	Msgs.push('<hl:message key="mensagem.informe.tju.codigo"/>');
    }

    if (<%=comarcaJusticaObrigatorio%>) {
    	Controles.push("djuEstado");
    	Msgs.push('<hl:message key="mensagem.informe.tju.estado"/>');
    }

    if (<%=comarcaJusticaObrigatorio%>) {
    	Controles.push("djuComarca");
    	Msgs.push('<hl:message key="mensagem.informe.tju.comarca"/>');
    }

    if (<%=numeroProcessoObrigatorio%>) {
    	Controles.push("djuNumProcesso");
    	Msgs.push('<hl:message key="mensagem.informe.num.processo"/>');
    }

    if (<%=dataDecisaoObrigatorio%>) {
    	Controles.push("djuData");
    	Msgs.push('<hl:message key="mensagem.informe.tju.data"/>');
    }

    if (<%=textoDecisaoObrigatorio%>) {
    	Controles.push("djuTexto");
    	Msgs.push('<hl:message key="mensagem.informe.tju.texto"/>');
    }

    if (<%=anexoObrigatorio%>) {
    	Controles.push("FILE1");
    	Msgs.push('<hl:message key="mensagem.informe.tju.anexo"/>');
    }

    if (!ValidaCampos(Controles, Msgs)) {      
       return false;
    }

    return true;
}
<% if (exibeChkIncidirNaMargem) { %>
	function alteraCheckIncidencia() {
		var removeIncidenciaSim = document.getElementById("removeIncidenciaSim").checked;
		var divIncidirNaMargem = document.getElementById("divIncidirNaMargem").value;
		var incidirNaMargem = document.getElementById("incidirNaMargem");
	   	if(removeIncidenciaSim){
	   		$("#divIncidirNaMargem").hide();
	   		incidirNaMargem.value = "false";
	   		incidirNaMargem.checked = false;
	   	} else {
	   		$("#divIncidirNaMargem").show();
	   		incidirNaMargem.value = "true";
	   		incidirNaMargem.checked = true;
	   	}
	
	}
<% } %>

<% if (exigeSenhaServidor && !TextHelper.isNull(adeCodigosSuspensoRejeitadaFolha)) { %>
	document.addEventListener('DOMContentLoaded', function() {
		const inputSenha = document.getElementById('senha');
		inputSenha.disabled = true;
	    
	    const checkConfirmRetornoDesconto = document.getElementById('confirmRetornoDesconto');
	    checkConfirmRetornoDesconto.addEventListener('change', function() {
	    	inputSenha.disabled = !checkConfirmRetornoDesconto.checked;
	    });
	});
<%} %>
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>   