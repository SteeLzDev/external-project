package com.zetra.econsig.web.controller.orgao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
/**
 * <p>Title: ListarOrgaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manutencao de Orgao.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarOrgao" })
public class ListarOrgaoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarOrgaoWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            /* orgao */
            boolean podeExcluirOrgao = responsavel.temPermissao(CodedValues.FUN_EXCL_ORGAO);
            boolean podeEditarOrgaos = responsavel.temPermissao(CodedValues.FUN_EDT_ORGAOS);
            boolean podeConsultarOrgaos = responsavel.temPermissao(CodedValues.FUN_CONS_ORGAOS);
            boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_ORG);
            boolean podeConsultarParamOrgao = responsavel.isCseSupOrg() && responsavel.temPermissao(CodedValues.FUN_CONS_PARAM_ORGAO);
            /* usuario */
            boolean podeCriarUsu = responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_ORG);
            boolean podeConsultarUsu = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG);
            /* perfil usuario */
            boolean podeConsultarPerfilUsu = responsavel.temPermissao(CodedValues.FUN_CONS_PERFIL_ORG);
            /* servico*/
            boolean podeEditarSvc = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);
            boolean podeConsultarSvc = responsavel.temPermissao(CodedValues.FUN_CONS_SERVICOS);
            boolean podeConsultarCnvCor = responsavel.isCsa() && responsavel.temPermissao(CodedValues.FUN_CONS_CONV_CORRESPONDENTE);

            List<TransferObject> orgaos = null;

            String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
            int filtro_tipo = -1;
            try {
                filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
            } catch (Exception ex1) {
            }

            try {
                CustomTransferObject criterio = new CustomTransferObject();

                // Seta csa_codigo caso responsável seja consignatária
                if (responsavel.isCsa()) {
                    criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
                }

                // -------------- Seta Criterio da Listagem ------------------
                // Bloqueado
                if (filtro_tipo == 0) {
                    List<Short> statusBloq = new ArrayList<>();
                    statusBloq.add(CodedValues.STS_INATIVO);
                    statusBloq.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                    criterio.setAttribute(Columns.ORG_ATIVO, statusBloq);
                    // Desbloqueado
                } else if (filtro_tipo == 1) {
                    criterio.setAttribute(Columns.ORG_ATIVO, CodedValues.STS_ATIVO);
                    // Outros
                } else if (!filtro.equals("") && filtro_tipo != -1) {
                    String campo = null;

                    switch (filtro_tipo) {
                        case 2:
                            campo = Columns.ORG_IDENTIFICADOR;
                            break;
                        case 3:
                            campo = Columns.ORG_NOME;
                            break;
                        case 4:
                            campo = Columns.EST_IDENTIFICADOR;
                            break;
                        case 5:
                            campo = Columns.EST_NOME;
                            break;
                        default:
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
                }
                // ---------------------------------------

                int total = consignanteController.countOrgaos(criterio, responsavel);
                int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (Exception ex) {
                }

                orgaos = consignanteController.lstOrgaos(criterio, offset, size, responsavel);

                String linkAction = "../v3/listarOrgao?acao=iniciar&FILTRO=" + filtro + "&FILTRO_TIPO=" + filtro_tipo;
                List<String> requestParams = new ArrayList<>();
                configurarPaginador(linkAction, "rotulo.paginacao.titulo.consignataria", total, size, requestParams, false, request, model);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                orgaos = new ArrayList<>();
            }

            // Exibe Botao que leva ao rodapé
            boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

            model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
            model.addAttribute("podeEditarOrgaos", podeEditarOrgaos);
            model.addAttribute("podeExcluirOrgao", podeExcluirOrgao);
            model.addAttribute("podeConsultarOrgaos", podeConsultarOrgaos);
            model.addAttribute("podeEditarEnderecoAcesso", podeEditarEnderecoAcesso);
            model.addAttribute("podeConsultarPerfilUsu", podeConsultarPerfilUsu);
            model.addAttribute("podeConsultarUsu", podeConsultarUsu);
            model.addAttribute("podeCriarUsu", podeCriarUsu);
            model.addAttribute("podeConsultarSvc", podeConsultarSvc);
            model.addAttribute("podeEditarSvc", podeEditarSvc);
            model.addAttribute("podeConsultarCnvCor", podeConsultarCnvCor);
            model.addAttribute("podeConsultarParamOrgao", podeConsultarParamOrgao);

            model.addAttribute("filtro_tipo", filtro_tipo);
            model.addAttribute("orgaos",orgaos);
            model.addAttribute("filtro", filtro);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/manterOrgao/listarOrgao", request, session, model, responsavel);
    }
}
