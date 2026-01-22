package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.beneficios.faturamento.ListarArquivoFaturamentoBeneficioPrincipalQuery;

public class ListarArquivoFaturamentoBeneficioPrincipalQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListarArquivoFaturamentoBeneficioPrincipalQuery query = new ListarArquivoFaturamentoBeneficioPrincipalQuery();
        query.setCriterios(criterios);

        query.fatCodigo = "123";
        query.creditos = true;

        executarConsulta(query);
    }
}

