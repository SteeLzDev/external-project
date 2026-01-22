package com.zetra.econsig.unittest.query;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoPortabilidadeCartaoCsaQuery;
import org.junit.jupiter.api.Test;

public class ObtemTotalConsignacaoPortabilidadeCartaoCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws ZetraException {
        final ObtemTotalConsignacaoPortabilidadeCartaoCsaQuery query = new ObtemTotalConsignacaoPortabilidadeCartaoCsaQuery();
        query.csaCodigo = "267";
        query.responsavel = AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}
