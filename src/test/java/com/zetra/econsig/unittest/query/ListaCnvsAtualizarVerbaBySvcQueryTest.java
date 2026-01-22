package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaCnvsAtualizarVerbaBySvcQuery;

public class ListaCnvsAtualizarVerbaBySvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCnvsAtualizarVerbaBySvcQuery query = new ListaCnvsAtualizarVerbaBySvcQuery();

        executarConsulta(query);
    }
}

