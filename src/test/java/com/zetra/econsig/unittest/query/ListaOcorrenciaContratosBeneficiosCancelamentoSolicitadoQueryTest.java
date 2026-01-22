package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.contrato.ListaOcorrenciaContratosBeneficiosCancelamentoSolicitadoQuery;

public class ListaOcorrenciaContratosBeneficiosCancelamentoSolicitadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaContratosBeneficiosCancelamentoSolicitadoQuery query = new ListaOcorrenciaContratosBeneficiosCancelamentoSolicitadoQuery();

        executarConsulta(query);
    }
}

