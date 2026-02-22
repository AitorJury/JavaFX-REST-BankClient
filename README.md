# JavaFX REST Bank Client 🏦 | Desktop UI & API Integration

Este proyecto consiste en el desarrollo de una aplicación de escritorio (Front-end) robusta utilizando **JavaFX**, diseñada para interactuar con un backend RESTful basado en servicios web. La aplicación permite la gestión integral de cuentas bancarias, movimientos financieros y perfiles de clientes.

---

## 🚀 Características Técnicas

### 1. Integración RESTful
* **Arquitectura Cliente-Servidor:** Consumo de APIs expuestas por un servidor Glassfish mediante clientes REST personalizados.
* **Formatos de Intercambio:** Tratamiento dinámico de datos en formatos **XML y JSON**.

### 2. Interfaz de Usuario (UI/UX)
* **Diseño Reactivo:** Implementación de vistas mediante **FXML** y estilos personalizados con **CSS**.
* **Gestión de Estados:** Controladores especializados para cada flujo de usuario (SignIn, SignUp, Movements, Accounts).
* **Validaciones en Cliente:** Control estricto de integridad referencial y formatos antes del envío al servidor para optimizar el tráfico de red.

### 3. Calidad y Testing
* **Unit Testing:** Cobertura de pruebas para controladores y lógica de negocio utilizando **JUnit 4**, garantizando el comportamiento esperado de la interfaz ante diferentes escenarios de datos.

---

## 🏗️ Estructura del Proyecto (Maven Standard)
* `src/main/java`: Lógica de negocio, modelos de datos y controladores de UI.
* `src/main/resources`: Recursos estáticos (CSS, Imágenes) y archivos de definición de vistas (FXML).
* `src/test/java`: Suite de pruebas unitarias para validación de componentes.

---

## 🛠️ Stack Tecnológico
- **Lenguaje:** Java 8+
- **UI Framework:** JavaFX
- **Backend Sync:** RESTful Services (Glassfish)
- **Testing:** JUnit 4
- **Database:** MySQL (vía Server-side API)

---

## 👤 Autor
**Aitor Jury Rodríguez** - *Fullstack Developer & Intern @ BBVA Technology*
- [LinkedIn](https://www.linkedin.com/in/aitor-jury-rodr%C3%ADguez-6330742b1/)
- [Email](mailto:aitor.jr04@gmail.com)
