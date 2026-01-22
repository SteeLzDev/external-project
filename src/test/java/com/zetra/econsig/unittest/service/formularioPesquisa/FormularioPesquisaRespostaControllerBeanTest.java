package com.zetra.econsig.unittest.service.formularioPesquisa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.FormularioPesquisaRespostaControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.entity.FormularioPesquisa;
import com.zetra.econsig.persistence.entity.FormularioPesquisaResposta;
import com.zetra.econsig.persistence.entity.FormularioPesquisaRespostaHome;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.query.formulariopesquisa.ListaFormularioPesquisaRespostaQuery;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaRespostaControllerBean;
import com.zetra.econsig.values.Columns;

@ExtendWith(MockitoExtension.class)
public class FormularioPesquisaRespostaControllerBeanTest {

    @InjectMocks
    private FormularioPesquisaRespostaControllerBean formularioPesquisaRespostaController;

    private final FormularioPesquisaResposta formularioPesquisaRespostaMock = new FormularioPesquisaResposta("1", "1",
            new Date(), "1", "{}", mock(FormularioPesquisa.class), mock(Usuario.class));

    @Nested
    @DisplayName("findByPrimaryKey")
    class FindByPrimaryKeyTests {

        @Test
        @DisplayName("Sucesso → retorna FormularioPesquisaResposta")
        void findByPrimaryKey_sucesso() throws FindException {
            try (MockedStatic<FormularioPesquisaRespostaHome> mocked = mockStatic(FormularioPesquisaRespostaHome.class)) {
                mocked.when(() -> FormularioPesquisaRespostaHome.findByPrimaryKey(formularioPesquisaRespostaMock.getFprCodigo()))
                        .thenReturn(formularioPesquisaRespostaMock);

                FormularioPesquisaResposta result = formularioPesquisaRespostaController
                        .findByPrimaryKey(formularioPesquisaRespostaMock.getFprCodigo());

                assertEquals(formularioPesquisaRespostaMock, result);
                mocked.verify(() -> FormularioPesquisaRespostaHome.findByPrimaryKey(formularioPesquisaRespostaMock.getFprCodigo()));
            }
        }

        @Test
        @DisplayName("Falha lógica → sem resultado throw find exception ")
        void findByPrimaryKey_semResultados() {
            try (MockedStatic<FormularioPesquisaRespostaHome> mocked = mockStatic(FormularioPesquisaRespostaHome.class)) {
                mocked.when(() -> FormularioPesquisaRespostaHome.findByPrimaryKey(formularioPesquisaRespostaMock.getFprCodigo()))
                        .thenThrow(FindException.class);

                assertThrows(FindException.class,
                    () -> formularioPesquisaRespostaController
                        .findByPrimaryKey(formularioPesquisaRespostaMock.getFprCodigo()),
                    "Should throw Exception");
            }
        }
    }

    @Nested
    @DisplayName("createFormularioPesquisaResposta")
    class CreateFormularioPesquisaRespostaTests {

        @Test
        @DisplayName("Sucesso → cria FormularioPesquisaResposta")
        void createFormularioPesquisaResposta_sucesso() throws CreateException {
            try (MockedStatic<FormularioPesquisaRespostaHome> mocked = mockStatic(FormularioPesquisaRespostaHome.class)) {
                mocked.when(() -> FormularioPesquisaRespostaHome.createFormularioPesquisaResposta(formularioPesquisaRespostaMock))
                        .thenReturn(formularioPesquisaRespostaMock);

                FormularioPesquisaResposta result = formularioPesquisaRespostaController
                        .createFormularioPesquisaResposta(formularioPesquisaRespostaMock);

                assertEquals(formularioPesquisaRespostaMock, result);
                mocked.verify(() -> FormularioPesquisaRespostaHome.createFormularioPesquisaResposta(formularioPesquisaRespostaMock));
            }
        }

        @Test
        @DisplayName("Falha lógica → throw create exception ")
        void createFormularioPesquisaResposta_throw_exception() {
            try (MockedStatic<FormularioPesquisaRespostaHome> mocked = mockStatic(FormularioPesquisaRespostaHome.class)) {
                mocked.when(() -> FormularioPesquisaRespostaHome.createFormularioPesquisaResposta(formularioPesquisaRespostaMock))
                        .thenThrow(CreateException.class);

                assertThrows(CreateException.class,
                    () -> formularioPesquisaRespostaController
                        .createFormularioPesquisaResposta(formularioPesquisaRespostaMock),
                    "Should throw Exception");
            }
        }
    }

    @Nested
    @DisplayName("countFormularioPesquisaRespostaByFpeCodigo")
    class CountFormularioPesquisaRespostaByFpeCodigoTests {

        @Test
        @DisplayName("Sucesso → count formulario pesquisa resposta")
        void countFormularioPesquisaRespostaByFpeCodigo_sucesso() throws Exception {
            TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.FPR_CODIGO, "1");
            to.setAttribute(Columns.FPR_USU_CODIGO, "1");
            to.setAttribute(Columns.FPR_DT_CRIACAO, DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()));
            List<TransferObject> expectedList = List.of(to);

            try (MockedConstruction<ListaFormularioPesquisaRespostaQuery> construction = Mockito.mockConstruction(
                    ListaFormularioPesquisaRespostaQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenReturn(expectedList))) {
                List<TransferObject> result = formularioPesquisaRespostaController.countFormularioPesquisaRespostaByFpeCodigo(formularioPesquisaRespostaMock.getFpeCodigo());
                assertEquals(expectedList, result);
            }
        }

        @Test
        @DisplayName("Falha lógica → Erro ao executar a query countFormularioPesquisaRespostaByFpeCodigo")
        void countFormularioPesquisaRespostaByFpeCodigo_throw_exception() throws Exception {
            try (MockedConstruction<ListaFormularioPesquisaRespostaQuery> construction = Mockito.mockConstruction(
                    ListaFormularioPesquisaRespostaQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenThrow(HQueryException.class))) {

                assertThrows(FormularioPesquisaRespostaControllerException.class,
                        () -> formularioPesquisaRespostaController.countFormularioPesquisaRespostaByFpeCodigo(formularioPesquisaRespostaMock.getFprCodigo()),
                        "Should throw Exception");
            }
        }
    }
}