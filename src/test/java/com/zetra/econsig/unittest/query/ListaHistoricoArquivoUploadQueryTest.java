package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.arquivo.ListaHistoricoArquivoUploadQuery;

public class ListaHistoricoArquivoUploadQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaHistoricoArquivoUploadQuery query = new ListaHistoricoArquivoUploadQuery();
        query.dataInicial = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.dataFinal = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.tarCodigo = java.util.List.of("1", "2");
        query.periodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.tipoEntidade = "ORG";
        query.funCodigo = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.estCodigo = "751F8080808080808080808080809680";

        executarConsulta(query);
    }
}

