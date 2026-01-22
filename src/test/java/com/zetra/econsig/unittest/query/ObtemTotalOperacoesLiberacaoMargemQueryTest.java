package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.seguranca.ObtemTotalOperacoesLiberacaoMargemQuery;

public class ObtemTotalOperacoesLiberacaoMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalOperacoesLiberacaoMargemQuery query = new ObtemTotalOperacoesLiberacaoMargemQuery();
        query.usuCodigo = "123";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

