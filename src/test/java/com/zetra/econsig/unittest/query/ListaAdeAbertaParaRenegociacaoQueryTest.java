package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.lote.ListaAdeAbertaParaRenegociacaoQuery;

public class ListaAdeAbertaParaRenegociacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAdeAbertaParaRenegociacaoQuery query = new ListaAdeAbertaParaRenegociacaoQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.adeIdentificador = "123";
        query.fixaServico = true;

        executarConsulta(query);
    }
}

