package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.faturamento.ListarFaturamentoBeneficioQuery;

public class ListarFaturamentoBeneficioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarFaturamentoBeneficioQuery query = new ListarFaturamentoBeneficioQuery();
        query.fatCodigo = "123";
        query.csaCodigo = "267";
        query.fatPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

