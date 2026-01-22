package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemBloqueioServicoRegistroServidorQuery;

public class ObtemBloqueioServicoRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemBloqueioServicoRegistroServidorQuery query = new ObtemBloqueioServicoRegistroServidorQuery();
        query.rseCodigo = "123";
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

