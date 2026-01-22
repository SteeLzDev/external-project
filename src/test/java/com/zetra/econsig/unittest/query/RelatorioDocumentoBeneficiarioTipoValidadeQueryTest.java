package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioDocumentoBeneficiarioTipoValidadeQuery;

public class RelatorioDocumentoBeneficiarioTipoValidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioDocumentoBeneficiarioTipoValidadeQuery query = new RelatorioDocumentoBeneficiarioTipoValidadeQuery();
        query.dataIni = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";
        query.tipoDocumento = java.util.List.of("1", "2");
        query.tarCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

