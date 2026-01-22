package com.zetra.econsig.dto.web;

/**
 * <p>Title: ArquivoDTO</p>
 * <p>Description: DTO para o caso de uso Relatório de Integração.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoDTO {

    private String originalNome;
    private String nome;
    private String formato;
    private String data;
    private String tam;
    private String orgIdentificador;
    private String estIdentificador;
    private String csaCodigo;
    private String csaIdentificador;
    private String csaNome;

    public ArquivoDTO() {}

    public ArquivoDTO(String originalNome, String nome, String formato, String data, String tam, String orgIdentificador, String estIdentificador) {
        this.originalNome = originalNome;
        this.nome = nome;
        this.formato = formato;
        this.data = data;
        this.tam = tam;
        this.orgIdentificador = orgIdentificador;
        this.estIdentificador = estIdentificador;
    }

    public ArquivoDTO(String originalNome, String nome, String formato, String data, String tam, String orgIdentificador, String estIdentificador, String csaCodigo, String csaIdentificador, String csaNome) {
        this.originalNome = originalNome;
        this.nome = nome;
        this.formato = formato;
        this.data = data;
        this.tam = tam;
        this.orgIdentificador = orgIdentificador;
        this.estIdentificador = estIdentificador;
        this.csaCodigo = csaCodigo;
        this.csaIdentificador = csaIdentificador;
        this.csaNome = csaNome;
    }

    public ArquivoDTO(String originalNome, String nome, String tam, String data, String formato) {
        this.originalNome = originalNome;
        this.nome = nome;
        this.tam = tam;
        this.data = data;
        this.formato = formato;
    }

    public String getOriginalNome() {
        return originalNome;
    }

    public void setOriginalNome(String originalNome) {
        this.originalNome = originalNome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTam() {
        return tam;
    }

    public void setTam(String tam) {
        this.tam = tam;
    }

    public String getEstIdentificador() {
        return estIdentificador;
    }

    public void setEstIdentificador(String estIdentificador) {
        this.estIdentificador = estIdentificador;
    }

    public String getOrgIdentificador() {
        return orgIdentificador;
    }

    public void setOrgIdentificador(String orgIdentificador) {
        this.orgIdentificador = orgIdentificador;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getCsaIdentificador() {
        return csaIdentificador;
    }

    public void setCsaIdentificador(String csaIdentificador) {
        this.csaIdentificador = csaIdentificador;
    }

    public String getCsaNome() {
        return csaNome;
    }

    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }

    @Override
    public String toString() {
        return "ArquivoDTO [originalNome="
                    + originalNome + ", nome=" + nome + ", formato=" + formato
                    + ", data=" + data + ", tam=" + tam + ", orgIdentificador="
                    + orgIdentificador + ", estIdentificador=" + estIdentificador
                    + ", csaCodigo=" + csaCodigo + ", csaIdentificador=" + csaIdentificador + ", csaNome=" + csaNome + "]";
    }

}