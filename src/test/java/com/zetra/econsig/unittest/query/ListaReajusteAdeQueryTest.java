package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.persistence.query.consignacao.ListaReajusteAdeQuery;

public class ListaReajusteAdeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        CustomTransferObject regras = new CustomTransferObject();
        regras.setAttribute("CSA_CODIGO", "267");

        ListaReajusteAdeQuery query = new ListaReajusteAdeQuery(regras);

        executarConsulta(query);
    }
}

