package com.zetra.econsig.unittest.query;

import java.text.ParseException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.relatorio.RelatorioComprometimentoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class RelatorioComprometimentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException, ParseException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.EST_CODIGO, "751F8080808080808080808080809680");
        criterio.setAttribute(Columns.ORG_CODIGO, List.of("751F8080808080808080808080809780"));
        criterio.setAttribute(CodedValues.TPS_INCIDE_MARGEM, CodedValues.INCIDE_MARGEM_SIM);

        criterio.setAttribute("SINAL_MARGEM", List.of("1", "0"));
        criterio.setAttribute("COMPROMETIMENTO_MARGEM", List.of(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_0_A_10, CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_10_A_20));
        criterio.setAttribute("PERCENTUAL_VARIACAO_MARGEM_INICIO", "5");
        criterio.setAttribute("PERCENTUAL_VARIACAO_MARGEM_FIM", "10");
        criterio.setAttribute("PENULTIMO_PERIODO", DateHelper.parse("2022-01-01", "yyyy-MM-dd"));


        RelatorioComprometimentoQuery query = new RelatorioComprometimentoQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}