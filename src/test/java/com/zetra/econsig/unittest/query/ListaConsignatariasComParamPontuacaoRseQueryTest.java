package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.pontuacao.ListaConsignatariasComParamPontuacaoRseQuery;

public class ListaConsignatariasComParamPontuacaoRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaConsignatariasComParamPontuacaoRseQuery query = new ListaConsignatariasComParamPontuacaoRseQuery();
        executarConsulta(query);
    }
}
