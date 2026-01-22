<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
  String obrOrigContratoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  
  String [] chkOrigem = request.getParameterValues("chkOrigem");
  List valueList = null;
  if (chkOrigem != null) {
     valueList = Arrays.asList(chkOrigem);
  }
  
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
          <fieldset class="col-sm-12 col-md-12">
            <div class="legend">
              <span>${descricoes[recurso]}</span>
            </div>
            <div class="form-check">
              <div class="row">
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkOrigem" ID="chkOrigem1" TITLE="<hl:message key="rotulo.consignacao.origem.renegociacao"/>" VALUE="<%=(String)CodedValues.ORIGEM_ADE_RENEGOCIADA%>" <%if (chkOrigem != null && valueList.contains(CodedValues.ORIGEM_ADE_RENEGOCIADA)) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkOrigem1"><hl:message key="rotulo.consignacao.origem.renegociacao"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkOrigem" ID="chkOrigem2" TITLE="<hl:message key="rotulo.consignacao.origem.compra"/>" VALUE="<%=(String)CodedValues.ORIGEM_ADE_COMPRADA%>" <%if (chkOrigem != null && valueList.contains(CodedValues.ORIGEM_ADE_COMPRADA)) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %>  onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkOrigem2"><hl:message key="rotulo.consignacao.origem.compra"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkOrigem" ID="chkOrigem3" TITLE="<hl:message key="rotulo.consignacao.origem.novo.contrato"/>" VALUE="<%=(String)CodedValues.ORIGEM_ADE_NOVA%>" <%if (chkOrigem != null && valueList.contains(CodedValues.ORIGEM_ADE_NOVA)) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkOrigem3"><hl:message key="rotulo.consignacao.origem.novo.contrato"/></label>
                  </span>
                </div>
              </div>
            </div>
          </fieldset>

    <% if (obrOrigContratoPage.equals("true")) { %>
        <script type="text/JavaScript">
        function funOrigContratoPage() {
            camposObrigatorios = camposObrigatorios + 'chkOrigem,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.origem.contrato"/>,';
        }
        addLoadEvent(funOrigContratoPage);     
        </script>
    <% } %>                                  

        <script type="text/JavaScript">
         function valida_campo_origem_contrato() {
        	 if (getCheckedRadio('form1', 'chkOrigem') && getCheckedRadio('form1', 'chkTermino')) {
                 alert(mensagem('mensagem.erro.filtros.origem.termino.contrato.nao.podem.ser.aplicados.simultaneamente'));
        		 return false;
        	 }
             return true;
         }
        </script>        
