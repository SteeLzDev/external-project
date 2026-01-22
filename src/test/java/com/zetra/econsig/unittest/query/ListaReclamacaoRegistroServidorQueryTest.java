package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.reclamacao.ListaReclamacaoRegistroServidorQuery;

public class ListaReclamacaoRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaReclamacaoRegistroServidorQuery query = new ListaReclamacaoRegistroServidorQuery();
        query.rrsCodigo = "123";
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.serCpf = "123";
        query.serCodigo = "123";
        query.periodoIni = "01/01/2023";
        query.periodoFim = "01/01/2023";
        query.rseMatricula = "123";
        query.tmrCodigos = java.util.List.of("1", "2");
        query.count = false;

        executarConsulta(query);
    }
}

