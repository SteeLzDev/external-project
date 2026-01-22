package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaServicoLimitePermitidoQuery;
import com.zetra.econsig.values.CodedValues;

public class ListaServicoLimitePermitidoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaServicoLimitePermitidoQuery query = new ListaServicoLimitePermitidoQuery();
        query.rseCodigo = "123";
        query.cnvCodigo = List.of("751F808080808080808080809090Z85", "1");
        query.svcCodigo = List.of("050E8080808080808080808080808280", "1");
        query.nseCodigo = List.of(CodedValues.NSE_EMPRESTIMO, CodedValues.NSE_SALARYPAY);
        query.ignoraConcluir = true;

        executarConsulta(query);
    }
}


