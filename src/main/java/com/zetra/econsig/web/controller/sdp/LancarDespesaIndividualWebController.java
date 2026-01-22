package com.zetra.econsig.web.controller.sdp;

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
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PermissionarioControllerException;
import com.zetra.econsig.exception.PlanoDescontoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sdp.DespesaIndividualController;
import com.zetra.econsig.service.sdp.PermissionarioController;
import com.zetra.econsig.service.sdp.PlanoDescontoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaPlanoEnum;
import com.zetra.econsig.web.controller.consignacao.AbstractIncluirConsignacaoWebController;

/**
 * <p>Title: LancarDespesaIndividualWebController</p>
 * <p>Description: Controlador Web base para o casos de uso de lançar despesa individual.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/lancarDespesaIndividual" })
public class LancarDespesaIndividualWebController extends AbstractIncluirConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LancarDespesaIndividualWebController.class);

    @Autowired
    private DespesaIndividualController despesaIndividualController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PermissionarioController permissionarioController;

    @Autowired
    private PlanoDescontoController planoDescontoController;

    @Override
    protected String getFunCodigo() {
        return CodedValues.FUN_INC_DESPESA_INDIVIDUAL;
    }

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Carregr a lista de planos de desconto
        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
            criterio.setAttribute(Columns.PLA_ATIVO, CodedValues.STS_ATIVO);
            criterio.setAttribute(Columns.NPL_CODIGO, false); // evita listar planos de taxa de uso
            List<TransferObject> lstPlano = planoDescontoController.lstPlanoDescontoSemRateio(criterio, -1, -1, responsavel);
            model.addAttribute("lstPlano", lstPlano);
        } catch (PlanoDescontoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected String validarServicoOperacao(String svcCodigo, String rseCodigo, Map<String, String> parametrosPlano, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (parametrosPlano == null) {
                parametrosPlano = new HashMap<>();
            }

            String prmCodigo = JspHelper.verificaVarQryStr(request, "PRM_CODIGO");
            if (TextHelper.isNull(prmCodigo)) {
                TransferObject permissionario = permissionarioController.findPermissionarioAtivoByRseCodigo(rseCodigo, responsavel);
                prmCodigo = permissionario.getAttribute(Columns.PRM_CODIGO).toString();
            }
            model.addAttribute("prmCodigo", prmCodigo);

            TransferObject permissionario = permissionarioController.findPermissionario(prmCodigo, responsavel);

            // Valida se o permissionário foi encontrado
            if (permissionario == null) {
                throw new ViewHelperException("mensagem.erro.uso.incorreto.permissionario.nao.encontrado", responsavel);
            }

            // Valida se o registro servidor passado corresponde ao permissionario
            if (!permissionario.getAttribute(Columns.RSE_CODIGO).toString().equals(rseCodigo)) {
                throw new ViewHelperException("mensagem.erro.uso.incorreto.permissionario.servidor.invalido", responsavel);
            }

            model.addAttribute("permissionario", permissionario);

            String plaCodigo = request.getParameter("PLA_CODIGO");
            if (TextHelper.isNull(plaCodigo)) {
                throw new ViewHelperException("mensagem.erro.uso.incorreto.plano.desconto.nao.encontrado", responsavel);
            }
            model.addAttribute("plaCodigo", plaCodigo);

            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.PLA_CODIGO, plaCodigo);

            TransferObject plano = planoDescontoController.buscaPlanoDesconto(criterio, responsavel);
            String svcCodigoPlano = plano.getAttribute(Columns.SVC_CODIGO).toString();
            // verifica se a natureza do plano é taxa de uso
            if (plano.getAttribute(Columns.NPL_CODIGO).equals(NaturezaPlanoEnum.TAXA_USO.getCodigo())) {
                throw new ViewHelperException("mensagem.erro.plano.taxa.uso.invalido.despesa.individual", responsavel);
            }

            String plaDescricao = (plano != null && !TextHelper.isNull(plano.getAttribute(Columns.PLA_DESCRICAO))) ? plano.getAttribute(Columns.PLA_DESCRICAO).toString() : "";
            model.addAttribute("plaDescricao", plaDescricao);

            parametrosPlano.putAll(carregarParametrosPlano(plaCodigo, responsavel));
            if (parametrosPlano != null && !TextHelper.isNull(parametrosPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO)) && !parametrosPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_SEM_RATEIO)) {
                throw new ViewHelperException("mensagem.erro.plano.rateio.invalido.despesa.individual", responsavel);
            }

            // Verifica se o serviço informado é o mesmo do plano
            if (!TextHelper.isNull(svcCodigo) && !svcCodigo.equals(svcCodigoPlano)) {
                throw new ViewHelperException("mensagem.erro.servico.invalido.plano.despesa", responsavel);
            }

            return svcCodigoPlano;
        } catch (PermissionarioControllerException ex) {
            throw new ViewHelperException("mensagem.erro.uso.incorreto.permissionario.nao.encontrado", responsavel);
        } catch (PlanoDescontoControllerException ex) {
            throw new ViewHelperException("mensagem.erro.plano.nao.encontrado.despesa", responsavel);
        }
    }

    @Override
    protected String incluirReservaMargem(ReservarMargemParametros rmParam, HttpServletRequest request, HttpSession session, AcessoSistema responsavel) throws ViewHelperException {
        try {
            String plaCodigo = JspHelper.verificaVarQryStr(request, "PLA_CODIGO");
            String prmCodigo = JspHelper.verificaVarQryStr(request, "PRM_CODIGO");
            String decCodigo = JspHelper.verificaVarQryStr(request, "DEC_CODIGO");

            // Valida o plano
            if (!TextHelper.isNull(plaCodigo)) {
                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.PLA_CODIGO, plaCodigo);
                try {
                    planoDescontoController.buscaPlanoDesconto(criterio, responsavel);
                } catch (PlanoDescontoControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ViewHelperException("mensagem.erro.plano.nao.encontrado.despesa", responsavel);
                }
            }

            // Lista dos parâmetros de plano necessários
            Map<String, String> parametrosPlano = carregarParametrosPlano(plaCodigo, responsavel);
            if (parametrosPlano != null && !TextHelper.isNull(parametrosPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO)) && !parametrosPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_SEM_RATEIO)) {
                throw new ViewHelperException("mensagem.erro.plano.rateio.invalido.despesa.individual", responsavel);
            }

            TransferObject despesaIndividual = new CustomTransferObject();
            despesaIndividual.setAttribute(Columns.DEI_PLA_CODIGO, plaCodigo);
            despesaIndividual.setAttribute(Columns.DEI_PRM_CODIGO, prmCodigo);
            despesaIndividual.setAttribute(Columns.DEI_DEC_CODIGO, decCodigo);

            return despesaIndividualController.createDespesaIndividual(despesaIndividual, rmParam, responsavel);
        } catch (DespesaIndividualControllerException ex) {
            throw new ViewHelperException(ex);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.incluir.despesa.individual.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/lancarDespesaIndividual");
        model.addAttribute("tipoOperacao", "despesa_individual");
    }

    private Map<String, String> carregarParametrosPlano(String plaCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            List<TransferObject> lstParamPlano = parametroController.selectParamPlano(plaCodigo, responsavel);

            Map<String, String> parametrosPlano = new HashMap<>();
            for (TransferObject ppl : lstParamPlano) {
                parametrosPlano.put((String) ppl.getAttribute(Columns.TPP_CODIGO), (String) ppl.getAttribute(Columns.PPL_VALOR));
            }

            return parametrosPlano;
        } catch (ParametroControllerException ex) {
            throw new ViewHelperException("mensagem.erro.carregar.parametros.plano", responsavel, ex);
        }
    }
}
