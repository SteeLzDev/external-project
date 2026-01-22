package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: EstabelecimentoTransferObject</p>
 * <p>Description: TransferObject do estabelecimento</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class EstabelecimentoTransferObject extends CustomTransferObject {

    public EstabelecimentoTransferObject() {
        super();
    }

    public EstabelecimentoTransferObject(String estCodigo) {
        this();
        setAttribute(Columns.EST_CODIGO, estCodigo);
    }

    public EstabelecimentoTransferObject(EstabelecimentoTransferObject estabelecimento) {
        this();
        setAtributos(estabelecimento.getAtributos());
    }

    // Getter
    public String getEstCodigo() {
        return (String) getAttribute(Columns.EST_CODIGO);
    }

    public String getCseCodigo() {
        return (String) getAttribute(Columns.EST_CSE_CODIGO);
    }

    public String getEstIdentificador() {
        return (String) getAttribute(Columns.EST_IDENTIFICADOR);
    }

    public String getEstNome() {
        return (String) getAttribute(Columns.EST_NOME);
    }

    public String getEstNomeAbrev() {
        return (String) getAttribute(Columns.EST_NOME_ABREV);
    }

    public String getEstCnpj() {
        return (String) getAttribute(Columns.EST_CNPJ);
    }

    public String getEstEmail() {
        return (String) getAttribute(Columns.EST_EMAIL);
    }

    public String getEstResponsavel() {
        return (String) getAttribute(Columns.EST_RESPONSAVEL);
    }

    public String getEstResponsavel2() {
        return (String) getAttribute(Columns.EST_RESPONSAVEL_2);
    }

    public String getEstResponsavel3() {
        return (String) getAttribute(Columns.EST_RESPONSAVEL_3);
    }

    public String getEstRespCargo() {
        return (String) getAttribute(Columns.EST_RESP_CARGO);
    }

    public String getEstRespCargo2() {
        return (String) getAttribute(Columns.EST_RESP_CARGO_2);
    }

    public String getEstRespCargo3() {
        return (String) getAttribute(Columns.EST_RESP_CARGO_3);
    }

    public String getEstRespTelefone() {
        return (String) getAttribute(Columns.EST_RESP_TELEFONE);
    }

    public String getEstRespTelefone2() {
        return (String) getAttribute(Columns.EST_RESP_TELEFONE_2);
    }

    public String getEstRespTelefone3() {
        return (String) getAttribute(Columns.EST_RESP_TELEFONE_3);
    }

    public String getEstLogradouro() {
        return (String) getAttribute(Columns.EST_LOGRADOURO);
    }

    public Integer getEstNro() {
        return (Integer) getAttribute(Columns.EST_NRO);
    }

    public String getEstCompl() {
        return (String) getAttribute(Columns.EST_COMPL);
    }

    public String getEstBairro() {
        return (String) getAttribute(Columns.EST_BAIRRO);
    }

    public String getEstCidade() {
        return (String) getAttribute(Columns.EST_CIDADE);
    }

    public String getEstUf() {
        return (String) getAttribute(Columns.EST_UF);
    }

    public String getEstCep() {
        return (String) getAttribute(Columns.EST_CEP);
    }

    public String getEstTel() {
        return (String) getAttribute(Columns.EST_TEL);
    }

    public String getEstFax() {
        return (String) getAttribute(Columns.EST_FAX);
    }

    public Short getEstAtivo() {
        return (Short) getAttribute(Columns.EST_ATIVO);
    }

    public String getEstFolha() {
        return (String) getAttribute(Columns.EST_FOLHA);
    }

    // Setter
    public void setCseCodigo(String cseCodigo) {
        setAttribute(Columns.EST_CSE_CODIGO, cseCodigo);
    }

    public void setEstIdentificador(String estIdentificador) {
        setAttribute(Columns.EST_IDENTIFICADOR, estIdentificador);
    }

    public void setEstNome(String estNome) {
        setAttribute(Columns.EST_NOME, estNome);
    }

    public void setEstNomeAbrev(String estNomeAbrev) {
        setAttribute(Columns.EST_NOME_ABREV, estNomeAbrev);
    }

    public void setEstCnpj(String estCnpj) {
        setAttribute(Columns.EST_CNPJ, estCnpj);
    }

    public void setEstEmail(String estEmail) {
        setAttribute(Columns.EST_EMAIL, estEmail);
    }

    public void setEstResponsavel(String estResponsavel) {
        setAttribute(Columns.EST_RESPONSAVEL, estResponsavel);
    }

    public void setEstResponsavel2(String estResponsavel2) {
        setAttribute(Columns.EST_RESPONSAVEL_2, estResponsavel2);
    }

    public void setEstResponsavel3(String estResponsavel3) {
        setAttribute(Columns.EST_RESPONSAVEL_3, estResponsavel3);
    }

    public void setEstRespCargo(String estRespCargo) {
        setAttribute(Columns.EST_RESP_CARGO, estRespCargo);
    }

    public void setEstRespCargo2(String estRespCargo2) {
        setAttribute(Columns.EST_RESP_CARGO_2, estRespCargo2);
    }

    public void setEstRespCargo3(String estRespCargo3) {
        setAttribute(Columns.EST_RESP_CARGO_3, estRespCargo3);
    }

    public void setEstRespTelefone(String estRespTelefone) {
        setAttribute(Columns.EST_RESP_TELEFONE, estRespTelefone);
    }

    public void setEstRespTelefone2(String estRespTelefone2) {
        setAttribute(Columns.EST_RESP_TELEFONE_2, estRespTelefone2);
    }

    public void setEstRespTelefone3(String estRespTelefone3) {
        setAttribute(Columns.EST_RESP_TELEFONE_3, estRespTelefone3);
    }

    public void setEstLogradouro(String estLogradouro) {
        setAttribute(Columns.EST_LOGRADOURO, estLogradouro);
    }

    public void setEstNro(Integer estNro) {
        setAttribute(Columns.EST_NRO, estNro);
    }

    public void setEstCompl(String estCompl) {
        setAttribute(Columns.EST_COMPL, estCompl);
    }

    public void setEstBairro(String estBairro) {
        setAttribute(Columns.EST_BAIRRO, estBairro);
    }

    public void setEstCidade(String estCidade) {
        setAttribute(Columns.EST_CIDADE, estCidade);
    }

    public void setEstUf(String estUf) {
        setAttribute(Columns.EST_UF, estUf);
    }

    public void setEstCep(String estCep) {
        setAttribute(Columns.EST_CEP, estCep);
    }

    public void setEstTel(String estTel) {
        setAttribute(Columns.EST_TEL, estTel);
    }

    public void setEstFax(String estFax) {
        setAttribute(Columns.EST_FAX, estFax);
    }

    public void setEstAtivo(Short estAtivo) {
        setAttribute(Columns.EST_ATIVO, estAtivo);
    }

    public void setEstFolha(String estFolha) {
        setAttribute(Columns.EST_FOLHA, estFolha);
    }
}
