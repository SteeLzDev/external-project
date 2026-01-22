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
  List<TransferObject> beneficiariosGrupoFamiliar = (List) request.getAttribute("beneficiariosGrupoFamiliar");
  TransferObject contratoBeneficio = (TransferObject) request.getAttribute("contratoBeneficio");
  String rseCodigo = (String) request.getAttribute("rseCodigo");
  String margemDisponivel = (String) request.getAttribute("margemDisponivel");
  boolean reiniciarLocalSession = (boolean) request.getAttribute("reiniciarLocalSession");
  BigDecimal valorTotal = (BigDecimal) contratoBeneficio.getAttribute("valorTotal");
  BigDecimal valorSubsidio = (BigDecimal) contratoBeneficio.getAttribute("valorSubsidio");
  BigDecimal totalDescontar = valorTotal.subtract(valorSubsidio);
  String benCodigo = (String) contratoBeneficio.getAttribute(Columns.BEN_CODIGO);
  String csaCodigo = (String) contratoBeneficio.getAttribute(Columns.CSA_CODIGO);
  List<String> beneficiariosComContratoSaude = (List) request.getAttribute("beneficiariosComContratoSaude");
  List<String> beneficiariosComContrato = new ArrayList<String>();
  String nseCodigo = (String) request.getAttribute("nseCodigo");
  boolean fluxoInicial = (boolean) request.getAttribute("fluxoInicial");
  boolean permiteSimularBeneficioSemMargem = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, CodedValues.TPC_SIM, responsavel);
%>
<c:set var="title">
  <hl:message key="rotulo.simulacao.beneficio.titulo.odonto" />
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
          <h2 class="card-header-title"><hl:message key="rotulo.simulacao.beneficio.plano.odontologico"/></h2>
        </div>
        <div class="card-body table-responsive">
          <form>
            <div class="row">
              <div class="col-sm">
                <%=contratoBeneficio.getAttribute(Columns.CSA_NOME) %>
              </div>
            </div>
            <div class="row">
              <div class="col-sm">
                <table class="table">
                  <tbody>
                  <%=TextHelper.forHtmlContent(contratoBeneficio.getAttribute(Columns.BEN_DESCRICAO)) %>
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
          <h2 class="card-header-title"><hl:message key="rotulo.simulacao.beneficio.resumo.plano.odontologico"/></h2>
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
            </div>
            <div class="divTableRow">
            <%if (permiteSimularBeneficioSemMargem) { %>
              <div class="divTableCell font-weight-bold" id="margemDisponivel"><%=TextHelper.forHtmlAttribute(margemDisponivel)%></div>
            <%} %>
              <div class="divTableCell font-weight-bold" id="totalMensalidadePlanoOdontologico"><%=TextHelper.forHtmlAttribute(NumberHelper.format(valorTotal.doubleValue(), NumberHelper.getLang()))%></div>
              <div class="divTableCell font-weight-bold" id="totalSubsidioPlanoOdontologico"><%=TextHelper.forHtmlAttribute(NumberHelper.format(valorSubsidio.doubleValue(), NumberHelper.getLang()))%></div>
              <div class="divTableCell font-weight-bold" id="totalADesconto"><%=TextHelper.forHtmlAttribute(NumberHelper.format(totalDescontar.doubleValue(), NumberHelper.getLang()))%></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="row" id="resultadoPlanoOdontologico">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.simulacao.beneficio.resumo.simulacao.plano.odontologico"/></h2>
        </div>
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th class="ocultarColuna" scope="col"
                  title="<%=ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.selecione.todos.beneficiarios", responsavel) %>"
                  width="10%">
                </th>
                <th scope="col"><hl:message key="rotulo.simulacao.beneficio.resultado.nome"/></th>
                <th scope="col"><hl:message key="rotulo.simulacao.beneficio.resultado.titularidade"/></th>
                <th scope="col"><hl:message key="rotulo.simulacao.beneficio.resultado.valor.do.plano"/></th>
                <th scope="col"><hl:message key="rotulo.simulacao.beneficio.resultado.valor.do.subsidio"/></th>
                <th scope="col"><hl:message key="rotulo.simulacao.beneficio.resultado.acoes"/></th>
              </tr>
            </thead>
            <tbody>
              <% for (TransferObject beneficiario : beneficiariosGrupoFamiliar) {
                      boolean temContrato = beneficiario.getAttribute(Columns.CBE_VALOR_TOTAL) != null ? true : false;
                      BigDecimal total = (BigDecimal) beneficiario.getAttribute(Columns.CBE_VALOR_TOTAL);
                      BigDecimal subsidio = (BigDecimal) beneficiario.getAttribute(Columns.CBE_VALOR_SUBSIDIO);
                      
                      if (temContrato) {
                          beneficiariosComContrato.add((String)beneficiario.getAttribute(Columns.BFC_CODIGO));
                      }
              %>
              <tr class="<%=temContrato ? "table-checked" : "selecionarLinha"%>" id="linha_plano_odontologico_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>">
                <td class="ocultarColuna"
                  aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.selecione.multiplos.beneficiarios", responsavel) %>"
                  title="<%=ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.selecione.multiplos.beneficiarios", responsavel) %>"
                  >
                  <div class="form-check">
                    <input class="form-check-input ml-0"  <%=temContrato ? "checked disabled" : ""%>
                      name="selecionarCheckBox" type="checkbox" value=<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%> id="input_plano_odontologico_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>">
                  </div>
                </td>
                <td class="<%=!temContrato ? "selecionarColuna" : ""%>"><%=TextHelper.forHtmlContent(beneficiario.getAttribute(Columns.BFC_NOME))%></td>
                <td class="<%=!temContrato ? "selecionarColuna" : ""%>"><%=TextHelper.forHtmlContent(beneficiario.getAttribute(Columns.TIB_DESCRICAO))%></td>
                <td class="<%=!temContrato ? "selecionarColuna" : ""%>" id="mensalidade_plano_odontologico_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"><%=temContrato ? TextHelper.forHtmlAttribute(NumberHelper.format(total.doubleValue(), NumberHelper.getLang())) : ""%></td>
                <td class="<%=!temContrato ? "selecionarColuna" : ""%>" id="subsidio_plano_odontologico_<%=TextHelper.forHtmlAttribute(beneficiario.getAttribute(Columns.BFC_CODIGO))%>"><%=temContrato ? TextHelper.forHtmlAttribute(NumberHelper.format(subsidio.doubleValue(), NumberHelper.getLang())) : ""%></td>
                <td>
                  <% if (!temContrato) { %>
                        <a href="#no-back" name="selecionaAcaoSelecionar"><hl:message key="rotulo.simulacao.beneficio.resultado.selecionar"/></a>
                  <% } %>
                </td>
              </tr>
              <% } %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="2"><hl:message key="rotulo.simulacao.beneficio.resumo.listagem.resultado.plano.odontologico"/></td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>



  <div class="btn-action col-sm">
    <a class="btn btn-outline-danger" href="#no-back" onClick="fluxoVoltar()" value="Cancelar"><hl:message key="rotulo.botao.voltar" /></a>
    <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/listarBeneficiarios?acao=listar&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.novo.beneficiario" /></a>
    <a href="#no-back" name="Button" class="btn btn-primary" onClick="proximo(); return false;"><hl:message key="rotulo.botao.continuar"/></a>  
  </div>
</c:set>
<c:set var="javascript">
  <script src="../js/colunaCheckbox.js?<hl:message key="release.tag"/>"></script>
  <script>

  function fluxoVoltar() { 
    <%
    if (responsavel.isSer() && fluxoInicial) { 
    %>
    	postData('../v3/carregarPrincipal');
    <%
    } else if (fluxoInicial) { 
    %>
    	postData('../v3/incluirBeneficiarioSimulacaoBeneficios?acao=iniciar');
    <%
    } else { 
    %>
    	postData('../v3/incluirBeneficiarioSimulacaoBeneficios?acao=planoSaude&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&reiniciarLocalSession=false');
    <%
    } 
    %>
  }
  
  $( document ).ready(function() {
	  if (<%=TextHelper.forJavaScript(reiniciarLocalSession)%>) {
		  sessionStorage.removeItem("dadosSimulacaoInclusao");
	  } else {
        let data = sessionStorage.getItem("dadosSimulacaoInclusao");
        try{
          if (data != null) {
              showModal();
              let dataJ = JSON.parse(data);
              preencherDadosSimulacao(data);
              hideModal();
          }
        } catch (err) {
          postData('../v3/carregarPrincipal');
        }
      }
      
	  simulaAjaxPlanoOdontologico();
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
  
  $(".selecionarColuna, a[name='selecionaAcaoSelecionar'], input[name='selecionarCheckBox'], #resultadoPlanoOdontologico #checkAll").click(simulaAjaxPlanoOdontologico);
  
  function simulaAjaxPlanoOdontologico() {
	  var bfcCodigoSelecionados = [];
	  $("#resultadoPlanoOdontologico input:checkbox[name=selecionarCheckBox]:checked").each(function(){
		  bfcCodigoSelecionados.push($(this).val());
	  });

	  if (bfcCodigoSelecionados !== undefined || bfcCodigoSelecionados.length == 0) {
  	      showModal();
    	  $.ajax({
    		    type: 'POST',
    		    url: '../v3/incluirBeneficiarioSimulacaoBeneficios/simulaAjax?<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_',
    		    data: {
    		    	'nseCodigo' : '<%=TextHelper.forJavaScript(nseCodigo)%>',
    		        'bfcCodigoSelecionados': bfcCodigoSelecionados,
    		        'rseCodigo': '<%=TextHelper.forJavaScript(rseCodigo)%>',
    		        'benCodigo': '<%=TextHelper.forJavaScript(benCodigo)%>',
    		        'csaCodigo': '<%=TextHelper.forJavaScript(csaCodigo)%>',
    		        'dadosSimulacao': sessionStorage.getItem("dadosSimulacaoInclusao")
    			    },
    		    success: preencherDadosSimulacao,
    		    error: function (request, status, error) {
    		    	trataErroDadosSimulacao(request, status, error);
    		    }
    	  }).always(function () {
    		  hideModal();
    	  });
	  }
  }
  
  function preencherDadosSimulacao(data) {
	  try {
		  	$('.selecionarLinha').css("display","none");
		  	
		  	var dataParse = JSON.parse(data);
  		    sessionStorage.setItem("dadosSimulacaoInclusao", data);

  		    var dadosNse = null;
  		    for (var i in dataParse.simulacao) {
  		      if (dataParse.simulacao[i].nseCodigo == <%= TextHelper.forJavaScript(nseCodigo) %>) {
  		        dadosNse = dataParse.simulacao[i];
  		        break;
  		      }
  		    }
		  	
	    	for (var i = 0; i < dadosNse.beneficiariosCalculados.length; i++) {
	    		$('#mensalidade_plano_odontologico_'+dadosNse.beneficiariosCalculados[i].bfcCodigo).html(dadosNse.beneficiariosCalculados[i].mensalidade)
	    		$('#subsidio_plano_odontologico_'+dadosNse.beneficiariosCalculados[i].bfcCodigo).html(dadosNse.beneficiariosCalculados[i].subsidio)
	    		$('#linha_plano_odontologico_'+dadosNse.beneficiariosCalculados[i].bfcCodigo).addClass("table-checked");
	    		$('#linha_plano_odontologico_'+dadosNse.beneficiariosCalculados[i].bfcCodigo).removeAttr( 'style' );
	    		$('#input_plano_odontologico_'+dadosNse.beneficiariosCalculados[i].bfcCodigo).prop('checked', true);
	    	}
	    	
	    	if (dadosNse.beneficiariosCalculados.length > 0) {
	    		$("table th:first").show();
	    		$(".ocultarColuna").show();
	    	}
	    	
	    	for (var i = 0; i < dadosNse.beneficiariosSemCalculos.length; i++) {
	    		$('#mensalidade_plano_odontologico_'+dadosNse.beneficiariosSemCalculos[i].bfcCodigo).html(dadosNse.beneficiariosSemCalculos[i].mensalidade)
	    		$('#subsidio_plano_odontologico_'+dadosNse.beneficiariosSemCalculos[i].bfcCodigo).html(dadosNse.beneficiariosSemCalculos[i].subsidio)
	    		$('#linha_plano_odontologico_'+dadosNse.beneficiariosSemCalculos[i].bfcCodigo).removeAttr( 'style' );
	    	}
	    	
	    	$('#totalMensalidadePlanoOdontologico').html(dadosNse.totalMensalidade);
	    	$('#totalSubsidioPlanoOdontologico').html(dadosNse.totalSubsidio);
	    	$('#margemDisponivel').html(dataParse.margemDisponivel);
	    	
	    	$('#totalADesconto').html(dadosNse.totalADesconto);
  	} catch (err) {
  		postData('../v3/carregarPrincipal');
  	}
  }
  
  function proximo() { 
	  showModal();
	  postData('../v3/incluirBeneficiarioSimulacaoBeneficios?acao=detalhes&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&beneficiariosComContratoSaude=<%=beneficiariosComContratoSaude != null ? TextHelper.forJavaScript(TextHelper.join(beneficiariosComContratoSaude, ",")) : ""%>&beneficiariosComContratoOdonto=<%=beneficiariosComContrato != null ? TextHelper.forJavaScript(TextHelper.join(beneficiariosComContrato, ",")) : ""%>');
  }

  function trataErroDadosSimulacao(request, status, error) { 
  	try {
  		sessionStorage.removeItem("dadosSimulacaoInclusao");
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
		} else { 
			postData('../v3/carregarPrincipal');
		}	
	} catch (err) {
		postData('../v3/carregarPrincipal');
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