package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.consignacao.ListaTotalConsignacaoAtivasPorOrgaoQuery;

public class ListaTotalConsignacaoAtivasPorOrgaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        ListaTotalConsignacaoAtivasPorOrgaoQuery query = new ListaTotalConsignacaoAtivasPorOrgaoQuery();
        query.setCriterios(criterios);
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("csa", getLoopbackAddress(), null);

        executarConsulta(query);
    }

}

