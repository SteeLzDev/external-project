package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaTipoRelacionamentosSvcQuery;

public class ListaTipoRelacionamentosSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoRelacionamentosSvcQuery query = new ListaTipoRelacionamentosSvcQuery();

        executarConsulta(query);
    }
}

