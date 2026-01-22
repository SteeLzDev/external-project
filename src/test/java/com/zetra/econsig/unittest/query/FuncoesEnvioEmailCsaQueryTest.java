package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.FuncoesEnvioEmailCsaQuery;

public class FuncoesEnvioEmailCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String csaCodigo = "267";

        FuncoesEnvioEmailCsaQuery query = new FuncoesEnvioEmailCsaQuery(csaCodigo);

        executarConsulta(query);
    }
}

