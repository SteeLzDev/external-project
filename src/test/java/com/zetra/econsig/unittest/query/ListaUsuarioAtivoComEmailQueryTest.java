package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuarioAtivoComEmailQuery;

public class ListaUsuarioAtivoComEmailQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuarioAtivoComEmailQuery query = new ListaUsuarioAtivoComEmailQuery();

        executarConsulta(query);
    }
}
