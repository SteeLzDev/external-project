package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_IOF;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_MENS_VINC;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_TAC;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_CARENCIA;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.COR_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.NSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
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
 * <p>Title: ReservarMargemAssembler</p>
 * <p>Description: Assembler para ReservarMargem.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ReservarMargemAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReservarMargemAssembler.class);

    private ReservarMargemAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v1.ReservarMargem reservarMargem) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, reservarMargem.getUsuario());
        parametros.put(SENHA, reservarMargem.getSenha());
        parametros.put(RSE_MATRICULA, reservarMargem.getMatricula());
        parametros.put(SER_CPF, getValue(reservarMargem.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(reservarMargem.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(reservarMargem.getEstabelecimentoCodigo()));
        parametros.put(SER_SENHA, reservarMargem.getSenhaServidor());
        parametros.put(TOKEN, reservarMargem.getTokenAutServidor());
        parametros.put(SERVICO_CODIGO, getValue(reservarMargem.getServicoCodigo()));
        parametros.put(DATA_NASC, getValueAsDate(reservarMargem.getDataNascimento()));
        final Double adeVlr = reservarMargem.getValorParcela();
        if (adeVlr.equals(Double.NaN)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        if ((reservarMargem.getPrazo() == null) || (reservarMargem.getPrazo().getValue() == null) ||
            (reservarMargem.getPrazo().getValue() == Integer.MAX_VALUE) || (reservarMargem.getPrazo().getValue() <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, getValue(reservarMargem.getPrazo()));
        }
        final Double vlrLib = getValue(reservarMargem.getValorLiberado());
        if ((vlrLib == null) || vlrLib.equals(Double.NaN)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }
        parametros.put(CNV_COD_VERBA, getValue(reservarMargem.getCodVerba()));
        parametros.put(COR_IDENTIFICADOR, getValue(reservarMargem.getCorrespondenteCodigo()));
        if ((reservarMargem.getCarencia() == null) || (reservarMargem.getCarencia().getValue() == null) ||
            (reservarMargem.getCarencia().getValue() == Integer.MAX_VALUE) || (reservarMargem.getCarencia().getValue() <= 0)) {
            parametros.put(ADE_CARENCIA, null);
        } else {
            parametros.put(ADE_CARENCIA, getValue(reservarMargem.getCarencia()));
        }
        parametros.put(ADE_VLR_TAC, getValue(reservarMargem.getValorTac()));
        parametros.put(ADE_INDICE, getValue(reservarMargem.getIndice()));
        parametros.put(ADE_VLR_IOF, getValue(reservarMargem.getValorIof()));
        parametros.put(ADE_VLR_MENS_VINC, getValue(reservarMargem.getValorMensVin()));
        parametros.put(ADE_TAXA_JUROS, getValue(reservarMargem.getTaxaJuros()));
        parametros.put(ADE_IDENTIFICADOR, getValue(reservarMargem.getAdeIdentificador()));
        parametros.put(RSE_BANCO, getValue(reservarMargem.getBanco()));
        parametros.put(RSE_AGENCIA, getValue(reservarMargem.getAgencia()));
        parametros.put(RSE_CONTA, getValue(reservarMargem.getConta()));
        parametros.put(CONVENIO, getValue(reservarMargem.getConvenio()));
        parametros.put(CLIENTE, getValue(reservarMargem.getCliente()));
        parametros.put(SER_LOGIN, reservarMargem.getLoginServidor());
        parametros.put(NSE_CODIGO, getValue(reservarMargem.getNaturezaServicoCodigo()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v3.ReservarMargem reservarMargem) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, reservarMargem.getUsuario());
        parametros.put(SENHA, reservarMargem.getSenha());
        parametros.put(RSE_MATRICULA, reservarMargem.getMatricula());
        parametros.put(SER_CPF, getValue(reservarMargem.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(reservarMargem.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(reservarMargem.getEstabelecimentoCodigo()));
        parametros.put(SER_SENHA, reservarMargem.getSenhaServidor());
        parametros.put(TOKEN, reservarMargem.getTokenAutServidor());
        parametros.put(SERVICO_CODIGO, getValue(reservarMargem.getServicoCodigo()));
        parametros.put(DATA_NASC, getValueAsDate(reservarMargem.getDataNascimento()));
        final Double adeVlr = reservarMargem.getValorParcela();
        if (adeVlr.equals(Double.NaN)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        if ((reservarMargem.getPrazo() == null) || (reservarMargem.getPrazo().getValue() == null) ||
            (reservarMargem.getPrazo().getValue() == Integer.MAX_VALUE) || (reservarMargem.getPrazo().getValue() <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, reservarMargem.getPrazo().getValue());
        }
        final Double vlrLib = getValue(reservarMargem.getValorLiberado());
        if ((vlrLib == null) || vlrLib.equals(Double.NaN)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }
        parametros.put(CNV_COD_VERBA, getValue(reservarMargem.getCodVerba()));
        parametros.put(COR_IDENTIFICADOR, getValue(reservarMargem.getCorrespondenteCodigo()));
        if ((reservarMargem.getCarencia() == null) || (reservarMargem.getCarencia().getValue() == null) ||
            (reservarMargem.getCarencia().getValue() == Integer.MAX_VALUE) || (reservarMargem.getCarencia().getValue() <= 0)) {
            parametros.put(ADE_CARENCIA, null);
        } else {
            parametros.put(ADE_CARENCIA, reservarMargem.getCarencia().getValue());
        }
        parametros.put(ADE_VLR_TAC, getValue(reservarMargem.getValorTac()));
        parametros.put(ADE_INDICE, getValue(reservarMargem.getIndice()));
        parametros.put(ADE_VLR_IOF, getValue(reservarMargem.getValorIof()));
        parametros.put(ADE_VLR_MENS_VINC, getValue(reservarMargem.getValorMensVin()));
        parametros.put(ADE_TAXA_JUROS, getValue(reservarMargem.getTaxaJuros()));
        parametros.put(ADE_IDENTIFICADOR, getValue(reservarMargem.getAdeIdentificador()));
        parametros.put(RSE_BANCO, getValue(reservarMargem.getBanco()));
        parametros.put(RSE_AGENCIA, getValue(reservarMargem.getAgencia()));
        parametros.put(RSE_CONTA, getValue(reservarMargem.getConta()));
        parametros.put(CONVENIO, getValue(reservarMargem.getConvenio()));
        parametros.put(CLIENTE, getValue(reservarMargem.getCliente()));
        parametros.put(SER_LOGIN, reservarMargem.getLoginServidor());
        parametros.put(NSE_CODIGO, getValue(reservarMargem.getNaturezaServicoCodigo()));

        final String periodo = getValue(reservarMargem.getPeriodo());
        if (!TextHelper.isNull(periodo)) {
            parametros.put(PERIODO, periodo);
        }

        if (getValue(reservarMargem.getAnexo()) != null) {
            final Anexo anexo = new Anexo();
            try {
                // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
                BeanUtils.copyProperties(anexo, reservarMargem.getAnexo().getValue());
                parametros.put(ANEXO, anexo);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                LOG.warn(ex.getMessage(), ex);
            }

        }

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.ReservarMargem reservarMargem) {
        final com.zetra.econsig.webservice.soap.operacional.v3.ReservarMargem reservarMargemV3 = new com.zetra.econsig.webservice.soap.operacional.v3.ReservarMargem();
        try {
            BeanUtils.copyProperties(reservarMargemV3, reservarMargem);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return toMap(reservarMargemV3);
    }
}
