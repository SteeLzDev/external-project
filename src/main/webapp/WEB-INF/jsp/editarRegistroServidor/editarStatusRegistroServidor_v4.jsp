<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

ServidorTransferObject servidor  = (ServidorTransferObject) request.getAttribute("servidor");
RegistroServidorTO registroServidor = (RegistroServidorTO) request.getAttribute("registroServidor");
%>
<c:set var="title">
<hl:message key="rotulo.editar.status.servidor.titulo" />
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
	<div class="card">
		<div class="card-header hasIcon">
            <span class="card-header-icon">
            	<svg width="26"><use xlink:href="#i-servidor" /></svg>
            </span>
			<h2 class="card-header-title"><hl:message key="rotulo.editar.servidor.grid" /></h2>
		</div>
		<div class="card-body">
			<form method="post" action="../v3/editarStatusRegistroServidor" name="form1">
				<fieldset>	
                <% if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO, responsavel) ||
                       ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA, responsavel) ||
                       ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, responsavel) ||
                       ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO, responsavel) ||
                       ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, responsavel)||
                       ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO, responsavel) ||
                       ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS, responsavel)) { %>
					<h3 class="legend"><span><hl:message key="rotulo.servidor.subtitulo.contrato.sem.arg" /></span></h3>
                <% } %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>">
                <div class="row">
                    <div class="form-group col-sm-6">
                    	<label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>"><hl:message key="rotulo.servidor.status" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>"/></label>
                        <hl:htmlcombo
                             listName="listaSrs" 
                             classe="form-control"
                             name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>" 
                             fieldValue="<%=TextHelper.forHtmlAttribute(Columns.SRS_CODIGO)%>" 
                             fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.SRS_DESCRICAO)%>" 
                             notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione.arg", responsavel, "")%>"
                             selectedValue="<%=TextHelper.forHtmlAttribute(registroServidor.getSrsCodigo())%>" 
                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>"
                             onChange="habilitaDesabilitaDatas(this);"
                             >
                        </hl:htmlcombo>
                    </div>
                </div>
                </show:showfield>

                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA)%>">
                <%
                    String rseDataSaida = null;
                            rseDataSaida = (registroServidor != null && registroServidor.getRseDataSaida() != null ? registroServidor.getRseDataSaida().toString() : "");
                            if (!rseDataSaida.equals("")) {
                                rseDataSaida = DateHelper.reformat(rseDataSaida, "yyyy-MM-dd", LocaleHelper.getDatePattern());
                            }
                %>
                <div class="row">
                    <div class="form-group col-sm-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA)%>"><hl:message key="rotulo.servidor.data.saida" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA)%>"
                            di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA)%>"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(rseDataSaida)%>"
                            size="10"
                            mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                            maxlength='10'
                            placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA)%>" />
                    </div>
                </div>
                </show:showfield>

                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>">
                <%
                    String rseDataUltSalario = null;
                            rseDataUltSalario = (registroServidor != null && registroServidor.getRseDataUltSalario() != null ? registroServidor.getRseDataUltSalario().toString() : "");
                            if (!rseDataUltSalario.equals("")) {
                                rseDataUltSalario = DateHelper.reformat(rseDataUltSalario, "yyyy-MM-dd", LocaleHelper.getDatePattern());
                            }
                %>
                <div class="row">
                    <div class="form-group col-sm-6">
                        <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>"><hl:message key="rotulo.servidor.data.ult.salario" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>"/></label>
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>"
                                                     di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>"
                                                     type="text"
                                                     classe="form-control"
                                                     value="<%=TextHelper.forHtmlAttribute(rseDataUltSalario)%>"
                                                     size="10"
                                                     mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                                     maxlength='10'
                                                     placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
                                                     configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>" />
                    </div>
                </div>
                </show:showfield>
                
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO)%>"><hl:message key="rotulo.servidor.salario" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO)%>"/></label>
           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO)%>"
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO)%>"
                      type="text"
                      classe="form-control"
                      value='<%=TextHelper.forHtml(registroServidor.getRseSalario() != null ? NumberHelper.format(registroServidor.getRseSalario().doubleValue(), NumberHelper.getLang()) : "")%>'
                      mask="#F11"
                      onFocus="SetarEventoMascara(this,'#F11',true);"
                      onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO)%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.salario", responsavel)%>"                             
            />
         </div>
        </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS)%>">
        <div class="row">
         <div class="form-group col-sm-6">
          <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS)%>"><hl:message key="rotulo.servidor.proventos" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS)%>"/></label>
           <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS)%>"
                      di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS)%>"
                      type="text"
                      classe="form-control"
                      value='<%=TextHelper.forHtml(registroServidor.getRseProventos() != null ? NumberHelper.format(registroServidor.getRseProventos().doubleValue(), NumberHelper.getLang()) : "")%>'
                      mask="#F11"
                      onFocus="SetarEventoMascara(this,'#F11',true);"
                      onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS)%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.proventos", responsavel)%>"                             
            />
         </div>
        </div>
       </show:showfield>

                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>">       
                <div class="row">
                    <div class="form-group col-sm-12 col-md-4 mt-1">
                    <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"><hl:message key="rotulo.servidor.pedido.demissao" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"/></label>
                    <br>
                        <div class="form-check-inline form-check">
                        <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"
                                          di="iDemissaoSim"
                                          type="radio"
                                          classe="form-check-input mt-1 ml-1"
                                          value="S"
                                          checked="<%=String.valueOf(registroServidor.getRsePedidoDemissao() != null && registroServidor.getRsePedidoDemissao().equalsIgnoreCase(\"S\"))%>"
                                          mask="#*10"
                                          size=''
                                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"
                            /><label class="form-check-label labelSemNegrito ml-1 pr-4" for="iDemissaoSim"><hl:message key="rotulo.sim"/></label>
                        </div>
                        <div class="form-check-inline form-check">
                            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"
                                          di="iDemissaoNao"
                                          type="radio"
                                          size='10'
                                          classe="form-check-input mt-1 ml-1"
                                          value="N"
                                          checked="<%=String.valueOf(registroServidor.getRsePedidoDemissao() != null && registroServidor.getRsePedidoDemissao().equalsIgnoreCase(\"N\"))%>"
                                          mask="#*10"
                                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>"
                            /><label class="form-check-label labelSemNegrito ml-1 pr-4" for="iDemissaoNao"><hl:message key="rotulo.nao"/></label>
                        </div>
                    </div>
                </div>
                </show:showfield>

                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO)%>">
                   <%
                       String rseDataRetorno = null;
                                  rseDataRetorno = (registroServidor != null && registroServidor.getRseDataRetorno() != null ? registroServidor.getRseDataRetorno().toString() : "");
                                  if (!rseDataRetorno.equals("")) {
                                      rseDataRetorno = DateHelper.reformat(rseDataRetorno, "yyyy-MM-dd", LocaleHelper.getDatePattern());
                                  }
                   %>
                <div class="row">
                    <div class="form-group col-sm-6">
                     <label for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO)%>"><hl:message key="rotulo.servidor.data.retorno" fieldKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO)%>"/></label>
                     <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO)%>"
                         di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO)%>"
                         type="text"
                         classe="form-control"
                         value="<%=TextHelper.forHtmlAttribute(rseDataRetorno)%>"
                         size="10"
                         mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                         maxlength='10'
                         placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
                         configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO)%>" />
                    </div>
                </div>
                </show:showfield>

                <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
                <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.alteracao.cadastro", responsavel)%>" operacaoRegistroServidor="true" tmoSempreObrigatorio="false" scriptOnly="false"/>
                <%-- Fim dos dados do Motivo da Operação --%>
     			</fieldset>
                <%= SynchronizerToken.generateHtmlToken(request) %>
                <hl:htmlinput type="hidden" name="acao" value="salvar" />
                <hl:htmlinput type="hidden" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(registroServidor.getRseCodigo())%>" />
                <hl:htmlinput type="hidden" name="SER_CODIGO" value="<%=TextHelper.forHtmlAttribute(servidor.getSerCodigo())%>" />
                
                <%if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_DETALHES_EXCL_BLOQ_SER, CodedValues.TPC_SIM, responsavel)) {%>
                <hl:htmlinput name="srsOriginal" type="hidden" value="<%=TextHelper.forHtmlAttribute((registroServidor != null && registroServidor.getSrsCodigo() != null ? registroServidor.getSrsCodigo() : ""))%>"/>
                <%}%>    
     		</form>
		</div>
	</div>
    <div class="btn-action">
    	<a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="javascript:void(0);" onClick="if (enviar()) {f0.submit();} return false;" alt="<hl:message key="rotulo.botao.salvar"/>" title="<hl:message key="rotulo.botao.salvar"/>"><hl:message key="rotulo.botao.salvar" /></a>
    </div>
</c:set>
<c:set var="javascript">
<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.alteracao.cadastro", responsavel)%>" operacaoRegistroServidor="true" tmoSempreObrigatorio="false" scriptOnly="true"/>
<script type="text/JavaScript">
var f0 = document.forms[0];
f0.onload = limpaDesabilitaCampo();

function enviar() {
    <%if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_DETALHES_EXCL_BLOQ_SER, CodedValues.TPC_SIM, responsavel)) {%>
    if (typeof checkCamposExclusao == 'function') {
        if (!checkCamposExclusao()) {
            return false;
        }
    }
    <%}%>

    <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO, responsavel)) {%>
    var situacaoField = f0.<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>;
    if (situacaoField.value == null || situacaoField.value == '') {
        alert('<hl:message key="mensagem.informe.registro.servidor.situacao"/>');
        situacaoField.focus();
        return false;
    }
    <%}%>

    <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA, responsavel)) {%>
    var dtSaidaField = f0.<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA)%>;
    if (dtSaidaField.value == null || dtSaidaField.value == '') {
        alert('<hl:message key="mensagem.erro.rse.informe.data.saida"/>');
        dtSaidaField.focus();
        return false;
    }
    <%}%>

    <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, responsavel)) {%>
    var dtUltSalField = f0.<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>;
    if (dtUltSalField.value == null || dtUltSalField.value == '') {
        alert('<hl:message key="mensagem.erro.rse.informe.data.ult.salario"/>');
        dtUltSalField.focus();
        return false;
    }
    <%}%>

    <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO, responsavel)) {%>
    var demissaoField = f0.<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>;
    if (demissaoField.value == null || demissaoField.value == '') {
        alert('<hl:message key="mensagem.erro.pedido.demissao.obrigatorio"/>');
        demissaoField[0].focus();
        return false;
    }
    <%}%>

    <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO, responsavel)) {%>
    var dtRetornoField = f0.<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO)%>;
    if (dtRetornoField.value == null || dtRetornoField.value == '') {
        alert('<hl:message key="mensagem.erro.rse.informe.data.retorno"/>');
        dtRetornoField.focus();
        return false;
    }
    <%}%>

    var status = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>;
    if (status != null && status.value == "<%=CodedValues.SRS_ATIVO%>") {
        <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS, responsavel)) {%>
        var proventosField = f0.<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS)%>;
        if (proventosField.value == null || proventosField.value == '') {
            alert('<hl:message key="mensagem.informe.registro.servidor.proventos"/>');
            proventosField.focus();
            return false;
        }
        <%}%>
    }

    if (status != null && status.value == "<%=CodedValues.SRS_ATIVO%>") {
        <%if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO, responsavel)) {%>
        var salarioField = f0.<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO)%>;
        if (salarioField.value == null || salarioField.value == '') {
            alert('<hl:message key="mensagem.informe.registro.servidor.salario"/>');
            salarioField.focus();
            return false;
        }
        <%}%>
    }

    if (vf_edt_registro_servidor('<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>',
        '<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_MARGEM_1)%>',
        '<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_MARGEM_2)%>',
        '<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_MARGEM_3)%>')) {

        // Habilita os campos antes de fazer um submit
        enableAll();
        return true;
    }

    return false;
}

function checkCamposExclusao() {
	
   var status = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>;
   var dataSaida      = null;
   var dataUltSalario = null;
   var pedidoDemissao = null;
   var dataRetorno    = null;

<%if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_DETALHES_EXCL_BLOQ_SER, CodedValues.TPC_SIM, responsavel)) {%>

   if (status != null && (status.value == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%> || status.value == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>)) {
       
	   dataSaida = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA)%>;
       if (dataSaida == null ||  dataSaida.value == '') {
      	alert('<hl:message key="mensagem.erro.rse.informe.data.saida"/>');
      	dataSaida.focus();
	    return false;  
       }
       
       dataUltSalario = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>;
       if (dataUltSalario == null ||  dataUltSalario.value == '') {
      	alert('<hl:message key="mensagem.erro.rse.informe.data.ult.salario"/>');
      	dataUltSalario.focus();
	    return false;  
       }
       
   } 
   
   if (status != null && status.value == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>) {
	   dataRetorno = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO)%>;
       if (dataRetorno == null ||  dataRetorno.value == '') {
           alert('<hl:message key="mensagem.erro.rse.informe.data.retorno"/>');
      	   dataRetorno.focus();        	
		   return false;  
       }
       
       if (verificaData(dataRetorno.value)) {
    	    var now = new Date();
    	    now.setHours(0);
    	    now.setMinutes(0);
    	    now.setSeconds(0);  
    	    now.setMilliseconds(0);  
   	        var campos = obtemPartesData(new String(dataRetorno.value));
    	    var then = new Date(campos[2], campos[1] - 1, campos[0]);
    	    if (then.getTime() < now.getTime()) {
    	      alert('<hl:message key="mensagem.erro.rse.data.retorno.menor.atual"/>');
         	  dataRetorno.focus();        	
    	      return false;
    	    }
   	   } else {
      	   dataRetorno.focus();        	
   	   	   return false;
   	   }
   } 
   
   if (status != null && status.value == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%>) {
       pedidoDemissao = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PEDIDO_DEMISSAO)%>;
       if (pedidoDemissao == null ||  pedidoDemissao.value == '') {
      	alert('<hl:message key="mensagem.erro.rse.informe.servidor.demitiuse"/>');        	
	    return false;
       }
   }

   if (status != null && status.value != f0.srsOriginal.value) {
       var tmoCodigo = document.getElementById("TMO_CODIGO");
       if (tmoCodigo == null || tmoCodigo.value == "") {
      	 alert('<hl:message key="mensagem.erro.informacao.motivo.operacao.ausente"/>');
         tmoCodigo.focus();        	
	  	 return false;  
       }

       var tmoObs = document.getElementById("ADE_OBS");
       if (tmoObs == null || tmoObs.value.trim() == "") {
      	 alert('<hl:message key="mensagem.erro.obs.motivo.operacao.ausente"/>');
      	 tmoObs.focus();        	
	     return false;
       }
   }
<%} %>   
   
   return true;
}
function habilitaDesabilitaDatas(obj) {
	var status = obj;
<%	if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA, responsavel) 
		|| ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, responsavel)
		|| ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO, responsavel)) {%>
		
    	var dtSaidaField = f0.<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA)%>;
    	var dataUltSalario = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>;
    	var dataRetorno = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO)%>;
    	
    	if (status.value == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%>) {
    		if (dataRetorno.value != null && dataRetorno.value !="") {
	  			var confirmar = confirm('<hl:message key="mensagem.desabilita.datas"/>');
    			if (confirmar == true) {
    				dtSaidaField.value = "";
        			dataUltSalario.value = "";
        			dataRetorno.value = ""; 
            		dataRetorno.disabled = true;
            		dtSaidaField.disabled = false;
            		dataUltSalario.disabled = false;    			
  	  			} else {
  	  				var statusAnterior = f0.<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>;
  	  				statusAnterior.value = '<%=TextHelper.forHtmlAttribute(registroServidor.getSrsCodigo())%>';
  	  			}    			
    		}else{
				dtSaidaField.value = "";
    			dataUltSalario.value = "";
    			dataRetorno.value = ""; 
        		dataRetorno.disabled = true;
        		dtSaidaField.disabled = false;
        		dataUltSalario.disabled = false;    			
    		}
    	}
    	if (status.value == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
    		if ((dtSaidaField.value != null && dtSaidaField.value !="") || (dataUltSalario.value != null && dataUltSalario.value !="") ) {
	  			var confirmar = confirm('<hl:message key="mensagem.desabilita.datas"/>');
    			if (confirmar == true) {
    				dtSaidaField.value = "";
        			dataUltSalario.value = "";
        			dataRetorno.value = ""; 
        			dtSaidaField.disabled = false;
        			dataUltSalario.disabled = false;
        			dataRetorno.disabled = false;   			
  	  			} else {
  	  				var statusAnterior = f0.<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>;
  	  				statusAnterior.value = '<%=TextHelper.forHtmlAttribute(registroServidor.getSrsCodigo())%>';
  	  			}    			
    		}else{
				dtSaidaField.value = "";
    			dataUltSalario.value = "";
    			dataRetorno.value = ""; 
    			dtSaidaField.disabled = false;
    			dataUltSalario.disabled = false;
    			dataRetorno.disabled = false;   			
    		}
    	}
    	if (status.value == <%="'" + CodedValues.SRS_ATIVO + "'"%> || status.value == <%="'" + CodedValues.SRS_PENDENTE + "'"%>){
     		if (
     			(dtSaidaField.value != null && dtSaidaField.value !="") 
         		|| (dataUltSalario.value != null && dataUltSalario.value !="") 
         		|| (dataRetorno.value != null && dataRetorno.value !="")
         		){
  		  		var confirmar = confirm('<hl:message key="mensagem.desabilita.datas"/>');
  		  		if (confirmar == true) {
  		      		dtSaidaField.value = "";
  		      		dataUltSalario.value = "";
  		      		dataRetorno.value = "";
  		      		dtSaidaField.disabled = true;
  		      		dataUltSalario.disabled = true;
  		      		dataRetorno.disabled = true;
  		  		}else {
  		  			var statusAnterior = f0.<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SITUACAO)%>;
  		  			statusAnterior.value = '<%=TextHelper.forHtmlAttribute(registroServidor.getSrsCodigo())%>';
  		  			if (statusAnterior.value != <%="'" + CodedValues.SRS_EXCLUIDO + "'"%> && statusAnterior.value != <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
  		  				dtSaidaField.disabled = true;
  		  				dataUltSalario.disabled = true;
  		  				dataRetorno.disabled = true;
  		  			}else{
  		  				if (statusAnterior.value == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%>){
  		  		    		dtSaidaField.disabled = false;
  		  		    		dataUltSalario.disabled = false;
  		  				}
  		  				if (statusAnterior.value == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
  		  					dtSaidaField.disabled = false;
  		  					dataUltSalario.disabled = false;
  		  					dataRetorno.disabled = false;
  		  		    	}
  		  			}
  		  		}
      		}
    	}
		if (status.value != <%="'" + CodedValues.SRS_EXCLUIDO + "'"%> && status.value != <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
			dtSaidaField.disabled = true;
			dataUltSalario.disabled = true;
			dataRetorno.disabled = true;
		}
  <%}%>
}
function limpaDesabilitaCampo(){

<%	if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA, responsavel) || 
        ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, responsavel) ||
		ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO, responsavel)){%>

		var dtSaidaField = f0.<%=(String)(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA)%>;
		var dataUltSalario = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO)%>;
		var dataRetorno = f0.<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO)%>;
		var status = '<%=TextHelper.forHtmlAttribute(registroServidor.getSrsCodigo())%>';
		if (status == <%="'" + CodedValues.SRS_EXCLUIDO + "'"%>) {
			dtSaidaField.disabled = false;
			dataUltSalario.disabled = false;
			dataRetorno.disabled = true;
		}
		if (status == <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
			dtSaidaField.disabled = false;
			dataUltSalario.disabled = false;
			dataRetorno.disabled = false;
		}
		if (status != <%="'" + CodedValues.SRS_EXCLUIDO + "'"%> && status != <%="'" + CodedValues.SRS_BLOQUEADO + "'"%>){
			dtSaidaField.disabled = true;
			dataUltSalario.disabled = true;
			dataRetorno.disabled = true;
		}
		
<%  }%>
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
  
