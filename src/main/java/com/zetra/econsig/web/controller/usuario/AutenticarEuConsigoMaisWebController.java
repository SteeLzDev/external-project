package com.zetra.econsig.web.controller.usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: AutenticarEuConsigoMaisWebController</p>
 * <p>Description: Controlador Web para o caso de uso de single singon para o euconsigoMais.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/autenticarEuConsigoMais" })
public class AutenticarEuConsigoMaisWebController extends AbstractWebController {

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = "acao=gerarToken")
    public String gerarToken(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String euConsigoMais = null;
        String usuEmail = null;
        String token = null;
        UsuarioTransferObject usu = new UsuarioTransferObject();

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_URL_EUCONSIGOMAIS, responsavel))) {
            euConsigoMais = ParamSist.getInstance().getParam(CodedValues.TPC_URL_EUCONSIGOMAIS, responsavel).toString();
        }

        try {
            usu = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);
            usuEmail = usu.getUsuEmail();

            //gerar o hash sha-3
            SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
            byte[] digest = digestSHA3.digest(usuEmail.getBytes());
            token = Hex.toHexString(digest);

            // registra dados no USU_OTP_CODIGO
            usu.setUsuOtpCodigo(token);
            usu.setUsuOtpDataCadastro(DateHelper.getSystemDatetime());

            // Grava ocorrência de geração de OTP
            OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setTocCodigo(CodedValues.TOC_INCLUSAO_OTP_USUARIO);
            ocorrencia.setOusUsuCodigo((responsavel.getUsuCodigo() != null) ? responsavel.getUsuCodigo() : AcessoSistema.getAcessoUsuarioSistema().getUsuCodigo());
            ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.usuario", responsavel));
            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

            usuarioController.updateUsuario(usu, ocorrencia, null, null, AcessoSistema.ENTIDADE_CSE, responsavel.getUsuCodigo(), null, responsavel); //updateUsuario(usu, ocorrencia, responsavel);
        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        euConsigoMais += "?email="+usuEmail+"&token="+token;

        request.setAttribute("url64", TextHelper.encode64(euConsigoMais));
        return "jsp/redirecionador/redirecionar";
    }

}
