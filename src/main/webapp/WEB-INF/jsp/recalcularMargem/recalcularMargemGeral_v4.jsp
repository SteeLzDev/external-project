<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String direction = (String) request.getAttribute("direction");
String chave = (String) request.getAttribute("chave"); 
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando"); 
String tipoEntidade = (String) request.getAttribute("tipoEntidade"); 
String estCodigo = (String) request.getAttribute("estCodigo"); 
String orgCodigo = (String) request.getAttribute("orgCodigo"); 
TransferObject criterio = (TransferObject) request.getAttribute("criterio");
String linkRefresh = (String) request.getAttribute("linkRefresh");
List<TransferObject> lstEstabelecimentos = (List<TransferObject>) request.getAttribute("lstEstabelecimentos");
List<TransferObject> lstOrgaos = (List<TransferObject>) request.getAttribute("lstOrgaos");

//Exibe Botao Rodapé
boolean exibeBotaoRodape = (boolean) (request.getAttribute("exibeBotaoRodape"));
%>
<c:set var="title">
  <hl:message key="rotulo.recalcula.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <form name="form1" method="post" action="../v3/recalcularMargemGeral?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>">
    <% if (!temProcessoRodando) { %>
      <% if (responsavel.isCseSup() || (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO))) { %>
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="mensagem.recalcula.margem.selecione.entidade.v4"/></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-12">
                <div><span id="iEntidade"><hl:message key="rotulo.recalcula.margem.entidade"/></span></div>
                <div class="form-check form-check-inline mt-2" role="radio-group" area-labeldbay="iEntidade">
                  <% if (responsavel.isCseSup()) { %>
                    <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeGeral" onChange="alterarTipoEntidade()" value="<%=(String)(AcessoSistema.ENTIDADE_CSE)%>" <% if (TextHelper.isNull(tipoEntidade) || tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                    <label class="form-check-label labelSemNegrito ml-1 pr-4" for="tipoEntidadeGeral"><hl:message key="rotulo.geral.singular"/></label>
                </div>
                  <% } %>
                  <div class="form-check form-check-inline mt-2">
                  <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeEstabelecimento" onChange="alterarTipoEntidade()" value="<%=(String)(AcessoSistema.ENTIDADE_EST)%>" <% if (tipoEntidade.equals(AcessoSistema.ENTIDADE_EST)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="tipoEntidadeEstabelecimento"><hl:message key="rotulo.estabelecimento.singular"/></label>
                  </div>
                <div class="form-check form-check-inline mt-2">
                  <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeOrgao" onChange="alterarTipoEntidade()" value="<%=(String)(AcessoSistema.ENTIDADE_ORG)%>" <% if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="tipoEntidadeOrgao"><hl:message key="rotulo.orgao.singular"/></label>
                </div>
              </div>
            </div>
            <input type="hidden" name="direction" id="direction" value="<%=direction%>">
            <div class="row">
              <div class="form-group col-sm-12">
                <label for="estCodigo"><hl:message key="rotulo.estabelecimento.singular"/></label>
                <%=JspHelper.geraCombo(lstEstabelecimentos, "estCodigo", Columns.EST_CODIGO, Columns.EST_NOME + ";" + Columns.EST_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) , "", false, 1, estCodigo, "f0.submit();", !tipoEntidade.equals(AcessoSistema.ENTIDADE_EST),"form-control")%>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-12">
                <label for="orgCodigo"><hl:message key="rotulo.orgao.singular"/></label>
                <%=JspHelper.geraCombo(lstOrgaos, "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "", false, 1, orgCodigo, "f0.submit();", !tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG), "form-control")%>
              </div>
            </div>
          </div>
        </div>
        <% } %>
        <div class="card">
        <hl:infoPeriodoV4 tipo="margem"/>
        </div>
        <div id="actions" class="btn-action">
          <%if (direction.isEmpty()){%>
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
          <% } else if (direction.equals("1")) { %>
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/importarMargem?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
          <% } else if(direction.equals("2")) { %>
          <a class="btn btn-outline-danger" href="#no-back" onClick=" postData('../v3/listarRetornoIntegracao?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
          <% } else if(direction.equals("3")) { %>
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/listarArquivosRetornoAtrasado?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
          <% } else if(direction.equals("4")) {%>
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/importarHistorico?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
          <% } %>
          <a class="btn btn-primary" data-bs-dismiss="modal" data-bs-toggle="modal" href="#confirmarSenha" onClick="confirmarRecalculo(); return false;">
            <svg width="17">
                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use>
            </svg>
            <hl:message key="rotulo.botao.confirmar"/>
          </a>
        </div>
      <% } %>
  </form>
  <% if (exibeBotaoRodape) { %>
	<div id="btns">
	  <a id="page-up" onclick="up()">
        <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
		  <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
		</svg>              
	  </a>
	  <a id="page-down" onclick="down()">
        <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
		  <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
		</svg>
	  </a>
	  <a id="page-actions" onclick="toActionBtns()">
		<svg xmlns="http://www.w3.org/2000/svg" width="145.344" height="145.344" viewBox="0 0 145.344 145.344">
		  <path id="União_1" data-name="União 1" d="M-20,59.672a72.672,72.672,0,1,1,72.671,72.672A72.671,72.671,0,0,1-20,59.672Zm10.164,0A62.508,62.508,0,1,0,52.672-2.836,62.579,62.579,0,0,0-9.836,59.672Zm82.6,40.182H24.545A12.069,12.069,0,0,1,12.49,87.8V31.544A12.069,12.069,0,0,1,24.545,19.49h44.2a4.014,4.014,0,0,1,2.841,1.177L91.678,40.757A4.019,4.019,0,0,1,92.855,43.6V87.8A12.069,12.069,0,0,1,80.8,99.854Zm0-40.182a4.018,4.018,0,0,1,4.019,4.018V91.817H80.8A4.023,4.023,0,0,0,84.818,87.8V45.263L67.081,27.526H36.6V39.58H64.727a4.019,4.019,0,0,1,0,8.037H32.581A4.018,4.018,0,0,1,28.563,43.6V27.526H24.545a4.023,4.023,0,0,0-4.018,4.019V87.8a4.023,4.023,0,0,0,4.018,4.018h4.019V63.689a4.018,4.018,0,0,1,4.018-4.018ZM36.6,91.817H68.745V67.708H36.6Z" transform="translate(20 13)"/>
		</svg>
	  </a>
	</div>
  <% }%>
</c:set>
<c:set var="javascript">
<script>
  var f0 = document.forms[0];

  window.onload = doLoad(<%=(boolean)temProcessoRodando%>);
  
  function confirmarRecalculo() {
    if (confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.recalcula.margem.confirma", responsavel)%>')) {
      document.forms[0].action= '../v3/recalcularMargemGeral?acao=confirmar&<%=SynchronizerToken.generateToken4URL(request)%>';
      f0.submit();
    }
  }
  function doLoad(reload) {
    if (reload) {
      setTimeout("refresh()", 15*1000);
    }
  }
  
  function refresh() {
    postData('<%= TextHelper.forJavaScriptBlock(linkRefresh) %>');
  }
  
  function alterarTipoEntidade() {
    var tipoEntidade = getCheckedRadio('form1', 'tipoEntidade');
  
    if (tipoEntidade == null || tipoEntidade == '') {
      alert('<hl:message key="mensagem.recalcula.margem.selecione.entidade"/>');
      return;
    }
  
    if (tipoEntidade == 'CSE') {
      f0.estCodigo.disabled = true;
      f0.orgCodigo.disabled = true;
      f0.submit();
    } else if (tipoEntidade == 'EST') {
      f0.estCodigo.disabled = false;
      f0.orgCodigo.disabled = true;
    } else if (tipoEntidade == 'ORG') {
      f0.estCodigo.disabled = true;
      f0.orgCodigo.disabled = false;
    }
  }
</script>
<script>
	let btnDown = document.querySelector('#btns');
	const pageActions = document.querySelector('#page-actions');
	const pageSize = document.body.scrollHeight;
	
	function up(){
		window.scrollTo({
			top: 0,
			behavior: "smooth",
		});
	}
	
	function down(){
		let toDown = document.body.scrollHeight;
		window.scrollBy({
			top: toDown,
			behavior: "smooth",
		});
	}

	function toActionBtns(){
		let save = document.querySelector('#actions').getBoundingClientRect().top;
		window.scrollBy({
			top: save,
			behavior: "smooth",
		});
	}
	
	function btnTab(){
	    let scrollSize = document.documentElement.scrollTop;
	    
	    if(scrollSize >= 300){
		    btnDown.classList.add('btns-active');    
	    } else {
		    btnDown.classList.remove('btns-active');
	    }
	}
	

	window.addEventListener('scroll', btnTab);
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>   