package com.zetra.econsig.web.controller.rest;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: BuscarEnderecoRestController</p>
 * <p>Description: Buscar Endereço Rest Controller</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 * */
@RestController
public class BuscarEnderecoRestController {

    @Autowired
    private SistemaController sistemaController;

    @RequestMapping(value = "/v3/listarCidades", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TransferObject> buscarCidades(@RequestParam(value = "codEstado", required = false) String codEstado, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String termo = request.getParameter("name");

        List<TransferObject> cidades = null;

        try {
            cidades = sistemaController.lstCidadeUf(codEstado, termo, responsavel);
        } catch (ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return cidades;
    }
}







