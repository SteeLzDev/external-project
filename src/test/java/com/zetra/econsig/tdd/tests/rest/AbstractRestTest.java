package com.zetra.econsig.tdd.tests.rest;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.config.ContextSpringConfiguration;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.OrgaoService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.ServidorService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.rest.request.UsuarioRestRequest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractRestTest extends ContextSpringConfiguration {

    protected static final String URL_AUTENTICACAO_SERVIDOR = "/consig/rest/usuario/login";
    protected static final String URL_CONSULTAR_MARGEM_SERVIDOR = "/consig/rest/usuario/consultarMargem";
    protected static final String URL_ALTERAR_SENHA_SERVIDOR = "/consig/rest/usuario/alterarSenha";
    protected static final String URL_GERAR_SENHA_AUT_SERVIDOR = "/consig/rest/usuario/gerarCodigoAutorizacao";
    protected static final String URL_CONSULTAR_SENHA_AUT_SERVIDOR = "/consig/rest/usuario/consultaCodigoAutorizacao";
    protected static final String URL_REMOVER_SENHA_AUT_SERVIDOR = "/consig/rest/usuario/removerCodigoAutorizacao";
    protected static final String URL_RECUPERA_DADOS_SER_VALIDACAO_V2  = "/consig/rest/usuario/v2/dadosValidacaoSer";
    protected static final String URL_RESERVAR_MARGEM_SERVIDOR = "/consig/rest/reservar/reservarMargem";
    protected static final String URL_REGISTRAR_LEITURA_MENSAGEM = "/consig/rest/mensagem/registrarLeituraMensagem";

    @Autowired
    private ServidorService servidorService;

    @Autowired
    private RegistroServidorService registroServidorService;

    @Autowired
    private OrgaoService orgaoService;

    @Autowired
    private UsuarioServiceTest usuarioService;

    @BeforeEach
    public void setup() throws Exception {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = EConsigInitializer.getServerRandomPort();
    }

    protected String obtainAccessToken(String username, String password, String authUrl) throws Exception {
        UsuarioRestRequest request = new UsuarioRestRequest();
        request.id = username;
        request.senha = password;

        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post(authUrl)
                .andReturn().jsonPath().getString("rseLogado.token")
                ;
    }

    protected void criarServidorParaTest(String estIdentificador, String orgIdentificador, String rseMatricula, String serNome, String serCpf, String senhaServidor) {
        criarServidorParaTest(estIdentificador, orgIdentificador, rseMatricula, serNome, serCpf, senhaServidor, null);
    }

    protected Usuario criarServidorParaTest(String estIdentificador, String orgIdentificador, String rseMatricula, String serNome, String serCpf, String senhaServidor, String email) {
        return criarServidorParaTest(estIdentificador, orgIdentificador, rseMatricula, serNome, serCpf, senhaServidor, email, new ArrayList<>(), new ArrayList<>());
    }

    protected Usuario criarServidorParaTest(String estIdentificador, String orgIdentificador, String rseMatricula, String serNome, String serCpf, String senhaServidor,
            String email, Collection<String> ipAccess, Collection<String> dnsAccess) {
        Orgao orgao = orgaoService.obterOrgaoPorIdentificador(orgIdentificador);
        // Cria novo servidor para uso no teste
        Servidor servidor = servidorService.obterServidorPeloCpf(serCpf);
        if (servidor == null) {
            servidor = servidorService.incluirServidor(serNome, serCpf, email);
        }
        // Busca o registro servidor pela matrícula e órgão, se não existir cria
        RegistroServidor registroServidor = registroServidorService.obterRegistroServidorPorMatriculaOrgao(rseMatricula, orgao.getOrgCodigo());
        if (registroServidor == null) {
            registroServidor = registroServidorService.incluirRegistroServidorAtivoComMargem(servidor.getSerCodigo(), orgao.getOrgCodigo(), rseMatricula);
        }
        // Cria usuário servidor
        String login = getLoginServidor(estIdentificador, orgIdentificador, rseMatricula);
        Usuario usuario = usuarioService.getUsuario(login);
        if (usuario == null) {
            usuario = usuarioService.criarUsuarioSer(login, senhaServidor, serNome, serCpf, CodedValues.STU_ATIVO, servidor.getSerCodigo(), email, ipAccess, dnsAccess);
        }
        log.debug("Servidor {}", servidor.getSerCodigo());
        log.debug("RegistroServidor {}", registroServidor.getRseCodigo());
        log.debug("Usuario {}", usuario.getUsuCodigo());

        return usuario;
    }

    protected Usuario criarServidorParaTest(String estIdentificador, String orgIdentificador, String rseMatricula, String serNome, String serCpf, String senhaServidor, String email, String statusRegistroServidor, String statusUsuarioServidor) {
        Orgao orgao = orgaoService.obterOrgaoPorIdentificador(orgIdentificador);
        // Cria novo servidor para uso no teste
        Servidor servidor = servidorService.obterServidorPeloCpf(serCpf);
        if (servidor == null) {
            servidor = servidorService.incluirServidor(serNome, serCpf, email);
        }
        // Busca o registro servidor pela matrícula e órgão, se não existir cria
        RegistroServidor registroServidor = registroServidorService.obterRegistroServidorPorMatriculaOrgao(rseMatricula, orgao.getOrgCodigo());
        if (registroServidor == null) {
            registroServidor = registroServidorService.incluirRegistroServidorAtivoComMargem(servidor.getSerCodigo(), orgao.getOrgCodigo(), rseMatricula, statusRegistroServidor);
        }
        // Cria usuário servidor
        String login = getLoginServidor(estIdentificador, orgIdentificador, rseMatricula);
        Usuario usuario = usuarioService.getUsuario(login);
        if (usuario == null) {
            usuario = usuarioService.criarUsuarioSer(login, senhaServidor, serNome, serCpf, statusUsuarioServidor, servidor.getSerCodigo());
        }
        log.debug("Servidor {}", servidor.getSerCodigo());
        log.debug("RegistroServidor {}", registroServidor.getRseCodigo());
        log.debug("Usuario {}", usuario.getUsuCodigo());

        return usuario;
    }

    protected String getLoginServidor(String estIdentificador, String orgIdentificador, String rseMatricula) {
        String login = estIdentificador + "-" + rseMatricula;
        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            login = estIdentificador + "-" + orgIdentificador + "-" + rseMatricula;
        }
        return login;
    }
}
