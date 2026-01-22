package com.zetra.econsig.util;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

public class AcessoSistemaBuilder {

	private final AcessoSistema responsavel;

	public AcessoSistemaBuilder() {
		responsavel = new AcessoSistema(null);

	}

	public AcessoSistemaBuilder(String usuCodigo) {
		responsavel = new AcessoSistema(usuCodigo);
	}

	public AcessoSistemaBuilder setUsuCodigo(String usuCodigo) {
		responsavel.setUsuCodigo(usuCodigo);

		return this;
	}

	public AcessoSistemaBuilder setTipoEntidade(String tipoEntidade) {
		responsavel.setTipoEntidade(tipoEntidade);

		return this;
	}

	public AcessoSistemaBuilder setCodigoEntidade(String codigoEntidade) {
		responsavel.setCodigoEntidade(codigoEntidade);

		return this;
	}

	public AcessoSistemaBuilder setUsuNome(String usuNome) {
		responsavel.setUsuNome(usuNome);

		return this;
	}

	public AcessoSistemaBuilder setUsuLogin(String usuLogin) {
		responsavel.setUsuLogin(usuLogin);

		return this;
	}

	public AcessoSistemaBuilder setUsuEmail(String usuEmail) {
		responsavel.setUsuEmail(usuEmail);

		return this;
	}

	public AcessoSistemaBuilder setFunCodigo(String funCodigo) {
		responsavel.setFunCodigo(funCodigo);

		return this;
	}

	public AcessoSistema build() {
		return responsavel;
	}

}
