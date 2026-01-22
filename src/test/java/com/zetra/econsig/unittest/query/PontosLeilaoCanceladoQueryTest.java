package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.PontosLeilaoCanceladoQuery;

public class PontosLeilaoCanceladoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        PontosLeilaoCanceladoQuery query = new PontosLeilaoCanceladoQuery();
        query.rseCodigo = "123";
        query.dataInicial = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

