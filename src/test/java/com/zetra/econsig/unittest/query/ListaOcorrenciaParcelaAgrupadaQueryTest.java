package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListaOcorrenciaParcelaAgrupadaQuery;

public class ListaOcorrenciaParcelaAgrupadaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaParcelaAgrupadaQuery query = new ListaOcorrenciaParcelaAgrupadaQuery();
        query.estCodigo = "751F8080808080808080808080809680";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.prdDataDesconto = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.spdCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

