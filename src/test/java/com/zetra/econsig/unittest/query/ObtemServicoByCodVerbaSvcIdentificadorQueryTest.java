package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ObtemServicoByCodVerbaSvcIdentificadorQuery;

public class ObtemServicoByCodVerbaSvcIdentificadorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemServicoByCodVerbaSvcIdentificadorQuery query = new ObtemServicoByCodVerbaSvcIdentificadorQuery();
        query.svcIdentificador = "123";
        query.cnvCodVerba = "123";
        query.orgCodigos = java.util.List.of("1", "2");
        query.csaCodigo = "267";
        query.nseCodigo = "123";
        query.ativo = true;

        executarConsulta(query);
    }
}

