-- DESENV-20462
-- MYSQL
INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO)
VALUES ('54', '1', '1', '1', 'Gera arquivo de margem (servico externo) de acordo com data prevista de retorno', 'com.zetra.econsig.job.jobs.GeraArquivoMargemServicoExternoJob', NOW(), NOW(), NULL);

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailErroCriarArqMargem', '<@nome_sistema>: Erro ao criar arquivo de margem', 'Prezado Suporte,<br>não foi possível criar o arquivo de margem utilizando o serviço externo.<br><br>Gentileza verificar os logs do sistema.');

