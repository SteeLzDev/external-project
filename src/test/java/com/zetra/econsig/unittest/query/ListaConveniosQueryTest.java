package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConveniosQuery;

public class ListaConveniosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConveniosQuery query = new ListaConveniosQuery();
        query.cnvCodVerba = "123";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.ativo = true;
        query.correspondenteConvenio = true;
        query.cnvCodigos = java.util.List.of("1", "2");
        query.svcAtivo = true;
        query.nseCodigo = "123";

        executarConsulta(query);
    }
}

