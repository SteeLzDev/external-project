package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioAlteracaoMultiplasConsignacoesQuery;

public class RelatorioAlteracaoMultiplasConsignacoesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioAlteracaoMultiplasConsignacoesQuery query = new RelatorioAlteracaoMultiplasConsignacoesQuery();

        executarConsulta(query);
    }
}

