package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.correcao.ObtemTipoCoeficienteCorrecaoQuery;

public class ObtemTipoCoeficienteCorrecaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTipoCoeficienteCorrecaoQuery query = new ObtemTipoCoeficienteCorrecaoQuery();
        query.count = false;
        query.tccCodigo = "123";
        query.notTccCodigo = "123";
        query.tccDescricao = "123";

        executarConsulta(query);
    }
}

