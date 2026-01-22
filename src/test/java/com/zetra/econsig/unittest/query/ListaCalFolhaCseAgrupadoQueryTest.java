package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaCseAgrupadoQuery;

public class ListaCalFolhaCseAgrupadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaCseAgrupadoQuery query = new ListaCalFolhaCseAgrupadoQuery();
        query.cseCodigo = "1";
        query.cfcPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.dataLimite = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

