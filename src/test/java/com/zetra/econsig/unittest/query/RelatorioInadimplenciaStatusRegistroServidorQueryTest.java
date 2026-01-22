package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioInadimplenciaStatusRegistroServidorQuery;

public class RelatorioInadimplenciaStatusRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioInadimplenciaStatusRegistroServidorQuery query = new RelatorioInadimplenciaStatusRegistroServidorQuery();
        query.prdDtDesconto = "2023-01-01";
        query.csaCodigo = "267";
        query.csaProjetoInadimplencia = "123";
        query.naturezaServico = "123";
        query.srsCodigo = "123";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

