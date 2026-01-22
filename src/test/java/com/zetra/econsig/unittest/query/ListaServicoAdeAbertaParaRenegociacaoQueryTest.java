package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoAdeAbertaParaRenegociacaoQuery;

public class ListaServicoAdeAbertaParaRenegociacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoAdeAbertaParaRenegociacaoQuery query = new ListaServicoAdeAbertaParaRenegociacaoQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.adeIdentificador = "123";
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

