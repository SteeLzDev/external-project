package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.correcao.ListaCoeficienteCorrecaoMaisProximoQuery;

public class ListaCoeficienteCorrecaoMaisProximoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCoeficienteCorrecaoMaisProximoQuery query = new ListaCoeficienteCorrecaoMaisProximoQuery();
        query.tccCodigo = "123";
        query.mes = 1;
        query.ano = 1;

        executarConsulta(query);
    }
}

