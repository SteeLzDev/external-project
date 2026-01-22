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
  AcessoSistema responsavelPage = JspHelper.getAcessoSistema(request);
  String obrPercentualVariacaoMargemPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descPercentualVariacaoMargemPage = pageContext.getAttribute("descricao").toString();   
  String percentualVariacaoMargemInicio = (String) JspHelper.verificaVarQryStr(request, "percentualVariacaoMargemInicio");
  String percentualVariacaoMargemFim = (String) JspHelper.verificaVarQryStr(request, "percentualVariacaoMargemFim");
  
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
<div class="col-sm-12">  
  <div class="legend">
    <span id="lblPercentualVariacaoMargemPage" for="percentualVariacaoMargem"><%=TextHelper.forHtmlContent(descPercentualVariacaoMargemPage)%></span>
  </div>
  <div class="row" role="group">
    <div class="col-sm-2">
      <div class="row">
        <div class="form-check col-sm-2 col-md-2">
          <div class="float-left align-middle mt-4 form-control-label">
            <label for="percentualVariacaoMargemInicio" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
          </div>
        </div>
        <div class="form-check col-sm-10 col-md-10">
            <INPUT TYPE="number" min="0" max="100" NAME="percentualVariacaoMargemInicio" ID="percentualVariacaoMargemInicio" di="percentualVariacaoMargemInicio" CLASS="form-control" SIZE="3" <% if (!TextHelper.isNull(percentualVariacaoMargemInicio) || desabilitado) { %>disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#D3',true);" onBlur="fout(this);ValidaMascaraV4(this);" value="<%=TextHelper.forHtmlAttribute(!TextHelper.isNull(percentualVariacaoMargemInicio) ? percentualVariacaoMargemInicio : "")%>"  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelPage, ApplicationResourcesHelper.getMessage("rotulo.descricao.percentual.variacao.inicio", responsavelPage))%>">
        </div>
      </div>
    </div>
    <div class="col-sm-2">
      <div class="row">
        <div class="form-check col-sm-2 col-md-2">
          <div class="float-left align-middle mt-4 form-control-label">
            <label for="percentualVariacaoMargemFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
          </div>
        </div>
        <div class="form-check col-sm-10 col-md-10">
            <INPUT TYPE="number" min="0" max="100" NAME="percentualVariacaoMargemFim" ID="percentualVariacaoMargemFim" di="percentualVariacaoMargemFim" CLASS="form-control" SIZE="3" <% if (!TextHelper.isNull(percentualVariacaoMargemFim) || desabilitado) { %>disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#D3',true);" onBlur="fout(this);ValidaMascaraV4(this);" value="<%=TextHelper.forHtmlAttribute(!TextHelper.isNull(percentualVariacaoMargemFim) ? percentualVariacaoMargemFim : "")%>"  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelPage, ApplicationResourcesHelper.getMessage("rotulo.descricao.percentual.variacao.fim", responsavelPage))%>">
        </div>
      </div>
    </div>
  </div>
</div>          
<script type="text/JavaScript">
function valida_campo_percentual_variacao_margem() {
    <% if (obrPercentualVariacaoMargemPage.equals("true")) { %>
          var percentual = parseInt(document.forms[0].percentualVariacaoMargem.value);
          if (percentual == null) {
            document.forms[0].periodicidade.focus();
            alert('<hl:message key="mensagem.informe.percentual.variacao.margem"/>');
            return false;
          }
    <% } %>
  return true;  
}
</script>
