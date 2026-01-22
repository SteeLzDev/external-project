package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaComOcorrenciaSaldoDevedorQuery;

public class ListaCsaComOcorrenciaSaldoDevedorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsaComOcorrenciaSaldoDevedorQuery query = new ListaCsaComOcorrenciaSaldoDevedorQuery();

        executarConsulta(query);
    }
}

