package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuarioInativoQuery;

public class ListaUsuarioInativoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuarioInativoQuery query = new ListaUsuarioInativoQuery();
        query.dataLimiteBloqueio = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.usuCodigo = "123";

        executarConsulta(query);
    }
}

