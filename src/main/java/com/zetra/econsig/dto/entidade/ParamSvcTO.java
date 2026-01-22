package com.zetra.econsig.dto.entidade;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ParamSvcTO</p>
 * <p>Description: Transfer Object dos parametros de servico</p>
 * <p>Copyright: Copyright (c) 2006-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public class ParamSvcTO implements Serializable {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParamSvcTO.class);

    private static final Map<String, ParamSvcTO> cache;

    static {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalMap<>();
        } else {
            cache = new HashMap<>();
        }
    }

    // DESENV-17859 : necessário para poder fazer o deserialize do redis
    public ParamSvcTO() {
    }

    private static synchronized void loadParamSvcTO(String svcCodigo, AcessoSistema responsavel) {
        try {
            final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
            final List<TransferObject> parametros = parametroController.selectParamSvcCse(svcCodigo, responsavel);
            final ParamSvcTO paramSvcTO = new ParamSvcTO(parametros, responsavel);
            cache.put(svcCodigo, paramSvcTO);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public static synchronized void removeParamSvcTO(String svcCodigo) {
        cache.remove(svcCodigo);
    }

    public static synchronized void reset() {
        cache.clear();
    }

    public static synchronized ParamSvcTO getParamSvcTO(String svcCodigo, AcessoSistema responsavel) {
        if (!cache.containsKey(svcCodigo)) {
            loadParamSvcTO(svcCodigo, responsavel);
        }
        return cache.get(svcCodigo);
    }

    private ParamSvcTO(List<TransferObject> param, AcessoSistema responsavel) {
        // Verifica se o sistema está configurado para trabalhar com o CET.
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        final boolean tpcDefaultReimplante = ParamSist.paramEquals(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, CodedValues.TPC_SIM, responsavel);
        final boolean tpcDefaultPreservacao = ParamSist.paramEquals(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_SIM, responsavel);
        final boolean tpcDefaultConclusao = ParamSist.paramEquals(CodedValues.TPC_DEFAULT_PARAM_SVC_CONCLUI_NAO_PG, CodedValues.TPC_SIM, responsavel);

        // Valor Default igual ao parâmetro de sistema
        tpsReimplantacaoAutomatica = tpcDefaultReimplante;
        tpsPreservaPrdRejeitadaReimpl = tpcDefaultPreservacao;
        tpsConcluiAdeNaoPaga = tpcDefaultConclusao;

        TransferObject next = null;
        String tpsCodigo = "";
        String valor = null;
        String valorRef = null;

        for (final TransferObject element : param) {
            next = element;
            tpsCodigo = next.getAttribute(Columns.TPS_CODIGO).toString();

            if (next.getAttribute(Columns.PSE_VLR) != null) {
                valor = next.getAttribute(Columns.PSE_VLR).toString();
            } else if (next.getAttribute(Columns.PSC_VLR) != null) {
                valor = next.getAttribute(Columns.PSC_VLR).toString();
            } else if (next.getAttribute(Columns.PCR_VLR) != null) {
                valor = next.getAttribute(Columns.PCR_VLR).toString();
            } else {
                valor = "";
            }
            if (next.getAttribute(Columns.PSE_VLR_REF) != null) {
                valorRef = next.getAttribute(Columns.PSE_VLR_REF).toString();
            } else if (next.getAttribute(Columns.PSC_VLR_REF) != null) {
                valorRef = next.getAttribute(Columns.PSC_VLR_REF).toString();
            } else {
                valorRef = "";
            }

            if (CodedValues.TPS_TAC_FINANCIADA.equals(tpsCodigo)) {
                tpsTacFinanciada = valor;

            } else if (CodedValues.TPS_INTEGRA_FOLHA.equals(tpsCodigo)) {
                tpsIntegraFolha = !"".equals(valor) ? Short.valueOf(valor) : CodedValues.INTEGRA_FOLHA_SIM;

            } else if (CodedValues.TPS_INCIDE_MARGEM.equals(tpsCodigo)) {
                tpsIncideMargem = !"".equals(valor) ? Short.valueOf(valor) : CodedValues.INCIDE_MARGEM_SIM;

            } else if (CodedValues.TPS_TIPO_VLR.equals(tpsCodigo)) {
                tpsTipoVlr = !"".equals(valor) ? valor : CodedValues.TIPO_VLR_FIXO;

            } else if (CodedValues.TPS_ADE_VLR.equals(tpsCodigo)) {
                tpsAdeVlr = valor;

            } else if (CodedValues.TPS_ALTERA_ADE_VLR.equals(tpsCodigo)) {
                tpsAlteraAdeVlr = !"0".equals(valor);

            } else if (CodedValues.TPS_MAX_PRAZO.equals(tpsCodigo)) {
                tpsMaxPrazo = valor;

            } else if (CodedValues.TPS_MAX_PRAZO_RELATIVO_AOS_RESTANTES.equals(tpsCodigo)) {
                tpsMaxPrazoRelativoAosRestantes = "1".equals(valor);

            } else if (CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE.equals(tpsCodigo)) {
                tpsMaxPrazoRenegociacao = valor;

            } else if (CodedValues.TPS_REQUER_DEFERIMENTO_RESERVAS.equals(tpsCodigo)) {
                tpsRequerDeferimentoReservas = "1".equals(valor);

            } else if (CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF.equals(tpsCodigo)) {
                tpsDiasDesblResNaoConf = valor;

            } else if (CodedValues.TPS_DIAS_DESBL_CONSIG_NAO_DEF.equals(tpsCodigo)) {
                tpsDiasDesblConsigNaoDef = valor;

            } else if (CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF.equals(tpsCodigo)) {
                tpsDiasDesblSolicitacaoNaoConf = valor;

            } else if (CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA.equals(tpsCodigo)) {
                tpsSerSenhaObrigatoriaCsa = (valor == null) || !CodedValues.PSE_SER_SENHA_OPCIONAL.equals(valor);

            } else if (CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSE.equals(tpsCodigo)) {
                tpsSerSenhaObrigatoriaCse = (valor == null) || !CodedValues.PSE_SER_SENHA_OPCIONAL.equals(valor);

            } else if (CodedValues.TPS_SER_SENHA_OBRIGATORIA_SER.equals(tpsCodigo)) {
                // Para servidor, o padrão é que a senha seja opcional
                tpsSerSenhaObrigatoriaSer = (valor != null) && !CodedValues.PSE_SER_SENHA_OPCIONAL.equals(valor);

            } else if (CodedValues.TPS_OP_FINANCIADA.equals(tpsCodigo)) {
                tpsOpFinanciada = valor;

            } else if (CodedValues.TPS_CARENCIA_MINIMA.equals(tpsCodigo)) {
                tpsCarenciaMinima = valor;

            } else if (CodedValues.TPS_CARENCIA_MAXIMA.equals(tpsCodigo)) {
                tpsCarenciaMaxima = valor;

            } else if (CodedValues.TPS_VLR_LIBERADO_MINIMO.equals(tpsCodigo)) {
                tpsVlrLiberadoMinimo = valor;

            } else if (CodedValues.TPS_VLR_LIBERADO_MAXIMO.equals(tpsCodigo)) {
                tpsVlrLiberadoMaximo = valor;

            } else if (CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO.equals(tpsCodigo)) {
                tpsNumContratosPorConvenio = valor;

            } else if (CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO.equals(tpsCodigo)) {
                tpsNumContratosPorServico = valor;

            } else if (CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM.equals(tpsCodigo)) {
                tpsVlrLimiteAdeSemMargem = valor;

            } else if (CodedValues.TPS_PERMITE_IMPORTACAO_LOTE.equals(tpsCodigo)) {
                tpsPermiteImportacaoLote = "1".equals(valor);

            } else if (CodedValues.TPS_PERMITE_AUM_VLR_PRZ_CONSIGNACAO.equals(tpsCodigo)) {
                tpsPermiteAumVlrPrzConsignacao = !TextHelper.isNull(valor) ? valor : CodedValues.NAO_PERMITE_AUMENTAR_VLR_PRZ_CONTRATO;

            } else if (CodedValues.TPS_REIMPLANTACAO_AUTOMATICA.equals(tpsCodigo)) {
                tpsReimplantacaoAutomatica = "".equals(valor) ? tpcDefaultReimplante : "S".equals(valor);

            } else if (CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL.equals(tpsCodigo)) {
                tpsPreservaPrdRejeitadaReimpl = "".equals(valor) ? tpcDefaultPreservacao : "S".equals(valor);

            } else if (CodedValues.TPS_INDICE.equals(tpsCodigo)) {
                tpsIndice = valor;

            } else if (CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM_ALTER.equals(tpsCodigo)) {
                tpsVlrLimiteAdeSemMargemAlter = valor;

            } else if (CodedValues.TPS_CARENCIA_FINAL.equals(tpsCodigo)) {
                tpsCarenciaFinal = valor;

            } else if (CodedValues.TPS_PRAZO_CARENCIA_FINAL.equals(tpsCodigo)) {
                tpsPrazoCarenciaFinal = valor;

            } else if (CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA.equals(tpsCodigo)) {
                tpsPermiteRepetirIndiceCsa = !"N".equals(valor);

            } else if (CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_ALONGAMENTO.equals(tpsCodigo)) {
                tpsVlrPercMaximoParcelaAlongamento = valor;

            } else if (CodedValues.TPS_VLR_LIQ_TAXA_JUROS.equals(tpsCodigo)) {
                tpsVlrLiqTaxaJuros = "1".equals(valor);

            } else if (CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA.equals(tpsCodigo)) {
                tpsPermiteContratoSuperSerCsa = "S".equals(valor);

            } else if (CodedValues.TPS_PRESERVA_DATA_RENEGOCIACAO.equals(tpsCodigo)) {
                tpsPreservaDataRenegociacao = "1".equals(valor);

            } else if (CodedValues.TPS_PRESERVA_DATA_MAIS_ANTIGA_RENEG.equals(tpsCodigo)) {
                tpsPreservaDataMaisAntigaReneg = "1".equals(valor);

            } else if (CodedValues.TPS_DIAS_DESBL_COMP_NAO_CONF.equals(tpsCodigo)) {
                tpsDiasDesblCompNaoConf = valor;

            } else if (CodedValues.TPS_PRAZO_FIXO.equals(tpsCodigo)) {
                tpsPrazoFixo = "1".equals(valor);

            } else if (CodedValues.TPS_INF_BANCARIA_OBRIGATORIA.equals(tpsCodigo)) {
                tpsInfBancariaObrigatoria = "1".equals(valor);

            } else if (CodedValues.TPS_DATA_LIMITE_DIGIT_TAXA.equals(tpsCodigo)) {
                tpsDataLimiteDigitTaxa = valor;

            } else if (CodedValues.TPS_DATA_ABERTURA_TAXA.equals(tpsCodigo)) {
                tpsDataAberturaTaxa = valor;
                tpsDataAberturaTaxaRef = valorRef;

            } else if (CodedValues.TPS_INCLUI_ALTERANDO_MESMO_PERIODO.equals(tpsCodigo)) {
                tpsIncluiAlterandoMesmoPeriodo = "1".equals(valor);

            } else if (CodedValues.TPS_VALIDAR_TAXA_JUROS.equals(tpsCodigo)) {
                tpsValidarTaxaJuros = "1".equals(valor);

            } else if (CodedValues.TPS_CONCLUI_ADE_NAO_PAGA.equals(tpsCodigo)) {
                tpsConcluiAdeNaoPaga = "".equals(valor) ? tpcDefaultConclusao : "S".equals(valor);

            } else if (CodedValues.TPS_CONTROLA_SALDO.equals(tpsCodigo)) {
                tpsControlaSaldo = "1".equals(valor);

            } else if (CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO.equals(tpsCodigo)) {
                tpsControlaVlrMaxDesconto = !"".equals(valor) && !"0".equals(valor);

            } else if (CodedValues.TPS_EXIBE_CONTRATO_SERVIDOR.equals(tpsCodigo)) {
                tpsNaoExibeContratoServidor = CodedValues.PSE_NAO_EXIBIR_CONTRATOS_SERVIDOR.equals(valor);

            } else if (CodedValues.TPS_EXIBE_CAPITAL_DEVIDO.equals(tpsCodigo)) {
                tpsExibeCapitalDevido = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_SEGURO_PRESTAMISTA.equals(tpsCodigo)) {
                tpsExigeSeguroPrestamista = !temCET && "1".equals(valor);

            } else if (CodedValues.TPS_PERMITE_ALTERACAO_CONTRATOS.equals(tpsCodigo)) {
                tpsPermiteAlteracaoContratos = !"0".equals(valor);

            } else if (CodedValues.TPS_PERMITE_RENEGOCIACAO.equals(tpsCodigo)) {
                tpsPermiteRenegociacao = !"0".equals(valor);

            } else if (CodedValues.TPS_QTDE_MAX_ADE_RENEGOCIACAO.equals(tpsCodigo)) {
                tpsQtdeMaxAdeRenegociacao = valor;

            } else if (CodedValues.TPS_QTDE_MAX_ADE_COMPRA.equals(tpsCodigo)) {
                tpsQtdeMaxAdeCompra = valor;

            } else if (CodedValues.TPS_EXIGE_SENHA_ALTERACAO_CONTRATOS.equals(tpsCodigo)) {
                tpsExigeSenhaAlteracaoContratos = !TextHelper.isNull(valor) ? valor : CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS;

            } else if (CodedValues.TPS_PERMITE_LIQUIDAR_PARCELA.equals(tpsCodigo)) {
                tpsPermiteLiquidarParcela = !"0".equals(valor);

            } else if (CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsBancoDepositoSaldoDevedor = valor;

            } else if (CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsAgenciaDepositoSaldoDevedor = valor;

            } else if (CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsContaDepositoSaldoDevedor = valor;

            } else if (CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS.equals(tpsCodigo)) {
                tpsEmailInfContratosComprados = valor;

            } else if (CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsEmailInfSaldoDevedor = valor;

            } else if (CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsEmailInfPgtSaldoDevedor = valor;

            } else if (CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO.equals(tpsCodigo)) {
                tpsEmailInfLiqContratoComprado = valor;

            } else if (CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV.equals(tpsCodigo)) {
                tpsNomeFavorecidoDepositoSdv = valor;

            } else if (CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV.equals(tpsCodigo)) {
                tpsCnpjFavorecidoDepositoSdv = valor;

            } else if (CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsPossuiCorrecaoSaldoDevedor = valor;

            } else if (CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV.equals(tpsCodigo)) {
                tpsFormaCalculoCorrecaoSaldoDv = valor;

            } else if (CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV.equals(tpsCodigo)) {
                tpsCorrecaoSobreTotalSaldoDv = "1".equals(valor);

            } else if (CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL.equals(tpsCodigo)) {
                tpsCorrecaoEnviadaAposPrincipal = "1".equals(valor);

            } else if (CodedValues.TPS_ADD_VALOR_TAC_VAL_TAXA_JUROS.equals(tpsCodigo)) {
                tpsAddValorTacValTaxaJuros = "1".equals(valor);

            } else if (CodedValues.TPS_ADD_VALOR_IOF_VAL_TAXA_JUROS.equals(tpsCodigo)) {
                tpsAddValorIofValTaxaJuros = "1".equals(valor);

            } else if (CodedValues.TPS_VALIDA_MENS_VINC_VAL_TAXA_JUROS.equals(tpsCodigo)) {
                tpsValidaMensVincValTaxaJuros = "1".equals(valor);

            } else if (CodedValues.TPS_TIPO_TAC.equals(tpsCodigo)) {
                tpsTipoTac = valor;

            } else if (CodedValues.TPS_VALOR_MIN_TAC.equals(tpsCodigo)) {
                tpsValorMinTac = valor;

            } else if (CodedValues.TPS_VALOR_MAX_TAC.equals(tpsCodigo)) {
                tpsValorMaxTac = valor;

            } else if (CodedValues.TPS_EXCEDENTE_MONETARIO_TX_JUROS.equals(tpsCodigo)) {
                tpsExcedenteMonetarioTxJuros = valor;

            } else if (CodedValues.TPS_PRESERVA_DATA_ALTERACAO.equals(tpsCodigo)) {
                tpsPreservaDataAlteracao = !"0".equals(valor);

            } else if (CodedValues.TPS_SERVICO_COMPULSORIO.equals(tpsCodigo)) {
                tpsServicoCompulsorio = "1".equals(valor);

            } else if (CodedValues.TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO.equals(tpsCodigo)) {
                tpsRetiravelporSvcCompPrioritario = !"0".equals(valor);

            } else if (CodedValues.TPS_IMPORTA_CONTRATOS_SEM_PROCESSAMENTO.equals(tpsCodigo)) {
                tpsImportaContratosSemProcessamento = !CodedValues.TPC_NAO.equals(valor);

            } else if (CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE.equals(tpsCodigo)) {
                tpsPossuiCorrecaoValorPresente = "1".equals(valor);

            } else if (CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE.equals(tpsCodigo)) {
                tpsFormaCalculoCorrecaoVlrPresente = valor;

            } else if (CodedValues.TPS_POSSUI_CONTROLE_TETO_DESCONTO.equals(tpsCodigo)) {
                tpsPossuiControleTetoDesconto = valor;

            } else if (CodedValues.TPS_PERCENTUAL_MAXIMO_PERMITIDO_VLR_REF.equals(tpsCodigo)) {
                tpsPercentualMaximoPermitidoVlrRef = valor;

            } else if (CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC.equals(tpsCodigo)) {
                tpsQuantidadeMaximaContratosSvc = valor;

            } else if (CodedValues.TPS_CALCULO_VALOR_ACUMULADO.equals(tpsCodigo)) {
                tpsCalculoValorAcumulado = "1".equals(valor);

            } else if (CodedValues.TPS_BUSCA_BOLETO_EXTERNO.equals(tpsCodigo)) {
                tpsBuscaBoletoExterno = "1".equals(valor);

            } else if (CodedValues.TPS_EXIBE_BOLETO.equals(tpsCodigo)) {
                tpsExibeBoleto = "1".equals(valor);

            } else if (CodedValues.TPS_CAD_VALOR_TAC.equals(tpsCodigo)) {
                tpsCadValorTac = !temCET && "1".equals(valor);

            } else if (CodedValues.TPS_CAD_VALOR_IOF.equals(tpsCodigo)) {
                tpsCadValorIof = !temCET && "1".equals(valor);

            } else if (CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO.equals(tpsCodigo)) {
                tpsCadValorLiquidoLiberado = "1".equals(valor);

            } else if (CodedValues.TPS_CAD_VALOR_MENSALIDADE_VINC.equals(tpsCodigo)) {
                tpsCadValorMensalidadeVinc = !temCET && "1".equals(valor);

            } else if (CodedValues.TPS_CNV_PODE_DEFERIR.equals(tpsCodigo)) {
                tpsCnvPodeDeferir = !"N".equals(valor);

            } else if (CodedValues.TPS_CALC_TAC_IOF_VALIDA_TAXA_JUROS.equals(tpsCodigo)) {
                tpsCalcTacIofValidaTaxaJuros = !temCET && "1".equals(valor);

            } else if (CodedValues.TPS_PER_RESTRICAO_CAD_NOVA_ADE_CNV_RSE.equals(tpsCodigo)) {
                tpsPerRestricaoCadNovaAdeCnvRse = valor;

            } else if (CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA.equals(tpsCodigo)) {
                tpsSomaIofSimulacaoReserva = !temCET && !"".equals(valor) ? "S".equals(valor) : null;

            } else if (CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA.equals(tpsCodigo)) {
                tpsValidarDataNascimentoNaReserva = "1".equals(valor);

            } else if (CodedValues.TPS_VALIDAR_INF_BANCARIA_NA_RESERVA.equals(tpsCodigo)) {
                tpsValidarInfBancariaNaReserva = "1".equals(valor);

            } else if (CodedValues.TPS_VLR_MINIMO_CONTRATO.equals(tpsCodigo)) {
                tpsVlrMinimoContrato = valor;

            } else if (CodedValues.TPS_VLR_MAXIMO_CONTRATO.equals(tpsCodigo)) {
                tpsVlrMaximoContrato = valor;

            } else if (CodedValues.TPS_SERVICO_TIPO_GAP.equals(tpsCodigo)) {
                tpsServicoTipoGap = "1".equals(valor);

            } else if (CodedValues.TPS_MES_INICIO_DESCONTO_GAP.equals(tpsCodigo)) {
                tpsMesInicioDescontoGap = valor;

            } else if (CodedValues.TPS_PERMITE_CANCELAR_CONTRATOS.equals(tpsCodigo)) {
                tpsPermiteCancelarContratos = !"0".equals(valor);

            } else if (CodedValues.TPS_PERMITE_LIQUIDAR_CONTRATOS.equals(tpsCodigo)) {
                tpsPermiteLiquidarContratos = !"0".equals(valor);

            } else if (CodedValues.TPS_PERMITE_SERVIDOR_SOLICITAR.equals(tpsCodigo)) {
                tpsPermiteServidorSolicitar = "1".equals(valor);

            } else if (CodedValues.TPS_CLASSE_JAVA_PROC_ESPECIFICO_RESERVA.equals(tpsCodigo)) {
                tpsClasseJavaProcEspecificoReserva = !"".equals(valor) ? valor : null;

            } else if (CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsPermiteCadastrarSaldoDevedor = !"".equals(valor) ? valor : CodedValues.NAO_POSSUI_CADASTRO_SALDO_DEVEDOR;

            } else if (CodedValues.TPS_SERVIDOR_LIQUIDA_CONTRATO.equals(tpsCodigo)) {
                tpsServidorLiquidaContrato = "1".equals(valor);

            } else if (CodedValues.TPS_DEFERE_AUT_CONTRATO_INCLUIDO_SER.equals(tpsCodigo)) {
                tpsDeferimentoAutoSolicitacaoServidor = "1".equals(valor);

            } else if (CodedValues.TPS_INCLUI_ALTERANDO_QUALQUER_PERIODO.equals(tpsCodigo)) {
                tpsIncluiAlterandoQualquerPeriodo = "1".equals(valor);

            } else if (CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR.equals(tpsCodigo)) {
                tpsMsgExibirSolicitacaoServidor = valor;

            } else if (CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC.equals(tpsCodigo)) {
                tpsMsgExibirSolicitacaoServidorOfertaOutroSvc = valor;

            } else if (CodedValues.TPS_ATUALIZA_ADE_VLR_NO_RETORNO.equals(tpsCodigo)) {
                tpsAtualizaAdeVlrNoRetorno = "1".equals(valor);

            } else if (CodedValues.TPS_CALCULA_SALDO_SOMENTE_VINCENDO.equals(tpsCodigo)) {
                tpsCalculaSaldoSomenteVincendo = valor;

            } else if (CodedValues.TPS_QTD_CSA_PERMITIDAS_SIMULADOR.equals(tpsCodigo)) {
                tpsQtdCsaPermitidasSimulador = !"".equals(valor) ? Integer.parseInt(valor) : Integer.MAX_VALUE;

            } else if (CodedValues.TPS_BLOQUEIA_RESERVA_LIMITE_SIMULADOR.equals(tpsCodigo)) {
                tpsBloqueiaReservaLimiteSimulador = "1".equals(valor);

            } else if (CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE.equals(tpsCodigo)) {
                tpsLimiteAumentoValorAde = valor;

            } else if (CodedValues.TPS_PODE_INCLUIR_NOVOS_CONTRATOS.equals(tpsCodigo)) {
                tpsPodeIncluirNovosContratos = !"0".equals(valor);

            } else if (CodedValues.TPS_PERMITE_DATA_RETROATIVA_IMP_LOTE.equals(tpsCodigo)) {
                tpsPermiteDataRetroativaImpLote = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_SENHA_CONFIRMACAO_SOLICITACAO.equals(tpsCodigo)) {
                tpsExigeSenhaConfirmacaoSolicitacao = "1".equals(valor);

            } else if (CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING.equals(tpsCodigo)) {
                tpsCsaDeveContarParaLimiteRanking = !"0".equals(valor);

            } else if (CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsEmailInfSolicitacaoSaldoDevedor = valor;

            } else if (CodedValues.TPS_EXIGE_ACEITE_TERMO_ADESAO.equals(tpsCodigo)) {
                tpsExigeAceiteTermoAdesao = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_TERMO_DE_ADESAO_RESERVAR_MARGEM_ANTES_DE_VALORES_OPERACAO.equals(tpsCodigo)) {
                tpsExigeAceiteTermoAdesaoAntesValores = "1".equals(valor);

            } else if (CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CADASTRADO.equals(tpsCodigo)) {
                tpsLimitaSaldoDevedorCadastrado = "1".equals(valor);

            } else if (CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsMargemErroLimiteSaldoDevedor = valor;
                tpsMargemErroLimiteSaldoDevedorRef = valorRef;

            } else if (CodedValues.TPS_ORDENACAO_CADASTRO_TAXAS.equals(tpsCodigo)) {
                tpsOrdenacaoCadastroTaxas = !"".equals(valor) ? valor : CodedValues.ORDEM_TAXAS_NA;

            } else if (CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA.equals(tpsCodigo)) {
                tpsDiasInfSaldoDvControleCompra = valor;

            } else if (CodedValues.TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA.equals(tpsCodigo)) {
                tpsDiasInfPgtSaldoControleCompra = valor;

            } else if (CodedValues.TPS_DIAS_LIQUIDACAO_ADE_CONTROLE_COMPRA.equals(tpsCodigo)) {
                tpsDiasLiquidacaoAdeControleCompra = valor;

            } else if (CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV.equals(tpsCodigo)) {
                tpsAcaoParaNaoInfSaldoDv = !"".equals(valor) ? valor : "0";

            } else if (CodedValues.TPS_ACAO_PARA_NAO_INF_PGT_SALDO.equals(tpsCodigo)) {
                tpsAcaoParaNaoInfPgtSaldo = !"".equals(valor) ? valor : "0";

            } else if (CodedValues.TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE.equals(tpsCodigo)) {
                tpsAcaoParaNaoLiquidacaoAde = !"".equals(valor) ? valor : "0";

            } else if (CodedValues.TPS_MINIMO_PRD_PAGAS_RENEGOCIACAO.equals(tpsCodigo)) {
                tpsMinimoPrdPagasRenegociacao = valor;

            } else if (CodedValues.TPS_CSA_LIMITE_SUPERIOR_TABELA_JUROS_CET.equals(tpsCodigo)) {
                tpsCsaLimiteSuperiorTabelaJurosCet = valor;

            } else if (CodedValues.TPS_DIAS_VIGENCIA_TAXAS_SUPERIOR_LIMITE.equals(tpsCodigo)) {
                tpsDiasVigenciaTaxasSuperiorLimite = valor;

            } else if (CodedValues.TPS_NUMERAR_CONTRATOS_SERVIDOR.equals(tpsCodigo)) {
                tpsNumerarContratosServidor = "1".equals(valor);

            } else if (CodedValues.TPS_VLR_MAX_RENEG_IGUAL_SOMA_CONTRATOS.equals(tpsCodigo)) {
                tpsVlrMaxRenegIgualSomaContratos = !"0".equals(valor);

            } else if (CodedValues.TPS_VLR_MAX_COMPRA_IGUAL_SOMA_CONTRATOS.equals(tpsCodigo)) {
                tpsVlrMaxCompraIgualSomaContratos = !"0".equals(valor);

            } else if (CodedValues.TPS_CLASSE_GERENCIADOR_AUTORIZACAO.equals(tpsCodigo)) {
                tpsClasseGerenciadorAutorizacao = valor;

            } else if (CodedValues.TPS_EXIBE_RANKING_CONFIRMACAO_RESERVA.equals(tpsCodigo)) {
                tpsExibeRankingConfirmacaoReserva = !"0".equals(valor);

            } else if (CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG.equals(tpsCodigo)) {
                tpsPercentualMinimoPrdPagasReneg = valor;

            } else if (CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_RENEG.equals(tpsCodigo)) {
                tpsPercentualMinimoVigenciaReneg = valor;

            } else if (CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA.equals(tpsCodigo)) {
                tpsMinimoPrdPagasCompra = valor;

            } else if (CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_COMPRA.equals(tpsCodigo)) {
                tpsPercentualMinimoPrdPagasCompra = valor;

            } else if (CodedValues.TPS_MINIMO_VIGENCIA_COMPRA.equals(tpsCodigo)) {
                tpsMinimoVigenciaCompra = valor;

            } else if (CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA.equals(tpsCodigo)) {
                tpsPercentualMinimoVigenciaCompra = valor;

            } else if (CodedValues.TPS_MINIMO_VIGENCIA_RENEG.equals(tpsCodigo)) {
                tpsMinimoVigenciaReneg = valor;

            } else if (CodedValues.TPS_USA_CAPITAL_DEVIDO_BASE_LIMITE_SALDO.equals(tpsCodigo)) {
                tpsUsaCapitalDevidoBaseLimiteSaldo = "1".equals(valor);

            } else if (CodedValues.TPS_PERMITE_SALDO_FORA_FAIXA_LIMITE.equals(tpsCodigo)) {
                tpsPermiteSaldoForaFaixaLimite = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_NUMERO_CONTRATO_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsExigeNroContratoInfSaldoDevedorCompra = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_NRO_CONTRATO_INF_SALDO_SOLIC.equals(tpsCodigo)) {
                tpsExigeNroContratoInfSaldoDevedorSolicSaldo = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_CODIGO_AUTORIZACAO_CONF_SOLIC.equals(tpsCodigo)) {
                tpsExigeCodAutorizacaoSolic = "1".equals(valor);

            } else if (CodedValues.TPS_LIMITA_SALDO_DEVEDOR_ANTERIOR_RENEG.equals(tpsCodigo)) {
                tpsLimitaSaldoDevedorAnteriorReneg = "1".equals(valor);

            } else if (CodedValues.TPS_LIMITA_SALDO_DEVEDOR_ANTERIOR_COMPRA.equals(tpsCodigo)) {
                tpsLimitaSaldoDevedorAnteriorCompra = "1".equals(valor);

            } else if (CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_RENEG.equals(tpsCodigo)) {
                tpsMargemErroLimiteSaldoAntReneg = valor;
                tpsMargemErroLimiteSaldoAntRenegRef = valorRef;

            } else if (CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_COMPRA.equals(tpsCodigo)) {
                tpsMargemErroLimiteSaldoAntCompra = valor;
                tpsMargemErroLimiteSaldoAntCompraRef = valorRef;

            } else if (CodedValues.TPS_PRAZO_DIAS_CANCELAMENTO_RENEGOCIACAO.equals(tpsCodigo)) {
                tpsPrazoDiasCancelamentoRenegociacao = valor;

            } else if (CodedValues.TPS_EXIGE_SENHA_SER_CANCEL_RENEGOCIACAO.equals(tpsCodigo)) {
                tpsExigeSenhaSerCancelRenegociacao = "1".equals(valor);

            } else if (CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR.equals(tpsCodigo)) {
                tpsPercMargemSimulador = valor;

            } else if (CodedValues.TPS_MASCARA_IDENTIFICADOR_ADE.equals(tpsCodigo)) {
                tpsMascaraIdentificadorAde = valor;

            } else if (CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO.equals(tpsCodigo)) {
                tpsLimitaCapitalDevidoABaseCalculo = valor;

            } else if (CodedValues.TPS_DIAS_APR_SALDO_DV_CONTROLE_COMPRA.equals(tpsCodigo)) {
                tpsDiasAprSaldoDvControleCompra = valor;

            } else if (CodedValues.TPS_ACAO_PARA_NAO_APR_SALDO_DV.equals(tpsCodigo)) {
                tpsAcaoParaNaoAprSaldoDv = !"".equals(valor) ? valor : "0";

            } else if (CodedValues.TPS_REJEICAO_PGT_SDV_BLOQUEIA_AMBAS_CSAS.equals(tpsCodigo)) {
                tpsRejeicaoPgtSdvBloqueiaAmbasCsas = "1".equals(valor);

            } else if (CodedValues.TPS_EXIBE_INF_BANCARIA_SERVIDOR.equals(tpsCodigo)) {
                tpsExibeInfBancariaServidor = "1".equals(valor);

            } else if (CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR.equals(tpsCodigo)) {
                tpsEmailInfAprovacaoSaldoDevedor = valor;

            } else if (CodedValues.TPS_NUM_ADE_HIST_LIQUIDACOES_ANTECIPADAS.equals(tpsCodigo)) {
                tpsNumAdeHistLiquidacoesAntecipadas = valor;

            } else if (CodedValues.TPS_INCLUI_ALTERANDO_SOMENTE_MESMO_PRAZO.equals(tpsCodigo)) {
                tpsIncluiAlterandoSomenteMesmoPrazo = "1".equals(valor);

            } else if (CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO.equals(tpsCodigo)) {
                tpsIdadeMinimaSerSolicSimulacao = valor;
                tpsIdadeMaximaSerSolicSimulacao = valorRef;

            } else if (CodedValues.TPS_PERMITE_ALTERAR_VLR_LIBERADO.equals(tpsCodigo)) {
                tpsPermiteAlterarVlrLiberado = !"0".equals(valor);

            } else if (CodedValues.TPS_PERMITE_ALTERAR_ADE_COM_BLOQUEIO.equals(tpsCodigo)) {
                tpsPermiteAlterarAdeComBloqueio = !CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_COM_BLOQUEIO.equals(valor);

            } else if (CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL.equals(tpsCodigo)) {
                tpsRetemMargemSvcPercentual = "1".equals(valor);

            } else if (CodedValues.TPS_NUM_ADE_HIST_SUSPENSOES.equals(tpsCodigo)) {
                tpsNumAdeHistSuspensoes = valor;

            } else if (CodedValues.TPS_QTE_DIAS_BLOQUEAR_RESERVA_APOS_LIQ.equals(tpsCodigo)){
                tpsQteDiasBloquearReservaAposLiq = !"".equals(valor) ? valor : "0";

            } else if (CodedValues.TPS_DIAS_DEFER_AUT_CONSIG_NAO_DEFERIDAS.equals(tpsCodigo)) {
                tpsDiasDeferAutConsigNaoDeferidas = valor;

            } else if (CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSE_ORG_SUP.equals(tpsCodigo)) {
                tpsAnexoInclusaoContratosObrigatorioCseOrgSup = "1".equals(valor) && responsavel.isCseSupOrg();

            } else if (CodedValues.TPS_PERMITE_SOLICITAR_SALDO_BENEFICIARIO.equals(tpsCodigo)) {
                tpsPermiteSolicitarSaldoBeneficiario = "1".equals(valor);

            } else if (CodedValues.TPS_PERCENTUAL_MINIMO_DESCONTO_VLR_SALDO.equals(tpsCodigo)) {
                tpsPercentualMinimoDescontoVlrSaldo = valor;

            } else if (CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO.equals(tpsCodigo)) {
                tpsQtdPropostasPagamentoParcelSaldo = valor;
                tpsQtdPropostasPagamentoParcelSaldoRef = valorRef;

            } else if (CodedValues.TPS_DIAS_VALIDADE_PROPOSTAS_PGTO_SALDO.equals(tpsCodigo)) {
                tpsDiasValidadePropostasPgtoSaldo = valor;

            } else if (CodedValues.TPS_DIAS_INF_PROPOSTAS_PGTO_SALDO_TERCEI.equals(tpsCodigo)) {
                tpsDiasInfPropostasPgtoSaldoTercei = valor;

            } else if (CodedValues.TPS_PERCENTUAL_MINIMO_MANTER_VALOR_RENEG.equals(tpsCodigo)) {
                tpsPercentualMinimoManterValorReneg = valor;

            } else if (CodedValues.TPS_DIAS_SOLICITAR_PROPOSTAS_PGTO_SALDO.equals(tpsCodigo)) {
                tpsDiasSolicitarPropostasPgtoSaldo = valor;

            } else if (CodedValues.TPS_BASE_CALC_RETENCAO_SVC_PERCENTUAL.equals(tpsCodigo)) {
                tpsBaseCalcRetencaoSvcPercentual = valor;

            } else if (CodedValues.TPS_PRZ_MAX_RENEG_IGUAL_MAIOR_CONTRATOS.equals(tpsCodigo)) {
                tpsPrzMaxRenegIgualMaiorContratos = "1".equals(valor);

            } else if (CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS.equals(tpsCodigo)) {
                tpsPrzMaxCompraIgualMaiorContratos = "1".equals(valor);

            } else if (CodedValues.TPS_QTD_DIAS_BLOQ_CSA_APOS_INF_SALDO_SER.equals(tpsCodigo)) {
                tpsQtdDiasBloqCsaAposInfSaldoSer = valor;

            } else if (CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO.equals(tpsCodigo)) {
                tpsIdentificadorAdeObrigatorio = "1".equals(valor);

            } else if (CodedValues.TPS_PERMITE_LIQUIDAR_ADE_SUSPENSA.equals(tpsCodigo)) {
                tpsPermiteLiquidarAdeSuspensa = "1".equals(valor);

            } else if (CodedValues.TPS_IMPEDIR_LIQUIDACAO_CONSIGNACAO.equals(tpsCodigo)) {
                tpsImpedirLiquidacaoConsignacao = "1".equals(valor);

            } else if (CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO.equals(tpsCodigo)) {
                tpsPermiteContratoValorNegativo = "S".equals(valor);

            } else if (CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CAD_CSE_ORG_SUP.equals(tpsCodigo)) {
                tpsLimitaSaldoDevedorCadastradoCseOrgSup = "1".equals(valor);

            } else if (CodedValues.TPS_PERCENTUAL_BASE_CALC_DESCONTO_EM_FILA.equals(tpsCodigo)) {
                tpsPercentualBaseCalcDescontoEmFila = valor;

            } else if (CodedValues.TPS_BASE_CALC_DESCONTO_EM_FILA.equals(tpsCodigo)) {
                tpsBaseCalcDescontoEmFila = valor;

            } else if (CodedValues.TPS_EXIBE_MSG_RESERVA_MESMA_VERBA_CSA_COR.equals(tpsCodigo)) {
                tpsExibeMsgReservaMesmaVerbaCsaCor = "1".equals(valor);

            } else if (CodedValues.TPS_MSG_EXIBIR_INCLUSAO_ALTERACAO_ADE_CSA.equals(tpsCodigo)) {
                tpsMsgExibirInclusaoAlteracaoAdeCsa = valor;

            } else if (CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA.equals(tpsCodigo)) {
                tpsPercentualMargemFolhaLimiteCsa = valor;

            } else if (CodedValues.TPS_EXIBE_TABELA_PRICE.equals(tpsCodigo)) {
                tpsExibeTabelaPrice = "1".equals(valor);

            } else if (CodedValues.TPS_OP_INSERE_ALTERA_USA_MAIOR_PRAZO.equals(tpsCodigo)) {
                tpsOpInsereAlteraUsaMaiorPrazo = "1".equals(valor);

            } else if (CodedValues.TPS_VISUALIZA_VALOR_LIBERADO_CALC.equals(tpsCodigo)) {
                tpsVisualizaValorLiberadoCalculado = "1".equals(valor);

            } else if (CodedValues.TPS_ALTERA_ADE_COM_MARGEM_NEGATIVA.equals(tpsCodigo)) {
                tpsAlteraAdeComMargemNegativa = valor;

            } else if (CodedValues.TPS_EXIBE_CIDADE_CONFIRMACAO_SOLICITACAO.equals(tpsCodigo)) {
                tpsExibeCidadeConfirmacaoSolicitacao = valor;

            } else if (CodedValues.TPS_EXIGENCIA_CONFIRMACAO_LEITURA_SERVIDOR.equals(tpsCodigo)) {
            	tpsExigenciaConfirmacaoLeituraServidor = "1".equals(valor);

            } else if (CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE.equals(tpsCodigo)) {
                tpsPeriodoLimiteAdeDuplicidade = valor;

            } else if (CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO.equals(tpsCodigo)) { //
                tpsIdadeMaxDependenteEstSubsidio = valor;

            } else if (CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO.equals(tpsCodigo)) {
                tpsIdadeMaxDependenteDireitoSubsidio = valor;

            } else if (CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO.equals(tpsCodigo)) {
                tpsAgregadoPodeTerSubsidio = valor;

            } else if (CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO.equals(tpsCodigo)) {
                tpsPaiMaeTitularesDivorciadosSubsidio = valor;

            } else if (CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO.equals(tpsCodigo)) {
                tpsOrdemPrioridadeSubsidio = valor;

            } else if (CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA.equals(tpsCodigo)) {
                tpsQtdeSubsidioPorNatureza = valor;

            } else if (CodedValues.TPS_TIPO_CALCULO_SUBSIDIO.equals(tpsCodigo)) {
                tpsTipoCalculoSubsidio = valor;

            } else if (CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO.equals(tpsCodigo)) {
                tpsDataLimiteVigenciaPaiMaeTitularesDivorciadosSubsidio = valor;

            } else if (CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO.equals(tpsCodigo)) {
                tpsMascaraNumeroContratoBeneficio = valor;

            } else if (CodedValues.TPS_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM.equals(tpsCodigo)) {
                tpsAtualizaAdeVlrAlteracaoMargem = valor;

            } else if (CodedValues.TPS_PULA_INFORMACAO_VALOR_PRAZO_FLUXO_RESERVA.equals(tpsCodigo)) {
                tpsPulaInformacaoValorPrazoFluxoReserva = "1".equals(valor);

            } else if (CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA.equals(tpsCodigo)) {
                tpsBloqIncAdeMesmaNsePrdRejeitada = "1".equals(valor);

            } else if (CodedValues.TPS_BLOQUEIA_INCLUSAO_ADE_MESMO_PERIODO_NSE_RSE.equals(tpsCodigo)) {
                tpsBloqueiaInclusaoAdeMesmoPeriodoNseRse = "1".equals(valor);

            } else if (CodedValues.TPS_SERVIDOR_ALTERA_CONTRATO.equals(tpsCodigo)) {
                tpsServidorAlteraContrato = "1".equals(valor);

            } else if (CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR.equals(tpsCodigo)) {
                tpsQtdMinAnexosAdeVlr = !TextHelper.isNull(valor) ? Short.parseShort(valor) : null;
                tpsQtdMinAnexosAdeVlrRef = !TextHelper.isNull(valorRef) ? Short.parseShort(valorRef) : null;

            } else if (CodedValues.TPS_SERVIDOR_DEVE_SER_KYC_COMPLIANT.equals(tpsCodigo)) {
                tpsServidorDeveSerKYCComplaint = "1".equals(valor);

            } else if (CodedValues.TPS_TEMPO_LIMITE_PARA_CANCELAMENTO_ULTIMA_ADE_SERVIDOR.equals(tpsCodigo)) {
                tpsTempoLimiteCancelamentoAde = valor;

            } else if (CodedValues.TPS_ANEXO_CONFIRMACAO_RESERVA_OBRIGATORIO.equals(tpsCodigo)) {
                tpsAnexoConfirmarReservaObrigatorio = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_SENHA_SER_SUSPENDER_CONSIGNACAO.equals(tpsCodigo)) {
                tpsExigeSenhaSerSuspenderConsignacao = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_SENHA_SER_CANCELAR_CONSIGNACAO.equals(tpsCodigo)) {
                tpsExigeSenhaSerCancelarConsignacao = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_SENHA_SER_REATIVAR_CONSIGNACAO.equals(tpsCodigo)) {
                tpsExigeSenhaSerReativarConsignacao = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_SENHA_SER_LIQUIDAR_CONSIGNACAO.equals(tpsCodigo)) {
                tpsExigeSenhaSerLiquidarConsignacao = "1".equals(valor);

            } else if (CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO.equals(tpsCodigo)) {
                tpsConfigurarIdPropostaConvenio = valor;

            } else if (CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO.equals(tpsCodigo)) {
                tpsConfigurarDiaVencimentoContrato = valor;

            } else if (CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO.equals(tpsCodigo)) {
                tpsConfigurarPeriodoCompetenciaDebito = valor;

            } else if (CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO.equals(tpsCodigo)) {
                tpsConfigurarNumeroConvenio = valor;

            } else if (CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO.equals(tpsCodigo)) {
                tpsConfigurarCodigoAdesao = valor;

            } else if (CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO.equals(tpsCodigo)) {
                tpsConfigurarIdadeMaximaContratacaoSeguro = valor;

            } else if (CodedValues.TPS_PERMITE_DESCONTO_VIA_BOLETO.equals(tpsCodigo)) {
                tpsPermiteDescontoViaBoleto = !TextHelper.isNull(valor) ? valor : CodedValues.NAO_PERMITE_PAGAMENTO_VIA_BOLETO;

            } else if (CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO.equals(tpsCodigo)) {
                tpsObrigaInformacoesServidorSolicitacao = valor;

            } else if (CodedValues.TPS_DIAS_VIGENCIA_CET.equals(tpsCodigo) && !TextHelper.isNull(valor)) {
                tpsDiasVigenciaCet = Short.valueOf(valor);

            } else if (CodedValues.TPS_OCULTAR_MENU_SERVIDOR.equals(tpsCodigo)) {
                tpsOcultarMenuServidor = "1".equals(valor);

            } else if (CodedValues.TPS_EXIBE_TEXTO_EXPLICATIVO_VALOR_PRESTACAO.equals(tpsCodigo)){
                tpsExibeTxtExplicativoValorPrestacao = valor;

            } else if (CodedValues.TPS_VALOR_SVC_FIXO_POSTO.equals(tpsCodigo)){
                tpsValorSvcFixoPosto = valor;

            } else if (CodedValues.TPS_INSERIR_VIRA_ALTERAR_POR_ADE_INDENTIFICADOR_IGUAL.equals(tpsCodigo)) {
            	tpsInsereAlteraPorAdeIdnIgual = "1".equals(valor);

            } else if (CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM.equals(tpsCodigo)) {
                tpsPrendeMargemLiqAdePrzIndetAteCargaMargem = "1".equals(valor);

            } else if (CodedValues.TPS_EXIGE_RECONHECIMENTO_FACIAL_SERVIDOR_SOLICITACAO.equals(tpsCodigo)) {
                tpsRequerReconhecimentoFacilServidor = ("1".equals(valor));

            } else if (CodedValues.TPS_CONSIDERA_MARGEM_REST_RESERVA_COMPULSORIO.equals(tpsCodigo)) {
                tpsCondiseraMargemRestReservaCompulsorio = ("1".equals(valor));

            } else if (CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSA_COR.equals(tpsCodigo)) {
                tpsAnexoInclusaoContratosObrigatorioCsaOrg = "1".equals(valor) && responsavel.isCsaCor();

            } else if (CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_SER.equals(tpsCodigo)) {
                tpsAnexoInclusaoContratosObrigatorioSer = "1".equals(valor) && responsavel.isSer();

            } else if (CodedValues.TPS_QUANTIDADE_MINIMA_ANEXO_INCLUSAO_CONTRATOS.equals(tpsCodigo)) {
                tpsQuantidadeMinimaAnexoInclusaoContratos = valor;

            } else if (CodedValues.TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO.equals(tpsCodigo)) {
                tpsPermiteIncluirAdeRseBloqueado = ("1".equals(valor));

            } else if (CodedValues.TPS_EXIGE_CADASTRO_TAXA_JUROS_PARA_CET.equals(tpsCodigo)) {
                tpsExigeCadastroTaxaJurosParaCet = ("1".equals(valor));

            } else if (CodedValues.TPS_PARTICIPA_DA_CONTAGEM_DE_INCLUSAO_POR_DIA_CSA.equals(tpsCodigo)) {
                tpsConsideradoInclusaoCsaPorDia = ("1".equals(valor));

            } else if (CodedValues.TPS_PRAZO_ATEND_SOLICIT_SALDO_DEVEDOR.equals(tpsCodigo)) {
                setTpsPrazoAtendSolicitSaldoDevedor(valor);

            }
        }
    }

    private String tpsPercMargemSimulador = null;
    private String tpsTacFinanciada = null;
    private Short tpsIntegraFolha = CodedValues.INTEGRA_FOLHA_SIM;
    private Short tpsIncideMargem = CodedValues.INCIDE_MARGEM_SIM;
    private String tpsTipoVlr = CodedValues.TIPO_VLR_FIXO;
    private String tpsAdeVlr = null;
    private boolean tpsAlteraAdeVlr = true;
    private String tpsMaxPrazo = null;
    private String tpsMaxPrazoRenegociacao = null;
    private boolean tpsRequerDeferimentoReservas = false;
    private String tpsDiasDesblResNaoConf = null;
    private String tpsDiasDesblConsigNaoDef = null;
    private String tpsDiasDesblSolicitacaoNaoConf = null;
    private boolean tpsSerSenhaObrigatoriaCsa = true;
    private boolean tpsSerSenhaObrigatoriaCse = true;
    private boolean tpsSerSenhaObrigatoriaSer = true;
    private String tpsOpFinanciada = null;
    private String tpsCarenciaMinima = null;
    private String tpsCarenciaMaxima = null;
    private String tpsVlrLiberadoMinimo = null;
    private String tpsVlrLiberadoMaximo = null;
    private String tpsNumContratosPorConvenio = null;
    private String tpsNumContratosPorServico = null;
    private String tpsVlrLimiteAdeSemMargem = null;
    private boolean tpsPermiteImportacaoLote = false;
    private String tpsPermiteAumVlrPrzConsignacao = CodedValues.NAO_PERMITE_AUMENTAR_VLR_PRZ_CONTRATO;
    private boolean tpsReimplantacaoAutomatica = false;
    private boolean tpsPreservaPrdRejeitadaReimpl = false;
    private String tpsIndice = null;
    private String tpsVlrLimiteAdeSemMargemAlter = null;
    private String tpsCarenciaFinal = null;
    private String tpsPrazoCarenciaFinal = null;
    private boolean tpsPermiteRepetirIndiceCsa = true;
    private String tpsVlrPercMaximoParcelaAlongamento = "1";
    private boolean tpsVlrLiqTaxaJuros = false;
    private boolean tpsPermiteContratoSuperSerCsa = false;
    private boolean tpsPreservaDataRenegociacao = false;
    private boolean tpsPreservaDataMaisAntigaReneg = false;
    private String tpsDiasDesblCompNaoConf = null;
    private boolean tpsPrazoFixo = false;
    private boolean tpsInfBancariaObrigatoria = false;
    private String tpsDataLimiteDigitTaxa = null;
    private String tpsDataAberturaTaxa = null;
    private String tpsDataAberturaTaxaRef = null;
    private boolean tpsIncluiAlterandoMesmoPeriodo = false;
    private boolean tpsValidarTaxaJuros = false;
    private boolean tpsConcluiAdeNaoPaga = false;
    private boolean tpsControlaSaldo = false;
    private boolean tpsControlaVlrMaxDesconto = false;
    private boolean tpsNaoExibeContratoServidor = false;
    private boolean tpsExibeCapitalDevido = false;
    private boolean tpsExigeSeguroPrestamista = false;
    private boolean tpsPermiteAlteracaoContratos = true;
    private boolean tpsPermiteRenegociacao = true;
    private String tpsQtdeMaxAdeRenegociacao = null;
    private String tpsQtdeMaxAdeCompra = null;
    private String tpsExigeSenhaAlteracaoContratos = CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS;
    private boolean tpsPermiteLiquidarParcela = true;
    private String tpsBancoDepositoSaldoDevedor = null;
    private String tpsAgenciaDepositoSaldoDevedor = null;
    private String tpsContaDepositoSaldoDevedor = null;
    private String tpsEmailInfContratosComprados = null;
    private String tpsEmailInfSaldoDevedor = null;
    private String tpsEmailInfPgtSaldoDevedor = null;
    private String tpsEmailInfLiqContratoComprado = null;
    private String tpsNomeFavorecidoDepositoSdv = null;
    private String tpsCnpjFavorecidoDepositoSdv = null;
    private String tpsPossuiCorrecaoSaldoDevedor = CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR;
    private String tpsFormaCalculoCorrecaoSaldoDv = null;
    private boolean tpsCorrecaoSobreTotalSaldoDv = false;
    private boolean tpsCorrecaoEnviadaAposPrincipal = false;
    private boolean tpsAddValorTacValTaxaJuros = false;
    private boolean tpsAddValorIofValTaxaJuros = false;
    private boolean tpsValidaMensVincValTaxaJuros = false;
    private String tpsTipoTac = CodedValues.TIPO_VLR_FIXO;
    private String tpsValorMinTac = null;
    private String tpsValorMaxTac = null;
    private String tpsExcedenteMonetarioTxJuros = null;
    private boolean tpsPreservaDataAlteracao = true;
    private boolean tpsServicoCompulsorio = false;
    private boolean tpsRetiravelporSvcCompPrioritario = true;
    private boolean tpsImportaContratosSemProcessamento = true;
    private boolean tpsPossuiCorrecaoValorPresente = false;
    private String tpsFormaCalculoCorrecaoVlrPresente = null;
    private String tpsPossuiControleTetoDesconto = null;
    private String tpsPercentualMaximoPermitidoVlrRef = null;
    private String tpsQuantidadeMaximaContratosSvc = null;
    private boolean tpsCalculoValorAcumulado = false;
    private boolean tpsBuscaBoletoExterno = false;
    private boolean tpsExibeBoleto = false;
    private boolean tpsCadValorTac = false;
    private boolean tpsCadValorIof = false;
    private boolean tpsCadValorLiquidoLiberado = false;
    private boolean tpsCadValorMensalidadeVinc = false;
    private boolean tpsCnvPodeDeferir = true;
    private boolean tpsCalcTacIofValidaTaxaJuros = false;
    private String tpsPerRestricaoCadNovaAdeCnvRse = null;
    private Boolean tpsSomaIofSimulacaoReserva = null;
    private boolean tpsValidarDataNascimentoNaReserva = false;
    private boolean tpsValidarInfBancariaNaReserva = false;
    private String tpsVlrMinimoContrato = null;
    private String tpsVlrMaximoContrato = null;
    private boolean tpsServicoTipoGap = false;
    private String tpsMesInicioDescontoGap = null;
    private boolean tpsPermiteCancelarContratos = true;
    private boolean tpsPermiteLiquidarContratos = true;
    private boolean tpsPermiteServidorSolicitar = false;
    private String tpsClasseJavaProcEspecificoReserva = null;
    private String tpsPermiteCadastrarSaldoDevedor = CodedValues.NAO_POSSUI_CADASTRO_SALDO_DEVEDOR;
    private boolean tpsIncluiAlterandoQualquerPeriodo = false;
    private String tpsMsgExibirSolicitacaoServidor = null;
    private String tpsMsgExibirSolicitacaoServidorOfertaOutroSvc = null;
    private boolean tpsAtualizaAdeVlrNoRetorno = false;
    private String tpsCalculaSaldoSomenteVincendo = CodedValues.CALCULA_SALDO_TUDO_EM_ABERTO;
    private int tpsQtdCsaPermitidasSimulador = Integer.MAX_VALUE;
    private boolean tpsBloqueiaReservaLimiteSimulador = false;
    private String tpsLimiteAumentoValorAde = null;
    private boolean tpsPodeIncluirNovosContratos = true;
    private boolean tpsPermiteDataRetroativaImpLote = false;
    private boolean tpsExigeSenhaConfirmacaoSolicitacao = false;
    private boolean tpsCsaDeveContarParaLimiteRanking = true;
    private String tpsEmailInfSolicitacaoSaldoDevedor = null;
    private boolean tpsExigeAceiteTermoAdesao = false;
    private boolean tpsExigeAceiteTermoAdesaoAntesValores = false;
    private boolean tpsLimitaSaldoDevedorCadastrado = false;
    private String tpsMargemErroLimiteSaldoDevedor = null;
    private String tpsMargemErroLimiteSaldoDevedorRef = null;
    private String tpsOrdenacaoCadastroTaxas = CodedValues.ORDEM_TAXAS_NA;
    private String tpsDiasInfSaldoDvControleCompra = null;
    private String tpsDiasInfPgtSaldoControleCompra = null;
    private String tpsDiasLiquidacaoAdeControleCompra = null;
    private String tpsAcaoParaNaoInfSaldoDv = "0";
    private String tpsAcaoParaNaoInfPgtSaldo = "0";
    private String tpsAcaoParaNaoLiquidacaoAde = "0";
    private String tpsMinimoPrdPagasRenegociacao = null;
    private String tpsCsaLimiteSuperiorTabelaJurosCet = null;
    private String tpsDiasVigenciaTaxasSuperiorLimite = null;
    private boolean tpsNumerarContratosServidor = false;
    private boolean tpsVlrMaxRenegIgualSomaContratos = true;
    private boolean tpsVlrMaxCompraIgualSomaContratos = true;
    private String tpsClasseGerenciadorAutorizacao = null;
    private boolean tpsExibeRankingConfirmacaoReserva = true;
    private String tpsPercentualMinimoPrdPagasReneg = null;
    private String tpsPercentualMinimoVigenciaReneg = null;
    private String tpsMinimoPrdPagasCompra = null;
    private String tpsPercentualMinimoPrdPagasCompra = null;
    private String tpsMinimoVigenciaCompra = null;
    private String tpsPercentualMinimoVigenciaCompra = null;
    private String tpsMinimoVigenciaReneg = null;
    private boolean tpsUsaCapitalDevidoBaseLimiteSaldo = false;
    private boolean tpsPermiteSaldoForaFaixaLimite = false;
    private boolean tpsExigeNroContratoInfSaldoDevedorCompra = false;
    private boolean tpsExigeNroContratoInfSaldoDevedorSolicSaldo = false;
    private boolean tpsExigeCodAutorizacaoSolic = false;
    private boolean tpsLimitaSaldoDevedorAnteriorReneg = false;
    private boolean tpsLimitaSaldoDevedorAnteriorCompra = false;
    private String tpsMargemErroLimiteSaldoAntReneg = null;
    private String tpsMargemErroLimiteSaldoAntRenegRef = null;
    private String tpsMargemErroLimiteSaldoAntCompra = null;
    private String tpsMargemErroLimiteSaldoAntCompraRef = null;
    private String tpsPrazoDiasCancelamentoRenegociacao = null;
    private boolean tpsExigeSenhaSerCancelRenegociacao = false;
    private String tpsMascaraIdentificadorAde = null;
    private String tpsLimitaCapitalDevidoABaseCalculo = null;
    private String tpsDiasAprSaldoDvControleCompra = null;
    private String tpsAcaoParaNaoAprSaldoDv = "0";
    private boolean tpsRejeicaoPgtSdvBloqueiaAmbasCsas = false;
    private String tpsEmailInfAprovacaoSaldoDevedor = null;
    private boolean tpsExibeInfBancariaServidor = false;
    private String tpsNumAdeHistLiquidacoesAntecipadas = "0";
    private boolean tpsIncluiAlterandoSomenteMesmoPrazo = false;
    private boolean tpsServidorLiquidaContrato = false;
    private boolean tpsDeferimentoAutoSolicitacaoServidor = false;
    private String tpsIdadeMinimaSerSolicSimulacao = null;
    private String tpsIdadeMaximaSerSolicSimulacao = null;
    private boolean tpsPermiteAlterarVlrLiberado = true;
    private boolean tpsPermiteAlterarAdeComBloqueio = false;
    private String tpsNumAdeHistSuspensoes = "0";
    private boolean tpsRetemMargemSvcPercentual = false;
    private String tpsQteDiasBloquearReservaAposLiq = "0";
    private String tpsDiasDeferAutConsigNaoDeferidas = null;
    private boolean tpsAnexoInclusaoContratosObrigatorioCseOrgSup = false;
    private boolean tpsAnexoInclusaoContratosObrigatorioCsaOrg = false;
    private boolean tpsAnexoInclusaoContratosObrigatorioSer = false;
    private boolean tpsAnexoConfirmarReservaObrigatorio = false;
    private boolean tpsPermiteSolicitarSaldoBeneficiario = false;
    private String tpsPercentualMinimoDescontoVlrSaldo = null;
    private String tpsQtdPropostasPagamentoParcelSaldo = null;
    private String tpsQtdPropostasPagamentoParcelSaldoRef = null;
    private String tpsDiasValidadePropostasPgtoSaldo = null;
    private String tpsDiasInfPropostasPgtoSaldoTercei = null;
    private String tpsPercentualMinimoManterValorReneg = null;
    private String tpsDiasSolicitarPropostasPgtoSaldo = null;
    private String tpsBaseCalcRetencaoSvcPercentual = null;
    private boolean tpsPrzMaxRenegIgualMaiorContratos  = false;
    private boolean tpsPrzMaxCompraIgualMaiorContratos = false;
    private String tpsQtdDiasBloqCsaAposInfSaldoSer = null;
    private boolean tpsIdentificadorAdeObrigatorio = false;
    private boolean tpsPermiteLiquidarAdeSuspensa = false;
    private boolean tpsImpedirLiquidacaoConsignacao = false;
    private boolean tpsPermiteContratoValorNegativo = false;
    private boolean tpsLimitaSaldoDevedorCadastradoCseOrgSup = false;
    private String tpsPercentualBaseCalcDescontoEmFila = null;
    private String tpsBaseCalcDescontoEmFila = null;
    private boolean tpsExibeMsgReservaMesmaVerbaCsaCor = false;
    private String tpsMsgExibirInclusaoAlteracaoAdeCsa = null;
    private String tpsPercentualMargemFolhaLimiteCsa = null;
    private boolean tpsExibeTabelaPrice = false;
    private boolean tpsOpInsereAlteraUsaMaiorPrazo = false;
    private boolean tpsVisualizaValorLiberadoCalculado = false;
    private boolean tpsMaxPrazoRelativoAosRestantes = false;
    private String tpsAlteraAdeComMargemNegativa = null;
    private String tpsExibeCidadeConfirmacaoSolicitacao = null;
    private boolean tpsExigenciaConfirmacaoLeituraServidor = false;
    private String tpsPeriodoLimiteAdeDuplicidade = null;
    private String tpsIdadeMaxDependenteEstSubsidio = null;
    private String tpsIdadeMaxDependenteDireitoSubsidio = null;
    private String tpsAgregadoPodeTerSubsidio = null;
    private String tpsPaiMaeTitularesDivorciadosSubsidio = null;
    private String tpsOrdemPrioridadeSubsidio = null;
    private String tpsQtdeSubsidioPorNatureza = null;
    private String tpsTipoCalculoSubsidio = null;
    private String tpsDataLimiteVigenciaPaiMaeTitularesDivorciadosSubsidio = null;
    private String tpsMascaraNumeroContratoBeneficio = null;
    private String tpsAtualizaAdeVlrAlteracaoMargem = CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_NUNCA;
    private boolean tpsPulaInformacaoValorPrazoFluxoReserva = false;
    private boolean tpsBloqIncAdeMesmaNsePrdRejeitada = false;
    private boolean tpsBloqueiaInclusaoAdeMesmoPeriodoNseRse = false;
    private boolean tpsServidorAlteraContrato = false;
    private Short tpsQtdMinAnexosAdeVlr = null;
    private Short tpsQtdMinAnexosAdeVlrRef = null;
    private boolean tpsServidorDeveSerKYCComplaint = false; // TPS_SERVIDOR_DEVE_SER_KYC_COMPLIANT
    private String tpsTempoLimiteCancelamentoAde = null;
    private boolean tpsExigeSenhaSerSuspenderConsignacao = false;
    private boolean tpsExigeSenhaSerCancelarConsignacao = false;
    private boolean tpsExigeSenhaSerReativarConsignacao = false;
    private boolean tpsExigeSenhaSerLiquidarConsignacao = false;
    private String tpsConfigurarIdPropostaConvenio = null;
    private String tpsConfigurarDiaVencimentoContrato = null;
    private String tpsConfigurarPeriodoCompetenciaDebito = null;
    private String tpsConfigurarNumeroConvenio = null;
    private String tpsConfigurarCodigoAdesao = null;
    private String tpsConfigurarIdadeMaximaContratacaoSeguro = null;
    private String tpsPermiteDescontoViaBoleto = CodedValues.NAO_PERMITE_PAGAMENTO_VIA_BOLETO;
    private String tpsObrigaInformacoesServidorSolicitacao = null;
    private Short tpsDiasVigenciaCet = null;
    private boolean tpsOcultarMenuServidor = false;
    private String tpsExibeTxtExplicativoValorPrestacao = null;
    private String tpsValorSvcFixoPosto = null;
    private boolean tpsInsereAlteraPorAdeIdnIgual = false;
    private boolean tpsPrendeMargemLiqAdePrzIndetAteCargaMargem = false;
    private boolean tpsRequerReconhecimentoFacilServidor = false;
    private boolean tpsCondiseraMargemRestReservaCompulsorio = false;
    private String tpsQuantidadeMinimaAnexoInclusaoContratos = null;
    private boolean tpsPermiteIncluirAdeRseBloqueado = false;
    private boolean tpsExigeCadastroTaxaJurosParaCet = false;
    private boolean tpsConsideradoInclusaoCsaPorDia = false;
    private String tpsPrazoAtendSolicitSaldoDevedor = null;

    /**
     * Retorna a descrição para parâmetro tipoValor
     * @param tpsTipoVlr
     * @return
     */
    public static String getDescricaoTpsTipoVlr(String tpsTipoVlr) {
        if (!TextHelper.isNull(tpsTipoVlr)) {
            if (CodedValues.TIPO_VLR_FIXO.equals(tpsTipoVlr) ||
                    CodedValues.TIPO_VLR_TOTAL_MARGEM.equals(tpsTipoVlr)) {
            } else if (CodedValues.TIPO_VLR_PERCENTUAL.equals(tpsTipoVlr)) {
                return ApplicationResourcesHelper.getMessage("rotulo.porcentagem", (AcessoSistema)null);
            } else if (CodedValues.TIPO_VLR_KILOGRAMAS.equals(tpsTipoVlr)) {
                return ApplicationResourcesHelper.getMessage("rotulo.kilograma.abreviado", (AcessoSistema)null);
            }
        }
        return ApplicationResourcesHelper.getMessage("rotulo.moeda", (AcessoSistema)null);
    }

    public String getTpsAlteraAdeComMargemNegativa() {
        return tpsAlteraAdeComMargemNegativa;
    }

    public boolean isTpsMaxPrazoRelativoAosRestantes() {
        return tpsMaxPrazoRelativoAosRestantes;
    }

    public boolean isTpsOpInsereAlteraUsaMaiorPrazo() {
        return tpsOpInsereAlteraUsaMaiorPrazo;
    }

    public boolean isTpsAddValorIofValTaxaJuros() {
        return tpsAddValorIofValTaxaJuros;
    }

    public boolean isTpsAddValorTacValTaxaJuros() {
        return tpsAddValorTacValTaxaJuros;
    }

    public String getTpsAdeVlr() {
        return tpsAdeVlr;
    }

    public String getTpsPercMargemSimulador() {
        return tpsPercMargemSimulador;
    }

    public String getTpsAgenciaDepositoSaldoDevedor() {
        return tpsAgenciaDepositoSaldoDevedor;
    }

    public boolean isTpsAlteraAdeVlr() {
        return tpsAlteraAdeVlr;
    }

    public String getTpsBancoDepositoSaldoDevedor() {
        return tpsBancoDepositoSaldoDevedor;
    }

    public boolean isTpsBuscaBoletoExterno() {
        return tpsBuscaBoletoExterno;
    }

    public boolean isTpsCadValorIof() {
        return tpsCadValorIof;
    }

    public boolean isTpsCadValorLiquidoLiberado() {
        return tpsCadValorLiquidoLiberado;
    }

    public boolean isTpsCadValorMensalidadeVinc() {
        return tpsCadValorMensalidadeVinc;
    }

    public boolean isTpsCadValorTac() {
        return tpsCadValorTac;
    }

    public boolean isTpsCalcTacIofValidaTaxaJuros() {
        return tpsCalcTacIofValidaTaxaJuros;
    }

    public boolean isTpsCalculoValorAcumulado() {
        return tpsCalculoValorAcumulado;
    }

    public String getTpsCarenciaFinal() {
        return tpsCarenciaFinal;
    }

    public String getTpsCarenciaMaxima() {
        return tpsCarenciaMaxima;
    }

    public String getTpsCarenciaMinima() {
        return tpsCarenciaMinima;
    }

    public String getTpsClasseJavaProcEspecificoReserva() {
        return tpsClasseJavaProcEspecificoReserva;
    }

    public String getTpsCnpjFavorecidoDepositoSdv() {
        return tpsCnpjFavorecidoDepositoSdv;
    }

    public boolean isTpsCnvPodeDeferir() {
        return tpsCnvPodeDeferir;
    }

    public boolean isTpsConcluiAdeNaoPaga() {
        return tpsConcluiAdeNaoPaga;
    }

    public String getTpsContaDepositoSaldoDevedor() {
        return tpsContaDepositoSaldoDevedor;
    }

    public boolean isTpsControlaSaldo() {
        return tpsControlaSaldo;
    }

    public boolean isTpsControlaVlrMaxDesconto() {
        return tpsControlaVlrMaxDesconto;
    }

    public boolean isTpsCorrecaoEnviadaAposPrincipal() {
        return tpsCorrecaoEnviadaAposPrincipal;
    }

    public boolean isTpsCorrecaoSobreTotalSaldoDv() {
        return tpsCorrecaoSobreTotalSaldoDv;
    }

    public boolean isTpsRequerDeferimentoReservas() {
        return tpsRequerDeferimentoReservas;
    }

    public String getTpsDataAberturaTaxa() {
        return tpsDataAberturaTaxa;
    }

    public String getTpsDataAberturaTaxaRef() {
        return tpsDataAberturaTaxaRef;
    }

    public String getTpsDataLimiteDigitTaxa() {
        return tpsDataLimiteDigitTaxa;
    }

    public String getTpsDiasDesblCompNaoConf() {
        return tpsDiasDesblCompNaoConf;
    }

    public String getTpsDiasDesblConsigNaoDef() {
        return tpsDiasDesblConsigNaoDef;
    }

    public String getTpsDiasDesblResNaoConf() {
        return tpsDiasDesblResNaoConf;
    }

    public String getTpsDiasDesblSolicitacaoNaoConf() {
        return tpsDiasDesblSolicitacaoNaoConf;
    }

    public String getTpsEmailInfContratosComprados() {
        return tpsEmailInfContratosComprados;
    }

    public String getTpsEmailInfLiqContratoComprado() {
        return tpsEmailInfLiqContratoComprado;
    }

    public String getTpsEmailInfPgtSaldoDevedor() {
        return tpsEmailInfPgtSaldoDevedor;
    }

    public String getTpsEmailInfSaldoDevedor() {
        return tpsEmailInfSaldoDevedor;
    }

    public String getTpsExcedenteMonetarioTxJuros() {
        return tpsExcedenteMonetarioTxJuros;
    }

    public boolean isTpsExibeCapitalDevido() {
        return tpsExibeCapitalDevido;
    }

    public boolean isTpsExigeSeguroPrestamista() {
        return tpsExigeSeguroPrestamista;
    }

    public String getTpsExigeSenhaAlteracaoContratos() {
        return tpsExigeSenhaAlteracaoContratos;
    }

    public String getTpsFormaCalculoCorrecaoSaldoDv() {
        return tpsFormaCalculoCorrecaoSaldoDv;
    }

    public String getTpsFormaCalculoCorrecaoVlrPresente() {
        return tpsFormaCalculoCorrecaoVlrPresente;
    }

    public boolean isTpsImportaContratosSemProcessamento() {
        return tpsImportaContratosSemProcessamento;
    }

    public Short getTpsIncideMargem() {
        return tpsIncideMargem;
    }

    public boolean isTpsIncluiAlterandoMesmoPeriodo() {
        return tpsIncluiAlterandoMesmoPeriodo;
    }

    public String getTpsIndice() {
        return tpsIndice;
    }

    public boolean isTpsInfBancariaObrigatoria() {
        return tpsInfBancariaObrigatoria;
    }

    public Short getTpsIntegraFolha() {
        return tpsIntegraFolha;
    }

    public String getTpsMaxPrazo() {
        return tpsMaxPrazo;
    }

    public String getTpsMesInicioDescontoGap() {
        return tpsMesInicioDescontoGap;
    }

    public boolean isTpsNaoExibeContratoServidor() {
        return tpsNaoExibeContratoServidor;
    }

    public String getTpsNomeFavorecidoDepositoSdv() {
        return tpsNomeFavorecidoDepositoSdv;
    }

    public String getTpsNumContratosPorConvenio() {
        return tpsNumContratosPorConvenio;
    }

    public String getTpsNumContratosPorServico() {
        return tpsNumContratosPorServico;
    }

    public String getTpsOpFinanciada() {
        return tpsOpFinanciada;
    }

    public String getTpsPercentualMaximoPermitidoVlrRef() {
        return tpsPercentualMaximoPermitidoVlrRef;
    }

    public String getTpsPermiteCadastrarSaldoDevedor() {
        return tpsPermiteCadastrarSaldoDevedor;
    }

    public boolean isTpsPermiteAlteracaoContratos() {
        return tpsPermiteAlteracaoContratos;
    }

    public String getTpsPermiteAumVlrPrzConsignacao() {
        return tpsPermiteAumVlrPrzConsignacao;
    }

    public boolean isTpsPermiteCancelarContratos() {
        return tpsPermiteCancelarContratos;
    }

    public boolean isTpsPermiteContratoSuperSerCsa() {
        return tpsPermiteContratoSuperSerCsa;
    }

    public boolean isTpsPermiteImportacaoLote() {
        return tpsPermiteImportacaoLote;
    }

    public boolean isTpsPermiteLiquidarContratos() {
        return tpsPermiteLiquidarContratos;
    }

    public boolean isTpsPermiteLiquidarParcela() {
        return tpsPermiteLiquidarParcela;
    }

    public boolean isTpsPermiteRenegociacao() {
        return tpsPermiteRenegociacao;
    }

    public boolean isTpsPermiteRepetirIndiceCsa() {
        return tpsPermiteRepetirIndiceCsa;
    }

    public boolean isTpsPermiteServidorSolicitar() {
        return tpsPermiteServidorSolicitar;
    }

    public String getTpsPerRestricaoCadNovaAdeCnvRse() {
        return tpsPerRestricaoCadNovaAdeCnvRse;
    }

    public String getTpsPossuiControleTetoDesconto() {
        return tpsPossuiControleTetoDesconto;
    }

    public String getTpsPossuiCorrecaoSaldoDevedor() {
        return tpsPossuiCorrecaoSaldoDevedor;
    }

    public boolean isTpsPossuiCorrecaoValorPresente() {
        return tpsPossuiCorrecaoValorPresente;
    }

    public String getTpsPrazoCarenciaFinal() {
        return tpsPrazoCarenciaFinal;
    }

    public boolean isTpsPrazoFixo() {
        return tpsPrazoFixo;
    }

    public boolean isTpsPreservaDataAlteracao() {
        return tpsPreservaDataAlteracao;
    }

    public boolean isTpsPreservaDataRenegociacao() {
        return tpsPreservaDataRenegociacao;
    }

    public boolean isTpsPreservaPrdRejeitadaReimpl() {
        return tpsPreservaPrdRejeitadaReimpl;
    }

    public String getTpsQtdeMaxAdeRenegociacao() {
        return tpsQtdeMaxAdeRenegociacao;
    }

    public String getTpsQtdeMaxAdeCompra() {
        return tpsQtdeMaxAdeCompra;
    }

    public String getTpsQuantidadeMaximaContratosSvc() {
        return tpsQuantidadeMaximaContratosSvc;
    }

    public boolean isTpsReimplantacaoAutomatica() {
        return tpsReimplantacaoAutomatica;
    }

    public boolean isTpsPreservaDataMaisAntigaReneg() {
        return tpsPreservaDataMaisAntigaReneg;
    }

    public boolean isTpsRetiravelporSvcCompPrioritario() {
        return tpsRetiravelporSvcCompPrioritario;
    }

    public boolean isTpsSerSenhaObrigatoriaCsa() {
        return tpsSerSenhaObrigatoriaCsa;
    }

    public boolean isTpsSerSenhaObrigatoriaCse() {
        return tpsSerSenhaObrigatoriaCse;
    }

    public boolean isTpsSerSenhaObrigatoriaSer() {
        return tpsSerSenhaObrigatoriaSer;
    }

    public boolean isTpsServicoCompulsorio() {
        return tpsServicoCompulsorio;
    }

    public boolean isTpsServicoTipoGap() {
        return tpsServicoTipoGap;
    }

    public boolean isTpsPulaInformacaoValorPrazoFluxoReserva() {
        return tpsPulaInformacaoValorPrazoFluxoReserva;
    }

    public boolean isTpsBloqIncAdeMesmaNsePrdRejeitada() {
        return tpsBloqIncAdeMesmaNsePrdRejeitada;
    }

    public Boolean getTpsSomaIofSimulacaoReserva() {
        return tpsSomaIofSimulacaoReserva;
    }

    public String getTpsTacFinanciada() {
        return tpsTacFinanciada;
    }

    public String getTpsTipoTac() {
        return tpsTipoTac;
    }

    public String getTpsTipoVlr() {
        return tpsTipoVlr;
    }

    public boolean isTpsValidaMensVincValTaxaJuros() {
        return tpsValidaMensVincValTaxaJuros;
    }

    public boolean isTpsValidarDataNascimentoNaReserva() {
        return tpsValidarDataNascimentoNaReserva;
    }

    public boolean isTpsValidarInfBancariaNaReserva() {
        return tpsValidarInfBancariaNaReserva;
    }

    public boolean isTpsValidarTaxaJuros() {
        return tpsValidarTaxaJuros;
    }

    public String getTpsValorMaxTac() {
        return tpsValorMaxTac;
    }

    public String getTpsValorMinTac() {
        return tpsValorMinTac;
    }

    public String getTpsVlrLiberadoMaximo() {
        return tpsVlrLiberadoMaximo;
    }

    public String getTpsVlrLiberadoMinimo() {
        return tpsVlrLiberadoMinimo;
    }

    public String getTpsVlrLimiteAdeSemMargem() {
        return tpsVlrLimiteAdeSemMargem;
    }

    public String getTpsVlrLimiteAdeSemMargemAlter() {
        return tpsVlrLimiteAdeSemMargemAlter;
    }

    public boolean isTpsVlrLiqTaxaJuros() {
        return tpsVlrLiqTaxaJuros;
    }

    public String getTpsVlrMaximoContrato() {
        return tpsVlrMaximoContrato;
    }

    public String getTpsVlrMinimoContrato() {
        return tpsVlrMinimoContrato;
    }

    public String getTpsVlrPercMaximoParcelaAlongamento() {
        return tpsVlrPercMaximoParcelaAlongamento;
    }

    public boolean isTpsIncluiAlterandoQualquerPeriodo() {
        return tpsIncluiAlterandoQualquerPeriodo;
    }

    public String getTpsMsgExibirSolicitacaoServidor() {
        return tpsMsgExibirSolicitacaoServidor;
    }

    public String getTpsMsgExibirSolicitacaoServidorOfertaOutroSvc() {
        return tpsMsgExibirSolicitacaoServidorOfertaOutroSvc;
    }

    public boolean isTpsAtualizaAdeVlrNoRetorno() {
        return tpsAtualizaAdeVlrNoRetorno;
    }

    public boolean isTpsBloqueiaReservaLimiteSimulador() {
        return tpsBloqueiaReservaLimiteSimulador;
    }

    public String getTpsCalculaSaldoSomenteVincendo() {
        return tpsCalculaSaldoSomenteVincendo;
    }

    public int getTpsQtdCsaPermitidasSimulador() {
        return tpsQtdCsaPermitidasSimulador;
    }

    public boolean isTpsExigeSenhaConfirmacaoSolicitacao() {
        return tpsExigeSenhaConfirmacaoSolicitacao;
    }

    public boolean isTpsCsaDeveContarParaLimiteRanking() {
        return tpsCsaDeveContarParaLimiteRanking;
    }

    public String getTpsEmailInfSolicitacaoSaldoDevedor() {
        return tpsEmailInfSolicitacaoSaldoDevedor;
    }

    public String getTpsLimiteAumentoValorAde() {
        return tpsLimiteAumentoValorAde;
    }

    public boolean isTpsPermiteDataRetroativaImpLote() {
        return tpsPermiteDataRetroativaImpLote;
    }

    public boolean isTpsPodeIncluirNovosContratos() {
        return tpsPodeIncluirNovosContratos;
    }

    public boolean isTpsExigeAceiteTermoAdesao() {
        return tpsExigeAceiteTermoAdesao;
    }

    public boolean isTpsExigeAceiteTermoAdesaoAntesValores() {
        return tpsExigeAceiteTermoAdesaoAntesValores;
    }

    public boolean isTpsLimitaSaldoDevedorCadastrado() {
        return tpsLimitaSaldoDevedorCadastrado;
    }

    public String getTpsMargemErroLimiteSaldoDevedor() {
        return tpsMargemErroLimiteSaldoDevedor;
    }

    public String getTpsMargemErroLimiteSaldoDevedorRef() {
        return tpsMargemErroLimiteSaldoDevedorRef;
    }

    public String getTpsOrdenacaoCadastroTaxas() {
        return tpsOrdenacaoCadastroTaxas;
    }

    public String getTpsDiasInfSaldoDvControleCompra() {
        return tpsDiasInfSaldoDvControleCompra;
    }

    public String getTpsDiasInfPgtSaldoControleCompra() {
        return tpsDiasInfPgtSaldoControleCompra;
    }

    public String getTpsDiasLiquidacaoAdeControleCompra() {
        return tpsDiasLiquidacaoAdeControleCompra;
    }

    public String getTpsAcaoParaNaoInfSaldoDv() {
        return tpsAcaoParaNaoInfSaldoDv;
    }

    public String getTpsAcaoParaNaoInfPgtSaldo() {
        return tpsAcaoParaNaoInfPgtSaldo;
    }

    public String getTpsAcaoParaNaoLiquidacaoAde() {
        return tpsAcaoParaNaoLiquidacaoAde;
    }

    public String getTpsMinimoPrdPagasRenegociacao() {
        return tpsMinimoPrdPagasRenegociacao;
    }

    public String getTpsCsaLimiteSuperiorTabelaJurosCet() {
        return tpsCsaLimiteSuperiorTabelaJurosCet;
    }

    public String getTpsDiasVigenciaTaxasSuperiorLimite() {
        return tpsDiasVigenciaTaxasSuperiorLimite;
    }

    public boolean isTpsVlrMaxRenegIgualSomaContratos() {
        return tpsVlrMaxRenegIgualSomaContratos;
    }

    public boolean isTpsVlrMaxCompraIgualSomaContratos() {
        return tpsVlrMaxCompraIgualSomaContratos;
    }

    public String getTpsClasseGerenciadorAutorizacao() {
        return tpsClasseGerenciadorAutorizacao;
    }

    public boolean isTpsNumerarContratosServidor() {
        return tpsNumerarContratosServidor;
    }

    public boolean isTpsExibeRankingConfirmacaoReserva() {
        return tpsExibeRankingConfirmacaoReserva;
    }

    public String getTpsPercentualMinimoPrdPagasReneg() {
        return tpsPercentualMinimoPrdPagasReneg;
    }

    public String getTpsPercentualMinimoVigenciaReneg() {
        return tpsPercentualMinimoVigenciaReneg;
    }

    public String getTpsMinimoPrdPagasCompra() {
        return tpsMinimoPrdPagasCompra;
    }

    public String getTpsPercentualMinimoPrdPagasCompra() {
        return tpsPercentualMinimoPrdPagasCompra;
    }

    public String getTpsMinimoVigenciaCompra() {
        return tpsMinimoVigenciaCompra;
    }

    public String getTpsPercentualMinimoVigenciaCompra() {
        return tpsPercentualMinimoVigenciaCompra;
    }

    public String getTpsMinimoVigenciaReneg() {
        return tpsMinimoVigenciaReneg;
    }

    public boolean isTpsUsaCapitalDevidoBaseLimiteSaldo() {
        return tpsUsaCapitalDevidoBaseLimiteSaldo;
    }

    public boolean isTpsPermiteSaldoForaFaixaLimite() {
        return tpsPermiteSaldoForaFaixaLimite;
    }

    public boolean isTpsExigeNroContratoInfSaldoDevedorCompra() {
        return tpsExigeNroContratoInfSaldoDevedorCompra;
    }

    public boolean isTpsExigeNroContratoInfSaldoDevedorSolicSaldo() {
        return tpsExigeNroContratoInfSaldoDevedorSolicSaldo;
    }

    public boolean isTpsExigeCodAutorizacaoSolic() {
        return tpsExigeCodAutorizacaoSolic;
    }

    public boolean isTpsLimitaSaldoDevedorAnteriorReneg() {
        return tpsLimitaSaldoDevedorAnteriorReneg;
    }

    public boolean isTpsLimitaSaldoDevedorAnteriorCompra() {
        return tpsLimitaSaldoDevedorAnteriorCompra;
    }

    public String getTpsMargemErroLimiteSaldoAntReneg() {
        return tpsMargemErroLimiteSaldoAntReneg;
    }

    public String getTpsMargemErroLimiteSaldoAntRenegRef() {
        return tpsMargemErroLimiteSaldoAntRenegRef;
    }

    public String getTpsMargemErroLimiteSaldoAntCompra() {
        return tpsMargemErroLimiteSaldoAntCompra;
    }

    public String getTpsMargemErroLimiteSaldoAntCompraRef() {
        return tpsMargemErroLimiteSaldoAntCompraRef;
    }

    public String getTpsPrazoDiasCancelamentoRenegociacao() {
        return tpsPrazoDiasCancelamentoRenegociacao;
    }

    public boolean isTpsExigeSenhaSerCancelRenegociacao() {
        return tpsExigeSenhaSerCancelRenegociacao;
    }

    public String getTpsMascaraIdentificadorAde() {
        return tpsMascaraIdentificadorAde;
    }

    public String isTpsLimitaCapitalDevidoABaseCalculo() {
        return tpsLimitaCapitalDevidoABaseCalculo;
    }

    public String getTpsDiasAprSaldoDvControleCompra() {
        return tpsDiasAprSaldoDvControleCompra;
    }

    public String getTpsAcaoParaNaoAprSaldoDv() {
        return tpsAcaoParaNaoAprSaldoDv;
    }

    public boolean isTpsRejeicaoPgtSdvBloqueiaAmbasCsas() {
        return tpsRejeicaoPgtSdvBloqueiaAmbasCsas;
    }

    public String getTpsEmailInfAprovacaoSaldoDevedor() {
        return tpsEmailInfAprovacaoSaldoDevedor;
    }

    public boolean isTpsExibeInfBancariaServidor() {
        return tpsExibeInfBancariaServidor;
    }

    public String getTpsNumAdeHistLiquidacoesAntecipadas() {
        return tpsNumAdeHistLiquidacoesAntecipadas;
    }

    public boolean isTpsIncluiAlterandoSomenteMesmoPrazo() {
        return tpsIncluiAlterandoSomenteMesmoPrazo;
    }

    public boolean isTpsServidorLiquidaContrato() {
        return tpsServidorLiquidaContrato;
    }

    public boolean isTpsDeferimentoAutoSolicitacaoServidor() {
        return tpsDeferimentoAutoSolicitacaoServidor;
    }

    public String getTpsIdadeMinimaSerSolicSimulacao() {
        return tpsIdadeMinimaSerSolicSimulacao;
    }

    public String getTpsIdadeMaximaSerSolicSimulacao() {
        return tpsIdadeMaximaSerSolicSimulacao;
    }

    public boolean isTpsPermiteAlterarVlrLiberado() {
        return tpsPermiteAlterarVlrLiberado;
    }

    public boolean isTpsPermiteAlterarAdeComBloqueio() {
        return tpsPermiteAlterarAdeComBloqueio;
    }

    public boolean isTpsRetemMargemSvcPercentual() {
        return tpsRetemMargemSvcPercentual;
    }

    public String getTpsNumAdeHistSuspensoes() {
        return tpsNumAdeHistSuspensoes;
    }

    public String getTpsQteDiasBloquearReservaAposLiq() {
        return tpsQteDiasBloquearReservaAposLiq;
    }

    public String getTpsDiasDeferAutConsigNaoDeferidas() {
        return tpsDiasDeferAutConsigNaoDeferidas;
    }

    public boolean isTpsAnexoInclusaoContratosObrigatorioCseOrgSup() {
        return tpsAnexoInclusaoContratosObrigatorioCseOrgSup;
    }

    public boolean isTpsPermiteSolicitarSaldoBeneficiario() {
        return tpsPermiteSolicitarSaldoBeneficiario;
    }

    public String getTpsPercentualMinimoDescontoVlrSaldo() {
        return tpsPercentualMinimoDescontoVlrSaldo;
    }

    public String getTpsQtdPropostasPagamentoParcelSaldo() {
        return tpsQtdPropostasPagamentoParcelSaldo;
    }

    public String getTpsQtdPropostasPagamentoParcelSaldoRef() {
        return tpsQtdPropostasPagamentoParcelSaldoRef;
    }

    public String getTpsDiasValidadePropostasPgtoSaldo() {
        return tpsDiasValidadePropostasPgtoSaldo;
    }

    public String getTpsDiasInfPropostasPgtoSaldoTercei() {
        return tpsDiasInfPropostasPgtoSaldoTercei;
    }

    public String getTpsPercentualMinimoManterValorReneg() {
        return tpsPercentualMinimoManterValorReneg;
    }

    public String getTpsDiasSolicitarPropostasPgtoSaldo() {
        return tpsDiasSolicitarPropostasPgtoSaldo;
    }

    public String getTpsBaseCalcRetencaoSvcPercentual() {
        return tpsBaseCalcRetencaoSvcPercentual;
    }

    public boolean isTpsPrzMaxRenegIgualMaiorContratos() {
        return tpsPrzMaxRenegIgualMaiorContratos;
    }

    public boolean isTpsPrzMaxCompraIgualMaiorContratos() {
        return tpsPrzMaxCompraIgualMaiorContratos;
    }

    public String getTpsQtdDiasBloqCsaAposInfSaldoSer() {
        return tpsQtdDiasBloqCsaAposInfSaldoSer;
    }

    public String getTpsMaxPrazoRenegociacao() {
        return tpsMaxPrazoRenegociacao;
    }

    public boolean isTpsIdentificadorAdeObrigatorio() {
        return tpsIdentificadorAdeObrigatorio;
    }

    public boolean isTpsExibeBoleto() {
        return tpsExibeBoleto;
    }

    public boolean isTpsPermiteLiquidarAdeSuspensa() {
        return tpsPermiteLiquidarAdeSuspensa;
    }

    public boolean isTpsImpedirLiquidacaoConsignacao() {
        return tpsImpedirLiquidacaoConsignacao;
    }

    public boolean isTpsPermiteContratoValorNegativo() {
        return tpsPermiteContratoValorNegativo;
    }

    public boolean isTpsLimitaSaldoDevedorCadastradoCseOrgSup() {
        return tpsLimitaSaldoDevedorCadastradoCseOrgSup;
    }

    public String getTpsPercentualBaseCalcDescontoEmFila() {
        return tpsPercentualBaseCalcDescontoEmFila;
    }

    public String getTpsBaseCalcDescontoEmFila() {
        return tpsBaseCalcDescontoEmFila;
    }

    public boolean isTpsExibeMsgReservaMesmaVerbaCsaCor() {
        return tpsExibeMsgReservaMesmaVerbaCsaCor;
    }

    public String getTpsMsgExibirInclusaoAlteracaoAdeCsa() {
        return tpsMsgExibirInclusaoAlteracaoAdeCsa;
    }

    public String getTpsPercentualMargemFolhaLimiteCsa() {
        return tpsPercentualMargemFolhaLimiteCsa;
    }

    public boolean isTpsExibeTabelaPrice() {
        return tpsExibeTabelaPrice;
    }

    public boolean isTpsVisualizaValorLiberadoCalculado() {
		return tpsVisualizaValorLiberadoCalculado;
	}

    public String getTpsExibeCidadeConfirmacaoSolicitacao() {
        return tpsExibeCidadeConfirmacaoSolicitacao;
    }

	public String getTpsPeriodoLimiteAdeDuplicidade() {
        return tpsPeriodoLimiteAdeDuplicidade;
    }

	public boolean isTpsExigenciaConfirmacaoLeituraServidor() {
		return tpsExigenciaConfirmacaoLeituraServidor;
	}

    public String getTpsIdadeMaxDependenteEstSubsidio() {
        return tpsIdadeMaxDependenteEstSubsidio;
    }

    public String getTpsIdadeMaxDependenteDireitoSubsidio() {
        return tpsIdadeMaxDependenteDireitoSubsidio;
    }

    public String getTpsAgregadoPodeTerSubsidio() {
        return tpsAgregadoPodeTerSubsidio;
    }

    public String getTpsPaiMaeTitularesDivorciadosSubsidio() {
        return tpsPaiMaeTitularesDivorciadosSubsidio;
    }

    public String getTpsOrdemPrioridadeSubsidio() {
        return tpsOrdemPrioridadeSubsidio;
    }

    public String getTpsQtdeSubsidioPorNatureza() {
        return tpsQtdeSubsidioPorNatureza;
    }

    public String getTpsTipoCalculoSubsidio() {
        return tpsTipoCalculoSubsidio;
    }

    public String getTpsDataLimiteVigenciaPaiMaeTitularesDivorciadosSubsidio() {
        return tpsDataLimiteVigenciaPaiMaeTitularesDivorciadosSubsidio;
    }

    public String getTpsMascaraNumeroContratoBeneficio() {
        return tpsMascaraNumeroContratoBeneficio;
    }

    public String getTpsAtualizaAdeVlrAlteracaoMargem() {
        return tpsAtualizaAdeVlrAlteracaoMargem;
    }

    public boolean isTpsBloqueiaInclusaoAdeMesmoPeriodoNseRse() {
        return tpsBloqueiaInclusaoAdeMesmoPeriodoNseRse;
    }

    public boolean isTpsServidorAlteraContrato() {
        return tpsServidorAlteraContrato;
    }

    public Short getTpsQtdMinAnexosAdeVlr() {
        return tpsQtdMinAnexosAdeVlr;
    }

    public Short getTpsQtdMinAnexosAdeVlrRef() {
        return tpsQtdMinAnexosAdeVlrRef;
    }

    public boolean isTpsServidorDeveSerKYCComplaint() {
        return tpsServidorDeveSerKYCComplaint;
    }

    public String getTpsTempoLimiteCancelamentoAde() {
        return tpsTempoLimiteCancelamentoAde;
    }

    public boolean isTpsAnexoConfirmarReservaObrigatorio() {
        return tpsAnexoConfirmarReservaObrigatorio;
    }

    public boolean isTpsExigeSenhaSerSuspenderConsignacao() {
        return tpsExigeSenhaSerSuspenderConsignacao;
    }

    public boolean isTpsExigeSenhaSerCancelarConsignacao() {
        return tpsExigeSenhaSerCancelarConsignacao;
    }

    public boolean isTpsExigeSenhaSerReativarConsignacao() {
        return tpsExigeSenhaSerReativarConsignacao;
    }

    public boolean isTpsExigeSenhaSerLiquidarConsignacao() {
        return tpsExigeSenhaSerLiquidarConsignacao;
    }

    public String getTpsConfigurarIdPropostaConvenio() {
        return tpsConfigurarIdPropostaConvenio;
    }

    public String getTpsConfigurarDiaVencimentoContrato() {
        return tpsConfigurarDiaVencimentoContrato;
    }

    public String getTpsConfigurarPeriodoCompetenciaDebito() {
        return tpsConfigurarPeriodoCompetenciaDebito;
    }

    public String getTpsConfigurarNumeroConvenio() {
        return tpsConfigurarNumeroConvenio;
    }

    public String getTpsConfigurarCodigoAdesao() {
        return tpsConfigurarCodigoAdesao;
    }

    public String getTpsConfigurarIdadeMaximaContratacaoSeguro() {
        return tpsConfigurarIdadeMaximaContratacaoSeguro;
    }

    public String getTpsObrigaInformacoesServidorSolicitacao() {
        return tpsObrigaInformacoesServidorSolicitacao;
    }

    public Short getTpsDiasVigenciaCet() {
        return tpsDiasVigenciaCet;
    }

    public boolean isTpsOcultarMenuServidor() {
        return tpsOcultarMenuServidor;
    }

    public String getTpsPermiteDescontoViaBoleto() {
        return tpsPermiteDescontoViaBoleto;
    }

    public String getTpsExibeTxtExplicativoValorPrestacao() {
        return tpsExibeTxtExplicativoValorPrestacao;
    }

    public String getTpsValorSvcFixoPosto() {
        return tpsValorSvcFixoPosto;
    }

	public boolean isTpsInsereAlteraPorAdeIdnIgual() {
		return tpsInsereAlteraPorAdeIdnIgual;
	}

	public boolean isTpsPrendeMargemLiqAdePrzIndetAteCargaMargem() {
	    return tpsPrendeMargemLiqAdePrzIndetAteCargaMargem;
	}

    public boolean isTpsRequerReconhecimentoFacilServidor() {
        return tpsRequerReconhecimentoFacilServidor;
    }

    public boolean isTpsCondiseraMargemRestReservaCompulsorio() {
        return tpsCondiseraMargemRestReservaCompulsorio;
    }

    public boolean isTpsAnexoInclusaoContratosObrigatorioCsaOrg() {
        return tpsAnexoInclusaoContratosObrigatorioCsaOrg;
    }

    public boolean isTpsAnexoInclusaoContratosObrigatorioSer() {
        return tpsAnexoInclusaoContratosObrigatorioSer;
    }

    public String getTpsQuantidadeMinimaInclusaoContratos() {
        return tpsQuantidadeMinimaAnexoInclusaoContratos;
    }

    public boolean isTpsPermiteIncluirAdeRseBloqueado() {
        return tpsPermiteIncluirAdeRseBloqueado;
    }

    public boolean isTpsExigeCadastroTaxaJurosParaCet() {return tpsExigeCadastroTaxaJurosParaCet; }

    public boolean isTpsConsideradoInclusaoCsaPorDia() {
        return tpsConsideradoInclusaoCsaPorDia;
    }

    public void setTpsConsideradoInclusaoCsaPorDia(boolean tpsConsideradoInclusaoCsaPorDia) {
        this.tpsConsideradoInclusaoCsaPorDia = tpsConsideradoInclusaoCsaPorDia;
    }

	public String getTpsPrazoAtendSolicitSaldoDevedor() {
		return tpsPrazoAtendSolicitSaldoDevedor;
	}

	public void setTpsPrazoAtendSolicitSaldoDevedor(String tpsPrazoAtendSolicitSaldoDevedor) {
		this.tpsPrazoAtendSolicitSaldoDevedor = tpsPrazoAtendSolicitSaldoDevedor;
	}
}