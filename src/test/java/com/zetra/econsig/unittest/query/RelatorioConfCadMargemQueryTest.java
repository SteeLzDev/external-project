package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioConfCadMargemQuery;
import com.zetra.econsig.values.Columns;

public class RelatorioConfCadMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute(Columns.MAR_CODIGO, List.of("1", "2"));

        RelatorioConfCadMargemQuery query = new RelatorioConfCadMargemQuery();
        query.setCriterios(criterios);

        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

