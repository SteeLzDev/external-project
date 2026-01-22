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
AcessoSistema responsavelFaqPage = JspHelper.getAcessoSistema(request);
String obrFaqPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");   
List<?> itensFaq = (List<?>) request.getAttribute("itensFaq");
%>
          <div class="form-group col-sm-12  col-md-12">
            <label id="lblFaq" for="faqCodigo">${descricoes[recurso]}</label>
            <SELECT NAME="faqCodigo" id="faqCodigo" class="form-control" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.nenhum"/></OPTION>
                <%
                  Iterator<?> iteFaq = itensFaq.iterator();
                  while (iteFaq.hasNext()) {
                      CustomTransferObject ctoFaq = (CustomTransferObject)iteFaq.next();
                      String fieldValueFaq = (String) ctoFaq.getAttribute(Columns.FAQ_CODIGO);
                      String fieldLabelFaq = (String) ctoFaq.getAttribute(Columns.FAQ_TITULO_1);
                %>
                <OPTION VALUE="<%=TextHelper.forHtmlAttribute(fieldValueFaq)%>;<%=TextHelper.forHtmlContent(fieldLabelFaq)%>"><%=TextHelper.forHtmlContent(fieldLabelFaq)%></OPTION>
                <%    
                  }
                %>
               </SELECT>
          </div>
          
    <% if (obrFaqPage.equals("true")) { %>
          <script type="text/JavaScript">
          function funFaqPage() {
              camposObrigatorios = camposObrigatorios + 'faqCodigo,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.faq"/>,';
          }
          addLoadEvent(funFaqPage);     
          </script>
    <% } %>             
         
        <script type="text/JavaScript">
         function valida_campo_faq() {
             return true;
         }
        </script>