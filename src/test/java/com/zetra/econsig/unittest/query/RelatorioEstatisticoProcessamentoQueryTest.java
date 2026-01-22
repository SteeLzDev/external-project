package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioEstatisticoProcessamentoQuery;

public class RelatorioEstatisticoProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioEstatisticoProcessamentoQuery query = new RelatorioEstatisticoProcessamentoQuery();
        query.funCodigo = "123";
        query.tarCodigo = "123";
        query.harPeriodos = null;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

