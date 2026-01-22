package com.zetra.econsig.persistence.entity;

public class ArquivoRetornoId implements java.io.Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY KEY ATTRIBUTES
	private String nomeArquivo;
	private int idLinha;

	public ArquivoRetornoId() {
	}

	public ArquivoRetornoId(String nomeArquivo, int idLinha) {
		this.nomeArquivo = nomeArquivo;
		this.idLinha = idLinha;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public int getIdLinha() {
		return idLinha;
	}

	public void setIdLinha(int idLinha) {
		this.idLinha = idLinha;
	}

	@Override
    public boolean equals(Object other) {
		if ((this == other)) {
            return true;
        }
		if ((other == null)) {
            return false;
        }
		if (!(other instanceof ArquivoRetornoId)) {
            return false;
        }
		ArquivoRetornoId castOther = (ArquivoRetornoId) other;

		return ((getNomeArquivo() == castOther.getNomeArquivo()) ||
		        (getNomeArquivo() != null && castOther.getNomeArquivo() != null && getNomeArquivo().equals(castOther.getNomeArquivo()))) &&
		       (getIdLinha() == castOther.getIdLinha());
	}

	@Override
    public int hashCode() {
		int result = 17;

		result = 37 * result + (getNomeArquivo() == null ? 0 : getNomeArquivo().hashCode());
		result = 37 * result + getIdLinha();
		return result;
	}
}
