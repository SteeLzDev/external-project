package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ListarRegistroServidorProcessadoQuery;

public class ListarRegistroServidorProcessadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarRegistroServidorProcessadoQuery query = new ListarRegistroServidorProcessadoQuery();
        query.bprPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.srsCodigos = java.util.List.of("1", "2");
        query.tocCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

