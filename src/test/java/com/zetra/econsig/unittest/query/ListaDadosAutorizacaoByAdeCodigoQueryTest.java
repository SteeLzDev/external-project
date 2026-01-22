package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaDadosAutorizacaoByAdeCodigoQuery;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

public class ListaDadosAutorizacaoByAdeCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaDadosAutorizacaoByAdeCodigoQuery query = new ListaDadosAutorizacaoByAdeCodigoQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.tdaCodigo = "123";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.visibilidade = VisibilidadeTipoDadoAdicionalEnum.WEB;

        executarConsulta(query);
    }
}

