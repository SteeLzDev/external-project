package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialGeralTaxasQuery;

public class RelatorioGerencialGeralTaxasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        String svcCodigo = "050E8080808080808080808080808280";

        TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute("maxPrazo", 120);
        criterios.setAttribute("svcCodigo", svcCodigo);

        RelatorioGerencialGeralTaxasQuery query = new RelatorioGerencialGeralTaxasQuery(svcCodigo);
        query.setCriterios(criterios);

        executarConsulta(query);
    }
}

