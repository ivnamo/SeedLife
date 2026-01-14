# Esquemas de Layout de Pantallas - SeedLife

## 1. SplashScreen

```mermaid
flowchart TD
    A[Box - fillMaxSize<br/>Background: LightGreenBg<br/>Centrado] --> B[Column - Centrado<br/>padding: 32dp]
    B --> C[Image<br/>splash_illustration.png<br/>300dp x 300dp<br/>padding bottom: 32dp]
    B --> D[Text<br/>LifeSeeds<br/>42sp, Bold<br/>Color: Verde #4CAF50<br/>Animación alpha: 0.3-1.0<br/>padding top: 16dp]
```

## 2. AuthScreen

```mermaid
flowchart TD
    A[Column - fillMaxSize<br/>padding: 24dp<br/>Centrado vertical/horizontal] --> B[Text - Título<br/>Iniciar Sesión / Registrarse<br/>headlineMedium<br/>padding bottom: 32dp]
    A --> C{isLoginMode?}
    C -->|No| D[OutlinedTextField<br/>Nombre<br/>Obligatorio<br/>padding bottom: 16dp]
    C -->|Sí| E[OutlinedTextField<br/>Email<br/>KeyboardType.Email<br/>Validación tiempo real<br/>padding bottom: 16dp]
    D --> E
    E --> F[OutlinedTextField<br/>Contraseña<br/>PasswordVisualTransformation<br/>Validación tiempo real<br/>padding bottom: 16dp/24dp]
    F --> G{isLoginMode?}
    G -->|No| H[OutlinedTextField<br/>Confirmar Contraseña<br/>PasswordVisualTransformation<br/>Validación tiempo real<br/>padding bottom: 24dp]
    G -->|Sí| I[Text - Error<br/>Si errorMessage != null<br/>Color: error<br/>padding bottom: 16dp]
    H --> I
    I --> J[Button - Principal<br/>Iniciar Sesión / Registrarse<br/>fillMaxWidth<br/>CircularProgressIndicator si Loading<br/>padding bottom: 16dp]
    J --> K[TextButton<br/>Alternar Login/Registro<br/>¿No tienes cuenta? / ¿Ya tienes cuenta?<br/>padding bottom: 16dp]
    K --> L[HorizontalDivider<br/>padding vertical: 16dp]
    L --> M[OutlinedButton<br/>Entrar como invitado<br/>fillMaxWidth]
```

## 3. HomeScreen (Garden)

```mermaid
flowchart TD
    A[Scaffold] --> B[TopAppBar]
    A --> C[FloatingActionButton<br/>Icon: Add<br/>Crear nueva seed]
    A --> D[SnackbarHost]
    A --> E[Column - fillMaxSize<br/>padding: paddingValues]
    
    B --> B1{showSearchBar?}
    B1 -->|Sí| B2[SearchBar<br/>OutlinedTextField<br/>Icon: Search<br/>Icon: Close si texto]
    B1 -->|No| B3[Text: Mi Jardín]
    B3 --> B4[IconButton: Search<br/>Abre barra búsqueda]
    B4 --> B5[IconButton: Filter<br/>Abre diálogo filtros]
    
    E --> F{isGuest?}
    F -->|Sí| G[Text: Modo Invitado<br/>bodyMedium, secondary color<br/>padding: 16dp]
    F -->|No| H[Text: Bienvenido, userName<br/>bodyLarge<br/>padding: 16dp]
    G --> I{searchFilters activos?}
    H --> I
    I -->|Sí| J[FilterChips<br/>Chips de filtros activos<br/>Botón Limpiar<br/>padding: 16dp horizontal, 8dp vertical]
    I -->|No| K{uiState}
    J --> K
    
    K -->|Loading| L[Box - Centrado<br/>CircularProgressIndicator<br/>padding: 32dp]
    K -->|Success| M{data.isEmpty?}
    K -->|Error| N[Box - Centrado<br/>Text: error message<br/>Button: Reintentar<br/>padding: 32dp]
    
    M -->|Sí| O[Box - fillMaxSize, Centrado<br/>padding: 32dp]
    O --> O1[Image: empty_garden.png<br/>250dp x 250dp<br/>padding bottom: 24dp]
    O1 --> O2[Text: No hay semillas<br/>headlineSmall, Bold<br/>padding bottom: 8dp]
    O2 --> O3[Text: Toca el botón + para crear...<br/>bodyMedium]
    
    M -->|No| P{filteredSeeds.isEmpty?}
    P -->|Sí| Q[EmptySearchState<br/>No se encontraron semillas<br/>Intenta ajustar filtros]
    P -->|No| R[LazyColumn - fillMaxSize<br/>contentPadding: 16dp<br/>spacedBy: 8dp]
    R --> S[SeedItem - Card<br/>Para cada seed]
    S --> S1[Row]
    S1 --> S2[AsyncImage<br/>Thumbnail 64dp<br/>photoUrl o placeholder]
    S2 --> S3[Column - weight 1]
    S3 --> S4[Text: title<br/>titleLarge, Bold]
    S4 --> S5[Text: description<br/>bodyMedium]
    S5 --> S6[Row]
    S6 --> S7[Image: nivel<br/>seed_bag/planta/plantas<br/>24dp]
    S7 --> S8[Text: Nivel X<br/>bodySmall, SemiBold]
    S3 --> S9[IconButton: MoreVert<br/>DropdownMenu]
    S9 --> S10[DropdownMenuItem: Editar]
    S10 --> S11[DropdownMenuItem: Eliminar<br/>Color: error]
    
    A --> T[DeleteSeedDialog<br/>Si showDeleteDialog != null]
    A --> U[FilterDialog<br/>Si showFilterDialog == true]
```

## 4. SeedDetailScreen

```mermaid
flowchart TD
    A[Scaffold] --> B[TopAppBar]
    A --> C[FloatingActionButton<br/>Icon: Add<br/>Añadir riego]
    A --> D[SnackbarHost]
    A --> E[Column - fillMaxSize<br/>padding: paddingValues + 16dp]
    
    B --> B1[IconButton: ArrowBack<br/>onNavigateBack]
    B1 --> B2[Text: Detalle de Semilla]
    B2 --> B3[TextButton: Editar]
    B3 --> B4[TextButton: Eliminar<br/>Color: error<br/>showDeleteDialog = true]
    
    E --> F{seedState}
    F -->|Loading| G[Box - Centrado<br/>CircularProgressIndicator<br/>padding: 32dp]
    F -->|Success| H[Card - fillMaxWidth<br/>padding bottom: 16dp]
    F -->|Error| I[Card - fillMaxWidth<br/>Column centrado<br/>Text: error message<br/>Button: Reintentar]
    
    H --> H1[Column - padding: 16dp]
    H1 --> H2[Box - fillMaxWidth<br/>height: 200dp<br/>padding bottom: 16dp]
    H2 --> H3[AsyncImage<br/>photoUrl o placeholder<br/>ContentScale.Crop<br/>fillMaxSize]
    H3 --> H4[Button: Añadir foto<br/>fillMaxWidth<br/>Icon: ic_photo_camera<br/>padding bottom: 16dp<br/>disabled si guest]
    H4 --> H5[Text: title<br/>headlineMedium, Bold<br/>padding bottom: 8dp]
    H5 --> H6[Text: description<br/>bodyMedium<br/>padding bottom: 8dp]
    H6 --> H7[Row - SpaceBetween]
    H7 --> H8[Text: Nivel X<br/>bodyLarge, SemiBold]
    H8 --> H9[Text: Último riego: date<br/>bodySmall<br/>Si lastWateredAt != null]
    
    E --> J[Text: Historial de Riegos<br/>titleLarge<br/>padding vertical: 8dp]
    J --> K{wateringsState}
    K -->|Loading| L[Box - Centrado<br/>CircularProgressIndicator<br/>padding: 32dp]
    K -->|Success| M{data.isEmpty?}
    K -->|Error| N[Box - Centrado<br/>Text: error message<br/>Button: Reintentar<br/>padding: 32dp]
    
    M -->|Sí| O[Box - Centrado<br/>Text: No hay riegos registrados<br/>bodyMedium<br/>padding: 32dp]
    M -->|No| P[LazyColumn<br/>spacedBy: 8dp]
    P --> Q[WateringItem - Card<br/>Para cada watering]
    Q --> Q1[Row - SpaceBetween]
    Q1 --> Q2[Column - weight 1]
    Q2 --> Q3[Text: mood<br/>GOOD/OK/BAD con emoji<br/>bodyLarge, SemiBold<br/>Color según mood]
    Q3 --> Q4[Text: date<br/>bodySmall]
    Q4 --> Q5[Text: note<br/>bodyMedium<br/>Si note != null y not blank]
    
    A --> R[AddWateringDialog<br/>Si showWateringDialog == true]
    A --> S[DeleteSeedDialog<br/>Si showDeleteDialog == true]
```

## 5. SeedEditorScreen

```mermaid
flowchart TD
    A[Scaffold] --> B[TopAppBar]
    A --> C[SnackbarHost]
    A --> D[Column - fillMaxSize<br/>padding: paddingValues + 16dp<br/>spacedBy: 16dp]
    
    B --> B1[IconButton: ArrowBack<br/>onNavigateBack]
    B1 --> B2[Text: Nueva Semilla / Editar Semilla<br/>Según seedId == null]
    
    D --> E[OutlinedTextField<br/>Título<br/>singleLine: true<br/>fillMaxWidth<br/>Validación tiempo real<br/>isError si titleError != null<br/>supportingText: titleError]
    E --> F[Column - weight 1<br/>fillMaxWidth]
    F --> G[OutlinedTextField<br/>Descripción<br/>maxLines: 10<br/>fillMaxWidth<br/>Validación tiempo real<br/>isError si descriptionError != null<br/>supportingText: Row]
    G --> G1[Row - SpaceBetween<br/>En supportingText]
    G1 --> G2[Text: descriptionError<br/>Si error != null]
    G2 --> G3[Text: X/200<br/>Contador caracteres<br/>bodySmall]
    F --> H[Button: Guardar<br/>fillMaxWidth<br/>CircularProgressIndicator si isLoading<br/>disabled si loading o errores]
```

## 6. StatsScreen

```mermaid
flowchart TD
    A[Column - fillMaxSize<br/>padding: 24dp] --> B[Text: Estadísticas<br/>headlineLarge<br/>padding bottom: 32dp]
    B --> C{isGuest?}
    
    C -->|Sí| D[Card - fillMaxWidth]
    D --> D1[Column - Centrado<br/>padding: 16dp]
    D1 --> D2[Text: No disponible en modo invitado<br/>bodyLarge]
    D2 --> D3[Text: Inicia sesión para ver estadísticas<br/>bodyMedium<br/>padding top: 8dp]
    
    C -->|No| E{isLoading?}
    E -->|Sí| F[Box - fillMaxSize, Centrado<br/>CircularProgressIndicator]
    E -->|No| G[StatsCard<br/>Total de Semillas<br/>fillMaxWidth<br/>padding bottom: 16dp]
    G --> H[StatsCard<br/>Total de Riegos<br/>fillMaxWidth]
    
    G --> G1[Card]
    G1 --> G2[Column - Centrado<br/>padding: 24dp]
    G2 --> G3[Text: title<br/>titleMedium<br/>onSurfaceVariant<br/>padding bottom: 8dp]
    G3 --> G4[Text: value<br/>displayMedium, Bold]
    
    H --> H1[Card]
    H1 --> H2[Column - Centrado<br/>padding: 24dp]
    H2 --> H3[Text: title<br/>titleMedium<br/>onSurfaceVariant<br/>padding bottom: 8dp]
    H3 --> H4[Text: value<br/>displayMedium, Bold]
```

## 7. ProfileScreen

```mermaid
flowchart TD
    A[Column - fillMaxSize<br/>padding: 24dp] --> B[Text: Perfil<br/>headlineLarge<br/>padding bottom: 32dp]
    B --> C{isGuest?}
    
    C -->|Sí| D[Card - fillMaxWidth<br/>padding bottom: 24dp]
    D --> D1[Column - padding: 16dp]
    D1 --> D2[Text: Modo Invitado<br/>titleLarge<br/>padding bottom: 8dp]
    D2 --> D3[Text: Para guardar tus datos...<br/>bodyMedium<br/>onSurfaceVariant]
    D --> E[Button: Crear cuenta / Iniciar sesión<br/>fillMaxWidth<br/>onNavigateToAuth]
    
    C -->|No| F[Card - fillMaxWidth<br/>padding bottom: 24dp]
    F --> F1[Column - padding: 16dp]
    F1 --> F2[Text: Información del Usuario<br/>titleLarge<br/>padding bottom: 16dp]
    F2 --> F3[ProfileInfoRow<br/>Label: Nombre<br/>Value: profile.name]
    F3 --> F4[Spacer - height: 8dp]
    F4 --> F5[ProfileInfoRow<br/>Label: Email<br/>Value: profile.email]
    F --> G[Button: Cerrar Sesión<br/>fillMaxWidth<br/>Color: error<br/>signOut + onNavigateToAuth]
    
    E --> H[Spacer - height: 32dp]
    G --> H
    H --> I[Card - fillMaxWidth]
    I --> I1[Row - SpaceBetween<br/>padding: 16dp]
    I1 --> I2[Row - Centrado vertical]
    I2 --> I3[Icon: Settings<br/>padding end: 16dp]
    I3 --> I4[Text: Modo Oscuro<br/>bodyLarge]
    I2 --> I5[Switch<br/>isDarkMode<br/>onCheckedChange]
    
    F3 --> F3A[Row - SpaceBetween<br/>fillMaxWidth]
    F3A --> F3B[Text: label<br/>bodyMedium<br/>onSurfaceVariant]
    F3B --> F3C[Text: value<br/>bodyMedium, SemiBold]
```
