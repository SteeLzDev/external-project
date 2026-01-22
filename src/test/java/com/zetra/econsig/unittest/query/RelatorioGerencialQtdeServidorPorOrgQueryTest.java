package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeServidorPorOrgQuery;

public class RelatorioGerencialQtdeServidorPorOrgQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioGerencialQtdeServidorPorOrgQuery query = new RelatorioGerencialQtdeServidorPorOrgQuery();
        query.somenteOrgaoAtivo = true;

        executarConsulta(query);
    }
}

