package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaFuncoesAuditadasQuery;

public class ListaFuncoesAuditadasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesAuditadasQuery query = new ListaFuncoesAuditadasQuery();
        query.tipo = "CSE";
        query.codigoEntidade = "123";
        query.count = false;

        executarConsulta(query);
    }
}

