package com.zetra.econsig.web.controller.usuario.servidor;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarUsuarioServidorWebController.java</p>
 * <p>Description: Controlador Web para o caso de uso Listar usuário servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarUsuarioServidor" })
public class ListarUsuarioServidorWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarUsuarioServidorWebController.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    // Este método sobrescrito continua o fluxo pelo endpoint de pesquisarServidor em AbstractConsultarServidorWebController.java, como este endpoint pesquisarServidor é sobrescrito nesta classe, a sobrescrita do método abaixo fica obsoleta
    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        return null;
    }

    // Este método sobrescrito define o fluxo pelo endpoint de pesquisarServidor em AbstractConsultarServidorWebController.java, como este endpoint pesquisarServidor é sobrescrito nesta classe, a sobrescrita do método abaixo fica obsoleta
    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return null;
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.pesquisar.usuario.servidor.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/listarUsuarioServidor");
        model.addAttribute("omitirAdeNumero", true);
    }

    // DESENV-16417: Exército - Alterar Consulta Manutenção Usuário Servidor
    @Override
    @RequestMapping(params = { "acao=pesquisarServidor" })
    public String pesquisarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException, ParseException, ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
        String serCpf       = JspHelper.verificaVarQryStr(request, "SER_CPF");
        String serDataNasc  = JspHelper.verificaVarQryStr(request, "SER_DATA_NASC");

        // Verifica se necessita de matricula e CPF para efetuar consulta
        // Parâmetro de obrigatoriedade de CPF e Matrícula
        boolean requerMatriculaCpf = false;
        if (ParamSist.paramEquals(CodedValues.TPC_INF_MAT_CPF_EDT_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
            requerMatriculaCpf = false;
        } else {
            requerMatriculaCpf = parametroController.requerMatriculaCpf(responsavel);
        }

        // Se pelo menos um campo foi informado, mas o sistema requer que ambos sejam informados,
        // redireciona para a tela de pesquisa
        if (requerMatriculaCpf && (
                (!TextHelper.isNull(rseMatricula) &&  TextHelper.isNull(serCpf)) ||
                (TextHelper.isNull(rseMatricula) && !TextHelper.isNull(serCpf)))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.pesquisa.matricula.e.cpf.obrigatorios", responsavel));
            return iniciar(request, response, session, model);
        }
        
        // Parâmetro de obrigatoriedade de data de nascimento
        boolean requerDataNascimento = parametroController.requerDataNascimento(responsavel);

        // Se não informou data de nascimento do servidor na pesquisa, redireciona para a tela de pesquisa
        if (requerDataNascimento && TextHelper.isNull(serDataNasc)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.dataNascNaoInformada", responsavel));
            return iniciar(request, response, session, model);
        }

        List<String> rseCodigos = new ArrayList<>();
        if (!TextHelper.isNull(rseMatricula) || !TextHelper.isNull(serCpf)) {
            String tipoEntidade = responsavel.getTipoEntidade();
            String codigoEntidade = responsavel.getCodigoEntidade();

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                tipoEntidade = AcessoSistema.ENTIDADE_EST;
                codigoEntidade = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                tipoEntidade = AcessoSistema.ENTIDADE_CSA;
                codigoEntidade = responsavel.getCodigoEntidadePai();
            }

            int total = pesquisarServidorController.countPesquisaServidor(tipoEntidade, codigoEntidade, null, null, rseMatricula, serCpf, responsavel, false, null, false, null, null);
            int offset = (!TextHelper.isNull(request.getParameter("offset")) && TextHelper.isNum(request.getParameter("offset"))) ?  Integer.parseInt(request.getParameter("offset")) : 0;
            int size = JspHelper.LIMITE;

            // Se não encontrou nenhum servidor, define mensagem de erro e retorna à página de pesquisa
            if (total == 0) {
                String msg = ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel) + ":<br>";

                if (!TextHelper.isNull(rseMatricula)) {
                    msg += ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavel) + ": <span class=\"normal\">" + rseMatricula + "</span> ";
                }
                if (!TextHelper.isNull(serCpf)) {
                    msg += ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel) + ": <span class=\"normal\">" + serCpf + "</span>";
                }

                session.setAttribute(CodedValues.MSG_ERRO, msg);
                return tratarSevidorNaoEncontrado(request, response, session, model);
            }

            List<TransferObject> lstServidor = pesquisarServidorController.pesquisaServidor(tipoEntidade, codigoEntidade, null, null, rseMatricula, serCpf, offset, size, responsavel, false, null, false, null, null);
            model.addAttribute("lstServidor", lstServidor);
            model.addAttribute("_paginacaoSubTitulo", ApplicationResourcesHelper.getMessage("rotulo.paginacao.registros.sem.estilo", responsavel, String.valueOf(1), String.valueOf(lstServidor.size()), String.valueOf(lstServidor.size())));

            if (lstServidor == null || lstServidor.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel));
                return tratarSevidorNaoEncontrado(request, response, session, model);
            }

            List<String> listParams = Arrays.asList(new String[] { "SER_CPF", "serDataNasc", "EST_CODIGO", "ORG_CODIGO", "RSE_MATRICULA", "RSE_CODIGO" });

            // Incluido para paginação
            String linkListagem = "../v3/listarUsuarioServidor?acao=pesquisarServidor";

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

            TransferObject servidor = lstServidor.get(0);

            if (!TextHelper.isNull(rseMatricula) && !TextHelper.isNull(serCpf)) {
                if (!servidor.getAttribute(Columns.SER_CPF).toString().substring(0, Math.min(serCpf.length(), servidor.getAttribute(Columns.SER_CPF).toString().length())).equals(serCpf)) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.cpfInvalido", responsavel));
                }

                String matricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
                if (matricula != null && !matricula.isEmpty()) {
                    if (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
                        if (!Long.valueOf(matricula.substring(0, Math.min(rseMatricula.length(), matricula.length()))).equals(Long.valueOf(rseMatricula))) {
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.matriculaInvalida", responsavel));
                        }
                    } else if (!matricula.substring(0, Math.min(rseMatricula.length(), matricula.length())).equals(rseMatricula)) {
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.matriculaInvalida", responsavel));
                    }
                }
            }

            for (TransferObject ser : lstServidor) {
                rseCodigos.add((String) ser.getAttribute(Columns.RSE_CODIGO));
            }
        }

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        return continuarOperacaoListarUsuarioServidor(rseCodigos, request, response, session, model);
    }

    private String continuarOperacaoListarUsuarioServidor(List<String> rseCodigos, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        List<TransferObject> usuarioSerList = null;
        if (rseCodigos != null && !rseCodigos.isEmpty()) {
            try {
                usuarioSerList = usuarioController.lstUsuariosSerByRseCodigos(rseCodigos, responsavel);
                for (TransferObject usuarioSer : usuarioSerList) {
                    usuarioSer = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) usuarioSer, null, responsavel);
                }
            } catch (UsuarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        model.addAttribute("usuarioSerList", usuarioSerList);

        return viewRedirect("jsp/editarUsuarioServidor/listarUsuarioServidor", request, session, model, responsavel);
    }
}