package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.calendario.ObtemPeriodoAposPrazoCalendarioFolhaQuery;

public class ObtemPeriodoAposPrazoCalendarioFolhaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ObtemPeriodoAposPrazoCalendarioFolhaQuery query = new ObtemPeriodoAposPrazoCalendarioFolhaQuery();
        query.setCriterios(criterios);

        query.orgCodigos = java.util.List.of("1", "2");
        query.orgCodigo = "751F8080808080808080808080809780";
        query.periodoInicial = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.qtdPeriodos = 1;
        query.ignoraPeriodosAgrupados = true;

        executarConsulta(query);
    }
}

