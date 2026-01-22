package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_MAE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_NOME;
import static com.zetra.econsig.webservice.CamposAPI.FUNCOES;
import static com.zetra.econsig.webservice.CamposAPI.PER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ENTIDADE;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO_AFETADO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO_CADASTRADO;
import static com.zetra.econsig.webservice.CamposAPI.USU_DATA_EXP_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USU_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.USU_NOME;
import static com.zetra.econsig.webservice.CamposAPI.USU_SENHA;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.folha.v1.PapelUsuario;

/**
 * <p>Title: CadastrarUsuarioCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de cadastrar usuário</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CadastrarUsuarioCommand extends RequisicaoExternaFolhaCommand {

    protected String tipoEntidade = null;
    protected String codigoEntidade = null;

    public CadastrarUsuarioCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        UsuarioDelegate usuDelegate = new UsuarioDelegate();
        UsuarioTransferObject usuario = (UsuarioTransferObject) parametros.get(USUARIO_AFETADO);

        String perCodigo = (String) parametros.get(PER_CODIGO);
        List<String> funcoes = (ArrayList<String>) parametros.get(FUNCOES);
        List<String> lstFuncoes = null;
        if (funcoes != null && !funcoes.isEmpty()) {
            /*
             * IMPORTANTE: Não usar Arrays.asList(), caso seja informado um array de String com função vazia,
             * será gerada uma lista com função vazia.
             */
            for (String funcao : funcoes) {
                if (!TextHelper.isNull(funcao)) {
                    if (lstFuncoes == null) {
                        lstFuncoes = new ArrayList<>();
                    }
                    lstFuncoes.add(funcao);
                }
            }
        }

        // Retorna erro caso perfil informado não pertença a entidade do usuário a ser criado ou esteja inativo
        if (!TextHelper.isNull(perCodigo)) {
            Short stsPerfil = usuDelegate.getStatusPerfil(tipoEntidade, codigoEntidade, perCodigo, responsavel);
            if (stsPerfil == null || !stsPerfil.equals(CodedValues.STS_ATIVO)) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
        }

        String usuCentralizador = usuario.getUsuCentralizador();
        if (responsavel.isSup() && !TextHelper.isNull(usuCentralizador)) {
            if (usuCentralizador.equalsIgnoreCase(CodedValues.TPC_SIM)) {
                usuario.setUsuCentralizador(CodedValues.TPC_SIM);
                // Caso seja usuário do Centralizador, a senha não poderá expirar.
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.YEAR, 2099);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);

                usuario.setUsuDataExpSenha(new java.sql.Date(cal.getTimeInMillis()));
            } else if (usuCentralizador.equalsIgnoreCase(CodedValues.TPC_NAO)) {
                usuario.setUsuCentralizador(CodedValues.TPC_NAO);
                usuario.setUsuDataExpSenha(null);
            } else {
                usuario.setUsuCentralizador(null);
            }
        } else {
            usuario.setUsuCentralizador(null);
        }

        parametros.put(USUARIO_CADASTRADO, saveOrUpdateUsuario(usuDelegate, usuario, perCodigo, lstFuncoes));

    }

    protected Map<CamposAPI, Object> saveOrUpdateUsuario(UsuarioDelegate usuDelegate, UsuarioTransferObject usuario, String perCodigo, List<String> lstFuncoes) throws ZetraException {
        // Nome deve ser informado
        if (TextHelper.isNull(usuario.getUsuNome())) {
            throw new ZetraException("mensagem.informe.usu.nome", responsavel);
        }

        // Pelo menos o código do perfil ou a lista de funções, com pelo menos um item, deve ser informado.
        if (TextHelper.isNull(perCodigo) && (lstFuncoes == null || lstFuncoes.isEmpty())) {
            throw new ZetraException("mensagem.erro.informe.permissao", responsavel);
        }

        // Caso não seja informado o status, usuário será criado ativo
        if (TextHelper.isNull(usuario.getStuCodigo())) {
            usuario.setStuCodigo(CodedValues.STU_ATIVO);
        }

        if (usuario.getStuCodigo().equals(CodedValues.STU_EXCLUIDO)) {
            throw new ZetraException("mensagem.erro.situacao.usuario.nao.permite.operacao", responsavel);
        }

        /*
         * Caso a senha não seja informada, o sistema deverá gerar uma senha aleatória, expirada,
         * que deve ser alterada no primeiro acesso ao sistema.
         */
        String usuSenha = null;

        boolean validaForcaSenha = true;

        if (TextHelper.isNull(usuario.getUsuSenha())) {
            usuSenha = SenhaHelper.gerarSenhaAleatoria(UsuarioHelper.obterTipoEntidade(usuario), responsavel);
            usuario.setUsuSenha(SenhaHelper.criptografarSenha(usuario.getUsuLogin(), usuSenha, false, responsavel));
            validaForcaSenha = false;
        } else {
            // Criptografa senha enviada pelo usuário
            usuSenha = usuario.getUsuSenha();
            usuario.setUsuSenha(SenhaHelper.criptografarSenha(usuario.getUsuLogin(), usuario.getUsuSenha(), false, responsavel));
        }

        // Cria usuário
        String usuCodigo = null;
        if (TextHelper.isNull(perCodigo) && !ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERFIL_PERSONALIZADO, CodedValues.TPC_NAO, responsavel)) {
            usuCodigo = usuDelegate.createUsuario(usuario, lstFuncoes, codigoEntidade, tipoEntidade, null, true, usuSenha, validaForcaSenha, responsavel);
        } else {
            usuCodigo = usuDelegate.createUsuario(usuario, perCodigo, codigoEntidade, tipoEntidade, null, true, usuSenha, validaForcaSenha, responsavel);
        }

        // Grava null na data de ultimo acesso para que o tutorial de primeiro acesso funcione
        usuDelegate.alteraDataUltimoAcessoSistema(new AcessoSistema(usuCodigo));

        /*
         * O WebService deverá retornar, além do código de retorno e mensagem, as seguintes informações:
         * Identificador da entidade do usuário criado;
         * Nome da entidade do usuário criado;
         * Nome do usuário criado;
         * Login do usuário criado;
         * Senha Plana criada, caso não seja informada, nulo caso informado;
         * Data de expiração da senha do usuário criado.
        */
        TransferObject usuCriado = usuDelegate.obtemUsuarioTipo(usuCodigo, null, responsavel);
        Map<CamposAPI, Object> usuCadastrado = new HashMap<>();
        usuCadastrado.put(ENTIDADE_CODIGO, usuCriado.getAttribute("IDENTIFICADOR"));
        usuCadastrado.put(ENTIDADE_NOME, usuCriado.getAttribute("ENTIDADE"));
        usuCadastrado.put(USU_NOME, usuCriado.getAttribute(Columns.USU_NOME));
        usuCadastrado.put(USU_LOGIN, usuCriado.getAttribute(Columns.USU_LOGIN));
        usuCadastrado.put(USU_SENHA, usuSenha);
        usuCadastrado.put(USU_DATA_EXP_SENHA, !TextHelper.isNull(usuCriado.getAttribute(Columns.USU_DATA_EXP_SENHA)) ? usuCriado.getAttribute(Columns.USU_DATA_EXP_SENHA) : DateHelper.getSystemDate());

        return usuCadastrado;
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        validarTipoEntidade();
        validarCodigoEntidade();

        String funCodigo = retornaFuncaoParaValidacao(parametros);

        if (funCodigo == null || !responsavel.temPermissao(funCodigo)) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        // Seta qual a função está sendo acessada, para gravação de log
        responsavel.setFunCodigo(funCodigo);
    }

    protected void validarTipoEntidade() throws ZetraException {
        PapelUsuario papel = (PapelUsuario) parametros.get(TIPO_ENTIDADE);
        if (papel == null) {
            throw new ZetraException("mensagem.informe.tipo.entidade", responsavel);
        }

        if (papel.getCse()) {
            tipoEntidade = AcessoSistema.ENTIDADE_CSE;
        } else if (papel.getOrg()) {
            tipoEntidade = AcessoSistema.ENTIDADE_ORG;
        } else if (papel.getCsa()) {
            tipoEntidade = AcessoSistema.ENTIDADE_CSA;
        } else if (papel.getCor()) {
            tipoEntidade = AcessoSistema.ENTIDADE_COR;
        } else if (papel.getSup()) {
            tipoEntidade = AcessoSistema.ENTIDADE_SUP;
        } else {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel);
        }
    }

    protected void validarCodigoEntidade() throws ZetraException {
        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();

        String idEntidade = (String) parametros.get(ENTIDADE_CODIGO);
        String idEntidadeMae = (String) parametros.get(ENTIDADE_MAE_CODIGO);

        // Em caso de tipo de entidade COR ou ORG o identificador da entidade pai NÃO poderá será nulo.
        PapelUsuario papel = (PapelUsuario) parametros.get(TIPO_ENTIDADE);
        if (papel.getCor() && TextHelper.isNull(idEntidadeMae)) {
            throw new ZetraException("mensagem.informe.consignataria", responsavel);
        } else if (papel.getOrg() && TextHelper.isNull(idEntidadeMae)) {
            throw new ZetraException("mensagem.informe.estabelecimento", responsavel);
        }

        // Em caso de tipo de entidade CSE e SUP o identificador da entidade será nulo.
        if (papel.getCse() || papel.getSup()) {
            codigoEntidade = CodedValues.CSE_CODIGO_SISTEMA;
        } else if (papel.getOrg()) {
            OrgaoTransferObject org = null;
            if (responsavel.isOrg()) {
                org = cseDelegate.findOrgao(responsavel.getOrgCodigo(), responsavel);
            } else {
                EstabelecimentoTransferObject est = cseDelegate.findEstabelecimentoByIdn(idEntidadeMae, responsavel);
                org = cseDelegate.findOrgaoByIdn(idEntidade, est.getEstCodigo(), responsavel);
            }
            codigoEntidade = org.getOrgCodigo();
        } else if (papel.getCsa()) {
            ConsignatariaTransferObject csa = null;
            if (responsavel.isCsa()) {
                csa = csaDelegate.findConsignataria(responsavel.getCsaCodigo(), responsavel);
            } else {
                csa = csaDelegate.findConsignatariaByIdn(idEntidade, responsavel);
            }
            codigoEntidade = csa.getCsaCodigo();
        } else if (papel.getCor()) {
            ConsignatariaTransferObject csa = null;
            CorrespondenteTransferObject cor = null;
            if (responsavel.isCsa()) {
                cor = csaDelegate.findCorrespondenteByIdn(idEntidade, responsavel.getCsaCodigo(), responsavel);
            } else if (responsavel.isCor()) {
                cor = csaDelegate.findCorrespondenteByIdn(responsavel.getCorCodigo(), responsavel.getCsaCodigo(), responsavel);
            } else {
                csa = csaDelegate.findConsignatariaByIdn(idEntidadeMae, responsavel);
                cor = csaDelegate.findCorrespondenteByIdn(idEntidade, csa.getCsaCodigo(), responsavel);
            }

            codigoEntidade = cor.getCorCodigo();
        }
    }

    protected String retornaFuncaoParaValidacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String funCodigo = null;
        if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
            if (!responsavel.isCseSup()) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
            funCodigo = CodedValues.FUN_CRIAR_USUARIOS_CSE;
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
            if (!responsavel.isCseSupOrg()) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
            funCodigo = CodedValues.FUN_CRIAR_USUARIOS_ORG;
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA)) {
            if (responsavel.isCor()) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
            funCodigo = CodedValues.FUN_CRIAR_USUARIOS_CSA;
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_COR)) {
            funCodigo = CodedValues.FUN_CRIAR_USUARIOS_COR;
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_SUP)) {
            if (!responsavel.isSup()) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
            funCodigo = CodedValues.FUN_CRIAR_USUARIOS_SUP;
        }

        return funCodigo;
    }
}
