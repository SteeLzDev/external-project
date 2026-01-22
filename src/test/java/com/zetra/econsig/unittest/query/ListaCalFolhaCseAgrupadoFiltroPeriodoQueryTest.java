package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaCseAgrupadoFiltroPeriodoQuery;

public class ListaCalFolhaCseAgrupadoFiltroPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaCseAgrupadoFiltroPeriodoQuery query = new ListaCalFolhaCseAgrupadoFiltroPeriodoQuery();
        query.cseCodigo = "1";
        query.periodoInicio = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

