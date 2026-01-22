package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.contrato.ListaOcorrenciaContratosBeneficiosQuery;

public class ListaOcorrenciaContratosBeneficiosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaContratosBeneficiosQuery query = new ListaOcorrenciaContratosBeneficiosQuery();
        query.cbeCodigo = "123";
        query.motivoExclusao = true;

        executarConsulta(query);
    }
}

