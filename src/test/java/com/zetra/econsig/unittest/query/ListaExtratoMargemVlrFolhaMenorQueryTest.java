package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemVlrFolhaMenorQuery;

public class ListaExtratoMargemVlrFolhaMenorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";

        ListaExtratoMargemVlrFolhaMenorQuery query = new ListaExtratoMargemVlrFolhaMenorQuery(rseCodigo);

        executarConsulta(query);
    }
}

