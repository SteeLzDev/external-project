-- DESENV-15455
UPDATE tb_relatorio_filtro SET RFI_EXIBE_CSA = '1', RFI_EXIBE_COR = '1' WHERE REL_CODIGO = 'sintetico_mov_fin' AND TFR_CODIGO IN ('campo_matricula', 'campo_cpf');
