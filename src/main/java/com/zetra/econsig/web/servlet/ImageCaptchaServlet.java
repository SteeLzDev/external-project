package com.zetra.econsig.web.servlet;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.zetra.econsig.config.SysConfig;
import com.zetra.econsig.helper.texto.TextHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.renderer.DefaultWordRenderer;

/**
 * <p>Title: ImageCaptchaServlet</p>
 * <p>Description: Servlet para Geração de Captcha em Imagem.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas, Douglas Neves, Leonel Martins
 */
public class ImageCaptchaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String IMAGE_CAPTCHA_SESSION_KEY = "IMAGE_CAPTCHA_ANSWER";

    private static final ConcurrentHashMap<String, String> armazenaCaptchaHashMap = new ConcurrentHashMap<>();

    private static final List<Font> CAPTCHA_FONTS = Arrays.asList(
            new Font("Verdana", Font.BOLD | Font.ITALIC, 40),
            new Font("SansSerif", Font.BOLD, 38),
            new Font("Serif", Font.ITALIC, 35),
            new Font("Arial", Font.BOLD | Font.ITALIC, 40),
            new Font("Helvetica", Font.ITALIC, 38),
            new Font("Liberation Sans", Font.BOLD, 35),
            new Font("Courier New", Font.BOLD | Font.ITALIC, 40),
            new Font("Monospaced", Font.BOLD, 38),
            new Font("Times New Roman", Font.ITALIC, 35)
    );

    private static final List<Color> CAPTCHA_COLORS = Arrays.asList(
            Color.BLACK,
            Color.BLACK,
            Color.BLACK,
            Color.BLACK,
            Color.BLUE
    );

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /**
         * Verifica o Agente que está requisitando o Captcha. Se for o Plugin
         * de segurança dos Bancos Brasileiros, então não repassa a geração,
         * para evitar que um novo código seja colocado na sessão diferente
         * daquele exibido ao usuário.
         */

        if (req != null) {
            final String agent = req.getHeader("user-agent");
            if ((agent == null) || !agent.equalsIgnoreCase("GbPlugin")) {
                final Captcha captcha = new Captcha.Builder(200, 50)
                    .addText(new DefaultWordRenderer(CAPTCHA_COLORS, CAPTCHA_FONTS))
                    .addBackground(new GradiatedBackgroundProducer(Color.LIGHT_GRAY, Color.WHITE))
                    .addNoise()
                    .addBorder()
                    .build()
                ;

                req.getSession().setAttribute(IMAGE_CAPTCHA_SESSION_KEY, SysConfig.isTestProfile() ? "_test_" : captcha.getAnswer());
                CaptchaServletUtil.writeImage(resp, captcha.getImage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public static boolean armazenaCaptcha(String session, String codigoCaptcha) {
        /**
         * Armazena o código do captcha da sessão, quando ele existir.
         * Caso não exista, retorna falso.
         */
         if (!TextHelper.isNull(codigoCaptcha)) {
             armazenaCaptchaHashMap.put(session, codigoCaptcha);
             return true;
         }
         return false;
    }

    public static boolean validaCaptcha(String session, String respostaUsuarioCaptcha) {
        /**
         * Verifica se a sessão possui um captcha armazenado e o compara ao código inserido pelo usuário.
         * Caso aquela não possua ou a resposta do usuário esteja diferente, retorna falso.
         */
        return armazenaCaptchaHashMap.containsKey(session) ? armazenaCaptchaHashMap.get(session).equalsIgnoreCase(respostaUsuarioCaptcha) : false;
    }
}
