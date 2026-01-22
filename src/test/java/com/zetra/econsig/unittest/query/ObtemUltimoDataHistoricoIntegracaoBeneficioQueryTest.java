package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ObtemUltimoDataHistoricoIntegracaoBeneficioQuery;

public class ObtemUltimoDataHistoricoIntegracaoBeneficioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemUltimoDataHistoricoIntegracaoBeneficioQuery query = new ObtemUltimoDataHistoricoIntegracaoBeneficioQuery();
        query.csaCodigo = "267";
        query.hibTipo = 0;

        executarConsulta(query);
    }
}

