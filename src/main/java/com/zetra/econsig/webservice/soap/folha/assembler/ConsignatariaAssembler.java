package com.zetra.econsig.webservice.soap.folha.assembler;

import static com.zetra.econsig.webservice.CamposAPI.CSA_CNPJ;
import static com.zetra.econsig.webservice.CamposAPI.CSA_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME_ABREV;
import static com.zetra.econsig.webservice.CamposAPI.CSA_TEL;
import static com.zetra.econsig.webservice.CamposAPI.NCA_CODIGO;

import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.Consignataria;
import com.zetra.econsig.webservice.soap.folha.v1.ObjectFactory;

/**
 * <p>Title: ConsignatariaAssembler</p>
 * <p>Description: Assembler para Consignataria.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsignatariaAssembler extends BaseAssembler {

    private ConsignatariaAssembler() {
    }

    public static Consignataria toConsignatariaV1(Map<CamposAPI, Object> paramResposta) {
        final ObjectFactory factory = new ObjectFactory();
        final Consignataria consignataria = new Consignataria();

        consignataria.setCodigo((String) paramResposta.get(CSA_IDENTIFICADOR));
        consignataria.setNome((String) paramResposta.get(CSA_NOME));
        consignataria.setNomeAbreviado(factory.createConsignatariaNomeAbreviado((String) paramResposta.get(CSA_NOME_ABREV)));
        consignataria.setCnpj(factory.createConsignatariaCnpj((String) paramResposta.get(CSA_CNPJ)));
        consignataria.setTelefone((String) paramResposta.get(CSA_TEL));
        consignataria.setEmail(factory.createConsignatariaEmail((String) paramResposta.get(CSA_EMAIL)));
        consignataria.setNaturezaCodigo(factory.createConsignatariaNaturezaCodigo((String) paramResposta.get(NCA_CODIGO)));

        return consignataria;
    }
}