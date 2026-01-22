<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelDataCompraPage = JspHelper.getAcessoSistema(request);
   String paramDataCompraPage = JspHelper.verificaVarQryStr(request, "PARAMETRO");
   String diasDataCompraPage = JspHelper.getParametroDifDatasRelatorio(paramDataCompraPage, responsavelDataCompraPage);
   String obrDataCompraPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
      
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
      
   String others = "";   
   
   if (desabilitado) {
       others = "disabled";
   }
%>
          <div class="form-group col-sm-12 col-md-6">
            <span id="dataCompra">${descricoes[recurso]}</span>
            <div class="row" role="group" aria-labelledby="dataCompra">
                <div class="col-sm-12 col-md-6">
                  <div class="row">
                    <div class="form-check col-sm-2 col-md-2">
                      <div class="float-left align-middle mt-4 form-control-label">
                        <label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
                      </div>
                    </div>
                    <div class="form-check col-sm-10 col-md-10">
                      <hl:htmlinput name="periodoIni" di="periodoIni" type="text" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" others="<%=TextHelper.forHtmlAttribute(others)%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoIni"))%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
                    </div>
                  </div>
                </div>
                <div class="col-sm-12 col-md-6">  
                 <div class="row"> 
                    <div class="form-check col-sm-2 col-md-2">
                      <div class="float-left align-middle mt-4 form-control-label">
                        <label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
                      </div>
                    </div>
                    <div class="form-check col-sm-10 col-md-10">
                      <hl:htmlinput name="periodoFim" di="periodoFim" type="text" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" others="<%=TextHelper.forHtmlAttribute(others)%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoFim"))%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
                    </div>
                  </div>
                </div>
            </div>
          </div>

    <% if (obrDataCompraPage.equals("true")) { %>
        <script type="text/JavaScript">
         function funDataCompraPage() {
            camposObrigatorios = camposObrigatorios + 'periodoIni,';
            camposObrigatorios = camposObrigatorios + 'periodoFim,';

            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.data.compra.inicio"/>,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.data.compra.fim"/>,';
         }
        addLoadEvent(funDataCompraPage);     
        </script>        
    <% } %>             

        <script type="text/JavaScript">
         function valida_campo_data_compra() {
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
                   <% if (!diasDataCompraPage.equals("")) { %>
                       diasDif = document.getElementById('agendadoSim') == null || !document.getElementById('agendadoSim').checked ? <%=TextHelper.forJavaScriptBlock(diasDataCompraPage)%> : "";
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
        </script>            
