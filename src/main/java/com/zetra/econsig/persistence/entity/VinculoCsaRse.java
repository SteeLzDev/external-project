package com.zetra.econsig.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name="tb_vinculo_csa_rse")
@IdClass(VinculoCsaRseId.class)
public class VinculoCsaRse implements Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "vrs_codigo", nullable = false, length = 32)
    private String vrsCodigo;

    @Id
    @Column(name = "vcs_codigo", nullable = false, length = 32)
    private String vcsCodigo;

    public String getVrsCodigo() {
        return vrsCodigo;
    }

    public void setVrsCodigo(String vrsCodigo) {
        this.vrsCodigo = vrsCodigo;
    }

    public String getVcsCodigo() {
        return vcsCodigo;
    }

    public void setVcsCodigo(String vcsCodigo) {
        this.vcsCodigo = vcsCodigo;
    }

}
