package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoQuery;

public class ListaServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoQuery query = new ListaServicoQuery();
        query.svcAtivo = 1;
        query.svcIdentificador = "123";
        query.svcDescricao = "123";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.tgsCodigo = "123";
        query.nseCodigo = "123";
        query.svcCodigos = java.util.List.of("1", "2");
        query.marCodigos = java.util.List.of("1", "2");
        query.count = false;

        executarConsulta(query);
    }
}

