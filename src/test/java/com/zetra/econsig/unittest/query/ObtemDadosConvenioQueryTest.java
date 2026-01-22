package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ObtemDadosConvenioQuery;

public class ObtemDadosConvenioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemDadosConvenioQuery query = new ObtemDadosConvenioQuery();
        query.cnvCodigo = "751F808080808080808080809090Z85";
        query.corCodigo = "EF128080808080808080808080809980";

        executarConsulta(query);
    }
}

