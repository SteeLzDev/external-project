package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListaParcelasComAcaoEmTipoDescontoQuery;

public class ListaParcelasComAcaoEmTipoDescontoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        List<String> estCodigos = List.of("751F8080808080808080808080809680");
        List<String> orgCodigos = List.of("751F8080808080808080808080809780");

        ListaParcelasComAcaoEmTipoDescontoQuery query = new ListaParcelasComAcaoEmTipoDescontoQuery(orgCodigos, estCodigos);
        executarConsulta(query);
    }
}


