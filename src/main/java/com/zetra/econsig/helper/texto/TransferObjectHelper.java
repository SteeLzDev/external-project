package com.zetra.econsig.helper.texto;

import java.util.ArrayList;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: TransferObjectHelper</p>
 * <p>Description: Ajuda a realizar operações de filtro em TransferObjects.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>

 */
public class TransferObjectHelper {

    public static CustomTransferObject mascararUsuarioHistorico(CustomTransferObject entrada, String campo, AcessoSistema responsavel) {
        boolean exibeLogin = ParamSist.paramEquals(CodedValues.TPC_EXIBE_LOGIN_OUTRAS_ENTIDADES, CodedValues.TPC_SIM, responsavel);
        boolean exibeIp = ParamSist.paramEquals(CodedValues.TPC_EXIBE_IP_OUTRAS_ENTIDADES, CodedValues.TPC_SIM, responsavel);
        CustomTransferObject saida = entrada;
        ArrayList<String> camposIp = new ArrayList<String>();
        String mascarar = TextHelper.isNull(campo) ? Columns.USU_LOGIN : campo;
        boolean mascarado = false;

        camposIp.add(Columns.CMN_IP_ACESSO);
        camposIp.add(Columns.OCC_IP_ACESSO);
        camposIp.add(Columns.OCA_IP_ACESSO);
        camposIp.add(Columns.OUS_IP_ACESSO);
        camposIp.add(Columns.ODC_IP_ACESSO);
        camposIp.add("ip_acesso");
        camposIp.add(Columns.ORS_IP_ACESSO);
        camposIp.add(Columns.OCS_IP_ACESSO);
        camposIp.add(Columns.CMN_IP_ACESSO);
        camposIp.add(Columns.ODC_IP_ACESSO);
        camposIp.add(Columns.OPE_IP_ACESSO);
        camposIp.add(Columns.RRS_IP_ACESSO);
        camposIp.add(Columns.AAD_IP_ACESSO);

        if ((!exibeLogin || !exibeIp) && !responsavel.isCseSup()) {

            //mascara login de acordo com o papel do responsavel
            if (responsavel.isSer() && ((!TextHelper.isNull(entrada.getAttribute(Columns.USE_SER_CODIGO)) && !entrada.getAttribute(Columns.USE_SER_CODIGO).equals(responsavel.getSerCodigo())) || (TextHelper.isNull(entrada.getAttribute(Columns.USE_SER_CODIGO))))) {

                if (!TextHelper.isNull(entrada.getAttribute(Columns.UCE_CSE_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.USP_CSE_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel));
                    }
                    mascarado = true;
                }
                else if (!TextHelper.isNull(entrada.getAttribute(Columns.UCO_COR_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.UOR_ORG_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.UCA_CSA_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel));
                    }
                    mascarado = true;
                }

            } else if (responsavel.isCor() && ((!TextHelper.isNull(entrada.getAttribute(Columns.UCO_COR_CODIGO)) && !entrada.getAttribute(Columns.UCO_COR_CODIGO).equals(responsavel.getCorCodigo())) || (TextHelper.isNull(entrada.getAttribute(Columns.UCO_COR_CODIGO))))) {

                if (!TextHelper.isNull(entrada.getAttribute(Columns.UCE_CSE_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.USP_CSE_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.USE_SER_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.UOR_ORG_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.UCA_CSA_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.UCO_COR_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel));
                    }
                    mascarado = true;
                }
            } else if (responsavel.isOrg() && ((TextHelper.isNull(entrada.getAttribute(Columns.UOR_ORG_CODIGO))) || (!TextHelper.isNull(entrada.getAttribute(Columns.UOR_ORG_CODIGO)) && !entrada.getAttribute(Columns.UOR_ORG_CODIGO).equals(responsavel.getOrgCodigo())))) {

                if (!TextHelper.isNull(entrada.getAttribute(Columns.UCE_CSE_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.USP_CSE_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.UCO_COR_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.UCA_CSA_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel));
                    }
                    mascarado = true;
                }
            } else if (responsavel.isCsa() && ((TextHelper.isNull(entrada.getAttribute(Columns.UCA_CSA_CODIGO))) || (!TextHelper.isNull(entrada.getAttribute(Columns.UCA_CSA_CODIGO)) && !entrada.getAttribute(Columns.UCA_CSA_CODIGO).equals(responsavel.getCsaCodigo())))) {

                if (!TextHelper.isNull(entrada.getAttribute(Columns.UCE_CSE_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.USP_CSE_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.USE_SER_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.UOR_ORG_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel));
                    }
                    mascarado = true;
                } else if (!TextHelper.isNull(entrada.getAttribute(Columns.UCA_CSA_CODIGO))) {
                    if (!exibeLogin) {
                        saida.setAttribute(mascarar, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel));
                    }
                    mascarado = true;
                }
            }

            if (mascarado && !exibeIp) {
                for (String ip : camposIp) {
                    if (!TextHelper.isNull(saida.getAttribute(ip))) {
                        saida.setAttribute(ip, "-");
                    }
                }
            }

            return saida;

        } else {
            return entrada;
        }
    }

    public static String mascaraIpRelatorio(String campo, String cseCodigo, String csaCodigo, String orgCodigo, String corCodigo, String serCodigo, String supCseCodigo, AcessoSistema responsavel) {
        boolean exibeIp = ParamSist.paramEquals(CodedValues.TPC_EXIBE_IP_OUTRAS_ENTIDADES, CodedValues.TPC_SIM, responsavel);
        if (!exibeIp && !responsavel.isCseSup()) {
            String ip = mascararCampoRelatorio(campo, cseCodigo, csaCodigo, orgCodigo, corCodigo, serCodigo, supCseCodigo, responsavel);
            if (campo.equals(ip)) {
                return campo;
            } else {
                return "-";
            }
        }
        return campo;

    }

    public static String mascararUsuarioRelatorio(String campo, String cseCodigo, String csaCodigo, String orgCodigo, String corCodigo, String serCodigo, String supCseCodigo, AcessoSistema responsavel) {
        boolean exibeLogin = ParamSist.paramEquals(CodedValues.TPC_EXIBE_LOGIN_OUTRAS_ENTIDADES, CodedValues.TPC_SIM, responsavel);
        if (!exibeLogin && !responsavel.isCseSup()) {
            return mascararCampoRelatorio(campo, cseCodigo, csaCodigo, orgCodigo, corCodigo, serCodigo, supCseCodigo, responsavel);
        }
        return campo;
    }

    public static String mascararCampoRelatorio(String campo, String cseCodigo, String csaCodigo, String orgCodigo, String corCodigo, String serCodigo, String supCseCodigo, AcessoSistema responsavel) {
        String saida = campo;

        if (responsavel.isSer() && ((!TextHelper.isNull(serCodigo) && !serCodigo.equals(responsavel.getSerCodigo())) || (TextHelper.isNull(serCodigo)))) {

            if (!TextHelper.isNull(cseCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel);
            } else if (!TextHelper.isNull(supCseCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel);
            } else if (!TextHelper.isNull(corCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel);
            } else if (!TextHelper.isNull(orgCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel);
            } else if (!TextHelper.isNull(csaCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel);
            }

        } else if (responsavel.isCor() && ((!TextHelper.isNull(corCodigo) && !corCodigo.equals(responsavel.getCorCodigo())) || (TextHelper.isNull(corCodigo)))) {

            if (!TextHelper.isNull(cseCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel);
            } else if (!TextHelper.isNull(supCseCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel);
            } else if (!TextHelper.isNull(serCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel);
            } else if (!TextHelper.isNull(orgCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel);
            } else if (!TextHelper.isNull(csaCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel);
            } else if (!TextHelper.isNull(corCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel);
            }
        } else if (responsavel.isOrg() && ((TextHelper.isNull(orgCodigo)) || (!TextHelper.isNull(orgCodigo) && !orgCodigo.equals(responsavel.getOrgCodigo())))) {

            if (!TextHelper.isNull(cseCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel);
            } else if (!TextHelper.isNull(supCseCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel);
            } else if (!TextHelper.isNull(serCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel);
            } else if (!TextHelper.isNull(corCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel);
            } else if (!TextHelper.isNull(csaCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel);
            }
        } else if (responsavel.isCsa() && ((TextHelper.isNull(csaCodigo)) || (!TextHelper.isNull(csaCodigo) && !csaCodigo.equals(responsavel.getCsaCodigo())))) {

            if (!TextHelper.isNull(cseCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel);
            } else if (!TextHelper.isNull(supCseCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel);
            } else if (!TextHelper.isNull(serCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel);
            } else if (!TextHelper.isNull(orgCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel);
            } else if (!TextHelper.isNull(csaCodigo)) {
                saida = ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel);
            }
        }

        return saida;
    }

}
