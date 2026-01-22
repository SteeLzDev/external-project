package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.coeficiente.ListaTaxasJurosQuery;
import com.zetra.econsig.values.Columns;

public class ListaTaxasJurosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        String svcCodigo = "050E8080808080808080808080808280";
        String csaCodigo = "267";

        TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute("periodo", "2023-01-01");
        criterios.setAttribute(Columns.SVC_CODIGO, svcCodigo);
        criterios.setAttribute(Columns.CSA_CODIGO, csaCodigo);
        criterios.setAttribute(AcessoSistema.SESSION_ATTR_NAME, responsavel);

        ListaTaxasJurosQuery query = new ListaTaxasJurosQuery();
        query.setCriterios(criterios);
        executarConsulta(query);
    }
}
