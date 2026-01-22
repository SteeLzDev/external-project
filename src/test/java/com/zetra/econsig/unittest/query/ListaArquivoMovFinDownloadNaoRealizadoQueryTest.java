package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.movimento.ListaArquivoMovFinDownloadNaoRealizadoQuery;

public class ListaArquivoMovFinDownloadNaoRealizadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaArquivoMovFinDownloadNaoRealizadoQuery query = new ListaArquivoMovFinDownloadNaoRealizadoQuery();
        query.dataInicio = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.diasEnvioEmail = java.util.List.of(1, 2);

        executarConsulta(query);
    }
}

