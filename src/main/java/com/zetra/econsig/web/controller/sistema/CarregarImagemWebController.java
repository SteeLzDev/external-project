package com.zetra.econsig.web.controller.sistema;

import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zetra.econsig.helper.sistema.ViewImageHelper;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: CarregarImagemWebController</p>
 * <p>Description: Controlador para buscar imagens dinâmicamente via ajax</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 27276 $
 * $Date: 2019-07-18 09:44:31 -0300 (qui, 18 jul 2019) $
 */
@RestController
public class CarregarImagemWebController extends AbstractWebController {

    @RequestMapping(value = "/v3/imagemCarousel", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> buscarImagemEmpresaCarousel(HttpServletRequest request) throws SQLException {
        String nseCodigo = request.getParameter("nseCodigo");

        String imageContent = ViewImageHelper.getInstance().buscarImagemEmpresa(nseCodigo);

        if (imageContent != null) {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<>(imageContent, headers, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
