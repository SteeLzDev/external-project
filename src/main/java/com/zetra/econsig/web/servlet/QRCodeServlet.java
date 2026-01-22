package com.zetra.econsig.web.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GoogleAuthenticatorHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;

public class QRCodeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        HttpSession session = request.getSession();
        String usuChaveValidacaoTotp = (String) session.getAttribute(GoogleAuthenticatorHelper.CHAVE_VALIDACAO);
        String contexto = request.getContextPath();
        contexto = !TextHelper.isNull(contexto) ? "." + contexto.replaceAll("([\\\\\\/])", "") : "";
        String qrText = !TextHelper.isNull(usuChaveValidacaoTotp) ? "otpauth://totp/" + responsavel.getUsuLogin() + "@econsig" + contexto + "?secret=" + usuChaveValidacaoTotp : null;

        if (!TextHelper.isNull(qrText)) {
            ByteArrayOutputStream out = QRCode.from(qrText).to(ImageType.PNG).stream();

            response.setContentType("image/png");
            response.setContentLength(out.size());

            OutputStream outStream = response.getOutputStream();

            outStream.write(out.toByteArray());

            outStream.flush();
            outStream.close();

        }
    }
}
