create database `2024-tpa-mi-no-grupo-08`;
use `2024-tpa-mi-no-grupo-08`;

create table cambio_estado (
       id bigint not null,
        estado varchar(255) not null,
        fechaCambio datetime(6),
        id_heladera bigint,
        primary key (id)
    ) engine=InnoDB;
    
create table cambio_temperatura (
       id bigint not null,
        fecha datetime(6) not null,
        temperaturaEnCelsius float not null,
        id_heladera bigint,
        primary key (id)
    ) engine=InnoDB;

create table carga_masiva (
       id bigint not null,
        fechaRegistro datetime(6) not null,
        usuario_id bigint not null,
        primary key (id)
    ) engine=InnoDB;
    
create table distribucion_vianda (
   id bigint not null,
	cantidadViandas integer not null,
	fecha DATE not null,
	motivo TEXT not null,
	terminada BIT(1) not null,
	id_personaHumana bigint not null,
	id_heladeraDestino bigint,
	id_heladeraOrigen bigint,
	primary key (id)
) engine=InnoDB;

create table donacion_dinero (
   id bigint not null,
	fecha DATE not null,
	monto float not null,
	unidadFrecuencia varchar(255) not null,
	id_personaHumana bigint,
	id_personaJuridica bigint,
	primary key (id)
) engine=InnoDB;

create table formas_contribucion_humana (
   personaHumana_id bigint not null,
	contribucionesElegidas varchar(255) not null,
	primary key (personaHumana_id, contribucionesElegidas)
) engine=InnoDB;

create table formas_contribucion_juridicas (
   personaJuridica_id bigint not null,
	contribucionesElegidas varchar(255) not null,
	primary key (personaJuridica_id, contribucionesElegidas)
) engine=InnoDB;

create table heladera (
   id bigint not null,
	capacidadMaximaVianda integer not null,
	latitud varchar(255),
	longitud varchar(255),
	direccion varchar(255),
	estadoHeladera varchar(255) not null,
	fechaRegistro datetime(6) not null,
	nombre varchar(255) not null,
	temperaturaEsperada float,
	modelo_id bigint not null,
	personaJuridica_id bigint,
	primary key (id)
) engine=InnoDB;

create table hibernate_sequence (
   next_val bigint
) engine=InnoDB;

insert into hibernate_sequence values ( 1 );

create table incidente (
   id bigint not null,
	descripcion_del_colaborador varchar(255),
	fecha DATE not null,
	ruta_foto varchar(255),
	solucionado BIT(1) not null,
	tipo_alerta varchar(255),
	tipo_incidente varchar(255) not null,
	id_personaHumana bigint,
	id_heladera bigint not null,
	id_tecnico bigint,
	primary key (id)
) engine=InnoDB;

create table mensaje (
   id bigint not null,
	asunto varchar(255) not null,
	cuerpo TEXT not null,
	fechaEmision datetime(6) not null,
	usuarioDestinatario_id bigint not null,
	usuarioEmisor_id bigint not null,
	primary key (id)
) engine=InnoDB;

create table modelo (
   id bigint not null,
	modelo varchar(255) not null,
	temperaturaMaxima float not null,
	temperaturaMinima float not null,
	primary key (id)
) engine=InnoDB;

create table municipio (
   id bigint not null,
	municipio varchar(255),
	provincia_id bigint,
	primary key (id)
) engine=InnoDB;

create table oferta (
   id bigint not null,
	cantidad_puntos_necesarios float not null,
	imagen_ruta varchar(255),
	nombre varchar(255) not null,
	persona_juridica_id bigint not null,
	rubro_id bigint not null,
	primary key (id)
) engine=InnoDB;

create table oferta_canjeada (
   id bigint not null,
	fechaCanje datetime(6) not null,
	canjeador_id bigint not null,
	oferta_id bigint not null,
	personaHumana_id bigint,
	primary key (id)
) engine=InnoDB;

create table opcion (
   id bigint not null,
	campo varchar(255),
	primary key (id)
) engine=InnoDB;

create table opcion_pregunta (
   pregunta_id bigint not null,
	opcion_id bigint not null,
	primary key (pregunta_id, opcion_id)
) engine=InnoDB;

create table opcion_respuesta (
   respuesta_id bigint not null,
	opcion_id bigint not null,
	primary key (respuesta_id, opcion_id)
) engine=InnoDB;

create table permiso (
   id bigint not null,
	nombre varchar(255),
	primary key (id)
) engine=InnoDB;

create table persona_humana (
   id bigint not null,
	apellido varchar(255) not null,
	mail varchar(255),
	userTelegram bigint,
	whatsapp varchar(255),
	latitud varchar(255),
	longitud varchar(255),
	direccion varchar(255),
	nroDocumento varchar(255),
	tipoDocumento varchar(255),
	fechaNacimiento DATE,
	nombre varchar(255) not null,
	puntajeActual float,
	tarjeta_id bigint,
	usuario_id bigint,
	primary key (id)
) engine=InnoDB;

create table persona_juridica (
   id bigint not null,
	mail varchar(255),
	userTelegram bigint,
	whatsapp varchar(255),
	latitud varchar(255),
	longitud varchar(255),
	direccion varchar(255),
	razonSocial varchar(255) not null,
	tipo varchar(255) not null,
	rubro_id bigint,
	usuario_id bigint,
	primary key (id)
) engine=InnoDB;

create table persona_vulnerable (
   id bigint not null,
	apellido varchar(255) not null,
	latitud varchar(255),
	longitud varchar(255),
	direccion varchar(255),
	nroDocumento varchar(255),
	tipoDocumento varchar(255),
	fechaDeNacimiento DATE not null,
	fechaDeRegistro DATE not null,
	menoresAcargo integer,
	nombre varchar(255) not null,
	personaQueLoRegistro_id bigint not null,
	tarjeta_id bigint,
	primary key (id)
) engine=InnoDB;

create table pregunta (
   tipo varchar(31) not null,
	id bigint not null,
	activa bit not null,
	campo varchar(255) not null,
	primary key (id)
) engine=InnoDB;

create table provincia (
   id bigint not null,
	provincia varchar(255),
	primary key (id)
) engine=InnoDB;

create table respuesta (
   id bigint not null,
	respuestaLibre TEXT,
	personaHumana_id bigint not null,
	pregunta_id bigint not null,
	primary key (id)
) engine=InnoDB;

create table rol (
   id bigint not null,
	tipoRol varchar(255) not null,
	primary key (id)
) engine=InnoDB;

create table rol_permiso (
   rol_id bigint not null,
	permiso_id bigint not null,
	primary key (rol_id, permiso_id)
) engine=InnoDB;

create table rubro (
   id bigint not null auto_increment,
	nombre varchar(255),
	primary key (id)
) engine=InnoDB;

create table solicitud_apertura (
   id bigint not null,
	accion varchar(255) not null,
	apreturaConcretada bit,
	cantidadViandas integer not null,
	fechaConcrecion datetime(6),
	fechaSolicitud datetime(6) not null,
	distribucionVianda_id bigint,
	tarjeta_id bigint not null,
	vianda_id bigint,
	id_heladera bigint,
	primary key (id)
) engine=InnoDB;

create table sugerencia_distribucion (
   id_sugerencia_heladeras bigint not null,
	id_heladera bigint not null
) engine=InnoDB;

create table sugerencia_heladeras (
   id bigint not null,
	fechaRealizacion datetime(6) not null,
	incidente_id bigint,
	suscripcion_id bigint,
	primary key (id)
) engine=InnoDB;

create table suscripcion (
   tipoSuscripcion varchar(31) not null,
	id bigint not null,
	cantidadViandasFaltantes integer,
	cantidadViandasQueQuedan integer,
	id_heladera bigint not null,
	id_persona_humana bigint not null,
	primary key (id)
) engine=InnoDB;

create table tarjeta (
   id bigint not null,
	codigo varchar(255) not null,
	fechaDeBaja DATE,
	fechaRecepcionColaborador DATE,
	fechaRecepcionPersonaVulnerable DATE,
	id_colaborador_repartidor bigint,
	id_colaborador_donador bigint,
	personaVulnerable_id bigint,
	primary key (id)
) engine=InnoDB;

create table tecnico (
   id bigint not null,
	apellido varchar(255) not null,
	mail varchar(255),
	userTelegram bigint,
	whatsapp varchar(255),
	cuil varchar(255) not null,
	latitud varchar(255),
	longitud varchar(255),
	direccion varchar(255),
	distanciaMaximaEnKMParaSerAvisado double precision,
	nroDocumento varchar(255),
	tipoDocumento varchar(255),
	nombre varchar(255) not null,
	usuario_id bigint not null,
	primary key (id)
) engine=InnoDB;

create table uso_de_tarjeta (
   id bigint not null,
	fechaDeUso datetime(6) not null,
	heladera_id bigint not null,
	tarjeta_id bigint,
	primary key (id)
) engine=InnoDB;

create table usuario (
   id bigint not null,
	clave varchar(255) not null,
	nombre varchar(255) not null,
	rol_id bigint not null,
	primary key (id)
) engine=InnoDB;

create table vianda (
   id bigint not null,
	caloriasEnKcal DECIMAL(5,2),
	comida varchar(255) not null,
	entregada BIT(1),
	fechaCaducidad DATE,
	fechaDonacion DATE not null,
	pesoEnGramos DECIMAL(5,2),
	personaHumana_id bigint not null,
	id_heladera bigint,
	primary key (id)
) engine=InnoDB;

create table vianda_por_distribucion (
   id_distribucion_vianda bigint not null,
	id_vianda bigint not null
) engine=InnoDB;

create table visita (
   id bigint not null,
	descripcion varchar(255) not null,
	foto_ruta varchar(255),
	id_incidente bigint not null,
	primary key (id)
) engine=InnoDB;

alter table tecnico 
   add constraint UK_9y8oe0m4evkg9fesshgwmis0 unique (cuil);

alter table usuario 
   add constraint UK_cto7dkti4t38iq8r4cqesbd8k unique (nombre);

alter table cambio_estado 
   add constraint FKfufqv39mmpep1f7hse4healky 
   foreign key (id_heladera) 
   references heladera (id);

alter table cambio_temperatura 
   add constraint FKru7bt03nlkw692hxyxxqpmvwl 
   foreign key (id_heladera) 
   references heladera (id);

alter table carga_masiva 
   add constraint FKjfxwmxuxqunhgkvedfnn21xpc 
   foreign key (usuario_id) 
   references usuario (id);

alter table distribucion_vianda 
   add constraint FK7tfp33mkkiiqlxlq23uvu48s6 
   foreign key (id_personaHumana) 
   references persona_humana (id);

alter table distribucion_vianda 
   add constraint FKt9b9p3p1swr2072i0bousv4v0 
   foreign key (id_heladeraDestino) 
   references heladera (id);

alter table distribucion_vianda 
   add constraint FKk2n4d8vi5uggoy9hgf5sixqqj 
   foreign key (id_heladeraOrigen) 
   references heladera (id);

alter table donacion_dinero 
   add constraint FKfh5wbjbjjki6yjogox65ujcag 
   foreign key (id_personaHumana) 
   references persona_humana (id);

alter table donacion_dinero 
   add constraint FK2kqfm6t2e8mfrwof6gcvawqem 
   foreign key (id_personaJuridica) 
   references persona_juridica (id);

alter table formas_contribucion_humana 
   add constraint FKgavo7f6jfyy1df3y2e09dyd1c 
   foreign key (personaHumana_id) 
   references persona_humana (id);

alter table formas_contribucion_juridicas 
   add constraint FKp3uwv99aybfvfhrt4sf7au3b 
   foreign key (personaJuridica_id) 
   references persona_juridica (id);

alter table heladera 
   add constraint FKdhra1mje81cu5dlv03kv977x9 
   foreign key (modelo_id) 
   references modelo (id);

alter table heladera 
   add constraint FKdhro9nf1wfp8mcn0srike2kgs 
   foreign key (personaJuridica_id) 
   references persona_juridica (id);

alter table incidente 
   add constraint FKlnqd9p5xxsue41r1tevm3102q 
   foreign key (id_personaHumana) 
   references persona_humana (id);

alter table incidente 
   add constraint FKaf9ws8v3mt965pioxyaejtlme 
   foreign key (id_heladera) 
   references heladera (id);

alter table incidente 
   add constraint FKpd6362f0xofs9wwrygfovdc3v 
   foreign key (id_tecnico) 
   references tecnico (id);

alter table mensaje 
   add constraint FK154c78fdnlvqeyioknpoium49 
   foreign key (usuarioDestinatario_id) 
   references usuario (id);

alter table mensaje 
   add constraint FKcikmtyxkqnkohqhrtn7xxqdsc 
   foreign key (usuarioEmisor_id) 
   references usuario (id);

alter table municipio 
   add constraint FK4ud8nsel0i9ti2kr3hboxrosg 
   foreign key (provincia_id) 
   references provincia (id);

alter table oferta 
   add constraint FKf8s3ajl97eac9govtxx9y7b2r 
   foreign key (persona_juridica_id) 
   references persona_juridica (id);

alter table oferta 
   add constraint FK1kmsh1pyxql9xnhs4ky0inlr9 
   foreign key (rubro_id) 
   references rubro (id);

alter table oferta_canjeada 
   add constraint FKtq0yvjgokncjw8ab1y6y2832i 
   foreign key (canjeador_id) 
   references persona_humana (id);

alter table oferta_canjeada 
   add constraint FKd46gn5iodhtgrn0e4iuacytu1 
   foreign key (oferta_id) 
   references oferta (id);

alter table oferta_canjeada 
   add constraint FKfettkjrcg7s4gbo7ic05iywdr 
   foreign key (personaHumana_id) 
   references persona_humana (id);

alter table opcion_pregunta 
   add constraint FKcd3t3w19591iccxl84t8qrac6 
   foreign key (opcion_id) 
   references opcion (id);

alter table opcion_pregunta 
   add constraint FKfi0g890y9huvjbg8slhx12xpe 
   foreign key (pregunta_id) 
   references pregunta (id);

alter table opcion_respuesta 
   add constraint FKtmwnom27au66ajdasif4qx5se 
   foreign key (opcion_id) 
   references opcion (id);

alter table opcion_respuesta 
   add constraint FK2vjfc7x3pjuijjx8h1i05ne6n 
   foreign key (respuesta_id) 
   references respuesta (id);

alter table persona_humana 
   add constraint FKf91s0v42fisjpu92b9l4ywktk 
   foreign key (tarjeta_id) 
   references tarjeta (id);

alter table persona_humana 
   add constraint FKnv4n5c465jilq0uylee6lv7tp 
   foreign key (usuario_id) 
   references usuario (id);

alter table persona_juridica 
   add constraint FKfb65c0c1o6wwbcwm931hirxe 
   foreign key (rubro_id) 
   references rubro (id);

alter table persona_juridica 
   add constraint FKienvm859beegmn058k5ufm25 
   foreign key (usuario_id) 
   references usuario (id);

alter table persona_vulnerable 
   add constraint FK4c72hhvbbxbkdfy9aldx6qd12 
   foreign key (personaQueLoRegistro_id) 
   references persona_humana (id);

alter table persona_vulnerable 
   add constraint FKbv0y1a3f4xrnqdfwuyfqixdma 
   foreign key (tarjeta_id) 
   references tarjeta (id);

alter table respuesta 
   add constraint FK2ibg44lj3mncwc90pdrrv2c1s 
   foreign key (personaHumana_id) 
   references persona_humana (id);

alter table respuesta 
   add constraint FKd9oyrwyjw1otr38btjeevanif 
   foreign key (pregunta_id) 
   references pregunta (id);

alter table rol_permiso 
   add constraint FKfyao8wd0o5tsyem1w55s3141k 
   foreign key (permiso_id) 
   references permiso (id);

alter table rol_permiso 
   add constraint FK6o522368i97la9m9cqn0gul2e 
   foreign key (rol_id) 
   references rol (id);

alter table solicitud_apertura 
   add constraint FKjk1kgnyuql291fxxrwyo44d2y 
   foreign key (distribucionVianda_id) 
   references distribucion_vianda (id);

alter table solicitud_apertura 
   add constraint FK8rrsva6jilb47212cp0cbk1tu 
   foreign key (tarjeta_id) 
   references tarjeta (id);

alter table solicitud_apertura 
   add constraint FKdt4so1q4s6wxvl705tty2rpyi 
   foreign key (vianda_id) 
   references vianda (id);

alter table solicitud_apertura 
   add constraint FKgsiwqmtpogb7w5v3nko8fu6sb 
   foreign key (id_heladera) 
   references heladera (id);

alter table sugerencia_distribucion 
   add constraint FK13x3h54htqvsv8a629upmre1i 
   foreign key (id_heladera) 
   references heladera (id);

alter table sugerencia_distribucion 
   add constraint FKnb6r8xr1oo5yadhln3q57tgsy 
   foreign key (id_sugerencia_heladeras) 
   references sugerencia_heladeras (id);

alter table sugerencia_heladeras 
   add constraint FKtrs193dte35dtmiqnic2a0124 
   foreign key (incidente_id) 
   references incidente (id);

alter table sugerencia_heladeras 
   add constraint FKrnhbvi4e74f23b6iltpkx7r02 
   foreign key (suscripcion_id) 
   references suscripcion (id);

alter table suscripcion 
   add constraint FKhdn8efnfljv14vmc2lt385rft 
   foreign key (id_heladera) 
   references heladera (id);

alter table suscripcion 
   add constraint FKl9huvv10fu10homg7aphecs5u 
   foreign key (id_persona_humana) 
   references persona_humana (id);

alter table tarjeta 
   add constraint FKcvde8c1ap2fjbgpu198gy7xcp 
   foreign key (id_colaborador_repartidor) 
   references persona_humana (id);

alter table tarjeta 
   add constraint FKqsd2i6amctipepmw3vboh4scp 
   foreign key (id_colaborador_donador) 
   references persona_humana (id);

alter table tarjeta 
   add constraint FK9e0e7buv21ruolgdyrstrfqkt 
   foreign key (personaVulnerable_id) 
   references persona_vulnerable (id);

alter table tecnico 
   add constraint FK3k1f8ce4xjsw2bg58bihd4ets 
   foreign key (usuario_id) 
   references usuario (id);

alter table uso_de_tarjeta 
   add constraint FKfta0ri5xxghcm8x39rhmjvq25 
   foreign key (heladera_id) 
   references heladera (id);

alter table uso_de_tarjeta 
   add constraint FKobgywdfcgknfwhn4atflcpmk6 
   foreign key (tarjeta_id) 
   references tarjeta (id);

alter table usuario 
   add constraint FKshkwj12wg6vkm6iuwhvcfpct8 
   foreign key (rol_id) 
   references rol (id);

alter table vianda 
   add constraint FKi8wgcdg2ay8cgh4p38braq8cq 
   foreign key (personaHumana_id) 
   references persona_humana (id);

alter table vianda 
   add constraint FKjwd0ksrv8t812fnd9h6ly1ubx 
   foreign key (id_heladera) 
   references heladera (id);

alter table vianda_por_distribucion 
   add constraint FK9580cqxtbjjetbia4wka7cn9d 
   foreign key (id_vianda) 
   references vianda (id);

alter table vianda_por_distribucion 
   add constraint FKn4mt76intepbec02a5mvbms8u 
   foreign key (id_distribucion_vianda) 
   references distribucion_vianda (id);

alter table visita 
   add constraint FKeul73he4b7v9e7ljftegmpqiw 
   foreign key (id_incidente) 
   references incidente (id);