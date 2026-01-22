package com.zetra.econsig.dto.entidade;

import java.sql.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: UsuarioTransferObject</p>
 * <p>Description: TransferObject da entidade usuário</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioTransferObject extends CustomTransferObject {
    private static final String LINK_RECUPERAR_SENHA_CHAVE = "LINK_RECUPERAR_SENHA";

    public UsuarioTransferObject() {
        super();
    }

    public UsuarioTransferObject(String usuCodigo) {
        this();
        setAttribute(Columns.USU_CODIGO, usuCodigo);
    }

    public UsuarioTransferObject(ConsignatariaTransferObject usuario) {
        this();
        setAtributos(usuario.getAtributos());
    }

    // Getter
    public String getUsuCodigo() {
        return (String) getAttribute(Columns.USU_CODIGO);
    }

    public String getStuCodigo() {
        return (String) getAttribute(Columns.USU_STU_CODIGO);
    }

    public java.sql.Date getUsuDataCad() {
        return (java.sql.Date) getAttribute(Columns.USU_DATA_CAD);
    }

    public java.sql.Date getUsuDataUltAcesso() {
        return (java.sql.Date) getAttribute(Columns.USU_DATA_ULT_ACESSO);
    }

    public String getUsuLogin() {
        return (String) getAttribute(Columns.USU_LOGIN);
    }

    public String getUsuSenha() {
        return (String) getAttribute(Columns.USU_SENHA);
    }

    public String getUsuSenha2() {
        return (String) getAttribute(Columns.USU_SENHA_2);
    }

    public String getUsuNome() {
        return (String) getAttribute(Columns.USU_NOME);
    }

    public String getUsuEmail() {
        return (String) getAttribute(Columns.USU_EMAIL);
    }

    public String getUsuTel() {
        return (String) getAttribute(Columns.USU_TEL);
    }

    public String getUsuDicaSenha() {
        return (String) getAttribute(Columns.USU_DICA_SENHA);
    }

    public String getUsuTipoBloq() {
        return (String) getAttribute(Columns.USU_TIPO_BLOQ);
    }

    public java.sql.Date getUsuDataExpSenha() {
        return (java.sql.Date) getAttribute(Columns.USU_DATA_EXP_SENHA);
    }

    public java.sql.Date getUsuDataExpSenha2() {
        return (java.sql.Date) getAttribute(Columns.USU_DATA_EXP_SENHA_2);
    }

    public java.sql.Date getUsuDataExpSenhaApp() {
        return (java.sql.Date) getAttribute(Columns.USU_DATA_EXP_SENHA_APP);
    }

    public java.lang.String getUsuIpAcesso() {
        return (java.lang.String) getAttribute(Columns.USU_IP_ACESSO);
    }

    public String getUsuDDNSAcesso() {
        return (java.lang.String) getAttribute(Columns.USU_DDNS_ACESSO);
    }

    public String getUsuCPF() {
        return (java.lang.String) getAttribute(Columns.USU_CPF);
    }

    public String getUsuCentralizador() {
        return (String) getAttribute(Columns.USU_CENTRALIZADOR);
    }

    public String getUsuVisivel() {
        return (String) getAttribute(Columns.USU_VISIVEL);
    }

    public String getUsuExigeCertificado() {
        return (String) getAttribute(Columns.USU_EXIGE_CERTIFICADO);
    }

    public String getUsuMatriculaInst() {
        return (String) getAttribute(Columns.USU_MATRICULA_INST);
    }

    public String getUsuChaveRecuperarSenha() {
        return (String) getAttribute(Columns.USU_CHAVE_RECUPERAR_SENHA);
    }

    public String getUsuNovaSenha() {
        return (String) getAttribute(Columns.USU_NOVA_SENHA);
    }

    public Short getUsuOperacoesSenha2() {
        return (Short) getAttribute(Columns.USU_OPERACOES_SENHA_2);
    }

    public Date getUsuDataFimVig() {
        return (java.sql.Date) getAttribute(Columns.USU_DATA_FIM_VIG);
    }

    public String getUsuDeficienteVisual() {
        return (String) getAttribute(Columns.USU_DEFICIENTE_VISUAL);
    }

    public java.util.Date getUsuDataRecSenha() {
        return (java.sql.Date) getAttribute(Columns.USU_DATA_REC_SENHA);
    }

    public String getUsuChaveValidacaoTotp() {
        return (String) getAttribute(Columns.USU_CHAVE_VALIDACAO_TOTP);
    }

    public String getUsuPermiteValidacaoTotp() {
        return (String) getAttribute(Columns.USU_PERMITE_VALIDACAO_TOTP);
    }

    public String getUsuOperacoesValidacaoTotp() {
        return (String) getAttribute(Columns.USU_OPERACOES_VALIDACAO_TOTP);
    }

    public String getUsuOtpCodigo () {
        return (String) getAttribute(Columns.USU_OTP_CODIGO);
    }

    public String getUsuOtpChaveSeguranca () {
        return (String) getAttribute(Columns.USU_OTP_CHAVE_SEGURANCA);
    }

    public java.util.Date getUsuOtpDataCadastro () {
        return (java.util.Date) getAttribute(Columns.USU_OTP_DATA_CADASTRO);
    }

    public String getUsuSenhaApp () {
        return (String) getAttribute(Columns.USU_SENHA_APP);
    }

    public Integer getUsuQtdConsultasMargem () {
        return (Integer) getAttribute(Columns.USU_QTD_CONSULTAS_MARGEM);
    }

    public String getUsuAutenticaSso() {
        return (String) getAttribute(Columns.USU_AUTENTICA_SSO);
    }

    public String getUsuChaveValidacaoEmail () {
        return (String) getAttribute(Columns.USU_CHAVE_VALIDACAO_EMAIL);
    }

    public String getUsuAutorizaEmailMarketing() {
        return (String) getAttribute(Columns.USU_AUTORIZA_EMAIL_MARKETING);
    }

    public String getLinkRecuperarSenha() {
        return (String) getAttribute(LINK_RECUPERAR_SENHA_CHAVE);
    }

    // Setter
    public void setStuCodigo(String stuCodigo) {
        setAttribute(Columns.USU_STU_CODIGO, stuCodigo);
    }

    public void setUsuDataCad(java.sql.Date usuDataCad) {
        setAttribute(Columns.USU_DATA_CAD, usuDataCad);
    }

    public void setUsuDataUltAcesso(java.sql.Date usuDataUltAcesso) {
        setAttribute(Columns.USU_DATA_ULT_ACESSO, usuDataUltAcesso);
    }

    public void setUsuLogin(String usuLogin) {
        setAttribute(Columns.USU_LOGIN, usuLogin);
    }

    public void setUsuSenha(String usuSenha) {
        setAttribute(Columns.USU_SENHA, usuSenha);
    }

    public void setUsuSenha2(String usuSenha2) {
        setAttribute(Columns.USU_SENHA_2, usuSenha2);
    }

    public void setUsuNovaSenha(String usuNovaSenha) {
        setAttribute(Columns.USU_NOVA_SENHA, usuNovaSenha);
    }

    public void setUsuNome(String usuNome) {
        setAttribute(Columns.USU_NOME, usuNome);
    }

    public void setUsuEmail(String usuEmail) {
        setAttribute(Columns.USU_EMAIL, usuEmail);
    }

    public void setUsuTel(String usuTel) {
        setAttribute(Columns.USU_TEL, usuTel);
    }

    public void setUsuDicaSenha(String usuDicaSenha) {
        setAttribute(Columns.USU_DICA_SENHA, usuDicaSenha);
    }

    public void setUsuTipoBloq(String usuTipoBloq) {
        setAttribute(Columns.USU_TIPO_BLOQ, usuTipoBloq);
    }

    public void setUsuDataExpSenha(java.sql.Date usuDataExpSenha) {
        setAttribute(Columns.USU_DATA_EXP_SENHA, usuDataExpSenha);
    }

    public void setUsuDataExpSenha2(java.sql.Date usuDataExpSenha2) {
        setAttribute(Columns.USU_DATA_EXP_SENHA_2, usuDataExpSenha2);
    }

    public void setUsuDataExpSenhaApp(java.sql.Date usuDataExpSenhaApp) {
        setAttribute(Columns.USU_DATA_EXP_SENHA_APP, usuDataExpSenhaApp);
    }

    public void setUsuIpAcesso(java.lang.String usuIpAcesso) {
        setAttribute(Columns.USU_IP_ACESSO, usuIpAcesso);
    }

    public void setUsuDDNSAcesso(java.lang.String usuDDNSAcesso) {
        setAttribute(Columns.USU_DDNS_ACESSO, usuDDNSAcesso);
    }

    public void setUsuCPF(java.lang.String usuCPF) {
        setAttribute(Columns.USU_CPF, usuCPF);
    }

    public void setUsuCentralizador(String usuCentralizador) {
        setAttribute(Columns.USU_CENTRALIZADOR, usuCentralizador);
    }

    public void setUsuVisivel(String usuVisivel) {
        setAttribute(Columns.USU_VISIVEL, usuVisivel);
    }

    public void setUsuExigeCertificado(String usuExigeCertificado) {
        setAttribute(Columns.USU_EXIGE_CERTIFICADO, usuExigeCertificado);
    }

    public void setUsuMatriculaInst(String usuMatriculaInst) {
        setAttribute(Columns.USU_MATRICULA_INST, usuMatriculaInst);
    }

    public void setUsuChaveRecuperarSenha(String usuChaveRecuperarSenha) {
        setAttribute(Columns.USU_CHAVE_RECUPERAR_SENHA, usuChaveRecuperarSenha);
    }

    public void setUsuOperacoesSenha2(Short usuOperacoesSenha2) {
        setAttribute(Columns.USU_OPERACOES_SENHA_2, usuOperacoesSenha2);
    }

    public void setUsuDataFimVig(Date usuDataFimVig) {
        setAttribute(Columns.USU_DATA_FIM_VIG, usuDataFimVig);
    }

    public void setUsuDeficienteVisual(String usuDeficienteVisual) {
        setAttribute(Columns.USU_DEFICIENTE_VISUAL, usuDeficienteVisual);
    }

    public void setUsuDataRecSenha(java.util.Date usuDataRecSenha) {
        setAttribute(Columns.USU_DATA_REC_SENHA, usuDataRecSenha);
    }

    public void setUsuChaveValidacaoTotp(String usuChaveValidacaoTotp) {
        setAttribute(Columns.USU_CHAVE_VALIDACAO_TOTP, usuChaveValidacaoTotp);
    }

    public void setUsuPermiteValidacaoTotp(String usuPermiteValidacaoTotp) {
        setAttribute(Columns.USU_PERMITE_VALIDACAO_TOTP, usuPermiteValidacaoTotp);
    }

    public void setUsuOperacoesValidacaoTotp(String usuOperacoesValidacaoTotp) {
        setAttribute(Columns.USU_OPERACOES_VALIDACAO_TOTP, usuOperacoesValidacaoTotp);
    }

    public void setUsuOtpCodigo (String usuOtpCodigo) {
        setAttribute(Columns.USU_OTP_CODIGO, usuOtpCodigo);
    }

    public void setUsuOtpChaveSeguranca (String usuOtpChaveSeguranca) {
        setAttribute(Columns.USU_OTP_CHAVE_SEGURANCA, usuOtpChaveSeguranca);
    }

    public void setUsuOtpDataCadastro (java.util.Date usuOtpDataCadastro) {
        setAttribute(Columns.USU_OTP_DATA_CADASTRO, usuOtpDataCadastro);
    }

    public void setUsuSenhaApp (String usuSenhaApp) {
        setAttribute(Columns.USU_SENHA_APP, usuSenhaApp);
    }

    public void setUsuQtdConsultasMargem (Integer usuQtdConsultasMargem) {
        setAttribute(Columns.USU_QTD_CONSULTAS_MARGEM, usuQtdConsultasMargem);
    }

    public void setUsuAutentiaSso (String usuAutenticaSso) {
        setAttribute(Columns.USU_AUTENTICA_SSO, usuAutenticaSso);
    }

    public void setUsuChaveValidacaoEmail(String usuChaveValidacaoEmail) {
        setAttribute(Columns.USU_CHAVE_VALIDACAO_EMAIL, usuChaveValidacaoEmail);
    }

    public void setUsuDataValidacaoEmail(java.util.Date usuDataValidacaoEmail) {
        setAttribute(Columns.USU_DATA_VALIDACAO_EMAIL, usuDataValidacaoEmail);
    }

    public void setUsuAutorizaEmailMarketing(String usuAutorizaEmailMarketing) {
        setAttribute(Columns.USU_AUTORIZA_EMAIL_MARKETING, usuAutorizaEmailMarketing);
    }

    public void setLinkRecuperarSenha(String linkRecuperarSenha) {
        setAttribute(LINK_RECUPERAR_SENHA_CHAVE, linkRecuperarSenha);
    }
}
