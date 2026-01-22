package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.beneficios.provedor.ListarProvedorBeneficioCsaEmAreaGeograficaQuery;

public class ListarProvedorBeneficioCsaEmAreaGeograficaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListarProvedorBeneficioCsaEmAreaGeograficaQuery query = new ListarProvedorBeneficioCsaEmAreaGeograficaQuery();
        query.setCriterios(criterios);

        query.orgCodigo = "751F8080808080808080808080809780";
        query.nseCodigos = java.util.List.of("1", "2");
        query.textoBusca = "123";
        query.latReferencia = 1.0f;
        query.longReferencia = 1.0f;
        query.raioAlcance = 1.0f;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

