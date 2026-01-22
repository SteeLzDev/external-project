package com.zetra.econsig.web.controller.servidor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: PesquisarServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Pesquisar Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/pesquisarServidor" })
public class PesquisarServidorWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PesquisarServidorWebController.class);

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Início do fluxo: não valida token, mas salva o valor para a próxima operação
        SynchronizerToken.saveToken(request);
        model.addAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, session.getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY));

        if (responsavel.isCseSup() || responsavel.isCsaCor()) {
            carregarListaEstabelecimento(request, session, model, responsavel);
            carregarListaOrgao(request, session, model, responsavel);
        }

        return viewRedirect("jsp/consultarServidorPorNome/pesquisarServidor", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=pesquisar" })
    public String pesquisar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UnsupportedEncodingException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String tipoEntidade;
            String codigoEntidade;

            if(responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)){
                tipoEntidade = "EST";
                codigoEntidade = responsavel.getCodigoEntidadePai();
            } else {
                tipoEntidade = responsavel.getTipoEntidade();
                codigoEntidade = responsavel.getCodigoEntidade();
            }

            String serCpf = request.getParameter("SER_CPF");
            String serNome = request.getParameter("serNome") != null ? request.getParameter("serNome").trim() : null;

            if (!TextHelper.isNull(serNome)) {
                serNome = java.net.URLDecoder.decode(serNome, "UTF-8");
            }

            String serSobrenome = request.getParameter("serSobreNome") != null ? request.getParameter("serSobreNome").trim() : null;

            if (!TextHelper.isNull(serSobrenome)) {
                serSobrenome = java.net.URLDecoder.decode(serSobrenome, "UTF-8");
            }

            String serDataNasc = request.getParameter("serDataNasc");
            Date serDataNascimento = null;
            String estCodigo = request.getParameter("EST_CODIGO");
            String orgCodigo = request.getParameter("ORG_CODIGO");
            String rseCodigo = request.getParameter("RSE_CODIGO");
            String rseMatricula = request.getParameter("RSE_MATRICULA");

            // Verifica formatação e realiza parse da data de nascimento
            if (!TextHelper.isNull(serDataNasc)) {
                try {
                    serDataNascimento = DateHelper.parse(serDataNasc, LocaleHelper.getDatePattern());
                } catch (ParseException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.data.nascimento.informada.invalida", responsavel, serDataNasc));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            List<String> listParams = Arrays.asList(new String[] { "SER_CPF", "serDataNasc", "EST_CODIGO", "ORG_CODIGO", "RSE_MATRICULA", "RSE_CODIGO" });

            TransferObject criterios = new CustomTransferObject();
            criterios.setAttribute("EST_CODIGO", estCodigo);
            criterios.setAttribute("ORG_CODIGO", orgCodigo);
            criterios.setAttribute("RSE_CODIGO", rseCodigo);
            criterios.setAttribute("NOME", serNome);
            criterios.setAttribute("SOBRENOME", serSobrenome);
            criterios.setAttribute("serDataNascimento", serDataNascimento);

            int offset = (request.getParameter("offset") != null) ? Integer.parseInt(request.getParameter("offset")) : 0;
            int size = JspHelper.LIMITE;
            int total = pesquisarServidorController.countPesquisaServidor(tipoEntidade, codigoEntidade, null, null, rseMatricula, serCpf, responsavel, false, null, false, null, criterios);

            if (total == 0 && responsavel.isCsaCor() && responsavel.temPermissao(CodedValues.FUN_INCLUIR_CONSIGNACAO)) {
                // Volta um elemento no topo da pilha para que ao voltar, o usuário caia no início da operação
                ParamSession paramSession = ParamSession.getParamSession(session);
                paramSession.halfBack();

                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

                return "forward:/v3/incluirConsignacao?acao=iniciarInclusaoServidor";
            }

            List<TransferObject> lstServidores = pesquisarServidorController.pesquisaServidor(tipoEntidade, codigoEntidade, null, null, rseMatricula, serCpf, offset, size, responsavel, false, null, false, null, criterios);
            model.addAttribute("lstServidor", lstServidores);

            // Incluido para paginação
            String linkListagem = "../v3/pesquisarServidor?acao=pesquisar";

            if (!TextHelper.isNull(serNome)) {
                linkListagem += "&serNome=" + java.net.URLEncoder.encode(serNome, "UTF-8");
            }

            if (!TextHelper.isNull(serSobrenome)) {
                linkListagem += "&serSobrenome=" + java.net.URLEncoder.encode(serSobrenome, "UTF-8");
            }

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(listParams);

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");
            params.remove("serNome");
            params.remove("serSobrenome");

            List<String> requestParams = new ArrayList<>(params);

            configurarPaginador(linkListagem, "rotulo.navegar.listagem.servidor", total, size, requestParams, false, request, model);

            // busca serviços para os quais o responsável tem convênio
            carregarListaServico(request, session, model, responsavel);

            List<String> listLinkRet = new ArrayList<>();
            listLinkRet.add("offset");
            listLinkRet.add("acao");
            listLinkRet.addAll(listParams);

            // monta link de retorno das ações
            String linkRet = montaLinkQrystring("../v3/pesquisarServidor", listLinkRet, request);
            if (!TextHelper.isNull(serNome)) {
                linkRet += "|serNome(" + java.net.URLEncoder.encode(serNome, "UTF-8");
            }

            if (!TextHelper.isNull(serSobrenome)) {
                linkRet += "|serSobrenome(" + java.net.URLEncoder.encode(serSobrenome, "UTF-8");
            }

            model.addAttribute("linkRet", linkRet);

            if(ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_OBRIGATORIA_EXIBIR_DADOS_CSA_COR, CodedValues.TPC_SIM, responsavel)) {
                model.addAttribute("senhaObrigatoria", true);
            }

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/consultarServidorPorNome/listarResultadoPesquisaServidor", request, session, model, responsavel);
    }


    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.servidor.pesquisa.titulo", responsavel));
        model.addAttribute("tituloResultado", ApplicationResourcesHelper.getMessage("rotulo.servidor.resultado.pesquisa.titulo", responsavel));
    }
}
