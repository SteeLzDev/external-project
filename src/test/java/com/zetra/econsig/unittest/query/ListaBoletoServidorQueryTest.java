package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.boleto.ListaBoletoServidorQuery;

public class ListaBoletoServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaBoletoServidorQuery query = new ListaBoletoServidorQuery();
        query.count = false;
        query.serCpf = "123";
        query.serNome = "123";
        query.listarSomenteNaoBaixados = true;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

