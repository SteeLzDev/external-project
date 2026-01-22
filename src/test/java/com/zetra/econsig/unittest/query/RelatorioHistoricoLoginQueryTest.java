package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioHistoricoLoginQuery;

public class RelatorioHistoricoLoginQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("DATA_INI", "2023-01-01 00:00:00");
        criterio.setAttribute("DATA_FIM", "2023-01-01 23:59:59");

        RelatorioHistoricoLoginQuery query = new RelatorioHistoricoLoginQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}

