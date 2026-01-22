package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.retorno.ObtemConsignatariaParaRetornoQuery;

public class ObtemConsignatariaParaRetornoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemConsignatariaParaRetornoQuery query = new ObtemConsignatariaParaRetornoQuery();
        query.csaIdentificador = "123";
        query.orgIdentificador = "123";
        query.estIdentificador = "123";
        query.svcIdentificador = "123";
        query.cnvCodVerba = "123";
        query.cnvCodVerbaRef = "123";
        query.cnvCodVerbaFerias = "123";
        query.rseMatricula = "123";
        query.adeNumero = "123";
        query.adeIndice = "123";

        executarConsulta(query);
    }
}

