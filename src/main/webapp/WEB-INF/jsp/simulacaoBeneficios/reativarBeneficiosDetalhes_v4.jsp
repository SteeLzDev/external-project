<%--
* <p>Title: reativarBeneficiosDetalhes</p>
* <p>Description: Reativar Beneficios Detalhes v4</p>
* <p>Copyright: Copyright (c) 2018</p>
* <p>Company: Nostrum Consultoria e Projetos</p>
* $Author: marcos.nolasco $
* $Revision: 29068 $
* $Date: 2020-03-13 14:05:59 -0300 (sex, 13 mar 2020) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session"
  class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  List<TransferObject> beneficiariosGrupoFamiliar = (List) request.getAttribute("beneficiariosGrupoFamiliar");
  String rseCodigo = (String) request.getAttribute("rseCodigo");
  String paraOndeVoltar = (String) request.getAttribute("paraOndeVoltar");
  StringBuffer campos = new StringBuffer();
%>
<c:set var="title">
  <hl:message key="rotulo.reativar.contrato.beneficio.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/reativarBeneficiarioSimulacaoBeneficios?acao=salvar&_skip_history_&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" id="form1">
      <div class="modal spinner-one" id="modalSimularBeneficio" tabindex="-1" role="dialog" data-bs-backdrop="static" data-bs-keyboard="false" aria-labelledby="modalSimularBeneficioLabel" aria-hidden="true">
          <div class="modal-dialog spinner-one modal-dialog-centered">
              <div class="modal-content spinner-two">
                  <div class="modal-body spinner-three" >
                      <div class="spinner-border text-primary" role="status">
                          <span class="visually-hidden"></span>
                      </div>
                  </div>
              </div>
          </div>
      </div>
    
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.simulacao.beneficio.detalhes.resultado"/></h2>
            </div>
            <div class="card-body">
              
              <%
                for (TransferObject beneficiario : beneficiariosGrupoFamiliar) {
                    int odonto = Integer.parseInt(beneficiario.getAttribute("ODONTO").toString());
                    int saude = Integer.parseInt(beneficiario.getAttribute("SAUDE").toString());
              %>
              <div style="display: none;"  id="detalhesGeral_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>">
                <dl class="row data-list firefox-print-fix">
                  <div class="legend">
                    <span><%=TextHelper.forHtmlContentComTags(beneficiario.getAttribute(Columns.BFC_NOME)) %></span>
                  </div>
                </dl>
                <div class="row mb-2">
                <%if (saude > 0) {%>
                  <div class="col-md-6" id="detalhesPlanoSaude_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>" name="detalhesGeralPlanoSaude">
                    <dl class="row data-list firefox-print-fix">
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.do.plano.de.saude"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoSaudeMensalidade_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.do.subsidio.de.saude"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoSaudeSubsidio_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.total.do.plano.de.saude"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoSaudeTotalAPagar_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><hl:message key="rotulo.reativar.beneficio.detalhes.numero.cliente"/>:</dt>
                        <div class="form-check" role="radiogroup">
                            <%for (int i = 0 ; i < saude ; i++) {%>
                              <dd class="col-4">
                                <label class="form-check-label pr-3" for="carteirinhaSaude_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"> 
                                  <input class="form-check-input" type="radio" id="cbeCodigoSaude<%=i%>_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>" name="cbeCodigoSaude_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>" value=""/>
                                  <span class="text-nowrap align-text-top" id="detalhesPlanoSaudeCarteirinha<%=i%>_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></span>
                                </label>
                              </dd>
                              <% campos.append("cbeCodigoSaude_").append(TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))).append(";"); %>
                            <%} %>
                        </div>
                    </dl>
                  </div>
                  <%} %>
                  <%if (odonto > 0) {%>
                  <div class="col-md-6" id="detalhesPlanoOdontologico_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>" name="detalhesGeralPlanoOdontologico">
                    <dl class="row data-list firefox-print-fix">
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.do.plano.odontologico"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoOdontologicoMensalidade_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.do.subsidio.odontologico"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoOdontologicoSubsidio_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.total.do.plano.odontologico"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoOdontologicoTotalAPagar_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><hl:message key="rotulo.reativar.beneficio.detalhes.numero.cliente"/>:</dt>
                        <div class="form-check" role="radiogroup">
                            <%for (int i = 0 ; i < odonto ; i++) {%>
                              <dd class="col-4">
                                <label class="form-check-label pr-3" for="carteirinhaOdonto_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"> 
                                  <input class="form-check-input" type="radio" id="cbeCodigoOdonto<%=i%>_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>" name="cbeCodigoOdonto_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>" value=""/>
                                  <span class="text-nowrap align-text-top" id="detalhesPlanoOdontologicoCarteirinha<%=i%>_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></span>
                                </label>
                              </dd>
                              <% campos.append("cbeCodigoOdonto_").append(TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))).append(";"); %>
                            <%} %>
                        </div>
                    </dl>
                  </div>
                  <%} %>
                </div>
               </div>
              <%
                }
              %>

            </div>
          </div>

          <div class="row">
              <div class="col-sm">
                  <div class="card" id="resumoPlanoSaude">
                      <div class="card-header">
                          <h2 class="card-header-title">
                              <hl:message key="rotulo.simulacao.beneficio.resumo.plano.saude" />
                          </h2>
                      </div>
                      <div class="card-body divTable">
                          <div class="divTableBody text-center">
                              <div class="divTableRow">
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.total.do.plano" />
                                  </div>
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.total.do.subsidio" />
                                  </div>
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.total.a.descontar" />
                                  </div>
                              </div>
                              <div class="divTableRow">
                                  <div class="divTableCell font-weight-bold" id="totalMensalidadePlanoSaude"><%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%></div>
                                  <div class="divTableCell font-weight-bold" id="totalSubsidioPlanoSaude"><%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%></div>
                                  <div class="divTableCell font-weight-bold" id="totalADescontoPlanoSaude"><%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%></div>
                              </div>
                          </div>
                      </div>
                  </div>
              </div>
              
              <div class="col-sm">
                  <div class="card" id="resumoPlanoOdontologico">
                      <div class="card-header">
                          <h2 class="card-header-title">
                              <hl:message key="rotulo.simulacao.beneficio.resumo.plano.odontologico" />
                          </h2>
                      </div>
                      <div class="card-body divTable">
                          <div class="divTableBody text-center">
                              <div class="divTableRow">
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.total.do.plano" />
                                  </div>
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.total.do.subsidio" />
                                  </div>
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.total.a.descontar" />
                                  </div>
                              </div>
                              <div class="divTableRow">
                                  <div class="divTableCell font-weight-bold" id="totalMensalidadePlanoOdontologico"><%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%></div>
                                  <div class="divTableCell font-weight-bold" id="totalSubsidioPlanoOdontologico"><%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%></div>
                                  <div class="divTableCell font-weight-bold" id="totalADescontoPlanoOdontologico"><%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%></div>
                              </div>
                          </div>
                      </div>
                  </div>
              </div>    
          </div>

          <div class="row">
              <div class="col-sm">
                  <div class="card">
                      <div class="card-header">
                          <h2 class="card-header-title">
                              <hl:message key="rotulo.simulacao.beneficio.resumo.geral" />
                          </h2>
                      </div>
                      <div class="card-body divTable">
                          <div class="divTableBody text-center">
                              <div class="divTableRow">
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.margem.disponivel.inicio.simulacao" />
                                  </div>
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.total.simulacao" />
                                  </div>
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.margem.disponivel.apos.simulacao" />
                                  </div>                        
                              </div>
                              <div class="divTableRow">
                                  <div class="divTableCell font-weight-bold" id="margemSemPlano"></div>
                                  <div class="divTableCell font-weight-bold" id="totalSimulacao"></div>
                                  <div class="divTableCell font-weight-bold" id="margemDisponivel"></div>
                              </div>
                          </div>
                      </div>
                  </div>
              </div>
          </div>
          
    <hl:htmlinput type="hidden" name="beneficioPlanoSaudeSelecionado" value="" />
    <hl:htmlinput type="hidden" name="beneficiariosPlanoSaudeSelecionado" value="" />
    
    <hl:htmlinput type="hidden" name="beneficioPlanoOdontologicoSelecionado" value="" />
    <hl:htmlinput type="hidden" name="beneficiariosPlanoOdontologicoSelecionado" value="" />
    
    <input name="CBE_CODIGOS" type="hidden" value="<%=TextHelper.forHtmlAttribute((campos))%>">
    
    <hl:htmlinput type="hidden" name="<%=TextHelper.forHtmlAttribute(Columns.getColumnName(Columns.RSE_CODIGO))%>" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" />
  </form>

  <div class="btn-action col-sm">      
    <a href="#no-back" class="btn btn-outline-danger" onClick="postData('../v3/reativarBeneficiarioSimulacaoBeneficios?acao=<%=TextHelper.forJavaScript(paraOndeVoltar)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <a href="#no-back" class="btn btn-primary" onClick="salvar(); return false;"><hl:message key="rotulo.botao.continuar"/></a>          
  </div>
</c:set>
<c:set var="javascript">
  <script>
  
  f0 = document.forms[0];
  var arrayIdInputRadioOdonto = [];
  var arrayBfcCodigosOdonto = [];
  var arrayBfcCodigosSaude = [];
  var arrayIdInputRadioSaude = [];  
  
  $( document ).ready(function() {
	  $.ajax({
			    type: 'POST',
			    url: '../v3/reativarBeneficiarioSimulacaoBeneficios/buscaNumCliente?<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_',
			    data: {
			        'simulacaoDados': sessionStorage.getItem("dadosSimulacao")
				    },
			    success: function (data) {
			    	var dataParse = JSON.parse(data);
			    	var dataOdonto;
			    	var dataSaude;
		        	dataSaude = dataParse.saude;
		        	dataOdonto = dataParse.odonto;
			      	preencherdados(dataSaude,dataOdonto);
			    },
			    error: function (request, status, error) {
			    }
		  })
  });
  
  function preencherdados(dadosSaude, dadosOdonto){
	  let dadosSimulacao = sessionStorage.getItem("dadosSimulacao");

	  if (dadosSimulacao == null) {
	    alert('<%= TextHelper.forJavaScriptBlock(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhuma.operadora.selecionada", responsavel))%>');
	    postData('../v3/carregarPrincipal');
	  } else {

	    try {
	      let teveAlgumDadoSimulado = false;
	      dadosSimulacao = JSON.parse(dadosSimulacao);

	      let dadosSimuladosPlanoOdontologico = null;
	      let dadosSimuladosPlanoSaude = null;

	      for (var i in dadosSimulacao.simulacao) {
	        if (dadosSimulacao.simulacao[i].nseCodigo == '4') {
	          dadosSimuladosPlanoSaude = dadosSimulacao.simulacao[i];
	        }

	        if (dadosSimulacao.simulacao[i].nseCodigo == '9') {
	          dadosSimuladosPlanoOdontologico = dadosSimulacao.simulacao[i];
	        }
	      }
	      
	      $('#margemSemPlano').html(dadosSimulacao.margemSemPlano);
	      $('#totalSimulacao').html(dadosSimulacao.totalSimulacao);
	      $('#margemDisponivel').html(dadosSimulacao.margemDisponivel);

	      if (dadosSimuladosPlanoOdontologico != null && dadosSimuladosPlanoOdontologico.beneficiariosCalculados.length != 0) {
	       
	        if (dadosSimulacao.rseCodigo != '<%=TextHelper.forJavaScript(rseCodigo)%>') {
	          sessionStorage.removeItem("dadosSimulacao");
	          postData('../v3/carregarPrincipal');
	        }

	        if (dadosSimuladosPlanoOdontologico.beneficiariosCalculados.length > 0) {
	          $('input[name="beneficioPlanoOdontologicoSelecionado"]').val(dadosSimuladosPlanoOdontologico.benCodigo);
	          teveAlgumDadoSimulado = true;
	        }
	        
		    $('#totalMensalidadePlanoOdontologico').html(dadosSimuladosPlanoOdontologico.totalMensalidade);
		    $('#totalSubsidioPlanoOdontologico').html(dadosSimuladosPlanoOdontologico.totalSubsidio);
		    $('#totalADescontoPlanoOdontologico').html(dadosSimuladosPlanoOdontologico.totalADesconto);

	        let s = "";
	        for (let i = 0; i < dadosSimuladosPlanoOdontologico.beneficiariosCalculados.length; i++) {
	          s = s + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo + ";";
	          arrayBfcCodigosOdonto.push(dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo);
	          $('#detalhesPlanoOdontologicoMensalidade_' + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].mensalidade);
	          $('#detalhesPlanoOdontologicoSubsidio_' + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].subsidio);
	          $('#detalhesPlanoOdontologicoTotalAPagar_' + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].totalAPagar);
   	          for (var z in dadosOdonto) {
    	    		if (z == dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo) {
        	    		for (let y = 0; y < dadosOdonto[z].length ; y++) {
        	    			var conteudo = dadosOdonto[z][y].split(';');
        	    			var cbeCodigo = conteudo[0];
        	    			var cbeNumero = conteudo[1];
        	    			var nseCodigo = conteudo[2];
        	    			if (nseCodigo == '9'){
	        	    			$('#detalhesPlanoOdontologicoCarteirinha'+y+'_' + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo).html(cbeNumero);
    	    	    			$('input[id=cbeCodigoOdonto'+y+'_'+ dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo + ']').val(cbeCodigo);
    	    	    			arrayIdInputRadioOdonto.push('cbeCodigoOdonto'+y+'_'+ dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo)
        	    			}
        	    		}
    	    		}
      		}	
	          $('#detalhesGeral_' + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo).removeAttr('style');
		    	
	        }

	        for (let i = 0; i < dadosSimuladosPlanoOdontologico.beneficiariosSemCalculos.length; i++) {
	          $('#detalhesPlanoOdontologico_' + dadosSimuladosPlanoOdontologico.beneficiariosSemCalculos[i].bfcCodigo).remove();
	        }

	        s = s.substring(0, s.length - 1);
	        $('input[name="beneficiariosPlanoOdontologicoSelecionado"]').val(s);
	      } else {
	        $("div[name$='detalhesGeralPlanoOdontologico']").remove();
	      }

	      if (dadosSimuladosPlanoSaude != null && dadosSimuladosPlanoSaude.beneficiariosCalculados.length != 0) {
	        if (dadosSimulacao.rseCodigo != '<%=TextHelper.forJavaScript(rseCodigo)%>') {
	          sessionStorage.removeItem("dadosSimulacao");
	          postData('../v3/carregarPrincipal');
	        }

	        if (dadosSimuladosPlanoSaude.beneficiariosCalculados.length > 0) {
	          $('input[name="beneficioPlanoSaudeSelecionado"]').val(dadosSimuladosPlanoSaude.benCodigo);
	          teveAlgumDadoSimulado = true;
	        }
	        
	        $('#totalMensalidadePlanoSaude').html(dadosSimuladosPlanoSaude.totalMensalidade);
		    $('#totalSubsidioPlanoSaude').html(dadosSimuladosPlanoSaude.totalSubsidio);
		    $('#totalADescontoPlanoSaude').html(dadosSimuladosPlanoSaude.totalADesconto);

	        let s = "";
	        for (let i = 0; i < dadosSimuladosPlanoSaude.beneficiariosCalculados.length; i++) {
	          s = s + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo + ";";
	          arrayBfcCodigosSaude.push(dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo);
	          $('#detalhesPlanoSaudeMensalidade_' + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoSaude.beneficiariosCalculados[i].mensalidade);
	          $('#detalhesPlanoSaudeSubsidio_' + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoSaude.beneficiariosCalculados[i].subsidio);
	          $('#detalhesPlanoSaudeTotalAPagar_' + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoSaude.beneficiariosCalculados[i].totalAPagar);
   	          for (var z in dadosSaude) {
  	    		if (z == dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo) {
      	    		for (let y = 0; y < dadosSaude[z].length ; y++) {
      	    			var conteudo = dadosSaude[z][y].split(';');
      	    			var cbeCodigo = conteudo[0];
      	    			var cbeNumero = conteudo[1];
      	    			var nseCodigo = conteudo[2];
      	    			if (nseCodigo == '4'){
	        	    		$('#detalhesPlanoSaudeCarteirinha'+y+'_' + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo).html(cbeNumero);
  	    	    			$('input[id=cbeCodigoSaude'+y+'_'+ dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo + ']').val(cbeCodigo);
  	    	    			arrayIdInputRadioSaude.push('cbeCodigoSaude'+y+'_'+ dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo)
      	    			}
      	    		}
  	    		}
    		}
	          $('#detalhesGeral_' + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo).removeAttr('style');
	        }

	        for (let i = 0; i < dadosSimuladosPlanoSaude.beneficiariosSemCalculos.length; i++) {
	          $('#detalhesPlanoSaude_' + dadosSimuladosPlanoSaude.beneficiariosSemCalculos[i].bfcCodigo).remove();
	        }

	        s = s.substring(0, s.length - 1);
	        $('input[name="beneficiariosPlanoSaudeSelecionado"]').val(s);
	      } else {
	        $("div[name$='detalhesGeralPlanoSaude']").remove();
	      }

	      if (!teveAlgumDadoSimulado) {
	        alert('<%= TextHelper.forJavaScriptBlock(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhuma.operadora.selecionada", responsavel))%>');
	        postData('../v3/carregarPrincipal');
	      }

	    } catch (err) {
	    	console.log(err);
	    	postData('../v3/carregarPrincipal');
	    }

	    hideModal();
	  }
  }
  
  function showModal() {
      var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('modalSimularBeneficio'),{
          keyboard: false,
          backdrop: 'static',
      });
      myModal.show();
  }
  
  function hideModal() {
      setTimeout(function(){
          var myModal = bootstrap.Modal.getInstance(document.querySelector('#modalSimularBeneficio'));
          myModal.hide();
      }, 200);
  }
  
  function salvar() {
	  
	  var qntCheckedOdonto = 0;
	  var qntCheckedSaude = 0;
	  var qntBfcSaude = arrayBfcCodigosSaude.length;
	  var qntBfcOdonto = arrayBfcCodigosOdonto.length;
	  
	  if (arrayBfcCodigosSaude.length > 0) {
		  for (let i =0; i < arrayIdInputRadioSaude.length; i++) {
			  if(document.getElementById(arrayIdInputRadioSaude[i]).checked) {
				  qntCheckedSaude += 1;
			  }
		  }
	  }
	  
	  if (arrayBfcCodigosOdonto.length > 0) {
		  for (let i =0; i < arrayIdInputRadioOdonto.length; i++) {
			  if(document.getElementById(arrayIdInputRadioOdonto[i]).checked) {
				  qntCheckedOdonto += 1;
			  }
		  }
	  }
	  
	  if (qntBfcSaude > 0 && qntBfcSaude != qntCheckedSaude) {
		  alert('<hl:message key="mensagem.erro.reativar.beneficio.detalhes.selecionar.saude"/>');
		  qntCheckedSaude = 0;
		  return false;
	  } 
	  
	  if (qntBfcOdonto > 0 && qntBfcOdonto != qntCheckedOdonto) {
		  alert('<hl:message key="mensagem.erro.reativar.beneficio.detalhes.selecionar.odonto"/>');
		  qntCheckedOdonto = 0;
		  return false;
	  } 

	  showModal();
      sessionStorage.removeItem("dadosSimulacao");
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