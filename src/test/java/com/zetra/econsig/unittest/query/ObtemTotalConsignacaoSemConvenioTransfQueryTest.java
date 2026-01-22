package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.transferencia.ObtemTotalConsignacaoSemConvenioTransfQuery;

public class ObtemTotalConsignacaoSemConvenioTransfQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalConsignacaoSemConvenioTransfQuery query = new ObtemTotalConsignacaoSemConvenioTransfQuery();
        query.rseCodigo = "123";
        query.orgCodigo = "751F8080808080808080808080809780";

        executarConsulta(query);
    }
}

