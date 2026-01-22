-- DESENV-18004
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('891', 'Permite impressão dos boletos da consignação a partir do mobile', 'SN', 'S', 'S', 'N', 'S', 'S', '3');

INSERT INTO tb_relatorio (REL_CODIGO,FUN_CODIGO,REL_TITULO,REL_ATIVO,REL_AGENDADO,REL_CLASSE_RELATORIO,REL_CLASSE_PROCESSO,REL_TEMPLATE_JASPER,REL_QTD_DIAS_LIMPEZA,REL_CUSTOMIZADO)
VALUES ('boleto_ade', '26','Boleto Autorização Desconto', 1, 'N', 'com.zetra.econsig.report.reports.RelatorioBoletoADE', 'com.zetra.econsig.job.process.ProcessaRelatorioBoletoADE', 'BoletoADE.jasper', 1, 'N');

