package com.zetra.econsig.webservice.soap.folha.assembler;

import static com.zetra.econsig.webservice.CamposAPI.EST_ATIVO;
import static com.zetra.econsig.webservice.CamposAPI.EST_CNPJ;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_NOME;

import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.Estabelecimento;
import com.zetra.econsig.webservice.soap.folha.v1.ObjectFactory;

/**
 * <p>Title: EstabelecimentoAssembler</p>
 * <p>Description: Assembler para Estabelecimento.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class EstabelecimentoAssembler extends BaseAssembler {

    private EstabelecimentoAssembler() {
        //
    }

    public static Estabelecimento toEstabelecimentoV1(Map<CamposAPI, Object> paramResposta) {
        final ObjectFactory factory = new ObjectFactory();
        final Estabelecimento estabelecimento = new Estabelecimento();

        estabelecimento.setCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
        estabelecimento.setNome((String) paramResposta.get(EST_NOME));
        estabelecimento.setCnpj((String) paramResposta.get(EST_CNPJ));
        estabelecimento.setAtivo(factory.createEstabelecimentoAtivo((short) paramResposta.get(EST_ATIVO)));

        return estabelecimento;
    }
}