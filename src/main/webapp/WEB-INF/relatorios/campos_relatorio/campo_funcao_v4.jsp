<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
String obrFunPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
boolean desabilitado = (JspHelper.verificaVarQryStr(request, "disabled").equals("true"));
String funcao = JspHelper.verificaVarQryStr(request, "funCodigo");
AcessoSistema responsavelFunPage = JspHelper.getAcessoSistema(request);
List<TransferObject> funcoes = (List<TransferObject>) request.getAttribute("listaFuncoes");
%>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblFuncaoFunPage" for="funCodigo">${descricoes[recurso]}</label>
            <%=JspHelper.geraCombo(funcoes, "funCodigo", Columns.FUN_CODIGO, Columns.GRF_DESCRICAO + ";" + Columns.FUN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelFunPage), null, false, 3, funcao, null, desabilitado, "form-control")%>
          </div>

  <% if (obrFunPage.equals("true")) { %>          
      <script type="text/JavaScript">
      function funFuncaoPage() {
          camposObrigatorios = camposObrigatorios + 'funCodigo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.funcao"/>,';
      }
      addLoadEvent(funFuncaoPage);     
      </script>          
  <% } %>
  
        <script type="text/JavaScript">
         function valida_campo_funcao() {
             return true;
         }
        </script>        
  