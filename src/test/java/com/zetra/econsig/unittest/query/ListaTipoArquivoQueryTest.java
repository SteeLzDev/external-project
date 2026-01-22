package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaTipoArquivoQuery;

public class ListaTipoArquivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoArquivoQuery query = new ListaTipoArquivoQuery();
        query.tarUploadSer = "123";

        executarConsulta(query);
    }
}

