package com.zetra.econsig.web.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.webservice.CamposAPI;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * <p>Title: XSSPreventionFilter</p>
 * <p>Description: Filtro de requisição utilizado para prevenir ataques
 * de Cross Site Scripting (XSS) através da inclusão de comandos
 * HTML em campos texto.</p>
 * <p>Copyright: Copyright (c) 2002-2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class XSSPreventionFilter extends EConsigFilter {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(XSSPreventionFilter.class);

    private static final String[] FORBIDDEN_WORDS = {
            "onclick",
            "onmouseover",
            "onmouseout",
            "onmousedown",
            "onmouseup",
            "onmousemove",
            "onkeyup",
            "onkeydown",
            "onfocus",
            "onsubmit",
            "onblur",
            "onchange",
            "onload",
            "onunload",
            "onresize",
    };

    class XSSRequestWrapper extends HttpServletRequestWrapper {

        public XSSRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String original = super.getParameter(name);
            String validado = null;
            if (original != null) {
                validado = stripXSS(original);
                // Tratamento especial para os parâmetros que são links de redirecionamento
                if((name.equals("linkRet") || name.equals("linkRet64")) && validado.length()>0){
                	String caminho = validado;
                    if (name.equals("linkRet64")){
                    	caminho = TextHelper.decode64(validado);
                    }
                    // Se for um link com um prococolo (http, https, ftp, etc), necessita conter ":" ou "//"
                    // Paths relativos e para uma página do sistema não terão este caracterers
                    if (caminho.contains(":") || caminho.startsWith("//")){
                    	validado = "";
                    }
                }
                log(name, original, validado);
            }
            return validado;
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] original = super.getParameterValues(name);
            String[] validado = null;
            if (original != null) {
                validado = new String[original.length];
                for (int i = 0; i < original.length; i++) {
                    validado[i] = stripXSS(original[i]);
                    log(name, original[i], validado[i]);
                }
            }
            return validado;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            Set<String> nomes = new HashSet<>();
            Enumeration<String> parametros = super.getParameterNames();
            while (parametros.hasMoreElements()) {
                String nomeParametro = parametros.nextElement();
                String validado = stripXSS(nomeParametro);
                nomes.add(validado);
            }
            return Collections.enumeration(nomes);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> validado = new HashMap<>();
            Enumeration<String> parametros = getParameterNames();
            while (parametros.hasMoreElements()) {
                String nomeParametro = parametros.nextElement();
                validado.put(nomeParametro, getParameterValues(nomeParametro));
            }
            return validado;
        }

        /**
         * Verifica se o valor do parâmetro foi alterado, o que significa que pode ser uma potencial
         * tentativa de ataque. Registra log de erro de segurança.
         * @param nomeParametro
         * @param vlrOriginal
         * @param vlrValidado
         */
        private void log(String nomeParametro, String vlrOriginal, String vlrValidado) {
            //alguns softwares incorporam contrabarra nos recursos onde não havia originalmente
            if (vlrOriginal != null && vlrValidado != null && !(vlrOriginal.replaceAll("\\\\", "").equals(vlrValidado.replaceAll("\\\\", "")))) {
                // Verifica no controle da requisição se já foi gerado log para o campo com erro.
                // É necessário para evitar geração dupla de log de erro caso o mesmo campo seja
                // validado mais de uma vez.
                Set<String> logsGerados = (Set<String>) getAttribute("__logged_error__");
                if (logsGerados == null) {
                    logsGerados = new HashSet<>();
                    setAttribute("__logged_error__", logsGerados);
                }
                if (!logsGerados.contains(nomeParametro)) {
                    logsGerados.add(nomeParametro);
                    try {
                        AcessoSistema responsavel = JspHelper.getAcessoSistema(this);
                        LogDelegate log = new LogDelegate(responsavel, Log.GERAL, null, Log.LOG_ERRO_SEGURANCA);
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.envio.parametro.inseguro", responsavel));
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.recurso.arg0", responsavel, getRecurso(this)));
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parametro.arg0.arg1", responsavel, nomeParametro, vlrOriginal));
                        log.write();
                    } catch (LogControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String recurso = getRecurso(httpRequest);
        if (recurso.equals("/xml/requisicao.jsp")) {
            // O recurso de acesso a Host-Host XML ou Lote aceita parâmetros
            // em formato XML que são bloqueados pelo filtro XSS. A interface
            // XML e Soap utilizam o método stripXSS(Map) para tratar os
            // parâmetros da operação Host-Host.
            chain.doFilter(request, response);
        } else {
            XSSRequestWrapper wrapper = new XSSRequestWrapper(httpRequest);
            chain.doFilter(wrapper, response);
        }
    }

    /**
     * Realiza o tratamento no campo "value" removendo qualquer
     * tag HTML que exista, mesmo estando codificada com conjunto
     * de caracteres Unicode, Decimal ou HTML. Se a tag HTML possuir
     * texto de conteúdo, o texto será mantido.
     * @param value
     * @return
     */
    public static String stripXSS(String value) {
        String cleanValue = null;
        if (value != null) {
            // Evita caracteres null
            cleanValue = value.replaceAll("\0", "");

            // Realiza unescape até que nenhuma nova alteração seja feita
            String lastValue = null;
            do {
                lastValue = cleanValue;
                cleanValue = StringEscapeUtils.unescapeHtml4(cleanValue);
                cleanValue = StringEscapeUtils.unescapeJava(cleanValue);
                cleanValue = removeForbiddenWords(cleanValue);
            } while (!lastValue.equals(cleanValue));

            // Se o resultado contém tags HTML, então realiza a remoção
            // dos conteúdos das tags, caso existam
            // OBS: a cláusula cleanValue.contains("<") serve para resolver Bug no Jsoup.isValid
            // com relação às tags <frame> e <frameset> que são erroneamente aceitas.
            if (!Jsoup.isValid(cleanValue, Safelist.none()) || cleanValue.contains("<")) {
                cleanValue = Jsoup.parse(cleanValue).text();

                // Após remover os conteúdos, eles podem formar novas tags
                // html ou até estarem codificados. Executa recursivamente
                // o método novamente.
                if (!cleanValue.isEmpty() && !cleanValue.equals(value)) {
                    return stripXSS(cleanValue);
                }
            }
        }
        return cleanValue;
    }

    /**
     * Realiza o tratamento em um Mapa Chave->Valor, tanto na chave
     * quanto no valor, evitando a inclusão de XSS.
     * @param values
     * @return
     */
    public static Map<String, Object> stripXSS(Map<String, Object> values) {
        Map<String, Object> cleanValues = null;
        if (values != null) {
            cleanValues = new HashMap<>(values.size());
            for (String key : values.keySet()) {
                Object value = values.get(key);
                if (value != null && value instanceof String) {
                    // Se for String, realiza o tratamento anti-XSS
                    cleanValues.put(stripXSS(key), stripXSS((String) value));
                } else {
                    // Se é nulo, ou não é String, adiciona o valor original
                    cleanValues.put(stripXSS(key), value);
                }
            }
        }
        return cleanValues;
    }

    public static Map<CamposAPI, Object> stripXSS_API(Map<CamposAPI, Object> values) {
        Map<CamposAPI, Object> cleanValues = null;
        if (values != null) {
            cleanValues = new HashMap<>(values.size());
            for (CamposAPI key : values.keySet()) {
                Object value = values.get(key);
                if (value != null && value instanceof String) {
                    // Se for String, realiza o tratamento anti-XSS
                    cleanValues.put(key, stripXSS((String) value));
                } else {
                    // Se é nulo, ou não é String, adiciona o valor original
                    cleanValues.put(key, value);
                }
            }
        }
        return cleanValues;
    }

    private static String removeForbiddenWords(String value) {
        for (String forbidden : FORBIDDEN_WORDS) {
            value = value.replaceAll("(?i)" + forbidden, "");
        }
        return value;
    }
}