package com.zetra.econsig.web.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nl.captcha.audio.AudioCaptcha;
import nl.captcha.servlet.CaptchaServletUtil;

/**
 * <p>Title: AudioCaptchaServlet</p>
 * <p>Description: Servlet para Geração de Captcha em Audio.</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AudioCaptchaServlet extends HttpServlet {
    public static final String AUDIO_CAPTCHA_SESSION_KEY = "AUDIO_CAPTCHA_ANSWER";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String agent = (req != null ? req.getHeader("user-agent") : null);

        if (agent == null || !agent.equalsIgnoreCase("GbPlugin")) {

            AudioCaptcha current = new AudioCaptcha.Builder()
                .addAnswer()
                .addNoise()
                .build()
            ;
            req.getSession().setAttribute(AUDIO_CAPTCHA_SESSION_KEY, current.getAnswer());
            CaptchaServletUtil.writeAudio(resp, current.getChallenge());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
