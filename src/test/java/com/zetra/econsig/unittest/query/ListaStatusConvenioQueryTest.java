package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaStatusConvenioQuery;

public class ListaStatusConvenioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaStatusConvenioQuery query = new ListaStatusConvenioQuery();
        query.count = false;
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.scvCodigo = "123";
        query.svcIdentificador = "123";
        query.svcDescricao = "123";
        query.verificaConvenioPossuiContratos = true;
        query.temAde = "123";
        query.filtroCampoSvcRelatorioCsa = true;

        executarConsulta(query);
    }
}

