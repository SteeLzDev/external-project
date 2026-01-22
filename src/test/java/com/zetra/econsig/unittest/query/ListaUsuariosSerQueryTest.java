package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuariosSerQuery;

public class ListaUsuariosSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuariosSerQuery query = new ListaUsuariosSerQuery();
        query.count = false;
        query.rseCodigo = "123";
        query.orgIdentificador = "123";
        query.estIdentificador = "123";
        query.serCpf = "123";
        query.rseMatricula = "123";
        query.serNome = "123";
        query.serDataNasc = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.serNomeMae = "123";
        query.serCelular = "123";
        query.somenteAtivos = true;

        executarConsulta(query);
    }
}

