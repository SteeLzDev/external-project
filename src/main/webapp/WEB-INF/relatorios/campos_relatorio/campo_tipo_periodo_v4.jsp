<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
   AcessoSistema responsavelTipoPeriodoPage = JspHelper.getAcessoSistema(request);

   String obrTipoPeriodoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String tipoPeriodo = (String) JspHelper.verificaVarQryStr(request, "tipoPeriodo");
   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
   
   List tiposPeriodo = new ArrayList<CustomTransferObject>();
   
   CustomTransferObject opcao1ComboTipoPeriodo = new CustomTransferObject();
   opcao1ComboTipoPeriodo.setAttribute("chave", CodedValues.TIPO_PERIODO_INCLUSAO + ";" + ApplicationResourcesHelper.getMessage("rotulo.combo.tipo.periodo.inclusao", responsavelTipoPeriodoPage));
   opcao1ComboTipoPeriodo.setAttribute("valor", ApplicationResourcesHelper.getMessage("rotulo.combo.tipo.periodo.inclusao", responsavelTipoPeriodoPage));
   tiposPeriodo.add(0, opcao1ComboTipoPeriodo);
   
   CustomTransferObject opcao2ComboTipoPeriodo = new CustomTransferObject();
   opcao2ComboTipoPeriodo.setAttribute("chave", CodedValues.TIPO_PERIODO_ALTERACAO_MAIOR + ";" + ApplicationResourcesHelper.getMessage("rotulo.combo.tipo.periodo.alteracao.maior", responsavelTipoPeriodoPage));
   opcao2ComboTipoPeriodo.setAttribute("valor", ApplicationResourcesHelper.getMessage("rotulo.combo.tipo.periodo.alteracao.maior", responsavelTipoPeriodoPage));
   tiposPeriodo.add(1, opcao2ComboTipoPeriodo);
   
   CustomTransferObject opcao3ComboTipoPeriodo = new CustomTransferObject();
   opcao3ComboTipoPeriodo.setAttribute("chave", CodedValues.TIPO_PERIODO_ALTERACAO_MENOR + ";" + ApplicationResourcesHelper.getMessage("rotulo.combo.tipo.periodo.alteracao.menor", responsavelTipoPeriodoPage));
   opcao3ComboTipoPeriodo.setAttribute("valor", ApplicationResourcesHelper.getMessage("rotulo.combo.tipo.periodo.alteracao.menor", responsavelTipoPeriodoPage));
   tiposPeriodo.add(2, opcao3ComboTipoPeriodo);
   
   CustomTransferObject opcao4ComboTipoPeriodo = new CustomTransferObject();
   opcao4ComboTipoPeriodo.setAttribute("chave", CodedValues.TIPO_PERIODO_EXCLUSAO + ";" + ApplicationResourcesHelper.getMessage("rotulo.combo.tipo.periodo.exclusao", responsavelTipoPeriodoPage));
   opcao4ComboTipoPeriodo.setAttribute("valor", ApplicationResourcesHelper.getMessage("rotulo.combo.tipo.periodo.exclusao", responsavelTipoPeriodoPage));
   tiposPeriodo.add(3, opcao4ComboTipoPeriodo);
   
   String tipoPeriodoValue = "";
   String desabilitadoTipoPeriodo = "";
   if (!TextHelper.isNull(tipoPeriodo)) {
       tipoPeriodoValue = tipoPeriodo;
       desabilitadoTipoPeriodo = "disabled";
   }  
   
   if (desabilitado) {
       desabilitadoTipoPeriodo = "disabled";
   }
%>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblTipoPeriodoTagPage" for="tipoPeriodo">${descricoes[recurso]}</label>              
          <%if (TextHelper.isNull(tipoPeriodoValue) && !desabilitado) { %>
            <%=JspHelper.geraCombo(tiposPeriodo, "tipoPeriodo", "chave", "valor", ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelTipoPeriodoPage), null, true, 1, tipoPeriodoValue, null, false, "form-control")%>
          <%} else if (!TextHelper.isNull(tipoPeriodoValue)) { %>
            <%=JspHelper.geraCombo(tiposPeriodo, "tipoPeriodo", "chave", "valor" , ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelTipoPeriodoPage), null, true, 1, tipoPeriodoValue, null, true, "form-control")%>
          <%} else if (desabilitado) {%>
            <%=JspHelper.geraCombo(tiposPeriodo, "tipoPeriodo", "chave", "valor", ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelTipoPeriodoPage), null, true, 1, null, null, true, "form-control")%>
          <%} %>
          </div>

    <% if (obrTipoPeriodoPage.equals("true")) { %>                    
      <script type="text/JavaScript">
      function funTipoPeriodoPage() {
          camposObrigatorios = camposObrigatorios + ' tipoPeriodo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.periodo"/>,';
      }
      addLoadEvent(funTipoPeriodoPage);     
      </script>
    <% } %>
    
        <script type="text/JavaScript">
         function valida_campo_tipo_periodo() {
             with(document.forms[0]) {
           	 <% if (obrTipoPeriodoPage.equals("true")) { %> 
                 if (tipoPeriodo.value == "") {
               	    tipoPeriodo.focus();
                    alert('<hl:message key="mensagem.informe.tipo.periodo"/>');
                    return false;
                  }
             <% } %>
             }                                       
             return true;
         }
        </script>        
