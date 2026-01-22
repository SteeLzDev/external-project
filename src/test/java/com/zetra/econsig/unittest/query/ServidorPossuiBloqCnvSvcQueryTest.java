package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ServidorPossuiBloqCnvSvcQuery;

public class ServidorPossuiBloqCnvSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ServidorPossuiBloqCnvSvcQuery query = new ServidorPossuiBloqCnvSvcQuery();
        query.rseCodigo = "123";
        query.vcoCodigo = "123";

        executarConsulta(query);
    }
}

