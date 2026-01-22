-- DESENV-24459
-- MYSQL
alter table tb_param_sist_consignante
   modify column PSI_VLR varchar(1000) not null DEFAULT '';

