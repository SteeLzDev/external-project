package com.zetra.econsig.unittest.query;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.termoAdesao.ObtemTermoAdesaoPorTadCodigoQuery;
import org.junit.jupiter.api.Test;

public class ObtemTermoAdesaoPorTadCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTermoAdesaoPorTadCodigoQuery query = new ObtemTermoAdesaoPorTadCodigoQuery();
        query.responsavel = AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.tadCodigo = "1";

        executarConsulta(query);
    }
}

