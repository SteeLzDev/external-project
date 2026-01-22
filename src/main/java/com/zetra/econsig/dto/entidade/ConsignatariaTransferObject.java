package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ConsignatariaTransferObject</p>
 * <p>Description: TransferObject da consignatária</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsignatariaTransferObject extends CustomTransferObject {

    public ConsignatariaTransferObject() {
        super();
    }

    public ConsignatariaTransferObject(String csaCodigo) {
        this();
        setAttribute(Columns.CSA_CODIGO, csaCodigo);
    }

    public ConsignatariaTransferObject(ConsignatariaTransferObject consignataria) {
        this();
        setAtributos(consignataria.getAtributos());
    }

    // Getter
    public String getCsaCodigo() {
        return (String) getAttribute(Columns.CSA_CODIGO);
    }

    public String getCsaIdentificador() {
        return (String) getAttribute(Columns.CSA_IDENTIFICADOR);
    }

    public String getCsaNome() {
        return (String) getAttribute(Columns.CSA_NOME);
    }

    public String getCsaNomeIdentificador(){
        return (String) getAttribute(Columns.CSA_NOME) + " - " + (String) getAttribute(Columns.CSA_IDENTIFICADOR);
    }

    public String getCsaCnpj() {
        return (String) getAttribute(Columns.CSA_CNPJ);
    }

    public String getCsaCnpjCta() {
        return (String) getAttribute(Columns.CSA_CNPJ_CTA);
    }

    public String getCsaEmail() {
        return (String) getAttribute(Columns.CSA_EMAIL);
    }

    public String getCsaEmailExpiracao() {
        return (String) getAttribute(Columns.CSA_EMAIL_EXPIRACAO);
    }

    public String getCsaEmailDesbloqueio() {
        return (String) getAttribute(Columns.CSA_EMAIL_DESBLOQUEIO);
    }

    public String getCsaResponsavel() {
        return (String) getAttribute(Columns.CSA_RESPONSAVEL);
    }

    public String getCsaResponsavel2() {
        return (String) getAttribute(Columns.CSA_RESPONSAVEL_2);
    }

    public String getCsaResponsavel3() {
        return (String) getAttribute(Columns.CSA_RESPONSAVEL_3);
    }

    public String getCsaRespCargo() {
        return (String) getAttribute(Columns.CSA_RESP_CARGO);
    }

    public String getCsaRespCargo2() {
        return (String) getAttribute(Columns.CSA_RESP_CARGO_2);
    }

    public String getCsaRespCargo3() {
        return (String) getAttribute(Columns.CSA_RESP_CARGO_3);
    }

    public String getCsaRespTelefone() {
        return (String) getAttribute(Columns.CSA_RESP_TELEFONE);
    }

    public String getCsaRespTelefone2() {
        return (String) getAttribute(Columns.CSA_RESP_TELEFONE_2);
    }

    public String getCsaRespTelefone3() {
        return (String) getAttribute(Columns.CSA_RESP_TELEFONE_3);
    }

    public String getCsaLogradouro() {
        return (String) getAttribute(Columns.CSA_LOGRADOURO);
    }

    public Integer getCsaNro() {
        return (Integer) getAttribute(Columns.CSA_NRO);
    }

    public String getCsaCompl() {
        return (String) getAttribute(Columns.CSA_COMPL);
    }

    public String getCsaBairro() {
        return (String) getAttribute(Columns.CSA_BAIRRO);
    }

    public String getCsaCidade() {
        return (String) getAttribute(Columns.CSA_CIDADE);
    }

    public String getCsaUf() {
        return (String) getAttribute(Columns.CSA_UF);
    }

    public String getCsaCep() {
        return (String) getAttribute(Columns.CSA_CEP);
    }

    public String getCsaTel() {
        return (String) getAttribute(Columns.CSA_TEL);
    }

    public String getCsaFax() {
        return (String) getAttribute(Columns.CSA_FAX);
    }

    public String getCsaNroBco() {
        return (String) getAttribute(Columns.CSA_NRO_BCO);
    }

    public String getCsaNroCta() {
        return (String) getAttribute(Columns.CSA_NRO_CTA);
    }

    public String getCsaNroAge() {
        return (String) getAttribute(Columns.CSA_NRO_AGE);
    }

    public String getCsaDigCta() {
        return (String) getAttribute(Columns.CSA_DIG_CTA);
    }

    public Short getCsaAtivo() {
        return (Short) getAttribute(Columns.CSA_ATIVO);
    }

    public String getCsaTxtContato() {
        return (String) getAttribute(Columns.CSA_TXT_CONTATO);
    }

    public String getCsaContato() {
        return (String) getAttribute(Columns.CSA_CONTATO);
    }

    public String getCsaContatoTel() {
        return (String) getAttribute(Columns.CSA_CONTATO_TEL);
    }

    public String getCsaEndereco2() {
        return (String) getAttribute(Columns.CSA_ENDERECO_2);
    }

    public String getCsaNomeAbreviado() {
        return (String) getAttribute(Columns.CSA_NOME_ABREV);
    }

    public String getTmbCodigo() {
        return (String) getAttribute(Columns.CSA_TMB_CODIGO);
    }

    public String getTgcCodigo() {
        return (String) getAttribute(Columns.CSA_TGC_CODIGO);
    }

    public String getTgcIdentificador() {
        return (String) getAttribute(Columns.TGC_IDENTIFICADOR);
    }

    public String getCsaIdentificadorInterno() {
        return (String) getAttribute(Columns.CSA_IDENTIFICADOR_INTERNO);
    }

    public Date getCsaDataExpiracao() {
        return (Date) getAttribute(Columns.CSA_DATA_EXPIRACAO);
    }

    public String getCsaNroContrato() {
        return (String) getAttribute(Columns.CSA_NRO_CONTRATO);
    }

    public String getCsaIPAcesso() {
        return (String) getAttribute(Columns.CSA_IP_ACESSO);
    }

    public String getCsaDDNSAcesso() {
        return (String) getAttribute(Columns.CSA_DDNS_ACESSO);
    }

    public String getCsaExigeEnderecoAcesso() {
        return (String) getAttribute(Columns.CSA_EXIGE_ENDERECO_ACESSO);
    }

    public String getCsaUnidadeOrganizacional() {
        return (String) getAttribute(Columns.CSA_UNIDADE_ORGANIZACIONAL);
    }

    public String getCsaNroContratoZetra() {
        return (String) getAttribute(Columns.CSA_NRO_CONTRATO_ZETRA);
    }

    public String getCsaNcaNatureza() {
        if (!TextHelper.isNull(getAttribute(Columns.CSA_NCA_NATUREZA))) {
            return (String) getAttribute(Columns.CSA_NCA_NATUREZA);
        }
        return (String) getAttribute(Columns.NCA_CODIGO);
    }

    public String getCsaProjetoInadimplencia() {
        return (String) getAttribute(Columns.CSA_PROJETO_INADIMPLENCIA);
    }

    public Date getCsaDataExpiracaoCadastral() {
        return (Date) getAttribute(Columns.CSA_DATA_EXPIRACAO_CADASTRAL);
    }

    public String getCsaInstrucaoAnexo() {
        return (String) getAttribute(Columns.CSA_INSTRUCAO_ANEXO);
    }

    public String getCsaPermiteIncluirAde() {
        return (String) getAttribute(Columns.CSA_PERMITE_INCLUIR_ADE);
    }

    public Date getCsaDataAtualizacaoCadastral() {
        return (Date) getAttribute(Columns.CSA_DATA_ATUALIZACAO_CADASTRAL);
    }

    public String getCsaCodigoAns() {
        return (String) getAttribute(Columns.CSA_CODIGO_ANS);
    }

    public String getCsaEmailProjInadimplencia() {
        return (String) getAttribute(Columns.CSA_EMAIL_PROJ_INADIMPLENCIA);
    }
    public Date getCsaDataDesbloqAutomatico() {
        return (Date) getAttribute(Columns.CSA_DATA_DESBLOQ_AUTOMATICO);
    }

    public Date getCsaDataInicioContrato() {
        return (Date) getAttribute(Columns.CSA_DATA_INICIO_CONTRATO);
    }

    public Date getCsaDataRenovacaoContrato() {
        return (Date) getAttribute(Columns.CSA_DATA_RENOVACAO_CONTRATO);
    }

    public String getCsaNumeroProcessoContrato(){
        return (String) getAttribute(Columns.CSA_NUMERO_PROCESSO_CONTRATO);
    }

    public String getCsaObsContrato(){
        return (String) getAttribute(Columns.CSA_OBS_CONTRATO);
    }

    public String getCsaPermiteApi() {
        return (String) getAttribute(Columns.CSA_PERMITE_API);
    }

    public String getCsaWhatsapp() {
        return (String) getAttribute(Columns.CSA_WHATSAPP);
    }

    public String getCsaEmailContato() {
        return (String) getAttribute(Columns.CSA_EMAIL_CONTATO);
    }

    public String getCsaConsultaMargemSemSenha() { return (String) getAttribute(Columns.CSA_CONSULTA_MARGEM_SEM_SENHA); }
    
    public String getCsaEmailNotificacaoRco() { 
    	return (String) getAttribute(Columns.CSA_EMAIL_NOTIFICACAO_RCO); 
	}

    // Setter
    public void setCsaIdentificador(String csaIdentificador) {
        setAttribute(Columns.CSA_IDENTIFICADOR, csaIdentificador);
    }

    public void setCsaNome(String csaNome) {
        setAttribute(Columns.CSA_NOME, csaNome);
    }

    public void setCsaCnpj(String csaCnpj) {
        setAttribute(Columns.CSA_CNPJ, csaCnpj);
    }

    public void setCsaCnpjCta(String csaCnpjCta) {
        setAttribute(Columns.CSA_CNPJ_CTA, csaCnpjCta);
    }

    public void setCsaEmail(String csaEmail) {
        setAttribute(Columns.CSA_EMAIL, csaEmail);
    }

    public void setCsaEmailExpiracao(String csaEmailExpiracao) {
        setAttribute(Columns.CSA_EMAIL_EXPIRACAO, csaEmailExpiracao);
    }

    public void setCsaEmailDesbloqueio(String csaEmailDesbloqueio) {
        setAttribute(Columns.CSA_EMAIL_DESBLOQUEIO, csaEmailDesbloqueio);
    }

    public void setCsaResponsavel(String csaResponsavel) {
        setAttribute(Columns.CSA_RESPONSAVEL, csaResponsavel);
    }

    public void setCsaResponsavel2(String csaResponsavel2) {
        setAttribute(Columns.CSA_RESPONSAVEL_2, csaResponsavel2);
    }

    public void setCsaResponsavel3(String csaResponsavel3) {
        setAttribute(Columns.CSA_RESPONSAVEL_3, csaResponsavel3);
    }

    public void setCsaRespCargo(String csaRespCargo) {
        setAttribute(Columns.CSA_RESP_CARGO, csaRespCargo);
    }

    public void setCsaRespCargo2(String csaRespCargo2) {
        setAttribute(Columns.CSA_RESP_CARGO_2, csaRespCargo2);
    }

    public void setCsaRespCargo3(String csaRespCargo3) {
        setAttribute(Columns.CSA_RESP_CARGO_3, csaRespCargo3);
    }

    public void setCsaRespTelefone(String csaRespTelefone) {
        setAttribute(Columns.CSA_RESP_TELEFONE, csaRespTelefone);
    }

    public void setCsaRespTelefone2(String csaRespTelefone2) {
        setAttribute(Columns.CSA_RESP_TELEFONE_2, csaRespTelefone2);
    }

    public void setCsaRespTelefone3(String csaRespTelefone3) {
        setAttribute(Columns.CSA_RESP_TELEFONE_3, csaRespTelefone3);
    }

    public void setCsaLogradouro(String csaLogradouro) {
        setAttribute(Columns.CSA_LOGRADOURO, csaLogradouro);
    }

    public void setCsaNro(Integer csaNro) {
        setAttribute(Columns.CSA_NRO, csaNro);
    }

    public void setCsaCompl(String csaCompl) {
        setAttribute(Columns.CSA_COMPL, csaCompl);
    }

    public void setCsaBairro(String csaBairro) {
        setAttribute(Columns.CSA_BAIRRO, csaBairro);
    }

    public void setCsaCidade(String csaCidade) {
        setAttribute(Columns.CSA_CIDADE, csaCidade);
    }

    public void setCsaUf(String csaUf) {
        setAttribute(Columns.CSA_UF, csaUf);
    }

    public void setCsaCep(String csaCep) {
        setAttribute(Columns.CSA_CEP, csaCep);
    }

    public void setCsaTel(String csaTel) {
        setAttribute(Columns.CSA_TEL, csaTel);
    }

    public void setCsaFax(String csaFax) {
        setAttribute(Columns.CSA_FAX, csaFax);
    }

    public void setCsaNroBco(String csaNroBco) {
        setAttribute(Columns.CSA_NRO_BCO, csaNroBco);
    }

    public void setCsaNroCta(String csaNroCta) {
        setAttribute(Columns.CSA_NRO_CTA, csaNroCta);
    }

    public void setCsaNroAge(String csaNroAge) {
        setAttribute(Columns.CSA_NRO_AGE, csaNroAge);
    }

    public void setCsaDigCta(String csaDigCta) {
        setAttribute(Columns.CSA_DIG_CTA, csaDigCta);
    }

    public void setCsaAtivo(Short csaAtivo) {
        setAttribute(Columns.CSA_ATIVO, csaAtivo);
    }

    public void setCsaTxtContato(String csaTxtContato) {
        setAttribute(Columns.CSA_TXT_CONTATO, csaTxtContato);
    }

    public void setCsaContato(String csaContato) {
        setAttribute(Columns.CSA_CONTATO, csaContato);
    }

    public void setCsaContatoTel(String csaContatoTel) {
        setAttribute(Columns.CSA_CONTATO_TEL, csaContatoTel);
    }

    public void setCsaEndereco2(String csaEndereco2) {
        setAttribute(Columns.CSA_ENDERECO_2, csaEndereco2);
    }

    public void setCsaNomeAbreviado(String csaNomeAbrev) {
        setAttribute(Columns.CSA_NOME_ABREV, csaNomeAbrev);
    }

    public void setTmbCodigo(String tmbCodigo) {
        setAttribute(Columns.CSA_TMB_CODIGO, tmbCodigo);
    }

    public void setTgcCodigo(String tgcCodigo) {
        setAttribute(Columns.CSA_TGC_CODIGO, tgcCodigo);
    }

    public void setTgcIdentificador(String tgcIdentificador) {
        setAttribute(Columns.TGC_IDENTIFICADOR, tgcIdentificador);
    }

    public void setCsaIdentificadorInterno(String csaIdentificadorInterno) {
        setAttribute(Columns.CSA_IDENTIFICADOR_INTERNO, csaIdentificadorInterno);
    }

    public void setCsaDataExpiracao(Date csaDataExpiracao) {
        setAttribute(Columns.CSA_DATA_EXPIRACAO, csaDataExpiracao);
    }

    public void setCsaNroContrato(String csaNroContrato) {
        setAttribute(Columns.CSA_NRO_CONTRATO, csaNroContrato);
    }

    public void setCsaIPAcesso(String csaIPAcesso) {
        setAttribute(Columns.CSA_IP_ACESSO, csaIPAcesso);
    }

    public void setCsaDDNSAcesso(String csaDDNSAcesso) {
        setAttribute(Columns.CSA_DDNS_ACESSO, csaDDNSAcesso);
    }

    public void setCsaExigeEnderecoAcesso(String csaExigeEnderecoAcesso) {
        setAttribute(Columns.CSA_EXIGE_ENDERECO_ACESSO, csaExigeEnderecoAcesso);
    }

    public void setCsaUnidadeOrganizacional(String csaUnidadeOrganizacional) {
        setAttribute(Columns.CSA_UNIDADE_ORGANIZACIONAL, csaUnidadeOrganizacional);
    }

    public void setCsaNroContratoZetra(String csaNroContratoZetra) {
        setAttribute(Columns.CSA_NRO_CONTRATO_ZETRA, csaNroContratoZetra);
    }

    public void setCsaNcaNatureza(String csaNcaNatureza) {
        setAttribute(Columns.CSA_NCA_NATUREZA, csaNcaNatureza);
        setAttribute(Columns.NCA_CODIGO, csaNcaNatureza);
    }

    public void setCsaProjetoInadimplencia(String csaProjetoInadimplencia) {
        setAttribute(Columns.CSA_PROJETO_INADIMPLENCIA, csaProjetoInadimplencia);
    }

    public void setCsaDataExpiracaoCadastral(Date csaDataExpiracaoCadastral) {
        setAttribute(Columns.CSA_DATA_EXPIRACAO_CADASTRAL, csaDataExpiracaoCadastral);
    }

    public void setCsaInstrucaoAnexo(String csaCsaInstrucaoAnexo) {
        setAttribute(Columns.CSA_INSTRUCAO_ANEXO, csaCsaInstrucaoAnexo);
    }

    public void setCsaPermiteIncluirAde(String csaPermiteIncluirAde) {
        setAttribute(Columns.CSA_PERMITE_INCLUIR_ADE, csaPermiteIncluirAde);
    }

    public void setCsaDataAtualizacaoCadastral(Date csaDataAtualizacaoCadastral) {
        setAttribute(Columns.CSA_DATA_ATUALIZACAO_CADASTRAL, csaDataAtualizacaoCadastral);
    }

    public void setCsaCodigoAns(String csaCodigoAns) {
        setAttribute(Columns.CSA_CODIGO_ANS, csaCodigoAns);
    }

    public void setCsaEmailProjInadimplencia(String csaEmailProjInadimplencia ) {
        setAttribute(Columns.CSA_EMAIL_PROJ_INADIMPLENCIA, csaEmailProjInadimplencia);
    }

    public void setCsaDataDesbloqAutomatico(Date csaDataDesbloqAutomatico) {
        setAttribute(Columns.CSA_DATA_DESBLOQ_AUTOMATICO, csaDataDesbloqAutomatico);
    }

    public void setCsaDataInicioContrato(Date csaDataInicioContrato) {
        setAttribute(Columns.CSA_DATA_INICIO_CONTRATO, csaDataInicioContrato);
    }

    public void setCsaDataRenovacaoContrato(Date csaDataRenovacaoContrato) {
        setAttribute(Columns.CSA_DATA_RENOVACAO_CONTRATO, csaDataRenovacaoContrato);
    }

    public void setCsaNumeroProcessoContrato(String csaNumeroProcessoContrato){
        setAttribute(Columns.CSA_NUMERO_PROCESSO_CONTRATO, csaNumeroProcessoContrato);
    }

    public void setCsaObsContrato(String obsContrato){
        setAttribute(Columns.CSA_OBS_CONTRATO, obsContrato);
    }

    public void setCsaPermiteApi(String permiteApi){
        setAttribute(Columns.CSA_PERMITE_API, permiteApi);
    }

    public void setCsaWhatsapp(String csaWhatsapp) {
        setAttribute(Columns.CSA_WHATSAPP, csaWhatsapp);
    }

    public void setCsaEmailContato(String csaEmailContato) {
        setAttribute(Columns.CSA_EMAIL_CONTATO, csaEmailContato);
    }

    public void setCsaConsultaMargemSemSenha(String csaConsultaMargemSemSenha) {
        setAttribute(Columns.CSA_CONSULTA_MARGEM_SEM_SENHA, csaConsultaMargemSemSenha);
    }
    
    public void setCsaEmailNotificacaoRco(String csaEmailNotificacaoRco) { 
    	setAttribute(Columns.CSA_EMAIL_NOTIFICACAO_RCO, csaEmailNotificacaoRco); 
	}
}