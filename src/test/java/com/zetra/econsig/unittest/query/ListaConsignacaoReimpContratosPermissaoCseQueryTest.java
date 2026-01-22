package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.movimento.ListaConsignacaoReimpContratosPermissaoCseQuery;
import com.zetra.econsig.values.Columns;

public class ListaConsignacaoReimpContratosPermissaoCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.EST_CODIGO, List.of("751F8080808080808080808080809680"));
        criterio.setAttribute(Columns.ORG_CODIGO, List.of("751F8080808080808080808080809780"));

        ListaConsignacaoReimpContratosPermissaoCseQuery query = new ListaConsignacaoReimpContratosPermissaoCseQuery();
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}
