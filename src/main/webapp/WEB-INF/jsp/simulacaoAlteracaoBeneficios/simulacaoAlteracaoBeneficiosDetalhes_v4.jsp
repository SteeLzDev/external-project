<%--
* <p>Title: simularAlteracaoBeneficiosDetalhes_v4</p>
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
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.persistence.entity.Consignataria"%>
<%@ page import="com.zetra.econsig.persistence.entity.NaturezaServico"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session"
  class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  String chaveSessionStore = (String) request.getAttribute("chaveSessionStore");
  NaturezaServico naturezaServico = (NaturezaServico) request.getAttribute("naturezaServico");
  List<TransferObject> beneficiariosGrupoFamiliar = (List) request.getAttribute("beneficiariosGrupoFamiliar");
  String rseCodigo = (String) request.getAttribute("rseCodigo");
  boolean permiteSimularBeneficioSemMargem = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, CodedValues.TPC_SIM, responsavel);
%>
<c:set var="title">
  <hl:message key="rotulo.simulacao.alteracao.beneficio.titulo" />
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
  
          <div class="alert alert-info" role="alert">
            <hl:message key="mensagem.info.simulacao.alteracao.confirmacao" />
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
                  <div class="col-md-6" id="detalhesPlano_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>" name="detalhesGeralPlano">
                    <dl class="row data-list firefox-print-fix">
                      <dt class="col-7"><%=TextHelper.forHtmlContent(ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.detalhes.valor.do.plano", responsavel, TextHelper.capitailizeFirstLetter(naturezaServico.getNseDescricao())))%>:</dt>
                      <dd class="col-4" id="detalhesPlanoMensalidade_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><%=TextHelper.forHtmlContent(ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.detalhes.valor.do.subsidio", responsavel, TextHelper.capitailizeFirstLetter(naturezaServico.getNseDescricao())))%>:</dt>
                      <dd class="col-4" id="detalhesPlanoSubsidio_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
                      <dt class="col-7"><%=TextHelper.forHtmlContent(ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.detalhes.valor.total.do.plano", responsavel, TextHelper.capitailizeFirstLetter(naturezaServico.getNseDescricao())))%>:</dt>
                      <dd class="col-4" id="detalhesPlanoTotalAPagar_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></dd>
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
                  <div class="card">
                      <div class="card-header">
                          <h2 class="card-header-title">
                              <%=TextHelper.forHtmlContent(ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.resumo", responsavel, TextHelper.capitailizeFirstLetter(naturezaServico.getNseDescricao())))%>
                          </h2>
                      </div>
                      <div class="card-body divTable p-0">
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
                                  <div class="divTableCell font-weight-bold" id="totalMensalidadePlanoSaude"></div>
                                  <div class="divTableCell font-weight-bold" id="totalSubsidioPlanoSaude"></div>
                                  <div class="divTableCell font-weight-bold" id="totalADesconto"></div>
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
                      <div class="card-body divTable p-0">
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
  
  <form method="post" action="../v3/simulacaoAlteracaoBeneficios?acao=salvar&&_skip_history_&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" id="form1">
    <hl:htmlinput type="hidden" name="beneficioPlanoSelecionado" value="" />
    <hl:htmlinput type="hidden" name="beneficiariosPlanoSelecionado" value="" />
    
    <hl:htmlinput type="hidden" name="<%=TextHelper.forHtmlAttribute(Columns.getColumnName(Columns.RSE_CODIGO))%>" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" />
  </form>

  <div class="btn-action col-sm">      
    <a class="btn btn-outline-danger" href="#no-back" onClick="fluxoVoltar()"><hl:message key="rotulo.botao.voltar" /></a>
    <a href="#no-back" class="btn btn-primary" onClick="salvar(); return false;"><hl:message key="rotulo.botao.continuar"/></a>          
  </div>
</c:set>
<c:set var="javascript">
  <script>
  
  function fluxoVoltar() { 
	    postData('../v3/simulacaoAlteracaoBeneficios?acao=simular&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&rseCodigo=<%=TextHelper.forJavaScript(rseCodigo)%>&nseCodigo=<%=TextHelper.forJavaScript(naturezaServico.getNseCodigo())%>');
  }
  
  f0 = document.forms[0];
  
  $( document ).ready(function() {
	  let dadosSimulados = sessionStorage.getItem('<%=TextHelper.forJavaScript(chaveSessionStore)%>');  
      
      showModal();
      
      if (dadosSimulados == null) { 
      	alert('<%= TextHelper.forJavaScriptBlock(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhuma.operadora.selecionada", responsavel))%>');
      	postData('../v3/carregarPrincipal');
      } else { 
      
        try {
        
            	dadosSimulados = JSON.parse(dadosSimulados);
            	
            	if (dadosSimulados.rseCodigo != '<%=TextHelper.forJavaScript(rseCodigo)%>'){
            		sessionStorage.removeItem('<%=TextHelper.forJavaScript(chaveSessionStore)%>');
        		  	postData('../v3/carregarPrincipal');
            	}
            	
            	if (dadosSimulados.beneficiariosCalculados.length > 0) {
            		$('input[name="beneficioPlanoSelecionado"]').val(dadosSimulados.benCodigo);
            	} else { 
            		sessionStorage.removeItem('<%=TextHelper.forJavaScript(chaveSessionStore)%>');
            		alert('<%= TextHelper.forJavaScriptBlock(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.titular.nao.selecionado", responsavel))%>');
                  	postData('../v3/carregarPrincipal');
            	}
            	
            	$('#margemSemPlano').html(dadosSimulados.margemSemPlano);
            	$('#totalSimulacao').html(dadosSimulados.totalADesconto);
            	$('#margemDisponivel').html(dadosSimulados.margemDisponivel);
            	
    	        $('#totalMensalidadePlanoSaude').html(dadosSimulados.totalMensalidade);
    	        $('#totalSubsidioPlanoSaude').html(dadosSimulados.totalSubsidio);
    	        $('#totalADesconto').html(dadosSimulados.totalADesconto);
            	
            	let s = "";
            	for (let i = 0; i < dadosSimulados.beneficiariosCalculados.length; i++) {
            		s = s+dadosSimulados.beneficiariosCalculados[i].bfcCodigo+";";
            		$('#detalhesPlanoMensalidade_'+dadosSimulados.beneficiariosCalculados[i].bfcCodigo).html(dadosSimulados.beneficiariosCalculados[i].mensalidade);
            		$('#detalhesPlanoSubsidio_'+dadosSimulados.beneficiariosCalculados[i].bfcCodigo).html(dadosSimulados.beneficiariosCalculados[i].subsidio);
            		$('#detalhesPlanoTotalAPagar_'+dadosSimulados.beneficiariosCalculados[i].bfcCodigo).html(dadosSimulados.beneficiariosCalculados[i].totalAPagar);
            		$('#detalhesGeral_'+dadosSimulados.beneficiariosCalculados[i].bfcCodigo).removeAttr( 'style' );
            	}
            	s = s.substring(0, s.length-1);
            	$('input[name="beneficiariosPlanoSelecionado"]').val(s);
              
        } catch (err) {
        	sessionStorage.removeItem('<%=TextHelper.forJavaScript(chaveSessionStore)%>');
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
	sessionStorage.removeItem('<%=TextHelper.forJavaScript(chaveSessionStore)%>');
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