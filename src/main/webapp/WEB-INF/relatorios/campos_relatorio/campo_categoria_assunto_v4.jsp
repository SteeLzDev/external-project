<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavelAscPage = JspHelper.getAcessoSistema(request);
String obrAscPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String ascCodigo = (String) JspHelper.verificaVarQryStr(request, "ascCodigo");
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
         <div class="form-group col-sm-12  col-md-6">
           <label for="ASC_CODIGO">${descricoes[recurso]}</label>
           <SELECT NAME="ASC_CODIGO" id="ASC_CODIGO" class="form-control" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);" >
             <option value="" SELECTED><hl:message key="rotulo.campo.todas"/></option>
               <%
               List<TransferObject> assuntos = (List<TransferObject>) request.getAttribute("listaAssuntos");
               Iterator<TransferObject> iteratorAssunto = assuntos.iterator();
               TransferObject asc = null;
               String ascDescricao;
               String selectedAssunto = JspHelper.verificaVarQryStr(request, "ASC_CODIGO");
               while (iteratorAssunto.hasNext()) {
                 asc = iteratorAssunto.next();
                 ascCodigo = (String)asc.getAttribute(Columns.ASC_CODIGO);
                 ascDescricao = asc.getAttribute(Columns.ASC_DESCRICAO).toString();
                 %>
                 <option value="<%=TextHelper.forHtmlAttribute(ascCodigo)%>" <%= (selectedAssunto.equals(ascCodigo)) ? "SELECTED" : "" %>><%=TextHelper.forHtmlContent(ascDescricao)%></option>
            <% } %>
           </SELECT>
          </div>
          
    <% if (obrAscPage.equals("true")) { %>
      <script type="text/JavaScript">
      function funAscPage() {
          camposObrigatorios = camposObrigatorios + 'ascCodigo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.categoria.assunto"/>,';
      }
      addLoadEvent(funAscPage);     
      </script>
    <% } %>   
    
        <script type="text/JavaScript">
         function valida_campo_categoria_assunto() {
             return true;
         }
        </script>        
                
