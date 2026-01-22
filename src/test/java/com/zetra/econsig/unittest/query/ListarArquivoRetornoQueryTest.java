package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.arquivo.ListarArquivoRetornoQuery;

public class ListarArquivoRetornoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarArquivoRetornoQuery query = new ListarArquivoRetornoQuery();
        query.nomeArquivo = "123";
        query.lstCsaIdentificador = java.util.List.of("1", "2");
        query.lstOrgIdentificador = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

