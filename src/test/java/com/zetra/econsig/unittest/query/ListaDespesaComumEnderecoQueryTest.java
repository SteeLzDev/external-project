package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.sdp.despesacomum.ListaDespesaComumEnderecoQuery;

public class ListaDespesaComumEnderecoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaDespesaComumEnderecoQuery query = new ListaDespesaComumEnderecoQuery();
        query.echCodigo = "123";
        query.plaCodigo = "123";
        query.tppCodigo = "123";
        query.pplValor = "123";

        executarConsulta(query);
    }
}

