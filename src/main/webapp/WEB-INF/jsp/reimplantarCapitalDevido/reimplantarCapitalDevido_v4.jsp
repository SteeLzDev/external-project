<%--
* <p>Title: reservar.jsp</p>
* <p>Description: Reimplantar Cappital Devido layout v4</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: anderson.assis $
* $Revision: 29198 $
* $Date: 2020-03-30 14:06:03 -0300 (Seg, 30 mar 2020) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String adeCodigo = (String) request.getAttribute("adeCodigo");
BigDecimal capitalDevido = (BigDecimal) request.getAttribute("capitalDevido");
CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");
BigDecimal adeVlrAtual = (BigDecimal) request.getAttribute("adeVlrAtual");
String rotuloPeriodicidadePrazo = (String) request.getAttribute("rotuloPeriodicidadePrazo");
String labelTipoVlr = (String) request.getAttribute("labelTipoVlr");
List<TransferObject> lstMtvOperacao = (List<TransferObject>) request.getAttribute("lstMtvOperacao");
%>
<c:set var="title">
  <%=TextHelper.forHtmlContent(request.getAttribute("tituloPagina"))%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
<div id="main">
  <form action="../v3/reimplantarCapitalDevido?acao=confirmarReimplantacao" method="post" name="form1">  
    <div class="row">  
      <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
      <%
        pageContext.setAttribute("autdes", autdes);
      %>
      <hl:detalharADEv4 name="autdes" table="true" type="alterar" scope="request" divSizeCSS="col-sm-6"/>

      <%
        out.print(SynchronizerToken.generateHtmlToken(request));
      %>
     <hl:htmlinput name="ADE_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" />  
     <hl:htmlinput name="flow" type="hidden" value="endpoint" />
    </div>  
    <div class="row">
      <div class="col-sm">
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="mensagem.reimplantar.capital.devido.informe.valores"/></h2>
          </div>
          <div class="card-body">
            <dl class="row">
             <dt class="ml-3"><hl:message key="rotulo.capital.devido"/>&nbsp;(<hl:message key="rotulo.moeda"/>):&nbsp;</dt>
             <dd class="">&nbsp;<%=TextHelper.forHtmlContent(NumberHelper.reformat(capitalDevido.toString(), "en", NumberHelper.getLang()))%></dd>
            </dl>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="adeVlr"><hl:message key="rotulo.consignacao.valor.parcela.novo"/>&nbsp;(<%=TextHelper.forHtmlContent(labelTipoVlr)%>):&nbsp;</label>              
                <hl:htmlinput name="adeVlr" type="text" classe="form-control" di="adeVlr" size="8" mask="#F11" value="" onBlur="if (this.value != '') { validaAdeVlr(this); this.value = FormataContabil(parse_num(this.value), 2); }"/>
              </div>
              <div class="form-group col-sm-6">
                <label for="adePrazoEdt"><hl:message key="rotulo.consignacao.prazo.novo"/>&nbsp;<%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%>:&nbsp;</label>
                <input name="adePrazoEdt" id="adePrazoEdt" class="form-control" onFocus="SetarEventoMascara(this,'#D4',true);" onBlur="fout(this);ValidaMascara(this);" type="text">   
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="tmoCodigo"><hl:message key="rotulo.avancada.tmoCodigo"/></label>
                 <%=JspHelper.geraCombo(lstMtvOperacao, "tmoCodigo", Columns.TMO_CODIGO, Columns.TMO_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\"", false, 1)%>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-12">        
               <label for="adeObs"><hl:message key="rotulo.avancada.adeObs"/></label>
               <textarea name="ocaObs" id="ocaObs" class="form-control" cols="32" rows="5" onFocus="SetarEventoMascara(this,'#*10000',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>         
              </div>
            </div>
          </div>
        </div>     
      </div>     
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" id="btnCancelar"><hl:message key="rotulo.botao.cancelar"/></a>   
      <a class="btn btn-primary" href="#" id="submit" onClick="return reimpCapitalDevido()"><svg width="17"><use xlink:href="#i-confirmar"></use></svg><hl:message key="rotulo.botao.confirmar"/></a>
    </div> 
  </form>
</div>
</c:set>
  <c:set var="javascript">      
    <script type="text/JavaScript">
      function validaAdeVlr(vlr) {
        var vlrformatado = parse_num(vlr.value);
        if (vlrformatado > <%=TextHelper.forJavaScript(adeVlrAtual)%>) {
            alert('<hl:message key="mensagem.reimp.capital.devido.parcela.maior.atual"/>');
            vlr.value = '';
            vlr.focus();
            return false;
        }
      }
      
      function reimpCapitalDevido() {
        var prazoEditado = f0.adePrazoEdt.value;
        var vlrEditado = parse_num(f0.adeVlr.value);
        var capitalDevidoDigitado = prazoEditado * vlrEditado;
        
        if (capitalDevidoDigitado > <%=TextHelper.forJavaScript(capitalDevido) %>) {
          alert('<hl:message key="mensagem.reimp.capital.devido.digitado.maior.vlr.devido"/>');
          f0.adeVlr.focus();
          return false;
        }
        
        var ControlesAvancados = new Array("tmoCodigo", "adeVlr", "adePrazoEdt", "ocaObs");
          var MsgsAvancadas = new Array('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>',
                          '<hl:message key="mensagem.informe.ade.valor"/>',
                          '<hl:message key="mensagem.informe.ade.prazo"/>',
             			  '<hl:message key="mensagem.informe.observacao"/>');
      
          if (!ValidaCampos(ControlesAvancados, MsgsAvancadas)) {
            return false;
          }
        
        if (confirm('<hl:message key="mensagem.confirmacao.reimplante.capital.devido"/>')) {
            enableAll();
            f0.submit();
            return true;
          } 
        
          return false;
      }
      
      function formLoad() {
        f0.adeVlr.focus();
      }
    </script>
    <script type="text/JavaScript">
     f0 = document.forms[0];
    </script>
  </c:set>
  <t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
  </t:page_v4>