package com.zetra.econsig.test;

import com.zetra.econsig.delegate.ImportaHistoricoDelegate;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.folha.ProcessaRetorno;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: GeradorBaseTeste</p>
 * <p>Description: Classe para executar rotina para geração de base aleatória de teste</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GeradorBaseTeste implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GeradorBaseTeste.class);
    private static final String NOME_CLASSE = ProcessaRetorno.class.getName();

    @Override
    public int executar(String args[]) {
        try {
            if (args.length < 4) {
                LOG.error("USE: java " + NOME_CLASSE + " QTD_SERVIDORES QTD_CONTRATOS MATRICULA_INICIAL CRIA_PARCELAS(N;S) NATUREZA(1=Empréstimo;2=Mensalidade;...)[Opcional]");
            } else {
                AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                int qtdRse = Integer.parseInt(args[0]);
                int qtdAde = Integer.parseInt(args[1]);
                int matriculaInicial = Integer.parseInt(args[2]);
                boolean criaParcelas = args[3].equalsIgnoreCase("S");
                String nseCodigo = (args.length >= 5 ? args[4] : null);

                LOG.info("INÍCIO GERAÇÃO: " + DateHelper.getSystemDatetime());

                ImportaHistoricoDelegate delegate = new ImportaHistoricoDelegate();
                delegate.gerarHistoricoTeste(qtdRse, qtdAde, matriculaInicial, criaParcelas, nseCodigo, responsavel);

                LOG.info("FIM GERAÇÃO: " + DateHelper.getSystemDatetime());
            }
        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
        return 0;
    }
}
