INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('anexos_consignacao', 'campo_data_inclusao_alteracao', '1', '1', '1', '1', '1', 3, NULL, '1');

UPDATE tb_relatorio_filtro
SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1
WHERE REL_CODIGO = 'anexos_consignacao' AND RFI_SEQUENCIA >= 3 AND TFR_CODIGO <> 'campo_data_inclusao_alteracao';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '1', RFI_EXIBE_COR = '1', RFI_EXIBE_ORG = '1', RFI_EXIBE_SER = '1', RFI_EXIBE_SUP = '1' WHERE REL_CODIGO = 'anexos_consignacao' and TFR_CODIGO = 'campo_data_periodo';

UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '1', RFI_EXIBE_COR = '1', RFI_EXIBE_ORG = '1', RFI_EXIBE_SER = '1', RFI_EXIBE_SUP = '1' WHERE REL_CODIGO = 'anexos_consignacao' and TFR_CODIGO = 'campo_tipo_periodo';
