package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.reclamacao.ListaReclamacaoMotivoQuery;

public class ListaReclamacaoMotivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaReclamacaoMotivoQuery query = new ListaReclamacaoMotivoQuery();
        query.rrsCodigo = "123";
        query.count = false;

        executarConsulta(query);
    }
}

