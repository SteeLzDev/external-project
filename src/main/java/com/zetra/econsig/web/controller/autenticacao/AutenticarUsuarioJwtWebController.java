package com.zetra.econsig.web.controller.autenticacao;

import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Estabelecimento;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ParamSenhaExternaEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: AuteticarUsuarioJWTWebController</p>
 * <p>Description: Controlador Web para o caso de uso Autenticar Usuario quando é via JWT.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.GET }, value = { "/v3/autenticarUsuarioJwt" })
public class AutenticarUsuarioJwtWebController extends AbstractWebController {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutenticarUsuarioJwtWebController.class);

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignanteControllerException, UsuarioControllerException {

        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
        Short status = sistemaController.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        String jwt = request.getParameter("jwt");
        boolean indisponivel = status.equals(CodedValues.STS_INDISP);
        if (indisponivel) {
            throw new UsuarioControllerException(LoginHelper.getMensagemSistemaIndisponivel(), responsavel);
        } else {
            try {
                if(!TextHelper.isNull(jwt)){
                    DecodedJWT jwtDecod = JWT.decode(jwt);
                    String cpf = jwtDecod.getClaim("pessoa_cpf").asString();
                    String entidadeCnpj = jwtDecod.getClaim("entidade_cnpj").asString();
                    String orgaoUrl = "";

                    if (!TextHelper.isNull(entidadeCnpj)) {
                        boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
                        ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
                        if(loginComEstOrg) {
                            Orgao orgao = consignanteController.findByOrgCnpj(entidadeCnpj, responsavel);
                            orgaoUrl = "&codigo_orgao=" + orgao.getOrgCodigo() + "&orgao=" + orgao.getOrgIdentificador();
                        } else {
                            Estabelecimento estabelecimento = consignanteController.findByEstCnpj(entidadeCnpj, responsavel);
                            orgaoUrl = "&codigo_orgao=" + estabelecimento.getEstCodigo() + "&orgao=" + estabelecimento.getEstIdentificador();
                        }
                    }

                    return ("redirect:/v3/autenticar?acao=autenticar&tipologin=cpf&username=" + cpf + "&serCpf=" + cpf + "&chave=" + jwt + orgaoUrl);
                } else {
                    throw new UsuarioControllerException("mensagem.erro.validacao.jwt.parametro", responsavel);
                }
            } catch (Exception ex ) {
                LOG.error(ex.getMessage(), ex);
                throw new UsuarioControllerException(ex);
            }
        }
    }

    @RequestMapping(params = { "acao=validar" })
    public ResponseEntity<String> validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String jwt = request.getParameter("chave");

        if(!TextHelper.isNull(jwt)){
            try {
                String token = ParamSenhaExternaEnum.JWT_TOKEN.getValor();
                Algorithm algorithm = Algorithm.HMAC256(token);
                JWTVerifier verifier = JWT.require(algorithm).build();

                // Verifica a autenticidade do token
                verifier.verify(jwt);

                // Verifica se o token já está expirado
                DecodedJWT jwtDecod = JWT.decode(jwt);
                long unixData = jwtDecod.getClaim("timestamp").asLong();
                Date dataToken = new Date(unixData*1000);

                if(DateHelper.minDiff(dataToken) > 5) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.jwt.token.expirado",responsavel));
                    return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.jwt.token.expirado",responsavel), HttpStatus.UNAUTHORIZED);
                }

                return new ResponseEntity<>("OK", HttpStatus.OK);

            } catch (JWTVerificationException ex){
                LOG.error(ex.getMessage(), ex);
            }
        }

        return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.jwt.parametro", responsavel), HttpStatus.UNAUTHORIZED);
    }
}
