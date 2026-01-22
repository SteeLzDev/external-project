<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
  String obrPrdRealizadoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  
  String [] chkPrdRealizado = request.getParameterValues("chkPrdRealizado");
  List valueList = null;
  if (chkPrdRealizado != null) {
     valueList = Arrays.asList(chkPrdRealizado);
  }
  
  String paramDisabledPrdRealizadoPage = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitadoPrdRealizadoPage = (!TextHelper.isNull(paramDisabledPrdRealizadoPage) && paramDisabledPrdRealizadoPage.equals("true")) ? true:false;
%>
          <fieldset class="col-sm-12 col-md-12">
            <div class="legend">
              <span>${descricoes[recurso]}</span>
            </div>
            <div class="form-check">
              <div class="row">
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkPrdRealizado" ID="chkPrdRealizado1" TITLE="<hl:message key="rotulo.relatorio.movimentofinanceiro.valor.menor.previsto"/>" VALUE="<%=(String)CodedValues.REL_FILTRO_VLR_REALIZADO_MENOR_PREVISTO%>" <%if (chkPrdRealizado != null && valueList.contains(CodedValues.REL_FILTRO_VLR_REALIZADO_MENOR_PREVISTO)) {%> checked disabled <%} else if (desabilitadoPrdRealizadoPage) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkPrdRealizado1"><hl:message key="rotulo.relatorio.movimentofinanceiro.valor.menor.previsto"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkPrdRealizado" ID="chkPrdRealizado2" TITLE="<hl:message key="rotulo.relatorio.movimentofinanceiro.valor.igual.previsto"/>" VALUE="<%=(String)CodedValues.REL_FILTRO_VLR_REALIZADO_IGUAL_PREVISTO%>" <%if (chkPrdRealizado != null && valueList.contains(CodedValues.REL_FILTRO_VLR_REALIZADO_IGUAL_PREVISTO)) {%> checked disabled <%} else if (desabilitadoPrdRealizadoPage) {%> disabled <%} %>  onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkPrdRealizado2"><hl:message key="rotulo.relatorio.movimentofinanceiro.valor.igual.previsto"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkPrdRealizado" ID="chkPrdRealizado3" TITLE="<hl:message key="rotulo.relatorio.movimentofinanceiro.valor.maior.previsto"/>" VALUE="<%=(String)CodedValues.REL_FILTRO_VLR_REALIZADO_MAIOR_PREVISTO%>" <%if (chkPrdRealizado != null && valueList.contains(CodedValues.REL_FILTRO_VLR_REALIZADO_MAIOR_PREVISTO)) {%> checked disabled <%} else if (desabilitadoPrdRealizadoPage) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkPrdRealizado3"><hl:message key="rotulo.relatorio.movimentofinanceiro.valor.maior.previsto"/></label>
                  </span>
                </div>
              </div>
            </div>
          </fieldset>

    <% if (obrPrdRealizadoPage.equals("true")) { %>
        <script type="text/JavaScript">
        function funPrdRealizadoPage() {
            camposObrigatorios = camposObrigatorios + 'chkPrdRealizado,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.prd.realizado"/>,';
        }
        addLoadEvent(funOrigContratoPage);     
        </script>
    <% } %>                                  

        <script type="text/JavaScript">
         function valida_campo_prd_realizado() {
             return true;
         }
        </script>        
