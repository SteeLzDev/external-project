package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioCseOrgQuery;

public class ObtemUsuarioCseOrgQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemUsuarioCseOrgQuery query = new ObtemUsuarioCseOrgQuery();
        query.usuCodigo = "123";
        query.usuCpf = "123";

        executarConsulta(query);
    }
}

