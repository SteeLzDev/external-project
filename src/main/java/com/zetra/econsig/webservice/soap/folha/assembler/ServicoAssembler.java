package com.zetra.econsig.webservice.soap.folha.assembler;

import static com.zetra.econsig.webservice.CamposAPI.NSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.NSE_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_ATIVO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SVC_OBS;
import static com.zetra.econsig.webservice.CamposAPI.SVC_PRIORIDADE;

import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ObjectFactory;
import com.zetra.econsig.webservice.soap.folha.v1.Servico;

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
        final ObjectFactory factory = new ObjectFactory();
        final Servico servico = new Servico();

        servico.setCodigo((String) paramResposta.get(SVC_IDENTIFICADOR));
        servico.setDescricao((String) paramResposta.get(SVC_DESCRICAO));
        servico.setCodigoNaturezaServico(factory.createServicoCodigoNaturezaServico((String) paramResposta.get(NSE_CODIGO)));
        servico.setNaturezaServico(factory.createServicoNaturezaServico((String) paramResposta.get(NSE_DESCRICAO)));
        servico.setAtivo(factory.createServicoAtivo((short) paramResposta.get(SVC_ATIVO)));
        servico.setObservacao(factory.createServicoObservacao((String) paramResposta.get(SVC_OBS)));
        servico.setPrioridade(factory.createServicoPrioridade((String) paramResposta.get(SVC_PRIORIDADE)));

        return servico;
    }
}