package com.zetra.econsig.persistence.query.usuario;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuariosEntidadeQuery</p>
 * <p>Description: Listagem de usuários de uma determinada entidade</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuariosEntidadeQuery extends HQuery {
    public boolean count = false;
    public String tipo;

    public String entCodigo;
    public String perCodigo;
    public String perDescricao;
    public Object stuCodigo;
    public String usuCodigo;
    public String usuLogin;
    public String usuNome;
    public String usuCpf;
    public String usuEmail;
    public boolean ocultaUsuVisivel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (count) {
            corpo = "select count(*) as total ";
        } else {
            corpo = "select usuario.usuCodigo, " +
                    "case " +
                    "when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '(*)'), ''), usuario.usuLogin) " +
                    "else usuario.usuLogin end AS USU_LOGIN, " +
                    "usuario.usuNome, usuario.statusLogin.stuCodigo," +
                    "usuario.usuTipoBloq, perfil.perCodigo, perfil.perDescricao, usuario.usuEmail, " +
                    "case when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then 99 else 1 end as ORDEM ";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (tipo.equals(AcessoSistema.ENTIDADE_CSE)) {
            if (!count) {
                corpoBuilder.append(" , usuarioCse.cseCodigo as CODIGO_ENTIDADE ");
            }

            corpoBuilder.append(" from Usuario usuario ");
            corpoBuilder.append("left outer join usuario.usuarioCseSet usuarioCse ");
            corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
            corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");

            corpoBuilder.append(" where 1=1 ");

            if (!TextHelper.isNull(entCodigo)) {
                corpoBuilder.append(" and usuarioCse.cseCodigo ").append(criaClausulaNomeada("entCodigo", entCodigo));
            }
        } else if (tipo.equals(AcessoSistema.ENTIDADE_ORG)) {
            if (!count) {
                corpoBuilder.append(" , usuarioOrg.orgCodigo as CODIGO_ENTIDADE ");
            }

            corpoBuilder.append(" from Usuario usuario ");
            corpoBuilder.append("left outer join usuario.usuarioOrgSet usuarioOrg ");
            corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
            corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");

            corpoBuilder.append(" where 1=1 ");

            if (!TextHelper.isNull(entCodigo)) {
                corpoBuilder.append(" and usuarioOrg.orgCodigo ").append(criaClausulaNomeada("entCodigo", entCodigo));
            }
        } else if (tipo.equals(AcessoSistema.ENTIDADE_CSA)) {
            if (!count) {
                corpoBuilder.append(" , usuarioCsa.csaCodigo as CODIGO_ENTIDADE ");
            }

            corpoBuilder.append(" from Usuario usuario ");
            corpoBuilder.append("left outer join usuario.usuarioCsaSet usuarioCsa ");
            corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
            corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");

            corpoBuilder.append(" where 1=1 ");

            if (!TextHelper.isNull(entCodigo)) {
                corpoBuilder.append(" and usuarioCsa.csaCodigo ").append(criaClausulaNomeada("entCodigo", entCodigo));
            }
        } else if (tipo.equals(AcessoSistema.ENTIDADE_COR)) {
            if (!count) {
                corpoBuilder.append(" , usuarioCor.corCodigo as CODIGO_ENTIDADE ");
            }

            corpoBuilder.append(" from Usuario usuario ");
            corpoBuilder.append("left outer join usuario.usuarioCorSet usuarioCor ");
            corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
            corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");

            corpoBuilder.append(" where 1=1 ");

            if (!TextHelper.isNull(entCodigo)) {
                corpoBuilder.append(" and usuarioCor.corCodigo ").append(criaClausulaNomeada("entCodigo", entCodigo));
            }
        } else if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) {
            if (!count) {
                corpoBuilder.append(" , usuarioSup.cseCodigo as CODIGO_ENTIDADE ");
            }

            corpoBuilder.append(" from Usuario usuario ");
            corpoBuilder.append("left outer join usuario.usuarioSupSet usuarioSup ");
            corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
            corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");

            corpoBuilder.append(" where 1=1 ");

            if (!TextHelper.isNull(entCodigo)) {
                corpoBuilder.append(" and usuarioSup.cseCodigo ").append(criaClausulaNomeada("entCodigo", entCodigo));
            }
        } else {
            throw new HQueryException("mensagem.erro.tipo.entidade.invalido", (AcessoSistema) null);
        }

        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("usuario.usuCodigo", "usuCodigo", usuCodigo));
        }

        if (!TextHelper.isNull(usuLogin)) {
            corpoBuilder.append(" and ( ").append(criaClausulaNomeada("usuario.usuLogin", "usuLogin", usuLogin));
            corpoBuilder.append(" or (usuario.statusLogin.stuCodigo = '").append(CodedValues.STU_EXCLUIDO).append("'");
            corpoBuilder.append(" and ").append(criaClausulaNomeada("usuario.usuTipoBloq", "usuLogin", usuLogin)).append(")) ");
        }

        if (!TextHelper.isNull(usuNome)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("usuario.usuNome", "usuNome", usuNome));
        }
        
        if (!TextHelper.isNull(usuCpf)) {
            corpoBuilder.append(" and usuario.usuCpf ").append(criaClausulaNomeada("usuCpf", usuCpf));
        }

        if (!TextHelper.isNull(usuEmail)) {
            corpoBuilder.append(" and usuario.usuEmail ").append(criaClausulaNomeada("usuEmail", usuEmail));
        }

        if (!TextHelper.isNull(stuCodigo)) {
            corpoBuilder.append(" and usuario.statusLogin.stuCodigo " ).append(criaClausulaNomeada("stuCodigo", stuCodigo));
        }

        if (!TextHelper.isNull(perCodigo)) {
            corpoBuilder.append(" and perfil.perCodigo " ).append(criaClausulaNomeada("perCodigo", perCodigo));
        }

        if (!TextHelper.isNull(perDescricao)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("perfil.perDescricao", "perDescricao", perDescricao));
        }

        if (ocultaUsuVisivel) {
            corpoBuilder.append(" and COALESCE(usuario.usuVisivel, 'S') <> 'N'");
            corpoBuilder.append(" and COALESCE(perfil.perVisivel, 'S') <> 'N'");
        }

        final List<Object> status = new ArrayList<>();
        status.add(CodedValues.STU_ATIVO);
        status.add(CodedValues.STU_BLOQUEADO);
        status.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE);
        status.add(CodedValues.STU_BLOQUEADO_POR_CSE);
        status.add(CodedValues.STU_BLOQUEADO_AUSENCIA_TEMPORARIA);
        status.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
        status.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_FIM_VIGENCIA);

        if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_USUARIOS_EXCLUIDOS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            status.add(CodedValues.STU_EXCLUIDO);
        }
        corpoBuilder.append(" AND usuario.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));

        if (!count) {
            corpoBuilder.append(" order by ");
            corpoBuilder.append("case when usuario.statusLogin.stuCodigo = '").append(CodedValues.STU_EXCLUIDO);
            corpoBuilder.append("' then 99 else 1 end ASC, usuario.usuNome");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Seta os parâmetros na query
        if (!TextHelper.isNull(entCodigo)) {
            defineValorClausulaNomeada("entCodigo", entCodigo, query);
        }

        if (!TextHelper.isNull(perCodigo)) {
            defineValorClausulaNomeada("perCodigo", perCodigo, query);
        }

        if (!TextHelper.isNull(perDescricao)) {
            defineValorClausulaNomeada("perDescricao", perDescricao, query);
        }

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(usuLogin)) {
            defineValorClausulaNomeada("usuLogin", usuLogin, query);
        }

        if (!TextHelper.isNull(usuNome)) {
            defineValorClausulaNomeada("usuNome", usuNome, query);
        }
        
        if (!TextHelper.isNull(usuCpf)) {
            defineValorClausulaNomeada("usuCpf", usuCpf, query);
        }

        if (!TextHelper.isNull(usuEmail)) {
            defineValorClausulaNomeada("usuEmail", usuEmail, query);
        }

        if (stuCodigo != null) {
            defineValorClausulaNomeada("stuCodigo", stuCodigo, query);
        }

        defineValorClausulaNomeada("status", status, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_NOME,
                Columns.USU_STU_CODIGO,
                Columns.USU_TIPO_BLOQ,
                Columns.PER_CODIGO,
                Columns.PER_DESCRICAO,
                Columns.USU_EMAIL,
                "ORDEM",
                "CODIGO_ENTIDADE"
        };
    }
}
