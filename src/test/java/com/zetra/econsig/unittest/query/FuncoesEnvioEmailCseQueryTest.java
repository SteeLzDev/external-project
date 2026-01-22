package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.FuncoesEnvioEmailCseQuery;

public class FuncoesEnvioEmailCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String cseCodigo = "1";

        FuncoesEnvioEmailCseQuery query = new FuncoesEnvioEmailCseQuery(cseCodigo);

        executarConsulta(query);
    }
}

