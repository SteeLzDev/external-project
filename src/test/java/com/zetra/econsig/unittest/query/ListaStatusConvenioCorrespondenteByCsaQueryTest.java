package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaStatusConvenioCorrespondenteByCsaQuery;

public class ListaStatusConvenioCorrespondenteByCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaStatusConvenioCorrespondenteByCsaQuery query = new ListaStatusConvenioCorrespondenteByCsaQuery();
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";

        executarConsulta(query);
    }
}

