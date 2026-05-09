# 🏛️ Athenea - Sistema de Gestión de Recursos Educativos

Athenea es una plataforma móvil robusta desarrollada en **Kotlin** para el ecosistema Android. Basada en la arquitectura **MVC (Modelo-Vista-Controlador)**, la aplicación permite a los estudiantes y docentes de Ingeniería de Sistemas gestionar y acceder a materiales de aprendizaje de forma eficiente y segura.

---

## 🚀 Funcionalidades Clave

### 🔐 Seguridad y Autenticación
* **Validación de Credenciales:** Implementación de un sistema de registro con reglas de contraseña estrictas:
    * Mínimo **12 caracteres**.
    * Uso obligatorio de **mayúsculas, minúsculas, números y caracteres especiales**.
* **Gestión de Sesión:** Cierre de sesión seguro con diálogo de confirmación personalizado.

### 👥 Control de Acceso por Roles (RBAC)
* **Perfil Docente:**
    * **CRUD Completo:** Capacidad de **Crear, Leer, Actualizar y Eliminar** recursos.
    * Gestión de metadatos (título, descripción, tipo, enlace e imagen).
    * Confirmación de seguridad mediante *Bottom Sheets* para evitar eliminaciones accidentales.
* **Perfil Estudiante:**
    * Visualización de catálogo de recursos.
    * **Búsqueda Avanzada:** Filtrado por ID, Título o Categoría.
    * **Sistema de Favoritos:** Persistencia de recursos de interés.
    * **Rating Interactivo:** Calificación de materiales con actualización de promedio en tiempo real.

### 🎨 Experiencia de Usuario (UI/UX)
* **Paleta de Colores Profesional:**
    * Primario: `#FF017E` (Pink Primary)
    * Fondo: `#0D1216` (Dark Deep)
    * Acento: `#E19CBB` (Pink Accent)
* **Feedback Dinámico:** Uso de *Custom Toasts* y barras de progreso para una navegación fluida.
* **Diseño Responsivo:** Adaptado para diferentes tamaños de pantalla y estándares de accesibilidad.

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Kotlin
* **Arquitectura:** MVC (Model-View-Controller)
* **Networking:** Retrofit 2 (Consumo de API REST JSON)
* **Carga de Imágenes:** Glide
* **Componentes:** Material Design, View Binding, BottomSheetDialog.

---

## 📦 Instalación y Uso

1. Clonar este repositorio.
2. Abrir el proyecto en **Android Studio Jellyfish** o superior.
3. Configurar la URL de la API en el `RetrofitClient`.
4. Ejecutar en un emulador o dispositivo físico (Android 7.0+).

---

## 📝 Información del Proyecto

* **Materia:** Desarrollo de Software para Móviles
* **Desafío:** #3 - Sistema de Gestión de Recursos
* **Fecha de entrega:** 9 de mayo de 2026
* **Institución:** Universidad Don Bosco