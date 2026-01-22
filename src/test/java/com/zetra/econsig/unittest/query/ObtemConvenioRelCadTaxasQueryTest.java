package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ObtemConvenioRelCadTaxasQuery;

public class ObtemConvenioRelCadTaxasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemConvenioRelCadTaxasQuery query = new ObtemConvenioRelCadTaxasQuery();
        query.svcCodigoOrigem = "123";
        query.csaCodigo = "267";
        query.corCodigo = "EF128080808080808080808080809980";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.rseCodigo = "123";
        query.adeCodigosReneg = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

