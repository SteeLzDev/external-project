package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignante.ListaOcorrenciaConsignanteQuery;

public class ListaOcorrenciaConsignanteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaConsignanteQuery query = new ListaOcorrenciaConsignanteQuery();
        query.count = false;
        query.versao = true;
        query.cseCodigo = "1";
        query.oceCodigo = "123";
        query.tocCodigo = "123";

        executarConsulta(query);
    }
}

