package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.mensagem.ListaMensagemQuery;

public class ListaMensagemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaMensagemQuery query = new ListaMensagemQuery();
        query.count = false;
        query.menExibeCsa = "S";
        //query.menExibeCor = "S";
        query.menExibeCse = "S";
        query.menExibeOrg = "S";
        query.menExibeSer = "S";
        query.menExibeSup = "S";
        query.menCodigo = java.util.List.of("1", "2");
        query.menTitulo = "123";
        query.exibeMenPublica = true;

        executarConsulta(query);
    }
}

