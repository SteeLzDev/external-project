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
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> autdesList = (List<TransferObject>) request.getAttribute("lstConsignacao");
Boolean exigeMotivoOperacao = (Boolean) request.getAttribute("exigeMotivoOperacao");
boolean exigeSenhaServidor = TextHelper.isNull(request.getAttribute("exigeSenhaServidor")) ? false : (boolean) request.getAttribute("exigeSenhaServidor");
Boolean temPermissaoSuspensaoAvancada = (Boolean) request.getAttribute("temPermissaoSuspensaoAvancada");
Boolean temPermissaoAnexarSuspensao = (Boolean) request.getAttribute("temPermissaoAnexarSuspensao");
Set<Date> periodos = (Set<Date>) request.getAttribute("periodos");
List<TransferObject> lstTipoJustica = (List<TransferObject>) request.getAttribute("lstTipoJustica");
Boolean exibeChkIncidirNaMargem = (Boolean) request.getAttribute("exibeChkIncidirNaMargem");
String descricaoMargemDestino = (String) request.getAttribute("descricaoMargemDestino");

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

%>
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
    var f0 = document.forms[0];

    function formLoad(){
        focusFirstField();
    }
    
    function enviarRequisicao() {
        if (f0.senha != null && trim(f0.senha.value) != '') {
            CriptografaSenha(f0.senha, f0.serAutorizacao, false);
        }
        f0.submit();
    }

    function vf_confirmar_suspensao() {
        var exigeMotivo = <%=exigeMotivoOperacao%>;
        var removeIncidencia = false;
        <% if (temPermissaoSuspensaoAvancada) { %>
        removeIncidencia = f0.removeIncidenciaMargem.value  == 'true';
        <% } %>

        var temDataReativacaoAut = false;
        if (f0.dataReativacaoAutomatica != null && f0.dataReativacaoAutomatica.value != '') {
            if (!verificaData(f0.dataReativacaoAutomatica.value)) {
                f0.dataReativacaoAutomatica.focus();
                return false;
            }
            var partesData = obtemPartesData(f0.dataReativacaoAutomatica.value);
            var dia = partesData[0];
            var mes = partesData[1];
            var ano = partesData[2];
            var dataReativacaoAut = new Date(ano, mes - 1, dia);
            dataReativacaoAut.setHours(0,0,0,0);
            var dataCorrente = new Date();
            dataCorrente.setHours(0,0,0,0);
            if (dataReativacaoAut.getTime() <= dataCorrente.getTime()) {
                f0.dataReativacaoAutomatica.focus();
                alert('<hl:message key="mensagem.erro.suspensao.data.reativacao.maior.hoje"/>');
                return false;
            }
            temDataReativacaoAut = true;
        }

        var motivo = f0.TMO_CODIGO;
        if (motivo != null && motivo.disabled == false && (exigeMotivo || removeIncidencia)) {
            if (motivo.value == '') {
                alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
                motivo.focus();
                return false;
            }
        }

        if (!confirmaAcaoConsignacao()) {
            return false;
        }

        return (!temDataReativacaoAut || confirm('<hl:message key="mensagem.aviso.suspensao.data.reativacao.previsao"/>')) && confirm('<hl:message key="mensagem.confirmacao.suspensao"/>');
    }
    
    function verificarAdesDataFim() {
    	
    	// Verifica se o campo existe na tela e está preenchido
    	if (!$('#dataReativacaoAutomatica').length || !$('#dataReativacaoAutomatica').val().length) {
    		return true;
    	}
    	
    	var msg = '<hl:message key="mensagem.suspender.consignacao.ades.prazo.final.contrato"/>\n\n';
    	
    	var adesCodigos = $('[name=chkSuspender]').map(function () {return this.value; }).get();
    	
    	var adesNumeros = [];
    	
    	$.ajax({
    		type: 'post',
    		url: "../v3/compararComDataFim?_skip_history_=true",
    		async : false,
    		data: JSON.stringify({
    			dataInformada: $('#dataReativacaoAutomatica').val(),
    			adeCodigos: adesCodigos
    		}),
    		contentType : 'application/json',
    		success : function(data) {
    			adesNumeros = data.entity;
    		},
    	});
    	
    	if (adesNumeros.length > 0) {
    		
    		msg += adesNumeros.join(",");
    	
    		if (confirm(msg)) {
    			return true;
    		} else {
    			alert('<hl:message key="mensagem.suspender.consignacao.ades.prazo.final.acao.cancelada"/>');
    			return false;
    		}
    	}
    	
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
  </script>
  <% } %>
<% if (exibeChkIncidirNaMargem) { %>
	<script>
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
	</script>
<% } %>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
  <%=TextHelper.forHtml(request.getAttribute("tituloPagina"))%>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="<%=TextHelper.forHtmlAttribute(SynchronizerToken.updateTokenInURL(request.getAttribute("acaoFormulario") + "?acao=suspenderConsignacao&_skip_history_=true", request))%>" name="formTmo" ENCTYPE='multipart/form-data'>
    <div class="row">
      <!--Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE -->
      <%pageContext.setAttribute("autdes", autdesList);%>
      <hl:detalharADEv4 name="autdes" table="false" type="alterar" />
      <!--Fim dos dados da ADE -->
    </div>
    <div class="col-sm p-0">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <hl:message key="rotulo.efetiva.acao.consignacao.dados.operacao" />
          </h2>
        </div>
        <div class="card-body">
          <% if (temPermissaoSuspensaoAvancada) { %>
          <div class="row">
            <div class="form-group ml-3" role="radiogroup" aria-labelledby="IncluirUsuariosDeSuporte">
              <div><span id="removeIncidencia">
                <hl:message key="rotulo.suspensao.avancada.remove.incidencia.margem" />
              </span></div>
              <div class="form-check form-check-inline pt-2">
                <input class="form-check-input ml-1" type="radio" name="removeIncidenciaMargem" id="removeIncidenciaSim" value="true" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" onclick="alteraCheckIncidencia();">
                <label class="form-check-label labelSemNegrito pr-4" for="removeIncidenciaSim">
                  <hl:message key="rotulo.sim" />
                </label>
              </div>
              <div class="form-check form-check-inline pt-2">
              <input class="form-check-input ml-1" type="radio" name="removeIncidenciaMargem" id="removeIncidenciaNao" value="false" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" onclick="alteraCheckIncidencia();">
                <label class="form-check-label labelSemNegrito pr-4" for="removeIncidenciaNao">
                  <hl:message key="rotulo.nao" />
                </label>
              </div>
            </div>
          </div>
          <% } %>
          <% if (exibeChkIncidirNaMargem) { %>
          <div class="form-group col-md-12 mt-2" id="divIncidirNaMargem">
	          <input class="form-check-input ml-4" id="incidirNaMargem" name="incidirNaMargem" type="checkbox" value="true" checked>
	          <label class="form-check-label ml-4" for="incidirNaMargem">
	            <span class="text-nowrap align-text-top font-weight-bold"><hl:message key="rotulo.suspensao.altera.contrato.para.incidir.na.margem" arg0="<%=descricaoMargemDestino%>"/></span>
	          </label>
          </div>
          <% } %>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="dataReativacaoAutomatica">
                <hl:message key="rotulo.consignacao.data.reativacao.automatica" />
              </label>
              <hl:htmlinput name="dataReativacaoAutomatica" di="dataReativacaoAutomatica" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"/>              
            </div>
          </div>
          <% if (periodos != null && !periodos.isEmpty()) { %>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="OCA_PERIODO">
                  <hl:message key="rotulo.folha.periodo" />
                </label>
                <select class="form-control form-select" name="OCA_PERIODO" id="OCA_PERIODO" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                  <% for (Date periodo : periodos) { %>
                    <option value="<%=TextHelper.forHtmlAttribute(periodo)%>"><%=TextHelper.forHtmlContent(DateHelper.toPeriodString(periodo))%></option>
                  <% } %>
                </select>
              </div>
            </div>
          <% } %>
          <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
          <hl:efetivaAcaoMotivoOperacaov4
            msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.suspensao", responsavel)%>" />
          <%-- Fim dos dados do Motivo da Operação --%>
          
          <% if (exigeSenhaServidor) { %>
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

          <% if (temPermissaoAnexarSuspensao && exibirAnexo) { %>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="FILE1">
                  <hl:message key="rotulo.efetiva.acao.consignacao.dados.arquivo" />
                  <% if (!anexoObrigatorio) { %>
                  <hl:message key="rotulo.campo.opcional" />
                  <% } %>
                </label>
                <input type="file" class="form-control" name="FILE1" id="FILE1">
              </div>
            </div>
            <% if (responsavel.isCseSupOrg()) { %>
            <div class="row">
              <div class="form-group ml-3" aria-labelledby="visibilidade">
                <div class="form-check  pt-2">
                  <div><label id="visibilidade">
                    <hl:message key="rotulo.avancada.anexos.visibilidade"/>
                  </label></div>
                  <fieldset class="col-sm-12 col-md-12">
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
                  </fieldset>
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

    </div>

    <% for (int i = 0; i < autdesList.size(); i++) { %>
      <hl:htmlinput name="chkSuspender" type="hidden" value="<%=TextHelper.forHtmlAttribute(autdesList.get(i).getAttribute(Columns.ADE_CODIGO))%>" />
    <% } %>

    <div class="btn-action"> 
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" id="btnEnvia" href="#no-back" onClick="if(vf_confirmar_suspensao() && verificarAdesDataFim() <% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) { %> && verificaCamposTju() <% } %>){enviarRequisicao();} return false;"><hl:message key="rotulo.botao.salvar"/></a> 
    </div>
  </form>
</c:set>
<t:page_v4>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>