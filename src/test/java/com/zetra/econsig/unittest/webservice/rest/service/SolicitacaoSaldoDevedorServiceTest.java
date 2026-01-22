package com.zetra.econsig.unittest.webservice.rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.service.SolicitacaoSaldoDevedorService;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

public class SolicitacaoSaldoDevedorServiceTest {

	private SolicitacaoSaldoDevedorService newServiceWith(SecurityContext securityContext) {
		SolicitacaoSaldoDevedorService service = new SolicitacaoSaldoDevedorService();
		try {
			Field f = SolicitacaoSaldoDevedorService.class.getDeclaredField("securityContext");
			f.setAccessible(true);
			f.set(service, securityContext);
		} catch (Exception e) {
			fail("Falha ao injetar SecurityContext no serviço: " + e.getMessage());
		}
		return service;
	}

	private SecurityContext mockSecurityContext(boolean isCseSup, boolean temPermissao) {
		SecurityContext ctx = mock(SecurityContext.class);
		AcessoSistema principal = mock(AcessoSistema.class);
		when(principal.isCseSup()).thenReturn(isCseSup);
		when(principal.temPermissao(anyString())).thenReturn(temPermissao);
		when(ctx.getUserPrincipal()).thenReturn(principal);
		return ctx;
	}

	@Test
	@DisplayName("Retorna 401 quando usuário não possui permissão/supervisor")
	void shouldReturn401WhenNoPermission() {
		SecurityContext ctx = mockSecurityContext(false, true);
		SolicitacaoSaldoDevedorService service = newServiceWith(ctx);

		try (MockedStatic<ApplicationResourcesHelper> mockedStatic = Mockito.mockStatic(ApplicationResourcesHelper.class)) {
		    mockedStatic.when(() -> ApplicationResourcesHelper.getMessage(anyString(), any(AcessoSistema.class), any()))
			    .thenAnswer(inv -> inv.getArgument(0));

		    Response resp = service.listaSolicitacaoSaldoDevedorByRseCodigo("RSE123");

		    assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), resp.getStatus());
		    assertTrue(resp.getEntity() instanceof ResponseRestRequest);
		}
	}

	@Test
	@DisplayName("Retorna 400 quando rseCodigo é nulo")
	void shouldReturn400WhenRseCodigoNull() {
		SecurityContext ctx = mockSecurityContext(true, true);
		SolicitacaoSaldoDevedorService service = newServiceWith(ctx);

		try (MockedStatic<ApplicationResourcesHelper> mockedStatic = Mockito.mockStatic(ApplicationResourcesHelper.class)) {
		    mockedStatic.when(() -> ApplicationResourcesHelper.getMessage(anyString(), any(AcessoSistema.class), any()))
			    .thenAnswer(inv -> inv.getArgument(0));

		    Response resp = service.listaSolicitacaoSaldoDevedorByRseCodigo(null);

		    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
		    assertTrue(resp.getEntity() instanceof ResponseRestRequest);
		}
	}

	@Test
	@DisplayName("Retorna 400 quando rseCodigo é vazio")
	void shouldReturn400WhenRseCodigoEmpty() {
		SecurityContext ctx = mockSecurityContext(true, true);
		SolicitacaoSaldoDevedorService service = newServiceWith(ctx);

		try (MockedStatic<ApplicationResourcesHelper> mockedStatic = Mockito.mockStatic(ApplicationResourcesHelper.class)) {
		    mockedStatic.when(() -> ApplicationResourcesHelper.getMessage(anyString(), any(AcessoSistema.class), any()))
			    .thenAnswer(inv -> inv.getArgument(0));

		    Response resp = service.listaSolicitacaoSaldoDevedorByRseCodigo("");

		    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
		    assertTrue(resp.getEntity() instanceof ResponseRestRequest);
		}
	}

	@Test
	@DisplayName("Retorna 500 quando delegate lança AutorizacaoControllerException")
	void shouldReturn500WhenDelegateThrows() throws Exception {
		SecurityContext ctx = mockSecurityContext(true, true);
		SolicitacaoSaldoDevedorService service = newServiceWith(ctx);

		try (MockedStatic<ApplicationResourcesHelper> mockedStatic = Mockito.mockStatic(ApplicationResourcesHelper.class);
		     MockedConstruction<AutorizacaoDelegate> mocked = Mockito.mockConstruction(AutorizacaoDelegate.class,
				(mock, context) -> when(mock.listaSolicitacaoSaldoDevedorPorRegistroServidor(anyString(), any(AcessoSistema.class)))
						.thenThrow(new AutorizacaoControllerException("erro.simulado", null)))) {
		    mockedStatic.when(() -> ApplicationResourcesHelper.getMessage(anyString(), any(AcessoSistema.class), any()))
			    .thenAnswer(inv -> inv.getArgument(0));

		    Response resp = service.listaSolicitacaoSaldoDevedorByRseCodigo("RSE123");

			assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), resp.getStatus());
			assertTrue(resp.getEntity() instanceof ResponseRestRequest);
		}
	}

	@Test
	@DisplayName("Retorna 200 e lista transformada quando sucesso")
	void shouldReturn200AndListOnSuccess() {
		SecurityContext ctx = mockSecurityContext(true, true);
		SolicitacaoSaldoDevedorService service = newServiceWith(ctx);

		List<TransferObject> delegateResult = new ArrayList<>();
		CustomTransferObject item = new CustomTransferObject();
		item.setAttribute("foo", "bar");
		delegateResult.add(item);

		try (MockedStatic<ApplicationResourcesHelper> mockedStatic = Mockito.mockStatic(ApplicationResourcesHelper.class);
		     MockedConstruction<AutorizacaoDelegate> mocked = Mockito.mockConstruction(AutorizacaoDelegate.class,
				(mock, context) -> when(mock.listaSolicitacaoSaldoDevedorPorRegistroServidor(anyString(), any(AcessoSistema.class)))
						.thenReturn(delegateResult))) {
		    mockedStatic.when(() -> ApplicationResourcesHelper.getMessage(anyString(), any(AcessoSistema.class), any()))
			    .thenAnswer(inv -> inv.getArgument(0));

		    Response resp = service.listaSolicitacaoSaldoDevedorByRseCodigo("RSE123");

			assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
			assertNotNull(resp.getEntity());
		}
	}
}
