package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioLeituraMensagemQuery;

public class RelatorioLeituraMensagemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute("ENTIDADE", AcessoSistema.ENTIDADE_CSE);

        RelatorioLeituraMensagemQuery query = new RelatorioLeituraMensagemQuery();
        query.setCriterios(criterios);

        executarConsulta(query);
    }
}

