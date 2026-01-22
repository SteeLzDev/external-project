package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioDescontoExpirarQuery;
import com.zetra.econsig.values.CodedValues;

public class RelatorioDescontoExpirarQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        RelatorioDescontoExpirarQuery query = new RelatorioDescontoExpirarQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.tipoEntidade = "ORG";
        query.adeAnoMesFim = "2023-01-01";
        query.orgCodigos = List.of("751F8080808080808080808080809780", "1");
        query.estCodigo = "751F8080808080808080808080809680";
        query.csaCodigo = "267";
        query.svcCodigo = List.of("050E8080808080808080808080808280", "1");
        query.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
        query.order = "CONSIGNATARIA";
        query.origemAdes = List.of(CodedValues.ORIGEM_ADE_NOVA);
        query.motivoTerminoAdes = List.of(CodedValues.TERMINO_ADE_CONCLUSAO);

        executarConsulta(query);
    }
}


