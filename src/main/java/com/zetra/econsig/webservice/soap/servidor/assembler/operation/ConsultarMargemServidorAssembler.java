package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarMargemServidor;

/**
 * <p>Title: ConsultarMargemServidorAssembler</p>
 * <p>Description: Assembler para ConsultarMargemServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarMargemServidorAssembler extends BaseAssembler {

    private ConsultarMargemServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarMargemServidor consultarMargemServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(RSE_MATRICULA, consultarMargemServidor.getMatricula());
        final double adeVlr = consultarMargemServidor.getValorParcela();
        if ((adeVlr == Double.NaN) || (adeVlr == 0.0)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        parametros.put(SENHA, consultarMargemServidor.getSenha());
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarMargemServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarMargemServidor.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(consultarMargemServidor.getLoginServidor()));
        parametros.put(SERVICO_CODIGO, getValue(consultarMargemServidor.getServicoCodigo()));
        parametros.put(CNV_COD_VERBA, getValue(consultarMargemServidor.getCodVerba()));

        return parametros;
    }
}