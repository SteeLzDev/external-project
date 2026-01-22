package com.zetra.econsig.helper.folha;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ImportarHistorico</p>
 * <p>Description: Classe para script de importação de histórico com opções de inclusão avançada de consignação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportarHistorico implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportarHistorico.class);
    private static final String NOME_CLASSE = ImportarHistorico.class.getName();

    public static final int TAMANHO_MSG_ERRO_DEFAULT = 100;
    public static final String COMPLEMENTO_DEFAULT = " ";

    @Override
    public int executar(String args[]) {
        try {
            if (args.length < 2 || args[0].equals("-h")) {
                printOpcoes();
                return -1;
            } else {
                String path = args[0];
                String nomeArqEntrada = args[1];

                String xmlEntrada = "imp_consignacao_entrada.xml";
                String xmlTradutor = "imp_consignacao_tradutor.xml";

                List<String> lstArgs = Arrays.asList(args);

                boolean serAtivo             = (lstArgs.contains("-sa")) ? true : false;
                boolean cnvAtivo             = (lstArgs.contains("-ca")) ? true : false;
                boolean svcAtivo             = (lstArgs.contains("-sva")) ? true : false;
                boolean serCnvAtivo          = (lstArgs.contains("-sca")) ? true : false;
                boolean csaAtivo             = (lstArgs.contains("-csa")) ? true : false;
                boolean orgAtivo             = (lstArgs.contains("-org")) ? true : false;
                boolean estAtivo             = (lstArgs.contains("-est")) ? true : false;
                boolean cseAtivo             = (lstArgs.contains("-cse")) ? true : false;
                boolean validaCET            = (lstArgs.contains("-cet")) ? false : true;
                boolean validaPrazo          = (lstArgs.contains("-p")) ? false : true;
                boolean validaDadosBancarios = (lstArgs.contains("-db")) ? false : true;
                boolean validaSenhaServidor  = (lstArgs.contains("-ss")) ? false : true;
                boolean validaDataNasc       = (lstArgs.contains("-da")) ? false : true;
                boolean validaLimiteAde      = (lstArgs.contains("-l")) ? false : true;
                boolean importaDadosAde      = (lstArgs.contains("-dad")) ? true : false;
                boolean retornaAdeNum        = (lstArgs.contains("-ade")) ? true : false;
                boolean selectPrimeiroCnv    = (lstArgs.contains("-pcd")) ? true : false;

                // registra as opções de inclusão avançada
                ReservarMargemParametros margemParam = new ReservarMargemParametros();

                margemParam.setValidaTaxaJuros(validaCET);
                margemParam.setValidaPrazo(validaPrazo);
                margemParam.setValidaDadosBancarios(validaDadosBancarios);
                margemParam.setValidaSenhaServidor(validaSenhaServidor);
                margemParam.setValidaDataNascimento(validaDataNasc);
                margemParam.setValidaLimiteAde(validaLimiteAde);

                AcessoSistema responsavel = new AcessoSistema(CodedValues.USU_CODIGO_SISTEMA);
                Map<String, EnderecoFuncaoTransferObject> permissoes = new HashMap<>();
                permissoes.put(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO, new EnderecoFuncaoTransferObject(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO, ""));
                permissoes.put(CodedValues.FUN_ALT_AVANCADA_CONSIGNACAO, new EnderecoFuncaoTransferObject(CodedValues.FUN_ALT_AVANCADA_CONSIGNACAO, ""));
                permissoes.put(CodedValues.FUN_CONF_RESERVA, new EnderecoFuncaoTransferObject(CodedValues.FUN_CONF_RESERVA, ""));
                responsavel.setPermissoes(permissoes);
                responsavel.setIpUsuario("127.0.0.1"); // Necessário para inserrir ocorrência de despesa individual
                responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SUP); // setando como entidade suporte

                for(int i = 0; i < args.length; i++) {
                    if (args[i].equals("-tmo")) {
                        try {
                            String tmo = args[++i];

                            if (!TextHelper.isNull(tmo)) {
                                TipoMotivoOperacaoDelegate tmoDelegate =  new TipoMotivoOperacaoDelegate();
                                TipoMotivoOperacaoTransferObject tmoTO = tmoDelegate.findMotivoOperacaoByCodIdent(tmo, responsavel);
                                margemParam.setTmoCodigo(tmoTO.getTmoCodigo());

                                for(int j = 0; j < args.length; j++) {
                                    if (args[j].equals("-tmoObs")) {
                                        String obs = args[++j];

                                        if (!TextHelper.isNull(obs)) {
                                            margemParam.setOcaObs(obs);
                                        }
                                    }
                                }
                                break;
                            } else {
                                printOpcoes();
                                throw new ZetraException("mensagem.informe.codigo.motivo.operacao", responsavel);
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            printOpcoes();
                            throw new ZetraException("mensagem.informe.codigo.motivo.operacao", responsavel);
                        }
                    }
                }

                LOG.debug("xmlEntrada = " + xmlEntrada);
                LOG.debug("xmlTradutor = " + xmlTradutor);
                LOG.debug("serAtivo = " + serAtivo);
                LOG.debug("cnvAtivo = " + cnvAtivo);
                LOG.debug("svcAtivo = " + svcAtivo);
                LOG.debug("serCnvAtivo = " + serCnvAtivo);
                LOG.debug("csaAtivo = " + csaAtivo);
                LOG.debug("orgAtivo = " + orgAtivo);
                LOG.debug("estAtivo = " + estAtivo);
                LOG.debug("cseAtivo = " + cseAtivo);
                LOG.debug("validaCET = " + validaCET);
                LOG.debug("validaPrazo = " + validaPrazo);
                LOG.debug("validaDadosBancarios = " + validaDadosBancarios);
                LOG.debug("validaSenhaServidor = " + validaSenhaServidor);
                LOG.debug("validaDataNasc = " + validaDataNasc);
                LOG.debug("validaLimiteAde = " + validaLimiteAde);

                LOG.info("INÍCIO IMPORTAÇÃO: " + DateHelper.getSystemDatetime());
                LOG.warn("O LOG DO PROCESSAMENTO SERÁ IMPRESSO JUNTO DO LOG DO JBOSS!");

                HistoricoHelper historicoHelper = new HistoricoHelper();
                historicoHelper.importaLoteConsignacao(path, path, CodedValues.CSE_CODIGO_SISTEMA, nomeArqEntrada, xmlEntrada, xmlTradutor, false, false,
                        serAtivo, cnvAtivo, svcAtivo, serCnvAtivo, csaAtivo, orgAtivo, estAtivo, cseAtivo, importaDadosAde, retornaAdeNum, selectPrimeiroCnv, margemParam, responsavel);

                LOG.info("FIM IMPORTAÇÃO: " + DateHelper.getSystemDatetime());
                return 0;
            }
        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    private void printOpcoes() {
        LOG.error("USE: java " + NOME_CLASSE + " PATH_DE_ENTRADA ARQUIVO_ENTRADA [OPÇÕES]\n" +
                "\n\n" +
                "OPÇÕES: \n" +
                "\n" +
                "-sa  Apenas servidores ativos\n" +
                "-ca  Apenas Convênios ativos\n" +
                "-sva Apenas Serviços ativos\n" +
                "-sca Apenas Servidores ativos para o convênio\n" +
                "-csa Apenas Consignatárias ativas\n" +
                "-org Apenas Órgãos ativos\n" +
                "-est Apenas Estabelecimentos ativos\n" +
                "-cse Apenas Consignantes ativos\n" +
                "-cet Não validar taxa de juros\n" +
                "-p   Não validar prazo\n" +
                "-db  Não validar dados bancários\n" +
                "-ss  Não validar senha servidor\n" +
                "-da  Não validar data nascimento\n" +
                "-l   Não validar limite contrato\n" +
                "-dad Importar dados autorização desconto\n" +
                "-ade Retornar ADE Número na crítica\n" +
                "-pcd seleciona primeiro código de verba disponível\n" +
                "[-tmo IDENTIFICADOR DO MOTIVO DE OPERAÇÃO]" +
                "[-tmoObs OBSERVAÇÃO DO MOTIVO DE OPERAÇÃO]" +
                "\n\n");
    }
}
