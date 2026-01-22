package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.comunicacao.ListaUsuariosComunicacoesPendentesCsaQuery;

public class ListaUsuariosComunicacoesPendentesCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuariosComunicacoesPendentesCsaQuery query = new ListaUsuariosComunicacoesPendentesCsaQuery();
        query.count = false;
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

