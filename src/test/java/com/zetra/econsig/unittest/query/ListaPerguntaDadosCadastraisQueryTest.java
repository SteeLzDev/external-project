package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.pergunta.ListaPerguntaDadosCadastraisQuery;

public class ListaPerguntaDadosCadastraisQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPerguntaDadosCadastraisQuery query = new ListaPerguntaDadosCadastraisQuery();
        query.pdcGrupo = 1;
        query.pdcNumero = 1;

        executarConsulta(query);
    }
}

