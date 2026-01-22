package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.sdp.plano.BuscaPlanoDescontoQuery;

public class BuscaPlanoDescontoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        BuscaPlanoDescontoQuery query = new BuscaPlanoDescontoQuery();
        query.plaCodigo = "123";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.plaDescricao = "123";
        query.plaIdentificador = "123";
        query.plaAtivo = 1;

        executarConsulta(query);
    }
}

