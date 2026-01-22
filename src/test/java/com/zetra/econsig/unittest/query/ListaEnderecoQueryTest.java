package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.endereco.ListaEnderecoQuery;

public class ListaEnderecoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaEnderecoQuery query = new ListaEnderecoQuery();
        query.echCodigo = "123";
        query.csaCodigo = "267";
        query.echIdentificador = "123";
        query.echDescricao = "123";
        query.echCondominio = "123";
        query.count = false;

        executarConsulta(query);
    }
}

