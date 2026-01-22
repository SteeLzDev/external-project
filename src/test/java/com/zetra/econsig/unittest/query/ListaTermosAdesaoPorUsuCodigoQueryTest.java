package com.zetra.econsig.unittest.query;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.termoAdesao.ListaTermosAdesaoPorUsuCodigoQuery;
import org.junit.jupiter.api.Test;

public class ListaTermosAdesaoPorUsuCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTermosAdesaoPorUsuCodigoQuery query = new ListaTermosAdesaoPorUsuCodigoQuery();
        query.responsavel = AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

