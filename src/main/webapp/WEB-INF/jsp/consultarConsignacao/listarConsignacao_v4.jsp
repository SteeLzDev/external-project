<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.dto.entidade.MargemTO" %>
<%@ page import="com.zetra.econsig.dto.entidade.ParamSvcTO" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean geraMargem = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITAR_CONTROLE_DOCUMENTO_MARGEM, responsavel);

String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);

List<TransferObject> ades = (List<TransferObject>) request.getAttribute("lstConsignacao");
List<TransferObject> adesAtivo = (List<TransferObject>) request.getAttribute("lstConsignacaoAtivo");
List<TransferObject> adesInativo = (List<TransferObject>) request.getAttribute("lstConsignacaoInativo");
List<MargemTO> margens = request.getAttribute("margensServidor") != null ? (List<MargemTO>) request.getAttribute("margensServidor") : null;
String mensagemDataMargem = (request.getAttribute("mensagemDataMargem") != null ? request.getAttribute("mensagemDataMargem").toString() : "");
boolean exibeComboOperacoes = (request.getAttribute("exibeComboOperacoes") != null);
boolean exibeAtivoInativo = (boolean) (request.getAttribute("exibeAtivoInativo"));
boolean pesquisaAvancada = (boolean) (request.getAttribute("pesquisaAvancada"));
boolean adeNumerosVazio = (boolean) (request.getAttribute("adeNumerosVazio"));
boolean decisaoJudicialPesquisaAvancada = false;
boolean decisaoJudicialAdeAtivo = false;
boolean decisaoJudicialAdeInativo = false;

String tituloResultado = (request.getAttribute("tituloResultado") != null ? request.getAttribute("tituloResultado").toString() : "");
String footerResultado = (request.getAttribute("footerResultado") != null ? request.getAttribute("footerResultado").toString() : "");

boolean ocultarColunaCheckBox = (boolean) (request.getAttribute("ocultarColunaCheckBox"));
boolean ativarVerificaoDataFim = request.getAttribute("ativarVerificaoDataFim") != null ? (Boolean) request.getAttribute("ativarVerificaoDataFim") : false;

String rseCodigo = (String) request.getAttribute("rseCodigo");
String rseMatricula = (String) request.getAttribute("rseMatricula");
String serCpf = (String) request.getAttribute("serCpf");

String tipoOperacao = request.getAttribute("tipoOperacao") != null ? (String) request.getAttribute("tipoOperacao") : "";

List<AcaoConsignacao> listaAcoes = (List<AcaoConsignacao>) request.getAttribute("listaAcoes");
List<ColunaListaConsignacao> listaColunas = (List<ColunaListaConsignacao>) request.getAttribute("listaColunas");

String queryString = SynchronizerToken.updateTokenInURL(request.getAttribute("queryString") != null ? "?" + request.getAttribute("queryString").toString() : "", request);

int qtdMaxSelecaoMultipla = (request.getAttribute("qtdMaxSelecaoMultipla") != null ? Integer.valueOf(request.getAttribute("qtdMaxSelecaoMultipla").toString()) : Integer.MAX_VALUE);
String msgErroQtdMaxSelecaoMultiplaSuperada = (request.getAttribute("msgErroQtdMaxSelecaoMultiplaSuperada") != null ? request.getAttribute("msgErroQtdMaxSelecaoMultiplaSuperada").toString() : ApplicationResourcesHelper.getMessage("mensagem.erro.quantidade.maxima.selecao.multipla.atingida", responsavel));

int posicaoOrdenacao = 0;
boolean temCheckbox = false;
boolean ehDeferirIndeferir = false;
String acaoConfirmar = "";
Boolean exibeInativo = (Boolean) (request.getAttribute("exibeInativo"));
boolean existeCheckBox = request.getAttribute("existeCheckBox") != null ? (Boolean) (request.getAttribute("existeCheckBox")) : false;
boolean checkAllCheckBox = request.getAttribute("checkAllCheckBox") != null ? (Boolean) (request.getAttribute("checkAllCheckBox")) : false;
boolean temAdeDisponivel = request.getAttribute("temAdeDisponivel") != null ? (Boolean) (request.getAttribute("temAdeDisponivel")) : true;
boolean exigeCaptcha = false;
boolean exibeCaptchaAvancado = false;
boolean exibeCaptchaDeficiente = false;
if(responsavel.isSer()) {
        exigeCaptcha = request.getAttribute("exigeCaptcha") != null && (boolean) request.getAttribute("exigeCaptcha");
        exibeCaptchaAvancado = request.getAttribute("exibeCaptchaAvancado") != null && (boolean) request.getAttribute("exibeCaptchaAvancado");
        exibeCaptchaDeficiente = request.getAttribute("exibeCaptchaDeficientee") != null && (boolean) request.getAttribute("exibeCaptchaDeficientee");
}
String nomeColuna = "";
String marCodigos = !TextHelper.isNull(request.getAttribute("marCodigos")) ? (String) request.getAttribute("marCodigos") : "";

%>
<c:set var="title">
   <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm">

  <div id="header-print">
	<% if ("v4".equals(versaoLeiaute)) { %>
		<img src="../img/econsig-logo.svg">
	<% } else { %>
		<img src="../img/logo_sistema_v5.png">
	<%} %>
	<p id="date-time-print"></p>
  </div>
<% if (margens != null && !exigeCaptcha) { %>
    <div class="card" id="consultaMargem">
        <div class="card-header hasIcon">
            <span class="card-header-icon"><svg width="26"><use xlink:href="../img/sprite.svg#i-margem"></use></svg></span>
             <div class="row">
              <div class="col-sm-7 col-md-9 col-12 ">
                <h2 class="card-header-title">
                    <c:choose>
                        <c:when test="${not empty tituloResultado}"><%=(tituloResultado.equals(ApplicationResourcesHelper.getMessage("rotulo.pesquisa.listar.todos", responsavel).toUpperCase()) ? ApplicationResourcesHelper.getMessage("rotulo.pesquisa.listar.todos", responsavel) : TextHelper.forHtmlContent(tituloResultado)) %></c:when>
                        <c:otherwise><hl:message key="rotulo.consignacao.plural"/></c:otherwise>
                    </c:choose>
                </h2>
              </div>
            <div class="col div-header-two">
                <% if (!TextHelper.isNull(mensagemDataMargem)) { %>
                <span class="ultima-edicao"><%=TextHelper.forHtmlContent(mensagemDataMargem)%></span>
                <% } %>
              </div>
            </div>
        </div>
    <div class="card-body">
      <dl class="row data-list firefox-print-fix">
         <% for (MargemTO margem: margens) {
              if (margem.getMarDescricao() != null) {
                String valorMargem = "";
                if (margem.getMrsMargemRest() != null) {
                  String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(margem.getMarTipoVlr() != null ? margem.getMarTipoVlr().toString() : CodedValues.TIPO_VLR_FIXO);
                  String vlrMargem = NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang());
                  String obsMargem = (!TextHelper.isNull(margem.getObservacao()) ? " (" + margem.getObservacao() + ")" : "");
                  valorMargem = labelTipoVlr + " " + TextHelper.forHtmlContent(vlrMargem) + TextHelper.forHtmlContent(obsMargem);
                }
         %>
         <dt class="col-6"><%= TextHelper.forHtmlContent(margem.getMarDescricao()) %></dt>
         <dd class="col-6"><%= valorMargem %></dd>
         <%
              }
            }
         %>
      </dl>
  </div>
</div>
<% } else if (exigeCaptcha) { %>
        <input type="hidden" id="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>">
        <hl:modalCaptchaSer type="consultar"/>
        <div class="card">
            <div class="card-header hasIcon">
                <span class="card-header-icon"><svg width="26"><use xlink:href="../img/sprite.svg#i-margem"></use></svg></span>
                <div class="row">
                    <div class="col-sm-12 col-md-12 col-12">
                        <h2 class="card-header-title">
                            <c:choose>
                                <c:when test="${not empty tituloResultado}"><%=(tituloResultado.equals(ApplicationResourcesHelper.getMessage("rotulo.pesquisa.listar.todos", responsavel).toUpperCase()) ? ApplicationResourcesHelper.getMessage("rotulo.pesquisa.listar.todos", responsavel) : TextHelper.forHtmlContent(tituloResultado)) %></c:when>
                                <c:otherwise><hl:message key="rotulo.consignacao.plural"/></c:otherwise>
                            </c:choose>
                        </h2>
                        <span class="card-header-icon-consulta">
                    <a href="#" onclick="exibirmargem()" id="olhoMargemOculto">
                        <svg  width="30" height="30" class="icon-oculta-margem-simu">
                            <use xlink:href="#i-eye-slash-regular"></use>
                        </svg>
                    </a>
                    </span>
                    </div>
                </div>
            </div>
            <div class="card-body">
                <dl class="row data-list firefox-print-fix">
                    <dt class="col-6"><hl:message key="rotulo.reservar.margem.disponivel"/>:</dt>
                    <dd class="col-6"><hl:message key="rotulo.margem.moeda"/><hl:message key="rotulo.margem.disponivel.codigo"/></dd>
                </dl>
            </div>
        </div>
    </div>
<% } %>

<% if (exibeComboOperacoes) { %>
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" name="operacao" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
         <% if (responsavel.temPermissao(CodedValues.FUN_SUSP_CONSIGNACAO)) { %>
             <a class="dropdown-item" href="#no-back" id="suspender" onClick="if (confirmaOperacao('suspender')) { selecionaOperacao('suspender'); f0.submit(); }"><hl:message key="rotulo.suspender.consignacao.titulo"/></a>
         <% } %>
         <% if (responsavel.temPermissao(CodedValues.FUN_REAT_CONSIGNACAO)) { %>
             <a class="dropdown-item" href="#no-back" id="reativar" onClick="if (confirmaOperacao('reativar')) { selecionaOperacao('reativar'); f0.submit(); }"><hl:message key="rotulo.reativar.consignacao.titulo"/></a>
         <% } %>
         <div class="dropdown-divider" role="separator"></div>
         <a class="dropdown-item" href="#no-back" onClick="imprimir();"><hl:message key="rotulo.botao.imprimir"/></a>
         <% if(geraMargem && responsavel.isSer()) { %>
         <a class="dropdown-item" href="#no-back" onClick="gerarPDFConsultaMargem();"> <hl:message key="rotulo.botao.imprimir"/> <hl:message key="rotulo.margem.singular"/> </button>
         <% } %>
        </div>
      </div>
    </div>
  </div>
<% } else { %>
    <div class="page-title">
      <div class="row d-print-none">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <% if(geraMargem && responsavel.isSer()) { %>
            <button class="btn btn-primary" href="#no-back" onClick="gerarPDFConsultaMargem();"> <hl:message key="rotulo.botao.imprimir"/> <hl:message key="rotulo.margem.singular"/>  </button>
            <% } %>
            <button id="acoes" class="btn btn-primary" type="submit" onClick="imprimir()"><hl:message key="rotulo.botao.imprimir"/></button>
          </div>
        </div>
      </div>
    </div>
<% } %>
<% if (request.getAttribute("exibirPesquisaAvancada") != null  && responsavel.temPermissao(CodedValues.FUN_PESQUISA_AVANCADA_CONSIGNACAO)) { %>
  <%@ include file="../consultarConsignacao/incluirCampoPesquisaAvancada_v4.jsp" %>
<% } %>
    <form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>" method="post" name="formLista">
    <% if (ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_NAO_SELECIONADOS_PARTICIPAM_ALT_MULT_CONTRATOS, responsavel)){ %>
        <input type="hidden" name="marCodigos" id="marCodigos" value="<%=marCodigos%>">
    <% } %>

    <%= JspHelper.geraCamposHidden(queryString) %>
         <% if (!exibeAtivoInativo || pesquisaAvancada || !adeNumerosVazio) { %>
            <div class="card">
              <div class="card-header hasIcon">
                <span class="card-header-icon">
                  <svg width="24">
                      <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consignacao"></use>
                  </svg>
                </span>
                <h2 class="card-header-title">
                   <hl:message key="rotulo.consignacao.plural"/>
                   <% if(tipoOperacao.equals("deferir") || tipoOperacao.equals("indeferir")){ %>
                       <span> - <%= tituloResultado %> </span>
                   <% } %>
                   <div></div>
                </h2>
              </div>
                  <table id="dataTables" class="table table-striped table-hover w-100">
                      <thead>
                        <tr>
                     <%
                     if (listaAcoes != null && !listaAcoes.isEmpty()) {
                         for (AcaoConsignacao acao : listaAcoes) {
                             String idCheckBox = acao.getIdCheckbox();
                             if (!TextHelper.isNull(idCheckBox)) {
                                 posicaoOrdenacao = 1;
                                 if(idCheckBox.equals("chkDeferir") || idCheckBox.equals("chkIndeferir") ) {
                                   ehDeferirIndeferir = true;
                                   boolean ehDeferir = idCheckBox.equals("chkDeferir");
                     %>

                                 <th scope="col" width="10%" title="<%= acao.getDescricaoCompleta() %>" style="display: none;">
                                <div class="form-check">
                                  <input type="checkbox" class="form-check-input ml-0" id="<%= ehDeferir ? "checkAllDeferir" : "checkAllIndeferir" %>">
                                </div> <%= acao.getDescricao() %>
                              </th>

                     <%
                                } else if (tipoOperacao.equals("notificar") || tipoOperacao.equals("registrarValorLiberado")) {
                     %>

                                <th scope="col" width="3%" class="colunaUnica" style="<%= existeCheckBox ? "" : "display: none;" %>">
                                  <div class="form-check"><%= acao.getDescricao() %><br/>
                                    <input type="checkbox" class="form-check-input ml-0" name="<%="checkAll_" + idCheckBox %>" id="<%="checkAll_" + idCheckBox %>" data-bs-toggle="tooltip" data-original-title="<%= acao.getDescricaoCompleta() %>" alt="<%= acao.getDescricaoCompleta() %>" title="<%= acao.getDescricaoCompleta() %>" <%= checkAllCheckBox ? "checked='cheked'" : "" %>>
                                  </div>
                                </th>

                     <%
                                } else {
                     %>
                                    <th scope="col" width="3%" class="colunaUnica" style="display: none;">
                                      <div class="form-check"><%= acao.getDescricao() %><br/>
                                        <input type="checkbox" class="form-check-input ml-0" name="<%="checkAll_" + idCheckBox %>" id="<%="checkAll_" + idCheckBox %>" data-bs-toggle="tooltip" data-original-title="<%= acao.getDescricaoCompleta() %>" alt="<%= acao.getDescricaoCompleta() %>" title="<%= acao.getDescricaoCompleta() %>">
                                      </div>
                                    </th>
                     <%
                                  }
                            }
                        }
                     }
                     %>
                     <%
                     int i = 0;
                     if (listaColunas != null && !listaColunas.isEmpty()) {
                         for (ColunaListaConsignacao coluna : listaColunas) {
                     %>
                         <th scope="col"><%= TextHelper.forHtmlContentComTags(coluna.getTitulo()) %></th>
                     <%
                         }
                     }
                     %>
                         <th scope="col"><hl:message key="rotulo.acoes"/></th>
                        </tr>
                      </thead>
                      <tbody>
                        <%=JspHelper.msgRstVazio(ades.size() == 0, listaColunas.size() + 3, responsavel)%>

                     <%
                     for (TransferObject ade : ades) {
                         i = 0;
                         String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                         String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                         boolean exibirMensagemAdicional = (ade.getAttribute("EXIBIR_MENSAGEM_ADICIONAL") != null);
                         boolean usarLinkAdicionalOperacao = (ade.getAttribute("USAR_LINK_ADICIONAL") != null);
                         boolean isSolicitacaoLiquidacao = (ade.getAttribute("VRF_SOLICITACAO_LIQUIDACAO") != null && ade.getAttribute("VRF_SOLICITACAO_LIQUIDACAO").equals("true"));
                         boolean isPossuiDadoAdicionalDecisaoJudicial = ade.getAttribute(Columns.DAD_VALOR + CodedValues.TDA_AFETADA_DECISAO_JUDICIAL) != null && ade.getAttribute(Columns.DAD_VALOR + CodedValues.TDA_AFETADA_DECISAO_JUDICIAL).equals(CodedValues.TDA_SIM);
                         boolean isCheck = ((ade.getAttribute(Columns.ADE_DATA_NOTIFICACAO_CSE) != null) && tipoOperacao.equals("notificar")) || ((ade.getAttribute(Columns.ADE_DATA_LIBERACAO_VALOR) != null) && tipoOperacao.equals("registrarValorLiberado"));
                         boolean desabilitarAcao = false;
                     %>

                         <tr class="selecionarLinha <%= isCheck ? "table-checked" : ""%> %>">


                    <%
                         if (listaAcoes != null && !listaAcoes.isEmpty()) {
                             for (AcaoConsignacao acao : listaAcoes) {
                                 String chkNome = acao.getIdCheckbox();
                                 if (!TextHelper.isNull(chkNome)) {
                                     temCheckbox = true;
                                     if (!TextHelper.isNull(acao.getLink())) {
                                         acaoConfirmar = acao.getAcao();
                                     }
                                     if(chkNome.equals("chkDeferir") || chkNome.equals("chkIndeferir") ) {

                                       boolean ehDeferir = chkNome.equals("chkDeferir");
                     %>
                                  <td class="ocultarColunaDupla" aria-label="<%= acao.getDescricaoCompleta() %>" title="<%= acao.getDescricaoCompleta() %>" style="display: none;">
                                <div class="form-check">
                                  <input type="checkbox" class="form-check-input ml-0" name="<%= ehDeferir ? "chkDeferir" : "chkIndeferir"%>" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" data-exibe-msg2="<%= exibirMensagemAdicional ? "1" : "0" %>" data-usa-link2="<%= usarLinkAdicionalOperacao ? "1" : "0" %>">
                                </div>
                              </td>
                     <%
                                 } else if (tipoOperacao.equals("notificar") || tipoOperacao.equals("registrarValorLiberado")) {
                     %>

                                   <td class="colunaUnica" aria-label="<%= acao.getDescricaoCompleta() %>" title="<%= acao.getDescricaoCompleta() %>" data-bs-toggle="tooltip" data-original-title="<%= acao.getDescricaoCompleta() %>" style="<%= existeCheckBox ? "" : "display: none;" %> ">
                                      <div class="form-check">
                                        <input type="checkbox" class="form-check-input ml-0" name="<%=chkNome %>" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" data-exibe-msg2="<%= exibirMensagemAdicional ? "1" : "0" %>" data-usa-link2="<%= usarLinkAdicionalOperacao ? "1" : "0" %>" <%= isCheck ? "checked='checked'" : "" %>>
                                      </div>
                                   </td>

                     <%
                                 } else {
                     %>
                                   <td class="colunaUnica" aria-label="<%= acao.getDescricaoCompleta() %>" title="<%= acao.getDescricaoCompleta() %>" data-bs-toggle="tooltip" data-original-title="<%= acao.getDescricaoCompleta() %>" style="display: none;">
                                      <div class="form-check">
                                        <input type="checkbox" class="form-check-input ml-0" name="<%=chkNome %>" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" data-exibe-msg2="<%= exibirMensagemAdicional ? "1" : "0" %>" data-usa-link2="<%= usarLinkAdicionalOperacao ? "1" : "0" %>">
                                      </div>
                                   </td>
                     <%
                                     }
                                 }
                             }
                         }
                     %>
                     <%
                         if (listaColunas != null && !listaColunas.isEmpty()) {
                               for (ColunaListaConsignacao coluna : listaColunas) {
                                   String valorCampo = (String) ade.getAttribute(coluna.getChaveCampo());
                                   String valorCampoAbbr = (String) ade.getAttribute(coluna.getChaveCampo() + "_ABREVIATURA");
                                   String simboloCampo = (String) ade.getAttribute(coluna.getChaveCampo() + "_SIMBOLO");
                                   desabilitarAcao |= coluna.isAcaoDesabilitada(ade);
                                   String hasIcon = "";
                                   if (ade.getAttribute("POSSUI_DECISAO_JUDICIAL") != null && ade.getAttribute("POSSUI_DECISAO_JUDICIAL").equals(CodedValues.DECISAO_JUDICIAL_SIM) && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                       valorCampo = valorCampo;
                                       hasIcon += "<span class='badge text-bg-pink'>DJ</span>";
                                       decisaoJudicialAdeAtivo = true;
                                   }
                                   if (ade.getAttribute("IMPORTADO") != null && ade.getAttribute("IMPORTADO").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                       valorCampo = valorCampo;
                                       hasIcon += "<span class='badge text-bg-pink'>I</span> ";
                                   }
                                   if (ade.getAttribute("SUSPENSO") != null && ade.getAttribute("SUSPENSO").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                       valorCampo = valorCampo;
                                       hasIcon += "<span class='badge text-bg-pink'>S</span> ";
                                   }
                                   if (ade.getAttribute("REFINANCIADO") != null && ade.getAttribute("REFINANCIADO").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                       valorCampo = valorCampo;
                                       hasIcon += "<span class='badge text-bg-pink'>R</span> ";
                                   }
                                   if (ade.getAttribute("PORTABILIDADE") != null && ade.getAttribute("PORTABILIDADE").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                       valorCampo = valorCampo;
                                       hasIcon += "<span class='badge text-bg-pink'>P</span> ";
                                   }
                                   if (coluna.isMonetario() && TextHelper.isNull(simboloCampo)) {
                                       simboloCampo = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel);
                                   }
                                   if (coluna.isMonetario() && !TextHelper.isNull(valorCampo)) {
                                       valorCampo = simboloCampo + " " + valorCampo;
                                   }
                                   if (TextHelper.isNull(valorCampo)) {
                                       valorCampo = "-";
                                   }
                                   if (!TextHelper.isNull(valorCampoAbbr)) {
                                   %>
                                     <td class="selecionarColuna"><%=TextHelper.forHtmlContent(valorCampoAbbr)%> <%=hasIcon%></td>
                                   <%
                                   } else if (coluna.isMonetario() || coluna.isNumerico()) {
                                   %>
                                     <td class="selecionarColuna"><%=TextHelper.forHtmlContent(valorCampo)%> <%=hasIcon%></td>
                                   <%
                                   } else {
                                   %>
                                     <td class="selecionarColuna"><%=TextHelper.forHtmlContent(valorCampo)%> <%=hasIcon%></td>
                                   <%
                                   }
                               }
                          }
                      %>
                      <%
                          if (listaAcoes != null && !listaAcoes.isEmpty()) {
                      %>
                             <td class="acoes">
                      <%
                               if(listaAcoes.size() > 1){
                    %>

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

                      <%
                                  for (AcaoConsignacao acao : listaAcoes) {
                                        String chkNome = acao.getIdCheckbox();
                                        if (acao.getAcao().equals("CANC_SOLICITACAO_LIQUIDACAO") && (!isSolicitacaoLiquidacao || !responsavel.temPermissao(CodedValues.FUN_CANC_SOLICITAR_LIQUIDAR_CONSIGNACAO))) {
                                            continue;
                                        }
                                        if ((acao.getAcao().equals("AUTORIZAR_DECISAO_JUDICIAL") && isPossuiDadoAdicionalDecisaoJudicial) || (acao.getAcao().equals("DESAUTORIZAR_DECISAO_JUDICIAL") && !isPossuiDadoAdicionalDecisaoJudicial)){
                                            continue;
                                        }
                                        if (acao.getAcao().equals("RENE_CONTRATO") && desabilitarAcao) {
                    %>
                                          <span class="dropdown-item indisponivel"><hl:message key="rotulo.status.indisponivel"/></span>
                    <%
                                        } else if (acao.getAcao().equals("LISTAR_CONSIGNACAO_SER")) {
                     %>     
                                            <a class="dropdown-item" href="#no-back" onclick ="listarConsignacaoSer('<%=TextHelper.forJavaScript(tipoOperacao)%>','<%=TextHelper.forJavaScript(adeCodigo)%>')"><%= acao.getDescricaoCompleta() %></a>
                    <%
                                        } else if (!TextHelper.isNull(chkNome)) {
                                          if (chkNome.equals("chkDeferir") || chkNome.equals("chkIndeferir")){
                                            boolean ehDeferir = chkNome.equals("chkDeferir");
                    %>
                                          <a class="dropdown-item" href="#" name="<%= ehDeferir ? "selecionaAcaoDeferir" : "selecionaAcaoIndeferir" %>"><%= acao.getDescricaoCompleta() %></a>
                    <%
                                          } else {
                    %>
                                          <a class="dropdown-item" href="#" onclick ="escolhechk('<%=TextHelper.forHtmlContent(acao.getDescricaoCompleta())%>',this)"><%= acao.getDescricaoCompleta() %></a>
                    <%
                                          }
                                      } else {
                    %>
                                          <div class="position-relative">
                                            <a href="#no-back" class="dropdown-item" onClick="<%=TextHelper.forJavaScript(acao.getAcao())%>('<%=TextHelper.forJavaScript(adeCodigo)%>');">
                                              <%= acao.getDescricaoCompleta() %>
                                            </a>
                                          </div>
                    <%
                                      }
                                  }
                    %>
                                </div>
                              </div>
                            </div>
                      <%
                               } else {
                                for (AcaoConsignacao acao : listaAcoes) {
                                    if (acao.getAcao().equals("CANC_SOLICITACAO_LIQUIDACAO") && (!isSolicitacaoLiquidacao || !responsavel.temPermissao(CodedValues.FUN_CANC_SOLICITAR_LIQUIDAR_CONSIGNACAO))) {
                                        continue;
                                    }
                                    if (desabilitarAcao) {
                      %>
                                  <div class="position-relative">
                                      <span class="indisponivel"><hl:message key="rotulo.status.indisponivel"/></span><span class="sr-only"><hl:message key="rotulo.consignacao.abreviado"/>&nbsp;<%=TextHelper.forHtmlContent(adeNumero)%></span>
                                  </div>
                      <%
                                    } else if (acao.getAcao().equals("LISTAR_CONSIGNACAO_SER")) {
                     %>     
                                        <a class="dropdown-item" href="#no-back" onclick ="listarConsignacaoSer('<%=TextHelper.forJavaScript(tipoOperacao)%>','<%=TextHelper.forJavaScript(adeCodigo)%>')"><%= acao.getDescricaoCompleta() %></a>
                	<%
                                    }
                                    
                                    else {
                      %>
                                  <div class="position-relative">
                                    <a href="#no-back" onClick="decideAcao('<%=TextHelper.forJavaScript(acao.getAcao())%>','<%=TextHelper.forJavaScript(adeCodigo)%>','<%=TextHelper.forHtmlContent(acao.getDescricaoCompleta())%>',this)">
                                      <%= acao.getDescricaoCompleta() %>
                                    </a>
                                  </div>
                      <%
                                    }
                                }
                            }
                      %>
                             </td>
                      <% } %>
                        </tr>
                      <%
                      }
                      %>
                      </tbody>
                    </table>
                  </div>
        <% } else { %>
           <ul id="active-buttons" class="nav nav-tabs responsive-tabs" role="tablist">
               <li class="nav-item nav-link mr-1 <%= !exibeInativo ? "active" : "" %>" onclick="postData('../v3/consultarConsignacao?acao=pesquisarConsignacao&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&RSE_MATRICULA=<%=TextHelper.forJavaScript(rseMatricula)%>&SER_CPF=<%=TextHelper.forJavaScript(serCpf)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.status.ativo"/></button>
               <li class="nav-item nav-link <%= exibeInativo ? "active" : "" %>" onclick="postData('../v3/consultarConsignacao?acao=pesquisarConsignacao&exibeInativo=1&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&RSE_MATRICULA=<%=TextHelper.forJavaScript(rseMatricula)%>&SER_CPF=<%=TextHelper.forJavaScript(serCpf)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.status.inativo.consignacao"/></button>
           </ul>
           <div class="tab-content table-responsive" id="consignacaoInfo">
            <div class="card">
           <%int i = 0;
              if(!exibeInativo){ %>
                 <div class="tab-pane fade show active" id="ativo" role="tabpanel" aria-labelledby="ativo-tab">
                   <table id="dataTables" class="table table-striped table-hover w-100">
                     <thead>
                       <tr>
                    <%
                    if (listaColunas != null && !listaColunas.isEmpty()) {
                        for (ColunaListaConsignacao coluna : listaColunas) {
                    %>
                        <th scope="col"><%= TextHelper.forHtmlContentComTags(coluna.getTitulo()) %></th>
                    <%
                        }
                    }
                    %>
                        <th scope="col"><hl:message key="rotulo.acoes"/></th>
                       </tr>
                     </thead>
                     <tbody>
                       <%=JspHelper.msgRstVazio(adesAtivo.size() == 0, listaColunas.size() + 3, responsavel)%>

                    <%
                    for (TransferObject ade : adesAtivo) {
                        i = 0;
                        String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                        String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                        boolean exibirMensagemAdicional = (ade.getAttribute("EXIBIR_MENSAGEM_ADICIONAL") != null);
                        boolean usarLinkAdicionalOperacao = (ade.getAttribute("USAR_LINK_ADICIONAL") != null);
                        String adeSadCodigo = ade.getAttribute(Columns.SAD_CODIGO).toString();
                        boolean isSolicitacaoLiquidacao = (ade.getAttribute("VRF_SOLICITACAO_LIQUIDACAO") != null && ade.getAttribute("VRF_SOLICITACAO_LIQUIDACAO").equals("true"));
                    %>

                        <tr class="selecionarLinha">
                    <%
                        if (listaColunas != null && !listaColunas.isEmpty()) {
                              for (ColunaListaConsignacao coluna : listaColunas) {
                                  String valorCampo = (String) ade.getAttribute(coluna.getChaveCampo());
                                  String valorCampoAbbr = (String) ade.getAttribute(coluna.getChaveCampo() + "_ABREVIATURA");
                                  String simboloCampo = (String) ade.getAttribute(coluna.getChaveCampo() + "_SIMBOLO");
                                  String hasIcon = "";
                                  if (ade.getAttribute("POSSUI_DECISAO_JUDICIAL") != null && ade.getAttribute("POSSUI_DECISAO_JUDICIAL").equals(CodedValues.DECISAO_JUDICIAL_SIM) && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                      valorCampo = valorCampo;
                                      hasIcon += "<span class='badge text-bg-pink'>DJ</span>";
                                      decisaoJudicialAdeAtivo = true;
                                  }
                                  if (ade.getAttribute("IMPORTADO") != null && ade.getAttribute("IMPORTADO").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                      valorCampo = valorCampo;
                                      hasIcon += "<span class='badge text-bg-pink'>I</span> ";
                                  }
                                  if (ade.getAttribute("SUSPENSO") != null && ade.getAttribute("SUSPENSO").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                      valorCampo = valorCampo;
                                      hasIcon += "<span class='badge text-bg-pink'>S</span> ";
                                  }
                                  if (ade.getAttribute("REFINANCIADO") != null && ade.getAttribute("REFINANCIADO").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                      valorCampo = valorCampo;
                                      hasIcon += "<span class='badge text-bg-pink'>R</span> ";
                                  }
                                  if (ade.getAttribute("PORTABILIDADE") != null && ade.getAttribute("PORTABILIDADE").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                     valorCampo = valorCampo;
                                     hasIcon += "<span class='badge text-bg-pink'>P</span> ";
                                  }
                                  if (coluna.isMonetario() && TextHelper.isNull(simboloCampo)) {
                                      simboloCampo = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel);
                                  }
                                  if (coluna.isMonetario() && !TextHelper.isNull(valorCampo)) {
                                      valorCampo = simboloCampo + " " + valorCampo;
                                  }
                                  if (TextHelper.isNull(valorCampo)) {
                                      valorCampo = "-";
                                  }
                                  if (!TextHelper.isNull(valorCampoAbbr)) {
                                  %>
                                    <td class="selecionarColuna"><%=TextHelper.forHtmlContent(valorCampoAbbr)%> <%=hasIcon%></td>
                                  <%
                                  } else if (coluna.isMonetario() || coluna.isNumerico()) {
                                  %>
                                    <td class="selecionarColuna"><%=TextHelper.forHtmlContent(valorCampo)%> <%=hasIcon%></td>
                                  <%
                                  } else {
                                  %>
                                    <td class="selecionarColuna"><%=TextHelper.forHtmlContent(valorCampo)%> <%=hasIcon%></td>
                                  <%
                                  }
                              }
                         }
                     %>
                     <%
                         if (listaAcoes != null && !listaAcoes.isEmpty()) {
                     %>
                            <td class="acoes">
                     <%
                               for (AcaoConsignacao acao : listaAcoes) {
                                   if (acao.getAcao().equals("CANC_SOLICITACAO_LIQUIDACAO") && (!isSolicitacaoLiquidacao || !responsavel.temPermissao(CodedValues.FUN_CANC_SOLICITAR_LIQUIDAR_CONSIGNACAO))) {
                                       continue;
                                   }
                     %>
                                 <div class="position-relative">
                                   <a href="#no-back" onClick="decideAcao('<%=TextHelper.forJavaScript(acao.getAcao())%>','<%=TextHelper.forJavaScript(adeCodigo)%>','<%=TextHelper.forHtmlContent(acao.getDescricaoCompleta())%>',this)">
                                     <%= acao.getDescricaoCompleta() %>
                                   </a>
                                 </div>
                     <%
                             }
                     %>
                            </td>
                     <% } %>
                       </tr>
                     <%
                     }
                     %>
                     </tbody>
                   </table>
                   <% request.setAttribute("_indice", "Ativo");{%>
                   <% } %>
                   </div>
             <%} else { %>
                   <div class="tab-pane <%= exibeInativo ? "fade show active" : "" %>" id="inativo" role="tabpanel" aria-labelledby="inativo-tab">
                     <table id="dataTablesInativo" class="table table-striped table-hover w-100">
                       <thead>
                         <tr>
                      <%
                      i = 0;
                      if (listaColunas != null && !listaColunas.isEmpty()) {
                          for (ColunaListaConsignacao coluna : listaColunas) {
                      %>
                          <th scope="col"><%= TextHelper.forHtmlContentComTags(coluna.getTitulo()) %></th>
                      <%
                          }
                      }
                      %>
                          <th scope="col"><hl:message key="rotulo.acoes"/></th>
                         </tr>
                       </thead>
                       <tbody>
                         <%=JspHelper.msgRstVazio(adesInativo.size() == 0, listaColunas.size() + 3, responsavel)%>

                      <%
                      for (TransferObject ade : adesInativo) {
                          i = 0;
                          String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                          String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                          boolean exibirMensagemAdicional = (ade.getAttribute("EXIBIR_MENSAGEM_ADICIONAL") != null);
                          boolean usarLinkAdicionalOperacao = (ade.getAttribute("USAR_LINK_ADICIONAL") != null);
                          String adeSadCodigo = ade.getAttribute(Columns.SAD_CODIGO).toString();
                          boolean isSolicitacaoLiquidacao = (ade.getAttribute("VRF_SOLICITACAO_LIQUIDACAO") != null && ade.getAttribute("VRF_SOLICITACAO_LIQUIDACAO").equals("true"));
                      %>

                          <tr class="selecionarLinha">
                      <%
                          if (listaColunas != null && !listaColunas.isEmpty()) {
                                for (ColunaListaConsignacao coluna : listaColunas) {
                                    String valorCampo = (String) ade.getAttribute(coluna.getChaveCampo());
                                    String valorCampoAbbr = (String) ade.getAttribute(coluna.getChaveCampo() + "_ABREVIATURA");
                                    String simboloCampo = (String) ade.getAttribute(coluna.getChaveCampo() + "_SIMBOLO");
                                    String hasIcon = "";
                                    if (ade.getAttribute("POSSUI_DECISAO_JUDICIAL") != null && ade.getAttribute("POSSUI_DECISAO_JUDICIAL").equals(CodedValues.DECISAO_JUDICIAL_SIM) && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                        valorCampo = valorCampo;
                                        hasIcon += "<span class='badge text-bg-pink'>DJ</span> ";
                                        decisaoJudicialAdeInativo = true;
                                    }
                                    if (ade.getAttribute("IMPORTADO") != null && ade.getAttribute("IMPORTADO").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                        valorCampo = valorCampo;
                                        hasIcon += "<span class='badge text-bg-pink'>I</span> ";
                                    }
                                    if (ade.getAttribute("SUSPENSO") != null && ade.getAttribute("SUSPENSO").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                        valorCampo = valorCampo;
                                        hasIcon += "<span class='badge text-bg-pink'>S</span> ";
                                    }
                                    if (ade.getAttribute("REFINANCIADO") != null && ade.getAttribute("REFINANCIADO").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                        valorCampo = valorCampo;
                                        hasIcon += "<span class='badge text-bg-pink'>R</span> ";
                                    }
                                    if (ade.getAttribute("PORTABILIDADE") != null && ade.getAttribute("PORTABILIDADE").equals("S") && coluna.getChaveCampo().equals("listaConsignacao_numero")){
                                        valorCampo = valorCampo;
                                        hasIcon += "<span class='badge text-bg-pink'>P</span> ";
                                    }
                                    if (coluna.isMonetario() && TextHelper.isNull(simboloCampo)) {
                                        simboloCampo = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel);
                                    }
                                    if (coluna.isMonetario() && !TextHelper.isNull(valorCampo)) {
                                        valorCampo = simboloCampo + " " + valorCampo;
                                    }
                                    if (TextHelper.isNull(valorCampo)) {
                                        valorCampo = "-";
                                    }
                                    if (!TextHelper.isNull(valorCampoAbbr)) {
                                    %>
                                      <td class="selecionarColuna"><%=TextHelper.forHtmlContent(valorCampoAbbr)%> <%=hasIcon%></td>
                                    <%
                                    } else if (coluna.isMonetario() || coluna.isNumerico()) {
                                    %>
                                      <td class="selecionarColuna"><%=TextHelper.forHtmlContent(valorCampo)%> <%=hasIcon%></td>
                                    <%
                                    } else {
                                    %>
                                      <td class="selecionarColuna"><%=TextHelper.forHtmlContent(valorCampo)%> <%=hasIcon%></td>
                                    <%
                                    }
                                }
                           }
                       %>
                       <%
                           if (listaAcoes != null && !listaAcoes.isEmpty()) {
                       %>
                              <td class="acoes">
                       <%
                                 for (AcaoConsignacao acao : listaAcoes) {
                                     if (acao.getAcao().equals("CANC_SOLICITACAO_LIQUIDACAO") && (!isSolicitacaoLiquidacao || !responsavel.temPermissao(CodedValues.FUN_CANC_SOLICITAR_LIQUIDAR_CONSIGNACAO))) {
                                         continue;
                                     }
                       %>
                                   <div class="position-relative">
                                     <a href="#no-back" onClick="decideAcao('<%=TextHelper.forJavaScript(acao.getAcao())%>','<%=TextHelper.forJavaScript(adeCodigo)%>','<%=TextHelper.forHtmlContent(acao.getDescricaoCompleta())%>',this)">
                                       <%= acao.getDescricaoCompleta() %>
                                     </a>
                                   </div>
                       <%
                               }
                       %>
                              </td>
                       <% } %>
                         </tr>
                       <%
                       }
                       %>
                       </tbody>
                     </table>
                     <% request.setAttribute("_indice", "Inativo");{%>
                     <% } %>
                   </div>
                   <%} %>
             	</div>
             </div>
        <% } %>
      </form>
      <div class="legendas">
              <h2 class="legenda-head-consultar"><hl:message key="rotulo.legendas.plural"/></h2>
              <div class="legenda-body">
                  <p class="legenda-item"><span class="badge text-bg-pink"><hl:message
                          key="rotulo.legenda.suspenso.abrev"/></span><span class="m-1"><hl:message
                          key="rotulo.legenda.suspenso"/></span></p>
                  <p class="legenda-item"><span class="badge text-bg-pink"><hl:message
                          key="rotulo.legenda.decisao.judicial.abrev"/></span><span class="m-1"><hl:message
                          key="rotulo.legenda.decisao.judicial"/></span></p>
                  <p class="legenda-item"><span class="badge text-bg-pink"><hl:message
                          key="rotulo.legenda.importado.abrev"/></span><span class="m-1"><hl:message
                          key="rotulo.legenda.importado"/></span></p>
                  <p class="legenda-item"><span class="badge text-bg-pink"><hl:message
                          key="rotulo.legenda.portabilidade.abrev"/></span><span class="m-1"><hl:message
                          key="rotulo.legenda.portabilidade"/></span></p>
                  <p class="legenda-item"><span class="badge text-bg-pink"><hl:message
                          key="rotulo.legenda.refinanciado.abrev"/></span><span class="m-1"><hl:message
                          key="rotulo.legenda.refinanciado"/></span></p>
              </div>
          </div>
      <input type="hidden" name="adeCodigo" id="adeCodigo">
      <div class="modal fade" id="confirmarRetirarPortabilidade" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
        <div class="modal-dialog modalTermoUso" role="document">
          <div class="modal-content p-3">
            <div class="modal-header">
              <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.acoes.retirar.contrato.compra"/></h5>
            </div>
            <div class="form-group modal-body m-0">
              <span class="modal-title mb-0" id="subTitulo"> <hl:message key="mensagem.consultar.consignacao.informe.observacao"/></span>
              <br>
              <label for="editfield"><hl:message key="rotulo.avancada.adeObs"/></label>
              <textarea class="form-control" id="editfield" name="editfield" rows="3" cols="28"></textarea>
            </div>
            <div class="ui-dialog-buttonset mb-3">
            <button type="button" class="btn btn-primary ml-4 mr-3 float-end" id="btnConfirmarRetirarAdeCompra" onClick="confirmarRetirarPortabilidade()"><hl:message key="rotulo.botao.confirmar"/></button>
            <button type="button" class="btn btn-outline-danger ml-4 float-end" id="voltarModalRetirar"><hl:message key="rotulo.botao.cancelar"/></button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal fade" id="modalReprovarAnexoConsignacao" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
        <div class="modal-dialog modalTermoUso" role="document">
          <div class="modal-content p-3">
            <div class="modal-header">
              <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="mensagem.reprovar.documentacao.solicitacao"/></h5>
            </div>
            <div class="form-group modal-body m-0">
              <span class="modal-title mb-0" id="subTitulo"> <hl:message key="mensagem.informar.reprovacao.documentos.solicitacao"/></span>
              <br>
              <label for="obsReprovaAnexoConsignacao"><hl:message key="rotulo.avancada.adeObs"/></label>
              <textarea class="form-control" id="obsReprovaAnexoConsignacao" name="obsReprovaAnexoConsignacao" rows="3" cols="28"></textarea>
            </div>
            <div class="ui-dialog-buttonset mb-3">
            <button type="button" class="btn btn-primary ml-4 mr-3 float-end" id="btnReprovarAnexoConsignacao" onClick="confirmarReprovarAnexoConsignacao()"><hl:message key="rotulo.botao.confirmar"/></button>
            <button type="button" class="btn btn-outline-danger ml-4 float-end" id="voltarModalReprovarDoc"><hl:message key="rotulo.botao.cancelar"/></button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal fade" id="modalListarConsignacaoSer" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" >
	     <div class="modal-dialog modal-xxl center-modal" role="document">
	       <div class="modal-content p-3">
	         <div class="modal-header">
	           <span class="modal-title about-title mb-0" id="exampleModalLabel"><hl:message key="rotulo.consultar.consignacao.titulo"/></span>
	           <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
	             <span aria-hidden="true">&times;</span>
	           </button>
	         </div>
	        <div class="modal-body" id="modalListarConsignacaoSerBody">         	
	        </div>
	        <div class="modal-footer pt-0">
	          <div class="btn-action mt-2 mb-0">
	            <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>">
	              <hl:message key="rotulo.botao.cancelar" />
	            </a>
	          </div>
	        </div>
	      </div>
	    </div>
	  </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL((responsavel.isSer() ? "../v3/carregarPrincipal" : paramSession.getLastHistory()), request))%>');"><hl:message key="rotulo.botao.voltar"/></a>
        <% if (temCheckbox && temAdeDisponivel && !TextHelper.isNull(acaoConfirmar)) { %>
        <a class="btn btn-primary" href="#confirmarSenha" data-bs-toggle="modal" onClick="<%=TextHelper.forJavaScript(acaoConfirmar)%>();">
          <svg width="17">
            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar" onClick="<%=TextHelper.forJavaScript(acaoConfirmar)%>();"></use>
          </svg><hl:message key="rotulo.botao.confirmar"/>
        </a>
        <% } %>
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
    </div>
  </div>
</c:set>
<c:set var="javascript">
<style>
  @media print {
	*{
		padding: 0;
		margin: 0;
	}
	body{color: #000 !important}
    #dataTables th:last-child {display: none;}
    #dataTables td:last-child {display: none;}
	#dataTables_length {display: none;}
	#dataTables_paginate {display: none;}
	#dataTables_filter {display: none;}
	#dataTables_info {display: none;}
    #active-buttons {display: none;}
    #menuAcessibilidade {display: none;}
    #footer-print {position: absolute; bottom: 0;}
    #header-print img{width: 10%;}
    .opcoes-avancadas {display: none;}
    .table thead tr th, .table tbody tr td {
	    font-size: 12px;
	    line-height: 1.25;
	    padding-top: 0;
	    padding-bottom: 0;
	    padding-left: .25rem;
	    padding-right: .25rem;
	    border: 1px solid #000 !important;
	    color: #000 !important;
     }
  }
  @page{margin: 1cm;}
  
	 .modal-xxl {
		max-width: 95%;		
	}
	
	.center-modal {
		position: relative;
		bottom: 20%;
	}

</style>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
  <script  src="../node_modules/jszip/dist/jszip.min.js"></script>
  <script  src="../node_modules/pdfmake/build/pdfmake.min.js"></script>
  <script  src="../node_modules/pdfmake/build/vfs_fonts.js"></script>
  <script  src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
  <script  src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
  <script  src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
  <script  src="../node_modules/moment/min/moment.min.js"></script>
  <script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
    <% if (exibeCaptchaAvancado) { %>
    <script src='https://www.google.com/recaptcha/api.js'></script>
    <% } %>
  <script>
	function injectDate(){
		const dateTimePrint = document.querySelector('#date-time-print');
		const printDate = new Date();
		printDate.toLocaleString("pt-br");
		dateTimePrint.innerText = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);
	}

	function imprimir(){
		injectDate();
		window.print();
	}
  </script>
  <script type="text/JavaScript">
    $(document).ready(function() {
  	<%
	  	for (ColunaListaConsignacao colunaOrder : listaColunas) {
	  		if(colunaOrder.getTitulo().equals(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel))){
	  			break;
	  		}
	  		posicaoOrdenacao++;
	  	}
  	%>

      $.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );

      $('#dataTablesInativo').DataTable({
          "paging": true,
          "pageLength": 20,
          "lengthMenu": [
              [20, 50, 100, -1],
              [20, 50, 100, '<hl:message key="mensagem.datatables.all"/>']
          ],
          "order": [[<%=posicaoOrdenacao%>, "desc" ]],
          "pagingType": "simple_numbers",
          "dom": '<"row" <"col-sm-2" B > <"col-sm-6" l > <"col-sm-4" f >> <"table-responsive" t > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
          buttons: [
              'colvis'
          ],
          stateSave: true,
          stateSaveParams: function (settings, data) {
              data.search.search = "";
          },
          language: {
              search:            '_INPUT_',
              searchPlaceholder: '<hl:message key="mensagem.datatables.search.placeholder"/>',
              processing:        '<hl:message key="mensagem.datatables.processing"/>',
              loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
              lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
              info:              '<hl:message key="mensagem.datatables.info.consignatarias"/>',
              infoEmpty:         '<hl:message key="mensagem.datatables.info.empty"/>',
              infoFiltered:      '<hl:message key="mensagem.datatables.info.filtered"/>',
              infoPostFix:       '',
              zeroRecords:       '<hl:message key="mensagem.datatables.zero.records"/>',
              emptyTable:        '<hl:message key="mensagem.datatables.empty.table"/>',
              paginate: {
                  first:         '<hl:message key="mensagem.datatables.paginate.first.padrao.econsig"/>',
                  previous:      '<hl:message key="mensagem.datatables.paginate.previous.padrao.econsig"/>',
                  next:          '<hl:message key="mensagem.datatables.paginate.next.padrao.econsig"/>',
                  last:          '<hl:message key="mensagem.datatables.paginate.last.padrao.econsig"/>'
              },
              aria: {
                  sortAscending: '<hl:message key="mensagem.datatables.aria.sort.ascending"/>',
                  sortDescending:'<hl:message key="mensagem.datatables.aria.sort.descending"/>'
              },
              buttons: {
                  print :        '<hl:message key="mensagem.datatables.buttons.print"/>',
                  colvis :       '<hl:message key="mensagem.datatables.buttons.colvis"/>'
              },
              decimal: ","
          },
          initComplete: function () {
              var btns = $('.dt-button');
              btns.addClass('btn <%= ("v4".equals(versaoLeiaute) || versaoLeiaute == null) ? "btn-primary" : "btn-outline-danger" %> btn-sm');
              btns.removeClass('dt-button');
          }
      });

      $("#dataTablesInativo_filter").addClass('pt-3');
      $('#dataTablesInativo_info').addClass('p-3');
      $("#dataTablesInativo_length").addClass('pt-3');

      $('#dataTables').DataTable({
          "paging": true,
          "pageLength": 20,
          "lengthMenu": [
              [20, 50, 100, -1],
              [20, 50, 100, '<hl:message key="mensagem.datatables.all"/>']
          ],
          "order": [[ <%=posicaoOrdenacao%>, "desc" ]],
          "pagingType": "simple_numbers",
          <% if (!exibeAtivoInativo || pesquisaAvancada || !adeNumerosVazio) { %>
          "dom": '<"card-body p-0" <"row pl-0 pr-4" <"col-sm-2 pl-0" B > <"col-sm-6 pl-0" l > <"col-sm-4 pr-0" f >> <"table-responsive" t> > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
          <%}else{%>
          "dom": '<"row" <"col-sm-2" B > <"col-sm-6" l > <"col-sm-4" f >> <"table-responsive" t > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
          <%}%>
          buttons: [
              'colvis'
          ],
          stateSave: true,
          stateSaveParams: function (settings, data) {
              data.search.search = "";
          },
          language: {
              search:            '_INPUT_',
              searchPlaceholder: '<hl:message key="mensagem.datatables.search.placeholder"/>',
              processing:        '<hl:message key="mensagem.datatables.processing"/>',
              loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
              lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
              info:              '<hl:message key="mensagem.datatables.info.consignatarias"/>',
              infoEmpty:         '<hl:message key="mensagem.datatables.info.empty"/>',
              infoFiltered:      '<hl:message key="mensagem.datatables.info.filtered"/>',
              infoPostFix:       '',
              zeroRecords:       '<hl:message key="mensagem.datatables.zero.records"/>',
              emptyTable:        '<hl:message key="mensagem.datatables.empty.table"/>',
              aria: {
                  sortAscending: '<hl:message key="mensagem.datatables.aria.sort.ascending"/>',
                  sortDescending:'<hl:message key="mensagem.datatables.aria.sort.descending"/>'
              },
              paginate: {
                  first:         '<hl:message key="mensagem.datatables.paginate.first.padrao.econsig"/>',
                  previous:      '<hl:message key="mensagem.datatables.paginate.previous.padrao.econsig"/>',
                  next:          '<hl:message key="mensagem.datatables.paginate.next.padrao.econsig"/>',
                  last:          '<hl:message key="mensagem.datatables.paginate.last.padrao.econsig"/>'
              },
              buttons: {
                  print :        '<hl:message key="mensagem.datatables.buttons.print"/>',
                  colvis :       '<hl:message key="mensagem.datatables.buttons.colvis"/>'
              },
              decimal: ","
          },
          initComplete: function () {
              var btns = $('.dt-button');
              btns.addClass('btn <%= ("v4".equals(versaoLeiaute) || versaoLeiaute == null) ? "btn-primary" : "btn-outline-danger" %> btn-sm');
              btns.removeClass('dt-button');
          }
      });
				
      	<% if (!exibeAtivoInativo) { %>
        	$("#dataTables_filter").addClass('pt-2 px-3');
       	<% } %>
        $('#dataTables_info').addClass('p-3');
        $("#dataTables_length").addClass('pt-3');
  });

  $(document).ready(function() {
  <%if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PRIORIDADE_DESCONTO, responsavel)) {%>
     var idColunaPrioridade = "";
     var intColuna = 0;
     <%for(int i = 0; i < listaColunas.size();i++){
           nomeColuna = listaColunas.get(i).getTitulo();
           if(!nomeColuna.equals(ApplicationResourcesHelper.getMessage("rotulo.consignacao.prioridade.desconto", responsavel))){
               continue;
           }
        %>
        intColuna = <%=i%>;
        var tabelaAtivo = $('#dataTables').DataTable();
        var tabelaInativo = $('#dataTablesInativo').DataTable();

        if($.fn.DataTable.isDataTable('#dataTables')){
         tabelaAtivo.column(intColuna).visible( false );
        }

        if($.fn.DataTable.isDataTable('#dataTablesInativo')){
         tabelaInativo.column(intColuna).visible( false );
        }
     <%}
  }%>
  
  <% if(decisaoJudicialAdeAtivo){%>
  	document.querySelector('#dataTables').insertAdjacentHTML('beforebegin', '<span><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.decisao.judicial", responsavel)%></span>');
  <%}%>
  
  <% if(decisaoJudicialAdeInativo){%>
	document.querySelector('#dataTablesInativo').insertAdjacentHTML('beforebegin', '<span><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.decisao.judicial", responsavel)%></span>');
  <%}%>

  <% if(decisaoJudicialPesquisaAvancada){%>
  	document.querySelector('#dataTables').insertAdjacentHTML('beforebegin', '<span><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.decisao.judicial", responsavel)%></span>');
  <%}%>
  
  });
</script>
<script type="text/JavaScript">
var f0 = document.getElementsByName("formLista")[0];

$('#voltarModalRetirar').on('click',function() {
    $('#confirmarRetirarPortabilidade').modal('hide');
});

$('#voltarModalReprovarDoc').on('click',function() {
    $('#modalReprovarAnexoConsignacao').modal('hide');
});

function exibirmargem() {
    <% if(exibeCaptchaDeficiente){ %>
    montaCaptchaSomSer('consultar');
    <% } %>
    $('#modalCaptcha_consultar').modal('show');
}

function confirmarRetirarPortabilidade(adeCodigo) {
    if (!adeCodigo) {
        adeCodigo = $('#adeCodigo').val();
    }
    var obs = $('#editfield').val()
    postData('../v3/retirarConsignacaoCompra?acao=retirarContratoDeCompra&ADE_CODIGO=' + adeCodigo + '&obs=' + obs + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
    $('#confirmarRetirarPortabilidade').modal('hide');
}

function confirmarReprovarAnexoConsignacao() {
    var adeCodigo = $('#adeCodigo').val();
    var obs = $('#obsReprovaAnexoConsignacao').val()
    postData('../v3/listarSolicitacao?acao=reprovarDocumentacao&telaEdicao=true&ADE_CODIGO=' + adeCodigo + '&obs_reprovacao=' + obs + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
    $('#modalReprovarAnexoConsignacao').modal('hide');
}

// Função responsável para selecionar operação do botão "Ações".
function selecionaOperacao(op) {
    <% if (exibeComboOperacoes) { %>
    if (op == 'suspender') {
        f0.action = '../v3/suspenderConsignacao?acao=confirmarSuspensao&origem=pesquisa_avancada&';
    } else if (op == 'reativar') {
        f0.action = '../v3/reativarConsignacao?acao=efetivarAcao&origem=pesquisa_avancada&';
    }
    <% } %>
}

// Funções responsáveis por validar ações do botão "Ações"
function confirmaOperacao(acao) {
    var msg = "";
        <% if (exibeComboOperacoes) { %>
    if (acao == 'suspender') {
        msg = '<hl:message key="mensagem.confirmacao.multiplo.suspender"/>';
    } else if (acao == 'reativar') {
        msg = '<hl:message key="mensagem.confirmacao.multiplo.reativar"/>';
    }
    msg+= '<hl:message key="mensagem.confirmacao.multiplo.pesquisa.avancada"/>';
    <% } %>
    return confirm(msg);
  }

// Funcao responsavel por tratar casos em que so tem uma acao : selecionar ou visualizar.
function decideAcao(acao, adeCodigo, tipo, e) {
    if (acao == "DETALHAR") {
        DETALHAR(adeCodigo);
    } else if (acao == "EXECUTAR_DECISAO_JUDICIAL") {
        EXECUTAR_DECISAO_JUDICIAL(adeCodigo);
    } else if (acao == "REIMPLANTAR_CAPITAL_DEVIDO") {
        reimplantarCapitalDevido(adeCodigo);
    } else if (acao == "RETIRAR_CONTRATO_COMPRA") {
        confirmarRetirarPortabilidade(adeCodigo);
    } else {
        escolhechk(tipo,e)
    }
}
  
function listarConsignacaoSer(tipoOperacao, adeCodigo) {
    $.ajax({
        type: 'POST',
        url: '<%=TextHelper.forJavaScriptSource(request.getAttribute("acaoFormulario")) + "?acao=listarConsignacaoSer&_skip_history_=true&" + TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>',
        data: {
            'ADE_CODIGO': adeCodigo
        },
        success: function (data, status, error) {
            $('#modalListarConsignacaoSerBody').html(data);
            paginarTabela();
            $('#modalListarConsignacaoSer').modal('show');
        },
        error: function (request, status, error) {
        	console.log(error);
        	postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL((responsavel.isSer() ? "../v3/carregarPrincipal" : paramSession.getLastHistory()), request))%>');
        }
    });	
}

function paginarTabela() {
	$('.dataTableConsignacaoSer').DataTable({
        "paging": true,
        "pageLength": 20,
        "lengthMenu": [
            [20, 50, 100, -1],
            [20, 50, 100, '<hl:message key="mensagem.datatables.all"/>']
        ],
        "order": [[ <%=posicaoOrdenacao%>, "desc" ]],
        "pagingType": "simple_numbers",
        "dom": '<"card-body p-0" <"row pl-0 pr-4" <"col-sm-2 pl-0" B > <"col-sm-6 pl-0" l > <"col-sm-4 pr-0" f >> <"table-responsive" t> > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
        buttons: [
            'colvis'
        ],
        stateSave: true,
        stateSaveParams: function (settings, data) {
            data.search.search = "";
        },
        language: {
            search:            '_INPUT_',
            searchPlaceholder: '<hl:message key="mensagem.datatables.search.placeholder"/>',
            processing:        '<hl:message key="mensagem.datatables.processing"/>',
            loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
            lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
            info:              '<hl:message key="mensagem.datatables.info.consignatarias"/>',
            infoEmpty:         '<hl:message key="mensagem.datatables.info.empty"/>',
            infoFiltered:      '<hl:message key="mensagem.datatables.info.filtered"/>',
            infoPostFix:       '',
            zeroRecords:       '<hl:message key="mensagem.datatables.zero.records"/>',
            emptyTable:        '<hl:message key="mensagem.datatables.empty.table"/>',
            aria: {
                sortAscending: '<hl:message key="mensagem.datatables.aria.sort.ascending"/>',
                sortDescending:'<hl:message key="mensagem.datatables.aria.sort.descending"/>'
            },
            paginate: {
                first:         '<hl:message key="mensagem.datatables.paginate.first.padrao.econsig"/>',
                previous:      '<hl:message key="mensagem.datatables.paginate.previous.padrao.econsig"/>',
                next:          '<hl:message key="mensagem.datatables.paginate.next.padrao.econsig"/>',
                last:          '<hl:message key="mensagem.datatables.paginate.last.padrao.econsig"/>'
            },
            buttons: {
                print :        '<hl:message key="mensagem.datatables.buttons.print"/>',
                colvis :       '<hl:message key="mensagem.datatables.buttons.colvis"/>'
            },
            decimal: ","
        },
        initComplete: function () {
            var btns = $('.dt-button');
            btns.addClass('btn btn-primary btn-sm');
            btns.removeClass('dt-button');
        }
    });
	
	$(".dataTableConsignacaoSer_filter").addClass('pt-2 px-3');
	$(".dataTableConsignacaoSer_length").addClass('pt-3');
}

function reimplantarCapitalDevido(adeCodigo) {
    postData('../v3/reimplantarCapitalDevido?acao=iniciarReimplantacao&ADE_CODIGO=' + adeCodigo + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
}

<%
if (temCheckbox) {
    if (ehDeferirIndeferir) {
%>

/* **Click na linha
 * 1- Mostrar as colunas de Deferir e Indeferir, quando se clica na linha.
*/

var clicklinha = false;

$(".selecionarColuna").click(function() {
    // 1- Seleciona a linha e mostrar as colunas dos checks
    var checked = $("table tbody tr input[type=checkbox]:checked").length;

    if (checked == 0) {
        if (clicklinha) {
            $("table th:nth-child(-n+2)").hide();
            $(".ocultarColunaDupla").hide();
        } else {
            $("table th:nth-child(-n+2)").show();
            $(".ocultarColunaDupla").show();
        }

        clicklinha = !clicklinha
    }
});

/* **Click no opções, Deferir
 * 1- Exibir colunas dos checkBoxes Deferir e Indeferir, independente de qual opção tenha sido escolhida
 * 2- Colorir a linha da opção desejada, marcar o checkBox da opção, e desmarca o da opção contrária
 * 3- Verificar se a coluna do checkBox correspondente está toda selecionada, para que se marque o checkAll do mesmo, e desmarque o outro.
*/
$("[name='selecionaAcaoDeferir']").click(function() {
	// 1- Exibe as colunas dos checksboxes
	$("table th:nth-child(-n+2)").show();
	$(".ocultarColunaDupla").show();

	// 2- Colore a linha e marca o checkbox do deferir, e caso o check do indeferir esteja marcado será desmarcado
	$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
	$(this).parentsUntil("tbody").find('input[name="chkIndeferir"]').prop("checked",false);
	$(this).parentsUntil("tbody").find('input[name="chkDeferir"]').prop("checked", true);
	$("#checkAllIndeferir").prop('checked', false);

	// 3- Verifica se todos os checkboxes do Deferir estão marcados, marca o checkAll do Deferir, e desmarca o do Indeferir
	var qtdCheckboxCheked = $("[name='chkDeferir']").not($("#checkAllDeferir")).filter(':checked').length;
	var qtdCheckbox = $("[name='chkDeferir']").not($("#checkAllDeferir")).length;
	if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAllDeferir").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAllDeferir").prop('checked', false);
	}
 });

/* **Click no opções, Indeferir
 * 1- Exibir colunas dos checkBoxes Deferir e Indeferir, independente de qual opção tenha sido escolhida
 * 2- Colorir a linha da opção desejada, marcar o checkBox da opção, e desmarca o da opção contrária
 * 3- Verificar se a coluna do checkBox correspondente está toda selecionada, para que se marque o checkAll do mesmo, e desmarque o outro.
*/
$("[name='selecionaAcaoIndeferir']").click(function() {
	// 1- Exibe as colunas dos checksboxes
	$("table th:nth-child(-n+2)").show();
	$(".ocultarColunaDupla").show();

	// 2- Colore a linha e marca o checkbox do indeferir, e caso o check do deferir esteja marcado será desmarcado
	$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
	$(this).parentsUntil("tbody").find('input[name="chkDeferir"]').prop("checked",false);
	$(this).parentsUntil("tbody").find('input[name="chkIndeferir"]').prop("checked",true);
	$("#checkAllDeferir").prop('checked', false);

	// 3- Verifica se todos os checkboxes do Indeferir estão marcados, marca o checkAll do Indeferir, e desmarca o do Deferir
	var qtdCheckboxCheked = $("[name='chkIndeferir']").not($("#checkAllIndeferir")).filter(':checked').length;
	var qtdCheckbox = $("[name='chkIndeferir']").not($("#checkAllIndeferir")).length;
	if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAllIndeferir").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAllIndeferir").prop('checked', false);
	}

});

/* **Click do check Deferir
 * 1- Ao ser selecionado, colorir a linha e ou ser desselecionado tirar a cor da linha
 * 2- Ao ser selecionado, caso complete a seleção de todos na coluna, marcar a opção checkall, caso quebre essa regra desmarcar o checkall
 * 3- Ao ser desselecionado, caso seja o único checkbox da página, esconder as colunas extras
 * 4- Ao ser selecionado, caso o checkall contrário esteja marcado, ele será desmarcado
 *
 */
$("[name='chkDeferir']").click(function() {
	//1- colore a linha
	if ($(this).is(":checked")) {
		$(this).parentsUntil("tbody", ".selecionarLinha").addClass("table-checked");
		$(this).parentsUntil("tbody").find('input[name="chkIndeferir"]').prop("checked",false);

		//4- desmarca o checkall contrário
		$("#checkAllIndeferir").prop('checked', false);
	} else {
		$(this).parentsUntil("tbody", ".selecionarLinha").removeClass("table-checked");
	}

	//2- marca ou desmarca o checkall correspondente
	var qtdCheckboxCheked = $("[name='chkDeferir']").not($("#checkAllDeferir")).filter(':checked').length;
	var qtdCheckbox = $("[name='chkDeferir']").not($("#checkAllDeferir")).length;
	if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAllDeferir").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAllDeferir").prop('checked', false);
	}

	//3- esconde as colunas
	if ($('input[type="checkbox"]').filter(':checked').length == 0) {
		$("table th:nth-child(-n+2)").hide();
		$(".ocultarColunaDupla").hide();
		clicklinha = false;
	}
});

/* **Click do check Indeferir
 * 1- Ao ser selecionado, colorir a linha e ou ser desselecionado tirar a cor da linha
 * 2- Ao ser selecionado, caso complete a seleção de todos na coluna, marcar a opção checkall, caso quebre essa regra desmarcar o checkall
 * 3- Ao ser desselecionado, caso seja o único checkbox da página, esconder as colunas extras
 * 4- Ao ser selecionado, caso o checkall contrário esteja marcado, ele será desmarcado
 *
 */
$("[name='chkIndeferir']").click(function() {
	//1- colore a linha
	if ($(this).is(":checked")) {
		$(this).parentsUntil("tbody", ".selecionarLinha").addClass("table-checked");
		$(this).parentsUntil("tbody").find('input[name="chkDeferir"]').prop("checked",false);
		
		//4- marca o checkAll contrário
		$("#checkAllDeferir").prop('checked', false);
	} else {
		$(this).parentsUntil("tbody", ".selecionarLinha").removeClass("table-checked");
	}
	
	//2- marca ou desmarca o checkall correpondente
	var qtdCheckboxCheked = $("[name='chkIndeferir']").not($("#checkAllIndeferir")).filter(':checked').length;
	var qtdCheckbox = $("[name='chkIndeferir']").not($("#checkAllIndeferir")).length;
	if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAllIndeferir").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAllIndeferir").prop('checked', false);
	}
	
	// 3- esconde as colunas
	if ($('input[type="checkbox"]').filter(':checked').length == 0) {
		$("table th:nth-child(-n+2)").hide();
		$(".ocultarColunaDupla").hide();
		clicklinha = false;
	}
});

/* **CheckAll Deferir
 * 1- Colorir todas as linhas, quando o checkAll Deferir for marcado, ou quando for desmarcado descolorir
 * 2- Ocultar as colunas dos checks quando não houver nenhum deles selecionados
 * 3- Desmarcar o checkAll contrário, quando o correspondente é marcado
*/
$("#checkAllDeferir").click(function() {
	$('input[name="chkDeferir"]').prop("checked",function(i, val) {
		if (!(i < 0)) {
			// 1- Colore as linhas, quando o checkAll está marcado ou quando for desmacado
			if ($("#checkAllDeferir").is(":checked")) {
				$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
			} else {
				$(this).parentsUntil("tbody",".selecionarLinha").removeClass("table-checked");
			}
			return $("#checkAllDeferir").is(":checked");
		}
	});
	
	// 2- Oculta as colunas dos checks, quando não contém nenhum checkbox marcado
	if (!$("#checkAllDeferir").is(":checked")) {
		if($('input[type="checkbox"]').filter(':checked').length == 0) {
			$("table th:nth-child(-n+2)").hide();
			$(".ocultarColunaDupla").hide();
		}
	} else {
		//3- Desmarca o checkAll contrário
		$('input[name="chkIndeferir"]').prop("checked",function(i, val) {
			return false;
		});
		$("#checkAllIndeferir").prop("checked", false);
	}
});

/* **CheckAll Indeferir
 * 1- Colorir todas as linhas, quando o checkAll Indeferir for marcado, ou quando for desmarcado descolorir
 * 2- Ocultar as colunas dos checks quando não houver nenhum deles selecionados
 * 3- Desmarcar o checkAll contrário, quando o correspondente é marcado
*/
$("#checkAllIndeferir").click(function() {
	$('input[name="chkIndeferir"]').prop("checked",function(i, val) {
		if (!(i < 0)) {
			// 1- Colore as linhas, quando o checkAll está marcado
			if ($("#checkAllIndeferir").is(":checked")) {
				$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
			} else {
				$(this).parentsUntil("tbody",".selecionarLinha").removeClass("table-checked");
			}
			return $("#checkAllIndeferir").is(":checked");
		}
	});
	
	// 2- Oculta as colunas dos checks, quando não contém nenhum checkbox marcado
	if (!$("#checkAllIndeferir").is(":checked")) {
		if($('input[type="checkbox"]').filter(':checked').length == 0) {
			$("table th:nth-child(-n+2)").hide();
			$(".ocultarColunaDupla").hide();
		}
	} else {
		//3- Desmarca o checkAll contrário
		$('input[name="chkDeferir"]').prop("checked",function(i, val) {
			return false;
		});
		$("#checkAllDeferir").prop("checked", false);
	}
});

//Oculta colula com checks
function ocultarColunaDupla() {
	//Oculta as duas colunas, incluindo os cabeçalhos de Deferir e Indeferir
	$("table th:nth-child(-n+2)").hide();
	$(".ocultarColunaDupla").hide();
	clicklinha = false;
}

<%
	} else {
%>

var verificarCheckbox = function () {
	var checked = $("table tbody tr input[type=checkbox]:checked:not([disabled])").length;
	var total = $("table tbody tr input[type=checkbox]:not([disabled])").length;
	$("input[id*=checkAll_]").prop('checked', checked == total);
	if (checked == 0) {
		$("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").hide();
	} else {
		$("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").show();
	}
};

$("table tbody tr td").not("td.colunaUnica, td.acoes").click(function (e) {
	$(e.target).parents('tr').find('input[type=checkbox]').click();
});

function escolhechk(idchk,e) {
 	$(e).parents('tr').find('input[type=checkbox]').click();
}

$("table tbody tr input[type=checkbox]").click(function (e) {
	verificarCheckbox();
	var checked = e.target.checked;
	if (checked) {
		$(e.target).parents('tr').addClass("table-checked");
	} else {
		$(e.target).parents('tr').removeClass("table-checked");
	}
});

$("input[id*=checkAll_").click(function (e){
	var checked = e.target.checked;
	$('table tbody tr input[type=checkbox]:not([disabled])').prop('checked', checked);
	if (checked) {
		$("table tbody tr").addClass("table-checked");
	} else {
		$("table tbody tr").removeClass("table-checked");
	}
	verificarCheckbox();
});

$(document).ready(function() {
    $('table tbody tr input[type=checkbox]').map((index,e) => {
        if ($(e).parents('tr').find('span.indisponivel').text()) {
        	$(e).prop("disabled", true);
        }
    });
});

<%
	}
}
%>

<%
if (listaAcoes != null && !listaAcoes.isEmpty()) {
    for (AcaoConsignacao acao : listaAcoes) {
        out.print(acao.getAcaoJavascript(queryString, tipoOperacao, qtdMaxSelecaoMultipla, msgErroQtdMaxSelecaoMultiplaSuperada, responsavel));
        //out.print(acao.addAcaoCheckBox(ocultarColunaCheckBox,responsavel));
    }
}
%>

<% if (ativarVerificaoDataFim) { %>

function verificarAdesDataFim() {
	
	var msg = '<hl:message key="mensagem.reativar.consignacao.ades.prazo.final.contrato"/>\n\n';
	
	var adesCodigos = $('[name=chkReativar]').filter('[checked]').map(function () {return this.value; }).get();
	
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

<% } %>

$('#ativo-tab').on('click', function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	  var element = document.getElementById("inativo");
	  element.classList.remove("active");
})

$('#inativo-tab').on('click', function (e) {
    e.preventDefault();
    $(this).tab('show');
    var element = document.getElementById("ativo");
	element.classList.remove("active");
})

<% if (geraMargem) { %>
    function gerarPDFConsultaMargem() {
      var dadosMargem = document.getElementById('consultaMargem').innerHTML;

      var doc = dadosMargem + "" + "";

      var dataToSend = JSON.stringify({'html': doc, 'rseCodigo': '<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>'});
      $.ajax({ url: "../v3/consultarMargem?acao=gerarPdf&<%=SynchronizerToken.generateToken4URL(request)%>&_skip_history_=true",
             type: "POST",
             contentType: "application/json; charset=utf-8",
             data: dataToSend ,
             responseType: 'arraybuffer',
             xhrFields: { responseType: 'blob' },
             success: function (response, status, xhr) {
                 var filename = "";
                 var disposition = xhr.getResponseHeader("Content-Disposition");
                 if (disposition && disposition.indexOf("filename") !== -1) {
                    var filenameRegex = /filename[^;=\n]*=(([""]).*?\2|[^;\n]*)/;
                    var matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1])
                        filename = matches[1].replace(/[""]/g, "");
                 }

                 var blob = new Blob([response], {type: 'application/pdf'});
                 var link = document.createElement('a');
                 link.href = window.URL.createObjectURL(blob);
                 link.download = filename;
                 link.click();
          	 },
             error: function (request, status, error) {
                 alert ("<hl:message key="mensagem.erro.download"/>");
          	 }
         });
    }
    <% } %>
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
