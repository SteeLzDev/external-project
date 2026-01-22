<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="parDelegate" scope="request" class="com.zetra.econsig.delegate.ParametroDelegate"/>
<jsp:useBean id="svcDelegate" scope="request" class="com.zetra.econsig.delegate.ServicoDelegate"/>
<%
/*
ATENÇÃO: Novos registros de acesso recurso devem ser criados com o recurso /v3/listarCidades para a função 
que fizer o include desta página.  
*/

AcessoSistema responsavelCidade = JspHelper.getAcessoSistema(request);
String tipo = JspHelper.verificaVarQryStr(request, "TIPO");
if (TextHelper.isNull(tipo)) {
    tipo = "acompanharLeilao";
}
String svcCodigoCidade = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
if (TextHelper.isNull(svcCodigoCidade)) {
    svcCodigoCidade = (String) request.getAttribute("SVC_CODIGO");
}
String nseCodigoCidade = JspHelper.verificaVarQryStr(request, "NSE_CODIGO");
if (TextHelper.isNull(nseCodigoCidade)) {
    nseCodigoCidade = (String) request.getAttribute("NSE_CODIGO");
}
boolean exibeCampoCidade = false;

List parametros = new ArrayList();
parametros.add(CodedValues.TPS_EXIBE_CIDADE_CONFIRMACAO_SOLICITACAO);
if (!TextHelper.isNull(svcCodigoCidade)) {
    ParamSvcTO paramSvcCse = parDelegate.selectParamSvcCse(svcCodigoCidade, parametros, responsavelCidade);
    String paramExibeCampoCidade = paramSvcCse.getTpsExibeCidadeConfirmacaoSolicitacao();
    if (!TextHelper.isNull(paramExibeCampoCidade) && (
        paramExibeCampoCidade.equals(CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OPCIONAL) ||
        paramExibeCampoCidade.equals(CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO) ||
        (paramExibeCampoCidade.equals(CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO_LEILAO) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavelCidade)) 
        )) {
        exibeCampoCidade = true;
    }
} else if (!TextHelper.isNull(nseCodigoCidade)) {
    List pseVlrs = new ArrayList();
    pseVlrs.add(CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OPCIONAL);
    pseVlrs.add(CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO);
    pseVlrs.add(CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO_LEILAO);
    List servicosExibeCampoCidade = svcDelegate.selectServicosComParametro(CodedValues.TPS_EXIBE_CIDADE_CONFIRMACAO_SOLICITACAO, null, null, null, pseVlrs, false, nseCodigoCidade, responsavelCidade);
    if (servicosExibeCampoCidade != null && !servicosExibeCampoCidade.isEmpty()) {
        exibeCampoCidade = true;
    }
}
%>

<script src="../node_modules/jquery/dist/jquery.min.js?<hl:message key="release.tag"/>"></script>
<script src="../node_modules/jquery-ui/dist/jquery-ui.min.js?<hl:message key="release.tag"/>"></script>
<link href="../node_modules/jquery-ui/dist/themes/base/jquery-ui.css?<hl:message key="release.tag"/>" rel="stylesheet">

<style type="text/css">
.ui-autocomplete {
    max-height: 200px;
    overflow-y: auto;   /* prevent horizontal scrollbar */
    overflow-x: hidden; /* add padding to account for vertical scrollbar */
    z-index:1000 !important;
}
.ui-menu .ui-menu-item a {
        font-family: Verdana,Arial;
        font-size: 8pt;
}
</style>
<script type="text/JavaScript">
$(document).ready(function() {
    $(function() {
    	$("#CID_NOME").autocomplete({
    	    source: function(request, response) {
    	    $.ajax({
    	    url: "../v3/listarCidades?acao=<%=tipo%>&_skip_history_=true",
    	    type: "POST",
    	    dataType: "json",
    	    data: { name: request.term},
    	        success: function( data ) {
    	            response( $.map( data, function( item ) {
    	            return {
    	            	label: item.atributos['<%=Columns.CID_NOME%>'] + ' - ' + item.atributos['<%=Columns.CID_UF_CODIGO%>'],
 	                    value: item.atributos['<%=Columns.CID_NOME%>'] + ' - ' + item.atributos['<%=Columns.CID_UF_CODIGO%>'],
 	                    value2: item.atributos['<%=Columns.CID_CODIGO%>'],
    	            }
    	            }));
	    	    },
   			    error: function (error) {
   			    	$("[name='CID_CODIGO']").val("");		
    	    	}
    	    });
    	    },
    	    select: function( event, ui ) {
   	         $("#CID_NOME").val(ui.item.label);
   	         $("[name='CID_CODIGO']").val(ui.item.value2);
   	         return false;
   	    	},
    	    minLength: 3
   	    });
    });
});
</script>
<%if (exibeCampoCidade) { %>
<div class="card">
	<div class="card-header">
		<h2 class="card-header-title"><hl:message key="rotulo.confirmacao.solicitacao.local"/></h2>
	</div>
	<div class="card-body">
		<div class="row">
            <div class="form-group col-sm-12  col-md-6">
                <label for="CID_NOME" ><hl:message key="rotulo.simulacao.cidade"/></label>
                <hl:htmlinput name="CID_NOME"
                              di="CID_NOME"
                              type="text"
                              classe="form-control"
                              size="30"
                              mask="#*40"
                              value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CID_NOME"))%>"
                      />            
            </div>
        </div>
	</div>
</div>
<% } %>
<hl:htmlinput name="CID_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CID_CODIGO"))%>"/>