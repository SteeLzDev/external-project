<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="hl" uri="/html-lib"%>

<%
   String obrDataExePage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String strDataPrevistaProx = (request.getParameter("dataPrevista") != null ? request.getParameter("dataPrevista") : (request.getAttribute("minDataExecucaoAgendamento") != null ? request.getAttribute("minDataExecucaoAgendamento").toString() : ""));
   boolean permiteAgendamentoMesmoDia = (request.getAttribute("permiteAgendamentoMesmoDia") != null);   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
   
   String others = "";   
   
   if (desabilitado) {
       others = "disabled";
   }
%>
                <div class="form-group col-sm-12 col-md-6">
                  <label id="lblDataPrevistaProx" for="dataPrevista">${descricoes[recurso]}</label>
                  <hl:htmlinput name="dataPrevista" di="dataPrevista" type="text" others="<%=TextHelper.forHtmlAttribute(others)%>" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(strDataPrevistaProx)%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"/>
                </div>
                <script type="text/JavaScript">
                <%if (obrDataExePage.equals("true")) {%>
                function funDataExePage() {
                  camposObrigatorios = camposObrigatorios + 'dataPrevista,';
                  msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.data.execucao"/>,';
                }
                addLoadEvent(funDataExePage);     
                <%}%>
                function valida_campo_data_execucao() {
                  with (document.forms[0]) {
                    if (dataPrevista != null && dataPrevista.value != '' && !dataPrevista.disabled) {
                      var partesDataPrevista = obtemPartesData(dataPrevista.value);

                      var dia = partesDataPrevista[0];
                      var mes = partesDataPrevista[1];
                      var ano = partesDataPrevista[2];
                      var dataExecucao = new Date(ano, mes - 1, dia);
                      dataExecucao.setHours(0,0,0,0);
                      var dataCorrente = new Date();
                      dataCorrente.setHours(0,0,0,0);
                      
                      if (!verificaData(dataPrevista.value)) {
                        dataPrevista.focus();
                        return false;
                      }
                      <% if (!permiteAgendamentoMesmoDia) { %>
                      if (dataExecucao.getTime() <= dataCorrente.getTime()) {
                        dataPrevista.focus();
                        alert('<hl:message key="mensagem.erro.data.execucao.maior.hoje"/>');
                        return false;
                      }
                      <% } else { %>
                      if (dataExecucao.getTime() < dataCorrente.getTime()) {
                        dataPrevista.focus();
                        alert('<hl:message key="mensagem.erro.data.execucao.maior.igual.hoje"/>');
                        return false;
                      }
                      <% } %>
                    }
                  }                                       
                  return true;
                }
                </script>
