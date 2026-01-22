<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.CamposRelatorioSinteticoEnum"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   String obrInfoAdePage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   AcessoSistema responsavelInfoAdePage = JspHelper.getAcessoSistema(request);
   
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
                <% if (responsavelInfoAdePage.isCsa() || (responsavelInfoAdePage.isCor() && responsavelInfoAdePage.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) { %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX"  NAME="chkCAMPOS" ID="chkCAMPOS1" TITLE="<hl:message key="rotulo.correspondente.singular"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_CORRESPONDENTE.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_CORRESPONDENTE.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key= "rotulo.correspondente.singular"/>', '<%=TextHelper.forHtmlContent(CamposRelatorioSinteticoEnum.CAMPO_CORRESPONDENTE.getCodigo())%>');" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS1"><hl:message key= "rotulo.correspondente.singular"/></label>
                  </span>
                </div>
                <% } else if (responsavelInfoAdePage.isCseSupOrg()) { %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS2" TITLE="<hl:message key="rotulo.consignataria.singular"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.consignataria.singular"/>', '<%=TextHelper.forHtmlContent(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo())%>');" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS2"><hl:message key= "rotulo.consignataria.singular"/></label>                    
                  </span>
                </div>
                <% } %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS3" TITLE='<hl:message key="rotulo.consignacao.status.contrato"/>' VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_STATUS.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_STATUS.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.consignacao.status.contrato"/>', '<%=TextHelper.forHtmlContent(CamposRelatorioSinteticoEnum.CAMPO_STATUS.getCodigo())%>');" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS3"><hl:message key="rotulo.consignacao.status.contrato"/></label>
                  </span>
                </div>
                <% if (responsavelInfoAdePage.isCseSup() || responsavelInfoAdePage.isCsaCor() || (responsavelInfoAdePage.isOrg() && responsavelInfoAdePage.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO))) { %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS4" TITLE="<hl:message key="rotulo.orgao.singular"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_ORGAO.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_ORGAO.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.orgao.singular"/>', '<%=TextHelper.forHtmlContent(CamposRelatorioSinteticoEnum.CAMPO_ORGAO.getCodigo())%>');" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS4"><hl:message key="rotulo.orgao.singular"/></label>
                  </span>
                </div>
                <% } %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS5" TITLE="<hl:message key="rotulo.relatorio.ade.data.inicial"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_DATA_INI.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_DATA_INI.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.relatorio.ade.data.inicial"/>', '<%=TextHelper.forHtmlContent(CamposRelatorioSinteticoEnum.CAMPO_DATA_INI.getCodigo())%>');" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS5"><hl:message key="rotulo.relatorio.ade.data.inicial"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS6" TITLE="<hl:message key="rotulo.servico.singular"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_SERVICO.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_SERVICO.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key= "rotulo.servico.singular"/>', '<%=TextHelper.forHtmlContent(CamposRelatorioSinteticoEnum.CAMPO_SERVICO.getCodigo())%>');" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS6"><hl:message key= "rotulo.servico.singular"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS7" TITLE="<hl:message key="rotulo.relatorio.periodo.inclusao"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_DATA.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_DATA.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.relatorio.periodo.inclusao"/>', '<%=TextHelper.forHtmlContent(CamposRelatorioSinteticoEnum.CAMPO_DATA.getCodigo())%>');" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS7"><hl:message key="rotulo.relatorio.periodo.inclusao"/></label>
                  </span>
                </div>
                <% if (responsavelInfoAdePage.isCseSup() || responsavelInfoAdePage.isCsaCor()) { %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS8" TITLE="<hl:message key="rotulo.estabelecimento.singular"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.estabelecimento.singular"/>', '<%=TextHelper.forHtmlContent(CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo())%>');" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS8"><hl:message key="rotulo.estabelecimento.singular"/></label>
                  </span>
                </div>
                <% } %>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS9" TITLE="<hl:message key="rotulo.relatorio.ade.data.final"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_DATA_FIM.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_DATA_FIM.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.relatorio.ade.data.final"/>', '<%=TextHelper.forHtmlContent(CamposRelatorioSinteticoEnum.CAMPO_DATA_FIM.getCodigo())%>');" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS9"><hl:message key="rotulo.relatorio.ade.data.final"/></label>
                  </span>
                </div>
                <div class="col-sm-12 col-md-4">
                  <span class="text-nowrap align-text-top">
                    <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkCAMPOS" ID="chkCAMPOS10" TITLE="<hl:message key="rotulo.natureza.servico.titulo"/>" VALUE="<%=TextHelper.forHtmlAttribute(CamposRelatorioSinteticoEnum.CAMPO_NATUREZA_SERVICO.getCodigo())%>" <%if (informacoes != null && valueList.contains(CamposRelatorioSinteticoEnum.CAMPO_NATUREZA_SERVICO.getCodigo())) {%> checked disabled <%} else if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onClick="atualiza(this.checked, document.forms[0].ORDENACAO, '<hl:message key="rotulo.natureza.servico.titulo"/>', '<%=TextHelper.forHtmlContent(CamposRelatorioSinteticoEnum.CAMPO_NATUREZA_SERVICO.getCodigo())%>');" onBlur="fout(this);ValidaMascaraV4(this);">
                    <label class="form-check-label labelSemNegrito ml-1" for="chkCAMPOS10"><hl:message key="rotulo.natureza.servico.titulo"/></label>
                  </span>
                </div>
              </div>
            </div>
          </fieldset>

    <% if (obrInfoAdePage.equals("true")) { %>                    
      <script type="text/JavaScript">
      function funInfoAdePage() {
          camposObrigatorios = camposObrigatorios + 'chkCAMPOS,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.info.grupo"/>,';
          
          $('input[name="chkCAMPOS"]').each(function () {
              if (this.checked) {
                  var label = $('label[for="' + $(this).attr('id') + '"]').text();
                  atualiza(true, document.forms[0].ORDENACAO, label, this.value);
              }
          });          
      }
      addLoadEvent(funInfoAdePage);     
      </script>
    <% } %>                           

        <script type="text/JavaScript">
         function valida_campo_info_group_ade() {
             return true;
         }
        </script>        
