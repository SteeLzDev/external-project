package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.contrato.ListaLancamentosContratosBeneficiosQuery;

public class ListaLancamentosContratosBeneficiosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaLancamentosContratosBeneficiosQuery query = new ListaLancamentosContratosBeneficiosQuery();
        query.cbeCodigo = "123";
        query.prdDataDesconto = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

