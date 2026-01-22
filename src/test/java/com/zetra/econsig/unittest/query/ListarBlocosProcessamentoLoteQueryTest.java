package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.lote.ListarBlocosProcessamentoLoteQuery;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;

public class ListarBlocosProcessamentoLoteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListarBlocosProcessamentoLoteQuery query = new ListarBlocosProcessamentoLoteQuery("/home/eConsig/arquivos/lote/csa/1/lote.txt", StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO, "267");

        executarConsulta(query);
    }
}
