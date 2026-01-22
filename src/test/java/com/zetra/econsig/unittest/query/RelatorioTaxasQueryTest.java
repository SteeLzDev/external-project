package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.relatorio.RelatorioTaxasQuery;

public class RelatorioTaxasQueryTest extends AbstractQueryTest {

    @Disabled("Usa um getReportTemplate() que é nulo")
    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("ordenacao", "CSA");
        criterio.setAttribute("svcCodigo", "050E8080808080808080808080808280");
        criterio.setAttribute("dataTaxa", DateHelper.getSystemDatetime());
        criterio.setAttribute("prazoInicial", "1");
        criterio.setAttribute("prazoFinal", "12");
        criterio.setAttribute("prazoMultiploDoze", Boolean.FALSE);
        criterio.setAttribute("prazosInformados", "1,2,3,4,5,6");
        criterio.setAttribute("CSA_ATIVO", new String[]{"1"});

        RelatorioTaxasQuery query = new RelatorioTaxasQuery(1);
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}


