<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.periodo.PeriodoHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="fl" uri="/function-lib"%>
<%
   AcessoSistema responsavelPeriodoPage = JspHelper.getAcessoSistema(request);
   String obrDataPeriodoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String periodo = (String) JspHelper.verificaVarQryStr(request, "periodo");
   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
   
   String value = "";
   String others = "placeholder=\"" + LocaleHelper.getPeriodoPlaceHolder() +"\" ";
   if (!TextHelper.isNull(periodo)) {
       value = TextHelper.forHtmlAttribute(periodo);
       others += "disabled";
   }  
   
   if (desabilitado) {
       others += "disabled";
   }
%>
              <div class="form-group col-sm-12 col-md-6">
                <label for="periodo">${descricoes[recurso]}</label>
                <hl:htmlinput name="periodo" di="periodo" type="text" value="<%=value%>" others="<%=others%>" classe="form-control" size="10" mask="DD/DDDD" placeHolder="<%=LocaleHelper.getPeriodoPlaceHolder()%>"/>
              </div>
              <script type="text/JavaScript">
            <% if (obrDataPeriodoPage.equals("true")) { %>
              function funDataPeriodoPage() {
                camposObrigatorios = camposObrigatorios + 'periodo,';
                msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.periodo"/>,';
              }
              addLoadEvent(funDataPeriodoPage);     
            <% } %>
              function valida_campo_data_periodo() {
                with(document.forms[0]) {
                <% if (obrDataPeriodoPage.equals("true")) { %>
                  if (periodo != null) {
                    if (!verificaPeriodo(periodo.value, '<%= PeriodoHelper.getPeriodicidadeFolha(responsavelPeriodoPage) %>')) {
                      periodo.focus();
                      return false;
                    }
                  }
                <% } %>
                }
                return true;
              }
              </script>
