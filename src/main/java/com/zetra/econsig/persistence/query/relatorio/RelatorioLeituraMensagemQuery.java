package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioLeituraMensagemQuery</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioLeituraMensagemQuery extends ReportHQuery {

    private String dataInicial;
    private String dataFinal;
    private String menCodigo;
    private String entidade;
    private String csaCodigo;
    private String nomeUsuario;
    private String loginUsuario;
    private String cpfUsuario;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataInicial = (String) criterio.getAttribute("DATA_INI");
        dataFinal = (String) criterio.getAttribute("DATA_FIM");
        menCodigo = (String) criterio.getAttribute(Columns.MEN_CODIGO);
        entidade = (String) criterio.getAttribute("ENTIDADE");
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        nomeUsuario = (String) criterio.getAttribute(Columns.USU_NOME);
        loginUsuario = (String) criterio.getAttribute(Columns.USU_LOGIN);
        cpfUsuario = (String) criterio.getAttribute(Columns.USU_CPF);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String stuExcluido = CodedValues.STU_EXCLUIDO;

        StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT ");

        //nome da entidade
        corpo.append(" case ");
        if(!TextHelper.isNull(entidade)){
            if(entidade.equals(AcessoSistema.ENTIDADE_CSE)){
                corpo.append("       when usu.usuCodigo = usuarioCse.usuCodigo then consignante.cseNome");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_CSA)){
                corpo.append("       when usu.usuCodigo = usuarioCsa.usuCodigo then consignataria.csaNome");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_ORG)){
                corpo.append("       when usu.usuCodigo = usuarioOrg.usuCodigo then orgao.orgNome");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_COR)){
                corpo.append("       when usu.usuCodigo = usuarioCor.usuCodigo then concat(concat(CSA_COR.csaNome,' - '), correspondente.corNome)");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_SER)){
                corpo.append("       when usu.usuCodigo = usuarioSer.usuCodigo then servidor.serNome ");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_SUP)){
                corpo.append("       when usu.usuCodigo = usuarioSup.usuCodigo then '").append(ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", null).toUpperCase()).append("'");
            }
        }

        corpo.append(" else NULL end as ENTIDADE, ");
        corpo.append(" usu.usuNome AS NOME_USUARIO, ");
        corpo.append(" case ");
        corpo.append(" when usu.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usu.usuTipoBloq, '(*)'), ''), usu.usuLogin) ");
        corpo.append(" else usu.usuLogin end as LOGIN_USUARIO, ");
        corpo.append(" usu.usuCpf AS CPF_USUARIO, ");
        corpo.append(" men.menTitulo AS TITULO_MENSAGEM, ");
        corpo.append(" men.menData AS DATA_MENSAGEM, ");
        corpo.append(" lmu.lmuData AS DATA_LEITURA ");

        corpo.append(" FROM LeituraMensagemUsuario lmu ");
        corpo.append(" INNER JOIN lmu.usuario usu ");
        corpo.append(" INNER JOIN lmu.mensagem men ");


        if(!TextHelper.isNull(entidade)){
            if(entidade.equals(AcessoSistema.ENTIDADE_CSE)){
                corpo.append("INNER JOIN usu.usuarioCseSet usuarioCse ");
                corpo.append("INNER JOIN usuarioCse.consignante consignante ");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_CSA)){
                corpo.append("INNER JOIN usu.usuarioCsaSet usuarioCsa ");
                corpo.append("INNER JOIN usuarioCsa.consignataria consignataria ");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_ORG)){
                corpo.append("INNER JOIN usu.usuarioOrgSet usuarioOrg ");
                corpo.append("INNER JOIN usuarioOrg.orgao orgao ");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_COR)){
                corpo.append("INNER JOIN usu.usuarioCorSet usuarioCor ");
                corpo.append("INNER JOIN usuarioCor.correspondente correspondente ");
                corpo.append("INNER JOIN correspondente.consignataria CSA_COR ");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_SER)){
                corpo.append("INNER JOIN usu.usuarioSerSet usuarioSer ");
                corpo.append("INNER JOIN usuarioSer.servidor servidor ");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_SUP)){
                corpo.append("INNER JOIN usu.usuarioSupSet usuarioSup ");
            }
        }

        corpo.append(" WHERE ");

        corpo.append(" 1 = 1 ");

        if(!TextHelper.isNull(dataInicial) && !TextHelper.isNull(dataFinal)){
            corpo.append(" AND lmu.lmuData between :dataInicial and :dataFinal ");
        }

        if(!TextHelper.isNull(menCodigo)){
            corpo.append(" AND men.menCodigo ").append(criaClausulaNomeada("menCodigo", menCodigo));
        }

        if(!TextHelper.isNull(entidade)){
            if(entidade.equals(AcessoSistema.ENTIDADE_CSE)){
                corpo.append(" AND men.menExibeCse = '").append(CodedValues.TPA_SIM).append("'");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_CSA)){
                corpo.append(" AND men.menExibeCsa = '").append(CodedValues.TPA_SIM).append("'");
                if(!TextHelper.isNull(csaCodigo)){
                    corpo.append(" AND consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
                }
            } else if(entidade.equals(AcessoSistema.ENTIDADE_ORG)){
                corpo.append(" AND men.menExibeOrg = '").append(CodedValues.TPA_SIM).append("'");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_COR)){
                corpo.append(" AND men.menExibeCor = '").append(CodedValues.TPA_SIM).append("'");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_SER)){
                corpo.append(" AND men.menExibeSer = '").append(CodedValues.TPA_SIM).append("'");
            } else if(entidade.equals(AcessoSistema.ENTIDADE_SUP)){
                corpo.append(" AND men.menExibeSup = '").append(CodedValues.TPA_SIM).append("'");
            }
        }

        if(!TextHelper.isNull(nomeUsuario)){
            corpo.append(" AND ").append(criaClausulaNomeada("usu.usuNome", "nomeUsuario", CodedValues.LIKE_MULTIPLO + nomeUsuario));
        }

        if(!TextHelper.isNull(loginUsuario)){
            corpo.append(" and (usu.usuLogin ").append(criaClausulaNomeada("loginUsuario", loginUsuario));
            corpo.append(" or (usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("stuExcluido", stuExcluido));
            corpo.append(" and usu.usuTipoBloq ").append(criaClausulaNomeada("loginUsuario", loginUsuario)).append(")) ");
        }

        if(!TextHelper.isNull(cpfUsuario)){
            corpo.append(" AND usu.usuCpf ").append(criaClausulaNomeada("cpfUsuario", cpfUsuario));
        }

        corpo.append(" ORDER BY ENTIDADE, men.menCodigo, lmu.lmuData ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if(!TextHelper.isNull(dataInicial) && !TextHelper.isNull(dataFinal)){
            defineValorClausulaNomeada("dataInicial", parseDateTimeString(dataInicial), query);
            defineValorClausulaNomeada("dataFinal", parseDateTimeString(dataFinal), query);
        }

        if(!TextHelper.isNull(menCodigo)){
            defineValorClausulaNomeada("menCodigo", menCodigo, query);
        }

        if(!TextHelper.isNull(entidade) && entidade.equals("CSA") && !TextHelper.isNull(csaCodigo)){
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if(!TextHelper.isNull(nomeUsuario)){
            defineValorClausulaNomeada("nomeUsuario", CodedValues.LIKE_MULTIPLO + nomeUsuario + CodedValues.LIKE_MULTIPLO, query);
        }

        if(!TextHelper.isNull(loginUsuario)){
            defineValorClausulaNomeada("loginUsuario", loginUsuario, query);
            defineValorClausulaNomeada("stuExcluido", stuExcluido, query);
        }

        if(!TextHelper.isNull(cpfUsuario)){
            defineValorClausulaNomeada("cpfUsuario", cpfUsuario, query);
        }

        return query;
    }


    @Override
    protected String[] getFields() {
        return new String[] {
                "ENTIDADE",
                "NOME_USUARIO",
                "LOGIN_USUARIO",
                "CPF_USUARIO",
                "TITULO_MENSAGEM",
                "DATA_MENSAGEM",
                "DATA_LEITURA"
        };
    }
}