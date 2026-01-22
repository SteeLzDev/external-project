package com.zetra.econsig.web.controller.sistema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Banco;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AtualizarCadastroWebController</p>
 * <p>Description: Controlador Web para o caso de uso Atualizar dados Usuário.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision:  $
 * $Date: 2019-12-30 13:57:41 -0300 (ter, 30 dez 2019) $
 */
@Controller
public class AtualizarCadastroWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AtualizarCadastroWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(value = { "/v3/atualizarCadastro" }, method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        Map<String,String> dadosAtualizacao = new HashMap<>();
        String msgAtualizacao = "";
        boolean exigeAtualizacaoCadastralCsaCnpj= ParamSist.paramEquals(CodedValues.TPC_EXIGE_ATUALIZACAO_CADASTRAL_CSA_CNPJ, CodedValues.TPC_SIM, responsavel);
        boolean listBancoVazia = false;
        boolean podeEditarConsignante = true;
        List<Banco> listBanco = new ArrayList<>();

        try {

            if (responsavel.isCse() && responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE)) {
                ConsignanteTransferObject cse = consignanteController.findConsignante(responsavel.getCseCodigo(), responsavel);
                listBancoVazia = (consignanteController.lstBancoFolha(responsavel).size() > 0 ? false : true);

                dadosAtualizacao.put("nomeResponsavel", cse.getCseResponsavel());
                dadosAtualizacao.put("telResponsavel", cse.getCseRespTelefone());
                dadosAtualizacao.put("email", cse.getCseEmail());
                dadosAtualizacao.put("codigoEntidade",responsavel.getCodigoEntidade());
                dadosAtualizacao.put("nomeEntidade",cse.getCseNome());
                dadosAtualizacao.put("cseNomeFolha",cse.getCseSistemaFolha());

                listBanco = consignanteController.lstBanco(responsavel);
                model.addAttribute("listBanco", listBanco);
                model.addAttribute("listBancoVazia", listBancoVazia);
                model.addAttribute("podeEditarConsignante", podeEditarConsignante);

                msgAtualizacao = ApplicationResourcesHelper.getMessage("mensagem.atualizar.cadastro.informacao", responsavel, responsavel.getUsuNome(),cse.getCseNome());
            } else if (responsavel.isCsa() && responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIA)) {

                ConsignatariaTransferObject csa = consignatariaController.findConsignataria(responsavel.getCsaCodigo(), responsavel);

                dadosAtualizacao.put("nomeResponsavel", csa.getCsaResponsavel());
                dadosAtualizacao.put("telResponsavel", csa.getCsaRespTelefone());
                dadosAtualizacao.put("email", csa.getCsaEmail());
                dadosAtualizacao.put("codigoEntidade",responsavel.getCsaCodigo());
                dadosAtualizacao.put("nomeEntidade",csa.getCsaNome());
                if (exigeAtualizacaoCadastralCsaCnpj) {
                    dadosAtualizacao.put("cnpjEntidade",csa.getCsaCnpj());
                }

                msgAtualizacao = ApplicationResourcesHelper.getMessage("mensagem.atualizar.cadastro.informacao", responsavel, responsavel.getUsuNome(),csa.getCsaNome());

            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.atualizar.cadastro.erro", responsavel));
                model.addAttribute("retornoErro", Boolean.TRUE);
                return viewRedirect("jsp/atualizarCadastro/atualizarCadastro", request, session, model, responsavel);
            }

        } catch (ConsignanteControllerException | ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            model.addAttribute("retornoErro", Boolean.TRUE);
            return viewRedirect("jsp/atualizarCadastro/atualizarCadastro", request, session, model, responsavel);
        }

        model.addAttribute("dadosAtualizacao", dadosAtualizacao);
        model.addAttribute("msgAtualizacao", msgAtualizacao);
        model.addAttribute("exigeAtualizacaoCadastralCsaCnpj", exigeAtualizacaoCadastralCsaCnpj);
        model.addAttribute("retornoErro", Boolean.FALSE);
        return viewRedirect("jsp/atualizarCadastro/atualizarCadastro", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/atualizarCadastro" }, params = { "acao=editar" })
    public String atualizarCadastro(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        String codigoEntidade = JspHelper.verificaVarQryStr(request, "codigoEntidade");

        String nomeResponsavel = JspHelper.verificaVarQryStr(request, "NOME_RESPONSAVEL");
        String telResponsavel = JspHelper.verificaVarQryStr(request, "TEL_RESP");
        String emailResponsavel = JspHelper.verificaVarQryStr(request, "RESP_EMAIL");
        String cseSistemFolha = JspHelper.verificaVarQryStr(request, "CSE_SISTEMA_FOLHA");
        String nomeEntidade = JspHelper.verificaVarQryStr(request, "nomeEntidade");
        String cnpjEntidade = JspHelper.verificaVarQryStr(request, "CNPJ_ENTIDADE");

        if (TextHelper.isNull(nomeResponsavel) || TextHelper.isNull(telResponsavel) || TextHelper.isNull(emailResponsavel) || (TextHelper.isNull(cnpjEntidade) && ParamSist.paramEquals(CodedValues.TPC_EXIGE_ATUALIZACAO_CADASTRAL_CSA_CNPJ, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.atualizar.cadastro.erro.vazio", responsavel));
            model.addAttribute("retornoErro", Boolean.TRUE);
            model.addAttribute("exigeAtualizacaoCadastralCsaCnpj", Boolean.FALSE);
            return viewRedirect("jsp/atualizarCadastro/atualizarCadastro", request, session, model, responsavel);
        }

        try {

            if (responsavel.isCse() && responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE)) {

                List<String> cseBancos = new ArrayList<>(Arrays.asList(request.getParameterValues("BCO_CODIGO")));

                ConsignanteTransferObject consignante = new ConsignanteTransferObject(codigoEntidade);
                consignante.setCseResponsavel(nomeResponsavel);
                consignante.setCseRespTelefone(telResponsavel);
                consignante.setCseEmail(emailResponsavel);
                consignante.setCseSistemaFolha(cseSistemFolha);
                consignante.setCseDataAtualizacaoCadastral(DateHelper.getSystemDatetime());

                if(cseBancos != null && cseBancos.size() > 0) {
                    cseBancos.removeAll(Arrays.asList(""));
                    consignanteController.setBancosCse(cseBancos);
                }

                boolean camposObrigatoriosOk = validaCamposObrigatorios(nomeResponsavel, telResponsavel, emailResponsavel, cseSistemFolha, cseBancos, responsavel);

                if(camposObrigatoriosOk) {
                    consignanteController.updateConsignante(consignante, responsavel);
                    consignanteController.createOcorrenciaCse(CodedValues.TOC_ALTERACAO_DADOS_CADASTRAIS_ENTIDADE, ApplicationResourcesHelper.getMessage("mensagem.atualizar.cadastro.ocorrencia",responsavel), responsavel);
                }else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

            } else if (responsavel.isCsa() && responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIA)) {

                final ConsignatariaTransferObject consignataria = new ConsignatariaTransferObject(codigoEntidade);
                consignataria.setCsaResponsavel(nomeResponsavel);
                consignataria.setCsaRespTelefone(telResponsavel);
                consignataria.setCsaEmail(emailResponsavel);
                consignataria.setCsaDataAtualizacaoCadastral(DateHelper.getSystemDatetime());
                if (!TextHelper.isNull(cnpjEntidade) && ParamSist.paramEquals(CodedValues.TPC_EXIGE_ATUALIZACAO_CADASTRAL_CSA_CNPJ, CodedValues.TPC_SIM, responsavel)) {
                    consignataria.setCsaCnpj(cnpjEntidade);
                }

                final StringBuilder atributosAlterados = new StringBuilder();
                final StringBuilder valorAtributosAlterados = new StringBuilder();
                try {
                    final ConsignatariaTransferObject consignatariaCache = consignatariaController.findConsignataria(codigoEntidade, responsavel);
                    final LogDelegate log = new LogDelegate(responsavel, Log.CONSIGNATARIA, Log.UPDATE, Log.LOG_INFORMACAO);
                    final CustomTransferObject merge = log.getUpdatedFields(consignataria.getAtributos(), consignatariaCache.getAtributos());

                    adicionarAtributoAlterado(merge, Columns.CSA_RESPONSAVEL, atributosAlterados, valorAtributosAlterados,
                                              ApplicationResourcesHelper.getMessage("rotulo.atualizar.cadastro.responsavel", responsavel), nomeResponsavel);

                    adicionarAtributoAlterado(merge, Columns.CSA_RESP_TELEFONE, atributosAlterados, valorAtributosAlterados,
                                              ApplicationResourcesHelper.getMessage("rotulo.atualizar.cadastro.telefone", responsavel), telResponsavel);

                    adicionarAtributoAlterado(merge, Columns.CSA_EMAIL, atributosAlterados, valorAtributosAlterados,
                                              ApplicationResourcesHelper.getMessage("rotulo.atualizar.cadastro.email", responsavel), emailResponsavel);

                    if (!TextHelper.isNull(cnpjEntidade) && ParamSist.paramEquals(CodedValues.TPC_EXIGE_ATUALIZACAO_CADASTRAL_CSA_CNPJ, CodedValues.TPC_SIM, responsavel)) {
                        adicionarAtributoAlterado(merge, Columns.CSA_CNPJ, atributosAlterados, valorAtributosAlterados,
                                                  ApplicationResourcesHelper.getMessage("rotulo.atualizar.cadastro.cnpj", responsavel), cnpjEntidade);
                    }
                } catch (LogControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                if (!atributosAlterados.isEmpty()) {
                    consignatariaController.updateConsignataria(consignataria, responsavel);
                    final String[] dadosAlterados = atributosAlterados.toString().split(",");
                    final String mensagemKey = (dadosAlterados.length == 1) ? "mensagem.atualizar.cadastro.ocorrencia.csa.singular" : "mensagem.atualizar.cadastro.ocorrencia.csa.plural";
                    final String tocObs = ApplicationResourcesHelper.getMessage(mensagemKey, responsavel, atributosAlterados.toString(), valorAtributosAlterados.toString());
                    consignatariaController.criarOcorrenciaAtualizarDados(codigoEntidade, tocObs, responsavel);
                } else {
                    final ConsignatariaTransferObject consignataria2 = new ConsignatariaTransferObject(codigoEntidade);
                    consignataria2.setCsaDataAtualizacaoCadastral(DateHelper.getSystemDatetime());

                    // Atualiza pelo menos a data do cadastro, evitando solicitar novamente a atualização
                    consignatariaController.updateConsignataria(consignataria2, responsavel);
                }
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.atualizar.cadastro.erro", responsavel));
                model.addAttribute("retornoErro", Boolean.TRUE);
                return viewRedirect("jsp/atualizarCadastro/atualizarCadastro", request, session, model, responsavel);
            }

        } catch (ConsignanteControllerException | ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            model.addAttribute("retornoErro", Boolean.TRUE);
            return viewRedirect("jsp/atualizarCadastro/atualizarCadastro", request, session, model, responsavel);
        }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.atualizar.cadastro.sucesso", responsavel, nomeEntidade));
        session.removeAttribute("exigeAtualizacaoCadastral");

        return "redirect:../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";
    }

    private boolean validaCamposObrigatorios(String nomeResponsavel, String telResponsavel, String emailResponsavel, String cseSistemFolha, List<String> cseBancos, AcessoSistema responsavel) {
        boolean camposObrigatoriosOk = true;
        try {
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_RESPONSAVEL, responsavel) && (TextHelper.isNull(nomeResponsavel) || nomeResponsavel.isEmpty())) {
                camposObrigatoriosOk = false;
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_RESP_TELEFONE, responsavel) && (TextHelper.isNull(telResponsavel) || telResponsavel.isEmpty())) {
                camposObrigatoriosOk = false;
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_EMAIL, responsavel) && (TextHelper.isNull(emailResponsavel) || emailResponsavel.isEmpty())) {
                camposObrigatoriosOk = false;
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_CSE_SISTEMA_FOLHA, responsavel) && (TextHelper.isNull(cseSistemFolha) || cseSistemFolha.isEmpty())) {
                camposObrigatoriosOk = false;
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZA_CADASTRO_BCO_FOLHA, responsavel) && (cseBancos == null || cseBancos.isEmpty())) {
                camposObrigatoriosOk = false;
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return camposObrigatoriosOk;
    }

    private void adicionarAtributoAlterado(CustomTransferObject merge, String coluna, StringBuilder atributosAlterados, StringBuilder valorAtributosAlterados, String label, String novoValor) {
       if (merge.getAtributos().containsKey(coluna)) {
           if (atributosAlterados.length() > 0) {
               atributosAlterados.append(", ");
               valorAtributosAlterados.append(", ");
           }
           atributosAlterados.append(label);
           valorAtributosAlterados.append("'").append(novoValor).append("'");
       }
   }
}
