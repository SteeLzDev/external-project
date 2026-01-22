package com.zetra.econsig.unittest.query;

import com.zetra.econsig.persistence.query.parametro.ListaCsaPorcentagemCadastradaTpaQuery;
import org.junit.jupiter.api.Test;

public class ListaCsaPorcentagemCadastradaTpaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        
        ListaCsaPorcentagemCadastradaTpaQuery query = new ListaCsaPorcentagemCadastradaTpaQuery();

        executarConsulta(query);
    }
}
