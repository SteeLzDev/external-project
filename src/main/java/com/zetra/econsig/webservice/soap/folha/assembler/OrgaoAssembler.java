package com.zetra.econsig.webservice.soap.folha.assembler;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CNPJ;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME_ABREV;

import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ObjectFactory;
import com.zetra.econsig.webservice.soap.folha.v1.Orgao;

/**
 * <p>Title: OrgaoAssembler</p>
 * <p>Description: Assembler para Orgao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class OrgaoAssembler extends BaseAssembler {

    private OrgaoAssembler() {
    }

    public static Orgao toOrgaoV1(Map<CamposAPI, Object> paramResposta) {
        final ObjectFactory factory = new ObjectFactory();
        final Orgao orgao = new Orgao();

        orgao.setCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
        orgao.setNome((String) paramResposta.get(ORG_NOME));
        orgao.setNomeAbreviado(factory.createOrgaoNomeAbreviado((String) paramResposta.get(ORG_NOME_ABREV)));
        orgao.setCnpj(factory.createOrgaoCnpj((String) paramResposta.get(ORG_CNPJ)));
        orgao.setCodigoEstabelecimento((String) paramResposta.get(EST_IDENTIFICADOR));

        return orgao;
    }
}