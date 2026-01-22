<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.parametros.ReservarMargemParametros"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<% 
AcessoSistema _responsavel = JspHelper.getAcessoSistema(request); 

Boolean _validaMargemAvancado = (Boolean) request.getAttribute("validaMargemAvancado");
Boolean _validaTaxaAvancado = (Boolean) request.getAttribute("validaTaxaAvancado");
Boolean _validaPrazoAvancado = (Boolean) request.getAttribute("validaPrazoAvancado");
Boolean _validaDadosBancariosAvancado = (Boolean) request.getAttribute("validaDadosBancariosAvancado");
Boolean _validaSenhaServidorAvancado = (Boolean) request.getAttribute("validaSenhaServidorAvancado");
Boolean _validaBloqSerCnvCsaAvancado = (Boolean) request.getAttribute("validaBloqSerCnvCsaAvancado");
Boolean _validaDataNascAvancado = (Boolean) request.getAttribute("validaDataNascAvancado");
Boolean _validaLimiteAdeAvancado = (Boolean) request.getAttribute("validaLimiteAdeAvancado");
List<TransferObject> lstTipoJustica = (List<TransferObject>) request.getAttribute("lstTipoJustica");

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

Boolean _exibirAnexo = (Boolean) request.getAttribute("exibirAnexo");
Boolean _anexoObrigatorio = (Boolean) request.getAttribute("anexoObrigatorio");
%>
<div class="opcoes-avancadas">
  <a class="opcoes-avancadas-head" href="#faq1" data-bs-toggle="collapse" aria-expanded="false" aria-controls="faq1" aria-label='<hl:message key="mensagem.inclusao.avancada.clique.aqui"/>'><hl:message key="rotulo.avancada.opcoes"/></a>
  <div class="collapse" id="faq1">
    <div class="opcoes-avancadas-body">
      <div class="row">
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="validaMargemReserva" id="divValidaMargem">
          <div class="form-group my-0">
            <span id="validaMargemReserva"><hl:message key="rotulo.inclusao.avancada.validaMargem"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaMargem" id="validaMargem_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaMargem"/>' for="validaMargem_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaMargem" id="validaMargem_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaMargem"/>' for="validaMargem_Nao"><hl:message key="rotulo.nao"/></label>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="cetReserva" id="divValidaTaxaJuros">
          <div class="form-group my-0">
            <span id="cetReserva"><hl:message key="rotulo.inclusao.avancada.validaTaxaJuros"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaTaxa" id="validaTaxa_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaTaxaJuros"/>' for="validaTaxa_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaTaxa" id="validaTaxa_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaTaxaJuros"/>' for="validaTaxa_Nao"><hl:message key="rotulo.nao"/></label>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="prazoReserva" id="divValidaPrazo">
          <div class="form-group my-0">
            <span id="prazoReserva"><hl:message key="rotulo.inclusao.avancada.validaPrazo"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaPrazo" id="validaPrazo_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaPrazo"/>' for="validaPrazo_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaPrazo" id="validaPrazo_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaPrazo"/>' for="validaPrazo_Nao"><hl:message key="rotulo.nao"/></label>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="dadosBancarios" id="divValidaDadosBancarios">
          <div class="form-group my-0">
            <span id="dadosBancarios"><hl:message key="rotulo.inclusao.avancada.validaDadosBancarios"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaDadosBancarios" id="validaDadosBancarios_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaDadosBancarios"/>' for="validaDadosBancarios_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaDadosBancarios" id="validaDadosBancarios_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaDadosBancarios"/>' for="validaDadosBancarios_Nao"><hl:message key="rotulo.nao"/></label>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="senhaServidor" id="divValidaSenhaServidor">
          <div class="form-group my-0">
            <span id="senhaServidor"><hl:message key="rotulo.inclusao.avancada.validaSenhaServidor"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaSenhaServidor" id="validaSenhaServidor_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaSenhaServidor"/>' for="validaSenhaServidor_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaSenhaServidor" id="validaSenhaServidor_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaSenhaServidor"/>' for="validaSenhaServidor_Nao"><hl:message key="rotulo.nao"/></label>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="bloqueioServidor" id="divValidaBloqSerCnvCsa">
          <div class="form-group my-0">
            <span id="bloqueioServidor"><hl:message key="rotulo.inclusao.avancada.validaBloqSerCnvCsa"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaBloqSerCnvCsa" id="validaBloqSerCnvCsa_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaBloqSerCnvCsa"/>' for="validaBloqSerCnvCsa_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaBloqSerCnvCsa" id="validaBloqSerCnvCsa_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaBloqSerCnvCsa"/>' for="validaBloqSerCnvCsa_Nao"><hl:message key="rotulo.nao"/></label>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="validaDataNascimento" id="divValidaDataNascimento">
          <div class="form-group my-0">
            <span id="validaDataNascimento"><hl:message key="rotulo.inclusao.avancada.validaDataNascimento"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaDataNascimento" id="validaDataNascimento_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaDataNascimento"/>' for="validaDataNascimento_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaDataNascimento" id="validaDataNascimento_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaDataNascimento"/>' for="validaDataNascimento_Nao"><hl:message key="rotulo.nao"/></label>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="validaLimiteContrato" id="divValidaLimiteAde">
          <div class="form-group my-0">
            <span id="validaLimiteContrato"><hl:message key="rotulo.inclusao.avancada.validaLimiteAde"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaLimiteAde" id="validaLimiteAde_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaLimiteAde"/>' for="validaLimiteAde_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaLimiteAde" id="validaLimiteAde_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaLimiteAde"/>' for="validaLimiteAde_Nao"><hl:message key="rotulo.nao"/></label>
          </div>
        </div>
        <div class="form-group col-sm-12 mb-2">
            <label for="tmoCodigo"><hl:message key="rotulo.efetiva.acao.consignacao.dados.tipo.mtv.cancelamento"/></label>
            <hl:htmlcombo listName="lstMtvOperacao" di="tmoCodigo" name="tmoCodigo" fieldValue="<%=Columns.TMO_CODIGO%>" fieldLabel="<%=Columns.TMO_DESCRICAO%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel)%>' autoSelect="true" classe="form-control form-select"/>
        </div>
        <div class="form-group col-sm-12">
           <label for="adeObs"><hl:message key="rotulo.efetiva.acao.consignacao.dados.observacao"/></label>
           <textarea id="adeObs" name="adeObs" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs",_responsavel)%>' class="form-control" rows="6" onFocus="SetarEventoMascaraV4(this,'#*10000',true);" onBlur="fout(this);ValidaMascaraV4(this);"></textarea>
        </div>
        <fieldset>
          <h3 class="legend"><span><hl:message key="rotulo.avancada.decisao.judicial.titulo"/></span></h3>
        
          <div class="row pl-3">
          	<% if (exibirTipoJustica) { %>
            <div class="form-group col-sm-6">
              <label for="tjuCodigo"><hl:message key="rotulo.avancada.decisao.judicial.tipo.justica"/></label>
              <%=JspHelper.geraCombo(lstTipoJustica, "tjuCodigo", Columns.TJU_CODIGO, Columns.TJU_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel), "class=\"form-control form-select\"", true, 1, JspHelper.verificaVarQryStr(request, "tjuCodigo"), false)%>
            </div>
            <% } %>
            <% if (exibirComarcaJustica) { %>
            <div class="form-group col-sm-6">
              <label for="djuEstado"><hl:message key="rotulo.avancada.decisao.judicial.estado"/></label>
              <%= JspHelper.geraComboUF("djuEstado", "djuEstado", JspHelper.verificaVarQryStr(request, "djuEstado"), false, "form-control", _responsavel) %>
            </div>
            <div class="form-group col-sm-6">
              <label for="djuComarca"><hl:message key="rotulo.avancada.decisao.judicial.comarca"/></label>
              <hl:htmlinput name="djuComarca" di="djuComarca" type="text" classe="form-control" size="40" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "djuComarca"))%>"/>
              <hl:htmlinput name="cidCodigo" di="cidCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "cidCodigo"))%>" />
            </div>
            <%} %>
            <% if (exibirNumeroProcesso) { %>
            <div class="form-group col-sm-6">
              <label for="djuNumProcesso"><hl:message key="rotulo.avancada.decisao.judicial.numero.processo"/></label>
              <hl:htmlinput name="djuNumProcesso" di="djuNumProcesso" type="text" classe="form-control" size="40" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "djuNumProcesso"))%>"/>
            </div>
            <%} %>
            <% if (exibirDataDecisao) { %>
            <div class="form-group col-sm-6">
              <label for="djuData"><hl:message key="rotulo.avancada.decisao.judicial.data"/></label>
              <hl:htmlinput name="djuData" di="djuData" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "djuData"))%>"/>
            </div>
            <%} %>
            <% if (exibirTextoDecisao) { %>
            <div class="form-group col-sm-12">
              <label for="djuTexto"><hl:message key="rotulo.avancada.decisao.judicial.texto"/></label>
                <textarea name="djuTexto" id="djuTexto" class="form-control" cols="32" rows="5" onFocus="SetarEventoMascaraV4(this,'#*10000',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                	<% if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "djuTexto"))) { %>
                		<%=TextHelper.forHtmlAttribute(java.net.URLDecoder.decode(JspHelper.verificaVarQryStr(request, "djuTexto"), "UTF-8"))%>
                	<%} %>
                </textarea>
            </div>
            <%} %>
            <% if (_exibirAnexo) {%>
            	<hl:fileUploadV4 tipoArquivo="anexo_consignacao"/>
            <% } %>
          </div>
        </fieldset>
      </div>
    </div>
   </div>
</div>
<script language="JavaScript" type="text/JavaScript">
function validarOpcoesAvancadas() {
  console.log(<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "djuTexto"))%>);
  if (verificarOpcoesAvancadasAlteradas()) {
		var ControlesAvancados = new Array("tmoCodigo", "adeObs");
	    var MsgsAvancadas = new Array('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>','<hl:message key="mensagem.informe.observacao"/>');
	    if (!ValidaCampos(ControlesAvancados, MsgsAvancadas)) {
	      return false;
	    }
  }
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

  if (<%=_anexoObrigatorio%>) {
	  if (document.getElementById('FILE1').value == '') {
          alert('<hl:message key="mensagem.informe.tju.anexo"/>');
          return false;
    }
  }

  if (!ValidaCampos(Controles, Msgs)) {      
     return false;
  }

  return true;
}

function verificarOpcoesAvancadasAlteradas() {  
    var validaMargem = getCheckedRadio('form1', 'validaMargem');    
    var validaTaxaJuros = getCheckedRadio('form1', 'validaTaxa');
    var validaPrazo = getCheckedRadio('form1', 'validaPrazo');
    var validaDadosBancarios = getCheckedRadio('form1', 'validaDadosBancarios');
    var validaSenhaServidor = getCheckedRadio('form1', 'validaSenhaServidor');
    var validaBloqSerCnvCsa = getCheckedRadio('form1', 'validaBloqSerCnvCsa');
    var validaDataNascimento = getCheckedRadio('form1', 'validaDataNascimento');
    var validaLimiteAde = getCheckedRadio('form1', 'validaLimiteAde');
    
    return true;
}
</script>
<script src="<c:url value='/js/jquery-3.6.1.js'/>?<hl:message key='release.tag'/>"></script>
<script language="JavaScript" type="text/JavaScript">
$(document).ready(function() {
    $(function() {
        $("#djuComarca").autocomplete({
            source: function(request, response) {
            $.ajax({
            url: "../v3/listarCidades",
            type: "POST",
            dataType: "json",
            data: { "acao" : "reservarConsignacao", "name": request.term , "ufCod" : $("#djuEstado").val() },
                success: function( data ) {
                    response( $.map( data, function( item ) {
                    return {
                      label: item.atributos['<%=Columns.CID_NOME%>'] + ' - ' + item.atributos['<%=Columns.CID_UF_CODIGO%>'],
                      value: item.atributos['<%=Columns.CID_NOME%>'] + ' - ' + item.atributos['<%=Columns.CID_UF_CODIGO%>'],
                      value2: item.atributos['<%=Columns.CID_CODIGO%>'],
                    }
                    }));
                },
                error: function (error) {
                    $("[name='cidCodigo']").val("");       
                }
            });
            },
            select: function( event, ui ) {
               $("#djuComarca").val(ui.item.label);
               $("[name='cidCodigo']").val(ui.item.value2);
               return false;
            },
            minLength: 3
        });
    });
   		const validaMargem_Nao = document.getElementById("validaMargem_Nao");
   		const validaTaxa_Nao = document.getElementById("validaTaxa_Nao");
   		const validaPrazo_Nao = document.getElementById("validaPrazo_Nao");
   		const validaDadosBancarios_Nao = document.getElementById("validaDadosBancarios_Nao");
   		const validaSenhaServidor_Nao = document.getElementById("validaSenhaServidor_Nao");
   		const validaBloqSerCnvCsa_Nao = document.getElementById("validaBloqSerCnvCsa_Nao");
   		const validaDataNascimento_Nao = document.getElementById("validaDataNascimento_Nao");
   		const validaLimiteAde_Nao = document.getElementById("validaLimiteAde_Nao");

   		if (validaMargem_Nao) {
   			validaMargem_Nao.checked = true;
   			document.getElementById("divValidaMargem").style.display = "none";
   	    }

   		if (validaTaxa_Nao) {
   			validaTaxa_Nao.checked = true;
   			document.getElementById("divValidaTaxaJuros").style.display = "none";
   	    }

   		if (validaPrazo_Nao) {
   			validaPrazo_Nao.checked = true;
   			document.getElementById("divValidaPrazo").style.display = "none";
   	    }

   		if (validaDadosBancarios_Nao) {
   			validaDadosBancarios_Nao.checked = true;
   			document.getElementById("divValidaDadosBancarios").style.display = "none";
   	    }

   		if (validaSenhaServidor_Nao) {
   			validaSenhaServidor_Nao.checked = true;
   			document.getElementById("divValidaSenhaServidor").style.display = "none";
   	    }

   		if (validaBloqSerCnvCsa_Nao) {
   			validaBloqSerCnvCsa_Nao.checked = true;
   			document.getElementById("divValidaBloqSerCnvCsa").style.display = "none";
   	    }

   		if (validaDataNascimento_Nao) {
   			validaDataNascimento_Nao.checked = true;
   			document.getElementById("divValidaDataNascimento").style.display = "none";
   	    }

   		if (validaLimiteAde_Nao) {
   			validaLimiteAde_Nao.checked = true;
   			document.getElementById("divValidaLimiteAde").style.display = "none";
   	    }
   		$('.collapse').collapse()
});
</script>