package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaMotivoOperacaoQuery;

public class ListaMotivoOperacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaMotivoOperacaoQuery query = new ListaMotivoOperacaoQuery();
        query.tenCodigos = java.util.List.of("1", "2");
        query.tmoAtivo = 1;
        query.acaCodigo = "123";
        query.tmoCodigo = "123";

        executarConsulta(query);
    }
}

