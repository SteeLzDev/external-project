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
@Table(name = "tb_campo_usuario")
@IdClass(CampoUsuarioId.class)
public class CampoUsuario implements Serializable{
	
	private static final long serialVersionUID = 2L;

	//--- ENTITY PRIMARY KEY
	@Id
	@Column(name = "usu_codigo", nullable = false, length = 32)
	private String usuCodigo;
	
	@Id
	@Column(name = "cau_chave", nullable = false, length = 100)
	private String cauChave;
	
	//--- ENTITY DATA FIELDS
	@Column(name = "cau_valor", nullable = false, length = 1)
	private String cauValor;
	
	//ENTITY LINKS (RELATIONSHIP)
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usu_codigo", referencedColumnName = "usu_codigo", insertable = false, updatable = false)
    private Usuario usuario;
	
	/**
	 * Constructor
	 */
	public CampoUsuario() {
		super();
	}
	
	
	//GETTER & SETTERS FOR FIELDS
	public String getUsuCodigo() {
		return usuCodigo;
	}

	public void setUsuCodigo(String usuCodigo) {
		this.usuCodigo = usuCodigo;
	}

	public String getCauChave() {
		return cauChave;
	}

	public void setCauChave(String cauChave) {
		this.cauChave = cauChave;
	}

	public String getCauValor() {
		return cauValor;
	}

	public void setCauValor(String cauValor) {
		this.cauValor = cauValor;
	}
	
	
	//--- GETTERS FOR LINKS
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
		setUsuCodigo(usuario != null ? usuario.getUsuCodigo() : null);
	}
	
	public void setId(CampoUsuarioId id) {
		setUsuCodigo(id != null ? id.getUsuCodigo() : null);
		setCauChave(id != null ? id.getCauChave() : null);
	}
}
