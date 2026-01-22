<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelFmtPage = JspHelper.getAcessoSistema(request);
   String obrFmtPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String formato = (String) JspHelper.verificaVarQryStr(request, "formato");
   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblFormatoFmtPage" for="formato">${descricoes[recurso]}</label>
            <select name="formato" id="formato" class="form-control" nf="btnEnvia" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <% if (!TextHelper.isNull(formato) || desabilitado) { %>disabled <%} %>>
              <option value="" <% if (TextHelper.isNull(formato)) { %>SELECTED <%} %>><hl:message key="rotulo.campo.selecione"/></option>            
              <option value="TEXT" <% if (!TextHelper.isNull(formato) && formato.equals("TEXT")) { %>SELECTED <%} %>>TXT</option>                
              <option value="XLS" <% if (!TextHelper.isNull(formato) && formato.equals("XLS")) { %>SELECTED <%} %>>XLS</option>
              <option value="ODT" <% if (!TextHelper.isNull(formato) && formato.equals("ODT")) { %>SELECTED <%} %>>ODT</option>
          	  <option value="ODS" <% if (!TextHelper.isNull(formato) && formato.equals("ODS")) { %>SELECTED <%} %>>ODS</option>                
            </select>
          </div>
          

    <% if (obrFmtPage.equals("true")) { %>                    
      <script type="text/JavaScript">
      function funFmtPage() {
          camposObrigatorios = camposObrigatorios + 'formato,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.formato"/>,';
      }
      addLoadEvent(funFmtPage);     
      </script>
    <% } %>                       

        <script type="text/JavaScript">
         function valida_campo_formato_relatorio_XLS_TXT() {
             return true;
         }
        </script>        
