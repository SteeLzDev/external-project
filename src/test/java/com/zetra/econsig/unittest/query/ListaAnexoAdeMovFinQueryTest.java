package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.anexo.ListaAnexoAdeMovFinQuery;

public class ListaAnexoAdeMovFinQueryTest extends AbstractQueryTest {

    @Disabled("Usa a tb_tmp_exportacao_ordenada que não existe na base")
    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaAnexoAdeMovFinQuery query = new ListaAnexoAdeMovFinQuery();
        query.estCodigos = List.of("751F8080808080808080808080809680", "1");
        query.orgCodigos = List.of("751F8080808080808080808080809780", "1");
        query.verbas = List.of("123", "22UC");

        executarConsulta(query);
    }
}


