package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioOcorrenciaAutorizacaoQuery;
import com.zetra.econsig.values.CodedValues;

public class RelatorioOcorrenciaAutorizacaoQuery2Test extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        RelatorioOcorrenciaAutorizacaoQuery query = new RelatorioOcorrenciaAutorizacaoQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.dataIni = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";
        query.orgCodigos = List.of("751F8080808080808080808080809780", "1");
        query.csaCodigo = "267";
        query.svcCodigo = List.of("050E8080808080808080808080808280", "1");
        query.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
        query.tocCodigos = CodedValues.TOC_CODIGOS_AUTORIZACAO;
        query.origemAdes = List.of(CodedValues.ORIGEM_ADE_NOVA);
        query.motivoTerminoAdes = List.of(CodedValues.TERMINO_ADE_CONCLUSAO);
        query.serCpf = "123";
        query.rseMatricula = "123";
        query.usuLogin = "123";
        query.cse = false;
        query.org = true;
        query.csa = true;
        query.cor = true;
        query.ser = true;
        query.sup = true;

        executarConsulta(query);
    }
}


