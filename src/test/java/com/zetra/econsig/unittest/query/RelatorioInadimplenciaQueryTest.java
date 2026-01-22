package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioInadimplenciaQuery;

public class RelatorioInadimplenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioInadimplenciaQuery query = new RelatorioInadimplenciaQuery();

        executarConsulta(query);
    }
}

