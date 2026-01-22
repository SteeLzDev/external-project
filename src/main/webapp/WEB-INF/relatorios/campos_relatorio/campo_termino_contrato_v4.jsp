<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
  String obrTerminoContratoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");

  String [] chkTermino = request.getParameterValues("chkTermino");
  List valueList = null;
  if (chkTermino != null) {
      valueList = Arrays.asList(chkTermino);
  }
  
  String disabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(disabled) && disabled.equals("true")) ? true:false;
%>
          <fieldset class="col-sm-12 col-md-12">
            <div class="legend">
              <span>${descricoes[recurso]}</span>
            </div>
            <div class="form-check">
              <div class="row">
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkTermino" ID="chkTermino1" TITLE="<hl:message key="rotulo.relatorio.venda"/>" VALUE="<%=(String)CodedValues.TERMINO_ADE_VENDA%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (chkTermino != null && valueList.contains(CodedValues.TERMINO_ADE_VENDA)) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkTermino1"><hl:message key="rotulo.relatorio.venda"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkTermino" ID="chkTermino2" TITLE="<hl:message key="rotulo.relatorio.renegociacao"/>" VALUE="<%=(String)CodedValues.TERMINO_ADE_RENEGOCIADA%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (chkTermino != null && valueList.contains(CodedValues.TERMINO_ADE_RENEGOCIADA)) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkTermino2"><hl:message key="rotulo.relatorio.renegociacao"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkTermino" ID="chkTermino3" TITLE="<hl:message key="rotulo.relatorio.liquidacao.antecipada"/>" VALUE="<%=(String)CodedValues.TERMINO_ADE_LIQ_ANTECIPADA%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (chkTermino != null && valueList.contains(CodedValues.TERMINO_ADE_LIQ_ANTECIPADA)) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkTermino3"><hl:message key="rotulo.relatorio.liquidacao.antecipada"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkTermino" ID="chkTermino4" TITLE="<hl:message key="rotulo.relatorio.conclusao"/>" VALUE="<%=(String)CodedValues.TERMINO_ADE_CONCLUSAO%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (chkTermino != null && valueList.contains(CodedValues.TERMINO_ADE_CONCLUSAO)) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkTermino4"><hl:message key="rotulo.relatorio.conclusao"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkTermino" ID="chkTermino5" TITLE="<hl:message key="rotulo.relatorio.cancelamento"/>" VALUE="<%=(String)CodedValues.TERMINO_ADE_CANCELADA%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (chkTermino != null && valueList.contains(CodedValues.TERMINO_ADE_CANCELADA)) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkTermino5"><hl:message key="rotulo.relatorio.cancelamento"/></label>
                  </span>
                </div>
              </div> 
            </div>
          </fieldset>
          
    <% if (obrTerminoContratoPage.equals("true")) { %>            
         <script type="text/JavaScript">
          function funTerminoContratoPage() {
              camposObrigatorios = camposObrigatorios + 'chkTermino,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ade.termino.contrato"/>,';
          }
          addLoadEvent(funTerminoContratoPage);     
         </script>
    <% } %>     
 
        <script type="text/JavaScript">
         function valida_campo_termino_contrato() {
             if (getCheckedRadio('form1', 'chkOrigem') && getCheckedRadio('form1', 'chkTermino')) {
                 alert(mensagem('mensagem.erro.filtros.origem.termino.contrato.nao.podem.ser.aplicados.simultaneamente'));
                 return false;
             }
             return true;
         }
        </script>        
