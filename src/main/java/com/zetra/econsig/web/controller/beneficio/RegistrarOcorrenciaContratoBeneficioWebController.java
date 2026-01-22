package com.zetra.econsig.web.controller.beneficio;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.web.RegistrarOcorrenciaDTO;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: RegistrarOcorrenciaContratoBeneficioWebController</p>
 * <p>Description:Registrar ocorrencia de contrato de beneficios (Editar, novo e excluir)</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date: 2018-09-17 16:36:10 -0300 (Seg, 17 set 2018) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/registrarOcorrenciaContratoBeneficio" })
public class RegistrarOcorrenciaContratoBeneficioWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegistrarOcorrenciaContratoBeneficioWebController.class);

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            final String benCodigo = request.getParameter(Columns.getColumnName(Columns.BEN_CODIGO));
            final String rseCodigo = request.getParameter(Columns.getColumnName(Columns.RSE_CODIGO));
            final String bfcCodigo = request.getParameter(Columns.getColumnName(Columns.BFC_CODIGO));
            final String cbeCodigo = request.getParameter(Columns.getColumnName(Columns.CBE_CODIGO));

            final Beneficiario beneficiario = beneficiarioController.findBeneficiarioByCodigo(bfcCodigo, responsavel);

            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            final String serNome = (String) servidor.getAttribute(Columns.SER_NOME);

            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.BEN_CODIGO, benCodigo);
            final List<TransferObject> listaBeneficio = beneficioController.listaBeneficio(criterio, responsavel);
            TransferObject beneficio = null;
            if (listaBeneficio != null && !listaBeneficio.isEmpty()) {
                beneficio = listaBeneficio.get(0);
            }

            // Recupera todos os tipos ocorrencias
            final List<TransferObject> motivoOperacaoLst = tipoMotivoOperacaoController.lstMotivoOperacao(Arrays.asList(Log.GERAL, Log.CONTRATO_BENEFICIO), Short.valueOf("1"), responsavel);
            final ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria((String) beneficio.getAttribute(Columns.BEN_CSA_CODIGO), responsavel);

            final RegistrarOcorrenciaDTO registrarOcorrenciaDTO = new RegistrarOcorrenciaDTO();
            registrarOcorrenciaDTO.setCbeCodigo(cbeCodigo);
            registrarOcorrenciaDTO.setBfcNome(beneficiario.getBfcNome());
            registrarOcorrenciaDTO.setSerNome(serNome);
            registrarOcorrenciaDTO.setBenCodigo(benCodigo);
            registrarOcorrenciaDTO.setBenDescricao((String) beneficio.getAttribute(Columns.BEN_DESCRICAO));
            registrarOcorrenciaDTO.setCsaNome((String) consignataria.getAttribute(Columns.CSA_NOME));
            registrarOcorrenciaDTO.setBenCodigoContrato((String) beneficio.getAttribute(Columns.BEN_CODIGO_CONTRATO));
            registrarOcorrenciaDTO.setBenCodigoRegistro((String) beneficio.getAttribute(Columns.BEN_CODIGO_REGISTRO));

            model.addAttribute("registrarOcorrenciaDTO", registrarOcorrenciaDTO);
            model.addAttribute("motivoOperacaoLst", motivoOperacaoLst);

            return viewRedirect("jsp/registrarOcorrenciaContratoBeneficio/registrarOcorrenciaContratoBeneficio", request, session, model, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, RegistrarOcorrenciaDTO dto) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {

            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            if (TextHelper.isNull(dto.getTmoCodigo())) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.registrar.ocorrencia.contrato.motivo.operacao.obrigatorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (TextHelper.isNull(dto.getOcbObs())) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.registrar.ocorrencia.contrato.observacao.obrigatorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final ParamSession paramSession = ParamSession.getParamSession(session);

            contratoBeneficioController.criaOcorrenciaContratoBeneficio(dto.getCbeCodigo(), CodedValues.TOC_RETIFICACAO_MOTIVO_OPERACAO, dto.getOcbObs(), new Date(), dto.getTmoCodigo(), responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.registrar.ocorrencia.contrato.gravado.sucesso", responsavel));

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
