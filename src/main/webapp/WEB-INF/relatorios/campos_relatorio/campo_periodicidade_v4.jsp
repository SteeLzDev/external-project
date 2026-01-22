<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<%
  AcessoSistema responsavelPeriodicidadePage = JspHelper.getAcessoSistema(request);
  String obrPeriodicidadePage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descPeriodicidadePage = pageContext.getAttribute("descricao").toString();   
  String periodicidade = (String) JspHelper.verificaVarQryStr(request, "periodicidade");
  
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
                <div class="form-group col-sm-12  col-md-6">
                  <label id="lblPeriodicidadePage" for="periodicidade"><%=TextHelper.forHtmlContent(descPeriodicidadePage)%></label>
                  <INPUT TYPE="text" NAME="periodicidade" ID="periodicidade" di="periodicidade" CLASS="form-control" SIZE="2" <% if (!TextHelper.isNull(periodicidade) || desabilitado) { %>disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#D2',true);" onBlur="fout(this);ValidaMascaraV4(this);" value="1" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelPeriodicidadePage, descPeriodicidadePage)%>">
                </div>
          
      <script type="text/JavaScript">
      <%if (obrPeriodicidadePage.equals("true")) {%>
      function funPeriodicidadePage() {
        camposObrigatorios = camposObrigatorios + 'periodicidade,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.periodicidade"/>,';
      }
      addLoadEvent(funPeriodicidadePage);     
      <%}%>

      function valida_campo_periodicidade() {
        if (document.forms[0].dataPrevista != null && !document.forms[0].periodicidade.disabled) {
          var periodo = parseInt(document.forms[0].periodicidade.value);
          if (periodo < 1) {
        	document.forms[0].periodicidade.focus();
            alert('<hl:message key="mensagem.erro.periodicidade.maior.zero"/>');
            return false;
          }
        }
        return true;
      }
      </script>
