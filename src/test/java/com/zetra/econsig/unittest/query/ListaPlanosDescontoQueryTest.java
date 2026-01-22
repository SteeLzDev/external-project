package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.sdp.plano.ListaPlanosDescontoQuery;

public class ListaPlanosDescontoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPlanosDescontoQuery query = new ListaPlanosDescontoQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.plaDescricao = "123";
        query.plaIdentificador = "123";
        query.plaAtivo = 1;
        query.count = false;
        query.taxaUso = true;

        executarConsulta(query);
    }
}

