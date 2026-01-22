<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelDataLiquidacaoPage = JspHelper.getAcessoSistema(request);
   String paramDataLiquidacaoPage = JspHelper.verificaVarQryStr(request, "PARAMETRO");
   String diasDataLiquidacaoPage = JspHelper.getParametroDifDatasRelatorio(paramDataLiquidacaoPage, responsavelDataLiquidacaoPage);
   String obrDataLiquidacaoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   
   String periodoIni = (String) JspHelper.verificaVarQryStr(request, "periodoIniLiquidacao");
   String periodoFim = (String) JspHelper.verificaVarQryStr(request, "periodoFimLiquidacao");
   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
   
   String valueIni = "";
   String others = "";
   if (!TextHelper.isNull(periodoIni)) {
       valueIni = periodoIni;
       others = "disabled";
   }
         
   String valueFim = "";
   if (!TextHelper.isNull(periodoFim)) {
       valueFim = periodoFim;
   }
   
   if (desabilitado) {
       others = "disabled";
   }
%>
          <div class="form-group col-sm-12 col-md-6">
            <span id="dataLiquidacao">${descricoes[recurso]}</span>
            <div class="row" role="group" aria-labelledby="dataLiquidacao">
              <div class="col-sm-6">
                <div class="row">
                  <div class="form-check col-sm-2 col-md-2">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="periodoIniLiquidacao" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
                    </div>
                  </div>
                  <div class="form-check col-sm-10 col-md-10">
                    <hl:htmlinput name="periodoIniLiquidacao" di="periodoIniLiquidacao" type="text" value="<%=TextHelper.forHtmlAttribute(valueIni )%>" others="<%=TextHelper.forHtmlAttribute(others )%>" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" onChange="atualizarSADCodigo()"/>
                  </div>
                </div>
              </div>
              <div class="col-sm-6">
                <div class="row">
                  <div class="form-check col-sm-2 col-md-2">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="periodoFimLiquidacao" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
                    </div>
                  </div>
                  <div class="form-check col-sm-10 col-md-10">
                    <hl:htmlinput name="periodoFimLiquidacao" di="periodoFimLiquidacao" type="text" value="<%=TextHelper.forHtmlAttribute(valueFim )%>" others="<%=TextHelper.forHtmlAttribute(others )%>" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" onChange="atualizarSADCodigo()"/>
                  </div>
                </div>
              </div>
            </div>
            <hl:htmlinput name="obrDataLiquidacaoPage" type="hidden" value="<%=TextHelper.forHtmlAttribute(obrDataLiquidacaoPage )%>" />
          </div>
          
    <% if (obrDataLiquidacaoPage.equals("true")) { %>                    
      <script type="text/JavaScript">
      function funDataLiquidacaoPage() {
          camposObrigatorios = camposObrigatorios + 'periodoIniLiquidacao,';
          camposObrigatorios = camposObrigatorios + 'periodoFimLiquidacao,';

          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.data.liquidacao.inicio"/>,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.data.liquidacao.fim"/>,';
      }
      addLoadEvent(funDataLiquidacaoPage);     
    </script>
    <% } %>
    
        <script type="text/JavaScript">
         function valida_campo_data_liquidacao() {
             with(document.forms[0]) {
               if (periodoIni != null && periodoIni.value != '' && (!verificaData(periodoIni.value))) {
                 periodoIni.focus();
                 return false;
               }
               if (periodoFim != null && periodoFim.value != '' && (!verificaData(periodoFim.value))) {
                 periodoFim.focus();
                 return false;
               }
               if (periodoIni != null && periodoFim != null) {
                   var diasDif = ''; 
                   <% if (!diasDataLiquidacaoPage.equals("")) { %>
                        diasDif = document.getElementById('agendadoSim') == null || !document.getElementById('agendadoSim').checked ? <%=TextHelper.forJavaScriptBlock(diasDataLiquidacaoPage)%> : "";
                   <% } %>
                   var PartesData = new Array();
                   PartesData = obtemPartesData(periodoIni.value);
                   var Dia = PartesData[0];
                   var Mes = PartesData[1];
                   var Ano = PartesData[2];
                   PartesData = obtemPartesData(periodoFim.value);
                   var DiaFim = PartesData[0];
                   var MesFim = PartesData[1];
                   var AnoFim = PartesData[2];

                   if (!VerificaPeriodoExt(Dia, Mes, Ano, DiaFim, MesFim, AnoFim, diasDif)) {
 					   if(document.getElementById('agendadoSim') != null && !document.getElementById('agendadoSim').checked){
	                      	alert('<hl:message key="mensagem.erro.relatorio.agendar.periodo"/>');
		               	  	document.getElementById('agendadoSim').checked = true;
		             	  	habilitaDesabilitaAgendamento();
					   }
                       return false;
                   }                               
               }
             }             
             return true;
         }

         function atualizarSADCodigo() {
       	    let periodoIniElement = document.getElementById("periodoIniLiquidacao");
       	    let periodoFimElement = document.getElementById("periodoFimLiquidacao");

       	    let periodoIni = periodoIniElement ? periodoIniElement.value.trim() : "";
       	    let periodoFim = periodoFimElement ? periodoFimElement.value.trim() : "";
       	    
       	    let todosOsCamposPreenchidos = periodoIni !== "" || periodoFim !== "";
       	    let elementos = document.querySelectorAll("[id^='SAD_CODIGO']");
       	 	let sadCodigo15 = document.getElementById("SAD_CODIGO15");

       	    elementos.forEach(el => {
       	        if (el.id !== "SAD_CODIGO15") {
       	            el.disabled = todosOsCamposPreenchidos;
       	        } else {
       	        	sadCodigo15.checked = todosOsCamposPreenchidos
             	}
       	    });
       	}
        </script>        
                       
