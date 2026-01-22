-- DESENV-17374
UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSE = '2' WHERE TFR_CODIGO = 'campo_formato_relatorio' AND RFI_EXIBE_CSE = '1';
UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSA = '2' WHERE TFR_CODIGO = 'campo_formato_relatorio' AND RFI_EXIBE_CSA = '1';
UPDATE tb_relatorio_filtro SET RFI_EXIBE_COR = '2' WHERE TFR_CODIGO = 'campo_formato_relatorio' AND RFI_EXIBE_COR = '1';
UPDATE tb_relatorio_filtro SET RFI_EXIBE_ORG = '2' WHERE TFR_CODIGO = 'campo_formato_relatorio' AND RFI_EXIBE_ORG = '1';
UPDATE tb_relatorio_filtro SET RFI_EXIBE_SER = '2' WHERE TFR_CODIGO = 'campo_formato_relatorio' AND RFI_EXIBE_SER = '1';
UPDATE tb_relatorio_filtro SET RFI_EXIBE_SUP = '2' WHERE TFR_CODIGO = 'campo_formato_relatorio' AND RFI_EXIBE_SUP = '1';

