package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_IOF;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_MENS_VINC;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_TAC;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_CARENCIA;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.NOVO_ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.Anexo;

/**
 * <p>Title: AlterarConsignacaoAssembler</p>
 * <p>Description: Assembler para AlterarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class AlterarConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlterarConsignacaoAssembler.class);

    private AlterarConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v1.AlterarConsignacao alterarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, alterarConsignacao.getUsuario());
        parametros.put(SENHA, alterarConsignacao.getSenha());
        parametros.put(SER_SENHA, getValue(alterarConsignacao.getSenhaServidor()));
        parametros.put(TOKEN, alterarConsignacao.getTokenAutServidor());
        parametros.put(VALOR_PARCELA, alterarConsignacao.getValorParcela());
        parametros.put(ADE_INDICE, getValue(alterarConsignacao.getIndice()));
        final Double adeVlr = alterarConsignacao.getValorParcela();
        if (adeVlr.equals(Double.NaN)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        if ((alterarConsignacao.getPrazo() == Integer.MAX_VALUE) || (alterarConsignacao.getPrazo() <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, alterarConsignacao.getPrazo());
        }

        final Double vlrLib = getValue(alterarConsignacao.getValorLiberado());
        if ((vlrLib == null) || vlrLib.equals(Double.NaN)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }

        final Double vlrTac = getValue(alterarConsignacao.getValorTac());
        if ((vlrTac == null) || vlrTac.equals(Double.NaN)) {
            parametros.put(ADE_VLR_TAC, null);
        } else {
            parametros.put(ADE_VLR_TAC, vlrTac);
        }

        final Double vlrIof = getValue(alterarConsignacao.getValorIof());
        if ((vlrIof == null) || vlrIof.equals(Double.NaN)) {
            parametros.put(ADE_VLR_IOF, null);
        } else {
            parametros.put(ADE_VLR_IOF, vlrIof);
        }

        final Double vlrMenVinc = getValue(alterarConsignacao.getValorMensVin());
        if ((vlrMenVinc == null) || vlrMenVinc.equals(Double.NaN)) {
            parametros.put(ADE_VLR_MENS_VINC, null);
        } else {
            parametros.put(ADE_VLR_MENS_VINC, vlrMenVinc);
        }

        parametros.put(ADE_IDENTIFICADOR, getValue(alterarConsignacao.getAdeIdentificador()));
        parametros.put(NOVO_ADE_IDENTIFICADOR, getValue(alterarConsignacao.getNovoAdeIdentificador()));
        final Long adeNumero = getValue(alterarConsignacao.getAdeNumero());
        if ((adeNumero == null) || (adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(alterarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(alterarConsignacao.getCliente()));

        final Double adeTaxaJuros = getValue(alterarConsignacao.getTaxaJuros());
        if ((adeTaxaJuros == null) || adeTaxaJuros.equals(Double.NaN)) {
            parametros.put(ADE_TAXA_JUROS, null);
        } else {
            parametros.put(ADE_TAXA_JUROS, adeTaxaJuros);
        }
        final Integer carencia = getValue(alterarConsignacao.getCarencia());
        if ((carencia == null) || (carencia == Integer.MAX_VALUE) || (carencia <= 0)) {
            parametros.put(ADE_CARENCIA, null);
        } else {
            parametros.put(ADE_CARENCIA, carencia);
        }
        parametros.put(SER_LOGIN, alterarConsignacao.getLoginServidor());

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v3.AlterarConsignacao alterarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, alterarConsignacao.getUsuario());
        parametros.put(SENHA, alterarConsignacao.getSenha());
        parametros.put(SER_SENHA, getValue(alterarConsignacao.getSenhaServidor()));
        parametros.put(TOKEN, alterarConsignacao.getTokenAutServidor());
        parametros.put(VALOR_PARCELA, alterarConsignacao.getValorParcela());
        parametros.put(ADE_INDICE, alterarConsignacao.getIndice());
        final Double adeVlr = alterarConsignacao.getValorParcela();
        if (adeVlr.equals(Double.NaN)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        if ((alterarConsignacao.getPrazo() == Integer.MAX_VALUE) || (alterarConsignacao.getPrazo() <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, alterarConsignacao.getPrazo());
        }

        final Double vlrLib = getValue(alterarConsignacao.getValorLiberado());
        if ((vlrLib == null) || vlrLib.equals(Double.NaN)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }

        final Double vlrTac = getValue(alterarConsignacao.getValorTac());
        if ((vlrTac == null) || vlrTac.equals(Double.NaN)) {
            parametros.put(ADE_VLR_TAC, null);
        } else {
            parametros.put(ADE_VLR_TAC, vlrTac);
        }

        final Double vlrIof = getValue(alterarConsignacao.getValorIof());
        if ((vlrIof == null) || vlrIof.equals(Double.NaN)) {
            parametros.put(ADE_VLR_IOF, null);
        } else {
            parametros.put(ADE_VLR_IOF, vlrIof);
        }

        final Double vlrMenVinc = getValue(alterarConsignacao.getValorMensVin());
        if ((vlrMenVinc == null) || vlrMenVinc.equals(Double.NaN)) {
            parametros.put(ADE_VLR_MENS_VINC, null);
        } else {
            parametros.put(ADE_VLR_MENS_VINC, vlrMenVinc);
        }

        parametros.put(ADE_IDENTIFICADOR, getValue(alterarConsignacao.getAdeIdentificador()));
        parametros.put(NOVO_ADE_IDENTIFICADOR, getValue(alterarConsignacao.getNovoAdeIdentificador()));
        final Long adeNumero = getValue(alterarConsignacao.getAdeNumero());
        if ((adeNumero == null) || (adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(alterarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(alterarConsignacao.getCliente()));

        final Double adeTaxaJuros = getValue(alterarConsignacao.getTaxaJuros());
        if ((adeTaxaJuros == null) || adeTaxaJuros.equals(Double.NaN)) {
            parametros.put(ADE_TAXA_JUROS, null);
        } else {
            parametros.put(ADE_TAXA_JUROS, adeTaxaJuros);
        }
        final Integer carencia = getValue(alterarConsignacao.getCarencia());
        if ((carencia == null) || (carencia == Integer.MAX_VALUE) || (carencia <= 0)) {
            parametros.put(ADE_CARENCIA, null);
        } else {
            parametros.put(ADE_CARENCIA, carencia);
        }
        parametros.put(SER_LOGIN, alterarConsignacao.getLoginServidor());

        final String periodo = getValue(alterarConsignacao.getPeriodo());
        if (!TextHelper.isNull(periodo)) {
            parametros.put(PERIODO, periodo);
        }

        if (getValue(alterarConsignacao.getAnexo()) != null) {
            try {
                final Anexo anexo = new Anexo();
                BeanUtils.copyProperties(anexo, alterarConsignacao.getAnexo().getValue());
                parametros.put(ANEXO, anexo);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.AlterarConsignacao alterarConsignacao) {
       final com.zetra.econsig.webservice.soap.operacional.v3.AlterarConsignacao alterarConsignacaoV3 = new com.zetra.econsig.webservice.soap.operacional.v3.AlterarConsignacao();
        try {
            BeanUtils.copyProperties(alterarConsignacaoV3, alterarConsignacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return toMap(alterarConsignacaoV3);
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v7.AlterarConsignacao alterarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, alterarConsignacao.getUsuario());
        parametros.put(SENHA, alterarConsignacao.getSenha());
        parametros.put(SER_SENHA, getValue(alterarConsignacao.getSenhaServidor()));
        parametros.put(TOKEN, alterarConsignacao.getTokenAutServidor());
        parametros.put(VALOR_PARCELA, alterarConsignacao.getValorParcela());
        parametros.put(ADE_INDICE, getValue(alterarConsignacao.getIndice()));
        final Double adeVlr = alterarConsignacao.getValorParcela();
        if (adeVlr.equals(Double.NaN)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        if ((alterarConsignacao.getPrazo() == Integer.MAX_VALUE) || (alterarConsignacao.getPrazo() <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, alterarConsignacao.getPrazo());
        }

        final Double vlrLib = getValue(alterarConsignacao.getValorLiberado());
        if ((vlrLib == null) || vlrLib.equals(Double.NaN)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }

        final Double vlrTac = getValue(alterarConsignacao.getValorTac());
        if ((vlrTac == null) || vlrTac.equals(Double.NaN)) {
            parametros.put(ADE_VLR_TAC, null);
        } else {
            parametros.put(ADE_VLR_TAC, vlrTac);
        }

        final Double vlrIof = getValue(alterarConsignacao.getValorIof());
        if ((vlrIof == null) || vlrIof.equals(Double.NaN)) {
            parametros.put(ADE_VLR_IOF, null);
        } else {
            parametros.put(ADE_VLR_IOF, vlrIof);
        }

        final Double vlrMenVinc = getValue(alterarConsignacao.getValorMensVin());
        if ((vlrMenVinc == null) || vlrMenVinc.equals(Double.NaN)) {
            parametros.put(ADE_VLR_MENS_VINC, null);
        } else {
            parametros.put(ADE_VLR_MENS_VINC, vlrMenVinc);
        }

        parametros.put(ADE_IDENTIFICADOR, getValue(alterarConsignacao.getAdeIdentificador()));
        parametros.put(NOVO_ADE_IDENTIFICADOR, getValue(alterarConsignacao.getNovoAdeIdentificador()));
        final Long adeNumero = getValue(alterarConsignacao.getAdeNumero());
        if ((adeNumero == null) || (adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(alterarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(alterarConsignacao.getCliente()));

        final Double adeTaxaJuros = getValue(alterarConsignacao.getTaxaJuros());
        if ((adeTaxaJuros == null) || adeTaxaJuros.equals(Double.NaN)) {
            parametros.put(ADE_TAXA_JUROS, null);
        } else {
            parametros.put(ADE_TAXA_JUROS, adeTaxaJuros);
        }
        final Integer carencia = getValue(alterarConsignacao.getCarencia());
        if ((carencia == null) || (carencia == Integer.MAX_VALUE) || (carencia <= 0)) {
            parametros.put(ADE_CARENCIA, null);
        } else {
            parametros.put(ADE_CARENCIA, carencia);
        }
        parametros.put(SER_LOGIN, alterarConsignacao.getLoginServidor());

        final String periodo = getValue(alterarConsignacao.getPeriodo());
        if (!TextHelper.isNull(periodo)) {
            parametros.put(PERIODO, periodo);
        }

        if (getValue(alterarConsignacao.getAnexo()) != null) {
            try {
                final Anexo anexo = new Anexo();
                BeanUtils.copyProperties(anexo, alterarConsignacao.getAnexo().getValue());
                parametros.put(ANEXO, anexo);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        parametros.put(TMO_OBS, getValue(alterarConsignacao.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(alterarConsignacao.getCodigoMotivoOperacao()));

        return parametros;
    }
}