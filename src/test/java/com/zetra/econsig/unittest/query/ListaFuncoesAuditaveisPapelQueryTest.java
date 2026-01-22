package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaFuncoesAuditaveisPapelQuery;

public class ListaFuncoesAuditaveisPapelQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesAuditaveisPapelQuery query = new ListaFuncoesAuditaveisPapelQuery();
        query.codigoEntidade = "123";
        query.usuCodigoResponsavel = "123";
        query.papCodigoDestino = "1";
        query.papCodigoOrigem = "1";
        query.perCodigoResponsavel = "123";
        query.tipo = "CSE";

        executarConsulta(query);
    }
}

