package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaCseDataIniFimAntInvalidaQuery;

public class ListaCalFolhaCseDataIniFimAntInvalidaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaCseDataIniFimAntInvalidaQuery query = new ListaCalFolhaCseDataIniFimAntInvalidaQuery();
        query.cseCodigo = "1";

        executarConsulta(query);
    }
}

