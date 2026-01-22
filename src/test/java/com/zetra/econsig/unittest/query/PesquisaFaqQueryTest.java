package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.faq.PesquisaFaqQuery;

public class PesquisaFaqQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        PesquisaFaqQuery query = new PesquisaFaqQuery();
        query.count = false;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.pesquisa = "123";
        query.usuCodigo = "123";

        executarConsulta(query);
    }
}

