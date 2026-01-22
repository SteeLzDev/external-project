package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListarServidoresConsignacaoPendenteReativacaoQuery;

public class ListarServidoresConsignacaoPendenteReativacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarServidoresConsignacaoPendenteReativacaoQuery query = new ListarServidoresConsignacaoPendenteReativacaoQuery();

        executarConsulta(query);
    }
}

