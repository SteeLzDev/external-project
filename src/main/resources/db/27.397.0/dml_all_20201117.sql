-- DESENV-14814
-- @@delimiter = /

drop temporary table if exists tmp_inclusao_historico_status_ade
/
create temporary table tmp_inclusao_historico_status_ade (ADE_CODIGO varchar(32), SAD_CODIGO_ANTERIOR varchar(32), SAD_CODIGO_NOVO varchar(32), HSA_DATA datetime, key ix01 (SAD_CODIGO_ANTERIOR), key ix02 (SAD_CODIGO_NOVO))
/
insert into tmp_inclusao_historico_status_ade (ADE_CODIGO, SAD_CODIGO_ANTERIOR, SAD_CODIGO_NOVO, HSA_DATA)
select distinct 
  ADE_CODIGO,
  trim(replace(substr(oca_obs, locate(' DE ', oca_obs) + length(' DE '), 2), '.', '')) as SAD_CODIGO_ANTERIOR, 
  trim(replace(substr(oca_obs, locate(' PARA ', oca_obs) + length(' PARA '), 2), '.', '')) as SAD_CODIGO_NOVO, 
  OCA_DATA AS HSA_DATA 
from tb_ocorrencia_autorizacao 
where oca_obs like concat((select tex_texto from tb_texto_sistema where tex_chave = 'mensagem.ocorrencia.autorizacao.alteracao.status.prefixo'), '%') 
/
insert into tb_historico_status_ade (ADE_CODIGO, SAD_CODIGO_ANTERIOR, SAD_CODIGO_NOVO, HSA_DATA)
select distinct ADE_CODIGO, SAD_CODIGO_ANTERIOR, SAD_CODIGO_NOVO, HSA_DATA
from tmp_inclusao_historico_status_ade
where SAD_CODIGO_ANTERIOR in (select sad_codigo from tb_status_autorizacao_desconto)
and SAD_CODIGO_NOVO in (select sad_codigo from tb_status_autorizacao_desconto)
/

DROP TRIGGER IF EXISTS trg_atualiza_ade_data_status 
/
CREATE TRIGGER trg_atualiza_ade_data_status BEFORE UPDATE ON tb_aut_desconto
  FOR EACH ROW BEGIN
    IF COALESCE(OLD.SAD_CODIGO, '') != COALESCE(NEW.SAD_CODIGO, '') THEN 
      SET NEW.ADE_DATA_STATUS = NOW();
      INSERT INTO tb_historico_status_ade (ADE_CODIGO, SAD_CODIGO_ANTERIOR, SAD_CODIGO_NOVO, HSA_DATA)
             VALUES (NEW.ADE_CODIGO, OLD.SAD_CODIGO, NEW.SAD_CODIGO, NOW())
      ;
    END IF; 
  END;
/

INSERT INTO tb_regra_validacao_ambiente (REA_CODIGO, REA_DESCRICAO, REA_ATIVO, REA_DATA_CADASTRO, REA_JAVA_CLASS_NAME, REA_SEQUENCIA, REA_BLOQUEIA_SISTEMA)
VALUES ('5', 'Trigger de histórico de status de ADE existe', 1, NOW(), 'com.zetra.ambiente.RegraValidacaoTriggerStatusAutDesconto', 5, 1) 
/
