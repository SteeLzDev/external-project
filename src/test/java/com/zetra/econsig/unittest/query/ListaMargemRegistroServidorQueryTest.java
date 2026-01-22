package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.margem.ListaMargemRegistroServidorQuery;
import com.zetra.econsig.values.CodedValues;

public class ListaMargemRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaMargemRegistroServidorQuery query = new ListaMargemRegistroServidorQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.rseCodigo = "123";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.nseCodigo = "123";
        query.marCodigo = CodedValues.INCIDE_MARGEM_SIM;
        query.marCodigosPai = java.util.List.of((short) 1, (short) 2);
        query.temConvenioAtivo = true;
        query.alteracaoMultiplaAde = true;

        executarConsulta(query);
    }
}

