package com.zetra.econsig.unittest.web.controller.formulariopesquisa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import com.zetra.econsig.dto.entidade.FormularioPesquisaTO;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FormularioPesquisaControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.FormularioPesquisaResposta;
import com.zetra.econsig.persistence.entity.UsuarioChaveSessao;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaController;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaRespostaController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.web.controller.formulariopesquisa.FormularioPesquisaRespostaWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Testes unitários para {@link FormularioPesquisaRespostaWebController}
 * alinhados ao padrão de ManterFormularioPesquisaWebControllerTest.
 */
class FormularioPesquisaRespostaWebControllerTest {

    private FormularioPesquisaRespostaWebController controller;
    private FormularioPesquisaController formularioPesquisaController;
    private FormularioPesquisaRespostaController formularioPesquisaRespostaController;
    private UsuarioController usuarioController;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private AcessoSistema responsavel;

    private MockedStatic<JspHelper> jspHelperMock;
    private MockedStatic<SynchronizerToken> tokenMock;
    private MockedStatic<ApplicationResourcesHelper> applicationResourceMock;

    @BeforeEach
    void setup() {
        formularioPesquisaController = mock(FormularioPesquisaController.class);
        formularioPesquisaRespostaController = mock(FormularioPesquisaRespostaController.class);
        usuarioController = mock(UsuarioController.class);

        controller = new FormularioPesquisaRespostaWebController();
        controller.formularioPesquisaController = formularioPesquisaController;
        controller.formularioPesquisaRespostaController = formularioPesquisaRespostaController;
        controller.usuarioController = usuarioController;

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        responsavel = new AcessoSistema("1");

        jspHelperMock = mockStatic(JspHelper.class);
        jspHelperMock.when(() -> JspHelper.getAcessoSistema(any(HttpServletRequest.class))).thenReturn(responsavel);
        tokenMock = mockStatic(SynchronizerToken.class);

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
    class ResponderFormularioPesquisaTest {
        @Test
        @DisplayName("Responder formulario - sucesso")
        void responderFormularioSucesso() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(formularioPesquisaController.verificaFormularioParaResponder(anyString(), any(AcessoSistema.class)))
                    .thenReturn("ABC");
            FormularioPesquisaTO to = new FormularioPesquisaTO();
            to.setFpeBloqueiaSistema(true);
            when(formularioPesquisaController.findByPrimaryKey("ABC")).thenReturn(to);

            Model model = new ConcurrentModel();
            String view = controller.responderFormularioPesquisa(request, response, session, model);
            assertEquals("jsp/manterFormularioPesquisaResposta/manterFormularioPesquisaResposta_v4", view);
            assertTrue(model.containsAttribute("formulario"));
            verify(session).setAttribute("formularioObrigatorio", "1");
        }

        @Test
        @DisplayName("Responder formulario - token inválido")
        void responderFormularioTokenInvalido() {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(false);
            Model model = new ConcurrentModel();
            String view = controller.responderFormularioPesquisa(request, response, session, model);
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
            verify(session).setAttribute(any(), any());
        }

        @Test
        @DisplayName("Responder formulario - exceção controller")
        void responderFormularioErroController() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(request)).thenReturn(true);
            when(formularioPesquisaController.verificaFormularioParaResponder(anyString(), any(AcessoSistema.class)))
                    .thenThrow(new FormularioPesquisaControllerException("erro", responsavel));
            Model model = new ConcurrentModel();
            String view = controller.responderFormularioPesquisa(request, response, session, model);
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }
    }

    @Nested
    class SalvarRespostaTest {
        @Test
        @DisplayName("Salvar resposta - token inválido retorna UNAUTHORIZED")
        void salvarRespostaTokenInvalido() {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(any(HttpServletRequest.class))).thenReturn(false);
            ResponseEntity<String> resp = controller.salvarResposta("{}", "123", session, request);
            assertEquals(401, resp.getStatusCode().value());
        }

        @Test
        @DisplayName("Salvar resposta - erro CreateException")
        void salvarRespostaErroCreate() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(any(HttpServletRequest.class))).thenReturn(true);
            doThrow(new CreateException("erro", responsavel)).when(formularioPesquisaRespostaController)
                    .createFormularioPesquisaResposta(any(FormularioPesquisaResposta.class));
            ResponseEntity<String> resp = controller.salvarResposta("{}", "123", session, request);
            assertEquals(500, resp.getStatusCode().value());
        }

        @Test
        @DisplayName("Salvar resposta - sucesso")
        void salvarRespostaSucesso() throws Exception {
            tokenMock.when(() -> SynchronizerToken.isTokenValid(any(HttpServletRequest.class))).thenReturn(true);
            ResponseEntity<String> resp = controller.salvarResposta("{}", "123", session, request);
            assertEquals(200, resp.getStatusCode().value());
        }
    }

    @Nested
    class MobileTest {
        @Test
        @DisplayName("Mobile - sucesso")
        void mobileSucesso() throws Exception {
            UsuarioChaveSessao chave = new UsuarioChaveSessao();
            chave.setUsuCodigo("1");
            when(usuarioController.validateToken("token")).thenReturn(chave);
            when(formularioPesquisaController.verificaFormularioParaResponder(anyString(), any(AcessoSistema.class)))
                    .thenReturn("ABC");
            FormularioPesquisaTO to = new FormularioPesquisaTO();
            to.setFpeBloqueiaSistema(true);
            when(formularioPesquisaController.findByPrimaryKey("ABC")).thenReturn(to);
            Model model = new ConcurrentModel();
            String view = controller.mobile("token", "1", request, response, session, model);
            assertEquals("jsp/manterFormularioPesquisaResposta/manterFormularioPesquisaRespostaMobile_v4", view);
            assertTrue(model.containsAttribute("formulario"));
        }

        @Test
        @DisplayName("Mobile - token outro usuário")
        void mobileTokenOutroUsuario() throws Exception {
            UsuarioChaveSessao chave = new UsuarioChaveSessao();
            chave.setUsuCodigo("2");
            when(usuarioController.validateToken("token")).thenReturn(chave);
            Model model = new ConcurrentModel();
            String view = controller.mobile("token", "1", request, response, session, model);
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }

        @Test
        @DisplayName("Mobile - UsuarioControllerException")
        void mobileUsuarioControllerException() throws Exception {
            when(usuarioController.validateToken("token"))
                    .thenThrow(new UsuarioControllerException("erro", responsavel));
            Model model = new ConcurrentModel();
            String view = controller.mobile("token", "1", request, response, session, model);
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }

        @Test
        @DisplayName("Mobile - erro ao buscar formulário")
        void mobileErroBuscarFormulario() throws Exception {
            UsuarioChaveSessao chave = new UsuarioChaveSessao();
            chave.setUsuCodigo("1");
            when(usuarioController.validateToken("token")).thenReturn(chave);
            when(formularioPesquisaController.verificaFormularioParaResponder(anyString(), any(AcessoSistema.class)))
                    .thenThrow(new FormularioPesquisaControllerException("erro", responsavel));
            Model model = new ConcurrentModel();
            String view = controller.mobile("token", "1", request, response, session, model);
            assertEquals("jsp/visualizarPaginaErro/visualizarMensagem_v4", view);
        }
    }

    @Nested
    class SalvarRespostaMobileTest {
        @Test
        @DisplayName("Salvar resposta mobile - sucesso")
        void salvarRespostaMobileSucesso() throws Exception {
            UsuarioChaveSessao chave = new UsuarioChaveSessao();
            chave.setUsuCodigo("1");
            when(usuarioController.validateToken("token")).thenReturn(chave);
            ResponseEntity<String> resp = controller.salvarRespostaMobile("{}", "123", "token", "1", session, request,
                    new ConcurrentModel());
            assertEquals(200, resp.getStatusCode().value());
        }

        @Test
        @DisplayName("Salvar resposta mobile - token outro usuário BAD_REQUEST")
        void salvarRespostaMobileOutroUsuario() throws Exception {
            UsuarioChaveSessao chave = new UsuarioChaveSessao();
            chave.setUsuCodigo("2");
            when(usuarioController.validateToken("token")).thenReturn(chave);
            ResponseEntity<String> resp = controller.salvarRespostaMobile("{}", "123", "token", "1", session, request,
                    new ConcurrentModel());
            assertEquals(400, resp.getStatusCode().value());
        }

        @Test
        @DisplayName("Salvar resposta mobile - erro CreateException")
        void salvarRespostaMobileErroCreate() throws Exception {
            UsuarioChaveSessao chave = new UsuarioChaveSessao();
            chave.setUsuCodigo("1");
            when(usuarioController.validateToken("token")).thenReturn(chave);
            doThrow(new CreateException("erro", responsavel)).when(formularioPesquisaRespostaController)
                    .createFormularioPesquisaResposta(any(FormularioPesquisaResposta.class));
            ResponseEntity<String> resp = controller.salvarRespostaMobile("{}", "123", "token", "1", session, request,
                    new ConcurrentModel());
            assertEquals(500, resp.getStatusCode().value());
        }

        @Test
        @DisplayName("Salvar resposta mobile - UsuarioControllerException")
        void salvarRespostaMobileUsuarioControllerException() throws Exception {
            when(usuarioController.validateToken("token"))
                    .thenThrow(new UsuarioControllerException("erro", responsavel));
            ResponseEntity<String> resp = controller.salvarRespostaMobile("{}", "123", "token", "1", session, request,
                    new ConcurrentModel());
            assertEquals(400, resp.getStatusCode().value());
        }
    }
}
