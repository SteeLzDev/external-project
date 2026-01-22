package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: OrgaoTransferObject</p>
 * <p>Description: Transfer Object do Orgao</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel e Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class OrgaoTransferObject extends CustomTransferObject {

    public OrgaoTransferObject() {
        super();
    }

    public OrgaoTransferObject(String orgCodigo) {
        this();
        setAttribute(Columns.ORG_CODIGO, orgCodigo);
    }

    public OrgaoTransferObject(OrgaoTransferObject orgao) {
        this();
        setAtributos(orgao.getAtributos());
    }

    // Getter
    public String getEstCodigo() {
        return (String) getAttribute(Columns.ORG_EST_CODIGO);
    }

    public Short getOrgAtivo() {
        return (Short) getAttribute(Columns.ORG_ATIVO);
    }

    public String getOrgBairro() {
        return (String) getAttribute(Columns.ORG_BAIRRO);
    }

    public String getOrgCep() {
        return (String) getAttribute(Columns.ORG_CEP);
    }

    public String getOrgCidade() {
        return (String) getAttribute(Columns.ORG_CIDADE);
    }

    public String getOrgCodigo() {
        return (String) getAttribute(Columns.ORG_CODIGO);
    }

    public String getOrgCompl() {
        return (String) getAttribute(Columns.ORG_COMPL);
    }

    public String getOrgEmail() {
        return (String) getAttribute(Columns.ORG_EMAIL);
    }

    public String getOrgEmailFolha() {
        return (String) getAttribute(Columns.ORG_EMAIL_FOLHA);
    }

    public String getOrgFax() {
        return (String) getAttribute(Columns.ORG_FAX);
    }

    public String getOrgIdentificador() {
        return (String) getAttribute(Columns.ORG_IDENTIFICADOR);
    }

    public String getOrgLogradouro() {
        return (String) getAttribute(Columns.ORG_LOGRADOURO);
    }

    public String getOrgNome() {
        return (String) getAttribute(Columns.ORG_NOME);
    }

    public String getOrgNomeAbrev() {
        return (String) getAttribute(Columns.ORG_NOME_ABREV);
    }

    public Integer getOrgNro() {
        return (Integer) getAttribute(Columns.ORG_NRO);
    }

    public String getOrgResponsavel() {
        return (String) getAttribute(Columns.ORG_RESPONSAVEL);
    }

    public String getOrgResponsavel2() {
        return (String) getAttribute(Columns.ORG_RESPONSAVEL_2);
    }

    public String getOrgResponsavel3() {
        return (String) getAttribute(Columns.ORG_RESPONSAVEL_3);
    }

    public String getOrgRespCargo() {
        return (String) getAttribute(Columns.ORG_RESP_CARGO);
    }

    public String getOrgRespCargo2() {
        return (String) getAttribute(Columns.ORG_RESP_CARGO_2);
    }

    public String getOrgRespCargo3() {
        return (String) getAttribute(Columns.ORG_RESP_CARGO_3);
    }

    public String getOrgRespTelefone() {
        return (String) getAttribute(Columns.ORG_RESP_TELEFONE);
    }

    public String getOrgRespTelefone2() {
        return (String) getAttribute(Columns.ORG_RESP_TELEFONE_2);
    }

    public String getOrgRespTelefone3() {
        return (String) getAttribute(Columns.ORG_RESP_TELEFONE_3);
    }

    public String getOrgTel() {
        return (String) getAttribute(Columns.ORG_TEL);
    }

    public String getOrgUf() {
        return (String) getAttribute(Columns.ORG_UF);
    }

    public String getOrgCnpj() {
        return (String) getAttribute(Columns.ORG_CNPJ);
    }

    public Integer getOrgDiaRepasse() {
        return (Integer) getAttribute(Columns.ORG_DIA_REPASSE);
    }

    public String getOrgIPAcesso() {
        return (String) getAttribute(Columns.ORG_IP_ACESSO);
    }

    public String getOrgDDNSAcesso() {
        return (String) getAttribute(Columns.ORG_DDNS_ACESSO);
    }

    public String getOrgFolha() {
        return (String) getAttribute(Columns.ORG_FOLHA);
    }

    public String getOrgEmailValidarServidor(){
        return (String) getAttribute(Columns.ORG_EMAIL_VALIDAR_SERVIDOR);
    }

    // Setter
    public void setEstCodigo(String estCodigo) {
        setAttribute(Columns.ORG_EST_CODIGO, estCodigo);
    }

    public void setOrgAtivo(Short orgAtivo) {
        setAttribute(Columns.ORG_ATIVO, orgAtivo);
    }

    public void setOrgBairro(String orgBairro) {
        setAttribute(Columns.ORG_BAIRRO, orgBairro);
    }

    public void setOrgCep(String orgCep) {
        setAttribute(Columns.ORG_CEP, orgCep);
    }

    public void setOrgCidade(String orgCidade) {
        setAttribute(Columns.ORG_CIDADE, orgCidade);
    }

    public void setOrgCompl(String orgCompl) {
        setAttribute(Columns.ORG_COMPL, orgCompl);
    }

    public void setOrgEmail(String orgEmail) {
        setAttribute(Columns.ORG_EMAIL, orgEmail);
    }

    public void setOrgEmailFolha(String orgEmailFolha) {
        setAttribute(Columns.ORG_EMAIL_FOLHA, orgEmailFolha);
    }

    public void setOrgFax(String orgFax) {
        setAttribute(Columns.ORG_FAX, orgFax);
    }

    public void setOrgIdentificador(String orgIdentificador) {
        setAttribute(Columns.ORG_IDENTIFICADOR, orgIdentificador);
    }

    public void setOrgLogradouro(String orgLogradouro) {
        setAttribute(Columns.ORG_LOGRADOURO, orgLogradouro);
    }

    public void setOrgNome(String orgNome) {
        setAttribute(Columns.ORG_NOME, orgNome);
    }

    public void setOrgNomeAbrev(String orgNomeAbrev) {
        setAttribute(Columns.ORG_NOME_ABREV, orgNomeAbrev);
    }

    public void setOrgNro(Integer orgNro) {
        setAttribute(Columns.ORG_NRO, orgNro);
    }

    public void setOrgResponsavel(String orgResponsavel) {
        setAttribute(Columns.ORG_RESPONSAVEL, orgResponsavel);
    }

    public void setOrgResponsavel2(String orgResponsavel2) {
        setAttribute(Columns.ORG_RESPONSAVEL_2, orgResponsavel2);
    }

    public void setOrgResponsavel3(String orgResponsavel3) {
        setAttribute(Columns.ORG_RESPONSAVEL_3, orgResponsavel3);
    }

    public void setOrgRespCargo(String orgRespCargo) {
        setAttribute(Columns.ORG_RESP_CARGO, orgRespCargo);
    }

    public void setOrgRespCargo2(String orgRespCargo2) {
        setAttribute(Columns.ORG_RESP_CARGO_2, orgRespCargo2);
    }

    public void setOrgRespCargo3(String orgRespCargo3) {
        setAttribute(Columns.ORG_RESP_CARGO_3, orgRespCargo3);
    }

    public void setOrgRespTelefone(String orgRespTelefone) {
        setAttribute(Columns.ORG_RESP_TELEFONE, orgRespTelefone);
    }

    public void setOrgRespTelefone2(String orgRespTelefone2) {
        setAttribute(Columns.ORG_RESP_TELEFONE_2, orgRespTelefone2);
    }

    public void setOrgRespTelefone3(String orgRespTelefone3) {
        setAttribute(Columns.ORG_RESP_TELEFONE_3, orgRespTelefone3);
    }

    public void setOrgTel(String orgTel) {
        setAttribute(Columns.ORG_TEL, orgTel);
    }

    public void setOrgUf(String orgUf) {
        setAttribute(Columns.ORG_UF, orgUf);
    }

    public void setOrgCnpj(String orgCnpj) {
        setAttribute(Columns.ORG_CNPJ, orgCnpj);
    }

    public void setOrgDiaRepasse(Integer orgDiaRepasse) {
        setAttribute(Columns.ORG_DIA_REPASSE, orgDiaRepasse);
    }

    public void setOrgIPAcesso(String orgIPAcesso) {
        setAttribute(Columns.ORG_IP_ACESSO, orgIPAcesso);
    }

    public void setOrgDDNSAcesso(String orgDDNSAcesso) {
        setAttribute(Columns.ORG_DDNS_ACESSO, orgDDNSAcesso);
    }

    public void setOrgFolha(String orgFolha){
    	setAttribute(Columns.ORG_FOLHA, orgFolha);
    }

    public void setOrgEmailValidarServidor(String orgEmailValidarServidor){
        setAttribute(Columns.ORG_EMAIL_VALIDAR_SERVIDOR, orgEmailValidarServidor);
    }
}
