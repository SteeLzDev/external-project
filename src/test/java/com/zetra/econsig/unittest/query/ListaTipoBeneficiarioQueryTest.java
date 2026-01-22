package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListaTipoBeneficiarioQuery;

public class ListaTipoBeneficiarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoBeneficiarioQuery query = new ListaTipoBeneficiarioQuery();
        query.tibCodigo = null;

        executarConsulta(query);
    }
}

