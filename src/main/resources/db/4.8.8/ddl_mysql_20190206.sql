/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     06/02/2019 17:01:12                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_arquivo                                            */
/*==============================================================*/
create table tb_arquivo
(
   ARQ_CODIGO           varchar(32) not null,
   TAR_CODIGO           varchar(32) not null,
   ARQ_CONTEUDO         longtext not null,
   primary key (ARQ_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_dirf_servidor                                      */
/*==============================================================*/
create table tb_dirf_servidor
(
   SER_CODIGO           varchar(32) not null,
   DIS_ANO_CALENDARIO   smallint not null,
   DIS_DATA_CARGA       datetime not null,
   ARQ_CODIGO           varchar(32) not null,
   primary key (SER_CODIGO, DIS_ANO_CALENDARIO)
) ENGINE=InnoDB;

alter table tb_arquivo add constraint FK_R_746 foreign key (TAR_CODIGO)
      references tb_tipo_arquivo (TAR_CODIGO) on delete restrict on update restrict;

alter table tb_dirf_servidor add constraint FK_R_747 foreign key (SER_CODIGO)
      references tb_servidor (SER_CODIGO) on delete restrict on update restrict;

alter table tb_dirf_servidor add constraint FK_R_748 foreign key (ARQ_CODIGO)
      references tb_arquivo (ARQ_CODIGO) on delete restrict on update restrict;

