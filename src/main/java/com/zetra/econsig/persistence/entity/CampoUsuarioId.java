package com.zetra.econsig.persistence.entity;

import java.io.Serializable;

/**
 * Composite primary key for entity "CampoUsuario" ( stored in table "tb_campo_usuario" )
 */

public class CampoUsuarioId implements Serializable{
	
	private static final long serialVersionUID = 2L;
	
	//--- ENTITY KEY ATTRIBUTES
	private String usuCodigo;
	
	private String cauChave;
	
	/**
	 * Constructor
	 */
	public CampoUsuarioId() {
		super();
	}
	
	/**
	 * Constructor with values
	 * @param usuCodigo
	 * @param cauChave
	 */
	public CampoUsuarioId(String usuCodigo, String cauChave) {
		super();
		this.usuCodigo = usuCodigo;
		this.cauChave = cauChave;
	}

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
	
	
	//--- equals METHOD
		@Override
		public boolean equals(Object obj) {
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if (this.getClass() != obj.getClass())
				return false;
			CampoUsuarioId other = (CampoUsuarioId) obj;
			//--- Attribute usuCodigo
			if(usuCodigo == null) {
				if(other.usuCodigo != null)
					return false;
			} else if(!usuCodigo.equals(other.usuCodigo))
					return false;
			//---Attribute cauChave
			if(cauChave == null) {
				if(other.cauChave != null)
					return false;
			} else if (!cauChave.equals(other.cauChave))
				return false;
			return true;		
		}
		
		//--- hashCode METHOD
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			
			//--- Attribute usuCodigo
			result = prime * result + ((usuCodigo == null) ? 0 : usuCodigo.hashCode());
			//--- Attribute cauChave
			result = prime * result + ((cauChave == null) ? 0 : cauChave.hashCode());
			
			return result;
		}
		
		//--- toString METHOD
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(usuCodigo);
			sb.append("|");
			sb.append(cauChave);
			return sb.toString();
		}

}







