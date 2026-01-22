package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.banner.ListaBannerPublicidadeQuery;

public class ListaBannerPublicidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaBannerPublicidadeQuery query = new ListaBannerPublicidadeQuery();
        query.count = false;
        query.exibeMobile = "123";
        query.nseCodigo = "123";
        query.bpuCodigo = "123";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

