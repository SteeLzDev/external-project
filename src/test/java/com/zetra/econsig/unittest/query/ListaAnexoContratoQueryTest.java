package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.anexo.ListaAnexoContratoQuery;

public class ListaAnexoContratoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaAnexoContratoQuery query = new ListaAnexoContratoQuery();
        query.setCriterios(criterios);

        query.csaCodigos = java.util.List.of("1", "2");
        query.svcCodigos = java.util.List.of("1", "2");
        query.sadCodigos = java.util.List.of("1", "2");
        query.aadDataIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.aadDataFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

