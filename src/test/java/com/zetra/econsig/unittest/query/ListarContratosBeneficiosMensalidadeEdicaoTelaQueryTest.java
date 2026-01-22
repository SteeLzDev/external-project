package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.contrato.ListarContratosBeneficiosMensalidadeEdicaoTelaQuery;

public class ListarContratosBeneficiosMensalidadeEdicaoTelaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarContratosBeneficiosMensalidadeEdicaoTelaQuery query = new ListarContratosBeneficiosMensalidadeEdicaoTelaQuery();
        query.cbeCodigo = "123";
        query.tntCodigo = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

