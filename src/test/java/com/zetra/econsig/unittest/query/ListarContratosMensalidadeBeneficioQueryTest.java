package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.contrato.ListarContratosMensalidadeBeneficioQuery;

public class ListarContratosMensalidadeBeneficioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarContratosMensalidadeBeneficioQuery query = new ListarContratosMensalidadeBeneficioQuery();
        query.csaCodigo = "267";
        query.cbeNumero = "123";
        query.tlaCodigoMensalidade = "123";
        query.svcCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

