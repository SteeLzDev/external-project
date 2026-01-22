package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.arquivo.ListaHistoricoArquivoQuery;

public class ListaHistoricoArquivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaHistoricoArquivoQuery query = new ListaHistoricoArquivoQuery();
        query.dataInicial = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.dataFinal = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.tarCodigo = java.util.List.of("1", "2");
        query.periodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.tipoEntidade = "ORG";

        executarConsulta(query);
    }
}

