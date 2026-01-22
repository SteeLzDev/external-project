package com.zetra.econsig.unittest.service.consignacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.persistence.query.consignacao.ListaTotalConsignacaoAtivasPorOrgaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoControllerBean;
import com.zetra.econsig.web.ApplicationContextProvider;

public class PesquisarConsignacaoControllerBeanTest {

    @InjectMocks
    private PesquisarConsignacaoControllerBean controller;

    private AcessoSistema responsavel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        responsavel = AcessoSistema.getAcessoUsuarioSistema();
    }

    @Nested
    @DisplayName("listaTotalConsignacaoAtivasPorOrgao")
    class ListaTotalConsignacaoAtivasPorOrgaoTests {

        @Test
        @DisplayName("Sucesso -> retorna lista de consignações ativas por órgão")
        void retornarListaQuandoSucesso() throws Exception {
            List<TransferObject> mockedResultado = new ArrayList<>();
            mockedResultado.add(new CustomTransferObject());

            try (MockedConstruction<ListaTotalConsignacaoAtivasPorOrgaoQuery> construction = Mockito.mockConstruction(
                    ListaTotalConsignacaoAtivasPorOrgaoQuery.class,
                    (mock, context) -> when(mock.executarDTO()).thenReturn(mockedResultado))) {

                List<TransferObject> resultado = controller.listaTotalConsignacaoAtivasPorOrgao(responsavel);

                assertNotNull(resultado);
                assertEquals(1, resultado.size());
                assertEquals(mockedResultado, resultado);
            }
        }

        @Test
        @DisplayName("Falha -> lança AutorizacaoControllerException ao falhar na query")
        void lancarAutorizacaoControllerExceptionQuandoHQueryFalhar() throws Exception {
            try (MockedStatic<ApplicationContextProvider> appCtxStatic = Mockito.mockStatic(ApplicationContextProvider.class);
                 MockedStatic<ApplicationResourcesHelper> appResStatic = Mockito.mockStatic(ApplicationResourcesHelper.class);
                 MockedConstruction<ListaTotalConsignacaoAtivasPorOrgaoQuery> construction = Mockito.mockConstruction(
                         ListaTotalConsignacaoAtivasPorOrgaoQuery.class,
                         (mock, context) -> when(mock.executarDTO()).thenThrow(new HQueryException("erro", responsavel)))) {

                assertThrows(AutorizacaoControllerException.class, () -> controller.listaTotalConsignacaoAtivasPorOrgao(responsavel));
            }
        }
    }

    @Nested
    @DisplayName("listaSolicitacaoSaldoDevedorPorRegistroServidor")
    class ListaSolicitacaoSaldoDevedorPorRegistroServidorTests {

        @Test
        @DisplayName("Sucesso -> retorna solicitações de saldo devedor por RSE")
        void retornarListaQuandoSucesso() throws Exception {
            List<TransferObject> mockedResultado = new ArrayList<>();
            mockedResultado.add(new CustomTransferObject());

            try (MockedConstruction<ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery> construction = Mockito.mockConstruction(
                    ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery.class,
                    (mock, context) -> when(mock.executarDTO()).thenReturn(mockedResultado))) {

                List<TransferObject> resultado = controller.listaSolicitacaoSaldoDevedorPorRegistroServidor("RSE123", responsavel);

                assertNotNull(resultado);
                assertEquals(1, resultado.size());
                assertEquals(mockedResultado, resultado);
            }
        }

        @Test
        @DisplayName("Falha -> lança AutorizacaoControllerException ao falhar na query de saldo devedor")
        void lancarAutorizacaoControllerExceptionQuandoHQueryFalhar() throws Exception {
            try (MockedStatic<ApplicationContextProvider> appCtxStatic = Mockito.mockStatic(ApplicationContextProvider.class);
                 MockedStatic<ApplicationResourcesHelper> appResStatic = Mockito.mockStatic(ApplicationResourcesHelper.class);
                 MockedConstruction<ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery> construction = Mockito.mockConstruction(
                         ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery.class,
                         (mock, context) -> when(mock.executarDTO()).thenThrow(new HQueryException("erro", responsavel)))) {

                assertThrows(AutorizacaoControllerException.class, () -> controller.listaSolicitacaoSaldoDevedorPorRegistroServidor("RSE123", responsavel));
            }
        }
    }
}
