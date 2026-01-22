package com.zetra.econsig.web.controller.usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ConsultarUsuarioConsignanteWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Consultar Usuário Consignante.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarUsuarioCse" })
public class ConsultarUsuarioConsignanteWebController extends AbstractManterUsuarioPapelWebController {

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        boolean podeCriarUsu = responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_CSE);
        model.addAttribute("podeCriarUsu", podeCriarUsu);
        model.addAttribute("podeEditarBloqFunUsu", responsavel.temPermissao(CodedValues.FUN_EDT_BLOQUEIO_FUN_SER_USU_CSE));
        model.addAttribute("podeConsultarUsu", responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE));

        boolean podeEditarUsu = podeEditarUsuario(responsavel);
        model.addAttribute("podeEditarUsu", podeEditarUsu);
        model.addAttribute("podeEdtRestAcessoFun", podeEditarUsu && responsavel.temPermissao(CodedValues.FUN_EDT_RESTRICAO_ACESSO_POR_FUNCAO));

        // Telefone pode ser obrigatório somente para CSA/COR dependendo de habilitação de parâmetro TPC_CADASTRO_EMAIL_OBRIGATORIO_USUARIO_CSA
        boolean telObrigatorio = ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_TELEFONE_OBRIGATORIO_USUARIO_CSE, responsavel);;
        boolean emailObrigatorio = ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_EMAIL_OBRIGATORIO_CSE_ORG_SUP, responsavel);

        model.addAttribute("telObrigatorio", telObrigatorio);
        model.addAttribute("emailObrigatorio", emailObrigatorio);

        // Se for edição de usuário csa/cor, verifica se parâmetro de sistema permite cadastro de ip interno
        model.addAttribute("permiteIpInterno", true);

        model.addAttribute("validarCpfSuporte", true);

        // Consulta não pode editar
        if (!podeCriarUsu && !podeEditarUsu) {
            model.addAttribute("readOnly", true);
        }
    }

    @Override
    protected String getLinkAction() {
        return "../v3/consultarUsuarioCse";
    }

    @Override
    protected String getTipoEntidade() {
        return AcessoSistema.ENTIDADE_CSE;
    }

    @Override
    protected String getLinkConsultarUsuario() {
        return "../v3/consultarUsuarioCse";
    }

    @Override
    protected String getLinkInserirUsuario() {
        return "../v3/inserirUsuarioCse";
    }

    @Override
    protected String getLinkEditarUsuario() {
        return "../v3/editarUsuarioCse";
    }

    @Override
    protected String getLinkBloquearUsuario() {
        return "../v3/bloquearUsuarioCse";
    }

    @Override
    protected String getLinkReinicializarSenhaUsuario() {
        return "../v3/reinicializarSenhaUsuarioCse";
    }

    @Override
    protected String getLinkDetalharUsuario() {
        return "../v3/detalharUsuarioCse";
    }

    @Override
    protected String getLinkExcluirUsuario() {
        return "../v3/excluirUsuarioCse";
    }

    @Override
    protected boolean podeEditarUsuario(AcessoSistema responsavel) {
        return responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSE);
    }

    @Override
    protected String getColunaCodigoEntidade() {
        return Columns.CSE_CODIGO;
    }

    @Override
    protected String getFuncaoEdicaoBloqueioFuncaoServidor() {
        return CodedValues.FUN_EDT_BLOQUEIO_FUN_SER_USU_CSE;
    }
}
