package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.validardocumento.AuditoriaValidarDocumentosQuery;

public class AuditoriaValidarDocumentosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        AuditoriaValidarDocumentosQuery query = new AuditoriaValidarDocumentosQuery();
        query.periodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.usuarios = true;

        executarConsulta(query);
    }
}

