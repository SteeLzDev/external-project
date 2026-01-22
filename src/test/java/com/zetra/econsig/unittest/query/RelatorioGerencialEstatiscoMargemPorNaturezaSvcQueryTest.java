package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery;

public class RelatorioGerencialEstatiscoMargemPorNaturezaSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String naturezaSvc = "123";

        RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery query = new RelatorioGerencialEstatiscoMargemPorNaturezaSvcQuery(naturezaSvc);

        executarConsulta(query);
    }
}

