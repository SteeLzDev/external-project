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
 * <p>Title: Subrelatorio</p>
 * <p>Description: Entidade de subrelatorio</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

@Entity
@Table(name = "tb_sub_relatorio")
public class Subrelatorio implements Serializable{

    private static final long serialVersionUID = 1L;

  //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "sre_codigo", nullable = false, length = 32)
    private String sreCodigo;

    //--- ENTITY DATA FIELDS
    @Column(name = "rel_codigo", nullable = false, length = 32)
    private String relCodigo;

    @Column(name = "sre_template_jasper", nullable = false, length = 100)
    private String sreTemplateJasper;

    @Column(name = "sre_nome_parametro", nullable = false, length = 100)
    private String sreNomeParametro;

    @Column(name = "sre_template_sql", length = 65535)
    private String sreTemplateSql;

  //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rel_codigo", referencedColumnName = "rel_codigo", insertable = false, updatable = false)
    private Relatorio relatorio;


    public String getSreCodigo() {
        return sreCodigo;
    }

    public void setSreCodigo(String sreCodigo) {
        this.sreCodigo = sreCodigo;
    }

    public String getRelCodigo() {
        return relCodigo;
    }

    public void setRelCodigo(String relCodigo) {
        this.relCodigo = relCodigo;
    }

    public String getSreTemplateJasper() {
        return sreTemplateJasper;
    }

    public void setSreTemplateJasper(String sreTemplateJasper) {
        this.sreTemplateJasper = sreTemplateJasper;
    }

    public String getSreNomeParametro() {
        return sreNomeParametro;
    }

    public void setSreNomeParametro(String sreNomeParametro) {
        this.sreNomeParametro = sreNomeParametro;
    }

    public String getSreTemplateSql() {
        return sreTemplateSql;
    }

    public void setSreTemplateSql(String sreTemplateSql) {
        this.sreTemplateSql = sreTemplateSql;
    }

    public void setRelatorio(Relatorio relatorio) {
        this.relatorio = relatorio;
    }

    public Relatorio getRelatorio() {
        return relatorio;
    }

}
