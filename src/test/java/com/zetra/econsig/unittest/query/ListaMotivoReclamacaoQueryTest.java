package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.reclamacao.ListaMotivoReclamacaoQuery;

public class ListaMotivoReclamacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaMotivoReclamacaoQuery query = new ListaMotivoReclamacaoQuery();
        query.count = false;

        executarConsulta(query);
    }
}

