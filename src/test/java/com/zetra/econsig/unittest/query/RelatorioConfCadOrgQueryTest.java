package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioConfCadOrgQuery;

public class RelatorioConfCadOrgQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioConfCadOrgQuery query = new RelatorioConfCadOrgQuery();

        executarConsulta(query);
    }
}

