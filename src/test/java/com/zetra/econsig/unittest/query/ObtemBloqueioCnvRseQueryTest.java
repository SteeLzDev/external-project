package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ObtemBloqueioCnvRseQuery;

public class ObtemBloqueioCnvRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemBloqueioCnvRseQuery query = new ObtemBloqueioCnvRseQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.inativosSomenteComBloqueio = true;

        executarConsulta(query);
    }
}

