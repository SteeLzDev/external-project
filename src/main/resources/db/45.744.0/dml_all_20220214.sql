-- DESENV-17401
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 13 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_formato_relatorio';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = 12 WHERE REL_CODIGO = 'estatistico' AND TFR_CODIGO = 'campo_status_servidor';
