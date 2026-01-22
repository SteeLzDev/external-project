package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaGeraArquivoMargemServicoExterno</p>
 * <p>Description: Classe de processamento para gerar arquivo de margem a partir de um serviço externo.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaGeraArquivoMargemServicoExterno extends ProcessoAgendadoPeriodico {

    public ProcessaGeraArquivoMargemServicoExterno(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        String urlSistemaExterno = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_CONSULTAR_MARGEM_SISTEMA_EXTERNO, AcessoSistema.getAcessoUsuarioSistema());
        if(!TextHelper.isNull(urlSistemaExterno)) {
            urlSistemaExterno = !urlSistemaExterno.endsWith("/") ? urlSistemaExterno + "/" : urlSistemaExterno;
            MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);
            margemController.criaArquivoMargemOrigemServicoExterno(urlSistemaExterno, getResponsavel());
        }
    }
}