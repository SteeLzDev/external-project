package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaLeilaoFinalizadoSemContatoQuery;

public class ListaLeilaoFinalizadoSemContatoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaLeilaoFinalizadoSemContatoQuery query = new ListaLeilaoFinalizadoSemContatoQuery();
        query.count = false;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("213464140-123456", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

