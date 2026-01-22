package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.arquivo.ListarNomeArquivoRetornoQuery;

public class ListarNomeArquivoRetornoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarNomeArquivoRetornoQuery query = new ListarNomeArquivoRetornoQuery();

        executarConsulta(query);
    }
}

