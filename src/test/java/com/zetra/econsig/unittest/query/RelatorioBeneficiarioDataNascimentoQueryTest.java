package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioBeneficiarioDataNascimentoQuery;

public class RelatorioBeneficiarioDataNascimentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("periodoIni", "2023-01-01 00:00:00");
        criterio.setAttribute("periodoFim", "2023-01-01 23:59:59");

        RelatorioBeneficiarioDataNascimentoQuery query = new RelatorioBeneficiarioDataNascimentoQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}

