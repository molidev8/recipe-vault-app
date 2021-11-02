# Recipe-Vault

Aplicación Android desarrollada para mi TFM en el Máster Desarrollo de Aplicaciones y Servicios para Dispositivos Móviles para almacenar y compartir recetas utilizando Nearby.

Esta app permite a los usuarios almacenar recetas de forma estructurada a través de una interfaz que utiliza componentes de diseño modernos centrados en la navegación por gestos, el aprovechamiento del espacio en pantalla y el uso de animaciones.

La aplicación sigue Clean Architecture, patrón de presentación MVVM, con un repositorio que gestiona una base de datos local con Room. Otra de sus características son la inyección de dependencias con Hilt, la automatización y programación de tareas con WorkManager, modelo Single Activity...

Puedes ver como funciona la aplicación y su interfaz consultando la memoria del TFM en el siguiente enlace: https://oa.upm.es/68165/

Requisitos: Para poder utilizar la aplicación es necesario conseguir una key de la API de Dropbox y añadirla al proyecto con el string resource propuesto en el fichero keys.xml.
