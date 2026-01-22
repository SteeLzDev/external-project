package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaProvisionamentoMargemQuery;

public class ListaProvisionamentoMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaProvisionamentoMargemQuery query = new ListaProvisionamentoMargemQuery();
        query.rseCodigo = "123";
        query.adeCodigos = java.util.List.of("1", "2");
        query.excluirAdesCodigos = true;

        executarConsulta(query);
    }
}

