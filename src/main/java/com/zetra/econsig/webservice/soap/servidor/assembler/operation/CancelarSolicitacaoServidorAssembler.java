package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.CancelarSolicitacaoServidor;

/**
 * <p>Title: CancelarSolicitacaoServidorAssembler</p>
 * <p>Description: Assembler para CancelarSolicitacaoServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CancelarSolicitacaoServidorAssembler extends BaseAssembler {

    private CancelarSolicitacaoServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CancelarSolicitacaoServidor cancelarSolicitacaoServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(RSE_MATRICULA, cancelarSolicitacaoServidor.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(cancelarSolicitacaoServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(cancelarSolicitacaoServidor.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(cancelarSolicitacaoServidor.getLoginServidor()));
        parametros.put(SENHA, cancelarSolicitacaoServidor.getSenha());
        final long adeNumero = cancelarSolicitacaoServidor.getAdeNumero();
        if ((adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(TMO_OBS, getValue(cancelarSolicitacaoServidor.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(cancelarSolicitacaoServidor.getCodigoMotivoOperacao()));

        return parametros;
    }
}