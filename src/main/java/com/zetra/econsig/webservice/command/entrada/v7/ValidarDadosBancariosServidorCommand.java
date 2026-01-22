package com.zetra.econsig.webservice.command.entrada.v7;

import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;

import java.util.Map;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;

/**
 * <p>Title: ValidarDadosBancariosServidorCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig para validar dados bancários do servidor</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarDadosBancariosServidorCommand extends RequisicaoExternaCommand {

    public ValidarDadosBancariosServidorCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        // Validar se os dados bancários foram informados
        if (TextHelper.isNull(parametros.get(RSE_BANCO)) || TextHelper.isNull(parametros.get(RSE_AGENCIA)) || TextHelper.isNull(parametros.get(RSE_CONTA))) {
            throw new ZetraException("mensagem.informacaoBancariaObrigatoria", responsavel);
        }
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = (String) parametros.get(RSE_CODIGO);

        // Buscar os dados do servidor
        ServidorDelegate serDelegate = new ServidorDelegate();
        RegistroServidorTO rseResultTo = serDelegate.findRegistroServidor(rseCodigo, responsavel);
        if (rseResultTo != null) {
            RegistroServidorTO rse = new RegistroServidorTO();
            rse.setRseAgenciaSal(rseResultTo.getRseAgenciaSal());
            rse.setRseBancoSal(rseResultTo.getRseBancoSal());
            rse.setRseContaSal(rseResultTo.getRseContaSal());
            rse.setRseBancoSalAlternativo(rseResultTo.getRseBancoSalAlternativo());
            rse.setRseAgenciaSalAlternativa(rseResultTo.getRseAgenciaSalAlternativa());
            rse.setRseContaSalAlternativa(rseResultTo.getRseContaSalAlternativa());

            // Recupera informações bancárias informadas pelo usuário
            String numBanco = parametros.get(RSE_BANCO).toString();
            String numAgencia = parametros.get(RSE_AGENCIA).toString();
            String numConta = parametros.get(RSE_CONTA).toString();

            try {
                // Validar as informações bancárias com as informadas
                validarDadosBancariosServidor(true, true, numBanco, numAgencia, numConta, rse);

                // Se não deu erro, é porque os dados estão corretos: define mensagem e código de retorno
                parametros.put(COD_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaCorreta.xml", responsavel));
                parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaCorreta", responsavel));

            } catch (ZetraException ex) {
                // Verifica a causa do erro, e caso seja dados incorretos, deve registrar no log (tb_log)
                // a consulta que os dados bancários forem incorretos (tipo de log = Aviso).
                if (ex.getMessageKey() != null && ex.getMessageKey().equals("mensagem.informacaoBancariaIncorreta")) {
                    LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.FIND, Log.LOG_AVISO);
                    log.setRegistroServidor(rseCodigo);
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.validar.dados.bancarios", responsavel));
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.servidor.codigo.banco", responsavel) + ": " + numBanco);
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.servidor.codigo.agencia", responsavel) + ": " + numAgencia);
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.servidor.codigo.conta", responsavel) + ": " + numConta);
                    log.write();
                }
                // Continua o fluxo de exceção para retornar erro na operação
                throw ex;
            }
        }
    }
}
