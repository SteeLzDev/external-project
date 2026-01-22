package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.persistence.query.servidor.ListaServidorPendenteQuery;

public class ListaServidorPendenteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServidorPendenteQuery query = new ListaServidorPendenteQuery();
        query.count = false;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.criterio = new CustomTransferObject();

        executarConsulta(query);
    }
}

