package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioInadimplenciaTipoOcorrenciaQuery;

public class RelatorioInadimplenciaTipoOcorrenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioInadimplenciaTipoOcorrenciaQuery query = new RelatorioInadimplenciaTipoOcorrenciaQuery();
        query.prdDtDesconto = "2023-01-01";
        query.csaCodigo = "267";
        query.csaProjetoInadimplencia = "123";
        query.naturezaServico = "123";
        query.tocCodigo = "123";
        query.count = false;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

