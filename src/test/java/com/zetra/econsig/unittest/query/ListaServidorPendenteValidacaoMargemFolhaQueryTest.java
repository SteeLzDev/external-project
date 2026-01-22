package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaServidorPendenteValidacaoMargemFolhaQuery;

public class ListaServidorPendenteValidacaoMargemFolhaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        List<String> estCodigos = List.of("751F8080808080808080808080809680");
        List<String> orgCodigos = List.of("751F8080808080808080808080809780");

        ListaServidorPendenteValidacaoMargemFolhaQuery query = new ListaServidorPendenteValidacaoMargemFolhaQuery(estCodigos, orgCodigos);
        executarConsulta(query);
    }
}


