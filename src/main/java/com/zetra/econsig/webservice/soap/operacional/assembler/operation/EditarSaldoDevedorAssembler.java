package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO_BOLETO_DSD;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO_DSD;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.DETALHE;
import static com.zetra.econsig.webservice.CamposAPI.OBS;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.SDV_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_CNPJ;
import static com.zetra.econsig.webservice.CamposAPI.SDV_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.SDV_DATA_VENC_1;
import static com.zetra.econsig.webservice.CamposAPI.SDV_DATA_VENC_2;
import static com.zetra.econsig.webservice.CamposAPI.SDV_DATA_VENC_3;
import static com.zetra.econsig.webservice.CamposAPI.SDV_LINK_BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_NOME_FAVORECIDO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_NUM_CONTRATO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_PROPOSTA_REFIN;
import static com.zetra.econsig.webservice.CamposAPI.SDV_VLR_SALDO_DEVEDOR_1;
import static com.zetra.econsig.webservice.CamposAPI.SDV_VLR_SALDO_DEVEDOR_2;
import static com.zetra.econsig.webservice.CamposAPI.SDV_VLR_SALDO_DEVEDOR_3;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v8.EditarSaldoDevedor;

/**
 * <p>Title: EditarSaldoDevedorAssembler</p>
 * <p>Description: Assembler para EditarSaldoDevedorAssembler.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Eduardo Fortes
 */

public class EditarSaldoDevedorAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarSaldoDevedorAssembler.class);

    private EditarSaldoDevedorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(EditarSaldoDevedor editarSaldoDevedor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, editarSaldoDevedor.getUsuario());
        parametros.put(SENHA, editarSaldoDevedor.getSenha());
        final Long adeNumero = getValue(editarSaldoDevedor.getAdeNumero());
        if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }

        parametros.put(ADE_IDENTIFICADOR, getValue(editarSaldoDevedor.getAdeIdentificador()));
        parametros.put(SDV_AGENCIA, getValue(editarSaldoDevedor.getAgencia()));
        parametros.put(SDV_BANCO, getValue(editarSaldoDevedor.getBanco()));
        parametros.put(SDV_CONTA, getValue(editarSaldoDevedor.getConta()));
        parametros.put(CLIENTE, getValue(editarSaldoDevedor.getCliente()));
        parametros.put(CONVENIO, getValue(editarSaldoDevedor.getConvenio()));
        parametros.put(SDV_DATA_VENC_1, getValueAsDate(editarSaldoDevedor.getDataVencimento()));
        parametros.put(SDV_DATA_VENC_2, getValueAsDate(editarSaldoDevedor.getDataVencimento2()));
        parametros.put(SDV_DATA_VENC_3, getValueAsDate(editarSaldoDevedor.getDataVencimento3()));
        parametros.put(SDV_LINK_BOLETO, getValue(editarSaldoDevedor.getLinkBoleto()));
        parametros.put(SDV_NOME_FAVORECIDO, getValue(editarSaldoDevedor.getNomeFavorecido()));
        parametros.put(SDV_NUM_CONTRATO, getValue(editarSaldoDevedor.getNumeroContrato()));
        parametros.put(PRAZO, getValue(editarSaldoDevedor.getNumeroPrestacoes()));
        parametros.put(OBS, getValue(editarSaldoDevedor.getObservacao()));
        final double saldoDevedor = editarSaldoDevedor.getValorSaldoDevedor();
        if (saldoDevedor == Double.NaN) {
            parametros.put(SDV_VLR_SALDO_DEVEDOR_1, null);
        } else {
            parametros.put(SDV_VLR_SALDO_DEVEDOR_1, saldoDevedor);
        }
        parametros.put(SDV_VLR_SALDO_DEVEDOR_2, getValue(editarSaldoDevedor.getValorSaldoDevedor2()));
        parametros.put(SDV_VLR_SALDO_DEVEDOR_3, getValue(editarSaldoDevedor.getValorSaldoDevedor3()));
        parametros.put(DETALHE, getValue(editarSaldoDevedor.getDetalheSaldoDevedor()));
        parametros.put(SDV_CNPJ, getValue(editarSaldoDevedor.getCnpjFavorecido()));
        parametros.put(SDV_PROPOSTA_REFIN, getValue(editarSaldoDevedor.getPropostaRefinanciamento()));

        if (getValue(editarSaldoDevedor.getAnexoDsdSaldoCompra()) != null) {
            final com.zetra.econsig.webservice.soap.operacional.v8.Anexo anexo = new com.zetra.econsig.webservice.soap.operacional.v8.Anexo();
            try {
                // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
                BeanUtils.copyProperties(anexo, editarSaldoDevedor.getAnexoDsdSaldoCompra().getValue());
                parametros.put(ANEXO_DSD, anexo);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                LOG.warn(ex.getMessage(), ex);
            }
        }

        if (getValue(editarSaldoDevedor.getAnexoBoletoDsdSaldo()) != null) {
            final com.zetra.econsig.webservice.soap.operacional.v8.Anexo anexo = new com.zetra.econsig.webservice.soap.operacional.v8.Anexo();
            try {
                // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
                BeanUtils.copyProperties(anexo, editarSaldoDevedor.getAnexoBoletoDsdSaldo().getValue());
                parametros.put(ANEXO_BOLETO_DSD, anexo);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                LOG.warn(ex.getMessage(), ex);
            }
        }

        return parametros;
    }

}
