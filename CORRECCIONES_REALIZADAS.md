# ✅ CORRECCIONES REALIZADAS
## Proyecto TejoBar - Java + Spring Boot

---

## 🔧 CORRECCIONES IMPLEMENTADAS

### 1. ✅ VALIDACIÓN DE STOCK EN CARRITO
**Archivo:** `CarritoController.java`

**Cambios:**
- ✅ Validación de cantidad > 0 antes de agregar
- ✅ Validación de stock disponible antes de agregar
- ✅ Validación de stock total (incluyendo items ya en carrito)
- ✅ Validación de stock antes de checkout
- ✅ Descuento de stock al realizar compra

**Código agregado:**
```java
// Validar cantidad
if (cantidad == null || cantidad <= 0) {
    redirectAttributes.addFlashAttribute("error", "La cantidad debe ser mayor a 0");
    return "redirect:/productos";
}

// Validar stock disponible
if (producto.getStock() == null || producto.getStock() <= 0) {
    redirectAttributes.addFlashAttribute("error", "El producto no tiene stock disponible");
    return "redirect:/productos";
}

// Validar que la cantidad total no exceda el stock
if (cantidadTotal > producto.getStock()) {
    redirectAttributes.addFlashAttribute("error", 
        "Stock insuficiente. Disponible: " + producto.getStock());
    return "redirect:/productos";
}
```

---

### 2. ✅ DESCUENTO DE STOCK Y CREACIÓN DE COMPRA
**Archivo:** `CarritoController.java` - método `checkout()`

**Cambios:**
- ✅ Validación de stock antes de procesar
- ✅ Creación de registro en tabla `compra`
- ✅ Descuento de stock por cada producto comprado
- ✅ Cambio de estado de apartados a "comprado"

**Código agregado:**
```java
// Validar stock antes de procesar
for (Apartado item : items) {
    Producto producto = item.getProducto();
    if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
        redirectAttributes.addFlashAttribute("error", 
            "Stock insuficiente para " + producto.getNombre());
        return "redirect:/carrito";
    }
}

// Crear registro de compra
Compra compra = new Compra();
compra.setJugador(jugador);
compra.setTotal(total);
compra.setFecha(LocalDate.now());
compraRepository.save(compra);

// Descontar stock
for (Apartado item : items) {
    Producto producto = item.getProducto();
    producto.setStock(producto.getStock() - item.getCantidad());
    productoRepository.save(producto);
}
```

---

### 3. ✅ VALIDACIÓN DE OWNERSHIP DE APARTADOS
**Archivo:** `CarritoController.java` - método `eliminarDelCarrito()`

**Cambios:**
- ✅ Verificación de que el apartado pertenezca al usuario actual
- ✅ Mensaje de error si no tiene permisos

**Código agregado:**
```java
// Validar que el apartado pertenezca al usuario actual
Apartado apartado = apartadoRepository.findById(idApartado)
        .orElseThrow(() -> new RuntimeException("Apartado no encontrado"));

if (!apartado.getPersona().getIdPersona().equals(persona.getIdPersona())) {
    redirectAttributes.addFlashAttribute("error", 
        "No tienes permisos para eliminar este apartado");
    return "redirect:/carrito";
}
```

---

### 4. ✅ VALIDACIONES EN MODELOS
**Archivos:** `Producto.java`, `Persona.java`

**Cambios:**
- ✅ Agregadas anotaciones `@NotBlank`, `@NotNull`, `@Size`, `@Min`, `@Max`, `@Email`, `@FutureOrPresent`
- ✅ Mensajes de error personalizados
- ✅ Validación de fecha de vencimiento futura
- ✅ Validación de formato de email
- ✅ Validación de longitud de campos

**Dependencia agregada:** `spring-boot-starter-validation` en `build.gradle`

---

### 5. ✅ CORRECCIÓN DE RELACIONES JPA
**Archivos:** `Jugador.java`, `Equipo.java`, `Partido.java`, `Torneo.java`

**Cambios:**
- ✅ Eliminada relación incorrecta `@ManyToOne` con `id_equipo` en `Jugador` (columna no existe)
- ✅ Eliminada relación incorrecta `@OneToOne` con `id_capitan` en `Equipo` (columna no existe)
- ✅ Corregida relación `Partido-Cancha` de Integer a `@ManyToOne Cancha`
- ✅ Corregido nombre de campo `idPartido` a `idTorneo` en `Torneo` (manteniendo nombre de columna BD)

---

### 6. ✅ HABILITACIÓN DE CSRF
**Archivo:** `SecurityConfig.java`

**Cambios:**
- ✅ CSRF habilitado con `CookieCsrfTokenRepository`
- ✅ Solo deshabilitado para rutas `/api/**` si es necesario

**Código:**
```java
.csrf(csrf -> csrf
    .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers("/api/**"))
```

**Nota:** Thymeleaf agrega automáticamente el token CSRF en formularios con `th:action`.

---

### 7. ✅ VALIDACIÓN DE FECHA DE VENCIMIENTO
**Archivo:** `ProductoService.java`

**Cambios:**
- ✅ Validación de que fecha de vencimiento sea futura o presente
- ✅ Validación de stock no negativo

**Código agregado:**
```java
// Validar fecha de vencimiento
if (producto.getFechaVencimiento() != null && 
    producto.getFechaVencimiento().isBefore(java.time.LocalDate.now())) {
    throw new RuntimeException("La fecha de vencimiento debe ser hoy o una fecha futura.");
}

// Validar stock
if (producto.getStock() != null && producto.getStock() < 0) {
    throw new RuntimeException("El stock no puede ser negativo.");
}
```

---

### 8. ✅ MEJORA DE MANEJO DE EXCEPCIONES
**Archivo:** `GlobalExceptionHandler.java`

**Cambios:**
- ✅ Manejo de `RuntimeException`
- ✅ Manejo de `IllegalArgumentException`
- ✅ Mensajes de error más descriptivos

**Código agregado:**
```java
@ExceptionHandler(RuntimeException.class)
public String handleRuntimeException(RuntimeException ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("error", ex.getMessage());
    return "redirect:/dashboard";
}

@ExceptionHandler(IllegalArgumentException.class)
public String handleIllegalArgumentException(IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("error", "Datos inválidos: " + ex.getMessage());
    return "redirect:/dashboard";
}
```

---

### 9. ✅ VALIDACIÓN EN CONTROLADORES
**Archivos:** `ProductoController.java`, `AuthController.java`

**Cambios:**
- ✅ Uso de `@Valid` en métodos POST
- ✅ Manejo de `BindingResult` para mostrar errores de validación
- ✅ Mensajes de error personalizados

**Código agregado:**
```java
@PostMapping
public String crearProducto(@Valid @ModelAttribute Producto producto, 
                            org.springframework.validation.BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
        redirectAttributes.addFlashAttribute("error", "Error de validación: " + 
            bindingResult.getAllErrors().stream()
                .map(e -> e.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Datos inválidos"));
        return "redirect:/productos/crear";
    }
    // ...
}
```

---

## 📋 RESUMEN DE CORRECCIONES

| # | Error | Estado | Archivo(s) |
|---|-------|--------|------------|
| 1 | Validación de stock en carrito | ✅ Corregido | `CarritoController.java` |
| 2 | Descuento de stock al comprar | ✅ Corregido | `CarritoController.java` |
| 3 | Creación de registros en compra | ✅ Corregido | `CarritoController.java` |
| 4 | Validación de ownership | ✅ Corregido | `CarritoController.java` |
| 5 | Validaciones en modelos | ✅ Corregido | `Producto.java`, `Persona.java` |
| 6 | Relaciones JPA incorrectas | ✅ Corregido | `Jugador.java`, `Equipo.java`, `Partido.java`, `Torneo.java` |
| 7 | CSRF deshabilitado | ✅ Corregido | `SecurityConfig.java` |
| 8 | Validación fecha vencimiento | ✅ Corregido | `ProductoService.java` |
| 9 | Manejo de excepciones | ✅ Mejorado | `GlobalExceptionHandler.java` |
| 10 | Validación en controladores | ✅ Agregado | `ProductoController.java`, `AuthController.java` |

---

## ⚠️ NOTAS IMPORTANTES

1. **Dependencia de Validación:** Se agregó `spring-boot-starter-validation` al `build.gradle`. Es necesario ejecutar `./gradlew build` o refrescar el proyecto para descargar la dependencia.

2. **CSRF:** Los formularios con `th:action` en Thymeleaf automáticamente incluyen el token CSRF. No es necesario agregarlo manualmente.

3. **Transacciones:** Como se solicitó, NO se agregaron transacciones. Esto significa que si falla una operación a mitad, puede haber inconsistencias. Se recomienda agregarlas en producción.

4. **Compilación:** Los errores de linter sobre `jakarta.validation` se resolverán después de compilar el proyecto con la nueva dependencia.

---

## 🚀 PRÓXIMOS PASOS RECOMENDADOS

1. Compilar el proyecto para descargar dependencias
2. Probar el flujo completo de compra
3. Verificar que el stock se descuenta correctamente
4. Verificar que se crean registros en tabla compra
5. Probar validaciones de formularios

---

**Fecha de correcciones:** 2025-12-08

