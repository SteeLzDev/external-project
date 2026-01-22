<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelDataHoraPage = JspHelper.getAcessoSistema(request);
   String paramDataHoraPage = JspHelper.verificaVarQryStr(request, "PARAMETRO");
   String diasDataHoraPage = JspHelper.getParametroDifDatasRelatorio(paramDataHoraPage, responsavelDataHoraPage);
   String obrDataHoraPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   
   String periodoIni = (String) JspHelper.verificaVarQryStr(request, "periodoIni");
   String periodoFim = (String) JspHelper.verificaVarQryStr(request, "periodoFim");
   String horaIni = (String) JspHelper.verificaVarQryStr(request, "horaIni");
   String horaFim = (String) JspHelper.verificaVarQryStr(request, "horaFim");
   
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
   
   String valueHoraIni = "";
   String valueHoraFim = "";
         
   if (!TextHelper.isNull(horaIni)) {
       valueHoraIni = horaIni;      
   }
   
   if (!TextHelper.isNull(horaFim)) {
       valueHoraFim = horaFim;
   }   
   
%>
          <div class="form-group col-sm-12 col-md-6 cep-input">
            <span id="periodoDataHora">${descricoes[recurso]}</span>
            <div class="row" role="group" aria-labelledby="periodoDataHora">
              <div class="col-sm-6">
                <div class="row">
                  <div class="form-check col-sm-12 col-md-2">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
                    </div>
                  </div>
                  <div class="form-check col-sm-12 col-md-10">
                    <hl:htmlinput name="periodoIni" di="periodoIni" type="text" value="<%=TextHelper.forHtmlAttribute(valueIni )%>" others="<%=TextHelper.forHtmlAttribute(others)%>" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
                  </div>
                </div>
              </div>
              <div class="col-sm-6">    
                <div class="row">   
                  <div class="form-check col-sm-12 col-md-2">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
                    </div>
                  </div> 
                  <div class="form-check col-sm-12 col-md-10">
                    <hl:htmlinput name="periodoFim" di="periodoFim" type="text" value="<%=TextHelper.forHtmlAttribute(valueFim )%>" others="<%=TextHelper.forHtmlAttribute(others)%>" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
                  </div>
                </div>
              </div>
            </div>
            <div class="row" role="group" aria-labelledby="periodoDataHora">
              <div class="col-sm-6">
                <div class="row">
                  <div class="form-check col-sm-12 col-md-2">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="horaIni" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
                    </div>
                  </div>
                  <div class="form-check col-sm-12 col-md-10">
                    <hl:htmlinput name="horaIni" di="horaIni" type="text" value="<%=TextHelper.forHtmlAttribute(valueHoraIni )%>" others="<%=TextHelper.forHtmlAttribute(others)%>" classe="form-control w-100" size="8" mask="DD:DD:DD" onBlur="setTimeDefault(this, '00:00:00');" placeHolder="<%=LocaleHelper.getTimePlaceHolder()%>" />
                  </div>
                </div>
              </div>
              <div class="col-sm-6">
                <div class="row">
                  <div class="form-check col-sm-12 col-md-2">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="horaFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
                    </div>
                  </div> 
                  <div class="form-check col-sm-12 col-md-10">
                    <hl:htmlinput name="horaFim" di="horaFim" type="text" value="<%=TextHelper.forHtmlAttribute(valueHoraFim )%>" others="<%=TextHelper.forHtmlAttribute(others)%>" classe="form-control w-100" size="8" mask="DD:DD:DD" onBlur="setTimeDefault(this, '23:59:59');" placeHolder="<%=LocaleHelper.getTimePlaceHolder()%>"/>
                  </div>
                </div>
              </div>
            </div>
          </div>

    <% if (obrDataHoraPage.equals("true")) { %>
       <script type="text/JavaScript">
       function funDataHoraPage() {
          camposObrigatorios = camposObrigatorios + 'periodoIni,';
          camposObrigatorios = camposObrigatorios + 'periodoFim,';
          camposObrigatorios = camposObrigatorios + 'horaIni,';
          camposObrigatorios = camposObrigatorios + 'horaFim,';

          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.periodo.inicio"/>,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.periodo.fim"/>,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.hora.periodo.inicio"/>,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.hora.periodo.fim"/>,';
  	   }
       addLoadEvent(funDataHoraPage); 
       </script>
    <% } %>
    
        <script type="text/JavaScript">
         function valida_campo_data_hora() {
             with(document.forms[0]) {
               if (periodoIni != null && periodoIni.value != '' && (!verificaData(periodoIni.value))) {
                 periodoIni.focus();
                 return false;
               }
               if (periodoFim != null && periodoFim.value != '' && (!verificaData(periodoFim.value))) {
                 periodoFim.focus();
                 return false;
               }
               if ((horaIni != null) && (!verificaHora(horaIni.value))) {
                  horaIni.focus();
                  return false;
               }
               if ((horaFim != null) && (!verificaHora(horaFim.value))) {
                  horaFim.focus();
                  return false;
               }               
               if (periodoIni != null && periodoFim != null) {
            	 var PartesData = new Array();
            	 PartesData = obtemPartesData(periodoIni.value);
            	 var Dia = PartesData[0];
            	 var Mes = PartesData[1];
            	 var Ano = PartesData[2];
            	 PartesData = obtemPartesData(periodoFim.value);
            	 var DiaFim = PartesData[0];
            	 var MesFim = PartesData[1];
            	 var AnoFim = PartesData[2];

            	 if (horaIni != null && horaFim != null) {
                   var diasDif = ''; 
                   <% if (!diasDataHoraPage.equals("")) { %>
                       diasDif = document.getElementById('agendadoSim') == null || !document.getElementById('agendadoSim').checked ? <%=TextHelper.forJavaScriptBlock(diasDataHoraPage)%> : "";
                   <% } %>                   
                   var PartesHora = new Array();
                   PartesHora = horaIni.value.split(':');
                   var Hora = PartesHora[0];
                   var Minuto = PartesHora[1];
                   var Segundo = PartesHora[2];
                   PartesHora = horaFim.value.split(':');
                   var HoraFim = PartesHora[0];
                   var MinutoFim = PartesHora[1];
                   var SegundoFim = PartesHora[2];
                   if (!VerificaPeriodoExt(Dia, Mes, Ano, DiaFim, MesFim, AnoFim, diasDif, Hora, Minuto, Segundo, HoraFim, MinutoFim, SegundoFim)) {
					  if(document.getElementById('agendadoSim') != null && !document.getElementById('agendadoSim').checked){
                      	alert('<hl:message key="mensagem.erro.relatorio.agendar.periodo"/>');
	               	  	document.getElementById('agendadoSim').checked = true;
	             	  	habilitaDesabilitaAgendamento();
					  }
                      return false;
                   }                           
            	 }
               }
             }        	 
             return true;
         }
        </script>        
             
