package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemQtdRegistroServidorAtivoQuery;

public class ObtemQtdRegistroServidorAtivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemQtdRegistroServidorAtivoQuery query = new ObtemQtdRegistroServidorAtivoQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";

        executarConsulta(query);
    }
}

