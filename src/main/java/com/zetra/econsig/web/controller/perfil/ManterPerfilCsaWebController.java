package com.zetra.econsig.web.controller.perfil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterPerfilCsa" })
public class ManterPerfilCsaWebController extends AbstractManterPerfilWebController {
    
    @Override
    protected String getColunaAtivo(HttpServletRequest request) {
        return "";
    }

    @Override
    protected String getTipo(HttpServletRequest request) {
        return AcessoSistema.ENTIDADE_CSA;
    }

    @Override
    protected String getOperacao(HttpServletRequest request) {
        return null;
    }
    
}

