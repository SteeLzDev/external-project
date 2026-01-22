package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListaParcelaDescontoUsuariosRejeitadosQuery;

public class ListaParcelaDescontoUsuariosRejeitadosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String periodo = "2023-01-01";
        List<String> estCodigos = List.of("751F8080808080808080808080809680");
        List<String> orgCodigos = List.of("751F8080808080808080808080809780");
        boolean notificaoObrigatoria = false;

        ListaParcelaDescontoUsuariosRejeitadosQuery query = new ListaParcelaDescontoUsuariosRejeitadosQuery(periodo, estCodigos, orgCodigos, notificaoObrigatoria);
        executarConsulta(query);
    }
}


