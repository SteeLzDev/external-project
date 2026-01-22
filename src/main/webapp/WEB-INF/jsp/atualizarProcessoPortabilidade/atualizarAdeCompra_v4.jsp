<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");

BigDecimal adeVlr = (BigDecimal) request.getAttribute("adeVlr");

String adeCodigoDestino = (String) request.getAttribute("adeCodigoDestino");
String rseCodigo = (String) request.getAttribute("rseCodigo");
String orgCodigo = (String) request.getAttribute("orgCodigo");
String labelTipoVlr = (String) request.getAttribute("labelTipoVlr");
String exigeSenhaServidor = (String) request.getAttribute("exigeSenhaServidor");
String adeVlrAtual = (String) request.getAttribute("adeVlrAtual");
String adeIndice = (String) request.getAttribute("adeIndice");
String mensagem = (String) request.getAttribute("mensagem");
String adePeriodicidade = (String) request.getAttribute("adePeriodicidade");

boolean temCET = (boolean) request.getAttribute("temCET");
boolean permiteCadIndice = (boolean) request.getAttribute("permiteCadIndice");
boolean indiceSomenteAutomatico = (boolean) request.getAttribute("indiceSomenteAutomatico");
boolean serInfBancariaObrigatoria = (boolean) request.getAttribute("serInfBancariaObrigatoria");
boolean permiteCadVlrTac = (boolean) request.getAttribute("permiteCadVlrTac");
boolean permiteCadVlrIof = (boolean) request.getAttribute("permiteCadVlrIof");
boolean permiteCadVlrMensVinc = (boolean) request.getAttribute("permiteCadVlrMensVinc");
boolean boolTpsSegPrestamista = (boolean) request.getAttribute("boolTpsSegPrestamista");
boolean permiteVlrLiqTxJuros = (boolean) request.getAttribute("permiteVlrLiqTxJuros");

int maxPrazo = (int) request.getAttribute("maxPrazo");
int prazo = (int) request.getAttribute("prazo");
int prazoRest = (int) request.getAttribute("prazoRest");
Integer valorAdeCarencia = (Integer) request.getAttribute("valorAdeCarencia");
String mascaraLogin = (String) request.getAttribute("mascaraLogin");
%>
<c:set var="title">
   <hl:message key="rotulo.alterar.consignacao.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
 <form action="../v3/atualizarProcessoPortabilidade?acao=atualizarContrato&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
  <hl:htmlinput name="adeUpdate"          type="hidden" value="<%=TextHelper.forHtmlAttribute(adeCodigoDestino)%>" />
  <hl:htmlinput name="adePeriodicidade"   type="hidden" value="<%=TextHelper.forHtmlAttribute(adePeriodicidade)%>" />
  <hl:htmlinput name="adeVlr"             type="hidden" value="<%=TextHelper.forHtmlAttribute(adeVlrAtual)%>" />
  <hl:htmlinput name="exigeSenhaServidor" type="hidden" value="<%=TextHelper.forHtmlAttribute(exigeSenhaServidor)%>" />
    <div class="row">
        <div class="col-sm-12">
               <dl class="row data-list">
              <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
                 <% pageContext.setAttribute("autdes", autdes); %>
                 <hl:detalharADEv4 name="autdes" table="false" type="alterar"/>
              <%-- Fim dos dados da consignação --%>
              </dl>   
        </div>
    </div>
    <div class="row">
     <div class="col-sm-12">
        <div class="card ml-1 mb-0">
          <div class="card-header">
              <h2 class="card-header-title"><hl:message key="mensagem.informe.ade.novos.valores"/></h2>
          </div>
         </div>
       <div class="card-body">
         <% if (ShowFieldHelper.showField(FieldKeysConstants.ATUALIZACAO_PORTABILIDADE_CARENCIA, responsavel)) {%>
         <div class="row">
          <div class="form-group col-sm-6 col-md-6">
            <label for="adeCarencia"><hl:message key="rotulo.consignacao.carencia.nova"/></label>
            <hl:htmlinput name="adeCarencia"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute((valorAdeCarencia))%>"
                          mask="#D2"
                          size="8"
                          others="disabled"/>
           </div>
          </div>
          <% } else { %>
                <hl:htmlinput name="adeCarencia" di="adeCarencia" type="hidden" value="<%=valorAdeCarencia != null ? TextHelper.forHtmlAttribute(String.valueOf(valorAdeCarencia)) : "0" %>"/>
          <% } %>
          <% if (serInfBancariaObrigatoria) { %>
          <h3 class="legend">
            <span><hl:message key="rotulo.servidor.informacoesbancarias"/></span>
          </h3>
          <div class="row">
            <div class="form-group col-sm-6">  
              <div class="row">
                <div class="col-sm-3">                                
                  <label for="numBanco">
                    <hl:message key="rotulo.servidor.informacoesbancarias.banco.abreviado"/>
                  </label>
                  <hl:htmlinput name="numBanco" 
                                type="text" 
                                classe="form-control" 
                                di="numBanco" 
                                size="3" 
                                mask="#D3"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.banco", responsavel)%>"
                  />
                </div>
                <div class="col-sm-9">              
                  <label for="numAgencia">
                    <hl:message key="rotulo.servidor.informacoesbancarias.agencia.abreviado"/>
                  </label>
                    <hl:htmlinput name="numAgencia" 
                                  type="text" 
                                  classe="form-control" 
                                  di="numAgencia" 
                                  size="5"
                                  mask="#*30"
                                  placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.agencia", responsavel)%>"/>
                </div> 
              </div>             
            </div>
            <div class="form-group col-sm-3">
              <label for="numConta">
                <hl:message key="rotulo.servidor.informacoesbancarias.conta.abreviado"/>
              </label>
              <hl:htmlinput name="numConta" 
                            type="text" 
                            classe="form-control" 
                            di="numConta"
                            size="12"
                            mask="#*40" 
                            placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.conta", responsavel)%>"/>
            </div>                                
          </div>            
          <% } %>
          <div class="row">
            <div class="form-group col-sm-6 col-md-6">
              <label for="adeVlr"><hl:message key="rotulo.consignacao.valor.parcela"/></label>
              <hl:htmlinput name="adeVlr"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(adeVlrAtual)%>"
                            mask="#F11"
                            size="8"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            others="disabled"/>
            </div>
          </div>
          <div class="row d-flex align-items-center">
            <div class="form-group col-sm-6 col-md-6">
              <label for="adePrazoEdt"><hl:message key="rotulo.consignacao.prazo.restante"/></label>
              <hl:htmlinput name="adePrazoEdt"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute((prazo != -1 ? String.valueOf(prazoRest) : ""))%>"
                            size="8"
                            onBlur="fout(this);ValidaMascara(this);verificaPrazo(this);"
                            onFocus="SetarEventoMascara(this,'#D4',true);"
                            onChange="setaPrazo(this);"
                            others="disabled"/>
            </div>
            
            <% if (maxPrazo <= 0) { %>
            <div class="form-group col-sm-6 col-md-6 pt-4">
              <input type="checkbox" class="form-check-input ml-1" name="adeSemPrazo" id="adeSemPrazo" mask="#*200" others="<%=TextHelper.forHtmlAttribute(((maxPrazo==0 || prazo==-1)? "CHECKED" + " DISABLED": "DISABLED") )%>" />
              <label for="adeSemPrazo" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.consignacao.prazo.indeterminado"/></label>
            </div>
            <% } %>
          </div>
          
           <% if (permiteCadVlrTac) { %>
          <div class="row">
            <div class="form-group col-sm-6 col-md-6">
              <label for="adeVlrTac"><hl:message key="rotulo.consignacao.valor.tac.moeda"/></label>
              <hl:htmlinput name="adeVlrTac"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR_TAC) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_TAC).toString(), "en", NumberHelper.getLang()) : "")%>"
                            mask="#F11"
                            size="8"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" 
                            others="disabled"/>
            </div>
          </div>
          <% } %>
          
          
          <% if (permiteCadVlrIof) { %>
          <div class="row">
            <div class="form-group col-sm-6 col-md-6">
              <label for="adeVlrIof"><hl:message key="rotulo.consignacao.valor.iof.moeda"/></label>
              <hl:htmlinput name="adeVlrIof"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR_IOF) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_IOF).toString(), "en", NumberHelper.getLang()) : "")%>"
                            mask="#F11"
                            size="8"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            others="disabled"/>
            </div>
          </div>
          <%  } %>
          
          <div class="row">
            <div class="form-group col-sm-6 col-md-6">
              <label for="adeVlrLiquido"><hl:message key="rotulo.consignacao.valor.liquido.liberado.moeda"/></label>
              <hl:htmlinput name="adeVlrLiquido"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR_LIQUIDO) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_LIQUIDO).toString(), "en", NumberHelper.getLang()) : "")%>"
                            mask="#F11"
                            size="8"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
            </div>
          </div>
          
          <% if (permiteCadVlrMensVinc) { %>
          <div class="row">
            <div class="form-group col-sm-6 col-md-6">
              <label for="adeVlrMensVinc"><hl:message key="rotulo.consignacao.valor.mensalidade.vinc.moeda"/></label>
              <hl:htmlinput name="adeVlrMensVinc"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR_MENS_VINC) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_MENS_VINC).toString(), "en", NumberHelper.getLang()) : "")%>"
                            mask="#F11"
                            size="8"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" 
                            others="disabled"/>
            </div>
          </div>
          <% } %>
          
          <% if (boolTpsSegPrestamista){ %>
          <div class="row">
            <div class="form-group col-sm-6 col-md-6">
              <label for=adeVlrSegPrestamista><hl:message key="rotulo.consignacao.seguro.prestamista"/></label>
              <hl:htmlinput name="adeVlrSegPrestamista"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR_SEG_PRESTAMISTA) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_SEG_PRESTAMISTA).toString(), "en", NumberHelper.getLang()) : "")%>"
                            mask="#F11"
                            size="8"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            others="disabled"/>
            </div>
          </div>
          <% } %>
          
          <%  if (permiteVlrLiqTxJuros) {  %>
          <div class="row">
            <div class="form-group col-sm-6 col-md-6">
              <label for=adeTaxaJuros><%=temCET ? ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet.atual", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros.atual", responsavel)%></label>
              <hl:htmlinput name="adeTaxaJuros"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_TAXA_JUROS) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_TAXA_JUROS).toString(), "en", NumberHelper.getLang()) : "")%>"
                            mask="#F11"
                            size="8"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" 
                            others="disabled"/>
            </div>
          </div>
          <%  }  %>
          <% if (permiteCadIndice && !indiceSomenteAutomatico) { %>
          <div class="row">
            <div class="form-group col-sm-6 col-md-6">
              <label for=adeIndice><hl:message key="rotulo.consignacao.indice"/><hl:message key="rotulo.campo.opcional"/></label>
              <hl:htmlinput name="adeIndice"
                            type="text"
                            classe="form-control"
                            value="<%=TextHelper.forHtmlAttribute(adeIndice)%>"
                            size="10"
                            others="disabled"/>
            </div>
          </div>
          <% } %>
          <% if (!exigeSenhaServidor.equals(CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS)) { %>
          <%  if (!TextHelper.isNull(mascaraLogin)) { %>
          <div class="row">
            <div class="form-group col-sm-6 col-md-6">
              <label for=serLogin><hl:message key="rotulo.usuario.autorizacao.servidor.singular"/><%=exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS) ? "" : " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel)%></label>
              <hl:htmlinput name="serLogin"
                            type="text"
                            classe="form-control"
                            mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>"
                            size="15"
                            others="disabled"/>
            </div>
          </div>
            <% }  %>
            <hl:senhaServidorv4 senhaObrigatoria="<%=(String)(exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS) ? "true" : "false")%>" 
                    senhaParaAutorizacaoReserva="false"                  
                    nomeCampoSenhaCriptografada="serAutorizacao"
                    rseCodigo="<%=rseCodigo %>"
                    nf="submit" />
          <% } %>
    </div>
   </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="atualizaContrato(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div> 
</form>      
<script type="text/JavaScript">
f0 = document.forms[0];

function atualizaContrato() {
	if (f0.senha != null && trim(f0.senha.value) != '') {
	    CriptografaSenha(f0.senha, f0.serAutorizacao, false);
	}
	
	if (confirm("<%=TextHelper.forJavaScriptBlock(mensagem)%><hl:message key="mensagem.confirmacao.atualizacao"/>")) {	 
		f0.submit();
		return true;
	}
}
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>