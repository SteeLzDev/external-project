package com.zetra.econsig.web.controller.ajuda;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.AjudaController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>Title: VisualizarAjudaRecursoWebController</p>
 * <p>Description: Controlador Web para Passo a Passo eConsig.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: larissa.silva $
 * $Revision: 23282 $
 * $Date: 2017-12-22 19:55:46 -0200 (Sex, 22 dez 2017) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/visualizarAjudaRecurso" })
public class VisualizarAjudaRecursoWebController extends AbstractWebController {

    @Autowired
    private AjudaController ajudaController;

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = { "acao=visualizarAjudaRecurso" })
    @ResponseBody
    public ResponseEntity<String> visualizarAjudaRecurso(@RequestParam(value = "acrCodigo", required = true)
    String acrCodigo, HttpServletRequest request) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            final JsonObjectBuilder raiz = Json.createObjectBuilder();
            final JsonArrayBuilder array = Json.createArrayBuilder();

            final TransferObject acessoUsuario = parametroController.getAcessoUsuario(acrCodigo, responsavel);
            final int acuNumeroAcesso = (acessoUsuario != null && !TextHelper.isNull(acessoUsuario.getAttribute(Columns.ACU_USU_NUMERO_ACESSO)) ? (int) acessoUsuario.getAttribute(Columns.ACU_USU_NUMERO_ACESSO) : 0);

            // Se só tem um acesso (o corrente) então exibe a ajuda do recurso
            if (acuNumeroAcesso <= 1) {
                final List<TransferObject> ajudaRecurso = ajudaController.listarAjudaRecurso(acrCodigo, responsavel);
                for (TransferObject to : ajudaRecurso) {
                    final JsonObjectBuilder ajudaJson = Json.createObjectBuilder();

                    if (!TextHelper.isNull(to.getAttribute(Columns.AJR_ELEMENTO))) {
                        ajudaJson.add("element", to.getAttribute(Columns.AJR_ELEMENTO).toString());
                    }

                    final JsonObjectBuilder popover = Json.createObjectBuilder();

                    if (!TextHelper.isNull(to.getAttribute(Columns.AJR_TEXTO))) {
                        popover.add("description", to.getAttribute(Columns.AJR_TEXTO).toString());
                    }

                    if (!TextHelper.isNull(to.getAttribute(Columns.AJR_POSICAO))) {
                        popover.add("side", to.getAttribute(Columns.AJR_POSICAO).toString());
                    }

                    ajudaJson.add("popover", popover);
                    array.add(ajudaJson);
                }

                raiz.add("ajudaRecursos", array);
                raiz.add("exibeAjudaRecurso", true);

            } else {

                raiz.add("ajudaRecursos", array);
                raiz.add("exibeAjudaRecurso", false);
            }

            return new ResponseEntity<>(raiz.build().toString(), HttpStatus.OK);

        } catch (ConsignanteControllerException | ParametroControllerException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
