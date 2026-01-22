ALTER TABLE tb_registro_servidor MODIFY RSE_MATRICULA varchar(35) NOT NULL DEFAULT '';
ALTER TABLE tb_arquivo_retorno MODIFY RSE_MATRICULA varchar(35) NOT NULL;
ALTER TABLE tb_arquivo_previa_operadora MODIFY RSE_MATRICULA varchar(35) NOT NULL;
ALTER TABLE tb_arquivo_faturamento_ben MODIFY RSE_MATRICULA varchar(35) NULL;
ALTER TABLE tb_bloco_processamento MODIFY RSE_MATRICULA varchar(35) NULL;
ALTER TABLE ht_registro_servidor MODIFY RSE_MATRICULA varchar(35) NOT NULL;
ALTER TABLE tb_registro_servidor MODIFY RSE_MATRICULA_INST varchar(35) NULL;
ALTER TABLE tb_arquivo_faturamento_ben MODIFY RSE_MATRICULA_INST varchar(35) NULL;
ALTER TABLE ht_registro_servidor MODIFY RSE_MATRICULA_INST varchar(35) NULL;

