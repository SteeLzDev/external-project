package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarConsignatariaByNaturezaServicoQuery;
import com.zetra.econsig.values.TipoFiltroPesquisaFluxoEnum;

public class ListarConsignatariaByNaturezaServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarConsignatariaByNaturezaServicoQuery query = new ListarConsignatariaByNaturezaServicoQuery();
        query.nseCodigo = "123";
        query.tipoFiltro = TipoFiltroPesquisaFluxoEnum.FILTRO_CIDADE;
        query.filtro = "123";
        query.orgCodigo = "751F8080808080808080808080809780";

        executarConsulta(query);
    }
}

