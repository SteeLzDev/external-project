package com.zetra.econsig.web.controller.servidor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: TransferirMargemServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Transferir Margem do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/transferirMargemServidor" })
public class TransferirMargemServidorWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TransferirMargemServidorWebController.class);

    @Autowired
    private MargemController margemController;

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);

            if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String readOnly = "false";
            if ((!responsavel.isCseSupOrg() && !responsavel.isSer()) || (!responsavel.temPermissao(CodedValues.FUN_TRANSFERIR_MARGEM))) {
                readOnly = "true";
            }

            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            String serNomeCodificado = JspHelper.verificaVarQryStr(request, "SER_NOME");
            String serNome = TextHelper.isNull(serNomeCodificado) ? serNomeCodificado : TextHelper.decode64(JspHelper.verificaVarQryStr(request, "SER_NOME"));
            String paginaAnterior = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);

            if (responsavel.isSer() && TextHelper.isNull(rseCodigo)) {
                rseCodigo = responsavel.getRseCodigo();
                rseMatricula = responsavel.getRseMatricula();
                serNome = responsavel.getUsuNome();
                serNomeCodificado = TextHelper.encode64(serNome);
                paginaAnterior = "../v3/carregarPrincipal";
            }

            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String marCodigoOrigem = JspHelper.verificaVarQryStr(request, "MAR_CODIGO_ORIGEM");
            String transfTotal = JspHelper.verificaVarQryStr(request, "TRANSF_TOTAL");

            List<String> margensOri = new ArrayList<>(); // Lista de margens que podem ser origem de transferencia
            List<String> margensDes = new ArrayList<>(); // Lista de margens que podem ser destino de transferencia
            Map<String, List<String>> mapMargens = new HashMap<>(); // Mapeia os relacionamentos entre as margens (Map<String, List<String>>)
            Map<String, List<String>> mapTransf = new HashMap<>(); // Mapeia os tipos de transferencia possiveis para cada relacionamento acima

            RegistroServidorTO registroServidor = null;
            try {
                registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);
                String csaCodigo = (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : null;
                String orgCodigo = (responsavel.isOrg()) ? responsavel.getCodigoEntidade() : null;
                String estCodigo = (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) ? responsavel.getEstCodigo() : null;

                // Lista os relacionamentos possiveis entre as margens incidentes
                List<TransferObject> margensIncidentes = margemController.lstMargensIncidentesTransferencia(csaCodigo, orgCodigo, rseCodigo, estCodigo, UsuarioHelper.getPapCodigo(responsavel.getTipoEntidade()), responsavel);
                Iterator<TransferObject> it = margensIncidentes.iterator();
                while (it.hasNext()) {

                    CustomTransferObject margem = (CustomTransferObject) it.next();
                    String codigoOri = margem.getAttribute(Columns.TRM_MAR_CODIGO_ORIGEM).toString();
                    String codigoDes = margem.getAttribute(Columns.TRM_MAR_CODIGO_DESTINO).toString();
                    String apenasTotal = (String) margem.getAttribute(Columns.TRM_APENAS_TOTAL);

                    List<String> margens = new ArrayList<>();
                    List<String> transf = new ArrayList<>();
                    if (mapMargens.get(codigoOri) != null) {
                        margens = mapMargens.get(codigoOri);
                        transf = mapTransf.get(codigoOri);
                        if (!margens.contains(codigoDes)) {
                            margens.add(codigoDes);
                            transf.add(apenasTotal);
                        }
                    } else {
                        margensOri.add(codigoOri);
                        margens.add(codigoDes);
                        transf.add(apenasTotal);
                    }
                    // Correspondencia entre relacionamento e tipo de transferencia é feita pela posicao na lista do map
                    mapMargens.put(codigoOri, margens);
                    mapTransf.put(codigoOri, transf);
                }
                // Seleciona o combo de origem e recupera valores do combo de destino a partir dessa origem
                if (TextHelper.isNull(marCodigoOrigem) && !margensOri.isEmpty() && !TextHelper.isNull(margensOri.get(0))) {
                    marCodigoOrigem = margensOri.get(0);
                }
                if (!TextHelper.isNull(marCodigoOrigem)) {
                    margensDes = mapMargens.get(marCodigoOrigem);
                    Collections.sort(margensDes); // Ordenacao natural para ficar coerente com a lista margensOri
                }

            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serNomeCodificado", serNomeCodificado);
            model.addAttribute("serNome", serNome);
            model.addAttribute("paginaAnterior", paginaAnterior);
            model.addAttribute("transfTotal", transfTotal);
            model.addAttribute("margensOri", margensOri);
            model.addAttribute("margensDes", margensDes);
            model.addAttribute("registroServidor", registroServidor);
            model.addAttribute("readOnly", readOnly);

            return viewRedirect("jsp/editarServidor/transferirMargemServidor", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            try {
                String marCodigoOrigem = JspHelper.verificaVarQryStr(request, "MAR_CODIGO_ORIGEM");
                String marCodigoDestino = JspHelper.verificaVarQryStr(request, "MAR_CODIGO_DESTINO");
                String transfTotal = JspHelper.verificaVarQryStr(request, "TRANSF_TOTAL");

                List<String> margensOri = new ArrayList<>(); // Lista de margens que podem ser origem de transferencia
                List<String> margensDes = new ArrayList<>(); // Lista de margens que podem ser destino de transferencia
                Map<String, List<String>> mapMargens = new HashMap<>(); // Mapeia os relacionamentos entre as margens (Map<String, List<String>>)
                Map<String, List<String>> mapTransf = new HashMap<>(); // Mapeia os tipos de transferencia possiveis para cada relacionamento acima

                RegistroServidorTO registroServidor = null;
                try {
                    registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);
                    String csaCodigo = (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : null;
                    String orgCodigo = (responsavel.isOrg()) ? responsavel.getCodigoEntidade() : null;
                    String estCodigo = (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) ? responsavel.getEstCodigo() : null;

                    // Lista os relacionamentos possiveis entre as margens incidentes
                    List<TransferObject> margensIncidentes = margemController.lstMargensIncidentesTransferencia(csaCodigo, orgCodigo, rseCodigo, estCodigo, UsuarioHelper.getPapCodigo(responsavel.getTipoEntidade()), responsavel);
                    Iterator<TransferObject> it = margensIncidentes.iterator();
                    while (it.hasNext()) {

                        CustomTransferObject margem = (CustomTransferObject) it.next();
                        String codigoOri = margem.getAttribute(Columns.TRM_MAR_CODIGO_ORIGEM).toString();
                        String codigoDes = margem.getAttribute(Columns.TRM_MAR_CODIGO_DESTINO).toString();
                        String apenasTotal = (String) margem.getAttribute(Columns.TRM_APENAS_TOTAL);

                        List<String> margens = new ArrayList<>();
                        List<String> transf = new ArrayList<>();
                        if (mapMargens.get(codigoOri) != null) {
                            margens = mapMargens.get(codigoOri);
                            transf = mapTransf.get(codigoOri);
                            if (!margens.contains(codigoDes)) {
                                margens.add(codigoDes);
                                transf.add(apenasTotal);
                            }
                        } else {
                            margensOri.add(codigoOri);
                            margens.add(codigoDes);
                            transf.add(apenasTotal);
                        }
                        // Correspondencia entre relacionamento e tipo de transferencia é feita pela posicao na lista do map
                        mapMargens.put(codigoOri, margens);
                        mapTransf.put(codigoOri, transf);
                    }
                    // Seleciona o combo de origem e recupera valores do combo de destino a partir dessa origem
                    if (TextHelper.isNull(marCodigoOrigem) && !margensOri.isEmpty() && !TextHelper.isNull(margensOri.get(0))) {
                        marCodigoOrigem = margensOri.get(0);
                    }
                    if (!TextHelper.isNull(marCodigoOrigem)) {
                        margensDes = mapMargens.get(marCodigoOrigem);
                        Collections.sort(margensDes); // Ordenacao natural para ficar coerente com a lista margensOri
                    }

                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                String termoAceite = JspHelper.verificaVarQryStr(request, "TERMO_ACEITE");
                if (!responsavel.isSer() || "SIM".equals(termoAceite)) {

                    BigDecimal valor = new BigDecimal(0.00);
                    if (transfTotal.equals(CodedValues.TPC_NAO) && JspHelper.verificaVarQryStr(request, "VALOR_TRANSF") != null) {
                        valor = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "VALOR_TRANSF"), NumberHelper.getLang(), "en"));
                    }

                    if ((valor != null || transfTotal.equals(CodedValues.TPC_SIM)) && marCodigoOrigem != null && marCodigoDestino != null && !marCodigoOrigem.equals(marCodigoDestino) && mapMargens.get(marCodigoOrigem) != null) {

                        List<String> margens = mapMargens.get(marCodigoOrigem);
                        List<String> transf = mapTransf.get(marCodigoOrigem);
                        String apenasTotal = transf.get(margens.indexOf(marCodigoDestino)); // Correspondencia pela posicao
                        if (!margens.contains(marCodigoDestino)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.transferencia.margem.destino", responsavel));

                        } else if (apenasTotal.equals(CodedValues.TPC_SIM) && transfTotal.equals(CodedValues.TPC_NAO)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.transferencia.margem.apenas.total", responsavel));

                        } else if (servidorController.transferirMargem(registroServidor, transfTotal, valor, Short.valueOf(marCodigoOrigem), Short.valueOf(marCodigoDestino), responsavel)) {
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.transferencia.margem.sucesso", responsavel));
                            registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.transferencia.margem", responsavel));
                        }
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.transferencia.margem", responsavel));
                    }

                } else {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.transferencia.margem.termo", responsavel));
                }
            } catch (ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.transferencia.margem", responsavel));
            }

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();

            return iniciar(request, response, session, model);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }
}
