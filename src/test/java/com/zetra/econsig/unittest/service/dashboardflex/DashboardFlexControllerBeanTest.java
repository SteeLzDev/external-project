package com.zetra.econsig.unittest.service.dashboardflex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.DashboardFlex;
import com.zetra.econsig.persistence.entity.DashboardFlexConsulta;
import com.zetra.econsig.persistence.entity.DashboardFlexConsultaHome;
import com.zetra.econsig.persistence.entity.DashboardFlexHome;
import com.zetra.econsig.persistence.entity.DashboardFlexToolbar;
import com.zetra.econsig.persistence.entity.DashboardFlexToolbarHome;
import com.zetra.econsig.service.dashboardflex.DashboardFlexControllerBean;

public class DashboardFlexControllerBeanTest {

    private DashboardFlexControllerBean dashboardFlexController;
    private AcessoSistema responsavel;

    @BeforeEach
    void setUp() {
        dashboardFlexController = new DashboardFlexControllerBean();
        responsavel = AcessoSistema.getAcessoUsuarioSistema();
    }

    @Nested
    @DisplayName("listarDashboardFlex")
    class ListarDashboardFlexTests {

        @Test
        @DisplayName("Sucesso → retorna lista com itens")
        void sucesso() throws Exception {
            boolean somenteAtivos = true;
            String papCodigo = "7";
            List<String> funCodigos = List.of("486");

            DashboardFlex dashboardFlex = mock(DashboardFlex.class);
            List<DashboardFlex> esperado = List.of(dashboardFlex);

            try (MockedStatic<DashboardFlexHome> mocked = mockStatic(DashboardFlexHome.class)) {
                mocked.when(() -> DashboardFlexHome.listarDashboardFlex(somenteAtivos, papCodigo, funCodigos)).thenReturn(esperado);

                List<DashboardFlex> resultado = dashboardFlexController.listarDashboardFlex(somenteAtivos, papCodigo, funCodigos, responsavel);

                assertNotNull(resultado);
                assertEquals(1, resultado.size());
                assertSame(esperado, resultado);
                mocked.verify(() -> DashboardFlexHome.listarDashboardFlex(somenteAtivos, papCodigo, funCodigos));
            }
        }

        @Test
        @DisplayName("Falha lógica → sem resultados (lista vazia)")
        void semResultados() throws Exception {
            boolean somenteAtivos = false;
            String papCodigo = "7";
            List<String> funCodigos = List.of();

            try (MockedStatic<DashboardFlexHome> mocked = mockStatic(DashboardFlexHome.class)) {
                mocked.when(() -> DashboardFlexHome.listarDashboardFlex(somenteAtivos, papCodigo, funCodigos)).thenReturn(List.of());

                List<DashboardFlex> resultado = dashboardFlexController.listarDashboardFlex(somenteAtivos, papCodigo, funCodigos, responsavel);

                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
                mocked.verify(() -> DashboardFlexHome.listarDashboardFlex(somenteAtivos, papCodigo, funCodigos));
            }
        }
    }

    @Nested
    @DisplayName("listarDashboardFlexConsulta")
    class ListarDashboardFlexConsultaTests {

        @Test
        @DisplayName("Sucesso → retorna lista de consultas")
        void sucesso() throws Exception {
            List<String> dflCodigos = List.of("1");
            boolean somenteAtivos = true;

            DashboardFlexConsulta dashboardFlexConsulta = mock(DashboardFlexConsulta.class);

            try (MockedStatic<DashboardFlexConsultaHome> mocked = mockStatic(DashboardFlexConsultaHome.class)) {
                mocked.when(() -> DashboardFlexConsultaHome.listarDashboardFlexConsulta(dflCodigos, somenteAtivos)).thenReturn(List.of(dashboardFlexConsulta));

                List<DashboardFlexConsulta> resultado = dashboardFlexController.listarDashboardFlexConsulta(dflCodigos, somenteAtivos, responsavel);

                assertNotNull(resultado);
                assertEquals(1, resultado.size());
                mocked.verify(() -> DashboardFlexConsultaHome.listarDashboardFlexConsulta(dflCodigos, somenteAtivos));
            }
        }

        @Test
        @DisplayName("Falha lógica → lista vazia")
        void semResultados() throws Exception {
            List<String> dflCodigos = List.of("0");
            boolean somenteAtivos = false;

            try (MockedStatic<DashboardFlexConsultaHome> mocked = mockStatic(DashboardFlexConsultaHome.class)) {
                mocked.when(() -> DashboardFlexConsultaHome.listarDashboardFlexConsulta(dflCodigos, somenteAtivos)).thenReturn(List.of());

                List<DashboardFlexConsulta> resultado = dashboardFlexController.listarDashboardFlexConsulta(dflCodigos, somenteAtivos, responsavel);

                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
                mocked.verify(() -> DashboardFlexConsultaHome.listarDashboardFlexConsulta(dflCodigos, somenteAtivos));
            }
        }
    }

    @Nested
    @DisplayName("listarDashboardFlexToolbar")
    class ListarDashboardFlexToolbarTests {

        @Test
        @DisplayName("Sucesso → retorna toolbars")
        void sucesso() throws Exception {
            List<String> dfoCodigos = List.of("1");
            DashboardFlexToolbar dashboardFlexToolbar = mock(DashboardFlexToolbar.class);

            try (MockedStatic<DashboardFlexToolbarHome> mocked = mockStatic(DashboardFlexToolbarHome.class)) {
                mocked.when(() -> DashboardFlexToolbarHome.listarDashboardFlexTollbar(dfoCodigos)).thenReturn(List.of(dashboardFlexToolbar));

                List<DashboardFlexToolbar> resultado = dashboardFlexController.listarDashboardFlexToolbar(dfoCodigos, responsavel);

                assertNotNull(resultado);
                assertEquals(1, resultado.size());
                mocked.verify(() -> DashboardFlexToolbarHome.listarDashboardFlexTollbar(dfoCodigos));
            }
        }

        @Test
        @DisplayName("Falha lógica → lista vazia")
        void semResultados() throws Exception {
            List<String> dfoCodigos = List.of();

            try (MockedStatic<DashboardFlexToolbarHome> mocked = mockStatic(DashboardFlexToolbarHome.class)) {
                mocked.when(() -> DashboardFlexToolbarHome.listarDashboardFlexTollbar(dfoCodigos)).thenReturn(List.of());

                List<DashboardFlexToolbar> resultado = dashboardFlexController.listarDashboardFlexToolbar(dfoCodigos, responsavel);

                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
                mocked.verify(() -> DashboardFlexToolbarHome.listarDashboardFlexTollbar(dfoCodigos));
            }
        }
    }

    @Nested
    @DisplayName("getDashboardFlexConsulta")
    class GetDashboardFlexConsultaTests {

        @Test
        @DisplayName("Sucesso → retorna uma consulta")
        void sucesso() throws Exception {
            String dfoCodigo = "1";
            DashboardFlexConsulta dashboardFlexConsulta = mock(DashboardFlexConsulta.class);

            try (MockedStatic<DashboardFlexConsultaHome> mocked = mockStatic(DashboardFlexConsultaHome.class)) {
                mocked.when(() -> DashboardFlexConsultaHome.findByPrimaryKey(dfoCodigo)).thenReturn(dashboardFlexConsulta);

                DashboardFlexConsulta resultado = dashboardFlexController.getDashboardFlexConsulta(dfoCodigo, responsavel);

                assertNotNull(resultado);
                assertSame(dashboardFlexConsulta, resultado);
                mocked.verify(() -> DashboardFlexConsultaHome.findByPrimaryKey(dfoCodigo));
            }
        }

        @Test
        @DisplayName("Falha lógica → não encontrado (retorna null)")
        void naoEncontradoRetornaNull() throws Exception {
            String dfoCodigo = "0";

            try (MockedStatic<DashboardFlexConsultaHome> mocked = mockStatic(DashboardFlexConsultaHome.class)) {
                mocked.when(() -> DashboardFlexConsultaHome.findByPrimaryKey(dfoCodigo)).thenReturn(null);

                DashboardFlexConsulta resultado = dashboardFlexController.getDashboardFlexConsulta(dfoCodigo, responsavel);

                assertNull(resultado);
                mocked.verify(() -> DashboardFlexConsultaHome.findByPrimaryKey(dfoCodigo));
            }
        }
    }
}