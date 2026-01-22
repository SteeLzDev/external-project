<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.web.AcaoConsignacao"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");
CustomTransferObject cde = (CustomTransferObject) request.getAttribute("cde");
List<TransferObject> hist = (List<TransferObject>) request.getAttribute("historico");
List<TransferObject> parcelas = (List<TransferObject>) request.getAttribute("parcelas");
List<TransferObject> anexos = (List<TransferObject>) request.getAttribute("anexos");
List<TransferObject> propostas = (List<TransferObject>) request.getAttribute("propostas");
List<TransferObject> propostasLeilao = (List<TransferObject>) request.getAttribute("propostasLeilao");
List<AcaoConsignacao> listaAcoes = (List<AcaoConsignacao>) request.getAttribute("listaAcoes");
List<TransferObject> historicoSolicitacaoAutorizacao = (List<TransferObject>) request.getAttribute("historicoSolicitacaoAutorizacao");
ParamSvcTO paramSvcCse = (ParamSvcTO) request.getAttribute("paramSvcCse");
String msgInfBancarias = (request.getAttribute("msgInfBancarias") != null ? request.getAttribute("msgInfBancarias").toString() : "");

String adeCodigo = autdes.getAttribute(Columns.ADE_CODIGO).toString();
String sadCodigo = autdes.getAttribute(Columns.ADE_SAD_CODIGO).toString();
String adeNumero = autdes.getAttribute(Columns.ADE_NUMERO).toString();
String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();

String linkRet = JspHelper.verificaVarQryStr(request, "linkRet");
String linkRetorno = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
if (TextHelper.isNull(linkRet)) {
    linkRet = linkRetorno.replace('?', '$').replace('&', '|').replace('=', '(');
}

String tipo = (request.getParameter("tipo") != null) ? request.getParameter("tipo").toString() : "consultar";

boolean botaoVoltarPaginaInicial = (request.getAttribute("botaoVoltarPaginaInicial") != null || request.getParameter("botaoVoltarPaginaInicial") != null);
boolean isReserva = (Boolean) request.getAttribute("isReserva");

// Determina se será exibido a barra de ações: usuário deve ter permissão de modificar o contrato
boolean usuarioPodeModificarAde = (Boolean) request.getAttribute("usuarioPodeModificarAde");
boolean usuarioPodeConsultarAde = (Boolean) request.getAttribute("usuarioPodeConsultarAde");
boolean arquivado = (Boolean) request.getAttribute("arquivado");
Short adeIncMargem = (Short) autdes.getAttribute(Columns.ADE_INC_MARGEM);
boolean exibirHistoricoMargem = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_HISTORICO_MARGEM_DETALHE_CONSIGNACAO, responsavel) && responsavel.temPermissao(CodedValues.FUN_CONS_HISTORICO_MARGEM) &&
                                (responsavel.isCseSupOrg() || responsavel.isSer()) &&
                                (adeIncMargem != null && adeIncMargem != CodedValues.INCIDE_MARGEM_NAO);

boolean exibirHistoricoAde = (hist != null && !hist.isEmpty() && (responsavel.isCseSupOrg() || usuarioPodeModificarAde || usuarioPodeConsultarAde));
boolean exibirHistoricoPrd = (parcelas != null && !parcelas.isEmpty() && (responsavel.isCseSupOrg() || usuarioPodeModificarAde));
boolean exibirAnexosAde = (anexos != null && !anexos.isEmpty() && (responsavel.isCseSupOrg() || usuarioPodeModificarAde || usuarioPodeConsultarAde));
boolean exibirPropostaPagamento = (propostas != null && !propostas.isEmpty() && (responsavel.isCseSupOrg() || usuarioPodeModificarAde));
boolean exibirPropostasLeilao = (propostasLeilao != null && !propostasLeilao.isEmpty()) && (responsavel.isCseSupOrg() || responsavel.isSer());
boolean exibirHistoricoSolicitacaoAutorizacao = (historicoSolicitacaoAutorizacao != null && !historicoSolicitacaoAutorizacao.isEmpty()) && !responsavel.isSer();
String voltar = TextHelper.isNull(request.getAttribute("voltar")) ? paramSession.getLastHistory() : (String) request.getAttribute("voltar");

List<TransferObject> comunicacoes = (List<TransferObject>) request.getAttribute("comunicacoes");
String codigoComunicacao = !TextHelper.isNull(request.getAttribute("codigoComunicacao")) ? (String) request.getAttribute("codigoComunicacao") : null;

String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="title">
   <hl:message key="rotulo.visualizar.consignacao.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
	<div id="header-print">
		<% if ("v4".equals(versaoLeiaute)) { %>
			<img src="../img/econsig-logo.svg">
		<% } else { %>
			<img src="../img/logo_sistema_v5.png">
		<%} %>
		<p id="date-time-print"></p>
	</div>
    <div class="page-title d-print-none">
      <div class="row">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
              <%
               if (listaAcoes != null && !listaAcoes.isEmpty()) {
                   for (AcaoConsignacao acao : listaAcoes) {
                       if (acao.getAcao().equals("CRIAR_COMUNICACAO_CONSIGNACAO") && !TextHelper.isNull(codigoComunicacao)){
                            %>
              		<a class="dropdown-item" href="#no-back" onClick="criarComunicacao('<%=codigoComunicacao%>','<%=acao.getLink()%>');"><hl:message key="rotulo.acao.criar.comunicacao.consignataria.consignacao"/></a>
              <%       } else {
              %>
              		<a class="dropdown-item" href="#no-back" onClick="doIt('<%=TextHelper.forJavaScript(acao.getAcao())%>', '<%=TextHelper.forJavaScript(adeCodigo)%>');"><%=TextHelper.forHtmlAttribute(acao.getDescricao())%></a>
              <%
                  	   }
                   }
               }
              %>
              <div class="dropdown-divider" role="separator"></div> 
              <a class="dropdown-item" href="#no-back" onClick="imprimir();"><hl:message key="rotulo.botao.imprimir"/></a>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="row firefox-print-fix">
      <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
      <hl:detalharADEv4 name="autdes" table="true" type="consultar" arquivado="<%=(boolean)(arquivado)%>" scope="request" exibeIconConSer="true"/>
      <%-- Fim dos dados da ADE --%>     
    </div>


    <% if (exibirHistoricoAde || exibirHistoricoPrd || exibirAnexosAde || exibirPropostasLeilao || exibirHistoricoMargem || exibirHistoricoSolicitacaoAutorizacao || (comunicacoes != null && !comunicacoes.isEmpty())) { %>
      <ul class="nav nav-tabs responsive-tabs" id="consignacaoInfo" role="tablist">
        <% if (exibirHistoricoAde) { %>
            <li class="nav-item">
              <a class="nav-link active" id="historico-tab" data-bs-toggle="tab" href="#historico" role="tab" aria-controls="profile" aria-selected="true"><hl:message key="rotulo.ocorrencia.titulo"/></a>
            </li>
        <% } %>
        <% if (exibirHistoricoPrd) { %>
          <% if (!isReserva) { %>
            <li class="nav-item">
              <a class="nav-link<%= !exibirHistoricoAde ? " active" : "" %>" id="parcelas-tab" data-bs-toggle="tab" href="#parcelas" role="tab" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.ocorrencia.parcela.titulo"/></a>
            </li>
          <% } else { %>
          <li class="nav-item">
              <a class="nav-link<%= !exibirHistoricoAde ? " active" : "" %>" id="parcelas-tab" data-bs-toggle="tab" href="#parcelas" role="tab" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.ocorrencia.lancamento.titulo"/></a>
          </li>
          <% } %>
        <% } %>
        <% if (exibirAnexosAde) { %>
            <li class="nav-item">
              <a class="nav-link<%= !exibirHistoricoAde && !exibirHistoricoPrd ? " active" : "" %>" id="anexos-tab" data-bs-toggle="tab" href="#anexos" role="tab" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.anexo.consignacao.titulo"/></a>
            </li>
        <% } %>
        <% if (exibirPropostaPagamento) { %>
            <li class="nav-item">
              <a class="nav-link<%= !exibirHistoricoAde && !exibirHistoricoPrd && !exibirAnexosAde ? " active" : "" %>" id="proposta-tab" data-bs-toggle="tab" href="#proposta" role="tab" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.anexo.proposta.titulo"/></a>
            </li>
        <% } %>
        <% if (exibirPropostasLeilao) { %>
            <li class="nav-item">
              <a class="nav-link<%= !exibirHistoricoAde && !exibirHistoricoPrd && !exibirAnexosAde && !exibirPropostaPagamento ? " active" : "" %>" id="proposta-leilao-tab" data-bs-toggle="tab" href="#proposta-leilao" role="tab" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.proposta.leilao.solicitacao.titulo"/></a>
            </li>
        <% } %>        
        <% if (exibirHistoricoMargem) { %>
            <li class="nav-item">
              <a class="nav-link<%= !exibirHistoricoAde && !exibirHistoricoPrd && !exibirAnexosAde && !exibirPropostaPagamento && !exibirPropostasLeilao ? " active" : "" %>" id="historicoMargem-tab" data-bs-toggle="tab" href="#historicoMargem" role="tab" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.historico.margem.titulo.maiusculo"/></a>
            </li>
        <% } %>
        <% if (exibirHistoricoSolicitacaoAutorizacao) { %>
            <li class="nav-item">
              <a class="nav-link<%= !exibirHistoricoAde && !exibirHistoricoPrd && !exibirAnexosAde && !exibirPropostaPagamento && !exibirPropostasLeilao && !exibirHistoricoMargem ? " active" : "" %>" id="historicoSolicitacaoAutorizacao-tab" data-bs-toggle="tab" href="#historicoSolicitacaoAutorizacao" role="tab" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.historico.solicitacao.autorizacao.validar.documentos.titulo.maiusculo"/></a>
            </li>
        <% } %>
        <% if (comunicacoes != null && !comunicacoes.isEmpty()) { %>
            <li class="nav-item">
              <a class="nav-link<%= !exibirHistoricoAde && !exibirHistoricoPrd && !exibirAnexosAde && !exibirPropostaPagamento && !exibirPropostasLeilao && !exibirHistoricoMargem && !exibirHistoricoSolicitacaoAutorizacao ? " active" : "" %>" id="comunicacoes-tab" data-bs-toggle="tab" href="#comunicacoes" role="tab" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.historico.comunicacao.titulo.maiusculo"/></a>
            </li>
        <% } %>

      </ul>

      <%-- Tab panes --%>
      <div class="tab-content" id="consignacaoInfo">
        <% if (exibirHistoricoAde) { %>
        <div class="tab-pane fade show active" id="historico" role="tabpanel" aria-labelledby="historico-tab">
          <%-- Utiliza a tag library ListaHistoricoContratoTag.java para exibir o histórico de ocorrências da ADE --%>
          <hl:listaHistoricoContratov4 name="historico" table="true" scope="request" />
          <%-- Fim dos dados da ADE --%>
        </div>
        <% } %>

        <% if (exibirHistoricoPrd) { %>
          <% if (!isReserva) { %>
        <div class="tab-pane fade <%= !exibirHistoricoAde ? "show active" : "" %>" id="parcelas" role="tabpanel" aria-labelledby="parcelas-tab">
          <%-- Utiliza a tag library ListaHistoricoParcelaTag.java para exibir o histórico de parcelas da ADE --%>
          <hl:listaHistoricoParcelav4 name="parcelas" table="true" scope="request" />
          <%-- Fim dos dados da ADE --%>
        </div>
          <% } else { %>
        <div class="tab-pane fade <%= !exibirHistoricoAde ? "show active" : "" %>" id="parcelas" role="tabpanel" aria-labelledby="parcelas-tab">
            <hl:listaHistoricoLancamentosv4 name="parcelas" table="true" scope="request"  />
        </div>
          <% } %>
        <% } %>

        <% if (exibirAnexosAde) { %>
        <div class="tab-pane fade <%= !exibirHistoricoAde && !exibirHistoricoPrd ? "show active" : "" %>" id="anexos" role="tabpanel" aria-labelledby="anexos-tab">
          <%-- Utiliza a tag library ListaAnexosContratoTag.java para listar os anexos do contrato --%>
          <hl:listaAnexosContratov4 name="anexos" table="true" type="consultar" scope="request"/>
          <%-- Fim dos anexos do contrato --%>
        </div>        
        <% } %>
        
        <% if (exibirPropostaPagamento) { %>
        <div class="tab-pane fade <%= !exibirHistoricoAde && !exibirHistoricoPrd && !exibirAnexosAde ? "show active" : "" %>" id="proposta" role="tabpanel" aria-labelledby="proposta-tab">
        <%-- Utiliza a tag library ListaPropostaPagamentoDividaTag.java para exibir as propostas de pagamento --%>
        <hl:listaPropostaPagamentoDividav4 lstPropostas="<%=(List)( propostas )%>" type="consultar" table="true" />
        <%-- Fim dos dados da ADE --%>
        </div>
        <% } %>
        
        <% if (exibirPropostasLeilao) { %>
        <div class="tab-pane fade <%= !exibirHistoricoAde && !exibirHistoricoPrd && !exibirPropostaPagamento && !exibirAnexosAde ? "show active" : "" %>" id="proposta-leilao" role="tabpanel" aria-labelledby="proposta-leilao-tab">
          <%-- Utiliza a tag library ListaPropostaLeilaoSolicitacaoTag.java para exibir as propostas de pagamento --%>
          <hl:listaPropostaLeilaoSolicitacaov4 lstPropostas="<%=(List)( propostasLeilao )%>" table="true" />
          <%-- Fim dos dados da ADE --%>
        </div>
        <% } %>

        <% if (exibirHistoricoMargem) { %>
        <div class="tab-pane fade <%= !exibirHistoricoAde && !exibirHistoricoPrd && !exibirPropostaPagamento && !exibirAnexosAde && !exibirPropostaPagamento && !exibirPropostasLeilao ? "show active" : "" %>" id="historicoMargem" role="tabpanel" aria-labelledby="historicoMargem-tab"></div>
        <% } %>
        
        <% if (exibirHistoricoSolicitacaoAutorizacao) { %>
          <div class="tab-pane fade <%= !exibirHistoricoAde && !exibirHistoricoPrd && !exibirPropostaPagamento && !exibirAnexosAde && !exibirPropostaPagamento && !exibirPropostasLeilao && !exibirHistoricoMargem ? "show active" : "" %>" id="historicoSolicitacaoAutorizacao" role="tabpanel" aria-labelledby="historicoSolicitacaoAutorizacao-tab">
            <hl:listaSolicitacaoAutorizacaov4 name="historicoSolicitacaoAutorizacao" table="true"/>
          </div>
        <% } %>
        
        <% if (comunicacoes != null && !comunicacoes.isEmpty()) { %>
        	<div class="tab-pane fade <%= !exibirHistoricoAde && !exibirHistoricoPrd && !exibirPropostaPagamento && !exibirAnexosAde && !exibirPropostaPagamento && !exibirPropostasLeilao && !exibirHistoricoMargem && !exibirHistoricoSolicitacaoAutorizacao ? "show active" : "" %>" id="comunicacoes" role="tabpanel" aria-labelledby="comunicacoes-tab">
            	<hl:listaComunicacoesv4 name="comunicacoes" table="true"/>
          	</div>
		<% } %>
      </div>
    <% } %>

    <div class="btn-action" aria-label="<hl:message key="rotulo.botoes.acao.pagina"/>">
      <a class="btn btn-outline-danger mt-2" href="#no-back" onClick="postData('<%=botaoVoltarPaginaInicial ? "../v3/carregarPrincipal" : TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(voltar, request))%>')" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    </div>

    <div class="modal fade" id="modalReprovarAnexoConsignacao" tabindex="-1" role="dialog" aria-labelledby="modalReprovarAnexoConsignacaoTitulo" aria-hidden="true" style="display: none;">
      <div class="modal-dialog modalTermoUso" role="document">
        <div class="modal-content p-3">
          <div class="modal-header">
            <h5 class="modal-title about-title mb-0" id="modalReprovarAnexoConsignacaoTitulo"><hl:message key="mensagem.reprovar.documentacao.solicitacao"/></h5>
          </div>
          <div class="form-group modal-body m-0">
            <span class="modal-title mb-0" id="subTitulo"><hl:message key="mensagem.informar.reprovacao.documentos.solicitacao"/></span>
            <br>
            <label for="editfield"><hl:message key="rotulo.avancada.adeObs"/></label>
            <textarea class="form-control" id="editfield" name="editfield" rows="3" cols="28"></textarea>
          </div>
          <div class="ui-dialog-buttonset mb-3">
          <button type="button" class="btn btn-primary ml-4 mr-3 float-end" id="btnReprovarAnexoConsignacao" onClick="confirmarReprovarAnexoConsignacao()"><hl:message key="rotulo.botao.confirmar"/></button>
          <button type="button" class="btn btn-outline-danger ml-4 float-end" id="voltarModalReprovarDoc"><hl:message key="rotulo.botao.cancelar"/></button>
          </div>          
        </div>
      </div>
    </div>
    
    <div class="modal fade" id="modalConfirmaComunicacao" tabindex="-1" role="dialog" aria-labelledby="modalConfirmaComunicacaoLabel" aria-hidden="true">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title about-title mb-0"><hl:message key="rotulo.detalhe.historico.comunicacao.ja.existe.titulo"/></h5>
	        <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
	          <span aria-hidden="true"></span>
	        </button>
	      </div>
	      <div class="modal-body">
	        <hl:message key="mensagem.detalhe.historico.comunicacao.ja.existe"/>
	      </div>
	      <div class="modal-footer pt-0">
			  <div class="btn-action mt-2 mb-0">
				<a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.detalhe.historico.comunicacao.ja.existe.editar"/>' id="editarComunicacao" href="#"><hl:message key="rotulo.detalhe.historico.comunicacao.ja.existe.editar"/></a>
				<a class="btn btn-primary" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.detalhe.historico.comunicacao.ja.existe.nova"/>' id="novaComunicao" href="#"><hl:message key="rotulo.detalhe.historico.comunicacao.ja.existe.nova"/></a>
			  </div>
		</div>
	    </div>
	  </div>
	</div>
    
    <div class="modal fade" id="simularSaldoModal" tabindex="-1" role="dialog" aria-labelledby="modalSimularSaldoTitulo" aria-hidden="true" style="display: none;">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title about-title mb-0" id="modalSimularSaldoTitulo">
              <hl:message key="rotulo.modal.saldo.devedor.simulacao"/>
            </h5>
            <button type="button" class="btn-close mr-2" data-bs-dismiss="modal" aria-label="<hl:message key='rotulo.botao.fechar'/>"></button>
          </div>
          
          <div class="form-group modal-body m-0">
            <span class="modal-title mb-0" id="subTitulo"><hl:message key="mensagem.confirmacao.simular.saldo.devedor"/></span>

            <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_SIMULACAO_PARCIAL_SALDO_DEVEDOR, CodedValues.TPC_SIM, responsavel)) { %>
              <div class="form-check mt-3" role="radiogroup" aria-labelledby="tipoSimulacaoTitulo">
                 <div class="form-group my-0">
                   <span id="tipoSimulacaoTitulo"><hl:message key="rotulo.modal.saldo.devedor.simulacao.tipo"/></span>
                 </div>
                 <div class="form-check form-check-inline">
                   <input type="radio" name="tipoSimulacao" id="tipoSimulacaoTotal" value="T" class="form-check-input ml-1" onfocus="SetarEventoMascaraV4(this,'#*10',true);" onblur="fout(this);ValidaMascaraV4(this);" onclick="$('#qtdParcelasSaldo').prop('disabled', true);" checked="checked"/>
                   <label class="form-check-label labelSemNegrito formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label="<hl:message key="rotulo.modal.saldo.devedor.simulacao.tipo.total"/>" for="tipoSimulacaoTotal"><hl:message key="rotulo.modal.saldo.devedor.simulacao.tipo.total"/></label>
                 </div>
                  <div class="form-check form-check-inline">
                   <input type="radio" name="tipoSimulacao" id="tipoSimulacaoParcial" value="P" class="form-check-input ml-1" onfocus="SetarEventoMascaraV4(this,'#*10',true);" onblur="fout(this);ValidaMascaraV4(this);" onclick="$('#qtdParcelasSaldo').prop('disabled', false);"/>
                   <label class="form-check-label formatacao labelSemNegrito ml-1 pr-4 text-nowrap align-text-top" aria-label="<hl:message key="rotulo.modal.saldo.devedor.simulacao.tipo.parcial"/>" for="tipoSimulacaoParcial"><hl:message key="rotulo.modal.saldo.devedor.simulacao.tipo.parcial"/></label>
                 </div>
              </div>
              <div class="mt-3">
                <label for="qtdParcelasSaldo"><hl:message key="rotulo.modal.saldo.devedor.simulacao.qtd.parcelas"/></label>            
                <hl:htmlinput name="qtdParcelasSaldo" di="qtdParcelasSaldo" type="text" classe="form-control" value="0" mask="#D3" others="disabled" />
              </div>
            <% } %>
           
          </div>

            <div class="btn-action ui-dialog-buttonset p-3">
            <button type="button" class="btn btn-primary ml-4 mr-3 float-end" id="btnSimularSaldo" onClick="simularSaldo()"><hl:message key="rotulo.botao.confirmar"/></button>
            <button type="button" class="btn btn-outline-danger ml-4 float-end" data-bs-dismiss="modal" id="voltarSimulacaoSaldoModal"><hl:message key="rotulo.botao.cancelar"/></button>
          </div> 
        </div>
      </div>        
    </div>

    <div class="modal fade" id="editarModal" tabindex="-1" role="dialog" aria-labelledby="modalTitulo"
         aria-hidden="true" style="display: none">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <form method="post"  name="formEditar" id="formEditar" action="../v3/consultarConsignacao?acao=editarOcorrenciaAde&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>">
                    <input type="hidden" id="observacaoHiden" name="observacaoHiden">
                    <input type="hidden" id="ocaCodigo" name="ocaCodigo">
                    <input type="hidden" id="adeCodigo" name="adeCodigo">
                    <div class="modal-header">
                    <h2 class="card-header-title">
                        <hl:message key="rotulo.historico.ocorrencia.editar"/>
                    </h2>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div id="modalEditar" class="modal-body">
                    <div class="col-sm">
                        <div class="card">
                            <div class="card-header">
                                <h2 class="card-header-title">
                                    <hl:message key="rotulo.acoes.editar"/>
                                </h2>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="form-group col-sm">
                                        <label for="tipoOcorrencia"><hl:message key="rotulo.registrar.ocorrencia.consignacao.tipo"/></label>
                                        <input type="text" class="form-control" id="tipoOcorrencia" name="tipoOcorrencia" disabled>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="form-group col-sm">
                                        <label for="observacao"><hl:message key="rotulo.usuario.observacao"/></label>
                                        <textarea class="form-control" id="observacao" name="observacao"></textarea>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer pt-0">
                    <div class="btn-action mt-2 mb-0">
                        <a class="btn btn-outline-danger" onclick="fecharModal(0); return false;" href="#no-back"
                           aria-label="<hl:message key="rotulo.botao.fechar"/>"><hl:message key="rotulo.botao.fechar"/></a>
                        <a class="btn btn-primary" onclick="editarOcorrencia(); return false;" aria-label="<hl:message key="rotulo.botao.confirmar"/>" href="#no-back"><hl:message key="rotulo.botao.confirmar"/></a>
                    </div>
                </div>
                </form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="historicoModal" tabindex="-1" role="dialog" aria-labelledby="modalTitulo"
         aria-hidden="true" style="display: none">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h2 class="card-header-title">
                        <hl:message key="rotulo.historico.ocorrencia.visualizar"/>
                    </h2>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="col-sm">
                        <div class="card">
                            <div class="card-header">
                                <h2 class="card-header-title">
                                    <hl:message key="rotulo.historico"/>
                                </h2>
                            </div>
                            <div id="modalHistorico" class="card-body">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer pt-0">
                    <div class="btn-action mt-2 mb-0">
                        <a class="btn btn-outline-danger" onclick="fecharModal(1); return false;" href="#no-back"
                           aria-label="<hl:message key="rotulo.botao.fechar"/>"><hl:message
                                key="rotulo.botao.fechar"/></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="modal fade" id="solicitarSaldoModal" tabindex="-1" role="dialog" aria-labelledby="modalSolicitarSaldoTitulo" aria-hidden="true" style="display: none;">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header pb-0">
            <h5 class="modal-title about-title mb-0" id="modalSolicitarSaldoTitulo">
              <hl:message key="rotulo.modal.saldo.devedor.informacao"/>
            </h5>
              <button type="button" class="btn-close mr-2" data-bs-dismiss="modal" aria-label="<hl:message key='rotulo.botao.fechar'/>"></button>
          </div>
          <div class="form-group modal-body m-0">
            <span class="modal-title mb-0" id="subTitulo"><hl:message key="mensagem.confirmacao.solicitar.saldo.devedor.info"/></span>
            <br><br>
  <% 
      boolean telAusente = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_SALDO_DEVEDOR, CodedValues.TEL_SER_SOLIC_SALDO_DEVEDOR_AUSENTE, responsavel);
      if (!telAusente) {
       String sdvTelefone = (String) request.getAttribute("sdvTelefone");
  %>
            <label for="editfield"><hl:message key="mensagem.solicitar.saldo.devedor.informe.telefone"/></label><br><br>
            <label for="editfield"><hl:message key="rotulo.solicitar.saldo.devedor.telefone"/>:</label>            
            <textarea class="form-control" id="editfieldSolicitar" name="editfieldSolicitar" rows="1" cols="28"> <%=!TextHelper.isNull(sdvTelefone) ? sdvTelefone : ""%> </textarea>
  <% } %>
          <div class="btn-action ui-dialog-buttonset mt-3 mb-3">
            <button type="button" class="btn btn-primary ml-4 mr-3 float-end" id="btnSolicitaSaldo" onClick="solicitarSaldoConsulta()"><hl:message key="rotulo.botao.confirmar"/></button>
            <button type="button" class="btn btn-outline-danger ml-4 float-end" id="voltarSolicitacaoSaldoModal"><hl:message key="rotulo.botao.cancelar"/></button>
          </div> 
          </div>
        </div>
      </div>        
    </div>

    <div class="modal fade" id="solicitarSaldoLiqModal" tabindex="-1" role="dialog" aria-labelledby="modalSolicitarSaldoLiqTitulo" aria-hidden="true" style="display: none;">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header pb-0">
            <h5 class="modal-title about-title mb-0" id="modalSolicitarSaldoLiqTitulo">
              <hl:message key="rotulo.modal.saldo.devedor.liquidacao"/>
            </h5>
              <button type="button" class="btn-close mr-2" data-bs-dismiss="modal" aria-label="<hl:message key='rotulo.botao.fechar'/>"></button>
          </div>
          <div class="form-group modal-body m-0">
            <span class="modal-title mb-0" id="subTitulo"> <hl:message key="mensagem.confirmacao.solicitar.saldo.devedor.liq"/></span>
            <br><br>
  <% 
    boolean telAusenteLiq = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_SALDO_DEVEDOR, CodedValues.TEL_SER_SOLIC_SALDO_DEVEDOR_AUSENTE, responsavel);
    if (!telAusenteLiq) {
     String sdvTelefone = (String) request.getAttribute("sdvTelefone");
  %>            
            <label for="editfield"><hl:message key="mensagem.solicitar.saldo.devedor.informe.telefone"/></label><br><br>
            <label for="editfield"><hl:message key="rotulo.solicitar.saldo.devedor.telefone"/>:</label>
            <textarea class="form-control" id="editfieldLiquidar" name="editfieldLiquidar" rows="1" cols="28"><%=!TextHelper.isNull(sdvTelefone) ? sdvTelefone : ""%></textarea>
  <% } %>
	          <div class="btn-action ui-dialog-buttonset mt-3 mb-3">
	            <button type="button" class="btn btn-primary ml-4 mr-3 float-end" id="btnSolicitaSaldoLiq" onClick="solicitarSaldoLiquidacao()"><hl:message key="rotulo.botao.confirmar"/></button>
	            <button type="button" class="btn btn-outline-danger ml-4 float-end" id="voltarSolicitacaoSaldoLiqModal"><hl:message key="rotulo.botao.cancelar"/></button>
	          </div>
          </div>
        </div>
      </div>
    </div>
	<% if ("v4".equals(versaoLeiaute)) { %>
	  <div id="footer-print">
		<img src="../img/footer-logo.png">
	  </div>
	<% } else { %>
		<div id="footer-print">
			<img src="../img/footer-logo-v5.png">
		</div>
	<%} %>
</c:set>

<c:set var="javascript">
  <style>
    @media print {
      *{
      	margin: 0;
      	padding: 0;
      }
      a{color: #000 !important;}
      #menuAcessibilidade {display: none;}
      #footer-print {position: absolute; bottom: 0;}
      #header-print img{width: 10%;}
      dl dt, dl dd{
      	line-height: 1;
      	color: #000;
	  }

      .firefox-print-fix {
        display: flex;
      }
      .firefox-print-fix dt {
        text-align: right;
      }

      .table th, .table td {
        font-size: 12px;
        line-height: 1.25;
        padding-top: 0;
        padding-bottom: 0;
        padding-left: .25rem;
        padding-left: .25rem;
        white-space: nowrap;
        color: #000 !important;
		border-left: 1px solid #000 !important;
      }
    }
    @page{
		margin: .5cm;
    }
  </style>
  <script src="../node_modules/responsive-bootstrap-tabs/jquery.responsivetabs.js"></script>
  <script src="../js/colunaCheckboxOcorrencia.js?<hl:message key="release.tag"/>"></script>
  <script>
	function injectDate(){
		const dateTimePrint = document.querySelector('#date-time-print');
		const printDate = new Date();
		printDate.toLocaleString("pt-br");
		dateTimePrint.innerText = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);
	}
  </script>
  <script type="text/JavaScript">
      <% if (exibirHistoricoAde) { %>
          $(document).ready(function() {
          	// Ocultar coluna de seleção
      		ocultarColuna();
          });
      <%}%>
	      
      $(function() {
          $('.nav-tabs').responsiveTabs();
      });
      
      function imprimir() {
      	  injectDate();
          window.print();
      }


      function historicoHoa(ocaCodigo) {
          $.ajax({
              type: 'POST',
              url: '../v3/buscaHistoricoOcorrenciaAde?_skip_history_=true',
              data: {
                  'ocaCodigo': ocaCodigo
              },
              success: function (data, status, error) {
                  $('#modalHistorico').html(data);
                  $('#historicoModal').modal('show');
              },
              error: function (request, status, error) {
              }
          });
      }

      function editarHoa(ocaCodigo, ocaTipo, ocaObs) {
          $('#adeCodigo').val('<%=adeCodigo%>');
          $('#tipoOcorrencia').val(ocaTipo);
          $('#ocaCodigo').val(ocaCodigo);
          $('#observacaoHiden').val(removerTags(ocaObs));
          $('#observacao').val(removerTags(ocaObs));
          $('#editarModal').modal('show');
      }

      function removerTags(html){
          const data = new DOMParser().parseFromString(html, 'text/html');
          return data.body.textContent || "";
      }

      function fecharModal(tipo){
          if (tipo === 0) {
              $('#editarModal').modal('hide');
          } else {
              $('#historicoModal').modal('hide');
          }
      }

      function editarOcorrencia() {
          const obsOld = $('#observacaoHiden').val();
          const obsNew = $('#observacao').val();
          if (obsOld === obsNew) {
              window.alert('<%=ApplicationResourcesHelper.getMessage("rotulo.historico.ocorrencia.igual.edicao", responsavel)%>')
          } else if(obsNew === '') {
              window.alert('<%=ApplicationResourcesHelper.getMessage("rotulo.historico.ocorrencia.nao.inserida.edicao", responsavel)%>')
          } else {
              const form = document.getElementById("formEditar");
              form.submit();
          }
      }

      $('#voltarModalReprovarDoc').on('click',function() {
           $('#modalReprovarAnexoConsignacao').modal('hide');
      });
      
      $('#voltarSolicitacaoSaldoModal').on('click',function() {
          $('#solicitarSaldoModal').modal('hide');
      });
      
      $('#voltarSolicitacaoSaldoLiqModal').on('click',function() {
          $('#solicitarSaldoLiqModal').modal('hide');
      });
      
      $('#voltarSimulacaoSaldoModal').on('click',function() {
          $('#simularSaldoModal').modal('hide');
      });

      function validaSessao() {
          var clock = parseInt(document.getElementById("clock").innerText.replace(':', ''));
          if (clock < "50") {
              setTimeout(validaClock, '1200', clock);
          } else {
              requisitaHistoricoMargem();
          }
      }

      function validaClock(clock,validaClock){
  	    var adeCodigo = '<%=adeCodigo%>';
        var clockAtual = parseInt(document.getElementById("clock").innerText.replace(':', ''));
        if (clock == clockAtual || clockAtual < "2"){
    	    postData('../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO=' + adeCodigo + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
        } else {
        	requisitaHistoricoMargem();
        }
      }
      
      function requisitaHistoricoMargem() {
    	$.ajax({
          type: 'POST',
          url: '../v3/listarHistoricoMargem?_skip_history_=true',
          data: {	              
              'rseCodigo' : '<%=TextHelper.forJavaScript(rseCodigo)%>',
              'adeNumero' : '<%=TextHelper.forJavaScript(adeNumero)%>',
              'marCodigo' : '<%=adeIncMargem%>'
          },
          success: function (data, status, error) {	        	  
        	  document.getElementById("historicoMargem").innerHTML = data;
          },
          error: function (request, status, error) {
        	  document.getElementById("historicoMargem").innerText = '';
          }
        });
      }

          
      
      <% if (exibirHistoricoMargem) { %>
        $( document ).ready(function() {	
      	if ( $( "#historicoMargem-tab" ).hasClass( "active" )) {		 
      		requisitaHistoricoMargem();	 
      	}
        });
        
        $('#historicoMargem-tab').on('click',function() {
      	  var margemHistContent = document.getElementById("historicoMargem").innerText;
      	  if ((margemHistContent == null || margemHistContent == '') ) {
      		validaSessao();
      	  }
      	});
      <%}%>
      
      function abrirModalReprovarDocumentacao() {
          $('#modalReprovarAnexoConsignacao').modal('show');
      }
      
      function abrirModaisSolicitacao(acao) {
          if (acao == "solicitar") {
              $('#solicitarSaldoModal').modal('show');
          } else if (acao == "solicitar_liq") {
              $('#solicitarSaldoLiqModal').modal('show');
          } else if (acao == "simular") {
              $('#simularSaldoModal').modal('show');
          }
      }
      
      function confirmarReprovarAnexoConsignacao() {
          var adeCodigo = '<%=adeCodigo%>';
          var obs = $('#editfield').val();
          postData('../v3/listarSolicitacao?acao=reprovarDocumentacao&telaEdicao=true&ADE_CODIGO=' + adeCodigo + '&obs_reprovacao=' + obs + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
          $('#modalReprovarAnexoConsignacao').modal('hide');
      }
      
      function solicitarSaldoConsulta() {
          var adeCodigo = '<%=adeCodigo%>';
          var sdvTelefone = $('#editfieldSolicitar').val();
          $('#solicitarSaldoModal').modal('hide');
        	postData('../v3/solicitarSaldoDevedor?acao=solicitar&ADE_CODIGO=' + adeCodigo + '&sdv_telefone=' + sdvTelefone + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');     
      }
      
      function solicitarSaldoLiquidacao() {
          var adeCodigo = '<%=adeCodigo%>';
          var sdvTelefone = $('#editfieldLiquidar').val();
          $('#solicitarSaldoLiqModal').modal('hide');
      	postData('../v3/solicitarSaldoDevedor?acao=solicitar_liquidacao&ADE_CODIGO=' + adeCodigo + '&sdv_telefone=' + sdvTelefone + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');    	
      }
      
      function simularSaldo() {
          var adeCodigo = '<%=adeCodigo%>';
          var qtdParcelasSaldo = $('#qtdParcelasSaldo').length ? $('#qtdParcelasSaldo').val() : 0;
          var simulacaoParcial = $("#tipoSimulacaoParcial").length && $("#tipoSimulacaoParcial").is(":checked");
          var url = '../v3/solicitarSaldoDevedor?acao=consultar&ADE_CODIGO=' + adeCodigo + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>';
          if (simulacaoParcial) {
              if (qtdParcelasSaldo <= 0) {
                  alert('<hl:message key="mensagem.erro.simular.saldo.devedor.parcial.qtd.parcelas.zero"/>');
                  return false;
              }
              url += "&parcial=true&qtdParcelas=" + qtdParcelasSaldo;
          }
          $('#simularSaldoModal').modal('hide');
          postData(url);
      }
      
      function doIt(opt, ade) {
       qs = 'tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>' + '&ADE_CODIGO=' + ade;
       var msg = '', j;
       var mmu = '';
          switch (opt) {
               case 'hi':
                   j   = '../v3/consultarConsignacao?acao=detalharConsignacao&';
                   mmu += '&MM_update=true&barraAcoes=false';
                   break;    
               case 'eho':
                   j   = '../v3/consultarConsignacao?acao=detalharConsignacao&';
                   qs = qs + '&oculto=true';
                   break;
               case 'oho':
                   j   = '../v3/consultarConsignacao?acao=detalharConsignacao&';
                   qs = qs + '&oculto=false';
                   break;
      
               case 'CALCULAR_SALDO_DEVEDOR':
                   return abrirModaisSolicitacao("simular");
              
               case 'SOLICITAR_SALDO_DEVEDOR':
                  return abrirModaisSolicitacao("solicitar");
      
               case 'SOLICITAR_SALDO_DEVEDOR_PARA_LIQ':
                  return abrirModaisSolicitacao("solicitar_liq");
                  
               case 'BOLETO_SALDO_DEVEDOR':
                   alert('<hl:message key="mensagem.alerta.pagina.externa.csa"/>');
                   window.open('', 'redirecionamento', 'height=450,width=800,status=no,toolbar=no,menubar=no,location=no,scrollbars=yes,left=200,top=200, rel="noopener noreferrer"');
                   postData('../v3/consultarConsignacao?acao=redirecionarBoletoSdv&ADE_CODIGO=' + ade + '&_skip_history_=true' + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>','redirecionamento');
                   return;
               case 'TABELA_PRICE':
                   openModalSubAcesso('../v3/consultarConsignacao?acao=exibirTabelaPrice&' + qs + '&_skip_history_=true' + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>')
                   return;             
                 
               case 'REPROVAR_ANEXO_CONSIGNACAO':
                   return abrirModalReprovarDocumentacao();             
      
             <%
             if (listaAcoes != null && !listaAcoes.isEmpty()) {
                  for (AcaoConsignacao acao : listaAcoes) {
                      if (!TextHelper.isNull(acao.getLink()) && !acao.getAcao().equals("REPROVAR_ANEXO_CONSIGNACAO") && !acao.getAcao().equals("SOLICITAR_SALDO_DEVEDOR") && !acao.getAcao().equals("SOLICITAR_SALDO_DEVEDOR_PARA_LIQ")) {
             %>
               case '<%= TextHelper.forJavaScriptBlock(acao.getAcao()) %>':
               
                   <% if (acao.getAcao().equals("REAT_CONSIGNACAO")) { %>
       
                        if (typeof verificarAdesDataFim !== 'undefined') {
                            var resultadoVerificar = verificarAdesDataFim('<%=adeCodigo%>'); 
                            if (!resultadoVerificar) {
                                return false;
                            }
                        }
       
                   <% } %>
       
                   msg = '<%= TextHelper.forJavaScriptBlock(acao.getMensagemConfirmacao()) %>';
                   j   = '<%= TextHelper.forJavaScriptBlock(acao.getLink()) %>&';
                   break;
      
             <%
                      }
                  }
             }
             %>            
      
               default:
                   return false;
                   break;  
              } 
      
          qs = qs + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>';
       
       if (msg == "" || confirm(msg)) {
         postData(j + qs + mmu);
       }
      }
      
      function dtlTerceiro(isOrigem, ade, adeDest, arquivado) {
          qs = 'tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>' + '&ADE_CODIGO=' + ade + '&adeDest=' + adeDest + '&isOrigem=' + isOrigem + '&arquivado=' + arquivado + '&barraAcoes=false' + '&BTN_voltar=<%=TextHelper.forJavaScriptBlock(JspHelper.verificaVarQryStr(request, "BTN_voltar"))%>';
          var j;
          var mmu = '&MM_update=true';
       
          j = '../v3/consultarConsignacao?acao=detalharConsignacao&';
          postData(j + qs + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>' + '&linkRet=<%=TextHelper.forJavaScriptBlock(linkRet)%>');
      }
      
      function verificarAdesDataFim(adeCodigo) {
          var msg = '<hl:message key="mensagem.reativar.consignacao.ades.prazo.final.contrato"/>\n\n';
          var adesCodigos = [adeCodigo];
          var adesNumeros = [];
      
          $.ajax({
              type: 'post',
              url: "../v3/verificarDataFim?_skip_history_=true",
              async : false,
              data: JSON.stringify(adesCodigos),
              contentType : 'application/json',
              success : function(data) {
                  adesNumeros = data.entity;
              },
          });
      
          if (adesNumeros.length > 0) {
              msg += adesNumeros.join(",");
      
              if (confirm(msg)) {
                  return true;
              } else {
                  alert('<hl:message key="mensagem.reativar.consignacao.ades.prazo.final.acao.cancelada"/>');
                  return false;
              }
          }
      
          return true;
      }
      
      function verificarDownload(adeCodigo, aadNome, dataReserva) {
      	$.post("../v3/verificarAnexoContratoConsignacao?arquivo_nome=" + aadNome + "&tipo=anexo&entidade=" + adeCodigo + "&data=" + dataReserva + "&_skip_history_=true", function(data) {
      	    try {
      	    	var jsonResult = $.trim(JSON.stringify(data));
      	        var obj = JSON.parse(jsonResult);
      	        var statusArquivo = obj.statusArquivo;
      
      	        if (statusArquivo) {
      	        	postData("../v3/downloadAnexoContratoConsignacao?arquivo_nome=" + aadNome + "&tipo=anexo&entidade=" + adeCodigo + "&data=" + dataReserva + "&_skip_history_=true");
      	        } else {
      	        	postData("../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO=" + adeCodigo + "&<%out.print(SynchronizerToken.generateToken4URL(request));%>");
      	        }
      	     } catch (err) {
      	     }
      	}, "json");
      }

      function criarComunicacao(codigoComunicacao, link) {
    	  var botaoEditarComunicacao = document.getElementById("editarComunicacao");
    	  botaoEditarComunicacao.setAttribute("onClick", "postData('../v3/enviarComunicacao?acao=editar&cmn_codigo=" + '<%=codigoComunicacao%>' + "&_skip_history_=true')");

    	  var botaoNovaComunicao = document.getElementById("novaComunicao");
    	  botaoNovaComunicao.setAttribute("onClick", "postData('"+ link + "&<%out.print(SynchronizerToken.generateToken4URL(request));%>')");
    	  $('#modalConfirmaComunicacao').modal('show');
      }
   </script>
</c:set>

<c:set var="pageModals">
  <t:modalSubAcesso>
    <jsp:attribute name="titulo"><hl:message key="mensagem.acao.exibe.tabela.price"/></jsp:attribute>
  </t:modalSubAcesso>
</c:set>
<t:page_v4>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
