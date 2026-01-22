package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaCseAjustesQuery;

public class ListaCalFolhaCseAjustesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaCseAjustesQuery query = new ListaCalFolhaCseAjustesQuery();
        query.cseCodigo = "1";
        query.dataLimite = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

