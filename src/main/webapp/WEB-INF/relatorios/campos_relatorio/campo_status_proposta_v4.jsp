<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
String obrStpPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String descStpPage = JspHelper.verificaVarQryStr(request, "DESCRICAO");   
List<TransferObject> listaStatusProposta = (List<TransferObject>) request.getAttribute("listaStatusProposta");

String[] stpCodigo = request.getParameterValues("stpCodigo");
List<String> stpCodigos = (stpCodigo != null ? Arrays.asList(stpCodigo) : null);

String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true"));
%>

       <fieldset class="col-sm-12 col-md-12">
         <div class="legend">
           <span>${descricoes[recurso]}</span>
         </div>
         <div class="form-check">
           <div class="row">
           <% for (TransferObject next : listaStatusProposta) { %>
             <div class="col-sm-12 col-md-4">
               <span class="text-nowrap align-text-top">
                 <input class="form-check-input ml-1" type="checkbox" name="stpCodigo" id="stpCodigo<%= next.getAttribute(Columns.STP_CODIGO).toString() %>" title="<%= next.getAttribute(Columns.STP_DESCRICAO).toString() %>" <%if (stpCodigos != null && stpCodigos.contains(next.getAttribute(Columns.STP_CODIGO).toString())) {%> checked <%} if (desabilitado) {%> disabled <%} %> value="<%= next.getAttribute(Columns.STP_CODIGO).toString() %>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                 <label class="form-check-label labelSemNegrito ml-1" for="stpCodigo<%= next.getAttribute(Columns.STP_CODIGO).toString() %>"><%= next.getAttribute(Columns.STP_DESCRICAO).toString() %></label>
               </span>
             </div>
           <% } %>
           </div>
         </div>
       </fieldset>

        <script language="JavaScript" type="text/JavaScript">
         function valida_campo_status_proposta() {
           <% if (obrStpPage.equals("true")) { %>
            var descStp = '<%=TextHelper.forJavaScriptBlock(descStpPage)%>';
            var tam = document.forms[0].stpCodigo.length;
            var qtd = 0;
            for(var i = 0; i < tam; i++) {
              if (document.forms[0].stpCodigo[i].checked == true) {
                qtd++;
              }
            }
            if (qtd <= 0) {
              alert('<hl:message key="mensagem.informe.stp.status"/>');
              return false;
            }                         
           <% } %>            
            return true;
         }
        </script>          
