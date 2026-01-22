package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.beneficiario.ListaTipoArquivoByTarCodigoQuery;

public class ListaTipoArquivoByTarCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoArquivoByTarCodigoQuery query = new ListaTipoArquivoByTarCodigoQuery();
        query.tarCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

