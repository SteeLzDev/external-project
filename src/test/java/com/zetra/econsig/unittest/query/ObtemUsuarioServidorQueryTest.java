package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioServidorQuery;

public class ObtemUsuarioServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemUsuarioServidorQuery query = new ObtemUsuarioServidorQuery();
        query.usuCodigo = "123";
        query.usuLogin = "123";
        query.rseMatricula = "123";
        query.orgIdentificador = "123";
        query.estIdentificador = "123";
        query.serCodigo = "123";
        query.permiteExcluidoFalecido = true;

        executarConsulta(query);
    }
}

