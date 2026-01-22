package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioPelosIdentificadoresQuery;

public class ListaConvenioPelosIdentificadoresQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioPelosIdentificadoresQuery query = new ListaConvenioPelosIdentificadoresQuery();
        query.csaIdentificador = "123";
        query.estIdentificador = "123";
        query.orgIdentificador = "123";
        query.svcIdentificador = "123";
        query.cnvCodVerba = "123";

        executarConsulta(query);
    }
}

