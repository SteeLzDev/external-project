package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: UsuarioRestRequest</p>
 * <p>Description: Requisição Rest de dados do usuário.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class UsuarioRestRequest {
    public String rseCodigo;

    public String login;

    public String matricula;

    public String senha;

    @Deprecated
    public String cpf;

    public String orgCodigo;

    public String estCodigo;

    public String usuCodigo;

    public String token;

    public String email;

    public String telefone;

    public String celular;

    public String otp;

    public String novaSenha;

    public String confirmarSenha;

    public String endereco;

    public String bairro;

    public String cidade;

    public String complemento;

    public String numero;

    public String cep;

    public String uf;

    public String fotoFrente;

    public String fotoEsquerda;

    public String fotoDireita;

    // Se a requisição chegou de uma chamada do primeiro acesso
    public boolean primeiroAcesso;

    //Se a requisição chegou de uma chamada do totem
    public boolean totem = false;

    //e-mail ou cpf
    public String id;

    // Se a requisição chegou de uma chamada mobile
    public boolean mobile = false;

    public boolean retornaApenasSrsAtivo = false;
}
