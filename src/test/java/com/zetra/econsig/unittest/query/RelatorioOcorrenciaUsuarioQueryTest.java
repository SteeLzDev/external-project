package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioOcorrenciaUsuarioQuery;

public class RelatorioOcorrenciaUsuarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute("tipoEntidade", AcessoSistema.ENTIDADE_CSE);
        criterios.setAttribute("DATA_INI", "2023-01-01 00:00:00");
        criterios.setAttribute("DATA_FIM", "2023-01-01 23:59:59");

        RelatorioOcorrenciaUsuarioQuery query = new RelatorioOcorrenciaUsuarioQuery();
        query.setCriterios(criterios);

        executarConsulta(query);
    }
}

