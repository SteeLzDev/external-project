package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.arquivo.ListaParametroValidacaoArquivoQuery;

public class ListaParametroValidacaoArquivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParametroValidacaoArquivoQuery query = new ListaParametroValidacaoArquivoQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.tvaCodigos = java.util.List.of("1", "2");
        query.tvaChaves = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

