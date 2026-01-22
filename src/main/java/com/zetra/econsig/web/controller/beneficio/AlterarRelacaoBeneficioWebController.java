package com.zetra.econsig.web.controller.beneficio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
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
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.persistence.entity.ContratoBeneficio;
import com.zetra.econsig.persistence.entity.EnderecoServidor;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.persistence.entity.StatusBeneficiario;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.AcaoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.StatusBeneficiarioEnum;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.values.TipoEnderecoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: AlterarRelacaoBeneficioWebController</p>
 * <p>Description: Alterar situação do contrato benefício.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: marcos.nolasco $
 * $Revision: 25669 $
 * $Date: 2020-02-12 14:30:43 -0200 (Qua, 12 fev 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/aprovarSolicitacao" })
public class AlterarRelacaoBeneficioWebController extends AbstractWebController {

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=aprovar" })
    public String aprovar(@RequestParam(value = "bfcCodigo", required = false) String bfcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String serCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO));
            boolean temEndereco = false;

            //DESENV-14557 - Necessário verificar se existe endereço deste servidor do tipo cobrança
            List<EnderecoServidor> enderecosServidor = servidorController.listEnderecoServidorByCodigo(serCodigo, responsavel);

            if (enderecosServidor != null && !enderecosServidor.isEmpty()) {
                for (EnderecoServidor endServidor : enderecosServidor) {
                    String tieCodigo = endServidor.getTipoEndereco().getTieCodigo();
                    temEndereco = tieCodigo.equals(TipoEnderecoEnum.COBRANCA.getCodigo());
                    if (temEndereco) {
                        break;
                    }
                }
            }

            if (!temEndereco) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relacao.beneficio.aprovar.falta.endereco.servidor", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String ocbObservacao = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.OCB_OBS));

            model.addAttribute("operacao", "aprovar");
            model.addAttribute("beneficiario", new Beneficiario());
            model.addAttribute("ocbObservacao", ocbObservacao);
            model.addAttribute("bfcCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO)));
            model.addAttribute("cbeCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_CODIGO)));
            model.addAttribute("serCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute("tibCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute("aprovarTodos", false);
            model.addAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute(Columns.SCB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SCB_CODIGO)));
            model.addAttribute(Columns.TIB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
            model.addAttribute(Columns.BEN_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO)));
            model.addAttribute("contratosAtivos", JspHelper.verificaVarQryStr(request, "contratosAtivos"));

            return viewRedirect("jsp/manterBeneficio/aprovarSolicitacao", request, session, model, responsavel);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=rejeitar" })
    public String rejeitar(@RequestParam(value = "bfcCodigo", required = false)  String bfcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String tibCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO));

            String tenCodigo = tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) ? Log.CONTRATO_BENEFICIO_TITULAR : (tibCodigo.equals(TipoBeneficiarioEnum.DEPENDENTE.tibCodigo) ? Log.CONTRATO_BENEFICIO_DEPENDENTE : Log.CONTRATO_BENEFICIO_AGREGADO);

            List<TransferObject> lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacao(Arrays.asList(Log.GERAL, Log.CONTRATO_BENEFICIO, tenCodigo), Short.valueOf("1"), responsavel);
            model.addAttribute("lstMtvOperacao", lstMtvOperacao);

            String tmoCodigo = JspHelper.verificaVarQryStr(request, Columns.TMO_CODIGO);
            String ocbObservacao = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.OCB_OBS));

            model.addAttribute("operacao", "rejeitar");
            model.addAttribute("beneficiario", new Beneficiario());
            model.addAttribute("tmoCodigo", tmoCodigo);
            model.addAttribute("ocbObservacao", ocbObservacao);
            model.addAttribute("bfcCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO)));
            model.addAttribute("cbeCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_CODIGO)));
            model.addAttribute("serCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute("tibCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute(Columns.SCB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SCB_CODIGO)));
            model.addAttribute(Columns.TIB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
            model.addAttribute(Columns.BEN_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO)));
            model.addAttribute("contratosAtivos", JspHelper.verificaVarQryStr(request, "contratosAtivos"));

            return viewRedirect("jsp/manterBeneficio/aprovarSolicitacao", request, session, model, responsavel);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=cancelar" })
    public String cancelar(@RequestParam(value = "bfcCodigo", required = false) String bfcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String tibCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO));

            String tenCodigo = tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) ? Log.CONTRATO_BENEFICIO_TITULAR : (tibCodigo.equals(TipoBeneficiarioEnum.DEPENDENTE.tibCodigo) ? Log.CONTRATO_BENEFICIO_DEPENDENTE : Log.CONTRATO_BENEFICIO_AGREGADO);

            List<TransferObject> lstMtvOperacao = new ArrayList<>();

            if (responsavel.isSer()) {
                lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoAcao(Arrays.asList(Log.GERAL, Log.CONTRATO_BENEFICIO, tenCodigo), Short.valueOf("1"), AcaoEnum.CANCELAMENTO_BENEFICIO_PELO_SERVIDOR.getCodigo(), responsavel);
            }

            if (lstMtvOperacao.isEmpty() || lstMtvOperacao == null || lstMtvOperacao.size() == 0) {
                lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacao(Arrays.asList(Log.GERAL, Log.CONTRATO_BENEFICIO,tenCodigo), Short.valueOf("1"), responsavel);
            }

            model.addAttribute("lstMtvOperacao", lstMtvOperacao);

            String tmoCodigo = JspHelper.verificaVarQryStr(request, Columns.TMO_CODIGO);
            String ocbObservacao = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.OCB_OBS));
            String bfcSubsidioConcedido = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_SUBSIDIO_CONCEDIDO));
            boolean solicitacaoCancelamento = JspHelper.verificaVarQryStr(request, "solicitacao").equals("true");

            if (bfcSubsidioConcedido.equals(CodedValues.TPC_SIM)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.beneficiario.cadastrado.como.excecao", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            model.addAttribute("operacao", "cancelar");
            model.addAttribute("serCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute("ocbObservacao", ocbObservacao);
            model.addAttribute("tmoCodigo", tmoCodigo);
            model.addAttribute("bfcCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO)));
            model.addAttribute("cbeCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_CODIGO)));
            model.addAttribute("tibCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute("tdaMesesContruicao", JspHelper.verificaVarQryStr(request, CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO));
            model.addAttribute("adesaoPlanoExFuncionarios", JspHelper.verificaVarQryStr(request, "adesaoPlanoExFuncionarios"));
            model.addAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute(Columns.SCB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SCB_CODIGO)));
            model.addAttribute(Columns.TIB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
            model.addAttribute(Columns.BEN_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO)));
            model.addAttribute("contratosAtivos", JspHelper.verificaVarQryStr(request, "contratosAtivos"));
            if (solicitacaoCancelamento) {
                model.addAttribute("solicitacaoCancelamento", solicitacaoCancelamento);
            }

            return viewRedirect("jsp/manterBeneficio/aprovarSolicitacao", request, session, model, responsavel);

        } catch (TipoMotivoOperacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            NaturezaServico nse = new NaturezaServico();
            nse.setNseCodigo(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.NSE_CODIGO)));

            String serCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO));
            String cbeCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_CODIGO));
            String tmoCodigo = JspHelper.verificaVarQryStr(request, "tmoCodigo");
            String ocbObs = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.OCB_OBS));
            String bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
            String bfcDataObitoStr = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_DATA_OBITO));
            Date bfcDataObito = null;

            if (!TextHelper.isNull(bfcDataObitoStr)) {
                bfcDataObito = DateHelper.parse(bfcDataObitoStr, "dd/MM/yyyy");
            }


            boolean solicitacaoCancelamento = JspHelper.verificaVarQryStr(request, "solicitacaoCancelamento").equals("true");

            ContratoBeneficio contratoBeneficio = contratoBeneficioController.findByPrimaryKey(cbeCodigo, responsavel);

            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.SER_CODIGO, serCodigo);
            criterio.setAttribute(Columns.BEN_CODIGO, contratoBeneficio.getBeneficio().getBenCodigo());

            /**
             * Alteração para retornar apenas contratos ativos para realizar a operação.
             * Futuramente deve ser refatorado para permitir que seja passado o status dos contratos
             * que devem ser retornados para cada operação.
             */
            criterio.setAttribute("contratosAtivos", "true");

            // Recupera todo o grupo familiar para verificar se é o beneficiário titular e se o titular possui contrato ativo
            List<TransferObject> beneficiarios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

            boolean titularAtivo = false;
            boolean beneficiarioTitular = false;

            for (TransferObject beneficiario : beneficiarios) {
                String tibCodigo = beneficiario.getAttribute(Columns.TIB_CODIGO).toString();
                String scbCodigo = beneficiario.getAttribute(Columns.SCB_CODIGO).toString();
                String sadCodigo = beneficiario.getAttribute(Columns.SAD_CODIGO).toString();
                String bfc_codigo = beneficiario.getAttribute(Columns.BFC_CODIGO).toString();

                if (tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && (scbCodigo.equals(StatusContratoBeneficioEnum.ATIVO.getCodigo()) || sadCodigo.equals(CodedValues.SAD_AGUARD_CONF))) {
                    titularAtivo = true;
                }

                if (tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && bfc_codigo.equals(bfcCodigo)) {
                    beneficiarioTitular = true;
                }
            }

            String tdaAdesao = JspHelper.verificaVarQryStr(request, "adesaoPlanoExFuncionarios");
            String tdaMesesContruicao = JspHelper.verificaVarQryStr(request, CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO);
            String operacao = JspHelper.verificaVarQryStr(request, "operacao");
            String apTodos = JspHelper.verificaVarQryStr(request, "aprovarTodos");

            boolean aprovar = operacao.equals("aprovar");
            boolean rejeitar = operacao.equals("rejeitar");
            boolean cancelar = operacao.equals("cancelar");
            boolean aprovarTodos = apTodos.equals("true");
            boolean desfazerCancelamento = operacao.equals("desfazerCancelamento");

            // Caso não seja aprovação de todos, recupera somente do beneficiário informado
            if (!aprovarTodos) {
                criterio.setAttribute(Columns.BFC_CODIGO, bfcCodigo);
            }
            // Recupera os contratos do beneficiário caso não seja
            beneficiarios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

            Iterator<TransferObject> it = beneficiarios.iterator();

            String tibCodigo = "-1";

            if (it.hasNext()) {
                TransferObject to = it.next();
                tibCodigo = (String) to.getAttribute(Columns.TIB_CODIGO);
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            /**
             * Caso seja o titular que está realizado a operação, recupera todos os beneficiários
             * quando é cancelamento, rejeição , aprovação de todos ou desfazer cancelamento.
             */
            if (tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && aprovar && aprovarTodos) {
                criterio.remove(Columns.BFC_CODIGO);
                beneficiarios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);
            } else if (tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && rejeitar) {
                criterio.remove(Columns.BFC_CODIGO);
                beneficiarios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);
            } else if (tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && cancelar) {
                criterio.remove(Columns.BFC_CODIGO);
                beneficiarios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);
            } else if (tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && desfazerCancelamento) {
                criterio.remove(Columns.BFC_CODIGO);
                beneficiarios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);
            }

            //Caso o usuário esteja tentando desfazer um cancelamento essa ação só pode ser feito quando o titular está com o status ativo
            if (desfazerCancelamento && !titularAtivo && !tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.beneficios.cancelado.desfeito", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Se não é aprovação de contrato de dependente, verificar se o contrato do titular foi aprovado anteriormente
            if (!titularAtivo && !beneficiarioTitular && aprovar) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.relacao.beneficios.aprovar.dependente.titular.inativo", responsavel));
            } else {
                if (aprovar || aprovarTodos) {
                    String validacao = validaCamposObrigatoriosBeneficiario(beneficiarios, request, session, model, responsavel);
                    if (TextHelper.isNull(validacao)) {
                        contratoBeneficioController.aprovar(beneficiarios, tmoCodigo, ocbObs, responsavel);
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.relacao.beneficios.aprovado.sucesso", responsavel));
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.beneficiario.aprovar.beneficio", responsavel) + validacao);
                    }
                } else if (rejeitar) {
                    contratoBeneficioController.rejeitar(beneficiarios, tmoCodigo, ocbObs, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.relacao.beneficios.rejeitado.sucesso", responsavel));
                } else if (cancelar) {
                    //DESENV-14555 - Quando é cancelamento por óbito, precisamos atualizar a data de óbito do beneficiario cancelado e
                    // cancelar os contratos ativos não somente este e quando é titular é cancelado todos os contratos.
                    if (bfcDataObito != null) {
                        beneficiarios = beneficioController.findRelacaoBeneficioObitoDependente(criterio, responsavel);
                        contratoBeneficioController.cancelar(beneficiarios, tmoCodigo, ocbObs, tdaAdesao, tdaMesesContruicao, solicitacaoCancelamento, responsavel);
                        modificaStatusBeneficiarioExcluido(bfcCodigo, bfcDataObito, responsavel);
                    } else {
                        contratoBeneficioController.cancelar(beneficiarios, tmoCodigo, ocbObs, tdaAdesao, tdaMesesContruicao, solicitacaoCancelamento, responsavel);
                    }

                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.relacao.beneficios.cancelado.sucesso", responsavel));
                } else if (desfazerCancelamento) {
                    contratoBeneficioController.desfazerCancelamento(beneficiarios, tmoCodigo, ocbObs, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.relacao.beneficios.cancelado.desfeito", responsavel));
                }
            }

            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            // Recupera todos os beneficiários para serem listados na interface
            criterio.remove(Columns.BFC_CODIGO);
            List<TransferObject> relacaoBeneficios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

            // Se não encontrou nenhum beneficiário é porque todos os contratos do grupo familiar foram cancelados
            if (relacaoBeneficios == null || relacaoBeneficios.isEmpty()) {
                criterio.setAttribute("contratosAtivos", "false");
                relacaoBeneficios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);
                model.addAttribute("contratosAtivos", "false");
            } else {
                model.addAttribute("contratosAtivos", JspHelper.verificaVarQryStr(request, "contratosAtivos"));
            }

            boolean permiteCancelarBeneficioSemAprovacao = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CANCELAR_BENEFICIO_SEM_APROVACAO, CodedValues.TPC_SIM, responsavel);

            model.addAttribute("relacaoBeneficios", relacaoBeneficios);
            model.addAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute(Columns.SCB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SCB_CODIGO)));
            model.addAttribute(Columns.TIB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
            model.addAttribute(Columns.BEN_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO)));
            model.addAttribute("permiteCancelarBeneficioSemAprovacao", permiteCancelarBeneficioSemAprovacao);

            return viewRedirect("jsp/manterBeneficio/consultarRelacaoBeneficios", request, session, model, responsavel);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=solicitarCancelamento" })
    public String solicitarCancelamento(@RequestParam(value = "bfcCodigo", required = false) String bfcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return cancelar(bfcCodigo, request, response, session, model);

    }

    @RequestMapping(params = { "acao=desfazerCancelamento" })
    public String desfazerCancelamento(@RequestParam(value = "bfcCodigo", required = false) String bfcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String tibCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO));

            String tenCodigo = tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) ? Log.CONTRATO_BENEFICIO_TITULAR : (tibCodigo.equals(TipoBeneficiarioEnum.DEPENDENTE.tibCodigo) ? Log.CONTRATO_BENEFICIO_DEPENDENTE : Log.CONTRATO_BENEFICIO_AGREGADO);

            List<TransferObject> lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacao(Arrays.asList(Log.GERAL, Log.CONTRATO_BENEFICIO, tenCodigo), Short.valueOf("1"), responsavel);
            model.addAttribute("lstMtvOperacao", lstMtvOperacao);

            String tmoCodigo = JspHelper.verificaVarQryStr(request, Columns.TMO_CODIGO);
            String ocbObservacao = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.OCB_OBS));

            model.addAttribute("operacao", "desfazerCancelamento");
            model.addAttribute("beneficiario", new Beneficiario());
            model.addAttribute("tmoCodigo", tmoCodigo);
            model.addAttribute("ocbObservacao", ocbObservacao);
            model.addAttribute("bfcCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO)));
            model.addAttribute("cbeCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_CODIGO)));
            model.addAttribute("serCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute("tibCodigo", JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute(Columns.SCB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SCB_CODIGO)));
            model.addAttribute(Columns.TIB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
            model.addAttribute(Columns.BEN_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO)));
            model.addAttribute("contratosAtivos", JspHelper.verificaVarQryStr(request, "contratosAtivos"));

            return viewRedirect("jsp/manterBeneficio/aprovarSolicitacao", request, session, model, responsavel);

        } catch (TipoMotivoOperacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/exibeObito" })
    @ResponseBody
    private ResponseEntity<String> exibeObito(@RequestParam(value = "tmoCodigo", required = true) String tmoCodigo, HttpServletRequest request) {

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String acaCodigo = "";

        List<TransferObject> lstMtvOperacao = new ArrayList<>();
        try {

            lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacao(tmoCodigo, responsavel);
        if (lstMtvOperacao !=null && !lstMtvOperacao.isEmpty()) {
            for (TransferObject motivo : lstMtvOperacao) {
                acaCodigo = motivo.getAttribute(Columns.TMO_ACA_CODIGO) != null && motivo.getAttribute(Columns.TMO_ACA_CODIGO).toString().equals(AcaoEnum.CANCELAMENTO_BENEFICIO_OBITO.getCodigo()) ? motivo.getAttribute(Columns.TMO_ACA_CODIGO).toString() : "";
            }
        }

        } catch (TipoMotivoOperacaoControllerException ex) {
            return new ResponseEntity<>(acaCodigo, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(acaCodigo, HttpStatus.OK);
    }

    private void modificaStatusBeneficiarioExcluido(String bfcCodigo, Date bfcDataObito, AcessoSistema responsavel) throws BeneficioControllerException {

        StatusBeneficiario statusBeneficiario = new StatusBeneficiario();
        statusBeneficiario.setSbeCodigo(StatusBeneficiarioEnum.INATIVO.sbeCodigo);

        Beneficiario bfc = beneficiarioController.findBeneficiarioByCodigo(bfcCodigo, responsavel);
        bfc.setStatusBeneficiario(statusBeneficiario);
        bfc.setBfcDataObito(bfcDataObito);

        beneficiarioController.update(bfc, responsavel);
    }

    private String validaCamposObrigatoriosBeneficiario (List<TransferObject> beneficiarios,HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ZetraException {

        String msgException = "";

        for (TransferObject beneficiario : beneficiarios) {
            String msg = "";
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_TIPO, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.TIB_CODIGO))) {
                msg +=" -" + ApplicationResourcesHelper.getMessage("rotulo.beneficiario.tipo.beneficiario", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_NOME, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_NOME))) {
                msg +=" -" +  ApplicationResourcesHelper.getMessage("rotulo.beneficiario.nome", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_CPF, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_CPF))) {
                msg +=" -" +  ApplicationResourcesHelper.getMessage("rotulo.beneficiario.cpf", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_SEXO, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_SEXO))) {
                msg +=" -" +  ApplicationResourcesHelper.getMessage("rotulo.beneficiario.sexo", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_TELEFONE, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_TELEFONE))) {
                msg +=" -" +  ApplicationResourcesHelper.getMessage("rotulo.beneficiario.telefone", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_CELULAR, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_CELULAR))) {
                msg +=" -" +  ApplicationResourcesHelper.getMessage("rotulo.beneficiario.celular", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_NOME_MAE, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_NOME_MAE))) {
                msg +=" -" +  ApplicationResourcesHelper.getMessage("rotulo.beneficiario.nome.mae", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_DATA_NASCIMENTO, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_DATA_NASCIMENTO))) {
                msg +=" -" +  ApplicationResourcesHelper.getMessage("rotulo.beneficiario.data.nascimento", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_NACIONALIDADE, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_NAC_CODIGO))) {
                msg +=" -" +  ApplicationResourcesHelper.getMessage("rotulo.beneficiario.nacionalidade", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_ESTADO_CIVIL, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_ESTADO_CIVIL))) {
                msg +=" -" +  ApplicationResourcesHelper.getMessage("rotulo.beneficiario.estado.civil", responsavel);
            }
            if (!beneficiario.getAttribute(Columns.TIB_CODIGO).equals(TipoBeneficiarioEnum.TITULAR.tibCodigo) && ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_BENEFICIARIO_GRAU_PARENTESCO, responsavel) && TextHelper.isNull(beneficiario.getAttribute(Columns.GRP_DESCRICAO))) {
                msg +=" -" +  ApplicationResourcesHelper.getMessage("rotulo.beneficiario.grau.paretensco", responsavel);
            }

            if (!TextHelper.isNull(msg)) {
                msgException += beneficiario.getAttribute(Columns.BFC_NOME) != null ? beneficiario.getAttribute(Columns.BFC_NOME).toString()+ ": " + msg + "<BR>" : msg + "<BR>";
            }
        }
        return msgException;
    }
}
