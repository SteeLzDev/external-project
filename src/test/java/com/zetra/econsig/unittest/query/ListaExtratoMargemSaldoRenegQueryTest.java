package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemSaldoRenegQuery;

public class ListaExtratoMargemSaldoRenegQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";

        ListaExtratoMargemSaldoRenegQuery query = new ListaExtratoMargemSaldoRenegQuery(rseCodigo);

        executarConsulta(query);
    }
}

