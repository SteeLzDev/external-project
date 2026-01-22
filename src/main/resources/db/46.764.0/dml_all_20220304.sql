-- DESENV-17567
UPDATE tb_relatorio SET 
REL_CLASSE_RELATORIO = REPLACE(REL_CLASSE_RELATORIO, 'com.zetra.report.reports.', 'com.zetra.econsig.report.reports.'),
REL_CLASSE_PROCESSO = REPLACE(REL_CLASSE_PROCESSO, 'com.zetra.processamento.', 'com.zetra.econsig.job.process.'),
REL_CLASSE_AGENDAMENTO = REPLACE(REL_CLASSE_AGENDAMENTO, 'com.zetra.timer.econsig.job.', 'com.zetra.econsig.job.jobs.')
;

UPDATE tb_agendamento SET
AGD_JAVA_CLASS_NAME = REPLACE(AGD_JAVA_CLASS_NAME, 'com.zetra.timer.econsig.job.', 'com.zetra.econsig.job.jobs.')
;

UPDATE tb_regra_validacao_ambiente SET
REA_JAVA_CLASS_NAME = REPLACE(REA_JAVA_CLASS_NAME, 'com.zetra.ambiente.', 'com.zetra.econsig.helper.validacaoambiente.')
;

UPDATE tb_regra_validacao_movimento SET
RVM_JAVA_CLASS_NAME = REPLACE(RVM_JAVA_CLASS_NAME, 'com.zetra.exportacao.validacao.regra.', 'com.zetra.econsig.folha.exportacao.validacao.regra.')
;

UPDATE tb_param_sist_consignante SET PSI_VLR = REPLACE(PSI_VLR, 'com.zetra.exportacao.', 'com.zetra.econsig.folha.exportacao.impl.') WHERE TPC_CODIGO = '81';

UPDATE tb_param_sist_consignante SET PSI_VLR = REPLACE(PSI_VLR, 'com.zetra.pattern.viewhelper.econsig.reserva.', 'com.zetra.econsig.helper.geradoradenumero.') WHERE TPC_CODIGO = '215';

UPDATE tb_param_sist_consignante SET PSI_VLR = REPLACE(PSI_VLR, 'com.zetra.retorno.implementacoes.', 'com.zetra.econsig.folha.retorno.impl.') WHERE TPC_CODIGO = '279';

UPDATE tb_param_sist_consignante SET PSI_VLR = REPLACE(PSI_VLR, 'com.zetra.margem.implementacoes.', 'com.zetra.econsig.folha.margem.impl.') WHERE TPC_CODIGO = '280';

UPDATE tb_param_sist_consignante SET PSI_VLR = REPLACE(PSI_VLR, 'com.zetra.contracheque.', 'com.zetra.econsig.folha.contracheque.impl.') WHERE TPC_CODIGO = '346';

UPDATE tb_param_sist_consignante SET PSI_VLR = 'com.zetra.econsig.parser.EscritorBaseDeDados,com.zetra.econsig.persistence.dao.generic.GenericServidorDAO,com.zetra.econsig.persistence.dao.mysql.MySqlAutorizacaoDAO' WHERE TPC_CODIGO = '855';

UPDATE tb_param_senha_externa SET PSX_VALOR = REPLACE(PSX_VALOR, 'com.zetra.senhaexterna.', 'com.zetra.econsig.helper.senhaexterna.impl.') WHERE PSX_CHAVE = 'java.class.name';
