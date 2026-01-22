package com.zetra.econsig.folha.dirf;

import java.io.Serializable;

/**
 * <p>Title: ImportaArquivoDirfDTO</p>
 * <p>Description: DTO para operações sobre arquivo de DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26247 $
 * $Date: 2019-02-14 09:44:20 -0200 (Qui, 14 fev 2019) $
 */
public class ImportaArquivoDirfDTO implements Serializable {
	
	private static final long serialVersionUID = -4877837673134183768L;
	
	private String nomeArquivo;
	private String nomeArquivoComplemento;
	
	public ImportaArquivoDirfDTO () {}

	public ImportaArquivoDirfDTO(String nomeArquivo,
			String nomeArquivoComplemento) {
		this.nomeArquivo = nomeArquivo;
		this.nomeArquivoComplemento = nomeArquivoComplemento;
	}
	
	public String getNomeArquivo() {
		return nomeArquivo;
	}
	
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}
	
	public String getNomeArquivoComplemento() {
		return nomeArquivoComplemento;
	}
	
	public void setNomeArquivoComplemento(String nomeArquivoComplemento) {
		this.nomeArquivoComplemento = nomeArquivoComplemento;
	}

}
