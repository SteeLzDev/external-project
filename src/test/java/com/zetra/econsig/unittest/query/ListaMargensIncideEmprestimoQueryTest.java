package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaMargensIncideEmprestimoQuery;

public class ListaMargensIncideEmprestimoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaMargensIncideEmprestimoQuery query = new ListaMargensIncideEmprestimoQuery();
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}
