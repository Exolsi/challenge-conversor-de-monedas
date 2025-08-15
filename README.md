# Challenge Conversor de Monedas

Aplicación de consola en **Java** que consulta **ExchangeRate-API** y permite convertir entre múltiples monedas, con **historial de conversiones** y **registros con marca de tiempo**.

## 🛠 Tecnologías utilizadas
- **Java** (17+)
- **Gson** para manejo de JSON
- **IntelliJ IDEA** (recomendado) u otro editor

---

## ✅ Características
- Conversión entre monedas usando `https://v6.exchangerate-api.com`.
- **Historial** de las últimas 20 conversiones (en memoria).
- **Marca de tiempo** (zona local) para cada conversión.
- **Más monedas** soportadas (puedes ampliarlas fácilmente).

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

> **Nota:** `ConversorMonedas.java` no usa `package`. Si lo agregas, ajusta los comandos de compilación/ejecución.

---

## 🔑 API Key
El código usa una **API key inline** (constante `API_KEY`) para facilitar las pruebas locales.  
Si vas a subir el repo a público, **no dejes la key en claro**. Alternativas:
- pásala a **variable de entorno** (`EXCHANGE_RATE_API_KEY`) y léela desde el código,
- o guárdala en un archivo local ignorado por Git.

---

## 🧑‍💻 Cómo ejecutar

### Opción A — IntelliJ (rápido)
1. Abre el proyecto.
2. Asegúrate de agregar `gson.jar` como **Library** al módulo:
   - Click derecho al `gson.jar` → **Add as Library…**
3. Ejecuta `ConversorMonedas` (botón Run).

### Opción B — Terminal con `javac/java`

**Windows (PowerShell / CMD):**
```bat
mkdir out
javac -cp ".;gson.jar" -d out src\ConversorMonedas.java
java  -cp ".;out;gson.jar" ConversorMonedas
```

**Linux / Mac:**
```bash
mkdir -p out
javac -cp ".:gson.jar" -d out src/ConversorMonedas.java
java  -cp ".:out:gson.jar" ConversorMonedas
```

---

## 🎮 Uso
1. El programa muestra un **menú**:
   - Realizar conversión
   - Ver historial
   - Listar monedas disponibles
   - Salir
2. Elige **moneda origen** y **destino**.
3. Ingresa el **monto**.
4. Se consulta la API y se muestra el resultado con:
   - monto convertido,
   - **tasa** usada,
   - **última actualización** reportada por la API,
   - **marca de tiempo local** del registro.
5. Cada conversión se guarda en el **historial** (máx. 20).

➡ *Ejemplo:* “Convertir 100 ARS a BOB” mostrará el valor actualizado según la API y quedará registrado en el historial.

---

https://github.com/user-attachments/assets/e1f89b22-6a1a-4488-92cd-b099a4a80ef4



## 💱 Monedas soportadas (por defecto)
```
USD, ARS, BOB, BRL, CLP, COP, EUR, MXN, PEN, PYG, UYU, GBP, CAD, JPY, CHF
```
Puedes editar la lista en `MONEDAS_PERMITIDAS` dentro del código para agregar/quitar códigos ISO-4217.

---

## 🔌 Sobre la API
Esta app utiliza **ExchangeRate-API** para obtener los tipos de cambio.  
Ejemplo de endpoint usado:
```
https://v6.exchangerate-api.com/v6/<API_KEY>/latest/USD
```
Regresa un JSON con:
- `base_code`, `result`, `time_last_update_utc`
- `conversion_rates` → objeto `{ "CODE": rate }`

---

## 🧪 Ejemplo de flujo
```
1) Realizar conversión
2) Ver historial
3) Listar monedas disponibles
0) Salir
Selecciona una opción: 1

Monedas soportadas: USD, ARS, BOB, BRL, ...
Moneda ORIGEN: USD
Moneda DESTINO: ARS
Monto a convertir: 100

100.0000 USD = 12345.6789 ARS
Tasa: 123.456789 | Base: USD
Última actualización (API): Fri, 01 Aug 2025 00:00:00 +0000
Registro local: 2025-08-14 12:34:56 AST
```

---

## 🧰 Problemas comunes
- **HTTP 401 / invalid-key**: tu API key es incorrecta o no está activa.
- **HTTP 429 / cuota**: alcanzaste el límite del plan gratuito; reintenta más tarde.
- **SSL/Handshake**: revisa fecha/hora del sistema o antivirus que “intercepte” HTTPS.
- **No encuentra Gson**: asegúrate de compilar/ejecutar con `-cp` incluyendo `gson.jar`.
