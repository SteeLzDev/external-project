package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamMensagemSolicitacaoOutroSvcQuery;

public class ListaParamMensagemSolicitacaoOutroSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamMensagemSolicitacaoOutroSvcQuery query = new ListaParamMensagemSolicitacaoOutroSvcQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.prazo = 1;
        query.dia = 1;

        executarConsulta(query);
    }
}

