package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.prazo.ListaPrazoCoeficienteQuery;

public class ListaPrazoCoeficienteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPrazoCoeficienteQuery query = new ListaPrazoCoeficienteQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.dia = 1;
        query.validaBloqSerCnvCsa = true;
        query.validaLimitePrazo = true;
        query.validaPrazoRenegociacao = true;

        executarConsulta(query);
    }
}

