package com.zetra.econsig.web.controller.consignacao;

import java.io.IOException;
import java.text.ParseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;

/**
 * <p>Title: AbstractListarTodasConsignacoesWebController</p>
 * <p>Description: Controlador Web base para o casos de uso que possuem listam de todas as consignações.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractListarTodasConsignacoesWebController extends AbstractConsultarConsignacaoWebController {

    @RequestMapping(params = { "acao=pesquisarServidor", "TIPO_LISTA=TUDO" })
    public String listarTudoViaPesquisaServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException, ParametroControllerException, ServidorControllerException, AutorizacaoControllerException, ParseException {
        return listarTudo(request, response, session, model);
    }

    @RequestMapping(params = { "acao=pesquisarConsignacao", "TIPO_LISTA=TUDO" })
    public String listarTudoViaPesquisaConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException, ParametroControllerException, ServidorControllerException, AutorizacaoControllerException, ParseException {
        return listarTudo(request, response, session, model);
    }

    private String listarTudo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException, ParametroControllerException, ServidorControllerException, AutorizacaoControllerException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        request.setAttribute("listarTodos", Boolean.TRUE);
        request.setAttribute("tituloResultado", ApplicationResourcesHelper.getMessage("rotulo.pesquisa.listar.todos", responsavel).toUpperCase());
        return pesquisarConsignacao(null, null, request, response, session, model);
    }
}
