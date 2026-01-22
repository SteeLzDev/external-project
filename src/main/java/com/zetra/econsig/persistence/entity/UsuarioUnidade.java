package com.zetra.econsig.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_usuario_unidade")
@IdClass(UsuarioUnidadeId.class)
public class UsuarioUnidade implements Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "usu_codigo", nullable = false, length = 32)
    private String usuCodigo;

    @Id
    @Column(name = "uni_codigo", nullable = false, length = 32)
    private String uniCodigo;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usu_codigo", referencedColumnName = "usu_codigo", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uni_codigo", referencedColumnName = "uni_codigo", insertable = false, updatable = false)
    private Unidade unidade;

    //--- GETTERS & SETTERS FOR FIELDS
    public void setUniCodigo(String uniCodigo) {
        this.uniCodigo = uniCodigo;
    }

    public String getUniCodigo() {
        return uniCodigo;
    }

    public void setUsuCodigo(String usuCodigo) {
        this.usuCodigo = usuCodigo;
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    //--- GETTERS FOR LINKS

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
        setUsuCodigo(unidade != null ? unidade.getUniCodigo() : null);
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        setUsuCodigo(usuario != null ? usuario.getUsuCodigo() : null);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setId(UsuarioUnidadeId id) {
        setUsuCodigo(id != null ? id.getUsuCodigo() : null);
        setUniCodigo(id != null ? id.getUniCodigo() : null);
    }
}
