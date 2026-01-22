package com.zetra.econsig.persistence.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * JPA entity class for "Log"
 *
 * @author Telosys
 *
 */
@Entity
@Table(name = "tb_log")
public class Log implements java.io.Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY PRIMARY KEY: a tabela não possui PK, mas o Hibernate exige uma PK
    @Id
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_data", nullable = false)
    private Date logData;

    //--- ENTITY DATA FIELDS
    @Column(name = "tlo_codigo", nullable = false, length = 32)
    private String tloCodigo;

    @Column(name = "ten_codigo", length = 32)
    private String tenCodigo;

    @Column(name = "usu_codigo", length = 32)
    private String usuCodigo;

    @Column(name = "fun_codigo", length = 32)
    private String funCodigo;

    @Column(name = "log_obs", nullable = false, length = 65535)
    private String logObs;

    @Column(name = "log_ip", length = 45)
    private String logIp;
    
    @Column(name = "log_porta", length = 45)
    private Integer logPortaLogica;

    @Column(name = "log_cod_ent_00", length = 32)
    private String logCodEnt00;

    @Column(name = "log_cod_ent_01", length = 32)
	private String logCodEnt01;

    @Column(name = "log_cod_ent_02", length = 32)
    private String logCodEnt02;

    @Column(name = "log_cod_ent_03", length = 32)
    private String logCodEnt03;

    @Column(name = "log_cod_ent_04", length = 32)
    private String logCodEnt04;

    @Column(name = "log_cod_ent_05", length = 32)
    private String logCodEnt05;

    @Column(name = "log_cod_ent_06", length = 32)
    private String logCodEnt06;

    @Column(name = "log_cod_ent_07", length = 32)
    private String logCodEnt07;

    @Column(name = "log_cod_ent_08", length = 32)
    private String logCodEnt08;

    @Column(name = "log_cod_ent_09", length = 32)
    private String logCodEnt09;

    @Column(name = "log_cod_ent_10", length = 32)
    private String logCodEnt10;

    @Column(name = "log_canal", nullable = false, length = 1)
    private String logCanal;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usu_codigo", referencedColumnName = "usu_codigo", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_codigo", referencedColumnName = "fun_codigo", insertable = false, updatable = false)
    private Funcao funcao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ten_codigo", referencedColumnName = "ten_codigo", insertable = false, updatable = false)
    private TipoEntidade tipoEntidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tlo_codigo", referencedColumnName = "tlo_codigo", insertable = false, updatable = false)
    private TipoLog tipoLog;

    /**
     * Constructor
     */
    public Log() {
        super();
    }

    //--- GETTERS & SETTERS FOR FIELDS
    public String getTloCodigo() {
        return tloCodigo;
    }

    public void setTloCodigo(String tloCodigo) {
        this.tloCodigo = tloCodigo;
    }

    public String getTenCodigo() {
        return tenCodigo;
    }

    public void setTenCodigo(String tenCodigo) {
        this.tenCodigo = tenCodigo;
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    public void setUsuCodigo(String usuCodigo) {
        this.usuCodigo = usuCodigo;
    }

    public String getFunCodigo() {
        return funCodigo;
    }

    public void setFunCodigo(String funCodigo) {
        this.funCodigo = funCodigo;
    }

    public Date getLogData() {
        return logData;
    }

    public void setLogData(Date logData) {
        this.logData = logData;
    }

    public String getLogObs() {
        return logObs;
    }

    public void setLogObs(String logObs) {
        this.logObs = logObs;
    }

    public String getLogIp() {
        return logIp;
    }

    public void setLogIp(String logIp) {
        this.logIp = logIp;
    }
    
    public Integer getLogPortaLogica() {
        return logPortaLogica;
    }

    public void setLogPortaLogica(Integer logPortaLogica) {
        this.logPortaLogica = logPortaLogica;
    }

    public String getLogCodEnt00() {
        return logCodEnt00;
    }

    public void setLogCodEnt00(String logCodEnt00) {
        this.logCodEnt00 = logCodEnt00;
    }

    public String getLogCodEnt01() {
        return logCodEnt01;
    }

    public void setLogCodEnt01(String logCodEnt01) {
        this.logCodEnt01 = logCodEnt01;
    }

    public String getLogCodEnt02() {
        return logCodEnt02;
    }

    public void setLogCodEnt02(String logCodEnt02) {
        this.logCodEnt02 = logCodEnt02;
    }

    public String getLogCodEnt03() {
        return logCodEnt03;
    }

    public void setLogCodEnt03(String logCodEnt03) {
        this.logCodEnt03 = logCodEnt03;
    }

    public String getLogCodEnt04() {
        return logCodEnt04;
    }

    public void setLogCodEnt04(String logCodEnt04) {
        this.logCodEnt04 = logCodEnt04;
    }

    public String getLogCodEnt05() {
        return logCodEnt05;
    }

    public void setLogCodEnt05(String logCodEnt05) {
        this.logCodEnt05 = logCodEnt05;
    }

    public String getLogCodEnt06() {
        return logCodEnt06;
    }

    public void setLogCodEnt06(String logCodEnt06) {
        this.logCodEnt06 = logCodEnt06;
    }

    public String getLogCodEnt07() {
        return logCodEnt07;
    }

    public void setLogCodEnt07(String logCodEnt07) {
        this.logCodEnt07 = logCodEnt07;
    }

    public String getLogCodEnt08() {
        return logCodEnt08;
    }

    public void setLogCodEnt08(String logCodEnt08) {
        this.logCodEnt08 = logCodEnt08;
    }

    public String getLogCodEnt09() {
        return logCodEnt09;
    }

    public void setLogCodEnt09(String logCodEnt09) {
        this.logCodEnt09 = logCodEnt09;
    }

    public String getLogCodEnt10() {
        return logCodEnt10;
    }

    public void setLogCodEnt10(String logCodEnt10) {
        this.logCodEnt10 = logCodEnt10;
    }

    public String getLogCanal() {
        return logCanal;
    }

    public void setLogCanal(String logCanal) {
        this.logCanal = logCanal;
    }

    //--- GETTERS FOR LINKS
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        setUsuCodigo(usuario != null ? usuario.getUsuCodigo() : null);
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
        setFunCodigo(funcao != null ? funcao.getFunCodigo() : null);
    }

    public TipoEntidade getTipoEntidade() {
        return tipoEntidade;
    }

    public void setTipoEntidade(TipoEntidade tipoEntidade) {
        this.tipoEntidade = tipoEntidade;
        setTenCodigo(tipoEntidade != null ? tipoEntidade.getTenCodigo() : null);
    }

    public TipoLog getTipoLog() {
        return tipoLog;
    }

    public void setTipoLog(TipoLog tipoLog) {
        this.tipoLog = tipoLog;
        setTloCodigo(tipoLog != null ? tipoLog.getTloCodigo() : null);
    }
}
