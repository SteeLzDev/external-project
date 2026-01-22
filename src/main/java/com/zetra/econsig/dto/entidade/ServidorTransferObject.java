package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ServidorTransferObject</p>
 * <p>Description: Transfer Object do Servidor</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel e Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServidorTransferObject extends CustomTransferObject {

    /**
     * Verifica que a chamada ocorrida foi feita por um dispositivo mobile
     */
    private Boolean mobile = false;

    public ServidorTransferObject() {
        super();
    }

    public ServidorTransferObject(String serCodigo) {
        this();
        setAttribute(Columns.SER_CODIGO, serCodigo);
    }

    public ServidorTransferObject(ServidorTransferObject servidor) {
        this();
        setAtributos(servidor.getAtributos());
    }

    // Getter
    public String getSerCodigo() {
        return (String) getAttribute(Columns.SER_CODIGO);
    }

    public String getSerCpf() {
        return (String) getAttribute(Columns.SER_CPF);
    }

    public java.sql.Date getSerDataNasc() {
        return (java.sql.Date) getAttribute(Columns.SER_DATA_NASC);
    }

    public String getSerNomeMae() {
        return (String) getAttribute(Columns.SER_NOME_MAE);
    }

    public String getSerNomePai() {
        return (String) getAttribute(Columns.SER_NOME_PAI);
    }

    public String getSerNome() {
        return (String) getAttribute(Columns.SER_NOME);
    }

    public String getSerSexo() {
        return (String) getAttribute(Columns.SER_SEXO);
    }

    public String getSerEstCivil() {
        return (String) getAttribute(Columns.SER_EST_CIVIL);
    }

    public String getSerNacionalidade() {
        return (String) getAttribute(Columns.SER_NACIONALIDADE);
    }

    public String getSerNroIdt() {
        return (String) getAttribute(Columns.SER_NRO_IDT);
    }

    public String getSerEmissorIdt() {
        return (String) getAttribute(Columns.SER_EMISSOR_IDT);
    }

    public String getSerUfIdt() {
        return (String) getAttribute(Columns.SER_UF_IDT);
    }

    public java.sql.Date getSerDataIdt() {
        return (java.sql.Date) getAttribute(Columns.SER_DATA_IDT);
    }

    public String getSerCartProf() {
        return (String) getAttribute(Columns.SER_CART_PROF);
    }

    public String getSerPis() {
        return (String) getAttribute(Columns.SER_PIS);
    }

    public String getSerEnd() {
        return (String) getAttribute(Columns.SER_END);
    }

    public String getSerBairro() {
        return (String) getAttribute(Columns.SER_BAIRRO);
    }

    public String getSerCidade() {
        return (String) getAttribute(Columns.SER_CIDADE);
    }

    public String getSerCompl() {
        return (String) getAttribute(Columns.SER_COMPL);
    }

    public String getSerNro() {
        return (String) getAttribute(Columns.SER_NRO);
    }

    public String getSerCep() {
        return (String) getAttribute(Columns.SER_CEP);
    }

    public String getSerUf() {
        return (String) getAttribute(Columns.SER_UF);
    }

    public String getSerTel() {
        return (String) getAttribute(Columns.SER_TEL);
    }

    public String getSerCelular() {
        return (String) getAttribute(Columns.SER_CELULAR);
    }

    public String getSerEmail() {
        return (String) getAttribute(Columns.SER_EMAIL);
    }

    public String getSerCidNasc() {
        return (String) getAttribute(Columns.SER_CID_NASC);
    }

    public String getSerUfNasc() {
        return (String) getAttribute(Columns.SER_UF_NASC);
    }

    public String getSerNomeConjuge() {
        return (String) getAttribute(Columns.SER_NOME_CONJUGE);
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.SER_USU_CODIGO);
    }

    public java.util.Date getSerDataAlteracao() {
        return (java.util.Date) getAttribute(Columns.SER_DATA_ALTERACAO);
    }

    public String getSerDeficienteVisual() {
        return (String) getAttribute(Columns.SER_DEFICIENTE_VISUAL);
    }

    public String getSerAcessaHostaHost() {
        return (String) getAttribute(Columns.SER_ACESSA_HOST_A_HOST);
    }

    public String getSerNomeMeio() {
        return (String) getAttribute(Columns.SER_NOME_MEIO);
    }

    public String getSerUltimoNome() {
        return (String) getAttribute(Columns.SER_ULTIMO_NOME);
    }

    public String getSerTitulacao() {
        return (String) getAttribute(Columns.SER_TITULACAO);
    }

    public String getSerPrimeiroNome() {
        return (String) getAttribute(Columns.SER_PRIMEIRO_NOME);
    }

    public Short getSerQtdFilhos() {
        return (Short) getAttribute(Columns.SER_QTD_FILHOS);
    }

    public String getThaCodigo() {
        return (String) getAttribute(Columns.SER_THA_CODIGO);
    }

    public String getNesCodigo() {
        return (String) getAttribute(Columns.SER_NES_CODIGO);
    }

    public String getSseCodigo() {
        return (String) getAttribute(Columns.SER_SSE_CODIGO);
    }

    public String getSerDispensaDigital() {
        return (String) getAttribute(Columns.SER_DISPENSA_DIGITAL);
    }

    public java.util.Date getSerDataIdentificacaoPessoal() {
        return (java.util.Date) getAttribute(Columns.SER_DATA_IDENTIFICACAO_PESSOAL);
    }

    public java.util.Date getSerDataValidacaoEmail() {
        return (java.util.Date) getAttribute(Columns.SER_DATA_VALIDACAO_EMAIL);
    }

    public String getSerPermiteAlterarEmail() {
        return (String) getAttribute(Columns.SER_PERMITE_ALTERAR_EMAIL);
    }

    // Setter
    public void setSerCodigo(String serCodigo) {
        setAttribute(Columns.SER_CODIGO, serCodigo);
    }

    public void setSerCpf(String serCpf) {
        setAttribute(Columns.SER_CPF, serCpf);
    }

    public void setSerDataNasc(java.sql.Date serDataNasc) {
        setAttribute(Columns.SER_DATA_NASC, serDataNasc);
    }

    public void setSerNomeMae(String serNomeMae) {
        setAttribute(Columns.SER_NOME_MAE, serNomeMae);
    }

    public void setSerNomePai(String serNomePai) {
        setAttribute(Columns.SER_NOME_PAI, serNomePai);
    }

    public void setSerNome(String serNome) {
        setAttribute(Columns.SER_NOME, serNome);
    }

    public void setSerNomeMeio(String serNomeMeio) {
        setAttribute(Columns.SER_NOME_MEIO, serNomeMeio);
    }

    public void setSerUltimoNome(String serUltimoNome) {
        setAttribute(Columns.SER_ULTIMO_NOME, serUltimoNome);
    }

    public void setSerTitulacao(String serTitulacao) {
        setAttribute(Columns.SER_TITULACAO, serTitulacao);
    }

    public void setSerSexo(String serSexo) {
        setAttribute(Columns.SER_SEXO, serSexo);
    }

    public void setSerEstCivil(String serEstCivil) {
        setAttribute(Columns.SER_EST_CIVIL, serEstCivil);
    }

    public void setSerNacionalidade(String serNacionalidade) {
        setAttribute(Columns.SER_NACIONALIDADE, serNacionalidade);
    }

    public void setSerNroIdt(String serNroIdt) {
        setAttribute(Columns.SER_NRO_IDT, serNroIdt);
    }

    public void setSerEmissorIdt(String serEmissorIdt) {
        setAttribute(Columns.SER_EMISSOR_IDT, serEmissorIdt);
    }

    public void setSerUfIdt(String serUfIdt) {
        setAttribute(Columns.SER_UF_IDT, serUfIdt);
    }

    public void setSerDataIdt(java.sql.Date serDataIdt) {
        setAttribute(Columns.SER_DATA_IDT, serDataIdt);
    }

    public void setSerCartProf(String serCartProf) {
        setAttribute(Columns.SER_CART_PROF, serCartProf);
    }

    public void setSerPis(String serPis) {
        setAttribute(Columns.SER_PIS, serPis);
    }

    public void setSerEnd(String serEnd) {
        setAttribute(Columns.SER_END, serEnd);
    }

    public void setSerBairro(String serBairro) {
        setAttribute(Columns.SER_BAIRRO, serBairro);
    }

    public void setSerCidade(String serCidade) {
        setAttribute(Columns.SER_CIDADE, serCidade);
    }

    public void setSerCompl(String serCompl) {
        setAttribute(Columns.SER_COMPL, serCompl);
    }

    public void setSerNro(String serNro) {
        setAttribute(Columns.SER_NRO, serNro);
    }

    public void setSerCep(String serCep) {
        setAttribute(Columns.SER_CEP, serCep);
    }

    public void setSerUf(String serUf) {
        setAttribute(Columns.SER_UF, serUf);
    }

    public void setSerTel(String serTel) {
        setAttribute(Columns.SER_TEL, serTel);
    }

    public void setSerCelular(String serCelular) {
        setAttribute(Columns.SER_CELULAR, serCelular);
    }

    public void setSerEmail(String serEmail) {
        setAttribute(Columns.SER_EMAIL, serEmail);
    }

    public void setSerNomeConjuge(String serNomeConjuge) {
        setAttribute(Columns.SER_NOME_CONJUGE, serNomeConjuge);
    }

    public void setSerCidNasc(String serCidNasc) {
        setAttribute(Columns.SER_CID_NASC, serCidNasc);
    }

    public void setSerUfNasc(String serUfNasc) {
        setAttribute(Columns.SER_UF_NASC, serUfNasc);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.SER_USU_CODIGO, usuCodigo);
    }

    public void setSerDataAlteracao(java.util.Date serDataAlteracao) {
        setAttribute(Columns.SER_DATA_ALTERACAO, serDataAlteracao);
    }

    public void setSerDeficienteVisual(String serDeficienteVisual) {
        setAttribute(Columns.SER_DEFICIENTE_VISUAL, serDeficienteVisual);
    }

    public void setSerAcessaHostaHost(String serAcessaHostaHost) {
        setAttribute(Columns.SER_ACESSA_HOST_A_HOST, serAcessaHostaHost);
    }

    public void setSerPrimeiroNome(String serPrimeiroNome) {
        setAttribute(Columns.SER_PRIMEIRO_NOME, serPrimeiroNome);
    }

    public void setSerQtdFilhos(Short serQtdFilhos) {
       setAttribute(Columns.SER_QTD_FILHOS, serQtdFilhos);
    }

    public void setNesCodigo(String nesCodigo) {
        setAttribute(Columns.SER_NES_CODIGO, nesCodigo);
    }

    public void setThaCodigo(String thaCodigo) {
        setAttribute(Columns.SER_THA_CODIGO, thaCodigo);
    }

    public void setSseCodigo(String sseCodigo) {
        setAttribute(Columns.SER_SSE_CODIGO, sseCodigo);
    }

    public void setSerDispensaDigital(String serDispensaDigital) {
        setAttribute(Columns.SER_DISPENSA_DIGITAL, serDispensaDigital);
    }

    public void setSerDataIdentificacaoPessoal(java.util.Date serDataIdentificacaoPessoal) {
        setAttribute(Columns.SER_DATA_IDENTIFICACAO_PESSOAL, serDataIdentificacaoPessoal);
    }

    public void setSerDataValidacaoEmail(java.util.Date serDataValidacaoEmail) {
        setAttribute(Columns.SER_DATA_VALIDACAO_EMAIL, serDataValidacaoEmail);
    }

    public void setSerPermiteAlterarEmail(String serPermiteAlterarEmail) {
        setAttribute(Columns.SER_PERMITE_ALTERAR_EMAIL, serPermiteAlterarEmail);
    }

    public Boolean getMobile() {
        return mobile;
    }

    public void setMobile(Boolean mobile) {
        this.mobile = mobile;
    }
}
