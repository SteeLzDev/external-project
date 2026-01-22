package com.zetra.econsig.web.controller.correspondente;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CorrespondenteControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.persistence.entity.EnderecoCorrespondente;
import com.zetra.econsig.persistence.entity.TipoEndereco;
import com.zetra.econsig.service.correspondente.CorrespondenteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.webclient.googlemaps.GoogleMapsClient;

/**
 * <p>Title: ManterEnderecosCorrespondenteWebController</p>
 * <p>Description: Manter registros da tb_endereco_correspondente</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterEnderecosCorrespondente" })
public class ManterEnderecosCorrespondenteWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterEnderecosCorrespondenteWebController.class);

    @Autowired
    private CorrespondenteController correspondenteController;

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.listar.colaborador.rescisao.titulo", responsavel, titulo));
        model.addAttribute("linkAction", getLinkAction());
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String listarEnderecosCorrespondente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            SynchronizerToken.saveToken(request);

            List<TransferObject> listaEnderecos = null;

            String corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");

            String csaCodigo = null;

            if (responsavel.isCor()) {
                corCodigo = responsavel.getCorCodigo();
            } else if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
            }

            int total = correspondenteController.countEnderecoCorrespondenteByCorCodigo(corCodigo, csaCodigo, responsavel);

            int size = JspHelper.LIMITE;
            int offset = request.getParameter("offset") != null ? Integer.parseInt(request.getParameter("offset")) : 0;

            listaEnderecos = correspondenteController.lstEnderecoCorrespondenteByCorCodigo(corCodigo, csaCodigo, size, offset, responsavel);

            // Monta lista de parâmetros e link de paginação
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            params.remove("offset");

            List<String> requestParams = new ArrayList<>(params);

            configurarPaginador(getLinkAction(), "rotulo.listar.enderecos.correspondente.paginacao.titulo", total, size, requestParams, false, request, model);

            // Seta atributos no model
            model.addAttribute("listaEnderecosCorrespondente", listaEnderecos);
            model.addAttribute("COR_CODIGO", corCodigo);

            return viewRedirect("jsp/manterCorrespondente/listarEnderecosCorrespondente", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String novoEnderecoCorrespondente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            String corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");

            if (responsavel.isCor()) {
                corCodigo = responsavel.getCorCodigo();
            }

            if (TextHelper.isNull(corCodigo)) {
                throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.correspondente.informar", responsavel);
            }

            String ecrCodigo = JspHelper.verificaVarQryStr(request, "ECR_CODIGO");
            EnderecoCorrespondente enderecoCorrespondente = null;

            if (!TextHelper.isNull(ecrCodigo)) {
                enderecoCorrespondente = correspondenteController.findEnderecoCorrespondenteByPKCorCodigo(ecrCodigo, corCodigo, responsavel);
            }

            List<TipoEndereco> tiposEndereco = correspondenteController.listAllTipoEndereco(responsavel);

            // Seta atributos no model
            model.addAttribute("lstTipoEndereco", tiposEndereco);
            model.addAttribute("COR_CODIGO", corCodigo);
            model.addAttribute("enderecoCorrespondente", enderecoCorrespondente);

            return viewRedirect("jsp/manterCorrespondente/editarEnderecoCorrespondente", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarEnderecoCorrespondente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            ParamSession paramSession = ParamSession.getParamSession(session);
            SynchronizerToken.saveToken(request);

            String corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");

            if (responsavel.isCor()) {
                corCodigo = responsavel.getCorCodigo();
            }

            if (TextHelper.isNull(corCodigo)) {
                throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.informar", responsavel);
            }

            String ecrCodigo = JspHelper.verificaVarQryStr(request, "ECR_CODIGO");

            String tieCodigo = JspHelper.verificaVarQryStr(request, "TIE_CODIGO");
            String ecrCep = JspHelper.verificaVarQryStr(request, "ECR_CEP");
            String ecrLogradouro = JspHelper.verificaVarQryStr(request, "ECR_LOGRADOURO");
            String ecrNumero = JspHelper.verificaVarQryStr(request, "ECR_NUMERO");
            String ecrComplemento = JspHelper.verificaVarQryStr(request, "ECR_COMPLEMENTO");
            String ecrBairro = JspHelper.verificaVarQryStr(request, "ECR_BAIRRO");
            String ecrUf = JspHelper.verificaVarQryStr(request, "ECR_UF");
            String ecrMunicipio = JspHelper.verificaVarQryStr(request, "ECR_MUNICIPIO");

            if (TextHelper.isNull(tieCodigo)) {
                throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.tipo.endereco.informar", responsavel);
            }

            if (TextHelper.isNull(ecrCep)) {
                throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.cep.informar", responsavel);
            }

            if (TextHelper.isNull(ecrLogradouro)) {
                throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.logradouro.informar", responsavel);
            }

            if (TextHelper.isNull(ecrNumero)) {
                throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.numero.informar", responsavel);
            }

            if (TextHelper.isNull(ecrBairro)) {
                throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.bairro.informar", responsavel);
            }

            if (TextHelper.isNull(ecrUf)) {
                throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.uf.informar", responsavel);
            }

            if (TextHelper.isNull(ecrMunicipio)) {
                throw new CorrespondenteControllerException("mensagem.erro.endereco.correspondente.municipio.informar", responsavel);
            }

            if (TextHelper.isNull(ecrCodigo)) {
                BigDecimal[] latitudeLongitude = GoogleMapsClient.buscaLatitudeLongitude(ecrLogradouro, ecrNumero, ecrBairro, ecrMunicipio, ecrUf, ecrCep, responsavel);
                correspondenteController.createEnderecoCorrespondente(corCodigo, tieCodigo, ecrLogradouro, ecrNumero, ecrComplemento, ecrBairro, ecrMunicipio, ecrUf, ecrCep, latitudeLongitude[0], latitudeLongitude[1], responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.endereco.correspondente.criado", responsavel));
            } else {
                EnderecoCorrespondente enderecoOld = correspondenteController.findEnderecoCorrespondenteByPKCorCodigo(ecrCodigo, corCodigo, responsavel);
                EnderecoCorrespondente endereco = new EnderecoCorrespondente();

                endereco.setEcrCodigo(ecrCodigo);
                Correspondente cor = new Correspondente();
                cor.setCorCodigo(corCodigo);
                endereco.setCorrespondente(cor);
                TipoEndereco tipoEndereco = new TipoEndereco();
                tipoEndereco.setTieCodigo(tieCodigo);
                endereco.setTipoEndereco(tipoEndereco);
                endereco.setEcrLogradouro(ecrLogradouro);
                endereco.setEcrNumero(ecrNumero);
                endereco.setEcrComplemento(ecrComplemento);
                endereco.setEcrBairro(ecrBairro);
                endereco.setEcrMunicipio(ecrMunicipio);
                endereco.setEcrUf(ecrUf);
                endereco.setEcrCep(ecrCep);

                if (!enderecoOld.equals(endereco)) {
                    BigDecimal[] latitudeLongitude = GoogleMapsClient.buscaLatitudeLongitude(ecrLogradouro, ecrNumero, ecrBairro, ecrMunicipio, ecrUf, ecrCep, responsavel);
                    correspondenteController.updateEnderecoCorrespondente(ecrCodigo, corCodigo, tieCodigo, ecrLogradouro, ecrNumero, ecrComplemento, ecrBairro, ecrMunicipio, ecrUf, ecrCep, latitudeLongitude[0], latitudeLongitude[1], responsavel);
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.endereco.correspondente.atualizado", responsavel));
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluirEnderecoCorrespondente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String ecrCodigo = JspHelper.verificaVarQryStr(request, "ECR_CODIGO");
            String corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");

            if (responsavel.isCor()) {
                corCodigo = responsavel.getCorCodigo();
            }

            correspondenteController.removeEnderecoCorrespondente(ecrCodigo, corCodigo, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.endereco.correspondente.removido", responsavel));

            return listarEnderecosCorrespondente(request, response, session, model);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private String getLinkAction() {
        return "../v3/manterEnderecosCorrespondente?acao=iniciar";
    }
}
