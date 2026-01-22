package com.zetra.econsig.unittest.service.formularioPesquisa;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.FormularioPesquisaTO;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.FormularioPesquisaControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.FormularioPesquisa;
import com.zetra.econsig.persistence.entity.FormularioPesquisaHome;
import com.zetra.econsig.persistence.query.formulariopesquisa.BuscaFormularioPesquisaSemRespostaQuery;
import com.zetra.econsig.persistence.query.formulariopesquisa.ListaFormularioPesquisaQuery;
import com.zetra.econsig.persistence.query.formulariopesquisa.ListaFormularioPesquisaRespostaDashQuery;
import com.zetra.econsig.persistence.query.formulariopesquisa.VerificaFormularioRespondidoQuery;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaControllerBean;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaRespostaControllerBean;
import com.zetra.econsig.values.Columns;

@ExtendWith(MockitoExtension.class)
public class FormularioPesquisaControllerBeanTest {

    @InjectMocks
    private FormularioPesquisaControllerBean formularioPesquisaController;

    @Mock
    private FormularioPesquisaRespostaControllerBean formularioPesquisaRespostaController;

    private final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

    private final FormularioPesquisa formularioPesquisaMock = new FormularioPesquisa("1", "Mock", true, null, null,
            true,
            "{}");
    private final FormularioPesquisa formularioPesquisaWithDtMock = new FormularioPesquisa("1", "Mock", true, new Date(), null, true,
                    "{}");

    @Nested
    @DisplayName("findByPrimaryKey")
    class FindByPrimaryKeyTests {

        @Test
        @DisplayName("Sucesso → retorna FormularioPesquisaTO")
        void findByPrimaryKey_sucesso() throws FindException {
            FormularioPesquisaTO expected = new FormularioPesquisaTO(formularioPesquisaMock);
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaMock);

                FormularioPesquisaTO result = formularioPesquisaController
                        .findByPrimaryKey(formularioPesquisaMock.getFpeCodigo());

                assertEquals(expected, result);
                mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
            }
        }

        @Test
        @DisplayName("Falha lógica → sem resultado throw find exception ")
        void findByPrimaryKey_semResultados() {
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(null)).thenThrow(FindException.class);
                try {
                    formularioPesquisaController.findByPrimaryKey(null);
                    fail();
                } catch (FindException ex) {
                    mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(null));
                }
            }
        }
    }

    @Nested
    @DisplayName("createFormularioPesquisa")
    class CreateFormularioPesquisaTests {

        @Test
        @DisplayName("Sucesso → retorna FormularioPesquisaTO")
        void createFormularioPesquisa_sucesso() throws CreateException {
            FormularioPesquisaTO expectedTO = new FormularioPesquisaTO(formularioPesquisaWithDtMock);
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.createFormularioPesquisa(formularioPesquisaMock))
                        .thenReturn(formularioPesquisaWithDtMock);

                FormularioPesquisaTO result = formularioPesquisaController
                        .createFormularioPesquisa(formularioPesquisaMock);

                assertEquals(expectedTO, result);
                mocked.verify(() -> FormularioPesquisaHome.createFormularioPesquisa(formularioPesquisaMock));
            }
        }

        @Test
        @DisplayName("Falha lógica → throw create exception ")
        void createFormularioPesquisa_throw_create_exception() {
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.createFormularioPesquisa(any()))
                        .thenThrow(CreateException.class);

                try {
                    formularioPesquisaController.createFormularioPesquisa(formularioPesquisaMock);
                    fail();
                } catch (CreateException ex) {
                    mocked.verify(() -> FormularioPesquisaHome.createFormularioPesquisa(any()));
                }
            }
        }
    }

    @Nested
    @DisplayName("updateFormularioPesquisa")
    class UpdateFormularioPesquisaTests {

        @Test
        @DisplayName("Sucesso → Update Formulario Pesquisa ")
        void updateFormularioPesquisa_sucesso_publicado_sem_repostas() throws Exception {
            when(formularioPesquisaRespostaController
                    .countFormularioPesquisaRespostaByFpeCodigo(formularioPesquisaMock.getFpeCodigo()))
                    .thenReturn(new ArrayList<TransferObject>());

            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaWithDtMock);

                formularioPesquisaController.updateFormularioPesquisa(formularioPesquisaMock, responsavel);

                mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                mocked.verify(() -> FormularioPesquisaHome.updateFormularioPesquisa(formularioPesquisaMock));
            }
        }

        @Test
        @DisplayName("Sucesso → Update Formulario Pesquisa não publicado")
        void updateFormularioPesquisa_sucesso_nao_publicado() throws Exception {
            FormularioPesquisa mockWithDate = new FormularioPesquisa("1", "Mock", true, new Date(), null, false,
                    "{}");

            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(mockWithDate);

                formularioPesquisaController.updateFormularioPesquisa(formularioPesquisaMock, responsavel);

                mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                mocked.verify(() -> FormularioPesquisaHome.updateFormularioPesquisa(formularioPesquisaMock));
            }
        }

        @Test
        @DisplayName("Falha lógica → Update Formulario Pesquisa com resposta")
        void updateFormularioPesquisa_publicado_com_repostas_throw_exception() throws Exception {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute("total", Long.valueOf(1));
            List<TransferObject> listTO = new ArrayList<>();
            listTO.add(cto);

            when(formularioPesquisaRespostaController
                    .countFormularioPesquisaRespostaByFpeCodigo(formularioPesquisaMock.getFpeCodigo()))
                    .thenReturn(listTO);

            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaWithDtMock);

                try {
                    formularioPesquisaController.updateFormularioPesquisa(formularioPesquisaMock, responsavel);
                    fail();
                } catch (FormularioPesquisaControllerException ex) {
                    mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                }
            }
        }

        @Test
        @DisplayName("Sucesso → Update Formulario Pesquisa com total de respostas 0")
        void updateFormularioPesquisa_sucesso_publicado_total_repostas_zero() throws Exception {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute("total", Long.valueOf(0));
            List<TransferObject> listTO = new ArrayList<>();
            listTO.add(cto);

            when(formularioPesquisaRespostaController
                    .countFormularioPesquisaRespostaByFpeCodigo(formularioPesquisaMock.getFpeCodigo()))
                    .thenReturn(listTO);

            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaWithDtMock);

                formularioPesquisaController.updateFormularioPesquisa(formularioPesquisaMock, responsavel);

                mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                mocked.verify(() -> FormularioPesquisaHome.updateFormularioPesquisa(formularioPesquisaMock));
            }
        }
    }

    @Nested
    @DisplayName("deleteFormularioPesquisa")
    class DeleteFormularioPesquisaTests {

        @Test
        @DisplayName("Sucesso → Delete Formulario Pesquisa publicado sem respostas")
        void deleteFormularioPesquisa_sucesso_publicado_sem_repostas() throws Exception {
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaWithDtMock);

                formularioPesquisaController.deleteFormularioPesquisa(formularioPesquisaMock.getFpeCodigo(),
                        responsavel);

                mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                mocked.verify(() -> FormularioPesquisaHome.removeFormularioPesquisa(formularioPesquisaWithDtMock));
            }
        }

        @Test
        @DisplayName("Sucesso → Delete Formulario Pesquisa não publicado")
        void deleteFormularioPesquisa_sucesso_nao_publicado() throws Exception {
            FormularioPesquisa mockWithDate = new FormularioPesquisa("1", "Mock", true, new Date(), null, false,
                    "{}");
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(mockWithDate);

                formularioPesquisaController.deleteFormularioPesquisa(formularioPesquisaMock.getFpeCodigo(),
                        responsavel);

                mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                mocked.verify(() -> FormularioPesquisaHome.removeFormularioPesquisa(mockWithDate));
            }
        }

        @Test
        @DisplayName("Sucesso → Delete Formulario Pesquisa publicado com total de respostas 0")
        void deleteFormularioPesquisa_sucesso_publicado_zero_respostas() throws Exception {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute("total", Long.valueOf(0));
            List<TransferObject> listTO = new ArrayList<>();
            listTO.add(cto);

            when(formularioPesquisaRespostaController
                    .countFormularioPesquisaRespostaByFpeCodigo(formularioPesquisaMock.getFpeCodigo()))
                    .thenReturn(listTO);

            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaWithDtMock);

                formularioPesquisaController.deleteFormularioPesquisa(formularioPesquisaMock.getFpeCodigo(),
                        responsavel);

                mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                mocked.verify(() -> FormularioPesquisaHome.removeFormularioPesquisa(formularioPesquisaWithDtMock));
            }
        }

        @Test
        @DisplayName("Falha lógica → Delete Formulario Pesquisa publicado com respostas")
        void deleteFormularioPesquisa_publicado_com_respostas_throw_exeception() throws Exception {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute("total", Long.valueOf(1));
            List<TransferObject> listTO = new ArrayList<>();
            listTO.add(cto);

            when(formularioPesquisaRespostaController
                    .countFormularioPesquisaRespostaByFpeCodigo(formularioPesquisaMock.getFpeCodigo()))
                    .thenReturn(listTO);

            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaWithDtMock);

                try {
                    formularioPesquisaController.deleteFormularioPesquisa(formularioPesquisaMock.getFpeCodigo(),
                            responsavel);
                    fail();
                } catch (FormularioPesquisaControllerException ex) {
                    mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                }
            }
        }

        @Test
        @DisplayName("Falha Lógica → Delete Formulario Pesquisa nao existente")
        void deleteFormularioPesquisa_nao_encontrado_throw_exception() throws Exception {
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenThrow(FindException.class);

                try {
                    formularioPesquisaController.deleteFormularioPesquisa(formularioPesquisaMock.getFpeCodigo(),
                            responsavel);
                    fail();
                } catch (FormularioPesquisaControllerException ex) {
                    mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                }
            }
        }

        @Test
        @DisplayName("Falha lógica → Delete Formulario Pesquisa erro ao remover")
        void deleteFormularioPesquisa_erro_remover_throw_exeception() throws Exception {
            FormularioPesquisa mockWithDate = new FormularioPesquisa("1", "Mock", true, new Date(), null, false,
                    "{}");

            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(mockWithDate.getFpeCodigo()))
                        .thenReturn(mockWithDate);
                mocked.when(() -> FormularioPesquisaHome.removeFormularioPesquisa(mockWithDate))
                        .thenThrow(RemoveException.class);
                try {
                    formularioPesquisaController.deleteFormularioPesquisa(mockWithDate.getFpeCodigo(), responsavel);
                    fail();
                } catch (FormularioPesquisaControllerException ex) {
                    mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(mockWithDate.getFpeCodigo()));
                    mocked.verify(() -> FormularioPesquisaHome.removeFormularioPesquisa(mockWithDate));
                }
            }
        }
    }

    @Nested
    @DisplayName("publicarFormularioPesquisa")
    class PublicarFormularioPesquisaTests {

        @Test
        @DisplayName("Sucesso → Publicar Formulario Pesquisa")
        void publicarFormularioPesquisa_sucesso() throws Exception {
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaMock);

                formularioPesquisaController.publicar(formularioPesquisaMock.getFpeCodigo());

                mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                mocked.verify(() -> FormularioPesquisaHome.updateFormularioPesquisa(formularioPesquisaMock));
            }
        }

        @Test
        @DisplayName("Falha lógica → Publicar Formulario Pesquisa não existente")
        void publicarFormularioPesquisa_nao_existente() throws Exception {
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenThrow(FindException.class);

                try {
                    formularioPesquisaController.publicar(formularioPesquisaMock.getFpeCodigo());
                    fail();
                } catch (FindException ex) {
                    mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                }

            }
        }

        @Test
        @DisplayName("Falha lógica → Erro ao Publicar Formulario Pesquisa")
        void publicarFormularioPesquisa_throw_exception() throws Exception {
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaMock);
                mocked.when(() -> FormularioPesquisaHome.updateFormularioPesquisa(any())).thenThrow(
                        UpdateException.class);

                try {
                    formularioPesquisaController.publicar(formularioPesquisaMock.getFpeCodigo());
                    fail();
                } catch (UpdateException ex) {
                    mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                    mocked.verify(() -> FormularioPesquisaHome.updateFormularioPesquisa(any()));
                }

            }
        }
    }

    @Nested
    @DisplayName("despublicarFormularioPesquisa")
    class DespublicarFormularioPesquisaTests {

        @Test
        @DisplayName("Sucesso → Despublicar Formulario Pesquisa sem respostas")
        void despublicarFormularioPesquisa_sem_respostas_sucesso() throws Exception {
            FormularioPesquisa expected = new FormularioPesquisa("1", "Mock", true, null, null, false,
                    "{}");

            when(formularioPesquisaRespostaController
                    .countFormularioPesquisaRespostaByFpeCodigo(formularioPesquisaMock.getFpeCodigo()))
                    .thenReturn(new ArrayList<TransferObject>());
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaMock);

                formularioPesquisaController.despublicar(formularioPesquisaMock.getFpeCodigo(), responsavel);

                mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                mocked.verify(() -> FormularioPesquisaHome.updateFormularioPesquisa(expected));
            }
        }

        @Test
        @DisplayName("Falha logica → Despublicar Formulario Pesquisa com respostas")
        void despublicarFormularioPesquisa_com_repostas() throws Exception {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute("total", Long.valueOf(1));
            List<TransferObject> listTO = new ArrayList<>();
            listTO.add(cto);
            
            when(formularioPesquisaRespostaController
                    .countFormularioPesquisaRespostaByFpeCodigo(formularioPesquisaMock.getFpeCodigo()))
                    .thenReturn(listTO);
            try (MockedStatic<FormularioPesquisaHome> mocked = mockStatic(FormularioPesquisaHome.class)) {
                mocked.when(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()))
                        .thenReturn(formularioPesquisaMock);

                try {
                    formularioPesquisaController.despublicar(formularioPesquisaMock.getFpeCodigo(), responsavel);
                    fail();
                } catch (FormularioPesquisaControllerException ex) {
                    mocked.verify(() -> FormularioPesquisaHome.findByPrimaryKey(formularioPesquisaMock.getFpeCodigo()));
                }
            }
        }
    }

    @Nested
    @DisplayName("listFormularioPesquisa")
    class ListFormularioPesquisaTests {

        @Test
        @DisplayName("Sucesso → Listar formulario pesquisa")
        void listFormularioPesquisa_sucesso() throws Exception {
            TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.FPE_CODIGO, "1");
            to.setAttribute(Columns.FPE_NOME, "Mock");
            to.setAttribute(Columns.FPE_BLOQUEIA_SISTEMA, "1");
            to.setAttribute(Columns.FPE_DT_CRIACAO, new Date().toString());
            to.setAttribute(Columns.FPE_DT_FIM, null);
            to.setAttribute(Columns.FPE_PUBLICADO, "1");
            List<TransferObject> expectedList = List.of(to);

            try (MockedConstruction<ListaFormularioPesquisaQuery> construction = Mockito.mockConstruction(
                    ListaFormularioPesquisaQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenReturn(expectedList))) {
                List<TransferObject> result = formularioPesquisaController.listFormularioPesquisa(null, -1, -1, responsavel);
                assertEquals(expectedList, result);
            }
        }

        @Test
        @DisplayName("Sucesso → Listar formulario pesquisa com criterio (nome e publicado), offset, count")
        void listFormularioPesquisa_sucesso_com_param() throws Exception {
            TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.FPE_CODIGO, "1");
            to.setAttribute(Columns.FPE_NOME, "Mock");
            to.setAttribute(Columns.FPE_BLOQUEIA_SISTEMA, "1");
            to.setAttribute(Columns.FPE_DT_CRIACAO, new Date().toString());
            to.setAttribute(Columns.FPE_DT_FIM, null);
            to.setAttribute(Columns.FPE_PUBLICADO, "1");
            List<TransferObject> expectedList = List.of(to);

            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.FPE_NOME, "Mock");
            criterio.setAttribute(Columns.FPE_PUBLICADO, true);

            try (MockedConstruction<ListaFormularioPesquisaQuery> construction = Mockito.mockConstruction(
                    ListaFormularioPesquisaQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenReturn(expectedList))) {
                List<TransferObject> result = formularioPesquisaController.listFormularioPesquisa(criterio, 1, 1, responsavel);
                assertEquals(expectedList, result);
            }
        }

        @Test
        @DisplayName("Sucesso → Listar formulario pesquisa com criterio (nome), offset, count")
        void listFormularioPesquisa_sucesso_com_param_nome() throws Exception {
            TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.FPE_CODIGO, "1");
            to.setAttribute(Columns.FPE_NOME, "Mock");
            to.setAttribute(Columns.FPE_BLOQUEIA_SISTEMA, "1");
            to.setAttribute(Columns.FPE_DT_CRIACAO, new Date().toString());
            to.setAttribute(Columns.FPE_DT_FIM, null);
            to.setAttribute(Columns.FPE_PUBLICADO, "1");
            List<TransferObject> expectedList = List.of(to);

            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.FPE_NOME, "Mock");

            try (MockedConstruction<ListaFormularioPesquisaQuery> construction = Mockito.mockConstruction(
                    ListaFormularioPesquisaQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenReturn(expectedList))) {
                List<TransferObject> result = formularioPesquisaController.listFormularioPesquisa(criterio, 1, 1, responsavel);
                assertEquals(expectedList, result);
            }
        }

        @Test
        @DisplayName("Falha lógica → Erro ao executar a query Listar formulario")
        void listFormularioPesquisa_throw_exception() throws Exception {
            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.FPE_NOME, "Mock");
            criterio.setAttribute(Columns.FPE_PUBLICADO, true);

            try (MockedConstruction<ListaFormularioPesquisaQuery> construction = Mockito.mockConstruction(
                    ListaFormularioPesquisaQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenThrow(HQueryException.class))) {

                assertThrows(FormularioPesquisaControllerException.class,
                        () -> formularioPesquisaController.listFormularioPesquisa(criterio, 1, 1, responsavel),
                        "Should throw Exception");
            }
        }
    }

    @Nested
    @DisplayName("findFormularioPesquisaMaisAntigoSemResposta")
    class FindFormularioPesquisaMaisAntigoSemRespostaTests {

        @Test
        @DisplayName("Sucesso → Find Formulario Pesquisa Mais Antigo Sem Resposta")
        void findFormularioPesquisaMaisAntigoSemResposta_sucesso() throws Exception {
            TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.FPE_CODIGO, "1");
            List<TransferObject> mockList = List.of(to);

            try (MockedConstruction<BuscaFormularioPesquisaSemRespostaQuery> construction = Mockito.mockConstruction(
                    BuscaFormularioPesquisaSemRespostaQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenReturn(mockList))) {
                TransferObject result = formularioPesquisaController.findFormularioPesquisaMaisAntigoSemResposta("1", responsavel);
                assertEquals(to.getAttribute(Columns.FPE_CODIGO), result.getAttribute(Columns.FPE_CODIGO));
            }
        }

        @Test
        @DisplayName("Sucesso → Find Formulario Pesquisa Mais Antigo Sem Resposta não encontrado")
        void findFormularioPesquisaMaisAntigoSemResposta_nao_encontrado() throws Exception {
            try (MockedConstruction<BuscaFormularioPesquisaSemRespostaQuery> construction = Mockito.mockConstruction(
                    BuscaFormularioPesquisaSemRespostaQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenReturn(Collections.emptyList()))) {
                TransferObject result = formularioPesquisaController.findFormularioPesquisaMaisAntigoSemResposta("1", responsavel);
                assertEquals(null, result);
            }
        }

        @Test
        @DisplayName("Falha lógica → Throw exception Find Formulario Pesquisa Mais Antigo Sem Resposta")
        void findFormularioPesquisaMaisAntigoSemResposta_throw_exception() throws Exception {
            try (MockedConstruction<BuscaFormularioPesquisaSemRespostaQuery> construction = Mockito.mockConstruction(
                    BuscaFormularioPesquisaSemRespostaQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenThrow(HQueryException.class))) {
                assertThrows(FormularioPesquisaControllerException.class,
                        () -> formularioPesquisaController.findFormularioPesquisaMaisAntigoSemResposta("1", responsavel),
                        "Should throw Exception");
            }
        }
    }

    @Nested
    @DisplayName("verificaFormularioParaResponder")
    class VerificaFormularioParaResponderTests {

        @Test
        @DisplayName("Sucesso → Verifica Formulario Para Responder")
        void verificaFormularioParaResponderTests_sucesso() throws Exception {
            TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.FPR_CODIGO, null);
            to.setAttribute(Columns.FPE_CODIGO, "1");
            to.setAttribute(Columns.FPE_PUBLICADO, true);
            List<TransferObject> mockList = List.of(to);

            try (MockedConstruction<VerificaFormularioRespondidoQuery> construction = Mockito.mockConstruction(
                    VerificaFormularioRespondidoQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenReturn(mockList))) {
                String result = formularioPesquisaController.verificaFormularioParaResponder("1", responsavel);
                assertEquals(to.getAttribute(Columns.FPE_CODIGO), result);
            }
        }

        @Test
        @DisplayName("Sucesso → Verifica Formulario Para Responder nao ublicado")
        void verificaFormularioParaResponderTests_nao_publicado() throws Exception {
            TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.FPR_CODIGO, null);
            to.setAttribute(Columns.FPE_CODIGO, "1");
            to.setAttribute(Columns.FPE_PUBLICADO, false);
            List<TransferObject> mockList = List.of(to);

            try (MockedConstruction<VerificaFormularioRespondidoQuery> construction = Mockito.mockConstruction(
                    VerificaFormularioRespondidoQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenReturn(mockList))) {
                String result = formularioPesquisaController.verificaFormularioParaResponder("1", responsavel);
                assertEquals(null, result);
            }
        }

        @Test
        @DisplayName("Sucesso → Verifica Formulario Para Responder")
        void verificaFormularioParaResponderTests_sem_resultado() throws Exception {
            try (MockedConstruction<VerificaFormularioRespondidoQuery> construction = Mockito.mockConstruction(
                    VerificaFormularioRespondidoQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenReturn(Collections.emptyList()))) {
                String result = formularioPesquisaController.verificaFormularioParaResponder("1", responsavel);
                assertEquals(null, result);
            }
        }

        @Test
        @DisplayName("Sucesso → Verifica Formulario Para Responder throw exception")
        void verificaFormularioParaResponderTests_throw_exception() throws Exception {
            try (MockedConstruction<VerificaFormularioRespondidoQuery> construction = Mockito.mockConstruction(
                    VerificaFormularioRespondidoQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenThrow(HQueryException.class))) {
                assertThrows(FormularioPesquisaControllerException.class,
                        () -> formularioPesquisaController.verificaFormularioParaResponder("1", responsavel),
                        "Should throw Exception");
            }
        }
    }

    @Nested
    @DisplayName("listFormularioPesquisaRespostaDash")
    class ListFormularioPesquisaRespostaDashTests {

        @Test
        @DisplayName("Sucesso → List Formulario Pesquisa Resposta Dash")
        void listFormularioPesquisaRespostaDash_sucesso() throws Exception {
            TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.FPE_JSON, "{fpeJson: 'fpeJson'}");
            to.setAttribute(Columns.FPR_JSON, "{fprJson: 'fprJson'}");
            List<TransferObject> mockList = List.of(to);

            try (MockedConstruction<ListaFormularioPesquisaRespostaDashQuery> construction = Mockito.mockConstruction(
                    ListaFormularioPesquisaRespostaDashQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenReturn(mockList))) {
                List<TransferObject> result = formularioPesquisaController.listFormularioPesquisaRespostaDash("1", responsavel);
                assertEquals(to.getAttribute(Columns.FPE_JSON), result.getFirst().getAttribute(Columns.FPE_JSON));
                assertEquals(to.getAttribute(Columns.FPR_JSON), result.getFirst().getAttribute(Columns.FPR_JSON));
            }
        }

        @Test
        @DisplayName("Falha lógica → List Formulario Pesquisa Resposta Dash throw exception")
        void listFormularioPesquisaRespostaDash_throw_exception() throws Exception {
            try (MockedConstruction<ListaFormularioPesquisaRespostaDashQuery> construction = Mockito.mockConstruction(
                    ListaFormularioPesquisaRespostaDashQuery.class,
                    (mock, context) -> Mockito.when(mock.executarDTO()).thenThrow(HQueryException.class))) {
                assertThrows(FormularioPesquisaControllerException.class,
                        () -> formularioPesquisaController.listFormularioPesquisaRespostaDash("1", responsavel),
                        "Should throw Exception");
            }
        }
    }
}