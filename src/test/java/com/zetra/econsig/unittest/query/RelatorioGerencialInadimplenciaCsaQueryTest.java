package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialInadimplenciaCsaQuery;

public class RelatorioGerencialInadimplenciaCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        RelatorioGerencialInadimplenciaCsaQuery query = new RelatorioGerencialInadimplenciaCsaQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.csaCodigo = List.of("267");
        query.periodo = DateHelper.getSystemDate();

        executarConsulta(query);
    }
}


