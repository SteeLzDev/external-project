package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListaParcelasRejeitadasQuery;

public class ListaParcelasRejeitadasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        String periodo = "2023-01-01";
        String idUsuario = "123";
        List<String> estCodigos = List.of("751F8080808080808080808080809680");
        List<String> orgCodigos = List.of("751F8080808080808080808080809780");
        boolean notificaoObrigatoria = false;

        ListaParcelasRejeitadasQuery query = new ListaParcelasRejeitadasQuery(periodo, idUsuario, orgCodigos, estCodigos, notificaoObrigatoria);
        executarConsulta(query);
    }
}


