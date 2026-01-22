package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaTipoDadoAdicionalQuery;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

public class ListaTipoDadoAdicionalQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoDadoAdicionalQuery query = new ListaTipoDadoAdicionalQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.acao = AcaoTipoDadoAdicionalEnum.ALTERA;
        query.visibilidade = VisibilidadeTipoDadoAdicionalEnum.WEB;
        query.svcCodigo = "050E8080808080808080808080808280";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

