package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.orgao.ObtemOrgaoMaxCnvAtivoQuery;

public class ObtemOrgaoMaxCnvAtivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemOrgaoMaxCnvAtivoQuery query = new ObtemOrgaoMaxCnvAtivoQuery();
        query.estCodigo = "751F8080808080808080808080809680";

        executarConsulta(query);
    }
}

