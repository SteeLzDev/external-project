package com.zetra.econsig.web.controller.funcao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCsa;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCsaSvc;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCse;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarFuncoesEnvioEmail" })
public class EditarFuncoesEnvioEmailWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarFuncoesEnvioEmailWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConsignanteController consignanteController;
    
    @Autowired
    private ServicoController servicoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : request.getParameter("CSA_CODIGO");
        final String cseCodigo = responsavel.isCse() ? responsavel.getCodigoEntidade() : request.getParameter("CSE_CODIGO");
        List<TransferObject> funcoes = new ArrayList<>();
        String email = null;
        String nome = null;

        if (!SynchronizerToken.isTokenValid(request) || (TextHelper.isNull(csaCodigo) && TextHelper.isNull(cseCodigo)) ) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            if(responsavel.isCseSup() && !TextHelper.isNull(cseCodigo) && TextHelper.isNull(csaCodigo)) {
                final ConsignanteTransferObject cse = consignanteController.findConsignante(cseCodigo, responsavel);

                nome = (String) cse.getAttribute(Columns.CSE_NOME);
                email = (String) cse.getAttribute(Columns.CSE_EMAIL);
                funcoes = consignanteController.lstFuncoesEnvioEmailCse(cseCodigo, responsavel);

                model.addAttribute("cseCodigo", cseCodigo);
            } else if((responsavel.isCsa() || responsavel.isCseSup()) && !TextHelper.isNull(csaCodigo)) {
                final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);

                nome = (String) (!TextHelper.isNull(csa.getAttribute(Columns.CSA_NOME_ABREV)) ? csa.getAttribute(Columns.CSA_NOME_ABREV) : csa.getAttribute(Columns.CSA_NOME));
                email = (String) csa.getAttribute(Columns.CSA_EMAIL);
                funcoes = consignatariaController.lstFuncoesEnvioEmailCsa(csaCodigo, responsavel);
                List<TransferObject> servicos = servicoController.selectServicosCsa(csaCodigo, responsavel);
                Map<String, List<String>> mapaServicosEnvioEmailCsa = consignatariaController.mapaServicosDestinatarioEmailCsaSvc(csaCodigo, responsavel);
                
                model.addAttribute("csaCodigo", csaCodigo);
                model.addAttribute("servicos", servicos);
                model.addAttribute("mapaServicosEnvioEmailCsa", mapaServicosEnvioEmailCsa);
            }

            model.addAttribute("email", email);
            model.addAttribute("nome", nome);
            model.addAttribute("readOnly", !responsavel.temPermissao(CodedValues.FUN_EDITAR_FUNCOES_ENVIO_EMAIL));
            model.addAttribute("funcoes", funcoes);
            return viewRedirect("jsp/editarFuncoesEnvioEmail/editarFuncoesEnvioEmail", request, session, model, responsavel);

        } catch (final ConsignatariaControllerException | ConsignanteControllerException | ServicoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : request.getParameter("CSA_CODIGO");
        final String cseCodigo = responsavel.isCse() ? responsavel.getCodigoEntidade() : request.getParameter("CSE_CODIGO");

        if (!SynchronizerToken.isTokenValid(request) || (TextHelper.isNull(csaCodigo) && TextHelper.isNull(cseCodigo))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            if((responsavel.isCsa() || responsavel.isCseSup()) && !TextHelper.isNull(csaCodigo)) {
                salvarCsa(csaCodigo, request, response, session, responsavel);
            }else if(responsavel.isCseSup() && !TextHelper.isNull(cseCodigo) && TextHelper.isNull(csaCodigo)) {
                salvarCse(cseCodigo, request, response, session, responsavel);
            }

            final ParamSession paramSession = ParamSession.getParamSession(session);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.funcao.alterado.sucesso", responsavel));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (final ConsignatariaControllerException | ConsignanteControllerException | ServicoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private void salvarCse(String cseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, AcessoSistema responsavel) throws ConsignanteControllerException{
        final ConsignanteTransferObject cse = consignanteController.findConsignante(cseCodigo, responsavel);
        final String cseEmail = (String) cse.getAttribute(Columns.CSE_EMAIL);

        final List<DestinatarioEmailCse> listaInc = new ArrayList<>();
        final List<DestinatarioEmailCse> listaAlt = new ArrayList<>();
        final List<DestinatarioEmailCse> listaExc = new ArrayList<>();

        final List<TransferObject> funcoes = consignanteController.lstFuncoesEnvioEmailCse(cseCodigo, responsavel);
        for (final TransferObject funcao : funcoes) {
            final String funCodigo = (String) funcao.getAttribute(Columns.FUN_CODIGO);
            final String papCodigo = (String) funcao.getAttribute(Columns.PAP_CODIGO);
            final String chaveCampo = funCodigo + "_" + papCodigo;

            final String deeEmailOld = !TextHelper.isNull(funcao.getAttribute(Columns.DEE_EMAIL)) ? (String) funcao.getAttribute(Columns.DEE_EMAIL) : cseEmail;
            final boolean receberOld = !"N".equalsIgnoreCase((String) funcao.getAttribute(Columns.DEE_RECEBER));

            final String deeEmailNew = !TextHelper.isNull(request.getParameter("email_" + chaveCampo)) ? request.getParameter("email_" + chaveCampo) : cseEmail;
            final boolean receberNew = "S".equals(request.getParameter("receber_" + chaveCampo));

            final boolean existe = !TextHelper.isNull(funcao.getAttribute(Columns.DEE_RECEBER));
            final boolean emailAlterado = !deeEmailNew.equals(cseEmail);

            // Se teve alteração, adiciona à lista de alterações a serem salvas
            if (!deeEmailOld.equals(deeEmailNew) || (receberOld != receberNew)) {
                final DestinatarioEmailCse dee = new DestinatarioEmailCse();
                dee.setFunCodigo(funCodigo);
                dee.setPapCodigo(papCodigo);
                dee.setCseCodigo(cseCodigo);
                dee.setDeeReceber(receberNew ? "S" : "N");
                dee.setDeeEmail(deeEmailNew);

                if (existe) {
                    if (receberNew) {
                        if (!emailAlterado) {
                            // Se existe e está com o padrão que é receber no e-mail geral, então exclui o registro
                            listaExc.add(dee);
                        } else {
                            // Se existe e está para receber em um e-mail alterado, então altera o registro
                            listaAlt.add(dee);
                        }
                    } else {
                        if (!emailAlterado) {
                            // Se existe e não é para receber e o e-mail não foi alterado, remove o e-mail
                            dee.setDeeEmail(null);
                        }
                        listaAlt.add(dee);
                    }
                } else {
                    if (!emailAlterado) {
                        // Se existe e não é para receber e o e-mail não foi alterado, remove o e-mail
                        dee.setDeeEmail(null);
                    }
                    if (!receberNew || emailAlterado) {
                        // Se não é para receber ou é, mas o e-mail é diferente do padrão, então salva o registro
                        listaInc.add(dee);
                    }
                }
            }
        }

        if (!listaInc.isEmpty() || !listaAlt.isEmpty() || !listaExc.isEmpty()) {
            // Salva as alterações
            consignanteController.salvarFuncoesEnvioEmailCse(listaInc, listaAlt, listaExc, responsavel);

            // Adiciona mensagem de sucesso na sessão
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.editar.funcoes.envio.email.sucesso", responsavel));
        }
    }

    private void salvarCsa(String csaCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, AcessoSistema responsavel) throws ConsignatariaControllerException, ServicoControllerException{
        final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);
        final String csaEmail = (String) csa.getAttribute(Columns.CSA_EMAIL);

        final List<DestinatarioEmailCsa> listaInc = new ArrayList<>();
        final List<DestinatarioEmailCsa> listaAlt = new ArrayList<>();
        final List<DestinatarioEmailCsa> listaExc = new ArrayList<>();

        final List<TransferObject> funcoes = consignatariaController.lstFuncoesEnvioEmailCsa(csaCodigo, responsavel);
        Map<String, Set<String>> servicosPorCsa = new HashMap<>();
        for (final TransferObject funcao : funcoes) {
            final String funCodigo = (String) funcao.getAttribute(Columns.FUN_CODIGO);
            final String papCodigo = (String) funcao.getAttribute(Columns.PAP_CODIGO);
            final String chaveCampo = funCodigo + "_" + papCodigo;
            
            final String[] servicosSelecionados = request.getParameterValues("svc_" + chaveCampo);
			Set<String> servicosNew = !TextHelper.isNull(servicosSelecionados) ? new LinkedHashSet<>(Arrays.asList(servicosSelecionados)) :  new LinkedHashSet<>();
            List<TransferObject> listaServicos = consignatariaController.lstServicosDestinatarioEmailCsaSvc(funCodigo, papCodigo, csaCodigo, responsavel);
			Set<String> servicosOld = listaServicos.stream().map(dcs -> dcs.getAttribute(Columns.SVC_CODIGO).toString()).collect(Collectors.toSet());

			if (!servicosOld.equals(servicosNew)) {
				String chaveCampoCsa = chaveCampo + "_" + csaCodigo;
			    servicosPorCsa.put(chaveCampoCsa, servicosNew);
			}			
            
            final String demEmailOld = !TextHelper.isNull(funcao.getAttribute(Columns.DEM_EMAIL)) ? (String) funcao.getAttribute(Columns.DEM_EMAIL) : csaEmail;
            final boolean receberOld = !"N".equalsIgnoreCase((String) funcao.getAttribute(Columns.DEM_RECEBER));

            final String demEmailNew = !TextHelper.isNull(request.getParameter("email_" + chaveCampo)) ? request.getParameter("email_" + chaveCampo) : csaEmail;
            final boolean receberNew = "S".equals(request.getParameter("receber_" + chaveCampo));

            final boolean existe = !TextHelper.isNull(funcao.getAttribute(Columns.DEM_RECEBER));
            final boolean emailAlterado = !demEmailNew.equals(csaEmail);

            // Se teve alteração, adiciona à lista de alterações a serem salvas
            if (!demEmailOld.equals(demEmailNew) || (receberOld != receberNew)) {
                final DestinatarioEmailCsa dem = new DestinatarioEmailCsa();
                dem.setFunCodigo(funCodigo);
                dem.setPapCodigo(papCodigo);
                dem.setCsaCodigo(csaCodigo);
                dem.setDemReceber(receberNew ? "S" : "N");
                dem.setDemEmail(demEmailNew);

                if (existe) {
                    if (receberNew) {
                        if (!emailAlterado) {
                            // Se existe e está com o padrão que é receber no e-mail geral, então exclui o registro
                            listaExc.add(dem);
                        } else {
                            // Se existe e está para receber em um e-mail alterado, então altera o registro
                            listaAlt.add(dem);
                        }
                    } else {
                        if (!emailAlterado) {
                            // Se existe e não é para receber e o e-mail não foi alterado, remove o e-mail
                            dem.setDemEmail(null);
                        }
                        listaAlt.add(dem);
                    }
                } else {
                    if (!emailAlterado) {
                        // Se existe e não é para receber e o e-mail não foi alterado, remove o e-mail
                        dem.setDemEmail(null);
                    }
                    if (!receberNew || emailAlterado) {
                        // Se não é para receber ou é, mas o e-mail é diferente do padrão, então salva o registro
                        listaInc.add(dem);
                    }
                }
            }
        }
        
        if(!servicosPorCsa.isEmpty()) {
        	consignatariaController.salvarServicosDestinatarioEmailCsaSvc(servicosPorCsa, responsavel);
        }

        if (!listaInc.isEmpty() || !listaAlt.isEmpty() || !listaExc.isEmpty()) {
            // Salva as alterações
            consignatariaController.salvarFuncoesEnvioEmailCsa(listaInc, listaAlt, listaExc, responsavel);            
		    
            // Adiciona mensagem de sucesso na sessão
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.editar.funcoes.envio.email.sucesso", responsavel));
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage(responsavel.temPermissao(CodedValues.FUN_EDITAR_FUNCOES_ENVIO_EMAIL) ? "rotulo.editar.funcoes.envio.email.titulo" : "rotulo.consultar.funcoes.envio.email.titulo", responsavel));
    }
}
