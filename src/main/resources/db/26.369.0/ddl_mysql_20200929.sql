/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     29/09/2020 08:47:47                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_consignataria_permite_tda                          */
/*==============================================================*/
create table tb_consignataria_permite_tda
(
   TDA_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   CPT_EXIBE            char(1) not null default 'S',
   primary key (TDA_CODIGO, CSA_CODIGO)
) ENGINE=InnoDB;

alter table tb_consignataria_permite_tda add constraint FK_R_816 foreign key (TDA_CODIGO)
      references tb_tipo_dado_adicional (TDA_CODIGO) on delete restrict on update restrict;

alter table tb_consignataria_permite_tda add constraint FK_R_817 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

