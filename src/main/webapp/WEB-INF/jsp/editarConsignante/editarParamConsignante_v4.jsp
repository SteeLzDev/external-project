<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditarConsignante = (Boolean) request.getAttribute("podeEditarConsignante");

List<?> paramSist = (List<?>) request.getAttribute("paramSist");
List<?> paramSistConsulta = (List<?>) request.getAttribute("paramSistConsulta");

// Busca os Parâmetros de Sistema Sobre a Reimplantação
boolean tpcReimplantacaoAutomatica = (Boolean) request.getAttribute("tpcReimplantacaoAutomatica");
boolean tpcCsaEscolheReimpl = (Boolean) request.getAttribute("tpcCsaEscolheReimpl");
boolean exibeBotaoRodape = (Boolean) request.getAttribute("exibeBotaoRodape");
%>
<c:set var="title">
  <hl:message key="rotulo.parametro.consignante.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post"
    action="../v3/manterParamConsignante?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>"
    name="form1">
    <%
        if (responsavel.isSup()) {
    %>
    <div class="row">
      <div class="col-sm-12 mb-2">
        <div class="float-end">
          <div class="btn-action">
            <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes"
              aria-expanded="false" class="btn btn-primary" type="submit">
              <hl:message key="rotulo.mais.acoes" />
            </button>
            <div class="dropdown-menu dropdown-menu-right"
              aria-labelledby="acoes" x-placement="bottom-end"
              style="position: absolute; transform: translate3d(350px, 50px, 0px); top: 0px; left: 0px; will-change: transform;">
              <a class="dropdown-item" href="#no-back"
                onClick="postData('../v3/manterParamConsignante?acao=listarHistoricoParametro&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')">
                <hl:message key="mensagem.historico.alteracao.parametros.cse.clique.aqui" /></a>
            </div>       
          </div>
        </div>
      </div>
    </div>
    <%
        }
    %>

    <%
        if (paramSist != null && !paramSist.isEmpty()) {
    %>

    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title">
          <hl:message key="rotulo.editar.grid" />
        </h2>
      </div>
      <div class="card-body">
        <div class="legend">
          <%=TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel))%>
        </div>
        <%
          String tpc_codigo, tpc_descricao, tpc_dominio, psi_vlr, controle;
		  String gps_descricao, gps_descricao_anterior = null;
		  CustomTransferObject next = null;

          Iterator<?> it = paramSist.iterator();
		  while (it.hasNext()) {
		  next = (CustomTransferObject) it.next();

		  tpc_codigo = next.getAttribute(Columns.TPC_CODIGO).toString();
		  tpc_descricao = next.getAttribute(Columns.TPC_DESCRICAO).toString();
		  gps_descricao = (next.getAttribute(Columns.GPS_DESCRICAO) != null
			? next.getAttribute(Columns.GPS_DESCRICAO).toString()
			: ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado",responsavel));
  
  		  tpc_dominio = next.getAttribute(Columns.TPC_DOMINIO).toString();
  		  psi_vlr = next.getAttribute(Columns.PSI_VLR) != null
			? next.getAttribute(Columns.PSI_VLR).toString()
			: (String) next.getAttribute(Columns.TPC_VLR_DEFAULT);

            if (gps_descricao_anterior == null || !gps_descricao_anterior.equals(gps_descricao)) {
        %>
        <div id="descricao" class="mb-2 mt-2">
          <%=TextHelper.forHtmlContent(gps_descricao)%>
        </div>
            <%
            }
            %>
        <div class="row">
          <div class="col-sm-6 col-md-12">
            <div class="form-group">
              <label for="<%=TextHelper.forHtmlContent(tpc_codigo)%>"><%=TextHelper.forHtmlContent(tpc_descricao)%></label>
              </div>
            <% if (podeEditarConsignante && !tpc_codigo.equals(CodedValues.TPC_DIR_RAIZ_ARQUIVOS)) {
					if (tpc_codigo.equals(CodedValues.TPC_CONCLUI_NAO_PAGAS) || tpc_codigo.equals(CodedValues.TPC_CSA_ALTERA_CONCLUSAO_NAO_PAGAS)) {
						controle = JspHelper.montaValor(tpc_codigo, tpc_dominio, TextHelper.forHtmlContent(psi_vlr),
								!tpcReimplantacaoAutomatica	|| (tpcReimplantacaoAutomatica && tpcCsaEscolheReimpl),
								null, -1, -1, "form-control", null, null);
					} else { controle = JspHelper.montaValor(tpc_codigo, tpc_dominio,
					      TextHelper.forHtmlContent(psi_vlr), true, null, -1, -1, "form-control", null,
					      null);
					}
				} else { controle = JspHelper.montaValor(tpc_codigo, tpc_dominio, TextHelper.forHtmlContent(psi_vlr),
              									false, null, -1, -1, "form-control", null, null);
				}
             %>
              <%=controle%>
            </div>
        </div>
        <%
            gps_descricao_anterior = gps_descricao;
	}
        %>
      </div>
    </div>
    <%
        }
    %>
  </form>
  <div class="row">
    <%
        if (podeEditarConsignante && paramSist != null && !paramSist.isEmpty()) {
    %>
    <div class="col-sm-12">
      <div id="actions" class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back"
          onClick="postData('../v3/editarConsignante?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>'); return false;">
          <hl:message key="rotulo.acoes.cancelar" />
        </a>
        <a class="btn btn-primary" href="#no-back" onClick="f0.submit(); return false;">
          <hl:message key="rotulo.botao.salvar" />
        </a>
      </div>
    </div>
    <%
        }
    %>
  </div>
<% if (paramSistConsulta != null && !paramSistConsulta.isEmpty()) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.editar.grid" /></h2>
    </div>
    <div class="card-body">
        <%
        String tpc_codigo, tpc_descricao, tpc_dominio, psi_vlr, controle;
        String gps_descricao, gps_descricao_anterior = null;
        CustomTransferObject next = null;
        
        Iterator<?> it = paramSistConsulta.iterator();
        while (it.hasNext()) {
          next = (CustomTransferObject)it.next();
        
          tpc_codigo = next.getAttribute(Columns.TPC_CODIGO).toString();
          tpc_descricao = next.getAttribute(Columns.TPC_DESCRICAO).toString();
          gps_descricao = (next.getAttribute(Columns.GPS_DESCRICAO) != null ? next.getAttribute(Columns.GPS_DESCRICAO).toString() : ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel));
        
          tpc_dominio = next.getAttribute(Columns.TPC_DOMINIO).toString();
          psi_vlr = next.getAttribute(Columns.PSI_VLR) != null ? next.getAttribute(Columns.PSI_VLR).toString() : "";
        
          if (gps_descricao_anterior == null || !gps_descricao_anterior.equals(gps_descricao)) {
        %>
          <div class="legend">
             <%=TextHelper.forHtmlContent(gps_descricao)%>
          </div>
          <%
           }
          %>
          <div id="descricao" class="mb-2 mt-2">
            <span><%=TextHelper.forHtmlContent(tpc_descricao)%></span>
          </div>
            	<%=JspHelper.montaValor(tpc_codigo, tpc_dominio, TextHelper.forHtmlContent(psi_vlr), false, null, -1, -1, "form-control", null, null)%>
          <%  gps_descricao_anterior = gps_descricao;
        } 
        %>
        </div>
      </div>
  <% } %>
  <div class="row">
    <%
        if (!podeEditarConsignante || paramSist == null || paramSist.isEmpty()) {
    %>
    <div class="col-sm-12 mt-2 mb-2">
      <div id="actions" class="btn-action">
        <div class="float-end">
          <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back"
            onClick="postData('../v3/editarConsignante?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.botao.voltar" /></a>
        </div>
      </div>
    </div>
      <%
        }
      %>
  </div>
  <%if (exibeBotaoRodape) { %>
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
  <% } %>  
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
	f0 = document.forms[0];
	function formLoad() {
		focusFirstField();
	}
    
	window.onload = formLoad;
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
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>