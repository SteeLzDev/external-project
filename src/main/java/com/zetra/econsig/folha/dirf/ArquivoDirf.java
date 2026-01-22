package com.zetra.econsig.folha.dirf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: ArquivoDirf</p>
 * <p>Description: Classe POJO que representa o conteúdo do arquivo DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoDirf {
    private Short anoReferencia;
    private Short anoCalendario;
    private String retificadora;
    private String recibo;
    private String leiaute;

    // 1-1 : RESPO
    private String responsavelCpf;
    private String responsavelNome;
    private String responsavelDDDTelefone;
    private String responsavelNumeroTelefone;
    private String responsavelRamal;
    private String responsavelFax;
    private String responsavelEmail;

    // 1-N : DECPJ
    private final List<DeclarantePJ> declarantesPJ;

    // 1-N : INF
    private final Map<String, List<String>> complementos;

    public ArquivoDirf() {
        declarantesPJ = new ArrayList<DeclarantePJ>();
        complementos = new HashMap<String, List<String>>();
    }

    public Short getAnoReferencia() {
        return anoReferencia;
    }

    public void setAnoReferencia(Short anoReferencia) {
        this.anoReferencia = anoReferencia;
    }

    public Short getAnoCalendario() {
        return anoCalendario;
    }

    public void setAnoCalendario(Short anoCalendario) {
        this.anoCalendario = anoCalendario;
    }

    public String getRetificadora() {
        return retificadora;
    }

    public void setRetificadora(String retificadora) {
        this.retificadora = retificadora;
    }

    public String getRecibo() {
        return recibo;
    }

    public void setRecibo(String recibo) {
        this.recibo = recibo;
    }

    public String getLeiaute() {
        return leiaute;
    }

    public void setLeiaute(String leiaute) {
        this.leiaute = leiaute;
    }

    public String getResponsavelCpf() {
        return responsavelCpf;
    }

    public void setResponsavelCpf(String responsavelCpf) {
        this.responsavelCpf = responsavelCpf;
    }

    public String getResponsavelNome() {
        return responsavelNome;
    }

    public void setResponsavelNome(String responsavelNome) {
        this.responsavelNome = responsavelNome;
    }

    public String getResponsavelDDDTelefone() {
        return responsavelDDDTelefone;
    }

    public void setResponsavelDDDTelefone(String responsavelDDDTelefone) {
        this.responsavelDDDTelefone = responsavelDDDTelefone;
    }

    public String getResponsavelNumeroTelefone() {
        return responsavelNumeroTelefone;
    }

    public void setResponsavelNumeroTelefone(String responsavelNumeroTelefone) {
        this.responsavelNumeroTelefone = responsavelNumeroTelefone;
    }

    public String getResponsavelRamal() {
        return responsavelRamal;
    }

    public void setResponsavelRamal(String responsavelRamal) {
        this.responsavelRamal = responsavelRamal;
    }

    public String getResponsavelFax() {
        return responsavelFax;
    }

    public void setResponsavelFax(String responsavelFax) {
        this.responsavelFax = responsavelFax;
    }

    public String getResponsavelEmail() {
        return responsavelEmail;
    }

    public void setResponsavelEmail(String responsavelEmail) {
        this.responsavelEmail = responsavelEmail;
    }

    public List<DeclarantePJ> getDeclarantesPJ() {
        return Collections.unmodifiableList(declarantesPJ);
    }

    public void addDeclarantePJ(DeclarantePJ declarante) {
        declarantesPJ.add(declarante);
    }

    public void addComplemento(String chave, String valor) {
        if (valor != null && !valor.isEmpty()) {
            List<String> valores = complementos.get(chave);
            if (valores == null) {
                valores = new ArrayList<String>();
                complementos.put(chave, valores);
            }
            valores.add(valor);
        }
    }

    public List<String> getComplementoByChave(String chave) {
        return complementos.containsKey(chave) ? Collections.unmodifiableList(complementos.get(chave)) : null;
    }
}