package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.beneficiario.ListarBeneficiariosPorTipoQuery;

public class ListarBeneficiariosPorTipoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBeneficiariosPorTipoQuery query = new ListarBeneficiariosPorTipoQuery();
        query.tipoEntidade = "ORG";
        query.tibCodigos = java.util.List.of("1", "2");
        query.entCodigos = java.util.List.of("1", "2");
        query.srsCodigos = java.util.List.of("1", "2");
        query.bfcCodigos = java.util.List.of("1", "2");
        query.serCodigo = "123";
        query.aplicarRegrasDeOrdemDependencia = true;
        query.simulacao = true;

        executarConsulta(query);
    }
}

