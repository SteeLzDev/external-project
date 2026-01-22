package com.zetra.econsig.web.controller.servidor;

import java.io.File;
import java.security.Key;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senha.GeradorSenhaUtil;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: AutorizarDescontoParcialServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Permitir Desconto Parcial Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 30120 $
 * $Date: 2020-10-21 14:15:47 -0300 (qua, 20 out 2020) $
 */

@Controller
@RequestMapping(value = { "/v3/autorizarDescontoParcialSer" })
public class AutorizarDescontoParcialServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutorizarDescontoParcialServidorWebController.class);

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String termoDescontoParcialServidor = "";

            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            absolutePath += File.separatorChar + "termo_de_uso" + File.separatorChar;
            absolutePath += CodedNames.TEMPLATE_TERMO_DESCONTO_PARCIAL_SERVIDOR;

            File file = new File(absolutePath);
            if (file != null && file.isFile() && file.exists()) {
                termoDescontoParcialServidor = FileHelper.readAll(absolutePath).replaceAll("\\r\\n|\\r|\\n", "");
            } else {
                throw new ServidorControllerException("mensagem.erro.servidor.autorizar.desconto.parcial.sem.arquivo", responsavel);
            }

            ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);

            boolean autorizaSemCodigo = ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_AUTORIZA_DESCONTO_PARCIAL, CodedValues.TPC_AUTORIZA_SEM_CODIGO, responsavel);

            model.addAttribute("termoDescontoParcialServidor", termoDescontoParcialServidor);
            model.addAttribute("serEmail",servidor.getSerEmail());
            model.addAttribute("serPrimeiroNome", servidor.getSerPrimeiroNome());
            model.addAttribute("serCodigo",servidor.getSerCodigo());
            if (autorizaSemCodigo) {
                model.addAttribute("acao", "salvar");
            } else {
                model.addAttribute("acao", "gerarTokenEmail");
            }

            return viewRedirect("jsp/autorizarDescontoParcialSer/autorizarDescontoParcialSer", request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("mensagem.atualizacao.email.telefone.titulo", responsavel));
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=gerarTokenEmail" })
    public String gerarTokenEmail(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            String autorizarDesconto = JspHelper.verificaVarQryStr(request, "autorizarDesconto");
            String autorizarDepois = JspHelper.verificaVarQryStr(request, "DEPOIS");

            if (TextHelper.isNull(autorizarDesconto) || autorizarDepois.equals("true")) {
                session.removeAttribute("AutorizaDescontoParcialSer");
                return "redirect:../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";
            }

            ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);

            String serEmail = servidor.getSerEmail();
            String serEmailCripto = "";

            if (!TextHelper.isNull(serEmail)) {
                String[] serEmailParts = serEmail.split("@");
                String user = serEmailParts[0].substring(0, 4);
                String domain = serEmailParts[1].substring((serEmailParts[1].length()-9));
                serEmailCripto = user + "********@*****" + domain;
            } else {
                throw new ServidorControllerException("mensagem.info.servidor.autorizar.desconto.parcial.email.invalido", responsavel, servidor.getSerPrimeiroNome());
            }

            model.addAttribute("acao", "salvar");
            model.addAttribute("exibeCodigo", "true");
            model.addAttribute("serEmail",serEmailCripto);
            model.addAttribute("serPrimeiroNome", servidor.getSerPrimeiroNome());
            model.addAttribute("autorizarDesconto", autorizarDesconto);
            model.addAttribute("serCodigo",servidor.getSerCodigo());

            //Gera um token e o criptografa para ser enviado no request
            String token = GeradorSenhaUtil.getPasswordNumber(4, responsavel);
            Key privateKeyEConsig = RSA.generatePrivateKey(CodedValues.RSA_MODULUS_ECONSIG, CodedValues.RSA_PRIVATE_KEY_ECONSIG);
            String criptoToken = RSA.encrypt(token, privateKeyEConsig);
            session.setAttribute("cripto", criptoToken);

            EnviaEmailHelper.enviarEmailCodigoAutorizarDescontoParcialSer(serEmail, token, responsavel);

            return viewRedirect("jsp/autorizarDescontoParcialSer/autorizarDescontoParcialSer", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            boolean autorizaSemCodigo = ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_AUTORIZA_DESCONTO_PARCIAL, CodedValues.TPC_AUTORIZA_SEM_CODIGO, responsavel);
            String cripto = (String) session.getAttribute("cripto");
            String token = JspHelper.verificaVarQryStr(request, "tokenEmail");
            String autorizarDesconto = JspHelper.verificaVarQryStr(request, "autorizarDesconto");
            String serCodigo = JspHelper.verificaVarQryStr(request, "serCodigo");

            ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);

            String serEmail = servidor.getSerEmail();
            String serEmailCripto = "";

            if (!TextHelper.isNull(serEmail)) {
                String[] serEmailParts = serEmail.split("@");
                String user = serEmailParts[0].substring(0, 4);
                String domain = serEmailParts[1].substring((serEmailParts[1].length()-9));
                serEmailCripto = user + "********@*****" + domain;
            } else {
                throw new ServidorControllerException("mensagem.info.servidor.autorizar.desconto.parcial.email.invalido", responsavel, servidor.getSerPrimeiroNome());
            }

            if (!autorizaSemCodigo) {
                Key privateKeyEConsig = RSA.generatePrivateKey(CodedValues.RSA_MODULUS_ECONSIG, CodedValues.RSA_PRIVATE_KEY_ECONSIG);
                String criptoToken = RSA.encrypt(token, privateKeyEConsig);

                if (TextHelper.isNull(token) || TextHelper.isNull(cripto)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.autorizar.desconto.parcial.token.invalido", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (!cripto.equals(criptoToken)) {
                    model.addAttribute("acao", "salvar");
                    model.addAttribute("exibeCodigo", "true");
                    model.addAttribute("serEmail", serEmailCripto);
                    model.addAttribute("serPrimeiroNome", servidor.getSerPrimeiroNome());
                    model.addAttribute("autorizarDesconto", autorizarDesconto);
                    model.addAttribute("serCodigo", servidor.getSerCodigo());
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.autorizar.desconto.parcial.token.invalido", responsavel));
                    return viewRedirect("jsp/autorizarDescontoParcialSer/autorizarDescontoParcialSer", request, session, model, responsavel);
                }
            }

            String tocCodigo = autorizarDesconto.equals("S") ? CodedValues.TOC_SER_AUTORIZA_DESC_PARCIAL : CodedValues.TOC_SER_NAO_AUTORIZA_DESC_PARCIAL;
            servidorController.criaOcorrenciaSER(serCodigo, tocCodigo, ApplicationResourcesHelper.getMessage("mensagem.info.servidor.autorizar.desconto.parcial.ocorrencia", responsavel), null, responsavel);

            if (autorizarDesconto.equals("S")) {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.servidor.autorizar.desconto.parcial.decisao.autorizada", responsavel));
            } else {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.servidor.autorizar.desconto.parcial.decisao.negada", responsavel));
            }

            session.removeAttribute("AutorizaDescontoParcialSer");

            return "redirect:../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServidorControllerException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        List<TransferObject> listaOcorrenciaSerAutorizaDesc = servidorController.lstDataOcorrenciaServidorDescontoParcial(responsavel.getSerCodigo(), CodedValues.TOC_SER_AUTORIZA_DESC_PARCIAL, true, responsavel);
        List<TransferObject> listaOcorrenciaSerNaoAutorizaDesc = servidorController.lstDataOcorrenciaServidorDescontoParcial(responsavel.getSerCodigo(), CodedValues.TOC_SER_NAO_AUTORIZA_DESC_PARCIAL, true, responsavel);

        Date autorizaDesconto = null;
        Date naoAutorizaDesconto = null;
        String permiteDesconto = "";

        // Pega a ocorrencia mais recente após a ordenação.
        if (listaOcorrenciaSerAutorizaDesc != null && !listaOcorrenciaSerAutorizaDesc.isEmpty()) {
            autorizaDesconto = DateHelper.parse(listaOcorrenciaSerAutorizaDesc.get(0).getAttribute(Columns.OCS_DATA).toString(), "yyyy-MM-dd HH:mm:ss");
        }
        if (listaOcorrenciaSerNaoAutorizaDesc != null && !listaOcorrenciaSerNaoAutorizaDesc.isEmpty()) {
            naoAutorizaDesconto = DateHelper.parse(listaOcorrenciaSerNaoAutorizaDesc.get(0).getAttribute(Columns.OCS_DATA).toString(), "yyyy-MM-dd HH:mm:ss");
        }

        if (autorizaDesconto != null && naoAutorizaDesconto == null) {
            permiteDesconto = CodedValues.TPC_SIM;
        } else if (naoAutorizaDesconto != null && autorizaDesconto == null) {
            permiteDesconto = CodedValues.TPC_NAO;
        } else if (autorizaDesconto != null && naoAutorizaDesconto != null && autorizaDesconto.compareTo(naoAutorizaDesconto) > 0) {
            permiteDesconto = CodedValues.TPC_SIM;
        } else if (autorizaDesconto != null && naoAutorizaDesconto != null && autorizaDesconto.compareTo(naoAutorizaDesconto) < 0) {
            permiteDesconto = CodedValues.TPC_NAO;
        }

        model.addAttribute("permiteDesconto", permiteDesconto);
        model.addAttribute("fluxoEditar",true);

        return iniciar(request, response, session, model);
    }
}
