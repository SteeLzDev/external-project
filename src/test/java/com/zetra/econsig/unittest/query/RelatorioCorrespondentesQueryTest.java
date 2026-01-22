package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioCorrespondentesQuery;

public class RelatorioCorrespondentesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioCorrespondentesQuery query = new RelatorioCorrespondentesQuery();
        query.csaCodigo = "267";
        query.ecoCodigo = "123";

        executarConsulta(query);
    }
}

