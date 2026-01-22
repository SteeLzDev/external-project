<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.values.TipoSolicitacaoEnum"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
AcessoSistema responsavelEntidadeComboPage = JspHelper.getAcessoSistema(request);
String descTipoSaldoPage = JspHelper.verificaVarQryStr(request, "DESCRICAO");   
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");

boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
    <div class="form-group col-sm-12 col-md-6">
      <label id="lblMensagens" for="tipoSaldoCombo">${descricoes[recurso]}</label>
      <SELECT NAME="tipoSaldoCombo" id="tipoSaldoCombo" CLASS="form-control" <%=desabilitado ? "disabled" : "" %>>
          <OPTION VALUE=""><hl:message key="rotulo.campo.todos"/></OPTION>
          <OPTION VALUE="<%=TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo()%>"><hl:message key="rotulo.sistema.informacao.saldo"/></OPTION>
          <OPTION VALUE="<%=TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo()%>"><hl:message key="rotulo.sistema.liquidacao.saldo"/></OPTION>
       </SELECT>
    </div>
    
    <script type="text/JavaScript">
      function valida_campo_tipo_saldo() {
          return true;
    }
  </script>
