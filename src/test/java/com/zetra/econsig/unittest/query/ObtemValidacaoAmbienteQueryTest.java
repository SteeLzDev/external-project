package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.admin.ObtemValidacaoAmbienteQuery;
import com.zetra.econsig.values.RegraValidacaoEnum;

public class ObtemValidacaoAmbienteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ObtemValidacaoAmbienteQuery query = new ObtemValidacaoAmbienteQuery();
        query.setCriterios(criterios);

        query.regraValidacaoEnum = RegraValidacaoEnum.VALIDAR_INNODB_MYSQL;

        executarConsulta(query);
    }
}

