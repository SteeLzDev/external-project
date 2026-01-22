package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.FuncoesEnvioEmailSerQuery;

public class FuncoesEnvioEmailSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String serCodigo = "123";

        FuncoesEnvioEmailSerQuery query = new FuncoesEnvioEmailSerQuery(serCodigo);

        executarConsulta(query);
    }
}

