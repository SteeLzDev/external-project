package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarConsignacaoServidor;

/**
 * <p>Title: ConsultarConsignacaoServidorAssembler</p>
 * <p>Description: Assembler para ConsultarConsignacaoServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarConsignacaoServidorAssembler extends BaseAssembler {

    private ConsultarConsignacaoServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarConsignacaoServidor consultarConsignacaoServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, consultarConsignacaoServidor.getSenha());
        parametros.put(RSE_MATRICULA, consultarConsignacaoServidor.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarConsignacaoServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarConsignacaoServidor.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(consultarConsignacaoServidor.getLoginServidor()));
        final Long adeNumero = getValue(consultarConsignacaoServidor.getAdeNumero());
        if ((adeNumero == null) || (adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }

        return parametros;
    }
}