<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>

<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String ade_codigo = JspHelper.verificaVarQryStr(request, "ade_codigo");
String prd_numero = JspHelper.verificaVarQryStr(request, "prd_numero");
String prd_codigo = JspHelper.verificaVarQryStr(request, "prd_codigo");

CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");
String prdVlrPrevisto = (String) request.getAttribute("prdVlrPrevisto");

boolean alterarParcela = (boolean) request.getAttribute("alterarParcela");

String statusParcela = (String) request.getAttribute("status") != null ? (String) request.getAttribute("status") : "";
String prdVlrRealizado = (String) request.getAttribute("prdVlrRealizado") != null ? (String) request.getAttribute("prdVlrRealizado") : "";
%>
<c:set var="title">
  <hl:message key="rotulo.folha.edicao.retorno.integracao.titulo"/>
    </c:set>
    <c:set var="imageHeader">
      <use xlink:href="#i-operacional"></use>
    </c:set>
    <c:set var="bodyContent">
    <form method="post" action="../v3/cadastrarRetornoIntegracao?acao=editarIntegracao&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
        <div class="row">
            <div class="col-sm-12 col-md-7 pl-0 pr-0">
              <div class="row">
               <div class="col-sm-12">
                    <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
                    <% pageContext.setAttribute("autdes", autdes); %>
                    <hl:detalharADEv4 name="autdes" table="true" type="alterar"/>
                    <%-- Fim dos dados da ADE --%>
                </div>
              </div>
            </div>
            <div class="col-sm-12 col-md-5">
              <div class="card">
                <div class="card-header">
                  <h2 class="card-header-title"><hl:message key="rotulo.motivo.operacao.singular"/></h2>
                </div>
                <div class="card-body">
                    <div class="row justify-content-md-center">
                      <div class="form-group col-sm">
                        <label for="iNumeroParcelas"><hl:message key="rotulo.folha.numero.parcela"/></label>
                        <input type="text" class="form-control" id="iNumeroParcelas" name="iNumeroParcelas" value="<%=TextHelper.forHtmlAttribute(prd_numero)%>" disabled/> 
                      </div>
                  </div>
                  <div class="row justify-content-md-center">
                     <div class="form-group col-sm">
                       <label for="prd_vlr_realizado"><hl:message key="rotulo.folha.valor.realizado"/></label>
                          <input type="text" class="form-control" id="prd_vlr_realizado"  name="prd_vlr_realizado" value="<%=TextHelper.forHtmlAttribute((!alterarParcela ? TextHelper.forHtmlAttribute(prdVlrPrevisto) : TextHelper.forHtmlAttribute(prdVlrRealizado)))%>" placeholder="Digite o valor realizado" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
                     </div>
                  </div>
                  <div class="row justify-content-md-center">
                    <div class="form-group col-sm">  
                      <label for="iSituacao"><hl:message key="rotulo.folha.situacao"/></label>
                        <select id="iSituacao" name="status" class="form-control Select" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                          <%if (!alterarParcela){ %>
                            <option value=""><hl:message key="rotulo.campo.selecione"/></option>
                            <option value="<%=(String)CodedValues.SPD_LIQUIDADAFOLHA%>"><hl:message key="rotulo.folha.liquidada"/></option>
                            <option value="<%=(String)CodedValues.SPD_REJEITADAFOLHA%>"><hl:message key="rotulo.folha.rejeitada"/></option>
                          <%} else { %>
                            <option value="<%=(String)CodedValues.SPD_LIQUIDADAFOLHA%>" <%=(String)(statusParcela.equals((String)CodedValues.SPD_LIQUIDADAFOLHA)?"SELECTED":"")%>><hl:message key="rotulo.folha.liquidada"/></option>
                            <option value="<%=(String)CodedValues.SPD_REJEITADAFOLHA%>" <%=(String)(statusParcela.equals((String)CodedValues.SPD_REJEITADAFOLHA)?"SELECTED":"")%>><hl:message key="rotulo.folha.rejeitada"/></option>
                          <%} %>
                        </select>
                  </div>
                </div>
               <div class="row justify-content-md-center">
                  <div class="form-group col-sm">
                    <label for="iMotivo"><hl:message key="rotulo.folha.motivo"/></label>
                    <textarea id="iMotivo" class="form-control" name="motivo" rows="6" wrap="VIRTUAL" class="Edit" placeHolder="<hl:message key='rotulo.consignante.informe.motivo.operacao'/>" onFocus="SetarEventoMascara(this,'#*65000',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>
                  </div>
                </div>
            </div>
          </div>
         <div class="btn-action">
              <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
              <a class="btn btn-primary" href="#no-back" onClick="if(vf_integra_parcela()){f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
         </div>
        </div>
        <hl:htmlinput name="ade_codigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(ade_codigo)%>" />
        <hl:htmlinput name="prd_numero" type="hidden" value="<%=TextHelper.forHtmlAttribute(prd_numero)%>" />
        <hl:htmlinput name="prd_codigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(prd_codigo)%>" />
        <hl:htmlinput name="alterarParcela" type="hidden" value="<%=TextHelper.forHtmlAttribute(alterarParcela)%>" />
        <hl:htmlinput name="operacao"   type="hidden" value="salvar" />
        <hl:htmlinput name="MM_update" type="hidden" value="form1"/>
     </div>
   </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    var f0 = document.forms[0];
    window.onload = formLoad;
  
    function formLoad() {
  	  f0.prd_vlr_realizado.focus();
  	}
  
  	function vf_integra_parcela() {
  	  var spdCodigo = f0.status.value; 
  	  if (spdCodigo == '') {
  	    alert('<hl:message key="mensagem.folha.selecione.status.realizado"/>');
  	    f0.status.focus();
  	    return false;
  	  }
  
  	  if (spdCodigo != '<%=(String)CodedValues.SPD_REJEITADAFOLHA%>') { 
  	    var valor = parseFloat(parse_num(f0.prd_vlr_realizado.value));
  	    if (isNaN(valor) || (valor <= 0)) {
  	      alert('<hl:message key="mensagem.folha.preencha.valor.parcela.ser.realizado"/>');
  	      f0.prd_vlr_realizado.focus();
  	      return false;
  	    }
  	  }
  
  	  return true;
  	}
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
