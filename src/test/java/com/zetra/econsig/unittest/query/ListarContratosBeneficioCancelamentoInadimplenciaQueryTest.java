package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.contrato.ListarContratosBeneficioCancelamentoInadimplenciaQuery;

public class ListarContratosBeneficioCancelamentoInadimplenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarContratosBeneficioCancelamentoInadimplenciaQuery query = new ListarContratosBeneficioCancelamentoInadimplenciaQuery();
        query.rseMatricula = "123";
        query.bfcCpf = "123";
        query.benCodigoContrato = "123";

        executarConsulta(query);
    }
}

