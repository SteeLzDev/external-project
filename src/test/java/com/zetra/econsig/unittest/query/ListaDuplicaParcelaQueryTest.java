package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaDuplicaParcelaQuery;

public class ListaDuplicaParcelaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaDuplicaParcelaQuery query = new ListaDuplicaParcelaQuery();
        query.csaCodigo = "267";
        query.cnvCodVerba = "123";
        query.adeIndice = "123";

        executarConsulta(query);
    }
}

