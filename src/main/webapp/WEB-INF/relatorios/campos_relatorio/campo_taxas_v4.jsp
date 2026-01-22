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
AcessoSistema responsavelTaxPage = JspHelper.getAcessoSistema(request);
String obrTaxPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String ORDENACAO = JspHelper.verificaVarQryStr(request, "ORDENACAO");

String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
            <div class="form-group col-sm-12  col-md-6">
              <label id="lblOrdenacaoTaxPage" for="ORDENACAO">${descricoes[recurso]}</label>
              <select name="ORDENACAO" id="ORDENACAO" class="form-control form-select"
                      onFocus="SetarEventoMascaraV4(this,'#*200',true);"
                      onBlur="fout(this);ValidaMascaraV4(this);" <% if (!TextHelper.isNull(ORDENACAO) || desabilitado) { %>disabled <%} %> >
                <option value="CSA" <%if (TextHelper.isNull(ORDENACAO) || (!TextHelper.isNull(ORDENACAO) && ORDENACAO.equals("CSA"))) { %> selected <%} %>><hl:message key="rotulo.consignataria.singular"/></option>
<%
List<TransferObject> prazos = (List<TransferObject>) request.getAttribute("listaPrazosServico");
Iterator<TransferObject> it = prazos.iterator();
TransferObject cto = null;
while (it.hasNext()) {
  cto = it.next();
  String selected = (!TextHelper.isNull(ORDENACAO) && ORDENACAO.equals(cto.getAttribute(Columns.PRZ_VLR).toString())) ? "SELECTED":"";
  out.print("<option value=\"" + cto.getAttribute(Columns.PRZ_VLR).toString() + "\" " + selected  + ">" + ApplicationResourcesHelper.getMessage("rotulo.prazo.singular", responsavelTaxPage) + ": " + cto.getAttribute(Columns.PRZ_VLR).toString() + "</option>");
}
%>
                </select>
            </div>

    <% if (obrTaxPage.equals("true")) { %>            
          <script type="text/JavaScript">
          function funTaxPage() {
              camposObrigatorios = camposObrigatorios + 'ORDENACAO,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ordenacao"/>,';
          }
          addLoadEvent(funTaxPage);     
          </script>
    <% } %>    
        
        <script type="text/JavaScript">
         function valida_campo_taxas() {
             return true;
         }
        </script>        
