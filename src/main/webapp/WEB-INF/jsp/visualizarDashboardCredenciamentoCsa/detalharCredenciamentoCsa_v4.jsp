<%@page import="com.zetra.econsig.values.TipoArquivoEnum"%>
<%@page import="com.zetra.econsig.values.StatusCredenciamentoEnum"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl"   uri="/html-lib" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.persistence.entity.*" %>
<%@page import="com.zetra.econsig.helper.upload.UploadHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.dto.web.AcaoConsignacao" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

CredenciamentoCsa credenciamentoCsa = (CredenciamentoCsa) request.getAttribute("credenciamentoCsa");
Consignataria consignataria = (Consignataria) request.getAttribute("consignataria");
List<OcorrenciaCredenciamentoCsa> lstOcorrencia = (List<OcorrenciaCredenciamentoCsa>) request.getAttribute("lstOcorrencia");
List<AnexoCredenciamentoCsa> lstAnexo = (List<AnexoCredenciamentoCsa>) request.getAttribute("lstAnexo");

String scrCodigo = credenciamentoCsa.getScrCodigo();
String creCodigo = credenciamentoCsa.getCreCodigo();
File arquivoListaDoc = (File) request.getAttribute("arquivoListaDoc");
File arquivoTermoAditivo = (File) request.getAttribute("arquivoTermoAditivo");
List<TransferObject> tiposMotivoOperacao = (List<TransferObject>) request.getAttribute("tiposMotivoOperacao");
List<AnexoCredenciamentoCsa> arquivoTermoAditivoPreenchidos = (List<AnexoCredenciamentoCsa>) request.getAttribute("arquivoTermoAditivoPreenchidos");
List<AnexoCredenciamentoCsa> arquivoTermoAditivoAssinados = (List<AnexoCredenciamentoCsa>) request.getAttribute("arquivoTermoAditivoAssinado");
boolean permiteConcluirAssinatura = request.getAttribute("permiteConcluirAssinatura") != null ? (boolean) request.getAttribute("permiteConcluirAssinatura") : false;
List<TransferObject> lstModelotermoAditivo = (List<TransferObject>) request.getAttribute("lstModelotermoAditivo");

// Lógica necessário para o caso de uso de assinar termo com certificado digital
if((arquivoTermoAditivoPreenchidos == null || arquivoTermoAditivoPreenchidos.isEmpty()) && (arquivoTermoAditivoAssinados != null && !arquivoTermoAditivoAssinados.isEmpty())){
    arquivoTermoAditivoPreenchidos.addAll(arquivoTermoAditivoAssinados);
}
%>
<c:set var="title">
   <hl:message key="rotulo.dashboard.credenciamento.csa.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <%if(!lstAnexo.isEmpty() 
          || responsavel.isCsa() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo()) 
          || responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_VALIDACAO_DOCUMENTACAO_CSE.getCodigo())
          || responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_PREENCHIMENTO_TERMO_ADITIVO_CSE.getCodigo())
          || responsavel.isCsa() && (scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo()) || scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo()))
          || responsavel.isCseSup()  && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo())
          || responsavel.isCseSup()  && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo())) { %>
        <div class="page-title d-print-none">
          <div class="row">
            <div class="col-sm-12 col-md-12 mb-2">
              <div class="float-end">
                <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
                    <%if(!lstAnexo.isEmpty()){ %>
	                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/downloadArquivosCredenciamento?tipo=anexo_credenciamento&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&csaCodigo=<%=TextHelper.forJavaScriptAttribute(consignataria.getCsaCodigo())%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.download" />
	                    </a>
                    <%} %>
                    <%if(responsavel.isCsa() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo())){ %>
	                    <a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#enviarDocumentos">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.enviar.doc" />
	                    </a>
                    <%} %>
                    <%if(responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_VALIDACAO_DOCUMENTACAO_CSE.getCodigo())){ %>
	                    <a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#modalAprovar">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.aprovar.doc" />
	                    </a>
	                    <a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#modalReprovar">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.rejeitar.doc" />
	                    </a>
                    <%} %>
                    <%if(responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_PREENCHIMENTO_TERMO_ADITIVO_CSE.getCodigo())){ %>
	                    <a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#modalPreencherTermoAditivo">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.preencher.termo" />
	                    </a>
                    <%} %>
                    <%if(responsavel.isCsa() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())){ %>
	                    <a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#modalAssinarTermoAditivoCertificado">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.assinar.termo.certificado.digital" />
	                    </a>
	                    <a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#modalAssinarTermoAditivo">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.assinar.termo" />
	                    </a>
                    	<%if (permiteConcluirAssinatura){%>
                    		<%if (consignataria.getCsaAtivo().equals(CodedValues.STS_ATIVO)) { %>
	                    		<a class="dropdown-item" href="#no-back" onClick="postData('../v3/visualizarDashboardCredenciamento?acao=finalizarAssinaturaTermo&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&csaCodigo=<%=TextHelper.forJavaScriptAttribute(consignataria.getCsaCodigo())%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
		                        	<hl:message key="rotulo.dashboard.detalhar.credenciamento.finalizar.credenciamento" />
		                    	</a>
	                    	<%} else { %>
		                    	<a class="dropdown-item" href="#no-back" onClick="abrirModaisDesbloqueioCsa();">
		                        	<hl:message key="rotulo.dashboard.detalhar.credenciamento.finalizar.credenciamento" />
		                    	</a>
	                    	<%} %>
                    	<%}%>
                    <%} %>
                    <%if(responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo())){ %>
                    	<a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#modalAssinarTermoAditivoCertificado">
							<hl:message key="rotulo.dashboard.credenciamento.csa.opcao.assinar.termo.certificado.digital" />
	                    </a>
	                    <a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#modalAssinarTermoAditivoCse">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.assinar.termo" />
	                    </a>
	                    <a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#modalReprovarAssTermo">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.rejeitar.ass.termo" />
	                    </a>
	                    <%if (permiteConcluirAssinatura){%>
                    		<%if (consignataria.getCsaAtivo().equals(CodedValues.STS_ATIVO)) { %>
	                    		<a class="dropdown-item" href="#no-back" onClick="postData('../v3/visualizarDashboardCredenciamento?acao=finalizarAssinaturaTermo&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&csaCodigo=<%=TextHelper.forJavaScriptAttribute(consignataria.getCsaCodigo())%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
		                        	<hl:message key="rotulo.dashboard.detalhar.credenciamento.finalizar.credenciamento" />
		                    	</a>
	                    	<%} else { %>
		                    	<a class="dropdown-item" href="#no-back" onClick="abrirModaisDesbloqueioCsa();">
		                        	<hl:message key="rotulo.dashboard.detalhar.credenciamento.finalizar.credenciamento" />
		                    	</a>
	                    	<%} %>
                    	<%}%>
                    <%} %>
                    <%if(responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo()) && responsavel.temPermissao(CodedValues.FUN_APROVAR_TERMO_ADITIVO_CSA)){ %>
						<a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#modalAssinarTermoAditivoCertificado">
							<hl:message key="rotulo.dashboard.credenciamento.csa.opcao.aprovar.termo.digitalmente" />
	                    </a>
	                    <a class="dropdown-item" href="#no-back" data-bs-toggle="modal" data-bs-target="#modalFinalizarTermoAditivoCse">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.aprovar.termo" />
	                    </a>
	                    <%if (permiteConcluirAssinatura){%>
	                    	<%if (consignataria.getCsaAtivo().equals(CodedValues.STS_ATIVO)) { %>
	                    		<a class="dropdown-item" href="#no-back" onClick="postData('../v3/visualizarDashboardCredenciamento?acao=finalizarAssinaturaTermo&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&csaCodigo=<%=TextHelper.forJavaScriptAttribute(consignataria.getCsaCodigo())%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
		                        	<hl:message key="rotulo.dashboard.detalhar.credenciamento.finalizar.credenciamento" />
		                    	</a>
	                    	<%} else { %>
		                    	<a class="dropdown-item" href="#no-back" onClick="abrirModaisDesbloqueioCsa();">
		                        	<hl:message key="rotulo.dashboard.detalhar.credenciamento.finalizar.credenciamento" />
		                    	</a>
	                    	<%} %>
                    	<%}%>
                    <%} %>
                    <% if (scrCodigo.equals(StatusCredenciamentoEnum.FINALIZADO.getCodigo())) { %>
	                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/downloadArquivosCredenciamento?tipo=anexo_credenciamento&termoAditivo=S&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&csaCodigo=<%=TextHelper.forJavaScriptAttribute(consignataria.getCsaCodigo())%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
	                        <hl:message key="rotulo.dashboard.credenciamento.csa.opcao.download.termo" />
	                    </a>
                    <% } %>
              </div>
            </div>
          </div>
        </div>
       </div>
 <%} %>
	<div class="row">
		<div class="col-sm-6">
		    <div class="card">
		        <div class="card-header">
		        	<h2 class="card-header-title"><hl:message key="rotulo.dashboard.detalhar.credenciamento.dados.consignataria"/></h2>
		        </div>
		        <div class="card-body">
		            <dl class="row data-list firefox-print-fix ">	
		                <dt class="col-6"><hl:message key="rotulo.dashboard.detalhar.credenciamento.consignataria"/></dt>
		                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaNome()) ? consignataria.getCsaNome() : "- -" )%></dd>
		                <dt class="col-6"><hl:message key="rotulo.dashboard.detalhar.credenciamento.identificador"/></dt>
		                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaIdentificador()) ? consignataria.getCsaIdentificador() : "- -" )%></dd>
		                <dt class="col-6"><hl:message key="rotulo.dashboard.detalhar.credenciamento.email"/></dt>
		                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaEmail()) ? consignataria.getCsaEmail() : " " )%></dd>
		                <dt class="col-6"><hl:message key="rotulo.dashboard.detalhar.credenciamento.telefone"/></dt>
						<dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaTel()) ? consignataria.getCsaTel() : "- -" )%></dd>
		                <dt class="col-6"><hl:message key="rotulo.dashboard.detalhar.credenciamento.responsavel"/></dt>
		                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaResponsavel()) ? consignataria.getCsaResponsavel() : "- -" )%></dd>
		                <dt class="col-6"><hl:message key="rotulo.dashboard.detalhar.credenciamento.telefone.responsavel"/></dt>
		                <dd class="col-6"><%=TextHelper.forHtmlContent(!TextHelper.isNull(consignataria.getCsaRespTelefone()) ? consignataria.getCsaRespTelefone() : "- -" )%></dd>
					<% if (!TextHelper.isNull(consignataria.getCsaResponsavel2())  && !TextHelper.isNull(consignataria.getCsaRespTelefone2())) { %>
		                <dt class="col-6"><hl:message key="rotulo.dashboard.detalhar.credenciamento.segundo.responsavel"/></dt>
		                <dd class="col-6"><%= TextHelper.forHtmlContent(consignataria.getCsaResponsavel2()) %></dd>
		                <dt class="col-6"><hl:message key="rotulo.dashboard.detalhar.credenciamento.segundo.telefone.responsavel"/></dt>
		                <dd class="col-6"><%= TextHelper.forHtmlContent(consignataria.getCsaRespTelefone2()) %></dd>
					<% } if (!TextHelper.isNull(consignataria.getCsaResponsavel3())  && !TextHelper.isNull(consignataria.getCsaRespTelefone3())) { %>
		                <dt class="col-6"><hl:message key="rotulo.dashboard.detalhar.credenciamento.terceiro.responsavel"/></dt>
		                <dd class="col-6"><%= TextHelper.forHtmlContent(consignataria.getCsaResponsavel3()) %></dd>
		                <dt class="col-6"><hl:message key="rotulo.dashboard.detalhar.credenciamento.terceiro.telefone.responsavel"/></dt>
		                <dd class="col-6"><%= TextHelper.forHtmlContent(consignataria.getCsaRespTelefone3()) %></dd>
					<% } %>
		            </dl>
		        </div>
		    </div>
		</div>
		<div class="col-sm-6">
		    <div class="card">
	        <div class="card-header">
	        	<h2 class="card-header-title"><hl:message key="rotulo.dashboard.detalhar.credenciamento.dados.credenciamento"/></h2>
	        </div>
	        <div class="card-body">
	            <dl class="row data-list firefox-print-fix">
                <dt class="col-6 "><hl:message key="rotulo.dashboard.detalhar.situacao.credenciamento"/></dt>
                <dd class="col-6"><%= TextHelper.forHtmlContent(credenciamentoCsa.getStatusCredenciamento().getScrDescricao()) %></dd>
                <dt class="col-6 "><hl:message key="rotulo.dashboard.detalhar.data.inicio.credenciamento"/></dt>
                <dd class="col-6"><%= TextHelper.forHtmlContent(DateHelper.format(credenciamentoCsa.getCreDataIni(), LocaleHelper.getDateTimePattern())) %></dd>
	            </dl>
	        </div>
	    </div>
		</div>
	</div>
  <% if(!lstOcorrencia.isEmpty() && !lstAnexo.isEmpty()){ %>
	<div class="card">
		<ul class="nav nav-tabs responsive-tabs" id="credenciamentoInfo" role="tablist">
	    <li class="nav-item">
	    	<a class="nav-link active" id="anexo-tab" data-bs-toggle="tab" href="#anexo" role="tab" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.dashboard.detalhar.titulo.historico.ocorrencias"/></a>
	    </li>
	  	<li class="nav-item">
	      <a class="nav-link" id="dadosCredenciamento-tab" data-bs-toggle="tab" href="#dadosCredenciamento" role="tab" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.dashboard.detalhar.titulo.historico.anexos"/></a>
	    </li>
	  </ul>   
	    <%-- Tab panes --%>
	  <div class="tab-content table-responsive" id="credenciamentoInfo">
	 		<div class="tab-pane fade show active" id="anexo" role="tabpanel" aria-labelledby="anexo-tab">
				<table class="table table-striped table-hover">
	        <thead>
	          <tr>
	            <th scope="col"><hl:message key="rotulo.dashboard.detalhar.data"/></th>
	            <th scope="col"><hl:message key="rotulo.dashboard.detalhar.responsavel"/></th>
	            <th scope="col"><hl:message key="rotulo.dashboard.detalhar.tipo"/></th>
	            <th scope="col"><hl:message key="rotulo.dashboard.detalhar.descricao"/></th>
	            <th scope="col"><hl:message key="rotulo.dashboard.detalhar.acesso"/></th>
	          </tr>
	        </thead>
	        <tbody>
	        <%
	        for(OcorrenciaCredenciamentoCsa ocorrencia : lstOcorrencia) { 
	            String ocdData = DateHelper.toDateTimeString(ocorrencia.getOcdData());
	            String ocdResponsavel = ocorrencia.getUsuario().getUsuNome() != null ? ocorrencia.getUsuario().getUsuNome() : "-";
	            String ocdTipo = ocorrencia.getTipoOcorrencia().getTocDescricao() != null ? ocorrencia.getTipoOcorrencia().getTocDescricao() : "-";
	            String ocdObs = ocorrencia.getOcdObs() != null ? ocorrencia.getOcdObs() : "-";
	            String oseIpAcesso = ocorrencia.getOcdIpAcesso() != null ? ocorrencia.getOcdIpAcesso() : "-";
	        %>
	          <tr>
	            <td><%=TextHelper.forHtmlContent(ocdData)%></td>
	            <td><%=TextHelper.forHtmlContent(ocdResponsavel)%></td>
	            <td><%=TextHelper.forHtmlContent(ocdTipo)%></td>
	            <td><%=TextHelper.forHtmlContent(ocdObs)%></td>
	            <td><%=TextHelper.forHtmlContent(oseIpAcesso)%></td>
	          </tr>
	  <%  } %>
	        </tbody>
	        <tfoot>
	          <tr>
	            <td colspan="5">
	              <span class="font-italic"> 
	                <hl:message key="rotulo.dashboard.detalhar.paginacao.ocorrencias"/>
	              </span>
	            </td>
	          </tr>
	        </tfoot>
	      </table>
	    </div>
		  <div class="tab-pane fade" id="dadosCredenciamento" role="tabpanel" aria-labelledby="dadosCredenciamento-tab">
		  	<table class="table table-striped table-hover">
	        <thead>
	          <tr>
	            <th scope="col"><hl:message key="rotulo.dashboard.detalhar.data"/></th>
	            <th scope="col"><hl:message key="rotulo.dashboard.detalhar.responsavel"/></th>
	            <th scope="col"><hl:message key="rotulo.dashboard.detalhar.nome.arquivo"/></th>
	            <th scope="col"><hl:message key="rotulo.dashboard.detalhar.tipo"/></th>
	            <th scope="col"><hl:message key="rotulo.dashboard.detalhar.acesso"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
	          </tr>
	        </thead>
	        <tbody>
	        <%
	        for(AnexoCredenciamentoCsa anexo : lstAnexo) { 
                String ancCodigo = anexo.getAncCodigo();
	            String ancData = anexo.getAncData() != null ? DateHelper.toDateTimeString(anexo.getAncData()) : "-";
	            String ancResponsavel = anexo.getUsuario().getUsuNome() != null ? anexo.getUsuario().getUsuNome() : "-";
	            String ancNome = anexo.getAncNome() != null ? anexo.getAncNome() : "-";
	            String ancTipo = anexo.getTipoArquivo().getTarDescricao() != null ? anexo.getTipoArquivo().getTarDescricao() : "-";
	            String ancIpAcesso = anexo.getAncIpAcesso() != null ? anexo.getAncIpAcesso() : "-";
                String tarCodigo = anexo.getTarCodigo();
	        %>
	          <tr>
	            <td><%=TextHelper.forHtmlContent(ancData)%></td>
	            <td><%=TextHelper.forHtmlContent(ancResponsavel)%></td>
	            <td><%=TextHelper.forHtmlContent(ancNome)%></td>
	            <td><%=TextHelper.forHtmlContent(ancTipo)%></td>
	            <td><%=TextHelper.forHtmlContent(ancIpAcesso)%></td>
                <td>
                 <div class="actions">
                   <a class="ico-action" href="#">
                     <div class="form-inline" <%=tarCodigo.equals(TipoArquivoEnum.ARQUIVO_ANEXO_CREDENCIAMENTO_DOC_CSA.getCodigo()) ? "onClick=\"javascript:fazDownloadArqHisAnexo('" + TextHelper.forJavaScript(ancCodigo) + ".zip" + "');\"" : "onClick=\"javascript:fazDownloadArqHisAnexo('"+TextHelper.forJavaScript(ancNome)+"');\"" %>>
                       <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key="rotulo.botao.download"/> <%=ancNome%>" title="" data-original-title="download">
                         <svg> <use xlink:href="../img/sprite.svg#i-download"></use></svg>
                       </span>
                       <hl:message key="rotulo.acoes.upload.arquivo.download"/>
                     </div>
                   </a>
                 </div>
               </td>
	          </tr>
	 			<%  } %>
	        </tbody>
	        <tfoot>
	          <tr>
	            <td colspan="5">
	              <span class="font-italic"> 
	                <hl:message key="rotulo.dashboard.detalhar.paginacao.anexos"/>
	              </span>
	            </td>
	          </tr>
	        </tfoot>
	      </table>
		  </div>
		 </div>
		</div>
    <% } %>	 
	<div class="float-end">
	  <div class="btn-action">
	      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
	  </div>
	</div>
  <%if(responsavel.isCsa() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo())){ %>
        <!-- Modal EnviarDocumentos -->
        <div class="modal fade" id="enviarDocumentos" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
          <form name="form1" id="form1" method="POST" action="../v3/visualizarDashboardCredenciamento?acao=uploadArquivoCredenciamentoCsa&creCodigo=<%=creCodigo%>&csaCodigo=<%=consignataria.getCsaCodigo()%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" enctype="multipart/form-data">
            <div class="modal-dialog modal-dialog-width" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.dashboard.credenciamento.acao.enviar.doc.csa"/></h5>
                  <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                  <span aria-hidden="true"></span>
                  </button>
                </div>
                <div class="form-group modal-body m-0">
                   <hl:fileUploadV4 obrigatorio="<%=true%>" mostraCampoDescricao="<%=false%>" nomeCampoArquivo="FILE1" multiplo="true" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_DOCUMENTOS_CREDENCIAMENTO%>" tipoArquivo="anexo_credenciamento"/>
                </div>
                <% if (!TextHelper.isNull(arquivoListaDoc)) { %>
                 <div class="col-md-12">             
                   <div class="card">
                      <div class="card-header hasIcon pl-3">
                        <h2 class="card-header-title"><hl:message key="rotulo.dashboard.titulo.table.modal.credenciamento"/></h2>
                      </div>
                      <div class="card-body table-responsive p-0">
                        <div class="table-responsive">
                        <table class="table table-striped table-hover">
                           <thead>
                              <tr>
                                <th scope="col"><hl:message key="rotulo.dashboard.nome"/></th>
                                <th scope="col"><hl:message key="rotulo.acoes"/></th>
                              </tr>
                           </thead>
                           <tbody>                      
                             <%     
                                 String nome_arquivo = arquivoListaDoc.getName();
                             %>
                               <tr>  
                                 <td><%=TextHelper.forHtmlContent(nome_arquivo)%></td>
                                 <td>
                                   <div class="actions">
                                     <a class="ico-action" href="#">
                                       <div class="form-inline" onClick="javascript:fazDownload('<%=TextHelper.forJavaScript(nome_arquivo)%>');">
                                         <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key="rotulo.botao.download"/> <%=nome_arquivo%>" title="" data-original-title="download">
                                           <svg> <use xlink:href="../img/sprite.svg#i-download"></use></svg>
                                         </span>
                                         <hl:message key="rotulo.acoes.upload.arquivo.download"/>
                                       </div>
                                     </a>
                                   </div>
                                 </td>
                               </tr> 
                           </tbody>
                           <tfoot>
                             <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.arquivos.credenciamento", responsavel)%></td></tr>
                           </tfoot>
                        </table>
                        </div> 
                      </div>
                   </div>
                </div>
              <% } %>
                <div class="modal-footer pt-0">
                  <div class="btn-action mt-2 mb-0">
                    <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></a>
                    <input hidden="true" id="aad_nome" value="">
                    <input hidden="true" id="aad_descricao" value="">
                    <input hidden="true" id="posicao" value="0">
                    <a class="btn btn-primary" id="botaoConfirmarUpload" href="#" onclick="if(vf_upload_arquivos()){document.form1.submit();} return false;"><hl:message key="rotulo.botao.confirmar" /></a>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
  <%} %>
  <%if(responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_VALIDACAO_DOCUMENTACAO_CSE.getCodigo())){ %>
      <!-- Modal aprovar -->
      <div class="modal fade" id="modalAprovar" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-body">
              <hl:message key="mensagem.credenciamento.aprovacao.documentacao"/>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-outline-danger ml-auto" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></button>
              <button type="button" class="btn btn-primary mr-auto" onClick="postData('../v3/visualizarDashboardCredenciamento?acao=aprovarCredenciamentoCsa&creCodigo=<%=creCodigo%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.dashboard.credenciamento.aprovar"/></button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Modal Reprovar -->
      <div class="modal fade" id="modalReprovar" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <form name="form2" id="form2" method="POST" action="../v3/visualizarDashboardCredenciamento?acao=reprovarCredenciamentoCsa&creCodigo=<%=creCodigo%>&_skip_history_=true=true&<%=SynchronizerToken.generateToken4URL(request)%>">
          <div class="modal-dialog modalTermoUso" role="document">
            <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="mensagem.credenciamento.reprovacao.titulo"/></h5>
                  <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                  <span aria-hidden="true"></span>
                  </button>
                </div>
              <div class="modal-body">
                 <% if (tiposMotivoOperacao != null && !tiposMotivoOperacao.isEmpty()) { %>	
                   <div class="row">                    
                      <div class="form-group col-sm">
                        <label for="tmoCodigo"><hl:message key="rotulo.motivo.singular"/></label>
                        <select class="form-control" id="tmoCodigo" name="tmoCodigo">
                          <option value=""><hl:message key="rotulo.campo.selecione"/>	</option>
                          <%for (TransferObject tipoMotivoTO: tiposMotivoOperacao) { %>
                              <option value="<%=(String) tipoMotivoTO.getAttribute(Columns.TMO_CODIGO)%>"><%=(String) tipoMotivoTO.getAttribute(Columns.TMO_DESCRICAO)%></option>                      
                          <%} %>
                        </select>
                      </div>
                   </div>
                 <% } %>
                 <div class="row">
                  <div class="form-group col-sm">
                    <label for="OCC_OBS"><hl:message key="rotulo.efetiva.acao.consignacao.dados.observacao"/></label>
                    <textarea class="form-control" 
                              placeholder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs", responsavel)%>'
                              id="OCC_OBS" 
                              name="OCC_OBS" 
                              rows="6"></textarea>
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></button>
                <button type="button" class="btn btn-primary" onClick="if(validarReprovacao()){document.form2.submit();} return false;"><hl:message key="rotulo.dashboard.credenciamento.reprovar"/></button>
              </div>
            </div>
          </div>
        </form>
      </div>
  <%} %>
  
  <%if(responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_PREENCHIMENTO_TERMO_ADITIVO_CSE.getCodigo())){ %>
        <!-- Modal Preencher Termo Aditivo -->
        <div class="modal fade" id="modalPreencherTermoAditivo" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
            <div class="modal-dialog modal-dialog-width" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title about-title" id="modalTitulo"><hl:message key="rotulo.dashboard.credenciamento.acao.preencher.termo.aditivo"/></h5>
                  <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                  <span aria-hidden="true"></span>
                  </button>
                </div>
                 <div class="col-md-12">             
                   <div class="card">
                      <div class="card-header">
                        <h2 class="card-header-title"><hl:message key="rotulo.dashboard.titulo.table.modal.termo.aditivo"/></h2>
                      </div>
                      <div class="card-body table-responsive p-0">
                        <div class="table-responsive">
                        <table class="table table-striped table-hover" id="tabelaPreencherTermo">
                           <thead>
                              <tr>
                                <th scope="col"><hl:message key="rotulo.dashboard.titulo.table.modal.titulo.termo.aditivo"/></th>
                                <th scope="col"><hl:message key="rotulo.acoes"/></th>
                              </tr>
                           </thead>
                           <tbody>                      
                             <%     
                                 for (TransferObject motivoTermoAditivo : lstModelotermoAditivo) {
                                     String codMotivoTermoAditivo = (String) motivoTermoAditivo.getAttribute(Columns.MTA_CODIGO);
                                     String tituloMotivoTermoAditivo = (String) motivoTermoAditivo.getAttribute(Columns.MTA_DESCRICAO);
                             %>
	                               <tr class="selecionarLinha">
	                                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(tituloMotivoTermoAditivo)%></td>
	                                 <td class="acoes">
			                            <div class="actions">
			                            	<div class="dropdown">
			                            		<a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
							                        <div class="form-inline">
							                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
							                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
							                          </span> <hl:message key="rotulo.botao.opcoes"/>
							                        </div>
							                    </a>
	                   	                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
					                                <a href="#" class="dropdown-item" onClick="postData('../v3/visualizarDashboardCredenciamento?acao=preVisualizarTermoCredenciamentoCsa&visualizar=true&mtaCodigo=<%=codMotivoTermoAditivo%>&csaCodigo=<%=consignataria.getCsaCodigo()%>&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.visualizar"/></a>
					                                <a href="#" class="dropdown-item" onClick="postData('../v3/visualizarDashboardCredenciamento?acao=preencherTermoCredenciamentoCsa&mtaCodigo=<%=codMotivoTermoAditivo%>&creCodigo=<%=creCodigo%>&csaCodigo=<%=consignataria.getCsaCodigo()%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.dashboard.credenciamento.acao.preencher.termo.aditivo"/></a>
	                   	                        </div>
                   	                        </div>
			                            </div>
			                        </td>
	                               </tr> 
                             <%	  } %>
                           </tbody>
                           <tfoot>
                             <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.arquivos.credenciamento", responsavel)%></td></tr>
                           </tfoot>
                        </table>
                        </div> 
                      </div>
                   </div>
                </div>
                <div class="modal-footer pt-0">
                  <div class="btn-action mt-2 mb-0">
                    <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></a>
                  </div>
                </div>
              </div>
            </div>
        </div>
  <%} %>
  <%if(responsavel.isCsa() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())){ %>
  <!-- Modal Preencher Assinar Termo Aditivo CSA -->
        <div class="modal fade" id="modalAssinarTermoAditivo" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
          <form name="form4" id="form4" method="POST" action="../v3/visualizarDashboardCredenciamento?acao=assinarTermoCredenciamentoCsa&creCodigo=<%=creCodigo%>&csaCodigo=<%=consignataria.getCsaCodigo()%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" enctype="multipart/form-data">
            <div class="modal-dialog modal-dialog-width" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.dashboard.credenciamento.acao.ass.termo.aditivo.csa"/></h5>
                  <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                  <span aria-hidden="true"></span>
                  </button>
                </div>
                <div class="form-group modal-body m-0">
                   <hl:fileUploadV4 obrigatorio="<%=true%>" mostraCampoDescricao="<%=false%>" nomeCampoArquivo="FILE1" multiplo="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_DOCUMENTOS_CREDENCIAMENTO%>" tipoArquivo="anexo_credenciamento"/>
                </div>
                <% if (!arquivoTermoAditivoPreenchidos.isEmpty()) { %>
                 <div class="col-md-12">             
                   <div class="card">
                      <div class="card-header hasIcon pl-3">
                        <h2 class="card-header-title"><hl:message key="rotulo.dashboard.titulo.table.modal.credenciamento"/></h2>
                      </div>
                      <div class="card-body table-responsive p-0">
                        <div class="table-responsive">
                        <table class="table table-striped table-hover">
                           <thead>
                              <tr>
                                <th scope="col"><hl:message key="rotulo.dashboard.nome"/></th>
                                <th scope="col"><hl:message key="rotulo.acoes"/></th>
                              </tr>
                           </thead>
                           <tbody>                      
                             <%for(AnexoCredenciamentoCsa arquivoTermoAditivoPreenchido : arquivoTermoAditivoPreenchidos){     
                                 String nome_arquivo = arquivoTermoAditivoPreenchido.getAncNome();
                             %>
                               <tr>  
                                 <td><%=TextHelper.forHtmlContent(nome_arquivo)%></td>
                                 <td>
                                   <div class="actions">
                                     <a class="ico-action" href="#">
                                       <div class="form-inline" onClick="javascript:fazDownload('<%=TextHelper.forJavaScript(nome_arquivo)%>');">
                                         <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key="rotulo.botao.download"/> <%=nome_arquivo%>" title="" data-original-title="download">
                                           <svg> <use xlink:href="../img/sprite.svg#i-download"></use></svg>
                                         </span>
                                         <hl:message key="rotulo.acoes.upload.arquivo.download"/>
                                       </div>
                                     </a>
                                   </div>
                                 </td>
                               </tr> 
                             <%} %>
                           </tbody>
                           <tfoot>
                             <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.arquivos.credenciamento", responsavel)%></td></tr>
                           </tfoot>
                        </table>
                        </div> 
                      </div>
                   </div>
                </div>
              <% } %>
                <div class="modal-footer pt-0">
                  <div class="btn-action mt-2 mb-0">
                    <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></a>
                    <input hidden="true" id="aad_nome" value="">
                    <input hidden="true" id="aad_descricao" value="">
                    <input hidden="true" id="posicao" value="0">
                    <a class="btn btn-primary" id="botaoConfirmarUpload" href="#" onclick="if(vf_upload_arquivos()){document.form4.submit();} return false;"><hl:message key="rotulo.botao.confirmar" /></a>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
  <%} %>
  
  <%if(responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo())){ %>
        <!-- Modal Preencher Assinar Termo Aditivo CSE -->
        <div class="modal fade" id="modalAssinarTermoAditivoCse" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
          <form name="form5" id="form5" method="POST" action="../v3/visualizarDashboardCredenciamento?acao=assinarTermoCseCredenciamentoCsa&creCodigo=<%=creCodigo%>&csaCodigo=<%=consignataria.getCsaCodigo()%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" enctype="multipart/form-data">
            <div class="modal-dialog modal-dialog-width" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.dashboard.credenciamento.acao.ass.termo.aditivo.cse"/></h5>
                  <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                  <span aria-hidden="true"></span>
                  </button>
                </div>
                <div class="form-group modal-body m-0">
                   <hl:fileUploadV4 obrigatorio="<%=true%>" mostraCampoDescricao="<%=false%>" nomeCampoArquivo="FILE1" multiplo="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_DOCUMENTOS_CREDENCIAMENTO%>" tipoArquivo="anexo_credenciamento"/>
                </div>
                <% if (!arquivoTermoAditivoAssinados.isEmpty()) { %>
                 <div class="col-md-12">             
                   <div class="card">
                      <div class="card-header hasIcon pl-3">
                        <h2 class="card-header-title"><hl:message key="rotulo.dashboard.titulo.table.modal.credenciamento"/></h2>
                      </div>
                      <div class="card-body table-responsive p-0">
                        <div class="table-responsive">
                        <table class="table table-striped table-hover">
                           <thead>
                              <tr>
                                <th scope="col"><hl:message key="rotulo.dashboard.nome"/></th>
                                <th scope="col"><hl:message key="rotulo.acoes"/></th>
                              </tr>
                           </thead>
                           <tbody>                      
                             <%for(AnexoCredenciamentoCsa arquivoTermoAditivoAssinado : arquivoTermoAditivoAssinados){     
                                 String nome_arquivo = arquivoTermoAditivoAssinado.getAncNome();
                             %>
                               <tr>  
                                 <td><%=TextHelper.forHtmlContent(nome_arquivo)%></td>
                                 <td>
                                   <div class="actions">
                                     <a class="ico-action" href="#">
                                       <div class="form-inline" onClick="javascript:fazDownload('<%=TextHelper.forJavaScript(nome_arquivo)%>');">
                                         <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key="rotulo.botao.download"/> <%=nome_arquivo%>" title="" data-original-title="download">
                                           <svg> <use xlink:href="../img/sprite.svg#i-download"></use></svg>
                                         </span>
                                         <hl:message key="rotulo.acoes.upload.arquivo.download"/>
                                       </div>
                                     </a>
                                   </div>
                                 </td>
                               </tr> 
                             <%} %>
                           </tbody>
                           <tfoot>
                             <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.arquivos.credenciamento", responsavel)%></td></tr>
                           </tfoot>
                        </table>
                        </div> 
                      </div>
                   </div>
                </div>
              <% } %>
                <div class="modal-footer pt-0">
                  <div class="btn-action mt-2 mb-0">
                    <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></a>
                    <input hidden="true" id="aad_nome" value="">
                    <input hidden="true" id="aad_descricao" value="">
                    <input hidden="true" id="posicao" value="0">
                    <a class="btn btn-primary" id="botaoConfirmarUpload" href="#" onclick="if(vf_upload_arquivos()){document.form5.submit();} return false;"><hl:message key="rotulo.botao.confirmar" /></a>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
      <!-- Modal Reprovar Assinatura Termo -->
      <div class="modal fade" id="modalReprovarAssTermo" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <form name="form6" id="form6" method="POST" action="../v3/visualizarDashboardCredenciamento?acao=reprovarCredenciamentoCsa&reprovarAssTermo=true&creCodigo=<%=creCodigo%>&_skip_history_=true=true&<%=SynchronizerToken.generateToken4URL(request)%>">
          <div class="modal-dialog modalTermoUso" role="document">
            <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.dashboard.credenciamento.csa.opcao.rejeitar.ass.termo"/></h5>
                  <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                  <span aria-hidden="true"></span>
                  </button>
                </div>
              <div class="modal-body">
                 <% if (tiposMotivoOperacao != null && !tiposMotivoOperacao.isEmpty()) { %> 
                   <div class="row">                    
                      <div class="form-group col-sm">
                        <label for="tmoCodigo"><hl:message key="rotulo.motivo.singular"/></label>
                        <select class="form-control" id="tmoCodigo" name="tmoCodigo">
                          <option value=""><hl:message key="rotulo.campo.selecione"/> </option>
                          <%for (TransferObject tipoMotivoTO: tiposMotivoOperacao) { %>
                              <option value="<%=(String) tipoMotivoTO.getAttribute(Columns.TMO_CODIGO)%>"><%=(String) tipoMotivoTO.getAttribute(Columns.TMO_DESCRICAO)%></option>                      
                          <%} %>
                        </select>
                      </div>
                   </div>
                 <% } %>
                 <div class="row">
                  <div class="form-group col-sm">
                    <label for="OCC_OBS"><hl:message key="rotulo.efetiva.acao.consignacao.dados.observacao"/></label>
                    <textarea class="form-control" 
                              placeholder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs", responsavel)%>'
                              id="OCC_OBS" 
                              name="OCC_OBS" 
                              rows="6"></textarea>
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></button>
                <button type="button" class="btn btn-primary" onClick="if(validarReprovacao()){document.form6.submit();} return false;"><hl:message key="rotulo.dashboard.credenciamento.reprovar"/></button>
              </div>
            </div>
          </div>
        </form>
      </div>
  <%} %>
  
  <%if(responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo()) && responsavel.temPermissao(CodedValues.FUN_APROVAR_TERMO_ADITIVO_CSA)){ %>
      <!-- Modal Finalizar credenciamento -->
        <div class="modal fade" id="modalFinalizarTermoAditivoCse" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
          <form name="form7" id="form7" method="POST" action="../v3/visualizarDashboardCredenciamento?acao=finalizarCredenciamentoCsa&creCodigo=<%=creCodigo%>&csaCodigo=<%=consignataria.getCsaCodigo()%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" enctype="multipart/form-data">
            <div class="modal-dialog modal-dialog-width" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.dashboard.credenciamento.csa.opcao.aprovar.termo"/></h5>
                  <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                  <span aria-hidden="true"></span>
                  </button>
                </div>
                <div class="form-group modal-body m-0">
                   <hl:fileUploadV4 obrigatorio="<%=true%>" mostraCampoDescricao="<%=false%>" nomeCampoArquivo="FILE1" multiplo="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_DOCUMENTOS_CREDENCIAMENTO%>" tipoArquivo="anexo_credenciamento"/>
                </div>
                <% if (!arquivoTermoAditivoAssinados.isEmpty()) { %>
                 <div class="col-md-12">             
                   <div class="card">
                      <div class="card-header hasIcon pl-3">
                        <h2 class="card-header-title"><hl:message key="rotulo.dashboard.titulo.table.modal.credenciamento"/></h2>
                      </div>
                      <div class="card-body table-responsive p-0">
                        <div class="table-responsive">
                        <table class="table table-striped table-hover">
                           <thead>
                              <tr>
                                <th scope="col"><hl:message key="rotulo.dashboard.nome"/></th>
                                <th scope="col"><hl:message key="rotulo.acoes"/></th>
                              </tr>
                           </thead>
                           <tbody>                      
                             <%for(AnexoCredenciamentoCsa arquivoTermoAditivoAssinado : arquivoTermoAditivoAssinados){     
                                 String nome_arquivo = arquivoTermoAditivoAssinado.getAncNome();
                             %>
                               <tr>  
                                 <td><%=TextHelper.forHtmlContent(nome_arquivo)%></td>
                                 <td>
                                   <div class="actions">
                                     <a class="ico-action" href="#">
                                       <div class="form-inline" onClick="javascript:fazDownload('<%=TextHelper.forJavaScript(nome_arquivo)%>');">
                                         <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key="rotulo.botao.download"/> <%=nome_arquivo%>" title="" data-original-title="download">
                                           <svg> <use xlink:href="../img/sprite.svg#i-download"></use></svg>
                                         </span>
                                         <hl:message key="rotulo.acoes.upload.arquivo.download"/>
                                       </div>
                                     </a>
                                   </div>
                                 </td>
                               </tr> 
                             <%} %>
                           </tbody>
                           <tfoot>
                             <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.arquivos.credenciamento", responsavel)%></td></tr>
                           </tfoot>
                        </table>
                        </div> 
                      </div>
                   </div>
                </div>
              <% } %>
                <div class="modal-footer pt-0">
                  <div class="btn-action mt-2 mb-0">
                    <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></a>
                    <input hidden="true" id="aad_nome" value="">
                    <input hidden="true" id="aad_descricao" value="">
                    <input hidden="true" id="posicao" value="0">
                    <a class="btn btn-primary" id="botaoConfirmarUpload" href="#" onclick="if(vf_upload_arquivos()){document.form7.submit();} return false;"><hl:message key="rotulo.botao.confirmar" /></a>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
  <%} %>

	<%if((responsavel.isCsa() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())) || 
	        (responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo())) ||
	        (responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo()) && responsavel.temPermissao(CodedValues.FUN_APROVAR_TERMO_ADITIVO_CSA))){ %>
	     <!-- Modal Preencher Assinar Termo Aditivo CSA -->
        <div class="modal fade" id="modalAssinarTermoAditivoCertificado" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
          <form name="formCertificado" id="formCertificado" method="POST" action="../v3/visualizarDashboardCredenciamento?acao=assinarTermoCertificado&creCodigo=<%=creCodigo%>&csaCodigo=<%=consignataria.getCsaCodigo()%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>">
            <div class="modal-dialog modal-dialog-width" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.dashboard.credenciamento.acao.ass.termo.aditivo.csa.certificado.digital"/></h5>
                  <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                  <span aria-hidden="true"></span>
                  </button>
                </div>
                <div class="alert alert-warning" role="alert">
			      <hl:message key="rotulo.dashboard.credenciamento.acao.ass.termo.aditivo.serpro"/>
			    </div>
			    <div class="alert alert-warning js-server-authorization" role="alert">
			      <a href="#" onClick="autorizarNavegador()"><hl:message key="rotulo.dashboard.credenciamento.autorizar.navegador"/></a>
			    </div>
                <% if (!arquivoTermoAditivoPreenchidos.isEmpty()) { %>
                <div class="row" id="verificaCertificado">
					<div class="col-sm">
						<div class="card">
							<div class="card-header hasIcon">
	                            <h2 class="card-header-title"><hl:message key="rotulo.termo.aditivo.csa.verifica.certificado.digital"/></h2>
							</div>
							<div class="card-body">
	                            <dl id="validaCertificadoLinha" class="row data-list">
	                            </dl>
	                        </div>
	                     </div>
	               </div>
               </div>
                 <div class="col-md-12">             
                   <div class="card">
                      <div class="card-header hasIcon pl-3">
                        <h2 class="card-header-title"><hl:message key="rotulo.dashboard.titulo.table.modal.credenciamento"/></h2>
                      </div>
                      <div class="card-body table-responsive p-0">
                        <div class="table-responsive">
                        <table class="table table-striped table-hover">
                           <thead>
                              <tr>
                                <th scope="col"><hl:message key="rotulo.dashboard.nome"/></th>
                                <th scope="col"><hl:message key="rotulo.acoes"/></th>
                              </tr>
                           </thead>
                           <tbody>                      
                             <%for(AnexoCredenciamentoCsa arquivoTermoAditivoPreenchido : arquivoTermoAditivoPreenchidos){     
                                 String nome_arquivo = arquivoTermoAditivoPreenchido.getAncNome();
                             %>
                               <tr>  
                                 <td><%=TextHelper.forHtmlContent(nome_arquivo)%></td>
                                 <td>
                                   <div class="actions">
                                   	<div class="dropdown">
		                                <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		                                  <div class="form-inline">
		                                     <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
		                                       <use xlink:href="#i-engrenagem"></use></svg>
		                                     </span><hl:message key="rotulo.botao.opcoes"/>
		                                  </div>
		                                </a>
			                                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
			                                  <a class="dropdown-item" onClick="javascript:fazDownload('<%=TextHelper.forJavaScript(nome_arquivo)%>');"><hl:message key="rotulo.acoes.upload.arquivo.download"/></a>
			                                  <a class="dropdown-item" onClick="visualizarPdf('<%=TextHelper.forJavaScript(consignataria.getCsaCodigo())%>','<%=TextHelper.forJavaScript(nome_arquivo)%>');"><hl:message key="rotulo.dashboard.detalhar.credenciamento.visualizar.termo"/></a>
			                                  <a class="dropdown-item" onClick="assinarPdf('<%=TextHelper.forJavaScript(consignataria.getCsaCodigo())%>','<%=TextHelper.forJavaScript(nome_arquivo)%>');"><hl:message key="rotulo.dashboard.detalhar.credenciamento.assinar.digitalmente.termo"/></a>
			                                  <a class="dropdown-item" onClick="validarAssinaturaDigital('<%=TextHelper.forJavaScript(consignataria.getCsaCodigo())%>','<%=TextHelper.forJavaScript(nome_arquivo)%>');"><hl:message key="rotulo.termo.aditivo.csa.validar.certificado.digital"/></a>
			                               </div>
			                            </div>
                                   </div>
                                 </td>
                               </tr> 
                             <%} %>
                           </tbody>
                           <tfoot>
                             <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.arquivos.credenciamento", responsavel)%></td></tr>
                           </tfoot>
                        </table>
                        </div> 
                      </div>
                   </div>
                </div>
              <% } %>
                <div class="modal-footer pt-0">
                  <div class="btn-action mt-2 mb-0">
                    <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></a>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
     <%} %>

  <!-- Modal aguarde -->
  <div class="modal fade" id="modalAguarde" data-focus="false" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
     <div class="modal-dialog-upload modal-dialog" role="document">
       <div class="modal-content">
         <div class="modal-body">
           <div class="row">
             <div class="col-md-12 d-flex justify-content-center">
             <img src="../img/loading.gif" class="loading">
             </div>
             <div class="col-md-12">
              <div class="modal-body"><span><hl:message key="mensagem.upload.generico.aguarde"/></span></div>            
             </div>
           </div>
         </div>
       </div>
     </div>
  </div>
  
  <div class="modal fade" id="finalizarTermoCsaBloqueadaModal" tabindex="-1" role="dialog" aria-labelledby="modalfinalizarTermoCsaBloqueadaTitulo" aria-hidden="true" style="display: none;">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header pb-0">
            <h5 class="modal-title about-title mb-0" id="finalizarTermoCsaBloqueadaModal">
              <hl:message key="rotulo.consignataria.bloqueada"/>
            </h5>
              <button type="button" class="btn-close mr-2" data-bs-dismiss="modal" aria-label="<hl:message key='rotulo.botao.fechar'/>"></button>
          </div>
          <div class="form-group modal-body m-0">
            <span class="modal-title mb-0" id="subTitulo"><hl:message key="mensagem.confirmacao.desbloqueio.csa.credenciamento"/></span>
            <br><br>
          <div class="btn-action ui-dialog-buttonset mt-3 mb-3">
            <button type="button" class="btn btn-primary ml-4 mr-3 float-end" onClick="postData('../v3/visualizarDashboardCredenciamento?acao=finalizarAssinaturaTermo&desbloquearCsa=true&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&csaCodigo=<%=TextHelper.forJavaScriptAttribute(consignataria.getCsaCodigo())%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.sim"/></button>
            <button type="button" class="btn btn-outline-danger ml-4 float-end" onClick="postData('../v3/visualizarDashboardCredenciamento?acao=finalizarAssinaturaTermo&desbloquearCsa=false&creCodigo=<%=TextHelper.forJavaScriptAttribute(creCodigo)%>&csaCodigo=<%=TextHelper.forJavaScriptAttribute(consignataria.getCsaCodigo())%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.nao"/></button>
          </div> 
          </div>
        </div>
      </div>        
    </div>
</c:set>
<c:set var="javascript">
	<script src="../node_modules/responsive-bootstrap-tabs/jquery.responsivetabs.js"></script>
	<script src="../js/serpro/is.min.js"></script>
	<script src="../js/serpro/serpro-signer-promise.js"></script>
	<script src="../js/serpro/serpro-signer-client.js"></script>
	<script src="../js/serpro/serpro-client-connector.js"></script>
    <%if(responsavel.isCsa() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo())){ %>
        <hl:fileUploadV4 botaoVisualizarRemover="<%=true%>" multiplo="true" scriptOnly="true" nomeCampoArquivo="FILE1" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_DOCUMENTOS_CREDENCIAMENTO%>" tipoArquivo="anexo_credenciamento"/>     
    <%} %>
    <%if(responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_PREENCHIMENTO_TERMO_ADITIVO_CSE.getCodigo())
            || responsavel.isCsa() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())
            || responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo())
            || responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo())) { %>
        <hl:fileUploadV4 botaoVisualizarRemover="<%=true%>" multiplo="false" scriptOnly="true" nomeCampoArquivo="FILE1" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_DOCUMENTOS_CREDENCIAMENTO%>" tipoArquivo="anexo_credenciamento"/>
    <%} %>
  <script type="text/JavaScript">
  <%if((responsavel.isCsa() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())) || 
	        (responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo())) ||
	        (responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo()) && responsavel.temPermissao(CodedValues.FUN_APROVAR_TERMO_ADITIVO_CSA))){ %>
	        $('#verificaCertificado').hide();
	        var serverAuthorizarion = $('.js-server-authorization');
	        serverAuthorizarion.hide();
  <%}%>
  function vf_upload_arquivos() {
      var arquivo = tratarArquivo();
      if ((arquivo == null) || (trim(arquivo) == "") || (arquivo.toUpperCase() == "NULL")) {
          alert('<hl:message key="mensagem.editar.anexo.consignacao.selecione.arquivo"/>');
          return false;
      } else {
    	  if (ativarLoadingBotao('#botaoConfirmarUpload')) {
    		  document.getElementById("FILE1").value = arquivo;
        	  return true; 
    	  }
      }
  }
  
  function ativarLoadingBotao(selector) {
	    var $btn = $(selector);
	    $btn.prop('disabled', true);
	    if ($btn.data('loading') === true) {
	    	return false;
	    }
	    $btn.data('loading', true);
	
	    var spinnerHtml = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>';
	    if ($btn.find('.spinner-border, .spinner-grow').length === 0) {
	      	$btn.prepend(spinnerHtml);
	    }
	    return true;
  }

  function tratarArquivo(){
	  var arquivo = document.getElementById("FILE1").value;
	  var nomes = arquivo.split(';');
	  var nomesArquivosFinal = "";
	  for(var i = 0; i < nomes.length; i++) {
    	  // Faz o tratamento caso o usuario tenha removido algum arquivo temporario para upload
    	  if(nomes[i] != "removido") {
      		if (nomesArquivosFinal == "") {
      			nomesArquivosFinal = nomes[i];
      		} else {
      			nomesArquivosFinal += ';' + nomes[i];
    		}  
    	  }
	  }
		  return nomesArquivosFinal;
  }
  
  function removeAnexoVisualizacao(posicao) {
  	  let nomeArquivo = $("#FILE1").val();
  	  var nomes = nomeArquivo.split(';');
  	  nomeArquivo = nomes[posicao];
  	  var novaFILE1 = "";
  	  for (var i = 0; i < nomes.length; i++) {
  		  if(i != posicao) {
  	  		if (novaFILE1 == "") {
  	  			novaFILE1 = nomes[i];
  	  		} else {
  	  			novaFILE1 += ';' + nomes[i];
  			}  
  		  } else if(novaFILE1 == ""){
  			    novaFILE1 += "removido";
  		  } else {
  	  			novaFILE1 += ';' + "removido";
  		  }
  	  }
  	  document.getElementById('FILE1').value = novaFILE1;
	  $.ajax({
		    type: 'POST',
		    url: '../v3/excluirArquivo?arquivo_nome=' + nomeArquivo + '&tipo=anexo_credenciamento_temp&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>',
		    success: function (data) {
		    	// Busca a div do arquivo que foi removido para nao exibir na lista abaixo do botao para anexar documento.
	           var divArquivoRemovido = document.getElementById(nomeArquivo);
	      	   if (divArquivoRemovido != null) {
	          	   divArquivoRemovido.parentNode.removeChild(divArquivoRemovido);
	      	   }
	      	           	   
        	   // Se tiver uma mensagem de sessao de sucesso, apaga ela para nao confundir o usuario
        	   var divAlertSucess = document.getElementsByClassName('alert alert-success');
        	   if(divAlertSucess.length > 0) {
        	   		divAlertSucess[0].parentNode.removeChild(divAlertSucess[0]);
        	   }
        	   
        	   // Caso nao tenha mais arquivos para upload ele oculta a barra azul, para melhorar experiencia do usuario.
        	   if(document.getElementById("pic-progress-wrap-FILE1") != null && arquivosRemovidos(novaFILE1)) {
        		   document.getElementById("pic-progress-wrap-FILE1").style.display = 'none';
        	   }
		    },
		    error: function (error) {
	  	     	   console.log(error);
		    }
	  })
	}
  
    $(function() {
        $('.nav-tabs').responsiveTabs();
    });
    
    function validarReprovacao(){
    	const tipoMotivo = document.getElementById("tmoCodigo");
    	const tipoMotivoObs = document.getElementById("OCC_OBS");
    	
    	if(tipoMotivo == null || tipoMotivo.value =='' || tipoMotivo.value == 'undefined'){
    		alert('<hl:message key="rotulo.consignante.informe.motivo.operacao"/>');
    		return false;
    	}
    	
    	if(tipoMotivoObs == null || tipoMotivoObs.value.trim() =='' || tipoMotivoObs.value == 'undefined'){
    		alert('<hl:message key="mensagem.informe.observacao"/>');
    		return false;
    	}
    	
    	return true;
    }
    
    function fazDownload(nome){
      <%if(responsavel.isCsa() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSA.getCodigo())
              || responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_ASSINATURA_TERMO_ADITIVO_CSE.getCodigo())
              || responsavel.isCseSup() && scrCodigo.equals(StatusCredenciamentoEnum.AGUARDANDO_APROVACAO_TERMO_ADITIVO.getCodigo())){ %>
          postData('../v3/downloadArquivo?arquivo_nome=' + encodeURIComponent(nome) + '&tipo=anexo_credenciamento_termo&csaCodigo=<%=consignataria.getCsaCodigo()%>&<%=SynchronizerToken.generateToken4URL(request)%>','download');
      <%} else { %>
          postData('../v3/downloadArquivo?arquivo_nome=' + encodeURIComponent(nome) + '&tipo=anexo_credenciamento&<%=SynchronizerToken.generateToken4URL(request)%>','download');
      <%}%>
  	}
  	
    function downloadAnexoVisualizacao(posicao) {
    	  let nomeArquivo = $("#FILE1").val();
    	  var nomes = nomeArquivo.split(';');
    	  nomeArquivo = nomes[posicao];
          postData("../v3/downloadArquivo?arquivo_nome=" + nomeArquivo + "&tipo=anexo_credenciamento_temp&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>");
    }
    
    function arquivosRemovidos(fileValores) {
    	  var todosRemovidos = true;
    	  var nomes = fileValores.split(';');
    	  for(var i=0; i<nomes.length; i++)
    		  if(nomes[i] != "removido") {
    			  todosRemovidos = false;
    		  }
    	  
    	  return todosRemovidos;
    }
    
    function fazDownloadArqHisAnexo(nome){
        postData('../v3/downloadArquivo?arquivo_nome=' + encodeURIComponent(nome) + '&tipo=anexo_credenciamento_termo&csaCodigo=<%=consignataria.getCsaCodigo()%>&<%=SynchronizerToken.generateToken4URL(request)%>','download');
    }
    
    function visualizarPdf(codigo, nome) {
        window.open('', codigo+nome, 'height=800,width=1000,status=no,toolbar=no,menubar=no,location=no,left=200,top=200'); 
        postData('../v3/carregarStream?acao=visualizarTermo&csaCodigo='+codigo+'&nomeArquivo='+nome+'&_skip_history_=true',codigo+nome);
     }
    
    function assinarPdf(codigo, nome) {
        $.ajax({
            type: 'POST',
            url: '../v3/carregarStream?acao=buscar&csaCodigo=' + codigo + '&nomeArquivo=' + nome + '&_skip_history_=true',
            success: function (data) {
                window.signPdf(data)
                    .then(function (arquivoConvertido) {
                        var nomeInput = $('<input>').attr({
                            type: 'hidden',
                            name: 'nomeArquivo',
                            value: nome
                        });
                        
                        var pdfAssinadoInput = $('<input>').attr({
                            type: 'hidden',
                            name: 'pdfAssinado',
                            value: arquivoConvertido
                        });
                        $('#formCertificado').append(nomeInput).append(pdfAssinadoInput).submit();
                    })
                    .catch(function (error) {
                        console.error('Erro ao assinar o PDF:', error);
                        alert(error.error);
                    });
            },
            error: function (error) {
                console.error('Erro na requisição:', error);
            }
        });
    }
    
    function validarAssinaturaDigital(codigo, nome) {
        $.ajax({
            type: 'POST',
            url: '../v3/carregarStream?acao=buscar&csaCodigo=' + codigo + '&nomeArquivo=' + nome + '&_skip_history_=true',
            success: function (data) {
                window.validatePdfSign(data)
                    .then(function (dados) {
                    	if(dados == 'undefined' || dados.length == 0){
                    		alert('<%=ApplicationResourcesHelper.getMessage("mensagem.erro.validar.certificado.digital", responsavel)%>');
                    		return;
                    	}
                    	var divValidaCertificadoLinha = $('#validaCertificadoLinha');
                        divValidaCertificadoLinha.empty();
                   		var contador = 1;
                        
                    	for (var d = 0; d < dados.length; d++){
                    	    var assinante = dados[d].assinante;
                            var cadeiaCertificado = dados[d].cadeiaCertificado.toString();

                            var dtAssinante = $("<dt class='col-5'>").text('<%=ApplicationResourcesHelper.getMessage("rotulo.termo.aditivo.csa.assinante.certificado.digital", responsavel)%>'+'('+ contador +'): ');
                            var ddAssinante = $("<dd class='col-5'>").text(assinante);
                            var dtCadeiaCertificado = $("<dt class='col-5'>").text('<%=ApplicationResourcesHelper.getMessage("rotulo.termo.aditivo.csa.cadeia.certificado.digital", responsavel)%>'+'('+ contador +'): ');
                            var ddCadeiaCertificado = $("<dd class='col-5'>").text(cadeiaCertificado);

                            divValidaCertificadoLinha.append(dtAssinante).append(ddAssinante);
                            divValidaCertificadoLinha.append(dtCadeiaCertificado).append(ddCadeiaCertificado);
                            
                            contador++;
                    	}
                    	
                    	$('#verificaCertificado').show();
                    })
                    .catch(function (error) {
                        console.error('Erro ao validar o PDF:', error);
                    });
            },
            error: function (error) {
                console.error('Erro na requisição:', error);
            }
        });
    }
    
    $('#modalAssinarTermoAditivoCertificado').on('shown.bs.modal', function (e) {
        window.initSerproSignerClient();
    });
    
    function autorizarNavegador(){
    	var novaAba = window.open('http://127.0.0.1:65056/', '_blank');
        novaAba.focus();
        postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');
    }
    
    function abrirModaisDesbloqueioCsa() {
        $('#finalizarTermoCsaBloqueadaModal').modal('show');
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>