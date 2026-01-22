package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ObtemNaturezaServicoQuery;

public class ObtemNaturezaServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemNaturezaServicoQuery query = new ObtemNaturezaServicoQuery();
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

