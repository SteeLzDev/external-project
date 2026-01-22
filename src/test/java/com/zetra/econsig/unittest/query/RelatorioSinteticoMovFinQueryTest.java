package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoMovFinQuery;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;
import com.zetra.econsig.values.CodedValues;

public class RelatorioSinteticoMovFinQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("DATA_INI", "2023-01-01 00:00:00");
        criterio.setAttribute("DATA_FIM", "2023-01-01 23:59:59");

        RelatorioSinteticoMovFinQuery query = new RelatorioSinteticoMovFinQuery();
        query.tipoEntidade = "ORG";
        query.periodo = "2023-01-01";
        query.orgCodigos = List.of("1", "2");
        query.csaCodigo = "267";
        query.corCodigos = List.of("1", "2");
        query.svcCodigos = List.of("1", "2");
        query.sadCodigo = CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO;
        query.spdCodigos = List.of(CodedValues.SPD_LIQUIDADAFOLHA, CodedValues.SPD_LIQUIDADAMANUAL);
        query.sboCodigo = "123";
        query.uniCodigo = "123";
        query.matricula = "123";
        query.cpf = "123";
        query.fields = new String[]{CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo(), CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo()};
        query.campos = List.of(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo(), CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo());
        query.camposOrdem = List.of(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo(), CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo());

        executarConsulta(query);
    }
}


