package com.zetra.econsig.web.controller.consignante;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.persistence.entity.Banco;
import com.zetra.econsig.persistence.entity.TipoConsignante;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: EditarConsignanteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Editar Consignante.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarConsignante" })
public class EditarConsignanteWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarConsignanteWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private SistemaController sistemaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String retornoSincronizarValidarToken = sincronizarValidarToken(request, session, model, responsavel);
            if (retornoSincronizarValidarToken != null) {
                return retornoSincronizarValidarToken;
            }
            SynchronizerToken.saveToken(request);

            String cseCodigo = responsavel.getCodigoEntidade();
            boolean podeEditarConsignante = responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE);
            boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_CSE);

            ConsignanteTransferObject consignante = consignanteController.findConsignante(cseCodigo, responsavel);
            List<TipoConsignante> listTipoCSE = consignanteController.lstTipoCse(responsavel);
            List<Banco> listBanco = consignanteController.lstBanco(responsavel);
            boolean listBancoVazia = (consignanteController.lstBancoFolha(responsavel).size() > 0 ? false : true);

            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CSE_CODIGO, cseCodigo);

            int total = consignanteController.countOcorrenciaConsignante(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            List<TransferObject> lstOcorrencias = new ArrayList<>();

            if (responsavel.isSup()) {
                lstOcorrencias = consignanteController.lstOcorrenciaConsignante(criterio, offset, size, responsavel);
                String linkListagem = "../v3/editarConsignante?acao=iniciar";
                configurarPaginador(linkListagem, "rotulo.consignante.pagina.titulo", total, size, null, false, request, model);
            }

            model.addAttribute("cse_ativo", consignante.getCseAtivo() != null ? consignante.getCseAtivo() : CodedValues.STS_ATIVO);
            model.addAttribute("cse_nome", consignante.getCseNome());
            model.addAttribute("cse_codigo", cseCodigo);
            model.addAttribute("cse_identificador", consignante.getCseIdentificador());
            model.addAttribute("cseDataCobranca", consignante.getCseDataCobranca());
            model.addAttribute("podeEditarConsignante", podeEditarConsignante);
            model.addAttribute("podeEditarEnderecoAcesso", podeEditarEnderecoAcesso);
            model.addAttribute("podeConsultarUsuarios", Boolean.valueOf(responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE)));
            model.addAttribute("podeCriarUsuarios", Boolean.valueOf(responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_CSE)));
            model.addAttribute("podeConsultarPerfilUsu", Boolean.valueOf(responsavel.temPermissao(CodedValues.FUN_CONS_PERFIL_CSE)));
            model.addAttribute("podeConsParamSistCse", Boolean.valueOf(responsavel.temPermissao(CodedValues.FUN_CONS_PARAM_SISTEMA_CSE)));
            model.addAttribute("habilitarCodigoFolha", Boolean.valueOf(ParamSist.paramEquals(CodedValues.TPC_HABILITAR_EDICAO_CODIGO_FOLHA, CodedValues.TPC_SIM, responsavel)));
            model.addAttribute("cseDataCobranca", consignante.getCseDataCobranca());
            model.addAttribute("cse_ip_acesso", consignante.getCseIPAcesso());
            model.addAttribute("cse_ddns_acesso", consignante.getCseDDNSAcesso());
            model.addAttribute("consignante", consignante);
            model.addAttribute("listTipoCSE", listTipoCSE);
            model.addAttribute("listBanco", listBanco);
            model.addAttribute("listBancoVazia", listBancoVazia);
            model.addAttribute("lstOcorrencias", lstOcorrencias);
            if (!model.containsAttribute("msgErro")) {
                model.addAttribute("msgErro", "");
            }

            return viewRedirect("jsp/editarConsignante/editarConsignante", request, session, model, responsavel);

        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ServletException, IOException, ConsignanteControllerException, ViewHelperException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String retornoSincronizarValidarToken = sincronizarValidarToken(request, session, model, responsavel);
        if (retornoSincronizarValidarToken != null) {
            return retornoSincronizarValidarToken;
        }

        // Tratamento para impedir que seja usado caracteres de controle no email
        String cseEmail = JspHelper.verificaVarQryStr(request, "CSE_EMAIL").replaceAll("[(\r\n|\n)&&[\\p{Cntrl}]]", "");
        String cseEmailFolha = JspHelper.verificaVarQryStr(request, "CSE_EMAIL_FOLHA").replaceAll("[(\r\n|\n)&&[\\p{Cntrl}]]", "");
        String cseEmailValidarServidor = JspHelper.verificaVarQryStr(request, "CSE_EMAIL_VALIDAR_SERVIDOR").replaceAll("[(\r\n|\n)&&[\\p{Cntrl}]]", "");

        String[] cseBancoRequest = request.getParameterValues("BCO_CODIGO");
        List<String> cseBancos = null;
        if(cseBancoRequest != null) {
            cseBancos = new ArrayList<>(Arrays.asList(cseBancoRequest));
        }

        boolean podeEditarConsignante = responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE);
        boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_CSE);

        String msgErro = "";
        String cseCodigo = responsavel.getCodigoEntidade();

        if (podeEditarConsignante) {
            if (request.getParameter("CSE_CODIGO") != null) {
                // Atualiza a consignante
                String[] param1 = { "CSE_IDENTIFICADOR", "CSE_NOME" };
                String[] param2 = { ApplicationResourcesHelper.getMessage("rotulo.funcao.codigo", responsavel) + ", " + ApplicationResourcesHelper.getMessage("rotulo.funcao.descricao", responsavel) };
                String[] param3 = { ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel) + " " + ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios.campos", responsavel, param2) };
                msgErro = JspHelper.verificaCamposForm(request, param1, param3);
                if (!TextHelper.isNull(msgErro)) {
                    JspHelper.addMsgSession(session, CodedValues.MSG_ERRO, msgErro);
                }

                if (msgErro.length() == 0) {
                    try {
                        ConsignanteTransferObject consignante = new ConsignanteTransferObject(cseCodigo);
                        consignante.setCseIdentificador(JspHelper.verificaVarQryStr(request, "CSE_IDENTIFICADOR"));
                        consignante.setCseNome(JspHelper.verificaVarQryStr(request, "CSE_NOME"));
                        consignante.setCseCnpj(JspHelper.verificaVarQryStr(request, "CSE_CNPJ"));
                        consignante.setCseEmail(cseEmail);
                        consignante.setCseResponsavel(JspHelper.verificaVarQryStr(request, "CSE_RESPONSAVEL"));
                        consignante.setCseResponsavel2(JspHelper.verificaVarQryStr(request, "CSE_RESPONSAVEL_2"));
                        consignante.setCseResponsavel3(JspHelper.verificaVarQryStr(request, "CSE_RESPONSAVEL_3"));
                        consignante.setCseRespCargo(JspHelper.verificaVarQryStr(request, "CSE_RESP_CARGO"));
                        consignante.setCseRespCargo2(JspHelper.verificaVarQryStr(request, "CSE_RESP_CARGO_2"));
                        consignante.setCseRespCargo3(JspHelper.verificaVarQryStr(request, "CSE_RESP_CARGO_3"));
                        consignante.setCseRespTelefone(JspHelper.verificaVarQryStr(request, "CSE_RESP_TELEFONE"));
                        consignante.setCseRespTelefone2(JspHelper.verificaVarQryStr(request, "CSE_RESP_TELEFONE_2"));
                        consignante.setCseRespTelefone3(JspHelper.verificaVarQryStr(request, "CSE_RESP_TELEFONE_3"));
                        consignante.setCseLogradouro(JspHelper.verificaVarQryStr(request, "CSE_LOGRADOURO"));
                        consignante.setCseCompl(JspHelper.verificaVarQryStr(request, "CSE_COMPL"));
                        consignante.setCseBairro(JspHelper.verificaVarQryStr(request, "CSE_BAIRRO"));
                        consignante.setCseCidade(JspHelper.verificaVarQryStr(request, "CSE_CIDADE"));
                        consignante.setCseUf(JspHelper.verificaVarQryStr(request, "CSE_UF"));
                        consignante.setCseCep(JspHelper.verificaVarQryStr(request, "CSE_CEP"));
                        consignante.setCseTel(JspHelper.verificaVarQryStr(request, "CSE_TEL"));
                        consignante.setCseFax(JspHelper.verificaVarQryStr(request, "CSE_FAX"));
                        consignante.setCseEmailFolha(cseEmailFolha);
                        consignante.setCseEmailValidarServidor(cseEmailValidarServidor);
                        consignante.setCseFolha(JspHelper.verificaVarQryStr(request, "CSE_FOLHA"));
                        consignante.setCseSistemaFolha(JspHelper.verificaVarQryStr(request, "CSE_SISTEMA_FOLHA"));

                        //Projeto Inadimplencia
                        String partProjInadimplenciaOld = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "cse_projeto_inadimplencia_old")) ? JspHelper.verificaVarQryStr(request, "cse_projeto_inadimplencia_old") : "N";
                        String partProjInadimplencia = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSE_PROJETO_INADIMPLENCIA")) ? JspHelper.verificaVarQryStr(request, "CSE_PROJETO_INADIMPLENCIA") : partProjInadimplenciaOld;
                        if (!partProjInadimplenciaOld.equals(partProjInadimplencia) && responsavel.isSup()) {
                            consignante.setCseProjetoInadimplencia(partProjInadimplencia);
                        }

                        if (responsavel.isSup()) {
                            consignante.setTipoConsignante(JspHelper.verificaVarQryStr(request, "TCE_CODIGO"));
                            if(cseBancos != null && cseBancos.size() > 0) {
                                cseBancos.removeAll(Arrays.asList(""));
                                consignanteController.setBancosCse(cseBancos);
                            }
                        }

                        consignante.setCseDataCobranca(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSE_DATA_COBRANCA")) ? java.sql.Date.valueOf(DateHelper.reformat(JspHelper.verificaVarQryStr(request, "CSE_DATA_COBRANCA"), LocaleHelper.getDatePattern(), "yyyy-MM-dd")) : null);
                        if (!JspHelper.verificaVarQryStr(request, "CSE_NRO").equals("")) {
                            consignante.setCseNro(Integer.valueOf(JspHelper.verificaVarQryStr(request, "CSE_NRO")));
                        } else {
                            consignante.setCseNro(null);
                        }
                        String cseIPAcesso = JspHelper.verificaVarQryStr(request, "cse_ip_acesso");
                        String cseDDNSAcesso = JspHelper.verificaVarQryStr(request, "cse_ddns_acesso");
                        if (podeEditarEnderecoAcesso) {
                            // Valida a lista de IPs
                            if (!TextHelper.isNull(cseIPAcesso)) {
                                List<String> ipsAcesso = Arrays.asList(cseIPAcesso.split(";"));
                                if (!JspHelper.validaListaIps(ipsAcesso)) {
                                    throw new ViewHelperException("mensagem.erro.ip.invalido", responsavel);
                                }
                            }
                            consignante.setCseIPAcesso(cseIPAcesso);
                            consignante.setCseDDNSAcesso(cseDDNSAcesso);
                        }
                        consignanteController.updateConsignante(consignante, responsavel);

                        // Atualiza nome
                        responsavel.setNomeEntidade(JspHelper.verificaVarQryStr(request, "CSE_NOME"));

                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.consignante.sucesso", responsavel));
                        LoginHelper.setCseNome(JspHelper.verificaVarQryStr(request, "CSE_NOME"));
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    }
                }
            }

            // Bloqueia a consignante
            if (request.getParameter("status") != null && request.getParameter("codigo") != null) {
                try {
                    Short status = Short.valueOf(JspHelper.verificaVarQryStr(request, "status"));
                    status = status.equals(CodedValues.STS_ATIVO) ? CodedValues.STS_INDISP : CodedValues.STS_ATIVO;
                    if (status.equals(CodedValues.STS_INDISP)) {
                        String motivo = request.getParameter("motivo");
                        if (!TextHelper.isNull(motivo)) {
                            sistemaController.alteraStatusSistema(cseCodigo, status, motivo, responsavel);
                            EnviaEmailHelper.enviarEmailBloqueioSistema(motivo, responsavel);
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.consignante.sucesso", responsavel));
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.consignante.motivo.bloqueio", responsavel));
                        }
                    } else {
                        sistemaController.alteraStatusSistema(cseCodigo, status, null, responsavel);
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.consignante.sucesso", responsavel));
                    }
                } catch (ConsignanteControllerException | ViewHelperException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }
        }
        if (!TextHelper.isNull(msgErro)) {
            model.addAttribute("msgErro", msgErro);
        }

        // Volta um elemento no topo da pilha para que ao voltar, o usuário caia no início da operação
        ParamSession paramSession = ParamSession.getParamSession(session);
        paramSession.halfBack();

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=editarEnderecoAcesso" })
    public String editarEnderecoAcesso(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ServletException, IOException, ConsignanteControllerException, ViewHelperException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String retornoSincronizarValidarToken = sincronizarValidarToken(request, session, model, responsavel);
        if (retornoSincronizarValidarToken != null) {
            return retornoSincronizarValidarToken;
        }
        //Tratamento para impedir que seja usado caracteres de controle no email.
        boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_CSE);
        String cse_codigo = responsavel.getCodigoEntidade();
        if (podeEditarEnderecoAcesso) {
            ConsignanteTransferObject consignante = new ConsignanteTransferObject(cse_codigo);
            String cseIPAcesso = JspHelper.verificaVarQryStr(request, "cse_ip_acesso");
            String cseDDNSAcesso = JspHelper.verificaVarQryStr(request, "cse_ddns_acesso");
            // Valida a lista de IPs
            if (!TextHelper.isNull(cseIPAcesso)) {
                List<String> ipsAcesso = Arrays.asList(cseIPAcesso.split(";"));
                if (!JspHelper.validaListaIps(ipsAcesso)) {
                    throw new ViewHelperException("mensagem.erro.ip.invalido", responsavel);
                }
            }
            consignante.setCseIPAcesso(cseIPAcesso);
            consignante.setCseDDNSAcesso(cseDDNSAcesso);
            consignanteController.updateConsignante(consignante, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.consignante.sucesso", responsavel));
            LoginHelper.setCseNome(JspHelper.verificaVarQryStr(request, "CSE_NOME"));
        }

        // Volta um elemento no topo da pilha para que ao voltar, o usuário caia no início da operação
        ParamSession paramSession = ParamSession.getParamSession(session);
        paramSession.halfBack();

        return iniciar(request, response, session, model);
    }

    private String sincronizarToken(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return null;
    }

    private String validarToken(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        if (request.getParameter("CSE_CODIGO") != null || request.getParameter("status") != null && request.getParameter("codigo") != null) {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }
        return null;
    }

    private String sincronizarValidarToken(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        String retornoSincronizar = sincronizarToken(request, session, model, responsavel);
        if (retornoSincronizar != null) {
            return retornoSincronizar;
        }
        String retornoValidar = validarToken(request, session, model, responsavel);
        if (retornoValidar != null) {
            return retornoValidar;
        }
        return null;
    }
}
