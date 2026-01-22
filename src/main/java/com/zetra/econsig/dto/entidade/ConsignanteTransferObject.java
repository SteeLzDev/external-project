package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ConsignanteTransferObject</p>
 * <p>Description: TransferObject da entidade Consignante</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsignanteTransferObject extends CustomTransferObject {

    public ConsignanteTransferObject() {
        super();
    }

    public ConsignanteTransferObject(String cseCodigo) {
        this();
        setAttribute(Columns.CSE_CODIGO, cseCodigo);
    }

    public ConsignanteTransferObject(ConsignatariaTransferObject consignante) {
        this();
        setAtributos(consignante.getAtributos());
    }

    // Getter
    public String getCseCodigo() {
        return (String) getAttribute(Columns.CSE_CODIGO);
    }

    public String getCseIdentificador() {
        return (String) getAttribute(Columns.CSE_IDENTIFICADOR);
    }

    public String getCseNome() {
        return (String) getAttribute(Columns.CSE_NOME);
    }

    public String getCseCnpj() {
        return (String) getAttribute(Columns.CSE_CNPJ);
    }

    public String getCseEmail() {
        return (String) getAttribute(Columns.CSE_EMAIL);
    }

    public String getCseEmailFolha() {
        return (String) getAttribute(Columns.CSE_EMAIL_FOLHA);
    }

    public String getCseResponsavel() {
        return (String) getAttribute(Columns.CSE_RESPONSAVEL);
    }

    public String getCseResponsavel2() {
        return (String) getAttribute(Columns.CSE_RESPONSAVEL_2);
    }

    public String getCseResponsavel3() {
        return (String) getAttribute(Columns.CSE_RESPONSAVEL_3);
    }

    public String getCseRespCargo() {
        return (String) getAttribute(Columns.CSE_RESP_CARGO);
    }

    public String getCseRespCargo2() {
        return (String) getAttribute(Columns.CSE_RESP_CARGO_2);
    }

    public String getCseRespCargo3() {
        return (String) getAttribute(Columns.CSE_RESP_CARGO_3);
    }

    public String getCseRespTelefone() {
        return (String) getAttribute(Columns.CSE_RESP_TELEFONE);
    }

    public String getCseRespTelefone2() {
        return (String) getAttribute(Columns.CSE_RESP_TELEFONE_2);
    }

    public String getCseRespTelefone3() {
        return (String) getAttribute(Columns.CSE_RESP_TELEFONE_3);
    }

    public String getCseLogradouro() {
        return (String) getAttribute(Columns.CSE_LOGRADOURO);
    }

    public Integer getCseNro() {
        return (Integer) getAttribute(Columns.CSE_NRO);
    }

    public String getCseCompl() {
        return (String) getAttribute(Columns.CSE_COMPL);
    }

    public String getCseBairro() {
        return (String) getAttribute(Columns.CSE_BAIRRO);
    }

    public String getCseCidade() {
        return (String) getAttribute(Columns.CSE_CIDADE);
    }

    public String getCseUf() {
        return (String) getAttribute(Columns.CSE_UF);
    }

    public String getCseCep() {
        return (String) getAttribute(Columns.CSE_CEP);
    }

    public String getCseTel() {
        return (String) getAttribute(Columns.CSE_TEL);
    }

    public String getCseFax() {
        return (String) getAttribute(Columns.CSE_FAX);
    }

    public Short getCseAtivo() {
        return (Short) getAttribute(Columns.CSE_ATIVO);
    }

    public String getCseLicenca() {
        return (String) getAttribute(Columns.CSE_LICENCA);
    }

    public String getCseRsaPublicKeyCentralizador() {
        return (String) getAttribute(Columns.CSE_RSA_PUBLIC_KEY_CENTRALIZADOR);
    }

    public String getCseRsaModulusCentralizador() {
        return (String) getAttribute(Columns.CSE_RSA_MODULUS_CENTRALIZADOR);
    }

    public String getCseCertificadoCentralizador() {
        return (String) getAttribute(Columns.CSE_CERTIFICADO_CENTRALIZADOR);
    }

    public String getCseCertificadoCentralMobile() {
        return (String) getAttribute(Columns.CSE_CERTIFICADO_CENTRAL_MOBILE);
    }

    public String getCseIPAcesso() {
        return (String) getAttribute(Columns.CSE_IP_ACESSO);
    }

    public String getCseDDNSAcesso() {
        return (String) getAttribute(Columns.CSE_DDNS_ACESSO);
    }

    public String getIdentificadorInterno() {
        return (String) getAttribute(Columns.CSE_IDENTIFICADOR_INTERNO);
    }

    public java.sql.Date getCseDataCobranca() {
        return (java.sql.Date) getAttribute(Columns.CSE_DATA_COBRANCA);
    }

    public String getTipoConsignante() {
        return (String) getAttribute(Columns.CSE_TCE_CODIGO);
    }

    public String getCseFolha() {
        return (String) getAttribute(Columns.CSE_FOLHA);
    }

    public Date getCseDataAtualizacaoCadastral() {
        return (Date) getAttribute(Columns.CSE_DATA_ATUALIZACAO_CADASTRAL);
    }

    public String getCseSistemaFolha() {
        return (String) getAttribute(Columns.CSE_SISTEMA_FOLHA);
    }

    public Short getBanco() {
        return (Short) getAttribute(Columns.CSE_BCO_CODIGO);
    }

    // Setter
    public void setCseIdentificador(String cseIdentificador) {
        setAttribute(Columns.CSE_IDENTIFICADOR, cseIdentificador);
    }

    public void setCseNome(String cseNome) {
        setAttribute(Columns.CSE_NOME, cseNome);
    }

    public void setCseCnpj(String cseCnpj) {
        setAttribute(Columns.CSE_CNPJ, cseCnpj);
    }

    public void setCseEmail(String cseEmail) {
        setAttribute(Columns.CSE_EMAIL, cseEmail);
    }

    public void setCseEmailFolha(String cseEmailFolha) {
        setAttribute(Columns.CSE_EMAIL_FOLHA, cseEmailFolha);
    }

    public void setCseResponsavel(String cseResponsavel) {
        setAttribute(Columns.CSE_RESPONSAVEL, cseResponsavel);
    }

    public void setCseResponsavel2(String cseResponsavel2) {
        setAttribute(Columns.CSE_RESPONSAVEL_2, cseResponsavel2);
    }

    public void setCseResponsavel3(String cseResponsavel3) {
        setAttribute(Columns.CSE_RESPONSAVEL_3, cseResponsavel3);
    }

    public void setCseRespCargo(String cseRespCargo) {
        setAttribute(Columns.CSE_RESP_CARGO, cseRespCargo);
    }

    public void setCseRespCargo2(String cseRespCargo2) {
        setAttribute(Columns.CSE_RESP_CARGO_2, cseRespCargo2);
    }

    public void setCseRespCargo3(String cseRespCargo3) {
        setAttribute(Columns.CSE_RESP_CARGO_3, cseRespCargo3);
    }

    public void setCseRespTelefone(String cseRespTelefone) {
        setAttribute(Columns.CSE_RESP_TELEFONE, cseRespTelefone);
    }

    public void setCseRespTelefone2(String cseRespTelefone2) {
        setAttribute(Columns.CSE_RESP_TELEFONE_2, cseRespTelefone2);
    }

    public void setCseRespTelefone3(String cseRespTelefone3) {
        setAttribute(Columns.CSE_RESP_TELEFONE_3, cseRespTelefone3);
    }

    public void setCseLogradouro(String cseLogradouro) {
        setAttribute(Columns.CSE_LOGRADOURO, cseLogradouro);
    }

    public void setCseNro(Integer cseNro) {
        setAttribute(Columns.CSE_NRO, cseNro);
    }

    public void setCseCompl(String cseCompl) {
        setAttribute(Columns.CSE_COMPL, cseCompl);
    }

    public void setCseBairro(String cseBairro) {
        setAttribute(Columns.CSE_BAIRRO, cseBairro);
    }

    public void setCseCidade(String cseCidade) {
        setAttribute(Columns.CSE_CIDADE, cseCidade);
    }

    public void setCseUf(String cseUf) {
        setAttribute(Columns.CSE_UF, cseUf);
    }

    public void setCseCep(String cseCep) {
        setAttribute(Columns.CSE_CEP, cseCep);
    }

    public void setCseTel(String cseTel) {
        setAttribute(Columns.CSE_TEL, cseTel);
    }

    public void setCseFax(String cseFax) {
        setAttribute(Columns.CSE_FAX, cseFax);
    }

    public void setCseAtivo(Short cseAtivo) {
        setAttribute(Columns.CSE_ATIVO, cseAtivo);
    }

    public void setCseLicenca(String cseLicenca) {
        setAttribute(Columns.CSE_LICENCA, cseLicenca);
    }

    public void setCseRsaPublicKeyCentralizador(String cseRsaPublicKeyCentralizador) {
        setAttribute(Columns.CSE_RSA_PUBLIC_KEY_CENTRALIZADOR, cseRsaPublicKeyCentralizador);
    }

    public void setCseRsaModulusCentralizador(String cseRsaModulusCentralizador) {
        setAttribute(Columns.CSE_RSA_MODULUS_CENTRALIZADOR, cseRsaModulusCentralizador);
    }

    public void setCseCertificadoCentralizador(String cseCertificadoCentralizador) {
        setAttribute(Columns.CSE_CERTIFICADO_CENTRALIZADOR, cseCertificadoCentralizador);
    }

    public void setCseCertificadoCentralMobile(String cseCertificadoCentralMobile) {
        setAttribute(Columns.CSE_CERTIFICADO_CENTRAL_MOBILE, cseCertificadoCentralMobile);
    }

    public void setCseIPAcesso(String cseIPAcesso) {
        setAttribute(Columns.CSE_IP_ACESSO, cseIPAcesso);
    }

    public void setCseDDNSAcesso(String cseDDNSAcesso) {
        setAttribute(Columns.CSE_DDNS_ACESSO, cseDDNSAcesso);
    }

    public void setIdentificadorInterno(String identificadorInterno) {
        setAttribute(Columns.CSE_IDENTIFICADOR_INTERNO, identificadorInterno);
    }

    public void setCseDataCobranca(java.sql.Date cseDataCobranca) {
        setAttribute(Columns.CSE_DATA_COBRANCA, cseDataCobranca);
    }

    public void setTipoConsignante(String tipoConsignante) {
        setAttribute(Columns.CSE_TCE_CODIGO, tipoConsignante);
    }

    public void setCseFolha(String cseFolha) {
        setAttribute(Columns.CSE_FOLHA, cseFolha);
    }

    public void setCseEmailValidarServidor(String cseEmailValidarServidor) {
        setAttribute(Columns.CSE_EMAIL_VALIDAR_SERVIDOR, cseEmailValidarServidor);
    }

    public String getCseEmailValidarServidor() {
        return (String) getAttribute(Columns.CSE_EMAIL_VALIDAR_SERVIDOR);
    }

    public void setCseProjetoInadimplencia(String cseProjetoInadimplencia) {
        setAttribute(Columns.CSE_PROJETO_INADIMPLENCIA, cseProjetoInadimplencia);
    }

    public String getCseProjetoInadimplencia() {
        return (String) getAttribute(Columns.CSE_PROJETO_INADIMPLENCIA);
    }

    public void setCseDataAtualizacaoCadastral(Date cseDataAtualizacaoCadastral) {
        setAttribute(Columns.CSE_DATA_ATUALIZACAO_CADASTRAL, cseDataAtualizacaoCadastral);
    }

    public void setCseSistemaFolha(String cseSistemaFolha) {
        setAttribute(Columns.CSE_SISTEMA_FOLHA, cseSistemaFolha);
    }

    public void setBanco(Short banco) {
        setAttribute(Columns.CSE_BCO_CODIGO, banco);
    }

}
