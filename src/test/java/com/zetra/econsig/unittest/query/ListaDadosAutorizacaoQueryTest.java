package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaDadosAutorizacaoQuery;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

public class ListaDadosAutorizacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaDadosAutorizacaoQuery query = new ListaDadosAutorizacaoQuery();
        query.tdaCodigo = "123";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.visibilidade = VisibilidadeTipoDadoAdicionalEnum.WEB;

        executarConsulta(query);
    }
}

