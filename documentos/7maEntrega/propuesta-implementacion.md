# Propuesta de Implementación de SSO con OAuth 2.0

## 1. Introducción

En este documento se detalla la implementación de un sistema de autenticación centralizado basado en OAuth 2.0 para el sistema de Mejora del Acceso Alimentario. Este enfoque busca garantizar seguridad, escalabilidad y una experiencia de usuario unificada, abordando roles específicos y necesidades del sistema actual.

## 2. Objetivos

1. Establecer un sistema seguro de inicio de sesión único para todos los usuarios.
2. Mejorar la gestión de permisos mediante la centralización de roles.
3. Proveer soporte para la autenticación multifactor (MFA) como una extensión futura.
4. Reducir la carga administrativa al integrar la autenticación con estándares modernos.

## 3. Requerimientos del Sistema

1. **Roles**: Soporte para roles ADMIN, PERSONA_HUMANA, PERSONA_JURIDICA y TÉCNICO.
2. **Inicio de sesión unificado**: Una única pantalla de autenticación para todos los usuarios.
3. **Integración**: Compatibilidad con Java y Javalin.
4. **Persistencia**: Almacenamiento de tokens y gestión de sesiones.
5. **Despliegue**: Operar en AWS con tráfico seguro.

## 4. Solución Propuesta

La solución propuesta se basa en el protocolo OAuth 2.0 con los siguientes componentes:

1. **Servidor de Autorización**: Keycloak será utilizado para gestionar autenticación y autorización.
2. **Flujo Authorization Code Flow con PKCE**: Este flujo garantiza seguridad adicional y es recomendado para aplicaciones web.
3. **Gestión de Tokens**: Emisión de tokens de acceso y refresh para mantener sesiones activas y seguras.
4. **Roles y Permisos**: Configuración directa en el servidor Keycloak para simplificar la validación.

## 5. Detalles de Implementación

### 5.1 Configuración del Servidor OAuth

1. Implementar Keycloak en AWS.
2. Crear clientes para módulos del sistema como Administración, Técnicos y Colaboradores.
3. Configurar roles y permisos directamente en Keycloak.
4. Proteger endpoints utilizando políticas basadas en roles.

### 5.2 Integración con Javalin

1. Reemplazar el middleware de autenticación actual con validación de OAuth tokens.
2. Implementar un cliente OAuth en el módulo de inicio de sesión.
3. Proteger rutas en el servidor con tokens de acceso y validación de roles.

### 5.3 Persistencia de Sesiones

1. Almacenar tokens de acceso y refresh en una base de datos relacional.
2. Implementar mecanismos para revocación de tokens comprometidos.

### 5.4 Despliegue Seguro

1. Configurar Keycloak en AWS con balanceadores de carga y HTTPS.
2. Realizar pruebas de carga para validar la escalabilidad del sistema.

## 6. Prueba de Concepto

La PoC estará enfocada en demostrar la funcionalidad básica de OAuth 2.0 mediante los siguientes pasos:

1. Configurar Keycloak localmente.
2. Proteger rutas específicas según roles.
3. Validar tokens de acceso y roles en una instancia local.

## 7. Roadmap

1. **Semana 1**: Configuración de Keycloak y clientes.
2. **Semana 2**: Integración con Javalin y protección de rutas.
3. **Semana 3**: Despliegue inicial en AWS.
4. **Semana 4**: Optimización de seguridad y pruebas finales.

## 8. Conclusión

Esta implementación de OAuth 2.0 mejorará significativamente la seguridad y escalabilidad del sistema, proporcionando una base sólida para el crecimiento y la integración futura de características avanzadas.

