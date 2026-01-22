package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorPorCpfEmailQuery;

public class ListaRegistroServidorPorCpfEmailQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegistroServidorPorCpfEmailQuery query = new ListaRegistroServidorPorCpfEmailQuery();
        query.serCpf = "111.111.111-11";
        query.recuperaRseExcluido = false;

        executarConsulta(query);
    }

    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {

        ListaRegistroServidorPorCpfEmailQuery query = new ListaRegistroServidorPorCpfEmailQuery();
        query.serEmail = "teste@nostrum.com.br";
        query.recuperaRseExcluido = false;

        executarConsulta(query);
    }
}
