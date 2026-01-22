package com.zetra.econsig.dto.web;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;

/**
 * <p>Title: ExtratoConsolidadoServidor</p>
 * <p>Description: POJO auxiliar de model da view de extrato consolidade de servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 24512 $
 * $Date: 2018-06-04 11:19:17 -0300 (seg, 04 jun 2018) $
 */
public class ExtratoConsolidadoServidor {

    private String data;
    private String serNome;
    private String rseMatricula;
    private String serCpf;
    private String cseNome;
    private String consignatarias;
    private List<String> svcCodigos;
    private String servicos;
    private List<TransferObject> ades;
    private List<TransferObject> parcelas;
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getSerNome() {
        return serNome;
    }
    public void setSerNome(String serNome) {
        this.serNome = serNome;
    }
    public String getRseMatricula() {
        return rseMatricula;
    }
    public void setRseMatricula(String rseMatricula) {
        this.rseMatricula = rseMatricula;
    }
    public String getSerCpf() {
        return serCpf;
    }
    public void setSerCpf(String serCpf) {
        this.serCpf = serCpf;
    }
    public String getCseNome() {
        return cseNome;
    }
    public void setCseNome(String cseNome) {
        this.cseNome = cseNome;
    }
    public String getConsignatarias() {
        return consignatarias;
    }
    public void setConsignatarias(String consignatarias) {
        this.consignatarias = consignatarias;
    }
    public List<String> getSvcCodigos() {
        return svcCodigos;
    }
    public void setSvcCodigos(List<String> svcCodigos) {
        this.svcCodigos = svcCodigos;
    }
    public String getServicos() {
        return servicos;
    }
    public void setServicos(String servicos) {
        this.servicos = servicos;
    }
    public List<TransferObject> getAdes() {
        return ades;
    }
    public void setAdes(List<TransferObject> ades) {
        this.ades = ades;
    }
    public List<TransferObject> getParcelas() {
        return parcelas;
    }
    public void setParcelas(List<TransferObject> parcelas) {
        this.parcelas = parcelas;
    }
}
