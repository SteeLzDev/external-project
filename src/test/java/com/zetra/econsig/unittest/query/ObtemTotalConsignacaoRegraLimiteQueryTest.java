package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoRegraLimiteQuery;

public class ObtemTotalConsignacaoRegraLimiteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigoOperacao = "123";
        java.util.List<java.lang.String> adeCodigosRenegociacao = java.util.List.of("1", "2");
        TransferObject regra = new CustomTransferObject();

        ObtemTotalConsignacaoRegraLimiteQuery query = new ObtemTotalConsignacaoRegraLimiteQuery(rseCodigoOperacao, adeCodigosRenegociacao, regra);

        executarConsulta(query);
    }
}

