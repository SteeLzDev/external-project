package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.faq.ListaFaqQuery;

public class ListaFaqQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFaqQuery query = new ListaFaqQuery();
        query.count = false;
        query.faqExibeCsa = "123";
        query.faqExibeCor = "123";
        query.faqExibeCse = "123";
        query.faqExibeOrg = "123";
        query.faqExibeSer = "123";
        query.faqExibeSup = "123";
        query.faqTitulo1 = "123";
        query.faqTitulo2 = "123";
        query.faqExibeMobile = "123";

        executarConsulta(query);
    }
}

