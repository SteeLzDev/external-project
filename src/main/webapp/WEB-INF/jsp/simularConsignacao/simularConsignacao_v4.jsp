<%--
* <p>Title: simulacao</p>
* <p>Description: Página de simulação de empréstimos</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.periodo.PeriodoHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.margem.MargemDisponivel" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    
    String adePeriodicidade = (String) request.getAttribute("adePeriodicidade");
    String titulo = (String) request.getAttribute("titulo");
    TransferObject servidor = (TransferObject) request.getAttribute("servidor");
    boolean podeMostrarMargem = (Boolean) request.getAttribute("podeMostrarMargem");
    ExibeMargem exibeMargem = (ExibeMargem) request.getAttribute("exibeMargem");
    MargemDisponivel margemDisponivel = (MargemDisponivel) request.getAttribute("margemDisponivel");
    BigDecimal rseMargemRest = (BigDecimal) request.getAttribute("rseMargemRest");
    String margemConsignavel = (String) request.getAttribute("margemConsignavel");
    boolean isIe = (Boolean) request.getAttribute("isIe");
    String adeVlrPadrao = (String) request.getAttribute("adeVlrPadrao");
    Set<Integer> prazosPossiveisMensal = (Set<Integer>) request.getAttribute("prazosPossiveisMensal");
    Set<Integer> prazosPossiveisPeriodicidadeFolha = (Set<Integer>) request.getAttribute("prazosPossiveisPeriodicidadeFolha");
    boolean alteraAdeVlr = (Boolean) request.getAttribute("alteraAdeVlr");
    boolean permiteEscolherPeriodicidade = (Boolean) request.getAttribute("permiteEscolherPeriodicidade");
    boolean permiteSimularSemMargem = (Boolean) request.getAttribute("permiteSimularSemMargem");
    String prz_vlr = (String) request.getAttribute("PRZ_VLR");
    String ade_vlr = (String) request.getAttribute("ADE_VLR");
    String vlr_liberado = (String) request.getAttribute("VLR_LIBERADO");
    String svcCodigo = (String) request.getAttribute("SVC_CODIGO");
    String rseCodigo = (responsavel.isSer()) ? responsavel.getRseCodigo() : (String) request.getAttribute("RSE_CODIGO");
    String rotuloPeriodicidadePrazo = "&nbsp;(" + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel) + ")";
    String rotuloUnidadePeriodicidadePrazo = (String) ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel);
    String mensagemDataMargem = (String) request.getAttribute("mensagemDataMargem");
    boolean leilaoReverso = (request.getAttribute("leilaoReverso") != null && (Boolean) request.getAttribute("leilaoReverso"));
    boolean origem  = (Boolean) request.getAttribute("origem");
    boolean tpcSolicitarPortabilidadeRanking  = (Boolean) request.getAttribute("tpcSolicitarPortabilidadeRanking");
    String adeCodigo  = (String) request.getAttribute("ADE_CODIGO");
    String vlrSolicitadoCalculado = (String) request.getAttribute("vlrSolicitadoCalculado") != null ? (String) request.getAttribute("vlrSolicitadoCalculado") : null;
    String txtExplicativo = (String) request.getAttribute("txtExplicativo");
    boolean exigeCaptcha = false;
    boolean exibeCaptchaAvancado = false;
    boolean exibeCaptchaDeficiente = false;
    if(responsavel.isSer()) {
        exigeCaptcha = request.getAttribute("exigeCaptcha") != null && (boolean) request.getAttribute("exigeCaptcha");
        exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
        exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
    }
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.simular.consignacao.titulo"/>
</c:set>
<c:set var="bodyContent">
<form NAME="form1" METHOD="post" ACTION="../v3/<%=leilaoReverso ? "solicitarLeilao" : "simularConsignacao"%>?<%=SynchronizerToken.generateToken4URL(request)%>">
    <div class="row">
        <% if (podeMostrarMargem) { %>
        <div class="col-sm-4">
            <div class="card">
                <div class="card-header hasIcon">
                    <span class="card-header-icon"><svg width="30"><use xlink:href="#i-menu-margem"></use></svg></span>
                    <h2 class="card-header-title"><hl:message key="rotulo.simulacao.margem.consignavel"/></h2>
                    <% if(exigeCaptcha){ %>
                    <span class="card-header-icon-ocultar-margem-ser">
                    <a href="#" onclick="exibirmargem()" id="olhoMargemOculto">
                        <svg  width="30" height="30" class="icon-oculta-margem-simu">
                            <use xlink:href="#i-eye-slash-regular"></use>
                        </svg>
                    </a>
                    </span>
                    <% } %>
                </div>
                <div class="card-body">
                    <% if (!exigeCaptcha){ %>
                    <% if (margemDisponivel.getMargemRestanteDependente() == null) { %>
                    <span class="margem-disponivel"><%=TextHelper.forHtmlAttribute(request.getAttribute("tipoVlrMargemDisponivel"))%> <strong><%=(String)(exibeMargem.isSemRestricao() || rseMargemRest.doubleValue() > 0 ? NumberHelper.reformat(margemConsignavel, "en", NumberHelper.getLang()) : "0,00")%></strong></span>
                    <% } else { %>
                    <label for="margem-disponivel"><%=TextHelper.forHtmlContent(margemDisponivel.getMarDescricao())%>:</label>
                    <span id="margem-disponivel" class="margem-disponivel"><%=TextHelper.forHtmlAttribute(request.getAttribute("tipoVlrMargemDisponivel"))%> <strong><%=(String)(exibeMargem.isSemRestricao() || rseMargemRest.doubleValue() > 0 ? NumberHelper.reformat(margemConsignavel, "en", NumberHelper.getLang()) : "0,00")%></strong></span>
                    <label for="margem-disponivel-dependente"><%=TextHelper.forHtmlContent(margemDisponivel.getMarDescricaoDependente())%>:</label>
                    <span id="margem-disponivel-dependente" class="margem-disponivel"><%=TextHelper.forHtmlContent(request.getAttribute("tipoVlrMargemDisponivel"))%>&nbsp;<%=TextHelper.forHtmlContent(margemDisponivel.getExibeMargem().isSemRestricao() || margemDisponivel.getMargemRestanteDependente().doubleValue() > 0 ? NumberHelper.format(margemDisponivel.getMargemRestanteDependente().doubleValue(), NumberHelper.getLang()) : "0,00")%></span>
                    <% } %>
                    <% if (!TextHelper.isNull(mensagemDataMargem)) { %>
                    <span class="ultima-edicao"><%=mensagemDataMargem%></span>
                    <% } %>
                    <hl:htmlinput name="rseMargemRest" di="rseMargemRest" value="<%=TextHelper.forHtmlAttribute(exibeMargem.isSemRestricao() || rseMargemRest.doubleValue() > 0  ? margemConsignavel : "0.00")%>" type="hidden" />
                    <% } else { %>
                    <div class="card-body">
                        <dl class="row data-list firefox-print-fix">
                            <dt class="col-6"><hl:message key="rotulo.reservar.margem.disponivel"/>:</dt>
                            <dd class="col-6"><hl:message key="rotulo.margem.moeda"/><hl:message key="rotulo.margem.disponivel.codigo"/></dd>
                        </dl>
                    </div>
                    <hl:modalCaptchaSer type="<%=TextHelper.forHtmlAttribute(!leilaoReverso ? "simular" : "leilaoR")%>"/>
                    <% } %>
                </div>
            </div>
        </div>
     <% } %>
        <div class="col-sm">
            <div class="card">
                <div class="card-header hasIcon">
                    <span class="card-header-icon"><svg width="25"><use xlink:href="#i-simular"></use></svg></span>
                    <h2 class="card-header-title"><hl:message key="rotulo.simulacao.simule.sua.consignacao"/></h2>
                </div>
                <div class="card-body">
                    <div class="alert alert-warning" role="alert">
                        <p class="mb-0"><hl:message key="rotulo.simulacao.defina.tipo.simulacao"/></p>
                    </div>
                      <% if (permiteEscolherPeriodicidade && !adePeriodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_MENSAL)) { %>
                          <% rotuloPeriodicidadePrazo = ""; %>
                    
                        <div class="row">
                          <div class="form-group col-sm-6">
                            <label for="periodicidade"><hl:message key="rotulo.consignacao.periodicidade"/></label>
                              <select class="form-control select" id="periodicidade" name="adePeriodicidade">
                                <option value="<%=(String)CodedValues.PERIODICIDADE_FOLHA_QUINZENAL%>" selected=" "><hl:message key="rotulo.consignacao.periodicidade.quinzenal"/></option>
                                <option value="<%=(String)CodedValues.PERIODICIDADE_FOLHA_MENSAL%>"><hl:message key="rotulo.consignacao.periodicidade.mensal"/></option>
                              </select>
                          </div>
                        </div>
                        <% } else { %>
                            <hl:htmlinput type="hidden" name="adePeriodicidade" di="adePeriodicidade" value="<%=TextHelper.forHtmlAttribute(PeriodoHelper.getPeriodicidadeFolha(responsavel))%>" />
                        <% } %>
                        <div class="row">
                            <div class="form-group col-sm-4">
                                <label for="VLR_LIBERADO"><hl:message key="rotulo.simulacao.valor.solicitado"/></label>
                                <div class="input-group">
                                    <div class="input-group-text" id="inputGroup-sizing-sm"><%=TextHelper.forHtmlAttribute(request.getAttribute("tipoVlrMargemDisponivel"))%></div>
                                    <hl:htmlinput name="VLR_LIBERADO"
                                                  type="text"
                                                  classe="form-control"
                                                  di="VLR_LIBERADO"
                                                  size="15"
                                                  mask="#F30"
                                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                                  others="<%=TextHelper.forHtmlAttribute( alteraAdeVlr ? "" : "disabled" )%>"
                                     />
                                </div>
                            </div>
                            <% if (podeMostrarMargem && !exigeCaptcha) { %>
                            <div class="col slider-limite">
                                <div class="form-range"  id="sliderVlrSolicitado"></div>
                                <label class="slider-valor-limite valor-solicitado-slider"></label>
                            </div>
                            <% } %>
                        </div>
                        <div class="row">
                            <div class="form-group col-sm-4">
                                <label for="ADE_VLR"><hl:message key="rotulo.simulacao.valor.prestacao"/>
                                    <%if (!txtExplicativo.isEmpty()) { %>
                                    <%=TextHelper.forHtmlContent(txtExplicativo)%>
                                    <% } %>
                                 </label>
                                <div class="input-group">
                                    <div class="input-group-text" id="inputGroup-sizing-sm"><%=TextHelper.forHtmlAttribute(request.getAttribute("tipoVlrMargemDisponivel"))%></div>
                                    <hl:htmlinput name="ADE_VLR"
                                                  type="text"
                                                  classe="form-control"
                                                  di="ADE_VLR"
                                                  size="15"
                                                  mask="#F30"
                                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                                  others="<%=TextHelper.forHtmlAttribute( alteraAdeVlr ? "" : "disabled" )%>"
                                     />
                                </div>
                            </div>
                            <% if (podeMostrarMargem && !exigeCaptcha) { %>
                            <div class="col slider-limite">
                                <div class="form-range" id="sliderVlrPrestacao"></div>
                                <label class="slider-valor-limite valor-max-prestacao"></label>
                            </div>
                            <% } %>
                        </div>
                        <div class="row">
                            <div class="form-group col-sm-4">
                                <label for="PRZ_VLR"><hl:message key="rotulo.simulacao.numero.prestacoes"/></label>
                                <div class="input-group">
                                    <input type="text" class="form-control" id="PRZ_VLR" name="PRZ_VLR" 
                                           onFocus="SetarEventoMascaraV4(this,'#D4',true);"
                                           placeholder="Meses"
                                           onBlur="fout(this);ValidaMascaraV4(this);" nf="btnConfirmar">
                                    <div class="input-group-text simular-numero-prestacao-addon" id="inputGroup-sizing-sm"><%=(String)(rotuloPeriodicidadePrazo)%></div>
                                </div>
                            </div>
                            <div class="col slider-limite">
                                <div class="form-range" id="sliderNumeroPrestacao"></div>
                                <label class="slider-valor-limite valor-max-parcelas"></label>
                            </div>
                        </div>
                </div>
            </div>
            <div class="btn-action">
                <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');"><hl:message key="rotulo.botao.cancelar"/></a>
                <a class="btn btn-primary" id="btnConfirmar" href="#" onClick="if(vf_simulacao()){f0.submit();} return false;"><svg width="17"><use xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.simulacao.acao.simular"/></a>
            </div>
         <input type="hidden" id="RSE_CODIGO" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>">
         <input type="hidden" id="SVC_CODIGO" name="SVC_CODIGO" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>">
         <input type="hidden" name="origem" value="<%=TextHelper.forHtmlAttribute(origem)%>">
         <input type="hidden" id="titulo" name="titulo" value="<%=TextHelper.forHtmlAttribute(titulo)%>">
         <input type="hidden" name="ORG_CODIGO" value="<%=TextHelper.forHtmlAttribute(request.getAttribute("orgCodigo"))%>">
         <input type="hidden" name="tpcSolicitarPortabilidadeRanking" value="<%=tpcSolicitarPortabilidadeRanking%>">
         <input type="hidden" name="ADE_CODIGO" value="<%=adeCodigo%>">
         <input type="hidden" name="acao" value="simular">
        </div>
</form>
</c:set>
<c:set var="javascript">
<script src="<c:url value='/noUiSlider-dist/nouislider.js'/>"></script>
<script src="<c:url value='/wNumb%201.2.0/wNumb.js'/>"></script>
<link href="<c:url value='/noUiSlider-dist/nouislider.css'/>" rel="stylesheet" type="text/css">
    <% if (exibeCaptchaAvancado) { %>
    <script src='https://www.google.com/recaptcha/api.js'></script>
    <% } %>
<script type="text/javascript">
  <% if (permiteEscolherPeriodicidade && !adePeriodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_MENSAL)) { %>
  $('#periodicidade').on('change', function (e) {
      $('.simular-numero-prestacao-addon').text(e.target.options[e.target.selectedIndex].innerText);
      carregarComponentes(e.target.options[e.target.selectedIndex].value);
  });    
  <% } %>

  function carregarComponentes(periodicidadeEscolhida) {

      <%=(prazosPossiveisMensal != null && !prazosPossiveisMensal.isEmpty() ? "var arPrazosMensal = [" + TextHelper.join(prazosPossiveisMensal, ", ") + "];" : "")%>
      <%=(prazosPossiveisPeriodicidadeFolha != null && !prazosPossiveisPeriodicidadeFolha.isEmpty() ? "var arPrazosPeriodicidadeFolha = [" + TextHelper.join(prazosPossiveisPeriodicidadeFolha, ", ") + "];" : "")%>

      /* numero prestacoes */
      var prazosPossiveis = periodicidadeEscolhida != 'M' ? arPrazosPeriodicidadeFolha : arPrazosMensal;

      var prazoMaximo = prazosPossiveis.sort((a,b) => a-b)[prazosPossiveis.length - 1];
      var margemDisponivel = Math.max(parseFloat('<%=(String)(exibeMargem.isSemRestricao() || rseMargemRest.doubleValue() > 0 ? NumberHelper.reformat(margemConsignavel, "en", NumberHelper.getLang()) : "0,00")%>'.replace(',','.')), 0);

      <% if (TextHelper.isNull(vlrSolicitadoCalculado)) {%>
      var valorMaximo = prazoMaximo * margemDisponivel / (periodicidadeEscolhida == 'Q' ? 2 : 1);
      <% } else { %>
      var valorMaximo = <%=vlrSolicitadoCalculado%>;
      <% } %>

      <% if (podeMostrarMargem && !exigeCaptcha){ %>

      //Valor Solicitado
      var sliderVlrSolicitado = document.getElementById('sliderVlrSolicitado');
      noUiSlider.create(sliderVlrSolicitado, {
          start: [0],
          tooltips:[
              wNumb({prefix: '<%= request.getAttribute("tipoVlrMargemDisponivel") %> ', decimals: 2})
          ],
          animate: true,
          animationDuration: 1600,
          step: 0.00,
          connect: [true, false],
          range: {
              'min': 0,
              'max': valorMaximo
          }
      });

      sliderVlrSolicitado.noUiSlider.on('update', function (value){
    	  <%if (LocaleHelper.getLocale().equals(LocaleHelper.BRASIL)) {%>
         	   $( "#VLR_LIBERADO" ).val(value.toString().replace('.', ','));
    	  <%} else {%>
	           $( "#VLR_LIBERADO" ).val(value.toString());
    	  <%}%>
      })

      $('.valor-solicitado-slider').text('<%=TextHelper.forHtmlAttribute(request.getAttribute("tipoVlrMargemDisponivel"))%> ' + FormataContabil(parse_num(valorMaximo), 2));

      //Valor Prestação
      var sliderVlrPrestacao = document.getElementById('sliderVlrPrestacao');
      noUiSlider.create(sliderVlrPrestacao, {
          start: [0],
          tooltips:[
              wNumb({prefix: '<%= request.getAttribute("tipoVlrMargemDisponivel") %> ', decimals: 2})
          ],
          animate: true,
          animationDuration: 1600,
          step: 0.00,
          connect: [true, false],
          range: {
              'min': 0,
              'max': margemDisponivel
          }
      });

      sliderVlrPrestacao.noUiSlider.on('update', function (value){
    	  <%if (LocaleHelper.getLocale().equals(LocaleHelper.BRASIL)) {%>
    	   	  $( "#ADE_VLR" ).val(value.toString().replace('.', ','));
    	  <%} else {%>
              $( "#ADE_VLR" ).val(value.toString());
    	  <%}%>
      })

      $('.valor-max-prestacao').text('<%=TextHelper.forHtmlAttribute(request.getAttribute("tipoVlrMargemDisponivel"))%> ' + '<%=(String)(exibeMargem.isSemRestricao() || rseMargemRest.doubleValue() > 0 ? NumberHelper.reformat(margemConsignavel, "en", NumberHelper.getLang()) : "0,00")%>');

      if (margemDisponivel <= 0) {
          sliderVlrSolicitado.setAttribute('disabled', true);
          sliderVlrPrestacao.setAttribute('disabled', true);
      } else {
          document.getElementById('ADE_VLR').addEventListener('change', function (){
              sliderVlrPrestacao.noUiSlider.set(this.value.toString().replace(',', '.'));
          });

          document.getElementById('VLR_LIBERADO').addEventListener('change', function (){
              sliderVlrSolicitado.noUiSlider.set(this.value.toString().replace(',', '.'));
          });
      }

      //Numero de prestações
      var sliderNumeroPrestacao = document.getElementById('sliderNumeroPrestacao');
      noUiSlider.create(sliderNumeroPrestacao, {
          start: [1],
          tooltips: [true],
          animate: true,
          animationDuration: 1600,
          step: 1,
          connect: [true, false],
          range: {
              'min': 1,
              'max': prazoMaximo,
          },
          format: wNumb({
              decimals: 1,
              thousand: '.'
          })
      });

      sliderNumeroPrestacao.noUiSlider.on('update', function (value){
          $( "#PRZ_VLR" ).val(value.toString());
      })

      document.getElementById('PRZ_VLR').addEventListener('change', function (){
          sliderNumeroPrestacao.noUiSlider.set(this.value.toString());
      });

      $('.valor-max-parcelas').text(prazoMaximo + ' <%=rotuloUnidadePeriodicidadePrazo%>');

      <% if (permiteEscolherPeriodicidade && !adePeriodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_MENSAL)) { %>
      $('.simular-numero-prestacao-addon').text($('#periodicidade')[0].options[$('#periodicidade')[0].selectedIndex].innerText);
      <% } %>

      $('#ADE_VLR').val(<%=TextHelper.forHtmlAttribute(!TextHelper.isNull(ade_vlr) && !ade_vlr.isEmpty() ? NumberHelper.reformat(ade_vlr, "en", NumberHelper.getLang()) : (!TextHelper.isNull(adeVlrPadrao) ? adeVlrPadrao : "") )%>)
      sliderVlrPrestacao.noUiSlider.set(<%=TextHelper.forHtmlAttribute(!TextHelper.isNull(ade_vlr) && !ade_vlr.isEmpty() ? NumberHelper.reformat(ade_vlr, "en", NumberHelper.getLang()) : (!TextHelper.isNull(adeVlrPadrao) ? adeVlrPadrao : "") )%>);

      $('#VLR_LIBERADO').val(<%=TextHelper.forHtmlAttribute(TextHelper.isNull(vlr_liberado) ? "" : NumberHelper.reformat(vlr_liberado, "en", NumberHelper.getLang()))%>)
      sliderVlrSolicitado.noUiSlider.set(<%=TextHelper.forHtmlAttribute(TextHelper.isNull(vlr_liberado) ? "" : NumberHelper.reformat(vlr_liberado, "en", NumberHelper.getLang()))%>)

      <% } else { %>
        $('#ADE_VLR').val('0,00');
        $('#VLR_LIBERADO').val('0,00');
      <% } %>

  }

  RSE_PRAZO = '<%=TextHelper.forHtmlContent(servidor.getAttribute(Columns.RSE_PRAZO)== null ? "" : servidor.getAttribute(Columns.RSE_PRAZO).toString())%>';

  function vf_simulacao() {
    f0 = document.forms[0];
    var vlrZerado = FormataContabil(parse_num(0), 2);

    if ((f0.ADE_VLR.value == '' || f0.ADE_VLR.value == vlrZerado) && (f0.VLR_LIBERADO.value == '' || f0.VLR_LIBERADO.value == vlrZerado)) {
      alert ('<hl:message key="mensagem.informe.simulacao.valor.prestacao.valor.solicitado"/>');
      f0.ADE_VLR.focus();
      return false;
    } else if ((f0.ADE_VLR.value != '' && f0.ADE_VLR.value != vlrZerado) && (f0.VLR_LIBERADO.value != '' && f0.VLR_LIBERADO.value != vlrZerado)) {
      alert ('<hl:message key="mensagem.erro.simulacao.somente.um.valor.prestacao.valor.solicitado"/>');
      f0.ADE_VLR.focus();
      return false;
    } else if (f0.PRZ_VLR.value == '' || parseInt(f0.PRZ_VLR.value) <= 0) {
      alert ('<hl:message key="mensagem.informe.simulacao.numero.prestacoes"/>');
      f0.PRZ_VLR.focus();
      return false;
    } else if (RSE_PRAZO != '' && (parseInt(f0.PRZ_VLR.value) > parseInt(RSE_PRAZO))) {
      alert ('<hl:message key="mensagem.erro.simulacao.quantidade.prestacoes.maior"/>'.replace("{0}", RSE_PRAZO));
      return false;
    } else {
      if (f0.ADE_VLR.value != '' && f0.ADE_VLR.value != vlrZerado) {
        var adeVlr = parseFloat(parse_num(f0.ADE_VLR.value));
        if (adeVlr <= 0) {
          alert('<hl:message key="mensagem.erro.simulacao.valor.prestacao"/>');
          if (f0.ADE_VLR != null && !f0.ADE_VLR.disabled) {
            f0.ADE_VLR.focus();
          }
          return false;
        }
        <% if (!permiteSimularSemMargem) { %>
        if (f0.rseMargemRest != null) {
          if(f0.rseMargemRest.value - adeVlr < 0) {
            alert('<hl:message key="mensagem.erro.simulacao.valor.prestacao.maior.margem"/>');
            if (f0.ADE_VLR != null && !f0.ADE_VLR.disabled) {
              f0.ADE_VLR.focus();
            }
            return false;
          }
        }
        <% } %>
      } else if (f0.VLR_LIBERADO.value != '' && f0.VLR_LIBERADO.value != vlrZerado) {
        var vlrLiberado = parseFloat(parse_num(f0.VLR_LIBERADO.value));
        if (vlrLiberado <= 0) {
          alert('<hl:message key="mensagem.erro.simulacao.valor.liberado"/>');
          if (f0.VLR_LIBERADO != null)
            f0.VLR_LIBERADO.focus();
          return false;
        }
      }

      f0.VLR_LIBERADO.value = f0.VLR_LIBERADO.value == vlrZerado ? '' : f0.VLR_LIBERADO.value;
      f0.ADE_VLR.value = f0.ADE_VLR.value == vlrZerado ? '' : f0.ADE_VLR.value;

      enableAll();
      return true;
    }
  }

  function exibirmargem(){
      <% if(leilaoReverso) { %>
      <% if (exibeCaptchaDeficiente) { %>
      montaCaptchaSomSer('leilaoR');
      <% } %>
      $('#modalCaptcha_leilaoR').modal('show');
      <% } else { %>
      <% if(exibeCaptchaDeficiente){ %>
      montaCaptchaSomSer('simular');
      <% } %>
      $('#modalCaptcha_simular').modal('show');
      <% } %>
  }

  function formLoad() {
      focusFirstField();
      carregarComponentes('<%=adePeriodicidade%>');
      <% if (!alteraAdeVlr) { %>
      disableVlrSlide();
      <% } %>
  }

  function disableVlrSlide(){
      document.getElementById('sliderVlrPrestacao').setAttribute("disabled", true);
      document.getElementById('sliderVlrSolicitado').setAttribute("disabled", true);
  }

  window.onload = formLoad;
</script>
</c:set>

<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>