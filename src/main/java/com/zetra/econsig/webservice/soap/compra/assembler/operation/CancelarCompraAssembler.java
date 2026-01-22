package com.zetra.econsig.webservice.soap.compra.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.OBS;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.CancelarCompra;

/**
 * <p>Title: CancelarCompraAssembler</p>
 * <p>Description: Assembler para CancelarCompra.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CancelarCompraAssembler extends BaseAssembler {

    private CancelarCompraAssembler() {
        //
    }

    public static Map<CamposAPI, Object> toMap(CancelarCompra cancelarCompra) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(CLIENTE, getValue(cancelarCompra.getCliente()));
        parametros.put(CONVENIO, getValue(cancelarCompra.getConvenio()));
        parametros.put(USUARIO, cancelarCompra.getUsuario());
        parametros.put(SENHA, cancelarCompra.getSenha());
        final long adeNumero = cancelarCompra.getAdeNumero();
        if ((adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(OBS, getValue(cancelarCompra.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(cancelarCompra.getCodigoMotivoOperacao()));

        return parametros;
    }
}