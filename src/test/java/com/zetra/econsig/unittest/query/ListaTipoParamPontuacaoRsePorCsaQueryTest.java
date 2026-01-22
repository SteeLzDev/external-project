package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.pontuacao.ListaTipoParamPontuacaoRsePorCsaQuery;

public class ListaTipoParamPontuacaoRsePorCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaTipoParamPontuacaoRsePorCsaQuery query = new ListaTipoParamPontuacaoRsePorCsaQuery("267");
        executarConsulta(query);
    }

}
