package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;

import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.Servico;


/**
 * <p>Title: ServicoAssembler</p>
 * <p>Description: Assembler para Servico.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ServicoAssembler extends BaseAssembler {
    private ServicoAssembler() {
    }

    public static Servico toServicoV1(Map<CamposAPI, Object> paramResposta) {
        final Servico servico = new Servico();

        servico.setServico((String) paramResposta.get(SERVICO));
        servico.setServicoCodigo((String) paramResposta.get(SERVICO_CODIGO));

        return servico;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v2.Servico toServicoV2(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v2.Servico servico = new com.zetra.econsig.webservice.soap.operacional.v2.Servico();

        servico.setServico((String) paramResposta.get(SERVICO));
        servico.setServicoCodigo((String) paramResposta.get(SERVICO_CODIGO));

        return servico;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v3.Servico toServicoV3(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v3.Servico servico = new com.zetra.econsig.webservice.soap.operacional.v3.Servico();

        servico.setServico((String) paramResposta.get(SERVICO));
        servico.setServicoCodigo((String) paramResposta.get(SERVICO_CODIGO));

        return servico;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v6.Servico toServicoV6(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v6.Servico servico = new com.zetra.econsig.webservice.soap.operacional.v6.Servico();

        servico.setServico((String) paramResposta.get(SERVICO));
        servico.setServicoCodigo((String) paramResposta.get(SERVICO_CODIGO));

        return servico;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v7.Servico toServicoV7(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v7.Servico servico = new com.zetra.econsig.webservice.soap.operacional.v7.Servico();

        servico.setServico((String) paramResposta.get(SERVICO));
        servico.setServicoCodigo((String) paramResposta.get(SERVICO_CODIGO));

        return servico;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v8.Servico toServicoV8(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v8.Servico servico = new com.zetra.econsig.webservice.soap.operacional.v8.Servico();

        servico.setServico((String) paramResposta.get(SERVICO));
        servico.setServicoCodigo((String) paramResposta.get(SERVICO_CODIGO));

        return servico;
    }
}