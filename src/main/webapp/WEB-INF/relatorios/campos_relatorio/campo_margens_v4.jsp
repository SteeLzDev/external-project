<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.entidade.MargemTO" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
AcessoSistema responsavelMarSelecPage = JspHelper.getAcessoSistema(request);
String obrMarSelecPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");

String[] margensCodigosMarSelecPage = request.getParameterValues("MAR_CODIGO");
List<String> marCodigoMarSelecPage = new ArrayList<String>();
if (margensCodigosMarSelecPage != null && margensCodigosMarSelecPage.length > 0) {
   marCodigoMarSelecPage = Arrays.asList(margensCodigosMarSelecPage);
}
String paramDisabledMarSelecPage = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitadoMarSelecPage = (!TextHelper.isNull(paramDisabledMarSelecPage) && paramDisabledMarSelecPage.equals("true")) ? true : false;
%>
          <div class="form-group col-sm-12  col-md-6">
            <label id="lblMargem" for="MAR_CODIGO">${descricoes[recurso]}</label>            
            <select multiple size="5" name="MAR_CODIGO" id="MAR_CODIGO" class="form-control" onChange="carregaDadosFiltro();" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <% if (desabilitadoMarSelecPage) { %>disabled <%} %>>
              <option value="" <% if (TextHelper.isNull(marCodigoMarSelecPage)) { %>SELECTED  <%} %>><hl:message key="rotulo.campo.selecione"/></option>
<% 
List<MargemTO> marCodigosMarSelecPage = (List<MargemTO>) request.getAttribute("listaMargens");
Iterator<MargemTO> it = marCodigosMarSelecPage.iterator();
while (it.hasNext()) {
  MargemTO margem = it.next();
  String marCodigo = margem.getMarCodigo().toString();
  Character exibeMargem = ExibeMargem.NAO_EXIBE;
  if (responsavelMarSelecPage.isCse()) {
      exibeMargem = margem.getMarExibeCse();
  } else if (responsavelMarSelecPage.isSup()) {
      exibeMargem = margem.getMarExibeSup();
  } else if (responsavelMarSelecPage.isOrg()) {
      exibeMargem = margem.getMarExibeOrg();
  } else if (responsavelMarSelecPage.isCsa()) {
      exibeMargem = margem.getMarExibeCsa();
  } else if (responsavelMarSelecPage.isCor()) {
      exibeMargem = margem.getMarExibeCor();
  } else if (responsavelMarSelecPage.isSer()) {
      exibeMargem = margem.getMarExibeSer();
  }
  
  if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO.toString()) && !ExibeMargem.NAO_EXIBE.equals(exibeMargem)) {
%>
                  <option value="<%=TextHelper.forHtmlAttribute(marCodigo)%>" <% if (marCodigoMarSelecPage.contains(marCodigo)) { %>SELECTED  <%} %>><%=TextHelper.forHtmlContent(margem.getMarDescricao())%></option>
<%
  }
}
%>
            </select>
          </div>

    <% if (obrMarSelecPage.equals("true")) { %>
          <script type="text/JavaScript">
          function funMarSelecPage() {
              camposObrigatorios = camposObrigatorios + 'MAR_CODIGO,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.margem"/>,';
          }
          addLoadEvent(funMarSelecPage);     
          </script>
    <% } %>                       

        <script type="text/JavaScript">
         function valida_campo_margens() {
        	 var options = f0.MAR_CODIGO.options, count = 0;
        	 for (var i=0; i < options.length; i++) {
        	   if (options[i].selected) {
        		   count++;
        	   }
        	 }
        	 
        	 if (count > 3 && f0.formato.options[f0.formato.selectedIndex].value == 'PDF') {
        		 alert('<hl:message key="mensagem.erro.relatorio.pdf.somente.tres.margens"/>');
        		 return false;
        	 }
        	 
             return true;
         }
        </script>        
