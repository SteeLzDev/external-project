package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaSerParaArquivamentoQuery;

public class ListaSerParaArquivamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSerParaArquivamentoQuery query = new ListaSerParaArquivamentoQuery();
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

