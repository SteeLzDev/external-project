/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     28/10/2021 12:31:41                          */
/*==============================================================*/

/*==============================================================*/
/* RENOMEIA AS TABELAS ATUAIS                                   */
/*==============================================================*/

drop table if exists tmp_tb_arquivo_retorno_parcela;

rename table tb_arquivo_retorno_parcela to tmp_tb_arquivo_retorno_parcela;


drop table if exists tmp_ht_ocorrencia_parcela;

rename table ht_ocorrencia_parcela to tmp_ht_ocorrencia_parcela;


drop table if exists tmp_ht_parcela_desconto;

rename table ht_parcela_desconto to tmp_ht_parcela_desconto;


drop table if exists tmp_tb_ocorrencia_parcela;

rename table tb_ocorrencia_parcela to tmp_tb_ocorrencia_parcela;


drop table if exists tmp_tb_parcela_desconto;

rename table tb_parcela_desconto to tmp_tb_parcela_desconto;


drop table if exists tmp_tb_ocorrencia_parcela_periodo;

rename table tb_ocorrencia_parcela_periodo to tmp_tb_ocorrencia_parcela_periodo;


drop table if exists tmp_tb_parcela_desconto_periodo;

rename table tb_parcela_desconto_periodo to tmp_tb_parcela_desconto_periodo;

/*==============================================================*/
/* RECRIA AS NOVAS TABELAS                                      */
/*==============================================================*/

/*==============================================================*/
/* Table: ht_ocorrencia_parcela                                 */
/*==============================================================*/
create table ht_ocorrencia_parcela
(
   OCP_CODIGO           varchar(32) not null,
   PRD_CODIGO           int not null,
   TOC_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32),
   OCP_DATA             datetime not null,
   OCP_OBS              text,
   primary key (OCP_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: ht_parcela_desconto                                   */
/*==============================================================*/
create table ht_parcela_desconto
(
   PRD_CODIGO           int not null auto_increment,
   ADE_CODIGO           varchar(32) not null,
   SPD_CODIGO           varchar(32) not null,
   TDE_CODIGO           varchar(32),
   MNE_CODIGO           varchar(32),
   PRD_NUMERO           smallint not null,
   PRD_DATA_DESCONTO    date not null,
   PRD_VLR_PREVISTO     decimal(13,2) not null,
   PRD_VLR_REALIZADO    decimal(13,2),
   PRD_DATA_REALIZADO   date,
   primary key (PRD_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_arquivo_retorno_parcela                            */
/*==============================================================*/
create table tb_arquivo_retorno_parcela
(
   ADE_CODIGO           varchar(32) not null,
   PRD_NUMERO           smallint not null,
   PRD_DATA_DESCONTO    date not null,
   NOME_ARQUIVO         varchar(100) not null,
   ID_LINHA             int not null,
   primary key (ADE_CODIGO, PRD_NUMERO, PRD_DATA_DESCONTO, NOME_ARQUIVO, ID_LINHA)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_ocorrencia_parcela                                 */
/*==============================================================*/
create table tb_ocorrencia_parcela
(
   OCP_CODIGO           varchar(32) not null,
   PRD_CODIGO           int not null,
   TOC_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   OCP_DATA             datetime not null,
   OCP_OBS              text,
   primary key (OCP_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_ocorrencia_parcela_periodo                         */
/*==============================================================*/
create table tb_ocorrencia_parcela_periodo
(
   OCP_CODIGO           varchar(32) not null,
   PRD_CODIGO           int not null,
   TOC_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32),
   OCP_DATA             datetime not null,
   OCP_OBS              text,
   primary key (OCP_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_parcela_desconto                                   */
/*==============================================================*/
create table tb_parcela_desconto
(
   PRD_CODIGO           int not null auto_increment,
   ADE_CODIGO           varchar(32) not null,
   TDE_CODIGO           varchar(32),
   SPD_CODIGO           varchar(32) not null,
   MNE_CODIGO           varchar(32),
   PRD_NUMERO           smallint not null,
   PRD_DATA_DESCONTO    date not null,
   PRD_VLR_PREVISTO     decimal(13,2) not null,
   PRD_VLR_REALIZADO    decimal(13,2),
   PRD_DATA_REALIZADO   date,
   primary key (PRD_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_parcela_desconto_periodo                           */
/*==============================================================*/
create table tb_parcela_desconto_periodo
(
   PRD_CODIGO           int not null auto_increment,
   ADE_CODIGO           varchar(32) not null,
   TDE_CODIGO           varchar(32),
   SPD_CODIGO           varchar(32) not null,
   MNE_CODIGO           varchar(32),
   PRD_NUMERO           smallint not null,
   PRD_DATA_DESCONTO    date not null,
   PRD_VLR_PREVISTO     decimal(13,2) not null,
   PRD_VLR_REALIZADO    decimal(13,2),
   PRD_DATA_REALIZADO   date,
   primary key (PRD_CODIGO)
) ENGINE=InnoDB;



insert into ht_parcela_desconto (ADE_CODIGO, SPD_CODIGO, TDE_CODIGO, MNE_CODIGO, PRD_NUMERO, PRD_DATA_DESCONTO, PRD_VLR_PREVISTO, PRD_VLR_REALIZADO, PRD_DATA_REALIZADO)
select ADE_CODIGO, SPD_CODIGO, TDE_CODIGO, MNE_CODIGO, PRD_NUMERO, PRD_DATA_DESCONTO, PRD_VLR_PREVISTO, PRD_VLR_REALIZADO, PRD_DATA_REALIZADO
from tmp_ht_parcela_desconto;

insert into tb_parcela_desconto_periodo (ADE_CODIGO, SPD_CODIGO, TDE_CODIGO, MNE_CODIGO, PRD_NUMERO, PRD_DATA_DESCONTO, PRD_VLR_PREVISTO, PRD_VLR_REALIZADO, PRD_DATA_REALIZADO)
select ADE_CODIGO, SPD_CODIGO, TDE_CODIGO, MNE_CODIGO, PRD_NUMERO, PRD_DATA_DESCONTO, PRD_VLR_PREVISTO, PRD_VLR_REALIZADO, PRD_DATA_REALIZADO
from tmp_tb_parcela_desconto_periodo;

insert into tb_parcela_desconto (ADE_CODIGO, SPD_CODIGO, TDE_CODIGO, MNE_CODIGO, PRD_NUMERO, PRD_DATA_DESCONTO, PRD_VLR_PREVISTO, PRD_VLR_REALIZADO, PRD_DATA_REALIZADO)
select ADE_CODIGO, SPD_CODIGO, TDE_CODIGO, MNE_CODIGO, PRD_NUMERO, PRD_DATA_DESCONTO, PRD_VLR_PREVISTO, PRD_VLR_REALIZADO, PRD_DATA_REALIZADO
from tmp_tb_parcela_desconto;


/*==============================================================*/
/* Index: FK_R_502                                              */
/*==============================================================*/
create index FK_R_502 on ht_parcela_desconto
(
   ADE_CODIGO, PRD_NUMERO
);

/*==============================================================*/
/* Index: FK_R_23                                               */
/*==============================================================*/
create index FK_R_23 on tb_parcela_desconto
(
   ADE_CODIGO, PRD_NUMERO
);

/*==============================================================*/
/* Index: FK_R_242                                              */
/*==============================================================*/
create index FK_R_242 on tb_parcela_desconto_periodo
(
   ADE_CODIGO, PRD_NUMERO
);



insert into ht_ocorrencia_parcela (OCP_CODIGO, PRD_CODIGO, TOC_CODIGO, USU_CODIGO, OCP_DATA, OCP_OBS)
select ocp.OCP_CODIGO, prd.PRD_CODIGO, ocp.TOC_CODIGO, ocp.USU_CODIGO, ocp.OCP_DATA, ocp.OCP_OBS
from tmp_ht_ocorrencia_parcela ocp
inner join ht_parcela_desconto prd on (ocp.ADE_CODIGO = prd.ADE_CODIGO and ocp.PRD_NUMERO = prd.PRD_NUMERO);

insert into tb_ocorrencia_parcela (OCP_CODIGO, PRD_CODIGO, TOC_CODIGO, USU_CODIGO, OCP_DATA, OCP_OBS)
select ocp.OCP_CODIGO, prd.PRD_CODIGO, ocp.TOC_CODIGO, ocp.USU_CODIGO, ocp.OCP_DATA, ocp.OCP_OBS
from tmp_tb_ocorrencia_parcela ocp
inner join tb_parcela_desconto prd on (ocp.ADE_CODIGO = prd.ADE_CODIGO and ocp.PRD_NUMERO = prd.PRD_NUMERO);

insert into tb_ocorrencia_parcela_periodo (OCP_CODIGO, PRD_CODIGO, TOC_CODIGO, USU_CODIGO, OCP_DATA, OCP_OBS)
select ocp.OCP_CODIGO, prd.PRD_CODIGO, ocp.TOC_CODIGO, ocp.USU_CODIGO, ocp.OCP_DATA, ocp.OCP_OBS
from tmp_tb_ocorrencia_parcela_periodo ocp
inner join tb_parcela_desconto_periodo prd on (ocp.ADE_CODIGO = prd.ADE_CODIGO and ocp.PRD_NUMERO = prd.PRD_NUMERO);


drop table if exists tmp_ht_ocorrencia_parcela;
drop table if exists tmp_ht_parcela_desconto;
drop table if exists tmp_tb_arquivo_retorno_parcela;
drop table if exists tmp_tb_ocorrencia_parcela;
drop table if exists tmp_tb_ocorrencia_parcela_periodo;
drop table if exists tmp_tb_parcela_desconto_periodo;
drop table if exists tmp_tb_parcela_desconto;

/*==============================================================*/
/* Index: IDX_ART                                               */
/*==============================================================*/
create index IDX_ART on tb_arquivo_retorno_parcela
(
   NOME_ARQUIVO,
   ID_LINHA
);

/*==============================================================*/
/* Index: PRD_DATA_DESCONTO_IDX                                 */
/*==============================================================*/
create index PRD_DATA_DESCONTO_IDX on tb_parcela_desconto
(
   PRD_DATA_DESCONTO, ADE_CODIGO
);

/*==============================================================*/
/* Index: PDP_DATA_DESCONTO_IDX                                 */
/*==============================================================*/
create index PDP_DATA_DESCONTO_IDX on tb_parcela_desconto_periodo
(
   PRD_DATA_DESCONTO, ADE_CODIGO
);

/*==============================================================*/
/* Index: PRD_DATA_REALIZADO_IDX                                */
/*==============================================================*/
create index PRD_DATA_REALIZADO_IDX on tb_parcela_desconto
(
   PRD_DATA_REALIZADO
);

/*==============================================================*/
/* Index: PDP_DATA_REALIZADO_IDX                                */
/*==============================================================*/
create index PDP_DATA_REALIZADO_IDX on tb_parcela_desconto_periodo
(
   PRD_DATA_REALIZADO
);

alter table ht_ocorrencia_parcela add constraint FK_R_506 foreign key (PRD_CODIGO)
      references ht_parcela_desconto (PRD_CODIGO) on delete restrict on update restrict;

alter table ht_ocorrencia_parcela add constraint FK_R_507 foreign key (TOC_CODIGO)
      references tb_tipo_ocorrencia (TOC_CODIGO) on delete restrict on update restrict;

alter table ht_ocorrencia_parcela add constraint FK_R_508 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;


alter table ht_parcela_desconto add constraint FK_R_502 foreign key (ADE_CODIGO)
      references ht_aut_desconto (ADE_CODIGO) on delete restrict on update restrict;

alter table ht_parcela_desconto add constraint FK_R_503 foreign key (SPD_CODIGO)
      references tb_status_parcela_desconto (SPD_CODIGO) on delete restrict on update restrict;

alter table ht_parcela_desconto add constraint FK_R_504 foreign key (TDE_CODIGO)
      references tb_tipo_desconto (TDE_CODIGO) on delete restrict on update restrict;

alter table ht_parcela_desconto add constraint FK_R_505 foreign key (MNE_CODIGO)
      references tb_tipo_motivo_nao_exportacao (MNE_CODIGO) on delete restrict on update restrict;


alter table tb_ocorrencia_parcela add constraint FK_TB_OCORR_R_32_TB_PARCE foreign key (PRD_CODIGO)
      references tb_parcela_desconto (PRD_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_parcela add constraint FK_TB_OCORR_R_56_TB_TIPO_ foreign key (TOC_CODIGO)
      references tb_tipo_ocorrencia (TOC_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_parcela add constraint FK_TB_OCORR_R_94_TB_USUAR foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;


alter table tb_ocorrencia_parcela_periodo add constraint FK_TB_OCORR_R_245_TB_PARCE foreign key (PRD_CODIGO)
      references tb_parcela_desconto_periodo (PRD_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_parcela_periodo add constraint FK_TB_OCORR_R_246_TB_TIPO_ foreign key (TOC_CODIGO)
      references tb_tipo_ocorrencia (TOC_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_parcela_periodo add constraint FK_TB_OCORR_R_247_TB_USUAR foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;


alter table tb_parcela_desconto add constraint FK_TB_PARCE_R_137_TB_TIPO_ foreign key (TDE_CODIGO)
      references tb_tipo_desconto (TDE_CODIGO) on delete restrict on update restrict;

alter table tb_parcela_desconto add constraint FK_TB_PARCE_R_23_TB_AUT_D foreign key (ADE_CODIGO)
      references tb_aut_desconto (ADE_CODIGO) on delete restrict on update restrict;

alter table tb_parcela_desconto add constraint FK_R_490 foreign key (MNE_CODIGO)
      references tb_tipo_motivo_nao_exportacao (MNE_CODIGO) on delete restrict on update restrict;

alter table tb_parcela_desconto add constraint FK_TB_PARCE_R_57_TB_STATU foreign key (SPD_CODIGO)
      references tb_status_parcela_desconto (SPD_CODIGO) on delete restrict on update restrict;


alter table tb_parcela_desconto_periodo add constraint FK_TB_PARCE_R_242_TB_AUT_D foreign key (ADE_CODIGO)
      references tb_aut_desconto (ADE_CODIGO) on delete restrict on update restrict;

alter table tb_parcela_desconto_periodo add constraint FK_TB_PARCE_R_243_TB_STATU foreign key (SPD_CODIGO)
      references tb_status_parcela_desconto (SPD_CODIGO) on delete restrict on update restrict;

alter table tb_parcela_desconto_periodo add constraint FK_TB_PARCE_R_244_TB_TIPO_ foreign key (TDE_CODIGO)
      references tb_tipo_desconto (TDE_CODIGO) on delete restrict on update restrict;

alter table tb_parcela_desconto_periodo add constraint FK_R_489 foreign key (MNE_CODIGO)
      references tb_tipo_motivo_nao_exportacao (MNE_CODIGO) on delete restrict on update restrict;

optimize table ht_parcela_desconto;
optimize table tb_parcela_desconto;
optimize table tb_parcela_desconto_periodo;
optimize table ht_ocorrencia_parcela;
optimize table tb_ocorrencia_parcela;
optimize table tb_ocorrencia_parcela_periodo;
