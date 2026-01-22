package com.zetra.econsig.helper.seguranca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.MenuTO;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.SSOToken;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper.AcessoRecurso;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.MenuEnum;
import com.zetra.econsig.values.OperacaoValidacaoTotpEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: AcessoSistema</p>
 * <p>Description: Classe com os parâmetros do usuário.</p>
 * <p>Copyright: Copyright (c) 2004-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class AcessoSistema implements Serializable, java.security.Principal, Cloneable {

    private static final long serialVersionUID = 75L;

    public static final String SESSION_ATTR_NAME = "_AcessoSistema_";

    public static final String ENTIDADE_USU = "USU";
    // CÓDIGOS DOS PAPÉIS:
    public static final String ENTIDADE_CSE = "CSE"; // 1 - CONSIGNANTE/GESTOR
    public static final String ENTIDADE_EST = "EST"; // PAPEL NÃO IMPLEMENTADO
    public static final String ENTIDADE_ORG = "ORG"; // 3 - ÓRGÃO
    public static final String ENTIDADE_CSA = "CSA"; // 2 - CONSIGNATÁRIA
    public static final String ENTIDADE_COR = "COR"; // 4 - CORRESPONDENTE
    public static final String ENTIDADE_SER = "SER"; // 6 - SERVIDOR
    public static final String ENTIDADE_SUP = "SUP"; // 7 - SUPORTE

    public static final String SEGUNDA_SENHA_AUTENTICADA = "_Autenticacao_";
    public static final String ULTIMA_FUNCAO_AUTENTICADA = "_ultima_func_aut_";
    public static final String ULTIMO_RECURSO_AUTENTICADO = "_ultimo_recurso_aut_";
    public static final String EXIGE_SENHA = "exigeSenha";
    public static final String ACESSO_RECURSO = "acessoRecurso";
    public static final String OPERACAO_FILA_AUTORIZADA = "op_fila_autorizado";

    public static final String ADDRESS_SEPARATOR = ",";

    private String usuCodigo;
    private String usuNome;
    private String usuLogin;
    private String usuEmail;
    private String usuCpf;
    private String perDescricao;
    private String ipUsuario;
    private Integer portaLogicaUsuario;
    private String ipOrigem;
    private String tipoEntidade;
    private String codigoEntidade;
    private String codigoEntidadePai;
    private String nomeEntidade;
    private String nomeEntidadePai;
    private String idEntidade;
    private String funCodigo;
    private String itmCodigo;
    private String ncaCodigo;
    private String usuChaveValidacaoTotp;
    private String usuPermiteValidacaoTotp;
    private OperacaoValidacaoTotpEnum usuOperacoesValidacaoTotp;
    // DESENV-9262: armazena a quantidade máxima de consultas de margens em um intervalo de tempo específica do usuário, se configurado
    private Integer qtdConsultasMargem;
    private boolean primeiroAcesso;
    private boolean sessaoInvalidaErroSeg;
    private boolean deficienteVisual;
    private DadosServidor dadosServidor;
    private Map<String, EnderecoFuncaoTransferObject> permissoes;
    private List<MenuTO> menu;
    private List<MenuTO> menuFavoritos;
    private DadosOperacao dadosOperacao;
    private String usuCentralizador;
    private String usuAutenticaSso;
    private AcessoRecurso recursoAcessado;
    private SSOToken ssoToken;
    private CanalEnum canal;
    private List<String> permissaoUnidadesEdt;
    private String sessionId;
    private Date dataUltimaRequisicao;
    private boolean navegadorExclusivo;

    public static AcessoSistema getAcessoUsuarioSistema() {
        return new AcessoSistema(CodedValues.USU_CODIGO_SISTEMA);
    }

    public AcessoSistema(String usuCodigo) {
        this.usuCodigo = usuCodigo;
    }

    public AcessoSistema(String usuCodigo, String ipUsuario, Integer portaLogicaUsuario) {
        this.usuCodigo = usuCodigo;
        this.ipUsuario = ipUsuario;
        this.portaLogicaUsuario = portaLogicaUsuario;
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
   }

    @Override
    public String getName() {
        return usuNome;
    }

    public String getCodigoEntidade() {
        return codigoEntidade;
    }

    public void setCodigoEntidade(String codigoEntidade) {
        this.codigoEntidade = codigoEntidade;
    }

    public String getCodigoEntidadePai() {
        return codigoEntidadePai;
    }

    public void setCodigoEntidadePai(String codigoEntidadePai) {
        this.codigoEntidadePai = codigoEntidadePai;
    }

    public String getNomeEntidade() {
        return nomeEntidade;
    }

    public void setNomeEntidade(String nomeEntidade) {
        this.nomeEntidade = nomeEntidade;
    }

    public String getNomeEntidadePai() {
        return nomeEntidadePai;
    }

    public void setNomeEntidadePai(String nomeEntidadePai) {
        this.nomeEntidadePai = nomeEntidadePai;
    }

    public String getIdEntidade() {
        return idEntidade;
    }

    public void setIdEntidade(String idEntidade) {
        this.idEntidade = idEntidade;
    }

    public String getIpUsuario() {
        return ipUsuario;
    }

    public Integer getPortaLogicaUsuario() {
        return portaLogicaUsuario;
    }

    public void setIpUsuario(String ipUsuario) {
        this.ipUsuario = ipUsuario;
    }

    public void setPortaLogicaUsuario(Integer portaLogicaUsuario) {
        this.portaLogicaUsuario = portaLogicaUsuario;
    }

    public String getIpOrigem() {
        return ipOrigem;
    }

    public void setIpOrigem(String ipOrigem) {
        this.ipOrigem = ipOrigem;
    }

    public String getTipoEntidade() {
        return tipoEntidade;
    }

    public void setTipoEntidade(String tipoEntidade) {
        this.tipoEntidade = tipoEntidade;
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    public void setUsuCodigo(String usuCodigo) {
        this.usuCodigo = usuCodigo;
    }

    public String getUsuNome() {
        return usuNome;
    }

    public void setUsuNome(String usuNome) {
        this.usuNome = usuNome;
    }

    public String getUsuLogin() {
        return usuLogin;
    }

    public void setUsuLogin(String usuLogin) {
        this.usuLogin = usuLogin;
    }

    public String getUsuEmail() {
        return usuEmail;
    }

    public void setUsuEmail(String usuEmail) {
        this.usuEmail = usuEmail;
    }

    public String getUsuCpf() {
		return isSer() ? getSerCpf() : usuCpf;
	}

	public void setUsuCpf(String usuCpf) {
		this.usuCpf = usuCpf;
	}

	public String getPapDescricao(){
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        if (isCse()) {
            return ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel);
        } else if (isCsa()) {
            return ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel);
        } else if (isCor()) {
            return ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel);
        } else if (isOrg()) {
            return ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel);
        } else if (isSer()) {
            return ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel);
        } else if (isSup()) {
            return ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel);
        } else {
            return "";
        }
    }

    public String getPerDescricao() {
        return perDescricao;
    }

    public void setPerDescricao(String perDescricao) {
        this.perDescricao = perDescricao;
    }

    public String getFunCodigo() {
        return funCodigo;
    }

    public void setFunCodigo(String funCodigo) {
        this.funCodigo = funCodigo;

        if (!CodedValues.FUN_COMP_CONTRATO.equals(funCodigo) && (dadosOperacao != null)) {
            dadosOperacao.reset();
        }
    }

    public String getItmCodigo() {
        return itmCodigo;
    }

    public void setItmCodigo(String itmCodigo) {
        this.itmCodigo = itmCodigo;
    }

    public String getNcaCodigo() {
        return ncaCodigo;
    }

    public void setNcaCodigo(String ncaCodigo) {
        this.ncaCodigo = ncaCodigo;
    }

    public String getUsuChaveValidacaoTotp() {
        return usuChaveValidacaoTotp;
    }

    public void setUsuChaveValidacaoTotp(String usuChaveValidacaoTotp) {
        this.usuChaveValidacaoTotp = usuChaveValidacaoTotp;
    }

    public String getUsuPermiteValidacaoTotp() {
        return usuPermiteValidacaoTotp;
    }

    public void setUsuPermiteValidacaoTotp(String usuPermiteValidacaoTotp) {
        this.usuPermiteValidacaoTotp = usuPermiteValidacaoTotp;
    }

    public OperacaoValidacaoTotpEnum getUsuOperacoesValidacaoTotp() {
        return usuOperacoesValidacaoTotp;
    }

    public void setUsuOperacoesValidacaoTotp(OperacaoValidacaoTotpEnum usuOperacoesValidacaoTotp) {
        this.usuOperacoesValidacaoTotp = usuOperacoesValidacaoTotp;
    }

    public boolean isValidaTotp(boolean autorizarOperacao) {
        return (isSup() ? isPermiteTotp() : true) && !TextHelper.isNull(usuChaveValidacaoTotp) && operacaoUsaTotp(autorizarOperacao);
    }

    private boolean operacaoUsaTotp(boolean autorizarOperacao) {
        return (autorizarOperacao && OperacaoValidacaoTotpEnum.AUTORIZACAO_OPERACAO_SENSIVEL.equals(usuOperacoesValidacaoTotp)) ||
                (!autorizarOperacao && OperacaoValidacaoTotpEnum.AUTENTICACAO_SISTEMA.equals(usuOperacoesValidacaoTotp)) ||
                OperacaoValidacaoTotpEnum.AMBOS.equals(usuOperacoesValidacaoTotp);
    }

    public boolean isPermiteTotp() {
        return TextHelper.isNull(usuPermiteValidacaoTotp) ? false : !"N".equals(usuPermiteValidacaoTotp);
    }

    public boolean isPrimeiroAcesso() {
        return primeiroAcesso;
    }

    public void setPrimeiroAcesso(boolean primeiroAcesso) {
        this.primeiroAcesso = primeiroAcesso;
    }

    public boolean isSessaoInvalidaErroSeg() {
        return sessaoInvalidaErroSeg;
    }

    public void setSessaoInvalidaErroSeg(boolean sessaoInvalidaErroSeg) {
        this.sessaoInvalidaErroSeg = sessaoInvalidaErroSeg;
    }

    public void setDadosServidor(String estCodigo, String orgCodigo, String rseCodigo, String rseMatricula, String serCpf, String serEmail, String rsePrazo, String srsCodigo) {
        dadosServidor = new DadosServidor(estCodigo, orgCodigo, rseCodigo, rseMatricula, serCpf, serEmail, rsePrazo, srsCodigo);
    }

    public void setDadosOperacao(String rseCodigo, List<String> adeCodigos) {
        dadosOperacao = new DadosOperacao(rseCodigo, adeCodigos);
    }

    public void setPermissoes(Map<String, EnderecoFuncaoTransferObject> permissoes) {
        this.permissoes = permissoes;
    }

    public Map<String, EnderecoFuncaoTransferObject> getPermissoes() {
        return permissoes;
    }

    public void setDeficienteVisual(boolean deficienteVisual) {
        this.deficienteVisual = deficienteVisual;
    }

    public boolean isDeficienteVisual() {
        return deficienteVisual;
    }

    public AcessoRecurso getRecursoAcessado() {
        return recursoAcessado;
    }

    public void setRecursoAcessado(AcessoRecurso recursoAcessado) {
        this.recursoAcessado = recursoAcessado;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getDataUltimaRequisicao() {
        return dataUltimaRequisicao;
    }

    public void setDataUltimaRequisicao(Date dataUltimaRequisicao) {
        this.dataUltimaRequisicao = dataUltimaRequisicao;
    }

    public SSOToken getSsoToken() {
        return ssoToken;
    }

    public void setSsoToken(SSOToken ssoToken) {
        this.ssoToken = ssoToken;
    }

    public CanalEnum getCanal() {
		return canal == null ? CanalEnum.WEB : canal;
	}

	public void setCanal(CanalEnum canal) {
		this.canal = canal;
	}

	public boolean isOperacaoViaLote() {
	    return (funCodigo != null) && CodedValues.FUNCOES_IMPORTACAO_LOTE.contains(funCodigo);
	}

	public boolean isNavegadorExclusivo() {
        return navegadorExclusivo;
    }

    public void setNavegadorExclusivo(boolean navegadorExclusivo) {
        this.navegadorExclusivo = navegadorExclusivo;
    }

    public void setPermissaoUnidadesEdt(List<String> permissaoUnidadesEdt) {
        this.permissaoUnidadesEdt = permissaoUnidadesEdt;
    }

    /**
     * Valida apenas se a função esta presente, não valida as restrições por IP/DDNS da função (tb_endereco_acesso_funcao).
     * @param funCodigo
     * @return
     */
    public boolean temPermissao(String funCodigo) {
        return temPermissao(funCodigo, false);
    }

	/**
     * Verifica a permissão usando a tabela de controle de acesso de função por IP/DDNS (tb_endereco_acesso_funcao).
     * @param funCodigo
     * @param useRestricaoAcesso
     * @return
     */
    public boolean temPermissao(String funCodigo, boolean useRestricaoAcesso) {
        // Deve verificar se IP/DDNS por usuário é autorizado (DESENV-4522)
        if (!useRestricaoAcesso) {
            return (permissoes != null) && permissoes.containsKey(funCodigo);
        } else if ((permissoes != null) && permissoes.containsKey(funCodigo)) {
            final EnderecoFuncaoTransferObject enderecoFuncaoTo = permissoes.get(funCodigo);
            return (TextHelper.isNull(enderecoFuncaoTo.getEafIpAcesso()) || JspHelper.validaIp(ipUsuario, enderecoFuncaoTo.getEafIpAcesso())) &&
                    (TextHelper.isNull(enderecoFuncaoTo.getEafDdnsAcesso()) || JspHelper.validaDDNS(ipUsuario, enderecoFuncaoTo.getEafDdnsAcesso()));
        } else {
            return false;
        }
    }

    /**
     * Valida apenas se a função esta presente, não valida as restrições por IP/DDNS da função (tb_endereco_acesso_funcao).
     * @param funCodigo
     * @return
     */
    public boolean temPermissao(String[] funCodigos) {
        if (permissoes != null) {
            for (final String funCodigo : funCodigos) {
                if (temPermissao(funCodigo, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<MenuTO> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuTO> menu) {
        this.menu = menu;
    }

    public List<MenuTO> getMenuFavoritos() {
        if (menuFavoritos == null) {
            menuFavoritos = new ArrayList<>();

            for (final MenuTO menuTO : menu) {
                final Integer mnuCodigo = menuTO.getMnuCodigo();
                if (mnuCodigo.equals(Integer.valueOf(MenuEnum.FAVORITOS.getCodigo()))) {
                    menuFavoritos.add(menuTO);
                }
            }
        }

        return menuFavoritos;
    }

    public void limparMenuFavoritos() {
        menuFavoritos = null;
    }

    public void setQtdConsultasMargem(Integer qtdConsultasMargem) {
        this.qtdConsultasMargem = qtdConsultasMargem;
    }

    public boolean isCse() {
        return ENTIDADE_CSE.equals(tipoEntidade);
    }

    public boolean isOrg() {
        return ENTIDADE_ORG.equals(tipoEntidade);
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
        return ENTIDADE_CSA.equals(tipoEntidade);
    }

    public boolean isCor() {
        return ENTIDADE_COR.equals(tipoEntidade);
    }

    public boolean isCsaCor() {
        return isCsa() || isCor();
    }

    public boolean isSer() {
        return ENTIDADE_SER.equals(tipoEntidade);
    }

    public boolean isCseSupSer() {
        return isCseSup() || isSer();
    }

    public boolean isSistema() {
        return equals(getAcessoUsuarioSistema());
    }

    public boolean isSup() {
        return ENTIDADE_SUP.equals(tipoEntidade);
    }

    public boolean isSessaoValida() {
        return !TextHelper.isNull(usuCodigo) && !CodedValues.USU_CODIGO_SISTEMA.equals(usuCodigo);
    }

    /**
     * Retorna o código do papel da entidade.
     * @return
     */
    public String getPapCodigo() {
        if (isCse()) {
            return CodedValues.PAP_CONSIGNANTE;
        } else if (isCsa()) {
            return CodedValues.PAP_CONSIGNATARIA;
        } else if (isCor()) {
            return CodedValues.PAP_CORRESPONDENTE;
        } else if (isOrg()) {
            return CodedValues.PAP_ORGAO;
        } else if (isSer()) {
            return CodedValues.PAP_SERVIDOR;
        } else if (isSup()) {
            return CodedValues.PAP_SUPORTE;
        } else {
            return "";
        }
    }

    /**
     * Retorna o código de consignante da entidade, seja ela
     * um consignante. Nos demais casos null será retornado.
     * @return
     */
    public String getCseCodigo() {
        return isCse() ? getCodigoEntidade() : null;
    }

    /**
     * Retorna o código de estabelecimento da entidade, seja ela
     * um órgão ou um servidor. Nos demais casos null será retornado.
     * @return
     */
    public String getEstCodigo() {
        return isOrg() ? getCodigoEntidadePai() : isSer() ? dadosServidor.estCodigo : null;
    }
    /**
     * Retorna o código de órgão da entidade, seja ela
     * um órgão ou um servidor. Nos demais casos
     * null será retornado.
     * @return
     */
    public String getOrgCodigo() {
        return isOrg() ? getCodigoEntidade() : isSer() ? dadosServidor.orgCodigo : null;
    }

    /**
     * Retorna o código de consignatária da entidade, seja ela
     * uma consignatária ou um correspondente. Nos demais casos
     * null será retornado.
     * @return
     */
    public String getCsaCodigo() {
        return isCsa() ? getCodigoEntidade() : isCor() ? getCodigoEntidadePai(): null;
    }

    /**
     * Retorna o código de correspondente da entidade, seja ela
     * uma correspondente. Nos demais casos null será retornado.
     * @return
     */
    public String getCorCodigo() {
        return isCor() ? getCodigoEntidade() : null;
    }

    /**
     * Retorna o código de servidor da entidade, seja ela
     * um servidor. Nos demais casos null será retornado.
     * @return
     */
    public String getSerCodigo() {
        return isSer() ? getCodigoEntidade() : null;
    }

    /**
     * Retorna o código do registro de servidor da entidade, seja ela
     * um servidor. Nos demais casos null será retornado.
     * @return
     */
    public String getRseCodigo() {
        return isSer() ? dadosServidor.rseCodigo : null;
    }

    /**
     * Retorna a matrícula do registro de servidor da entidade, seja ela
     * um servidor. Nos demais casos null será retornado.
     * @return
     */
    public String getRseMatricula() {
        return isSer() ? dadosServidor.rseMatricula : null;
    }

    /**
     * Retorna o cpf de servidor, seja ela
     * um servidor. Nos demais casos null será retornado.
     * @return
     */
    public String getSerCpf() {
        return isSer() ? dadosServidor.serCpf : null;
    }

    /**
     * Retorna o email do servidor, seja ela
     * um servidor. Nos demais casos null será retornado.
     * @return
     */
    public String getSerEmail() {
        return isSer() ? dadosServidor.serEmail : null;
    }

    /**
     * Retorna o prazo do registro servidor, seja ele um
     * servidor. Nos demais casos null será retornado.
     * @return
     */
    public String getRsePrazo() {
        return isSer() ? dadosServidor.rsePrazo : null;
    }

    /**
     * Retorna o status do registro servidor, seja ele um
     * servidor. Nos demais casos null será retornado.
     * @return
     */
    public String getSrsCodigo() {
        return isSer() ? dadosServidor.srsCodigo : null;
    }

    /**
     * Retorna o código do registro servidor da operação.
     * Caso o responsável seja um servidor, retorna o próprio código do registro servidor.
     * @return
     */
    public String getRseCodigoOperacao() {
        return isSer() ? getRseCodigo() : dadosOperacao.rseCodigo;
    }

    /**
     * retorna a quantidade máxima de consultas de margem em um intervalo de tempo específica do usuário
     * representado por este AcessoSistema
     * @return
     */
    public Integer getQtdConsultasMargem() {
        return qtdConsultasMargem;
    }

    /**
     * Retorna uma lista de códigos de uma consignação da operação.
     * @return
     */
    public List<String> getAdeCodigosOperacao() {
        return dadosOperacao.adeCodigos;
    }

    @Override
    public String toString() {
        return usuCodigo;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof final AcessoSistema other)) {
            return (usuCodigo != null) &&
                    (other.usuCodigo != null) &&
                    usuCodigo.equals(other.usuCodigo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return usuCodigo != null ? usuCodigo.hashCode() : super.hashCode();
    }

    /**
     * Recupera o AcessoSistema dado o código do usuário.
     *
     * @param usuCodigo Código do usuário
     * @param ipAcesso IP de Acesso de origem das requisições
     * @return Retorna o AcessoSistema encontrado.
     * @throws ZetraException Caso não seja encontrado o usuário para o código informado
     */
    public static AcessoSistema recuperaAcessoSistema(String usuCodigo, String ipAcesso, Integer portaLogicaUsuario) throws ZetraException {
        AcessoSistema responsavel = null;

        try {
            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
            final UsuarioTransferObject usuario = usuarioController.findUsuario(usuCodigo, AcessoSistema.getAcessoUsuarioSistema());
            final TransferObject usuarioTO = usuarioController.findTipoUsuarioByLogin(usuario.getUsuLogin(), responsavel);

            responsavel = new AcessoSistema(usuario.getUsuCodigo());
            responsavel.setUsuLogin(usuario.getUsuLogin());
            responsavel.setIpUsuario(ipAcesso);
            responsavel.setPortaLogicaUsuario(portaLogicaUsuario);
            setaEntidadeTipoEntidade(usuarioTO, responsavel);
        } catch (final UsuarioControllerException e) {
            throw new ZetraException(e);
        }

        return responsavel;
    }

    /**
     * Recupera o AcessoSistema de acordo com o login do usuário passado.
     *
     * @param usuLogin Login do usuário a ser encontrado.
     * @param ipAcesso IP de Acesso de origem das requisições
     * @return Retorna o AcessoSistema caso encontrado, ou nulo caso não seja encontrado.
     * @throws ZetraException
     */
    public static AcessoSistema recuperaAcessoSistemaByLogin(String usuLogin, String ipAcesso, Integer portaLogicaUsuario) throws ZetraException {
        AcessoSistema responsavel = null;

        if (TextHelper.isNull(usuLogin)) {
            throw new ZetraException("mensagem.erro.responsavel.nao.encontrado", responsavel);
        }

        try {
            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
            TransferObject usuarioTO = usuarioController.findTipoUsuarioByLogin(usuLogin, responsavel);

            if (usuarioTO == null) {
                // Busca pelo usuLogin assumindo que é e-mail
                final List<TransferObject> usuariosListTO = usuarioController.findUsuarioByEmail(usuLogin, responsavel);

                // Recupera o usuário caso seja somente um
                if ((usuariosListTO != null) && !usuariosListTO.isEmpty() && (usuariosListTO.size() == 1)) {
                	usuarioTO = usuariosListTO.get(0);
                }

                // Levanta exceção se usuário ainda não foi localizado
                if (usuarioTO == null) {
                	throw new ZetraException("mensagem.erro.responsavel.nao.encontrado", responsavel);
                }
            }

            responsavel = new AcessoSistema(usuarioTO.getAttribute(Columns.USU_CODIGO).toString());
            final String[] addresses = ipAcesso == null ? new String[]{null, null} : TextHelper.split(ipAcesso, ADDRESS_SEPARATOR);
            responsavel.setIpUsuario(addresses[0]);
            responsavel.setPortaLogicaUsuario(portaLogicaUsuario);
            if (addresses.length > 1) {
                responsavel.setIpOrigem(addresses[addresses.length - 1]);
            }
            setaEntidadeTipoEntidade(usuarioTO, responsavel);

        } catch (final UsuarioControllerException e) {
            throw new ZetraException(e);
        }

        return responsavel;
    }

    /**
     * Seta a Entidade e o Tipo de Entidade de um AcessoSistema dado o dados do seu usuario.
     *
     * @param usuario Dados do usuário
     * @param acessoSistema AcessoSistema que será setado
     */
    private static void setaEntidadeTipoEntidade(TransferObject usuario, AcessoSistema acessoSistema) throws ZetraException {
        String tipo = "";
        String codigoEntidade = "";
        String nomeEntidade = "";

        final String cseCodigo = usuario.getAttribute(Columns.UCE_CSE_CODIGO) != null ? usuario.getAttribute(Columns.UCE_CSE_CODIGO).toString() : "";
        final String csaCodigo = usuario.getAttribute(Columns.UCA_CSA_CODIGO) != null ? usuario.getAttribute(Columns.UCA_CSA_CODIGO).toString() : "";
        final String corCodigo = usuario.getAttribute(Columns.UCO_COR_CODIGO) != null ? usuario.getAttribute(Columns.UCO_COR_CODIGO).toString() : "";
        final String orgCodigo = usuario.getAttribute(Columns.UOR_ORG_CODIGO) != null ? usuario.getAttribute(Columns.UOR_ORG_CODIGO).toString() : "";
        final String serCodigo = usuario.getAttribute(Columns.USE_SER_CODIGO) != null ? usuario.getAttribute(Columns.USE_SER_CODIGO).toString() : "";
        final String cspCodigo = usuario.getAttribute(Columns.USP_CSE_CODIGO) != null ? usuario.getAttribute(Columns.USP_CSE_CODIGO).toString() : "";

        // Determina o tipo da entidade do usuário
        if (!TextHelper.isNull(cseCodigo)) {
            tipo = ENTIDADE_CSE;
            codigoEntidade = cseCodigo;
            nomeEntidade = usuario.getAttribute(Columns.CSE_NOME) != null ? usuario.getAttribute(Columns.CSE_NOME).toString() : "";

        } else if (!TextHelper.isNull(csaCodigo)) {
            tipo = ENTIDADE_CSA;
            codigoEntidade = csaCodigo;
            nomeEntidade = usuario.getAttribute(Columns.CSA_NOME) != null ? usuario.getAttribute(Columns.CSA_NOME).toString() : "";

        } else if (!TextHelper.isNull(corCodigo)) {
            tipo = ENTIDADE_COR;
            codigoEntidade = corCodigo;
            nomeEntidade = usuario.getAttribute(Columns.COR_NOME) != null ? usuario.getAttribute(Columns.COR_NOME).toString() : "";

        } else if (!TextHelper.isNull(orgCodigo)) {
            tipo = ENTIDADE_ORG;
            codigoEntidade = orgCodigo;
            nomeEntidade = usuario.getAttribute(Columns.ORG_NOME) != null ? usuario.getAttribute(Columns.ORG_NOME).toString() : "";

        } else if (!TextHelper.isNull(serCodigo)) {
            tipo = ENTIDADE_SER;
            codigoEntidade = serCodigo;
            nomeEntidade = usuario.getAttribute(Columns.SER_NOME) != null ? usuario.getAttribute(Columns.SER_NOME).toString() : "";

        } else if (!TextHelper.isNull(cspCodigo)) {
            tipo = ENTIDADE_SUP;
            codigoEntidade = cspCodigo;
            // Na consulta ObtemUsuarioQuery a coluna é retornada como "SUP_NOME"
            nomeEntidade = usuario.getAttribute("SUP_NOME") != null ? usuario.getAttribute("SUP_NOME").toString() : "";
        }

        acessoSistema.setTipoEntidade(tipo);
        acessoSistema.setCodigoEntidade(codigoEntidade);
        acessoSistema.setNomeEntidade(nomeEntidade);

        // Carrega dados da entidade PAI
        if (acessoSistema.isOrg()) {
            final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
            final EstabelecimentoTransferObject est = consignanteController.findEstabelecimentoByOrgao(acessoSistema.getCodigoEntidade(), acessoSistema);

            acessoSistema.setCodigoEntidadePai(est.getEstCodigo());
            acessoSistema.setNomeEntidadePai(est.getEstNome());

        } else if (acessoSistema.isCor()) {
            final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            final ConsignatariaTransferObject csa = consignatariaController.findConsignatariaByCorrespondente(acessoSistema.getCodigoEntidade(), acessoSistema);

            acessoSistema.setCodigoEntidadePai(csa.getCsaCodigo());
            acessoSistema.setNomeEntidadePai(csa.getCsaNome());

        } else if (acessoSistema.isSer()) {
            final boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, acessoSistema);
            final String usuLogin = usuario.getAttribute(Columns.USU_LOGIN).toString();
            final String[] partesLogin = usuLogin.split("-");

            final String idEstabelecimento = partesLogin[0];
            final String idOrgao = loginComEstOrg ? partesLogin[1] : null;
            final String matricula;

            if (loginComEstOrg) {
                final StringBuilder matriculaBuilder = new StringBuilder(partesLogin[2]);
                if(partesLogin.length > 3) {
                    for (int i = 3; i < partesLogin.length; i++) {
                        matriculaBuilder.append("-").append(partesLogin[i]);
                    }
                }
                matricula = matriculaBuilder.toString();
            } else {
                final StringBuilder matriculaBuilder = new StringBuilder(partesLogin[1]);
                if(partesLogin.length > 2) {
                    for (int i = 2; i < partesLogin.length; i++) {
                        matriculaBuilder.append("-").append(partesLogin[i]);
                    }
                }
                matricula = matriculaBuilder.toString();
            }

            final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
            final EstabelecimentoTransferObject est = consignanteController.findEstabelecimentoByIdn(idEstabelecimento, acessoSistema);
            final OrgaoTransferObject org = !TextHelper.isNull(idOrgao) ? consignanteController.findOrgaoByIdn(idOrgao, est.getEstCodigo(), acessoSistema) : new OrgaoTransferObject();

            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            final TransferObject servidor = servidorController.getRegistroServidorPelaMatricula((String) usuario.getAttribute(Columns.USE_SER_CODIGO), org.getOrgCodigo(), est.getEstCodigo(), matricula, acessoSistema);

            acessoSistema.setDadosServidor(servidor.getAttribute(Columns.EST_CODIGO).toString(), servidor.getAttribute(Columns.ORG_CODIGO).toString(), servidor.getAttribute(Columns.RSE_CODIGO).toString(), servidor.getAttribute(Columns.RSE_MATRICULA).toString(), (String) servidor.getAttribute(Columns.SER_CPF), (String) servidor.getAttribute(Columns.SER_EMAIL), null, (String) servidor.getAttribute(Columns.SRS_CODIGO));
        }
    }

    public String getUsuCentralizador() {
		return usuCentralizador;
	}

	public void setUsuCentralizador(String usuCentralizador) {
		this.usuCentralizador = usuCentralizador;
	}
	
	public String getUsuAutenticaSso() {
		return usuAutenticaSso;
	}
	
	public void setUsuAutenticaSso (String usuAutenticaSso) {
		this.usuAutenticaSso = usuAutenticaSso;
	}

	/**
     * Classe que armazena os dados de um usuário servidor quando
     * este acessa o sistema.
     * @author Igor Lucas
     */
    private static class DadosServidor implements Serializable {
        private static final long serialVersionUID = 74L;

        public String estCodigo;
        public String orgCodigo;
        public String rseCodigo;
        public String rseMatricula;
        public String serCpf;
        public String serEmail;
        public String rsePrazo;
        public String srsCodigo;

        public DadosServidor(String estCodigo, String orgCodigo, String rseCodigo, String rseMatricula, String serCpf, String serEmail, String rsePrazo, String srsCodigo) {
            this.estCodigo = estCodigo;
            this.orgCodigo = orgCodigo;
            this.rseCodigo = rseCodigo;
            this.rseMatricula = rseMatricula;
            this.serCpf = serCpf;
            this.serEmail = serEmail;
            this.rsePrazo = rsePrazo;
            this.srsCodigo = srsCodigo;
        }
    }

    /**
     * Classe que armazena os dados de uma operação.
     * @author Alexandre Goncalves
     */
    private static class DadosOperacao implements Serializable {
        private static final long serialVersionUID = 74L;

        public String rseCodigo;
        public List<String> adeCodigos;

        public DadosOperacao(String rseCodigo, List<String> adeCodigos) {
            this.rseCodigo = rseCodigo;
            this.adeCodigos = adeCodigos;
        }

        public void reset() {
            rseCodigo = null;
            adeCodigos = null;
        }
    }

    /**
     * Valida apenas se a unidade do registro passado está na lista de permissões de unidades.
     * @param uniCodigo
     * @return
     */
    public boolean temPermissaoEdtUnidade(String uniCodigo) {
        return (permissaoUnidadesEdt != null) && permissaoUnidadesEdt.contains(uniCodigo);
    }

    /**
     * Quando é um processo do módulo de rescisão, não deve validar carência, pois o contrato deve ser concluído ainda dentro do processo em questão.
     */
    public boolean isRescisao() {
        final List<String> funcoesRescisao = new ArrayList<>();
        funcoesRescisao.add(CodedValues.FUN_DEMITIR_COLABORADOR);
        funcoesRescisao.add(CodedValues.FUN_PROCESSA_LOTE_RECISAO);

        return (funCodigo != null) && funcoesRescisao.contains(funCodigo);
    }
}
