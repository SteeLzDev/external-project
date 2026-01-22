package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioMovFinSerQuery;

public class RelatorioMovFinSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioMovFinSerQuery query = new RelatorioMovFinSerQuery();
        query.tipoEntidade = "ORG";
        query.periodoIni = "2023-01-01";
        query.periodoFim = "2023-01-01";
        query.orgCodigos = java.util.List.of("1", "2");
        query.csaCodigo = "267";
        query.corCodigos = java.util.List.of("1", "2");
        query.estCodigo = "751F8080808080808080808080809680";
        query.svcCodigos = java.util.List.of("1", "2");
        query.sadCodigos = java.util.List.of("1", "2");
        query.spdCodigos = java.util.List.of("1", "2");
        query.order = "CONSIGNATARIA";
        query.sboCodigo = "123";
        query.uniCodigo = "123";
        query.nseCodigos = java.util.List.of("1", "2");
        query.echCodigo = "123";
        query.plaCodigo = "123";
        query.cnvCodVerba = "123";
        query.matricula = "123";
        query.cpf = "123";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

