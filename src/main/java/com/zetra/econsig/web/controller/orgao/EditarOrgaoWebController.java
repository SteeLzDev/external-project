package com.zetra.econsig.web.controller.orgao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.RepasseHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.AbstractWebController; 

/**
 * <p>Title: EditarOrgaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manutencao de Orgao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarOrgao" })
public class EditarOrgaoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarOrgaoWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @RequestMapping(params = { "acao=consultarOrgao" })
    public String consultarOrgao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=editarOrgao" })
    public String editarOrgao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=editarIp" })
    public String editarIp(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    private String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            ParamSession paramSession = new ParamSession();
            // Valida o token
            if (!responsavel.isOrg() && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            /* orgao */
            boolean podeEditarOrgaos = (responsavel.isCseSup() ? responsavel.temPermissao(CodedValues.FUN_EDT_ORGAOS) : (responsavel.isOrg() ? responsavel.temPermissao(CodedValues.FUN_EDT_ORGAO) : false));
            boolean podeConsultarParamOrgao = responsavel.isCseSupOrg() && responsavel.temPermissao(CodedValues.FUN_CONS_PARAM_ORGAO);
            /* usuario */
            boolean podeCriarUsu = responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_ORG);

            boolean podeConsultarUsu = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG);
            /* perfil usuario */
            boolean podeConsultarPerfilUsu = responsavel.temPermissao(CodedValues.FUN_CONS_PERFIL_ORG);
            boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_ORG);

            boolean isOrg = false;

            if (responsavel.isOrg()) {
                isOrg = true;
            }

            String criaConvenio = (request.getParameter("cria_convenio") != null) ? request.getParameter("cria_convenio").toString() : "";
            String orgaoCopiado = (request.getParameter("copia_cnv") != null) ? request.getParameter("copia_cnv").toString() : "";
            String org_codigo = null;
            String org_ativo = null;
            String org_nome = null;

            if (!isOrg) {
                org_codigo = JspHelper.verificaVarQryStr(request, "org");
            } else {
                org_codigo = responsavel.getCodigoEntidade();
            }
            // Atualiza o orgao
            String reqColumnsStr = "EST_CODIGO|ORG_IDENTIFICADOR|ORG_NOME";
            String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
            OrgaoTransferObject orgao = null;

            if (request.getParameter("MM_update") != null && msgErro.length() == 0) {
                // Se usuário tiver permissão permite continuar com o caso de uso salvar órgão
                if (podeEditarOrgaos || podeEditarEnderecoAcesso) {
                    //Verificar se todos os campos obrigatórios foram preenchidos antes de salvar
                    boolean camposObrigatoriosOk = validaCamposObrigatorios(request, responsavel);
                    try {
                        if (org_codigo != null && !org_codigo.equals("")) {
                            orgao = new OrgaoTransferObject(org_codigo);
                        } else {
                            orgao = new OrgaoTransferObject();
                            orgao.setOrgAtivo(Short.valueOf("1"));
                        }
                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_CNPJ, responsavel)) {
                            if (JspHelper.verificaVarQryStr(request, "ORG_CNPJ").equals("")) {
                                orgao.setOrgCnpj(null);
                            } else {
                                orgao.setOrgCnpj(JspHelper.verificaVarQryStr(request, "ORG_CNPJ"));
                            }
                        }
                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_ESTABELECIMENTO, responsavel)) {
                            orgao.setEstCodigo(JspHelper.verificaVarQryStr(request, "EST_CODIGO"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_BAIRRO, responsavel)) {
                            orgao.setOrgBairro(JspHelper.verificaVarQryStr(request, "ORG_BAIRRO"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_CEP, responsavel)) {
                            orgao.setOrgCep(JspHelper.verificaVarQryStr(request, "ORG_CEP"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_CIDADE, responsavel)) {
                            orgao.setOrgCidade(JspHelper.verificaVarQryStr(request, "ORG_CIDADE"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_COMPLEMENTO, responsavel)) {
                            orgao.setOrgCompl(JspHelper.verificaVarQryStr(request, "ORG_COMPL"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_EMAIL, responsavel)) {
                            orgao.setOrgEmail(JspHelper.verificaVarQryStr(request, "ORG_EMAIL"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_EMAIL_INTEGRA_FOLHA, responsavel)) {
                            orgao.setOrgEmailFolha(JspHelper.verificaVarQryStr(request, "ORG_EMAIL_FOLHA"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_EMAIL_VALIDA_SERVIDOR, responsavel)) {
                            orgao.setOrgEmailValidarServidor(JspHelper.verificaVarQryStr(request, "ORG_EMAIL_VALIDAR_SERVIDOR"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_FAX, responsavel)) {
                            orgao.setOrgFax(JspHelper.verificaVarQryStr(request, "ORG_FAX"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_IDENTIFICADOR, responsavel)) {
                            orgao.setOrgIdentificador(JspHelper.verificaVarQryStr(request, "ORG_IDENTIFICADOR"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_LOGRADOURO, responsavel)) {
                            orgao.setOrgLogradouro(JspHelper.verificaVarQryStr(request, "ORG_LOGRADOURO"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_NOME, responsavel)) {
                            orgao.setOrgNome(JspHelper.verificaVarQryStr(request, "ORG_NOME"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_NRO, responsavel)) {
                            if (!JspHelper.verificaVarQryStr(request, "ORG_NRO").equals("")) {
                                orgao.setOrgNro(Integer.valueOf(JspHelper.verificaVarQryStr(request, "ORG_NRO")));
                            } else {
                                orgao.setOrgNro(null);
                            }
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_UF, responsavel)) {
                            orgao.setOrgUf(JspHelper.verificaVarQryStr(request, "ORG_UF"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL, responsavel)) {
                            orgao.setOrgResponsavel(JspHelper.verificaVarQryStr(request, "ORG_RESPONSAVEL"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL_2, responsavel)) {
                            orgao.setOrgResponsavel2(JspHelper.verificaVarQryStr(request, "ORG_RESPONSAVEL_2"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL_3, responsavel)) {
                            orgao.setOrgResponsavel3(JspHelper.verificaVarQryStr(request, "ORG_RESPONSAVEL_3"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_RESP_CARGO, responsavel)) {
                            orgao.setOrgRespCargo(JspHelper.verificaVarQryStr(request, "ORG_RESP_CARGO"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_RESP_CARGO_2, responsavel)) {
                            orgao.setOrgRespCargo2(JspHelper.verificaVarQryStr(request, "ORG_RESP_CARGO_2"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_RESP_CARGO_3, responsavel)) {
                            orgao.setOrgRespCargo3(JspHelper.verificaVarQryStr(request, "ORG_RESP_CARGO_3"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE, responsavel)) {
                            orgao.setOrgRespTelefone(JspHelper.verificaVarQryStr(request, "ORG_RESP_TELEFONE"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE_2, responsavel)) {
                            orgao.setOrgRespTelefone2(JspHelper.verificaVarQryStr(request, "ORG_RESP_TELEFONE_2"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE_3, responsavel)) {
                            orgao.setOrgRespTelefone3(JspHelper.verificaVarQryStr(request, "ORG_RESP_TELEFONE_3"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_TELEFONE, responsavel)) {
                            orgao.setOrgTel(JspHelper.verificaVarQryStr(request, "ORG_TEL"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_COD_FOLHA, responsavel)) {
                            orgao.setOrgFolha(JspHelper.verificaVarQryStr(request, "ORG_FOLHA"));
                        }

                        if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_DIA_REPASSE, responsavel)) {
                            if (!JspHelper.verificaVarQryStr(request, "ORG_DIA_REPASSE").equals("")) {
                                orgao.setOrgDiaRepasse(Integer.valueOf(JspHelper.verificaVarQryStr(request, "ORG_DIA_REPASSE")));
                            } else {
                                orgao.setOrgDiaRepasse(null);
                            }
                        }

                        String orgIPAcesso = JspHelper.verificaVarQryStr(request, "org_ip_acesso");
                        String orgDDNSAcesso = JspHelper.verificaVarQryStr(request, "org_ddns_acesso");

                        if (podeEditarEnderecoAcesso) {
                            // Valida a lista de IPs
                            if (!TextHelper.isNull(orgIPAcesso)) {
                                List<String> ipsAcesso = Arrays.asList(orgIPAcesso.split(";"));
                                if (!JspHelper.validaListaIps(ipsAcesso)) {
                                    throw new ViewHelperException("mensagem.orgao.ip.invalido", responsavel);
                                }
                            }

                            if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_IP_ACESSOS, responsavel)) {
                                orgao.setOrgIPAcesso(orgIPAcesso);
                            }

                            if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_ORG_DDNS_ACESSOS, responsavel)) {
                                orgao.setOrgDDNSAcesso(orgDDNSAcesso);
                            }
                        }

                        if (camposObrigatoriosOk) {
                            if (org_codigo != null && !org_codigo.equals("")) {
                                consignanteController.updateOrgao(orgao, responsavel);
                            } else {
                                if (TextHelper.isNull(criaConvenio) || !criaConvenio.equals("S")) {
                                    org_codigo = consignanteController.createOrgao(orgao, responsavel);
                                } else {
                                    // Se foi selecionado box de criação de convênios...
                                    org_codigo = consignanteController.createOrgao(orgao, true, orgaoCopiado, responsavel);
                                }

                                //Colocando um endereço no paramSession
                                Map<String, String[]> parametros = new HashMap<>();
                                parametros.put("tipo", new String[] { "consultar" });
                                parametros.put("org", new String[] { org_codigo });
                                String link = request.getRequestURI();
                                paramSession.addHistory(link, parametros);
                            }

                            // Limpa o cache de parâmetros de dia de repasse
                            RepasseHelper.getInstance().reset();

                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.orgao.alterado.sucesso", responsavel));
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel));
                        }

                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    }
                    // DESENV-13923 - Se usuário NÃO tiver permissão para editar órgão retorna erro na tela
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            List<TransferObject> orgaos = null;
            try {
                if (!org_codigo.equals("")) {
                    orgao = consignanteController.findOrgao(org_codigo, responsavel);
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<TransferObject> estabelecimentos = null;
            try {
                CustomTransferObject criterio = null;
                if (isOrg) {
                    criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.EST_CODIGO, orgao.getEstCodigo());
                }
                estabelecimentos = consignanteController.lstEstabelecimentos(criterio, responsavel);

            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            org_nome = orgao != null ? (orgao.getOrgNome() != null ? orgao.getOrgNome() : JspHelper.verificaVarQryStr(request, "ORG_NOME")) : "";

            boolean habilitarCodigoFolha = ParamSist.paramEquals(CodedValues.TPC_HABILITAR_EDICAO_CODIGO_FOLHA, CodedValues.TPC_SIM, responsavel);

            if (org_codigo == null || org_codigo.equals("")) {
                orgaos = consignanteController.lstOrgaos(null, responsavel);
            }

            // Exibe Botao que leva ao rodapé
            boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

            model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
            model.addAttribute("org_codigo", org_codigo);
            model.addAttribute("msgErro", msgErro);
            model.addAttribute("org_ativo", org_ativo);
            model.addAttribute("orgao", orgao);
            model.addAttribute("isOrg", isOrg);
            model.addAttribute("podeEditarOrgaos", podeEditarOrgaos);
            model.addAttribute("podeConsultarPerfilUsu", podeConsultarPerfilUsu);
            model.addAttribute("podeConsultarUsu", podeConsultarUsu);
            model.addAttribute("podeCriarUsu", podeCriarUsu);
            model.addAttribute("org_nome", org_nome);
            model.addAttribute("estabelecimentos", estabelecimentos);
            model.addAttribute("habilitarCodigoFolha", habilitarCodigoFolha);
            model.addAttribute("podeEditarEnderecoAcesso", podeEditarEnderecoAcesso);
            model.addAttribute("podeConsultarParamOrgao", podeConsultarParamOrgao);
            model.addAttribute("orgaos", orgaos);
            if (orgao != null) {
                model.addAttribute("org_ip_acesso", orgao.getOrgIPAcesso());
                model.addAttribute("org_ddns_acesso", orgao.getOrgDDNSAcesso());
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterOrgao/editarOrgao", request, session, model, responsavel);
    }

    private boolean validaCamposObrigatorios(HttpServletRequest request, AcessoSistema responsavel) throws ZetraException {
        boolean camposObrigatoriosOk = true;
        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_CNPJ, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_CNPJ").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_CNPJ")))) {
            camposObrigatoriosOk = false;
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_ESTABELECIMENTO, responsavel) && (JspHelper.verificaVarQryStr(request, "EST_CODIGO").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "EST_CODIGO")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_BAIRRO, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_BAIRRO").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_BAIRRO")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_CEP, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_CEP").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_CEP")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_CIDADE, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_CIDADE").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_CIDADE")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_COMPLEMENTO, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_COMPL").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_COMPL")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_EMAIL, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_EMAIL").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_EMAIL")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_EMAIL_INTEGRA_FOLHA, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_EMAIL_FOLHA").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_EMAIL_FOLHA")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_EMAIL_VALIDA_SERVIDOR, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_EMAIL_VALIDAR_SERVIDOR").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_EMAIL_VALIDAR_SERVIDOR")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_FAX, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_FAX").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_FAX")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_IDENTIFICADOR, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_IDENTIFICADOR").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_IDENTIFICADOR")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_LOGRADOURO, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_LOGRADOURO").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_LOGRADOURO")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_NOME, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_NOME").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_NOME")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_NRO, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_NRO").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_NRO")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_UF, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_UF").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_UF")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_RESPONSAVEL").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_RESPONSAVEL")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL_2, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_RESPONSAVEL_2").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_RESPONSAVEL_2")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL_3, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_RESPONSAVEL_3").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_RESPONSAVEL_3")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_RESP_CARGO, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_RESP_CARGO").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_RESP_CARGO")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_RESP_CARGO_2, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_RESP_CARGO_2").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_RESP_CARGO_2")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_RESP_CARGO_3, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_RESP_CARGO_3").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_RESP_CARGO_3")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_RESP_TELEFONE").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_RESP_TELEFONE")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE_2, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_RESP_TELEFONE_2").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_RESP_TELEFONE_2")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE_3, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_RESP_TELEFONE_3").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_RESP_TELEFONE_3")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_TELEFONE, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_TEL").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_TEL")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_COD_FOLHA, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_FOLHA").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_FOLHA")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_DIA_REPASSE, responsavel) && (JspHelper.verificaVarQryStr(request, "ORG_DIA_REPASSE").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ORG_DIA_REPASSE")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_IP_ACESSOS, responsavel) && (JspHelper.verificaVarQryStr(request, "org_ip_acesso").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "org_ip_acesso")))) {
            camposObrigatoriosOk = false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_ORG_DDNS_ACESSOS, responsavel) && (JspHelper.verificaVarQryStr(request, "org_ddns_acesso").equals("") || TextHelper.isNull(JspHelper.verificaVarQryStr(request, "org_ddns_acesso")))) {
            camposObrigatoriosOk = false;
        }

        return camposObrigatoriosOk;
    }

    @RequestMapping(params = { "acao=bloquearOrgao" })
    public String bloquearOrgao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);
        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            // Bloqueia o órgão
            if (request.getParameter("status") != null && request.getParameter("codigo") != null) {
                try {
                    String ativo = request.getParameter("status");
                    ativo = ativo.equals("1") ? "0" : "1";
                    OrgaoTransferObject orgBloq = new OrgaoTransferObject(request.getParameter("codigo"));
                    orgBloq.setOrgAtivo(Short.valueOf(ativo));
                    consignanteController.updateOrgao(orgBloq, responsavel);
                    if (ativo.equals("1")) {
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.orgao.status.desbloqueado", responsavel));
                    } else {
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.orgao.status.bloqueado", responsavel));
                    }
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                }
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";

    }

    @RequestMapping(params = { "acao=excluirOrgao" })
    public String excluirOrgao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);
        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            if (request.getParameter("excluir") != null && request.getParameter("codigo") != null) {
                try {
                    OrgaoTransferObject orgRem = new OrgaoTransferObject(request.getParameter("codigo"));
                    consignanteController.removeOrgao(orgRem, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.orgao.excluido.sucesso", responsavel));
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                }
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
