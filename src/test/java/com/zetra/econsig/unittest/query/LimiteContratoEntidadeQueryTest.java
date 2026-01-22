package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.LimiteContratoEntidadeQuery;

public class LimiteContratoEntidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        LimiteContratoEntidadeQuery query = new LimiteContratoEntidadeQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.rseMatricula = "123";
        query.rseNome = "123";
        query.order = "ORD01";
        query.tipo = "123";
        query.crsDescricao = "123";

        executarConsulta(query);
    }
}

