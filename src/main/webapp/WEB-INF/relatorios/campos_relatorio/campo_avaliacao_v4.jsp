<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
AcessoSistema responsavelEntidadeComboPage = JspHelper.getAcessoSistema(request);
String descAvaliacaoPage = JspHelper.verificaVarQryStr(request, "DESCRICAO");   
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");

boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
    <div class="form-group col-sm-12 col-md-6">
      <label id="lblMensagens" for="avaliacaoCombo">${descricoes[recurso]}</label>
      <SELECT NAME="avaliacaoCombo" id="avaliacaoCombo" CLASS="form-control" <%=desabilitado ? "disabled" : "" %>>
          <OPTION VALUE=""><hl:message key="rotulo.campo.todos"/></OPTION>
          <OPTION VALUE="1"><hl:message key="rotulo.relatorio.avaliacao.util"/></OPTION>
          <OPTION VALUE="0"><hl:message key="rotulo.relatorio.avaliacao.inutil"/></OPTION>
      </SELECT>
    </div>
    
    <script type="text/JavaScript">
    	function valida_campo_avaliacao() {
        	return true;
		}
	</script>
