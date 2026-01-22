<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
AcessoSistema responsavelCsePage = JspHelper.getAcessoSistema(request);
String obrCsePage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");   
List<TransferObject> cseList = (List<TransferObject>) request.getAttribute("listaConsignantes");
%>
          <div class="form-group col-sm-12  col-md-6">
            <label id="lblConsignante" for="cseCodigo">${descricoes[recurso]}</label>
            <SELECT NAME="cseCodigo" id="cseCodigo" class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
              <OPTION VALUE="NENHUM" SELECTED><hl:message key="rotulo.campo.nenhum"/></OPTION>
              <%
                Iterator iteCse = cseList.iterator();
                while (iteCse.hasNext()) {
                    CustomTransferObject ctoCse = (CustomTransferObject)iteCse.next();
                    String fieldValueCse = ctoCse.getAttribute(Columns.CSE_CODIGO) + ";" + ctoCse.getAttribute(Columns.CSE_IDENTIFICADOR) + ";" + ctoCse.getAttribute(Columns.CSE_NOME);
                    String fieldLabelCse = ctoCse.getAttribute(Columns.CSE_NOME) + " - " + ctoCse.getAttribute(Columns.CSE_IDENTIFICADOR);
              %>
              <OPTION VALUE="<%=TextHelper.forHtmlAttribute(fieldValueCse)%>"><%=TextHelper.forHtmlContent(fieldLabelCse)%></OPTION>
              <%    
                }
              %>
             </SELECT>
          </div>
          
    <% if (obrCsePage.equals("true")) { %>
          <script type="text/JavaScript">
          function funCsePage() {
              camposObrigatorios = camposObrigatorios + 'cseCodigo,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.consignante"/>,';
          }
          addLoadEvent(funCsePage);     
          </script>
    <% } %>             
         
        <script type="text/JavaScript">
         function valida_campo_cse() {
             return true;
         }
        </script>        
 