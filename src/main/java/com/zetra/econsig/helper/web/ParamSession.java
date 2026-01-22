package com.zetra.econsig.helper.web;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: ParamSession</p>
 * <p>Description: Controle de parâmetros de sessão</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamSession implements Serializable {
    @Serial
    private static final long serialVersionUID = -3643521066985874978L;
    private final List<RegistroHistorico> historico;

    public static final String SESSION_ATTR_NAME = "paramSession";
    private static final int MAX_SIZE = 10;
    private static final String linkDefault = "../v3/carregarPrincipal";

    public ParamSession() {
        historico = new ArrayList<>();
    }

    public void addHistory(String link, Map<String, String[]> parametros) {
        RegistroHistorico novo = new RegistroHistorico(link, parametros);

        if (historico.size() > 0) {
            RegistroHistorico topo = historico.get(historico.size() - 1);
            String linkTopo = topo.getLink();
            if (topo != null && link.equals(linkTopo)) {
                boolean mesmaAcao = true;
                if (linkTopo.indexOf("/v3/") != -1 && !parametros.containsKey("pager")) {
                    String[] acaoTopo = topo.getParametro("acao");
                    String[] acaoAtual = parametros.get("acao");

                    if ((TextHelper.isNull(acaoTopo) && !TextHelper.isNull(acaoAtual)) ||
                            (!TextHelper.isNull(acaoTopo) && TextHelper.isNull(acaoAtual)) ||
                            (!TextHelper.isNull(acaoTopo) && !TextHelper.isNull(acaoAtual) && !acaoTopo[0].equals(acaoAtual[0]))) {
                        mesmaAcao = false;
                    }
                }

                if (mesmaAcao) {
                    // Remove o elemento do topo
                    historico.remove(historico.size() - 1);
                }
            }
        }

        // Adiciona o novo histórico
        historico.add(novo);

        if (historico.size() > MAX_SIZE) {
            // Se o histórico excedeu o tamanho máximo, remove o elemento
            // do inicio da pilha
            historico.remove(0);
        }
    }

    private String getHistory(int position) {
        int index = historico.size() + position;
        if (index <= historico.size() && index > 0) {
            return historico.get(index - 1).getURL();
        } else {
            return linkDefault;
        }
    }

    public String getCurrentHistory(){
        return getHistory(0);
    }

    public String getLastHistory() {
        return getHistory( -1);
    }

    public String getLastHistoryNoContext(HttpServletRequest request) {
        String url = getLastHistory();
        String context = request.getContextPath();
        return url.substring(url.indexOf(context) + context.length());
    }

    public String getLastRelativeHistory(String context) {
        String url = getLastHistory();
        if (!TextHelper.isNull(context)) {
            url = url.replace(context, "..");
        }
        return url;
    }

    public void back() {
        if (!historico.isEmpty()) {
            historico.remove(historico.size() - 1);
        }
        if (!historico.isEmpty()) {
            historico.remove(historico.size() - 1);
        }
    }

    /**
     * Volta para o primeiro ítem do histórico
     */
    public void backToFirst() {
        while (historico.size() > 1){
            if (!historico.isEmpty()) {
                historico.remove(historico.size() - 1);
            }
        }
    }


    public void halfBack() {
        if (!historico.isEmpty()) {
            historico.remove(historico.size() - 1);
        }
    }

    @Override
    public String toString() {
        String retorno = "";
        Iterator<RegistroHistorico> it = historico.iterator();
        while (it.hasNext()) {
            retorno += it.next().getURL() + "\r\n";
        }
        return retorno;
    }

    public static ParamSession getParamSession(HttpSession session) {
        ParamSession paramSession = (ParamSession) session.getAttribute(ParamSession.SESSION_ATTR_NAME);
        if (paramSession == null) {
            paramSession = new ParamSession();
            session.setAttribute(ParamSession.SESSION_ATTR_NAME, paramSession);
        }
        return paramSession;
    }
}

class RegistroHistorico implements Serializable {
    private final String link;
    private final Map<String, String[]> parametros;

    public RegistroHistorico(String link, Map<String, String[]> parametros) {
        this.link = link;
        this.parametros = parametros;
    }

    public String getLink() {
        return link;
    }

    public String[] getParametro(String nome) {
        if (parametros != null) {
            return parametros.get(nome);
        }
        return null;
    }

    public String getURL() {
        if (parametros != null) {
            parametros.remove("pager");
        }


        String linkRetorno = JspHelper.makeURL(link, parametros);

        if (parametros != null && parametros.size() > 0) {
            linkRetorno += "&back=1";
        } else {
            linkRetorno += "?back=1";
        }

        return linkRetorno;
    }

    public boolean equals(RegistroHistorico outro) {
        return getURL().equals(outro.getURL());
    }
}