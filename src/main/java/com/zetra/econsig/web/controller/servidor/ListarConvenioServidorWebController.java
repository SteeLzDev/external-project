package com.zetra.econsig.web.controller.servidor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ParamCnvRseTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarConvenioServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Convênio do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarConvenioServidor" })
public class ListarConvenioServidorWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarConvenioServidorWebController.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String readOnly = "false";
            final List<Object> listaSvcBloqueaveisServidor = new ArrayList<>();

            if (responsavel.isCseSupOrg() && !responsavel.temPermissao(CodedValues.FUN_EDT_CNV_REG_SERVIDOR)) {
                readOnly = "true";
            } else if (responsavel.isSer()) {
                // Se responsavel for servidor, recupera a lista de servicos bloqueaveis por ele
                final List<TransferObject> lista = parametroController.lstParamSvcCse(CodedValues.TPS_PERMITE_SER_BLOQUEAR_SERVICO_VERBA, "1", responsavel);
                if (!lista.isEmpty()) {
                    readOnly = "false";
                    for (final TransferObject to : lista) {
                        listaSvcBloqueaveisServidor.add(to.getAttribute(Columns.PSE_SVC_CODIGO));
                    }
                }
            }

            final String rseCodigo = responsavel.isSer() ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            final String csaCodigo = responsavel.getCsaCodigo();
            final String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            final String serNomeCodificado = JspHelper.verificaVarQryStr(request, "SER_NOME");
            final String serNome = TextHelper.isNull(serNomeCodificado) ? serNomeCodificado : TextHelper.decode64(JspHelper.verificaVarQryStr(request, "SER_NOME"));

            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);
            final String qtdDefault = registroServidor.getRseParamQtdAdeDefault() == null ? "" : registroServidor.getRseParamQtdAdeDefault().toString();

            // Pega os valores dos bloqueios por serviços
            final Map<Object, Object> bloqueioServico = new HashMap<>();
            try {
                final List<TransferObject> servicosBloqueados = parametroController.lstBloqueioSvcRegistroServidor(rseCodigo, null, responsavel);
                final Iterator<TransferObject> itSB = servicosBloqueados.iterator();
                TransferObject sbTO;
                while (itSB.hasNext()) {
                    sbTO = itSB.next();
                    bloqueioServico.put(sbTO.getAttribute(Columns.SVC_CODIGO), sbTO.getAttribute(Columns.PSR_VLR));
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<TransferObject> convenios = null;
            try {
                convenios = parametroController.lstBloqueioCnvRegistroServidor(rseCodigo, csaCodigo, null, Boolean.TRUE, responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Se for servidor, não exige motivo para operação
            final Boolean exigeMotivo = responsavel.isSer() ? false : FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_CNV_REG_SERVIDOR, responsavel);

            final List<String> tocCodigos = new ArrayList<>();
            tocCodigos.add(CodedValues.TOC_RSE_BLOQUEIO_VERBA);
            tocCodigos.add(CodedValues.TOC_RSE_DESBLOQUEIO_VERBAS);
            List<TransferObject> historicoOcorrencia = servidorController.findByRseTocCodigos(rseCodigo, tocCodigos, responsavel);

            model.addAttribute("exigeMotivo", exigeMotivo);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serNomeCodificado", serNomeCodificado);
            model.addAttribute("serNome", serNome);
            model.addAttribute("convenios", convenios);
            model.addAttribute("listaSvcBloqueaveisServidor", listaSvcBloqueaveisServidor);
            model.addAttribute("bloqueioServico", bloqueioServico);
            model.addAttribute("readOnly", readOnly);
            model.addAttribute("qtdDefault", qtdDefault);
            model.addAttribute("historicoOcorrencia", historicoOcorrencia);

            // Confere o parâmetro para saber por qual meio o OTP deve ser enviado
            final boolean enviaOtpEmail = ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_EMAIL, responsavel) || ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_SMS_OU_EMAIL, responsavel);
            final boolean enviaOtpCelular = ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_SMS, responsavel) || ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_SMS_OU_EMAIL, responsavel);
            model.addAttribute("exigeOtp", responsavel.isSer() && (enviaOtpEmail || enviaOtpCelular));

            return viewRedirect("jsp/editarServidor/listarConvenioServidor", request, session, model, responsavel);

        } catch (ParametroControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String tmoCodigo = request.getParameter("TMO_CODIGO");
            final String orsObs = request.getParameter("ADE_OBS");

            // Se for servidor, não exige motivo para operação
            if (!responsavel.isSer() && TextHelper.isNull(tmoCodigo) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_CNV_REG_SERVIDOR, responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                return iniciar(request, response, session, model);
            }

            // Valida o OTP enviado para o servidor
            final boolean enviaOtpEmail = ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_EMAIL, responsavel) || ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_SMS_OU_EMAIL, responsavel);
            final boolean enviaOtpCelular = ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_SMS, responsavel) || ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_SMS_OU_EMAIL, responsavel);
            if (responsavel.isSer() && (enviaOtpEmail || enviaOtpCelular)) {
                try {
                    usuarioController.validarOtpServidorEnviadoPorEmailOuCelular(responsavel.getUsuCodigo(), request.getParameter("codigoOtp"), responsavel);
                } catch (UsuarioControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    // Repassa o token salvo, pois o método irá revalidar o token
                    request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                    return iniciar(request, response, session, model);
                }
            }

            final List<Object> listaSvcBloqueaveisServidor = new ArrayList<>();

            // Se responsavel for servidor, recupera a lista de servicos bloqueaveis por ele
            if (responsavel.isSer()) {
                final List<TransferObject> lista = parametroController.lstParamSvcCse(CodedValues.TPS_PERMITE_SER_BLOQUEAR_SERVICO_VERBA, "1", responsavel);
                if (!lista.isEmpty()) {
                    for (final TransferObject to : lista) {
                        listaSvcBloqueaveisServidor.add(to.getAttribute(Columns.PSE_SVC_CODIGO));
                    }
                }
            }

            final String rseCodigo = responsavel.isSer() ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

            if ((rseCodigo == null) || "".equals(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);

            try {
                if (JspHelper.verificaVarQryStr(request, "convenios") != null) {
                    final List<ParamCnvRseTO> bloqueios = new ArrayList<>();

                    final StringTokenizer stn = new StringTokenizer(JspHelper.verificaVarQryStr(request, "convenios"), ",");
                    while (stn.hasMoreTokens()) {
                        final String cnvCodigo = stn.nextToken();
                        final String pcrVlr = JspHelper.verificaVarQryStr(request, "cnv_" + cnvCodigo);
                        final String pcrObs = JspHelper.verificaVarQryStr(request, "cnv2_" + cnvCodigo);

                        final ParamCnvRseTO dto = new ParamCnvRseTO();
                        dto.setTpsCodigo(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO);
                        dto.setCnvCodigo(cnvCodigo);
                        dto.setRseCodigo(rseCodigo);

                        if (responsavel.isCseSupOrg()) {
                            dto.setPcrVlrCse(pcrVlr);
                        } else if (responsavel.isSer()) {
                            dto.setPcrVlrSer(pcrVlr);
                        } else if (responsavel.isCsa()) {
                            dto.setPcrVlrCsa(pcrVlr);
                        }
                        dto.setPcrVlr(pcrVlr);

                        dto.setPcrObs(pcrObs);
                        bloqueios.add(dto);
                    }

                    final CustomTransferObject tmoObject = new CustomTransferObject();
                    tmoObject.setAttribute("tmoCodigo", tmoCodigo);
                    tmoObject.setAttribute("orsObs", orsObs);

                    // Salva os bloqueios de servidor
                    parametroController.setBloqueioCnvRegistroServidor(bloqueios, tmoObject , responsavel);

                    //Atualiza o RegistroServidor quando o valor default de contratos estiver preenchido
                    final String _qtd = JspHelper.verificaVarQryStr(request, "qtd_default");
                    Short qtd = null;
                    if (!"".equals(_qtd.trim())) {
                        qtd = Short.valueOf(_qtd);
                    }
                    registroServidor.setRseParamQtdAdeDefault(qtd);
                    servidorController.updateRegistroServidorSemHistoricoMargem(registroServidor, responsavel);

                    // Seta mensagem de sucesso na sessão do usuário
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.convenio.alterado.sucesso", responsavel));
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.alterar.limite.convenio", responsavel, ex.getMessage()));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            return iniciar(request, response, session, model);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=bloqueios" })
    public String bloqueios(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");

        if(responsavel.isCor() || responsavel.isSer() || responsavel.isOrg()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.autorizar.operacao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final List<TransferObject> convenios = parametroController.lstBloqueioCnvRegistroServidorEntidade(responsavel.isCsa() ? responsavel.getCodigoEntidade() : null, responsavel);

            if((convenios == null) || convenios.isEmpty()) {
                final ParamSession paramSession = ParamSession.getParamSession(session);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.convenios.bloqueados.nao.existe.papel", responsavel));
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                return "jsp/redirecionador/redirecionar";
            }

            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("convenios", convenios);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final Boolean exigeMotivo = responsavel.isSer() ? false : FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_CNV_REG_SERVIDOR, responsavel);
        model.addAttribute("exigeMotivo", exigeMotivo);

        return viewRedirect("jsp/editarServidor/listarConvenioServidorEntidade", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=desbloquear" })
    public String desbloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if(responsavel.isCor() || responsavel.isSer() || responsavel.isOrg()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.autorizar.operacao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String tmoCodigo = request.getParameter("TMO_CODIGO");
        final String orsObs = request.getParameter("ADE_OBS");

        if (TextHelper.isNull(tmoCodigo) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_CNV_REG_SERVIDOR, responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.convenios.bloqueados.nao.existe.papel", responsavel));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }
        final CustomTransferObject tmoObject = new CustomTransferObject();
        tmoObject.setAttribute("tmoCodigo", tmoCodigo);
        tmoObject.setAttribute("orsObs", orsObs);

        try {
            if (JspHelper.verificaVarQryStr(request, "convenios") != null) {
                final List<ParamCnvRseTO> bloqueios = new ArrayList<>();
                final List<String> cnvCodigos = new ArrayList<>();
                final StringTokenizer stn = new StringTokenizer(JspHelper.verificaVarQryStr(request, "convenios"), ",");
                while (stn.hasMoreTokens()) {
                    cnvCodigos.add(stn.nextToken());
                }

                if (cnvCodigos.isEmpty()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.convenios.bloqueados.nao.existe.papel", responsavel));
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                    return "jsp/redirecionador/redirecionar";
                }

                final List<TransferObject> convenioRseBloqueados = parametroController.lstBloqueioCnvRegistroServidorCnvCodigos(cnvCodigos, responsavel);

                String rseCodigoOld = "";
                String csaCodigoOld = "";
                HashMap<String, Integer> dispatchCsa = new HashMap<>();
                HashMap<String, Integer> dispatchEmail = new HashMap<>();
                for (final TransferObject cnvBloqueadoRse : convenioRseBloqueados) {
                    final String cnvCodigo = (String) cnvBloqueadoRse.getAttribute(Columns.CNV_CODIGO);
                    final String rseCodigo = (String) cnvBloqueadoRse.getAttribute(Columns.RSE_CODIGO);
                    final String csaCodigo = (String) cnvBloqueadoRse.getAttribute(Columns.CSA_CODIGO);

                    final String pcrVlr = "";

                    final ParamCnvRseTO dto = new ParamCnvRseTO();
                    dto.setTpsCodigo(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO);
                    dto.setCnvCodigo(cnvCodigo);
                    dto.setRseCodigo(rseCodigo);
                    dto.setAttribute(Columns.CSA_CODIGO, cnvBloqueadoRse.getAttribute(Columns.CSA_CODIGO));

                    if (responsavel.isCseSup()) {
                        dto.setPcrVlrCse(pcrVlr);
                    } else if (responsavel.isCsa()) {
                        dto.setPcrVlrCsa(pcrVlr);
                    }
                    dto.setPcrVlr(pcrVlr);

                    dto.setPcrObs("");
                    csaCodigoOld = csaCodigo;

                    if (!rseCodigo.equals(rseCodigoOld)) {
                        if (!bloqueios.isEmpty()) {
                            parametroController.setBloqueioCnvRegistroServidor(bloqueios, tmoObject, responsavel);
                            for (final Map.Entry<String, Integer> entry : dispatchCsa.entrySet()) {
                                if (dispatchEmail.containsKey(entry.getKey())) {
                                    dispatchEmail.put(entry.getKey(), dispatchEmail.get(entry.getKey()) + 1);
                                } else {
                                    dispatchEmail.put(entry.getKey(), entry.getValue() + 1);
                                }
                            }
                            dispatchCsa.clear();
                            bloqueios.clear();
                        }

                        rseCodigoOld = rseCodigo;
                    }

                    if (!csaCodigoOld.isEmpty() && !dispatchCsa.containsKey(csaCodigoOld) && !dispatchEmail.containsKey(csaCodigoOld)) {
                        dispatchCsa.put(csaCodigoOld, 0);
                        dispatchEmail.put(csaCodigoOld, 0);
                    }

                    bloqueios.add(dto);
                }

                // Essa lógica se faz necessária se a lista contiver somente um item ou for o último.
                if (!bloqueios.isEmpty()) {
                    parametroController.setBloqueioCnvRegistroServidor(bloqueios, tmoObject, responsavel);
                    if (dispatchEmail.containsKey(csaCodigoOld)) {
                        dispatchEmail.put(csaCodigoOld, dispatchEmail.get(csaCodigoOld) + 1);
                    } else {
                        dispatchEmail.put(csaCodigoOld, 1);
                    }
                }

                if (!dispatchEmail.isEmpty()) {
                    for (final Map.Entry<String, Integer> entry : dispatchEmail.entrySet()) {
                        List<String> csaCodigos = new ArrayList<>();
                        csaCodigos.add(entry.getKey());
                        consignatariaController.incluirOcorrenciaConsignatarias(csaCodigos, CodedValues.TOC_DESBLOQUEIO_VERBA_RSE_CSA, ApplicationResourcesHelper.getMessage("mensagem.informacao.desbloqueio.convenio.csa.count", responsavel, String.valueOf(entry.getValue())), responsavel);

                        String csaEmail = parametroController.getParamCsa(entry.getKey(), CodedValues.TPA_EMAIL_CSA_NOTIFICACAO_BLOQUEIO_VARIACAO_MARGEM, responsavel);
                        final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(entry.getKey(), responsavel);

                        String cseNome = responsavel.isCseSup() && responsavel.getNomeEntidade() != null ? responsavel.getNomeEntidade() : LoginHelper.getCseNome(responsavel) != null ? LoginHelper.getCseNome(responsavel) : "";
                        csaEmail = TextHelper.isNull(csaEmail) ? csa.getCsaEmail() : csaEmail.replace(";", ",");

                        if (!TextHelper.isNull(csaEmail)) {
                            EnviaEmailHelper.notificaCsaDesbloqueioVerbaRse(csaEmail, cseNome, csa.getCsaNome(), entry.getValue(), responsavel);
                        }
                    }
                }

                // Seta mensagem de sucesso na sessão do usuário
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.convenio.alterado.sucesso", responsavel));
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.alterar.limite.convenio", responsavel, ex.getMessage()));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=enviarOtp" })
    public ResponseEntity<String> enviarOtp(HttpServletRequest request, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Confere o parâmetro para saber por qual meio o OTP deve ser enviado
        final boolean enviaOtpEmail = ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_EMAIL, responsavel) || ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_SMS_OU_EMAIL, responsavel);
        final boolean enviaOtpCelular = ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_SMS, responsavel) || ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR, CodedValues.ENVIA_OTP_SMS_OU_EMAIL, responsavel);

        if (responsavel.isSer() && (enviaOtpEmail || enviaOtpCelular)) {
            LOG.debug("Gerando OTP para bloqueio de verbas pelo servidor");

            try {
                usuarioController.enviarOtpServidorPorEmailOuCelular(responsavel.getUsuCodigo(), enviaOtpEmail, enviaOtpCelular, responsavel);
            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
