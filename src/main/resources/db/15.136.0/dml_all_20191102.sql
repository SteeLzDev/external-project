-- DESENV-12693
UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSE = '0', RFI_EXIBE_ORG = '0' WHERE REL_CODIGO IN ('provisionamento_margem','liquidado_pos_corte') AND TFR_CODIGO = 'campo_cor';
DELETE FROM tb_relatorio_filtro WHERE REL_CODIGO IN ('conf_cad_csa', 'conf_cad_cor') AND TFR_CODIGO = 'campo_org';

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 4 WHERE REL_CODIGO = 'rel_inc_ben_por_periodo' AND TFR_CODIGO = 'campo_agendado';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 5 WHERE REL_CODIGO = 'rel_inc_ben_por_periodo' AND TFR_CODIGO = 'campo_data_execucao';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 6 WHERE REL_CODIGO = 'rel_inc_ben_por_periodo' AND TFR_CODIGO = 'campo_tipo_agendamento';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 7 WHERE REL_CODIGO = 'rel_inc_ben_por_periodo' AND TFR_CODIGO = 'campo_periodicidade';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 8 WHERE REL_CODIGO = 'rel_inc_ben_por_periodo' AND TFR_CODIGO = 'campo_formato_relatorio';

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 3 WHERE REL_CODIGO = 'contratos_beneficios' AND TFR_CODIGO = 'campo_agendado';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 4 WHERE REL_CODIGO = 'contratos_beneficios' AND TFR_CODIGO = 'campo_data_execucao';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 5 WHERE REL_CODIGO = 'contratos_beneficios' AND TFR_CODIGO = 'campo_tipo_agendamento';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 6 WHERE REL_CODIGO = 'contratos_beneficios' AND TFR_CODIGO = 'campo_periodicidade';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 7 WHERE REL_CODIGO = 'contratos_beneficios' AND TFR_CODIGO = 'campo_formato_relatorio';

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 5 WHERE REL_CODIGO = 'agenciamento_analitico_operadora' AND TFR_CODIGO = 'campo_agendado';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 6 WHERE REL_CODIGO = 'agenciamento_analitico_operadora' AND TFR_CODIGO = 'campo_data_execucao';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 7 WHERE REL_CODIGO = 'agenciamento_analitico_operadora' AND TFR_CODIGO = 'campo_tipo_agendamento';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 8 WHERE REL_CODIGO = 'agenciamento_analitico_operadora' AND TFR_CODIGO = 'campo_periodicidade';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 9 WHERE REL_CODIGO = 'agenciamento_analitico_operadora' AND TFR_CODIGO = 'campo_formato_relatorio';

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/autorizarOperacaoV2' WHERE ACR_RECURSO = '/geral/segunda_senha.jsp';
