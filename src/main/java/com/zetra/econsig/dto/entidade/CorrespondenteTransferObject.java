package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: CorrespondenteTransferObject</p>
 * <p>Description: TransferObject do correspondente</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class CorrespondenteTransferObject extends CustomTransferObject {

    public CorrespondenteTransferObject() {
        super();
    }

    public CorrespondenteTransferObject(String corCodigo) {
        this();
        setAttribute(Columns.COR_CODIGO, corCodigo);
    }

    public CorrespondenteTransferObject(CorrespondenteTransferObject correspondente) {
        this();
        setAtributos(correspondente.getAtributos());
    }

    // Getter
    public String getCorCodigo() {
        return (String) getAttribute(Columns.COR_CODIGO);
    }

    public String getCsaCodigo() {
        return (String) getAttribute(Columns.COR_CSA_CODIGO);
    }

    public String getCorNome() {
        return (String) getAttribute(Columns.COR_NOME);
    }

    public String getCorNomeIdentificador() {
        return (String) getAttribute(Columns.COR_NOME) + " - " + (String) getAttribute(Columns.COR_IDENTIFICADOR);
    }
    
    public String getCorEmail() {
        return (String) getAttribute(Columns.COR_EMAIL);
    }

    public String getCorResponsavel() {
        return (String) getAttribute(Columns.COR_RESPONSAVEL);
    }

    public String getCorResponsavel2() {
        return (String) getAttribute(Columns.COR_RESPONSAVEL_2);
    }

    public String getCorResponsavel3() {
        return (String) getAttribute(Columns.COR_RESPONSAVEL_3);
    }

    public String getCorRespCargo() {
        return (String) getAttribute(Columns.COR_RESP_CARGO);
    }

    public String getCorRespCargo2() {
        return (String) getAttribute(Columns.COR_RESP_CARGO_2);
    }

    public String getCorRespCargo3() {
        return (String) getAttribute(Columns.COR_RESP_CARGO_3);
    }

    public String getCorRespTelefone() {
        return (String) getAttribute(Columns.COR_RESP_TELEFONE);
    }

    public String getCorRespTelefone2() {
        return (String) getAttribute(Columns.COR_RESP_TELEFONE_2);
    }

    public String getCorRespTelefone3() {
        return (String) getAttribute(Columns.COR_RESP_TELEFONE_3);
    }

    public String getCorLogradouro() {
        return (String) getAttribute(Columns.COR_LOGRADOURO);
    }

    public Integer getCorNro() {
        return (Integer) getAttribute(Columns.COR_NRO);
    }

    public String getCorCompl() {
        return (String) getAttribute(Columns.COR_COMPL);
    }

    public String getCorBairro() {
        return (String) getAttribute(Columns.COR_BAIRRO);
    }

    public String getCorCidade() {
        return (String) getAttribute(Columns.COR_CIDADE);
    }

    public String getCorUf() {
        return (String) getAttribute(Columns.COR_UF);
    }

    public String getCorCep() {
        return (String) getAttribute(Columns.COR_CEP);
    }

    public String getCorTel() {
        return (String) getAttribute(Columns.COR_TEL);
    }

    public String getCorFax() {
        return (String) getAttribute(Columns.COR_FAX);
    }

    public String getCorIdentificador() {
        return (String) getAttribute(Columns.COR_IDENTIFICADOR);
    }

    public Short getCorAtivo() {
        return (Short) getAttribute(Columns.COR_ATIVO);
    }

    public String getCorCnpj() {
        return (String) getAttribute(Columns.COR_CNPJ);
    }

    public String getCorIdentificadorAntigo() {
        return (String) getAttribute(Columns.COR_IDENTIFICADOR_ANTIGO);
    }

    public String getCorIPAcesso() {
        return (String) getAttribute(Columns.COR_IP_ACESSO);
    }

    public String getCorDDNSAcesso() {
        return (String) getAttribute(Columns.COR_DDNS_ACESSO);
    }
    
    public String getCorExigeEnderecoAcesso() {
        return (String) getAttribute(Columns.COR_EXIGE_ENDERECO_ACESSO);
    }
    
    public String getEcoCodigo() {
        return (String) getAttribute(Columns.COR_ECO_CODIGO);
    }
    
    // Setter
    public void setCsaCodigo(String csaCodigo) {
        setAttribute(Columns.COR_CSA_CODIGO, csaCodigo);
    }

    public void setCorNome(String corNome) {
        setAttribute(Columns.COR_NOME, corNome);
    }

    public void setCorEmail(String corEmail) {
        setAttribute(Columns.COR_EMAIL, corEmail);
    }

    public void setCorResponsavel(String corResponsavel) {
        setAttribute(Columns.COR_RESPONSAVEL, corResponsavel);
    }

    public void setCorResponsavel2(String corResponsavel2) {
        setAttribute(Columns.COR_RESPONSAVEL_2, corResponsavel2);
    }

    public void setCorResponsavel3(String corResponsavel3) {
        setAttribute(Columns.COR_RESPONSAVEL_3, corResponsavel3);
    }

    public void setCorRespCargo(String corRespCargo) {
        setAttribute(Columns.COR_RESP_CARGO, corRespCargo);
    }

    public void setCorRespCargo2(String corRespCargo2) {
        setAttribute(Columns.COR_RESP_CARGO_2, corRespCargo2);
    }

    public void setCorRespCargo3(String corRespCargo3) {
        setAttribute(Columns.COR_RESP_CARGO_3, corRespCargo3);
    }

    public void setCorRespTelefone(String corRespTelefone) {
        setAttribute(Columns.COR_RESP_TELEFONE, corRespTelefone);
    }

    public void setCorRespTelefone2(String corRespTelefone2) {
        setAttribute(Columns.COR_RESP_TELEFONE_2, corRespTelefone2);
    }

    public void setCorRespTelefone3(String corRespTelefone3) {
        setAttribute(Columns.COR_RESP_TELEFONE_3, corRespTelefone3);
    }

    public void setCorLogradouro(String corLogradouro) {
        setAttribute(Columns.COR_LOGRADOURO, corLogradouro);
    }

    public void setCorNro(Integer corNro) {
        setAttribute(Columns.COR_NRO, corNro);
    }

    public void setCorCompl(String corCompl) {
        setAttribute(Columns.COR_COMPL, corCompl);
    }

    public void setCorBairro(String corBairro) {
        setAttribute(Columns.COR_BAIRRO, corBairro);
    }

    public void setCorCidade(String corCidade) {
        setAttribute(Columns.COR_CIDADE, corCidade);
    }

    public void setCorUf(String corUf) {
        setAttribute(Columns.COR_UF, corUf);
    }

    public void setCorCep(String corCep) {
        setAttribute(Columns.COR_CEP, corCep);
    }

    public void setCorTel(String corTel) {
        setAttribute(Columns.COR_TEL, corTel);
    }

    public void setCorFax(String corFax) {
        setAttribute(Columns.COR_FAX, corFax);
    }

    public void setCorIdentificador(String corIdentificador) {
        setAttribute(Columns.COR_IDENTIFICADOR, corIdentificador);
    }

    public void setCorAtivo(Short corAtivo) {
        setAttribute(Columns.COR_ATIVO, corAtivo);
    }

    public void setCorCnpj(String corCnpj) {
        setAttribute(Columns.COR_CNPJ, corCnpj);
    }
    
    public void setCorIdentificadorAntigo(String corIdentificadorAntigo) {
        setAttribute(Columns.COR_IDENTIFICADOR_ANTIGO, corIdentificadorAntigo);
    }
    
    public void setCorIPAcesso(String corIPAcesso) {
        setAttribute(Columns.COR_IP_ACESSO, corIPAcesso);        
    } 

    public void setCorDDNSAcesso(String corDDNSAcesso) {
        setAttribute(Columns.COR_DDNS_ACESSO, corDDNSAcesso);        
    }
    
    public void setCorExigeEnderecoAcesso(String corExigeEnderecoAcesso) {
        setAttribute(Columns.COR_EXIGE_ENDERECO_ACESSO, corExigeEnderecoAcesso);
    }
    
    public void setEcoCodigo(String ecoCodigo) {
        setAttribute(Columns.COR_ECO_CODIGO, ecoCodigo);
    }
    
    public void setCsaNome(String csaNome) {
        setAttribute(Columns.CSA_NOME, csaNome);
    }
    
    public void setCsaNomeAbrev(String csaNomeAbrev) {
        setAttribute(Columns.CSA_NOME_ABREV, csaNomeAbrev);
    }
    
    public void setCsaIdentificadorInterno(String csaIdentificadorInterno) {
        setAttribute(Columns.CSA_IDENTIFICADOR_INTERNO, csaIdentificadorInterno);
    }

    public void setCsaCnpj(String csaCnpj) {
        setAttribute(Columns.CSA_CNPJ, csaCnpj);
    }
        
}
