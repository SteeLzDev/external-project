package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ListarLinhasBlocosProcessamentoQuery;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;
import com.zetra.econsig.values.TipoBlocoProcessamentoEnum;

public class ListarLinhasBlocosProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListarLinhasBlocosProcessamentoQuery query = new ListarLinhasBlocosProcessamentoQuery();
        query.tipoEntidade = "CSE";
        query.codigoEntidade = "1";
        query.csaCodigo = "267";
        query.tbpCodigos = java.util.List.of(TipoBlocoProcessamentoEnum.RETORNO.getCodigo(), TipoBlocoProcessamentoEnum.RETORNO_ATRASADO.getCodigo());
        query.sbpCodigos = java.util.List.of(StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO.getCodigo(), StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo());

        executarConsulta(query);
    }

    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {
        ListarLinhasBlocosProcessamentoQuery query = new ListarLinhasBlocosProcessamentoQuery();
        query.tipoEntidade = "EST";
        query.codigoEntidade = "001";
        query.csaCodigo = "267";
        query.tbpCodigos = java.util.List.of(TipoBlocoProcessamentoEnum.RETORNO.getCodigo(), TipoBlocoProcessamentoEnum.RETORNO_ATRASADO.getCodigo());
        query.sbpCodigos = java.util.List.of(StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO.getCodigo(), StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo());

        executarConsulta(query);
    }

    @Test
    public void test_03() throws com.zetra.econsig.exception.ZetraException {
        ListarLinhasBlocosProcessamentoQuery query = new ListarLinhasBlocosProcessamentoQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "0001";
        query.csaCodigo = "267";
        query.tbpCodigos = java.util.List.of(TipoBlocoProcessamentoEnum.RETORNO.getCodigo(), TipoBlocoProcessamentoEnum.RETORNO_ATRASADO.getCodigo());
        query.sbpCodigos = java.util.List.of(StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO.getCodigo(), StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo());

        executarConsulta(query);
    }
}

