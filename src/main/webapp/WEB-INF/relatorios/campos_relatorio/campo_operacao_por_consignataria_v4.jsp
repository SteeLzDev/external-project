<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
AcessoSistema responsavelOpePorCsa = JspHelper.getAcessoSistema(request);
String obrOpePorCsaPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String nseCodigo = (String) JspHelper.verificaVarQryStr(request, "nseCodigo");

String descNseSelecPage = ApplicationResourcesHelper.getMessage("rotulo.relatorio.servicooperacaomes.natureza.servico", responsavelOpePorCsa);
List<TransferObject> naturezas = (List<TransferObject>) request.getAttribute("listaNaturezasServico");

boolean desabilitadoNse = true;
String style = "";
if (!JspHelper.verificaVarQryStr(request, "operacaoPorConsignataria").equals("true")) {
   style = "style=\"display: none\"";
   desabilitadoNse = true;
} else {
   style = "style=\"display: ''\"";
   desabilitadoNse = false;
}
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
          <div class="col-sm-12">
            <fieldset>
              <div class="legend"><span>${descricoes[recurso]}</span></div>
              <div class="row">
                <div class="col-sm-12 col-md-6">
                  <div class="form-check form-check-inline pt-2" role="radiogroup" aria-labelledby="operacaoPorConsignataria">
                    <input type="radio" name="operacaoPorConsignataria" id="operacaoPorConsignataria2" title="<hl:message key="rotulo.sim"/>" value="true" <%= JspHelper.verificaVarQryStr(request, "operacaoPorConsignataria").equals("true") ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled" : "")%> onChange="habilitaCampoNatureza();">
                    <label class="form-check-label labelSemNegrito ml-1 pl-0 pr-4" for="operacaoPorConsignataria2"><hl:message key="rotulo.sim"/></label>
                    </div>
                    <div class="form-check-inline form-check">
                    <input type="radio" name="operacaoPorConsignataria" id="operacaoPorConsignataria1" title="<hl:message key="rotulo.nao"/>" value="false" <%=!JspHelper.verificaVarQryStr(request, "operacaoPorConsignataria").equals("true") ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled" : "")%> onChange="desabilitaCampoNatureza();">
                    <label class="form-check-label labelSemNegrito ml-1 pl-0 pr-4" for="operacaoPorConsignataria1"><hl:message key="rotulo.nao"/></label>
                  </div>
                </div>
              </div>
            </fieldset>
          </div>
          
          <div id="linhaNseCodigo" class="form-group col-sm-12 col-md-6" <%=(String)style%>>
            <label id="lblNaturezaNsePage" for="nseCodigo"><%=TextHelper.forHtmlContent(descNseSelecPage)%></label>
            <%if (TextHelper.isNull(nseCodigo) && !desabilitadoNse) { %>
               <%=JspHelper.geraCombo(naturezas, "nseCodigo", Columns.NSE_CODIGO + ";" + Columns.NSE_DESCRICAO, Columns.NSE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelOpePorCsa), null, true, 3, nseCodigo, null, false, "form-control")%>
            <%} else if (!TextHelper.isNull(nseCodigo)) { %>
               <%=JspHelper.geraCombo(naturezas, "nseCodigo", Columns.NSE_CODIGO + ";" + Columns.NSE_DESCRICAO, Columns.NSE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelOpePorCsa), null, true, 3, nseCodigo, null, true, "form-control")%>
            <%} else if (desabilitadoNse) {%>
               <%=JspHelper.geraCombo(naturezas, "nseCodigo", Columns.NSE_CODIGO + ";" + Columns.NSE_DESCRICAO, Columns.NSE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelOpePorCsa), null, true, 3, null, null, true, "form-control")%> 
            <%} %>
          <div class="slider col-sm-12 col-md-12 mt-2 pl-0 pr-0">
              <div class="tooltip-inner">Use Ctrl para selecionar v�rios.</div>
          </div>
          </div>          
          
          
          
          <script type="text/JavaScript">
           // habilita o campo de natureza de servi�o 
           function habilitaCampoNatureza() {
      		   f0.nseCodigo.disabled = false;   
      		   var linhaNaturezaServico = document.getElementById('linhaNseCodigo');
      		   linhaNaturezaServico.style.display = '';      		   
           }
           
           function desabilitaCampoNatureza() {
        	   // apaga sele��o 
        	   f0.nseCodigo.selectedIndex = 0;
        	   f0.nseCodigo.disabled = true;
        	   var linhaNaturezaServico = document.getElementById('linhaNseCodigo');
        	   linhaNaturezaServico.style.display = 'none';
           }
          </script>

    <% if (obrOpePorCsaPage.equals("true")) { %>
        <script type="text/JavaScript">
         function funOpePorCsaPage() {
            camposObrigatorios = camposObrigatorios + 'operacaoPorConsignataria,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.detalhar.consignataria"/>,';
         }
        addLoadEvent(funOpePorCsaPage);     
        </script>        
    <% } %>             

        <script type="text/JavaScript">
         function valida_campo_operacao_por_consignataria() {
      	 	 with(document.forms[0]) {
      	 	 	if (getCheckedRadio('form1', 'operacaoPorConsignataria') != 'false') {
      	 			// se o usu�rio escolhei detalhar por consignat�ria, garante que seja informado pelo menos uma natureza de servi�o  
      	 			for (var i = 0; i < f0.nseCodigo.length; i = i + 1) {
      	 				if (i != 0 && f0.nseCodigo.options[i].selected) {
      	 					return true;
      	 				}
      	 			}
  	 				alert('<hl:message key="mensagem.informe.relatorio.servicooperacaomes.natureza.servico"/>');
  	 				return false;
      	 	    }
        	 }
        	 return true;
         }
        </script>        

