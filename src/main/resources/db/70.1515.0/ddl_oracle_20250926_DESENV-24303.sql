ALTER TABLE tb_registro_servidor MODIFY (rse_matricula VARCHAR2(35) DEFAULT '' NOT NULL);
ALTER TABLE tb_arquivo_retorno MODIFY (rse_matricula VARCHAR2(35) NOT NULL);
ALTER TABLE tb_arquivo_previa_operadora MODIFY (rse_matricula VARCHAR2(35) NOT NULL);
ALTER TABLE tb_arquivo_faturamento_ben MODIFY (rse_matricula VARCHAR2(35) NULL);
ALTER TABLE tb_bloco_processamento MODIFY (rse_matricula VARCHAR2(35) NULL);
ALTER TABLE ht_registro_servidor MODIFY (rse_matricula VARCHAR2(35) NOT NULL);
ALTER TABLE tb_registro_servidor MODIFY (rse_matricula_inst VARCHAR2(35) NULL);
ALTER TABLE tb_arquivo_faturamento_ben MODIFY (RSE_MATRICULA_INST VARCHAR2(35) NULL);
ALTER TABLE ht_registro_servidor MODIFY (RSE_MATRICULA_INST VARCHAR2(35) NULL);

