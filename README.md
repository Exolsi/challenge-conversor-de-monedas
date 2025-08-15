# 💱 Challenge Conversor de Monedas

Este proyecto es una aplicación escrita en Java que permite convertir valores entre distintas monedas utilizando la API de [ExchangeRate-API](https://www.exchangerate-api.com/).

---

## 🧠 Funcionalidades

- Consulta el valor de cambio entre monedas.
- Utiliza una API externa (ExchangeRate-API).
- Conversión precisa usando tasas reales.
- Compatible con al menos 3 monedas distintas:
  - ARS – Peso argentino 🇦🇷  
  - BOB – Boliviano 🇧🇴  
  - USD - Dólar estadounidense US

---

## 🛠 Tecnologías utilizadas

- **Java** (versión 17+)
- **Librería Gson** para manejo de JSON
- IntelliJ IDEA como entorno de desarrollo

---

## 📂 Estructura del proyecto

```
Challenge Conversor de Monedas/
├─ src/
│  └─ ConversorMonedas.java
├─ gson.jar
├─ README.md
└─ ...
```

---

## 🧑‍💻 Cómo usarlo (presentación)

1. Al iniciar el programa, se muestra un menú con distintas monedas disponibles para convertir.
2. El usuario debe seleccionar una **moneda de origen** y luego una **moneda de destino**.
3. A continuación, se solicita el **monto que desea convertir**.
4. El programa realiza la consulta a la **ExchangeRate-API** y obtiene la tasa de conversión actual.
5. Finalmente, muestra el resultado de la conversión con formato claro y amigable.

➡ Ejemplo: Convertir 100 ARS a BOB mostrará el resultado con el valor actualizado al instante según la API.

---

## 🔌 Sobre la API

Esta app utiliza [ExchangeRate-API](https://www.exchangerate-api.com/) para obtener los valores de cambio actualizados.  
Puedes registrarte gratis y obtener tu propia clave si es necesario.

---

