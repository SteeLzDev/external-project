package com.zetra.econsig.persistence.query.usuario;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuariosQuery</p>
 * <p>Description: Listagem geral de usuários</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuariosQuery extends HQuery {
    public String usuLogin;
    public String usuNome;
    public String usuCpf;
    public String usuCodigo;
    public String cseNome;
    public String orgNome;
    public String csaNome;
    public String csaNomeAbrev;
    public String corNome;
    public String usuCseUsuCodigo;
    public String usuOrgUsuCodigo;
    public String usuCsaUsuCodigo;
    public String usuCorUsuCodigo;
    public Object stuCodigo;
    public AcessoSistema responsavel;

    public String entCodigo;
    public boolean ocultaUsuVisivel;

    public boolean count = false;
    public String tipo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";
        Query<Object[]> query = null;

        if (tipo.equals(AcessoSistema.ENTIDADE_CSE)) {
            StringBuilder corpoBuilder = null;
            if (count) {
                corpoBuilder = new StringBuilder("select count(distinct usuario.usuCodigo) as total ");
            } else {
                corpo = "select distinct usuario.usuCodigo, " +
                "case " +
                "when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '(*)'), ''), usuario.usuLogin) " +
                "else usuario.usuLogin end AS USU_LOGIN, " +
                "usuario.usuNome, usuario.statusLogin.stuCodigo," +
                "usuario.usuTipoBloq, perfil.perCodigo, perfil.perDescricao," +
                " case ";
                corpoBuilder = new StringBuilder(corpo);
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE)) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioCse.usuCodigo then 'CSE'");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioCsa.usuCodigo then 'CSA'");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR)) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioCor.usuCodigo then 'COR'");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP) && responsavel.isSup()) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioSup.usuCodigo then 'SUP'");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG)) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioOrg.usuCodigo then 'ORG'");
                }
                corpoBuilder.append(" else NULL end as TIPO," );
                corpoBuilder.append(" case ");
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE)) {
                    corpoBuilder.append("           when usuario.usuCodigo = usuarioCse.usuCodigo then consignante.cseNome");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioCsa.usuCodigo then consignataria.csaNome");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR)) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioCor.usuCodigo then concat(concat(CSA_COR.csaNome,' - '), correspondente.corNome)");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP) && responsavel.isSup()) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioSup.usuCodigo then '"+ ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel).toUpperCase() +"' ");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG)) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioOrg.usuCodigo then orgao.orgNome");
                }
                corpoBuilder.append(" else NULL end as ENTIDADE, ");
                corpoBuilder.append(" case ");
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE)) {
                    corpoBuilder.append("           when usuario.usuCodigo = usuarioCse.usuCodigo then usuarioCse.cseCodigo");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioCsa.usuCodigo then usuarioCsa.csaCodigo");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR)) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioCor.usuCodigo then usuarioCor.corCodigo");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP) && responsavel.isSup()) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioSup.usuCodigo then usuarioSup.cseCodigo");
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG)) {
                    corpoBuilder.append("       when usuario.usuCodigo = usuarioOrg.usuCodigo then usuarioOrg.orgCodigo");
                }
                corpoBuilder.append(" else NULL end as CODIGO_ENTIDADE, ");
                corpoBuilder.append("case when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then 99 else 1 end as ORDEM ");
            }


            corpoBuilder.append("from Usuario usuario ");
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE)) {
            corpoBuilder.append("left outer join usuario.usuarioCseSet usuarioCse ");
            corpoBuilder.append("left outer join usuarioCse.consignante consignante ");
            }
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
            corpoBuilder.append("left outer join usuario.usuarioCsaSet usuarioCsa ");
            corpoBuilder.append("left outer join usuarioCsa.consignataria consignataria ");
            }
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR)) {
            corpoBuilder.append("left outer join usuario.usuarioCorSet usuarioCor ");
            corpoBuilder.append("left outer join usuarioCor.correspondente correspondente ");
            corpoBuilder.append("left outer join correspondente.consignataria CSA_COR ");
            }
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG)) {
            corpoBuilder.append("left outer join usuario.usuarioOrgSet usuarioOrg ");
            corpoBuilder.append("left outer join usuarioOrg.orgao orgao ");
            }
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP) && responsavel.isSup()) {
            corpoBuilder.append("left outer join usuario.usuarioSupSet usuarioSup ");
            }
            corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
            corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");

            corpoBuilder.append(" where 1=1 ");
            StringBuilder whereBuilder = null;
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE)) {
                whereBuilder = new StringBuilder("and (");
                whereBuilder.append("usuario.usuCodigo = usuarioCse.usuCodigo ");
            }
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                if (whereBuilder == null) {
                    whereBuilder = new StringBuilder("and (");
                    whereBuilder.append("usuario.usuCodigo = usuarioCsa.usuCodigo ");
                } else {
                    whereBuilder.append(" or ");
                    whereBuilder.append("usuario.usuCodigo = usuarioCsa.usuCodigo ");
                }
            }
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR)) {
                if (whereBuilder == null) {
                    whereBuilder = new StringBuilder("and (");
                    whereBuilder.append("usuario.usuCodigo = usuarioCor.usuCodigo ");
                } else {
                    whereBuilder.append(" or ");
                    whereBuilder.append("usuario.usuCodigo = usuarioCor.usuCodigo ");
                }
            }
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG)) {
                if (whereBuilder == null) {
                    whereBuilder = new StringBuilder("and (");
                    whereBuilder.append("usuario.usuCodigo = usuarioOrg.usuCodigo ");
                } else {
                    whereBuilder.append(" or ");
                    whereBuilder.append("usuario.usuCodigo = usuarioOrg.usuCodigo ");
                }
            }
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP) && responsavel.isSup()) {
                if (whereBuilder == null) {
                    whereBuilder = new StringBuilder("and (");
                    whereBuilder.append("usuario.usuCodigo = usuarioSup.usuCodigo ");
                } else {
                    whereBuilder.append(" or ");
                    whereBuilder.append("usuario.usuCodigo = usuarioSup.usuCodigo ");
                }
            }
            if (whereBuilder != null) {
                whereBuilder.append(")");
                corpoBuilder.append(whereBuilder.toString());
            }

            query = setWhereClause(session, corpoBuilder);
        } else if (tipo.equals(AcessoSistema.ENTIDADE_CSA)) {
            StringBuilder corpoBuilder = null;
            if (count) {
                corpoBuilder = new StringBuilder("select count(distinct usuario.usuCodigo) as total ");
            } else {
                 corpoBuilder = new StringBuilder("select distinct usuario.usuCodigo, " +
                        "case " +
                        "when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '(*)'), ''), usuario.usuLogin) " +
                        "else usuario.usuLogin end AS USU_LOGIN, " +
                        "usuario.usuNome, usuario.statusLogin.stuCodigo," +
                        "       usuario.usuTipoBloq, perfil.perCodigo, perfil.perDescricao," +
                        "case ");
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioCsa.usuCodigo then 'CSA' ");
                        }
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioCor.usuCodigo then 'COR' ");
                        }
                        corpoBuilder.append(" else NULL end as TIPO,");
                        corpoBuilder.append("case ");
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioCsa.usuCodigo then consignataria.csaNome ");
                        }
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioCor.usuCodigo then correspondente.corNome ");
                        }
                        corpoBuilder.append(" else NULL end as ENTIDADE, ");
                        corpoBuilder.append("case ");
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioCsa.usuCodigo then usuarioCsa.csaCodigo ");
                        }
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioCor.usuCodigo then usuarioCor.corCodigo ");
                        }
                        corpoBuilder.append("else NULL end as CODIGO_ENTIDADE, " );
                        corpoBuilder.append("case when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then 99 else 1 end as ORDEM ");
            }

            corpoBuilder.append("from Usuario usuario ");
            corpoBuilder.append("left outer join usuario.usuarioCsaSet usuarioCsa ");
            corpoBuilder.append("left outer join usuarioCsa.consignataria consignataria ");
            corpoBuilder.append("left outer join usuario.usuarioCorSet usuarioCor ");
            corpoBuilder.append("left outer join usuarioCor.correspondente correspondente ");
            corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
            corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");

            corpoBuilder.append(" where 1=1 ");
            StringBuilder whereBuilder = null;
            if (!TextHelper.isNull(entCodigo)) {
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                    whereBuilder = new StringBuilder(" and ((usuarioCsa.csaCodigo ").append(criaClausulaNomeada("entCodigo", entCodigo));
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR)) {
                    if (whereBuilder == null) {
                        whereBuilder = new StringBuilder("and (");
                        whereBuilder.append(" (usuarioCor.corCodigo = correspondente.corCodigo");
                        whereBuilder.append(" and correspondente.consignataria.csaCodigo" ).append(criaClausulaNomeada("entCodigo", entCodigo));
                    } else {
                        whereBuilder.append(") or (usuarioCor.corCodigo = correspondente.corCodigo");
                        whereBuilder.append(" and correspondente.consignataria.csaCodigo" ).append(criaClausulaNomeada("entCodigo", entCodigo));
                    }
                }
                whereBuilder.append("))");
                corpoBuilder.append(whereBuilder.toString());
            }

            query = setWhereClause(session, corpoBuilder);

            if (!TextHelper.isNull(entCodigo)) {
                defineValorClausulaNomeada("entCodigo", entCodigo, query);
            }
        } else if (tipo.equals(AcessoSistema.ENTIDADE_COR)) {
            if (count) {
                corpo = "select count(distinct usuario.usuCodigo) as total ";
            } else {
                corpo = "select distinct usuario.usuCodigo, " +
                        "case " +
                        "when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '(*)'), ''), usuario.usuLogin) " +
                        "else usuario.usuLogin end AS USU_LOGIN, " +
                        "usuario.usuNome, usuario.statusLogin.stuCodigo," +
                        "       usuario.usuTipoBloq, perfil.perCodigo, perfil.perDescricao," +
                        " 'COR' as TIPO, " +
                        "correspondente.corNome as ENTIDADE, " +
                        "correspondente.corCodigo as CODIGO_ENTIDADE, " +
                        "case when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then 99 else 1 end as ORDEM ";
            }

            StringBuilder corpoBuilder = new StringBuilder(corpo);

            corpoBuilder.append("from Usuario usuario ");
            corpoBuilder.append("left outer join usuario.usuarioCorSet usuarioCor ");
            corpoBuilder.append("left outer join usuarioCor.correspondente correspondente ");
            corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
            corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");

            corpoBuilder.append(" where 1=1 ");

            if (!TextHelper.isNull(entCodigo)) {
                corpoBuilder.append(" and usuarioCor.corCodigo ").append(criaClausulaNomeada("entCodigo", entCodigo));
            }

            query = setWhereClause(session, corpoBuilder);

            if (!TextHelper.isNull(entCodigo)) {
                defineValorClausulaNomeada("entCodigo", entCodigo, query);
            }

        } else if (tipo.equals(AcessoSistema.ENTIDADE_ORG)) {
            StringBuilder corpoBuilder = null;
            if (count) {
                corpoBuilder = new StringBuilder("select count(distinct usuario.usuCodigo) as total ");
            } else {
                 corpoBuilder = new StringBuilder("select distinct usuario.usuCodigo, " +
                        "case " +
                        "when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '(*)'), ''), usuario.usuLogin) " +
                        "else usuario.usuLogin end AS USU_LOGIN, " +
                        "usuario.usuNome, usuario.statusLogin.stuCodigo," +
                        "       usuario.usuTipoBloq, perfil.perCodigo, perfil.perDescricao," +
                        "case ");
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioOrg.usuCodigo then 'ORG' ");
                        }
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioCsa.usuCodigo then 'CSA' ");
                        }
                        corpoBuilder.append(" else NULL end as TIPO,");
                        corpoBuilder.append("case ");
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioOrg.usuCodigo then orgao.orgNome ");
                        }
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioCsa.usuCodigo then consignataria.csaNome ");
                        }
                        corpoBuilder.append(" else NULL end as ENTIDADE, ");
                        corpoBuilder.append("case ");
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioOrg.usuCodigo then usuarioOrg.orgCodigo ");
                        }
                        if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                            corpoBuilder.append(" when usuario.usuCodigo = usuarioCsa.usuCodigo then usuarioCsa.csaCodigo ");
                        }
                        corpoBuilder.append("else NULL end as CODIGO_ENTIDADE, " );
                        corpoBuilder.append("case when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then 99 else 1 end as ORDEM ");
            }
            corpoBuilder.append("from Usuario usuario ");
            corpoBuilder.append("left outer join usuario.usuarioOrgSet usuarioOrg ");
            corpoBuilder.append("left outer join usuarioOrg.orgao orgao ");
            if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
            corpoBuilder.append("left outer join usuario.usuarioCsaSet usuarioCsa ");
            corpoBuilder.append("left outer join usuarioCsa.consignataria consignataria ");
            }
            corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
            corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");

            corpoBuilder.append(" where 1=1 ");
            StringBuilder whereBuilder = null;
            if (!TextHelper.isNull(entCodigo)) {
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG)) {
                    whereBuilder = new StringBuilder(" and ((usuarioOrg.orgCodigo ").append(criaClausulaNomeada("entCodigo", entCodigo));
                }
                if (responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA)) {
                    if (whereBuilder == null) {
                        whereBuilder = new StringBuilder("and (");
                        whereBuilder.append("usuario.usuCodigo = usuarioCsa.usuCodigo ");
                    } else {
                        whereBuilder.append(" or ");
                        whereBuilder.append("usuario.usuCodigo = usuarioCsa.usuCodigo ");
                    }
                }

            }
            whereBuilder.append("))");
            corpoBuilder.append(whereBuilder.toString());
            query = setWhereClause(session, corpoBuilder);

            if (!TextHelper.isNull(entCodigo)) {
                defineValorClausulaNomeada("entCodigo", entCodigo, query);
            }

        } else if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) {
            if (count) {
                corpo = "select count(distinct usuario.usuCodigo) as total ";
            } else {
                corpo = "select distinct usuario.usuCodigo, case "
                      + "when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '(*)'), ''), usuario.usuLogin) "
                      + "else usuario.usuLogin end AS USU_LOGIN, "
                      + "usuario.usuNome, usuario.statusLogin.stuCodigo, "
                      + "usuario.usuTipoBloq, perfil.perCodigo, perfil.perDescricao,"
                      + " 'SUP' as TIPO, 'SUPORTE' as ENTIDADE, '1' as CODIGO_ENTIDADE, "
                      + "case when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then 99 else 1 end as ORDEM ";
            }

            StringBuilder corpoBuilder = new StringBuilder(corpo);
            corpoBuilder.append("from Usuario usuario ");
            corpoBuilder.append("inner join usuario.usuarioSupSet usuarioSup ");
            corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
            corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");
            corpoBuilder.append(" where 1=1 ");
            query = setWhereClause(session, corpoBuilder);
        }

        return query;
    }

    private Query<Object[]> setWhereClause(Session session, StringBuilder corpoBuilder) {
        Query<Object[]> query;
        if (!TextHelper.isNull(usuLogin)) {
            corpoBuilder.append(" and (").append(criaClausulaNomeada("usuario.usuLogin", "usuLogin", usuLogin));
            corpoBuilder.append(" or (usuario.statusLogin.stuCodigo = '").append(CodedValues.STU_EXCLUIDO).append("'");
            corpoBuilder.append(" and usuario.usuTipoBloq ").append(criaClausulaNomeada("usuLogin", usuLogin)).append(")) ");
        }

        if (!TextHelper.isNull(usuNome)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("usuario.usuNome", "usuNome", usuNome));
        }

        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" and usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }
        
        if (!TextHelper.isNull(usuCpf)) {
            corpoBuilder.append(" and usuario.usuCpf").append(criaClausulaNomeada("usuCpf", usuCpf));
        }

        if (!TextHelper.isNull(cseNome)) {
            corpoBuilder.append(" and consignante.cseNome " ).append(criaClausulaNomeada("cseNome", cseNome));
        }

        if (!TextHelper.isNull(orgNome)) {
            corpoBuilder.append(" and orgao.orgNome " ).append(criaClausulaNomeada("orgNome", orgNome));
        }

        if (!TextHelper.isNull(stuCodigo)) {
            corpoBuilder.append(" and usuario.statusLogin.stuCodigo " ).append(criaClausulaNomeada("stuCodigo", stuCodigo));
        }

        if (!TextHelper.isNull(csaNome) && !TextHelper.isNull(csaNomeAbrev)) {
            corpoBuilder.append(" and (consignataria.csaNome " ).append(criaClausulaNomeada("csaNome", csaNome));
            corpoBuilder.append(" or consignataria.csaNomeAbrev " ).append(criaClausulaNomeada("csaNomeAbrev", csaNomeAbrev)).append(")");
        }

        if (!TextHelper.isNull(corNome)) {
            corpoBuilder.append(" and correspondente.corNome " ).append(criaClausulaNomeada("corNome", corNome));
        }

        if (!TextHelper.isNull(usuCseUsuCodigo)) {
            corpoBuilder.append(" and usuarioCse.usuCodigo IS NOT NULL");
        }

        if (!TextHelper.isNull(usuOrgUsuCodigo)) {
            corpoBuilder.append(" and usuarioOrg.usuCodigo IS NOT NULL");
        }

        if (!TextHelper.isNull(usuCsaUsuCodigo)) {
            corpoBuilder.append(" and usuarioCsa.usuCodigo IS NOT NULL");
        }

        if (!TextHelper.isNull(usuCorUsuCodigo)) {
            corpoBuilder.append(" and usuarioCor.usuCodigo IS NOT NULL");
        }

        if (ocultaUsuVisivel) {
            corpoBuilder.append(" and COALESCE(usuario.usuVisivel, 'S') <> 'N'");
            corpoBuilder.append(" and COALESCE(perfil.perVisivel, 'S') <> 'N'");
        }

        List<Object> status = new ArrayList<Object>();
        status.add(CodedValues.STU_ATIVO);
        status.add(CodedValues.STU_BLOQUEADO);
        status.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE);
        status.add(CodedValues.STU_BLOQUEADO_POR_CSE);
        status.add(CodedValues.STU_BLOQUEADO_AUSENCIA_TEMPORARIA);
        status.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
        status.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_FIM_VIGENCIA);

        boolean exibeUsuExcluidos = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_USUARIOS_EXCLUIDOS, AcessoSistema.getAcessoUsuarioSistema());
        if (exibeUsuExcluidos) {
            status.add(CodedValues.STU_EXCLUIDO);
        }
        corpoBuilder.append(" AND usuario.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));

        if (!count) {
            corpoBuilder.append(" order by ");
            corpoBuilder.append("case when usuario.statusLogin.stuCodigo = '").append(CodedValues.STU_EXCLUIDO);
            corpoBuilder.append("' then 99 else 1 end ASC, usuario.usuNome");
        }

        query = instanciarQuery(session, corpoBuilder.toString());

        // Seta os parâmetros na query
        if (!TextHelper.isNull(usuLogin)) {
            defineValorClausulaNomeada("usuLogin", usuLogin, query);
        }

        if (!TextHelper.isNull(usuNome)) {
            defineValorClausulaNomeada("usuNome", usuNome, query);
        }
        
        if (!TextHelper.isNull(usuCpf)) {
            defineValorClausulaNomeada("usuCpf", usuCpf, query);
        }

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(cseNome)) {
            defineValorClausulaNomeada("cseNome", cseNome, query);
        }

        if (!TextHelper.isNull(orgNome)) {
            defineValorClausulaNomeada("orgNome", orgNome, query);
        }

        if (!TextHelper.isNull(csaNome)) {
            defineValorClausulaNomeada("csaNome", csaNome, query);
        }

        if (!TextHelper.isNull(csaNomeAbrev)) {
            defineValorClausulaNomeada("csaNomeAbrev", csaNomeAbrev, query);
        }

        if (!TextHelper.isNull(corNome)) {
            defineValorClausulaNomeada("corNome", corNome, query);
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
                "TIPO",
                "ENTIDADE",
                "CODIGO_ENTIDADE",
                "ORDEM"
        };
    }

}
