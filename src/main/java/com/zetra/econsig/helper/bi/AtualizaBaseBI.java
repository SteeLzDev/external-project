package com.zetra.econsig.helper.bi;

import com.zetra.econsig.exception.ConsigBIControllerException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.bi.ConsigBIController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: AtualizaBaseBI</p>
 * <p>Description: Classe para execução do processo de atualização da base de BI</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AtualizaBaseBI implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AtualizaBaseBI.class);
    private static final String NOME_CLASSE = AtualizaBaseBI.class.getName();

    @Override
    public int executar(String[] args) {
        final String msgErro = "USE para atualização da base de BI: \n"
                       + "==> java " + NOME_CLASSE + " TIPO(0=Tudo; 1=Dimensões; 2=Auxiliares; 3=Fato Contrato; 4=Fato Parcela; 5=Fato Margem) "
                               + "DADOS(0=Não popula tabela de dados; 1=Popula tabela de dados)";

        if (args.length != 2) {
            LOG.error(msgErro);
            return -1;
        } else {
            final int tipo = Integer.parseInt(args[0]);
            final boolean populaDados = Integer.valueOf(args[1]).equals(1);
            if ((tipo < 0) || (tipo > 5)) {
                LOG.error("Tipo Incorreto.");
                LOG.error(msgErro);
                return -1;
            } else {
                try {
                    final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                    final ConsigBIController consigBIController = ApplicationContextProvider.getApplicationContext().getBean(ConsigBIController.class);
                    consigBIController.atualizarBaseBI(tipo, populaDados, responsavel);
                    return 0;
                } catch (final ConsigBIControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    return -1;
                }
            }
        }
    }
}
