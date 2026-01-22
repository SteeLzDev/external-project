package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioCompraContratoQuery;

public class RelatorioCompraContratoQueryTest extends AbstractQueryTest {

    @Disabled("Não tem um getFields implementado")
    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioCompraContratoQuery query = new RelatorioCompraContratoQuery();
        query.parametrosTO = new CustomTransferObject();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

