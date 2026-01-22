package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListaCalculoBeneficioQuery;

public class ListaCalculoBeneficioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalculoBeneficioQuery query = new ListaCalculoBeneficioQuery();
        query.ncaCodigo = "1";
        query.benCodigo = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.tibCodigo = "123";
        query.grpCodigo = "123";
        query.mdeCodigo = "123";
        query.statusRegra = "123";
        query.data = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.count = false;

        executarConsulta(query);
    }
}

