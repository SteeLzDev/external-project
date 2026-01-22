package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficiariosEscolhidosQuery;

public class ListarBeneficiariosEscolhidosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListarBeneficiariosEscolhidosQuery query = new ListarBeneficiariosEscolhidosQuery();
        query.setCriterios(criterios);

        query.bfcCodigos = java.util.List.of("1", "2");
        query.rseCodigo = "123";
        query.notScbCodigos = java.util.List.of("1", "2");
        query.nseCodigo = "123";
        query.sbeCodigos = java.util.List.of("1", "2");
        query.benCodigo = "123";

        executarConsulta(query);
    }
}

