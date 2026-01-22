package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.consignacao.ListaTransfereAdeQuery;

public class ListaTransfereAdeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaTransfereAdeQuery query = new ListaTransfereAdeQuery();
        query.setCriterios(criterios);

        query.csaCodigoOrigem = "123";
        query.csaCodigoDestino = "123";
        query.svcCodigoOrigem = "123";
        query.svcCodigoDestino = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.sadCodigo = java.util.List.of("1", "2");
        query.periodoIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.adeNumero = java.util.List.of(1l, 2l);
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.count = false;
        query.somenteConveniosAtivos = true;

        executarConsulta(query);
    }
}

