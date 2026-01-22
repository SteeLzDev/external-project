<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.CamposRelatorioSinteticoEnum"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   String obrInfoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   AcessoSistema responsavelInfoPage = JspHelper.getAcessoSistema(request);
   
   String [] informacoes = request.getParameterValues("chkCAMPOS");
   List valueList = null;
   if (informacoes != null) {
      valueList = Arrays.asList(informacoes);
   }
   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
 %>
          <fieldset class="col-sm-12 col-md-12">
            <div class="legend"><span>${descricoes[recurso]}</span></div>
            <div class="form-check">
              <div class="row">
                <% if (responsavelInfoPage.isCsa() || (responsavelInfoPage.isCor() && responsavelInfoPage.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) { %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS1" TITLE="<hl:message key="rotulo.correspondente.singular"/>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_CORRESPONDENTE.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_CORRESPONDENTE.getCodigo())%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key= "rotulo.correspondente.singular"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS1"><hl:message key= "rotulo.correspondente.singular"/></label>
                  </span>
                </div>
                <% } else if (responsavelInfoPage.isCseSupOrg()) { %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS2" TITLE="<hl:message key="rotulo.consignataria.singular"/>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo())%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.consignataria.singular"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS2"><hl:message key="rotulo.consignataria.singular"/></label>
                  </span>
                </div>
                <% } %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS3" TITLE="<hl:message key="rotulo.consignacao.status.contrato"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_STATUS.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_STATUS.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.consignacao.status.contrato"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS3"><hl:message key="rotulo.consignacao.status.contrato"/></label>
                  </span>
                </div>
                <% if (responsavelInfoPage.isCseSup() || responsavelInfoPage.isCsaCor() || (responsavelInfoPage.isOrg() && responsavelInfoPage.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO))) { %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS4" TITLE="<hl:message key="rotulo.orgao.singular"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_ORGAO.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_ORGAO.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.orgao.singular"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS4"><hl:message key="rotulo.orgao.singular"/></label>
                  </span>
                </div>
                <% } %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS5" TITLE="<hl:message key="rotulo.convenio.codigo.verba"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_VERBA.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_VERBA.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.convenio.codigo.verba"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS5"><hl:message key="rotulo.convenio.codigo.verba"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS6" TITLE="<hl:message key="rotulo.servico.singular"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_SERVICO.getCodigo())%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_SERVICO.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key= "rotulo.servico.singular"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS6"><hl:message key= "rotulo.servico.singular"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS7" TITLE="<hl:message key="rotulo.relatorio.ade.data.inicial"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_DATA.getCodigo())%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_DATA.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.relatorio.ade.data.inicial"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS7"><hl:message key="rotulo.relatorio.ade.data.inicial"/></label>
                  </span>
                </div>
                <% if (responsavelInfoPage.isCseSup() || responsavelInfoPage.isCsaCor()) { %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS8" TITLE="<hl:message key="rotulo.estabelecimento.singular"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.estabelecimento.singular"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS8"><hl:message key="rotulo.estabelecimento.singular"/></label>
                  </span>
                </div>
                <% } %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS9" TITLE="<hl:message key="rotulo.relatorio.qtd.media.parcelas"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_MEDIA_QTD_PARCELAS.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_MEDIA_QTD_PARCELAS.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.relatorio.qtd.media.parcelas"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS9"><hl:message key="rotulo.relatorio.qtd.media.parcelas"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS10" TITLE="<hl:message key="rotulo.relatorio.valor.media.parcelas"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_VALOR_MEDIO_PARCELAS.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_VALOR_MEDIO_PARCELAS.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.relatorio.valor.media.parcelas"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS10"><hl:message key="rotulo.relatorio.valor.media.parcelas"/></label>
                  </span>
                </div>         
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS11" TITLE="<hl:message key="rotulo.relatorio.qtd.media.parcelas.pagas"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_MEDIA_QNTD_PARCELAS_PAGAS.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_MEDIA_QNTD_PARCELAS_PAGAS.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.relatorio.qtd.media.parcelas.pagas"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS11"><hl:message key="rotulo.relatorio.qtd.media.parcelas.pagas"/></label>
                  </span>
                </div>         
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS12" TITLE="<hl:message key="rotulo.relatorio.capital.devido"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_CAPITAL_DEVIDO.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_CAPITAL_DEVIDO.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.relatorio.capital.devido"/>', this.value);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS12"><hl:message key="rotulo.relatorio.capital.devido"/></label>
                  </span>
                </div>                                
              </div>
            </div>
          </fieldset>

    <% if (obrInfoPage.equals("true")) { %>                    
      <script type="text/JavaScript">
      function funInfoPage() {
          camposObrigatorios = camposObrigatorios + 'chkCAMPOS,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.info.grupo"/>,';
          
          $('input[name="chkCAMPOS"]').each(function () {
              if (this.checked) {
                  var label = $('label[for="' + $(this).attr('id') + '"]').text();
                  atualiza(true, document.forms[0].ORDENACAO, label, this.value);
              }
          });          
      }
      addLoadEvent(funInfoPage);     
      </script>
    <% } %>                           

        <script type="text/JavaScript">
         function valida_campo_info_group() {
             return true;
         }
        </script>        
