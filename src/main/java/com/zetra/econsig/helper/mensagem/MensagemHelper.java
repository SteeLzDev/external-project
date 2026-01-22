package com.zetra.econsig.helper.mensagem;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.dto.entidade.MensagemTO;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: MensagemHelper</p>
 * <p>Description: Classe utilitária para criação de mensagens via script</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MensagemHelper implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MensagemHelper.class);
    private static final String NOME_CLASSE = MensagemHelper.class.getName();

    @Override
    public int executar(String[] args) {
        final String textoAjuda = "USE: java " + NOME_CLASSE + " M [TITULO] [TEXTO] [EXIBE_CSE](0=Não; 1=Sim) [EXIBE_CSA](0=Não; 1=Sim) [EXIBE_ORG](0=Não; 1=Sim) [EXIBE_COR](0=Não; 1=Sim) [EXIBE_SER](0=Não; 1=Sim) [ENVIA_EMAIL_CSA](0=Não; 1=Sim) [EXIBE_SUP](0=Não; 1=Sim) [ENVIA_EMAIL_CSE](0=Não; 1=Sim) [ENVIA_EMAIL_ORG](0=Não; 1=Sim) [ENVIA_EMAIL_COR](0=Não; 1=Sim) [ENVIA_EMAIL_SER](0=Não; 1=Sim) [ENVIA_EMAIL_SUP](0=Não; 1=Sim)";
        try {
            if (args.length < 3) {
                LOG.error(textoAjuda);
                return -1;
            } else if (args[0].equals("M")) {
                String menTitulo    = args[1];
                String menTexto     = args[2];
                boolean menExibeCse = (args.length >= 4 && args[3] != null && args[3].equals("1"));
                boolean menExibeCsa = (args.length >= 5 && args[4] != null && args[4].equals("1"));
                boolean menExibeOrg = (args.length >= 6 && args[5] != null && args[5].equals("1"));
                boolean menExibeCor = (args.length >= 7 && args[6] != null && args[6].equals("1"));
                boolean menExibeSer = (args.length >= 8 && args[7] != null && args[7].equals("1"));
                boolean enviaEmailCsa  = (args.length >= 9 && args[8] != null && args[8].equals("1"));
                boolean menExibeSup = (args.length >= 10 && args[9] != null && args[9].equals("1")) ? true : menExibeCse;
                boolean enviaEmailCse  = (args.length >= 11 && args[10] != null && args[10].equals("1"));
                boolean enviaEmailOrg  = (args.length >= 12 && args[11] != null && args[11].equals("1"));
                boolean enviaEmailCor  = (args.length >= 13 && args[12] != null && args[12].equals("1"));
                boolean enviaEmailSer  = (args.length >= 14 && args[13] != null && args[13].equals("1"));
                boolean enviaEmailSup  = (args.length >= 15 && args[14] != null && args[14].equals("1"));

                AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_CSE);

                MensagemTO menTO = new MensagemTO();
                menTO.setUsuCodigo(responsavel.getUsuCodigo());
                menTO.setMenData(DateHelper.getSystemDatetime());
                menTO.setMenExigeLeitura("S");

                menTO.setMenExibeCse(menExibeCse ? "S" : "N");
                menTO.setMenExibeCsa(menExibeCsa ? "S" : "N");
                menTO.setMenExibeOrg(menExibeOrg ? "S" : "N");
                menTO.setMenExibeCor(menExibeCor ? "S" : "N");
                menTO.setMenExibeSer(menExibeSer ? "S" : "N");
                menTO.setMenExibeSup(menExibeSup ? "S" : "N");
                // valor padrão do banco
                menTO.setMenPermiteLerDepois("S");
                menTO.setMenNotificarCseLeitura("N");
                menTO.setMenBloqCsaSemLeitura("N");
                menTO.setMenPushNotificationSer("N");

                if (!TextHelper.isNull(menTitulo)) {
                    menTO.setMenTitulo(menTitulo);
                } else {
                    LOG.error("O título da mensagem deve ser informado.");
                    return -1;
                }
                if (!TextHelper.isNull(menTexto)) {
                    menTO.setMenTexto(menTexto);
                } else {
                    LOG.error("O texto da mensagem deve ser informado.");
                    return -1;
                }

                LOG.info("INÍCIO - INCLUSÃO DE MENSAGEM: " + DateHelper.getSystemDatetime());

                // Cria a mensagem
                MensagemController mensagemController = ApplicationContextProvider.getApplicationContext().getBean(MensagemController.class);
                String menCodigo = mensagemController.createMensagem(menTO, responsavel);

                LOG.info("INÍCIO - ENVIANDO MENSAGEM POR EMAIL: " + DateHelper.getSystemDatetime());

                // Envia por email apenas se a mensagem for exibida para a consignatária
                menTO.setMenCodigo(menCodigo);

                try {
                    List<String> papeis = new ArrayList<>();
                    if (enviaEmailCse) {
                        papeis.add(AcessoSistema.ENTIDADE_CSE);
                    }
                    if (enviaEmailOrg) {
                        papeis.add(AcessoSistema.ENTIDADE_ORG);
                    }
                    if (enviaEmailCsa) {
                        papeis.add(AcessoSistema.ENTIDADE_CSA);
                    }
                    if (enviaEmailCor) {
                        papeis.add(AcessoSistema.ENTIDADE_COR);
                    }
                    if (enviaEmailSer) {
                        papeis.add(AcessoSistema.ENTIDADE_SER);
                    }
                    if (enviaEmailSup) {
                        papeis.add(AcessoSistema.ENTIDADE_SUP);
                    }

                    mensagemController.enviaMensagemEmail(menTO, papeis, false, responsavel);
                    LOG.info("FIM - ENVIADO MENSAGEM POR EMAIL: " + DateHelper.getSystemDatetime());
                } catch (MensagemControllerException e) {
                    LOG.info("FIM - NÃO FOI POSSÍVEL ENVIAR MENSAGEM POR EMAIL: " + DateHelper.getSystemDatetime());
                }
                LOG.info("FIM - INCLUSÃO DE MENSAGEM: " + DateHelper.getSystemDatetime());
                return 0;

            } else {
                LOG.error(textoAjuda);
                return -1;
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }
}
