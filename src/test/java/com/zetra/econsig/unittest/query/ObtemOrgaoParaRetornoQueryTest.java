package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.retorno.ObtemOrgaoParaRetornoQuery;

public class ObtemOrgaoParaRetornoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemOrgaoParaRetornoQuery query = new ObtemOrgaoParaRetornoQuery();
        query.estIdentificador = "123";
        query.orgIdentificador = "123";

        executarConsulta(query);
    }
}

