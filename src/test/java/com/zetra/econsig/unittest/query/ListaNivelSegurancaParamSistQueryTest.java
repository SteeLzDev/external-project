package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.seguranca.ListaNivelSegurancaParamSistQuery;

public class ListaNivelSegurancaParamSistQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaNivelSegurancaParamSistQuery query = new ListaNivelSegurancaParamSistQuery();

        executarConsulta(query);
    }
}

