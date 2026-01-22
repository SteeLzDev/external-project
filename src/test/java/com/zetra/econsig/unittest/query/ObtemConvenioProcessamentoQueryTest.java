package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ObtemConvenioProcessamentoQuery;

public class ObtemConvenioProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemConvenioProcessamentoQuery query = new ObtemConvenioProcessamentoQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.cnvCodigos = java.util.List.of("1", "2");
        query.rseCodigo = "123";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.adeNumero = 1l;
        query.adeIndice = "123";

        executarConsulta(query);
    }
}

