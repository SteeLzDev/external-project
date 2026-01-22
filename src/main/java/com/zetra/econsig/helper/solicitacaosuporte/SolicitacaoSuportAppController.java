package com.zetra.econsig.helper.solicitacaosuporte;

import java.io.IOException;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SolicitacaoSuporteAPIException;

public class SolicitacaoSuportAppController {

    private static SolicitacaoSuporteAPI solicitacaoAPI;

    private static SolicitacaoSuporteAPI getSolicitacaoSuporteAPI() {
        if (solicitacaoAPI == null) {
            SolicitacaoSuporteAPIFactory factory = new SolicitacaoSuporteAPIFactory();
            solicitacaoAPI = factory.getSolicitacaoSuporteAPI();
        }

        return solicitacaoAPI;
    }

    public static Map<String, String> getValoresCampo(String campo) throws SolicitacaoSuporteAPIException, IOException {
        SolicitacaoSuporteAPI solicitacaoAPI = getSolicitacaoSuporteAPI();

        return solicitacaoAPI.getValoresCampos(campo);
    }

    public static String criarSolicicataoSuporte(TransferObject solicitacaoSuporte) throws SolicitacaoSuporteAPIException, IOException {
        SolicitacaoSuporteAPI solicitacaoAPI = getSolicitacaoSuporteAPI();

        return solicitacaoAPI.criarSolicitacaoSuporte(solicitacaoSuporte);
    }

    public static TransferObject findSolicitacaoSuporte(String chave) throws SolicitacaoSuporteAPIException, IOException {
        SolicitacaoSuporteAPI solicitacaoAPI = getSolicitacaoSuporteAPI();

        return solicitacaoAPI.findByChave(chave);
    }

}
