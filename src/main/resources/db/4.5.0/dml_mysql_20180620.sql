-- DESENV-8808

-- FAZ DROP DAS TABELAS QUE ANTES ERAM CRIADAS A CADA ROTINA E QUE NÃO SÃO MAIS NECESSÁRIAS

drop table if exists tmp_valor_correcao_saldo_devedor;
drop table if exists tb_tmp_inclusao_alteracao_sem_anexo;
drop table if exists tb_tmp_ades_renegociacao_periodo;
drop table if exists tb_tmp_dados_renegociacao_periodo;
drop table if exists tb_tmp_exportacao_renegociacao_consolidada;
