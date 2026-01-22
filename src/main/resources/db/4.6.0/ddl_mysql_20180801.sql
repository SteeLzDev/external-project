/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     01/08/2018 10:31:39                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_hist_integracao_beneficio                          */
/*==============================================================*/
create table tb_hist_integracao_beneficio
(
   HIB_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   HIB_PERIODO          date not null,
   HIB_DATA_INI         datetime not null,
   HIB_DATA_FIM         datetime not null,
   HIB_DATA             datetime not null,
   HIB_TIPO             char(1) not null,
   primary key (HIB_CODIGO)
) ENGINE=InnoDB;

alter table tb_hist_integracao_beneficio add constraint FK_R_718 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_hist_integracao_beneficio add constraint FK_R_719 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

