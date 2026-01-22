<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% 
AcessoSistema responsavelBeneficioPage = JspHelper.getAcessoSistema(request);
String benCodigo = JspHelper.verificaVarQryStr(request, "BEN_CODIGO");
  
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true  :false;

String fieldValue = Columns.BEN_CODIGO + ";" + Columns.BEN_DESCRICAO;
String fieldLabel = Columns.BEN_DESCRICAO;
String rotuloTodos = ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelBeneficioPage);

List<TransferObject> beneficios = (List<TransferObject>) request.getAttribute("listaBeneficio");
%>
              <div class="form-group col-sm-12 col-md-6">
                <label id="lblBeneficioEndPage" for="BEN_CODIGO">${descricoes[recurso]}</label>
                <%=JspHelper.geraCombo(beneficios, "BEN_CODIGO", fieldValue, fieldLabel, rotuloTodos, null, false, 1, benCodigo, null, desabilitado, "form-control")%>
              </div>

              <script type="text/JavaScript">
              function valida_campo_beneficio() {
                 return true;
              }
              </script>
