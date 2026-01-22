<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.values.TpsExigeConfirmacaoRenegociacaoValoresEnum"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  boolean temCET = (Boolean) request.getAttribute("temCET");
  boolean permiteCadIndice = (Boolean) request.getAttribute("permiteCadIndice");
  boolean tpcConcluiNaoPagas = (Boolean) request.getAttribute("tpcConcluiNaoPagas");
  boolean tpcCsaEscolheConclusao = (Boolean) request.getAttribute("tpcCsaEscolheConclusao");
  boolean tpcReimplantacaoAutomatica = (Boolean) request.getAttribute("tpcReimplantacaoAutomatica");
  boolean tpcCsaEscolheReimpl = (Boolean) request.getAttribute("tpcCsaEscolheReimpl");
  boolean tpcPreservaPrdRejeitada = (Boolean) request.getAttribute("tpcPreservaPrdRejeitada");
  boolean tpcCsaEscolhePrdRejeitada = (Boolean) request.getAttribute("tpcCsaEscolhePrdRejeitada");
  boolean CSE_TPS_VLR_INTERVENIENCIA = (Boolean) request.getAttribute("CSE_TPS_VLR_INTERVENIENCIA");
  boolean CSE_TPS_CARENCIA_MINIMA = (Boolean) request.getAttribute("CSE_TPS_CARENCIA_MINIMA");
  boolean CSE_TPS_CARENCIA_MAXIMA = (Boolean) request.getAttribute("CSE_TPS_CARENCIA_MAXIMA");
  boolean temSimulacaoConsignacao = (Boolean) request.getAttribute("temSimulacaoConsignacao");
  boolean CSE_TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR = (Boolean) request.getAttribute("CSE_TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR");
  boolean permiteCsaLimitarMargem = (Boolean) request.getAttribute("permiteCsaLimitarMargem");
  boolean CSE_TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA = (Boolean) request.getAttribute("CSE_TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA");
  boolean CSE_TPS_IDADE_MIN_MAX = (Boolean) request.getAttribute("CSE_TPS_IDADE_MIN_MAX");
  boolean CSE_TPS_REIMPLANTACAO_AUTOMATICA = (Boolean) request.getAttribute("CSE_TPS_REIMPLANTACAO_AUTOMATICA");
  boolean cseReimplantacaoAut = (Boolean) request.getAttribute("cseReimplantacaoAut");
  boolean reimplanta = (boolean) request.getAttribute("reimplanta");
  boolean CSE_TPS_PRESERVA_PRD_REJEITADA_REIMPL = (boolean) request.getAttribute("CSE_TPS_PRESERVA_PRD_REJEITADA_REIMPL");
  boolean csePreservaPrdRejeitada = (boolean) request.getAttribute("csePreservaPrdRejeitada");
  boolean preserva = (boolean) request.getAttribute("preserva");
  boolean CSE_TPS_CONCLUI_ADE_NAO_PAGA = (boolean) request.getAttribute("CSE_TPS_CONCLUI_ADE_NAO_PAGA");
  boolean cseConcluiNaoPagas = (boolean) request.getAttribute("cseConcluiNaoPagas"); 
  boolean concluiNaoPagas = (boolean) request.getAttribute("concluiNaoPagas");
  boolean concluiAdeSerExcluido = (Boolean) request.getAttribute("concluiAdeSerExcluido");
  boolean podeRepetirIndice = (boolean) request.getAttribute("podeRepetirIndice");
  boolean CSE_TPS_PERMITE_REPETIR_INDICE_CSA = (boolean) request.getAttribute("CSE_TPS_PERMITE_REPETIR_INDICE_CSA");
  boolean CSE_TPS_PERMITE_CONTRATO_SUPER_SER_CSA = (boolean) request.getAttribute("CSE_TPS_PERMITE_CONTRATO_SUPER_SER_CSA");
  boolean CSE_TPS_SER_SENHA_OBRIGATORIA_CSA =  (boolean) request.getAttribute("CSE_TPS_SER_SENHA_OBRIGATORIA_CSA");
  boolean serSenhaObrigatoriaCse =  (boolean) request.getAttribute("serSenhaObrigatoriaCse");
  boolean serSenhaObrigatoriaCsa =  (boolean) request.getAttribute("serSenhaObrigatoriaCsa");
  boolean CSE_TPS_SOMA_IOF_SIMULACAO_RESERVA =  (boolean) request.getAttribute("CSE_TPS_SOMA_IOF_SIMULACAO_RESERVA");
  boolean incluiIofCSE = (boolean) request.getAttribute("incluiIofCSE");
  boolean incluiIof = (boolean) request.getAttribute("incluiIof");
  boolean CSE_TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING = (boolean) request.getAttribute("CSE_TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING");
  boolean cseDeveContarLimiteRanking = (boolean) request.getAttribute("cseDeveContarLimiteRanking");
  boolean csaDeveContarLimiteRanking = (boolean) request.getAttribute("csaDeveContarLimiteRanking");
  boolean CSE_TPS_IDENTIFICADOR_ADE_OBRIGATORIO = (boolean) request.getAttribute("CSE_TPS_IDENTIFICADOR_ADE_OBRIGATORIO");
  boolean adeIdentificadorObrigatorioCse = (boolean) request.getAttribute("adeIdentificadorObrigatorioCse");
  boolean adeIdentificadorObrigatorioCsa = (boolean) request.getAttribute("adeIdentificadorObrigatorioCsa");
  boolean temModuloCompra = (boolean) request.getAttribute("temModuloCompra");
  boolean temEtapaAprovacaoSaldo = (boolean) request.getAttribute("temEtapaAprovacaoSaldo");
  boolean csaExigeServidorCorrentista = (boolean) request.getAttribute("csaExigeServidorCorrentista");
  boolean tpsExibeBoleto = (boolean) request.getAttribute("tpsExibeBoleto");
  boolean permiteValorNegativo = (boolean) request.getAttribute("permiteValorNegativo");
  boolean CSE_TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE = (boolean) request.getAttribute("CSE_TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE");
  boolean exigeAssinaturaDigital = (boolean) request.getAttribute("exigeAssinaturaDigital");
  boolean exibeCsaListagemSolicitacao = (boolean) request.getAttribute("exibeCsaListagemSolicitacao");
  boolean exibirComoValorForaMargem = (boolean) request.getAttribute("exibirComoValorForaMargem");
  boolean bloqueiaInclusaoLoteRseTipo = (boolean) request.getAttribute("bloqueiaInclusaoLoteRseTipo");
  boolean exigeSenhaServidorViaLote = (boolean) request.getAttribute("exigeSenhaServidorViaLote");
  boolean CSE_TPS_DIAS_DESBL_AUT_RESERVA = (boolean) request.getAttribute("CSE_TPS_DIAS_DESBL_AUT_RESERVA");
  boolean CSE_TPS_VLR_MINIMO_CONTRATO = (Boolean) request.getAttribute("CSE_TPS_VLR_MINIMO_CONTRATO");
  boolean CSE_TPS_VLR_MAXIMO_CONTRATO = (Boolean) request.getAttribute("CSE_TPS_VLR_MAXIMO_CONTRATO");
  
  Map<String, Boolean> parametrosSvc = (Map<String, Boolean>) request.getAttribute("parametrosSvc");
  
  String bancoSaldoDevedor = (String) request.getAttribute("bancoSaldoDevedor");
  String csa_codigo = (String) request.getAttribute("csa_codigo");
  String csa_nome = (String) request.getAttribute("csa_nome");
  String cnv_codigo = (String) request.getAttribute("cnv_codigo");
  String svc_codigo = (String) request.getAttribute("svc_codigo");
  String svc_identificador = (String) request.getAttribute("svc_identificador");
  String svc_descricao = (String) request.getAttribute("svc_descricao");
  String nse_codigo = (String) request.getAttribute("nse_codigo");
  String carenciaMaxCse = (String) request.getAttribute("carenciaMaxCse");
  String valorIntervenienciaRefCSE = (String) request.getAttribute("valorIntervenienciaRefCSE");
  String valorInterveniencia = (String) request.getAttribute("valorInterveniencia");
  String valorIntervenienciaCSE = (String) request.getAttribute("valorIntervenienciaCSE");
  String valorIntervenienciaRef = (String) request.getAttribute("valorIntervenienciaRef");
  String diasDesblAutReservaCSE = (String) request.getAttribute("diasDesblAutReservaCSE");
  String diasDesblAutReserva = (String) request.getAttribute("diasDesblAutReserva");
  String carenciaMinimaCSE = (String) request.getAttribute("carenciaMinimaCSE");
  String carenciaMinima = (String) request.getAttribute("carenciaMinima");
  String carenciaMaximaCSE = (String) request.getAttribute("carenciaMaximaCSE");
  String carenciaMaxima = (String) request.getAttribute("carenciaMaxima");
  String vlrLiberadoMinimo = (String) request.getAttribute("vlrLiberadoMinimo");
  String vlrLiberadoMaximo = (String) request.getAttribute("vlrLiberadoMaximo");
  String percMargemSimuladorCSE = (String) request.getAttribute("percMargemSimuladorCSE");
  String percMargemSimulador = (String) request.getAttribute("percMargemSimulador");
  String percMargemFolhaLimiteCsaCSE = (String) request.getAttribute("percMargemFolhaLimiteCsaCSE");
  String percMargemFolhaLimiteCsa = (String) request.getAttribute("percMargemFolhaLimiteCsa");
  String idadeMinimaCSE = (String) request.getAttribute("idadeMinimaCSE");
  String idadeMaximaCSE = (String) request.getAttribute("idadeMaximaCSE");
  String idadeMinima = (String) request.getAttribute("idadeMinima");
  String idadeMaxima = (String) request.getAttribute("idadeMaxima");
  String indice = (String) request.getAttribute("indice");
  String tpsPermiteRepetirIndiceCse = (String) request.getAttribute("tpsPermiteRepetirIndiceCse");
  String tpsPermiteRepetirIndiceCsa = (String) request.getAttribute("tpsPermiteRepetirIndiceCsa");
  String csePermitePrazoMaior = (String) request.getAttribute("csePermitePrazoMaior");
  String csaPermitePrazoMaior = (String) request.getAttribute("csaPermitePrazoMaior");
  String cnvDefere = (String) request.getAttribute("cnvDefere");
  String agenciaSaldoDevedor = (String) request.getAttribute("agenciaSaldoDevedor");
  String contaSaldoDevedor = (String) request.getAttribute("contaSaldoDevedor");
  String nomeFavorecidoSaldoDevedor = (String) request.getAttribute("nomeFavorecidoSaldoDevedor");
  String cnpjSaldoDevedor = (String) request.getAttribute("cnpjSaldoDevedor");
  String destinatariosEmailsCompra = (String) request.getAttribute("destinatariosEmailsCompra");
  String emailInfContratosComprados = (String) request.getAttribute("emailInfContratosComprados");
  String emailInfSaldoDevedor = (String) request.getAttribute("emailInfSaldoDevedor");
  String emailInfAprSaldoDevedor = (String) request.getAttribute("emailInfAprSaldoDevedor");
  String emailInfPgtSaldoDevedor = (String) request.getAttribute("emailInfPgtSaldoDevedor");
  String emailInfLiqContratoComprado = (String) request.getAttribute("emailInfLiqContratoComprado");
  String emailInfSolicitacaoSaldoDevedor = (String) request.getAttribute("emailInfSolicitacaoSaldoDevedor");
  String emailInfNovoLeilao = (String) request.getAttribute("emailInfNovoLeilao");
  String emailInfAlterCodVerbaConvenioCsa = (String) request.getAttribute("emailInfAlterCodVerbaConvenioCsa");
  String emailNotifCsaSerFezCancelouSolicitacao = (String) request.getAttribute("emailNotifCsaSerFezCancelouSolicitacao");
  String limite_aumento_valor_ade = (String) request.getAttribute("limite_aumento_valor_ade");
  String limite_aumento_valor_ade_ref = (String) request.getAttribute("limite_aumento_valor_ade_ref");
  String maxPrazoRenegociacaoPeriodo = (String) request.getAttribute("maxPrazoRenegociacaoPeriodo");
  String minPrazoRenegociacaoPeriodo = (String) request.getAttribute("minPrazoRenegociacaoPeriodo");
  String dataExpiracaoCnv = (String) request.getAttribute("dataExpiracaoCnv");
  String numeroContratoCnv = (String) request.getAttribute("numeroContratoCnv");
  String periodoLimiteAdeDuplicidade = (String) request.getAttribute("periodoLimiteAdeDuplicidade");
  String periodoLimiteAdeDuplicidadeCSE = (String) request.getAttribute("periodoLimiteAdeDuplicidadeCSE");
  String mascaraNumeroContratoBeneficio = (String) request.getAttribute("mascaraNumeroContratoBeneficio");
  String mensagemSerSolDeferida = (String) request.getAttribute("mensagemSerSolDeferida");
  String voltar = (String) request.getAttribute("voltar");
  String exigeConfirmacaoRenegociacao = (String) request.getAttribute("exigeConfirmacaoRenegociacao");
  String msgExibirSolicitacaoServidorOfertaOutroSvc = (String) request.getAttribute("msgExibirSolicitacaoServidorOfertaOutroSvc");
  String relevanciaCsaRanking = (String) request.getAttribute("relevanciaCsaRanking");
  String configurarIdPropostaConvenio = (String) request.getAttribute("configurarIdPropostaConvenio");
  String configurarDiaVencimentoContrato = (String) request.getAttribute("configurarDiaVencimentoContrato");
  String configurarPeriodoCompetenciaDebito = (String) request.getAttribute("configurarPeriodoCompetenciaDebito");
  String configurarNumeroConvenio = (String) request.getAttribute("configurarNumeroConvenio");
  String configurarCodigoAdesao = (String) request.getAttribute("configurarCodigoAdesao");
  String configurarIdadeMaximaContratacaoSeguro = (String) request.getAttribute("configurarIdadeMaximaContratacaoSeguro");
  String formaNumeracaoParcelas = (String) request.getAttribute("formaNumeracaoParcelas");
  String formaNumeracaoParcelasPadrao = (String) request.getAttribute("formaNumeracaoParcelasPadrao");
  String obrigaInformacoesServidorSolicitacao = (String) request.getAttribute("obrigaInformacoesServidorSolicitacao");
  String obrigaInfoBancariasReserva = (String) request.getAttribute("obrigaInfoBancariasReserva");
  String valorFixoPostoCsaSvc = (String) request.getAttribute("valorFixoPostoCsaSvc");
  String permiteCadastroSaldoDevedor = (String) request.getAttribute("permiteCadastroSaldoDevedor");
  String vlrMinimoContrato = (String) request.getAttribute("vlrMinimoContrato");
  String vlrMinimoContratoCSE = (String) request.getAttribute("vlrMinimoContratoCSE");
  String vlrMaximoContrato = (String) request.getAttribute("vlrMaximoContrato");
  String vlrMaximoContratoCSE = (String) request.getAttribute("vlrMaximoContratoCSE");
  boolean permiteInclusaoSerBloqSemSenha =  (boolean) request.getAttribute("permiteInclusaoSerBloqSemSenha");
  boolean permiteCancelarRenegociacaoDeixandoMargemNegativa =  (boolean) request.getAttribute("permiteCancelarRenegociacaoDeixandoMargemNegativa");
  boolean prazoLimitadoDataAdmissaoRse = (boolean) request.getAttribute("prazoLimitadoDataAdmissaoRse");
%>

<c:set var="javascript">
<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
<script type="text/JavaScript" src="../js/validaform.js"></script>
<script type="text/JavaScript" src="../js/validacoes.js"></script>
<script type="text/JavaScript" src="../js/xbdhtml.js"></script>
<script type="text/JavaScript" src="../js/validaemail.js"></script>
<link href="<c:url value='/css/custom_v4.css'/>" rel="stylesheet" type="text/css">
<script type="text/JavaScript">
var f0 = document.forms['form1'];
    <% if (parametrosSvc.containsKey(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR) &&((Boolean)parametrosSvc.get(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR)).booleanValue()) { %>
    var arrayBancos = <%=(String)JspHelper.geraArrayBancos(responsavel)%>;
    <% } %>
    
    function formLoad() {
      <% if (parametrosSvc.containsKey(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR) && ((Boolean)parametrosSvc.get(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR)).booleanValue()) { %>
        // Filta o combo de bancos com o valor pré-selecionado
        AtualizaFiltraComboExt(document.forms[0].tps_<%=(String)CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR%>, arrayBancos, '', '', '<%=TextHelper.forJavaScriptBlock(bancoSaldoDevedor)%>', false, false, '', '');
      <% } %>
      // Focaliza o primeiro campo de edição
      focusFirstField();
    }
    
    function validaParametros() {
      return (verificaCarencia() && verificaEmailCompraContratos() && verificaIndice() && verificaVlrContrato());
    }
    
    function verificaCarencia() {
      var arCarenciaMaxCse = '<%=TextHelper.forHtmlContent(carenciaMaxCse)%>';
      var carenciaMinCse = '<%=TextHelper.forHtmlContent(carenciaMinimaCSE)%>';
      
      if (f0.tps_<%=(String)CodedValues.TPS_CARENCIA_MINIMA%> != null && f0.tps_<%=(String)CodedValues.TPS_CARENCIA_MAXIMA%> != null) {
           if (arCarenciaMaxCse != '' && (parseInt(f0.tps_<%=(String)CodedValues.TPS_CARENCIA_MAXIMA%>.value) > arCarenciaMaxCse)) {
             alert("<hl:message key="mensagem.erro.servico.csa.carencia.maxima"/>");
             f0.tps_<%=(String)CodedValues.TPS_CARENCIA_MAXIMA%>.focus();
             return false;
           }
           if (carenciaMinCse != '' && (parseInt(f0.tps_<%=(String)CodedValues.TPS_CARENCIA_MINIMA%>.value) < carenciaMinCse)) {
             alert("<hl:message key="mensagem.erro.servico.csa.carencia.minima"/>");
             f0.tps_<%=(String)CodedValues.TPS_CARENCIA_MINIMA%>.focus();
             return false;
           }
           if (parseInt(f0.tps_<%=(String)CodedValues.TPS_CARENCIA_MINIMA%>.value) > parseInt(f0.tps_<%=(String)CodedValues.TPS_CARENCIA_MAXIMA%>.value)) {
             alert("<hl:message key="mensagem.erro.servico.csa.carencia.maxima.minima"/>");
             f0.tps_<%=(String)CodedValues.TPS_CARENCIA_MAXIMA%>.focus();
             return false;
           }
      }
    
      if (f0.tps_<%=(String)CodedValues.TPS_VLR_LIBERADO_MINIMO%> != null && f0.tps_<%=(String)CodedValues.TPS_VLR_LIBERADO_MAXIMO%> != null) {
         if (parseFloat(f0.tps_<%=(String)CodedValues.TPS_VLR_LIBERADO_MINIMO%>.value) > parseFloat(f0.tps_<%=(String)CodedValues.TPS_VLR_LIBERADO_MAXIMO%>.value)) {
            alert("<hl:message key="mensagem.erro.servico.csa.valor.liberado.maximo"/>");
            f0.tps_<%=(String)CodedValues.TPS_VLR_LIBERADO_MAXIMO%>.focus();
            return false;
         }
      }
    
      if (f0.tps_<%=(String)CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO%> != null && f0.tps_<%=(String)CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO%>_ref != null) {
         if (parseFloat(f0.tps_<%=(String)CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO%>.value) >
             parseFloat(f0.tps_<%=(String)CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO%>_ref.value)) {
            alert("<hl:message key="mensagem.erro.servico.csa.idade.maxima"/>");
            f0.tps_<%=(String)CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO%>.focus();
            return false;
         }
      }
      return true;
    }
	
	function verificaVlrContrato() {
	  if (f0.tps_<%=(String)CodedValues.TPS_VLR_MINIMO_CONTRATO%> != null && f0.tps_<%=(String)CodedValues.TPS_VLR_MAXIMO_CONTRATO%> != null) {
	    if (parseFloat(f0.tps_<%=(String)CodedValues.TPS_VLR_MINIMO_CONTRATO%>.value) > parseFloat(f0.tps_<%=(String)CodedValues.TPS_VLR_MAXIMO_CONTRATO%>.value)) {
	      alert("<hl:message key="mensagem.erro.servico.csa.vlrContrato.maximo.minimo"/>");
	      f0.tps_<%=(String)CodedValues.TPS_VLR_MAXIMO_CONTRATO%>.focus();
	      return false;
	    }
	  }
	  return true;
	}
    
    function verificaParametrosReimplante() {
      if (f0.tps_<%=(String)CodedValues.TPS_REIMPLANTACAO_AUTOMATICA%> != null) {
        for (i = 0; i < f0.tps_<%=(String)CodedValues.TPS_REIMPLANTACAO_AUTOMATICA%>.length; i++) {
          if (f0.tps_<%=(String)CodedValues.TPS_REIMPLANTACAO_AUTOMATICA%>[i].value == 'S' && f0.tps_<%=(String)CodedValues.TPS_REIMPLANTACAO_AUTOMATICA%>[i].checked) {
            if (f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%> != null && !f0.check_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>.checked) {
              for (j = 0; j < f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>.length; j++) {
                f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>[j].disabled = false;
              }
            }
            if (f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%> != null) {
              for (j = 0; j < f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>.length; j++) {
                if (f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>[j].value == 'N') {
                  f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>[j].checked = true;
                }
                f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>[j].disabled = true;
                f0.check_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>.disabled = true;
                f0.check_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>.checked = true;
              }
            }
          } else if (f0.tps_<%=(String)CodedValues.TPS_REIMPLANTACAO_AUTOMATICA%>[i].value == 'N' && f0.tps_<%=(String)CodedValues.TPS_REIMPLANTACAO_AUTOMATICA%>[i].checked) {
            if (f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%> != null) {
              for (j = 0; j < f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>.length; j++) {
                if (f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>[j].value == 'N') {
                  f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>[j].checked = true;
                }
                f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>[j].disabled = true;
    	        f0.check_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>.disabled = true;
              }
            }
            if (f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%> != null) {
              for (j = 0; j < f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>.length; j++) {
                if (f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>[j].value == 'S') {
                  <% if(tpcConcluiNaoPagas && !tpcCsaEscolheConclusao) { %>
                    f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>[j].checked = true;
                  <% } %>
                }
                f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>[j].disabled = false;
                f0.check_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>.disabled = false;
              }
            }
          }
        }
      }
    
    <% if (!tpcReimplantacaoAutomatica || !tpcCsaEscolheReimpl) { %>
      for (j = 0; j < f0.tps_<%=(String)CodedValues.TPS_REIMPLANTACAO_AUTOMATICA%>.length; j++) {
        f0.tps_<%=(String)CodedValues.TPS_REIMPLANTACAO_AUTOMATICA%>[j].disabled = true;
        f0.check_<%=(String)CodedValues.TPS_REIMPLANTACAO_AUTOMATICA%>.disabled = true;
        f0.check_<%=(String)CodedValues.TPS_REIMPLANTACAO_AUTOMATICA%>.checked = true;
      }
    <% } %>
    <% if (!tpcPreservaPrdRejeitada || !tpcCsaEscolhePrdRejeitada) { %>
      if (f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%> != null) {
        for (j = 0; j < f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>.length; j++) {
          f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>[j].disabled = true;
          f0.check_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>.disabled = true;
        }
      }
    <% } %>
    <% if (!tpcConcluiNaoPagas || !tpcCsaEscolheConclusao || (tpcReimplantacaoAutomatica && !tpcCsaEscolheReimpl)) { %>
      if (f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%> != null) {
        for (j = 0; j < f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>.length; j++) {
          f0.tps_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>[j].disabled = true;
          f0.check_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>.disabled = true;
          f0.check_<%=(String)CodedValues.TPS_CONCLUI_ADE_NAO_PAGA%>.checked = true;
        }
      }
    <% } %>
    }
    
    function verificaParametrosPreservacao() {
      if (f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%> != null && f0.tps_<%=(String)CodedValues.TPS_FORMA_NUMERACAO_PARCELAS%> != null) {
        for (i = 0; i < f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>.length; i++) {
          if (f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>[i].value == 'S' && f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>[i].checked) {
            for (j = 0; j < f0.tps_<%=(String)CodedValues.TPS_FORMA_NUMERACAO_PARCELAS%>.length; j++) {
              f0.tps_<%=(String)CodedValues.TPS_FORMA_NUMERACAO_PARCELAS%>[j].disabled = false;
            }
          } else if (f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>[i].value == 'N' && f0.tps_<%=(String)CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL%>[i].checked) {
            for (j = 0; j < f0.tps_<%=(String)CodedValues.TPS_FORMA_NUMERACAO_PARCELAS%>.length; j++) {
              f0.tps_<%=(String)CodedValues.TPS_FORMA_NUMERACAO_PARCELAS%>[j].disabled = true;
            }
          }
        }
      }
    }
    
    function verificaEmailCompraContratos() {
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS%> != null) {
        if ((f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS%>.value != '') && (!isEmailValid(f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS%>.value))) {
          alert("<hl:message key="mensagem.erro.email.notificacao.contratos.comprados.invalido"/>");
          f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS%>.focus();
          return false;
        }
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR%> != null) {
        if ((f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR%>.value != '') && (!isEmailValid(f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR%>.value))) {
          alert("<hl:message key="mensagem.erro.email.notificacao.cadastro.saldo.devedor.comprado.invalido"/>");
          f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR%>.focus();
          return false;
        }
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR%> != null) {
        if ((f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR%>.value != '') && (!isEmailValid(f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR%>.value))) {
          alert("<hl:message key="mensagem.erro.email.notificacao.aprovacao.rejeicao.saldo.devedor.compra.invalido"/>");
          f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR%>.focus();
          return false;
        }
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR%> != null) {
        if ((f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR%>.value != '') && (!isEmailValid(f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR%>.value))) {
          alert("<hl:message key="mensagem.erro.email.notificacao.saldo.devedor.invalido"/>");
          f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR%>.focus();
          return false;
        }
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO%> != null) {
        if ((f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO%>.value != '') && (!isEmailValid(f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO%>.value))) {
          alert("<hl:message key="mensagem.erro.email.notificacao.liquidacao.contrato.comprado.invalido"/>");
          f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO%>.focus();
          return false;
        }
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR%> != null) {
        if ((f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR%>.value != '') && (!isEmailValid(f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR%>.value))) {
          alert("<hl:message key="mensagem.erro.email.solicitacao.saldo.devedor.invalido"/>");
          f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR%>.focus();
          return false;
        }
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO%> != null) {
        if ((f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO%>.value != '') && (!isEmailValid(f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO%>.value))) {
    	      alert("<hl:message key="mensagem.erro.email.notificacao.leilao.invalido"/>");
    	      f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO%>.focus();
    	      return false;
    	}
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR%> != null) {
        if ((f0.tps_<%=(String)CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR%>.value != '') && (!isEmailValid(f0.tps_<%=(String)CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR%>.value))) {
    	      alert("<hl:message key="mensagem.erro.email.notificacao.leilao.invalido"/>");
    	      f0.tps_<%=(String)CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR%>.focus();
    	      return false;
    	}
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA%> != null) {
    	    if ((f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA%>.value != '') && (!isEmailValid(f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA%>.value))) {
    		      alert("<hl:message key="mensagem.erro.email.notificacao.alter.codVerba.convenio.csa.invalido"/>");
    		      f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA%>.focus();
    		      return false;
    		}
    	  }
      return true;
    }
    
    function verificaIndice() {
      var e=document.forms[0].tps_<%=(String)CodedValues.TPS_INDICE%>;
      if (e != null && !ValidaMascara(e)) {
        alert("<hl:message key="mensagem.erro.verifique.campos"/>");
        e.focus();
        return false;
      }
      return true;
    }
    
    function setHidden(campo, valor) {
      campo.value = valor;
    }
    
    function habilitaRadios() {
      var f0 = document.forms[0];
      for (i=0; i < f0.elements.length; i++) {
        var e = f0.elements[i];
        if ((e.type == 'radio') && (e.type == 'text')) {
            e.disabled = false;
        }
      }
    }
    
    function validaRadiosPadrao(checkbox, cse, nome) {
     var f0 = document.forms[0];
     for (i=0; i < f0.elements.length; i++) {
       var e = f0.elements[i];
       if (e.name == nome){
           if ((!checkbox.checked) && (e.type == 'radio')) {
            e.disabled = false;
            } else {
              e.disabled = true;
              if(((cse == "S") || (cse == 'true')) && (e.value == "S")){
                e.checked = true;
              }
              if(((cse == "N") || (cse == 'false')) && (e.value == "N")){
                e.checked = true;
              }
             }
       }
     }   
    }
    
    function validaTextPadrao(checkbox, cse, nome, nome_ref) {
     var f0 = document.forms[0];
     for (i=0; i < f0.elements.length; i++) {
       var e = f0.elements[i];
       if (e.name == nome){
          if (!checkbox.checked) {
             e.disabled = false;
          } else {
              e.disabled = true;
              if (e.type == 'text') {
                e.value = cse;
              } 
            }
       }
       if (e.name == nome_ref) {
         if ((checkbox.checked) && (<%=(boolean)valorIntervenienciaRefCSE.equals("2")%>)) {
            e.options[1].selected = true;
            e.disabled = true;   
         }else if ((checkbox.checked) && (<%=(boolean)valorIntervenienciaRefCSE.equals("1")%>)) {
                 e.options[0].selected = true;
                 e.disabled = true;
           } else if ((checkbox.checked) &&(<%=(boolean)valorIntervenienciaRefCSE.equals("")%>)) {
                e.disabled = true;
             } else {
                e.disabled = false;
             } 
        }
     }        
    }        
    
    function habilitarCamposEmails(habilitar) {
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS%> != null) {
        f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS%>.disabled = !habilitar;
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR%> != null) {
        f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR%>.disabled = !habilitar;
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR%> != null) {
        f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR%>.disabled = !habilitar;
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR%> != null) {
        f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR%>.disabled = !habilitar;
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO%> != null) {
        f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO%>.disabled = !habilitar;
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR%> != null) {
        f0.tps_<%=(String)CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR%>.disabled = !habilitar;
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO%> != null) {
    	f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO%>.disabled = !habilitar;
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA%> != null) {
    	f0.tps_<%=(String)CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA%>.disabled = !habilitar;
      }
      if (f0.tps_<%=(String)CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR%> != null) {
    	f0.tps_<%=(String)CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR%>.disabled = !habilitar;
      }
    }
    window.onload = formLoad;
    verificaParametrosReimplante();
</script>
</c:set>
<c:set var="title">
<hl:message key="rotulo.manutencao.servico.csa"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(svc_identificador)%> - <%=TextHelper.forHtmlContent(svc_descricao)%></h2>
    </div>
    <div class="card-body">
      <form method="post" action="../v3/manterConsignataria?acao=salvarServico&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
       <% if (parametrosSvc.containsKey(CodedValues.TPS_VLR_INTERVENIENCIA) && ((Boolean) parametrosSvc.get(CodedValues.TPS_VLR_INTERVENIENCIA)).booleanValue()) {
               valorInterveniencia = (!valorInterveniencia.equals("") ? NumberHelper.reformat(valorInterveniencia, "en", NumberHelper.getLang()) : "");
               valorIntervenienciaCSE = (!valorIntervenienciaCSE.equals("") ? NumberHelper.reformat(valorIntervenienciaCSE, "en", NumberHelper.getLang()) : "");
        %>
      <div class="row">
        <% if (!CSE_TPS_VLR_INTERVENIENCIA) { %>
          <div class="form-check-inline form-group col-sm-12 col-md-4 mt-1">
            <label for="valorInterveniencia"><hl:message key="rotulo.servico.csa.valor.interveniencia"/></label>
            <INPUT NAME="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" VALUE="<%=TextHelper.forHtmlAttribute(valorIntervenienciaCSE)%>" SIZE="10" disabled >
          </div>
          <div class=" form-group col-sm-2 col-md-3">
            <label for="tipoDeValor"><hl:message key="rotulo.servico.csa.tipo.valor"/></label>
              <SELECT CLASS="form-control form-select col-sm-12 m-1" NAME="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF" id="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" DISABLED>
                <OPTION VALUE="1" <%=(String)(valorIntervenienciaRefCSE.equals("1")?"SELECTED":"")%>><hl:message key="rotulo.servico.valor"/></OPTION>
                <OPTION VALUE="2" <%=(String)(valorIntervenienciaRefCSE.equals("2")?"SELECTED":"")%>><hl:message key="rotulo.servico.valor.percentual"/></OPTION>
              </SELECT>
          </div>
          <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" value="1" onChange="validaTextPadrao(this, '<%=TextHelper.forJavaScript(valorIntervenienciaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_INTERVENIENCIA)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF');" checked>
              <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
          </div>
            <% } else { %>
              <div class="form-group col-sm-12 col-md-4 mt-1">
                <label for="valorInterveniencia"><hl:message key="rotulo.servico.csa.valor.interveniencia"/></label>
                <INPUT NAME="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" VALUE="<%=TextHelper.forHtmlAttribute(valorInterveniencia)%>" SIZE="10" >
              </div>
           <div class="form-group col-sm-2 col-md-3">
              <label for="tipoDeValor"><hl:message key="rotulo.servico.csa.tipo.valor"/></label>
              <SELECT CLASS="form-control form-select col-sm-12 m-1" NAME="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF" id="tps_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <OPTION VALUE="1" <%=(String)(valorIntervenienciaRef.equals("1")?"SELECTED":"")%>><hl:message key="rotulo.servico.valor"/></OPTION>
                <OPTION VALUE="2" <%=(String)(valorIntervenienciaRef.equals("2")?"SELECTED":"")%>><hl:message key="rotulo.servico.valor.percentual"/></OPTION>
              </SELECT>
          </div>
          <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
            <INPUT NAME="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>" value="1" onChange="validaTextPadrao(this, '<%=TextHelper.forJavaScript(valorIntervenienciaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_INTERVENIENCIA)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_INTERVENIENCIA)%>_REF');">
            <label for="check_<%=(String)(CodedValues.TPS_VLR_INTERVENIENCIA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
          </div>
           <% } %>
        </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CARENCIA_MINIMA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_CARENCIA_MINIMA)).booleanValue()) { %>
        <div class="row">
           <% if (!CSE_TPS_CARENCIA_MINIMA) { %>
             <div class="form-group col-sm-12 col-md-4 mt-1">
               <label for="carenciaMinima"><hl:message key="rotulo.param.svc.carencia.minima"/></label>
               <input name="tps_<%=(String)(CodedValues.TPS_CARENCIA_MINIMA)%>" type="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_CARENCIA_MINIMA)%>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" value="<%=TextHelper.forHtmlAttribute(carenciaMinimaCSE)%>" size="10" disabled>
             </div>
             <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
               <INPUT NAME="check_<%=(String)(CodedValues.TPS_CARENCIA_MINIMA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_CARENCIA_MINIMA)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(carenciaMinimaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_CARENCIA_MINIMA)%>');" checked>
               <label for="check_<%=(String)(CodedValues.TPS_CARENCIA_MINIMA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
             </div>
              <% } else { %>
             <div class="form-group col-sm-12 col-md-4 mt-1">
               <label for="carenciaMinima"><hl:message key="rotulo.param.svc.carencia.minima"/></label>
               <input name="tps_<%=(String)(CodedValues.TPS_CARENCIA_MINIMA)%>" type="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_CARENCIA_MINIMA)%>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" value="<%=TextHelper.forHtmlAttribute(carenciaMinima)%>" size="10">
             </div>
             <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
               <INPUT NAME="check_<%=(String)(CodedValues.TPS_CARENCIA_MINIMA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_CARENCIA_MINIMA)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(carenciaMinimaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_CARENCIA_MINIMA)%>');" >
               <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_CARENCIA_MINIMA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
             </div>
          <% } %>
       </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CARENCIA_MAXIMA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_CARENCIA_MAXIMA)).booleanValue()) { %>
       <div class="row">
        <% if (!CSE_TPS_CARENCIA_MAXIMA) { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="carenciaMaxima"><hl:message key="rotulo.param.svc.carencia.maxima"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CARENCIA_MAXIMA)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_CARENCIA_MAXIMA)%>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TextHelper.forHtmlAttribute(carenciaMaximaCSE)%>" SIZE="10" disabled>
            </div>
            <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_CARENCIA_MAXIMA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_CARENCIA_MAXIMA)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(carenciaMaximaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_CARENCIA_MAXIMA)%>');" checked>
              <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_CARENCIA_MAXIMA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
            </div>
          <% } else { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="carenciaMaxima"><hl:message key="rotulo.param.svc.carencia.maxima"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CARENCIA_MAXIMA)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_CARENCIA_MAXIMA)%>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TextHelper.forHtmlAttribute(carenciaMaxima)%>" SIZE="10">
            </div>
            <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_CARENCIA_MAXIMA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_CARENCIA_MAXIMA)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(carenciaMaximaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_CARENCIA_MAXIMA)%>');" >
              <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_CARENCIA_MAXIMA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
            </div>
          <% } %>
      </div>
      <% } %>
      <% if (temSimulacaoConsignacao && parametrosSvc.containsKey(CodedValues.TPS_VLR_LIBERADO_MINIMO) && ((Boolean) parametrosSvc.get(CodedValues.TPS_VLR_LIBERADO_MINIMO)).booleanValue()) {
          List<?> prazos = (List<?>) request.getAttribute("prazos");
          if (prazos.size() > 0) {
      %>
        <div class="row">
          <div class="form-group col-sm-12 col-md-4 mt-1">
            <label for="valorLiberadoMinimo"><hl:message key="rotulo.param.svc.valor.liberado.minimo"/></label>
            <INPUT NAME="tps_<%=(String)(CodedValues.TPS_VLR_LIBERADO_MINIMO)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_VLR_LIBERADO_MINIMO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" VALUE="<%=TextHelper.forHtmlAttribute(vlrLiberadoMinimo)%>" SIZE="10">
          </div>
          <div class="form-group col-sm-12 col-md-4 mt-1">
            <label for="valorLiberadoMaximo"><hl:message key="rotulo.param.svc.valor.liberado.maximo"/></label>
            <INPUT NAME="tps_<%=(String)(CodedValues.TPS_VLR_LIBERADO_MAXIMO)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_VLR_LIBERADO_MAXIMO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" VALUE="<%=TextHelper.forHtmlAttribute(vlrLiberadoMaximo)%>" SIZE="10">
          </div>
        </div>
      <%
          }
      }
      %>
      <% if (temSimulacaoConsignacao && parametrosSvc.containsKey(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR) && ((Boolean) parametrosSvc.get(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)).booleanValue()) { %>
        <div class="row">
        <% if (!CSE_TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR) { %>
          <div class="form-group col-sm-12 col-md-4 mt-1">
            <label for="percentualMargem"><hl:message key="rotulo.param.svc.percentual.margem.simulador"/></label>
            <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" VALUE="<%=TextHelper.forHtmlAttribute(percMargemSimuladorCSE)%>" SIZE="10" disabled>
          </div>
          <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
            <INPUT NAME="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>" value="1" onChange="validaTextPadrao(this, '<%=TextHelper.forJavaScript(percMargemSimuladorCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>');" checked>
            <label for="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
          </div>
            <% } else { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
             <label for="percentualMargem"><hl:message key="rotulo.param.svc.percentual.margem.simulador"/></label>
             <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" VALUE="<%=TextHelper.forHtmlAttribute(percMargemSimulador)%>" SIZE="10">
            </div>
            <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>" value="1" onChange="validaTextPadrao(this, '<%=TextHelper.forJavaScript(percMargemSimuladorCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>');" >
              <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
            </div>
        <% } %>
        </div>
      <% } %>
      <% if (permiteCsaLimitarMargem && parametrosSvc.containsKey(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA) && ((Boolean) parametrosSvc.get(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)).booleanValue()) { %>
        <div class="row">
          <% if (!CSE_TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA) { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="percentualMargemFolha"><hl:message key="rotulo.param.svc.percentual.margem.folha.limite.csa"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" VALUE="<%=TextHelper.forHtmlAttribute(percMargemFolhaLimiteCsaCSE)%>" SIZE="10" disabled>
            </div>
            <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>" value="1" onChange="validaTextPadrao(this, '<%=TextHelper.forJavaScript(percMargemFolhaLimiteCsaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>');" checked>
              <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
            </div>
          <% } else { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="percentualMargemFolha"><hl:message key="rotulo.param.svc.percentual.margem.folha.limite.csa"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" VALUE="<%=TextHelper.forHtmlAttribute(percMargemFolhaLimiteCsa)%>" SIZE="10">
            </div>
            <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>" value="1" onChange="validaTextPadrao(this, '<%=TextHelper.forJavaScript(percMargemFolhaLimiteCsaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>');" >
              <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
            </div>
          <% } %>
        </div>
      <% } %>
      <% if (temSimulacaoConsignacao && parametrosSvc.containsKey(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO) && ((Boolean) parametrosSvc.get(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)).booleanValue()) {%>
        <div class="row">
          <div class="form-group col-sm-12 col-md-12 mt-1 mb-0" aria-labelledby="idadeNecessaria">
             <span id="idadeNecessaria"><hl:message key="rotulo.param.svc.idade.necessaria.solicitar.contratos"/></span>
          </div>
          <% if (!CSE_TPS_IDADE_MIN_MAX) { %>
            <div class="form-group col-sm-2 col-md-3">
              <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>"><hl:message key="rotulo.param.svc.idade.necessaria.solicitar.contratos.minima"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>" id="tps_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>" class="form-control" TYPE="text" VALUE="<%=TextHelper.forHtmlAttribute(idadeMinimaCSE)%>" MAXLENGTH="3" SIZE="10" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" disabled/>
            </div>
            <div class="form-group col-sm-2 col-md-3">
              <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>_ref"><hl:message key="rotulo.param.svc.idade.necessaria.solicitar.contratos.maxima"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>_ref" id="tps_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>_ref" class="form-control" TYPE="text" VALUE="<%=TextHelper.forHtmlAttribute(idadeMaximaCSE)%>" MAXLENGTH="3" SIZE="10" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" disabled/>
            </div>
            <div class="float-end align-middle usrprd mt-5 col-sm-4 col-md-3">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(idadeMinimaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>');validaTextPadrao(this, '<%=TextHelper.forJavaScript(idadeMaximaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>_ref');" checked>
              <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>">
              <hl:message key="rotulo.servico.usar.padrao"/></label>
            </div>
          <% } else { %>
            <div class="form-group col-sm-2 col-md-3">
              <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>"><hl:message key="rotulo.param.svc.idade.necessaria.solicitar.contratos.minima"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>" class="form-control" TYPE="text" VALUE="<%=TextHelper.forHtmlAttribute(idadeMinima)%>" MAXLENGTH="3" SIZE="10" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
            <div class="form-group col-sm-2 col-md-3">
              <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>_ref"><hl:message key="rotulo.param.svc.idade.necessaria.solicitar.contratos.maxima"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>_ref" class="form-control" TYPE="text" VALUE="<%=TextHelper.forHtmlAttribute(idadeMaxima)%>" MAXLENGTH="3" SIZE="10" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
            <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(idadeMinimaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>');validaTextPadrao(this, '<%=TextHelper.forJavaScript(idadeMaximaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>_ref');">
              <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
            </div>
          <% } %>
        </div>
      <%
        }
      %>
      <%
      if (permiteCadIndice) {
            // ******************** mascara do indice  ********************
            //  Define se o indice eh numero ou nao (true numero) se !existe =null
            boolean indiceNumerico = (Boolean) request.getAttribute("indiceNumerico");
            //  Limite numérico do indice
            int limiteIndice = (int) request.getAttribute("limiteIndice");
            
            String maskNum = "#D" + ( String.valueOf(limiteIndice).length()) ;
            String maskNaoNum = "#A" + ( String.valueOf(limiteIndice).length());
            String mascara = indiceNumerico ? maskNum  : maskNaoNum;
        if (parametrosSvc.containsKey(CodedValues.TPS_INDICE) && ((Boolean)parametrosSvc.get(CodedValues.TPS_INDICE)).booleanValue()) { %>
         <div class="row">
          <div class="form-group col-sm-12 col-md-4 mt-2">
            <label for="indiceSingular"><hl:message key="rotulo.indice.singular"/></label>
            <INPUT NAME="tps_<%=(String)(CodedValues.TPS_INDICE)%>" TYPE="text" class="form-control" VALUE="<%=TextHelper.forHtmlAttribute(indice)%>" MAXLENGTH="2" SIZE="10" onFocus="SetarEventoMascara(this,'<%=TextHelper.forJavaScript(mascara)%>',true);" onBlur="fout(this);ValidaMascara(this);"/>
          </div>
         </div>
        <% } %>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="reimplantacaoAutomatica">
                  <div><span id="reimplantacaoAutomatica"><hl:message key="rotulo.param.svc.reimplantacao.automatica"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <% if (!CSE_TPS_REIMPLANTACAO_AUTOMATICA) { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(cseReimplantacaoAut)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>');verificaParametrosReimplante();" checked>
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" onClick="verificaParametrosReimplante();" VALUE="S" <%=(String)(reimplanta ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" ><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" onClick="verificaParametrosReimplante();" VALUE="N" <%=(String)(reimplanta ? "" : "CHECKED")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>"><hl:message key="rotulo.nao"/></label>
                      <% } else { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(cseReimplantacaoAut)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>');verificaParametrosReimplante();">
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" onClick="verificaParametrosReimplante();" VALUE="S" <%=(String)(reimplanta ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" onClick="verificaParametrosReimplante();" VALUE="N" <%=(String)(reimplanta ? "" : "CHECKED")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA)%>"><hl:message key="rotulo.nao"/></label>
                      <% } %>
                  </div>
              </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL) && ((Boolean)parametrosSvc.get(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)).booleanValue()) { %>
        <div class="row">
          <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramPreservarParcelaNaoDescontada">
             <div><span id="paramPreservarParcelaNaoDescontada"><hl:message key="rotulo.param.svc.preservar.parcela.nao.descontada"/></span></div>
             <div class="form-check form-check-inline mt-1">
                  <% if (!CSE_TPS_PRESERVA_PRD_REJEITADA_REIMPL){ %>
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(csePreservaPrdRejeitada)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>');" checked>
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" onClick="verificaParametrosPreservacao();" VALUE="S" <%=(String)(preserva ? "CHECKED" : "")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" onClick="verificaParametrosPreservacao();" VALUE="N" <%=(String)(preserva ? "" : "CHECKED")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>"><hl:message key="rotulo.nao"/></label>
                  <% } else {%>
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(csePreservaPrdRejeitada)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>');">
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" onClick="verificaParametrosPreservacao();" VALUE="S" <%=(String)(preserva ? "CHECKED" : "")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" onClick="verificaParametrosPreservacao();" VALUE="N" <%=(String)(preserva ? "" : "CHECKED")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL)%>"><hl:message key="rotulo.nao"/></label>
                  <% } %>
          </div>
         </div>
        </div>
      <% } %>

      <% if (parametrosSvc.containsKey(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)).booleanValue()) { %>
        <div class="row">
          <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramConcluirContratosNaoPagos">
             <div><span id="paramConcluirContratosNaoPagos"><hl:message key="rotulo.param.svc.concluir.contratos.nao.pagos"/></span></div>
             <div class="form-check form-check-inline mt-1">
                 <% if (!CSE_TPS_CONCLUI_ADE_NAO_PAGA){ %>
                 <INPUT NAME="check_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(cseConcluiNaoPagas)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>');" checked >
                 <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
             </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(concluiNaoPagas ? "CHECKED" : "")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(concluiNaoPagas ? "" : "CHECKED")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>"><hl:message key="rotulo.nao"/></label>
                  <% } else { %>
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(cseConcluiNaoPagas)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>');">
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(concluiNaoPagas ? "CHECKED" : "")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(concluiNaoPagas ? "" : "CHECKED")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA)%>"><hl:message key="rotulo.nao"/></label>
                  <% } %>
          </div>
         </div>
        </div>
      <% } %>

      <% if (parametrosSvc.containsKey(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)).booleanValue()) { %>
        <div class="row">
          <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramConcluirContratosSerExcluido">
             <div><span id="paramConcluirContratosSerExcluido"><hl:message key="rotulo.param.svc.concluir.contratos.ser.excluido"/></span></div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)%>" value="1" onChange="validaRadiosPadrao(this, 'false', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)%>');" <%=(String)(concluiAdeSerExcluido ? "" : "CHECKED")%>>
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(concluiAdeSerExcluido ? "CHECKED" : "DISABLED")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)%>"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(concluiAdeSerExcluido ? "" : "CHECKED DISABLED")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO)%>"><hl:message key="rotulo.nao"/></label>
              </div>
          </div>
        </div>
      <% } %>

      <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_ESCOLHER_FORMA_NUMERACAO_PARCELAS, CodedValues.TPC_SIM, responsavel) &&
              parametrosSvc.containsKey(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS) && ((Boolean)parametrosSvc.get(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)).booleanValue()) { %>
        <div class="row">
          <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramFormaNumeracaoParcelas">
             <div><span id="paramFormaNumeracaoParcelas"><hl:message key="rotulo.param.svc.forma.numeracao.parcelas"/></span></div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)%>" value="<%=TextHelper.forHtmlAttribute(formaNumeracaoParcelasPadrao)%>" onChange="validaRadiosPadrao(this, 'false', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)%>');" <%=!TextHelper.isNull(formaNumeracaoParcelas) ? "" : "CHECKED" %><%=(String)(!preserva ? " DISABLED" : "")%>>
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TextHelper.forHtmlAttribute(CodedValues.FORMA_NUMERACAO_PARCELAS_SEQUENCIAL)%>" <%=(String)(CodedValues.FORMA_NUMERACAO_PARCELAS_SEQUENCIAL.equals(formaNumeracaoParcelas) || TextHelper.isNull(formaNumeracaoParcelas) ? "CHECKED" : "")%><%= TextHelper.isNull(formaNumeracaoParcelas) || !preserva ? " DISABLED" : "" %>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)%>"><hl:message key="rotulo.param.svc.forma.numeracao.parcelas.sequencial"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TextHelper.forHtmlAttribute(CodedValues.FORMA_NUMERACAO_PARCELAS_MANTEM_AO_REJEITAR)%>" <%=(String)(CodedValues.FORMA_NUMERACAO_PARCELAS_MANTEM_AO_REJEITAR.equals(formaNumeracaoParcelas) ? "CHECKED" : "")%><%= TextHelper.isNull(formaNumeracaoParcelas) || !preserva ? " DISABLED" : "" %>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS)%>"><hl:message key="rotulo.param.svc.forma.numeracao.parcelas.mantem.ao.rejeitar"/></label>
              </div>
          </div>
        </div>
      <% } %>

      <% if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)).booleanValue()) { %>
        <div class="row">
          <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramPermiteRepetirIndice">
              <div><span id="paramPermiteRepetirIndice"><hl:message key="rotulo.param.svc.permite.repetir.indice"/></span></div>
              <div class="form-check form-check-inline mt-1">
                  <% if (permiteCadIndice) { %>
                  <% if(podeRepetirIndice) { %>
                  <% if(!CSE_TPS_PERMITE_REPETIR_INDICE_CSA){ %>
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=TextHelper.forJavaScript(tpsPermiteRepetirIndiceCse)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>');" checked>
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(tpsPermiteRepetirIndiceCse.equals("S") ? "checked" : "")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(tpsPermiteRepetirIndiceCse.equals("N") ? "checked" : "")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>"><hl:message key="rotulo.nao"/></label>
                  <% } else { %>
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=TextHelper.forJavaScript(tpsPermiteRepetirIndiceCse)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>');">
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(tpsPermiteRepetirIndiceCsa.equals("S") ? "checked" : "")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(tpsPermiteRepetirIndiceCsa.equals("N") ? "checked" : "")%>>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>"><hl:message key="rotulo.nao"/></label>
                  <% } %>
                  <% } else { %>
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" value="1" disabled checked >
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" disabled >
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline mt-1">
                  <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" disabled checked>
                  <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA)%>"><hl:message key="rotulo.nao"/></label>
                  <% } %>
                  <% } %>
              </div>
          </div>
        </div>
      <% } %>
      
      <%-- Parametro que permite fazer contrato com prazo superior ao contrato do servidor ao orgão --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramPrazoMaiorContratoServidor">
                  <div><span id="paramPrazoMaiorContratoServidor"><hl:message key="rotulo.param.svc.csa.prazo.maior.contrato.servidor"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <% if (!CSE_TPS_PERMITE_CONTRATO_SUPER_SER_CSA){ %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=TextHelper.forJavaScript(csePermitePrazoMaior)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>');" checked >
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(csaPermitePrazoMaior.equals("S") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(csaPermitePrazoMaior.equals("N") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>"><hl:message key="rotulo.nao"/></label>
                      <%} else { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=TextHelper.forJavaScript(csePermitePrazoMaior)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>');">
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(csaPermitePrazoMaior.equals("S") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(csaPermitePrazoMaior.equals("N") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA)%>"><hl:message key="rotulo.nao"/></label>
                      <% } %>
                  </div>
              </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CNV_PODE_DEFERIR) &&
          ((Boolean)parametrosSvc.get(CodedValues.TPS_CNV_PODE_DEFERIR)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramCsaPodeDeferir">
                  <div><span id="paramCsaPodeDeferir"><hl:message key="rotulo.param.svc.csa.pode.deferir"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CNV_PODE_DEFERIR)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CNV_PODE_DEFERIR)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(cnvDefere.equals("S") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CNV_PODE_DEFERIR)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CNV_PODE_DEFERIR)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CNV_PODE_DEFERIR)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(cnvDefere.equals("N") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CNV_PODE_DEFERIR)%>"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>

      <%-- Parametro Serviço verifica se a senha da consignatária é obrigatória na reserva e renegociação de margem --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramSvcSenhaObrigatoriacsa">
                  <div><span id="paramSvcSenhaObrigatoriacsa"><hl:message key="rotulo.param.svc.senha.obrigatoria.csa.reserva.margem"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <% if (!CSE_TPS_SER_SENHA_OBRIGATORIA_CSA) { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(serSenhaObrigatoriaCse)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>');" checked >
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(serSenhaObrigatoriaCsa ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(!serSenhaObrigatoriaCsa ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>"><hl:message key="rotulo.nao"/></label>
                      <% } else { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(serSenhaObrigatoriaCse)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>');">
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(serSenhaObrigatoriaCsa ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(!serSenhaObrigatoriaCsa ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)%>"><hl:message key="rotulo.nao"/></label>
                      <%   } %>
                  </div>
              </div>
          </div>
      <% } %>

      <% if (!temCET && parametrosSvc.containsKey(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramSvcSomaIofSimulacao">
                  <div><span id="paramSvcSomaIofSimulacao"><hl:message key="rotulo.param.svc.soma.iof.simulacao.reserva"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <% if (!CSE_TPS_SOMA_IOF_SIMULACAO_RESERVA) { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(incluiIofCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>');" checked >
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(incluiIof ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(incluiIof ? "" : "CHECKED")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>"><hl:message key="rotulo.nao"/></label>
                      <% } else { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(incluiIofCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>');">
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(incluiIof ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(incluiIof ? "" : "CHECKED")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA)%>"><hl:message key="rotulo.nao"/></label>
                      <%   } %>
                  </div>
              </div>
          </div>
      <% } %>
      <% if (temSimulacaoConsignacao && parametrosSvc.containsKey(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING) &&  ((Boolean) parametrosSvc.get(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)).booleanValue()) {
      %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramCsaConsideradaLimiteRanking">
                  <div><span id="paramCsaConsideradaLimiteRanking"><hl:message key="rotulo.param.svc.csa.considerada.limite.ranking"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <% if (!CSE_TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING) { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(cseDeveContarLimiteRanking)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>');" checked >
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(csaDeveContarLimiteRanking ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(csaDeveContarLimiteRanking ? "" : "CHECKED")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>"><hl:message key="rotulo.nao"/></label>
                      <% } else { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(cseDeveContarLimiteRanking)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>');">
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(csaDeveContarLimiteRanking ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(csaDeveContarLimiteRanking ? "" : "CHECKED")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING)%>"><hl:message key="rotulo.nao"/></label>
                      <%   } %>
                  </div>
              </div>
          </div>
      <% } %>

      <% if (responsavel.isCseSup() && parametrosSvc.containsKey(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramIdentificadorObrigatorio">
                  <div><span id="paramIdentificadorObrigatorio"><hl:message key="rotulo.param.svc.identificador.obrigatorio"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <% if (!CSE_TPS_IDENTIFICADOR_ADE_OBRIGATORIO) { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(adeIdentificadorObrigatorioCse)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>');" checked>
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(adeIdentificadorObrigatorioCsa ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(adeIdentificadorObrigatorioCsa ? "" : "CHECKED")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>"><hl:message key="rotulo.nao"/></label>
                      <% } else { %>
                      <INPUT NAME="check_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" value="1" onChange="validaRadiosPadrao(this, '<%=(boolean)(adeIdentificadorObrigatorioCse)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>');">
                      <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(adeIdentificadorObrigatorioCsa ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(adeIdentificadorObrigatorioCsa ? "" : "CHECKED")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)%>"><hl:message key="rotulo.nao"/></label>
                      <% } %>
                  </div>
              </div>
          </div>
      <% } %>

      <%-- Parâmetros de cadastro de conta para depósito do saldo devedor de contratos comprados --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR) && ((Boolean)parametrosSvc.get(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR)).booleanValue() && temModuloCompra) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-12 mb-0" aria-labelledby="bandoDepositoDevedorComprados">
              <span id="bandoDepositoDevedorComprados"><hl:message key="rotulo.codigo.banco.deposito.saldo.devedor.comprados"/></span>
            </div>
            <div class="form-group col-sm-12 col-md-4 mt-0">
             <INPUT TYPE="text" class="form-control" NAME="ajudaBanco" ID="ajudaBanco" VALUE="<%=TextHelper.forHtmlAttribute(bancoSaldoDevedor)%>" SIZE="8" MAXLENGTH="8" onFocus="SetarEventoMascara(this,'#A8',true);" onBlur="fout(this);ValidaMascara(this);if (!IsNulo(document.forms[0].ajudaBanco)) {SelecionaComboBanco(document.forms[0].tps_<%=TextHelper.forJavaScript(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR)%>, document.forms[0].ajudaBanco.value, arrayBancos);}">
            </div>
            <div class="form-group col-sm-12 col-md-4 mt-0">
               <SELECT CLASS="form-control form-select col-sm-12 m-1" NAME="tps_<%=(String)(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR)%>" onChange="document.forms[0].ajudaBanco.value = document.forms[0].tps_<%=TextHelper.forJavaScript(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR)%>.value;">
                 <OPTION VALUE="" SELECTED>
                 <hl:message key="rotulo.codigo.banco.deposito.saldo.devedor.comprados.selecione.banco"/>
                 </OPTION>
               </SELECT>
            </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR) && ((Boolean)parametrosSvc.get(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR)).booleanValue()  && temModuloCompra) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="agenciaDepositoDevedorComprados"><hl:message key="rotulo.codigo.agencia.deposito.saldo.devedor.comprados"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR)%>" value="<%=TextHelper.forHtmlAttribute(agenciaSaldoDevedor)%>" TYPE="text" class="form-control" MAXLENGTH="5" SIZE="10" onFocus="SetarEventoMascara(this,'#D5',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR) && ((Boolean)parametrosSvc.get(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR)).booleanValue() && temModuloCompra) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="contaDepositoDevedorComprados"><hl:message key="rotulo.codigo.conta.deposito.saldo.devedor.comprados"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR)%>" value="<%=TextHelper.forHtmlAttribute(contaSaldoDevedor)%>" TYPE="text" class="form-control" MAXLENGTH="10" SIZE="10" onFocus="SetarEventoMascara(this,'#D10',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV) && ((Boolean)parametrosSvc.get(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV)).booleanValue() && temModuloCompra) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="favorecidoSaldoDevedorComprados"><hl:message key="rotulo.nome.favorecido.saldo.devedor.comprados"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV)%>" value="<%=TextHelper.forHtmlAttribute(nomeFavorecidoSaldoDevedor)%>" TYPE="text" class="form-control" MAXLENGTH="100" SIZE="30" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV) && ((Boolean)parametrosSvc.get(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV)).booleanValue() && temModuloCompra) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="cnpjSaldoDevedorComprados"><hl:message key="rotulo.cnpj.saldo.devedor.comprados"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV)%>" value="<%=TextHelper.forHtmlAttribute(cnpjSaldoDevedor)%>" TYPE="text" class="form-control" MAXLENGTH="18" SIZE="30" onFocus="SetarEventoMascara(this,'DD.DDD.DDD/DDDD-DD',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>

      <% if (parametrosSvc.containsKey(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA)).booleanValue() && temModuloCompra) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6" aria-labelledby="destinatariosEmailSolicitacaoSaldoCompra">
                  <div><span id="destinatariosEmailSolicitacaoSaldoCompra"><hl:message key="rotulo.destinatarios.email.solicitacao.saldo.compra"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TextHelper.forHtmlAttribute( CodedValues.RECEBE_EMAIL_APENAS_CONSIGNATARIA )%>" <%=(String)((TextHelper.isNull(destinatariosEmailsCompra) || destinatariosEmailsCompra.equals(CodedValues.RECEBE_EMAIL_APENAS_CONSIGNATARIA)) ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA)%>"><hl:message key="rotulo.destinatarios.email.solicitacao.saldo.compra.csa"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TextHelper.forHtmlAttribute( CodedValues.RECEBE_EMAIL_APENAS_CORRESPONDENTE )%>" <%=(String)((!TextHelper.isNull(destinatariosEmailsCompra) && destinatariosEmailsCompra.equals(CodedValues.RECEBE_EMAIL_APENAS_CORRESPONDENTE)) ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA)%>"><hl:message key="rotulo.destinatarios.email.solicitacao.saldo.compra.cor"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TextHelper.forHtmlAttribute( CodedValues.RECEBE_EMAIL_CSA_E_COR )%>" <%=(String)((!TextHelper.isNull(destinatariosEmailsCompra) && destinatariosEmailsCompra.equals(CodedValues.RECEBE_EMAIL_CSA_E_COR)) ? "CHECKED" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA)%>"><hl:message key="rotulo.destinatarios.email.solicitacao.saldo.compra.ambos"/></label>
                  </div>
              </div>
          </div>
      <% } %>

      <%-- Parâmetros para cadastro dos emails de notificação dos eventos de compra de contratos --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS)).booleanValue() && temModuloCompra) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="emailAvisoCompraTerceiros"><hl:message key="rotulo.email.aviso.compra.terceiros"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS)%>" value="<%=TextHelper.forHtmlAttribute(emailInfContratosComprados)%>" TYPE="text" class="form-control" MAXLENGTH="500" SIZE="50" onFocus="SetarEventoMascara(this,'#*500',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR)).booleanValue() && temModuloCompra) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="emailAvisoCadastroSaldoDevedorComprado"><hl:message key="rotulo.email.aviso.cadastro.saldo.devedor.comprado"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR)%>" value="<%=TextHelper.forHtmlAttribute(emailInfSaldoDevedor)%>" TYPE="text" class="form-control" MAXLENGTH="500" SIZE="50" onFocus="SetarEventoMascara(this,'#*500',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR)).booleanValue() && temModuloCompra && temEtapaAprovacaoSaldo) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="emailAvisoAprovacaoRejeicaoSaldoDevedor"><hl:message key="rotulo.email.aviso.aprovacao.rejeicao.saldo.devedor.compra"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR)%>" value="<%=TextHelper.forHtmlAttribute(emailInfAprSaldoDevedor)%>" TYPE="text" class="form-control" MAXLENGTH="500" SIZE="50" onFocus="SetarEventoMascara(this,'#*500',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR)).booleanValue() && temModuloCompra) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="emailAvisoPagamentoSaldoDevedorComprados"><hl:message key="rotulo.email.aviso.pagamento.saldo.devedor.comprados.terceiros"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR)%>" value="<%=TextHelper.forHtmlAttribute(emailInfPgtSaldoDevedor)%>" TYPE="text" class="form-control" MAXLENGTH="500" SIZE="50" onFocus="SetarEventoMascara(this,'#*500',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO)).booleanValue() && temModuloCompra) { %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="emailAvisoLiquidacaoContratoComprado"><hl:message key="rotulo.email.aviso.liquidacao.contrato.comprado"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO)%>" value="<%=TextHelper.forHtmlAttribute(emailInfLiqContratoComprado)%>" TYPE="text" class="form-control" MAXLENGTH="500" SIZE="50" onFocus="SetarEventoMascara(this,'#*500',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa o e-mail para notificação de solicitação de saldo devedor pelo servidor --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="emailAvisoSolicitacaoSaldoDevedorServidor"><hl:message key="rotulo.email.aviso.soliticacao.saldo.devedor.servidor"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR)%>" value="<%=TextHelper.forHtmlAttribute(emailInfSolicitacaoSaldoDevedor)%>" TYPE="text" class="form-control" MAXLENGTH="500" SIZE="50" onFocus="SetarEventoMascara(this,'#*500',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa o e-mail para notificação de novo leilao --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="emailAvisoNovoLeilao"><hl:message key="rotulo.email.aviso.novo.leilao"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO)%>" value="<%=TextHelper.forHtmlAttribute(emailInfNovoLeilao)%>" TYPE="text" class="form-control" MAXLENGTH="500" SIZE="50" onFocus="SetarEventoMascara(this,'#*500',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa o e-mail para notificação de alteração do código de verba do serviço --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="emailAvisoNotificaoAlteracaoCodverbaServico"><hl:message key="rotulo.email.aviso.notificacao.alteracao.codVerba.servico"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA)%>" value="<%=TextHelper.forHtmlAttribute(emailInfAlterCodVerbaConvenioCsa)%>" TYPE="text" class="form-control" MAXLENGTH="500" SIZE="50" onFocus="SetarEventoMascara(this,'#*500',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa o e-mail para notificação da CSA que servidor  --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="emailAvisoCsaServidorNovaCanSolicitacao"><hl:message key="rotulo.email.aviso.csa.servidor.fez.nova.cancelou.solicitacao"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR)%>" value="<%=TextHelper.forHtmlAttribute(emailNotifCsaSerFezCancelouSolicitacao)%>" TYPE="text" class="form-control" MAXLENGTH="500" SIZE="50" onFocus="SetarEventoMascara(this,'#*500',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa o valor percentual máximo permitido para a alteração --%>
      <% if(parametrosSvc.containsKey(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE) && ((Boolean)parametrosSvc.get(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                  <label for="paramSvcLimiteAumentoValorAde"><hl:message key="rotulo.param.svc.limite.aumento.valor.ade"/></label>
                  <input type="hidden" name="tps_<%=(String)(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)%>" value="<%=TextHelper.forHtmlAttribute((limite_aumento_valor_ade + ";" + limite_aumento_valor_ade_ref))%>">
                  <input class="form-control" type="text" name="tps_<%=(String)(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)%>_VLR" value="<%=TextHelper.forHtmlAttribute((!limite_aumento_valor_ade.equals("") ? NumberHelper.reformat(limite_aumento_valor_ade, "en", NumberHelper.getLang()) : ""))%>" size="12" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);setHidden(f0.tps_<%=TextHelper.forJavaScript(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)%>, f0.tps_<%=(String)(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)%>_VLR.value + ';' + f0.tps_<%=TextHelper.forJavaScript(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)%>_VLR_REF.value); ">
              </div>
              <div class="form-group col-sm-12 col-md-6">
                  <label for="paramSvcLimiteAumentoValorAdeData"><hl:message key="rotulo.param.svc.limite.aumento.valor.ade.data"/></label>
                  <input class="form-control" type="text" name="tps_<%=(String)(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)%>_VLR_REF" value="<%=TextHelper.forHtmlAttribute(limite_aumento_valor_ade_ref)%>" size="12" onFocus="SetarEventoMascara(this,'<%=LocaleHelper.getDateJavascriptPattern()%>',true);" onBlur="fout(this);ValidaMascara(this);setHidden(f0.tps_<%=TextHelper.forJavaScript(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)%>, f0.tps_<%=(String)(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)%>_VLR.value + ';' + f0.tps_<%=TextHelper.forJavaScript(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)%>_VLR_REF.value);">
              </div>
          </div>
      <%  } %>

      <%-- Parametro Serviço verifica se a consignatária exige que o servidor seja correntista --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6" aria-labelledby="paramSvcNovosContratosCorrentistas">
                  <div><span id="paramSvcNovosContratosCorrentistas"><hl:message key="rotulo.param.svc.permitir.novos.contratos.correntistas"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(csaExigeServidorCorrentista ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(!csaExigeServidorCorrentista ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA)%>"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>

      <%-- Parametro Serviço verifica se exibe boleto ao fim da operação --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EXIBE_BOLETO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EXIBE_BOLETO)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6" aria-labelledby="paramSvcExibeBoleto">
                  <div><span id="paramSvcExibeBoleto"><hl:message key="rotulo.param.svc.exibe.boleto"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EXIBE_BOLETO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_EXIBE_BOLETO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(tpsExibeBoleto ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_EXIBE_BOLETO)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EXIBE_BOLETO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_EXIBE_BOLETO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(!tpsExibeBoleto ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_EXIBE_BOLETO)%>"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que permite contratos com valores negativos --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6" aria-labelledby="paramSvcPermiteValorNegativo">
                  <div><span id="paramSvcPermiteValorNegativo"><hl:message key="rotulo.param.svc.permite.valor.negativo"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(permiteValorNegativo ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(!permiteValorNegativo ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO)%>"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que exibe ou não a csa na listagem de solicitação --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO) &&
              ((Boolean)parametrosSvc.get(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO)).booleanValue() && !nse_codigo.equalsIgnoreCase(CodedValues.NSE_EMPRESTIMO)) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6" aria-labelledby="paramSvcCsaListagemSolicitacao">
                  <div><span id="paramSvcCsaListagemSolicitacao"><hl:message key="rotulo.param.svc.csa.listagem.solicitacao"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(exibeCsaListagemSolicitacao ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(!exibeCsaListagemSolicitacao ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO)%>"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que permite assinatura digital do contrato da consignação para novas solicitações  --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES) &&  ((Boolean)parametrosSvc.get(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6" aria-labelledby="paramDescricaoExigeAssinaturaDigital">
                  <div><span id="paramDescricaoExigeAssinaturaDigital"><hl:message key="mensagem.parametro.descricao.exige.assinatura.digital"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(exigeAssinaturaDigital ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(!exigeAssinaturaDigital ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES)%>"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>
      
      <%-- Parametro Serviço que informa quantidade de dias para desbloqueio automático de reservas não confirmadas --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF) && ((Boolean)parametrosSvc.get(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)).booleanValue()) { %>
          <div class="row">
              <% if (!CSE_TPS_DIAS_DESBL_AUT_RESERVA) { %>
              <div class="form-group col-sm-12 col-md-6 mt-1">
                  <label for="tps_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>"><hl:message key="rotulo.param.svc.desbloqueio.automatico.reservas.nao.confirmadas.dias"/></label>
                  <input name="tps_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>" type="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" value="<%=TextHelper.forHtmlAttribute(diasDesblAutReservaCSE)%>" size="10" disabled>
              </div>
              <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(diasDesblAutReservaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>');" checked>
                  <label for="check_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <% } else { %>
              <div class="form-group col-sm-12 col-md-6 mt-1">
                  <label for="tps_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>"><hl:message key="rotulo.param.svc.desbloqueio.automatico.reservas.nao.confirmadas.dias"/></label>
                  <input name="tps_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>" type="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" value="<%=TextHelper.forHtmlAttribute(diasDesblAutReserva)%>" size="10">
              </div>
              <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(diasDesblAutReservaCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>');" >
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <% } %>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa o prazo máximo em dias entre a data de inclusão do contrato e o dia do repasse da consignatária para renegociação no período --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="paramSvcMaxPrazoRenegociacaoPeriodo"><hl:message key="rotulo.param.svc.max.prazo.renegociacao.no.periodo"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO)%>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TextHelper.forHtmlAttribute(maxPrazoRenegociacaoPeriodo)%>" SIZE="10">
            </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa o prazo mínimo em dias entre a data de inclusão do contrato e o dia do repasse da consignatária para renegociação --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="paramSvcMinPrazoIniDescontoRenegociacao"><hl:message key="rotulo.param.svc.min.prazo.ini.desconto.renegociacao"/></label>
                <INPUT NAME="tps_<%=(String)(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO)%>" TYPE="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO)%>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TextHelper.forHtmlAttribute(minPrazoRenegociacaoPeriodo)%>" SIZE="10">
            </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa a data de expiração dos convênios desta CSA e SVC --%>
      <% if(parametrosSvc.containsKey(CodedValues.TPS_DATA_EXPIRACAO_CONVENIO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_DATA_EXPIRACAO_CONVENIO)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="paramSvcDataExpiracaoConvenio"><hl:message key="rotulo.param.svc.data.expiracao.convenio"/></label>
                <input name="tps_<%=(String)(CodedValues.TPS_DATA_EXPIRACAO_CONVENIO)%>" value="<%=TextHelper.forHtmlAttribute(dataExpiracaoCnv)%>" type="text" class="form-control" size="12" onFocus="SetarEventoMascara(this,'<%=LocaleHelper.getDateJavascriptPattern()%>',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <%  } %>

      <%-- Parametro Serviço que informa o numero do contrato no convenio --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_NUMERO_CONTRATO_CONVENIO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_NUMERO_CONTRATO_CONVENIO)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="paramSvcNumeroContratoConvenio"><hl:message key="rotulo.param.svc.numero.contrato.convenio"/></label>
              <input name="tps_<%=(String)(CodedValues.TPS_NUMERO_CONTRATO_CONVENIO)%>" value="<%=TextHelper.forHtmlAttribute(numeroContratoCnv)%>" type="text" class="form-control" maxlength="255" size="50" onFocus="SetarEventoMascara(this,'#*255',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa o limite por tempo a inclusão de contratos em duplicidade --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE) && ((Boolean)parametrosSvc.get(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)).booleanValue()) { %>
          <div class="row">
              <% if (!CSE_TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE) { %>
              <div class="form-group col-sm-12 col-md-6">
                  <label for="paramSvcPeriodoRestricaoNovasConsig"><hl:message key="rotulo.param.svc.periodo.restricao.novas.consignacoes.duplicidade"/></label>
                  <input name="tps_<%=(String)(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>" type="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" value="<%=TextHelper.forHtmlAttribute(periodoLimiteAdeDuplicidadeCSE)%>" size="10" disabled>
              </div>
              <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(periodoLimiteAdeDuplicidadeCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>');" checked>
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <% } else { %>
              <div class="form-group col-sm-12 col-md-6">
                  <label for="paramSvcPeriodoRestricaoNovasConsig"><hl:message key="rotulo.param.svc.periodo.restricao.novas.consignacoes.duplicidade"/></label>
                  <input name="tps_<%=(String)(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>" type="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);" value="<%=TextHelper.forHtmlAttribute(periodoLimiteAdeDuplicidade)%>" size="10">
              </div>
              <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
                  <INPUT NAME="check_<%=(String)(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(periodoLimiteAdeDuplicidadeCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>');" >
                  <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
              </div>
              <% } %>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa a máscara para o campo de número de contrato de benefício (tb_contrato_beneficio.cbe_numero) utilizado pela operadora (CSA) --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="paramSvcNumeroContratoBeneficioOperadora"><hl:message key="rotulo.param.svc.numero.contrato.beneficio.operadora"/></label>
              <input name="tps_<%=(String)(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO)%>" value="<%=TextHelper.forHtmlAttribute(mascaraNumeroContratoBeneficio)%>" type="text" class="form-control" maxlength="10" size="10" onFocus="SetarEventoMascara(this,'#*10',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
           </div>
      <% } %>

      <%-- Parametro Serviço que informa a mensagem a ser enviada ao servidor/funcionário após o deferimento de uma solicitação --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA) && ((Boolean)parametrosSvc.get(CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="paramSvcMengEnviadaAposDeferimentoSolicitacao"><hl:message key="rotulo.param.svc.mensagem.enviada.apos.deferimento.solicitacao"/></label>
              <INPUT NAME="tps_<%=(String)(CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA)%>" value="<%=TextHelper.forHtmlAttribute(mensagemSerSolDeferida)%>" TYPE="text" class="form-control" MAXLENGTH="255" SIZE="50" onFocus="SetarEventoMascara(this,'#*255',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
           </div>
      <% } %>

      <%-- DESENV-14336 : Parâmetro para oferecer contratação de seguro prestamista ao solicitar empréstimo --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC) && ((Boolean)parametrosSvc.get(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="paramSvcMsgExibirSolicitacaoServidorOfertaOutroSvc"><hl:message key="rotulo.param.svc.mensagem.solicitacao.servidor.oferta.outro.svc"/></label>
              <input name="tps_<%=(String)(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC)%>" id="paramSvcMsgExibirSolicitacaoServidorOfertaOutroSvc" value="<%=TextHelper.forHtmlAttribute(msgExibirSolicitacaoServidorOfertaOutroSvc)%>" type="text" class="form-control" maxlength="500" size="50" onFocus="SetarEventoMascara(this,'#*500',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
           </div>
      <% } %>

      <%-- Parametro Serviço para exibir o somatório do valor das consignações do serviço/consignatária como valor fora da margem --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM) &&
              ((Boolean)parametrosSvc.get(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM)).booleanValue() && responsavel.isCseSup()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6" aria-labelledby="paramSvcExibirComoValorForaMargem">
                  <div><span id="paramSvcExibirComoValorForaMargem"><hl:message key="rotulo.param.svc.exibir.como.valor.fora.margem"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM)%>" onFocus="SetarEventoMascara(this,'#*255',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(exibirComoValorForaMargem ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM)%>" onFocus="SetarEventoMascara(this,'#*255',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(!exibirComoValorForaMargem ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM)%>"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>

      <% if (parametrosSvc.containsKey(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramExigeConfirmarRenegociacao">
                  <div><span id="paramExigeConfirmarRenegociacao"><hl:message key="rotulo.param.svc.exige.confirmacao.renegociacao"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo()%>" <%=(String)(exigeConfirmacaoRenegociacao.equals(TpsExigeConfirmacaoRenegociacaoValoresEnum.TODAS.getCodigo()) ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>"><hl:message key="rotulo.campo.todas.simples"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TpsExigeConfirmacaoRenegociacaoValoresEnum.SOMENTE_PARA_MAIOR.getCodigo()%>" <%=(String)(exigeConfirmacaoRenegociacao.equals(TpsExigeConfirmacaoRenegociacaoValoresEnum.SOMENTE_PARA_MAIOR.getCodigo()) ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>"><hl:message key="rotulo.somente.maior"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="<%=TpsExigeConfirmacaoRenegociacaoValoresEnum.SOMENTE_PARA_MENOR.getCodigo()%>" <%=(String)(exigeConfirmacaoRenegociacao.equals(TpsExigeConfirmacaoRenegociacaoValoresEnum.SOMENTE_PARA_MENOR.getCodigo()) ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>"><hl:message key="rotulo.somente.menor"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);"VALUE="<%=TpsExigeConfirmacaoRenegociacaoValoresEnum.NENHUMA.getCodigo()%>" <%=(String)(exigeConfirmacaoRenegociacao.equals(TpsExigeConfirmacaoRenegociacaoValoresEnum.NENHUMA.getCodigo()) ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO)%>"><hl:message key="rotulo.campo.nenhuma.simples"/></label>
                  </div>
              </div>
          </div>
      <% } %>

      <%-- Parametro Serviço verifica se a consignatária bloqueia inclusão de contratos por tipo de registro servidor --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6" aria-labelledby="paramSvcBloqueiaInclusaoLoteRseTipo">
                  <div><span id="paramSvcBloqueiaInclusaoLoteRseTipo"><hl:message key="rotulo.param.svc.bloqueia.inclusao.lote.rse.tipo"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(bloqueiaInclusaoLoteRseTipo ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(!bloqueiaInclusaoLoteRseTipo ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO)%>"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>

      <%-- Parametro Serviço verifica se exige senha do servidor via lote --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE) && ((Boolean)parametrosSvc.get(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6" aria-labelledby="paramSvcExigeSenhaSerLote">
                  <div><span id="paramSvcExigeSenhaSerLote"><hl:message key="rotulo.param.svc.exige.senha.servidor.lote"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="S" <%=(String)(exigeSenhaServidorViaLote ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE)%>"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE)%>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(!exigeSenhaServidorViaLote ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE)%>"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>

      <%-- Parametro Serviço que informa a máscara para o campo de número de contrato de benefício (tb_contrato_beneficio.cbe_numero) utilizado pela operadora (CSA) --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_RELEVANCIA_CSA_RANKING) && ((Boolean) parametrosSvc.get(CodedValues.TPS_RELEVANCIA_CSA_RANKING)).booleanValue()) { %>
           <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="tps_<%=(String)(CodedValues.TPS_RELEVANCIA_CSA_RANKING)%>"><hl:message key="rotulo.param.svc.relevancia.csa"/></label>
              <input name="tps_<%=(String)(CodedValues.TPS_RELEVANCIA_CSA_RANKING)%>" value="<%=TextHelper.forHtmlAttribute(relevanciaCsaRanking)%>" type="text" class="form-control" maxlength="2" size="2" onFocus="SetarEventoMascara(this,'#D2',true);" onBlur="fout(this);ValidaMascara(this);"/>
            </div>
           </div>
      <% } %>

      <%-- DESENV-16329 - Projeto MAG - eConsig - Criar novo REST para retornar parâmetros MAG --%>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO) && ((Boolean) parametrosSvc.get(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO)).booleanValue()) { %>
         <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO)%>"><hl:message key="rotulo.param.svc.configurar.id.proposta.convenio"/></label>
            <input name="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO)%>" value="<%=TextHelper.forHtmlAttribute(configurarIdPropostaConvenio)%>" type="text" class="form-control" maxlength="10" size="10"/>
          </div>
         </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO) && ((Boolean) parametrosSvc.get(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO)).booleanValue()) { %>
         <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO)%>"><hl:message key="rotulo.param.svc.configurar.dia.vencimento.contrato"/></label>
            <input name="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO)%>" value="<%=TextHelper.forHtmlAttribute(configurarDiaVencimentoContrato)%>" type="number" class="form-control" min="1" max="31" />
          </div>
         </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO) && ((Boolean) parametrosSvc.get(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO)).booleanValue()) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-6" aria-labelledby="paramConfigurarPeriodoCompetenciaDebito">
                  <div><span id="paramConfigurarPeriodoCompetenciaDebito"><hl:message key="rotulo.param.svc.configurar.periodo.competencia.debito"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO)%>" onFocus="SetarEventoMascara(this,'#*255',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="1" <%=(configurarPeriodoCompetenciaDebito.equals("1") || TextHelper.isNull(configurarPeriodoCompetenciaDebito) ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO)%>"><hl:message key="rotulo.param.svc.configurar.periodo.competencia.debito.mes.atual"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO)%>" onFocus="SetarEventoMascara(this,'#*255',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="2" <%=(configurarPeriodoCompetenciaDebito.equals("2") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO)%>"><hl:message key="rotulo.param.svc.configurar.periodo.competencia.debito.mes.seguinte"/></label>
                  </div>
              </div>
          </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO) && ((Boolean) parametrosSvc.get(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO)).booleanValue()) { %>
         <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO)%>"><hl:message key="rotulo.param.svc.configurar.numero.convenio"/></label>
            <input name="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO)%>" value="<%=TextHelper.forHtmlAttribute(configurarNumeroConvenio)%>" type="text" class="form-control" maxlength="255" size="10" onFocus="SetarEventoMascara(this,'#D11',true);" onBlur="fout(this);ValidaMascara(this);"/>
          </div>
         </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO) && ((Boolean) parametrosSvc.get(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO)).booleanValue()) { %>
         <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO)%>"><hl:message key="rotulo.param.svc.configurar.codigo.adesao"/></label>
            <input name="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO)%>" value="<%=TextHelper.forHtmlAttribute(configurarCodigoAdesao)%>" type="text" class="form-control" maxlength="10" size="10" onFocus="SetarEventoMascara(this,'#*255',true);" onBlur="fout(this);ValidaMascara(this);"/>
          </div>
         </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO) && ((Boolean) parametrosSvc.get(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO)).booleanValue()) { %>
         <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO)%>"><hl:message key="rotulo.param.svc.configurar.idade.maxima.contratacao.seguro"/></label>
            <input name="tps_<%=(String)(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO)%>" value="<%=TextHelper.forHtmlAttribute(configurarIdadeMaximaContratacaoSeguro)%>" type="text" class="form-control" maxlength="3" size="10" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);"/>
          </div>
         </div>
      <% } %>
      <% if (parametrosSvc.containsKey(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO) && ((Boolean) parametrosSvc.get(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO).booleanValue())) { %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramObrigaInformacoesServidorSolicitacao">
                  <div><span id="paramObrigaInformacoesServidorSolicitacao"><hl:message key="rotulo.param.svc.obriga.informacoes.servidor.solicitacao"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>_N" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="N" <%=(String)(obrigaInformacoesServidorSolicitacao.equals("N") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>_N"><hl:message key="rotulo.nao"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>_E" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="E" <%=(String)(obrigaInformacoesServidorSolicitacao.equals("E") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>_E"><hl:message key="rotulo.param.svc.obriga.informacoes.servidor.solicitacao.endereco.obrigatorio"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>_C" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="C" <%=(String)(obrigaInformacoesServidorSolicitacao.equals("C") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>_C"><hl:message key="rotulo.param.svc.obriga.informacoes.servidor.solicitacao.celular.obrigatorio"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>_EC" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);" VALUE="EC" <%=(String)(obrigaInformacoesServidorSolicitacao.equals("EC") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)%>_EC"><hl:message key="rotulo.param.svc.obriga.informacoes.servidor.solicitacao.celular.endereco.obrigatorio"/></label>
                  </div>
              </div>
          </div>
      <% } %>
      <%
        if (parametrosSvc.containsKey(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA) && ((Boolean) parametrosSvc.get(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA).booleanValue())) {
      %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramObrigaInfoBancariaReservaSvcCsa">
                  <div><span id="paramObrigaInfoBancariaReservaSvcCsa"><hl:message key="rotulo.param.svc.csa.info.bancaria.em.lote.obrigatorio"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA)%>_S" VALUE="S" <%=(String)(obrigaInfoBancariasReserva.equals("S") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA)%>_S"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA)%>_N" VALUE="N" <%=(String)(obrigaInfoBancariasReserva.equals("N") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA)%>_N"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>
      <%
           if (parametrosSvc.containsKey(CodedValues.TPS_VALOR_SVC_FIXO_POSTO) && ((Boolean) parametrosSvc.get(CodedValues.TPS_VALOR_SVC_FIXO_POSTO).booleanValue())) {
      %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12">
                  <div><span id="posto"><hl:message key="rotulo.posto.servico"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <input type="radio" class="Radio" name="tps_<%=(String)(CodedValues.TPS_VALOR_SVC_FIXO_POSTO)%>" value="S" id="tps_<%=(String)(CodedValues.TPS_VALOR_SVC_FIXO_POSTO)%>_S" <%=(String) (valorFixoPostoCsaSvc.equals("S") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_VALOR_SVC_FIXO_POSTO)%>"> <hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <input type="radio" class="Radio" name="tps_<%=(String)(CodedValues.TPS_VALOR_SVC_FIXO_POSTO)%>" value="N" id="tps_<%=(String)(CodedValues.TPS_VALOR_SVC_FIXO_POSTO)%>_N" <%=(String) (valorFixoPostoCsaSvc.equals("N") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_VALOR_SVC_FIXO_POSTO)%>"> <hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <% } %>
          <%
              if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR) && ((Boolean) parametrosSvc.get(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR).booleanValue())) {
          %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12">
                  <div><span id="permiteCadastroSaldoDevedor"><hl:message key="rotulo.permite.cadastro.saldo.devedor"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <input type="radio" class="Radio" name="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>" value="1" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>_S" <%=(String) (permiteCadastroSaldoDevedor.equals("1") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>"> <hl:message key="rotulo.param.svc.cadastro.saldo.devedor.csa"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <input type="radio" class="Radio" name="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>" value="2" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>_N" <%=(String) (permiteCadastroSaldoDevedor.equals("2") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>"> <hl:message key="rotulo.param.svc.cadastro.saldo.devedor.sistema"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <input type="radio" class="Radio" name="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>" value="3" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>_N" <%=(String) (permiteCadastroSaldoDevedor.equals("3") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>"> <hl:message key="rotulo.param.svc.cadastro.saldo.devedor.ambos"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <input type="radio" class="Radio" name="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>" value="0" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>_N" <%=(String) (permiteCadastroSaldoDevedor.equals("0") ? "checked" : "")%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)%>"> <hl:message key="rotulo.param.svc.cadastro.saldo.devedor.nao"/></label>
                  </div>
              </div>
          </div>
          <% } %>

      <%
        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA) && ((Boolean) parametrosSvc.get(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA).booleanValue())) {
      %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramPermiteInclusaoSerBloqSemSenha">
                  <div><span id="paramPermiteInclusaoSerBloqSemSenha"><hl:message key="rotulo.param.svc.permite.inclusao.ser.bloq.sem.senha"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA)%>_S" VALUE="1" <%=permiteInclusaoSerBloqSemSenha ? "checked" : ""%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA)%>_S"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA)%>_N" VALUE="0" <%=!permiteInclusaoSerBloqSemSenha ? "checked" : ""%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA)%>_N"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <%} %>
      
       <%
        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA) && ((Boolean) parametrosSvc.get(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA).booleanValue())) {
      %>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramPermiteCancelarRenegociacaoMesmoAMargemFicandoNegativa">
                  <div><span id="paramPermiteCancelarRenegociacaoMesmoAMargemFicandoNegativa"><hl:message key="rotulo.param.svc.permite.cancelar.renegociacao.deixando.margem.negativa"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA)%>_S" VALUE="1" <%=permiteCancelarRenegociacaoDeixandoMargemNegativa ? "checked" : ""%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA)%>_S"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA)%>_N" VALUE="0" <%=!permiteCancelarRenegociacaoDeixandoMargemNegativa ? "checked" : ""%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA)%>_N"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <%} %>
      
      <% if (parametrosSvc.containsKey(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR) && ((Boolean) parametrosSvc.get(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR).booleanValue())) {%>
          <div class="row">
              <div class="form-group col-sm-12 col-md-12" aria-labelledby="paramPrazoLimitadoDataAdmissaoRse">
                  <div><span id="paramPrazoLimitadoDataAdmissaoRse"><hl:message key="rotulo.param.svc.prazo.limite.data.admissao.rse"/></span></div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR)%>_S" VALUE="S" <%=prazoLimitadoDataAdmissaoRse ? "checked" : ""%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR)%>_S"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline mt-1">
                      <INPUT NAME="tps_<%=(String)(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR)%>" TYPE="radio" class="Radio" id="tps_<%=(String)(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR)%>_N" VALUE="N" <%=!prazoLimitadoDataAdmissaoRse ? "checked" : ""%>>
                      <label class="labelSemNegrito" for="tps_<%=(String)(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR)%>_N"><hl:message key="rotulo.nao"/></label>
                  </div>
              </div>
          </div>
      <%} %>
      
	  <% if (parametrosSvc.containsKey(CodedValues.TPS_VLR_MINIMO_CONTRATO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_VLR_MINIMO_CONTRATO)).booleanValue()) { %>
		 <div class="row">
        <% if (!CSE_TPS_VLR_MINIMO_CONTRATO) { %>
          <div class="form-group col-sm-12 col-md-4 mt-1">
            <label for="paramSvcVlrMinimoContrato"><hl:message key="rotulo.param.svc.vlr.minimo.contrato"/></label>
            <input name="tps_<%=(String)(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>" type="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>" value="<%=TextHelper.forHtmlAttribute(vlrMinimoContratoCSE)%>" size="10" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" disabled>
          </div>
          <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
            <INPUT NAME="check_<%=(String)(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(vlrMinimoContratoCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>');" checked>
            <label for="check_<%=(String)(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
          </div>
        <% } else { %>
          <div class="form-group col-sm-12 col-md-4 mt-1">
            <label for="paramSvcVlrMinimoContrato"><hl:message key="rotulo.param.svc.vlr.minimo.contrato"/></label>
            <input name="tps_<%=(String)(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>" type="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>" value="<%=TextHelper.forHtmlAttribute(vlrMinimoContrato)%>" size="10" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }">
          </div>
          <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
            <INPUT NAME="check_<%=(String)(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(vlrMinimoContratoCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>');" >
            <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_VLR_MINIMO_CONTRATO)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
          </div>
       <% } %>
      </div>
    <% } %>
        
      <% if (parametrosSvc.containsKey(CodedValues.TPS_VLR_MAXIMO_CONTRATO) && ((Boolean)parametrosSvc.get(CodedValues.TPS_VLR_MAXIMO_CONTRATO)).booleanValue()) { %>
        <div class="row">
          <% if (!CSE_TPS_VLR_MAXIMO_CONTRATO) { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="paramSvcVlrMaximoContrato"><hl:message key="rotulo.param.svc.vlr.maximo.contrato"/></label>
              <input name="tps_<%=(String)(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>" type="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>" value="<%=TextHelper.forHtmlAttribute(vlrMaximoContratoCSE)%>" size="10" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" disabled>
            </div>
            <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(vlrMinimoContratoCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>');" checked>
              <label for="check_<%=(String)(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
            </div>
          <% } else { %>
            <div class="form-group col-sm-12 col-md-4 mt-1">
              <label for="paramSvcVlrMaximoContrato"><hl:message key="rotulo.param.svc.vlr.maximo.contrato"/></label>
              <input name="tps_<%=(String)(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>" type="text" class="form-control" id="tps_<%=(String)(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>" value="<%=TextHelper.forHtmlAttribute(vlrMaximoContrato)%>" size="10" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }">
            </div>
            <div class="float-end align-middle usrprd mt-5 col-sm-2 col-md-3">
              <INPUT NAME="check_<%=(String)(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>" TYPE="checkbox" id="check_<%=(String)(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>" value="1" onClick="validaTextPadrao(this, '<%=TextHelper.forJavaScript(vlrMaximoContratoCSE)%>', 'tps_<%=TextHelper.forJavaScript(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>');" >
              <label class="labelSemNegrito" for="check_<%=(String)(CodedValues.TPS_VLR_MAXIMO_CONTRATO)%>"><hl:message key="rotulo.servico.usar.padrao"/></label>
          </div>
          <% } %>
        </div>
        <% } %>

      <input name="svc" type="hidden" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
      <input type="hidden" name="MM_update" value="form1">
      <input type="hidden" name="svc" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
      <input type="hidden" name="SVC_IDENTIFICADOR" value="<%=TextHelper.forHtmlAttribute(svc_identificador)%>">
      <input type="hidden" name="SVC_DESCRICAO" value="<%=TextHelper.forHtmlAttribute(svc_descricao)%>">
      <input type="hidden" name="CNV_CODIGO" value="<%=TextHelper.forHtmlAttribute(cnv_codigo)%>">
      <input type="hidden" name="csa_codigo" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
      <input type="hidden" name="csa_nome" value="<%=TextHelper.forHtmlAttribute(csa_nome)%>">
      <input type="hidden" name="csaExigeServidorCorrentista" value="<%=(Boolean)csaExigeServidorCorrentista%>">
      <input type="hidden" name="bloqueiaInclusaoLoteRseTipo" value="<%=(Boolean)bloqueiaInclusaoLoteRseTipo%>">
      <input type="hidden" name="exigeSenhaServidorViaLote" value="<%=(Boolean)exigeSenhaServidorViaLote%>">
      </form>
    </div>
  </div>
    <div class="btn-action">
     <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=voltar%>'); return false;">
      <hl:message key="rotulo.botao.cancelar"/>
     </a>
     <a class="btn btn-primary" HREF="#no-back" onClick="if(validaParametros()){habilitaRadios();habilitarCamposEmails(true);f0.submit();} return false;">
      <hl:message key="rotulo.botao.salvar"/>
     </a>
    </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
