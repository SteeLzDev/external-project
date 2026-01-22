package com.zetra.econsig.web.controller.beneficio;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.NaturezaServicoControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.BeneficioServico;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.TipoBeneficiario;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servico.NaturezaServicoController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaConsignatariaEnum;
import com.zetra.econsig.web.controller.AbstractWebController;
/**
 * <p>Title: AlterarBeneficioWebController</p>
 * <p>Description:Alterar benefícios(Editar, novo e excluir)</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/alterarBeneficio" })
public class AlterarBeneficioWebController extends AbstractWebController {

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private NaturezaServicoController naturezaServicoController;

    @Autowired
    private BeneficioController beneficioController;

    @RequestMapping(params = { "acao=novo" })
    public String novoBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        List<Consignataria> operadoras = new ArrayList<>();
        List<NaturezaServico> naturezas = new ArrayList<>();
        List<CustomTransferObject> servicosNatureza = new ArrayList<>();
        List<TransferObject> tipoBeneficiario = new ArrayList<>();

        try {
            operadoras = consignatariaController.lstConsignatariaByNcaCodigo(NaturezaConsignatariaEnum.OPERADORA_BENEFICIOS.getCodigo(), responsavel);

            naturezas = naturezaServicoController.listaNaturezas(responsavel);

            CustomTransferObject criterio = new CustomTransferObject();
            tipoBeneficiario = beneficioController.listaTipoBeneficiario(criterio, responsavel);

        } catch (ConsignatariaControllerException | NaturezaServicoControllerException | BeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<CustomTransferObject> operadorasBeneficios = new ArrayList<>();

        for (Consignataria consig : operadoras) {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.CSA_IDENTIFICADOR, consig.getCsaIdentificador());
            cto.setAttribute(Columns.CSA_CODIGO, consig.getCsaCodigo());
            cto.setAttribute(Columns.CSA_NOME, consig.getCsaNome());
            operadorasBeneficios.add(cto);
        }

        List<CustomTransferObject> naturezasServico = new ArrayList<>();

        for (NaturezaServico natureza : naturezas) {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.NSE_CODIGO, natureza.getNseCodigo());
            cto.setAttribute(Columns.NSE_DESCRICAO, natureza.getNseDescricao());
            naturezasServico.add(cto);
        }

        model.addAttribute("tipoBeneficiario", tipoBeneficiario);
        model.addAttribute("servicos", servicosNatureza);
        model.addAttribute("podeEditar", true);
        model.addAttribute("novo", true);
        model.addAttribute("beneficio", new Beneficio());
        model.addAttribute("operadoras", operadorasBeneficios);
        model.addAttribute("naturezas", naturezasServico);

        return viewRedirect("jsp/manterBeneficio/alterarBeneficio", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editarBeneficio(@RequestParam(value = "benCodigo", required = false) String benCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServicoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Variavel que vem do método salvarBeneficio (quando o beneficio é novo)
        if (TextHelper.isNull(benCodigo)) {
            // Alterando um beneficio ja existente
            benCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO));
        }

        Beneficio beneficio = null;
        List<Consignataria> operadoras = new ArrayList<>();
        List<NaturezaServico> naturezas = new ArrayList<>();
        List<Servico> servicos = new ArrayList<>();
        List<TransferObject> tipoBeneficiario = new ArrayList<>();

        try {
            beneficio = beneficioController.findBeneficioFetchBeneficioServicoByCodigo(benCodigo, responsavel);

            operadoras = consignatariaController.lstConsignatariaByNcaCodigo(NaturezaConsignatariaEnum.OPERADORA_BENEFICIOS.getCodigo(), responsavel);

            naturezas = naturezaServicoController.listaNaturezas(responsavel);

            servicos = servicoController.findByNseCodigo(beneficio.getNaturezaServico().getNseCodigo(), responsavel);

            CustomTransferObject criterio = new CustomTransferObject();
            tipoBeneficiario = beneficioController.listaTipoBeneficiario(criterio, responsavel);

        } catch (BeneficioControllerException | ConsignatariaControllerException | NaturezaServicoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<CustomTransferObject> operadorasBeneficios = new ArrayList<>();

        for (Consignataria consig : operadoras) {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.CSA_IDENTIFICADOR, consig.getCsaIdentificador());
            cto.setAttribute(Columns.CSA_CODIGO, consig.getCsaCodigo());
            cto.setAttribute(Columns.CSA_NOME, consig.getCsaNome());
            operadorasBeneficios.add(cto);
        }

        List<CustomTransferObject> naturezasServico = new ArrayList<>();

        for (NaturezaServico natureza : naturezas) {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.NSE_CODIGO, natureza.getNseCodigo());
            cto.setAttribute(Columns.NSE_DESCRICAO, natureza.getNseDescricao());
            naturezasServico.add(cto);
        }

        List<CustomTransferObject> servicosNatureza = new ArrayList<>();

        List<BeneficioServico> beneficioServico = new ArrayList<>(beneficio.getBeneficioServicoSet());

        for (Servico servico : servicos) {
            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.SVC_CODIGO, servico.getSvcCodigo());
            cto.setAttribute(Columns.SVC_DESCRICAO, servico.getSvcDescricao());
            servicosNatureza.add(cto);
        }

        beneficioServico.sort((m1, m2) -> {
            if (m1.getBseOrdem() > m2.getBseOrdem()) {
                return 0;
            }
            return -1;
        });

        model.addAttribute("tipoBeneficiario", tipoBeneficiario);
        model.addAttribute("beneficioServico", beneficioServico);
        model.addAttribute("servicos", servicosNatureza);
        model.addAttribute("novo", false);
        model.addAttribute("podeEditar", true);
        model.addAttribute("beneficio", beneficio);
        model.addAttribute("operadoras", operadorasBeneficios);
        model.addAttribute("naturezas", naturezasServico);

        return viewRedirect("jsp/manterBeneficio/alterarBeneficio", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluirBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String benCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO));
            Beneficio beneficio = new Beneficio();
            beneficio.setBenCodigo(benCodigo);
            beneficioController.remove(beneficio, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.remover.beneficio.exibicao.sucesso", responsavel));
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.beneficio.erro.remover.beneficio", responsavel));
        }
        return "forward:/v3/listarBeneficio?acao=listar&_skip_history_=true";
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException, ServicoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        Consignataria csa = new Consignataria();
        csa.setCsaCodigo(JspHelper.verificaVarQryStr(request, Columns.CSA_CODIGO));

        NaturezaServico nse = new NaturezaServico();
        nse.setNseCodigo(JspHelper.verificaVarQryStr(request, Columns.NSE_CODIGO));

        //Será preenchido se o benefício for novo
        String benCodigo = "";
        String msg = "";

        Beneficio beneficio = new Beneficio();
        Map<String, List<BeneficioServico>> relacionamentos = new HashMap<>();

        //Verifica se existe serviços a serem vinculados ao benefício
        if (!JspHelper.verificaVarQryStr(request, "servicos").equals("")) {

            String servicosRequest = JspHelper.verificaVarQryStr(request, "servicos");

            String[] svcAndTibList = servicosRequest.split(",");

            for (String svcAndTib : svcAndTibList) {
                BeneficioServico relacionamentoBeneficioServico = new BeneficioServico();
                Servico svc = new Servico();
                svc.setSvcCodigo(svcAndTib.split(";")[0]);
                relacionamentoBeneficioServico.setServico(svc);
                TipoBeneficiario tib = new TipoBeneficiario();
                tib.setTibCodigo(svcAndTib.split(";")[1]);
                relacionamentoBeneficioServico.setTipoBeneficiario(tib);

                if (relacionamentos.get(svcAndTib.split(";")[1]) != null) {
                    relacionamentos.get(svcAndTib.split(";")[1]).add(relacionamentoBeneficioServico);
                } else {
                    relacionamentos.put(svcAndTib.split(";")[1], new ArrayList<BeneficioServico>());
                    relacionamentos.get(svcAndTib.split(";")[1]).add(relacionamentoBeneficioServico);
                }
            }
        }

        if (!JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO)).equals("")) {

            beneficio.setBenCodigo(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO)));
            beneficio.setConsignataria(csa);
            beneficio.setNaturezaServico(nse);
            beneficio.setBenDescricao(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_DESCRICAO)));
            beneficio.setBenCodigoPlano(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO_PLANO)));
            beneficio.setBenCodigoRegistro(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO_REGISTRO)));
            beneficio.setBenCodigoContrato(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO_CONTRATO)));
            beneficio.setBenAtivo(Short.valueOf(JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_ATIVO))));

            try {
                if (JspHelper.verificaVarQryStr(request, "ignorarRelacionamentoBeneficioServidor").equals("true")) {
                    beneficioController.update(beneficio, responsavel);
                } else {
                    beneficioController.update(beneficio, relacionamentos, responsavel);
                }
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.beneficio.exibicao.sucesso", responsavel));
            } catch (BeneficioControllerException ex) {
                msg += ex.getMessage();
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

        } else {
            //Cria um novo benefício
            try {
                Beneficio ben = beneficioController.create(csa, nse, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_DESCRICAO)), JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO_PLANO)), JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO_REGISTRO)), JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO_CONTRATO)), relacionamentos, responsavel);

                benCodigo = ben.getBenCodigo();

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.novo.beneficio.exibicao.sucesso", responsavel));
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        return editarBeneficio(benCodigo, request, response, session, model);
    }
}