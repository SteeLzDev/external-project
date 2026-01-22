package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoPorRseCnvQuery;

public class ObtemTotalValorConsignacaoPorRseCnvQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalValorConsignacaoPorRseCnvQuery query = new ObtemTotalValorConsignacaoPorRseCnvQuery();
        query.rseCodigo = "123";
        query.sadCodigos = java.util.List.of("1", "2");
        query.cnvCodigos = java.util.List.of("1", "2");
        query.periodoAtual = DateHelper.toSQLDate(DateHelper.getSystemDate());

        executarConsulta(query);
    }
}

