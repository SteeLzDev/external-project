package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.servidor.ObtemRelacionamentoInconsistenteTransferenciaQuery;

public class ObtemRelacionamentoInconsistenteTransferenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ObtemRelacionamentoInconsistenteTransferenciaQuery query = new ObtemRelacionamentoInconsistenteTransferenciaQuery();
        query.setCriterios(criterios);


        executarConsulta(query);
    }
}

