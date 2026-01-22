package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.relatorio.RelatorioAnexosConsignacaoQuery;
import com.zetra.econsig.values.CodedValues;

public class RelatorioAnexosConsignacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute("DATA_PERIODO", DateHelper.toSQLDate(DateHelper.getSystemDate()));
        criterios.setAttribute("tipoPeriodo", CodedValues.TIPO_PERIODO_ALTERACAO_MAIOR);

        RelatorioAnexosConsignacaoQuery query = new RelatorioAnexosConsignacaoQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.setCriterios(criterios);

        executarConsulta(query);
    }
}

