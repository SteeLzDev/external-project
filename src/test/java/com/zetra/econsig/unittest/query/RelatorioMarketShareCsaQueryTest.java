package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.relatorio.RelatorioMarketShareCsaQuery;

public class RelatorioMarketShareCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        RelatorioMarketShareCsaQuery query = new RelatorioMarketShareCsaQuery();
        query.setCriterios(criterios);

        query.periodo = "2023-01-01";
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigo = "751F8080808080808080808080809680";
        query.nseCodigos = java.util.List.of("1", "2");
        query.svcCodigos = java.util.List.of("1", "2");
        query.csaCodigos = java.util.List.of("1", "2");
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

