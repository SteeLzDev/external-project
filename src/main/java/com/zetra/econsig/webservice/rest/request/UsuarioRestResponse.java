package com.zetra.econsig.webservice.rest.request;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zetra.econsig.dto.TransferObject;

/**
 * <p>Title: UsuarioRestResponse</p>
 * <p>Description: Rest response com dados do usuario.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class UsuarioRestResponse {
    public String nome;
    public String cpf;
    public String email;
    public String token;
    public String usuCodigo;
    public String telefone;
    public String tipoEntidade;
    public String codigoEntidade;
    public String nomeEntidade;
    public String codigoEntidadePai;
    public String nomeEntidadePai;
    public String dataUltAcesso;
    public boolean requiresTOTP;
    public String imagem;
    public int diasExpiracaoSenha;
    public List<String> permissoes;
    public Map<String, String> nseCodigos;
    public List<TransferObject> naturezasCsa;
    public Map<String, String> camposSistema;
    public List<MensagemRestResponse> mensagens;

    public UsuarioRestResponse() {}
}
