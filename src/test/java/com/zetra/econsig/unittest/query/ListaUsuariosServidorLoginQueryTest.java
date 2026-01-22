package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuariosServidorLoginQuery;

public class ListaUsuariosServidorLoginQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuariosServidorLoginQuery query = new ListaUsuariosServidorLoginQuery();
        query.usuLogin = "123";
        query.estIdentificador = "123";
        query.orgIdentificador = "123";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.serSomenteAtivo = true;

        executarConsulta(query);
    }
}

