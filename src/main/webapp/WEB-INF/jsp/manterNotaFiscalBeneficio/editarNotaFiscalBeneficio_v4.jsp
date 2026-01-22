<%--
* <p>Title: ManterNotaFiscalBeneficioWebController</p>
* <p>Description: Contem formulario de reajuste de contratos</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.persistence.entity.FaturamentoBeneficioNf"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<FaturamentoBeneficioNf> nfList = (List<FaturamentoBeneficioNf>) request.getAttribute("nfList");

String fatCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "faturamentoCodigo")) ? JspHelper.verificaVarQryStr(request, "faturamentoCodigo") : (String) request.getAttribute("faturamentoCodigo");
String fnfCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "fnfCodigo")) ? JspHelper.verificaVarQryStr(request, "fnfCodigo") : (String) request.getAttribute("fnfCodigo");
String tipoNotaFiscal = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "tipoNotaFiscal")) ? JspHelper.verificaVarQryStr(request, "tipoNotaFiscal") : (String) request.getAttribute("tipoNotaFiscal");
String codigoContrato = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "codigoContrato")) ? JspHelper.verificaVarQryStr(request, "codigoContrato") : (String) request.getAttribute("codigoContrato");
String numeroNf = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "numeroNf")) ? JspHelper.verificaVarQryStr(request, "numeroNf") : (String) request.getAttribute("numeroNf");
String numeroTitulo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "numeroTitulo")) ? JspHelper.verificaVarQryStr(request, "numeroTitulo") : (String) request.getAttribute("numeroTitulo");
String valorIss = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "valorIss")) ? JspHelper.verificaVarQryStr(request, "valorIss") : (String) request.getAttribute("valorIss");
String valorIr = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "valorIr")) ? JspHelper.verificaVarQryStr(request, "valorIr") : (String) request.getAttribute("valorIr");
String pisCofins = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "pisCofins")) ? JspHelper.verificaVarQryStr(request, "pisCofins") : (String) request.getAttribute("pisCofins");
String valorBruto = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "valorBruto")) ? JspHelper.verificaVarQryStr(request, "valorBruto") : (String) request.getAttribute("valorBruto");
String valorLiquido = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "valorLiquido")) ? JspHelper.verificaVarQryStr(request, "valorLiquido") : (String) request.getAttribute("valorLiquido");
String dataVencimento = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "dataVencimento")) ? JspHelper.verificaVarQryStr(request, "dataVencimento") : (String) request.getAttribute("dataVencimento");

%>

<c:set var="title">
  <hl:message key="rotulo.notafiscal.faturamento.beneficio.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
   <!-- Formulário -->
  <form method="POST" action="../v3/manterNotaFiscalBeneficio?_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form">
    <input type="hidden" name="acao" value="salvar">
    <input type="hidden" name="faturamentoCodigo" value="<%=TextHelper.forHtmlAttribute(fatCodigo)%>">
    <% if (!TextHelper.isNull(fnfCodigo)) { %>
    <input type="hidden" name="fnfCodigo" value="<%=fnfCodigo%>">
    <% } %>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title">
          <hl:message key="rotulo.notafiscal.faturamento.beneficio.dados.nota.fiscal" />
        </h2>
      </div>
      <div class="card-body">
        <fieldset>
        <div class="row">
          <div class="form-group col-sm-6">
            <label> * <hl:message key="rotulo.notafiscal.faturamento.beneficio.tipo.nota.fiscal" /></label>
            <SELECT name="tipoNotaFiscal" class="form-control" id="tipoNotaFiscal">
              <OPTION VALUE="S" <%="S".equals(tipoNotaFiscal) ? "selected" : ""%>><hl:message key="rotulo.notafiscal.faturamento.beneficio.tipo.nota.fiscal.subsidio"/></OPTION>
              <OPTION VALUE="M" <%="M".equals(tipoNotaFiscal) ? "selected" : ""%>><hl:message key="rotulo.notafiscal.faturamento.beneficio.tipo.nota.fiscal.mc.mnc"/></OPTION>
              <OPTION VALUE="C" <%="C".equals(tipoNotaFiscal) ? "selected" : ""%>><hl:message key="rotulo.notafiscal.faturamento.beneficio.tipo.nota.fiscal.copart"/></OPTION>
            </SELECT>
          </div>
        </div>
        <div class="row">
            <div class="form-group col-sm-12 col-md-2">
              <label for="codigoContrato"> 
                * 
                <hl:message key="rotulo.notafiscal.faturamento.beneficio.codigo.contrato" />
              </label>
              <hl:htmlinput name="codigoContrato" type="text" classe="form-control" di="codigoContrato" value="<%=codigoContrato%>"  maxlength="40" mask="#A40" />
            </div>
            <div class="form-group col-sm-12 col-md-2">
              <label for="numeroNf">
                *
                <hl:message key="rotulo.notafiscal.faturamento.beneficio.numero.nf" />
              </label>
              <hl:htmlinput name="numeroNf" type="text" classe="form-control" di="numeroNf" value="<%=numeroNf%>"  maxlength="30" mask="#A30"/>
            </div>
            <div class="form-group col-sm-12 col-md-4">
              <label for="numeroTitulo">
                *
                <hl:message key="rotulo.notafiscal.faturamento.beneficio.numero.titulo" />
              </label>
              <hl:htmlinput name="numeroTitulo" type="text" di="numeroTitulo" classe="form-control" value="<%=numeroTitulo%>" maxlength="30" mask="#A30" />
            </div>
            <div class="form-group col-sm-12 col-md-4">
              <label for="dataVencimento">
                *
                <hl:message key="rotulo.notafiscal.faturamento.beneficio.data.vencimento" />
              </label>
              <hl:htmlinput name="dataVencimento" type="text" di="dataVencimento" classe="form-control" value="<%=dataVencimento%>" maxlength="14" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" />
            </div>
        </div>
        <div class="legend"></div>
        <div class="row">
            <div class="form-group col-sm-4">
              <label for="valorIss">
                 * <hl:message key="rotulo.notafiscal.faturamento.beneficio.valor.iss" />
              </label>
              <hl:htmlinput name="valorIss" type="text" classe="form-control" di="valorIss"  value="<%=valorIss%>" size="15" maxlength="11" mask="#F15" 
              onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
            </div>
            <div class="form-group col-sm-4">
              <label for="valorIr">
                 * <hl:message key="rotulo.notafiscal.faturamento.beneficio.valor.ir" />
              </label>
              <hl:htmlinput name="valorIr" type="text"  classe="form-control" di="valorIr" value="<%=valorIr%>" size="15" maxlength="11" mask="#F15"
              onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
            </div>
            <div class="form-group col-sm-4">
              <label for="pisCofins">
                 * <hl:message key="rotulo.notafiscal.faturamento.beneficio.valor.pis.cofins" />
              </label>
              <hl:htmlinput name="pisCofins" type="text" classe="form-control" di="pisCofins" value="<%=pisCofins%>" size="15" maxlength="11" mask="#F15"
              onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
            </div>
         </div>
         <div class="row">
            <div class="form-group col-sm-12 col-md-4">
              <label for="valorBruto">
                 * <hl:message key="rotulo.notafiscal.faturamento.beneficio.valor.bruto" />
              </label>
              <hl:htmlinput name="valorBruto" type="text" classe="form-control" di="valorBruto" value="<%=valorBruto%>" size="15" maxlength="11" mask="#F15"
              onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
            </div>
            <div class="form-group col-sm-4">
              <label for="valorLiquido">
                 * <hl:message key="rotulo.notafiscal.faturamento.beneficio.valor.liquido" />
              </label>
              <hl:htmlinput name="valorLiquido" type="text" classe="form-control" di="valorLiquido"  value="<%=valorLiquido%>" size="15" maxlength="11" mask="#F15"
              onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
            </div>
         </div>
        </fieldset>
      </div>
    </div>
  </form>
  <div class="float-end">
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar" /></a>
      <a class="btn btn-primary" href="#no-back" onClick="salvar(); return false;" title="<hl:message key="rotulo.botao.salvar"/>"><hl:message key="rotulo.botao.salvar"/></a>
    </div>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/javascript">

f0 = document.forms[0];

function salvar() {
	f0.submit();
}

</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>


