<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>

<% 
   AcessoSistema responsavelDataInclusaoTodosPage = JspHelper.getAcessoSistema(request);
   String paramDataInclusaoTodosPage = JspHelper.verificaVarQryStr(request, "PARAMETRO");
   String diasDataInclusaoTodosPage = JspHelper.getParametroDifDatasRelatorio(paramDataInclusaoTodosPage, responsavelDataInclusaoTodosPage);
   String obrDataInclusaoTodosPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   
   String periodoIni = (String) JspHelper.verificaVarQryStr(request, "periodoIni");
   String periodoFim = (String) JspHelper.verificaVarQryStr(request, "periodoFim");
   
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
   
   String [] chkTodos = request.getParameterValues("chkTodos");
   List valueList = null;
   if (chkTodos != null) {
      valueList = Arrays.asList(chkTodos);
   }
%>
          <div class="form-group col-sm-12 col-md-6">
            <span id="dataInclusaoTodos">${descricoes[recurso]}</span>
            <div class="row" role="group" aria-labelledby="dataInclusaoTodos">
              <div class="col-sm-6"> 
                <div class="row">
                  <div class="form-check col-sm-2 col-md-2">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
                    </div>
                  </div>
                  <div class="form-check col-sm-10 col-md-10">
                    <hl:htmlinput name="periodoIni" di="periodoIni" type="text" value="<%=TextHelper.forHtmlAttribute(valueIni )%>" others="<%=TextHelper.forHtmlAttribute(others)%>" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
                  </div>
                </div>
              </div>
              <div class="col-sm-6">
                <div class="row">
                  <div class="form-check col-sm-2 col-md-2">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
                    </div>
                  </div>
                  <div class="form-check col-sm-10 col-md-10">
                    <hl:htmlinput name="periodoFim" di="periodoFim" type="text" value="<%=TextHelper.forHtmlAttribute(valueFim )%>" others="<%=TextHelper.forHtmlAttribute(others)%>" classe="form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="d-flex align-items-center" style="height: 100px">
            <span class="text-nowrap">
              <div class="form-check">
                <INPUT TYPE="CHECKBOX" NAME="chkTodos" ID="chkTodos" VALUE="1" class="form-check-input ml-1" title="<hl:message key="rotulo.campo.todos.simples"/>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (chkTodos != null && valueList.contains("1")) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);">
                <label class="form-check-label labelSemNegrito ml-1" for="chkTodos"><hl:message key="rotulo.campo.todos.simples"/></label>
              </div>
            </span>
          </div>
          
         <script type="text/JavaScript">        
         function valida_campo_data_inclusao_todos() {
             with(document.forms[0]) {          
               if (chkTodos.checked) {
                 periodoIni.value = '';
                 periodoFim.value = '';
                 return true;                
              <% if (obrDataInclusaoTodosPage.equals("true")) { %>  
               } else {
                      var lcontroles = new Array();
                      lcontroles[0] = 'periodoIni';      
                      lcontroles[1] = 'periodoFim';      
                      var lmsgs = new Array();                      
                      lmsgs[0] = '<hl:message key="mensagem.informe.data.inclusao.inicio"/>';  
                      lmsgs[1] = '<hl:message key="mensagem.informe.data.inclusao.fim"/>';                      
                      if (!ValidaCampos(lcontroles, lmsgs)) {
                         return false;
                      }                   
              <% } %>
               }               
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
                   <% if (!diasDataInclusaoTodosPage.equals("")) { %>
                        diasDif = document.getElementById('agendadoSim') == null || !document.getElementById('agendadoSim').checked ? <%=TextHelper.forJavaScriptBlock(diasDataInclusaoTodosPage)%> : "";
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
