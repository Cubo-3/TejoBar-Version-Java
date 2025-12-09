# 🔍 ANÁLISIS DE INCONSISTENCIAS Y ERRORES CRÍTICOS
## Proyecto TejoBar - Java + Spring Boot

---

## 🚨 ERRORES CRÍTICOS DE SEGURIDAD

### 1. **CSRF DESHABILITADO** ⚠️ CRÍTICO
**Ubicación:** `SecurityConfig.java:38`
```java
.csrf(csrf -> csrf.disable()) // Temporalmente deshabilitado para desarrollo
```
**Problema:** La aplicación está vulnerable a ataques CSRF. En producción esto es un riesgo grave.
**Impacto en sustentación:** Pregunta obvia sobre seguridad.
**Solución:** Habilitar CSRF y usar tokens en formularios.

---

### 2. **FALTA DE VALIDACIÓN DE STOCK EN CARRITO** ⚠️ CRÍTICO
**Ubicación:** `CarritoController.java:50-84`
**Problema:** 
- No valida si hay stock suficiente antes de agregar al carrito
- No valida stock al hacer checkout
- No descuenta stock al comprar
- Permite agregar cantidad mayor al stock disponible

**Código problemático:**
```java
@PostMapping("/agregar/{idProducto}")
public String agregarAlCarrito(...) {
    // ❌ NO VALIDA STOCK
    if (existingItem != null) {
        existingItem.setCantidad(existingItem.getCantidad() + cantidad);
        // ❌ Puede exceder el stock
    }
}

@PostMapping("/checkout")
public String checkout(...) {
    // ❌ NO DESCUENTA STOCK
    // ❌ Solo cambia estado a "comprado"
    item.setEstado(EstadoApartado.comprado);
}
```

**Comparación con PHP:** El proyecto PHP SÍ valida stock (líneas 66-68 de ApartadoController.php)

**Impacto:** 
- Permite vender productos sin stock
- Inconsistencia de datos
- Problema de negocio grave

---

### 3. **FALTA DE VALIDACIONES EN MODELOS** ⚠️ ALTO
**Problema:** No hay anotaciones de validación (`@NotNull`, `@NotEmpty`, `@Min`, `@Max`, etc.)
**Ubicación:** Todos los modelos (`Producto.java`, `Persona.java`, etc.)

**Ejemplo:**
```java
// ❌ ACTUAL - Sin validaciones
private String nombre;
private Double precio;
private Integer stock;

// ✅ DEBERÍA SER
@NotBlank(message = "El nombre es obligatorio")
@Size(max = 100)
private String nombre;

@NotNull(message = "El precio es obligatorio")
@Min(value = 0, message = "El precio no puede ser negativo")
private Double precio;

@NotNull(message = "El stock es obligatorio")
@Min(value = 0, message = "El stock no puede ser negativo")
private Integer stock;
```

**Impacto:** Datos inválidos pueden guardarse en la BD.

---

## 🔴 INCONSISTENCIAS DE MODELOS Y RELACIONES

### 4. **INCONSISTENCIA EN MODELO JUGADOR** ⚠️ ALTO
**Ubicación:** `Jugador.java:20-25`

**Problema:** El modelo tiene DOS formas de relacionarse con Equipo:
1. `@ManyToOne` directo con `id_equipo` (línea 20-25)
2. Tabla intermedia `jugador_equipo` (modelo `JugadorEquipo.java`)

**Código:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_equipo")  // ❌ Esta columna NO existe en la BD
private Equipo equipo;
```

**En la BD:** La relación es muchos-a-muchos a través de `jugador_equipo`, NO hay columna `id_equipo` en `jugador`.

**Impacto:** Error al intentar usar esta relación.

---

### 5. **INCONSISTENCIA EN MODELO EQUIPO** ⚠️ MEDIO
**Ubicación:** `Equipo.java:22-26`

**Problema:** 
- `@OneToOne` con `id_capitan` pero en la BD el capitán se identifica por `esCapitan=true` en `jugador_equipo`
- No hay columna `id_capitan` en la tabla `equipo`

**Código:**
```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_capitan")  // ❌ Esta columna NO existe
private Jugador capitan;
```

---

### 6. **NOMBRES INCONSISTENTES EN TORNO** ⚠️ MEDIO
**Ubicación:** `Torneo.java:13`
```java
private Integer idPartido;  // ❌ Debería ser idTorneo
```
**Problema:** El campo se llama `idPartido` pero es un Torneo, no un Partido.

---

### 7. **RELACIÓN PARTIDO-CANCHA INCORRECTA** ⚠️ MEDIO
**Ubicación:** `Partido.java:18`
```java
private Integer cancha;  // ❌ Debería ser @ManyToOne Cancha
```
**Problema:** Usa Integer en lugar de relación JPA. Debería ser:
```java
@ManyToOne
@JoinColumn(name = "cancha")
private Cancha cancha;
```

---

## ⚠️ PROBLEMAS DE LÓGICA DE NEGOCIO

### 8. **NO HAY VALIDACIÓN DE CUPOS EN EQUIPOS** ⚠️ MEDIO
**Ubicación:** `EquipoController.java:42-53`
**Problema:** Permite unirse a equipos sin verificar `cuposDisponibles`.

---

### 9. **FALTA DE TRANSACCIONES** ⚠️ ALTO
**Problema:** Operaciones críticas no están en transacciones:
- `CarritoController.checkout()` - Debería ser transaccional
- `EquipoController` - Crear/eliminar equipos debería ser transaccional
- `ProductoController` - Actualizar stock debería ser transaccional

**Ejemplo:**
```java
// ❌ ACTUAL
@PostMapping("/checkout")
public String checkout(...) {
    for (Apartado item : items) {
        item.setEstado(EstadoApartado.comprado);
        apartadoRepository.save(item);
    }
    // ❌ Si falla a mitad, datos inconsistentes
}

// ✅ DEBERÍA SER
@Transactional
@PostMapping("/checkout")
public String checkout(...) {
    // Todo en una transacción
}
```

---

### 10. **NO SE VALIDA QUE EL USUARIO SEA DUEÑO DEL APARTADO** ⚠️ ALTO
**Ubicación:** `CarritoController.java:86-95`
**Problema:** Cualquier usuario autenticado puede eliminar cualquier apartado si conoce el ID.

**Código:**
```java
@PostMapping("/eliminar/{idApartado}")
public String eliminarDelCarrito(@PathVariable Integer idApartado, ...) {
    // ❌ NO VERIFICA que el apartado pertenezca al usuario actual
    apartadoRepository.deleteById(idApartado);
}
```

**Comparación con PHP:** El proyecto PHP SÍ valida (líneas 159-161 de ApartadoController.php)

---

## 🔧 PROBLEMAS DE CONFIGURACIÓN

### 11. **CONTRASEÑA DE ADMIN HARDCODEADA** ⚠️ MEDIO
**Ubicación:** `DataInitializer.java:27`
```java
admin.setContrasena(passwordEncoder.encode("12345"));  // ❌ Contraseña débil
```
**Problema:** Contraseña débil y visible en código.

---

### 12. **FALTA DE VALIDACIÓN EN REGISTRO** ⚠️ ALTO
**Ubicación:** `AuthController.java:24-33`
**Problema:** 
- No valida formato de email
- No valida fortaleza de contraseña
- No valida que el correo sea único antes de intentar guardar

**Código:**
```java
@PostMapping("/registro")
public String registrarPersona(@ModelAttribute Persona persona, Model model) {
    // ❌ Sin @Valid
    // ❌ Sin validaciones
    try {
        personaService.registrar(persona);
    } catch (RuntimeException e) {
        // Solo maneja excepciones, no valida antes
    }
}
```

---

### 13. **MANEJO DE EXCEPCIONES INADECUADO** ⚠️ MEDIO
**Problema:** 
- Se usa `RuntimeException` genérico en lugar de excepciones específicas
- `GlobalExceptionHandler` solo maneja `AccessDeniedException`
- No hay manejo de `EntityNotFoundException`, `ValidationException`, etc.

---

## 📊 INCONSISTENCIAS CON EL PROYECTO PHP

### 14. **FUNCIONALIDADES FALTANTES** ⚠️ ALTO
Comparando con `DiseñoPHP/routes/web.php`, faltan:

1. **Apartados:**
   - ❌ `confirmar` apartado (cambiar estado y descontar stock)
   - ❌ `entregar` apartado
   - ❌ `cancelar` apartado
   - ❌ Validación de stock al crear apartado

2. **Compras:**
   - ❌ Crear compra desde apartado
   - ❌ Historial de compras
   - ❌ Reportes de ventas

3. **Dashboard:**
   - ❌ Estadísticas por rol (admin vs jugador vs capitan)
   - ❌ Actividad reciente
   - ❌ Gestión de equipos para capitanes

4. **API Endpoints:**
   - ❌ `/api/estadisticas`
   - ❌ `/api/productos/disponibles`
   - ❌ `/api/apartados/pendientes`
   - ❌ Y muchos más...

---

### 15. **DIFERENCIAS EN FLUJO DE COMPRA** ⚠️ ALTO
**PHP:** Apartado → Confirmar → Descontar Stock → Crear Compra
**Java:** Apartado → Checkout → Solo cambia estado (NO descuenta stock, NO crea compra)

---

## 🐛 BUGS Y ERRORES LÓGICOS

### 16. **NO SE ACTUALIZA STOCK AL COMPRAR** ⚠️ CRÍTICO
**Ubicación:** `CarritoController.java:97-121`
**Problema:** Al hacer checkout, solo cambia el estado pero NO descuenta el stock del producto.

**Código:**
```java
for (Apartado item : items) {
    item.setEstado(EstadoApartado.comprado);
    apartadoRepository.save(item);
    // ❌ FALTA: producto.setStock(producto.getStock() - item.getCantidad());
}
```

---

### 17. **NO SE CREA REGISTRO EN TABLA COMPRA** ⚠️ ALTO
**Problema:** El checkout no crea un registro en la tabla `compra`, solo cambia el estado del apartado.

**Impacto:** No hay historial de compras, no se puede hacer reportes.

---

### 18. **VALIDACIÓN DE CANTIDAD INEXISTENTE** ⚠️ MEDIO
**Ubicación:** `CarritoController.java:51`
**Problema:** No valida que `cantidad` sea > 0, puede ser negativa o cero.

---

### 19. **FALTA DE VALIDACIÓN DE FECHA DE VENCIMIENTO** ⚠️ MEDIO
**Ubicación:** `ProductoService.java:26-36`
**Problema:** No valida que `fechaVencimiento` sea futura al crear/editar productos.

---

## 📝 PROBLEMAS DE CÓDIGO Y MEJORES PRÁCTICAS

### 20. **USO EXCESIVO DE RuntimeException** ⚠️ MEDIO
**Problema:** Se usa `RuntimeException` genérico en lugar de excepciones específicas del dominio.

**Ejemplo:**
```java
// ❌ ACTUAL
throw new RuntimeException("Producto no encontrado");
throw new RuntimeException("El correo ya está registrado.");

// ✅ DEBERÍA SER
throw new ProductoNotFoundException(id);
throw new EmailAlreadyExistsException(email);
```

---

### 21. **FALTA DE LOGGING** ⚠️ MEDIO
**Problema:** No hay logging de operaciones importantes (crear usuario, compras, errores, etc.)

---

### 22. **MÉTODOS SIN DOCUMENTACIÓN** ⚠️ BAJO
**Problema:** Falta JavaDoc en métodos públicos.

---

### 23. **CÓDIGO DUPLICADO** ⚠️ BAJO
**Problema:** Lógica repetida en varios controladores (obtener Persona por email, manejo de errores, etc.)

---

## 🎯 RECOMENDACIONES PARA LA SUSTENTACIÓN

### PREGUNTAS PROBABLES Y RESPUESTAS:

1. **"¿Cómo manejan la seguridad CSRF?"**
   - ❌ Respuesta actual: "Está deshabilitado temporalmente"
   - ✅ Respuesta correcta: "Usamos tokens CSRF de Spring Security en todos los formularios"

2. **"¿Cómo validan el stock antes de vender?"**
   - ❌ Respuesta actual: "No lo validamos actualmente"
   - ✅ Respuesta correcta: "Validamos stock antes de agregar al carrito y al hacer checkout, además usamos transacciones para garantizar consistencia"

3. **"¿Qué pasa si dos usuarios compran el último producto al mismo tiempo?"**
   - ❌ Respuesta actual: "No está manejado"
   - ✅ Respuesta correcta: "Usamos transacciones con nivel de aislamiento adecuado y validación optimista"

4. **"¿Cómo garantizan la integridad de datos?"**
   - ❌ Respuesta actual: "Confiamos en la BD"
   - ✅ Respuesta correcta: "Usamos validaciones en múltiples capas: anotaciones en modelos, validación en servicios, constraints en BD, y transacciones"

---

## ✅ PRIORIDADES DE CORRECCIÓN

### CRÍTICO (Antes de sustentar):
1. ✅ Validar y descontar stock en carrito
2. ✅ Habilitar CSRF
3. ✅ Validar ownership de apartados
4. ✅ Crear registros en tabla compra

### ALTO (Muy recomendado):
5. ✅ Agregar validaciones en modelos
6. ✅ Corregir relaciones JPA (Jugador-Equipo)
7. ✅ Agregar transacciones
8. ✅ Validar stock antes de agregar al carrito

### MEDIO (Recomendado):
9. ✅ Mejorar manejo de excepciones
10. ✅ Agregar logging
11. ✅ Validar cupos en equipos
12. ✅ Corregir nombres inconsistentes

---

## 📋 CHECKLIST PRE-SUSTENTACIÓN

- [ ] Habilitar CSRF
- [ ] Validar stock en todas las operaciones
- [ ] Descontar stock al comprar
- [ ] Crear registros en tabla compra
- [ ] Validar ownership de recursos
- [ ] Agregar transacciones a operaciones críticas
- [ ] Agregar validaciones en modelos
- [ ] Corregir relaciones JPA incorrectas
- [ ] Agregar manejo de excepciones específicas
- [ ] Documentar métodos críticos
- [ ] Agregar logging básico
- [ ] Probar casos edge (stock 0, usuarios concurrentes, etc.)

---

**Fecha de análisis:** 2025-12-08
**Versión analizada:** Proyecto actual con diseño PHP adaptado

