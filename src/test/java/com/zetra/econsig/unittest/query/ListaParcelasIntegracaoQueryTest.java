package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListaParcelasIntegracaoQuery;

public class ListaParcelasIntegracaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParcelasIntegracaoQuery query = new ListaParcelasIntegracaoQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.count = false;
        query.tipo = "123";
        query.adeNumero = "123";
        query.adeIdentificador = "123";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.csaCodigo = "267";
        query.tocCodigos = java.util.List.of("1", "2");
        query.spdCodigos = java.util.List.of("1", "2");
        query.papCodigos = java.util.List.of("1", "2");
        query.periodoIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.matriculaExataSoap = true;
        query.buscaCse = true;
        query.buscaCsa = true;
        query.buscaCor = true;
        query.buscaOrg = true;
        query.buscaSup = true;

        executarConsulta(query);
    }
}

