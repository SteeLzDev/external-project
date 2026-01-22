package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioUsuariosQuery;

public class RelatorioUsuariosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioUsuariosQuery query = new RelatorioUsuariosQuery();
        query.corCodigo = "EF128080808080808080808080809980";
        query.csaCodigo = "267";
        query.cseCodigo = "1";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.includeSuporte = true;
        query.funCodigos = java.util.List.of("1", "2");
        query.stuCodigos = java.util.List.of("1", "2");
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        //query.csaCodigoTodosCor = "123";

        executarConsulta(query);
    }
}

