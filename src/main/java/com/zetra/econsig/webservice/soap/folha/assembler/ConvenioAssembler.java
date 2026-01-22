package com.zetra.econsig.webservice.soap.folha.assembler;

import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA_FERIAS;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA_REF;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SVC_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;

import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.Convenio;
import com.zetra.econsig.webservice.soap.folha.v1.ObjectFactory;

/**
 * <p>Title: ConvenioAssembler</p>
 * <p>Description: Assembler para Convenio.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConvenioAssembler extends BaseAssembler {

    private ConvenioAssembler() {
    }

    public static Convenio toConvenioV1(Map<CamposAPI, Object> paramResposta) {
        final ObjectFactory factory = new ObjectFactory();
        final Convenio convenio = new Convenio();

        convenio.setCodigoServico((String) paramResposta.get(SVC_IDENTIFICADOR));
        convenio.setDescricaoServico(factory.createConvenioDescricaoServico((String) paramResposta.get(SVC_DESCRICAO)));
        convenio.setCodigoOrgao((String) paramResposta.get(ORG_IDENTIFICADOR));
        convenio.setNomeOrgao(factory.createConvenioNomeOrgao((String) paramResposta.get(ORG_NOME)));
        convenio.setCodigoConsignataria((String) paramResposta.get(CSA_IDENTIFICADOR));
        convenio.setNomeConsignataria(factory.createConvenioNomeConsignataria((String) paramResposta.get(CSA_NOME)));
        convenio.setVerbaConvenio((String) paramResposta.get(CNV_COD_VERBA));
        convenio.setVerbaConvenioRef(factory.createConvenioVerbaConvenioRef((String) paramResposta.get(CNV_COD_VERBA_REF)));
        convenio.setVerbaConvenioFerias(factory.createConvenioVerbaConvenioFerias((String) paramResposta.get(CNV_COD_VERBA_FERIAS)));
        convenio.setCodigoEstabelecimento((String) paramResposta.get(EST_IDENTIFICADOR));

        return convenio;
    }
}