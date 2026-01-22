package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaEstAgrupadoFiltroPeriodoQuery;

public class ListaCalFolhaEstAgrupadoFiltroPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaEstAgrupadoFiltroPeriodoQuery query = new ListaCalFolhaEstAgrupadoFiltroPeriodoQuery();
        query.estCodigo = "751F8080808080808080808080809680";
        query.periodoInicio = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

