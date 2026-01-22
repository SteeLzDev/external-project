package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaCodigoVerbaInvalidoQuery;

public class ListaCodigoVerbaInvalidoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.util.List<java.lang.String> orgCodigos = java.util.List.of("1", "2");
        java.util.List<java.lang.String> estCodigos = java.util.List.of("1", "2");

        ListaCodigoVerbaInvalidoQuery query = new ListaCodigoVerbaInvalidoQuery(orgCodigos, estCodigos);

        executarConsulta(query);
    }
}

