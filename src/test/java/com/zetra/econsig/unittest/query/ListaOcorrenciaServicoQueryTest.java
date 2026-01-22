package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaOcorrenciaServicoQuery;

public class ListaOcorrenciaServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaServicoQuery query = new ListaOcorrenciaServicoQuery();
        query.count = false;
        query.svcCodigo = "050E8080808080808080808080808280";
        query.tocCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

