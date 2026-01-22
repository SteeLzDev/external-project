package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaConvenioSvcComTpsCodigoQuery;

public class ListaCsaConvenioSvcComTpsCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String tpsCodigo = "123";
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        ListaCsaConvenioSvcComTpsCodigoQuery query = new ListaCsaConvenioSvcComTpsCodigoQuery(tpsCodigo, responsavel);

        executarConsulta(query);
    }
}

