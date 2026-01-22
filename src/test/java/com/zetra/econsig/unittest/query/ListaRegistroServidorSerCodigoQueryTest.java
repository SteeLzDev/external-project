package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorSerCodigoQuery;

public class ListaRegistroServidorSerCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegistroServidorSerCodigoQuery query = new ListaRegistroServidorSerCodigoQuery();
        query.serCpf = "123";
        query.serCodigo = "123";
        query.count = false;
        query.recuperaRseExcluido = true;

        executarConsulta(query);
    }
}

