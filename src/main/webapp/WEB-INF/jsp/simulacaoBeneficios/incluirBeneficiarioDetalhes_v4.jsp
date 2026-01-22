<%--
* <p>Title: simularBeneficios_v4</p>
* <p>Description: Simular Beneficios v4</p>
* <p>Copyright: Copyright (c) 2018</p>
* <p>Company: Nostrum Consultoria e Projetos</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session"
  class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  List<TransferObject> beneficiariosGrupoFamiliar = (List<TransferObject>) request.getAttribute("beneficiariosGrupoFamiliar");
  String rseCodigo = (String) request.getAttribute("rseCodigo");
  List<String> beneficiariosComContratoSaude = (List<String>) request.getAttribute("beneficiariosComContratoSaude");
  List<String> beneficiariosComContratoOdonto = (List<String>) request.getAttribute("beneficiariosComContratoOdonto");
  // define para qual tela o botão voltar deve redirecionar 
  String paramLinkVoltar = (String) request.getAttribute("paramLinkVoltar");
  boolean permiteSimularBeneficioSemMargem = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, CodedValues.TPC_SIM, responsavel);
%>
<c:set var="title">
  <hl:message key="rotulo.simulacao.beneficio.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
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
              %>
              <div style="display: none;"  id="detalhesGeral_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>">
                <dl class="row data-list firefox-print-fix">
                  <div class="legend">
                    <span><%=TextHelper.forHtmlContentComTags(beneficiario.getAttribute(Columns.BFC_NOME)) %></span>
                  </div>
                </dl>
                <div class="row mb-2">
                  <div class="col-md-6" id="detalhesPlanoSaude_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>" name="detalhesGeralPlanoSaude" style="display: none;">
                    <dl class="row data-list firefox-print-fix">
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.do.plano.de.saude"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoSaudeMensalidade_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.do.subsidio.de.saude"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoSaudeSubsidio_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.total.do.plano.de.saude"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoSaudeTotalAPagar_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                    </dl>
                  </div>
                  <div class="col-md-6" id="detalhesPlanoOdontologico_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>" name="detalhesGeralPlanoOdontologico" style="display: none;">
                    <dl class="row data-list firefox-print-fix">
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.do.plano.odontologico"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoOdontologicoMensalidade_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.do.subsidio.odontologico"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoOdontologicoSubsidio_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><hl:message key="rotulo.simulacao.beneficio.detalhes.valor.total.do.plano.odontologico"/>:</dt>
                      <dd class="col-4" id="detalhesPlanoOdontologicoTotalAPagar_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                    </dl>
                  </div>
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
                      <div class="card-body divTable ">
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
                                  <div class="divTableCell font-weight-bold" id="totalMensalidadePlanoSaude">
                                      <%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%>
                                  </div>
                                  <div class="divTableCell font-weight-bold" id="totalSubsidioPlanoSaude">
                                      <%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%>
                                  </div>
                                  <div class="divTableCell font-weight-bold" id="totalADescontoPlanoSaude">
                                      <%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%>
                                  </div>
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
                                  <div class="divTableCell font-weight-bold" id="totalMensalidadePlanoOdontologico">
                                      <%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%>
                                  </div>
                                  <div class="divTableCell font-weight-bold" id="totalSubsidioPlanoOdontologico">
                                      <%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%>
                                  </div>
                                  <div class="divTableCell font-weight-bold" id="totalADescontoPlanoOdontologico">
                                      <%=TextHelper.forHtmlContentComTags(NumberHelper.format(0.00, NumberHelper.getLang()))%>
                                  </div>
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
                                <%if (permiteSimularBeneficioSemMargem) { %>
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.margem.disponivel.inicio.simulacao" />
                                  </div>
                              <%} %>    
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.total.simulacao" />
                                  </div>
                              <%if (permiteSimularBeneficioSemMargem) { %>
                                  <div class="divTableHead">
                                      <hl:message key="rotulo.simulacao.beneficio.margem.disponivel.apos.simulacao" />
                                  </div>                        
                              <%} %>
                              </div>
                              <div class="divTableRow">
                              <%if (permiteSimularBeneficioSemMargem) { %>
                                  <div class="divTableCell font-weight-bold" id="margemSemPlano"></div>
                              <%} %>
                                  <div class="divTableCell font-weight-bold" id="totalSimulacao"></div>
                              <%if (permiteSimularBeneficioSemMargem) { %>
                                  <div class="divTableCell font-weight-bold" id="margemDisponivel"></div>
                              <%} %>
                              </div>
                            </div>
                          </div>
                      </div>
                  </div>
              </div>
          </div>
  
  <form method="post" action="../v3/incluirBeneficiarioSimulacaoBeneficios?acao=salvar&&_skip_history_&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" id="form1">
    <hl:htmlinput type="hidden" name="beneficioPlanoSaudeSelecionado" value="" />
    <hl:htmlinput type="hidden" name="beneficiariosPlanoSaudeSelecionado" value="" />
    
    <hl:htmlinput type="hidden" name="beneficioPlanoOdontologicoSelecionado" value="" />
    <hl:htmlinput type="hidden" name="beneficiariosPlanoOdontologicoSelecionado" value="" />
    
    <hl:htmlinput type="hidden" name="<%=TextHelper.forJavaScript(Columns.getColumnName(Columns.RSE_CODIGO))%>" value="<%=TextHelper.forJavaScript(rseCodigo)%>" />
    
    <hl:htmlinput type="hidden" name="beneficiariosComContratoSaude" value="<%=beneficiariosComContratoSaude != null ? TextHelper.forHtmlAttribute(TextHelper.join(beneficiariosComContratoSaude, ",")) : ""%>" />
    <hl:htmlinput type="hidden" name="beneficiariosComContratoOdonto" value="<%=beneficiariosComContratoOdonto != null ? TextHelper.forHtmlAttribute(TextHelper.join(beneficiariosComContratoOdonto, ",")) : ""%>" />
  </form>

  <div class="btn-action col-sm">
    <a href="#no-back" class="btn btn-outline-danger" onClick="postData('../v3/incluirBeneficiarioSimulacaoBeneficios?acao=<%=TextHelper.forJavaScript(paramLinkVoltar)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&reiniciarLocalSession=false'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <a href="#no-back" class="btn btn-primary" onClick="salvar(); return false;"><hl:message key="rotulo.botao.continuar"/></a>          
  </div>
</c:set>
<c:set var="javascript">
  <script>
  
  f0 = document.forms[0];
  
  $(document).ready(function () {
	  let dadosSimulacao = sessionStorage.getItem("dadosSimulacaoInclusao");
	  showModal();

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

	      $('#totalMensalidadePlanoOdontologico').html(dadosSimuladosPlanoOdontologico != null ? dadosSimuladosPlanoOdontologico.totalMensalidade : "<%=NumberHelper.format(0.00, NumberHelper.getLang())%>");
		  $('#totalSubsidioPlanoOdontologico').html(dadosSimuladosPlanoOdontologico != null ? dadosSimuladosPlanoOdontologico.totalSubsidio : "<%=NumberHelper.format(0.00, NumberHelper.getLang())%>");
          $('#totalADescontoPlanoOdontologico').html(dadosSimuladosPlanoOdontologico != null ? dadosSimuladosPlanoOdontologico.totalADesconto : "<%=NumberHelper.format(0.00, NumberHelper.getLang())%>");
		    
          $('#totalMensalidadePlanoSaude').html(dadosSimuladosPlanoSaude != null ? dadosSimuladosPlanoSaude.totalMensalidade : "<%=NumberHelper.format(0.00, NumberHelper.getLang())%>");
          $('#totalSubsidioPlanoSaude').html(dadosSimuladosPlanoSaude != null ? dadosSimuladosPlanoSaude.totalSubsidio : "<%=NumberHelper.format(0.00, NumberHelper.getLang())%>");
          $('#totalADescontoPlanoSaude').html(dadosSimuladosPlanoSaude != null ? dadosSimuladosPlanoSaude.totalADesconto : "<%=NumberHelper.format(0.00, NumberHelper.getLang())%>");		    

	      if (dadosSimuladosPlanoOdontologico != null && dadosSimuladosPlanoOdontologico.beneficiariosCalculados.length != 0) {
	       
	        if (dadosSimulacao.rseCodigo != '<%=TextHelper.forJavaScript(rseCodigo)%>') {
	          sessionStorage.removeItem("dadosSimulacaoInclusao");
	          postData('../v3/carregarPrincipal');
	        }

	        if (dadosSimuladosPlanoOdontologico.beneficiariosCalculados.length > 0) {
	          $('input[name="beneficioPlanoOdontologicoSelecionado"]').val(dadosSimuladosPlanoOdontologico.benCodigo);
	          teveAlgumDadoSimulado = true;
	        }
	        
	        let s = "";
	        for (let i = 0; i < dadosSimuladosPlanoOdontologico.beneficiariosCalculados.length; i++) {
	          s = s + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo + ";";
	          $('#detalhesPlanoOdontologicoMensalidade_' + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].mensalidade);
	          $('#detalhesPlanoOdontologicoSubsidio_' + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].subsidio);
	          $('#detalhesPlanoOdontologicoTotalAPagar_' + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].totalAPagar);
	          $('#detalhesPlanoOdontologico_' + dadosSimuladosPlanoOdontologico.beneficiariosCalculados[i].bfcCodigo).removeAttr('style');
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
	          sessionStorage.removeItem("dadosSimulacaoInclusao");
	          postData('../v3/carregarPrincipal');
	        }

	        if (dadosSimuladosPlanoSaude.beneficiariosCalculados.length > 0) {
	          $('input[name="beneficioPlanoSaudeSelecionado"]').val(dadosSimuladosPlanoSaude.benCodigo);
	          teveAlgumDadoSimulado = true;
	        }
	        


	        let s = "";
	        for (let i = 0; i < dadosSimuladosPlanoSaude.beneficiariosCalculados.length; i++) {
	          s = s + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo + ";";
	          $('#detalhesPlanoSaudeMensalidade_' + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoSaude.beneficiariosCalculados[i].mensalidade);
	          $('#detalhesPlanoSaudeSubsidio_' + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoSaude.beneficiariosCalculados[i].subsidio);
	          $('#detalhesPlanoSaudeTotalAPagar_' + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo).html(dadosSimuladosPlanoSaude.beneficiariosCalculados[i].totalAPagar);
	          $('#detalhesPlanoSaude_' + dadosSimuladosPlanoSaude.beneficiariosCalculados[i].bfcCodigo).removeAttr('style');
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
	      postData('../v3/carregarPrincipal');
	    }

	    hideModal();
	  }
	});
    
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
	showModal();
	sessionStorage.removeItem("dadosSimulacaoInclusao");
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