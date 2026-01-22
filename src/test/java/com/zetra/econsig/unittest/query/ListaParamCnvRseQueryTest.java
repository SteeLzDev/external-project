package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamCnvRseQuery;

public class ListaParamCnvRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamCnvRseQuery query = new ListaParamCnvRseQuery();
        query.count = false;
        query.rseCodigo = "123";
        query.cnvCodigo = "751F808080808080808080809090Z85";
        query.tpsCodigo = "123";

        executarConsulta(query);
    }
}

