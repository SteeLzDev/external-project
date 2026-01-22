package com.zetra.econsig.unittest.query;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPortabilidadeCartaoQuery;
import org.junit.jupiter.api.Test;

public class ListaConsignacaoPortabilidadeCartaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws ZetraException {
        final ListaConsignacaoPortabilidadeCartaoQuery query = new ListaConsignacaoPortabilidadeCartaoQuery();
        query.adeCodigos = java.util.List.of("1","2");
        query.responsavel = AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }

    @Test
    public void test_02() throws ZetraException {
        final ListaConsignacaoPortabilidadeCartaoQuery query = new ListaConsignacaoPortabilidadeCartaoQuery();
        query.csaCodigo = "267";
        query.responsavel = AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}
