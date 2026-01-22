package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaEstAjustesQuery;

public class ListaCalFolhaEstAjustesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaEstAjustesQuery query = new ListaCalFolhaEstAjustesQuery();
        query.estCodigo = "751F8080808080808080808080809680";
        query.dataLimite = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

