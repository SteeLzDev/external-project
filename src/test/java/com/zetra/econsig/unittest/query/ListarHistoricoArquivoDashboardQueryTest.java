package com.zetra.econsig.unittest.query;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.arquivo.ListarHistoricoArquivoDashboardQuery;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class ListarHistoricoArquivoDashboardQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        final String csaCodigo = "C88E808080808080808080808888F01A";
        Date filterDate = DateHelper.getDate(24, 4, 1);
        ListarHistoricoArquivoDashboardQuery query = new ListarHistoricoArquivoDashboardQuery(csaCodigo, filterDate);

        executarConsulta(query);
    }
}
