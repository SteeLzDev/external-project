-- DESENV-22767
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=23 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_formato_relatorio';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=22 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_envio_email';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=21 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_periodicidade';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=20 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_tipo_agendamento';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=19 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_data_execucao';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=18 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_agendado';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=17 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_tipo_motivo_operacao';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=16 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_tipo_ocorrencia';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=15 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_ordenacao_ade';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=14 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_info_group_oca_ade';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=13 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_termino_contrato';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=12 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_origem_contrato';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=11 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_status_contrato';
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA=10 WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_status_servidor';

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('ocorrencia_autorizacao', 'campo_papel_usuario', '1', '1', '0', '1', '0', 9, NULL, '1');
