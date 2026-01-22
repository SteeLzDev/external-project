package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.correcao.ObtemCoeficienteCorrecaoQuery;

public class ObtemCoeficienteCorrecaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemCoeficienteCorrecaoQuery query = new ObtemCoeficienteCorrecaoQuery();
        query.tccCodigo = "123";
        query.correcaoVlr = "123";
        query.mes = 1;
        query.ano = 1;
        query.primeiro = true;
        query.ultimo = true;

        executarConsulta(query);
    }
}

