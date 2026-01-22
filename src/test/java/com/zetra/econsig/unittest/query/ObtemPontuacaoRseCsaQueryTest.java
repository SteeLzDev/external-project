package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.pontuacao.ObtemPontuacaoRseCsaQuery;

public class ObtemPontuacaoRseCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ObtemPontuacaoRseCsaQuery query = new ObtemPontuacaoRseCsaQuery("123", "123");
        executarConsulta(query);
    }
}
