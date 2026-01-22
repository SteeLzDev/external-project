package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.contrato.ListarContratosBeneficioPorRegistroServidorQuery;

public class ListarContratosBeneficioPorRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarContratosBeneficioPorRegistroServidorQuery query = new ListarContratosBeneficioPorRegistroServidorQuery();
        query.scbCodigos = java.util.List.of("1", "2");
        query.rseCodigo = "123";
        query.nseCodigo = "123";
        query.tibCodigo = java.util.List.of("1", "2");
        query.bfcCodigo = "123";
        query.tntCodigo = java.util.List.of("1", "2");
        query.csaCodigo = "267";
        query.reativar = true;
        query.reservaSemRegrasModulo = true;
        query.benCodigo = "123";

        executarConsulta(query);
    }
}

