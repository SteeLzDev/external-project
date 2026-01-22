package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaDadosServidorQuery;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

public class ListaDadosServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaDadosServidorQuery query = new ListaDadosServidorQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.acao = AcaoTipoDadoAdicionalEnum.ALTERA;
        query.visibilidade = VisibilidadeTipoDadoAdicionalEnum.WEB;
        query.serCodigo = "123";

        executarConsulta(query);
    }
}

