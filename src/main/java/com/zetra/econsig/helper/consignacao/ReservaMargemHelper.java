package com.zetra.econsig.helper.consignacao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ReservaMargemHelper</p>
 * <p>Description: Helper Class para Operação de Reserva de Margem</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReservaMargemHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReservaMargemHelper.class);

    private static ParamSvcTO getParamSvc(String svcCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_CARENCIA_MINIMA);
            tpsCodigos.add(CodedValues.TPS_CARENCIA_MAXIMA);
            tpsCodigos.add(CodedValues.TPS_SERVICO_COMPULSORIO);
            tpsCodigos.add(CodedValues.TPS_VLR_MINIMO_CONTRATO);
            tpsCodigos.add(CodedValues.TPS_VLR_MAXIMO_CONTRATO);
            tpsCodigos.add(CodedValues.TPS_POSSUI_CONTROLE_TETO_DESCONTO);
            tpsCodigos.add(CodedValues.TPS_INCIDE_MARGEM);
            tpsCodigos.add(CodedValues.TPS_TIPO_VLR);
            tpsCodigos.add(CodedValues.TPS_ADE_VLR);
            tpsCodigos.add(CodedValues.TPS_ALTERA_ADE_VLR);
            tpsCodigos.add(CodedValues.TPS_MAX_PRAZO);
            tpsCodigos.add(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE);
            tpsCodigos.add(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM);
            tpsCodigos.add(CodedValues.TPS_MASCARA_IDENTIFICADOR_ADE);

            ParametroDelegate parDelegate = new ParametroDelegate();
            ParamSvcTO paramSvc = parDelegate.selectParamSvcCse(svcCodigo, tpsCodigos, responsavel);
            return paramSvc;
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel);
        }
    }

    private static List<TransferObject> getParamSvcCsa(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_VLR_MINIMO_CONTRATO);
            tpsCodigos.add(CodedValues.TPS_VLR_MAXIMO_CONTRATO);

            ParametroDelegate parDelegate = new ParametroDelegate();
            List<TransferObject> paramSvcCsa = parDelegate.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            return paramSvcCsa;
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel);
        }
    }

    /**
     * Validação da reserva. Utilizado pela rotina de importação de contratos (Integracao.java)
     * e pelo processamento Host-Host
     * @param convenio
     * @param reserva
     * @param responsavel
     * @param transacao
     * @throws ViewHelperException
     */
    public static void validaReserva(CustomTransferObject convenio, CustomTransferObject reserva, AcessoSistema responsavel, boolean transacao, boolean validaMargem, boolean serAtivo) throws ViewHelperException {
        String svcCodigo = (String) reserva.getAttribute("SVC_CODIGO");
        ParamSvcTO paramSvc = getParamSvc(svcCodigo, responsavel);
        validaReserva(convenio, reserva, null, paramSvc, responsavel, transacao, validaMargem, serAtivo);
    }

    /**
     * Validação da reserva. Utilizada pelo processamento de Lote (LoteHelper.java)
     * @param paramCnv
     * @param reserva
     * @param responsavel
     * @param transacao
     * @param validaMargem
     * @throws ViewHelperException
     */
    public static void validaReserva(Map<String, Object> paramCnv, CustomTransferObject reserva, AcessoSistema responsavel, boolean transacao, boolean validaMargem) throws ViewHelperException {
        // Gera TO com os parâmetros do convênio
        CustomTransferObject convenio = new CustomTransferObject();
        convenio.setAttribute(Columns.CNV_CODIGO, paramCnv.get(Columns.CNV_CODIGO));
        convenio.setAttribute(Columns.CNV_CSA_CODIGO, paramCnv.get(Columns.CSA_CODIGO));
        convenio.setAttribute(Columns.SVC_PRIORIDADE, paramCnv.get(Columns.SVC_PRIORIDADE));

        convenio.setAttribute("CARENCIA_MINIMA", paramCnv.get("CARENCIA_MINIMA"));
        convenio.setAttribute("CARENCIA_MAXIMA", paramCnv.get("CARENCIA_MAXIMA"));
        convenio.setAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO", paramCnv.get(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA));

        validaReserva(convenio, reserva, paramCnv, null, responsavel, transacao, validaMargem, true);
    }

    private static void validaReserva(CustomTransferObject convenio, CustomTransferObject reserva, Map<String, Object> paramCnv, ParamSvcTO paramSvc, AcessoSistema responsavel, boolean transacao, boolean validaMargem, boolean serAtivo) throws ViewHelperException {
        // verifica se a parâmetros de inclusão avançada
        boolean usuPossuiIncAvancadaAde = responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO);

        // Parâmetros de Inclusão Avançada
        // Verifica opção avançada para validação de margem, somente se o usuário possuir permissão para inclusão avançada de contrato
        Boolean paramValidaMargem = (usuPossuiIncAvancadaAde && !TextHelper.isNull(reserva.getAttribute(CodedValues.PARAM_INC_AVANCADA_VALIDA_MARGEM))) ? (Boolean) reserva.getAttribute(CodedValues.PARAM_INC_AVANCADA_VALIDA_MARGEM) : ReservarMargemParametros.PADRAO_VALIDA_MARGEM;
        Boolean paramValidaPrazo = (usuPossuiIncAvancadaAde && !TextHelper.isNull(reserva.getAttribute(CodedValues.PARAM_INC_AVANCADA_VALIDA_PRAZO))) ? (Boolean) reserva.getAttribute(CodedValues.PARAM_INC_AVANCADA_VALIDA_PRAZO) : ReservarMargemParametros.PADRAO_VALIDA_PRAZO;

        String csaCodigo = convenio.getAttribute(Columns.CNV_CSA_CODIGO).toString();
        String svcCodigo = reserva.getAttribute("SVC_CODIGO").toString();
        String rseCodigo = reserva.getAttribute("RSE_CODIGO").toString();
        String adeIdentificador = (String) reserva.getAttribute("ADE_IDENTIFICADOR");

        String svcPrioridade = convenio.getAttribute(Columns.SVC_PRIORIDADE) != null ? convenio.getAttribute(Columns.SVC_PRIORIDADE).toString() : "";

        // Recupera valores padrão do sistema, minimo e maximo para os contratos.
        BigDecimal paramCseMin = null;
        BigDecimal paramCseMax = null;

        String mascaraIdentificador = (paramSvc != null) ? paramSvc.getTpsMascaraIdentificadorAde() : (String) paramCnv.get(CodedValues.TPS_MASCARA_IDENTIFICADOR_ADE);
        if (!responsavel.isSer() && !TextHelper.isNull(mascaraIdentificador) && !TextHelper.isNull(adeIdentificador)) {
            try {
                adeIdentificador = TextHelper.aplicarMascara(adeIdentificador, mascaraIdentificador);
            } catch (ZetraException ex) {
                throw new ViewHelperException("mensagem.erro.ade.identificador.invalido", responsavel, ex);
            }
        }

        // Busca os parâmetros de sistema necessários
        ParamSist paramSist = ParamSist.getInstance();

        boolean temControleCompulsorios = (ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_ESTOQUE, CodedValues.TPC_SIM, responsavel) &&
                                           ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, CodedValues.TPC_SIM, responsavel));

        Object objParamCseMin = paramSist.getParam(CodedValues.TPC_VLR_PADRAO_MINIMO_CONTRATO, responsavel);
        if (!TextHelper.isNull(objParamCseMin)) {
            paramCseMin = new BigDecimal(objParamCseMin.toString().replaceAll(",","."));
        }

        Object objParamCseMax = paramSist.getParam(CodedValues.TPC_VLR_PADRAO_MAXIMO_CONTRATO, responsavel);
        if (!TextHelper.isNull(objParamCseMax)) {
            paramCseMax = new BigDecimal(objParamCseMax.toString().replaceAll(",","."));
        }

        // Busca os parâmetros de seriço necessários
        String tpsPseVlr = null;

        // TPS_CARENCIA_MINIMA
        tpsPseVlr = (paramSvc != null) ? paramSvc.getTpsCarenciaMinima() : (String) paramCnv.get(CodedValues.TPS_CARENCIA_MINIMA);
        int carenciaMinCse = (tpsPseVlr != null && !tpsPseVlr.equals("")) ? Integer.parseInt(tpsPseVlr) : 0;

        // TPS_CARENCIA_MAXIMA
        tpsPseVlr = (paramSvc != null) ? paramSvc.getTpsCarenciaMaxima() : (String) paramCnv.get(CodedValues.TPS_CARENCIA_MAXIMA);
        int carenciaMaxCse = (tpsPseVlr != null && !tpsPseVlr.equals("")) ? Integer.parseInt(tpsPseVlr) : 99;

        // TPS_SERVICO_COMPULSORIO
        boolean servicoCompulsorio = false;
        if (temControleCompulsorios) {
            if (paramSvc != null) {
                servicoCompulsorio = paramSvc.isTpsServicoCompulsorio();
            } else {
                tpsPseVlr = (String) paramCnv.get(CodedValues.TPS_SERVICO_COMPULSORIO);
                servicoCompulsorio = (tpsPseVlr != null && tpsPseVlr.equals("1"));
            }
        }

        BigDecimal vlrMin = null;
        BigDecimal vlrMax = null;

        final List<TransferObject> paramSvcCsa = getParamSvcCsa(svcCodigo, csaCodigo, responsavel);
        for (final TransferObject vo : paramSvcCsa) {
            try {
                if (CodedValues.TPS_VLR_MINIMO_CONTRATO.equals(vo.getAttribute(Columns.TPS_CODIGO)) && !TextHelper.isNull(vo.getAttribute(Columns.PSC_VLR))) {
                    vlrMin = new BigDecimal(NumberHelper.reformat(vo.getAttribute(Columns.PSC_VLR).toString(), NumberHelper.getLang(), "en"));
                }

                if (CodedValues.TPS_VLR_MAXIMO_CONTRATO.equals(vo.getAttribute(Columns.TPS_CODIGO)) && !TextHelper.isNull(vo.getAttribute(Columns.PSC_VLR))) {
                    vlrMax = new BigDecimal(NumberHelper.reformat(vo.getAttribute(Columns.PSC_VLR).toString(), NumberHelper.getLang(), "en"));
                }
            } catch (ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ViewHelperException(ex);
            }
        }

        // Se o valor mínimo não foi configurado pela csa, recebe o valor configurado pelo parâmetro de serviço
        if(TextHelper.isNull(vlrMin)) {
            // TPS_VLR_MINIMO_CONTRATO
            tpsPseVlr = (paramSvc != null) ? paramSvc.getTpsVlrMinimoContrato() : (String) paramCnv.get(CodedValues.TPS_VLR_MINIMO_CONTRATO);
            // Se o valor mínimo for null ou vazio, o vlrMin recebe o valor mínimo padrão do sistema
            if (tpsPseVlr == null || tpsPseVlr.equals("")) {
                vlrMin = paramCseMin;
            } else {
                vlrMin = new BigDecimal(tpsPseVlr);
            }
        }

        // Se o valor máximo não foi configurado pela csa, recebe o valor configurado pelo parâmetro de serviço
        if(TextHelper.isNull(vlrMax)) {
            // TPS_VLR_MAXIMO_CONTRATO
            tpsPseVlr = (paramSvc != null) ? paramSvc.getTpsVlrMaximoContrato() : (String) paramCnv.get(CodedValues.TPS_VLR_MAXIMO_CONTRATO);
            // Se o valor máximo for null ou vazio, vlrMax recebe o valor máximo padrão do sistema
            if (tpsPseVlr == null || tpsPseVlr.equals("")) {
                vlrMax = paramCseMax;
            } else {
                vlrMax = new BigDecimal(tpsPseVlr);
            }
        }

        // TPS_POSSUI_CONTROLE_TETO_DESCONTO
        tpsPseVlr = (paramSvc != null) ? paramSvc.getTpsPossuiControleTetoDesconto() : (String) paramCnv.get(CodedValues.TPS_POSSUI_CONTROLE_TETO_DESCONTO);
        String controleTetoDesconto = tpsPseVlr;

        // TPS_TIPO_VLR
        tpsPseVlr = (paramSvc != null) ? paramSvc.getTpsTipoVlr() : (String) paramCnv.get(CodedValues.TPS_TIPO_VLR);
        String tipoVlr = (tpsPseVlr != null && !tpsPseVlr.equals("")) ? tpsPseVlr : CodedValues.TIPO_VLR_FIXO;
        String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr);

        // TPS_INCIDE_MARGEM
        tpsPseVlr = (paramSvc != null) ? paramSvc.getTpsIncideMargem().toString() : (String) paramCnv.get(CodedValues.TPS_INCIDE_MARGEM);
        Short incMargem = (tpsPseVlr != null && !tpsPseVlr.equals("")) ? Short.valueOf(tpsPseVlr) : CodedValues.INCIDE_MARGEM_SIM;

        // TPS_ADE_VLR
        tpsPseVlr = (paramSvc != null) ? paramSvc.getTpsAdeVlr() : (String) paramCnv.get(CodedValues.TPS_ADE_VLR);
        BigDecimal adeVlrPadrao = (tpsPseVlr != null && !tpsPseVlr.equals("")) ? new BigDecimal(tpsPseVlr) : null;

        // TPS_ALTERA_ADE_VLR
        boolean alteraAdeVlr = true;
        if (paramSvc != null) {
            alteraAdeVlr = paramSvc.isTpsAlteraAdeVlr();
        } else {
            tpsPseVlr = (String) paramCnv.get(CodedValues.TPS_ALTERA_ADE_VLR);
            alteraAdeVlr = (tpsPseVlr == null || tpsPseVlr.equals("1"));
        }

        // TPS_VLR_LIMITE_ADE_SEM_MARGEM
        tpsPseVlr = (paramSvc != null) ? paramSvc.getTpsVlrLimiteAdeSemMargem() : (String) paramCnv.get(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM);
        BigDecimal vlrLimite = (tpsPseVlr != null && !tpsPseVlr.equals("")) ? new BigDecimal(tpsPseVlr) : new BigDecimal("0.00");

        // TPS_MAX_PRAZO
        String operacao = (String) reserva.getAttribute("OPERACAO");
        tpsPseVlr = (paramSvc != null) ? ((!TextHelper.isNull(operacao)) && (CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) || operacao.equalsIgnoreCase(CodedValues.OP_COMPRAR_CONTRATO))) ?
                     (!TextHelper.isNull(paramSvc.getTpsMaxPrazoRenegociacao())) ? paramSvc.getTpsMaxPrazoRenegociacao() : paramSvc.getTpsMaxPrazo() : paramSvc.getTpsMaxPrazo() : (String) paramCnv.get(CodedValues.TPS_MAX_PRAZO);
        int maxPrazo = (paramValidaPrazo && tpsPseVlr != null && !tpsPseVlr.equals("")) ? Integer.parseInt(tpsPseVlr) : -1;

        // Parâmetros de convênio
        boolean permitePrazoMaiorContSer = (convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO") != null && convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO").equals("S"));
        int carenciaMinima = (convenio.getAttribute("CARENCIA_MINIMA") != null && !convenio.getAttribute("CARENCIA_MINIMA").toString().equals("")) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MINIMA").toString()) : 0;
        int carenciaMaxima = (convenio.getAttribute("CARENCIA_MAXIMA") != null && !convenio.getAttribute("CARENCIA_MAXIMA").toString().equals("")) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MAXIMA").toString()) : 99;

        // Dados da nova reserva
        int adePrazo = (reserva.getAttribute("ADE_PRAZO") != null && !reserva.getAttribute("ADE_PRAZO").toString().equals("")) ? Integer.parseInt(reserva.getAttribute("ADE_PRAZO").toString()) : -1;
        int adeCarencia = (reserva.getAttribute("ADE_CARENCIA") != null && !reserva.getAttribute("ADE_CARENCIA").toString().equals("")) ? Integer.parseInt(reserva.getAttribute("ADE_CARENCIA").toString()) : -1;
        int rsePrazo = (reserva.getAttribute("RSE_PRAZO") != null && !reserva.getAttribute("RSE_PRAZO").toString().equals("")) ? Integer.parseInt(reserva.getAttribute("RSE_PRAZO").toString()) : -1;
        BigDecimal adeVlr = (reserva.getAttribute("ADE_VLR") != null && !reserva.getAttribute("ADE_VLR").toString().equals("")) ? new BigDecimal(reserva.getAttribute("ADE_VLR").toString()) : new BigDecimal("0.00");
        String adePeriodicidade = ((String) reserva.getAttribute("ADE_PERIODICIDADE"));

        // Se o serviço ou servidor tem prazo máximo, porém é contrato quinzenal, a unidade do prazo máximo
        // será em meses, portanto ao comparar com o contrato quinzenal, deve multiplicar por 2
        if (!PeriodoHelper.folhaMensal(responsavel)) {
            if (!CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
                maxPrazo = (maxPrazo > 1) ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(maxPrazo, responsavel) : maxPrazo;
                rsePrazo = (rsePrazo > 0) ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(rsePrazo, responsavel) : rsePrazo;
            }
        }

        // Se prazo deve ser determinado e o prazo determinado for maior do que maxPrazo
        if (maxPrazo > 0 && (adePrazo == -1 || adePrazo > maxPrazo)) {
            throw new ViewHelperException("mensagem.erro.prazo.maior.maximo", responsavel, String.valueOf(maxPrazo));
        }

        // Se o prazo é indeterminado e foi escolhido prazo
        if (maxPrazo == 0 && adePrazo != -1) {
            throw new ViewHelperException("mensagem.erro.prazo.determinado", responsavel);
        }

        // Se o prazo foi informado como Zero
        if (adePrazo == 0) {
            throw new ViewHelperException("mensagem.erro.prazo.zero", responsavel);
        }

        // Se o valor de carencia for menor ou maior que os valores pre-definidos indica um erro
        int[] carenciaPermitida = getCarenciaPermitida(carenciaMinima, carenciaMaxima, carenciaMinCse, carenciaMaxCse);
        int carenciaMinPermitida = carenciaPermitida[0];
        int carenciaMaxPermitida = carenciaPermitida[1];

        if (!PeriodoHelper.folhaMensal(responsavel)) {
            if (!CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
                carenciaMinPermitida = PeriodoHelper.converterPrazoMensalEmPeriodicidade(carenciaMinPermitida, responsavel);
                carenciaMaxPermitida = PeriodoHelper.converterPrazoMensalEmPeriodicidade(carenciaMaxPermitida, responsavel);
            }
        }

        if ((adeCarencia < carenciaMinPermitida) || (adeCarencia > carenciaMaxPermitida)) {
            if (carenciaMaxPermitida > carenciaMinPermitida) {
                throw new ViewHelperException("mensagem.erro.carencia.entre.min.max", responsavel, String.valueOf(carenciaMinPermitida), String.valueOf(carenciaMaxPermitida));
            } else if (carenciaMaxPermitida < carenciaMinPermitida) {
                throw new ViewHelperException("mensagem.erro.carencia.menor.max", responsavel, String.valueOf(carenciaMaxPermitida));
            } else if (carenciaMaxPermitida == carenciaMinPermitida) {
                throw new ViewHelperException("mensagem.erro.carencia.fixa", responsavel, String.valueOf(carenciaMinPermitida));
            }
        }

        // Se não pode alterar valor da ADE e o valor passado é diferente do padrão
        if (!alteraAdeVlr) {
            if (tipoVlr.equals(CodedValues.TIPO_VLR_TOTAL_MARGEM)) {
                // Compara o valor informado com a margem do servidor
                BigDecimal margemConsignavel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, incMargem, responsavel).getMargemRestante();
                if (margemConsignavel.compareTo(adeVlr) != 0) {
                    throw new ViewHelperException("mensagem.erro.valor.parcela.nao.permitido", responsavel, NumberHelper.format(margemConsignavel.doubleValue(), NumberHelper.getLang(), true));
                }
            } else if (adeVlrPadrao != null && adeVlrPadrao.compareTo(adeVlr) != 0) {
                // Compara o valor informado com o valor padrão
                throw new ViewHelperException("mensagem.erro.valor.parcela.nao.permitido", responsavel, NumberHelper.format(adeVlrPadrao.doubleValue(), NumberHelper.getLang(), true));
            }
        }

        // Verifica se o valor não é maior que zero
        if (adeVlr.signum() < 0 || (alteraAdeVlr && adeVlr.signum() == 0)) {
            try {
                ParametroDelegate parDelegate = new ParametroDelegate();
                if (!parDelegate.permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel)) {
                    throw new ViewHelperException("mensagem.valorParcelaMenorIgualZero", responsavel);
                }
            } catch (ParametroControllerException ex) {
                throw new ViewHelperException(ex);
            }
        }

        // Verifica se o prazo do servidor não é igual a Zero
        if (rsePrazo == 0 && adePrazo != -1 && !permitePrazoMaiorContSer && paramValidaPrazo) {
            throw new ViewHelperException("mensagem.erro.prazo.servidor.zero", responsavel);
        }

        // Verifica se a quantidade de parcelas não é maior que o prazo do servidor
        int prazoTotal = adePrazo + (adeCarencia < 0 ? 0 : adeCarencia);
        if (rsePrazo != -1 && adePrazo != -1 && (prazoTotal > rsePrazo) && !permitePrazoMaiorContSer && paramValidaPrazo) {
            throw new ViewHelperException("mensagem.erro.prazo.total.maior.servidor", responsavel, String.valueOf(rsePrazo));
        }

        // Verifica valores minimo e maximo a partir do valor proposto.
        // Faz as comparações necessárias com o adeVlr e os valores minimos e máximos
        if (vlrMin != null && adeVlr.compareTo(vlrMin) < 0) {
            // se adeVlr é menor que o valor minimo se for, lança mensagem de advertencia
            throw new ViewHelperException("mensagem.erro.valor.parcela.minimo", responsavel, labelTipoVlr, NumberHelper.format(vlrMin.doubleValue(), NumberHelper.getLang()));
        } else if (vlrMax != null && adeVlr.compareTo(vlrMax) > 0) {
            // se adeVlr é maior que o valor maximo se for, lança mensagem de advertencia
            throw new ViewHelperException("mensagem.erro.valor.parcela.maximo", responsavel, labelTipoVlr, NumberHelper.format(vlrMax.doubleValue(), NumberHelper.getLang()));
        }

        validaMargem = validaMargem && paramValidaMargem;

        // Valida a margem
        if (validaMargem) {
            try {
                BigDecimal valor = adeVlr.subtract(vlrLimite);

                if (servicoCompulsorio) {
                    // Busca a margem disponível para compulsório e subtrai este valor da margem para a validação
                    boolean controlaMargem = !ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel);
                    ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
                    BigDecimal margemDisponivelCompulsorio = consultarMargemController.getMargemDisponivelCompulsorio(rseCodigo, svcCodigo, svcPrioridade, incMargem, controlaMargem, null, responsavel);
                    if (margemDisponivelCompulsorio != null) {
                        valor = valor.subtract(margemDisponivelCompulsorio);
                    }
                    if (valor.signum() > 0) {
                        LogDelegate log = new LogDelegate (responsavel, Log.REGISTRO_SERVIDOR, Log.RESERVAR_MARGEM, Log.LOG_ERRO);
                        log.setRegistroServidor(rseCodigo);
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.margemInsuficiente", responsavel));
                        log.write();
                        throw new ViewHelperException("mensagem.margemInsuficiente", responsavel);
                    }
                } else {
                    AutorizacaoController adeController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
                    if (!adeController.temMargem(rseCodigo, svcCodigo, valor, incMargem, serAtivo, responsavel)) {
                        LogDelegate log = new LogDelegate (responsavel, Log.REGISTRO_SERVIDOR, Log.RESERVAR_MARGEM, Log.LOG_ERRO);
                        log.setRegistroServidor(rseCodigo);
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.margemInsuficiente", responsavel));
                        log.write();
                        throw new ViewHelperException("mensagem.margemInsuficiente", responsavel);
                    }
                }
            } catch (Exception ex) {
                if (ex.getClass().equals(ViewHelperException.class)) {
                    throw (ViewHelperException) ex;
                } else {
                    throw new ViewHelperException(ex);
                }
            }
        }

            // Verifica se o serviço tem controle de teto de desconto
        if (controleTetoDesconto != null && !controleTetoDesconto.equals("")) {
            if (controleTetoDesconto.equals(CodedValues.CONTROLA_TETO_DESCONTO_PELO_CARGO)) {
                // Verifica se o valor está dentro do teto máximo
                try {
                    AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                    adeDelegate.validarTetoDescontoPeloCargo(rseCodigo, svcCodigo, adeVlr, responsavel);
                } catch (AutorizacaoControllerException ex) {
                    throw new ViewHelperException(ex);
                }
            }
        }
    }

    /**
     * Determina a carência mínima e máxima permitida de acordo com os parâmetros
     * do consignante e da consignatária. Retorna um array onde a posição zero
     * é a carência mínima e a posição um é a carência máxima.
     * @param carenciaMinima
     * @param carenciaMaxima
     * @param carenciaMinCse
     * @param carenciaMaxCse
     * @return
     */
    public static int[] getCarenciaPermitida(int carenciaMinima, int carenciaMaxima, int carenciaMinCse, int carenciaMaxCse) {
        int carenciaMinPermitida = (carenciaMinima > carenciaMinCse) ? carenciaMinima : carenciaMinCse;
        int carenciaMaxPermitida = (carenciaMaxima < carenciaMaxCse) ? carenciaMaxima : carenciaMaxCse;

        carenciaMinPermitida = (carenciaMinPermitida > carenciaMaxCse) ? carenciaMaxCse : carenciaMinPermitida;
        carenciaMaxPermitida = (carenciaMaxPermitida < carenciaMinCse) ? carenciaMinCse : carenciaMaxPermitida;

        return new int[]{carenciaMinPermitida, carenciaMaxPermitida};
    }

    /**
     * Determina a função a partir da ação executada
     * @param acao
     * @return
     */
    public static String getFuncaoPorAcao(String acao) {
        String funCodigo = null;
        if (acao != null) {
            if (acao.equalsIgnoreCase("RESERVAR")) {
                funCodigo = CodedValues.FUN_RES_MARGEM;
            } else if (acao.equalsIgnoreCase("RENEGOCIAR")) {
                funCodigo = CodedValues.FUN_RENE_CONTRATO;
            } else if (acao.equalsIgnoreCase("COMPRAR")) {
                funCodigo = CodedValues.FUN_COMP_CONTRATO;
            } else if (acao.equalsIgnoreCase("ALONGAR")) {
                funCodigo = CodedValues.FUN_ALONGAR_CONTRATO;
            } else if (acao.equalsIgnoreCase("DUPLICAR")) {
                funCodigo = CodedValues.FUN_DUPLICAR_PARCELA;
            } else if (acao.equalsIgnoreCase("LEILAO")) {
                funCodigo = CodedValues.FUN_SOLICITAR_LEILAO_REVERSO;
            }
        }
        return funCodigo;
    }
}
