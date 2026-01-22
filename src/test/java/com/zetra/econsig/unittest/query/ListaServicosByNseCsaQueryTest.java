package com.zetra.econsig.unittest.query;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.persistence.query.servico.ListaServicosByNseCsaQuery;
import org.junit.jupiter.api.Test;

public class ListaServicosByNseCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws ZetraException {

        ListaServicosByNseCsaQuery query = new ListaServicosByNseCsaQuery();
        query.nseCodigo = "123";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}
