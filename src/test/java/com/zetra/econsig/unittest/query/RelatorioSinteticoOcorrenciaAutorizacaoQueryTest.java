package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoOcorrenciaAutorizacaoQuery;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;
import com.zetra.econsig.values.CodedValues;

public class RelatorioSinteticoOcorrenciaAutorizacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("DATA_INI", "2023-01-01 00:00:00");
        criterio.setAttribute("DATA_FIM", "2023-01-01 23:59:59");

        RelatorioSinteticoOcorrenciaAutorizacaoQuery query = new RelatorioSinteticoOcorrenciaAutorizacaoQuery();
        query.setCriterios(criterio);
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.campos = List.of(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo(), CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo());
        query.sadCodigo = CodedValues.SAD_CODIGOS_ATIVOS;
        query.svcCodigo = List.of("050E8080808080808080808080808280", "1");
        query.fields = new String[]{CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo(), CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo()};
        query.order = CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo();
        query.echCodigo = "123";
        query.plaCodigo = "123";
        query.cnvCodVerba = "123";
        query.sboCodigo = "123";
        query.uniCodigo = "123";
        query.nseCodigos = List.of(CodedValues.NSE_EMPRESTIMO, CodedValues.NSE_CARTAO);

        executarConsulta(query);
    }
}


