<%--
* <p>Title: listarRelacaoBeneficios</p>
* <p>Description: Listar relação benefícios v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 25184 $
* $Date: 2018-08-06 15:07:27 -0300 (Seg, 06 ago 2018) $
--%>

<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.persistence.entity.Beneficiario"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.TipoBeneficiarioEnum"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  Beneficiario beneficiario = (Beneficiario) request.getAttribute("beneficiario");
  
  List lstMtvOperacao = (List) request.getAttribute("lstMtvOperacao");
  String bfc_codigo = (String) request.getAttribute("bfcCodigo");
  String cbe_codigo = (String) request.getAttribute("cbeCodigo");
  String scb_codigo = (String) request.getAttribute("scbCodigo");
  String ser_codigo = (String) request.getAttribute("serCodigo");
  String tib_codigo = (String) request.getAttribute("tibCodigo");
  String toc_codigo = (String) request.getAttribute("tocCodigo");
  String ben_codigo = (String) request.getAttribute(Columns.BEN_CODIGO);
  String rse_codigo = (String) request.getAttribute(Columns.RSE_CODIGO);
  String contratosAtivos = (String) request.getAttribute("contratosAtivos");
  boolean solicitacaoCancelamento = request.getAttribute("solicitacaoCancelamento")!=null;
  
  boolean cancelar = false;
  boolean aprovar = false;
  boolean rejeitar = false;

  boolean aprovarTodos = false;
  
  String adesaoPlanoExFuncionarios = CodedValues.TDA_NAO;
  
  String operacao = (String) request.getAttribute("operacao");
  
  if(operacao.equals("aprovar")) {
    aprovar = true;
  } else if (operacao.equals("rejeitar")) {
   rejeitar = true; 
  } else if (operacao.equals("cancelar")) {
   cancelar = true; 
  }

%>
<c:set var="javascript">
  <script>
    function formLoad() {
	   f0 = document.forms[0];  
	  }

    $(document).ready(function() {
    	$('#dataObito').hide();
      	formLoad();
    
    	   if(typeof document.getElementById("aprovarTodos") != 'undefined' && document.getElementById("aprovarTodos") != null && document.getElementById("aprovarTodos").checked) {
    	    
          <% 
          if(aprovar){
    	      aprovarTodos = true;
            }
    	  %>
    		 }
    });
    
    function exibeDataObito(selectObject) {
    	let tmoCodigo = selectObject.value
    	$.ajax({
		    type: 'POST',
		    url: '../v3/aprovarSolicitacao/exibeObito?<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_',
		    data: {
		    	'tmoCodigo' : tmoCodigo
			    },
		    success: function (data) {
		    	var acaCodigo = data;
		    	if (acaCodigo != "") {
		    		$('#dataObito').show();
		    	} else {
		    		$('#dataObito').hide();
		    	}
		    },
		    error: function (request, status, error) {
		    	$('#dataObito').hide();
		    }
	  })
  	}

    function verificaCampos() {
    	if($("#dataObito").is(":visible")) {
    		let dataObitoString = document.getElementById("<%=Columns.getColumnName(Columns.BFC_DATA_OBITO)%>");
    		let dataAtual = new Date(); 
    		
	  		if (dataObitoString && dataObitoString.value) {
  				let datas = obtemPartesData(dataObitoString.value); 
  				let dataObito = new Date(datas[2], datas[1]-1, datas[0]); 
  				
  				if (dataObito > dataAtual) {
  					alert("<hl:message key='mensagem.erro.data.obito.data.futura'/>");
  					return false;
				}
				
			} else {
				alert("<hl:message key='mensagem.erro.data.obito.obrigatorio'/>");
				return false;
			}
    		
      		f0.submit();
        } else {
     	    f0.submit();
			return true;
        }
   }
   
  </script>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.relacao.beneficios.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/aprovarSolicitacao?acao=salvar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" id="form1">
    <input TYPE="hidden" NAME="<%=Columns.getColumnName(Columns.BFC_CODIGO)%>" VALUE="<%= TextHelper.forHtmlAttribute(bfc_codigo)%>">
    <input TYPE="hidden" NAME="<%=Columns.getColumnName(Columns.CBE_CODIGO)%>" VALUE="<%= TextHelper.forHtmlAttribute(cbe_codigo)%>">    
    <input TYPE="hidden" NAME="<%=Columns.getColumnName(Columns.SCB_CODIGO)%>" VALUE="<%= TextHelper.forHtmlAttribute(scb_codigo)%>">
    <input TYPE="hidden" NAME="<%=Columns.getColumnName(Columns.SER_CODIGO)%>" VALUE="<%= TextHelper.forHtmlAttribute(ser_codigo)%>">
    <input TYPE="hidden" NAME="<%=Columns.getColumnName(Columns.TOC_CODIGO)%>" VALUE="<%= TextHelper.forHtmlAttribute(toc_codigo)%>">
    <input TYPE="hidden" NAME="<%=Columns.getColumnName(Columns.RSE_CODIGO)%>" VALUE="<%= TextHelper.forHtmlAttribute(rse_codigo)%>">
    <input TYPE="hidden" NAME="operacao" VALUE="<%=TextHelper.forHtmlAttribute(operacao)%>">
    <%if (solicitacaoCancelamento){ %>
        <input TYPE="hidden" NAME="solicitacaoCancelamento" VALUE="<%=TextHelper.forHtmlAttribute(solicitacaoCancelamento)%>">
    <%} %>
    <input TYPE="hidden" NAME="contratosAtivos" VALUE="<%=TextHelper.forHtmlAttribute(contratosAtivos)%>">
    <div class="row">
      <div class="col-sm">
        <div class="card">
          <div class="card-header">
          </div>
          <div class="card-body">
            <%if(lstMtvOperacao != null && !lstMtvOperacao.isEmpty()) { %>
                  <div class="row">
                    <div class="col-sm">
                      <div class="form-group">
                        <label for="TMO_CODIGO"><hl:message key="rotulo.motivo.operacao.singular" /></label>
                        <%=JspHelper.geraCombo(lstMtvOperacao,  "tmoCodigo", Columns.TMO_CODIGO, Columns.TMO_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, "" ,"exibeDataObito(this)", false, "form-control")%>
                      </div>
                    </div>
                  </div>
              <% } %>
            <div class="row">
              <div class="col-sm">
                <div class="form-group">
                  <label for="<%=Columns.getColumnName(Columns.OCB_OBS)%>"><hl:message key="rotulo.relacao.beneficios.observacao" /></label>
                  <input class="form-control" type="text" name="<%=Columns.getColumnName(Columns.OCB_OBS)%>">
                </div>
              </div>
            </div>
            <% if(tib_codigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && aprovar) { %>
            <div class="row">
              <div class="col-sm">
                <div class="form-group">
                  <input type="checkbox" id="aprovarTodos" class="form-check-input ml-1" name="aprovarTodos" value="<%= TextHelper.forHtmlAttribute(aprovarTodos)%>" />
                  <label for="aprovarTodos" class="form-check-label"><hl:message key="rotulo.relacao.beneficios.aprovar.todos"/></label>
                </div>
              </div>
            </div>
            <% } %>
            <% if(tib_codigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && rejeitar) { %>
            <div class="row">
              <div class="col-sm">
                <p><hl:message key="rotulo.relacao.beneficios.rejeitar.todos" /></p>
              </div>
            </div>
            <% } %>
            <% if(tib_codigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && cancelar && !responsavel.isSer()) { %>
              <div class="row">
                <div class="col-sm">
                  <div class="form-group">
                    <input type="checkbox" id="adesaoPlanoExFuncionarios" class="form-check-input ml-1" name="adesaoPlanoExFuncionarios" value="<%=CodedValues.TDA_SIM%>" />
                    <label for="adesaoPlanoExFuncionarios" class="form-check-label"><hl:message key="rotulo.relacao.beneficios.adesao.plano.ex.funcionarios"/></label>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-sm">
                  <div class="form-group">
                    <label for="periodoContribuicao"><hl:message key="rotulo.relacao.beneficios.periodo.contribuicao" /></label>
                    <hl:htmlinput name="<%=CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO%>" di="periodoContribuicao" type="text" classe="form-control" size="3" mask="#F3"/>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-sm">
                  <p><hl:message key="rotulo.relacao.beneficios.cancelar.todos" /></p>
                </div>
              </div>
            <% } %>
            <% if(cancelar && !responsavel.isSer()) { %>
                <div class="row" id="dataObito">
                  <div class="form-group col-sm-4">
                    <label for=""><hl:message key="rotulo.beneficiario.data.obito"/></label>
                    <hl:htmlinput name="<%=Columns.getColumnName(Columns.BFC_DATA_OBITO)%>" di="<%=Columns.getColumnName(Columns.BFC_DATA_OBITO)%>" type="text" 
                    classe="Edit form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                    value="" 
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.beneficiario.data.obito",responsavel)%>"
                    others="" />
                  </div> 
                </div>  
            <% } %>
          </div>
        </div>
      </div>
    </div>
    <div class="float-end">
      <div class="btn-action">
        <a href="#no-back" name="Button" class="btn btn-outline-danger" onClick="postData('../v3/relacaoBeneficios?acao=consultar&contratosAtivos=<%=contratosAtivos%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.TIB_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(tib_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
        <a href="#no-back" name="submit2" onClick="javascript: verificaCampos(); return false;" class="btn btn-primary"><hl:message key="rotulo.botao.salvar"/></a>
      </div>
    </div>
  </form>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>