package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.rescisao.ListarContratosVerbaRescisoriaConcluidaQuery;

public class ListarContratosVerbaRescisoriaConcluidaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarContratosVerbaRescisoriaConcluidaQuery query = new ListarContratosVerbaRescisoriaConcluidaQuery();
        query.vrrCodigo = "123";

        executarConsulta(query);
    }
}

