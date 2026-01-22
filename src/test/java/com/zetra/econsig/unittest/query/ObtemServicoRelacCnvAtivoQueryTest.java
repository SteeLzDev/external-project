package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ObtemServicoRelacCnvAtivoQuery;

public class ObtemServicoRelacCnvAtivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemServicoRelacCnvAtivoQuery query = new ObtemServicoRelacCnvAtivoQuery();
        query.svcCodigoOrigem = "123";
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.tntCodigo = "123";

        executarConsulta(query);
    }
}

