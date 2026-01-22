<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<% 
AcessoSistema responsavelCsaPage = JspHelper.getAcessoSistema(request);
String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");

String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

String fieldValue = Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME;
String fieldLabel = Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR;
String rotuloTodos = ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavelCsaPage);

List<TransferObject> operadorasBeneficios = (List<TransferObject>) request.getAttribute("listaOperadorasBeneficio");

if (operadorasBeneficios != null && !operadorasBeneficios.isEmpty()) {
%>
              <div class="form-group col-sm-12 col-md-6">
                <label id="lblOperadoraBeneficio" for="csaCodigoOperadora">${descricoes[recurso]}</label>
                <%=JspHelper.geraCombo(operadorasBeneficios, "csaCodigoOperadora", fieldValue, fieldLabel, rotuloTodos, null, false, 1, csaCodigo, null, desabilitado, "form-control")%>
              </div>
<% } %>

              <script type="text/JavaScript">
              function valida_campo_csa_natureza_beneficio() {
                 return true;
              }
              </script>
