package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ObtemTotalUsuariosPorEmailQuery;

public class ObtemTotalUsuariosPorEmailQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalUsuariosPorEmailQuery query = new ObtemTotalUsuariosPorEmailQuery();
        query.usuEmail = "123";
        query.usuCpfExceto = "123";

        executarConsulta(query);
    }
}

