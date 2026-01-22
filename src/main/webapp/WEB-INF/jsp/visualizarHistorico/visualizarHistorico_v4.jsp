<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.ParamSession"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.values.OperacaoHistoricoMargemEnum"%>
<%@ page import="com.zetra.econsig.dto.web.VisualizarHistoricoDTO"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
ParamSession paramSession = (ParamSession) request.getAttribute("paramSession");
boolean carregaAtributos = (boolean) request.getAttribute("carregaAtributos");
    boolean exigeCaptcha = false;
    boolean exibeCaptchaDeficiente = false;
    boolean exibeMargem = false;
    CustomTransferObject servidor = null;
    List margensServidor = null;
    List margens = null;
    String rseCodigo = null;
    List<VisualizarHistoricoDTO> visualizarHistoricoLst = null;
if(carregaAtributos) {
    servidor = (CustomTransferObject) request.getAttribute("servidor");
    margensServidor = (List) request.getAttribute("margensServidor");
    margens = (List) request.getAttribute("margens");
    rseCodigo = (String) request.getAttribute("rseCodigo");
    visualizarHistoricoLst = (List<VisualizarHistoricoDTO>) request.getAttribute("visualizarHistoricoLst");
} else {
    exibeMargem = request.getAttribute("exibeMargemTopo") != null && (boolean) request.getAttribute("exibeMargemTopo");
    exigeCaptcha = request.getAttribute("exigeCaptchaTopo") != null && (boolean) request.getAttribute("exigeCaptchaTopo");
    exibeCaptchaDeficiente = request.getAttribute("exibeCaptchaDeficiente") != null && (boolean) request.getAttribute("exibeCaptchaDeficiente");
}
    boolean exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");

String voltar = (String) request.getAttribute("destinoBotaoVoltar");
//Exibe Botao Rodapé
boolean exibeBotaoRodape = (boolean) (request.getAttribute("exibeBotaoRodape"));
%>
<c:set var="title">
<hl:message key="rotulo.historico.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <%if(carregaAtributos){%>
  <div class="row">
    <div class="col-sm-7">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.consultar.margem.resultado"/></h2>
        </div>
        <div class="card-body">
          <dl class= "row data-list firefox-print-fix">
            <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
            <% pageContext.setAttribute("servidor", servidor); %>
            <% pageContext.setAttribute("lstMargens", margensServidor); %>
            <hl:detalharServidorv4 name="servidor" margem="lstMargens"/>
            <%-- Fim dos dados da ADE --%>
          </dl>
        </div>
      </div>
    </div>
    <div class="col-sm-5">
      <form action="../v3/visualizarHistorico?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="formPesqAvancada">
        <div class="opcoes-avancadas">
          <a class="opcoes-avancadas-head collapsed" href="#faq1" data-bs-toggle="collapse" aria-expanded="false" aria-controls="faq1"><hl:message key="rotulo.botao.pesquisar"/></a>
          <div class="collapse" id="faq1">
            <div class="opcoes-avancadas-body">
               <div class="row form-group">
                  <label for="dataEvento"><hl:message key="rotulo.historico.margem.data.evento"/></label>
                   <div class="row mt-2" role="group" aria-labelledby="dataEvento">
                      <div class="form-check col-sm-1" >
                        <div class="float-left align-middle mt-4 form-control-label">
                          <label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
                        </div>
                      </div>
                     <div class="form-check col-sm-11">
                      <hl:htmlinput 
                            name="periodoIni" 
                            di="periodoIni" 
                            type="text" 
                            classe="form-control w-100 pr-0" 
                            placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" 
                            mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoIni"))%>" 
                            ariaLabel='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inclusao.ini", responsavel)%>'/>
                     </div>
                     <div class="form-check col-sm-1">
                       <div class="float-left align-middle mt-4 form-control-label">
                         <label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
                       </div>
                     </div>
                     <div class="form-check col-sm-11">
                     <hl:htmlinput 
                            name="periodoFim" 
                            di="periodoFim"  
                            type="text" 
                            classe="form-control w-100 pr-0" 
                            placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" 
                            mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoFim"))%>" 
                            ariaLabel='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inclusao.fim", responsavel)%>'/>
                      </div>
                    </div>
              </div>
              <div class="row form-group col-sm-12 p-0">
                  <label for="marCodigo"><hl:message key="rotulo.historico.margem.tipo.margem"/></label>
                  <%= JspHelper.geraCombo(margens, "marCodigo", Columns.MAR_CODIGO, Columns.MAR_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "marCodigo"), null, false, "form-control")%>                
              </div>
              <div class="row form-group col-sm-12 p-0">
                  <label for="hmrOperacao"><hl:message key="rotulo.historico.margem.operacao"/></label>
                  <%= JspHelper.geraCombo(OperacaoHistoricoMargemEnum.getListValues(), "hmrOperacao", "CODIGO", "DESCRICAO", ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "hmrOperacao"), null, false, "form-control") %>
              </div>
              <div class="row form-group col-sm-12 p-0">
                <label for="adeNumero"><hl:message key="rotulo.consignacao.numero"/></label>
                <hl:htmlinput name="adeNumero" 
                              di="adeNumero" 
                              type="text" 
                              classe="form-control w-100 pr-0"
                              mask="#D11" 
                              size="8"
                              placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero", responsavel)%>'
                              value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeNumero"))%>"/>
              </div>
              <div class="row form-group col-sm-12 p-0">
                <input name="RSE_CODIGO" di="RSE_CODIGO" type="hidden" class="form-control" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>"/>
              </div>
            </div>
          </div>
        </div>
          <div id="actions" class="btn-action">
            <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="pesquisar(); return false;"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
          </div>
      </form>  
    </div>
  
  </div>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.historico.margem.resultado.titulo"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col" width="10%"><hl:message key="rotulo.historico.margem.data.evento"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.historico.margem.tipo.margem"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.historico.margem.operacao"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.consignacao.numero"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.historico.margem.valor.margem.antes"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.historico.margem.valor.margem.depois"/></th>
            <th scope="col" width="5%"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>  
            <%=JspHelper.msgRstVazio(visualizarHistoricoLst.size()==0, 8, responsavel)%>       
            <%
	            for (int i = 0; i < visualizarHistoricoLst.size(); i++) {
	    			VisualizarHistoricoDTO dto = visualizarHistoricoLst.get(i);
            %>
                     <tr>
                       <td valign="top"><%=TextHelper.forHtmlContent(dto.getHmrData())%></td>
                       <td valign="top"><%=TextHelper.forHtmlContent(dto.getMarDescricao())%>&nbsp;</td>
                       <td valign="top"><%=TextHelper.forHtmlContentComTags(dto.getDescricao())%>&nbsp;</td>
                       <td valign="top"><%=TextHelper.forHtmlContent(dto.getAdeNumero())%>&nbsp;</td>
                       <td valign="top" align="right"><%=TextHelper.forHtmlContent(dto.getLabelTipoVlr())%>&nbsp;<%=TextHelper.forHtmlContent(dto.getAdeVlr())%>&nbsp;</td>
                       <td valign="top" align="right"><%=TextHelper.forHtmlContent(dto.getHmrMargemAntes())%>&nbsp;</td>
                       <td valign="top" align="right"><%=TextHelper.forHtmlContent(dto.getHmrMargemDepois())%>&nbsp;</td>
                       <td valign="top" align="center">
                         <% if (!dto.getAdeCodigo().equals("")) { %>
                           <a href="#no-back" onClick="doIt('hi', '<%=TextHelper.forJavaScript(dto.getAdeCodigo())%>');">
                             <hl:message key="rotulo.acoes.visualizar"/> 
                           </a>
                         <% } else if (OperacaoHistoricoMargemEnum.EDT_REGISTRO_SERVIDOR.getCodigo().equals(dto.getHmrOperacao())) { %>
                           <a href="#no-back" onClick="doIt('ors', '<%=TextHelper.forJavaScript(rseCodigo)%>');">
                             <hl:message key="rotulo.acoes.visualizar"/>
                           </a>
                         <% }else { %>
                             <hl:message key="rotulo.acoes.nao.disponivel"/>
                         <% }       %>
                       </td>
                     </tr>                     
            <%
               }
            %>
        </tbody>
        <tfoot>
          <tr>
           <td colspan="8"><%=ApplicationResourcesHelper.getMessage("rotulo.historico.margem.resultado.listagem", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
          </tr>
        </tfoot>
      </table>
    </div>
    <div class="card-footer">
       <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>
  </div>
    <%} else { %>
    <hl:modalCaptchaSer type="historico"/>
    <% } %>
  <div class="btn-action">
  <% if (!responsavel.isSer()){%>
    <a class="btn btn-outline-danger" href="#no-back" onClick="return postData('<%=TextHelper.forJavaScriptAttribute(voltar)%>');" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
  <%}else{ %>
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
  <%}%>
  </div>
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
<% if (exibeCaptchaAvancado) { %>
    <script src='https://www.google.com/recaptcha/api.js'></script>
<% } %>
  <script type="text/JavaScript">
   function doIt(opt, ade) {
       if (opt == 'hi') {
         postData('../v3/consultarConsignacao?acao=detalharConsignacao&tipo=historico&ADE_CODIGO=' + ade + '&<%=SynchronizerToken.generateToken4URL(request)%>');
       } else if (opt == 'ors') {
    	   postData('../v3/listarOcorrenciaRegistroServidor?acao=iniciar&RSE_CODIGO=' + ade + '&<%=SynchronizerToken.generateToken4URL(request)%>');
       }
     }
     function showHidePesquisar() {
       var pesquisar = document.getElementById('pesquisarBar');
       var image = document.getElementById('showPesquisarImage');
       if(pesquisar.style.display == 'none') {
         pesquisar.style.display = 'block';
         image.src = '../img/icones/minus.gif';
       } else {
         pesquisar.style.display = 'none';
         image.src = '../img/icones/plus_pesquisar.gif';
       }
     }
     function pesquisar() {
       with (document.formPesqAvancada) {
         submit();
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

        window.onload = () => {
            <%if(!carregaAtributos){ %>
            <% if (exibeCaptchaDeficiente) { %>
            montaCaptchaSomSer('historico');
            <% } %>
            $('#modalCaptcha_historico').modal('show');
            <% } %>
        }

    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
  