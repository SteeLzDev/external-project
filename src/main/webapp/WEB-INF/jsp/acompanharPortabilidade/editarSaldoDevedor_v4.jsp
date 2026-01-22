<%--
* <p>Title: editarSaldoDevedor.jsp</p>
* <p>Description: Página de edição de saldo devedor</p>
* <p>Copyright: Copyright (c) 2006</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: igor.lucas $
* $Revision: 22236 $
* $Date: 2017-08-24 18:50:40 -0300 (qui, 24 ago 2017) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t"  tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<?> anexos = (List<?>) request.getAttribute("anexos");  
CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");
Map<Integer,TransferObject> propostas = (Map<Integer,TransferObject>) request.getAttribute("propostas");

boolean showInfBancarias = (boolean) request.getAttribute("showInfBancarias");
boolean exigeMultiplosSaldos = (boolean) request.getAttribute("exigeMultiplosSaldos");
boolean exigeValorComDesconto = (boolean) request.getAttribute("exigeValorComDesconto");
boolean numeroContratoObrigatorio = (boolean) request.getAttribute("numeroContratoObrigatorio");
boolean infSaldoDevedorOpcional = (boolean) request.getAttribute("infSaldoDevedorOpcional");
boolean exibeCamposdvLinkBoleto = (boolean) request.getAttribute("exibeCamposdvLinkBoleto");
boolean exibeCampoTaxaJurosContratoSaldoDevedor = (boolean) request.getAttribute("exibeCampoTaxaJurosContratoSaldoDevedor");
boolean opInserir = (boolean) request.getAttribute("opInserir"); 
boolean exigeAnexoBoleto = (boolean) request.getAttribute("exigeAnexoBoleto");
boolean exigeAnexoDsd = (boolean) request.getAttribute("exigeAnexoDsd");
boolean isCompra = (boolean) request.getAttribute("isCompra");
boolean detalheInfSaldoObrigatorio = (boolean) request.getAttribute("detalheInfSaldoObrigatorio");

boolean temAnexo = exigeAnexoBoleto || exigeAnexoDsd ? true : false;

int qtdMinPropostas = (int) request.getAttribute("qtdMinPropostas");
int qtdMaxPropostas = (int) request.getAttribute("qtdMaxPropostas");

boolean exibePropostaRefinaciamento = (boolean) request.getAttribute("exibePropostaRefinaciamento");
String tipo = (String) request.getAttribute("tipo");
String codBancoPadraoCsa = !TextHelper.isNull(request.getAttribute("codBancoPadraoCsa")) ? (String) request.getAttribute("codBancoPadraoCsa") : "";
String codAgenciaPadraoCsa = !TextHelper.isNull(request.getAttribute("codAgenciaPadraoCsa")) ? (String) request.getAttribute("codAgenciaPadraoCsa") : "";
String codContaPadraoCsa = !TextHelper.isNull(request.getAttribute("codContaPadraoCsa")) ? (String) request.getAttribute("codContaPadraoCsa") : "";
String nomeFavorecidoPadraoCsa = (String) request.getAttribute("nomeFavorecidoPadraoCsa");
String cnpjFavorecidoPadraoCsa = (String) request.getAttribute("cnpjFavorecidoPadraoCsa");
String codBancoSaldoDevedor = (String) request.getAttribute("codBancoSaldoDevedor");
String adeCodigo = (String) request.getAttribute("adeCodigo");
String valorSaldoDevedor = (String) request.getAttribute("valorSaldoDevedor");
String valorSaldoDevedor1 = (String) request.getAttribute("valorSaldoDevedor1");
String valorSaldoDevedor2 = (String) request.getAttribute("valorSaldoDevedor2");
String dataSaldoDevedor1 = (String) request.getAttribute("dataSaldoDevedor1");
String valorSaldoDevedor3 = (String) request.getAttribute("valorSaldoDevedor3");
String dataSaldoDevedor2 = (String) request.getAttribute("dataSaldoDevedor2");
String dataSaldoDevedor3 = (String) request.getAttribute("dataSaldoDevedor3");
String taxaJurosContratoSaldoDevedor = (String) request.getAttribute("taxaJurosContratoSaldoDevedor");
String qtdePrestacoes = (String) request.getAttribute("qtdePrestacoes");
String valorSaldoDevedorDesc = (String) request.getAttribute("valorSaldoDevedorDesc");
String codAgenciaSaldoDevedor = (String) request.getAttribute("codAgenciaSaldoDevedor");
String codContaSaldoDevedor = (String) request.getAttribute("codContaSaldoDevedor");
String nomeFavorecidoSdv = (String) request.getAttribute("nomeFavorecidoSdv");
String cnpjFavorecidoSdv = (String) request.getAttribute("cnpjFavorecidoSdv");
String numeroContrato = (String) request.getAttribute("numeroContrato");
String linkBoleto = (String) request.getAttribute("linkBoleto");
String observacao = (String) request.getAttribute("observacao");
String urlEditarSaldoDevedor = (String) request.getAttribute("urlEditarSaldoDevedor");
boolean isSaldoRescisao = (boolean) request.getAttribute("isSaldoRescisao");

String maskData = "SetarEventoMascaraV4(this,'"+TextHelper.forHtmlAttribute(LocaleHelper.getDateJavascriptPattern())+"',true);";
String maskCnpj = "SetarEventoMascaraV4(this,'"+LocaleHelper.getCnpjMask()+"',true);"; 
%>
<c:set var="title">
  <hl:message key="rotulo.editar.saldo.devedor.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form action="<%=TextHelper.forHtmlAttribute(SynchronizerToken.updateTokenInURL(".." + urlEditarSaldoDevedor + "?acao=salvar&_skip_history_=true", request))%>&ADE_CODIGO=<%=TextHelper.forHtmlAttribute(adeCodigo)%>&tipo=<%=TextHelper.forHtmlAttribute(tipo)%>" method="post" name="form1" <%=(String)(exigeAnexoBoleto || exigeAnexoDsd ? " enctype=\"multipart/form-data\"" : "" )%>>
    <div class="row firefox-print-fix">
      <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
      <hl:detalharADEv4 name="autdes" table="true" type="edt_saldo_devedor" scope="request" divSizeCSS="col-sm-6" />
      <%-- Fim dos dados da ADE --%>     
    </div>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><%= isCompra ? ApplicationResourcesHelper.getMessage("rotulo.editar.saldo.devedor.compra", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.editar.saldo.devedor.titulo", responsavel)%></h2>
      </div>
      <div class="card-body">
      <% if (!exigeMultiplosSaldos) { %>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="valorSaldoDevedor"><hl:message key="rotulo.saldo.devedor.vencimento"/></label>
          <hl:htmlinput name="valorSaldoDevedor" type="text" classe="form-control" di="valorSaldoDevedor" size="8" maxlength="10" value="<%=TextHelper.forHtmlAttribute(valorSaldoDevedor)%>" onFocus="SetarEventoMascaraV4(this,'#F10',true);" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" />
        </div>
        <% if (exibeCampoTaxaJurosContratoSaldoDevedor) { %>
        <div class="form-group col-sm-12 col-md-6">
          <label for="taxaJurosContratoSaldoDevedor"><hl:message key="rotulo.saldo.devedor.taxa.juros.contrato"/></label>
          <hl:htmlinput name="taxaJurosContratoSaldoDevedor" type="text" classe="form-control" di="taxaJurosContratoSaldoDevedor" size="8" maxlength="10" value="<%=TextHelper.forHtmlAttribute(taxaJurosContratoSaldoDevedor)%>" onFocus="SetarEventoMascaraV4(this,'#*300',true);" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" />
        </div>
       <% } %>
      </div>
      <% } else { %>
      <div class="row">
         <div class="form-group col-sm-12 col-md-6">
           <label for="valorSaldoDevedor1"><hl:message key="rotulo.saldo.devedor.vencimento.1.v4"/></label>
           <hl:htmlinput name="valorSaldoDevedor1" type="text" classe="form-control" di="valorSaldoDevedor1" size="8" maxlength="10" value="<%=TextHelper.forHtmlAttribute(valorSaldoDevedor1)%>" onFocus="SetarEventoMascaraV4(this,'#F10',true);" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
         </div>
         <div class="form-group col-sm-12 col-md-6">
           <label for="dataSaldoDevedor1"><hl:message key="rotulo.saldo.devedor.vencimento.data.v4"/></label> 
           <hl:htmlinput name="dataSaldoDevedor1" di="dataSaldoDevedor1" type="text" classe="form-control" size="10" value="<%=TextHelper.forHtmlAttribute(dataSaldoDevedor1)%>" onFocus="<%=maskData%>"/> 
         </div>
      </div>
      <div class="row">
         <div class="form-group col-sm-12 col-md-6">
           <label for="valorSaldoDevedor2"><hl:message key="rotulo.saldo.devedor.vencimento.2.v4"/></label>
           <hl:htmlinput name="valorSaldoDevedor2" type="text" classe="form-control" di="valorSaldoDevedor2" size="8" maxlength="10" onFocus="SetarEventoMascaraV4(this,'#F10',true);" value="<%=TextHelper.forHtmlAttribute(valorSaldoDevedor2)%>" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
         </div>
         <div class="form-group col-sm-12 col-md-6">
           <label for="dataSaldoDevedor2"><hl:message key="rotulo.saldo.devedor.vencimento.data.v4"/></label>
           <hl:htmlinput name="dataSaldoDevedor2" di="dataSaldoDevedor2" type="text" classe="form-control" size="10" value="<%=TextHelper.forHtmlAttribute(dataSaldoDevedor2)%>" onFocus="<%=maskData%>"/> 
         </div>
      </div>
      <div class="row">
         <div class="form-group col-sm-12 col-md-6">
           <label for="valorSaldoDevedor3"><hl:message key="rotulo.saldo.devedor.vencimento.3.v4"/></label>
           <hl:htmlinput name="valorSaldoDevedor3" type="text" classe="form-control" di="valorSaldoDevedor3" size="8" maxlength="10" onFocus="SetarEventoMascaraV4(this,'#F10',true);" value="<%=TextHelper.forHtmlAttribute(valorSaldoDevedor3)%>" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
         </div>
         <div class="form-group col-sm-12 col-md-6">
           <label for="dataSaldoDevedor3"><hl:message key="rotulo.saldo.devedor.vencimento.data.v4"/></label>
           <hl:htmlinput name="dataSaldoDevedor3" di="dataSaldoDevedor3" type="text" classe="form-control" size="10" value="<%=TextHelper.forHtmlAttribute(dataSaldoDevedor3)%>" onFocus="<%=maskData%>"/> 
         </div>
      </div>
      <div class="row">
      <% if (exibeCampoTaxaJurosContratoSaldoDevedor) { %>
        <div class="form-group col-sm-12 col-md-6">
          <label for="taxaJurosContratoSaldoDevedor"><hl:message key="rotulo.saldo.devedor.taxa.juros.contrato"/></label>
          <hl:htmlinput name="taxaJurosContratoSaldoDevedor" type="text" classe="form-control" di="taxaJurosContratoSaldoDevedor" size="55" maxlength="300" onFocus="SetarEventoMascaraV4(this,'#*300',true);" value="<%=TextHelper.forHtmlAttribute(taxaJurosContratoSaldoDevedor)%>" />
        </div>
       <% } %>
        <div class="form-group col-sm-12 col-md-6">
          <label for="qtdePrestacoes"><hl:message key="rotulo.saldo.devedor.qtde.parcelas.v4"/></label>
          <hl:htmlinput name="qtdePrestacoes" type="text" classe="form-control" di="qtdePrestacoes" size="8" maxlength="10" value="<%=TextHelper.forHtmlAttribute(qtdePrestacoes)%>" onFocus="SetarEventoMascaraV4(this,'#D3',true);" />
        </div>
      </div>
      <% } %>
      <% if (exigeValorComDesconto) { %>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="qtdePrestacoes"><hl:message key="rotulo.saldo.devedor.qtde.parcelas.v4"/></label>
          <hl:htmlinput name="qtdePrestacoes" type="text" classe="form-control" di="qtdePrestacoes" size="8" maxlength="10" value="<%=TextHelper.forHtmlAttribute(qtdePrestacoes)%>" onFocus="SetarEventoMascaraV4(this,'#D3',true);" />
        </div>
      </div>
      <% } %>
      <% if (exigeValorComDesconto) { %>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="valorSaldoDevedorDesc"><hl:message key="rotulo.saldo.devedor.desconto.vencimento"/></label>
          <hl:htmlinput name="valorSaldoDevedorDesc" type="text" classe="form-control" di="valorSaldoDevedorDesc" size="8" maxlength="10" value="<%=TextHelper.forHtmlAttribute(valorSaldoDevedorDesc)%>" onFocus="SetarEventoMascaraV4(this,'#F10',true);"  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
        </div>
      </div>
      <% } %>
      <% if (!codBancoPadraoCsa.equals("") || !codAgenciaPadraoCsa.equals("") || !codContaPadraoCsa.equals("")) { %>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="usarInfPadrao"><hl:message key="rotulo.saldo.devedor.informacoes.deposito.v4"/></label>
          <div class="form-check pt-2">
            <input type="checkbox" class="form-check-input ml-1" name="usarInfPadrao" id="usarInfPadrao" value="1" onclick="changeForm()" <%=TextHelper.forHtmlContent( opInserir ? "checked" : "" )%> />
            <label for="usarInfPadrao" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.saldo.devedor.utilizar.padrao"/></label>
          </div>
        </div>
      </div>
      <% } %>
      <% if (showInfBancarias) { %>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6" aria-labelledby="infBanco">
          <label for="infBanco"> <%= infSaldoDevedorOpcional ? " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel) : "" %></label>
          <label for="ajudaBanco"><hl:message key="rotulo.saldo.devedor.codigo.banco.deposito.v4"/></label>
          <hl:htmlinput name="ajudaBanco" type="text" classe="form-control" di="ajudaBanco" size="8" maxlength="8" onFocus="SetarEventoMascaraV4(this,'#A8',true);" value="<%=TextHelper.forHtmlAttribute(!codBancoSaldoDevedor.equals(\"\") ? codBancoSaldoDevedor : codBancoPadraoCsa)%>" onBlur="if (!IsNulo(f0.ajudaBanco)) {SelecionaComboBanco(f0.banco, f0.ajudaBanco.value, arrayBancos);}" />
        </div>
        <div class="form-group col-sm-12 col-md-6">
          <label for="banco"><hl:message key="rotulo.saldo.devedor.banco.deposito.v4"/></label>
          <SELECT CLASS="form-control" NAME="banco" id='banco' onChange="f0.ajudaBanco.value = f0.banco.value;"><OPTION VALUE="" SELECTED><hl:message key="mensagem.informe.banco"/></OPTION></SELECT>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="agencia"><hl:message key="rotulo.saldo.devedor.agencia.deposito.v4"/><%= infSaldoDevedorOpcional ? " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel) : "" %></label>
          <hl:htmlinput name="agencia" type="text" classe="form-control" di="agencia" size="8" maxlength="5" onFocus="SetarEventoMascaraV4(this,'#D5',true);" value="<%=TextHelper.forHtmlAttribute(!codAgenciaSaldoDevedor.equals(\"\") ? codAgenciaSaldoDevedor : codAgenciaPadraoCsa)%>" />
        </div>
        <div class="form-group col-sm-12 col-md-6">
        <label for="conta"><hl:message key="rotulo.saldo.devedor.conta.deposito.v4"/><%= infSaldoDevedorOpcional ? " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel) : "" %></label>
        <hl:htmlinput name="conta" type="text" classe="form-control" di="conta" size="8" maxlength="11" onFocus="SetarEventoMascaraV4(this,'#D11',true);" value="<%=TextHelper.forHtmlAttribute(!codContaSaldoDevedor.equals(\"\") ? codContaSaldoDevedor : codContaPadraoCsa)%>" />
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="nomeFavorecido"><hl:message key="rotulo.saldo.devedor.favorecido.deposito.v4"/><%= infSaldoDevedorOpcional ? " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel) : "" %></label>
          <hl:htmlinput name="nomeFavorecido" type="text" classe="form-control" di="nomeFavorecido" size="30" maxlength="100" onFocus="SetarEventoMascaraV4(this,'#*100',true);" value="<%=TextHelper.forHtmlAttribute(!nomeFavorecidoSdv.equals(\"\") ? nomeFavorecidoSdv : nomeFavorecidoPadraoCsa)%>" />
        </div>
        <div class="form-group col-sm-12 col-md-6">
          <label for="cnpjFavorecido"><hl:message key="rotulo.saldo.devedor.cnpj.deposito.v4"/><%= infSaldoDevedorOpcional ? " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel) : "" %></label>
          <hl:htmlinput name="cnpjFavorecido" type="text" classe="form-control" di="cnpjFavorecido" size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjSize())%>" maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMaxLenght())%>" 
              value="<%=TextHelper.forHtmlAttribute(!cnpjFavorecidoSdv.equals(\"\") ? cnpjFavorecidoSdv : cnpjFavorecidoPadraoCsa)%>" 
              onFocus="<%=maskCnpj%>"/>
        </div>
      </div>
      <% } %>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="numeroContrato"><hl:message key="rotulo.saldo.devedor.numero.contrato"/><%= !numeroContratoObrigatorio ? " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel) : "" %></label>
          <hl:htmlinput name="numeroContrato" type="text" classe="form-control" di="numeroContrato" size="30" maxlength="40" onFocus="SetarEventoMascaraV4(this,'#*40',true);"  value="<%=TextHelper.forHtmlAttribute(numeroContrato)%>" />
        </div>
      <% if (exibeCamposdvLinkBoleto) { %>
        <div class="form-group col-sm-12 col-md-6">
          <label for="sdvLinkBoleto"><hl:message key="rotulo.saldo.devedor.url.boleto"/></label>
          <hl:htmlinput name="sdvLinkBoleto" type="text" classe="form-control" di="sdvLinkBoleto" size="55" maxlength="300" onFocus="SetarEventoMascaraV4(this,'#*300',true);" value="<%=TextHelper.forHtmlAttribute(linkBoleto)%>" />
        </div>
       <% } %>
      </div>
      <div class="row">
         <div class="form-group col-sm-12 col-md-6">
           <label for="obs"><hl:message key="rotulo.saldo.devedor.observacao"/></label>
           <hl:htmlinput name="obs" type="textarea" classe="form-control" di="obs" cols="32" rows="5" onFocus="SetarEventoMascaraV4(this,'#*10000',true);" value="<%=TextHelper.forHtmlAttribute(observacao)%>" />
         </div>
       </div>
       <div class="row">
       <% if (exigeAnexoDsd) { %>
         <div class="form-group col-sm-12 col-md-6">
           <label for="anexo_dsd"><hl:message key="rotulo.anexo.saldo.dsd"/></label>
           <div class='form-check'><input type="file" class="form-control" name="anexo_dsd" id="anexo_dsd" size="50"></div>
         </div>
       <% } %>
       <% if (exigeAnexoBoleto) { %>
         <div class="form-group col-sm-12 col-md-6">
           <label for="anexo_boleto"><hl:message key="rotulo.anexo.saldo.boleto"/></label>
           <div class='form-check'><input type="file" class="form-control" name="anexo_boleto" id="anexo_boleto" size="50"></div>
         </div>
       <% } %>
       </div>
       <% if (detalheInfSaldoObrigatorio) { %>
       <div class="row">
         <div class="form-group col-sm-12 col-md-6">
           <label for="detalhe"><hl:message key="rotulo.saldo.devedor.detalhes.calculo"/></label>
           <hl:htmlinput name="detalhe" type="textarea" classe="form-control" di="detalhe" cols="60" rows="10" others="onFocus=\"SetarEventoMascara(this,'#*65000',true);\" onBlur=\"fout(this);ValidaMascara(this);\""/>
         </div>
       </div>
       <% } %>
          <% if (exibePropostaRefinaciamento) { %>
          <div class="row">
              <div class=" col-md-12 form-check mt-2" role="radiogroup"
                   aria-labelledby="refinanciamentoReducaoParcelas">
                  <div class="form-group my-0">
                      <span id="refinanciamentoReducaoParcelas"><hl:message
                              key="rotulo.saldo.devedor.proposta.refinanciamento.parcelas"/></span>
                  </div>
                  <div class="form-check form-check-inline mt-2">
                      <input class="form-check-input ml-1" type="radio" name="refinanciamentoReducaoParcela"
                             id="refinanciamentoReducaoParcelasSim" value="true"
                             onFocus="SetarEventoMascara(this,'#*100',true);" onclick="exibeRefinancimanetoParcelas();"
                             onBlur="fout(this);ValidaMascara(this);">
                      <label class="form-check-label pr-3" for="refinanciamentoReducaoParcelasSim">
                          <span class="text-nowrap align-text-top"><hl:message key="rotulo.sim"/></span>
                      </label>
                  </div>
                  <div class="form-check-inline form-check mt-02">
                      <input class="form-check-input ml-1" type="radio" name="refinanciamentoReducaoParcela"
                             id="refinanciamentoReducaoParcelasNao" value="false" CHECKED
                             onFocus="SetarEventoMascara(this,'#*100',true);" onclick="exibeRefinancimanetoParcelas();"
                             onBlur="fout(this);ValidaMascara(this);">
                      <label class="form-check-label" for="refinanciamentoReducaoParcelasNao">
                          <span class="text-nowrap align-text-top"><hl:message key="rotulo.nao"/></span>
                      </label>
                  </div>
              </div>
          </div>
          <div class="row" id="exibeRefinancimanetoReducaoParcelas">
              <div class="form-group col-sm-12">
                  <hl:htmlinput name="exibeRefinancimanetoReducaoParcelasText" type="textarea" classe="form-control"
                                di="exibeRefinancimanetoReducaoParcelasText" cols="32" rows="5"
                                onFocus="SetarEventoMascaraV4(this,'#*10000',true);" value=""/>
              </div>
          </div>
          <% } %>
      </div>
 </div>
    <hl:editaPropostaPagamentoDividav4 qtdMinPropostas="<%=(int)( qtdMinPropostas )%>" qtdMaxPropostas="<%=(int)( qtdMaxPropostas )%>" lstPropostas="<%=(Map<Integer,TransferObject>) propostas%>" />
  <% if (exigeAnexoBoleto || exigeAnexoDsd) { %>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.anexo.disponivel.plural.titulo"/></h2>
      </div>
      <div class="card-body table-responsive">
         <%-- Utiliza a tag library ListaAnexosContratoTag.java para listar os anexos do contrato --%>
         <% pageContext.setAttribute("anexos", anexos); %>
         <hl:listaAnexosContratov4 name="anexos" table="true" type="alterar"/>
         <%-- Fim dos anexos do contrato --%>
      </div>
    </div>
        <%-- Fim dos anexos do contrato --%>
  <% } %>
  <input type="hidden" id="permitirEditarSaldoDevedorNovamente" name="permitirEditarSaldoDevedorNovamente" value="true" />
   <!-- Modal ConfirmaPermissaoEditarSaldoDevedor-->
     <div class="modal fade" id="modalConfirmaPermissaoEditarSaldoDevedor" tabindex="-1" role="dialog" aria-labelledby="modalConfirmaPermissaoEditarSaldoDevedorLabel" aria-hidden="true">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title about-title mb-0"><hl:message key="rotulo.suspensao.decisao.judicial.confirma.titulo"/></h5>
	        <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
	          <span aria-hidden="true"></span>
	        </button>
	      </div>
	      <div class="modal-body" id="modal-body-confirmacao-editar-sdv">
	      </div>
	      <div class="modal-footer pt-0">
			  <div class="btn-action mt-2 mb-0">
				<a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.nao"/>' href="#"><hl:message key="rotulo.nao"/></a>
				<a class="btn btn-primary" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.sim"/>' onclick="permitirEditarSaldoDevedorNovamente(false);" href="#"><hl:message key="rotulo.sim"/></a>
			  </div>
		</div>
	    </div>
	  </div>
  	</div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" id="btnConfirmar" href="#no-back" onClick="if(validForm()){ enableAll(); enviaForm();} return false;"><hl:message key="rotulo.botao.confirmar"/></a>
    </div>
   </form>
</c:set>
<c:set var="javascript">
    <script src="../js/scripts_2810.js"></script>
    <script src="../js/validacoes.js"></script>
    <script type="text/JavaScript">
      var f0 = document.forms[0];

      var arrayBancos = <%=(String)JspHelper.geraArrayBancos(responsavel)%>;
      var bancoPadraoCsa = '<%=TextHelper.forJavaScriptBlock(codBancoPadraoCsa)%>';
      var agenciaPadraoCsa = '<%=TextHelper.forJavaScriptBlock(codAgenciaPadraoCsa)%>';
      var contaPadraoCsa = '<%=TextHelper.forJavaScriptBlock(codContaPadraoCsa)%>';
      var nomeFavorecidoPadraoCsa = '<%=TextHelper.forJavaScriptBlock(nomeFavorecidoPadraoCsa)%>';
      var cnpjFavorecidoPadraoCsa = '<%=TextHelper.forJavaScriptBlock(cnpjFavorecidoPadraoCsa)%>';

      function formLoad() {
        // Filta o combo de bancos com o valor pré-selecionado
        var banco = '<%=TextHelper.forJavaScriptBlock(!codBancoSaldoDevedor.equals("") ? codBancoSaldoDevedor : codBancoPadraoCsa)%>';
        <% if (showInfBancarias) { %>
            AtualizaFiltraComboExt(f0.banco, arrayBancos, '', '', banco, false, false, '', '');
        <% } %>
        // Seta o foco para o primeiro campo do formulário
        f0.valorSaldoDevedor<%=(String)(exigeMultiplosSaldos?"1":"")%>.focus();
        // Habilita/Desabilita os campos de depósito
        changeForm();
      }

      function changeForm() {
        with(document.form1) {
          if (f0.usarInfPadrao != null &&
              usarInfPadrao.checked) {
            
            <% if (showInfBancarias) { %>
                  if (bancoPadraoCsa != '') {
                    ajudaBanco.value = bancoPadraoCsa;
                    AtualizaFiltraComboExt(f0.banco, arrayBancos, '', '', bancoPadraoCsa, false, false, '', '');
                    ajudaBanco.disabled = true;
                    banco.disabled = true;
                  }
                  if (agenciaPadraoCsa != '') {
                    agencia.value = agenciaPadraoCsa;
                    agencia.disabled = true;
                  }
                  if (contaPadraoCsa != '') {
                    conta.value = contaPadraoCsa;
                    conta.disabled = true;
                  }
                  if (nomeFavorecidoPadraoCsa != '') {
                    nomeFavorecido.value = nomeFavorecidoPadraoCsa;
                    nomeFavorecido.disabled = true;
                  }
                  if (cnpjFavorecidoPadraoCsa != '') {
                    cnpjFavorecido.value = cnpjFavorecidoPadraoCsa;
                    cnpjFavorecido.disabled = true;
                  }
            <% } %>
          } else {
            enableAll();
          }
        }
      }

      function enableAll() {
        with(document.form1) {
          <% if (showInfBancarias) { %>         
                ajudaBanco.disabled = false;
                banco.disabled = false;
                agencia.disabled = false;
                conta.disabled = false;
                nomeFavorecido.disabled = false;
                cnpjFavorecido.disabled = false;
          <% } %>
        }
      }

      function validForm() {
        <% if (exigeMultiplosSaldos) { %>
        var campos = new Array("valorSaldoDevedor1", "dataSaldoDevedor1",
                               "valorSaldoDevedor2", "dataSaldoDevedor2",
                               "valorSaldoDevedor3", "dataSaldoDevedor3",
                               "qtdePrestacoes", "taxaJurosContratoSaldoDevedor"
        <% } else { %>
        var campos = new Array("valorSaldoDevedor"
        <% } %>

        <% if (exigeValorComDesconto) { %>
                               , "valorSaldoDevedorDesc"         
        <% } %>
        
        <% if (exibeCampoTaxaJurosContratoSaldoDevedor) { %>
        					   , "taxaJurosContratoSaldoDevedor"         
		<% } %>
        
        <% if (numeroContratoObrigatorio) { %>
                               , "numeroContrato"         
        <% } %>
        
        <% if (!infSaldoDevedorOpcional) { %>
                               , "banco", "agencia", "conta", "nomeFavorecido", "cnpjFavorecido");
        <% } else { %>
                              );
        <% } %>
        <% if (exigeMultiplosSaldos) { %>
        var msgs = new Array('<hl:message key="mensagem.informe.sdv.valor.primeiro.vencimento"/>', 
                   '<hl:message key="mensagem.informe.sdv.data.primeiro.vencimento"/>',
                             '<hl:message key="mensagem.informe.sdv.valor.segundo.vencimento"/>', 
                             '<hl:message key="mensagem.informe.sdv.data.segundo.vencimento"/>',
                             '<hl:message key="mensagem.informe.sdv.valor.terceiro.vencimento"/>', 
                             '<hl:message key="mensagem.informe.sdv.data.terceiro.vencimento"/>',
                             '<hl:message key="mensagem.informe.sdv.qtde.parcelas"/>',
                             '<hl:message key="mensagem.informe.sdv.taxa.juros.contrato"/>'
        <% } else { %>
        var msgs = new Array('<hl:message key="mensagem.informe.saldo.devedor"/>'
        <% } %>

        <% if (exigeValorComDesconto) { %>
                             , '<hl:message key="mensagem.informe.saldo.devedor.desconto"/>'         
        <% } %>

        <% if (exibeCampoTaxaJurosContratoSaldoDevedor) { %>
				   			 , '<hl:message key="mensagem.informe.sdv.taxa.juros.contrato"/>'         
		<% } %>
        
        <% if (numeroContratoObrigatorio) { %>
                             , '<hl:message key="mensagem.informe.numero.contrato"/>'
        <% } %>
        
        <% if (!infSaldoDevedorOpcional) { %>
                             , '<hl:message key="mensagem.informe.banco.deposito"/>',
                             '<hl:message key="mensagem.informe.agencia.deposito"/>',
                             '<hl:message key="mensagem.informe.conta.deposito"/>',
                             '<hl:message key="mensagem.informe.favorecido.deposito"/>',
                             '<hl:message key="mensagem.informe.cnpj.deposito"/>');
        <% } else { %>
                             );
        <% } %>

        if (f0.valorSaldoDevedor != null && (f0.valorSaldoDevedor.value == '' || isNaN(parseFloat(parse_num(f0.valorSaldoDevedor.value))) || parseFloat(parse_num(f0.valorSaldoDevedor.value)) <= 0.0)) {
           alert('<hl:message key="mensagem.erro.saldo.devedor.maior.zero"/>');
           f0.valorSaldoDevedor.focus();
           return false;
        }
        if (f0.valorSaldoDevedor1 != null && (f0.valorSaldoDevedor1.value == '' || isNaN(parseFloat(parse_num(f0.valorSaldoDevedor1.value))) || parseFloat(parse_num(f0.valorSaldoDevedor1.value)) <= 0.0)) {
           alert('<hl:message key="mensagem.erro.primeiro.saldo.devedor.maior.zero"/>');
           f0.valorSaldoDevedor1.focus();
           return false;
        }
        if (f0.valorSaldoDevedor2 != null && (f0.valorSaldoDevedor2.value == '' || isNaN(parseFloat(parse_num(f0.valorSaldoDevedor2.value))) || parseFloat(parse_num(f0.valorSaldoDevedor2.value)) <= 0.0)) {
           alert('<hl:message key="mensagem.erro.segundo.saldo.devedor.maior.zero"/>');
           f0.valorSaldoDevedor2.focus();
           return false;
        }
        if (f0.valorSaldoDevedor3 != null && (f0.valorSaldoDevedor3.value == '' || isNaN(parseFloat(parse_num(f0.valorSaldoDevedor3.value))) || parseFloat(parse_num(f0.valorSaldoDevedor3.value)) <= 0.0)) {
           alert('<hl:message key="mensagem.erro.terceiro.saldo.devedor.maior.zero"/>');
           f0.valorSaldoDevedor3.focus();
           return false;
        }
        if (f0.valorSaldoDevedorDesc != null && (f0.valorSaldoDevedorDesc.value == '' || isNaN(parseFloat(parse_num(f0.valorSaldoDevedorDesc.value))) || parseFloat(parse_num(f0.valorSaldoDevedorDesc.value)) <= 0.0)) {
           alert('<hl:message key="mensagem.erro.saldo.devedor.desconto.maior.zero"/>');
           f0.valorSaldoDevedorDesc.focus();
           return false;
        }
        if (f0.taxaJurosContratoSaldoDevedor != null && (f0.taxaJurosContratoSaldoDevedor.value == '' || isNaN(parseFloat(parse_num(f0.taxaJurosContratoSaldoDevedor.value))) || parseFloat(parse_num(f0.taxaJurosContratoSaldoDevedor.value)) <= 0.0)) {
            alert('<hl:message key="mensagem.erro.saldo.devedor.taxa.juros.contrato.maior.zero"/>');
            f0.valorSaldoDevedorDesc.focus();
            return false;
        }
        if (f0.dataSaldoDevedor1 != null && f0.dataSaldoDevedor1.value != '' && !verificaData(f0.dataSaldoDevedor1.value)) {
          f0.dataSaldoDevedor1.focus();
          return false;
        }
        if (f0.dataSaldoDevedor2 != null && f0.dataSaldoDevedor2.value != '' && !verificaData(f0.dataSaldoDevedor2.value)) {
          f0.dataSaldoDevedor2.focus();
          return false;
        }
        if (f0.dataSaldoDevedor3 != null && f0.dataSaldoDevedor3.value != '' && !verificaData(f0.dataSaldoDevedor3.value)) {
          f0.dataSaldoDevedor3.focus();
          return false;
        }

        if ((f0.anexo_boleto == null || f0.anexo_boleto.value == '') && <%=exigeAnexoBoleto%> && <%=(anexos == null || anexos.isEmpty())%>) {
        	alert('<hl:message key="mensagem.informe.anexo.saldo.boleto"/>');
            return false;
        }

        if ((f0.anexo_dsd == null || f0.anexo_dsd.value == '') && <%=exigeAnexoDsd%>  && <%=(anexos == null || anexos.isEmpty())%>) {
        	alert('<hl:message key="mensagem.informe.anexo.saldo.dsd"/>');
            return false;
        }

        if (f0.anexo_dsd != null && f0.anexo_dsd.value != '' && 
              f0.anexo_boleto != null && f0.anexo_boleto.value != '' &&
                f0.anexo_dsd.value == f0.anexo_boleto.value) {
           alert('<hl:message key="mensagem.erro.saldo.devedor.anexo.diferentes"/>');
           return false;
        }

        <% if (exibeCamposdvLinkBoleto) { %>
        var sdvLinkBoleto = document.getElementById('sdvLinkBoleto');
        if (sdvLinkBoleto != null && sdvLinkBoleto.value != '') {
          var pattern = /^(((ht|f)tp(s?))\:\/\/)+(www.|[a-zA-Z].)+[a-zA-Z0-9\-\.]*[a-zA-Z][^\.\-\&](\:[0-9])?/;         
          var match = pattern.test(sdvLinkBoleto.value.toLowerCase());
          
          if(!match) { 
            sdvLinkBoleto.focus();
            sdvLinkBoleto.select();
            alert('<hl:message key="mensagem.informe.url.saldo.devedor"/>');
                        
            return;
          }
        }
        <% } %>

          <% if (exibePropostaRefinaciamento) { %>
          if ($("#exibeRefinancimanetoReducaoParcelas").is(":visible")) {
              if ($('#exibeRefinancimanetoReducaoParcelasText').val() == '') {
                  alert("<hl:message key='mensagem.erro.saldo.devedor.refinanciamento.parcelas'/>");
                  return false;
              }
          }
          <% } %>

        return ValidaCampos(campos, msgs);
      }

      <% if (exibePropostaRefinaciamento) { %>
  	function exibeRefinancimanetoParcelas() {
  		var radios = document.getElementById("refinanciamentoReducaoParcelasSim");
  		if (radios.checked) {
  			$("#exibeRefinancimanetoReducaoParcelas").show();
  		} else {
  			$("#exibeRefinancimanetoReducaoParcelas").hide();
  		}
  	}
      
      $(document).ready(function() {
  		var radios = document.getElementById("refinanciamentoReducaoParcelasSim");
  		if (radios.checked) {
    			$("#exibeRefinancimanetoReducaoParcelas").show();
    		} else {
    			$("#exibeRefinancimanetoReducaoParcelas").hide();
    		}
      });
      <% } %>

      function permitirEditarSaldoDevedorNovamente(valor){
			const permitirEditarSaldoDevedorNovamente = document.getElementById('permitirEditarSaldoDevedorNovamente');
			permitirEditarSaldoDevedorNovamente.value = valor;
			f0.submit(); 
	  }

      function consultarResultadoComparacaoSaldoDevedor() {
    		$.ajax({
    			type: 'POST',
    			url: '../v3/editarSaldoDevedor?acao=consultarCalculoSaldoDevedor&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>',
    			data: {
    				'adeCodigo' : '<%=autdes.getAttribute(Columns.ADE_CODIGO).toString()%>'
    			},
    			success: function (data, status, xhr) {
    				try {
    					var resultadoCalculoSaldoDevedor = parseFloat(parse_num(data));
    					const valorSaldoDevedorInput = parseFloat(parse_num(f0.valorSaldoDevedor.value));
    					
    					if (resultadoCalculoSaldoDevedor != null && valorSaldoDevedorInput > resultadoCalculoSaldoDevedor) {
    						const modalBody = document.getElementById("modal-body-confirmacao-editar-sdv");
    						modalBody.innerHTML = '<%= ApplicationResourcesHelper.getMessage("mensagem.info.sdv.informado.maior.que.saldo.calculado", responsavel, "' + resultadoCalculoSaldoDevedor.toFixed(2) + '") %>';
    						$('#modalConfirmaPermissaoEditarSaldoDevedor').modal('show');
    					} else {
    						f0.submit();
    					}
    				} catch (e) {
    					alert(e.message);
    				}
    			},
    			error: function (xhr, status, error) {
    				let msg = "";
    				try {
    					const response = JSON.parse(xhr.responseText);
    					if (response && response.mensagem) {
    						msg = response.mensagem;
    					}
    				} catch (e) {
    					console.error(e);
    				}
    				alert(msg);
    			}
    		});
    	}
      
      function enviaForm() {
    	  if (<%=isSaldoRescisao%>) {
	    	  consultarResultadoComparacaoSaldoDevedor();
           } else {
           	   f0.submit();
           }
  	  }

      function permitirEditarSaldoDevedorNovamente(valor){
			const permitirEditarSaldoDevedorNovamente = document.getElementById('permitirEditarSaldoDevedorNovamente');
			permitirEditarSaldoDevedorNovamente.value = valor;
			f0.submit(); 
	  }

      window.onload = formLoad;
    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>