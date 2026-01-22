<%@page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.web.EditarPropostaLeilaoModel" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

EditarPropostaLeilaoModel editarPropostaLeilaoModel = (EditarPropostaLeilaoModel) request.getAttribute("editarPropostaLeilaoModel");
boolean podeEditarProposta = editarPropostaLeilaoModel.isPodeEditarProposta();
%>
<c:set var="imageHeader">
    <use xlink:href="#i-mensagem"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.editar.proposta.leilao.solicitacao.titulo"/>
</c:set>
<c:set var="bodyContent">
<form action="../v3/editarPropostaLeilao" method="post" name="form1">
  <input type="hidden" name="acao" value="salvar" />
  <input type="hidden" name="ADE_CODIGO" value="<%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getAdeCodigo())%>" />
  <input type="hidden" name="filtro" value="<%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getFiltro())%>"/>      
  <% out.print(SynchronizerToken.generateHtmlToken(request));%>
  <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
  <% pageContext.setAttribute("autdes", editarPropostaLeilaoModel.getAde()); pageContext.setAttribute("autdesOrigem", editarPropostaLeilaoModel.getAdeOrigem());%>
  <hl:detalharADEv4 name="autdes" table="false" type="leilao"/>
  <%-- Fim dos dados da ADE --%>
  <div class="card" id="cardServidor">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.servidor.singular"/></h2>
    </div>
    <div class="card-body">
      <dl class="row data-list firefox-print-fix" id="dlServidor">
        <% if(responsavel.isCsa() && editarPropostaLeilaoModel.isTemRiscoPelaCsa()) { %>
        <dt class="col-6"><hl:message key="rotulo.servidor.risco.csa"/>:</dt>
        <dd class="col-6"><%=TextHelper.forHtmlContent(editarPropostaLeilaoModel.getArrRisco())%></dd>
        <%}%>
        <%-- Margem disponível do servidor ao qual incide o serviço da consignação base do leilão; --%>
        <% if(responsavel.isCsaCor() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_CONSIGNAVEL_SERVIDOR_EDITAR_PROPOSTA_LEILAO, CodedValues.TPC_SIM, responsavel)) { %>
        <dt class="col-6"><hl:message key="rotulo.simulacao.margem.consignavel" /></dt>
        <dd class="col-6"><%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getTipoVlrMargemDisponivel())%> <%=(String)(editarPropostaLeilaoModel.getExibeMargem().isSemRestricao() || editarPropostaLeilaoModel.getRseMargemRest().doubleValue() > 0 ? NumberHelper.reformat(editarPropostaLeilaoModel.getMargemConsignavel(), "en", NumberHelper.getLang()) : "0,00")%></dd>            
        <%} else if(!responsavel.isCsaCor()) {%>
        <dt class="col-6"><hl:message key="rotulo.simulacao.margem.consignavel" /></dt>
        <dd class="col-6"><%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getTipoVlrMargemDisponivel())%> <%=(String)(editarPropostaLeilaoModel.getExibeMargem().isSemRestricao() || editarPropostaLeilaoModel.getRseMargemRest().doubleValue() > 0 ? NumberHelper.reformat(editarPropostaLeilaoModel.getMargemConsignavel(), "en", NumberHelper.getLang()) : "0,00")%></dd>            
        <%} %>
        <%if (!TextHelper.isNull(editarPropostaLeilaoModel.getBcoDesc())) { %>
        <dt class="col-6"><hl:message key="rotulo.servidor.informacoesbancarias.banco.salario" /></dt>
        <dd class="col-6"><%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getBcoDesc())%></dd>  
        <%}%> 
      </dl>
    </div>
  </div> 
  <% if (responsavel.isCsaCor()) { %>
  <%-- Utiliza a tag library ListaPropostaLeilaoSolicitacaoTag.java para exibir as propostas de pagamento --%>
  <hl:listaPropostaLeilaoSolicitacaov4 lstPropostas="<%=editarPropostaLeilaoModel.getPropostas()%>" table="true" card="true" />
  <%-- Fim dos dados da ADE --%>
  <% } %> 
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="mensagem.proposta.leilao.solicitacao.informe.valores"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="form-group col-sm-12  col-md-4">
          <label for="dataDeAbertura"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.data.abertura"/></label>
          <input type="text" class="form-control" id="dataDeAbertura" name="dataDeAbertura" value="<%=TextHelper.forHtmlContent(editarPropostaLeilaoModel.getSoaData())%>" disabled>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12  col-md-4">
          <label for="horasParaFechamento"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.horas.restantes.fim.leilao"/></label>
          <input type="text" class="form-control" id="horasParaFechamento" name="horasParaFechamento" value="<%=TextHelper.forHtmlContent(editarPropostaLeilaoModel.getSoaDataValidade())%>" disabled>
        </div>
      </div>   
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="svcCodigo"><hl:message key="rotulo.servico.singular"/></label>
          <select class="form-control form-select" name="svcCodigo" id="svcCodigo" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%= (!podeEditarProposta) ? "disabled": "" %>>
            <option value="" SELECTED><hl:message key="rotulo.campo.selecione"/></option>
            <%
            List<TransferObject> servicos = TextHelper.groupConcat(editarPropostaLeilaoModel.getConvenio(), new String[]{Columns.SVC_DESCRICAO,Columns.SVC_CODIGO}, new String[]{Columns.CNV_COD_VERBA}, ",", true, true);
            Iterator<TransferObject> ite = servicos.iterator();
            TransferObject cnv = null;
            while (ite.hasNext()) {
              cnv = ite.next();
              String codigo = cnv.getAttribute(Columns.SVC_CODIGO).toString();
              String identificador = (cnv.getAttribute(Columns.CNV_COD_VERBA) != null) ? cnv.getAttribute(Columns.CNV_COD_VERBA).toString() : cnv.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
              String descricao = cnv.getAttribute(Columns.SVC_DESCRICAO).toString() + " - " + identificador;
              if (descricao.length() > 100) {
                  descricao = descricao.substring(0, 100) + "...";
              }
              %>
                <option value="<%=TextHelper.forHtmlAttribute(codigo)%>" <%=(String)((editarPropostaLeilaoModel.getSvcCodigo().equals(codigo) || servicos.size() == 1) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(descricao)%></option>
            <% } %>
          </select>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="adeTaxaJuros"><%=editarPropostaLeilaoModel.isTemCET() ? ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel) %></label>
          <hl:htmlinput name="adeTaxaJuros"
                        type="text"
                        classe="form-control"
                        di="adeTaxaJuros"
                        size="10"
                        mask="#F10"
                        others='<%=!podeEditarProposta ? "disabled" : ""%>'
                        onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); calcularValorPrestacao(this.value); }"
                        value="<%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getTaxaJuros())%>"
                        nf="btnEnviar"
          />
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="valorLiberado"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.valor.liberado"/> (<hl:message key="rotulo.moeda"/>)</label>
          <hl:htmlinput name="valorLiberado"
                        di="valorLiberado"
                        type="text"
                        classe="form-control"
                        size="12"
                        others="disabled"
                        mask="#F15"
                        value="<%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getValorLiberado())%>"
          />
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="prazo"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.prazo"/> (<hl:message key="rotulo.meses"/>)</label>
          <hl:htmlinput name="prazo"
                        di="prazo"
                        type="text"
                        classe="form-control"
                        size="8"
                        others="disabled"
                        value="<%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getPrazo())%>"
          />
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="valorParcela"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.valor.prestacao"/> (<hl:message key="rotulo.moeda"/>)</label>
          <hl:htmlinput name="valorParcela"
                        di="valorParcela"
                        type="text"
                        classe="form-control"
                        size="8"
                        others="disabled"
                        value="<%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getValorParcela())%>"
          />
        </div>
      </div>        
      <div class="row">
        <div class="form-group col-sm-6 col-md-6">
          <label for="txtContatoCsa"><hl:message key="rotulo.acompanhamento.leilao.solicitacao.txt.contato.csa"/></label>
           <hl:htmlinput name="txtContatoCsa"
                          di="txtContatoCsa"
                          type="textarea"
                          classe="form-control"
                          size="40"
                          mask="#*65000"
                          cols="50"
                          rows="5"
                          others='<%=!podeEditarProposta ? "disabled" : ""%>'
                          value="<%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getTxtContatoCsa())%>"
            />
        </div>
      </div>
    </div>
  </div>
  <% String telefoneContato = editarPropostaLeilaoModel.getTelefoneContato();
    String emailContato = editarPropostaLeilaoModel.getEmailContato();
    if (editarPropostaLeilaoModel.isPlsCsaAprovada() && (!TextHelper.isNull(telefoneContato) || !TextHelper.isNull(emailContato))) { %>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.servidor.dados" /></h2>
      </div>
      <div class="card-body">
        <div class="alert alert-warning" role="alert">
          <p class="mb-0"><hl:message key="mensagem.dados.contato.leilao.temporario" /></p>
        </div>
        <% if (!TextHelper.isNull(telefoneContato)) { %>
        <div class="row">
          <div class="form-group col-sm-12  col-md-4">
            <label for="dddTelefoneContato"><hl:message key="rotulo.confirmar.dados.leilao.telefone" /></label>
            <% if (!TextHelper.isNull(editarPropostaLeilaoModel.getDddTelefoneContato())) { %>
            <hl:htmlinput name="dddTelefoneContato"
                          di="dddTelefoneContato"
                          type="text"
                          classe="form-control"
                          size="2"
                          readonly="true"
                          value="<%=(TextHelper.isNull(editarPropostaLeilaoModel.getDddTelefoneContato()) ? TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getDddTelefoneContato()) : TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getDddTelefoneContato()) + " " + TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getDddTelefoneContato()))%>"
            />
            <% } %>
          </div>
        </div>
      <% } %>
      <% if (!TextHelper.isNull(emailContato)) { %>
      <div class="row">
        <div class="form-group col-sm-12  col-md-4">
          <label for="emailContato"><hl:message key="rotulo.confirmar.dados.leilao.email" /></label>
          <hl:htmlinput name="emailContato"
                      di="emailContato"
                      type="text"
                      classe="form-control"
                      size="50"
                      readonly="true"
                      value="<%=TextHelper.forHtmlAttribute(emailContato)%>"
        />
        </div>
      </div>
      <% } %>
      </div>
    </div>  
    <% } %>
    <% if (podeEditarProposta) { %>
    <div class="opcoes-avancadas">         
      <a class="opcoes-avancadas-head collapsed" href="#faq1" data-bs-toggle="collapse" aria-expanded="false" aria-controls="faq1"><hl:message key="rotulo.editar.proposta.leilao.solicitacao.oferta.automatica"/></a>
      <div class="collapse" id="faq1" style="">
        <div class="opcoes-avancadas-body">         
          <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <label for="decremento"><hl:message key="rotulo.editar.proposta.leilao.solicitacao.oferta.automatica.decremento"/> (<hl:message key="rotulo.porcentagem"/>)</label>
              <hl:htmlinput name="decremento"
                            di="decremento"
                            type="text"
                            classe="form-control"
                            size="8"
                            mask="#F11"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            others="disabled"
                            value="<%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getDecremento())%>"
              />
            </div>
          </div>     
          <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <label for="taxaMin"><hl:message key="rotulo.editar.proposta.leilao.solicitacao.oferta.automatica.taxa.min"/> (<hl:message key="rotulo.porcentagem"/>)</label>
              <hl:htmlinput name="taxaMin"
                            di="taxaMin"
                            type="text"
                            classe="form-control"
                            size="8"
                            mask="#F11"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.editar.proposta.leilao.solicitacao.oferta.automatica.taxa.min.placeholder", responsavel) %>"
                            value="<%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getTaxaMin())%>"
              />
            </div>
          </div>   
          <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <label for="email"><hl:message key="rotulo.editar.proposta.leilao.solicitacao.oferta.automatica.email"/></label>
              <hl:htmlinput name="email"
                            di="email"
                            type="text"
                            classe="form-control"
                            size="40"
                            mask="#*100"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.editar.proposta.leilao.solicitacao.oferta.automatica.email.placeholder", responsavel) %>"
                            value="<%=TextHelper.forHtmlAttribute(editarPropostaLeilaoModel.getEmail())%>"
              />
            </div>
          </div>
        </div>
      </div>  
    </div>
    <% } %>
    
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" id="btnCancelar"><hl:message key="rotulo.botao.cancelar"/></a>
      <% if (podeEditarProposta) { %>
      <hl:htmlinput name="exibeOfertaAut" type="hidden" value="true" />
      <a class="btn btn-primary" onClick="if(validForm()){ f0.submit();} return false;" id="btnEnviar" href="#no-back"><hl:message key="rotulo.botao.confirmar"/></a>
      <% } %>
    </div>  
  </form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
  f0 = document.forms[0];
  
  if (f0.adeTaxaJuros != null && f0.adeTaxaJuros.value != '' && f0.valorParcela != null && f0.valorParcela.value == '') {
  	calcularValorPrestacao(f0.adeTaxaJuros.value);
  }
</script>
<script type="text/JavaScript">
  function formLoad() {
    focusFirstField();
  }

  function validForm() {
    var Controles = new Array("adeTaxaJuros", "txtContatoCsa");
    
    <% if (editarPropostaLeilaoModel.isTemCET()) { %>
      var msgInformeTaxa = '<hl:message key="mensagem.informe.proposta.leilao.solicitacao.cet"/>'
    <% } else { %>
      var msgInformeTaxa = '<hl:message key="mensagem.informe.proposta.leilao.solicitacao.taxa.juros"/>'
<% } %>
  
    var Msgs = new Array(msgInformeTaxa, '<hl:message key="mensagem.informe.proposta.leilao.solicitacao.txt.contato.csa"/>');

    if (ValidaCampos(Controles, Msgs)) {
    if (f0.adeTaxaJuros.value != null && parse_num(f0.adeTaxaJuros.value) <= 0) {
      <% if (editarPropostaLeilaoModel.isTemCET()) { %>
          alert('<hl:message key="mensagem.erro.cet.proposta.leilao.solicitacao.incorreto"/>');
  <% } else { %>
        alert('<hl:message key="mensagem.erro.taxa.juros.proposta.leilao.solicitacao.incorreto"/>');          
  <% } %>
  f0.adeTaxaJuros.focus();
        return false;
    }
    
      var msg = trim(f0.txtContatoCsa.value);
      // Verifica quantidade de caracteres informados no texto para contato
      if (msg.length < 10) {
        alert('<hl:message key="mensagem.erro.proposta.leilao.solicitacao.txt.contato.csa.minimo"/>');
      f0.txtContatoCsa.focus();
        return false;
      }

      // Verifica se existe pelo menos uma letra no texto para contato
      var regex = /([a-zA-Z]+)/g;
      if (!msg.match(regex)) {
        alert('<hl:message key="mensagem.erro.proposta.leilao.solicitacao.txt.contato.csa.invalido"/>');
      f0.txtContatoCsa.focus();
        return false;
      }
    
      return true;
    }
    return false;
  }
  
  function calcularValorPrestacao(taxaJuros) {
      if (f0.adeTaxaJuros.value != null && parse_num(f0.adeTaxaJuros.value) <= 0) {
        document.getElementById('valorParcela').value = <%= NumberHelper.format(BigDecimal.ZERO.doubleValue(), NumberHelper.getLang())%>;
            return false;
      } else {
      var parametros = "acao=calcularValorPrestacao" + "&taxaJuros=" + taxaJuros + "&adeCodigo=" + f0.ADE_CODIGO.value + "&_skip_history_=1";
          $.post("../v3/editarPropostaLeilao", parametros, function(dataAjax) {
                try {
                  var dataTrim = $.trim(JSON.stringify(dataAjax));
                    var objeto = JSON.parse(dataTrim);
                    if (typeof objeto.valor != 'undefined' && objeto.valor != null && objeto.valor != '') {
                      document.getElementById('valorParcela').value = objeto.valor;
                    }
                } catch(err) {
                }
            }, "json");
          return false;
      }
 }
  
   function exibeOfertaAut() {
      var linhas = document.getElementsByTagName("tr");
      for (var i=0; i < linhas.length; i++) {
          if (linhas[i].getAttribute('ofertaAut') == 'true') {
              linhas[i].style.display = '';
          }
      }
  }

  function escondeOfertaAut() {
      var linhas = document.getElementsByTagName("tr");
      for (var i=0; i < linhas.length; i++) {
          if (linhas[i].getAttribute('ofertaAut') == 'true') {
              linhas[i].style.display = 'none';
          }
      }
  }

  function exibeEscondeOfertaAut() {
      if (f0.exibeOfertaAut == null || f0.exibeOfertaAut == undefined) {
          return;
      }
      if (f0.exibeOfertaAut.value == 'true') {
          f0.imgOfertaAut.src = '../img/menu/HM_More_black_top.gif';
          exibeOfertaAut();
          f0.exibeOfertaAut.value = 'false';
      } else if (f0.exibeOfertaAut.value == 'false') {
          f0.imgOfertaAut.src = '../img/menu/HM_More_black_bot.gif';
          escondeOfertaAut();
          f0.exibeOfertaAut.value = 'true';
      }
  }
  
  if ( $('#dlServidor').children().length == 0 ) {
	    $('#cardServidor').hide();
	}
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
