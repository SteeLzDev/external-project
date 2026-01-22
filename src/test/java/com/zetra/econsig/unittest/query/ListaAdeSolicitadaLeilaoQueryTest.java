package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaAdeSolicitadaLeilaoQuery;

public class ListaAdeSolicitadaLeilaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAdeSolicitadaLeilaoQuery query = new ListaAdeSolicitadaLeilaoQuery();
        query.rseCodigo = "123";
        query.dataInicial = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.solicitacaoLeilao = true;
        query.concretizado = true;

        executarConsulta(query);
    }
}

