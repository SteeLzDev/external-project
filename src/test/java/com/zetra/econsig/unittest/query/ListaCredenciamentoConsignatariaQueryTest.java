package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCredenciamentoConsignatariaQuery;

public class ListaCredenciamentoConsignatariaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCredenciamentoConsignatariaQuery query = new ListaCredenciamentoConsignatariaQuery();
        query.csaCodigo = "267";
        query.creDataIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.creDataFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.csaCodigos = java.util.List.of("1", "2");
        query.scrCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

