package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.prazo.ListaPrazoCoeficienteCompartilhadoQuery;

public class ListaPrazoCoeficienteCompartilhadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPrazoCoeficienteCompartilhadoQuery query = new ListaPrazoCoeficienteCompartilhadoQuery();
        query.svcCodigoDestino = "123";
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.dia = 1;

        executarConsulta(query);
    }
}

