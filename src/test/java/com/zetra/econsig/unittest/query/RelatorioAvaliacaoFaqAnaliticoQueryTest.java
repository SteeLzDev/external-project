package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioAvaliacaoFaqAnaliticoQuery;

public class RelatorioAvaliacaoFaqAnaliticoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute("periodoIni", "2023-01-01");
        criterios.setAttribute("periodoFim", "2023-01-01");
        criterios.setAttribute("faqCodigo", "123");
        criterios.setAttribute("avaliacaoFaq", "0");
        criterios.setAttribute("cse", Boolean.TRUE);
        criterios.setAttribute("org", Boolean.TRUE);
        criterios.setAttribute("csa", Boolean.TRUE);
        criterios.setAttribute("cor", Boolean.TRUE);
        criterios.setAttribute("ser", Boolean.TRUE);


        RelatorioAvaliacaoFaqAnaliticoQuery query = new RelatorioAvaliacaoFaqAnaliticoQuery();
        query.setCriterios(criterios);
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }

}
