package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.PERFIL_CONSIGNADO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.pontuacao.PontuacaoServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

public class ConsultarPerfilConsignadoCommand extends RequisicaoExternaCommand {

    protected String tipoEntidade = null;
    protected String codigoEntidade = null;

    public ConsultarPerfilConsignadoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    public void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        if (TextHelper.isNull(parametros.get(RSE_MATRICULA)) &&
                TextHelper.isNull(parametros.get(SER_CPF))) {
            throw new ZetraException("mensagem.requerMatrOuCpf", responsavel);
        }
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final List<TransferObject> servidores = (List<TransferObject>) parametros.get(SERVIDORES);
        final TransferObject servidor = (TransferObject) parametros.get(SERVIDOR);

        if (!ParamSist.getBoolParamSist(CodedValues.TPC_HABILITAR_MODULO_PERFIL_CONSIGNADO, responsavel)) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        if (servidores != null && servidores.size() > 1) {
            throw new ZetraException("mensagem.multiplosServidoresEncontrados", responsavel);
        } else if (servidor == null) {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }
         
        // Verifica se existe perfil consignado deste RSE para a CSA do usuário
        final String rseCodigo = servidor.getAttribute(Columns.RSE_CODIGO).toString();
        final PontuacaoServidorController pontuacaoServidorController = ApplicationContextProvider.getApplicationContext().getBean(PontuacaoServidorController.class);
        final String pontuacaoCsa = pontuacaoServidorController.consultarPontuacaoCsa(rseCodigo, responsavel);
        if (!TextHelper.isNull(pontuacaoCsa)) {
            servidor.setAttribute(Columns.RSE_PONTUACAO, pontuacaoCsa);
        }

        // Objeto com os dados de retorno
        parametros.put(PERFIL_CONSIGNADO, servidor);
    }
}
