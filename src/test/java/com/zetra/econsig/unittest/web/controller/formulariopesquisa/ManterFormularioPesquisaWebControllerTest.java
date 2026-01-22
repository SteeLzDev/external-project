package com.zetra.econsig.unittest.web.controller.formulariopesquisa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.FormularioPesquisaTO;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.FormularioPesquisaControllerException;
import com.zetra.econsig.exception.FormularioPesquisaRespostaControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.FormularioPesquisa;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaController;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaRespostaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.formulariopesquisa.ManterFormularioPesquisaWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Testes unitários para {@link ManterFormularioPesquisaWebController}.
 */
class ManterFormularioPesquisaWebControllerTest {

    private ManterFormularioPesquisaWebController controller;
    private FormularioPesquisaController service;
    private FormularioPesquisaRespostaController respostaController;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private AcessoSistema responsavel;

    private MockedStatic<JspHelper> jspHelperMock;
    private MockedStatic<SynchronizerToken> tokenMock;
    private MockedStatic<ApplicationResourcesHelper> applicationResourceMock;

    @BeforeEach
    void setup() {
        service = mock(FormularioPesquisaController.class);
        respostaController = mock(FormularioPesquisaRespostaController.class);
        controller = new ManterFormularioPesquisaWebController();
        controller.service = service;
        controller.formularioPesquisaRespostaController = respostaController;

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        responsavel = mock(AcessoSistema.class);

        jspHelperMock = mockStatic(JspHelper.class);
        jspHelperMock.when(() -> JspHelper.getAcessoSistema(request)).thenReturn(responsavel);
        // Generic stub to delegate to request.getParameter
        jspHelperMock.when(() -> JspHelper.verificaVarQryStr(any(HttpServletRequest.class), anyString()))
                .thenAnswer(inv -> {
                    HttpServletRequest req = (HttpServletRequest) inv.getArgument(0);
                    String param = (String) inv.getArgument(1);
                    return req.getParameter(param);
                });
        tokenMock = mockStatic(SynchronizerToken.class);
        tokenMock.when(() -> SynchronizerToken.updateTokenInURL(anyString(), any(HttpServletRequest.class)))
                .thenReturn("/v3/formularioPesquisa?acao=listar&eConsig.page.token=ABC");
        
        applicationResourceMock = mockStatic(ApplicationResourcesHelper.class);
        applicationResourceMock.when(() -> ApplicationResourcesHelper.getMessage(anyString(), any()))
                .thenAnswer(inv -> inv.getArgument(0));
        applicationResourceMock.when(() -> ApplicationResourcesHelper.getMessage(anyString(), any(), any()))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        jspHelperMock.close();
        tokenMock.close();
        applicationResourceMock.close();
    }

    @Nested
    class ListarFormularioPesquisaTest {
        @Test
        @DisplayName("Deve listar formularios com sucesso")
        void deveListarComSucesso() throws ZetraException {
            List<TransferObject> lista = new ArrayList<>();
            when(service.listFormularioPesquisa(any(CustomTransferObject.class), eq(-1), eq(-1), eq(responsavel)))
                    .thenReturn(lista);

            Model model = new ConcurrentModel();
            String view = controller.listarFormularioPesquisa(request, response, session, model);

            assertTrue(model.containsAttribute("formulariosPesquisa"));
            assertEquals("jsp/manterFormularioPesquisa/listarFormularioPesquisa_v4", view);
        }

        @Test
        @DisplayName("Deve tratar excecao e redirecionar para pagina de erro")
        void deveTratarErro() throws FormularioPesquisaControllerException {
            when(service.listFormularioPesquisa(any(CustomTransferObject.class), anyInt(), anyInt(), eq(responsavel)))
                    .thenThrow(new FormularioPesquisaControllerException("erro", responsavel));
            String view = controller.listarFormularioPesquisa(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
            verify(session).setAttribute(eq(CodedValues.MSG_ERRO), any());
        }
    }

    @Nested
    class ExcluirTest {
        @Test
        @DisplayName("Token invalido deve redirecionar erro")
        void tokenInvalido() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(false);
            String view = controller.excluir(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }

        @Test
        @DisplayName("Erro ao excluir")
        void excluirErro() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn("123");
            doThrow(new FormularioPesquisaControllerException("falha", responsavel)).when(service)
                    .deleteFormularioPesquisa(eq("123"), eq(responsavel));
            String view = controller.excluir(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
            verify(session).setAttribute(eq(CodedValues.MSG_ERRO), any());
        }

        @Test
        @DisplayName("Excluir sucesso redireciona")
        void excluirSucesso() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn("999");
            String view = controller.excluir(request, response, session, new ConcurrentModel());
            assertEquals("jsp/redirecionador/redirecionar", view);
            verify(service).deleteFormularioPesquisa(eq("999"), eq(responsavel));
            verify(request).setAttribute(eq("url64"), any());
        }
    }

    @Nested
    class EditarTest {
        @Test
        @DisplayName("Editar sem codigo retorna view")
        void editarSemCodigo() {
            when(request.getParameter("fpeCodigo")).thenReturn(null);
            Model model = new ConcurrentModel();
            String view = controller.editar(request, response, session, model);
            assertEquals("jsp/manterFormularioPesquisa/editarFormularioPesquisa_v4", view);
            // attribute null acceptable
            assertNull(model.getAttribute("formularioPesquisaTO"));
        }

        @Test
        @DisplayName("Editar com codigo sucesso")
        void editarComCodigoSucesso() throws Exception {
            when(request.getParameter("fpeCodigo")).thenReturn("123");
            FormularioPesquisaTO to = mock(FormularioPesquisaTO.class);
            when(service.findByPrimaryKey("123")).thenReturn(to);
            Model model = new ConcurrentModel();
            String view = controller.editar(request, response, session, model);
            assertEquals("jsp/manterFormularioPesquisa/editarFormularioPesquisa_v4", view);
            assertEquals(to, model.getAttribute("formularioPesquisaTO"));
        }

        @Test
        @DisplayName("Editar com erro redireciona lista")
        void editarErro() throws Exception {
            when(request.getParameter("fpeCodigo")).thenReturn("123");
            when(service.findByPrimaryKey("123")).thenThrow(FindException.byMessage("falha"));
            String view = controller.editar(request, response, session, new ConcurrentModel());
            // Em caso de erro chama listarFormularioPesquisa
            assertEquals("jsp/manterFormularioPesquisa/listarFormularioPesquisa_v4", view);
        }
    }

    @Nested
    class SalvarTest {
        @Test
        @DisplayName("Token invalido salvar")
        void tokenInvalidoSalvar() {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(false);
            String view = controller.salvar(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }

        @Test
        @DisplayName("Salvar create quando codigo nulo")
        void salvarCreate() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn(null);
            when(request.getParameter("fpeNome")).thenReturn("Pesquisa X");
            when(request.getParameter("fpeBloqueiaSistema")).thenReturn("false");
            when(request.getParameter("fpePublicado")).thenReturn("true");
            when(request.getParameter("fpeJson")).thenReturn("e30="); // "{}" base64
            when(request.getParameter("fpeDtFim")).thenReturn(null);

            String view = controller.salvar(request, response, session, new ConcurrentModel());
            verify(service).createFormularioPesquisa(any(FormularioPesquisa.class));
            assertEquals("jsp/manterFormularioPesquisa/listarFormularioPesquisa_v4", view);
        }

        @Test
        @DisplayName("Erro salvar deve redirecionar erro")
        void erroSalvar() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn(null);
            when(request.getParameter("fpeNome")).thenReturn("Pesquisa X");
            when(request.getParameter("fpeBloqueiaSistema")).thenReturn("false");
            when(request.getParameter("fpePublicado")).thenReturn("true");
            when(request.getParameter("fpeJson")).thenReturn("e30="); // "{}" base64
            doThrow(new CreateException("erro", responsavel)).when(service)
                    .createFormularioPesquisa(any(FormularioPesquisa.class));
            String view = controller.salvar(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }

        @Test
        @DisplayName("Salvar update quando codigo presente")
        void salvarUpdate() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn("ABC");
            when(request.getParameter("fpeNome")).thenReturn("Pesquisa Update");
            when(request.getParameter("fpeBloqueiaSistema")).thenReturn("true");
            when(request.getParameter("fpePublicado")).thenReturn("false");
            when(request.getParameter("fpeJson")).thenReturn("e30K"); // "{}\n" base64
            when(request.getParameter("fpeDtFim")).thenReturn(null);
            String view = controller.salvar(request, response, session, new ConcurrentModel());
            verify(service).updateFormularioPesquisa(any(FormularioPesquisa.class), eq(responsavel));
            assertEquals("jsp/manterFormularioPesquisa/listarFormularioPesquisa_v4", view);
        }

        @Test
        @DisplayName("Salvar update erro UpdateException")
        void salvarUpdateErro() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn("ABC");
            when(request.getParameter("fpeNome")).thenReturn("Pesquisa Update");
            when(request.getParameter("fpeBloqueiaSistema")).thenReturn("true");
            when(request.getParameter("fpePublicado")).thenReturn("false");
            when(request.getParameter("fpeJson")).thenReturn("e30K"); // "{}\n" base64
            when(request.getParameter("fpeDtFim")).thenReturn(null);
            doThrow(UpdateException.class).when(service)
                    .updateFormularioPesquisa(any(FormularioPesquisa.class), eq(responsavel));
            String view = controller.salvar(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }

        @Test
        @DisplayName("Salvar update erro FormularioPesquisaRespostaControllerException")
        void salvarUpdateErroFormularioPesquisaRespostaControllerException() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn("ABC");
            when(request.getParameter("fpeNome")).thenReturn("Pesquisa Update");
            when(request.getParameter("fpeBloqueiaSistema")).thenReturn("true");
            when(request.getParameter("fpePublicado")).thenReturn("false");
            when(request.getParameter("fpeJson")).thenReturn("e30K"); // "{}\n" base64
            when(request.getParameter("fpeDtFim")).thenReturn(null);
            doThrow(FormularioPesquisaRespostaControllerException.class).when(service)
                    .updateFormularioPesquisa(any(FormularioPesquisa.class), eq(responsavel));
            String view = controller.salvar(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }

        @Test
        @DisplayName("Salvar parse data invalida gera erro")
        void salvarParseDateErro() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn(null);
            when(request.getParameter("fpeNome")).thenReturn("Pesquisa Data");
            when(request.getParameter("fpeBloqueiaSistema")).thenReturn("false");
            when(request.getParameter("fpePublicado")).thenReturn("true");
            when(request.getParameter("fpeJson")).thenReturn("e30="); // "{}" base64
            when(request.getParameter("fpeDtFim")).thenReturn("31/02/2025");
            
            try (MockedConstruction<SimpleDateFormat> construction = Mockito.mockConstruction(
                    SimpleDateFormat.class,
                    (mock, context) -> Mockito.when(mock.parse(anyString())).thenThrow(ParseException.class))) {
                String view = controller.salvar(request, response, session, new ConcurrentModel());
                assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
            }
        }
    }

    @Nested
    class UpdateStatusTest {
        @Test
        @DisplayName("Token invalido updateStatus")
        void tokenInvalido() {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(false);
            String view = controller.updateStatus(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }

        @Test
        @DisplayName("Despublicar quando publicado")
        void despublicarQuandoPublicado() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn("123");
            FormularioPesquisaTO to = mock(FormularioPesquisaTO.class);
            when(to.isFpePublicado()).thenReturn(true);
            when(to.getFpeNome()).thenReturn("Pesquisa X");
            when(service.findByPrimaryKey("123")).thenReturn(to);
            String view = controller.updateStatus(request, response, session, new ConcurrentModel());
            verify(service).despublicar(eq("123"), eq(responsavel));
            assertEquals("jsp/manterFormularioPesquisa/listarFormularioPesquisa_v4", view);
        }

        @Test
        @DisplayName("Publicar quando nao publicado")
        void publicarQuandoNaoPublicado() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn("123");
            FormularioPesquisaTO to = mock(FormularioPesquisaTO.class);
            when(to.isFpePublicado()).thenReturn(false);
            when(to.getFpeNome()).thenReturn("Pesquisa X");
            when(service.findByPrimaryKey("123")).thenReturn(to);
            String view = controller.updateStatus(request, response, session, new ConcurrentModel());
            verify(service).publicar(eq("123"));
            assertEquals("jsp/manterFormularioPesquisa/listarFormularioPesquisa_v4", view);
        }

        @Test
        @DisplayName("UpdateStatus erro FindException")
        void updateStatusErroFind() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn("404");
            when(service.findByPrimaryKey("404")).thenThrow(FindException.byMessage("nao encontrado"));
            String view = controller.updateStatus(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }

        @Test
        @DisplayName("UpdateStatus erro FormularioPesquisaRespostaControllerException")
        void updateStatusErroForm() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(request.getParameter("fpeCodigo")).thenReturn("123");
            FormularioPesquisaTO to = mock(FormularioPesquisaTO.class);
            when(to.isFpePublicado()).thenReturn(true);
            when(to.getFpeNome()).thenReturn("Pesquisa X");
            when(service.findByPrimaryKey("123")).thenReturn(to);
            doThrow(FormularioPesquisaRespostaControllerException.class).when(service).despublicar("123", responsavel);
            String view = controller.updateStatus(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }
    }

    @Nested
    class ListarPesquisaTest {
        @Test
        @DisplayName("Listar pesquisas sucesso")
        void listarPesquisasSucesso() throws ZetraException {
            tokenMock.when(() -> SynchronizerToken.saveToken(request)).thenAnswer(inv -> null);
            List<TransferObject> lista = new ArrayList<>();
            when(service.listFormularioPesquisa(any(CustomTransferObject.class), eq(-1), eq(-1), eq(responsavel)))
                    .thenReturn(lista);
            String view = controller.listarPesquisa(request, response, session, new ConcurrentModel());
            assertEquals("jsp/manterFormularioPesquisa/listarPesquisa_v4", view);
        }

        @Test
        @DisplayName("Listar pesquisas erro")
        void listarPesquisasErro() throws FormularioPesquisaControllerException {
            tokenMock.when(() -> SynchronizerToken.saveToken(request)).thenAnswer(inv -> null);
            when(service.listFormularioPesquisa(any(CustomTransferObject.class), anyInt(), anyInt(), eq(responsavel)))
                    .thenThrow(new FormularioPesquisaControllerException("erro", responsavel));
            String view = controller.listarPesquisa(request, response, session, new ConcurrentModel());
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }
    }

    @Nested
    class ProxyHandshakeTest {
        @Test
        @DisplayName("Exibir sucesso parse json")
        void exibirSucesso() throws Exception {
            List<TransferObject> lista = new ArrayList<>();
            TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.FPE_JSON, "{\"a\":1}");
            to.setAttribute(Columns.FPR_JSON, "{\"b\":2}");
            lista.add(to);
            when(service.listFormularioPesquisaRespostaDash(eq("123"), eq(responsavel))).thenReturn(lista);
            when(request.getParameter("fpeCodigo")).thenReturn("123");
            ResponseEntity<Map<String, Object>> resp = controller.proxyHandshake(request, response, null);
            assertEquals(200, resp.getStatusCode().value());
            assertTrue(resp.getBody().containsKey("fpr"));
        }

        @Test
        @DisplayName("Exibir erro controller")
        void exibirErroController() throws Exception {
            when(request.getParameter("fpeCodigo")).thenReturn("999");
            when(service.listFormularioPesquisaRespostaDash(eq("999"), eq(responsavel)))
                    .thenThrow(new FormularioPesquisaControllerException("erro", responsavel));
            ResponseEntity<Map<String, Object>> resp = controller.proxyHandshake(request, response, null);
            assertEquals(500, resp.getStatusCode().value());
        }

        @Test
        @DisplayName("Exibir fallback json invalido mantem string")
        void exibirFallbackJsonInvalido() throws Exception {
            List<TransferObject> lista = new ArrayList<>();
            TransferObject to1 = new CustomTransferObject();
            to1.setAttribute(Columns.FPE_JSON, "{invalido}"); // JSON inválido
            to1.setAttribute(Columns.FPR_JSON, "{invalido2}"); // JSON inválido
            lista.add(to1);
            when(service.listFormularioPesquisaRespostaDash(eq("222"), eq(responsavel))).thenReturn(lista);
            when(request.getParameter("fpeCodigo")).thenReturn("222");
            ResponseEntity<Map<String, Object>> resp = controller.proxyHandshake(request, response, null);
            assertEquals(200, resp.getStatusCode().value());
            assertTrue(resp.getBody().containsKey("fpe"));
            assertTrue(resp.getBody().get("fpr") instanceof List<?>);
            // Como JSON era inválido, resultado deve conter string original em 'fpe'
            assertEquals("{invalido}", resp.getBody().get("fpe"));
        }
    }
}
