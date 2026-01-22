package com.zetra.econsig.web.controller.usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarUsuarioWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Usuário.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarUsuario" })
public class ListarUsuarioWebController extends ControlePaginacaoWebController {

    @Autowired
    private UsuarioController usuarioController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.usuario.lista.titulo", responsavel, titulo));
        model.addAttribute("linkAction", getLinkAction());
    }

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        boolean podeEditarAlgumUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSE) || responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSA) || responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_COR) || responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_ORG) || responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_SUP);
        boolean podeBlDesblAlgumUsu = responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSE) || responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSA) || responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_COR) || responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_ORG) || responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_SUP);
        boolean podeExcluirAlgumUsu = responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_CSE) || responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_CSA) || responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_COR) || responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_ORG) || responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_SUP);
        boolean podeReinicSenhaAlgumUsu = responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_CSE) || responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_CSA) || responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_COR) || responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_ORG) || responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_SUP);

        //Exige tipo de motivo da operacao
        boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel);

        String tipo = AcessoSistema.ENTIDADE_CSE;
        if (responsavel.isCsa()) {
            tipo = AcessoSistema.ENTIDADE_CSA;
        } else if (responsavel.isCor()) {
            tipo = AcessoSistema.ENTIDADE_COR;
        } else if (responsavel.isOrg()) {
            tipo = AcessoSistema.ENTIDADE_ORG;
        }
        int qtdColunas = 5;

        String codigo = responsavel.getCodigoEntidade();

        List<TransferObject> usuarios = null;
        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;
        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();

            // Não exibe usuários de centralizador para consignatárias/correspondentes
            if (!responsavel.isSup()) {
                criterio.setAttribute("OCULTA_USU_VISIVEL", Boolean.TRUE);
            }

            // -------------- Seta Criterio da Listagem ------------------
            // Bloqueado
            if (filtro_tipo == 0) {
                List<String> statusBloqueados = new ArrayList<>();
                statusBloqueados.add(CodedValues.STU_BLOQUEADO);
                statusBloqueados.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE);
                statusBloqueados.add(CodedValues.STU_BLOQUEADO_POR_CSE);
                statusBloqueados.add(CodedValues.STU_EXCLUIDO);
                statusBloqueados.add(CodedValues.STU_BLOQUEADO_AUSENCIA_TEMPORARIA);
                statusBloqueados.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                statusBloqueados.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_FIM_VIGENCIA);
                criterio.setAttribute(Columns.USU_STU_CODIGO, statusBloqueados);

                // Desbloqueado
            } else if (filtro_tipo == 1) {
                criterio.setAttribute(Columns.USU_STU_CODIGO, CodedValues.STU_ATIVO);
                // Outros
            } else if (!filtro.equals("") && filtro_tipo != -1 && filtro_tipo != 10) {
                String campo = null;

                switch (filtro_tipo) {
                    case 2:
                        campo = Columns.USU_LOGIN;
                        break;
                    case 3:
                        campo = Columns.USU_NOME;
                        break;
                    case 4:
                        campo = Columns.CSE_NOME;
                        break;
                    case 5:
                        campo = Columns.ORG_NOME;
                        break;
                    case 6:
                        campo = Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV;
                        break;
                    case 7:
                        campo = Columns.COR_NOME;
                        break;
                    case 8:
                        campo = Columns.USU_NOME;
                        break;
                    case 9:
                        campo = Columns.USU_CPF;
                        break;

                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
                // Tipo
            } else if (filtro.equals("") && filtro_tipo > 3 && filtro_tipo < 9) {
                String campo = null;

                switch (filtro_tipo) {
                    case 4:
                        campo = Columns.UCE_USU_CODIGO;
                        break;
                    case 5:
                        campo = Columns.UOR_USU_CODIGO;
                        break;
                    case 6:
                        campo = Columns.UCA_USU_CODIGO;
                        break;
                    case 7:
                        campo = Columns.UCO_USU_CODIGO;
                        break;
                    case 8:
                        campo = Columns.USP_USU_CODIGO;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, Columns.USU_CODIGO);
            }
            // ---------------------------------------

            int total = usuarioController.listCountUsuarios(filtro_tipo != 8 ? tipo : AcessoSistema.ENTIDADE_SUP, codigo, criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            usuarios = usuarioController.listUsuarios(filtro_tipo != 8 ? tipo : AcessoSistema.ENTIDADE_SUP, codigo, criterio, offset, size, responsavel);

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("senha");
            params.remove("serAutorizacao");
            params.remove("cryptedPasswordFieldName");
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador(getLinkAction(), "rotulo.paginacao.titulo.usuario", total, size, requestParams, false, request, model);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            usuarios = new ArrayList<>();
        }

        for (TransferObject usuario : usuarios) {
            String usu_tipo = usuario.getAttribute("TIPO").toString().toUpperCase();
            String sufixo = StringUtils.capitalize(usu_tipo.toLowerCase());

            usuario.setAttribute("linkConsultarUsuario", "../v3/consultarUsuario" + sufixo);
            //usuario.setAttribute("linkInserirUsuario", "../v3/inserirUsuario" + sufixo);
            usuario.setAttribute("linkEditarUsuario", "../v3/editarUsuario" + sufixo);
            usuario.setAttribute("linkBloquearUsuario", "../v3/bloquearUsuario" + sufixo);
            usuario.setAttribute("linkReinicializarSenhaUsuario", "../v3/reinicializarSenhaUsuario" + sufixo);
            usuario.setAttribute("linkExcluirUsuario", "../v3/excluirUsuario" + sufixo);
            usuario.setAttribute("linkDetalharUsuario", "../v3/detalharUsuario" + sufixo);
        }

        Map<String, Map<String, Boolean>> permissoes = new HashMap<>();

        Map<String, Boolean> permissoesCse = new HashMap<>();
        permissoesCse.put("reiniciarSenhaExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_CSE, responsavel));
        permissoesCse.put("excluirUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EXCL_USUARIO_CSE, responsavel));
        permissoesCse.put("bloquearUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSE, responsavel));
        permissoesCse.put("podeEditarUsu", responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSE));
        permissoesCse.put("podeBlDesblUsu", responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSE));
        permissoesCse.put("podeExcluirUsu", responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_CSE));
        permissoesCse.put("podeReinicSenha", responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_CSE));
        permissoes.put(AcessoSistema.ENTIDADE_CSE, permissoesCse);

        Map<String, Boolean> permissoesCsa = new HashMap<>();
        permissoesCsa.put("reiniciarSenhaExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_CSA, responsavel));
        permissoesCsa.put("excluirUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EXCL_USUARIO_CSA, responsavel));
        permissoesCsa.put("bloquearUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSA, responsavel));
        permissoesCsa.put("podeEditarUsu", responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSA));
        permissoesCsa.put("podeBlDesblUsu", responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSA));
        permissoesCsa.put("podeExcluirUsu", responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_CSA));
        permissoesCsa.put("podeReinicSenha", responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_CSA));
        permissoes.put(AcessoSistema.ENTIDADE_CSA, permissoesCsa);

        Map<String, Boolean> permissoesCor = new HashMap<>();
        permissoesCor.put("reiniciarSenhaExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_COR, responsavel));
        permissoesCor.put("excluirUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EXCL_USUARIO_COR, responsavel));
        permissoesCor.put("bloquearUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_COR, responsavel));
        permissoesCor.put("podeEditarUsu", responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_COR));
        permissoesCor.put("podeBlDesblUsu", responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_COR));
        permissoesCor.put("podeExcluirUsu", responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_COR));
        permissoesCor.put("podeReinicSenha", responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_COR));
        permissoes.put(AcessoSistema.ENTIDADE_COR, permissoesCor);

        Map<String, Boolean> permissoesOrg = new HashMap<>();
        permissoesOrg.put("reiniciarSenhaExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_ORG, responsavel));
        permissoesOrg.put("excluirUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EXCL_USUARIO_ORG, responsavel));
        permissoesOrg.put("bloquearUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_ORG, responsavel));
        permissoesOrg.put("podeEditarUsu", responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_ORG));
        permissoesOrg.put("podeBlDesblUsu", responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_ORG));
        permissoesOrg.put("podeExcluirUsu", responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_ORG));
        permissoesOrg.put("podeReinicSenha", responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_ORG));
        permissoes.put(AcessoSistema.ENTIDADE_ORG, permissoesOrg);

        Map<String, Boolean> permissoesSup = new HashMap<>();
        permissoesSup.put("reiniciarSenhaExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REINICIALIZAR_SENHA_SUP, responsavel));
        permissoesSup.put("excluirUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EXCL_USUARIO_SUP, responsavel));
        permissoesSup.put("bloquearUsuExigeMotivo", FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_SUP, responsavel));
        permissoesSup.put("podeEditarUsu", responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_SUP));
        permissoesSup.put("podeBlDesblUsu", responsavel.temPermissao(CodedValues.FUN_BLOQ_DESBLOQUEAR_USUARIOS_SUP));
        permissoesSup.put("podeExcluirUsu", responsavel.temPermissao(CodedValues.FUN_EXCL_USUARIO_SUP));
        permissoesSup.put("podeReinicSenha", responsavel.temPermissao(CodedValues.FUN_REINICIALIZAR_SENHA_SUP));
        permissoes.put(AcessoSistema.ENTIDADE_SUP, permissoesSup);

        model.addAttribute("podeEditarAlgumUsu", podeEditarAlgumUsu);
        model.addAttribute("podeBlDesblAlgumUsu", podeBlDesblAlgumUsu);
        model.addAttribute("podeExcluirAlgumUsu", podeExcluirAlgumUsu);
        model.addAttribute("podeReinicSenhaAlgumUsu", podeReinicSenhaAlgumUsu);
        model.addAttribute("exigeMotivoOperacaoUsu", exigeMotivoOperacaoUsu);
        model.addAttribute("permissoes", permissoes);
        model.addAttribute("usuarios", usuarios);

        model.addAttribute("tipo", tipo);
        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("qtdColunas", qtdColunas);

        return viewRedirect("jsp/manterUsuario/listarUsuario", request, session, model, responsavel);

    }

    protected String getCodigoEntidade(HttpServletRequest request) {
        return JspHelper.verificaVarQryStr(request, "codigo").toUpperCase();
    }

    protected String getLinkAction() {
        return "../v3/listarUsuario?acao=listar";
    }
}
