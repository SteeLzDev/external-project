package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.PesquisaAdeLstIndiceQuery;

public class PesquisaAdeLstIndiceQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        PesquisaAdeLstIndiceQuery query = new PesquisaAdeLstIndiceQuery();
        query.rseCodigo = "123";
        query.cnvCodigo = "751F808080808080808080809090Z85";
        query.sadCodigos = java.util.List.of("1", "2");
        query.adeCodigosRenegociacao = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

