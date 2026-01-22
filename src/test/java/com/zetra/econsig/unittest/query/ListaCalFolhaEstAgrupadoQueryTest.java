package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaEstAgrupadoQuery;

public class ListaCalFolhaEstAgrupadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaEstAgrupadoQuery query = new ListaCalFolhaEstAgrupadoQuery();
        query.estCodigo = "751F8080808080808080808080809680";
        query.cfePeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.dataLimite = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

