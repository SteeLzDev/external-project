package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialGeralBuscaSvcTaxasQuery;

public class RelatorioGerencialGeralBuscaSvcTaxasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioGerencialGeralBuscaSvcTaxasQuery query = new RelatorioGerencialGeralBuscaSvcTaxasQuery();
        query.internacional = true;

        executarConsulta(query);
    }
}

