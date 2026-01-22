package com.zetra.econsig.web.controller.rest;

import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: BuscarServicosRestController</p>
 * <p>Description: Buscar Servços Rest Controller</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 * */
@RestController
public class BuscarServicosRestController {

    @Autowired
    private ServicoController servicoController;


    @RequestMapping(value = "/v3/buscarServicos", method = RequestMethod.POST)
    public HashMap<String, String> buscarServicos(@RequestParam(value = "naturezaServico", required = true) String naturezaServico, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        HashMap<String, String> servicosHashMap = new HashMap<>();

        try {
            List<Servico> servicos = servicoController.findByNseCodigo(naturezaServico, responsavel);
            for (Servico servico : servicos) {
                servicosHashMap.put(servico.getSvcCodigo(), servico.getSvcDescricao());
            }
        } catch (ServicoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return servicosHashMap;
    }
}
