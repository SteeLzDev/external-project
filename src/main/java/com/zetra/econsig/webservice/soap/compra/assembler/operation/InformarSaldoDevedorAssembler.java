package com.zetra.econsig.webservice.soap.compra.assembler.operation;

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

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.InformarSaldoDevedor;

/**
 * <p>Title: InformarSaldoDevedorAssembler</p>
 * <p>Description: Assembler para InformarSaldoDevedor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class InformarSaldoDevedorAssembler extends BaseAssembler {

    private InformarSaldoDevedorAssembler() {
        //
    }

    public static Map<CamposAPI, Object> toMap(InformarSaldoDevedor informarSaldoDevedor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, informarSaldoDevedor.getUsuario());
        parametros.put(SENHA, informarSaldoDevedor.getSenha());
        final long adeNumero = informarSaldoDevedor.getAdeNumero();
        if ((adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(SDV_AGENCIA, getValue(informarSaldoDevedor.getAgencia()));
        parametros.put(SDV_BANCO, getValue(informarSaldoDevedor.getBanco()));
        parametros.put(SDV_CONTA, getValue(informarSaldoDevedor.getConta()));
        parametros.put(CLIENTE, getValue(informarSaldoDevedor.getCliente()));
        parametros.put(CONVENIO, getValue(informarSaldoDevedor.getConvenio()));
        parametros.put(SDV_DATA_VENC_1, getValueAsDate(informarSaldoDevedor.getDataVencimento()));
        parametros.put(SDV_DATA_VENC_2, getValueAsDate(informarSaldoDevedor.getDataVencimento2()));
        parametros.put(SDV_DATA_VENC_3, getValueAsDate(informarSaldoDevedor.getDataVencimento3()));
        parametros.put(SDV_LINK_BOLETO, getValue(informarSaldoDevedor.getLinkBoleto()));
        parametros.put(SDV_NOME_FAVORECIDO, getValue(informarSaldoDevedor.getNomeFavorecido()));
        parametros.put(SDV_NUM_CONTRATO, getValue(informarSaldoDevedor.getNumeroContrato()));
        parametros.put(PRAZO, getValue(informarSaldoDevedor.getNumeroPrestacoes()));
        parametros.put(OBS, getValue(informarSaldoDevedor.getObservacao()));
        final double saldoDevedor = informarSaldoDevedor.getValorSaldoDevedor();
        if (saldoDevedor == Double.NaN) {
            parametros.put(SDV_VLR_SALDO_DEVEDOR_1, null);
        } else {
            parametros.put(SDV_VLR_SALDO_DEVEDOR_1, saldoDevedor);
        }
        parametros.put(SDV_VLR_SALDO_DEVEDOR_2, getValue(informarSaldoDevedor.getValorSaldoDevedor2()));
        parametros.put(SDV_VLR_SALDO_DEVEDOR_3, getValue(informarSaldoDevedor.getValorSaldoDevedor3()));
        parametros.put(DETALHE, getValue(informarSaldoDevedor.getDetalheSaldoDevedor()));
        parametros.put(SDV_CNPJ, getValue(informarSaldoDevedor.getCnpjFavorecido()));
        parametros.put(SDV_PROPOSTA_REFIN, getValue(informarSaldoDevedor.getPropostaRefinanciamento()));

        parametros.put(ANEXO_DSD, getValue(informarSaldoDevedor.getAnexoDsdSaldoCompra()));
        parametros.put(ANEXO_BOLETO_DSD, getValue(informarSaldoDevedor.getAnexoBoletoDsdSaldo()));

        return parametros;
    }
}