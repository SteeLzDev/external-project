package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListaAdesPorPeriodoParcelasQuery;
import com.zetra.econsig.values.CodedValues;

public class ListaAdesPorPeriodoParcelasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaAdesPorPeriodoParcelasQuery query = new ListaAdesPorPeriodoParcelasQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.tipoEntidade = "EST";
        query.dataIni = "2023-01-01";
        query.dataFim = "2023-01-01";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.orgCodigos = List.of("751F8080808080808080808080809780", "1");
        query.estCodigo = "751F8080808080808080808080809680";
        query.csaCodigo = "267";
        query.corCodigos = null;
        query.sboCodigo = "123";
        query.uniCodigo = "123";
        query.svcCodigo = List.of("050E8080808080808080808080808280", "1");
        query.sadCodigos = CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO;
        query.origemAdes = List.of(CodedValues.ORIGEM_ADE_NOVA, CodedValues.ORIGEM_ADE_RENEGOCIADA);
        query.motivoTerminoAdes = List.of(CodedValues.TERMINO_ADE_CONCLUSAO, CodedValues.TERMINO_ADE_LIQ_ANTECIPADA);
        query.srsCodigos = CodedValues.SRS_ATIVOS;
        query.nseCodigos = List.of(CodedValues.NSE_EMPRESTIMO);
        query.tmoDecisaoJudicial = false;
        query.parcelaDescontoPeriodo = false;

        executarConsulta(query);
    }
}


