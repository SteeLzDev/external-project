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
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.persistence.entity.Consignataria" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session"
  class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  List<Consignataria> consignatariasPlanoSaude = (List) request.getAttribute("consignatariasPlanoSaude");
  List<TransferObject> beneficiariosGrupoFamiliar = (List) request.getAttribute("beneficiariosGrupoFamiliar");
  String rseCodigo = (String) request.getAttribute("rseCodigo");
  String nseCodigo = (String) request.getAttribute("nseCodigo");
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
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.simulacao.beneficio.plano.saude"/></h2>
        </div>
        <div class="card-body table-responsive">
          <form>
            <div class="row">
              <div class="col-sm">
                <label for="selectOperadoraPlanoSaude"><hl:message key="rotulo.simulacao.beneficio.operadora"/></label>
                <select class="form-control"
                  id="selectOperadoraPlanoSaude">
                  <option value="1"><hl:message key="rotulo.simulacao.beneficio.selecione.operadora"/></option>
                  <%
                      for (Consignataria consignataria : consignatariasPlanoSaude) {
                  %>
                  <option
                    value="<%=TextHelper.forHtmlAttribute(consignataria.getCsaCodigo())%>"><%=TextHelper.forHtmlContent(consignataria.getCsaNome())%></option>
                  <%
                      }
                  %>
                </select>
              </div>
            </div>
            <div class="row">
              <div class="col-sm">
                <table class="table">
                  <tbody id="detalhesBeneficiosPlanoSaude">
                  </tbody>
                </table>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.simulacao.beneficio.resumo.plano.saude"/></h2>
        </div>
        <div class="card-body divTable">
          <div class="divTableBody text-center">
            <div class="divTableRow">
            <%if (permiteSimularBeneficioSemMargem) { %>
              <div class="divTableHead"><hl:message key="rotulo.simulacao.beneficio.margem.disponivel"/></div>
            <%} %>  
              <div class="divTableHead"><hl:message key="rotulo.simulacao.beneficio.total.do.plano"/></div>
              <div class="divTableHead"><hl:message key="rotulo.simulacao.beneficio.total.do.subsidio"/></div>
              <div class="divTableHead"><hl:message key="rotulo.simulacao.beneficio.total.a.descontar"/></div>
            </div>
            <div class="divTableRow">
            <%if (permiteSimularBeneficioSemMargem) { %>
              <div class="divTableCell font-weight-bold" id="margemDisponivel"></div>
            <%} %>  
              <div class="divTableCell font-weight-bold" id="totalMensalidadePlanoSaude"></div>
              <div class="divTableCell font-weight-bold" id="totalSubsidioPlanoSaude"></div>
              <div class="divTableCell font-weight-bold" id="totalADesconto"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="row" id="resultadoPlanoSaude">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.simulacao.beneficio.resumo.simulacao.plano.saude"/></h2>
        </div>
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th class="ocultarColuna" scope="col"
                  title="<%=ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.selecione.todos.beneficiarios", responsavel) %>"
                  style="display: none;" width="10%">
                  <form class="form-check">
                    <input class="form-check-input ml-0" id="checkAll"
                      type="checkbox">
                  </form>
                </th>
                <th scope="col"><hl:message key="rotulo.simulacao.beneficio.resultado.nome"/></th>
                <th scope="col"><hl:message key="rotulo.simulacao.beneficio.resultado.titularidade"/></th>
                <th scope="col"><hl:message key="rotulo.simulacao.beneficio.resultado.valor.do.plano"/></th>
                <th scope="col"><hl:message key="rotulo.simulacao.beneficio.resultado.valor.do.subsidio"/></th>
                <th scope="col"><hl:message key="rotulo.simulacao.beneficio.resultado.acoes"/></th>
              </tr>
            </thead>
            <tbody>
              <% for (TransferObject beneficiario : beneficiariosGrupoFamiliar) { %>
              <tr class="selecionarLinha" id="linha_plano_saude_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>" style="display: none;">
                <td class="ocultarColuna"
                  aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.selecione.multiplos.beneficiarios", responsavel) %>"
                  title="<%=ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.selecione.multiplos.beneficiarios", responsavel) %>"
                  style="display: none;">
                  <div class="form-check">
                    <input class="form-check-input ml-0"
                      name="selecionarCheckBox" type="checkbox" value=<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%> id="input_plano_saude_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>">
                  </div>
                </td>
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(beneficiario.getAttribute(Columns.BFC_NOME))%></td>
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(beneficiario.getAttribute(Columns.TIB_DESCRICAO))%></td>
                <td class="selecionarColuna" id="mensalidade_plano_saude_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></td>
                <td class="selecionarColuna" id="subsidio_plano_saude_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"></td>
                <td><a href="#no-back" name="selecionaAcaoSelecionar"><hl:message key="rotulo.simulacao.beneficio.resultado.selecionar"/></a></td>
              </tr>
              <% } %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="2"><hl:message key="rotulo.simulacao.beneficio.resumo.listagem.resultado.plano.saude"/></td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>



  <div class="btn-action col-sm">
    <a class="btn btn-outline-danger" href="#no-back" onClick="fluxoVoltar()"><hl:message key="rotulo.botao.voltar" /></a>
    <a class="btn btn-primary" href="#no-back" onClick="fluxoAddBeneficiario()"><hl:message key="rotulo.botao.novo.beneficiario" /></a>
    <a href="#no-back" class="btn btn-primary" onClick="proximo(); return false;"><hl:message key="rotulo.botao.continuar"/></a>  
  </div>
</c:set>
<c:set var="javascript">
  <script src="../js/colunaCheckbox.js?<hl:message key="release.tag"/>"></script>
  <script src="<c:url value='/js/bootstrap/5.2.3/bootstrap.js'/>"></script>

  <script>
  
  function fluxoAddBeneficiario() { 
	  postData('../v3/listarBeneficiarios?acao=listar&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')
  }
  
  function fluxoVoltar() { 
    <%
    if (responsavel.isSer()) { 
    %>
        postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');
    <%
    } else { 
    %>
    	postData('../v3/simulacaoBeneficios?acao=iniciar');
    <%
    } 
    %>
  }
    
  String.prototype.replaceAll = function(search, replacement) {
	    var target = this;
	    return target.replace(new RegExp(search, 'g'), replacement);
	};
  
  $( document ).ready(function() {
	  let data = sessionStorage.getItem("dadosSimulacao"); 
	  try{
		  if (data != null) {
			  showModal();
    		  let dataJ = JSON.parse(data);
    		  if (dataJ.rseCodigo == '<%=TextHelper.forJavaScript(rseCodigo)%>'){
    			  var dataNse = null;
    			  for (var i in dataJ.simulacao) {
    			      if (dataJ.simulacao[i].nseCodigo == <%= TextHelper.forJavaScript(nseCodigo) %>) {
						dataNse = dataJ.simulacao[i];
    			        break;
    			      }
    			  }
    			  
    			  if (dataNse != null) {    			  
        			  $('#selectOperadoraPlanoSaude option[value="'+dataNse.csaCodigo+'"]').attr("selected",true);
        			  carregaBeneficiosAjax(dataNse.csaCodigo, '#radioPlano_'+dataNse.benCodigo).then(function () {
        				  preencherDadosSimulacao(data, false);
        				  simulaAjaxPlanoSaude();
        			  });
    			  } else { 
    				  hideModal();
    			  }
    			  
    		  } else { 
    			  sessionStorage.removeItem("dadosSimulacao");
    			  hideModal();
    		  }		
		  }
	  } catch (err) {
		  sessionStorage.removeItem("dadosSimulacao");
		  postData('../v3/carregarPrincipal');
	  }
  });
  
  var htmlTagBase = '<tr onclick="<ACTION_TR>"><td><div class="form-check" style="margin-left: 5% !important; margin-top: 5% !important;"> <input class="form-check-input" name="radioPlano" value="<BEN_CODIGO>" type="radio"  id="radioPlano_<BEN_CODIGO>"> </div> </td> <td> <label for="radioPlano"><BEN_DESCRICAO></label> </td></tr>';
  
  function showModal() {
    var myModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('modalSimularBeneficio'),{
		  keyboard: false,
		  backdrop: 'static',
	  });
    myModal.show();
  }
  
  function hideModal() {
	  setTimeout(function(){
        var modal_obj = bootstrap.Modal.getInstance(document.querySelector('#modalSimularBeneficio'));
        modal_obj.hide();
	  }, 200);
  }
  
  $('#selectOperadoraPlanoSaude').change(function () {
	    var csaCodigo = $(this).find(':selected')[0].value;
	    showModal();
	    carregaBeneficiosAjax(csaCodigo).then(function () {
			  hideModal();
		});
  });
  
  function carregaBeneficiosAjax(csaCodigo, elementoDefault) {
	  	return $.ajax({
	        type: 'POST',
	        url: '../v3/simulacaoBeneficios/carregaBeneficiosAjax?<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_',
	        data: {
	            'csaCodigo': csaCodigo, 
	            'nseCodigo' : <%=TextHelper.forJavaScript(CodedValues.NSE_PLANO_DE_SAUDE)%>
	        },
	        success: function (data) {
	        	var data = JSON.parse(data); 
	            var detalhesBeneficios = $('#detalhesBeneficiosPlanoSaude');
	            detalhesBeneficios.empty();
	            
	            for (var i = 0; i < data.beneficios.length; i++) {
	            	var tmp = htmlTagBase.replaceAll("<BEN_CODIGO>", data.beneficios[i].id);
	            	tmp = tmp.replaceAll("<BEN_DESCRICAO>", data.beneficios[i].detalhe);
	            	tmp = tmp.replaceAll("<ACTION_TR>", "preSimulaAjaxPlanoSaude(this)");
	            	detalhesBeneficios.append(tmp);
	            }
	            detalhesBeneficios.change();
	        },
	        error: function (request, status, error) {
	        	trataErroDadosSimulacao(request);
		    }
	    }).done(function () {
	    	if (typeof(elementoDefault) != "undefined") {
	    		$(elementoDefault).prop("checked", true);
	    	}
	    });
  }
  
  $("#resultadoPlanoSaude .selecionarColuna,#resultadoPlanoSaude #checkAll,#resultadoPlanoSaude input[name='selecionarCheckBox'], a[name='selecionaAcaoSelecionar']").click(simulaAjaxPlanoSaude);
  
  function preSimulaAjaxPlanoSaude(x) {
	  x.querySelector(".form-check-input").checked = true;
	  simulaAjaxPlanoSaude()
  }
  
  function simulaAjaxPlanoSaude() {
	  var bfcCodigoSelecionados = [];
	  $("#resultadoPlanoSaude input:checkbox[name=selecionarCheckBox]:checked").each(function(){
		  bfcCodigoSelecionados.push($(this).val());
	  });
	  
	  var benCodigo = $("#detalhesBeneficiosPlanoSaude :radio:checked").val()
	  var csaCodigo = $('#selectOperadoraPlanoSaude').find(':selected')[0].value;
	  if (typeof(benCodigo) != "undefined") {
		  showModal();
		  
		  $.ajax({
			    type: 'POST',
			    url: '../v3/simulacaoBeneficios/simulaAjax?<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_',
			    data: {
			    	'nseCodigo' : '<%=TextHelper.forJavaScript(nseCodigo)%>',
			        'bfcCodigoSelecionados': bfcCodigoSelecionados,
			        'benCodigo': benCodigo,
			        'rseCodigo': '<%=TextHelper.forJavaScript(rseCodigo)%>',
			        'csaCodigo': csaCodigo,
			        'dadosSimulacao': sessionStorage.getItem("dadosSimulacao")
				    },
			    success: function (data) {
			    	preencherDadosSimulacao(data, true);
			    },
			    error: function (request, status, error) {
			    	trataErroDadosSimulacao(request, status, error);
			    }
		  })
	  } 
  }
  
  function trataErroDadosSimulacao(request, status, error) { 
  	try {
  		
  		alert(request.responseText);
		if (request.status == 500) {
			postData('../v3/carregarPrincipal');
		} else if (request.status == 400) {
	        $("#resultadoPlanoSaude input:checkbox[name=selecionarCheckBox]:checked").each(function(i, e){
	  			e.checked = false;
	  	  	});
	        
	        $("table th:first").hide();
  			$(".ocultarColuna").hide();
	        
  			hideModal();
	        simulaAjaxPlanoSaude();				
		} else if (request.status == 409) { 
			$('.selecionarLinha').css("display","none");
			$("#detalhesBeneficiosPlanoSaude :radio:checked").prop('checked', false);
			hideModal();
		} else if (request.status == 406) {
			$('.selecionarLinha').css("display","none");
			$("#detalhesBeneficiosPlanoSaude").html("");
			hideModal();
		} else if (request.status == 307) {
			postData('../v3/simulacaoBeneficios?acao=planoSaude&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>');
		} else { 
			postData('../v3/carregarPrincipal');
		}	
	} catch (err) {
		postData('../v3/carregarPrincipal');
	}
  }

  function preencherDadosSimulacao(data, pararModal) {
	  try {
	    $('.selecionarLinha').css("display", "none");

	    var dataParse = JSON.parse(data);
	    sessionStorage.setItem("dadosSimulacao", data);

	    var dadosNse = null;
	    for (var i in dataParse.simulacao) {
	      if (dataParse.simulacao[i].nseCodigo == <%= TextHelper.forJavaScript(nseCodigo) %>) {
	        dadosNse = dataParse.simulacao[i];
	        break;
	      }
	    }

	    for (var i = 0; i < dadosNse.beneficiariosCalculados.length; i++) {
	      $('#mensalidade_plano_saude_' + dadosNse.beneficiariosCalculados[i].bfcCodigo).html(dadosNse.beneficiariosCalculados[i].mensalidade);
	      $('#subsidio_plano_saude_' + dadosNse.beneficiariosCalculados[i].bfcCodigo).html(dadosNse.beneficiariosCalculados[i].subsidio);
	      $('#linha_plano_saude_' + dadosNse.beneficiariosCalculados[i].bfcCodigo).addClass("table-checked");
	      $('#linha_plano_saude_' + dadosNse.beneficiariosCalculados[i].bfcCodigo).removeAttr('style');
	      $('#input_plano_saude_' + dadosNse.beneficiariosCalculados[i].bfcCodigo).prop('checked', true);
	    }

	    if (dadosNse.beneficiariosCalculados.length > 0) {
	      $("table th:first").show();
	      $(".ocultarColuna").show();
	    }

	    for (var i = 0; i < dadosNse.beneficiariosSemCalculos.length; i++) {
	      $('#mensalidade_plano_saude_' + dadosNse.beneficiariosSemCalculos[i].bfcCodigo).html(dadosNse.beneficiariosSemCalculos[i].mensalidade);
	      $('#subsidio_plano_saude_' + dadosNse.beneficiariosSemCalculos[i].bfcCodigo).html(dadosNse.beneficiariosSemCalculos[i].subsidio);
	      $('#linha_plano_saude_' + dadosNse.beneficiariosSemCalculos[i].bfcCodigo).removeAttr('style');
	      $('#linha_plano_saude_' + dadosNse.beneficiariosSemCalculos[i].bfcCodigo).removeClass("table-checked");
	    }

	    $('#totalMensalidadePlanoSaude').html(dadosNse.totalMensalidade);
	    $('#totalSubsidioPlanoSaude').html(dadosNse.totalSubsidio);
	    $('#totalADesconto').html(dadosNse.totalADesconto);
	    
	    
	    $('#margemDisponivel').html(dataParse.margemDisponivel);
	    
	    if (pararModal){
	    	hideModal();
	    }
	  } catch (err) {
	    postData('../v3/carregarPrincipal');
	  }
  }
  
  function proximo() { 
	  showModal();
	  postData('../v3/simulacaoBeneficios?acao=planoOdontologico&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>');
  }
    
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>