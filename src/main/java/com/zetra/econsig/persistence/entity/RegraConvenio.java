package com.zetra.econsig.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity class for "RegraConvenio"
 *
 * @author Seany
 *
 */
@Entity
@Table(name = "tb_regra_convenio")
public class RegraConvenio implements Serializable {

    private static final long serialVersionUID = 2L;
    
  //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "rco_codigo", nullable = false, length = 32)
    private String rcoCodigo;

    //--- ENTITY DATA FIELDS
    
    @Column(name = "rco_campo_codigo", nullable = false, length = 32)
    private String rcoCampoCodigo;
    
    @Column(name = "rco_campo_nome", nullable = false, length = 255)
    private String rcoCampoNome;
    
    @Column(name = "rco_campo_valor", nullable = false, length = 100)
    private String rcoCampoValor;
    
    @Column(name = "csa_codigo", nullable = false, length = 32)
    private String csaCodigo;
    
    @Column(name = "svc_codigo", length = 32)
    private String svcCodigo;
    
    @Column(name = "org_codigo", length = 32)
    private String orgCodigo;
    
    @Column(name = "mar_codigo", length = 32)
    private Short marCodigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csa_codigo", referencedColumnName = "csa_codigo", insertable = false, updatable = false)
    private Consignataria consignataria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "svc_codigo", referencedColumnName = "svc_codigo", insertable = false, updatable = false)
    private Servico servico;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_codigo", referencedColumnName = "org_codigo", insertable = false, updatable = false)
    private Orgao orgao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mar_codigo", referencedColumnName = "mar_codigo", insertable = false, updatable = false)
    private Margem margem;
    
    /**
     * Constructor
     */
    public RegraConvenio() {
    }

    //--- GETTERS & SETTERS FOR FIELDS
	public String getRcoCodigo() {
		return rcoCodigo;
	}

	public void setRcoCodigo(String rcoCodigo) {
		this.rcoCodigo = rcoCodigo;
	}

	public String getRcoCampoCodigo() {
		return rcoCampoCodigo;
	}

	public void setRcoCampoCodigo(String rcoCampoCodigo) {
		this.rcoCampoCodigo = rcoCampoCodigo;
	}

	public String getRcoCampoNome() {
		return rcoCampoNome;
	}

	public void setRcoCampoNome(String rcoCampoNome) {
		this.rcoCampoNome = rcoCampoNome;
	}

	public String getRcoCampoValor() {
		return rcoCampoValor;
	}

	public void setRcoCampoValor(String rcoCampoValor) {
		this.rcoCampoValor = rcoCampoValor;
	}

	public String getCsaCodigo() {
		return csaCodigo;
	}

	public void setCsaCodigo(String csaCodigo) {
		this.csaCodigo = csaCodigo;
	}
	

	public String getSvcCodigo() {
		return svcCodigo;
	}

	public void setSvcCodigo(String svcCodigo) {
		this.svcCodigo = svcCodigo;
	}
	
	public String getOrgCodigo() {
		return orgCodigo;
	}

	public void setOrgCodigo(String orgCodigo) {
		this.orgCodigo = orgCodigo;
	}

	public Short getMarCodigo() {
		return marCodigo;
	}

	public void setMarCodigo(Short marCodigo) {
		this.marCodigo = marCodigo;
	}

	public Consignataria getConsignataria() {
		return consignataria;
	}

	public void setConsignataria(Consignataria consignataria) {
		this.consignataria = consignataria;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}

	public Margem getMargem() {
		return margem;
	}

	public void setMargem(Margem margem) {
		this.margem = margem;
	}
}
