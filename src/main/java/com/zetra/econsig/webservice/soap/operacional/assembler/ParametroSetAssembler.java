package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.DADOS_SISTEMA;
import static com.zetra.econsig.webservice.CamposAPI.PARAMETRO_SET;

import java.util.Date;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.ConsultarParametrosCommand;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.ObjectFactory;
import com.zetra.econsig.webservice.soap.operacional.v1.ParametroSet;

/**
 * <p>Title: ParametroSetAssembler</p>
 * <p>Description: Assembler para ParametroSet.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ParametroSetAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParametroSetAssembler.class);

    private static final int SEM_LIMITE_DE_PRAZO = -1;

    private ParametroSetAssembler() {
    }

    public static ParametroSet toParametroSetV1(Map<CamposAPI, Object> parametros) {
        final ObjectFactory factory = new ObjectFactory();
        final ParametroSet paramSet = new ParametroSet();

        final CustomTransferObject resultado = (CustomTransferObject) parametros.get(PARAMETRO_SET);

        paramSet.setSvcDescricao((String) resultado.getAttribute(ConsultarParametrosCommand.SVC_DESCRICAO));
        paramSet.setTamMinMatriculaServidor((Integer) resultado.getAttribute(ConsultarParametrosCommand.TAM_MIN_MATR_SRV));
        paramSet.setTamMaxMatriculaServidor((Integer) resultado.getAttribute(ConsultarParametrosCommand.TAMANHO_MATRICULA_MAX));
        paramSet.setExigeCpfMatriculaPesquisa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.REQUER_MATRICULA_E_CPF));
        paramSet.setValidaCpfPesquisa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VALIDA_CPF_PESQUISA_SERVIDOR));
        paramSet.setAlteraAutMargemNegativa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.REDUZ_VLR_ADE_MARGEM_NEG));
        paramSet.setExigeSenhaServConsMargem((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_SENHA_SERVIDOR_CONS_MARGEM));
        paramSet.setValidaInfoBancaria((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VALIDAR_INF_BANCARIA_NA_RESERVA));
        paramSet.setExigeInfoBancaria((Boolean) resultado.getAttribute(ConsultarParametrosCommand.INFO_BANCARIA_OBRIGATORIA));
        paramSet.setExigeTac((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CADASTRO_VALOR_TAC));
        paramSet.setQtdMaxParcelas(factory.createParametroSetQtdMaxParcelas((!TextHelper.isNull(resultado.getAttribute(ConsultarParametrosCommand.QTD_MAX_PARCELAS))) ? Integer.valueOf((String) resultado.getAttribute(ConsultarParametrosCommand.QTD_MAX_PARCELAS)) : SEM_LIMITE_DE_PRAZO));
        paramSet.setExigeCadMensVinc((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VLR_MENSALIDADE_VINC));
        paramSet.setExigeCadVlrLiberado((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VLR_LIQUIDO_LIBERADO));
        paramSet.setExigeIof((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VALOR_IOF));
        paramSet.setValidaDataNascReserva((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VALIDA_DATA_NASCIMENTO_NA_RESERVA));
        paramSet.setExigeSenhaServReservarRenegociar((Boolean) resultado.getAttribute(ConsultarParametrosCommand.SENHA_SERVIDOR_OBRIGATORIA_PARA_CSA));
        paramSet.setExigeSenhaServAltContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_SENHA_ALTERACAO_CONTRATOS));
        paramSet.setPermiteAltContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.PERMITE_ALTERACAO_CONTRATOS));
        paramSet.setPermiteRenegociarContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.PERMITE_RENEGOCIACAO));
        paramSet.setQtdMinPrdPgsParaRenegociarAut((!TextHelper.isNull(resultado.getAttribute(ConsultarParametrosCommand.MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO))) ? Integer.valueOf((String) resultado.getAttribute(ConsultarParametrosCommand.MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO)) : 0);
        paramSet.setVisualizaMargem((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VISUALIZA_MARGEM));
        paramSet.setVisualizaMargemNegativa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VISUALIZA_MARGEM_NEGATIVA));
        paramSet.setDiaCorte((Short) resultado.getAttribute(ConsultarParametrosCommand.DIA_DE_CORTE));
        try {
            paramSet.setPeriodoAtual(toXMLGregorianCalendar((Date) resultado.getAttribute(ConsultarParametrosCommand.PERIODO_ATUAL), false));
        } catch (final DatatypeConfigurationException e) {
            LOG.warn("ERRO AO RECUPERAR PERIODO ATUAL");
        }
        paramSet.setPermiteCompraContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.PERMITE_COMPRAR_CONTRATOS));
        paramSet.setDiasInfoSaldoDevedor(factory.createParametroSetDiasInfoSaldoDevedor(resultado.getAttribute(ConsultarParametrosCommand.DIAS_INFO_SALDO_DEVEDOR) != null ? (Short) resultado.getAttribute(ConsultarParametrosCommand.DIAS_INFO_SALDO_DEVEDOR) : 0));
        paramSet.setDiasAprovSaldoDevedor(factory.createParametroSetDiasAprovSaldoDevedor(resultado.getAttribute(ConsultarParametrosCommand.DIAS_APRV_SALDO_DEVEDOR) != null ? (Short) resultado.getAttribute(ConsultarParametrosCommand.DIAS_APRV_SALDO_DEVEDOR) : 0));
        paramSet.setDiasInfoPgSaldoDevedor(factory.createParametroSetDiasInfoPgSaldoDevedor(resultado.getAttribute(ConsultarParametrosCommand.DIAS_INFO_PG_SALDO_DEVEDOR) != null ? (Short) resultado.getAttribute(ConsultarParametrosCommand.DIAS_INFO_PG_SALDO_DEVEDOR) : 0));
        paramSet.setDiasLiquidacaoAdeCompra(factory.createParametroSetDiasLiquidacaoAdeCompra(resultado.getAttribute(ConsultarParametrosCommand.DIAS_PARA_LIQUIDAR_CONTRATO) != null ? (Short) resultado.getAttribute(ConsultarParametrosCommand.DIAS_PARA_LIQUIDAR_CONTRATO) : 0));

        return paramSet;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v2.ParametroSet toParametroSetV2(Map<CamposAPI, Object> parametros) {
        final com.zetra.econsig.webservice.soap.operacional.v2.ObjectFactory factory = new com.zetra.econsig.webservice.soap.operacional.v2.ObjectFactory();
        final com.zetra.econsig.webservice.soap.operacional.v2.ParametroSet paramSet = new com.zetra.econsig.webservice.soap.operacional.v2.ParametroSet();

        final CustomTransferObject resultado = (CustomTransferObject) parametros.get(PARAMETRO_SET);

        paramSet.setSvcDescricao((String) resultado.getAttribute(ConsultarParametrosCommand.SVC_DESCRICAO));
        paramSet.setTamMinMatriculaServidor((Integer) resultado.getAttribute(ConsultarParametrosCommand.TAM_MIN_MATR_SRV));
        paramSet.setTamMaxMatriculaServidor((Integer) resultado.getAttribute(ConsultarParametrosCommand.TAMANHO_MATRICULA_MAX));
        paramSet.setExigeCpfMatriculaPesquisa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.REQUER_MATRICULA_E_CPF));
        paramSet.setValidaCpfPesquisa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VALIDA_CPF_PESQUISA_SERVIDOR));
        paramSet.setAlteraAutMargemNegativa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.REDUZ_VLR_ADE_MARGEM_NEG));
        paramSet.setExigeSenhaServConsMargem((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_SENHA_SERVIDOR_CONS_MARGEM));
        paramSet.setValidaInfoBancaria((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VALIDAR_INF_BANCARIA_NA_RESERVA));
        paramSet.setExigeInfoBancaria((Boolean) resultado.getAttribute(ConsultarParametrosCommand.INFO_BANCARIA_OBRIGATORIA));
        paramSet.setExigeTac((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CADASTRO_VALOR_TAC));
        paramSet.setQtdMaxParcelas(factory.createParametroSetQtdMaxParcelas((!TextHelper.isNull(resultado.getAttribute(ConsultarParametrosCommand.QTD_MAX_PARCELAS))) ? Integer.parseInt((String) resultado.getAttribute(ConsultarParametrosCommand.QTD_MAX_PARCELAS)) : SEM_LIMITE_DE_PRAZO));
        paramSet.setExigeCadMensVinc((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VLR_MENSALIDADE_VINC));
        paramSet.setExigeCadVlrLiberado((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VLR_LIQUIDO_LIBERADO));
        paramSet.setExigeIof((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VALOR_IOF));
        paramSet.setValidaDataNascReserva((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VALIDA_DATA_NASCIMENTO_NA_RESERVA));
        paramSet.setExigeSenhaServReservarRenegociar((Boolean) resultado.getAttribute(ConsultarParametrosCommand.SENHA_SERVIDOR_OBRIGATORIA_PARA_CSA));
        paramSet.setExigeSenhaServAltContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_SENHA_ALTERACAO_CONTRATOS));
        paramSet.setPermiteAltContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.PERMITE_ALTERACAO_CONTRATOS));
        paramSet.setPermiteRenegociarContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.PERMITE_RENEGOCIACAO));
        paramSet.setQtdMinPrdPgsParaRenegociarAut((!TextHelper.isNull(resultado.getAttribute(ConsultarParametrosCommand.MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO))) ? Integer.parseInt((String) resultado.getAttribute(ConsultarParametrosCommand.MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO)) : 0);
        paramSet.setVisualizaMargem((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VISUALIZA_MARGEM));
        paramSet.setVisualizaMargemNegativa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VISUALIZA_MARGEM_NEGATIVA));
        paramSet.setDiaCorte((Short) resultado.getAttribute(ConsultarParametrosCommand.DIA_DE_CORTE));
        try {
            paramSet.setPeriodoAtual(toXMLGregorianCalendar((Date) resultado.getAttribute(ConsultarParametrosCommand.PERIODO_ATUAL), false));
        } catch (final DatatypeConfigurationException e) {
            LOG.warn("ERRO AO RECUPERAR PERIODO ATUAL");
        }
        paramSet.setPermiteCompraContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.PERMITE_COMPRAR_CONTRATOS));
        paramSet.setDiasInfoSaldoDevedor(factory.createParametroSetDiasInfoSaldoDevedor(resultado.getAttribute(ConsultarParametrosCommand.DIAS_INFO_SALDO_DEVEDOR) != null ? (Short) resultado.getAttribute(ConsultarParametrosCommand.DIAS_INFO_SALDO_DEVEDOR) : 0));
        paramSet.setDiasAprovSaldoDevedor(factory.createParametroSetDiasAprovSaldoDevedor(resultado.getAttribute(ConsultarParametrosCommand.DIAS_APRV_SALDO_DEVEDOR) != null ? (Short) resultado.getAttribute(ConsultarParametrosCommand.DIAS_APRV_SALDO_DEVEDOR) : 0));
        paramSet.setDiasInfoPgSaldoDevedor(factory.createParametroSetDiasInfoPgSaldoDevedor(resultado.getAttribute(ConsultarParametrosCommand.DIAS_INFO_PG_SALDO_DEVEDOR) != null ? (Short) resultado.getAttribute(ConsultarParametrosCommand.DIAS_INFO_PG_SALDO_DEVEDOR) : 0));
        paramSet.setDiasLiquidacaoAdeCompra(factory.createParametroSetDiasLiquidacaoAdeCompra(resultado.getAttribute(ConsultarParametrosCommand.DIAS_PARA_LIQUIDAR_CONTRATO) != null ? (Short) resultado.getAttribute(ConsultarParametrosCommand.DIAS_PARA_LIQUIDAR_CONTRATO) : 0));
        paramSet.setUsaCet(factory.createParametroSetUsaCet((boolean) resultado.getAttribute(com.zetra.econsig.webservice.command.entrada.v2.ConsultarParametrosCommand.USA_CET)));
        return paramSet;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v8.ParametroSet toParametroSetV8(Map<CamposAPI, Object> parametros) {
        final com.zetra.econsig.webservice.soap.operacional.v8.ObjectFactory factory = new com.zetra.econsig.webservice.soap.operacional.v8.ObjectFactory();
        final com.zetra.econsig.webservice.soap.operacional.v8.ParametroSet paramSet = new com.zetra.econsig.webservice.soap.operacional.v8.ParametroSet();

        final CustomTransferObject resultado = (CustomTransferObject) parametros.get(PARAMETRO_SET);

        final String svcDescricao = (String) resultado.getAttribute(ConsultarParametrosCommand.SVC_DESCRICAO);

        boolean possuiDescricao = !TextHelper.isNull(svcDescricao);

        paramSet.setSvcDescricao(svcDescricao);
        paramSet.setTamMinMatriculaServidor((Integer) resultado.getAttribute(ConsultarParametrosCommand.TAM_MIN_MATR_SRV));
        paramSet.setTamMaxMatriculaServidor((Integer) resultado.getAttribute(ConsultarParametrosCommand.TAMANHO_MATRICULA_MAX));
        paramSet.setExigeCpfMatriculaPesquisa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.REQUER_MATRICULA_E_CPF));
        paramSet.setValidaCpfPesquisa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VALIDA_CPF_PESQUISA_SERVIDOR));
        paramSet.setAlteraAutMargemNegativa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.REDUZ_VLR_ADE_MARGEM_NEG));
        paramSet.setExigeSenhaServConsMargem((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_SENHA_SERVIDOR_CONS_MARGEM));
        paramSet.setValidaInfoBancaria((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VALIDAR_INF_BANCARIA_NA_RESERVA));
        paramSet.setExigeInfoBancaria((Boolean) resultado.getAttribute(ConsultarParametrosCommand.INFO_BANCARIA_OBRIGATORIA));
        paramSet.setExigeTac((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CADASTRO_VALOR_TAC));
        paramSet.setQtdMaxParcelas(factory.createParametroSetQtdMaxParcelas(obterQtdMaxParcelasSeNaoForDadosSistema(resultado, possuiDescricao)));
        paramSet.setExigeCadMensVinc((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VLR_MENSALIDADE_VINC));
        paramSet.setExigeCadVlrLiberado((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VLR_LIQUIDO_LIBERADO));
        paramSet.setExigeIof((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VALOR_IOF));
        paramSet.setValidaDataNascReserva((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VALIDA_DATA_NASCIMENTO_NA_RESERVA));
        paramSet.setExigeSenhaServReservarRenegociar((Boolean) resultado.getAttribute(ConsultarParametrosCommand.SENHA_SERVIDOR_OBRIGATORIA_PARA_CSA));
        paramSet.setExigeSenhaServAltContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.EXIGE_SENHA_ALTERACAO_CONTRATOS));
        paramSet.setPermiteAltContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.PERMITE_ALTERACAO_CONTRATOS));
        paramSet.setPermiteRenegociarContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.PERMITE_RENEGOCIACAO));
        paramSet.setQtdMinPrdPgsParaRenegociarAut(obterQtdMinPrdPgsParaRenegociarAutSeNaoForDadosSistema(resultado, possuiDescricao));
        paramSet.setVisualizaMargem((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VISUALIZA_MARGEM));
        paramSet.setVisualizaMargemNegativa((Boolean) resultado.getAttribute(ConsultarParametrosCommand.VISUALIZA_MARGEM_NEGATIVA));
        paramSet.setDiaCorte((Short) resultado.getAttribute(ConsultarParametrosCommand.DIA_DE_CORTE));
        try {
            paramSet.setPeriodoAtual(toXMLGregorianCalendar((Date) resultado.getAttribute(ConsultarParametrosCommand.PERIODO_ATUAL), false));
        } catch (final DatatypeConfigurationException e) {
            LOG.warn("ERRO AO RECUPERAR PERIODO ATUAL");
        }
        paramSet.setPermiteCompraContrato((Boolean) resultado.getAttribute(ConsultarParametrosCommand.PERMITE_COMPRAR_CONTRATOS));
        paramSet.setDiasInfoSaldoDevedor(factory.createParametroSetDiasInfoSaldoDevedor(obterValorSeNaoForDadosSistema(resultado, ConsultarParametrosCommand.DIAS_INFO_SALDO_DEVEDOR, possuiDescricao)));
        paramSet.setDiasAprovSaldoDevedor(factory.createParametroSetDiasAprovSaldoDevedor(obterValorSeNaoForDadosSistema(resultado, ConsultarParametrosCommand.DIAS_APRV_SALDO_DEVEDOR, possuiDescricao)));
        paramSet.setDiasInfoPgSaldoDevedor(factory.createParametroSetDiasInfoPgSaldoDevedor(obterValorSeNaoForDadosSistema(resultado, ConsultarParametrosCommand.DIAS_INFO_PG_SALDO_DEVEDOR, possuiDescricao)));
        paramSet.setDiasLiquidacaoAdeCompra(factory.createParametroSetDiasLiquidacaoAdeCompra(obterValorSeNaoForDadosSistema(resultado, ConsultarParametrosCommand.DIAS_PARA_LIQUIDAR_CONTRATO, possuiDescricao)));
        paramSet.setUsaCet(factory.createParametroSetUsaCet((boolean) resultado.getAttribute(com.zetra.econsig.webservice.command.entrada.v2.ConsultarParametrosCommand.USA_CET)));
        return paramSet;
    }

    private static Integer obterQtdMaxParcelasSeNaoForDadosSistema(CustomTransferObject resultado, boolean possuiDescricao) {
        
        if (possuiDescricao) {
            return (!TextHelper.isNull(resultado.getAttribute(ConsultarParametrosCommand.QTD_MAX_PARCELAS))) ? Integer.parseInt((String) resultado.getAttribute(ConsultarParametrosCommand.QTD_MAX_PARCELAS)) : SEM_LIMITE_DE_PRAZO;
        } else {
            return null;
        }

    }

    private static Integer obterQtdMinPrdPgsParaRenegociarAutSeNaoForDadosSistema(CustomTransferObject resultado, boolean possuiDescricao) {
        
        if (possuiDescricao) {
            return (!TextHelper.isNull(resultado.getAttribute(ConsultarParametrosCommand.MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO))) ? Integer.parseInt((String) resultado.getAttribute(ConsultarParametrosCommand.MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO)) : 0;
        } else {
            return null;
        }

    }

    private static Short obterValorSeNaoForDadosSistema(CustomTransferObject resultado, String parametro, boolean possuiDescricao) {
        
        if (possuiDescricao) {
            return resultado.getAttribute(parametro) != null ? (Short) resultado.getAttribute(parametro) : 0;
        } else {
            return null;
        }

    }

    
}