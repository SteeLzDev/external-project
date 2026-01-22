package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresByCsaConvenioQuery;

public class ListaRegistrosServidoresByCsaConvenioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegistrosServidoresByCsaConvenioQuery query = new ListaRegistrosServidoresByCsaConvenioQuery();
        query.csaCodigo = "267";
        query.cnvCodVerba = "123";

        executarConsulta(query);
    }
}

