package com.zetra.econsig.values;

/**
 * <p>Title: MetodoSenhaExternaEnum</p>
 * <p>Description: Enumeração de métodos de senha externa.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum MetodoSenhaExternaEnum {

/*
    NENHUM: não utiliza nenhum método externo (bypass do metodo buscarSenha)
     QUERY: busca em banco de dados
   NRH7UI9: busca em mainframe usando a classe Nrh7ui9
    NRH7UE: busca em mainframe usando a classe Nrh7ue01 ou a classe Nrh7ue09
        AD: busca em Active Directory usando a classe AD
    SOAPDI: busca usando o cliente SOAP DynamicInvoker
HTTPCLIENT: busca usando cliente HTTP
      JAVA: busca usando uma classe java que implementa a interface com.zetra.senhaexterna.ValidarSenhaExterna
    OAUTH2: chama uma URL para autenticação, recebe o token e valida o token
*/

    NENHUM("NENHUM"),
    DB("QUERY"),
    NRH7UI9("NRH7UI9"),
    NRH7UE("NRH7UE"),
    AD("AD"),
    SOAPDI("SOAPDI"),
    SOAP("SOAP"),
    HTTPCLIENT("HTTPCLIENT"),
    JAVA("JAVA"),
    OAUTH2("OAUTH2"),
    ;

    private final String metodo;

    private MetodoSenhaExternaEnum(String metodo) {
        this.metodo = metodo;
    }

    public String getMetodo() {
        return metodo;
    }
}
