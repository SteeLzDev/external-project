package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemServidorNaoPertenceEntidadeQuery;

public class ObtemServidorNaoPertenceEntidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemServidorNaoPertenceEntidadeQuery query = new ObtemServidorNaoPertenceEntidadeQuery();
        query.rseCodigo = java.util.List.of("1", "2");
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";

        executarConsulta(query);
    }
}

