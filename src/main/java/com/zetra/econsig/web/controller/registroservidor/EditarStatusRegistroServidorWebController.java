package com.zetra.econsig.web.controller.registroservidor;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.servidor.AbstractServidorWebController;

/**
 * <p>Title: EditarStatusRegistroServidorWebController</p>
 * <p>Description: Controlador Web para o caso de Editar status do registro do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarStatusRegistroServidor" })
public class EditarStatusRegistroServidorWebController extends AbstractServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarStatusRegistroServidorWebController.class);

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "SER_CODIGO", required = true, defaultValue = "") String serCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);
            model.addAttribute("registroServidor", registroServidor);

            ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);
            model.addAttribute("servidor", servidor);

            List<TransferObject> listaSrs = servidorController.lstStatusRegistroServidor(false, true, responsavel);
            request.setAttribute("listaSrs", listaSrs);

            return viewRedirect("jsp/editarRegistroServidor/editarStatusRegistroServidor", request, session, model, responsavel);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);
            recuperarDadosRegistroServidor(registroServidor, request, responsavel);

            //busca as variáveis vindas da pagina

            String situacao = request.getParameter("editarStatusRegistroServidor_situacao");
            String dataSaida = request.getParameter("editarStatusRegistroServidor_dataSaida");
            String dataUltSalario = request.getParameter("editarStatusRegistroServidor_dataUltSalario");
            String salario = request.getParameter("editarStatusRegistroServidor_salario");
            String proventos = request.getParameter("editarStatusRegistroServidor_proventos");
            String pedidoDemissao = request.getParameter("editarStatusRegistroServidor_pedidoDemissao");
            String dataRetorno = request.getParameter("editarStatusRegistroServidor_dataRetorno");

            //atribui os valores vindos da pagina para o registroServidor, caso não esteja em branco e seja diferente do que consta no banco de dados
            if ((!situacao.equals(registroServidor.getSrsCodigo()))) {
                registroServidor.setSrsCodigo(situacao);
            }
            if ((!TextHelper.isNull(dataSaida))) {
                Date novaDataSaida = DateHelper.parse(dataSaida, LocaleHelper.getDatePattern());
                if ((!novaDataSaida.equals(registroServidor.getRseDataSaida()))) {
                    registroServidor.setRseDataSaida(novaDataSaida);
                }
            } else {
                if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_SAIDA, responsavel)) {
                    registroServidor.setRseDataSaida(null);
                }
            }

            if ((!TextHelper.isNull(dataUltSalario))) {
                Date novaDataUltSalario = DateHelper.parse(dataUltSalario, LocaleHelper.getDatePattern());
                if ((!novaDataUltSalario.equals(registroServidor.getRseDataUltSalario()))) {
                    registroServidor.setRseDataUltSalario(novaDataUltSalario);
                }
            } else {
                if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_ULT_SALARIO, responsavel)) {
                    registroServidor.setRseDataUltSalario(null);
                }
            }

            if ((!TextHelper.isNull(salario))) {
                BigDecimal novoSalario = new BigDecimal(NumberHelper.reformat(salario, NumberHelper.getLang(), "en"));
                if ((!novoSalario.equals(registroServidor.getRseSalario()))) {
                    registroServidor.setRseSalario(novoSalario);
                }
            } else {
                if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_SALARIO, responsavel)) {
                    registroServidor.setRseSalario(null);
                }
            }
            if ((!TextHelper.isNull(proventos))) {
                BigDecimal novoProvento = new BigDecimal(NumberHelper.reformat(proventos, NumberHelper.getLang(), "en"));
                if ((!novoProvento.equals(registroServidor.getRseProventos()))) {
                    registroServidor.setRseProventos(novoProvento);
                }
            } else {
                if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_PROVENTOS, responsavel)) {
                    registroServidor.setRseProventos(null);
                }
            }
            if ((!TextHelper.isNull(pedidoDemissao))) {
                if ((!pedidoDemissao.equals(registroServidor.getRsePedidoDemissao()))) {
                    registroServidor.setRsePedidoDemissao(pedidoDemissao);
                }
            }
            if ((!TextHelper.isNull(dataRetorno))) {
                Date novaDataRetorno = DateHelper.parse(dataRetorno, LocaleHelper.getDatePattern());
                if ((!novaDataRetorno.equals(registroServidor.getRseDataRetorno()))) {
                    registroServidor.setRseDataRetorno(novaDataRetorno);
                }
            } else {
                if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_STATUS_REGISTRO_SERVIDOR_DATA_RETORNO, responsavel)) {
                    registroServidor.setRseDataRetorno(null);
                }
            }

            if (validarDadosStatusRegistroServidor(registroServidor, request, responsavel)) {
                // Executa rotina de atualização
                servidorController.updateRegistroServidor(registroServidor, null, !responsavel.temPermissao(CodedValues.FUN_ALT_MARGEM_CONSIGNAVEL), true, false, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.registro.servidor.alterado.sucesso", responsavel));

                ParamSession paramSession = ParamSession.getParamSession(session);
                paramSession.halfBack();

                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                return "jsp/redirecionador/redirecionar";

            } else {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
