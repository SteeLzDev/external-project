package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.anexo.ListaConsignatariasPendenciaAnexoConsignacaoQuery;

public class ListaConsignatariasPendenciaAnexoConsignacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        ListaConsignatariasPendenciaAnexoConsignacaoQuery query = new ListaConsignatariasPendenciaAnexoConsignacaoQuery(responsavel);
        query.setCriterios(criterios);

        query.count = false;

        executarConsulta(query);
    }
}

