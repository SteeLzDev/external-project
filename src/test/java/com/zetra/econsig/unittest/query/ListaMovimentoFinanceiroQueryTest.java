package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.movimento.ListaMovimentoFinanceiroQuery;

public class ListaMovimentoFinanceiroQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaMovimentoFinanceiroQuery query = new ListaMovimentoFinanceiroQuery();
        query.setCriterios(criterios);

        query.periodo = DateHelper.toSQLDate(DateHelper.getSystemDate());
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.orgIdentificador = "123";
        query.estIdentificador = "123";
        query.csaIdentificador = "123";
        query.svcIdentificador = "123";
        query.cnvCodVerba = "123";

        executarConsulta(query);
    }
}

