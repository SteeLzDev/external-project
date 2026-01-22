package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioOcorrenciaPermissionarioQuery;

public class RelatorioOcorrenciaPermissionarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        RelatorioOcorrenciaPermissionarioQuery query = new RelatorioOcorrenciaPermissionarioQuery();
        query.setCriterios(criterios);

        query.dataIni = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.echCodigo = "123";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

