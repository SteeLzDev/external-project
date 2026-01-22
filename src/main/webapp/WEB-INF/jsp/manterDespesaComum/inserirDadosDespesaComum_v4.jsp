<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.helper.processareserva.ProcessaReservaMargem"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>

<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
	String tipo = (String) request.getAttribute("tipo");
	String decData = (String) request.getAttribute("decData");
	String nextLinkRet = (String) request.getAttribute("nextLinkRet");
	String linkRet = (String) request.getAttribute("linkRet");
	String titulo = (String) request.getAttribute("titulo");
	String echCodigo = (String) request.getAttribute("echCodigo");
	String plaCodigo = (String) request.getAttribute("plaCodigo");
	TransferObject calendario = (TransferObject) request.getAttribute("endereco");
	List<TransferObject> postos = (List<TransferObject>) request.getAttribute("postos");
	Map<?,?> cache = (Map<?,?>) request.getAttribute("cache");
	boolean descontoPosto = (boolean) request.getAttribute("descontoPosto");
	String arPrazos = (String) request.getAttribute("arPrazos");;
	String csaNome = (String) request.getAttribute("csaNome");
	String tipoVlr   = (String) request.getAttribute("tipoVlr");
	boolean alteraAdeVlr = (boolean) request.getAttribute("alteraAdeVlr");
	String adeVlrPadrao = (String) request.getAttribute("adeVlrPadrao");
	boolean prazoFixo = (boolean) request.getAttribute("prazoFixo");
	String maxPrazo = (String) request.getAttribute("maxPrazo");
	String mascaraAdeIdentificador = (String) request.getAttribute("mascaraAdeIdentificador");
	String endDescricao = (String) request.getAttribute("endDescricao");
	String plaDescricao = (String) request.getAttribute("plaDescricao");
	String descricao = (String) request.getAttribute("descricao");
	String cnvCodigo = (String) request.getAttribute("cnvCodigo");
	ProcessaReservaMargem processador = (ProcessaReservaMargem) request.getAttribute("processador");
	HashMap<?,?> hshParamPlano = (HashMap<?,?>) request.getAttribute("hshParamPlano");
	String tppIndice = (String) request.getAttribute("tppIndice");
	String adeCarencia = (String) request.getAttribute("adeCarencia");
	String planoPorDesconto = (String) request.getAttribute("planoPorDesconto");
	String rateio =  (String) request.getAttribute("rateio");
%>

<c:set var="title">
	<%=TextHelper.forHtmlAttribute(titulo)%>
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
	<div class="row">
		<div class="col-sm">
			<div class="card">
				<div class="card-header">
					<h2 class="card-header-title"><%=TextHelper.forHtmlAttribute(titulo)%></h2>
				</div>
				<div class="card-body">
					<form action="../v3/lancarDespesaComum?acao=listarPermissionario&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
						<hl:htmlinput type="hidden" name="ECH_CODIGO" value="<%=TextHelper.forHtmlAttribute(echCodigo)%>" />
						<hl:htmlinput type="hidden" name="PLA_CODIGO" value="<%=TextHelper.forHtmlAttribute(plaCodigo)%>" />
						<hl:htmlinput type="hidden" name="tipo" value="<%=TextHelper.forHtmlAttribute(tipo)%>" />
						<hl:htmlinput type="hidden" name="linkRet" value="<%=TextHelper.forHtmlAttribute(nextLinkRet)%>"/>
						<hl:htmlinput type="hidden" name="planoPorDesconto" value="<%=TextHelper.forHtmlAttribute(planoPorDesconto)%>"/>
						<hl:htmlinput type="hidden" name="indice" value="<%=TextHelper.forHtmlAttribute((hshParamPlano.get(CodedValues.TPP_INDICE_PLANO)))%>"/>
						<hl:htmlinput type="hidden" name="rateio" value="<%=TextHelper.forHtmlAttribute(rateio)%>"/>
						<div class="row">
							<div class="form-group col-sm-12 col-md-4 mt-1">
								<label for="CSA_NOME"><hl:message key="rotulo.servico.identificador"/></label>
								<input class="form-control" name="CSA_NOME" id="CSA_NOME" type="text" size="32" 
									value="<%=TextHelper.forHtmlAttribute(csaNome)%>"
									disabled="true"
								/>
							</div>

							<div class="form-group col-sm-12 col-md-4 mt-1">
								<label for="ECH_DESCRICAO"><hl:message key="rotulo.endereco.singular"/></label>
								<input class="form-control" name="ECH_CODIGO" id="ECH_CODIGO" type="text" size="32" 
									value="<%=TextHelper.forHtmlAttribute(endDescricao)%>"
									disabled="true"
								/>
							</div>

							<div class="form-group col-sm-12 col-md-4 mt-1">
								<label for="PLA_DESCRICAO"><hl:message key="rotulo.plano.singular"/></label>
								<input class="form-control" name="PLA_DESCRICAO" id="PLA_DESCRICAO" type="text" size="32" 
									value="<%=TextHelper.forHtmlAttribute(plaDescricao)%>"
									disabled="true"
								/>
							</div>
						</div>
						<div class="row">
							<div class="form-group col-sm-12 col-md-3 mt-1">
								<label for="SNV_DESCRICAO"><hl:message key="rotulo.servico.singular"/></label>
								<input class="form-control" name="SNV_DESCRICAO" id="SNV_DESCRICAO" type="text" size="32" 
									value="<%=TextHelper.forHtmlAttribute(descricao)%>"
									disabled="true"
								/>
							</div>
							<div class="form-group col-sm-12 col-md-3 mt-1">
								<label for="decData"><hl:message key="rotulo.despesa.comum.data"/></label>
								<hl:htmlinput name="decData"
												type="text"
												classe="form-control"
												value="<%=TextHelper.forHtmlAttribute(decData)%>"
												size="10"
												mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
								/>
							</div>
							<div class="form-group col-sm-12 col-md-3 mt-1">
								<label for="adeVlr"><hl:message key="rotulo.valor.singular"/> (<%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr))%>)</label>
								<hl:htmlinput name="adeVlr"
												type="text"
												classe="form-control"
												di="adeVlr"
												size="8"
												mask="#F11"
												onBlur="if(!/\d+[,|.]\d{2}/.test(this.value)) { this.value = FormataContabil(parse_num(this.value), 2); }"
												value="<%=TextHelper.forHtmlAttribute(cache.get("adeVlr") != null ? (String)cache.get("adeVlr") : "" )%>"
												others="<%=TextHelper.forHtmlAttribute(responsavel.isSer() && (prazoFixo || maxPrazo.equals("0")) ? "nf='btnEnvia'" : "" )%>"
								/>
							</div>
							<div class="form-group col-sm-12 col-md-3 mt-1">
								<label for"adePrazo"><hl:message key="rotulo.consignacao.prazo"/></label>
								<input type="text" name="adePrazo" id="adePrazo" di="adePrazo" class="form-control" size="8" value="<%=TextHelper.forHtmlAttribute(prazoFixo ? maxPrazo : "" )%>" onfocus="SetarEventoMascara(this,'#D4',true);" onblur="verificaPrazo();fout(this);ValidaMascara(this);">
								<div class="form-check pt-2">
									<label for="adeSemPrazo"><hl:message key="rotulo.consignacao.prazo.indeterminado"/></label>
									<input type="checkbox" name="adeSemPrazo" id="adeSemPrazo" di="adeSemPrazo" class="form-check-input ml-1" onclick="setaPrazo(true);" onfocus="SetarEventoMascara(this,'#*200',true);" onblur="fout(this);ValidaMascara(this);"/>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="form-group col-sm-12 col-md-4 mt-1">
								<label for="adeCarencia"><hl:message key="rotulo.consignacao.carencia"/></label>
								<input type="text" name="adeCarencia" id="adeCarencia" class="form-control" size="8" value="<%=TextHelper.forHtmlAttribute(adeCarencia)%>" onfocus="SetarEventoMascara(this,'#*200',true);" onblur="fout(this);ValidaMascara(this);">
							</div>
							
							<% if (descontoPosto) { %>
								<div class="form-group col-sm-12 col-md-4 mt-1">
									<hl:message key="rotulo.posto.singular"/>
									<%=JspHelper.geraCombo(postos, "POS_CODIGO", Columns.POS_CODIGO, Columns.POS_IDENTIFICADOR + ";" + Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\"", false, 1)%>
								</div>
							<% } %>

							<div class="form-group col-sm-12 col-md-4 mt-1">
								<label for="adeIdentificador">
									<hl:message key="rotulo.consignacao.identificador"/> <hl:message key="rotulo.campo.opcional"/>
								</label>
								<hl:htmlinput name="adeIdentificador" type="text" classe="form-control" di="adeIdentificador" size="15" mask="<%=TextHelper.isNull(mascaraAdeIdentificador) ? "#*40":mascaraAdeIdentificador %>" nf="btnEnvia" value="<%=TextHelper.forHtmlAttribute(cache.get("adeIdentificador") != null ? (String)cache.get("adeIdentificador") : "" )%>"/>
							</div>
															
							<div class="form-group col-sm-12 col-md-4 mt-1">
								<label for="tppIdx"><hl:message key="rotulo.indice.singular"/></label>
								<input type="text" name="tppIdx" id="tppIdx" class="form-control" size="8" value="<%=TextHelper.forHtmlAttribute(!TextHelper.isNull(tppIndice) ? tppIndice : "" )%>" onfocus="SetarEventoMascara(this,'#*200',true);" onblur="fout(this);ValidaMascara(this);" disabled="true">
							</div>
							
							<% if (processador != null) { %>
								<%=TextHelper.forHtmlContent( processador.incluirPasso1(request) )%>
							<% } %>
						</div>
						</form>
					</div>
			</div>
		</div>
	</div>
    <div class="btn-action">
         <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(linkRet)%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
         <a class="btn btn-primary" href="#no-back" onclick="desabilitaCampos(); if(vf_despesa_comum() && verificaPrazo() && vf_PrazosNE()) { enableAllCustom(); f0.submit(); } return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>
</c:set>

<c:set var="javascript">
	<script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
	<script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
	<script type="text/JavaScript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
	<script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
	<script type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>
	<script type="text/JavaScript">
		var valor = 'IB';

		function formLoad() {
			setaParamSvc();

			<% if (prazoFixo) { %>
				f0.adePrazo.disabled = true;
			<% } %>

			focusFirstField();
		}

		function enableAllCustom() {
			if (document.forms[0] != null) {
				for (var i = 0; (i < document.forms[0].elements.length); i++) {
					var e = document.forms[0].elements[i];
					if (e.type != 'button' && e.type != 'hidden' &&
						e.type != 'image' && e.type != 'reset' &&
						e.type != 'submit') {
						if (e.disabled) {	
							e.disabled = false;
						}
					}
				}
			}
		}

		// Verifica os dados do formulário de despesa comum
		function vf_despesa_comum() {
			var Controles;
			var Msgs;

			var dataDespesa = new String(f0.decData.value);
			if (f0.decData != null && f0.decData.value != '') {
				var campos = obtemPartesData(dataDespesa);
				if (!verificaData(f0.decData.value)) {
					return false;
				} else {
					var now = new Date();
					var then = new Date(campos[2], campos[1] - 1, campos[0]);
					if (then.getTime() > now.getTime()) {
						alert('<hl:message key="mensagem.erro.despesa.comum.data.invalida"/>');
						return false;
					}        	   
				}
			}

			var comPrazo = ((f0.adeSemPrazo == null) || (!f0.adeSemPrazo.checked));
			if (comPrazo) {
				<% if (!descontoPosto) { %>
				Controles = new Array("adeVlr", "adePrazo");
				Msgs = new Array('<hl:message key="mensagem.informe.ade.valor"/>',
								'<hl:message key="mensagem.informe.ade.prazo"/>');
				<% } else { %>
				Controles = new Array("adeVlr", "adePrazo", "POS_CODIGO");
				Msgs = new Array('<hl:message key="mensagem.informe.ade.valor"/>',
								'<hl:message key="mensagem.informe.ade.prazo"/>',
								'<hl:message key="mensagem.informe.posto"/>');
				<% } %>
			} else {
				<% if (!descontoPosto) { %>
				Controles = new Array("adeVlr");
				Msgs = new Array('<hl:message key="mensagem.informe.ade.valor"/>');
				<% } else { %>
				Controles = new Array("adeVlr", "POS_CODIGO");
				Msgs = new Array('<hl:message key="mensagem.informe.ade.valor"/>',
								'<hl:message key="mensagem.informe.posto"/>');
				<% } %>
			}
			var adeVlr = parseFloat(parse_num(f0.adeVlr.value));

			if(!ValidaCampos(Controles, Msgs)) {
				return false;
			} else {
				if (isNaN(adeVlr) || (adeVlr <= 0)) {
					alert('<hl:message key="mensagem.erro.valor.parcela.incorreto"/>');
					if (f0.adeVlr != null && !f0.adeVlr.disabled) {
						f0.adeVlr.focus();
					}
					return false;
				} else if (parseFloat(adeVlr) < 0.0) {
					alert('<hl:message key="mensagem.erro.valor.parcela.negativo"/>');
					if (f0.adeVlr != null && !f0.adeVlr.disabled) {
						f0.adeVlr.focus();
					}
					return false;
				} else {
					if (comPrazo) {
						if (f0.adePrazo != null) {
						var adePrazo = parseInt(f0.adePrazo.value);

							if (isNaN(adePrazo) || (adePrazo <= 0)) {
								alert('<hl:message key="mensagem.erro.prazo.negativo"/>');
								f0.adePrazo.focus();
								return false;
							}
						}
					}
					return true;
				}
			}
		}

		function setaPrazo(focar) {
			if (f0.adePrazo != null && f0.adeSemPrazo != null) {
				f0.adePrazo.disabled = f0.adeSemPrazo.checked || <%=(boolean) prazoFixo %>;
				
				if (f0.adeSemPrazo.checked) {
					f0.adePrazo.value = '';
				} else if (focar) {
					f0.adePrazo.focus();
				}
			}
		}

		function setaParamSvc() {
			if (alteraAdeVlr) {
				if (adeVlr != null && adeVlr != undefined && adeVlr != '' && parseFloat(parse_num(adeVlr)) > 0) {
					f0.adeVlr.value = adeVlr;
				}
				f0.adeVlr.disabled = false;
			} else {
				f0.adeVlr.value = adeVlr;
				f0.adeVlr.disabled = true;
			}

			<% if (arPrazos != null) { %>
				<% if (cache.get("adePrazo") != null) { %>
					f0.adePrazo.value = '<%=TextHelper.forJavaScriptBlock(cache.get("adePrazo"))%>'
				<% } %>
				FiltraCombo(f0.adePrazoAux, arPrazos, '<%=TextHelper.forJavaScriptBlock(cnvCodigo)%>', f0.adePrazo.value);
			<% } %>
			desabilitaCampos();
		}

		function desabilitaCampos() {
			if (maxPrazo == 0) {       // somente prazo indeterminado
				if (f0.adeSemPrazo != null) {
					f0.adeSemPrazo.checked = true;
					f0.adeSemPrazo.disabled = true;
				}
			} else if (maxPrazo > 0) { // somente prazo determinado menor que maxPrazo
				if (f0.adeSemPrazo != null) {
					f0.adeSemPrazo.checked = false;
					f0.adeSemPrazo.disabled = true;
				}
			} else {                   // qualquer prazo
				if (f0.adePrazo != null)
					f0.adePrazo.disabled = false;
				if (f0.adeSemPrazo != null)
					f0.adeSemPrazo.disabled = false;
			}
			setaPrazo(false);
		}

		function vf_PrazosNE() {
			if (QualNavegador() == 'NE') {
				var strRetorno = false;
				var arMsg = '';
				desabilitaCampos();
				if (window.arPrazos != null) {
					for(i = 0; i < arPrazos.length; i++) {
						if (arPrazos[i][2] == '<%=TextHelper.forJavaScriptBlock(cnvCodigo)%>') {
							if (f0.adePrazo.value == arPrazos[i][0]) {
								strRetorno = true;
							}
							arMsg += ' - ' + arPrazos[i][0] + '\n';
						}
					}
				}
				if (!strRetorno && arMsg != '') {
					alert('<hl:message key="mensagem.erro.despesa.comum.numero.prestacoes.invalido"/>\n' +
							'<hl:message key="mensagem.erro.despesa.comum.prazos.validos"/>:\n' +
							arMsg);
					f0.adeSemPrazo.checked = false;
					f0.adePrazo.focus();
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		}

		window.onload = formLoad()
		window.onresize = setaParamSvc()
	</script>
	<script type="text/JavaScript">
		f0 = document.forms[0];
		adeVlr = '<%=TextHelper.forJavaScriptBlock(adeVlrPadrao)%>';
		alteraAdeVlr = '<%=TextHelper.forJavaScriptBlock(alteraAdeVlr)%>';
		maxPrazo = '<%=TextHelper.forJavaScriptBlock(maxPrazo)%>';
		permitePrazoMaiorContSer = false;
		<%=TextHelper.forJavaScriptBlock(arPrazos != null ? arPrazos : "")%>
	</script>
</c:set>

<t:page_v4>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>