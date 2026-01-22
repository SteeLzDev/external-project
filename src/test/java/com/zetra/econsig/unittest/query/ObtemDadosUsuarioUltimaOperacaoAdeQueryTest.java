package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemDadosUsuarioUltimaOperacaoAdeQuery;

public class ObtemDadosUsuarioUltimaOperacaoAdeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemDadosUsuarioUltimaOperacaoAdeQuery query = new ObtemDadosUsuarioUltimaOperacaoAdeQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.tocCodigo = "123";

        executarConsulta(query);
    }
}

