<%--
* <p>Title: alterarAnexoBeneficiario_v4</p>
* <p>Description: Alterar anexo de beneficiário v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 25018 $
* $Date: 2018-07-19 14:43:42 -0300 (Ter, 18 jul 2018) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.persistence.entity.AnexoBeneficiario"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
Boolean podeEditar = (Boolean) request.getAttribute("podeEditar");
Boolean novo = (Boolean) request.getAttribute("novo");
List<TransferObject> tipoDocumento = (List<TransferObject>) request.getAttribute("tipoDocumento");
AnexoBeneficiario anexo = (AnexoBeneficiario) request.getAttribute("anexo");
String rse_codigo = (String) request.getAttribute(Columns.RSE_CODIGO);
String bfc_codigo = (String) request.getAttribute(Columns.BFC_CODIGO);
%>
<c:set var="javascript">
  <script type="text/javascript">
  function formLoad() {
	  f0 = document.forms[0];  
	}

  window.onload = formLoad();
  
	function verificaCampos() {
		if(<%=novo%>){
			  var controles = new Array("<%=Columns.getColumnName(Columns.TAR_CODIGO)%>", "FILE1");
			  var msgs = new Array ("<hl:message key='mensagem.anexo.beneficiario.tipo.documento'/>", "<hl:message key='mensagem.anexo.beneficiario.anexo'/>");

			  if (!ValidaCampos(controles, msgs)) {
			    return false;
			  }
        f0.submit();
			  return true;
		} else {
			 var controles = new Array("<%=Columns.getColumnName(Columns.TAR_CODIGO)%>");
			  var msgs = new Array ("<hl:message key='mensagem.anexo.beneficiario.tipo.documento'/>");

			  if (!ValidaCampos(controles, msgs)) {
			    return false;
			  }
        f0.submit();
			  return true;
		}
		return false;
	}
  </script>
</c:set>
<c:set var="title">
  ${tituloPagina}
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <div class="col-sm">
    <form method="post" action="../v3/alterarAnexoBeneficiario?acao=salvar&<%=Columns.getColumnName(Columns.BFC_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" ENCTYPE="multipart/form-data">
      <div class="card">
        <div class="card-header">
          <%if(novo){%>
            <hl:message key="rotulo.anexo.beneficiario.inclusao.anexo.beneficiario"/>
          <%}else if(podeEditar){%>
            <hl:message key="rotulo.anexo.beneficiario.edicao"/>
          <%}else{%>
            <hl:message key="rotulo.anexo.beneficiario.visualizar"/>
          <%}%>
        </div>
        <div class="card-body">
            <INPUT class="Edit" TYPE="hidden" NAME="<%=Columns.getColumnName(Columns.BFC_CODIGO)%>" VALUE="<%= !novo ? TextHelper.forHtmlAttribute(anexo.getBeneficiario().getBfcCodigo()) : ""%>" SIZE="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
            <INPUT class="Edit" TYPE="hidden" NAME="<%=Columns.getColumnName(Columns.ABF_NOME)%>" VALUE="<%= !novo ? TextHelper.forHtmlAttribute(anexo.getAbfNome()) : ""%>" SIZE="100" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
            <div class="row">
              <div class="form-group col-sm">
                <label for="estCvlCodigo"><hl:message key="rotulo.anexo.beneficiario.tipo.documento"/></label>
                <% if(podeEditar){%>
                  <%=JspHelper.geraCombo(tipoDocumento, Columns.getColumnName(Columns.TAR_CODIGO), Columns.TAR_CODIGO, Columns.TAR_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo ? anexo.getTipoArquivo().getTarCodigo() : "", null, false, "form-control")%>
                  <% } else {%>
                  <%=JspHelper.geraCombo(tipoDocumento, Columns.getColumnName(Columns.TAR_CODIGO), Columns.TAR_CODIGO, Columns.TAR_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, anexo.getTipoArquivo().getTarCodigo(), null, true, "form-control")%>
                  <% } %>
              </div>
              <% if(novo) { %>
              <div class="form-group col-sm">
                <label><hl:message key="rotulo.anexo.beneficiario.data.validade"/></label>
                  <hl:htmlinput name="<%=Columns.getColumnName(Columns.ABF_DATA_VALIDADE)%>" di="<%=Columns.getColumnName(Columns.ABF_DATA_VALIDADE)%>" type="text" classe="Edit form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%= !novo ? TextHelper.forHtmlAttribute(DateHelper.format(anexo.getAbfDataValidade(), "dd/MM/yyyy")) : ""%>" others="<%=(String)(!podeEditar ? "disabled" : "")%>" />
              </div>
              <% } %>
            </div>
            <div class="row">
              <% if(!novo) { %>
              <div class="form-group col-sm">
                <label><hl:message key="rotulo.anexo.beneficiario.data.inclusao"/></label>
              	<hl:htmlinput name="<%=Columns.getColumnName(Columns.ABF_DATA)%>" di="<%=Columns.getColumnName(Columns.ABF_DATA)%>" type="text" classe="Edit form-control" size="16" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%= !novo ? TextHelper.forHtmlAttribute(DateHelper.format(anexo.getAbfData(), "dd/MM/yyyy HH:mm:ss")) : ""%>" others="disabled" />
              </div>
              <div class="form-group col-sm">
                <label><hl:message key="rotulo.anexo.beneficiario.data.validade"/></label>
                  <hl:htmlinput name="<%=Columns.getColumnName(Columns.ABF_DATA_VALIDADE)%>" di="<%=Columns.getColumnName(Columns.ABF_DATA_VALIDADE)%>" type="text" classe="Edit form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%= !novo ? TextHelper.forHtmlAttribute(DateHelper.format(anexo.getAbfDataValidade(), "dd/MM/yyyy")) : ""%>" others="<%=(String)(!podeEditar ? "disabled" : "")%>" />
              </div>
              <% } %>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label><hl:message key="rotulo.anexo.beneficiario.descricao"/></label>
                <input class="Edit form-control" type="text" name="<%=Columns.getColumnName(Columns.ABF_DESCRICAO)%>" value="<%= !novo ? TextHelper.forHtmlAttribute(anexo.getAbfDescricao()) : ""%>" size="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)( !podeEditar ? "disabled" : "")%>>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <% if(novo){ %>
                <label><hl:message key="rotulo.anexo.beneficiario.anexo"/></label>
                <input type="FILE" class="form-control" name="FILE1" value="<%= !novo ? TextHelper.forHtmlAttribute(anexo.getAbfNome()) : ""%>" size="20" <%=(String)( !podeEditar ? "disabled" : "")%>>
                <% } else { %>
                <label><hl:message key="rotulo.anexo.beneficiario.anexo"/></label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.ABF_NOME)%>" di="<%=Columns.getColumnName(Columns.ABF_NOME)%>" type="text" classe="Edit form-control" size="32" value="<%= !novo ? TextHelper.forHtmlAttribute(anexo.getAbfNome()) : ""%>" others="disabled" />
                <% }%>
              </div>
            </div>
        </div>
      </div>
      <div class="btn-action col-sm">
        <a href="#no-back" name="Button" class="btn btn-outline-danger" onClick="postData('../v3/listarAnexoBeneficiario?acao=listar&_skip_history_=true&<%=Columns.getColumnName(Columns.BFC_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
        <input type="hidden" name="MM_update" value="form1">
        <input type="hidden" name="tipo" value="editar">
        <input type="hidden" name="tmrCodigo" value="">
        <% if(podeEditar){%>
        <a href="#no-back" name="submit2" value="Salvar" onClick="javascript: return verificaCampos();" class="btn btn-primary"><hl:message key="rotulo.botao.salvar"/></a>
        <%} %>
      </div>
    </form>
  </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
  <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>