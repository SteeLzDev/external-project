<%--
* <p>Title: reservar.jsp</p>
* <p>Description: Página de reserva da margem do servidor layout v4</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@page import="com.zetra.econsig.values.TipoBeneficiarioEnum"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.periodo.PeriodoHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.helper.criptografia.JCryptOld"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.helper.margem.MargemDisponivel" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

// Verifica se há permissão para parâmetros de inclusão avançada
boolean usuPossuiIncAvancadaAde = responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO);

// Verifica se o sistema está configurado para trabalhar com o CET.
boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");
CustomTransferObject permissionario = (CustomTransferObject) request.getAttribute("permissionario");
ParamSvcTO paramSvcCse = (ParamSvcTO) request.getAttribute("paramSvcCse");
Set<Integer> prazosPossiveisMensal = (Set<Integer>) request.getAttribute("prazosPossiveisMensal");
Set<Integer> prazosPossiveisPeriodicidadeFolha = (Set<Integer>) request.getAttribute("prazosPossiveisPeriodicidadeFolha");
ExibeMargem exibeMargemLimite = (ExibeMargem) request.getAttribute("exibeMargemLimite");
MargemDisponivel margemDisponivel = (MargemDisponivel) request.getAttribute("margemDisponivel");
MargemTO margemLimiteDisponivel = (MargemTO) request.getAttribute("margemLimiteDisponivel");
BigDecimal margemLimiteConsignavel = (BigDecimal) request.getAttribute("margemLimiteConsignavel");
BigDecimal margemConsignavel = (BigDecimal) request.getAttribute("margemConsignavel");
BigDecimal margemTratamentoEspecial = (BigDecimal) request.getAttribute("margemTratamentoEspecial");
BigDecimal somaValorContratosTratamentoEspecial = (BigDecimal) request.getAttribute("somaValorContratosTratamentoEspecial");
String serCodigo = (String) request.getAttribute("serCodigo");
String rseCodigo = (String) request.getAttribute("rseCodigo");
String cnvCodigo = (String) request.getAttribute("cnvCodigo");
String plaCodigo = (String) request.getAttribute("plaCodigo");
String prmCodigo = (String) request.getAttribute("prmCodigo");
String svcCodigo = (String) request.getAttribute("svcCodigo");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String svcDescricao = (String) request.getAttribute("svcDescricao");
String svcPrioridade = (String) request.getAttribute("svcPrioridade");
Boolean servicoCompulsorio = (Boolean) request.getAttribute("servicoCompulsorio");
Boolean isServidor = (Boolean) request.getAttribute("isServidor");

Boolean serInfBancariaObrigatoria = (Boolean) request.getAttribute("serInfBancariaObrigatoria");
Boolean validarInfBancaria = (Boolean) request.getAttribute("validarInfBancaria");
Boolean validarDataNasc = (Boolean) request.getAttribute("validarDataNasc");
Boolean permiteCadVlrLiqTxJuros = (Boolean) request.getAttribute("permiteCadVlrLiqTxJuros");
Boolean permiteCadVlrTac = (Boolean) request.getAttribute("permiteCadVlrTac");
Boolean permiteCadVlrIof = (Boolean) request.getAttribute("permiteCadVlrIof");
Boolean permiteCadVlrLiqLib = (Boolean) request.getAttribute("permiteCadVlrLiqLib");
Boolean permiteCadVlrMensVinc = (Boolean) request.getAttribute("permiteCadVlrMensVinc");
Boolean permiteCadVlrSegPrestamista = (Boolean) request.getAttribute("permiteCadVlrSegPrestamista");
Boolean possuiCorrecaoVlrPresente = (Boolean) request.getAttribute("possuiCorrecaoVlrPresente");
Boolean possuiControleVlrMaxDesconto = (Boolean) request.getAttribute("possuiControleVlrMaxDesconto");
Boolean possuiComposicaoMargem = (Boolean) request.getAttribute("possuiComposicaoMargem");
Boolean possuiVariacaoMargem = (Boolean) request.getAttribute("possuiVariacaoMargem");
Boolean podeMostrarMargem = (Boolean) request.getAttribute("podeMostrarMargem");
Boolean validaMargemViaJavascript = (Boolean) request.getAttribute("validaMargemViaJavascript");
Boolean exigeSenha = (Boolean) request.getAttribute("exigeSenha");
Boolean senhaServidorOK = (Boolean) request.getAttribute("senhaServidorOK");
Boolean exibeAlgumaMargem = (Boolean) request.getAttribute("exibeAlgumaMargem");
Boolean exibeHistLiqAntecipadas = (Boolean) request.getAttribute("exibeHistLiqAntecipadas");
Short intFolha = (Short) request.getAttribute("intFolha");
Short incMargem = (Short) request.getAttribute("incMargem");
Boolean prazoFixo = (Boolean) request.getAttribute("prazoFixo");
String maxPrazo = (String) request.getAttribute("maxPrazo");
String adeVlrPadrao = (String) request.getAttribute("adeVlrPadrao");
Integer carenciaMinPermitida = (Integer) request.getAttribute("carenciaMinPermitida");
Integer carenciaMaxPermitida = (Integer) request.getAttribute("carenciaMaxPermitida");
String permitePrazoMaiorContSer = (String) request.getAttribute("permitePrazoMaiorContSer");
String tipoVlr = (String) request.getAttribute("tipoVlr");
Boolean alteraAdeVlr = (Boolean) request.getAttribute("alteraAdeVlr");
Boolean permiteVlrNegativo = (Boolean) request.getAttribute("permiteVlrNegativo");
String vlrLimite = (String) request.getAttribute("vlrLimite");
BigDecimal vlrMaxParcelaSaldoDevedor = (BigDecimal) request.getAttribute("vlrMaxParcelaSaldoDevedor");
Boolean identificadorAdeObrigatorio = (Boolean) request.getAttribute("identificadorAdeObrigatorio");
String mascaraAdeIdentificador = (String) request.getAttribute("mascaraAdeIdentificador");
Integer numAdeHistLiqAntecipadas = (Integer) request.getAttribute("numAdeHistLiqAntecipadas");
Double maxTacCse = (Double) request.getAttribute("maxTacCse");
Boolean servidorDeveSerKYCComplaint = (Boolean) request.getAttribute("servidorDeveSerKYCComplaint");
Boolean servidorValidouKYC = (Boolean) request.getAttribute("servidorValidouKYC");
String txtExplicativo = (String) request.getAttribute("txtExplicativo");
Boolean exibeAlertaMsgPertenceCategoria = (Boolean) request.getAttribute("exibeAlertaMsgPertenceCategoria");
String msgPertenceCategoria = (String) request.getAttribute("msgPertenceCategoria");

String rseMatricula = (String) request.getAttribute("rseMatricula");
String serDataNasc = (String) request.getAttribute("serDataNasc");
String numBanco = (String) request.getAttribute("numBanco");
String numAgencia = (String) request.getAttribute("numAgencia");
String numConta1 = (String) request.getAttribute("numConta1");
String numConta2 = (String) request.getAttribute("numConta2");
String numBancoAlt = (String) request.getAttribute("numBancoAlt");
String numAgenciaAlt = (String) request.getAttribute("numAgenciaAlt");
String numContaAlt1 = (String) request.getAttribute("numContaAlt1");
String numContaAlt2 = (String) request.getAttribute("numContaAlt2");
Integer sizeNumAgencia = (Integer) request.getAttribute("sizeNumAgencia");

boolean disabledVlrPostoFixo = request.getAttribute("disabledVlrFixoPosto") != null ? (boolean) request.getAttribute("disabledVlrFixoPosto") : false;

Boolean validaMargemAvancado = (Boolean) request.getAttribute("validaMargemAvancado");
Boolean validaTaxaAvancado = (Boolean) request.getAttribute("validaTaxaAvancado");
Boolean validaDadosBancariosAvancado = (Boolean) request.getAttribute("validaDadosBancariosAvancado");
Boolean validaDataNascAvancado = (Boolean) request.getAttribute("validaDataNascAvancado");

String tipo = (String) request.getAttribute("tipoOperacao");
boolean exigeCaptcha = false;
boolean exibeCaptchaAvancado = false;
boolean exibeCaptchaDeficiente = false;
if(responsavel.isSer()) {
        exigeCaptcha = request.getAttribute("exigeCaptcha") != null && (boolean) request.getAttribute("exigeCaptcha");
        exibeCaptchaAvancado = request.getAttribute("exibeCaptchaAvancado") != null && (boolean) request.getAttribute("exibeCaptchaAvancado");
        exibeCaptchaDeficiente = request.getAttribute("exibeCaptchaDeficiente") != null && (boolean) request.getAttribute("exibeCaptchaDeficiente");
}

List<TransferObject> lstTipoDadoAdicional = (List<TransferObject>) request.getAttribute("lstTipoDadoAdicional");
Map<String,String> dadosAutorizacao = (Map<String,String>) request.getAttribute("dadosAutorizacao");
String termoConsentimentoDadosServidor = (String) request.getAttribute("termoConsentimentoDadosServidor");

List<TransferObject> beneficiarios = (List<TransferObject>) request.getAttribute("beneficiarios");
String permiteDescontoViaBoleto = !TextHelper.isNull(request.getAttribute("descontoViaBoleto")) ? (String) request.getAttribute("descontoViaBoleto") : "";
boolean enderecoObrigatorio = request.getAttribute("enderecoObrigatorio") != null && request.getAttribute("enderecoObrigatorio").toString().equals("true");
boolean celularObrigatorio = request.getAttribute("celularObrigatorio") != null && request.getAttribute("celularObrigatorio").toString().equals("true");
boolean enderecoCelularObrigatorio = request.getAttribute("enderecoCelularObrigatorio") != null && request.getAttribute("enderecoCelularObrigatorio").toString().equals("true");

boolean portalBeneficio = request.getAttribute("portalBeneficio") != null;
String corCodigoPortal = (String) request.getAttribute("corCodigo");
String nomeArqFotoServidor =(String) request.getAttribute("nomeArqFotoServidor");

List<?> informacoesServidor = (List<?>) request.getAttribute("informacaoServidor");
String nomeServidorInformacaoCsa = (String) request.getAttribute("nomeServidorInformacaoCsa");
HashMap<String, TransferObject> hashCsaPermiteContato = (HashMap<String, TransferObject>) request.getAttribute("hashCsaPermiteContato"); 
String csa_whatsapp = "", csa_email_contato ="", csa_email = "", csa_email_usar = "", csa_contato_tel = "", tipo_contato = "";
if(hashCsaPermiteContato.get(csaCodigo) != null){
    TransferObject consignatariaContato = hashCsaPermiteContato.get(csaCodigo);
    csa_whatsapp = (String) consignatariaContato.getAttribute(Columns.CSA_WHATSAPP);
    csa_email_contato = (String) consignatariaContato.getAttribute(Columns.CSA_EMAIL_CONTATO);
    csa_email = (String) consignatariaContato.getAttribute(Columns.CSA_EMAIL);
    tipo_contato = (String) consignatariaContato.getAttribute(Columns.PCS_VLR);
    csa_email_usar = !TextHelper.isNull(csa_email_contato) ? csa_email_contato : csa_email;
    if(!TextHelper.isNull(csa_whatsapp)){
        csa_whatsapp = LocaleHelper.formataCelular(csa_whatsapp);
    }
    csa_contato_tel = (String) consignatariaContato.getAttribute(Columns.CSA_TEL);
}

boolean anexoObrigatorio = request.getAttribute("anexoObrigatorio") != null && (boolean) request.getAttribute("anexoObrigatorio");
String qtdeMinAnexos = (String) request.getAttribute("qtdeMinAnexos");
boolean inclusaoJudicial = request.getAttribute("inclusaoJudicial") != null;
boolean usaListaPrazos = prazosPossiveisMensal != null && !prazosPossiveisMensal.isEmpty() && !maxPrazo.equals("0");
%>
<c:set var="title">
<%=TextHelper.forHtmlContent(request.getAttribute("tituloPagina"))%>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>" method="post" name="form1">
      <div class="row">
        <c:if test="${exibeMaisAcoes}">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.mais.acoes" /></button>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="<hl:message key="rotulo.mais.acoes" />">
             <%-- Composição de margem --%>
             <% if (servicoCompulsorio && (svcPrioridade != null && !svcPrioridade.trim().equals(""))) { %>
             <a class="dropdown-item" href="#no-back" onClick="abrirComposicaoCompulsorio()" title="<hl:message key="mensagem.composicao.margem.compulsorios.clique.aqui"/>" ><hl:message key="rotulo.compulsivo.acao"/></a>
             <% } %>   
             <% if (podeMostrarMargem && possuiComposicaoMargem && !responsavel.isSer()) { %>
               <% if (!exigeSenha || (exigeSenha && senhaServidorOK)) { %>
              <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarComposicaoMargem?acao=<%=TextHelper.forJavaScriptAttribute(tipo)%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');"><hl:message key="rotulo.composicao.margem.acao"/></a>
               <% } else { %>
              <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarComposicaoMargem?acao=iniciar&RSE_MATRICULA=<%=TextHelper.forJavaScriptAttribute(rseMatricula)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');"><hl:message key="rotulo.composicao.margem.acao"/></a>
               <% } %>
             <% } %>
             <%-- Variação de margem --%>
             <% if (possuiVariacaoMargem && exibeAlgumaMargem && !responsavel.isSer()) { %>
               <% if (!exigeSenha || (exigeSenha && senhaServidorOK)) { %>
              <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarVariacaoMargem?acao=<%=TextHelper.forJavaScriptAttribute(tipo)%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');"><hl:message key="rotulo.variacao.margem.acao"/></a>
               <% } else { %>
              <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarVariacaoMargem?acao=iniciar&RSE_MATRICULA=<%=TextHelper.forJavaScriptAttribute(rseMatricula)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');"><hl:message key="rotulo.variacao.margem.acao"/></a>
               <% } %>
            <% } %>
              <%-- Extrato de margem --%>              
              <% if (responsavel.temPermissao(CodedValues.FUN_CONS_EXTRATO_MARGEM)) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/consultarExtratoMargem?acao=iniciar&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                    <hl:message key="rotulo.extrato.margem.acao"/>
                 </a>
              <% } %>
            <%-- Histórico de liquidação antecipada --%>
            <% if (exibeHistLiqAntecipadas && numAdeHistLiqAntecipadas > 0 && !responsavel.isSer()) { %>
              <div class="dropdown-divider" role="separator"></div>
              <a class="dropdown-item" href="#no-back" onclick="openModalSubAcesso('../v3/reservarMargem?RSE_MATRICULA=<%=TextHelper.forJavaScriptAttribute(rseMatricula)%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&SVC_CODIGO=<%=TextHelper.forJavaScript(svcCodigo)%>&SER_NOME=<%=TextHelper.forJavaScript(TextHelper.encode64(servidor.getAttribute(Columns.SER_NOME).toString()))%>&acao=listarHistLiquidacoesAntecipadas&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');"><hl:message key="rotulo.historico.liq.antecipada.acao"/></a>
            <% } %>
            <%-- Informações do servidor --%>
            <%  if (request.getAttribute("exibeInformacaoCsaServidor") != null) { %>
              <div class="dropdown-divider" role="separator"></div>
              <a class="dropdown-item" href="#no-back" onclick="postData('../v3/manterInformacaoCsaServidor?acao=iniciar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(serCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.reserva.margem.informacao.servidor"/></a>
            <%  } %>
            </div>
          </div>
        </div>
        </c:if>
        <div class="col-sm-5">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.confirmar.dados.titulo" /></h2>
                <% if(exigeCaptcha){ %>
                <span class="card-header-icon-ocultar-margem-ser">
                    <a href="#" onclick="exibirmargem()" id="olhoMargemOculto">
                        <svg  width="30" height="30" class="icon-oculta-margem-simu">
                            <use xlink:href="#i-eye-slash-regular"></use>
                        </svg>
                    </a>
                    </span>
                <% } %>
            </div>
            <div class="card-body">
              <dl class="row data-list">
              <% if (request.getAttribute("csaNome") != null) { %>
                <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/>:</dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("csaNome"))%></dd>
              <% } %>
              <% if (request.getAttribute("taxaCadastrada") != null) { %>
                <dt class="col-6"><hl:message key="<%= temCET ? "rotulo.consignacao.cet" : "rotulo.consignacao.taxa.juros"%>"/>:</dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("taxaCadastrada"))%></dd>
              <% } %>
              <% if (!responsavel.isSer()) { %>
                <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
                <hl:detalharServidorv4 name="servidor" complementos="true" validaAvancadaDataNasc="validaDataNascAvancado" scope="request"/>
                <%-- Fim dos dados da ADE --%>  
              <% } %>
              <% if (request.getAttribute("exibirValorMargem") != null) { /* Mostra a Margem */ %>
                <% if (margemDisponivel.getMargemRestanteDependente() == null) { %>
                <dt class="col-6"><hl:message key="rotulo.reservar.margem.disponivel"/>:</dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()))%>&nbsp;<%=TextHelper.forHtmlContent(margemDisponivel.getExibeMargem().isSemRestricao() || margemConsignavel.doubleValue() > 0 ? NumberHelper.format(margemConsignavel.doubleValue(), NumberHelper.getLang()) : "0,00")%></dd>
                <% } else { %>
                <dt class="col-6"><%=TextHelper.forHtmlContent(margemDisponivel.getMarDescricao())%>:</dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()))%>&nbsp;<%=TextHelper.forHtmlContent(margemDisponivel.getExibeMargem().isSemRestricao() || margemConsignavel.doubleValue() > 0 ? NumberHelper.format(margemConsignavel.doubleValue(), NumberHelper.getLang()) : "0,00")%></dd>
                <dt class="col-6"><%=TextHelper.forHtmlContent(margemDisponivel.getMarDescricaoDependente())%>:</dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()))%>&nbsp;<%=TextHelper.forHtmlContent(margemDisponivel.getExibeMargem().isSemRestricao() || margemDisponivel.getMargemRestanteDependente().doubleValue() > 0 ? NumberHelper.format(margemDisponivel.getMargemRestanteDependente().doubleValue(), NumberHelper.getLang()) : "0,00")%></dd>
                <% } %>
              <% } else if(exigeCaptcha){ %>
                  <dt class="col-6"><hl:message key="rotulo.reservar.margem.disponivel"/>:</dt>
                    <dd class="col-6"><hl:message key="rotulo.margem.moeda"/><hl:message key="rotulo.margem.disponivel.codigo"/></dd>
                  <% } %>
              <% if (request.getAttribute("exibirValorMargemLimite") != null) { /* Mostra a Margem Limite */ %>
                <dt class="col-6"><hl:message key="rotulo.reservar.margem.margem.limite.por.csa.disponivel"/>:</dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(margemLimiteDisponivel.getMarTipoVlr().toString()))%>&nbsp;<%=TextHelper.forHtmlContent(exibeMargemLimite.isSemRestricao() || margemLimiteConsignavel.doubleValue() > 0 ? NumberHelper.format(margemLimiteConsignavel.doubleValue(), NumberHelper.getLang()) : "0,00")%></dd>
              <% } %>
              <% if (request.getAttribute("plaDescricao") != null) { %>
                <dt class="col-6"><hl:message key="rotulo.plano.singular"/>:</dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("plaDescricao"))%></dd>
              <% } %>
              <% if (request.getAttribute("cnvDescricao") != null) { %>
                <dt class="col-6"><hl:message key="rotulo.servico.singular"/>:&nbsp;</dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("cnvDescricao"))%></dd>
              <% } %>
              <% if (permissionario != null) {%>
                <dt class="col-6"><hl:message key="rotulo.endereco.singular"/>:&nbsp;</dt>
                <dd class="col-6"><%=TextHelper.forHtmlContent((permissionario.getAttribute(Columns.ECH_DESCRICAO) != null ? permissionario.getAttribute(Columns.ECH_DESCRICAO) : "") + " " + (permissionario.getAttribute(Columns.PRM_COMPL_ENDERECO) != null ? " - " + permissionario.getAttribute(Columns.PRM_COMPL_ENDERECO) : ""))%></dd>
              <% } %>
              <%if (!TextHelper.isNull(tipo_contato) && hashCsaPermiteContato.get(csaCodigo) != null){ %>
                <dt class="col-6"><hl:message key="rotulo.consignataria.contato.qr.code"/>:</dt>
                <dd class="col-6">
                    <%if (!TextHelper.isNull(csa_whatsapp) && CodedValues.TPA_CONTATOS_WHATSAPP.contains(tipo_contato)){ %>
                        <i class="fa fa-whatsapp icon-contato-ranking" onclick="openModalQRCode(1,'<%=csa_whatsapp%>')"></i>
                    <%} %>
                    <%if (!TextHelper.isNull(csa_email_usar) && CodedValues.TPA_CONTATOS_EMAIL.contains(tipo_contato)){ %>
                        <i class="fa fa-at icon-contato-ranking" onclick="openModalQRCode(2,'<%=csa_email_usar%>')"></i>
                    <%} %>
                    <%if (!TextHelper.isNull(csa_contato_tel) && CodedValues.TPA_CONTATOS_TELEFONE.contains(tipo_contato)){ %>
                        <i class="fa fa-phone icon-contato-ranking" onclick="openModalQRCode(3,'<%=csa_contato_tel%>')"></i>                              
                    <%} %>                
                </dd>
              <% } %>
              </dl>
            </div>
          </div>
          <%if(!TextHelper.isNull(nomeArqFotoServidor) && (responsavel.isCsaCor() || responsavel.isCseOrg())){ %>
              <div class="card">
                <div class="card-header">
                  <h2 class="card-header-title"><hl:message key="rotulo.titulo.foto.servidor"/></h2>
                </div>
                <div class="card-body">
                  <div id="conteudo">
                    <IMG class="img-servidor" SRC="<%=TextHelper.forHtmlAttribute("../img/view.jsp?nome=" + nomeArqFotoServidor)%>" >
                  </div>
                </div>
              </div>
          <%} %>
        </div>
        <div class="col-sm">
          <div class="card">
            <div class="card-header hasIcon">
              <span class="card-header-icon"><svg width="26"><use xlink:href="#i-consignacao"></use></svg></span>
              <h2 class="card-header-title"><hl:message key="rotulo.dados.consignacao.titulo" /></h2>
            </div>
            <div class="card-body">
                <% if (request.getAttribute("lstCorrespondentes") != null) { %>
                <div class="row">
                  <div class="form-group col-sm-3">
                    <label for="correspondente"><hl:message key="rotulo.correspondente.codigo"/></label>
                    <input type="text" class="form-control" id="correspondente" name="correspondente" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);SelecionaComboExt(COR_CODIGO, this.value);" placeholder='<hl:message key="mensagem.correspondente.digite.codigo"/>'>
                  </div>
                  <div class="form-group col-sm-9">
                    <label for="COR_CODIGO"><hl:message key="rotulo.correspondente.singular"/>&nbsp;<hl:message key="rotulo.campo.opcional"/></label>
                    <select class="form-control form-select" id="COR_CODIGO" name="COR_CODIGO">
                      <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
                      <%
                        List<TransferObject> correspondentes = (List<TransferObject>) request.getAttribute("lstCorrespondentes");
                        Iterator<TransferObject> it = correspondentes.iterator();
                        while (it.hasNext()) {
                          TransferObject next = it.next();
                          String corCodigo = next.getAttribute(Columns.COR_CODIGO) + ";" + next.getAttribute(Columns.COR_IDENTIFICADOR) + ";" + next.getAttribute(Columns.COR_NOME);
                          String corNome = next.getAttribute(Columns.COR_IDENTIFICADOR) + " - " + next.getAttribute(Columns.COR_NOME);
                          if (corNome.length() > 35) {
                              corNome = corNome.substring(0, 32) + "...";
                          }
                          out.print("<option value=\"" + corCodigo + "\">" + corNome + "</option>");
                        }
                      %>
                    </select>
                  </div>
                </div>
                <% } %>
                <% if (validarDataNasc) { %>
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="dataNasc"><hl:message key="rotulo.servidor.dataNasc"/></label>
                    <hl:htmlinput name="dataNasc" type="text" classe="form-control" di="dataNasc" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"/>
                  </div>
                </div>
                <% } %>
                <% if (permiteCadVlrLiqLib) { %>
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="adeVlrLiquido"><hl:message key="rotulo.consignacao.valor.liquido.liberado"/>&nbsp;(<hl:message key="rotulo.moeda"/>)</label>
                    <hl:htmlinput name="adeVlrLiquido"
                                  type="text"
                                  classe="form-control"
                                  di="adeVlrLiquido"
                                  size="12"
                                  mask="#F15"
                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                  value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeVlrLiquido") != null ? request.getParameter("adeVlrLiquido") : "" )%>"
                                  placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.valor.liquido.liberado", responsavel)%>'
                    />
                  </div>
                </div>
                <% } %>
				<% if (serInfBancariaObrigatoria) {%>
                 <div class="row">
                  <div class="form-group col-sm-3">
                    <label for="numBanco"><hl:message key="rotulo.servidor.informacoesbancarias.banco"/></label>
                    <hl:htmlinput name="numBanco" type="text" classe="form-control" di="numBanco" size="3" mask="#D3" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias.numero.banco", responsavel)%>"/>
                  </div>
                  <div class="form-group col-sm-4">
                    <label for="numAgencia"><hl:message key="rotulo.servidor.informacoesbancarias.agencia"/></label>
                    <hl:htmlinput name="numAgencia" type="text" classe="form-control" di="numAgencia" size="5" mask="#*30" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias.numero.agencia", responsavel)%>"/>
                  </div>
                  <div class="form-group col-sm-5">
                    <label for="numConta"><hl:message key="rotulo.servidor.informacoesbancarias.conta"/></label>
                    <hl:htmlinput name="numConta" type="text" classe="form-control" di="numConta" size="12" mask="#*40" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias.numero.conta", responsavel)%>"/>
                  </div>
                  </div>
                <% } %>
                
                 <% if (permiteCadVlrLiqTxJuros) { %>
                 <div class="row">
                  <div class="form-group col-sm-5">
                    <label for="adeTaxaJuros"><%=temCET ? ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel) %></label>
                     <hl:htmlinput name="adeTaxaJuros"
                                       type="text"
                                       classe="form-control"
                                       di="adeTaxaJuros"
                                       size="10"
                                       mask="#F10"
                                       placeHolder="<%= temCET ? (String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.cet", responsavel) : (String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.taxa.juros", responsavel)%>"
                                       onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 8); }"
                                       value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeTaxaJuros") != null ? request.getParameter("adeTaxaJuros") : "" )%>"
                         />
                  </div>
                 </div>
                 <% } %>
                 
                <% if (permiteCadVlrTac || permiteCadVlrIof) { %>
                  <div class="row">
                    <%if (permiteCadVlrTac) {%>
                     <div class="form-group col-sm-6">
                      <label><hl:message key="rotulo.consignacao.valor.tac"/>(<hl:message key="rotulo.moeda"/>)</label>
                            <hl:htmlinput name="adeVlrTac"
                                          type="text"
                                          classe="form-control"
                                          di="adeVlrTac"
                                          size="8"
                                          mask="#F11"
                                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                          value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeVlrTac") != null ? request.getParameter("adeVlrTac") : "" )%>"
                                          placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.tac", responsavel)%>"
                            /> 
                          </div>
                  <%}%>
                     
                  <%if (permiteCadVlrIof) {%>
                  <div class="form-group col-sm-6">
                      <label><hl:message key="rotulo.consignacao.valor.iof"/>(<hl:message key="rotulo.moeda"/>)</label>
                        <hl:htmlinput name="adeVlrIof"
                                      type="text"
                                      classe="form-control"
                                      di="adeVlrIof"
                                      size="8"
                                      mask="#F11"
                                      onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                      value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeVlrIof") != null ? request.getParameter("adeVlrIof") : "" )%>"
                                      placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.iof", responsavel)%>"
                        />
                   </div>
                <%}%>
              </div>
             <% } %>

                <div class="row">
                    <div class="form-group col-sm-12">
                        <label for="adeVlr"><hl:message key="rotulo.consignacao.valor.parcela"/>
                            <%if (!txtExplicativo.isEmpty()) { %>
                            <%=TextHelper.forHtmlContent(txtExplicativo)%>
                            <% } %>
                            (<%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr))%>)
                        </label>
                        <hl:htmlinput name="adeVlr"
                                      type="text"
                                      classe="form-control"
                                      di="adeVlr"
                                      disabled="<%=disabledVlrPostoFixo%>"
                                      size="8"
                                      mask="#F11"
                                      onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                      value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeVlr") != null ? request.getParameter("adeVlr") : "" )%>"
                                      others="<%=TextHelper.forHtmlAttribute(responsavel.isSer() && (prazoFixo || maxPrazo.equals("0")) ? "nf='btnEnvia'" : "" )%>"
                                      placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.digite.valor.parcela", responsavel)%>'
                        />
                    </div>
                </div>
                
                 <% if (permiteCadVlrMensVinc) { %>
                <div class="row">
                  <div class="form-group col-sm-6">
                   <label><hl:message key="rotulo.consignacao.valor.mensalidade.vinc"/>&nbsp;(<hl:message key="rotulo.moeda"/>)</label>
                     <hl:htmlinput name="adeVlrMensVinc"
                                   type="text"
                                   classe="form-control"
                                   di="adeVlrMensVinc"
                                   size="12"
                                   mask="#F15"
                                   onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                   value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeVlrMensVinc") != null ? request.getParameter("adeVlrMensVinc") : "" )%>"
                                   placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.valor.mens.vinc", responsavel)%>"
                     />
                   </div>
                 </div>
               <% } %>
                
              <% if (permiteCadVlrSegPrestamista) { %>
                <div class="row">
                  <div class="form-group col-sm-6">
                  <label><hl:message key="rotulo.consignacao.seguro.prestamista"/>(<hl:message key="rotulo.moeda"/>)</label>
                   <hl:htmlinput name="adeVlrSegPrestamista"
                                 type="text"
                                 classe="form-control"
                                 di="adeVlrSegPrestamista"
                                 size="8"
                                 mask="#F11"
                                 onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                 placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.seguro.prestamista", responsavel)%>"
                   />
                 </div>
               </div>
             <% } %>
                
                <% String rotuloPeriodicidadePrazo = "&nbsp;(" + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel) + ")"; %>
                <% if (request.getAttribute("exibirCampoPeriodicidade") != null) { %>
                <% rotuloPeriodicidadePrazo = ""; %>
                <div class="row">
                  <div class="form-group col-sm-12">
                      <label for="adePeriodicidade"><hl:message key="rotulo.consignacao.periodicidade"/></label>
                      <select class="form-control form-select"
                              onFocus="SetarEventoMascara(this,'#*200',true);"
                              onBlur="fout(this);ValidaMascara(this);"
                              onChange="mudaPeriodicidade()"
                              name="adePeriodicidade" id="adePeriodicidade">
                        <option value="<%=(String)CodedValues.PERIODICIDADE_FOLHA_QUINZENAL%>" selected><hl:message key="rotulo.consignacao.periodicidade.quinzenal"/></option>
                        <option value="<%=(String)CodedValues.PERIODICIDADE_FOLHA_MENSAL%>"><hl:message key="rotulo.consignacao.periodicidade.mensal"/></option>
                      </select>
                  </div>
                </div>
                <% } else { %>
                <hl:htmlinput type="hidden" name="adePeriodicidade" di="adePeriodicidade" value="<%=TextHelper.forHtmlAttribute(PeriodoHelper.getPeriodicidadeFolha(responsavel))%>" />
                <% } %>
                <div class="row">
                  <% if (usaListaPrazos) { %>
                  <div class="form-group col-sm-12">
                    <label for="adePrazoAux"><hl:message key="rotulo.consignacao.prazo"/><%=(String)(rotuloPeriodicidadePrazo)%></label>
                    <select class="form-control form-select" onFocus="SetarEventoMascara(this,'#*200',true);"
                            onBlur="verificaPrazo();fout(this);ValidaMascara(this);"
                            name="adePrazoAux" id="adePrazoAux"
                            onChange="if(!isNaN(parseInt(f0.adePrazoAux.options[f0.adePrazoAux.selectedIndex].value))){f0.adePrazo.value=f0.adePrazoAux.options[f0.adePrazoAux.selectedIndex].value;}else{f0.adePrazo.value='';}"></select>
                  </div>
                  <% } else { %>
                    <% if (!maxPrazo.equals("0") || !isServidor) { %>
                     <div id="divAdePrz" class="adePrz form-group col-sm-8 col-md-6">
                      <label for="adePrz"><hl:message key="rotulo.consignacao.prazo"/><%=(String)(rotuloPeriodicidadePrazo)%></label>
                      <input type="text" name="adePrz" id="adePrz" <%=(responsavel.isSer() ? "nf=\"btnEnvia\"" : "")%> class="form-control" size="8" value="<%=TextHelper.forHtmlAttribute(prazoFixo ? maxPrazo : "" )%>" onFocus="SetarEventoMascara(this,'#D4',true);" onBlur="f0.adePrazo.value=f0.adePrz.value;fout(this);ValidaMascara(this);" placeholder='<hl:message key="mensagem.informacao.ade.prazo"/>'>
                    </div>
                    <% } %>
                    <div id="adePrzInd" class="adeSemPrazo form-group col-sm-4 col-md-6">
                      <span class="text-nowrap align-text-top">
                        <input class="form-check-input ml-1" type="checkbox" name="adeSemPrazo" id="adeSemPrazo" onClick="setaPrazo(true);" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" value="1">
                        <label class="form-check-label labelSemNegirto ml-1" aria-label='<hl:message key="rotulo.consignacao.prazo.indeterminado"/>' for="adeSemPrazo"><hl:message key="rotulo.consignacao.prazo.indeterminado"/></label>
                      </span>
                    </div>
                  <% } %>
                </div>
                <% if (request.getAttribute("lstPeriodos") != null) { %>
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="ocaPeriodo"><hl:message key="rotulo.folha.periodo"/></label>
                    <select name="ocaPeriodo" id="ocaPeriodo" class="form-control form-select" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                      <% for (Date periodo : (Set<Date>) request.getAttribute("lstPeriodos")) { %>
                        <option value="<%=TextHelper.forHtmlAttribute(periodo)%>"><%=TextHelper.forHtmlContent(DateHelper.toPeriodString(periodo))%></option>
                      <% } %>
                    </select>
                  </div>
                </div>
                <% } %>
                <% if (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.RESERVAR_MARGEM_CARENCIA, responsavel)) { %>
                  <div class="row">
                    <div class="form-group col-sm-12">
                      <label for="adeCarencia"><hl:message key="rotulo.consignacao.carencia"/><%=(String)(rotuloPeriodicidadePrazo)%></label>
                      <hl:htmlinput name="adeCarencia" type="text" classe="form-control" di="adeCarencia" size="8" mask="#D2" value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeCarencia") != null ? request.getParameter("adeCarencia").toString() : "0" )%>" others="<%=TextHelper.forHtmlAttribute((carenciaMinPermitida==carenciaMaxPermitida) ? "disabled" : "" )%>" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.carencia", responsavel) %>'/>
                    </div>
                  </div>
                <% } else { %>
                 <hl:htmlinput name="adeCarencia" di="adeCarencia" type="hidden" value="<%=request.getParameter("adeCarencia") != null ? TextHelper.forHtmlAttribute(request.getParameter("adeCarencia").toString()) : "0"%>"/>
                <% } %>

              <% if (request.getAttribute("exibirCampoIndice") != null) { %>
                <% if (request.getAttribute("lstIndices") != null) { %>
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="adeIndice"><hl:message key="rotulo.consignacao.indice"/>&nbsp;<hl:message key="rotulo.campo.opcional"/></label>
                    <select name="adeIndice" id="adeIndice" class="form-control form-select"
                            onFocus="SetarEventoMascara(this,'#*200',true);"
                            onBlur="fout(this);ValidaMascara(this);">
                      <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
                      <%
                       List<TransferObject> indices = (List<TransferObject>) request.getAttribute("lstIndices");
                       Iterator<TransferObject> it = indices.iterator();
                       while (it.hasNext()) {
                           TransferObject next = it.next();
                           String indCodigo = next.getAttribute(Columns.IND_CODIGO).toString() ;  
                           String indDescricao = next.getAttribute(Columns.IND_DESCRICAO).toString() ;
                           out.print("<option value=\"" + indCodigo + ";" + indDescricao + "\">" + indCodigo + " - " + indDescricao + "</option>");
                       }
                     %>
                    </select>
                  </div>
                </div>
                <% } else { %>
                <%
                      boolean vlrIndiceDisabled = (Boolean) request.getAttribute("vlrIndiceDisabled");
                      String vlrIndice = (String) request.getAttribute("vlrIndice");
                      String mascaraIndice = (String) request.getAttribute("mascaraIndice");
                %>
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="adeIndice"><hl:message key="rotulo.consignacao.indice"/>&nbsp;<hl:message key="rotulo.campo.opcional"/></label>
                    <hl:htmlinput name="adeIndice" type="text" others="<%=TextHelper.forHtmlAttribute(vlrIndiceDisabled ? "disabled" : "")%>" classe="form-control" di="adeIndice" size="10" mask="<%=mascaraIndice%>" value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeIndice") != null && !request.getParameter("adeIndice").equals("null")? request.getParameter("adeIndice").toString() : vlrIndice )%>" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.indice", responsavel)%>'/>
                  </div>
                </div>
                <% } %>
              <% } %>

                <% if (possuiCorrecaoVlrPresente) { %>
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="dataEvento"><hl:message key="rotulo.consignacao.data.evento"/></label>
                    <hl:htmlinput name="dataEvento" di="dataEvento" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.data.evento", responsavel)%>'/>
                  </div>
                </div>
                <% } %>

                <% if (!responsavel.isSer()) { %>
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="adeIdentificador"><hl:message key="rotulo.consignacao.identificador"/><% if (!identificadorAdeObrigatorio) { %>&nbsp;<hl:message key="rotulo.campo.opcional"/><% } %></label>
                    <hl:htmlinput name="adeIdentificador" type="text" classe="form-control" di="adeIdentificador" size="15" mask="<%=TextHelper.isNull(mascaraAdeIdentificador) ? "#*40":TextHelper.forJavaScriptAttribute(mascaraAdeIdentificador) %>" nf="btnEnvia" value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeIdentificador") != null ? request.getParameter("adeIdentificador").toString() : "" )%>" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.identificador", responsavel)%>'/>
                  </div>
                </div>
                <% } %>

                <% if (request.getAttribute("exigeModalidadeOperacao") != null) { %>
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="tdaModalidadeOp"><hl:message key="rotulo.consignacao.modalidade.operacao"/></label>
                    <hl:htmlinput name="tdaModalidadeOp" type="text" classe="form-control" di="tdaModalidadeOp" size="6" mask="#*6" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.modalidade.operacao", responsavel)%>'/>
                  </div>
                </div>
                <% } %>

                <% if (request.getAttribute("exigeMatriculaSerCsa") != null) { %>
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="tdaMatriculaCsa"><hl:message key="rotulo.consignacao.matricula.ser.csa"/></label>
                    <hl:htmlinput name="tdaMatriculaCsa" type="text" classe="form-control" di="tdaMatriculaCsa" size="20" mask="#*20" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.matricula.ser.csa", responsavel)%>'/>
                  </div>
                </div>
                <% } %>
                
                <%if (beneficiarios != null && !beneficiarios.isEmpty()) {%>
                    <div class="row">
                      <div class="form-group col-sm-4 col-md-6">
                        <span class="text-nowrap align-text-top">
                          <input class="form-check-input ml-1" type="checkbox" name="reservaDependene" id="reservaDependene" onClick="exibeCodigoDependente();" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                          <label class="form-check-label labelSemNegirto ml-1" aria-label='<hl:message key="rotulo.reservar.margem.dependente"/>' for="reservaDependene"><hl:message key="rotulo.reservar.margem.dependente"/></label>
                        </span>
                      </div>
                      <div class="form-group col-sm-8" id="codigoDependenteVisible">
                        <label for="codigoDependente"><hl:message key="rotulo.reservar.margem.dependente.titulo"/></label>
                        <select class="form-control form-select" name="codigoDependente" id="codigoDependente">
                        <option selected value=""><hl:message key="rotulo.campo.selecione"/></option>
                        <%
                        Iterator<TransferObject> it = beneficiarios.iterator();

                        while (it.hasNext()) {
                        TransferObject beneficiario;
                        String codigo, nome, identificador, tibCodigo;
                        beneficiario = it.next();
                        identificador = (String) beneficiario.getAttribute(Columns.BFC_IDENTIFICADOR);
                        codigo = (String) beneficiario.getAttribute(Columns.BFC_CODIGO);
                        nome = "";
                        if (TipoBeneficiarioEnum.TITULAR.tibCodigo.equals((String) beneficiario.getAttribute(Columns.TIB_CODIGO))){
                            continue;
                        }
                        if (!TextHelper.isNull(identificador) && !TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_NOME))) {
                            nome = identificador + "-" + (String) beneficiario.getAttribute(Columns.BFC_NOME);
                        } else if (!TextHelper.isNull(identificador)){
                            nome = identificador;
                        } else if (!TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_NOME))) {
                            nome = (String) beneficiario.getAttribute(Columns.BFC_NOME);
                        } else {
                            nome = "-";
                        }
                        %>
                        <option value="<%=TextHelper.forHtmlAttribute(codigo)%>" ><%=TextHelper.forHtmlContent(nome)%></option>
                        <% } %>
                        </select>
                      </div>
                      <% if(responsavel.temPermissao(CodedValues.FUN_CADASTRAR_BENEFICIARIO_NA_RESERVA) && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, responsavel)) { %>
                      <div id="novoBeneficiario" class="form-group col-sm-4 col-md-4 align-content-center"><strong><a href="#no-back" onClick="postData('../v3/alterarBeneficiarios?acao=novoReserva&_skip_history_=true&reserva=S&<%=Columns.getColumnName(Columns.SER_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(serCodigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.novo.beneficiario" /></a></strong></div>
                      <% } %>
                    </div>
                    <%if (!TextHelper.isNull(permiteDescontoViaBoleto) && !permiteDescontoViaBoleto.equals(CodedValues.NAO_PERMITE_PAGAMENTO_VIA_BOLETO)) { %>
                      <div class="row">
                        <div class="form-group col-sm-4 col-md-6">
                          <span class="text-nowrap align-text-top">
                            <input class="form-check-input ml-1" type="checkbox" name="permiteDescontoViaBoleto" id="permiteDescontoViaBoleto" value="S" <%=permiteDescontoViaBoleto.equals(CodedValues.PAGAMENTO_VIA_BOLETO_OBRIGATORIO) ? "checked disabled" :"" %>>
                            <label class="form-check-label labelSemNegirto ml-1" aria-label='<hl:message key="rotulo.reservar.margem.pagar.via.boleto"/>' for="permiteDescontoViaBoleto"><hl:message key="rotulo.reservar.margem.pagar.via.boleto"/></label>
                          </span>
                        </div>
                      </div>
                    <%} %>
                <%} %>

                <% if (lstTipoDadoAdicional != null) { %>
                  <% for (TransferObject tda : lstTipoDadoAdicional) { %>
                   <hl:paramv4 
                       prefixo="TDA_"
                       descricao="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"
                       codigo="<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>"
                       dominio="<%=(String) tda.getAttribute(Columns.TDA_DOMINIO)%>"
                       valor="<%= dadosAutorizacao != null && dadosAutorizacao.get((String) tda.getAttribute(Columns.TDA_CODIGO)) != null ? dadosAutorizacao.get((String) tda.getAttribute(Columns.TDA_CODIGO)) : "" %>"
                       desabilitado="<%= CodedValues.CAS_BLOQUEADO.equals(tda.getAttribute(Columns.SPT_EXIBE)) %>"
                       />
                  <% } %>
                <% } %>

                <% if (request.getAttribute("processaReservaMargem") != null) { %>
                  <%=TextHelper.forHtmlContent(request.getAttribute("processaReservaMargem"))%>
                <% } %>
  
<%--                 TODO Anexo de arquivo	--%>
                <%
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) &&
                                responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO) && !inclusaoJudicial) {
                %>
                    <hl:fileUploadV4 obrigatorio="<%=(boolean)anexoObrigatorio%>" tipoArquivo="anexo_consignacao"/>
                <%
                }
                %>
            </div>
          </div>
          <%-- Inclusão Avançada --%>
          <%
          if (responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO) && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) {
          %>
              <%@ include file="../reservarMargem/incluirCamposInclusaoAvancada_v4.jsp" %>
          <% }%>
          <%
          if (!TextHelper.isNull(termoConsentimentoDadosServidor)) {
          %>
             <div>
               <p>  
                 <span class="info" style="display: block;">
                    <input type="checkbox" name="aceitoTermoUsoColetaDados" id="aceitoTermoUsoColetaDados" value="S" />
                    <hl:message key="mensagem.termo.de.consentimento.coleta.dados.servidor.aceito"/>&nbsp;<a href="#" data-bs-toggle="modal" data-bs-target="#confirmarTermoUso"><hl:message key="mensagem.termo.de.consentimento.coleta.dados.servidor.link"/></a>
                 </span>   
               </p>
             </div>
          <%
          }
          %> 
        </div>
        <%=SynchronizerToken.generateHtmlToken(request)%>
        <hl:htmlinput type="hidden" name="acao" value="<%=TextHelper.forHtmlAttribute(request.getAttribute("proximaOperacao") != null ? request.getAttribute("proximaOperacao").toString() : "autorizarReserva")%>" />
        <hl:htmlinput type="hidden" di="CSA_CODIGO" name="CSA_CODIGO" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>" />
        <hl:htmlinput type="hidden" name="CNV_CODIGO" value="<%=TextHelper.forHtmlAttribute(cnvCodigo)%>" />
        <hl:htmlinput type="hidden" di="SVC_CODIGO" name="SVC_CODIGO" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>" />
        <hl:htmlinput type="hidden" name="PLA_CODIGO" value="<%=TextHelper.forHtmlAttribute(plaCodigo)%>" />
        <hl:htmlinput type="hidden" name="PRM_CODIGO" value="<%=TextHelper.forHtmlAttribute(prmCodigo)%>" />
        <hl:htmlinput type="hidden" di="RSE_CODIGO" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" />

        <hl:htmlinput type="hidden" name="adePrazo" value="<%=TextHelper.forHtmlAttribute(prazoFixo ? maxPrazo : "" )%>" />
        <hl:htmlinput type="hidden" name="adeIntFolha" value="<%=TextHelper.forHtmlAttribute((intFolha))%>" />
        <hl:htmlinput type="hidden" name="adeIncMargem" value="<%=TextHelper.forHtmlAttribute((incMargem))%>" />
        <hl:htmlinput type="hidden" name="adeTipoVlr" value="<%=TextHelper.forHtmlAttribute(tipoVlr)%>" />
        <hl:htmlinput type="hidden" name="vlrLimite" value="<%=TextHelper.forHtmlAttribute(vlrLimite)%>" />
        <hl:htmlinput type="hidden" name="rsePrazo" value="<%=TextHelper.forHtmlAttribute(servidor.getAttribute(Columns.RSE_PRAZO))%>" />
        <hl:htmlinput type="hidden" name="rseMatricula" value="<%=TextHelper.forHtmlAttribute(rseMatricula)%>" />
        <hl:htmlinput type="hidden" name="tipo" value="<%=TextHelper.forHtmlAttribute(tipo)%>" />
        <hl:htmlinput type="hidden" name="nomeDependente" di="nomeDependente" />
        <%
        if (possuiControleVlrMaxDesconto) {
        %>
          <hl:htmlinput type="hidden" name="vlrMaxParcelaSaldoDevedor" value="<%=TextHelper.forHtmlAttribute(vlrMaxParcelaSaldoDevedor)%>" />
        <%
        }
        %>
        <%
        if (servidorDeveSerKYCComplaint && !servidorValidouKYC) {
        %>
          <hl:htmlinput type="hidden" name="cienteKYCNaoFinalizado" value="" />
        <%
        }
        %>
        <%
        if (beneficiarios != null && !beneficiarios.isEmpty()) {
        %>
          <hl:htmlinput type="hidden" name="reservaSaudeSemRegras" value="true" />
        <%
        }
        %>
        <%
        if(portalBeneficio && !TextHelper.isNull(corCodigoPortal)){
        %>
          <hl:htmlinput type="hidden" name="COR_CODIGO" value="<%=TextHelper.forHtmlAttribute(corCodigoPortal)%>" />
          <hl:htmlinput type="hidden" name="PORTAL_BENEFICIO" value="<%=TextHelper.forHtmlAttribute(portalBeneficio)%>" />
        <%
        }
        %>
        <%
        if(enderecoObrigatorio){
        %>
            <hl:htmlinput type="hidden" name="enderecoObrigatorio" value="<%=TextHelper.forHtmlAttribute(enderecoObrigatorio)%>" />
        <%
        }
        %>
        <%
        if(celularObrigatorio){
        %>
            <hl:htmlinput type="hidden" name="celularObrigatorio" value="<%=TextHelper.forHtmlAttribute(celularObrigatorio)%>" />
        <%
        }
        %>
        <%
        if(enderecoCelularObrigatorio){
        %>
            <hl:htmlinput type="hidden" name="enderecoCelularObrigatorio" value="<%=TextHelper.forHtmlAttribute(enderecoCelularObrigatorio)%>" />
        <%
        }
        %>
        <%if (inclusaoJudicial){ %>
            <hl:htmlinput type="hidden" name="inclusaoJudicial" value="<%=TextHelper.forHtmlAttribute(inclusaoJudicial)%>" />
        <%} %>
      </div>
        <%
        if(exigeCaptcha) {
        %>
        <hl:modalCaptchaSer type="reservar"/>
        <%
        }
        %>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="desabilitaCampos(); if(exibeMensagemCategoria('<%=msgPertenceCategoria%>') && verificaAnexo() && verificaDataNasc() && (<%=(String)((permiteCadVlrTac || permiteCadVlrIof || permiteCadVlrLiqLib || permiteCadVlrMensVinc) ? "verificaCadInfFin() && " : "")%> <%=(String)(serInfBancariaObrigatoria && validaDadosBancariosAvancado.booleanValue() ? "verificaInfBanco() && " : "")%> campos() && verificaPrazo() && vf_reservar_margem('<%=TextHelper.forJavaScriptAttribute(validaMargemAvancado.toString())%>', <%=(boolean) permiteVlrNegativo%>) && verificaCarencia()) && verificarCamposAdicionais() && verificaTermoConsentimento()){enableAllCustom(); <%=(String)(responsavel.isCsaCor() && somaValorContratosTratamentoEspecial.signum()>0 ? "tratamentoEspecialMargem() && " : "")%> f0.submit();} return false;" alt="<hl:message key="rotulo.botao.confirmar"/>" title="<hl:message key="rotulo.botao.confirmar"/>"><svg width="17"><use xlink:href="#i-confirmar"></use></svg><hl:message key="rotulo.botao.confirmar"/></a>
      </div>
    </form>
</c:set>
<c:set var="javascript">
<%
if (request.getAttribute("exibeInformacaoCsaServidor") != null) {
%>
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
   <%
   }
   %>
   <link rel="stylesheet" type="text/css" href="../viewer/css/viewer.css"/>
   <script  src="../viewer/js/viewer.min.js"></script>
   <script type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>
    <%
    if (exibeCaptchaAvancado) {
    %>
    <script src='https://www.google.com/recaptcha/api.js'></script>
    <%
    }
    %>
   <hl:fileUploadV4 scriptOnly="true" tipoArquivo="anexo_consignacao" />
   <script type="text/JavaScript">
     f0 = document.forms[0];
     <%=validaMargemViaJavascript ? "arMargemConsignavel = '" + (margemDisponivel.getExibeMargem().isSemRestricao() || margemConsignavel.doubleValue() > 0 ? TextHelper.forJavaScriptBlock(margemConsignavel.toString()) : "0.00") + "';" : ""%>
     <%="adeVlr = '" + TextHelper.forJavaScriptBlock(adeVlrPadrao) + "';"%>
     <%="alteraAdeVlr = " + TextHelper.forJavaScriptBlock(alteraAdeVlr) + ";"%>
     <%="maxPrazo = '" + TextHelper.forJavaScriptBlock(maxPrazo) + "';"%>
     <%="carenciaMinPermitida = " + TextHelper.forJavaScriptBlock(carenciaMinPermitida) + ";"%>
     <%="carenciaMaxPermitida = " + TextHelper.forJavaScriptBlock(carenciaMaxPermitida) + ";"%>
     <%="permitePrazoMaiorContSer = " + TextHelper.forJavaScriptBlock(permitePrazoMaiorContSer) + ";"%>
     <%="identificadorObrigatorio = " + TextHelper.forJavaScriptBlock(identificadorAdeObrigatorio) + ";"%>
     <%=(usaListaPrazos ? "var arPrazosMensal = [" + TextHelper.join(prazosPossiveisMensal, ", ") + "];" : "")%>
     <%=(prazosPossiveisPeriodicidadeFolha != null && !prazosPossiveisPeriodicidadeFolha.isEmpty() ? "var arPrazosPeriodicidadeFolha = [" + TextHelper.join(prazosPossiveisPeriodicidadeFolha, ", ") + "];" : "")%>
     <%if (!permiteCadVlrLiqLib && paramSvcCse.isTpsValidarTaxaJuros() && validaTaxaAvancado) {%> 
         alert('<hl:message key="mensagem.erro.valida.cet.sem.vlr.lib"/>');
     <%}%>
     var valor = 'IB';
     var validarInfBancaria = <%=TextHelper.forJavaScriptBlock(serInfBancariaObrigatoria && validarInfBancaria)%>;
     var validarDataNasc = <%=TextHelper.forJavaScriptBlock(validarDataNasc)%>;

     function formLoad() {
       setaParamSvc();
       mudaPeriodicidade();
       focusFirstField();
       <%if (servidorDeveSerKYCComplaint && !servidorValidouKYC) {%>
       var deAcordo = confirm('<hl:message key="mensagem.aviso.kyc.nao.validado"/>');
       if (deAcordo) {
         f0.cienteKYCNaoFinalizado.value = 'S';
       } else {
         postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');
       }
       <%}%>

       var reservaDependeneInput = document.getElementById("reservaDependene");
       var codigoDependenteInput = document.getElementById("codigoDependenteVisible");
       if (reservaDependeneInput != "undefined" && reservaDependeneInput !=null && reservaDependeneInput.checked){
    	   $("#codigoDependenteVisible").show();
           <% if(responsavel.temPermissao(CodedValues.FUN_CADASTRAR_BENEFICIARIO_NA_RESERVA) && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, responsavel)) { %>
           $("#novoBeneficiario").show();
           <% } %>
       } else if (codigoDependenteInput != null && codigoDependenteInput != "undefined"){
    	   $("#codigoDependenteVisible").hide();
           <% if(responsavel.temPermissao(CodedValues.FUN_CADASTRAR_BENEFICIARIO_NA_RESERVA) && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, responsavel)) { %>
           $("#novoBeneficiario").hide();
           <% } %>
       }
       
       <%if (request.getAttribute("exibeInformacaoCsaServidor") != null) {%>
       	$('#exibeInformacaoCsaServidor').modal('show');
       <%}%>
     }

     window.onload = formLoad;

     function enableAllCustom() {
       if (document.forms[0] != null) {
         for (var i = 0; (i < document.forms[0].elements.length); i++) {
           var e = document.forms[0].elements[i];
           if (e.type != 'button' && e.type != 'hidden' &&
               e.type != 'image' && e.type != 'reset' &&
               e.type != 'submit') {
             if (e.disabled && e.id.slice(-1) != '_') {  
               e.disabled = false;
             }
           }
         }
       }
     } 

     function campos() {
       if (f0.adeVlrLiquido != null && f0.adeVlrLiquido.disabled == false) {
         if (f0.adeVlrLiquido.value == '') {
           alert('<hl:message key="mensagem.informe.ade.valor.liberado"/>');
           f0.adeVlrLiquido.focus();
           return false;
         }
       }
       if (f0.dataEvento != null && f0.dataEvento.disabled == false) {
         if (f0.dataEvento.value != '' && verificaData(f0.dataEvento.value) == false) {
           f0.dataEvento.focus();
           return false;
         }
       }
       <%if (request.getAttribute("exigeModalidadeOperacao") != null) {%>       
       if (f0.tdaModalidadeOp.value == null || f0.tdaModalidadeOp.value == '') {
         alert('<hl:message key="mensagem.erro.modalidade.operacao.obrigatorio"/>');
         return false;
       }
       <%}%>
       
       <%if (request.getAttribute("exigeMatriculaSerCsa") != null) {%>
       if (f0.tdaMatriculaCsa.value == null || f0.tdaMatriculaCsa.value == '') {
         alert('<hl:message key="mensagem.erro.matricula.csa.obrigatoria"/>');
         return false;
       }    
       <%}%>
       var dependenteSomenteDescontoEmFolha = "";
       <%if (beneficiarios != null && !beneficiarios.isEmpty()) {%>
            var reservaDependenteCheck = document.getElementById("reservaDependene");
            if (reservaDependenteCheck.checked){
                var codigoDependenteValue = document.getElementById("codigoDependente").value;
                var beneficiarioExiste = "";
                var beneficiarioNome = "";
                if (codigoDependenteValue == "undefined" || codigoDependenteValue == "" || codigoDependenteValue == null){
                	alert('<hl:message key="mensagem.reservar.margem.dependente.obrigatorio"/>');
                    return false;
                }
                <%for (TransferObject beneficiario : beneficiarios){
                      String bfcCodigo = (String) beneficiario.getAttribute(Columns.BFC_CODIGO);
                      String bfcClassificacao = (String) beneficiario.getAttribute(Columns.BFC_CLASSIFICACAO);
                      String bfcNome = (String) beneficiario.getAttribute(Columns.BFC_NOME);%>
                    if(<%=!TextHelper.isNull(bfcCodigo)%> && codigoDependenteValue === "<%=bfcCodigo%>"){
                       beneficiarioExiste = codigoDependenteValue;
                       beneficiarioNome = "<%=bfcNome%>";
                       dependenteSomenteDescontoEmFolha = "<%=bfcClassificacao%>";
                    }
                    console.log(beneficiarioNome);
                <%}%>
                if(beneficiarioExiste === ""){
                	alert('<hl:message key="mensagem.reservar.margem.dependente.nao.existe"/>');
                    return false;
                }
                document.getElementById("nomeDependente").value = beneficiarioNome;
            }
       <%}%>
       var pagamentoViaBoletoCheck = document.getElementById("permiteDescontoViaBoleto");
       <%if (!TextHelper.isNull(permiteDescontoViaBoleto) && !permiteDescontoViaBoleto.equals(CodedValues.NAO_PERMITE_PAGAMENTO_VIA_BOLETO)) {%>
           if (pagamentoViaBoletoCheck.checked){
               <%="arMargemConsignavel = ''"%>
               if(dependenteSomenteDescontoEmFolha != "" && dependenteSomenteDescontoEmFolha != null && dependenteSomenteDescontoEmFolha == "<%=CodedValues.BFC_CLASSIFICACAO_ESPECIAL%>"){
               	alert('<hl:message key="mensagem.reservar.margem.dependente.via.boleto.nao.permitido"/>');
                   return false;
               }
           }
      <%}%>
      <%if (!TextHelper.isNull(permiteDescontoViaBoleto) && permiteDescontoViaBoleto.equals(CodedValues.NAO_PERMITE_PAGAMENTO_VIA_BOLETO)) {%>
          if (pagamentoViaBoletoCheck != null && pagamentoViaBoletoCheck.checked){
              alert('<hl:message key="mensagem.erro.reservar.margem.via.boleto.nao.permitido"/>');
              return false;
          }
      <%}%>
       if(<%=responsavel.isSer()%>){
          if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)%> && (<%=celularObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_TEL != null && trim(f0.SER_TEL.value) == '') {
      	    alert('<hl:message key="mensagem.informe.servidor.telefone"/>');
      	    f0.SER_TEL.focus();
      	    return false;
      	  }
      	  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_END != null && trim(f0.SER_END.value) == '') {
      	    alert('<hl:message key="mensagem.informe.servidor.logradouro"/>');
      	    f0.SER_END.focus();
      	    return false;  
      	  }
      	  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_NRO != null && trim(f0.SER_NRO.value) == '') {
      	    alert('<hl:message key="mensagem.informe.servidor.numero"/>');
      	    f0.SER_NRO.focus();
      	    return false;  
      	  }
      	  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)%> && f0.SER_COMPL != null && trim(f0.SER_COMPL.value) == '') {
      	    alert('<hl:message key="mensagem.informe.servidor.complemento"/>');
      	    f0.SER_COMPL.focus();
      	    return false;  
      	  }
      	  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_BAIRRO != null && trim(f0.SER_BAIRRO.value) == '') {
      	    alert('<hl:message key="mensagem.informe.servidor.bairro"/>');
      	    f0.SER_BAIRRO.focus();
      	    return false;  
      	  }
      	  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_CIDADE != null && trim(f0.SER_CIDADE.value) == '') {
      	    alert('<hl:message key="mensagem.informe.servidor.cidade"/>');
      	    f0.SER_CIDADE.focus();
      	    return false;  
      	  }
      	  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_CEP != null && trim(f0.SER_CEP.value) == '') {
      	    alert('<hl:message key="mensagem.informe.servidor.cep"/>');
      	    f0.SER_CEP.focus();
      	    return false;  
      	  }
      	  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_UF != null && trim(f0.SER_UF.value) == '') {
      	    alert('<hl:message key="mensagem.informe.servidor.estado"/>');
      	    f0.SER_UF.focus();
      	    return false;  
      	  }
      	  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)%> && (<%=celularObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_CEL != null && trim(f0.SER_CEL.value) == '') {
      	    alert('<hl:message key="mensagem.informe.servidor.celular"/>');
      	    f0.SER_CEL.focus();
      	    return false;  
      	  }
       }
       return true;
     }

     function verificaDataNasc() {
       if (validarDataNasc) {
         var dataNascBase = '<%=TextHelper.forJavaScriptBlock(JCryptOld.crypt("IB", TextHelper.isNull(serDataNasc) ? "vazio" : serDataNasc.replaceAll("/", "")))%>';
         var dataNasc = f0.dataNasc.value;
         if (dataNasc == '') { 
           alert('<hl:message key="mensagem.dataNascNaoInformada"/>');
           f0.dataNasc.focus();
           return false; 
         } else {
           dataNasc = dataNasc.replace(/\//g, '');
           dataNasc = Javacrypt.crypt(valor, dataNasc)[0];
           if (dataNasc != dataNascBase) {
             alert('<hl:message key="mensagem.dataNascNaoConfere"/>');
             return false;
           }
         }
       }
       return true;
     }

     function verificaInfBanco() {
       var Controles;
       var Msgs;

       Controles = new Array("numBanco", "numAgencia", "numConta");
       Msgs = new Array('<hl:message key="mensagem.informacaoBancariaObrigatoria"/>',
                        '<hl:message key="mensagem.informacaoBancariaObrigatoria"/>',
                        '<hl:message key="mensagem.informacaoBancariaObrigatoria"/>');

       var banco = Javacrypt.crypt(valor, formataParaComparacao(f0.numBanco.value))[0];
       var agencia = Javacrypt.crypt(valor, formataParaComparacao(f0.numAgencia.value))[0];

       var conta = formataParaComparacao(f0.numConta.value);
       var pos = 0;
       var letra = conta.substr(pos, 1);
       while (letra == 0 && pos < conta.length) {
         pos++;
         letra = conta.substr(pos, 1);
       }

       conta = conta.substr(pos,conta.length);
       var conta1 = Javacrypt.crypt(valor, conta.substr(0, conta.length/2))[0];
       var conta2 = Javacrypt.crypt(valor, conta.substr(conta.length/2, conta.length))[0];
   
       if (ValidaCampos(Controles, Msgs)) {
         <%if (request.getAttribute("rseNaoTemInfBancaria") != null) { /* servidor não tem informações bancárias cadastradas */%>
         return true;
         <%} else {%>
         if (((banco != '<%=TextHelper.forJavaScriptBlock(numBanco)%>') || (agencia != '<%=TextHelper.forJavaScriptBlock(numAgencia)%>') || (conta1 != '<%=TextHelper.forJavaScriptBlock(numConta1)%>') || (conta2 != '<%=TextHelper.forJavaScriptBlock(numConta2)%>')) &&
             ((banco != '<%=TextHelper.forJavaScriptBlock(numBancoAlt)%>') || (agencia != '<%=TextHelper.forJavaScriptBlock(numAgenciaAlt)%>') || (conta1 != '<%=TextHelper.forJavaScriptBlock(numContaAlt1)%>') || (conta2 != '<%=TextHelper.forJavaScriptBlock(numContaAlt2)%>'))) {
           if (validarInfBancaria) {
             alert('<hl:message key="mensagem.informacaoBancariaIncorreta"/>');
             return false;
           }
           if (confirm('<hl:message key="mensagem.informacaoBancariaIncorreta.continuar"/>')) {
             return true;
           } else {
             f0.numBanco.focus();
             return false;
           }
         }
         return true;
         <%}%>
       }
       return false;
     }
     
     function exibeMensagemCategoria(msg){
    	 <%if(exibeAlertaMsgPertenceCategoria){%>
    		 if(confirm(msg)){
     	        return true;
     	    }else {
     	        return false;
     	    }
    	 <%} else { %>
    	 		return true;
    	 <%}%>
    	    
    	}

     function verificaCadInfFin() {
       var Controles;
       var Msgs;

       Controles = new Array("adeVlrTac", "adeVlrIof", "adeVlrLiquido", "adeVlrMensVinc", "adeVlrSegPrestamista", "adeTaxaJuros", "dataEvento");

       Msgs = new Array('<hl:message key="mensagem.informe.ade.valor.tac"/>',
                        '<hl:message key="mensagem.informe.ade.valor.iof"/>',
                        '<hl:message key="mensagem.informe.ade.valor.liberado"/>',
                        '<hl:message key="mensagem.informe.ade.valor.mensalidade"/>',
                        '<hl:message key="mensagem.informe.ade.valor.seguro"/>',
                        '<hl:message key="<%=(String)(temCET ? "mensagem.informe.ade.valor.cet" : "mensagem.informe.ade.valor.taxa.juros")%>"/>',
                        '<hl:message key="mensagem.informe.data.evento"/>');

       if (ValidaCampos(Controles, Msgs)) {
         if (f0.adeVlrLiquido != null ? parse_num(f0.adeVlrLiquido.value) <= 0.00 : false) {
           alert('<hl:message key="mensagem.erro.valor.liberado.incorreto"/>');
           f0.adeVlrLiquido.focus();
         } else if (f0.adeVlrTac != null && parse_num(f0.adeVlrTac.value) > <%=TextHelper.forJavaScriptBlock(maxTacCse)%>) {
           alert('<hl:message key="mensagem.erro.valor.tac.maximo" arg0="<%=TextHelper.forHtmlAttribute(NumberHelper.format(maxTacCse, NumberHelper.getLang()))%>"/>');
           f0.adeVlrTac.focus();
         } else {
           return true;
         }
         return false;
       }
     }

     function setaPrazo(focar) {
       if (f0.adePrz != null && f0.adeSemPrazo != null) {
         f0.adePrz.disabled = f0.adeSemPrazo.checked || <%=TextHelper.forJavaScriptBlock(prazoFixo)%>;
         if (f0.adeSemPrazo.checked) {
           f0.adePrazo.value = '';
           f0.adePrz.value = '';
         } else if (focar) {
           f0.adePrz.focus();
         }
       }
     }

     function setaParamSvc() {
       if (alteraAdeVlr) {
       if (adeVlr != null && adeVlr != undefined && adeVlr != '' && parseFloat(parse_num(adeVlr)) > 0) {
           f0.adeVlr.value = adeVlr;
       }
         f0.adeVlr.disabled = false;
       } else {
         f0.adeVlr.value = adeVlr;
         f0.adeVlr.disabled = true;
       }

       var carencia = parseInt(f0.adeCarencia.value);

       if (carencia > carenciaMaxPermitida) {
         f0.adeCarencia.value = carenciaMaxPermitida;
       }
       if (carencia < carenciaMinPermitida) {
         f0.adeCarencia.value = carenciaMinPermitida;
       }

       <%if (usaListaPrazos) {%>
         loadSelectOptions(f0.adePrazoAux, arPrazosMensal, f0.adePrazo.value);
       <%}%>
       desabilitaCampos();
     }

     function desabilitaCampos() {
         if (maxPrazo > 0) { // somente prazo determinado menor que maxPrazo
             if (f0.adeSemPrazo != null) {
                 f0.adeSemPrazo.checked = false;
                 f0.adeSemPrazo.disabled = true;
             }
         } else if (maxPrazo == 0) {  // quando prazo inderteminado
           f0.adeSemPrazo.checked = true;
           f0.adeSemPrazo.disabled = true;
       } else {                   // qualquer prazo
         if (f0.adePrz != null)
           f0.adePrz.disabled = false;
         if (f0.adeSemPrazo != null)
           f0.adeSemPrazo.disabled = false;
       }
       setaPrazo(false);
     }

     function verificaCarencia() {
       var mensagem = "";
       var carencia = parseInt(f0.adeCarencia.value);
  
       if ((carencia < carenciaMinPermitida) || (carencia > carenciaMaxPermitida)) {
         if (carenciaMaxPermitida > carenciaMinPermitida) {
           mensagem = '<hl:message key="mensagem.erro.carencia.entre.min.max"/>'.replace("{0}", carenciaMinPermitida).replace("{1}", carenciaMaxPermitida);
         } else if (carenciaMaxPermitida < carenciaMinPermitida) {
           mensagem = '<hl:message key="mensagem.erro.carencia.menor.max"/>'.replace("{0}", carenciaMaxPermitida);
         } 
         alert(mensagem);
         f0.adeCarencia.focus();
         return false;
       } else {
         return true;
       }
     }

     <%if (servicoCompulsorio) {%>
     function abrirComposicaoCompulsorio() {       
       postData('../v3/listarCompulsorio?acao=reservar&SVC_CODIGO=<%=TextHelper.forJavaScriptBlock(svcCodigo)%>&RSE_CODIGO=<%=TextHelper.forJavaScriptBlock(rseCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>');
     }
     <%}%>
     
     function verificaAnexo() {
       <%if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) &&
                   responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO) && anexoObrigatorio && !inclusaoJudicial) {%>
         if (document.getElementById('FILE1').value == '') {
           alert('<hl:message key="mensagem.informe.arquivo.upload"/>');
           return false;
         }
         
         <%if(!TextHelper.isNull(qtdeMinAnexos) && Integer.valueOf(qtdeMinAnexos) > 0){%>
         		let elemento = document.getElementById("pic-progress-wrap-FILE1");
         		let qtdeMin = <%=qtdeMinAnexos%>;
         		if(elemento == null || elemento == 'undefined' || elemento.childNodes.length < qtdeMin){
         			alert('<%=ApplicationResourcesHelper.getMessage("mensagem.erro.upload.arquivo.qunt.min", responsavel, qtdeMinAnexos)%>');
                    return false;
         		}
         <%}%>
       <% } %>
         return true;
     }

     function mudaPeriodicidade() {
     <% if (!PeriodoHelper.folhaMensal(responsavel)) { %>       
       if (f0.adePeriodicidade != undefined && f0.adePeriodicidade != null) {
         if (f0.adePeriodicidade.value == 'Q') {
           carenciaMinPermitida = carenciaMinPermitida * 2;
           carenciaMaxPermitida = carenciaMaxPermitida * 2;
         } else {
           carenciaMinPermitida = carenciaMinPermitida / 2;
           carenciaMaxPermitida = carenciaMaxPermitida / 2;
         }
         if (f0.adePrazoAux) {
           f0.adePrazoAux.selectedIndex = "0";
           if (f0.adePeriodicidade.value != 'M') {
             loadSelectOptions(f0.adePrazoAux, arPrazosPeriodicidadeFolha, '');
           } else {
             loadSelectOptions(f0.adePrazoAux, arPrazosMensal, '');
           }
         }
       }
     <% } %>
     }

     <% if (responsavel.isCsaCor() && somaValorContratosTratamentoEspecial.signum()>0 ) { %>
     function tratamentoEspecialMargem() {
       if (f0.adeVlr != null && f0.adeVlr.value != '' && f0.adeVlr.disabled == false) {
       somaValorContratosTratamentoEspecial=<%=TextHelper.forJavaScriptBlock(somaValorContratosTratamentoEspecial)%>;
       margemTratamentoEspecial=<%=TextHelper.forJavaScriptBlock(margemTratamentoEspecial)%>;
         if (somaValorContratosTratamentoEspecial>0 && parse_num(f0.adeVlr.value)>margemTratamentoEspecial) {
           return confirm('<hl:message key="mensagem.informacao.valor.parcela.maior.margem.tratamento.especial"/>'); 
         }       
      }
       return true;
     }
     <% } %>

     function exibirmargem(){
         <% if (exibeCaptchaDeficiente) { %>
         montaCaptchaSomSer('reservar');
         <% } %>
         $('#modalCaptcha_reservar').modal('show');
     }
     function verificarCamposAdicionais() {
     
     <% if (lstTipoDadoAdicional != null) { %>
     <% for (TransferObject tda : lstTipoDadoAdicional) { %>
     	var sptExibe = '<%=(String) tda.getAttribute(Columns.SPT_EXIBE)%>'; 
        var cptExibe = '<%=(String) tda.getAttribute(Columns.CPT_EXIBE)%>'; 
     	if ('O' == sptExibe || 'O' == cptExibe) {
     		var elements = document.getElementsByName('TDA_<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>')

     		if (elements[0].type == 'text') {
     			var value = elements[0].value;
     			if (!value || !value.trim()) {
         			alert('<hl:message key="mensagem.preencher.campos.adicionais" arg0="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"/> ');
         			return false;
         		}
     		} else if (elements[0].type == 'radio') {
				var preenchido = false;
     			for (el of elements) {
    				if (el.checked) {
						preenchido = true;
						break;
            		}
             	}
             	if (!preenchido) {
         			alert('<hl:message key="mensagem.preencher.campos.adicionais" arg0="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"/> ');
             		return false;
                }
         	}
        }
     <% } %>
   <% } %>

   		return true;
     }

     function verificaTermoConsentimento() {
    	  <% if (!TextHelper.isNull(termoConsentimentoDadosServidor)) { %>
    	  var checkboxVer = document.getElementById("aceitoTermoUsoColetaDados");
    	  if (!checkboxVer.checked) {
    	    alert('<hl:message key="mensagem.termo.de.consentimento.coleta.dados.servidor.alerta"/>');
    	    return false;
    	  }
    	  <% } %>
    	  return true;
  	}

    function exibeCodigoDependente(){
    	if(document.getElementById("reservaDependene").checked){
            $("#codigoDependenteVisible").show();
            <% if(responsavel.temPermissao(CodedValues.FUN_CADASTRAR_BENEFICIARIO_NA_RESERVA) && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, responsavel)) { %>
            $("#novoBeneficiario").show();
            <% } %>
        } else {
            $("#codigoDependenteVisible").hide();
            <% if(responsavel.temPermissao(CodedValues.FUN_CADASTRAR_BENEFICIARIO_NA_RESERVA) && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, responsavel)) { %>
            $("#novoBeneficiario").hide();
            <% } %>
        }
    }
    <%if(!TextHelper.isNull(nomeArqFotoServidor) && (responsavel.isCsaCor() || responsavel.isCseOrg())){ %>
        $(document).ready(function() {
        	const viewer = new Viewer(document.getElementById('conteudo'), {
          	  inline: false,
                backdrop: 'static',
                toolbar: {
              	    zoomIn: true,
              	    zoomOut: true,
              	    oneToOne: true,
              	    rotateLeft: true,
              	    rotateRight: true,
              	    flipHorizontal: true,
              	    flipVertical: true,
              	  },
            	});
        });
    <%}%>
    
    <% if (request.getAttribute("exibeInformacaoCsaServidor") != null) { %>
    $(document).ready(function() {
	    $.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );
	    $('#dataTables').DataTable({
		    "paging": true,
		  	"pageLength": 5,
	    	"lengthMenu": [
	          [5, 10, 20, -1],
	          [5, 10, 20, '<hl:message key="mensagem.datatables.all"/>']
	        ],
	        "pagingType": "simple_numbers",
	        "dom": '<"card-body" <"row pl-0 pr-4" <"col-sm-2 pl-0" B > <"col-sm-6 pl-0" l > <"col-sm-4 pr-0" f >> <"table-responsive" t> > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
	        buttons: [],
	        stateSave: true,
	        bFilter: false,
	        bLengthChange: false,
	        stateSaveParams: function (settings, data) {
	      	    data.search.search = "";
	      	  },
	        language: {
	        		  processing:        '<hl:message key="mensagem.datatables.processing"/>',
	                  loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
	                  info:              '<hl:message key="mensagem.datatables.info.informacao.csa.servidor"/>',
	                  lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
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
	                decimal: ",",
	              },
	              initComplete: function () {
	                  var btns = $('.dt-button');
	                  btns.addClass('btn btn-primary btn-sm');
	                  btns.removeClass('dt-button');
	              }
	    });
	  });
    <%}%>
   </script>
</c:set>
<c:set var="pageModals">
  <t:modalSubAcesso>
    <jsp:attribute name="titulo"><hl:message key="rotulo.historico.liq.antecipada.acao"/></jsp:attribute>
  </t:modalSubAcesso>
  
 <%-- Modal: Termo de Consentimento de coleta de dados do servidor --%>
 <% if (!TextHelper.isNull(termoConsentimentoDadosServidor)) { %>
 <div class="modal fade" id="confirmarTermoUso" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
   <div class="modal-dialog modalTermoUso" role="document">
     <div class="modal-content">
       <div class="modal-header pb-0">
         <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="mensagem.termo.de.consentimento.coleta.dados.servidor.titulo"/></h5>
         <button type="button" class="logout mr-2" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
           <span aria-hidden="true">x</span>
         </button>
       </div>
       <div class="modal-body">
         <span id="textoTermoUso">
           <%=termoConsentimentoDadosServidor%>
         </span>
       </div>
       <div class="modal-footer pt-0">
         <div class="btn-action mt-2 mb-0">
           <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>' href="#" alt="<hl:message key="rotulo.botao.fechar"/>" title="<hl:message key="rotulo.botao.fechar"/>">
             <hl:message key="rotulo.botao.fechar" />
           </a>
         </div>
       </div>
     </div>
   </div>
 </div>
 <% } %>
 
 <%-- Modal: Exibe informações csa servidor --%>
<% if (request.getAttribute("exibeInformacaoCsaServidor") != null) { %>
 <div class="modal fade" id="exibeInformacaoCsaServidor" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
   <div class="modal-dialog modal-xl" role="document">
     <div class="modal-content">
       <div class="modal-header pb-0">
         <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="mensagem.informacao.csa.servidor.modal.titulo"/></h5>
         <button type="button" class="logout mr-2" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
           <span aria-hidden="true">x</span>
         </button>
       </div>
       <div class="modal-body">
         <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header hasIcon">
          <h2 class="card-header-title"><%=TextHelper.forHtmlContent(nomeServidorInformacaoCsa)%></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table id="dataTables"  class="table table-striped table-hover w-100">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.informacao.csa.servidor.lista.usuario"/></th>
                <th scope="col"><hl:message key="rotulo.informacao.csa.servidor.lista.data"/></th>
                <th scope="col"><hl:message key="rotulo.informacao.csa.servidor.lista.valor"/></th>
                <th scope="col"><hl:message key="rotulo.botao.opcoes"/></th>
              </tr>
            </thead>
            <tbody>
              <%
              String icsCodigo, icsUsuario, icsValor;
              Date icsData, teste;
              Iterator<?> it = informacoesServidor.iterator();
              while (it.hasNext()) {
                  CustomTransferObject informacaoServidor = (CustomTransferObject)it.next();
                  icsCodigo = (String)informacaoServidor.getAttribute(Columns.ICS_CODIGO);
                  icsUsuario = (String)informacaoServidor.getAttribute(Columns.USU_NOME);
                  icsData = (Date) informacaoServidor.getAttribute(Columns.ICS_DATA);
                  icsValor = (String)informacaoServidor.getAttribute(Columns.ICS_VALOR);
              %>
              <tr>
              <td style="text-align:left;"><%=TextHelper.forHtmlContent(icsUsuario)%></td>
              <td style="text-align:left;"><%=DateHelper.toDateTimeString(icsData)%></td>
              <td style="text-align:left;"><%=TextHelper.forHtmlContent(icsValor)%></td> 
              <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="MASTER" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes" />" aria-label="<hl:message key="rotulo.botao.opcoes" />"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterInformacaoCsaServidor?acao=editar&ICS_CODIGO=<%=TextHelper.forJavaScriptAttribute(icsCodigo)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(serCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterInformacaoCsaServidor?acao=excluir&ICS_CODIGO=<%=TextHelper.forJavaScriptAttribute(icsCodigo)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(serCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                      </div>
                    </div>
                  </div>
                </td>  
              </tr>
              <% } %>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
       </div>
       <div class="modal-footer pt-0">
       <div class="btn-action mt-2 mb-0">
            <a class="btn btn-primary" data-bs-dismiss="modal" href="#no-back" aria-label='<hl:message key="rotulo.botao.fechar"/>' onClick="postData('../v3/manterInformacaoCsaServidor?acao=editar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(serCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.informacao.csa.servidor.criar.novo"/></a>
         </div>
         <div class="btn-action mt-2 mb-0">
           <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>' href="#" alt="<hl:message key="rotulo.botao.fechar"/>" title="<hl:message key="rotulo.botao.fechar"/>">
             <hl:message key="rotulo.botao.fechar" />
           </a>
         </div>
       </div>
     </div>
   </div>
 </div>
 <% } %>
 
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>