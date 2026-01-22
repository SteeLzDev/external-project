package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.log.ObtemLogDataAtualQuery;

public class ObtemLogDataAtualQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemLogDataAtualQuery query = new ObtemLogDataAtualQuery();

        executarConsulta(query);
    }
}

