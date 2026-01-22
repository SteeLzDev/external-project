package com.zetra.econsig.helper.usuario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MontaPermissoesHelper</p>
 * <p>Description: Monta visualização de permissões editadas pelo usuário.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MontaPermissoesHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MontaPermissoesHelper.class);

    public static String montaPermissoes(List<String> selecionados, String tipoEntidade, String codigoEntidade, boolean disabled, AcessoSistema responsavel) {
        List<TransferObject> funcoes = null;
        String tipo = tipoEntidade;
        StringBuilder corpo = new StringBuilder();

        try {
            UsuarioDelegate usuDelegate = new UsuarioDelegate();

            funcoes = usuDelegate.lstFuncoesPermitidasPerfil(tipo, codigoEntidade, responsavel);

            String funcao = "", fun_codigo = "";
            String grf_codigo = "";
            String grf_descricao = "";

            List<TransferObject> funcoes_grf = new ArrayList<>();
            List<String> fun_codigos = new ArrayList<>();

            List<Integer> inicio_grupo = new ArrayList<>();
            int fim_grupo = -1;

            TransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.FUN_GRF_CODIGO, "");
            funcoes.add(cto);

            Iterator<TransferObject> it2 = funcoes.iterator();
            TransferObject customs;

            corpo.append("<br><br><br>");
            corpo.append("<table width=\"100%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">");
            corpo.append("<tr>");
            corpo.append("<tr valign=\"top\">");
            corpo.append("<td colspan=\"4\">");
            corpo.append("<table WIDTH=\"100%\" BORDER=\"0\" align=\"center\" CELLPADDING=\"0\" CELLSPACING=\"1\" CLASS=\"TabelaEntradaDeDados\">");
            corpo.append("<tr valign=\"baseline\">");
            corpo.append("<td colspan=\"2\" align=\"right\" nowrap CLASS=\"TituloColuna\"><DIV ALIGN=\"LEFT\">").append(ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.funcoes.disponiveis", responsavel)).append(" </DIV></td>");
            corpo.append("</tr>");
            corpo.append("<tr valign=\"baseline\">");
            corpo.append("<td colspan=\"2\" align=\"right\" nowrap CLASS=\"CEDtopo\">");
            corpo.append("<table width=\"100%\" CELLPADDING=\"0\" CELLSPACING=\"0\">");

            while (it2.hasNext()) {
                customs = it2.next();

                if (!customs.getAttribute(Columns.FUN_GRF_CODIGO).toString().equals(grf_codigo)) {
                    if (!grf_codigo.equals("")) {
                        if (TextHelper.isNull(grf_descricao)) {
                            CustomTransferObject custom = (CustomTransferObject)funcoes_grf.get(0);
                            grf_descricao = (String) custom.getAttribute(Columns.GRF_DESCRICAO);
                        }

                        corpo.append("<tr>");
                        corpo.append("<td colspan=\"2\" align=\"left\">");
                        corpo.append("<input name=\"checkGrupo\" type=\"checkbox\"");
                        corpo.append((disabled) ? " disabled " : " ").append("><b>").append(grf_descricao).append("</b>");
                        corpo.append("</td>");
                        corpo.append("</tr>");

                        int meio = Math.round(funcoes_grf.size() / 2f);
                        for (int i=0; i<meio; i++) {
                            CustomTransferObject custom = (CustomTransferObject)funcoes_grf.get(i);

                            corpo.append("<tr>");

                            funcao = custom.getAttribute(Columns.FUN_DESCRICAO).toString();
                            fun_codigo = custom.getAttribute(Columns.FUN_CODIGO).toString();

                            corpo.append("<td><input name=\"funcao\" type=\"checkbox\" value=\"").append(fun_codigo).append("\"").append(selecionados == null || selecionados.contains(fun_codigo) ? " CHECKED " : " ");
                            corpo.append((disabled) ? "disabled" : "").append(">").append(funcao).append("</td>");

                            if (i+meio < funcoes_grf.size()) {
                                custom = (CustomTransferObject)funcoes_grf.get(i+meio);
                                funcao = custom.getAttribute(Columns.FUN_DESCRICAO).toString();
                                fun_codigo = custom.getAttribute(Columns.FUN_CODIGO).toString();

                                corpo.append("<td><input name=\"funcao\" type=\"checkbox\" value=\"").append(fun_codigo).append("\"").append(selecionados == null || selecionados.contains(fun_codigo) ? " CHECKED " : " ");
                                corpo.append((disabled) ? "disabled" : "").append(">").append(funcao).append("</td>");

                            }

                            corpo.append("</tr>");

                        }

                        corpo.append("<tr>");
                        corpo.append("<td colspan=\"2\"><hr></td>");
                        corpo.append("</tr>");

                    }
                    funcoes_grf.clear();

                    grf_codigo = (String) customs.getAttribute(Columns.FUN_GRF_CODIGO);
                    grf_descricao = (String) customs.getAttribute(Columns.GRF_DESCRICAO);

                    if (!grf_codigo.equals("")) {
                        inicio_grupo.add(Integer.valueOf(fim_grupo + 1));
                    } else {
                        break;
                    }
                }

                fun_codigos.add((String) customs.getAttribute(Columns.FUN_CODIGO));
                fim_grupo ++;
                funcoes_grf.add(customs);
            }
            inicio_grupo.add(Integer.valueOf(fim_grupo + 1));

            corpo.append("</table>");
            corpo.append("</td>");
            corpo.append("</tr>");
            corpo.append("</table>");
            corpo.append("</td>");
            corpo.append("</tr>");
            corpo.append("</table>");
            corpo.append("</center>");
            corpo.append("<center>");
            corpo.append("<table>");
        } catch (Exception ex) {
            LOG.warn("Erro ao montar visualização de permissões", ex);
        }

        return corpo.toString();
    }
}
