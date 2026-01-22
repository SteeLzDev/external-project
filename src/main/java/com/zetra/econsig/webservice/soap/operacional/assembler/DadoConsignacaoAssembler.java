package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.DAD_VALOR;
import static com.zetra.econsig.webservice.CamposAPI.TDA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TDA_DESCRICAO;

import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.DadoConsignacao;

/**
 * <p>Title: DadoConsignacaoAssembler</p>
 * <p>Description: Assembler para DadoConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public class DadoConsignacaoAssembler extends BaseAssembler {

    private DadoConsignacaoAssembler() {
        //
    }

    public static DadoConsignacao toDadoConsignacaoV1(Map<CamposAPI, Object> paramResposta) {
        final DadoConsignacao dado = new DadoConsignacao();

        dado.setCodigo((String) paramResposta.get(TDA_CODIGO));
        dado.setDescricao((String) paramResposta.get(TDA_DESCRICAO));
        dado.setValor((String) paramResposta.get(DAD_VALOR));

        return dado;
    }
}
