package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.rescisao.ListarContratosSaldoDevedorPendenteQuery;

public class ListarContratosSaldoDevedorPendenteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarContratosSaldoDevedorPendenteQuery query = new ListarContratosSaldoDevedorPendenteQuery();
        query.vrrCodigo = "123";

        executarConsulta(query);
    }
}

