package com.zetra.econsig.webservice.soap.compra.assembler;

import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;

import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.Servico;

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
}