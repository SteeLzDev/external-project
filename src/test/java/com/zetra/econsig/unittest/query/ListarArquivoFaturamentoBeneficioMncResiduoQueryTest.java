package com.zetra.econsig.unittest.query;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.beneficios.faturamento.ListarArquivoFaturamentoBeneficioMncResiduoQuery;

public class ListarArquivoFaturamentoBeneficioMncResiduoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListarArquivoFaturamentoBeneficioMncResiduoQuery query = new ListarArquivoFaturamentoBeneficioMncResiduoQuery();
        query.setCriterios(criterios);

        query.fatCodigo = "123";
        query.pcsVlr = BigDecimal.ONE;

        executarConsulta(query);
    }
}

