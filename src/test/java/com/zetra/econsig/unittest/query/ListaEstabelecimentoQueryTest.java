package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.estabelecimento.ListaEstabelecimentoQuery;

public class ListaEstabelecimentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaEstabelecimentoQuery query = new ListaEstabelecimentoQuery();
        query.count = false;
        query.estIdentificador = "123";
        query.estNome = "123";
        query.estAtivo = 1;
        query.estCodigo = "751F8080808080808080808080809680";

        executarConsulta(query);
    }
}

