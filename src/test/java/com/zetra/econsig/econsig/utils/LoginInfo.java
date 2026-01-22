package com.zetra.econsig.econsig.utils;

public class LoginInfo {

	private final String login;
	private final String senha;
	private final String senhaAutorizacao;
	private final TipoUsuario tipoUsuario;
	private String novaSenha;

	public enum TipoUsuario {
		CSE(1), CSA(2), ORG(3), COR(4), SER(6), SUP(7);
		private int papCodigo;

		private TipoUsuario(int papCodigo) {
			this.papCodigo = papCodigo;
		}

		public String getPapCodigo() {
			return Integer.toString(papCodigo);
		}
	}

	public LoginInfo(String login, String senha) {
		this.login = login;
		this.senha = senha;
		tipoUsuario = TipoUsuario.CSE;
		senhaAutorizacao = "";
	}

	public LoginInfo(String login, String senha, TipoUsuario tipoUsuario) {
		this.login = login;
		this.senha = senha;
		this.tipoUsuario = tipoUsuario;
		senhaAutorizacao = "";
	}

	public LoginInfo(String login, String senha, TipoUsuario tipoUsuario, String senhaAutorizacao) {
		this.login = login;
		this.senha = senha;
		this.tipoUsuario = tipoUsuario;
		this.senhaAutorizacao = senhaAutorizacao;
	}

	/**
	 * Construtor passando informações necessárias para o caso de uso alterar senha usuário(s).
	 * @param login
	 * @param senha
	 * @param tipoUsuario
	 * @param senhaAutorizacao
	 * @param novaSenha
	 */
	public LoginInfo(String login, String senha, TipoUsuario tipoUsuario, String senhaAutorizacao, String novaSenha) {
		this.login = login;
		this.senha = senha;
		this.tipoUsuario = tipoUsuario;
		this.senhaAutorizacao = senhaAutorizacao;
		this.novaSenha = novaSenha;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public String getLogin() {
		return login;
	}

	public String getSenha() {
		return senha;
	}

	public String getSenhaAutorizacao() {
		return senhaAutorizacao;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	@Override
	public String toString() {
		return "LoginInfo [login=" + login + ", senha=" + senha
				+ ", senhaAutorizacao=" + senhaAutorizacao + ", tipoUsuario="
				+ tipoUsuario + ", novaSenha=" + novaSenha + "]";
	}

    public boolean isCse() {
        return TipoUsuario.CSE.equals(tipoUsuario);
    }

    public boolean isOrg() {
        return TipoUsuario.ORG.equals(tipoUsuario);
    }

    public boolean isCseSup() {
        return isCse() || isSup();
    }

    public boolean isCseSupOrg() {
        return isCseSup() || isOrg();
    }

    public boolean isCseOrg() {
        return isCse() || isOrg();
    }

    public boolean isCsa() {
        return TipoUsuario.CSA.equals(tipoUsuario);
    }

    public boolean isCor() {
        return TipoUsuario.COR.equals(tipoUsuario);
    }

    public boolean isCsaCor() {
        return isCsa() || isCor();
    }

    public boolean isSer() {
        return TipoUsuario.SER.equals(tipoUsuario);
    }

    public boolean isSup() {
        return TipoUsuario.SUP.equals(tipoUsuario);
    }
}
